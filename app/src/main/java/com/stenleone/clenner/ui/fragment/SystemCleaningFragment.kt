package com.stenleone.clenner.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentSystemCleaningBinding
import com.stenleone.clenner.managers.clean.SystemCleaningManager
import com.stenleone.clenner.ui.fragment.base.BaseFragmentWithCleanProgressLogic
import com.stenleone.stanleysfilm.util.extencial.throttleClicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SystemCleaningFragment(override var layId: Int = R.layout.fragment_system_cleaning) : BaseFragmentWithCleanProgressLogic<FragmentSystemCleaningBinding>() {

    @Inject
    lateinit var systemCleaningManager: SystemCleaningManager

    companion object {
        const val TAG = "SystemCleaningFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {
        binding.data = systemCleaningManager.getData()
        setupClicks()
    }

    private fun setupClicks() {
        binding.apply {
            start.throttleClicks({
                start.isClickable = false
                lifecycleScope.launch {
                    systemCleaningManager.clean().collect {
                        setCleanText(it)
                        animateProgress(it, binding.progress)
                    }
                }
            }, lifecycleScope)
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
        binding.data = binding.data?.also {
            it.cleanData()
        }
        systemCleaningManager.cleanSuccess()
    }
}