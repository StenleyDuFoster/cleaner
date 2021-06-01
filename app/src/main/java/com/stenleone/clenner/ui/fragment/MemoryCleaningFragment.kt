package com.stenleone.clenner.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentMemoryCleaningBinding
import com.stenleone.clenner.managers.clean.MemoryCleaningManager
import com.stenleone.clenner.ui.fragment.base.BaseFragmentWithCleanProgressLogic
import com.stenleone.stanleysfilm.util.extencial.throttleClicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MemoryCleaningFragment(override var layId: Int = R.layout.fragment_memory_cleaning) : BaseFragmentWithCleanProgressLogic<FragmentMemoryCleaningBinding>() {

    @Inject
    lateinit var memoryCleaningManager: MemoryCleaningManager

    companion object {
        const val TAG = "MemoryCleaningFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {

        binding.data = memoryCleaningManager.getData()
        setupClicks()
    }

    private fun setupClicks() {
        binding.apply {
            start.throttleClicks({
                lifecycleScope.launch {
                    memoryCleaningManager.clean().collect {
                        setCleanText(it)
                        animateProgress(it, binding.progress)
                        start.isClickable = false
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
        memoryCleaningManager.cleanSuccess()
        binding.data = memoryCleaningManager.getData()
    }
}