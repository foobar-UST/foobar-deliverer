package com.foobarust.deliverer.ui.verify

import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Created by kevin on 4/4/21
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object VerifyDeliveryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideBarcodeScannerOptions(): BarcodeScannerOptions {
        return BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideBarcodeScanner(options: BarcodeScannerOptions): BarcodeScanner {
        return BarcodeScanning.getClient(options)
    }
}