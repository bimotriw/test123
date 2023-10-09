package com.oustme.oustsdk.calendar_ui.custom;

import android.content.Context;

import com.annimon.stream.Stream;
import com.oustme.oustsdk.R;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        setMidnight(calendar);

        return calendar;
    }


    public static void setMidnight(Calendar calendar) {
        if (calendar != null) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
    }


    public static boolean isMonthBefore(Calendar firstCalendar, Calendar secondCalendar) {
        if (firstCalendar == null) {
            return false;
        }

        Calendar firstDay = (Calendar) firstCalendar.clone();
        setMidnight(firstDay);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar secondDay = (Calendar) secondCalendar.clone();
        setMidnight(secondDay);
        secondDay.set(Calendar.DAY_OF_MONTH, 1);

        return secondDay.before(firstDay);
    }


    public static boolean isMonthAfter(Calendar firstCalendar, Calendar secondCalendar) {
        if (firstCalendar == null) {
            return false;
        }

        Calendar firstDay = (Calendar) firstCalendar.clone();
        setMidnight(firstDay);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar secondDay = (Calendar) secondCalendar.clone();
        setMidnight(secondDay);
        secondDay.set(Calendar.DAY_OF_MONTH, 1);

        return secondDay.after(firstDay);
    }


    public static String getMonthAndYearDate(Context context, Calendar calendar) {
        return String.format("%s  %s",
                context.getResources().getStringArray(R.array.calendar_months_array)[calendar.get(Calendar.MONTH)],
                calendar.get(Calendar.YEAR));
    }


    public static int getMonthsBetweenDates(Calendar startCalendar, Calendar endCalendar) {
        int years = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        return years * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }



    private static long getDaysBetweenDates(Calendar startCalendar, Calendar endCalendar) {
        long milliseconds = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(milliseconds);
    }

    public static boolean isFullDatesRange(List<Calendar> days) {
        int listSize = days.size();

        if (days.isEmpty() || listSize == 1) {
            return true;
        }

        List<Calendar> sortedCalendars = Stream.of(days).sortBy(Calendar::getTimeInMillis).toList();

        return listSize == getDaysBetweenDates(sortedCalendars.get(0), sortedCalendars.get(listSize - 1)) + 1;
    }
}
