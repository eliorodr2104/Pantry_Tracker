package com.project.pantrytracker.BarcodeScanner

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.TimeUnit

/**
 * Analizzatore personalizzato per il rilevamento dei codici a barre con l'utilizzo della fotocamera.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * Questa classe implementa l'interfaccia ImageAnalysis.Analyzer per elaborare le immagini
 * provenienti dalla fotocamera e rilevare i codici a barre utilizzando la scansione
 * dei codici a barre di ML Kit.
 *
 * @param onBarcodeDetected Funzione di callback per gestire i codici a barre rilevati.
 */
@SuppressLint("UnsafeOptInUsageError")
class BarCodeAnalyser(
    private val onBarcodeDetected: (barcodes: List<Barcode>) -> Unit
): ImageAnalysis.Analyzer {
    private var lastAnalyzedTimeStamp = 0L

    /**
     * Analizza ciascun frame dal feed della fotocamera per il rilevamento dei codici a barre.
     * @author Eliomar Alejandro Rodriguez Ferrer.
     *
     * @param image Oggetto ImageProxy che rappresenta il frame corrente dal feed della fotocamera.
     */
    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimeStamp >= TimeUnit.SECONDS.toMillis(1)) {
            image.image?.let { imageToAnalyze ->
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()

                val barcodeScanner = BarcodeScanning.getClient(options)
                val imageToProcess = InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)

                barcodeScanner.process(imageToProcess)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            onBarcodeDetected(barcodes)
                        } else {
                            Log.d("TAG", "analyze: Nessun codice a barre rilevato")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "BarcodeAnalyser: Si è verificato un errore $exception")
                    }
                    .addOnCompleteListener {
                        image.close()
                    }
            }
            lastAnalyzedTimeStamp = currentTimestamp
        } else {
            image.close()
        }
    }
}
