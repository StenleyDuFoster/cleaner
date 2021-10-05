package com.stenleone.clenner.ui.fragment.base

import android.animation.ValueAnimator
import android.content.Intent
import androidx.databinding.ViewDataBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.SuccessCleanActivityActivity
import kotlin.random.Random

abstract class BaseFragmentWithCleanProgressLogic<T : ViewDataBinding> : BaseFragment<T>() {

    fun animateProgress(newProgress: Int, progressView: CircularProgressBar) {
        ValueAnimator.ofFloat(progressView.progress, newProgress.toFloat()).also {
            it.addUpdateListener {
                progressView.progress = it.animatedValue as Float
                checkIfCleanFinished(newProgress, (it.animatedValue as Float).toInt())
            }
            it.duration = Random.nextLong(100, 1000)
            it.start()
        }
    }

    private fun checkIfCleanFinished(newProgress: Int, animatedProgress: Int) {
        if (newProgress == 100 && animatedProgress == 100) {
            cleanSuccess()
            InterstitialAd.load(requireContext(), getString(R.string.ad_interstitial_id), AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAd.show(requireActivity())
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                }
            })
            startActivity(Intent(requireContext(), SuccessCleanActivityActivity::class.java))
        }
    }

    abstract fun cleanSuccess()
}