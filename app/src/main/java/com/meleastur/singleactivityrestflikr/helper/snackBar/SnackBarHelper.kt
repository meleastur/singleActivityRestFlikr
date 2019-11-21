package com.meleastur.singleactivityrestflikr.helper.snackBar

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.meleastur.singleactivityrestflikr.R
import org.androidannotations.annotations.EBean

@EBean
open class SnackBarHelper {

    fun makeDefaultSnack(view: View, text: String, isLongTime: Boolean) {
        Snackbar.make(
            view,
            text,
            if (isLongTime) {
                Snackbar.LENGTH_SHORT
            } else {
                Snackbar.LENGTH_LONG
            }
        ).setTextColor(view.resources.getColor(R.color.colorPrimary))
            .setBackgroundTint(view.resources.getColor(R.color.colorAccent))
            .show()
    }

    fun makeSimpleSnack(view: View, text: String, isLongTime: Boolean, textColor: Int, backgroundColor: Int) {
        Snackbar.make(
            view,
            text,
            if (isLongTime) {
                Snackbar.LENGTH_SHORT
            } else {
                Snackbar.LENGTH_LONG
            }
        ).setTextColor(view.resources.getColor(textColor))
            .setBackgroundTint(view.resources.getColor(backgroundColor))
            .show()
    }
}