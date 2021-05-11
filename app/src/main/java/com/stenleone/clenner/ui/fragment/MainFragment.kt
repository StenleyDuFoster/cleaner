package com.stenleone.clenner.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentMainBinding
import com.stenleone.clenner.ui.activity.MainActivity
import com.stenleone.clenner.ui.fragment.base.BaseFragment
import com.stenleone.clenner.util.notification.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment(override var layId: Int = R.layout.fragment_main) : BaseFragment<FragmentMainBinding>() {

    companion object {
        const val TAG = "MainFragment"
    }

    private lateinit var swipeListener: MotionLayout.TransitionListener

    override fun setup(savedInstanceState: Bundle?) {

        setupSwipeListener()
    }

    private fun setupSwipeListener() {
        binding.apply {
            swipeListener = object : MotionLayout.TransitionListener {
                override fun onTransitionChange(lay: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                    lottieRocket.progress = progress / 10
                }

                override fun onTransitionCompleted(lay: MotionLayout?, p1: Int) {
                    if (lottieRocket.progress >= 0.05f) {
                        startMainLogic()
                    }
                }

                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) = Unit
                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) = Unit
            }
            root.addTransitionListener(swipeListener)
        }
    }

    private fun startMainLogic() {
        binding.apply {
            lottieSwipe.visibility = View.GONE
            root.removeTransitionListener(swipeListener)
            animateRocket()
            (requireActivity() as MainActivity).findDataToClean()
        }
    }

    private fun animateRocket() {
        binding.apply {
            ValueAnimator.ofFloat(lottieRocket.progress, 1f).also { animator ->
                animator.duration = 5000
                animator.addUpdateListener {
                    lottieRocket.progress = it.animatedValue as Float
                }
                animator.repeatCount = ValueAnimator.INFINITE
                animator.repeatMode = ValueAnimator.REVERSE
                animator.start()
            }
        }
    }
}