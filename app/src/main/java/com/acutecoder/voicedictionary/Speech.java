package com.acutecoder.voicedictionary;

/*
 *Created by Bhuvaneshwaran
 *on 1:08 AM, 12/6/2022
 *AcuteCoder
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.ArrayList;

public class Speech implements TextToSpeech.OnInitListener, RecognitionListener {

    private final TextToSpeech speak;
    private final Context context;
    private SpeechRecognizer speech;
    private Intent recognizerIntent;
    private OnActionListener onActionListener;
    private boolean isPaused;

    public Speech(Context context) {
        this.context = context;
        speak = new TextToSpeech(context, this);
        reInit();
    }

    public void reInit() {
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS)
            Toast.makeText(context, "Unable to initialize speech service!", Toast.LENGTH_SHORT).show();
    }

    public void stop() {
        if (speak.isSpeaking())
            speak.stop();
    }

    public void onPause() {
        if (speech != null) {
            speech.stopListening();
            speech.cancel();
            isPaused = true;
        }
    }

    public void recognize() {
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        if (onActionListener != null) onActionListener.onStart();
    }

    @Override
    public void onError(int error) {
        if (onActionListener != null) onActionListener.onError(error);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (onActionListener != null && matches.size() > 0)
            onActionListener.onResult(matches.get(0));
    }

    public void speak(String... messages) {
        for (String msg : messages)
            speak.speak(msg, TextToSpeech.QUEUE_ADD, null, null);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float db) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    public interface OnActionListener {
        void onStart();

        void onResult(String result);

        void onError(int code);
    }
}
