package com.foobarust.deliverer.ui.sectionuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentSectionUserListBinding
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 4/2/21
 */

@AndroidEntryPoint
class SectionUserListFragment : Fragment(), SectionUserListAdapter.SectionUserListAdapterListener {

    private var binding: FragmentSectionUserListBinding by AutoClearedValue(this)
    private val sectionUserListViewModel: SectionUserListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSectionUserListBinding.inflate(inflater, container, false)

        val sectionUserListAdapter = SectionUserListAdapter(this)

        with(binding.joinedUsersRecyclerView) {
            adapter = sectionUserListAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sectionUserListViewModel.sectionUserListModels.collectLatest {
                sectionUserListAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sectionUserListViewModel.sectionUserListUiState.collect { uiState ->
                with(binding) {
                    loadingProgressBar.isVisible = uiState is SectionUserListUiState.Loading
                    loadErrorLayout.root.isVisible = uiState is SectionUserListUiState.Error
                    swipeRefreshLayout.isRefreshing = false
                }

                if (uiState is SectionUserListUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sectionUserListViewModel.onRefreshSectionUsers()
        }

        // Swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            sectionUserListViewModel.onRefreshSectionUsers(isSwipeRefresh = true)
        }

        return binding.root
    }

    override fun onUserOrderClicked(orderId: String, username: String, userPhotoUrl: String?) {
        findNavController(R.id.sectionUserListFragment)?.navigate(
            SectionUserListFragmentDirections.actionSectionUserListFragmentToSectionUserDetailFragment(
                SectionUserDetailProperty(orderId, username, userPhotoUrl)
            )
        )
    }
}