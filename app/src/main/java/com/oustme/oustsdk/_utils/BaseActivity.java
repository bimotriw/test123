package com.oustme.oustsdk._utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oustme.oustsdk.tools.OustSdkTools;

public abstract class BaseActivity extends AppCompatActivity {

    protected static final String TAG = com.oustme.oustsdk.base.BaseActivity.class.getName();

    public Context context;
    public Activity activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            context = getApplicationContext();
            activity = this;
            setContentView(getContentView());
            initView();
            initializeData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initializeData() {
        try {
            Log.d(TAG, "initializeData: ");
            initData();
            initListener();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    @Override
    protected void onPause() {
        super.onPause();
    }
}
