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
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentSettingsBinding
import com.foobarust.deliverer.ui.main.MainViewModel
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.applySystemWindowInsetsPadding
import com.foobarust.deliverer.utils.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 3/13/21
 */

@AndroidEntryPoint
class SettingsFragment : Fragment(), SettingsAdapter.SettingsAdapterListener {

    private var binding: FragmentSettingsBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var packageManager: PackageManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Set toolbar
        binding.appBarLayout.applySystemWindowInsetsPadding(applyTop = true)

        // Setup recycler view
        val settingsAdapter = SettingsAdapter(this)

        binding.settingsRecyclerView.run {
            adapter = settingsAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.settingsListModels.collect {
                settingsAdapter.submitList(it)
            }
        }

        // Ui state
        settingsViewModel.settingsUiState.observe(viewLifecycleOwner) {
            binding.loadingProgressBar.isVisible = it == SettingsUiState.LOADING
        }

        // Show snack bar
        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.snackBarMessage.collect {
                showMessageSnackbar(it)
            }
        }

        return binding.root
    }

    override fun onProfileClicked() {
        findNavController(R.id.settingsFragment)?.navigate(
            SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
        )
    }

    override fun onSectionItemClicked(sectionId: String) {
        when (sectionId) {
            SETTINGS_EMPLOYED_BY -> findNavController(R.id.settingsFragment)?.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToSellerMiscFragment()
            )
            SETTINGS_CONTACT_US -> sendContactUsEmail()
            SETTINGS_TERMS_CONDITIONS -> findNavController(R.id.settingsFragment)?.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToLicenseFragment()
            )
            SETTINGS_SIGN_OUT -> showSignOutConfirmDialog()
        }
    }

    private fun showMessageSnackbar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
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