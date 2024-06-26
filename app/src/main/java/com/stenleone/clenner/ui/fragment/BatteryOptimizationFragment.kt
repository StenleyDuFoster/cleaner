package com.stenleone.clenner.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
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

    private var isCanWriteIntentOpened = false

    companion object {
        const val TAG = "BateryOptimizationFragment"
        const val OPENED_CAN_WRITE_SETTINGS_INTENT = "OPENED_CAN_WRITE_SETTINGS_INTENT"
    }

    override fun setup(savedInstanceState: Bundle?) {

        setupDefaultValues(savedInstanceState)
        setupClicks()
    }

    private fun setupDefaultValues(savedInstanceState: Bundle?) {

        savedInstanceState?.let {
            it.getBoolean(OPENED_CAN_WRITE_SETTINGS_INTENT)?.let {
                isCanWriteIntentOpened = it
            }
        }
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
                            setCleanText(it)
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
                            setCleanText(it)
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
                            setCleanText(it)
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
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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

    private fun setCleanText(progress: Int) {
        binding.apply {
            cleanInProcessText.visibility = if (progress != 100) {
                View.VISIBLE
            } else {
                View.GONE
            }
            cleanInProcessText.text = getString(R.string.cleaning_in_process, progress.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(OPENED_CAN_WRITE_SETTINGS_INTENT, isCanWriteIntentOpened)
    }

    override fun cleanSuccess() {
        unblockButtons()
    }
}