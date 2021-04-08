package com.foobarust.deliverer.ui.sectiondetail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.*
import com.foobarust.deliverer.databinding.FragmentSectionDetailBinding
import com.foobarust.deliverer.ui.bottomdrawer.*
import com.foobarust.deliverer.ui.shared.FullScreenDialogFragment
import com.foobarust.deliverer.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 3/19/21
 */

private const val GOOGLE_MAP_PACKAGE_NAME = "com.google.android.apps.maps"

@AndroidEntryPoint
class SectionDetailFragment : FullScreenDialogFragment(),
    SectionDetailAdapter.SectionDetailAdapterListener {

    private var binding: FragmentSectionDetailBinding by AutoClearedValue(this)
    private val viewModel: SectionDetailViewModel by viewModels()
    private val navArgs: SectionDetailFragmentArgs by navArgs()

    @Inject
    lateinit var packageManager: PackageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchSectionDetail(
                sectionId = navArgs.sectionId,
                mode = navArgs.sectionDetailMode
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSectionDetailBinding.inflate(inflater, container, false).apply {
            root.applyLayoutFullscreen()
            appBarLayout.applySystemWindowInsetsPadding(applyTop = true)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.sectionDetailFragment)?.navigateUp()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onFetchSectionDetail(
                sectionId = navArgs.sectionId,
                mode = navArgs.sectionDetailMode,
                isSwipeRefresh = true
            )
        }

        binding.updateProgressBar.setVisibilityAfterHide(View.GONE)

        // Action button
        with(binding.actionButton) {
            isVisible = navArgs.sectionDetailMode == SectionDetailMode.OVERVIEW
            text = getString(R.string.section_detail_action_button_start_delivery)

            setOnClickListener {
                viewModel.onConfirmApplySectionDelivery()
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchSectionDetail(
                sectionId = navArgs.sectionId,
                mode = navArgs.sectionDetailMode
            )
        }

        // Setup recycler view
        val sectionDetailAdapter = SectionDetailAdapter(this)

        with(binding.sectionDetailRecyclerView) {
            adapter = sectionDetailAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sectionDetailListModels.collectLatest {
                sectionDetailAdapter.submitList(it)
            }
        }

        // Set toolbar title
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sectionDetail.collect {
                binding.toolbar.title = it?.getNormalizedTitle()
            }
        }

        // Loading Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingUiState.collect { uiState ->
                with(binding) {
                    loadErrorLayout.root.isVisible = uiState is SectionDetailUiState.Error
                    loadingProgressBar.isVisible = uiState is SectionDetailUiState.Loading
                }

                if (uiState is SectionDetailUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Update Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateUiState.collect { uiState ->
                if (uiState == null) return@collect
                binding.updateProgressBar.hideIf(uiState !is SectionDetailUiState.Loading)

                when (uiState) {
                    SectionDetailUiState.Success -> {
                        findNavController(R.id.sectionDetailFragment)?.navigateUp()
                    }
                    is SectionDetailUiState.Error -> {
                        showShortToast(uiState.message)
                    }
                    SectionDetailUiState.Loading -> Unit
                }
            }
        }

        // Finish swipe refreshing
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.finishSwipeRefresh.collect {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        // Show confirm dialog
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showApplyConfirmDialog.collect {
                showStartDeliveryConfirmDialog(it)
            }
        }

        // Navigate to section user
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSectionUser.collect {
                findNavController(R.id.sectionDetailFragment)?.navigate(
                    SectionDetailFragmentDirections.actionSectionDetailFragmentToSectionUserFragment(it)
                )
            }
        }

        return binding.root
    }

    override fun onShowSellerDetail() {
        findNavController(R.id.sectionDetailFragment)?.navigate(
            SectionDetailFragmentDirections.actionSectionDetailFragmentToSellerMiscFragment()
        )
    }

    override fun onShowUserOrders() {
        viewModel.onNavigateToSectionUser()
    }

    override fun onShowDeliveryLocationMap(locationPoint: GeolocationPoint) {
        // Open in Google Map
        val intentUri = """
            geo:${locationPoint.latitude},${locationPoint.longitude}
            ?q=${locationPoint.latitude},${locationPoint.longitude}
        """.trimIndent()

        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(intentUri)
            `package` = GOOGLE_MAP_PACKAGE_NAME
        }.let { intent ->
            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            } ?: showShortToast(getString(R.string.error_resolve_activity_failed))
        }
    }

    override fun onCompleteDelivery() {
        viewModel.onCompleteSectionDelivery()
    }

    override fun onCancelDelivery() {
        showCancelDeliveryConfirmDialog()
    }

    private fun showStartDeliveryConfirmDialog(sectionDetail: SellerSectionDetail) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.section_detail_start_delivery_confirm_dialog_title))
            .setMessage(getString(
                R.string.section_detail_start_delivery_confirm_dialog_message,
                sectionDetail.getNormalizedTitle(),
                sectionDetail.getDeliveryDateString(),
                sectionDetail.getDeliveryTimeString(),
                sectionDetail.getNormalizedSellerName()
            ))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onApplySectionDelivery(sectionDetail.id)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showCancelDeliveryConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.section_detail_cancel_delivery_confirm_dialog_title))
            .setMessage(getString(R.string.section_detail_cancel_delivery_confirm_dialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onCancelSectionDelivery()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}