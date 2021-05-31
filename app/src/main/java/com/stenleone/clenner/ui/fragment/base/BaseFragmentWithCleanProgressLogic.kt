package com.stenleone.clenner.ui.fragment.base

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.stenleone.clenner.util.extencial.hideKeyboard
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
        }
    }

    abstract fun cleanSuccess()
}