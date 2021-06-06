package com.stenleone.clenner.ui.activity

import android.os.Bundle
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityCleanSuccessBinding
import com.stenleone.clenner.ui.activity.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessCleanActivityActivity(override var layId: Int = R.layout.activity_clean_success) : BaseActivity<ActivityCleanSuccessBinding>() {

    override fun setup(savedInstanceState: Bundle?) {

        setupNativeAds()
    }

    private fun setupNativeAds() {

    }

    private fun setupClicks() {

    }

}