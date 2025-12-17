package com.mkhglab.agecalculator

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

private const val BANNER_AD_UNIT_ID = "ca-app-pub-5755684289908017/3623547684"
private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-5755684289908017/5100280882"

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun rememberInterstitialAd(
    context: Context,
    reloadKey: Int
): InterstitialAd? {
    var interstitialAd by remember(reloadKey) { mutableStateOf<InterstitialAd?>(null) }

    DisposableEffect(reloadKey) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
        onDispose {
            interstitialAd = null
        }
    }

    return interstitialAd
}

fun showInterstitialIfReady(activity: Activity?, interstitialAd: InterstitialAd?, onShown: () -> Unit) {
    if (activity == null || interstitialAd == null) {
        onShown()
        return
    }
    interstitialAd.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            onShown()
        }

        override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
            onShown()
        }
    }
    interstitialAd.show(activity)
}
