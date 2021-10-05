package com.stenleone.clenner.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.stenleone.clenner.R
import com.stenleone.clenner.databinding.ActivityMainBinding
import com.stenleone.clenner.managers.config.Config
import com.stenleone.clenner.managers.config.ConfigService
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.receiver.AlarmNotificationReceiver
import com.stenleone.clenner.receiver.InstallReferrerReceiver
import com.stenleone.clenner.service.AlarmNotificationService
import com.stenleone.clenner.ui.activity.base.BaseActivity
import com.stenleone.clenner.ui.adapters.pager.FragmentsAdapter
import com.stenleone.clenner.util.bind.BindViewPager
import com.stenleone.clenner.worker.CreateOrUpdateNotificationWorker
import com.stenleone.clenner.worker.CreatePushNotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity(override var layId: Int = R.layout.activity_main) :
    BaseActivity<ActivityMainBinding>() {

    companion object {
        const val SAVED_SHOWN_INTERNAL = "saved_isFirstInternalShown"
    }

    @Inject
    lateinit var prefs: SharedPreferences
    val isShown = false

    @Inject
    lateinit var configString: ConfigService

    private lateinit var viewPagerAdapter: FragmentsAdapter
    private var isFirstInternalShown = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setup(savedInstanceState: Bundle?) {

        savedInstanceState?.let {
            it.getBoolean(SAVED_SHOWN_INTERNAL)?.let {
                isFirstInternalShown = it
                Log.d("test", "it")
            }
        }

        showCustomPopupMenu()
        setupAdMob()
        setupViewPagerAndBottomNav()
        setupDefaultValues()
        setupTextLoaderAnimator()
        setUpAlarmNotification()
        checkFirstOpen()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showCustomPopupMenu() {

//        if (!Settings.canDrawOverlays(this)) {
//            val intent =
//                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
//            startActivityForResult(intent, 1234)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLastAlarmNotificationIsClosed(this, true)
        startService(Intent(this, AlarmNotificationService::class.java))
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
        lifecycleScope.launch {
            CreatePushNotificationWorker.start(
                this@MainActivity,
                configString.getIntAsync(Config.SHOW_NOTIFICATION_TIME_IN_HOUR)
            )
        }
        if (!prefs.isSendedDataAfterFirstOpen) {
            checkPostBack()
        }
    }

    private fun setUpAlarmNotification(){
        val notifyIntent = Intent(this, AlarmNotificationReceiver::class.java)
        notifyIntent.action = "PUSH"
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notifyIntent,
            0
        )
        var alarmManager: AlarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

//        val h = 20
//        val m = 0
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        calendar.set(Calendar.MINUTE, m)
        calendar.set(Calendar.HOUR_OF_DAY, h + 2)
//            calendar.set(Calendar.MINUTE, m + 1)
//            calendar.set(Calendar.HOUR_OF_DAY, h)

        val sdf1 = SimpleDateFormat("HH", Locale.getDefault())
        val timeHours: String = sdf1.format(Date())
        val sdf2 = SimpleDateFormat("mm", Locale.getDefault())
        val timeMinute: String = sdf2.format(Date())
//        if (Integer.parseInt(timeHours) >= h && Integer.parseInt(timeMinute) >= m){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, calendar.timeInMillis + 1000 * 60 * 60 * 24, pendingIntent)
//            }else{
//                alarmManager.setExact(AlarmManager.RTC, calendar.timeInMillis + 1000 * 60 * 60 * 24, pendingIntent)
//
//            }
//        }else{
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setExact(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
//            }else{
//                alarmManager.setExact(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
//
//            }
//        }


    }


    override fun onResume() {
        super.onResume()
    }

    private fun setupTextLoaderAnimator() {
        binding.apply {
            ValueAnimator.ofInt(2).also { // todo add cancel logic
                it.addUpdateListener {
                    when ((it.getAnimatedValue() as Int)) {
                        0 -> {
                            loadedText.text = getString(R.string.scanning_in_progress, ".")
                        }
                        1 -> {
                            loadedText.text = getString(R.string.scanning_in_progress, "..")
                        }
                        2 -> {
                            loadedText.text = getString(R.string.scanning_in_progress, "...")
                        }
                    }
                }
                it.duration = 500
                it.repeatCount = ValueAnimator.INFINITE
                it.repeatMode = ValueAnimator.REVERSE
                it.start()
            }
        }
    }

    private fun setupAdMob() {
        MobileAds.initialize(this) {}

        adBannerView.apply {
            loadAd(AdRequest.Builder().build())
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adBannerView.isVisible = true
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }
            }
        }

        InterstitialAd.load(this, getString(R.string.ad_interstitial_id), AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                if (!isFirstInternalShown) {
                    interstitialAd.show(this@MainActivity)
                    isFirstInternalShown = true
                }
                showMainContent()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                showMainContent()
            }
        })

    }

    private fun showMainContent() {
        ObjectAnimator.ofFloat(binding.rootLay, View.ALPHA, 1.0f).also {
            it.duration = 1000
            it.start()
        }
        binding.loadedText.visibility = View.GONE
    }

    private fun checkPostBack() {
        try {
            registerReceiver(
                InstallReferrerReceiver(),
                IntentFilter("com.android.vending.INSTALL_REFERRER")
            )
            val build: InstallReferrerClient = InstallReferrerClient.newBuilder(this).build()
            build.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerServiceDisconnected() {}
                override fun onInstallReferrerSetupFinished(i: Int) {
                    if (i == 0) {
                        try {
//                            Sentry.captureMessage("All Ok for send INSTALL_REFERRER event")
                            val installReferrer: String =
                                build.getInstallReferrer().getInstallReferrer()
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

    fun checkFirstOpen() {
//        if (getIsFirstOpen(this)) {
//            AlarmNotificationService.updateSingleAlarm(this)
//        }
//        setIsFirstOpen(this)
    }

    private fun getIsFirstOpen(context: Context): Boolean {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)

        return mSettings.getBoolean("isFirstOpen", true)
    }

    private fun setIsFirstOpen(context: Context) {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)

        val editor = mSettings.edit()
        editor.putBoolean("isFirstOpen", false)
        editor.apply()
    }

    private val LAST_ALARM_NOTIFICATION_IS_CLOSED = "LAST_ALARM_NOTIFICATION_IS_CLOSED"
    private fun getLastAlarmNotificationIsClosed(context: Context): Boolean {
        return context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE).getBoolean(LAST_ALARM_NOTIFICATION_IS_CLOSED, true)
    }
    private fun setLastAlarmNotificationIsClosed(context: Context, value: Boolean) {
        context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE).edit {
            putBoolean(LAST_ALARM_NOTIFICATION_IS_CLOSED, value)
        }
    }
}