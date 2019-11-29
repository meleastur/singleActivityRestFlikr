package com.meleastur.singleactivityrestflikr.common.callback

interface GenericCallback<T> {

    fun onSuccess(successObject: T)

    fun onError(error: String)
}
