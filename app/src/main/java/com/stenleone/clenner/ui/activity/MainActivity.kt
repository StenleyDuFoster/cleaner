package com.stenleone.clenner.ui.activity

import android.os.Bundle
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityMainBinding
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.clenner.ui.adapters.FragmentsAdapter
import com.stenleone.clenner.util.bind.BindViewPager
import com.stenleone.clenner.worker.CreateOrUpdateNotificationWorker
import com.stenleone.clenner.worker.CreatePushNotificationWorker
import com.stenleone.clenner.worker.SendFirstOpenByUserWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity(override var layId: Int = R.layout.activity_main) : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var prefs: SharedPreferences

    private lateinit var viewPagerAdapter: FragmentsAdapter

    override fun setup(savedInstanceState: Bundle?) {

        setupDefaultValues()
        setupViewPagerAndBottomNav()
    }

    private fun setupViewPagerAndBottomNav() {
        viewPagerAdapter = FragmentsAdapter(this)
        binding.apply {
            pager.adapter = viewPagerAdapter
            pager.isUserInputEnabled = false
            BindViewPager(pager).withBottomNav(bottomNav)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.fragments.size == 0) {
            finish()
        }
    }

    private fun setupDefaultValues() {
        CreateOrUpdateNotificationWorker.start(this)
        CreatePushNotificationWorker.start(this)

        if (!prefs.isSendedDataAfterFirstOpen) {
            SendFirstOpenByUserWorker.start(this)
        }
    }
}