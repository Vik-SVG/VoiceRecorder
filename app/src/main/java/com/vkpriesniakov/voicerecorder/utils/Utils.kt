package com.vkpriesniakov.voicerecorder.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.vkpriesniakov.voicerecorder.databinding.FragmentRecordBinding

class Utils {
    companion object {

        fun showSettingsSnackbar(
            binding: FragmentRecordBinding,
            activity: Activity,
            activityResult: ActivityResultLauncher<Intent>
        ) {
            val snackbar = Snackbar.make(
                binding.root,
                "Please, allow recording",
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction("Settings", View.OnClickListener {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri =
                    Uri.fromParts("package", (activity).packageName, null)
                intent.data = uri
                activityResult.launch(intent)
            })
            snackbar.show()
        }

        fun checkIfPermissionGranted(context: Context?): Boolean {
            return ContextCompat.checkSelfPermission(
                context as Context,
                RECORD_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }

    }
}