package com.foobarust.deliverer.ui.bottomdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.getNormalizedName
import com.foobarust.deliverer.databinding.FragmentSectionPendingBinding
import com.foobarust.deliverer.ui.location.LocationViewModel
import com.foobarust.deliverer.ui.sectiondetail.SectionDetailMode
import com.foobarust.deliverer.ui.shared.PagingLoadStateAdapter
import com.foobarust.deliverer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 3/8/21
 */

@AndroidEntryPoint
class SectionPendingFragment : Fragment(), SectionPendingAdapter.SectionPendingAdapterListener {

    private var binding: FragmentSectionPendingBinding by AutoClearedValue(this)
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.navigation_bottom_drawer)
    private val sectionPendingViewModel: SectionPendingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSectionPendingBinding.inflate(inflater, container, false)

        // Setup recycler view
        val sectionPendingAdapter = SectionPendingAdapter(this)

        with(binding.sectionsRecyclerView) {
            adapter = sectionPendingAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sectionPendingAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        // Prevent the last item in recycler view get covered
        with(binding) {
            headerLayout.doOnLayout { headerLayout ->
                sectionsRecyclerView.setPadding(
                    0, 0, 0,  headerLayout.height
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sectionPendingViewModel.sectionPendingListModels.collectLatest {
                sectionPendingAdapter.submitData(it)
            }
        }

        // Seller detail
        viewLifecycleOwner.lifecycleScope.launch {
            sectionPendingViewModel.sellerDetail.collect { sellerDetail ->
                if (sellerDetail == null) return@collect

                binding.headerSellerNameTextView.text = getString(
                    R.string.section_pending_seller_title,
                    sellerDetail.getNormalizedName()
                )

                binding.headerSellerImageView.loadGlideUrl(
                    imageUrl = sellerDetail.imageUrl,
                    centerCrop = true,
                    circularCrop = true,
                    placeholder = R.drawable.placeholder_card
                )
            }
        }

        // Control views with respect to load states
        sectionPendingAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.sectionsRecyclerView,
                    progressBar = binding.loadingProgressBar,
                    errorLayout = binding.loadErrorLayout.root
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sectionPendingViewModel.onRefreshSectionPending()
        }

        // Show toast
        viewLifecycleOwner.lifecycleScope.launch {
            sectionPendingViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        // Set bottom sheet peek height
        binding.headerLayout.doOnLayout {
            locationViewModel.onUpdateBottomSheetPeekHeight(
                peekHeight = it.height
            )
        }

        return binding.root
    }

    override fun onSectionClicked(sectionId: String) {
        findNavController(R.id.sectionPendingFragment)?.navigate(
            SectionPendingFragmentDirections.actionSectionPendingFragmentToSectionDetailFragment(
                sectionId,
                SectionDetailMode.OVERVIEW
            )
        )
    }
}