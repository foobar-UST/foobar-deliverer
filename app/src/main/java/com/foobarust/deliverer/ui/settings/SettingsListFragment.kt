package com.foobarust.deliverer.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentSettingsListBinding
import com.foobarust.deliverer.ui.main.MainViewModel
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/17/21
 */

@AndroidEntryPoint
class SettingsListFragment : Fragment(), SettingsAdapter.SettingsAdapterListener {

    private var binding: FragmentSettingsListBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by hiltNavGraphViewModels(R.id.navigation_settings)
    private val settingsListViewModel: SettingsListViewModel by viewModels()

    @Inject
    lateinit var packageManager: PackageManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsListBinding.inflate(inflater, container, false)

        // Setup recycler view
        val settingsAdapter = SettingsAdapter(this)

        binding.settingsRecyclerView.run {
            adapter = settingsAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsListViewModel.settingsListModels.collect {
                settingsAdapter.submitList(it)
            }
        }

        // Ui state
        settingsListViewModel.settingsUiState.observe(viewLifecycleOwner) {
            binding.loadingProgressBar.isVisible = it == SettingsUiState.LOADING
        }

        // Show toast message
        viewLifecycleOwner.lifecycleScope.launch {
            settingsListViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        return binding.root
    }

    override fun onProfileClicked() {
        findNavController(R.id.settingsListFragment)?.navigate(
            SettingsListFragmentDirections.actionSettingsListFragmentToProfileFragment()
        )
    }

    override fun onSectionItemClicked(sectionId: String) {
        when (sectionId) {
            SETTINGS_EMPLOYED_BY -> findNavController(R.id.settingsListFragment)?.navigate(
                SettingsListFragmentDirections.actionSettingsListFragmentToSellerMiscFragment()
            )
            SETTINGS_CONTACT_US -> sendContactUsEmail()
            SETTINGS_TERMS_CONDITIONS -> findNavController(R.id.settingsListFragment)?.navigate(
                SettingsListFragmentDirections.actionSettingsListFragmentToLicenseFragment()
            )
            SETTINGS_SIGN_OUT -> showSignOutConfirmDialog()
        }
    }


    private fun sendContactUsEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kthon@connect.ust.hk"))
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showShortToast(getString(R.string.error_resolve_activity_failed))
        }
    }

    private fun showSignOutConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sign_out_dialog_title))
            .setMessage(getString(R.string.sign_out_dialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> mainViewModel.onUserSignOut() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}