package com.foobarust.deliverer.ui.location

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.TravelMode
import com.foobarust.deliverer.databinding.FragmentLocationBinding
import com.foobarust.deliverer.ui.main.MainViewModel
import com.foobarust.deliverer.ui.shared.AppConfig.MAP_ROUTE_WIDTH
import com.foobarust.deliverer.ui.shared.AppConfig.MAP_ZOOM_LEVEL
import com.foobarust.deliverer.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.maps.android.ktx.addPolyline
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by kevin on 3/19/21
 */

@AndroidEntryPoint
class LocationFragment : Fragment(), LocationSource {

    private var binding: FragmentLocationBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var locationViewModel: LocationViewModel

    private var currentPolyline: Polyline? = null
    private var onLocationChangedListener: LocationSource.OnLocationChangedListener? = null

    private var bottomSheetBehavior: BottomSheetBehavior<*> by AutoClearedValue(this)

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(inflater, container, false)

        // Get bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout)

        // Chip group
        binding.actionsChipGroup.applySystemWindowInsetsMargin(applyTop = true)

        // Delivery mode chip
        binding.deliveryModeChip.setOnClickListener {
            showDeliveryModeDialog()
        }

        // Move camera to current location
        binding.currentLocationButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val locationInfo = mainViewModel.currentLocationInfo.first()
                moveMapCameraToLocation(locationInfo.locationPoint)
            }
        }

        // Arrived destination chip
        binding.arrivedDestChip.setOnClickListener {
            showConfirmStartPickupDialog()
        }

        // Setup navigation
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.sectionInDelivery.collect { sectionId ->
                initializeBottomDrawer(sectionId)
            }
        }

        // Initialize GoogleMap
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.initMap.collect {
                getMapInstance().run {
                    // Set night mode
                    if (requireContext().isNightModeOn()) {
                        val nightStyle = MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.night_map_style
                        )
                        setMapStyle(nightStyle)
                    }

                    // Set location source (use fused location provider and sensor data)
                    // Permission should be enabled when initMap is called
                    isMyLocationEnabled = true
                    setLocationSource(this@LocationFragment)

                    // Set padding
                    val paddingTop = binding.actionsScrollView.bottom
                    setPadding(0, paddingTop, 0, 0)

                    // Ui Settings
                    with(uiSettings) {
                        isCompassEnabled = false
                        isMyLocationButtonEnabled = false
                    }
                }
            }
        }

        // Delivery mode
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.travelMode.collect { deliveryMode ->
                with(binding.deliveryModeChip) {
                    text = when (deliveryMode) {
                        TravelMode.DRIVING -> getString(R.string.travel_mode_driving)
                        TravelMode.WALKING -> getString(R.string.travel_mode_walking)
                    }
                    chipIcon = when (deliveryMode) {
                        TravelMode.DRIVING -> requireContext().getDrawableOrNull(
                            R.drawable.ic_local_shipping
                        )
                        TravelMode.WALKING -> requireContext().getDrawableOrNull(
                            R.drawable.ic_directions_run
                        )
                    }
                }
            }
        }

        // Pass current location to map
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.currentLocationInfo.collect { locationInfo ->
                onLocationChangedListener?.onLocationChanged(locationInfo.toLocation())
            }
        }

        // Keep centering camera to current location when delivery
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.centerCameraLocation.collectLatest { locationPoint ->
                locationPoint?.let {
                    moveMapCameraToLocation(it)
                }
            }
        }

        // Zoom to current location
        viewLifecycleOwner.lifecycleScope.launch {
            val currentLocation = mainViewModel.currentLocationInfo.first()
            getMapInstance().animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLocation.locationPoint.latitude,
                    currentLocation.locationPoint.longitude
                ),
                MAP_ZOOM_LEVEL
            ))
        }

        // Display delivery route
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.deliveryRoute.collectLatest { route ->
                currentPolyline?.remove()
                if (route.isNotEmpty()) {
                    currentPolyline = getMapInstance().addPolyline {
                        color(requireContext().themeColor(R.attr.colorPrimary))
                        width(MAP_ROUTE_WIDTH)
                        addAll(route.map { it.toLatLng() })
                    }
                }
            }
        }

        // Initialize bottom sheet
        with(binding.bottomSheetLayout) {
            background = MaterialShapeDrawable(context, null,
                R.attr.bottomSheetStyle, 0
            ).apply {
                fillColor = ColorStateList.valueOf(context.themeColor(R.attr.colorSurface))
                elevation = resources.getDimension(R.dimen.elevation_xmedium)
                initializeElevationOverlay(context)
            }
        }

        // Set bottom sheet peek height
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.bottomSheetPeekHeight.collect { peekHeight ->
                peekHeight?.let {
                    bottomSheetBehavior.setPeekHeight(it, true)
                }
            }
        }

        // Check if arrived destination
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.showArrivedDest.collect { arrived ->
                binding.arrivedDestChip.isVisible = arrived
            }
        }

        return binding.root
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        onLocationChangedListener = listener
    }

    override fun deactivate() {
        onLocationChangedListener = null
    }

    private fun initializeBottomDrawer(sectionId: String?) {
        val bottomDrawerContainer = childFragmentManager.findFragmentById(
            R.id.bottom_drawer_container) as NavHostFragment
        val isTransit = sectionId != null

        with(bottomDrawerContainer.navController) {
            graph = navInflater.inflate(R.navigation.navigation_bottom_drawer).apply {
                startDestination = if (isTransit) {
                    R.id.sectionTransitFragment
                } else {
                    R.id.sectionPendingFragment
                }
            }
            locationViewModel = getHiltNavGraphViewModel(
                navGraphId = R.id.navigation_bottom_drawer,
                navController = this
            )
        }

        binding.actionsScrollView.isVisible = isTransit

        //bottomSheetBehavior.isDraggable = !isTransitMode
    }

    private fun moveMapCameraToLocation(locationPoint: GeolocationPoint) {
        viewLifecycleOwner.lifecycleScope.launch {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                locationPoint.toLatLng(),
                MAP_ZOOM_LEVEL
            )
            getMapInstance().animateCamera(cameraUpdate)
        }
    }

    private suspend fun getMapInstance(): GoogleMap {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container)
            as SupportMapFragment
        return mapFragment.awaitMap()
    }

    private fun showDeliveryModeDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentDeliveryMode = mainViewModel.travelMode.value
            val deliveryModeDialogItems = locationViewModel.deliveryModeDialogItems

            val dialogItemTitles = deliveryModeDialogItems.map {
                it.deliveryModeTitle
            }.toTypedArray()

            val checkedItemIndex = deliveryModeDialogItems.indexOfFirst {
                it.travelMode == currentDeliveryMode
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.travel_mode_dialog_title)
                .setSingleChoiceItems(dialogItemTitles, checkedItemIndex) { dialog, which ->
                    mainViewModel.onUpdateTravelMode(deliveryModeDialogItems[which].travelMode)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showConfirmStartPickupDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_start_pickup_dialog_title)
            .setMessage(R.string.confirm_start_pickup_dialog_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                mainViewModel.onStartSectionPickup()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}