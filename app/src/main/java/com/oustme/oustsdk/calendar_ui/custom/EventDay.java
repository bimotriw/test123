package com.oustme.oustsdk.calendar_ui.custom;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

import java.util.Calendar;

public class EventDay {
    private Calendar mDay;
    private Object mDrawable;
    private int mLabelColor;
    private boolean mIsDisabled;

    public EventDay(Calendar day) {
        mDay = day;

    }


    public EventDay(Calendar day, @DrawableRes int drawable) {
        DateUtils.setMidnight(day);
        mDay = day;
        mDrawable = drawable;
    }


    public EventDay(Calendar day, Drawable drawable) {
        DateUtils.setMidnight(day);
        mDay = day;
        mDrawable = drawable;
    }


    public EventDay(Calendar day, @DrawableRes int drawable, int labelColor) {
        DateUtils.setMidnight(day);
        mDay = day;
        mDrawable = drawable;
        mLabelColor = labelColor;
    }


    public EventDay(Calendar day, Drawable drawable, int labelColor) {
        DateUtils.setMidnight(day);
        mDay = day;
        mDrawable = drawable;
        mLabelColor = labelColor;
    }


    public Object getImageDrawable() {
        return mDrawable;
    }

    public int getLabelColor() {
        return mLabelColor;
    }

    public Calendar getCalendar() {
        return mDay;
    }

    public boolean isEnabled() {
        return !mIsDisabled;
    }

    public void setEnabled(boolean enabled) {
        mIsDisabled = enabled;
    }


}

