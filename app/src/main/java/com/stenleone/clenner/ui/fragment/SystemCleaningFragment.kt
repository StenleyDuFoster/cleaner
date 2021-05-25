package com.stenleone.clenner.ui.fragment

import android.os.Bundle
import android.widget.Toast
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentSystemCleaningBinding
import com.stenleone.clenner.managers.clean.SystemCleaningManager
import com.stenleone.clenner.ui.fragment.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SystemCleaningFragment(override var layId: Int = R.layout.fragment_system_cleaning) : BaseFragment<FragmentSystemCleaningBinding>() {

    @Inject
    lateinit var manager: SystemCleaningManager

    companion object {
        const val TAG = "SystemCleaningFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {


        manager.deleteCache()

        manager.searchCache {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}