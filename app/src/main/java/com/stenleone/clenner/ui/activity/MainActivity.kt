package com.stenleone.clenner.ui.activity

import android.os.Bundle
import android.view.View
import com.appodeal.ads.Appodeal
import com.appodeal.ads.BannerCallbacks
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityMainBinding
import com.stenleone.clenner.managers.config.Config
import com.stenleone.clenner.managers.config.ConfigService
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.clenner.ui.adapters.pager.FragmentsAdapter
import com.stenleone.clenner.util.bind.BindViewPager
import com.stenleone.clenner.worker.CreatePushNotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity(override var layId: Int = R.layout.activity_main) : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var configString: ConfigService

    private lateinit var viewPagerAdapter: FragmentsAdapter

    override fun setup(savedInstanceState: Bundle?) {

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
//        CreateOrUpdateNotificationWorker.start(this)
        CreatePushNotificationWorker.start(this, configString.getInt(Config.SHOW_NOTIFICATION_TIME_IN_HOUR))
//
//        if (!prefs.isSendedDataAfterFirstOpen) {
//            SendFirstOpenByUserWorker.start(this)
//        }
    }

//    private fun setupConsent() {
//        val consentManager = ConsentManager.getInstance(this)
//
//        consentManager.requestConsentInfoUpdate(
//            getString(R.string.appo_daeal_ads_app_id),
//            object : ConsentInfoUpdateListener {
//                override fun onConsentInfoUpdated(consent: Consent) {
//                    val consentShouldShow = consentManager.shouldShowConsentDialog()
//                    // If ConsentManager return Consent.ShouldShow.TRUE, than we should show consent form
//                    if (consentShouldShow == ShouldShow.TRUE) {
//                        showConsentForm()
//                    } else {
//                        if (consent.status == Consent.Status.UNKNOWN) {
//                            // Start our main activity with default Consent value
//                            setupAppoDeal(null)
//                        } else {
//                            val hasConsent = consent.status == Consent.Status.PERSONALIZED
//                            // Start our main activity with resolved Consent value
//                            setupAppoDeal(consent)
//                        }
//                    }
//                }
//
//                override fun onFailedToUpdateConsentInfo(e: ConsentManagerException) {
//                    setupAppoDeal(null)
//                }
//            })
//    }

//    private fun showConsentForm() {
//        if (consentForm == null) {
//            consentForm = ConsentForm.Builder(this)
//                .withListener(object : ConsentFormListener {
//                    override fun onConsentFormLoaded() {
//                        // Show ConsentManager Consent request form
//                        consentForm?.showAsActivity()
//                    }
//
//                    override fun onConsentFormError(error: ConsentManagerException) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Consent form error: " + error.reason,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        // Start our main activity with default Consent value
//                        setupAppoDeal(null)
//                    }
//
//                    override fun onConsentFormOpened() {
//                        //ignore
//                    }
//
//                    override fun onConsentFormClosed(consent: Consent) {
//                        val hasConsent = consent.status == Consent.Status.PERSONALIZED
//                        // Start our main activity with resolved Consent value
//                        setupAppoDeal(consent)
//                    }
//                }).build()
//        }
//        // If Consent request form is already loaded, then we can display it, otherwise, we should load it first
//        if (consentForm?.isLoaded() == true) {
//            consentForm?.showAsActivity()
//        } else {
//            consentForm?.load()
//        }
//    }

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    private fun setupAppoDeal() {

        Appodeal.initialize(this, getString(R.string.appo_daeal_ads_app_id), Appodeal.INTERSTITIAL or Appodeal.NATIVE or Appodeal.BANNER)

        Appodeal.disableLocationPermissionCheck()
//        Appodeal.setBannerViewId(R.id.appodealBannerView)
        Appodeal.show(this, Appodeal.BANNER_VIEW)
        Appodeal.cache(this, Appodeal.INTERSTITIAL)
        Appodeal.cache(this, Appodeal.NATIVE)
        Appodeal.setBannerCallbacks(object : BannerCallbacks {
            override fun onBannerLoaded(p0: Int, p1: Boolean) {
                binding.rootLay.visibility = View.VISIBLE
            }

            override fun onBannerFailedToLoad() {

                binding.rootLay.visibility = View.VISIBLE
            }

            override fun onBannerShown() {
                binding.rootLay.visibility = View.VISIBLE
            }

            override fun onBannerShowFailed() {
                binding.rootLay.visibility = View.VISIBLE
            }

            override fun onBannerClicked() = Unit

            override fun onBannerExpired() = Unit

        })
    }
}