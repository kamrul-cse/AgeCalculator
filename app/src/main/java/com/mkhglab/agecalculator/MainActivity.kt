package com.mkhglab.agecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mkhglab.agecalculator.ui.theme.AgeCalculatorTheme
import com.mkhglab.agecalculator.tools.AppUpdateChecker
import com.google.android.gms.ads.MobileAds
import android.util.Log

class MainActivity : ComponentActivity() {
    private var appUpdateChecker: AppUpdateChecker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        appUpdateChecker = AppUpdateChecker(this).also {
            setupUpdateCallback(it)
            it.checkForUpdate()
        }
        setContent {
            AgeCalculatorApp()
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateChecker?.onResume()
    }

    override fun onDestroy() {
        appUpdateChecker?.onDestroy()
        super.onDestroy()
    }

    private fun setupUpdateCallback(updateChecker: AppUpdateChecker) {
        updateChecker.setUpdateCallback(object : AppUpdateChecker.UpdateCallback {
            override fun onUpdateAvailable(updateInfo: com.google.android.play.core.appupdate.AppUpdateInfo) {
                Log.d("AppUpdate", "Update available")
            }

            override fun onNoUpdateAvailable() {
                Log.d("AppUpdate", "No update available")
            }

            override fun onUpdateError(exception: Exception) {
                Log.e("AppUpdate", "Update error", exception)
            }

            override fun onUpdateDownloaded() {
                Log.d("AppUpdate", "Update downloaded")
            }

            override fun onUpdateInstalling() {
                Log.d("AppUpdate", "Update installing")
            }
        })
    }
}
