package com.mkhglab.agecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mkhglab.agecalculator.ui.theme.AgeCalculatorTheme
import com.mkhglab.agecalculator.tools.AppUpdateChecker

class MainActivity : ComponentActivity() {
    private var appUpdateChecker: AppUpdateChecker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateChecker = AppUpdateChecker(this).also { it.checkForUpdate() }
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
}
