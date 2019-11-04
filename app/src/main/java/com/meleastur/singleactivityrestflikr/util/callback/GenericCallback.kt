package com.meleastur.singleactivityrestflikr.util.callback

interface GenericCallback<T> {

    fun onSuccess(successObject: T)

    fun onError(error: String?)
}
