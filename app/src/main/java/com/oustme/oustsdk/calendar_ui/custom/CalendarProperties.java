package com.oustme.oustsdk.calendar_ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.annimon.stream.Stream;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.calendar_ui.custom.utils.SelectedDay;
import com.oustme.oustsdk.tools.OustPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarProperties {

    public static final int CALENDAR_SIZE = 2401;
    public static final int FIRST_VISIBLE_PAGE = CALENDAR_SIZE / 2;

    private int mCalendarType, mHeaderColor, mHeaderLabelColor, mSelectionColor, mTodayLabelColor, mTodayColor,
            mDialogButtonsColor, mItemLayoutResource, mDisabledDaysLabelsColor, mHighlightedDaysLabelsColor, mPagesColor,
            mAbbreviationsBarColor, mAbbreviationsLabelsColor, mDaysLabelsColor, mSelectionLabelColor,
            mAnotherMonthsDaysLabelsColor, mHeaderVisibility, mNavigationVisibility, mAbbreviationsBarVisibility, mMaximumDaysRange;

    private boolean mEventsEnabled;
    private boolean mSwipeEnabled;

    private Drawable mPreviousButtonSrc, mForwardButtonSrc;

    private Calendar mFirstPageCalendarDate = DateUtils.getCalendar();
    private Calendar mCalendar, mMinimumDate, mMaximumDate;

    private OnDayClickListener mOnDayClickListener;
    private OnSelectDateListener mOnSelectDateListener;
    private OnSelectionAbilityListener mOnSelectionAbilityListener;
    private OnCalendarPageChangeListener mOnPreviousPageChangeListener;
    private OnCalendarPageChangeListener mOnForwardPageChangeListener;

    private List<EventDay> mEventDays = new ArrayList<>();
    private List<Calendar> mDisabledDays = new ArrayList<>();
    private List<Calendar> mHighlightedDays = new ArrayList<>();
    private List<SelectedDay> mSelectedDays = new ArrayList<>();

    private Context mContext;
    String toolbarColorCode;

    public CalendarProperties(Context context) {
        mContext = context;
        if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
            toolbarColorCode = OustPreferences.get("toolbarColorCode");
        } else {
            toolbarColorCode = "#01b5a2";
        }
    }

    public int getCalendarType() {
        return mCalendarType;
    }

    public void setCalendarType(int calendarType) {
        mCalendarType = calendarType;
    }

    public boolean getEventsEnabled() {
        return mEventsEnabled;
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        mEventsEnabled = eventsEnabled;
    }

    public boolean getSwipeEnabled() {
        return mSwipeEnabled;
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        mSwipeEnabled = swipeEnabled;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
    }

    public OnSelectDateListener getOnSelectDateListener() {
        return mOnSelectDateListener;
    }

    public void setOnSelectDateListener(OnSelectDateListener onSelectDateListener) {
        mOnSelectDateListener = onSelectDateListener;
    }

    public int getHeaderColor() {
        if (mHeaderColor <= 0) {
            return mHeaderColor;
        }

        return ContextCompat.getColor(mContext, mHeaderColor);
    }

    public void setHeaderColor(int headerColor) {
        mHeaderColor = headerColor;
    }

    public int getHeaderLabelColor() {
        if (mHeaderLabelColor <= 0) {
            return mHeaderLabelColor;
        }

        return ContextCompat.getColor(mContext, mHeaderLabelColor);
    }

    public void setHeaderLabelColor(int headerLabelColor) {
        mHeaderLabelColor = headerLabelColor;
    }

    public Drawable getPreviousButtonSrc() {
        return mPreviousButtonSrc;
    }

    public void setPreviousButtonSrc(Drawable previousButtonSrc) {
        mPreviousButtonSrc = previousButtonSrc;
    }

    public Drawable getForwardButtonSrc() {
        return mForwardButtonSrc;
    }

    public void setForwardButtonSrc(Drawable forwardButtonSrc) {
        mForwardButtonSrc = forwardButtonSrc;
    }

    public int getSelectionColor() {
        return Color.parseColor("#FF0000");
    }

    public void setSelectionColor(int selectionColor) {
        mSelectionColor = selectionColor;
    }

    public int getTodayLabelColor() {
        return Color.parseColor(toolbarColorCode);
    }

    public void setTodayLabelColor(int todayLabelColor) {
        mTodayLabelColor = todayLabelColor;
    }

    public int getDialogButtonsColor() {
        return mDialogButtonsColor;
    }

    public void setDialogButtonsColor(int dialogButtonsColor) {
        mDialogButtonsColor = dialogButtonsColor;
    }

    public Calendar getMinimumDate() {
        return mMinimumDate;
    }

    public void setMinimumDate(Calendar minimumDate) {
        mMinimumDate = minimumDate;
    }

    public Calendar getMaximumDate() {
        return mMaximumDate;
    }

    public void setMaximumDate(Calendar maximumDate) {
        mMaximumDate = maximumDate;
    }

    public OnSelectionAbilityListener getOnSelectionAbilityListener() {
        return mOnSelectionAbilityListener;
    }

    public void setOnSelectionAbilityListener(OnSelectionAbilityListener onSelectionAbilityListener) {
        mOnSelectionAbilityListener = onSelectionAbilityListener;
    }

    public int getItemLayoutResource() {
        return mItemLayoutResource;
    }

    public void setItemLayoutResource(int itemLayoutResource) {
        mItemLayoutResource = itemLayoutResource;
    }

    public OnCalendarPageChangeListener getOnPreviousPageChangeListener() {
        return mOnPreviousPageChangeListener;
    }

    public void setOnPreviousPageChangeListener(OnCalendarPageChangeListener onPreviousButtonClickListener) {
        mOnPreviousPageChangeListener = onPreviousButtonClickListener;
    }

    public OnCalendarPageChangeListener getOnForwardPageChangeListener() {
        return mOnForwardPageChangeListener;
    }

    public void setOnForwardPageChangeListener(OnCalendarPageChangeListener onForwardButtonClickListener) {
        mOnForwardPageChangeListener = onForwardButtonClickListener;
    }

    public Calendar getFirstPageCalendarDate() {
        return mFirstPageCalendarDate;
    }

    public OnDayClickListener getOnDayClickListener() {
        return mOnDayClickListener;
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public List<EventDay> getEventDays() {
        return mEventDays;
    }

    public void setEventDays(List<EventDay> eventDays) {
        mEventDays = eventDays;

    }

    public List<Calendar> getDisabledDays() {
        return mDisabledDays;
    }

    public void setDisabledDays(List<Calendar> disabledDays) {
        mSelectedDays.removeAll(disabledDays);

        mDisabledDays = Stream.of(disabledDays)
                .map(calendar -> {
                    DateUtils.setMidnight(calendar);
                    return calendar;
                }).toList();
    }

    public List<Calendar> getHighlightedDays() {
        return mHighlightedDays;
    }

    public void setHighlightedDays(List<Calendar> highlightedDays) {
        mHighlightedDays = Stream.of(highlightedDays)
                .map(calendar -> {
                    DateUtils.setMidnight(calendar);
                    return calendar;
                }).toList();
    }

    public List<SelectedDay> getSelectedDays() {
        return mSelectedDays;
    }

    public void setSelectedDay(Calendar calendar) {
        setSelectedDay(new SelectedDay(calendar));
    }

    public void setSelectedDay(SelectedDay selectedDay) {
        mSelectedDays.clear();
        mSelectedDays.add(selectedDay);
    }

    public void setSelectedDays(List<Calendar> selectedDays) {
      /*  if (mCalendarType == OustCalendarView.SINGLE_DAY_PICKER) {
            throw new UnsupportedMethodsException(ErrorsMessages.ONE_DAY_PICKER_MULTIPLE_SELECTION);
        }

        if(mCalendarType == OustCalendarView.BETWEEN_PICKER && !DateUtils.isFullDatesRange(selectedDays)){
            throw new UnsupportedMethodsException(ErrorsMessages.RANGE_PICKER_NOT_RANGE);
        }*/

        mSelectedDays = Stream.of(selectedDays)
                .map(calendar -> {
                    DateUtils.setMidnight(calendar);
                    return new SelectedDay(calendar);
                }).filterNot(value -> mDisabledDays.contains(value.getCalendar()))
                .toList();
    }

    public int getDisabledDaysLabelsColor() {
        if (mDisabledDaysLabelsColor == 0) {
            return ContextCompat.getColor(mContext, R.color.nextMonthDayColor);
        }

        return mDisabledDaysLabelsColor;
    }

    public void setDisabledDaysLabelsColor(int disabledDaysLabelsColor) {
        mDisabledDaysLabelsColor = disabledDaysLabelsColor;
    }

    public int getHighlightedDaysLabelsColor() {
        if (mHighlightedDaysLabelsColor == 0) {
            return ContextCompat.getColor(mContext, R.color.nextMonthDayColor);
        }

        return mHighlightedDaysLabelsColor;
    }

    public void setHighlightedDaysLabelsColor(int highlightedDaysLabelsColor) {
        mHighlightedDaysLabelsColor = highlightedDaysLabelsColor;
    }

    public int getPagesColor() {
        return mPagesColor;
    }

    public void setPagesColor(int pagesColor) {
        mPagesColor = pagesColor;
    }

    public int getAbbreviationsBarColor() {
        return mAbbreviationsBarColor;
    }

    public void setAbbreviationsBarColor(int abbreviationsBarColor) {
        mAbbreviationsBarColor = abbreviationsBarColor;
    }

    public int getAbbreviationsLabelsColor() {
        return mAbbreviationsLabelsColor;
    }

    public void setAbbreviationsLabelsColor(int abbreviationsLabelsColor) {
        mAbbreviationsLabelsColor = abbreviationsLabelsColor;
    }

    public int getDaysLabelsColor() {
        String toolbarColorCode;
        if(OustPreferences.get("toolbarColorCode")!=null&&!OustPreferences.get("toolbarColorCode").isEmpty()){
            toolbarColorCode = OustPreferences.get("toolbarColorCode");
        }else{
            toolbarColorCode = "#01b5a2";
        }
      /*  if (mDaysLabelsColor == 0) {
            return ContextCompat.getColor(mContext, Color.parseColor(toolbarColorCode));
        }*/

        return Color.parseColor(toolbarColorCode);
    }

    public void setDaysLabelsColor(int daysLabelsColor) {
        mDaysLabelsColor = daysLabelsColor;
    }

    public int getSelectionLabelColor() {
        if (mSelectionLabelColor == 0) {
            return ContextCompat.getColor(mContext, android.R.color.white);
        }

        return mSelectionLabelColor;
    }

    public void setSelectionLabelColor(int selectionLabelColor) {
        mSelectionLabelColor = selectionLabelColor;
    }

    public int getAnotherMonthsDaysLabelsColor() {
        if (mAnotherMonthsDaysLabelsColor == 0) {
            return ContextCompat.getColor(mContext, R.color.nextMonthDayColor);
        }

        return mAnotherMonthsDaysLabelsColor;
    }

    public void setAnotherMonthsDaysLabelsColor(int anotherMonthsDaysLabelsColor) {
        mAnotherMonthsDaysLabelsColor = anotherMonthsDaysLabelsColor;
    }

    public int getHeaderVisibility() {
        return mHeaderVisibility;
    }

    public void setHeaderVisibility(int headerVisibility) {
        mHeaderVisibility = headerVisibility;
    }

    public int getNavigationVisibility() {
        return mNavigationVisibility;
    }

    public void setNavigationVisibility(int navigationVisibility) {
        mNavigationVisibility = navigationVisibility;
    }


    public int getAbbreviationsBarVisibility() {
        return mAbbreviationsBarVisibility;
    }

    public void setAbbreviationsBarVisibility(int abbreviationsBarVisbility) {
        mAbbreviationsBarVisibility = abbreviationsBarVisbility;
    }

    public int getMaximumDaysRange() {
        return mMaximumDaysRange;
    }

    public void setMaximumDaysRange(int maximumDaysRange) {
        mMaximumDaysRange = maximumDaysRange;
    }

    public int getTodayColor() {
        return mTodayColor;
    }

    public void setTodayColor(int todayColor) {
        mTodayColor = todayColor;
    }
}
