package com.foobarust.deliverer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentProfileBinding
import com.foobarust.deliverer.ui.main.MainViewModel
import com.foobarust.deliverer.ui.shared.FullScreenDialogFragment
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import com.foobarust.deliverer.utils.hideIf
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 2/17/21
 */

@AndroidEntryPoint
class ProfileFragment : FullScreenDialogFragment(), ProfileAdapter.ProfileAdapterListener {

    private var binding: FragmentProfileBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var textInputResultObserver: LifecycleEventObserver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.profileFragment)?.navigateUp()
        }

        // Setup recycler view
        val profileAdapter = ProfileAdapter(this)

        binding.profileRecyclerView.run {
            adapter = profileAdapter
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }

        profileViewModel.profileListModels.observe(viewLifecycleOwner) {
            profileAdapter.submitList(it)
        }

        profileViewModel.profileUiState.observe(viewLifecycleOwner) {
            binding.loadingProgressBar.hideIf(it !is ProfileUiState.Loading)

            if (it is ProfileUiState.Error) {
                showShortToast(it.message)
            }
        }

        // Navigate to text input bottom sheet
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.navigateToTextInput.collect {
                // Observe for edit result once the text input dialog is opened
                subscribeTextInputResult(editItemId = it.id)
                findNavController(R.id.profileFragment)?.navigate(
                    ProfileFragmentDirections.actionProfileFragmentToTextInputDialog(it)
                )
            }
        }

        // Show snack bar message
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.snackBarMessage.collect {
                showMessageSnackBar(it)
            }
        }

        // Remove observer when the view is destroyed
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.profileFragment)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                unsubscribeTextInputResult(navBackStackEntry)
            }
        })

        return binding.root
    }

    override fun onProfileAvatarClicked() {
        mainViewModel.onPickUserPhoto()
    }

    override fun onProfileEditItemClicked(editItemModel: ProfileListModel.ProfileEditItemModel) {
        profileViewModel.onNavigateToTextInput(editItemModel)
    }

    private fun subscribeTextInputResult(editItemId: String) {
        // After a configuration change or process death, the currentBackStackEntry
        // points to the dialog destination, so you must use getBackStackEntry()
        // with the specific ID of your destination to ensure we always
        // get the right NavBackStackEntry
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.profileFragment)

        // Remove existing observer
        unsubscribeTextInputResult(navBackStackEntry)

        // Attach new result observer
        textInputResultObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (navBackStackEntry.savedStateHandle.contains(editItemId)) {
                    val result = navBackStackEntry.savedStateHandle.get<String>(editItemId)
                    result?.let {
                        updateInputResult(editItemId = editItemId, result = it)
                    }
                    navBackStackEntry.savedStateHandle.remove<String>(editItemId)
                }

                // Remove observer after obtaining the result
                unsubscribeTextInputResult(navBackStackEntry)
            }
        }.also {
            navBackStackEntry.lifecycle.addObserver(it)
        }
    }

    private fun unsubscribeTextInputResult(navBackStackEntry: NavBackStackEntry) {
        textInputResultObserver?.let {
            navBackStackEntry.lifecycle.removeObserver(it)
        }
    }

    private fun updateInputResult(editItemId: String, result: String) {
        when (editItemId) {
            EDIT_PROFILE_NAME -> profileViewModel.updateUserName(result)
            EDIT_PROFILE_PHONE_NUMBER -> profileViewModel.updateUserPhoneNum(result)
        }
    }

    private fun showMessageSnackBar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}