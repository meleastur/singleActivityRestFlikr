package com.meleastur.singleactivityrestflikr.common.callback

interface VoidCallback {

    fun onSuccess()

    fun onError(error: String?)
}
