package com.oustme.oustsdk.calendar_ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.calendar_ui.custom.utils.DayColorsUtils;
import com.oustme.oustsdk.calendar_ui.custom.utils.EventDayUtils;
import com.oustme.oustsdk.calendar_ui.custom.utils.SelectedDay;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class CalendarDayAdapter extends ArrayAdapter<Date> {
    private OustCalendarPageAdapter mCalendarPageAdapter;
    private LayoutInflater mLayoutInflater;
    private int mPageMonth;
    private Calendar mToday = DateUtils.getCalendar();

    private CalendarProperties mCalendarProperties;

    CalendarDayAdapter(OustCalendarPageAdapter calendarPageAdapter, Context context, CalendarProperties calendarProperties,
                       ArrayList<Date> dates, int pageMonth) {
        super(context, calendarProperties.getItemLayoutResource(), dates);
        mCalendarPageAdapter = calendarPageAdapter;
        mCalendarProperties = calendarProperties;
        mPageMonth = pageMonth < 0 ? 11 : pageMonth;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = mLayoutInflater.inflate(mCalendarProperties.getItemLayoutResource(), parent, false);
        }

        TextView dayLabel = view.findViewById(R.id.dayLabel);
        ImageView dayIcon = view.findViewById(R.id.dayIcon);
        LinearLayout day_layout = view.findViewById(R.id.day_layout);

        Calendar day = new GregorianCalendar();
        day.setTime(Objects.requireNonNull(getItem(position)));

        // Loading an image of the event
      /*  if (dayIcon != null) {
            loadIcon(dayIcon, day);
        }*/

        setLabelColors(dayLabel, day,dayIcon);

        dayLabel.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

        return view;
    }

    private void setLabelColors(TextView dayLabel, Calendar day,ImageView dayIcon) {
        // Setting not current month day color
        if (!isCurrentMonthDay(day)) {
            DayColorsUtils.setDayColors(dayLabel, mCalendarProperties.getAnotherMonthsDaysLabelsColor(),
                    Typeface.NORMAL, R.drawable.background_transparent);
            return;
        }

        // Setting view for all SelectedDays
        if (isSelectedDay(day)) {
            Stream.of(mCalendarPageAdapter.getSelectedDays())
                    .filter(selectedDay -> selectedDay.getCalendar().equals(day))
                    .findFirst().ifPresent(selectedDay -> selectedDay.setView(dayLabel));


            if(day.compareTo(Calendar.getInstance())==0){
                DayColorsUtils.setTodayColors(dayLabel, mCalendarProperties);
            }else{
                DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties);

            }

            return;
        }

        // Setting disabled days color
        if (!isActiveDay(day)) {
            DayColorsUtils.setDayColors(dayLabel, mCalendarProperties.getDisabledDaysLabelsColor(),
                    Typeface.NORMAL, R.drawable.background_transparent);
            return;
        }

        // Setting custom label color for event day
        if (isEventDayWithLabelColor(day)) {
            dayIcon.setVisibility(View.VISIBLE);
            try{
                OustSdkTools.drawableColor(dayIcon.getDrawable());
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties);
            return;
        }

        // Setting current month day color
        DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties);


    }

    private boolean isSelectedDay(Calendar day) {
        return mCalendarProperties.getCalendarType() != OustCalendarView.STANDARD && day.get(Calendar.MONTH) == mPageMonth
                && mCalendarPageAdapter.getSelectedDays().contains(new SelectedDay(day));
    }

    private boolean isEventDayWithLabelColor(Calendar day) {
        return EventDayUtils.isEventDayWithLabelColor(day, mCalendarProperties);
    }

    private boolean isCurrentMonthDay(Calendar day) {
        return day.get(Calendar.MONTH) == mPageMonth &&
                !((mCalendarProperties.getMinimumDate() != null && day.before(mCalendarProperties.getMinimumDate()))
                        || (mCalendarProperties.getMaximumDate() != null && day.after(mCalendarProperties.getMaximumDate())));
    }

    private boolean isActiveDay(Calendar day) {
        return !mCalendarProperties.getDisabledDays().contains(day);
    }

    private void loadIcon(ImageView dayIcon, Calendar day) {
        if (mCalendarProperties.getEventDays() == null || !mCalendarProperties.getEventsEnabled()) {
            dayIcon.setVisibility(View.GONE);
            return;
        }

        Stream.of(mCalendarProperties.getEventDays()).filter(eventDate ->
                eventDate.getCalendar().equals(day)).findFirst().executeIfPresent(eventDay -> {


            // If a day doesn't belong to current month then image is transparent
            if (!isCurrentMonthDay(day) || !isActiveDay(day)) {
                dayIcon.setAlpha(0.12f);
            }

        });
    }
}

