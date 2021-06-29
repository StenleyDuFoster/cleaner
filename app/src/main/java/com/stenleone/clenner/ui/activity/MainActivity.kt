package com.stenleone.clenner.ui.activity

import android.animation.ObjectAnimator
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.appodeal.ads.Appodeal
import com.appodeal.ads.BannerCallbacks
import com.appodeal.ads.InterstitialCallbacks
import com.appodeal.ads.Native
import com.stenleone.clenner.BuildConfig
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityMainBinding
import com.stenleone.clenner.managers.config.Config
import com.stenleone.clenner.managers.config.ConfigService
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.receiver.InstallReferrerReceiver
import com.stenleone.clenner.service.PushNonCancelService
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.clenner.ui.adapters.pager.FragmentsAdapter
import com.stenleone.clenner.util.bind.BindViewPager
import com.stenleone.clenner.worker.CreateOrUpdateNotificationWorker
import com.stenleone.clenner.worker.CreatePushNotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity(override var layId: Int = R.layout.activity_main) : BaseActivity<ActivityMainBinding>() {

    companion object {
        const val SAVED_SHOWN_INTERNAL = "saved_isFirstInternalShown"
    }

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var configString: ConfigService

    private lateinit var viewPagerAdapter: FragmentsAdapter
    private var isFirstInternalShown = false

    override fun setup(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getBoolean(SAVED_SHOWN_INTERNAL)?.let {
                isFirstInternalShown = it
            }
        }

        setupAppoDeal()
        setupViewPagerAndBottomNav()
        setupDefaultValues()
    }

    private fun setupViewPagerAndBottomNav() {
        viewPagerAdapter = FragmentsAdapter(this)
        binding.apply {
            pager.adapter = viewPagerAdapter
            pager.isUserInputEnabled = false
            pager.offscreenPageLimit = 4
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
        this.startService(Intent(this, PushNonCancelService::class.java))
        lifecycleScope.launch {
            CreatePushNotificationWorker.start(this@MainActivity, configString.getIntAsync(Config.SHOW_NOTIFICATION_TIME_IN_HOUR))
        }

        if (!prefs.isSendedDataAfterFirstOpen) {
            checkPostBack()
        }
    }

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    private fun setupAppoDeal() {

        Appodeal.initialize(this, getString(R.string.appo_daeal_ads_app_id), Appodeal.INTERSTITIAL or Appodeal.NATIVE or Appodeal.BANNER)

        Appodeal.disableLocationPermissionCheck()
        Appodeal.setNativeAdType(Native.NativeAdType.Video)
        Appodeal.setBannerViewId(R.id.appodealBannerView)
        Appodeal.show(this, Appodeal.BANNER_VIEW)

        if (BuildConfig.DEBUG) {
            Appodeal.setTesting(true)
        }

        Appodeal.cache(this, Appodeal.INTERSTITIAL)
        Appodeal.cache(this, Appodeal.NATIVE)
        Appodeal.setBannerCallbacks(object : BannerCallbacks {
            override fun onBannerLoaded(p0: Int, p1: Boolean) {
                binding.appodealBannerView.minimumHeight = (p0.toFloat() * (this@MainActivity.getResources().getDisplayMetrics().densityDpi / 160.0f)).toInt()
                binding.appodealBannerView.visibility = View.VISIBLE
            }

            override fun onBannerFailedToLoad() {
                binding.appodealBannerView.visibility = View.VISIBLE
            }

            override fun onBannerShown() {
                binding.appodealBannerView.visibility = View.VISIBLE
            }

            override fun onBannerShowFailed() {
                binding.appodealBannerView.visibility = View.VISIBLE
            }

            override fun onBannerClicked() = Unit

            override fun onBannerExpired() = Unit

        })

        Appodeal.setInterstitialCallbacks(object : InterstitialCallbacks {
            override fun onInterstitialLoaded(p0: Boolean) {
                if (!isFirstInternalShown) {
                    Appodeal.show(this@MainActivity, Appodeal.INTERSTITIAL)
                    Appodeal.cache(this@MainActivity, Appodeal.INTERSTITIAL)
                    isFirstInternalShown = true
                }
                showMainContent()
            }

            override fun onInterstitialFailedToLoad() {
                showMainContent()
            }

            override fun onInterstitialShown() {

            }

            override fun onInterstitialShowFailed() {
                showMainContent()
            }

            override fun onInterstitialClicked() {

            }

            override fun onInterstitialClosed() {

            }

            override fun onInterstitialExpired() {

            }

        })
    }

    private fun showMainContent() {
        ObjectAnimator.ofFloat(binding.rootLay, View.ALPHA, 1.0f).also {
            it.duration = 1000
            it.start()
        }
    }

    private fun checkPostBack() {
        try {
            registerReceiver(InstallReferrerReceiver(), IntentFilter("com.android.vending.INSTALL_REFERRER"))
            val build: InstallReferrerClient = InstallReferrerClient.newBuilder(this).build()
            build.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerServiceDisconnected() {}
                override fun onInstallReferrerSetupFinished(i: Int) {
                    if (i == 0) {
                        try {
//                            Sentry.captureMessage("All Ok for send INSTALL_REFERRER event")
                            val installReferrer: String = build.getInstallReferrer().getInstallReferrer()
                            val intent = Intent()
                            intent.action = "com.android.vending.INSTALL_REFERRER"
                            intent.putExtra("referrer", installReferrer)
                            this@MainActivity.sendBroadcast(intent)
//                            Sentry.captureMessage("INSTALL_REFERRER sent")
                        } catch (e: Exception) {
                            Log.e("NEW_INSTALL_REFERRER", e.toString())
                        }
                    } else if (i == 1) {
//                        Sentry.captureMessage("SERVICE_UNAVAILABLE")
                    } else if (i == 2) {
//                        Sentry.captureMessage("FEATURE_NOT_SUPPORTED")
                    }
                }
            })
        } catch (th: Throwable) {

        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(SAVED_SHOWN_INTERNAL, isFirstInternalShown)
    }
}