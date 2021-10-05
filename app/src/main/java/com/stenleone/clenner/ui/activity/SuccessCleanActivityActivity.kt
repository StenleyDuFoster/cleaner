package com.stenleone.clenner.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityCleanSuccessBinding
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.stanleysfilm.util.extencial.throttleClicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_clean_success.*

var currentNativeAd: NativeAd? = null

@AndroidEntryPoint
class SuccessCleanActivityActivity(override var layId: Int = R.layout.activity_clean_success) : BaseActivity<ActivityCleanSuccessBinding>() {

    override fun setup(savedInstanceState: Bundle?) {
        setupClicks()
        refreshAd()
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = ad_media

        adView.headlineView = ad_headline
        adView.bodyView = ad_body
        adView.callToActionView = ad_call_to_action
        adView.iconView = ad_app_icon
        adView.priceView = ad_price
        adView.starRatingView = ad_stars
        adView.storeView = ad_store
        adView.advertiserView = ad_advertiser

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            ad_stars.isVisible = false
        } else {
            ad_stars.rating = nativeAd.starRating!!.toFloat()
            ad_stars.isVisible = true
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.isVisible = false
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    private fun refreshAd() {
        val builder = AdLoader.Builder(this, getString(R.string.ad_native_id))

        builder.forNativeAd { nativeAd ->
            var activityDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activityDestroyed = isDestroyed
            }
            if (activityDestroyed || isFinishing || isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            currentNativeAd = nativeAd
            populateNativeAdView(nativeAd, nativeFeedAds)
        }

        val videoOptions = VideoOptions.Builder()
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                nativeFeedAds.isVisible = true
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                nativeFeedAds.isVisible = false
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroy() {
        currentNativeAd?.destroy()
        super.onDestroy()
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