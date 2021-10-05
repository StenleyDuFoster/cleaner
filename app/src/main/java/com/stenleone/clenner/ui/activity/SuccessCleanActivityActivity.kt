package com.stenleone.clenner.ui.activity

import android.os.Bundle
import android.view.ViewGroup.OnHierarchyChangeListener
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityCleanSuccessBinding
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.stanleysfilm.util.extencial.throttleClicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_clean_success.*


@AndroidEntryPoint
class SuccessCleanActivityActivity(override var layId: Int = R.layout.activity_clean_success) : BaseActivity<ActivityCleanSuccessBinding>() {

    override fun setup(savedInstanceState: Bundle?) {

        setupClicks()
        setupAdMob()
    }

    private fun setupAdMob() {
        val adLoader = AdLoader.Builder(this, getString(R.string.ad_native_id))
            .forNativeAd {
                nativeFeedAds.apply {
                    mediaView = nativeMedia
                    headlineView = nativeTitle
                    setNativeAd(it)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

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