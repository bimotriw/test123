package com.oustme.oustsdk.calendar_ui.custom.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CalendarUtils {


    public static Drawable getDrawableText(Context context, String text, Typeface typeface, int color, int size) {
        Resources resources = context.getResources();
        Bitmap bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setTypeface(typeface != null ? typeface : Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(ContextCompat.getColor(context, color));

        float scale = resources.getDisplayMetrics().density;
        paint.setTextSize((int) (size * scale));

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(text, x, y, paint);

        return new BitmapDrawable(context.getResources(), bitmap);
    }


    public static ArrayList<Calendar> getDatesRange(Calendar firstDay, Calendar lastDay) {
        if (lastDay.before(firstDay)) {
            return getCalendarsBetweenDates(lastDay.getTime(), firstDay.getTime());
        }

        return getCalendarsBetweenDates(firstDay.getTime(), lastDay.getTime());
    }

    private static ArrayList<Calendar> getCalendarsBetweenDates(Date dateFrom, Date dateTo) {
        ArrayList<Calendar> calendars = new ArrayList<>();

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(dateFrom);

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(dateTo);

        long daysBetweenDates = TimeUnit.MILLISECONDS.toDays(
                calendarTo.getTimeInMillis() - calendarFrom.getTimeInMillis());

        for (int i = 1; i < daysBetweenDates; i++) {
            Calendar calendar = (Calendar) calendarFrom.clone();
            calendars.add(calendar);
            calendar.add(Calendar.DATE, i);
        }

        return calendars;
    }

    private CalendarUtils() {
    }
}

