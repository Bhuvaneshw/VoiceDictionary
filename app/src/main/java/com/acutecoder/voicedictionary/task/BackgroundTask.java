package com.acutecoder.voicedictionary.task;

/*
 *Created by Bhuvaneshwaran
 *on 11:00 PM, 12/5/2022
 *AcuteCoder
 */

import android.os.Handler;
import android.os.Looper;

public class BackgroundTask {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void execute(Runnable bgTask) {
        execute(bgTask, null);
    }

    public static void execute(Runnable bgTask, Runnable onFinished) {
        new Thread(() -> {
            bgTask.run();
            if (onFinished != null)
                handler.post(onFinished);
        }).start();
    }

    public static void onMainThread(Runnable runnable){
        handler.post(runnable);
    }
}
