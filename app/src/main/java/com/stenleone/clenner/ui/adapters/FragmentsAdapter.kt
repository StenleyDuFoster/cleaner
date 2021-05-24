package com.stenleone.clenner.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stenleone.clenner.ui.fragment.BateryOptimizationFragment
import com.stenleone.clenner.ui.fragment.CpuCoolingFragment
import com.stenleone.clenner.ui.fragment.MemoryCleaningFragment
import com.stenleone.clenner.ui.fragment.SystemCleaningFragment

class FragmentsAdapter(
    supportFragmentManager: FragmentActivity
) : FragmentStateAdapter(supportFragmentManager) {

    val listFragments: ArrayList<Fragment> = arrayListOf(
        SystemCleaningFragment(),
        MemoryCleaningFragment(),
        BateryOptimizationFragment(),
        CpuCoolingFragment()
    )

    override fun createFragment(position: Int): Fragment {
        return listFragments[position]
    }

    override fun getItemCount(): Int = listFragments.size

}