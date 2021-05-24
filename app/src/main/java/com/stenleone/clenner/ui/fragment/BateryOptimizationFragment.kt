package com.stenleone.clenner.ui.fragment

import android.os.Bundle
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.FragmentMainBinding
import com.stenleone.clenner.ui.fragment.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BateryOptimizationFragment(override var layId: Int = R.layout.fragment_main) : BaseFragment<FragmentMainBinding>() {

    companion object {
        const val TAG = "BateryOptimizationFragment"
    }

    override fun setup(savedInstanceState: Bundle?) {

    }
}