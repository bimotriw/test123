package com.oustme.oustsdk.util;

import android.annotation.SuppressLint;
import android.util.Log;


import com.oustme.oustsdk.tools.OustSdkTools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotificationUtils {

    public static String findInBetweenDateCount(String toData, String fromDate) {
        String noOfDays = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String from = fromDate;
        String to = toData;

        try {
            Date date1 = myFormat.parse(from);
            Date date2 = myFormat.parse(to);
            assert date2 != null;
            assert date1 != null;
            long diff = date2.getTime() - date1.getTime();
            noOfDays = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return noOfDays;
    }
}
