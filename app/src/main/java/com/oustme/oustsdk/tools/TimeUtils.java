package com.oustme.oustsdk.tools;

import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class TimeUtils {
    private static final String TAG = "OustAndroid:" + TimeUtils.class.getName();

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss a";

    public static String getCurrentDateAsString() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }

    public static String getDateAsString(String time) {
        try {
            Log.d(TAG, "In date " + time);
            String timezoneID = TimeZone.getDefault().getID();
            Log.d(TAG,"Time zone = " + timezoneID);
            //create SimpleDateFormat object with source string date format
            SimpleDateFormat sdfSource = new SimpleDateFormat(DATE_FORMAT_NOW);
            sdfSource.setTimeZone(TimeZone.getDefault());
            //parse the string into Date object
            Date date = new Date(Long.parseLong(time));
            //create SimpleDateFormat object with desired date format
            //SimpleDateFormat sdfDestination = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
            //parse the date into another format
            time = sdfSource.format(date);

        } catch (Exception pe) {
            Log.d(TAG,"Parse Exception : " + pe);
        }

        return time;
    }
}
