package com.foobarust.deliverer.ui.sectionuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentSectionUserDetailBinding
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 4/2/21
 */

@AndroidEntryPoint
class SectionUserDetailFragment : Fragment(),
    SectionUserDetailAdapter.SectionUserDetailAdapterListener {

    private var binding: FragmentSectionUserDetailBinding by AutoClearedValue(this)
    private val sectionUserViewModel: SectionUserViewModel by navGraphViewModels(R.id.navigation_section_user)
    private val sectionUserDetailViewModel: SectionUserDetailViewModel by viewModels()
    private val navArgs: SectionUserDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            sectionUserDetailViewModel.onFetchOrderDetail(navArgs.property)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSectionUserDetailBinding.inflate(inflater, container, false)

        val sectionUserDetailAdapter = SectionUserDetailAdapter(this)

        with(binding.userDetailRecyclerView) {
            adapter = sectionUserDetailAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sectionUserDetailViewModel.sectionUserDetailListModels.collectLatest {
                sectionUserDetailAdapter.submitList(it)
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sectionUserDetailViewModel.onFetchOrderDetail(navArgs.property)
        }

        // Swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            sectionUserDetailViewModel.onFetchOrderDetail(navArgs.property)
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            sectionUserDetailViewModel.sectionUserDetailUiState.collect { uiState ->
                with(binding) {
                    loadingProgressBar.isVisible = uiState is SectionUserDetailUiState.Loading
                    loadErrorLayout.root.isVisible = uiState is SectionUserDetailUiState.Error
                }

                if (uiState is SectionUserDetailUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Back pressed
        viewLifecycleOwner.lifecycleScope.launch {
            sectionUserViewModel.backPressed.collect {
                findNavController(R.id.sectionUserDetailFragment)?.navigateUp()
            }
        }

        // Toast message
        viewLifecycleOwner.lifecycleScope.launch {
            sectionUserDetailViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        // Finish swipe refresh
        viewLifecycleOwner.lifecycleScope.launch {
            sectionUserDetailViewModel.finishSwipeRefresh.collect {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding.root
    }

    override fun onDeliverOrderManual() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_order_delivered_dialog_title)
            .setMessage(R.string.confirm_order_delivered_dialog_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                sectionUserDetailViewModel.onConfirmOrderDelivered()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDeliverOrderScan() {
        sectionUserDetailViewModel.orderDetail.value?.let { orderDetail ->
            findNavController(R.id.sectionUserDetailFragment)?.navigate(
                SectionUserDetailFragmentDirections
                    .actionSectionUserDetailFragmentToVerifyDeliveryActivity(
                        orderId = orderDetail.id,
                        verifyCode = orderDetail.verifyCode
                    )
            )
        }
    }

    override fun onContactUser(userId: String) {
        findNavController(R.id.sectionUserDetailFragment)?.navigate(
            SectionUserDetailFragmentDirections.actionSectionUserDetailFragmentToUserInfoFragment(userId)
        )
    }
}