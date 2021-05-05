package com.foobarust.deliverer.ui.verify

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.ActivityVerifyDeliveryBinding
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.utils.applySystemWindowInsetsPadding
import com.foobarust.deliverer.utils.buildApplicationDetailSettingsIntent
import com.foobarust.deliverer.utils.isPermissionGranted
import com.foobarust.deliverer.utils.setLayoutFullscreen
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Created by kevin on 4/2/21
 */

private const val TAG = "VerifyDelivery"

@AndroidEntryPoint
class VerifyDeliveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyDeliveryBinding
    private val viewModel: VerifyDeliveryViewModel by viewModels()
    private val navArgs: VerifyDeliveryActivityArgs by navArgs()

    @Inject
    lateinit var barcodeScanner: BarcodeScanner

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            handlePermissionGranted()
        } else {
            showCameraPermissionPermanentDeniedSnackbar()
        }
    }

    private var cameraProvider: ProcessCameraProvider? = null

    private val cameraConfig: CameraConfig by lazy {
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val imageCapture = ImageCapture.Builder().build()

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.scanPreviewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val barcodeAnalyzer = BarcodeAnalyzer(barcodeScanner) { result ->
            when (result) {
                is Resource.Success -> {
                    val firstCapture = result.data.firstOrNull()?.displayValue
                        ?: return@BarcodeAnalyzer

                    if (viewModel.verifyDeliveryUiState.value == VerifyDeliveryUiState.Capturing) {
                        viewModel.onReceiveCaptureResult(
                            orderId = navArgs.orderId,
                            verifyCode = navArgs.verifyCode,
                            content = firstCapture
                        )
                    }
                }
                is Resource.Error -> {
                    showShortToast(result.message)
                }
                is Resource.Loading -> Unit
            }
        }

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), barcodeAnalyzer)
        CameraConfig(cameraSelector, preview, imageCapture, imageAnalysis)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutFullscreen(aboveNavBar = true)

        binding = ActivityVerifyDeliveryBinding.inflate(layoutInflater).apply {
            setContentView(root)
            appBarLayout.applySystemWindowInsetsPadding(applyTop = true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Ui state
        lifecycleScope.launchWhenStarted {
            viewModel.verifyDeliveryUiState.collect { uiState ->
                when (uiState) {
                    is VerifyDeliveryUiState.Processing -> {
                        // Unbind camera
                        cameraProvider?.unbindAll()
                    }
                    VerifyDeliveryUiState.Capturing -> {
                        // Bind camera
                        cameraProvider?.unbindAll()
                        cameraProvider?.bindToLifecycle(
                            this@VerifyDeliveryActivity,
                            cameraConfig.cameraSelector, cameraConfig.cameraPreview,
                            cameraConfig.imageCapture, cameraConfig.imageAnalysis
                        )
                    }
                    VerifyDeliveryUiState.VerifyCompleted -> {
                        finish()
                    }
                }
            }
        }

        // Toast message
        lifecycleScope.launchWhenStarted {
            viewModel.toastMessage.collect {
                showShortToast(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Check permission before starting camera
        if (isPermissionGranted(Manifest.permission.CAMERA)) {
            handlePermissionGranted()
        } else {
            handlePermissionMissing()
        }
    }

    private fun handlePermissionGranted() {
        configViewsForCamera()
        startCamera()
    }

    private fun handlePermissionMissing() {
        configViewForMissingPermission()
        requestCameraPermission()
    }

    private fun configViewsForCamera() = with(binding) {
        permissionMissingLayout.isVisible = false
        scanHintTextView.isVisible = true
    }

    private fun configViewForMissingPermission() = with(binding) {
        binding.permissionMissingLayout.isVisible = true
        binding.scanHintTextView.isVisible = false
    }

    private fun startCamera() {
        configViewsForCamera()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Unbind use cases before rebinding
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraConfig.cameraSelector, cameraConfig.cameraPreview,
                cameraConfig.imageCapture, cameraConfig.imageAnalysis
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestCameraPermission() {
        if (isPermissionGranted(Manifest.permission.CAMERA)) {
            showCameraPermissionTemporaryDeniedSnackbar()
        } else {
            startRequestCameraPermission()
        }
    }

    private fun startRequestCameraPermission() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun showCameraPermissionTemporaryDeniedSnackbar() {
        Snackbar.make(
            binding.constraintLayout,
            R.string.request_camera_permission_message,
            Snackbar.LENGTH_LONG
        )
            .setAction(android.R.string.ok) {
                startRequestCameraPermission()
            }
            .show()
    }

    private fun showCameraPermissionPermanentDeniedSnackbar() {
        Snackbar.make(
            binding.constraintLayout,
            R.string.request_camera_permission_message,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.request_permission_action_settings) {
                startActivity(buildApplicationDetailSettingsIntent())
            }
            .show()
    }
}

data class CameraConfig(
    val cameraSelector: CameraSelector,
    val cameraPreview: Preview,
    val imageCapture: ImageCapture,
    val imageAnalysis: ImageAnalysis
)