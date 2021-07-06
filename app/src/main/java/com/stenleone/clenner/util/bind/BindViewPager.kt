package com.stenleone.clenner.util.bind

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.stenleone.clenner.R

class BindViewPager(private val pager: ViewPager2) {

    fun withBottomNav(nav: BottomNavigationView) {
        nav.setOnNavigationItemSelectedListener {
            pager.currentItem = converterViewPagerAndBottomNavPosition(it.itemId)
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun converterViewPagerAndBottomNavPosition(positionOrId: Int): Int {
        return when (positionOrId) {
            R.id.navigation_one -> 0
            R.id.navigation_two -> 1
            R.id.navigation_three -> 2
            R.id.navigation_four -> 3
            else -> 3
        }
    }
}