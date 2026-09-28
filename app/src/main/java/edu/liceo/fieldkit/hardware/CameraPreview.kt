package edu.liceo.fieldkit.hardware

import android.content.Context
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun ProcessCameraProvider.Companion.awaitInstance(context: Context): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            try {
                continuation.resume(future.get())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))
        continuation.invokeOnCancellation {
            future.cancel(true)
        }
    }

@Composable
fun CameraPreview(
    capture: ImageCapture,
    modifier: Modifier = Modifier,
    onCameraReady: (Camera) -> Unit = {}
) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var request by remember { mutableStateOf<SurfaceRequest?>(null) }

    LaunchedEffect(owner) {
        val provider = ProcessCameraProvider.awaitInstance(context)

        // TODO 7a
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider { req -> request = req }
        }

        // TODO 7b
        provider.unbindAll()
        val camera = provider.bindToLifecycle(
            owner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            capture
        )

        // TODO 7c
        onCameraReady(camera)

        try { awaitCancellation() } finally { provider.unbindAll() }
    }

    request?.let {
        CameraXViewfinder(surfaceRequest = it, modifier = modifier)
    }
}