package com.foobarust.deliverer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentLicenseBinding
import com.foobarust.deliverer.ui.shared.FullScreenDialogFragment
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController

class LicenseFragment : FullScreenDialogFragment() {

    private var binding: FragmentLicenseBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLicenseBinding.inflate(inflater, container, false)

        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.licenseFragment)?.navigateUp()
        }

        with(binding.licenseWebView) {
            loadUrl("file:///android_asset/open_source_licenses.html")
            settings.apply {
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
            }
        }

        return binding.root
    }
}