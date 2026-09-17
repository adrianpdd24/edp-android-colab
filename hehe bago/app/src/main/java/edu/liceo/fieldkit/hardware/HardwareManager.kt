package edu.liceo.fieldkit.hardware

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * HardwareManager manages CameraX, Location Services, and Sensors.
 * Implementation for TODO 6 to 12 and 14.
 */
class HardwareManager(private val context: Context) : SensorEventListener {

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    val surfaceRequestState: MutableState<SurfaceRequest?> = mutableStateOf(null)

    // Sensor-related states
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val pitchState = mutableStateOf(0f)
    val rollState = mutableStateOf(0f)
    val locationAccuracyState = mutableStateOf(0f)

    // Shake capture callback
    var onShakeDetected: (() -> Unit)? = null
    private var lastShakeTime: Long = 0

    init {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // TODO 6: Fetch the ProcessCameraProvider instance
    // TODO 7, 8, 9: Initialize Preview and ImageCapture, and bind to lifecycle
    fun startCamera(
        lifecycleOwner: androidx.lifecycle.LifecycleOwner,
        onCameraReady: () -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider { request ->
                    surfaceRequestState.value = request
                }
            }

            // TODO 8: Setup ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
                onCameraReady()
            } catch (exc: Exception) {
                Log.e("HardwareManager", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Toggles the camera torch / flashlight.
     */
    fun toggleTorch(enable: Boolean) {
        camera?.cameraControl?.enableTorch(enable)
    }

    @SuppressLint("MissingPermission")
    fun updateLocationAccuracy() {
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location?.let {
                locationAccuracyState.value = it.accuracy
            }
        }
    }

    // TODO 10 & 11: Fetch location and take photo
    @SuppressLint("MissingPermission")
    fun takePhoto(
        outputDirectory: File,
        onPhotoSaved: (File, Location?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val imageCapture = imageCapture ?: return

        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location != null) {
                locationAccuracyState.value = location.accuracy
            }
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                    .format(System.currentTimeMillis()) + ".jpg"
            )

            // TODO 12: Metadata with location
            val metadata = ImageCapture.Metadata().apply {
                this.location = location
            }

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e("HardwareManager", "Photo capture failed: ${exc.message}", exc)
                        onError(exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        onPhotoSaved(photoFile, location)
                    }
                }
            )
        }.addOnFailureListener {
            onError(it)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val ax = event.values[0]
            val ay = event.values[1]
            val az = event.values[2]

            // Calculate pitch and roll
            pitchState.value = atan2(-ax, sqrt(ay * ay + az * az)) * 57.2957795f
            rollState.value = atan2(ay, az) * 57.2957795f

            // Shake capture detection
            val gForce = sqrt(ax * ax + ay * ay + az * az) / SensorManager.GRAVITY_EARTH
            if (gForce > 2.5f) {
                val now = System.currentTimeMillis()
                if (now - lastShakeTime > 2000) {
                    lastShakeTime = now
                    Log.d("HardwareManager", "Shake capture")
                    onShakeDetected?.invoke()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // TODO 14: Resource cleanup
    fun shutdown() {
        sensorManager.unregisterListener(this)
        cameraExecutor.shutdown()
    }
}
