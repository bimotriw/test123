package com.oustme.oustsdk.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;


public class LanguagePreferences {

    private static LanguagePreferences instance = null;
    private static final String PREFERENCE_NAME = "OustLanguage";
    private static String TAG = "LanguagePreferences";

    private LanguagePreferences() {
        // Exists only to defeat instantiation.
    }

    public static LanguagePreferences getInstance() {
        if (instance == null) {
            synchronized (OustPreferences.class) {
                if (instance == null) {
                    instance = new LanguagePreferences();
                }
            }
        }
        return instance;
    }

    public static void save(String Key, String Value) {
        try {
            SharedPreferences preference;
            SharedPreferences.Editor editor;
            // preference = OustSdkApplication.getContext().getSharedPreferences("OustLanguage", Context.MODE_PRIVATE);
            Context storageContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                final Context deviceContext = OustSdkApplication.getContext().createDeviceProtectedStorageContext();
                if (!deviceContext.moveSharedPreferencesFrom(OustSdkApplication.getContext(),
                        PREFERENCE_NAME)) {
                    Log.w(TAG, "Failed to migrate shared preferences.");
                }
                storageContext = deviceContext;
            } else {
                storageContext = OustSdkApplication.getContext();
            }
            preference = storageContext.getSharedPreferences(PREFERENCE_NAME,
                    Context.MODE_PRIVATE);
            editor = preference.edit();
            editor.putString(Key, Value);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public static String get(String Key) {
        String text = "";
        try {
            SharedPreferences preference;
            //preference = OustSdkApplication.getContext().getSharedPreferences("OustLanguage", Context.MODE_PRIVATE);
            Context storageContext = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (OustSdkApplication.getContext() != null) {
                    final Context deviceContext = OustSdkApplication.getContext().createDeviceProtectedStorageContext();
                    if (!deviceContext.moveSharedPreferencesFrom(OustSdkApplication.getContext(),
                            PREFERENCE_NAME)) {
                        Log.w(TAG, "Failed to migrate shared preferences.");
                    }
                    storageContext = deviceContext;
                }
            } else {
                storageContext = OustSdkApplication.getContext();
            }
            if (storageContext != null) {
                preference = storageContext.getSharedPreferences(PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
                text = preference.getString(Key, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return text;
    }
}
