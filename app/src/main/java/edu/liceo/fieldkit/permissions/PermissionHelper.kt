package edu.liceo.fieldkit.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

// GIVEN (read it, do not change it)
enum class PermStatus { Granted, NeedsRationale, Denied, NotAsked }

// GIVEN (read it, do not change it)
class PermissionState(val status: PermStatus, val request: () -> Unit)

// GIVEN (read it, do not change it)
fun Context.openAppSettings() = startActivity(
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
)

fun Activity.permStatus(p: String, asked: Boolean): PermStatus {
    // TODO 2a: checkSelfPermission is GRANTED -> return PermStatus.Granted
    if (ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED) {
        return PermStatus.Granted
    }
    // TODO 2b: shouldShowRequestPermissionRationale is true -> return NeedsRationale
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
        return PermStatus.NeedsRationale
    }
    // TODO 2c: asked is true -> return PermStatus.Denied ("don't ask again")
    if (asked) {
        return PermStatus.Denied
    }
    // TODO 2d: otherwise return PermStatus.NotAsked
    return PermStatus.NotAsked
}

@Composable
fun rememberPermission(permission: String): PermissionState {
    val activity = LocalActivity.current ?: error("No Activity found")
    var asked by rememberSaveable { mutableStateOf(false) }
    var status by remember { mutableStateOf(activity.permStatus(permission, asked)) }

    // TODO 3a: launcher setup
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        asked = true
        status = activity.permStatus(permission, true)
    }

    // TODO 3b: LifecycleResumeEffect(permission)
    LifecycleResumeEffect(permission) {
        status = activity.permStatus(permission, asked)
        onPauseOrDispose { }
    }

    // TODO 3c: return PermissionState
    return PermissionState(status) { launcher.launch(permission) }
}