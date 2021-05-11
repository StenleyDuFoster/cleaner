package com.stenleone.clenner.ui.activity

import android.os.Bundle
import com.jaeger.library.StatusBarUtil
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityMainBinding
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.clenner.ui.fragment.MainFragment
import com.stenleone.clenner.worker.CreateOrUpdateNotificationWorker
import com.stenleone.clenner.worker.CreatePushNotificationWorker
import com.stenleone.clenner.worker.SendFirstOpenByUserWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity(override var layId: Int = R.layout.activity_main) : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var prefs: SharedPreferences

    override fun setup(savedInstanceState: Bundle?) {

        StatusBarUtil.setTransparent(this)
        setupMainFragment()
        setupDefaultValues()
    }

    private fun setupMainFragment() {
        fragmentContainerId = binding.rootView.id
        if (supportFragmentManager.findFragmentByTag(MainFragment.TAG) == null) {
            addFragment(null, MainFragment(), MainFragment.TAG)
        }
    }

    fun findDataToClean() {


        getPermissions()
    }

    private fun getPermissions() {

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