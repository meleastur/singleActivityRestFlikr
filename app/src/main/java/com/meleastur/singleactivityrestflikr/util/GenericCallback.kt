package com.meleastur.singleactivityrestflikr.util

interface GenericCallback<T> {

    fun onSuccess(successObject: T)

    fun onError(error: String?)
}
