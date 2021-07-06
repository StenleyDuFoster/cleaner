package com.stenleone.clenner.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentCpuCoolingBinding
import com.stenleone.clenner.managers.clean.CpuCleaningManager
import com.stenleone.clenner.ui.fragment.base.BaseFragmentWithCleanProgressLogic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CpuCoolingFragment(override var layId: Int = R.layout.fragment_cpu_cooling) : BaseFragmentWithCleanProgressLogic<FragmentCpuCoolingBinding>() {

    @Inject
    lateinit var cpuCleaningManager: CpuCleaningManager

    companion object {
        const val TAG = "CpuCoolingFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {

        binding.dataList = cpuCleaningManager.getAppList()
        binding.temperature = cpuCleaningManager.getTemperature()
        setupClicks()
    }

    private fun setupClicks() {
        binding.apply {

            start.setOnClickListener { button ->

                button.isClickable = false
                lifecycleScope.launch {
                    animateProgress(0, binding.progress)
                    delay(400)
                    cpuCleaningManager.clean().collect {
                        setCleanText(it)
                        animateProgress(it, binding.progress)
                    }
                }
            }
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

    override fun cleanSuccess() {
        binding.apply {
            start.isClickable = true
            cpuCleaningManager.cleanSuccess()
            binding.temperature = cpuCleaningManager.getTemperature()
        }
    }
}