package edu.liceo.fieldkit.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.liceo.fieldkit.hardware.HardwareManager
import edu.liceo.fieldkit.permissions.PermissionManager
import java.io.File

/**
 * MainActivity for LiceoFieldKit.
 * Handles permissions, hardware initialization, and UI binding.
 * Implementation for TODO 5 and TODO 13.
 */
class MainActivity : ComponentActivity() {

    private lateinit var hardwareManager: HardwareManager
    private var permissionState by mutableStateOf("INITIAL")

    // TODO 5: Activity Result Launcher for runtime permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        updatePermissionState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        hardwareManager = HardwareManager(this)
        updatePermissionState()
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (permissionState) {
                        "GRANTED" -> MainScreen(
                            hardwareManager = hardwareManager,
                            outputDirectory = getOutputDirectory()
                        )
                        "RATIONALE" -> RationaleScreen(
                            onRetry = { requestPermissionLauncher.launch(PermissionManager.REQUIRED_PERMISSIONS) }
                        )
                        "BLOCKED" -> BlockedScreen(
                            onOpenSettings = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                                startActivity(intent)
                            }
                        )
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionState()
    }

    private fun updatePermissionState() {
        permissionState = when {
            PermissionManager.allPermissionsGranted(this) -> "GRANTED"
            PermissionManager.shouldShowRationale(this) -> "RATIONALE"
            permissionState != "INITIAL" -> "BLOCKED" // Transitioned from request and still not granted/rationale
            else -> {
                requestPermissionLauncher.launch(PermissionManager.REQUIRED_PERMISSIONS)
                "INITIAL"
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "LiceoFieldKit").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO 14: Resource cleanup
        hardwareManager.shutdown()
    }
}

@Composable
fun RationaleScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera and Location permissions are essential for this field kit to capture observations with metadata.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Try again")
        }
    }
}

@Composable
fun BlockedScreen(onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permissions are blocked. Please enable them in system settings to use the app.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onOpenSettings) {
            Text("Open Settings")
        }
    }
}
