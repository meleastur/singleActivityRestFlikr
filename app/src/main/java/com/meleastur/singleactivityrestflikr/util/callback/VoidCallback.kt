package com.meleastur.singleactivityrestflikr.util.callback

interface VoidCallback {

    fun onSuccess()

    fun onError(error: String?)
}
