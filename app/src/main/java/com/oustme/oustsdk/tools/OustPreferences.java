package com.oustme.oustsdk.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class OustPreferences {
    private static OustPreferences instance = null;

    private OustPreferences() {
        // Exists only to defeat instantiation.
    }

    public static OustPreferences getInstance() {
        if (instance == null) {
            synchronized (OustPreferences.class) {
                if (instance == null) {
                    instance = new OustPreferences();
                }
            }
        }
        return instance;
    }


    private static final String TAG = "OustAndroid:" + OustPreferences.class.getName();


    public static void save(String Key, String Value) {
        try {
            SharedPreferences preference;
            SharedPreferences.Editor editor;
            if (OustSdkApplication.getContext() != null) {
                preference = OustSdkApplication.getContext().getSharedPreferences("OustMe", Context.MODE_PRIVATE);
                editor = preference.edit();
                editor.putString(Key, Value);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public static String get(String Key) {
        String text = "";
        try {
            SharedPreferences preference;
            if (OustSdkApplication.getContext() != null) {
                preference = OustSdkApplication.getContext().getSharedPreferences("OustMe", Context.MODE_PRIVATE);
                text = preference.getString(Key, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return text;
    }

    public static void clear(String Key) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        if (OustSdkApplication.getContext() != null) {
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                    Context.MODE_PRIVATE);
            editor = preference.edit();
            editor.remove(Key);
            editor.commit();
        }
    }

    public static void clearAll() {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.clear();
        editor.commit();
        instance = new OustPreferences();
    }

    public static void saveAppInstallVariable(String key, boolean value) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        try {
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                    Context.MODE_PRIVATE);
            editor = preference.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static boolean getAppInstallVariable(String key) {
        SharedPreferences preference;
        boolean val = false;
        try {
            if (OustSdkApplication.getContext() != null) {
                preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                        Context.MODE_PRIVATE);
                val = preference.getBoolean(key, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return val;
    }

    public static boolean getAppInstallVariableDefaultTrue(String key) {
        SharedPreferences preference;
        boolean val = false;
        try {
            if (OustSdkApplication.getContext() != null) {
                preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                        Context.MODE_PRIVATE);
                val = preference.getBoolean(key, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return val;
    }

    public static void saveTimeForNotification(String key, long value) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getTimeForNotification(String key) {
        SharedPreferences preference;
        long val = 0;
        if (OustSdkApplication.getContext() != null) {
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                    Context.MODE_PRIVATE);
            val = preference.getLong(key, 0);
        }
        return val;
    }

    public static void saveLocalNotificationMsg(String key, List<String> msgs) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = OustSdkApplication.getContext().getSharedPreferences("OustMe", Context.MODE_PRIVATE);
        editor = preference.edit();
        Set<String> set = new HashSet<>();
        set.addAll(msgs);
        editor.putStringSet(key, set);
        editor.commit();
    }

    public static List<String> getLoacalNotificationMsgs(String key) {
        SharedPreferences preference;
        List<String> msgs = new ArrayList<>();
        try {
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe", Context.MODE_PRIVATE);
            Set<String> set;
            set = preference.getStringSet(key, new HashSet<String>());
            if (set != null) {
                msgs.addAll(set);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return msgs;
    }

    public static void saveReminderNotification(String key, List<String> commonLandingData) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = OustSdkApplication.getContext().getSharedPreferences("OustMe", Context.MODE_PRIVATE);
        editor = preference.edit();
        Set<String> set = new HashSet<>();
        set.addAll(commonLandingData);
        editor.putStringSet(key, set);
        editor.commit();
    }

    public static List<String> getRminderNotification(String key) {
        SharedPreferences preference;
        List<String> msgs = new ArrayList<>();
        try {
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe", Context.MODE_PRIVATE);
            Set<String> set = new HashSet<>();
            set = preference.getStringSet(key, new HashSet<String>());
            msgs.addAll(set);
        } catch (Exception e) {
        }
        return msgs;
    }

    public static void saveintVar(String key, int value) {
        try {
            SharedPreferences preference;
            SharedPreferences.Editor editor;
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                    Context.MODE_PRIVATE);
            editor = preference.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static int getSavedInt(String key) {
        try {
            SharedPreferences preference;
            int val;
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                    Context.MODE_PRIVATE);
            val = preference.getInt(key, 0);
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return 0;
    }

    public static void saveThroughOtherClass(String Key, String Value, Context context) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = context.getSharedPreferences("OustMe", Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.putString(Key, Value);
        editor.commit();
    }

    public static void saveLongVar(String key, long value) {
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                Context.MODE_PRIVATE);
        editor = preference.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getSavedLong(String key) {
        try {
            SharedPreferences preference;
            long val;
            preference = OustSdkApplication.getContext().getSharedPreferences("OustMe",
                    Context.MODE_PRIVATE);
            val = preference.getLong(key, 0);
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return 0;
    }


}