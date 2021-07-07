package com.stenleone.clenner.receiver

import android.content.Context
import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore
import com.infoedge.installreferrer.receiver.ReferralReceiver
import com.stenleone.clenner.managers.config.Config
import com.stenleone.clenner.managers.config.ConfigService
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.network.ApiService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import javax.inject.Inject


@AndroidEntryPoint
class InstallReferrerReceiver : BaseReceiver() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var configService: ConfigService

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        try {
            ReferralReceiver().onReceive(context, intent)
            val stringExtra = intent.getStringExtra("referrer")
            val clId = getClId(stringExtra)

            CoroutineScope(Dispatchers.IO).launch {

                var mainUrl = configService.getStringAsync(Config.POST_USER_DATA_URL)

                try {

                    FirebaseFirestore.getInstance().collection("postBack")
                        .add(hashMapOf(Pair("list", clId)))


//                    apiService.sendSource(mainUrl + "${ApiService.POSTBACK}/?cnv_id=${clId}")

                    apiService.postUserAppOpen("${mainUrl}${clId}")

                    prefs.isSendedDataAfterFirstOpen = true
                    this.cancel()
                } catch (e: Exception) {

                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getClId(str: String?): String {
        if (!(str == null || str.length == 0)) {
            try {
                try {
                    for (split in URLDecoder.decode(str, "UTF-8").split("&")) {
                        val split2 = split.split("=".toRegex()).toTypedArray()
                        if (split2.size == 1) {
                            return split2[0]
                        }
                        if ("clid" == split2[0]) {
                            return split2[1]
                        }
                    }
                } catch (e: Exception) {
                }
                return "EMPTY_REFERRER"
            } catch (e2: UnsupportedEncodingException) {
            }
        }
        return "EMPTY_REFERRER"
    }


}