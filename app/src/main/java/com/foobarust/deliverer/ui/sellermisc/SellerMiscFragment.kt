package com.foobarust.deliverer.ui.sellermisc

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.*
import com.foobarust.deliverer.databinding.FragmentSellerMiscBinding
import com.foobarust.deliverer.ui.shared.AppConfig.MAP_ZOOM_LEVEL
import com.foobarust.deliverer.ui.shared.FullScreenDialogFragment
import com.foobarust.deliverer.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerMiscFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerMiscBinding by AutoClearedValue(this)
    private val viewModel: SellerMiscViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchSellerDetail()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerMiscBinding.inflate(inflater, container, false)

        // Extend map to fullscreen
        with(binding) {
            root.applyLayoutFullscreen()
            toolbarLayout.applySystemWindowInsetsPadding(applyTop = true)
        }

        // Attach map fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, SupportMapFragment.newInstance())
            .commitNow()

        // Set map night mode
        viewLifecycleOwner.lifecycleScope.launch {
            if (requireContext().isNightModeOn()) {
                getSupportMapFragment()?.awaitMap()?.run {
                    val nightStyle = MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.night_map_style
                    )
                    setMapStyle(nightStyle)
                }
            }
        }

        // Add seller coordinate
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerLocation.collect { location ->
                location?.let {
                    getSupportMapFragment()?.awaitMap()?.run {
                        addMarker { position(it) }
                        moveCamera(CameraUpdateFactory.newLatLngZoom(it, MAP_ZOOM_LEVEL))
                    }
                }
            }
        }

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.sellerMiscFragment)?.navigateUp()
        }

        // Setup bottom sheet
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        with(binding.bottomSheet) {
            background = MaterialShapeDrawable(context, null,
                R.attr.bottomSheetStyle, 0
            ).apply {
                fillColor = ColorStateList.valueOf(
                    context.themeColor(R.attr.colorSurface)
                )
                elevation = resources.getDimension(R.dimen.elevation_xmedium)
                initializeElevationOverlay(context)
            }
            applySystemWindowInsetsMargin(applyTop = true)
        }

        // Retry button
        binding.retryButton.setOnClickListener {
            viewModel.onFetchSellerDetail()
        }

        // Set bottom sheet peek height
        viewLifecycleOwner.lifecycleScope.launch {
            bottomSheetPeekTo(
                bottomSheet = binding.bottomSheet,
                toView = binding.headerGroup
            )
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerMiscUiState.collect { uiState ->
                bottomSheetBehavior.hideIf(uiState !is SellerMiscUiState.Success)

                with(binding) {
                    loadingProgressBar.hideIf(uiState !is SellerMiscUiState.Loading)
                    retryButton.isVisible = uiState is SellerMiscUiState.Error
                }

                when (uiState) {
                    is SellerMiscUiState.Success -> setupSellerMiscLayout(uiState.sellerDetail)
                    is SellerMiscUiState.Error -> showShortToast(uiState.message)
                    SellerMiscUiState.Loading -> Unit
                }
            }
        }

        return binding.root
    }

    private fun setupSellerMiscLayout(sellerDetail: SellerDetail) = binding.run {
        titleTextView.text = sellerDetail.getNormalizedName()

        ratingBar.rating = sellerDetail.orderRating.toFloat()
        ratingTextView.text = sellerDetail.getNormalizedOrderRating()

        with(phoneNumTextView) {
            text = sellerDetail.phoneNum
            drawableFitVertical()
        }

        with(websiteTextView) {
            text = sellerDetail.website
            isGone = sellerDetail.website.isNullOrBlank()
            drawableFitVertical()
        }

        addressTextView.text = sellerDetail.getNormalizedAddress()
        openingHoursTextView.text = sellerDetail.openingHours

        descriptionSubtitleTextView.isGone = sellerDetail.description.isNullOrBlank()
        descriptionTextView.text = sellerDetail.getNormalizedDescription()
        descriptionTextView.isGone = sellerDetail.description.isNullOrBlank()
    }

    private fun getSupportMapFragment(): SupportMapFragment? {
        return childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
    }
}