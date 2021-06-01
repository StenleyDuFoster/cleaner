package com.stenleone.clenner.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentBateryOptimizationBinding
import com.stenleone.clenner.managers.clean.BatteryCleaningManager
import com.stenleone.clenner.ui.fragment.base.BaseFragmentWithCleanProgressLogic
import com.stenleone.clenner.util.enum.BatteryClean
import com.stenleone.stanleysfilm.util.extencial.throttleClicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BatteryOptimizationFragment(override var layId: Int = R.layout.fragment_batery_optimization) : BaseFragmentWithCleanProgressLogic<FragmentBateryOptimizationBinding>() {

    @Inject
    lateinit var batteryCleaningManager: BatteryCleaningManager

    companion object {
        const val TAG = "BateryOptimizationFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {

        setupClicks()
    }

    private fun setupClicks() {
        binding.apply {
            normalButton.throttleClicks({
                doIfCanWriteSettings {
                    lifecycleScope.launch {
                        animateProgress(0, binding.progress)
                        blockButtons()
                        delay(400)
                        batteryCleaningManager.clean(BatteryClean.Normal).collect {
                            animateProgress(it, binding.progress)
                            setHoursByProgress(it, 5)
                        }
                    }
                }
            }, lifecycleScope)
            highButton.throttleClicks({
                doIfCanWriteSettings {
                    blockButtons()
                    lifecycleScope.launch {
                        animateProgress(0, binding.progress)
                        blockButtons()
                        delay(400)
                        batteryCleaningManager.clean(BatteryClean.High).collect {
                            animateProgress(it, binding.progress)
                            setHoursByProgress(it, 9)
                        }
                    }
                }
            }, lifecycleScope)
            maximumButton.throttleClicks({
                doIfCanWriteSettings {
                    blockButtons()
                    lifecycleScope.launch {
                        animateProgress(0, binding.progress)
                        blockButtons()
                        delay(400)
                        batteryCleaningManager.clean(BatteryClean.Maximum).collect {
                            animateProgress(it, binding.progress)
                            setHoursByProgress(it, 14)
                        }
                    }
                }
            }, lifecycleScope)
        }
    }

    private fun setHoursByProgress(progress: Int, maxHours: Int) {
        binding.hoursText.text = (maxHours - ((maxHours / (progress + 1) + 1))).toString()
        if (progress == 100) {
            binding.hoursText.text = maxHours.toString()
        }
    }

    private fun doIfCanWriteSettings(runCode: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(requireContext())) { // todo
            runCode()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + requireActivity().packageName)
            startActivity(intent)
        }
    }

    private fun blockButtons() {
        binding.apply {
            normalButton.isClickable = false
            highButton.isClickable = false
            maximumButton.isClickable = false
        }
    }

    private fun unblockButtons() {
        binding.apply {
            normalButton.isClickable = true
            highButton.isClickable = true
            maximumButton.isClickable = true
        }
    }

    override fun cleanSuccess() {
        unblockButtons()
    }
}