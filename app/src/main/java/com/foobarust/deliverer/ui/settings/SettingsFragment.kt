package com.foobarust.deliverer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentSettingsBinding
import com.foobarust.deliverer.ui.shared.FullScreenDialogFragment
import com.foobarust.deliverer.utils.getHiltNavGraphViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 3/13/21
 */

@AndroidEntryPoint
class SettingsFragment : FullScreenDialogFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: SettingsViewModel

    override var onBackPressed: (() -> Unit)? = { handleBackPressed() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Setup navigation
        val navHostFragment = childFragmentManager.findFragmentById(R.id.settings_nav_container)
            as NavHostFragment
        navController = navHostFragment.navController.apply {
            setGraph(R.navigation.navigation_settings)
        }

        viewModel = getHiltNavGraphViewModel(
            navGraphId = R.id.navigation_settings,
            navController = navController
        )

        // Set up toolbar
        val appBarConfiguration = AppBarConfiguration.Builder()
            .setFallbackOnNavigateUpListener { handleBackPressed(); true }
            .build()

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        // Show snack bar
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackBarMessage.collect {
                showMessageSnackbar(it)
            }
        }

        return binding.root
    }

    private fun showMessageSnackbar(message: String) {
        Snackbar.make(binding.constraintLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun handleBackPressed() {
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.settingsListFragment) {
            dismiss()
        } else {
            viewModel.onBackPressed()
        }
    }

    companion object {
        const val TAG = "SettingsFragment"

        @JvmStatic
        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}