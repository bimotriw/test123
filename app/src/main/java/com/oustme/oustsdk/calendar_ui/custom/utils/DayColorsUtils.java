package com.oustme.oustsdk.calendar_ui.custom.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.calendar_ui.custom.CalendarProperties;
import com.oustme.oustsdk.tools.OustPreferences;

import java.util.Calendar;

public class DayColorsUtils {


    public static void setDayColors(TextView textView, int textColor, int typeface, int background) {
        if (textView == null) {
            return;
        }

        textView.setTypeface(null, typeface);
        textView.setTextColor(textColor);
        textView.setBackgroundResource(background);
    }


    public static void setSelectedDayColors(TextView dayLabel, CalendarProperties calendarProperties) {

        setDayColors(dayLabel, calendarProperties.getSelectionLabelColor(), Typeface.NORMAL,
                R.drawable.background_color_circle_selector);

        setDayBackgroundColor(dayLabel, calendarProperties.getSelectionColor());
    }


    public static void setCurrentMonthDayColors(Calendar day, Calendar today, TextView dayLabel,
                                                CalendarProperties calendarProperties) {
        if (today.equals(day)) {
            setTodayColors(dayLabel, calendarProperties);
        } else if (EventDayUtils.isEventDayWithLabelColor(day, calendarProperties)) {
            setEventDayColors(day, dayLabel, calendarProperties);
        } else if (calendarProperties.getHighlightedDays().contains(day)) {
            setHighlightedDayColors(dayLabel, calendarProperties);
        } else {
            setNormalDayColors(dayLabel, calendarProperties);
        }
    }

    public static void setTodayColors(TextView dayLabel, CalendarProperties calendarProperties) {
        setDayColors(dayLabel, calendarProperties.getTodayLabelColor(), Typeface.BOLD,
                R.drawable.background_transparent);

        // Sets custom background color for present
        if (calendarProperties.getTodayColor() != 0) {
            setDayColors(dayLabel, calendarProperties.getSelectionLabelColor(), Typeface.NORMAL,
                    R.drawable.background_color_circle_selector);
            setDayBackgroundColor(dayLabel, calendarProperties.getTodayColor());
        }
    }

    private static void setEventDayColors(Calendar day, TextView dayLabel, CalendarProperties calendarProperties) {
        String toolbarColorCode = "#01b5a2";
        if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
            toolbarColorCode = OustPreferences.get("toolbarColorCode");
        }
        String finalToolbarColorCode = toolbarColorCode;
        EventDayUtils.getEventDayWithLabelColor(day, calendarProperties).executeIfPresent(eventDay ->
                DayColorsUtils.setDayColors(dayLabel, Color.parseColor(finalToolbarColorCode),
                        Typeface.NORMAL, R.drawable.background_transparent));
    }

    private static void setHighlightedDayColors(TextView dayLabel, CalendarProperties calendarProperties) {
        setDayColors(dayLabel, calendarProperties.getHighlightedDaysLabelsColor(),
                Typeface.NORMAL, R.drawable.background_transparent);
    }

    private static void setNormalDayColors(TextView dayLabel, CalendarProperties calendarProperties) {
        setDayColors(dayLabel, calendarProperties.getDaysLabelsColor(), Typeface.NORMAL,
                R.drawable.background_transparent);
    }

    private static void setDayBackgroundColor(TextView dayLabel, int color) {
        dayLabel.getBackground().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
    }
}
