package com.mkhglab.agecalculator.tools

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Mirrors the Azan app's in-app update flow: checks on launch, supports immediate/flexible,
 * resumes in-progress updates, and auto-completes when downloaded.
 */
class AppUpdateChecker(private val activity: Activity) {

    companion object {
        private const val TAG = "AppUpdateChecker"
        private const val UPDATE_REQUEST_CODE = 1001
    }

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(activity)
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        handleInstallState(state)
    }

    private var cachedUpdateInfo: AppUpdateInfo? = null
    private var isUpdateFlowLaunched = false
    private val autoCompleteOnDownload = true

    interface UpdateCallback {
        fun onUpdateAvailable(updateInfo: AppUpdateInfo)
        fun onNoUpdateAvailable()
        fun onUpdateError(exception: Exception)
        fun onUpdateDownloaded()
        fun onUpdateInstalling()
    }

    private var callback: UpdateCallback? = null

    fun setUpdateCallback(callback: UpdateCallback) {
        this.callback = callback
    }

    fun checkForUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            try {
                when (appUpdateInfo.updateAvailability()) {
                    UpdateAvailability.UPDATE_AVAILABLE -> {
                        Log.d(TAG, "Update available")
                        cachedUpdateInfo = appUpdateInfo
                        startUpdate()
                        callback?.onUpdateAvailable(appUpdateInfo)
                    }
                    UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                        Log.d(TAG, "No update available")
                        cachedUpdateInfo = null
                        callback?.onNoUpdateAvailable()
                    }
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                        Log.d(TAG, "Update already in progress")
                        cachedUpdateInfo = appUpdateInfo
                        resumeUpdate(appUpdateInfo)
                    }
                    else -> {
                        Log.d(TAG, "Unknown update availability status")
                        callback?.onNoUpdateAvailable()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking for update", e)
                callback?.onUpdateError(e)
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to check for update", exception)
            callback?.onUpdateError(exception)
        }
    }

    fun startUpdate() {
        val updateInfo = cachedUpdateInfo
        if (updateInfo == null) {
            Log.w(TAG, "startUpdate called without cached AppUpdateInfo")
            checkForUpdate()
            return
        }

        if (isUpdateFlowLaunched) {
            Log.d(TAG, "Update flow already launched")
            return
        }

        when {
            updateInfo.updatePriority() >= 4 && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                startImmediateUpdate(updateInfo)
            }
            updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                startFlexibleUpdate(updateInfo)
            }
            else -> {
                val exception = IllegalStateException("No allowed update type for this update")
                Log.e(TAG, "Cannot start update", exception)
                callback?.onUpdateError(exception)
            }
        }
    }

    private fun startImmediateUpdate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                activity,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                UPDATE_REQUEST_CODE
            )
            isUpdateFlowLaunched = true
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "Failed to start immediate update", e)
            callback?.onUpdateError(e)
        }
    }

    private fun startFlexibleUpdate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.registerListener(installStateUpdatedListener)

            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                activity,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                UPDATE_REQUEST_CODE
            )
            isUpdateFlowLaunched = true
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "Failed to start flexible update", e)
            callback?.onUpdateError(e)
        }
    }

    private fun resumeUpdate(appUpdateInfo: AppUpdateInfo) {
        isUpdateFlowLaunched = true
        if (appUpdateInfo.installStatus() != InstallStatus.DOWNLOADED) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
        when (appUpdateInfo.installStatus()) {
            InstallStatus.DOWNLOADING, InstallStatus.INSTALLING -> {
                callback?.onUpdateInstalling()
            }
            InstallStatus.DOWNLOADED -> {
                callback?.onUpdateDownloaded()
                if (autoCompleteOnDownload) {
                    completeUpdate()
                }
            }
        }
    }

    private fun handleInstallState(state: InstallState) {
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                Log.d(TAG, "Update downloading...")
                callback?.onUpdateInstalling()
            }
            InstallStatus.DOWNLOADED -> {
                Log.d(TAG, "Update downloaded")
                callback?.onUpdateDownloaded()
                if (autoCompleteOnDownload) {
                    completeUpdate()
                }
                appUpdateManager.unregisterListener(installStateUpdatedListener)
            }
            InstallStatus.INSTALLING -> {
                Log.d(TAG, "Update installing...")
                callback?.onUpdateInstalling()
            }
            InstallStatus.FAILED -> {
                Log.e(TAG, "Update failed")
                callback?.onUpdateError(Exception("Update installation failed"))
                isUpdateFlowLaunched = false
                appUpdateManager.unregisterListener(installStateUpdatedListener)
                cachedUpdateInfo = null
                checkForUpdate()
            }
            InstallStatus.CANCELED -> {
                Log.d(TAG, "Update canceled")
                isUpdateFlowLaunched = false
                appUpdateManager.unregisterListener(installStateUpdatedListener)
                cachedUpdateInfo = null
                checkForUpdate()
            }
            else -> {
                Log.d(TAG, "Install status: ${state.installStatus()}")
            }
        }
    }

    fun completeUpdate() {
        appUpdateManager.completeUpdate()
        isUpdateFlowLaunched = false
        cachedUpdateInfo = null
    }

    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startImmediateUpdate(appUpdateInfo)
                }
            }
        }
    }

    fun onDestroy() {
        try {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering listener", e)
        }
    }
}
