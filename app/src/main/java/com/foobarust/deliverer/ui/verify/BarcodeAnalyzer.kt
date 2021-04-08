package com.foobarust.deliverer.ui.verify

import androidx.annotation.experimental.UseExperimental
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.foobarust.deliverer.states.Resource
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

/**
 * Created by kevin on 4/4/21
 */

typealias BarcodeResult = (barcodes: Resource<List<Barcode>>) -> Unit

@UseExperimental(markerClass = androidx.camera.core.ExperimentalGetImage::class)
class BarcodeAnalyzer @Inject constructor(
    private val barcodeScanner: BarcodeScanner,
    private val barcodeResult: BarcodeResult
): ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Process image via ML kit
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodeResult(Resource.Success(barcodes))
                }
                .addOnFailureListener { e ->
                    barcodeResult(Resource.Error(e.message))
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}