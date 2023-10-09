package com.oustme.oustsdk.util;

import android.text.format.DateFormat;

public class AchievementUtils {

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

}
