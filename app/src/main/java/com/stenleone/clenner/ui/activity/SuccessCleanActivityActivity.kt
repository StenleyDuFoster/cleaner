package com.stenleone.clenner.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.appodeal.ads.Appodeal
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityCleanSuccessBinding
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.stanleysfilm.util.extencial.throttleClicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessCleanActivityActivity(override var layId: Int = R.layout.activity_clean_success) : BaseActivity<ActivityCleanSuccessBinding>() {

    override fun setup(savedInstanceState: Bundle?) {

        setupClicks()
        setupNativeAds()
    }

    private fun setupNativeAds() {

        if (Appodeal.isLoaded(Appodeal.NATIVE)) {

            val nativeAds = Appodeal.getNativeAds(1)

            nativeAds.firstOrNull()?.let {
                binding.nativeFeedAds.setNativeAd(it)
            }
            Appodeal.cache(this, Appodeal.NATIVE)
        } else {
            Appodeal.cache(this, Appodeal.NATIVE)
        }

    }

    private fun setupClicks() {
        binding.apply {
            backButton.throttleClicks(
                {
                    onBackPressed()
                }, lifecycleScope
            )
        }
    }

}