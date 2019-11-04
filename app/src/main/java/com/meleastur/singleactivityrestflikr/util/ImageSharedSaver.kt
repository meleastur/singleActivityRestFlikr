package com.meleastur.singleactivityrestflikr.util

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.meleastur.singleactivityrestflikr.util.callback.GenericCallback
import com.meleastur.singleactivityrestflikr.util.callback.VoidCallback
import org.androidannotations.annotations.Background
import org.androidannotations.annotations.EBean
import java.io.File
import java.io.FileOutputStream
import java.net.URI


@EBean
open class ImageSharedSaver {

    @Background
    open fun saveBitmap(activity: Activity, fullImageURL: String, bitmap: Bitmap, callback: GenericCallback<Uri?>) {
        try {
            val uri: Uri?
            val tokensVal = URI(fullImageURL).path.split("/")
            var fileName = "simpleActivity_image_share_"
            when {
                tokensVal.size == 3 -> fileName += tokensVal[2]
                tokensVal.size == 2 -> fileName += tokensVal[1]
                tokensVal.size == 1 -> fileName += tokensVal[0]
            }
            val file = File(activity.cacheDir, fileName)

            if (!file.exists()) {
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                stream.close()
            }

            uri = FileProvider.getUriForFile(activity, ".fileprovider", file)
            callback.onSuccess(uri)
        } catch (e: Exception) {
            Log.e("ImageSharedSaver", "Exception while trying to write file for sharing: " + e.message)
            callback.onError(e.message)
        }
    }

    @Background
    open fun deleteBitmap(activity: Activity, fullImageURL: String, bitmap: Bitmap, callBack: VoidCallback) {
        try {
            val uriAux = URI(fullImageURL)
            val tokensVal = uriAux.path.split("/")
            var fileName = "simpleActivity_image_share_"
            when {
                tokensVal.size == 3 -> fileName += tokensVal[2]
                tokensVal.size == 2 -> fileName += tokensVal[1]
                tokensVal.size == 1 -> fileName += tokensVal[0]
            }
            val file = File(activity.cacheDir, fileName)

            if (file.exists()) {
                file.delete()
            }
            callBack.onSuccess()
        } catch (e: Exception) {
            Log.e("ImageSharedSaver", "Exception while trying to write file for sharing: " + e.message)
            callBack.onError(e.message)
        }
    }
}