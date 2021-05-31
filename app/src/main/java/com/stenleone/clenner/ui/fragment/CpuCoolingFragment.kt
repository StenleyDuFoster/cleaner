package com.stenleone.clenner.ui.fragment

import android.os.Bundle
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentCpuCoolingBinding
import com.stenleone.clenner.ui.fragment.base.BaseFragment
import com.stenleone.clenner.ui.fragment.base.BaseFragmentWithCleanProgressLogic
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CpuCoolingFragment(override var layId: Int = R.layout.fragment_cpu_cooling) : BaseFragmentWithCleanProgressLogic<FragmentCpuCoolingBinding>() {

    companion object {
        const val TAG = "CpuCoolingFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {

    }

    override fun cleanSuccess() {

    }
}