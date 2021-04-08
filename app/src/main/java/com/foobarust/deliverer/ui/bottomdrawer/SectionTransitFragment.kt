package com.foobarust.deliverer.ui.bottomdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentSectionTransitBinding
import com.foobarust.deliverer.ui.location.LocationViewModel
import com.foobarust.deliverer.ui.main.MainViewModel
import com.foobarust.deliverer.ui.sectiondetail.SectionDetailMode
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import com.foobarust.deliverer.utils.hideIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 3/19/21
 */

@AndroidEntryPoint
class SectionTransitFragment : Fragment() {

    private var binding: FragmentSectionTransitBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.navigation_bottom_drawer)
    private val sectionTransitViewModel: SectionTransitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSectionTransitBinding.inflate(inflater, container, false)

        // Navigate to section detail
        binding.sectionDetailButton.setOnClickListener {
            sectionTransitViewModel.onNavigateToSectionDetail()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sectionTransitViewModel.navigateToSectionDetail.collect { sectionId ->
                findNavController(R.id.sectionTransitFragment)?.navigate(
                    SectionTransitFragmentDirections.actionSectionTransitFragmentToSectionDetailFragment(
                        sectionId,
                        SectionDetailMode.TRANSIT
                    )
                )
            }
        }

        // Get deliverer location
        viewLifecycleOwner.lifecycleScope.launch {
            sectionTransitViewModel.sectionDetail.collect { sectionDetail ->
                sectionDetail?.let {
                    mainViewModel.onUpdateSectionDetail(it)
                }
            }
        }

        // Delivery time left
        viewLifecycleOwner.lifecycleScope.launch {
            sectionTransitViewModel.deliveryTimeRemainTitle.collect {
                binding.deliveryTimeLeftTextView.text = it
            }
        }

        // Delivery address
        viewLifecycleOwner.lifecycleScope.launch {
            sectionTransitViewModel.deliveryAddress.collect {
                binding.deliveryLocationTextView.text = it
            }
        }

        // Ui State
        viewLifecycleOwner.lifecycleScope.launch {
            sectionTransitViewModel.sectionTransitUiState.collect { uiState ->
                with(binding) {
                    loadSuccessGroup.isGone = uiState is SectionTransitUiState.Error
                    loadErrorLayout.root.isVisible = uiState is SectionTransitUiState.Error
                    loadingProgressBar.hideIf(uiState !is SectionTransitUiState.Loading)
                }

                if (uiState is SectionTransitUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sectionTransitViewModel.onFetchSectionTransit()
        }

        // Set bottom sheet peek height
        binding.root.doOnLayout {
            locationViewModel.onUpdateBottomSheetPeekHeight(
                peekHeight = it.height
            )
        }

        return binding.root
    }
}