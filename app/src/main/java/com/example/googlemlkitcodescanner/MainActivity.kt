package com.example.googlemlkitcodescanner

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.googlemlkitcodescanner.ui.theme.GoogleMLKitCodeScannerTheme
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.common.InputImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleMLKitCodeScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        OutlinedButton(onClick = { openQRScanner() }) {
                            Text(text = "Open Camera")
                        }
                        OutlinedButton(onClick = {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Text(text = "Open Gallery")
                        }
                    }
                }
            }
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                scanBarCodeImage(uri)
            }
        }

    private fun openQRScanner() {
        val options = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC
        ).build()
        // val scanner = GmsBarcodeScanning.getClient(this)
        // Or with a configured options
        val scanner = GmsBarcodeScanning.getClient(this, options)
        scanner.startScan().addOnSuccessListener { barcode ->
            // Task completed successfully
            Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show()
        }.addOnCanceledListener {
            // Task canceled
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            // Task failed with an exception
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scanBarCodeImage(uri: Uri) {
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC
        ).build()
        val image = InputImage.fromFilePath(this, uri)

//        val scanner = BarcodeScanning.getClient()
// Or, to specify the formats to recognize:
        val scanner = BarcodeScanning.getClient(options)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    // Task completed successfully
                    Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                // Task failed with an exception
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }
}