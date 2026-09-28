package edu.liceo.fieldkit.hardware

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun rememberAccelerometer(): FloatArray {
    val context = LocalContext.current
    var values by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    LifecycleResumeEffect(Unit) {
        val sm = context.getSystemService(SensorManager::class.java)
        val sensor = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                // TODO 5a: copy values array
                values = e.values.clone()
            }

            override fun onAccuracyChanged(s: Sensor?, accuracy: Int) = Unit
        }

        // TODO 5b: register listener
        if (sensor != null) {
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        }

        onPauseOrDispose {
            // TODO 5c: unregister listener
            sm?.unregisterListener(listener)
        }
    }

    return values
}