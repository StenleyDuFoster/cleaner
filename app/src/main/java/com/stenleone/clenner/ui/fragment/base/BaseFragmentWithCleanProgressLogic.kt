package com.stenleone.clenner.ui.fragment.base

import android.animation.ValueAnimator
import android.content.Intent
import androidx.databinding.ViewDataBinding
import com.appodeal.ads.Appodeal
import com.mikhaellopez.circularprogressbar.CircularProgressBar
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

            Appodeal.show(requireActivity(), Appodeal.INTERSTITIAL)
            Appodeal.cache(requireActivity(), Appodeal.INTERSTITIAL)
            startActivity(Intent(requireContext(), SuccessCleanActivityActivity::class.java))
        }
    }

    abstract fun cleanSuccess()
}