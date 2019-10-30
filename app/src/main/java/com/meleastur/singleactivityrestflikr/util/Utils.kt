package com.meleastur.singleactivityrestflikr.util

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import org.androidannotations.annotations.EBean
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@EBean
open class Utils {

    fun getLocalBitmapUri(activity: Activity, bitmap: Bitmap): Uri? {
        val bmp = bitmap
        var bmpUri: Uri? = null
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ), "share_image_" + System.currentTimeMillis() + ".png"
            )
            file.parentFile.mkdirs()
            val out = FileOutputStream(file)
            bmp!!.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(
                activity.applicationContext,
                "com.meleastur.singleactivityrestflikr.provider", file
            )
        } catch (e: IOException) {
            Log.e("getLocalBitmapUri", "" + e.localizedMessage)
        }

        return bmpUri
    }
}