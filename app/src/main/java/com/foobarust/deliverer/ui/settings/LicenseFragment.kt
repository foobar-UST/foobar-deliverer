package com.foobarust.deliverer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentLicenseBinding
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LicenseFragment : Fragment() {

    private var binding: FragmentLicenseBinding by AutoClearedValue(this)
    private val settingsViewModel: SettingsViewModel by hiltNavGraphViewModels(R.id.navigation_settings)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLicenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe dialog back press and navigate up
        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.backPressed.collect {
                findNavController(R.id.licenseFragment)?.navigateUp()
            }
        }

        binding.licenseWebView.run {
            loadUrl("file:///android_asset/open_source_licenses.html")
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
        }
    }
}