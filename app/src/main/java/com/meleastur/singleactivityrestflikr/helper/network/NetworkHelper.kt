package com.meleastur.singleactivityrestflikr.helper.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import org.androidannotations.annotations.EBean

@EBean
open class NetworkHelper {

    @Suppress("DEPRECATION")
    fun isWiFiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                        else -> return false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        return true
                    }
                }
            }
        }
        return false
    }
}