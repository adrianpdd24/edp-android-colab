package edu.liceo.fieldkit.ui

import android.graphics.BitmapFactory
import android.location.Location
import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import edu.liceo.fieldkit.hardware.HardwareManager
import java.io.File
import java.util.Locale

/**
 * MainScreen for LiceoFieldKit.
 * Implementation for TODO 13.
 */
@Composable
fun MainScreen(
    hardwareManager: HardwareManager,
    outputDirectory: File
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var lastPhotoPath by remember { mutableStateOf<String?>(null) }
    val surfaceRequest by hardwareManager.surfaceRequestState

    val pitch by hardwareManager.pitchState
    val roll by hardwareManager.rollState
    val accuracy by hardwareManager.locationAccuracyState
    var torchEnabled by remember { mutableStateOf(false) }

    // Start Camera and update current info
    LaunchedEffect(lifecycleOwner) {
        hardwareManager.startCamera(lifecycleOwner)
        hardwareManager.updateLocationAccuracy()
    }

    // Set up shake capture trigger
    LaunchedEffect(hardwareManager) {
        hardwareManager.onShakeDetected = {
            hardwareManager.takePhoto(
                outputDirectory = outputDirectory,
                onPhotoSaved = { file, location ->
                    lastLocation = location
                    lastPhotoPath = file.absolutePath
                    Toast.makeText(context, "Shake captured!\nSaved to: ${file.name}", Toast.LENGTH_LONG).show()
                },
                onError = { exc ->
                    Toast.makeText(context, "Error: ${exc.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // TODO 7: Camera Preview using CameraXViewfinder
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay for Location info
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = String.format(Locale.US, "Lat: %.6f", lastLocation?.latitude ?: 0.0),
                color = Color.White
            )
            Text(
                text = String.format(Locale.US, "Lon: %.6f", lastLocation?.longitude ?: 0.0),
                color = Color.White
            )
            Text(
                text = String.format(Locale.US, "± %.1fm accuracy", accuracy),
                color = Color.LightGray
            )
            Text(
                text = "Location access: Precise",
                color = Color.Green
            )
        }

        // Level Card Overlay (05-level.png requirement)
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Level Tool", color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(String.format(Locale.US, "Pitch: %.1f°", pitch), color = Color.Yellow)
                Text(String.format(Locale.US, "Roll: %.1f°", roll), color = Color.Yellow)
            }
        }

        // Bottom Operations Panel
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Torch control button
            Button(
                onClick = {
                    torchEnabled = !torchEnabled
                    hardwareManager.toggleTorch(torchEnabled)
                }
            ) {
                Text(if (torchEnabled) "Torch off" else "Torch on")
            }

            // Capture Button
            Button(
                onClick = {
                    hardwareManager.takePhoto(
                        outputDirectory = outputDirectory,
                        onPhotoSaved = { file, location ->
                            lastLocation = location
                            lastPhotoPath = file.absolutePath
                            Toast.makeText(context, "Saved to: ${file.name}\nLat: ${location?.latitude}, Lon: ${location?.longitude}", Toast.LENGTH_LONG).show()
                        },
                        onError = { exc ->
                            Toast.makeText(context, "Error: ${exc.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            ) {
                Text("Capture Observation")
            }
        }

        // Bonus: Show last photo thumbnail with "Saved:" descriptor
        lastPhotoPath?.let { path ->
            val file = File(path)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Saved: ${file.name}",
                    color = Color.Yellow,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                val bitmap = remember(path) {
                    BitmapFactory.decodeFile(path)
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Last captured photo",
                        modifier = Modifier
                            .size(90.dp)
                            .background(Color.White)
                            .padding(2.dp)
                            .clickable {
                                Toast.makeText(context, "Opening photo: ${file.name}", Toast.LENGTH_SHORT).show()
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
