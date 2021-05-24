package com.stenleone.clenner.ui.fragment

import android.os.Bundle
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentSystemCleaningBinding
import com.stenleone.clenner.ui.fragment.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SystemCleaningFragment(override var layId: Int = R.layout.fragment_system_cleaning) : BaseFragment<FragmentSystemCleaningBinding>() {

    companion object {
        const val TAG = "SystemCleaningFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {

    }
}