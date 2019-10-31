package com.meleastur.singleactivityrestflikr.util

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
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
            val resolver = activity.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "share_image_" + System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            val file: File? = null
            var out: FileOutputStream? = null
            if (uri != null) {
                resolver.openOutputStream(uri).use {
                    file?.parentFile?.mkdirs()
                    if(file!=null){
                        out = FileOutputStream(file)
                        bmp.compress(Bitmap.CompressFormat.PNG, 90, out)

                        bmpUri = FileProvider.getUriForFile(activity.applicationContext,
                            "com.meleastur.singleactivityrestflikr.provider", file
                        )
                    }
                }
            }
            out?.close()
        } catch (e: IOException) {
            Log.e("getLocalBitmapUri", "" + e.localizedMessage)
        }

        return bmpUri
    }
}