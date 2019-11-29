package com.meleastur.singleactivityrestflikr.helper.speechToText

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.meleastur.singleactivityrestflikr.common.callback.GenericCallback
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext

@EBean
open class STTHelper : RecognitionListener {

    @RootContext
    lateinit var activity: Activity

    private val tag = "STTHelper"
    private var speechRecognizer: SpeechRecognizer? = null
    private var intent: Intent? = null
    private var callback: GenericCallback<String>? = null

    fun makeSpeechInput(stringCallback: GenericCallback<String>) {
        callback = stringCallback
        makeSpeechInput()
    }

    fun start() {
        initIntent()
        try {
            speechRecognizer!!.startListening(intent)
        } catch (e: Exception) {
            callback?.onError(e.message!!)
        }
    }

    fun stop() {
        try {
            speechRecognizer!!.stopListening()
        } catch (e: Exception) {
            callback!!.onError(e.message!!)
        }
    }

    private fun initIntent() {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test")
        intent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2)
    }

    private fun makeSpeechInput() {
        if (callback == null) {
            callback!!.onError("activity: $activity| callback: $callback")
            return
        }
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
            speechRecognizer!!.setRecognitionListener(this)
        }
    }

    override fun onError(error: Int) {
        Log.d(tag, "error $error")
        var errorString = "unknown error"
        when (error) {
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> errorString = "Network operation timed out"
            SpeechRecognizer.ERROR_NETWORK -> errorString = "Other network related errors"
            SpeechRecognizer.ERROR_AUDIO -> errorString = "Audio recording error"
            SpeechRecognizer.ERROR_SERVER -> errorString = "Server sends error status"
            SpeechRecognizer.ERROR_CLIENT -> errorString = "Other client side errors"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> return // errorString = "No speech input"
            SpeechRecognizer.ERROR_NO_MATCH -> errorString = "No recognition result matched"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> errorString = "RecognitionService busy"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> errorString = "Insufficient permissions"
        }
        callback!!.onError("error $errorString")
    }

    override fun onResults(results: Bundle) {
        var str = String()
        Log.d(tag, "onResults $results")
        val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList
        data.forEach {
            Log.d(tag, "results: $it")
        }
        callback!!.onSuccess(data[0])
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(tag, "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(tag, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(tag, "onRmsChanged")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(tag, "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        Log.d(tag, "onEndofSpeech")
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.d(tag, "onPartialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(tag, "onEvent $eventType")
    }
}
