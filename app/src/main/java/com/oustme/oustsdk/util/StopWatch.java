package com.oustme.oustsdk.util;

import android.os.CountDownTimer;
import android.util.Log;


public class StopWatch {

    private CountDownTimer timer;
    private boolean paused, stoped, started;

    private long swMillisInFuture;
    private long swCountDownInterval;

    private StopwatchCallback callback;

    private static final String TAG = "StopWatch";

    public StopWatch(long millisInFuture, long countDownInterval, StopwatchCallback callback) {
        swMillisInFuture = millisInFuture;
        swCountDownInterval = countDownInterval;
        this.callback = callback;
    }

    public void start() {
        started = true;
        timer = new CountDownTimer(swMillisInFuture, swCountDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, timer.hashCode() + " onTick paused "+ paused+" stoped " +stoped +" "+ millisUntilFinished);
                if (paused || stoped) {

                    swMillisInFuture = millisUntilFinished;
                    timer.cancel();
                    return;
                }

                if (callback != null)
                    callback.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, timer.hashCode() + " onFinish stoped " + stoped);
                if (callback != null && !stoped)
                    callback.onFinish();
            }
        };
        timer.start();
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
        start();
    }

    public void stop() {
        stoped = true;
        timer.cancel();
        Log.d(TAG, timer.hashCode() + " stop stoped " + stoped);

    }

    public interface StopwatchCallback {
        void onTick(long millisUntilFinished);

        void onFinish();
    }
}
