package com.oustme.oustsdk.calendar_ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.annimon.stream.Stream;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.calendar_ui.custom.exceptions.ErrorsMessages;
import com.oustme.oustsdk.calendar_ui.custom.exceptions.OutOfDateRangeException;
import com.oustme.oustsdk.calendar_ui.custom.utils.AppearanceUtils;
import com.oustme.oustsdk.calendar_ui.custom.utils.SelectedDay;
import com.oustme.oustsdk.tools.OustPreferences;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.oustme.oustsdk.calendar_ui.custom.CalendarProperties.FIRST_VISIBLE_PAGE;

public class OustCalendarView extends LinearLayout {

    public static final int STANDARD = 0;
    public static final int SINGLE_DAY_PICKER = 1;
    public static final int MULTIPLE_DAYS_PICKER = 2;
    public static final int BETWEEN_PICKER = 3;

    private Context mContext;
    private OustCalendarPageAdapter mCalendarPageAdapter;

    public TextView month_name_text;
    public String monthName;
    ConstraintLayout calendar_nav_bar;
    private int mCurrentPage;
    private OustCalendarVP calendar_view_pager;

    private CalendarProperties mCalendarProperties;

    String toolbarColorCode;

    public OustCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
        initCalendar();
    }

    public OustCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
        initCalendar();
    }

    private void initControl(Context context, AttributeSet attrs) {
        mContext = context;
        mCalendarProperties = new CalendarProperties(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.oust_calendar_view, this);

        if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
            toolbarColorCode = OustPreferences.get("toolbarColorCode");
        } else {
            toolbarColorCode = "#01b5a2";
        }


        initUiElements();
        setAttributes(attrs);
    }


    private void setAttributes(AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            initCalendarProperties(typedArray);
            initAttributes();
        } finally {
            typedArray.recycle();
        }
    }

    private void initCalendarProperties(TypedArray typedArray) {
        int headerColor = typedArray.getColor(R.styleable.CalendarView_headerColor, 0);
        mCalendarProperties.setHeaderColor(headerColor);

        int headerLabelColor = typedArray.getColor(R.styleable.CalendarView_headerLabelColor, 0);
        mCalendarProperties.setHeaderLabelColor(headerLabelColor);

        int abbreviationsBarColor = typedArray.getColor(R.styleable.CalendarView_abbreviationsBarColor, 0);
        mCalendarProperties.setAbbreviationsBarColor(abbreviationsBarColor);

        int abbreviationsLabelsColor = typedArray.getColor(R.styleable.CalendarView_abbreviationsLabelsColor, 0);
        mCalendarProperties.setAbbreviationsLabelsColor(abbreviationsLabelsColor);

        int pagesColor = typedArray.getColor(R.styleable.CalendarView_pagesColor, 0);
        mCalendarProperties.setPagesColor(pagesColor);

        int daysLabelsColor = typedArray.getColor(R.styleable.CalendarView_daysLabelsColor, 0);
        mCalendarProperties.setDaysLabelsColor(daysLabelsColor);

        int anotherMonthsDaysLabelsColor = typedArray.getColor(R.styleable.CalendarView_anotherMonthsDaysLabelsColor, 0);
        mCalendarProperties.setAnotherMonthsDaysLabelsColor(anotherMonthsDaysLabelsColor);

        int todayLabelColor = typedArray.getColor(R.styleable.CalendarView_todayLabelColor, 0);
        String toolbarColorCode;
        if(OustPreferences.get("toolbarColorCode")!=null&&!OustPreferences.get("toolbarColorCode").isEmpty()){
            toolbarColorCode = OustPreferences.get("toolbarColorCode");
        }else{
            toolbarColorCode = "#01b5a2";
        }
        todayLabelColor = Color.parseColor(toolbarColorCode);
        mCalendarProperties.setTodayLabelColor(todayLabelColor);

        int selectionColor = typedArray.getColor(R.styleable.CalendarView_selectionColor, 0);
        selectionColor = Color.parseColor(toolbarColorCode);
        mCalendarProperties.setSelectionColor(selectionColor);

        int selectionLabelColor = typedArray.getColor(R.styleable.CalendarView_selectionLabelColor, 0);
        mCalendarProperties.setSelectionLabelColor(selectionLabelColor);

        int disabledDaysLabelsColor = typedArray.getColor(R.styleable.CalendarView_disabledDaysLabelsColor, 0);
        mCalendarProperties.setDisabledDaysLabelsColor(disabledDaysLabelsColor);

        int highlightedDaysLabelsColor = typedArray.getColor(R.styleable.CalendarView_highlightedDaysLabelsColor, 0);
        mCalendarProperties.setHighlightedDaysLabelsColor(highlightedDaysLabelsColor);

        int calendarType = typedArray.getInt(R.styleable.CalendarView_type, SINGLE_DAY_PICKER);
        mCalendarProperties.setCalendarType(calendarType);

        int maximumDaysRange = typedArray.getInt(R.styleable.CalendarView_maximumDaysRange, 0);
        mCalendarProperties.setMaximumDaysRange(maximumDaysRange);

        // Set picker mode !DEPRECATED!
        if (typedArray.getBoolean(R.styleable.CalendarView_datePicker, false)) {
            mCalendarProperties.setCalendarType(SINGLE_DAY_PICKER);
        }
/*
        boolean eventsEnabled = typedArray.getBoolean(R.styleable.CalendarView_eventsEnabled,
                mCalendarProperties.getCalendarType() == STANDARD);*/
        mCalendarProperties.setEventsEnabled(false);

        boolean swipeEnabled = typedArray.getBoolean(R.styleable.CalendarView_swipeEnabled, true);
        mCalendarProperties.setSwipeEnabled(swipeEnabled);

        Drawable previousButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_previousButtonSrc);
        mCalendarProperties.setPreviousButtonSrc(previousButtonSrc);

        Drawable forwardButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_forwardButtonSrc);
        mCalendarProperties.setForwardButtonSrc(forwardButtonSrc);
    }

    private void initAttributes() {
        AppearanceUtils.setHeaderColor(getRootView(), mCalendarProperties.getHeaderColor());

        AppearanceUtils.setHeaderVisibility(getRootView(), mCalendarProperties.getHeaderVisibility());

        AppearanceUtils.setAbbreviationsBarVisibility(getRootView(), mCalendarProperties.getAbbreviationsBarVisibility());

        AppearanceUtils.setNavigationVisibility(getRootView(), mCalendarProperties.getNavigationVisibility());

        AppearanceUtils.setHeaderLabelColor(getRootView(), mCalendarProperties.getHeaderLabelColor());

        AppearanceUtils.setAbbreviationsBarColor(getRootView(), mCalendarProperties.getAbbreviationsBarColor());

        AppearanceUtils.setAbbreviationsLabels(getRootView(), mCalendarProperties.getAbbreviationsLabelsColor(),
                mCalendarProperties.getFirstPageCalendarDate().getFirstDayOfWeek());

        AppearanceUtils.setPagesColor(getRootView(), mCalendarProperties.getPagesColor());

        AppearanceUtils.setPreviousButtonImage(getRootView(), mCalendarProperties.getPreviousButtonSrc());

        AppearanceUtils.setForwardButtonImage(getRootView(), mCalendarProperties.getForwardButtonSrc());

        calendar_view_pager.setSwipeEnabled(mCalendarProperties.getSwipeEnabled());

        // Sets layout for date picker or normal calendar
        setCalendarRowLayout();
    }

    public void setHeaderColor(@ColorRes int color) {
        mCalendarProperties.setHeaderColor(color);
        AppearanceUtils.setHeaderColor(getRootView(), mCalendarProperties.getHeaderColor());
    }

    public void setHeaderVisibility(int visibility) {
        mCalendarProperties.setHeaderVisibility(visibility);
        AppearanceUtils.setHeaderVisibility(getRootView(), mCalendarProperties.getHeaderVisibility());
    }

    public void setAbbreviationsBarVisibility(int visibility) {
        mCalendarProperties.setAbbreviationsBarVisibility(visibility);
        AppearanceUtils.setAbbreviationsBarVisibility(getRootView(), mCalendarProperties.getAbbreviationsBarVisibility());
    }

    public void setHeaderLabelColor(@ColorRes int color) {
        mCalendarProperties.setHeaderLabelColor(color);
        AppearanceUtils.setHeaderLabelColor(getRootView(), mCalendarProperties.getHeaderLabelColor());
    }

    public void setPreviousButtonImage(Drawable drawable) {
        mCalendarProperties.setPreviousButtonSrc(drawable);
        AppearanceUtils.setPreviousButtonImage(getRootView(), mCalendarProperties.getPreviousButtonSrc());
    }

    public void setForwardButtonImage(Drawable drawable) {
        mCalendarProperties.setForwardButtonSrc(drawable);
        AppearanceUtils.setForwardButtonImage(getRootView(), mCalendarProperties.getForwardButtonSrc());
    }

    private void setCalendarRowLayout() {
//        if (mCalendarProperties.getEventsEnabled()) {
//            mCalendarProperties.setItemLayoutResource(R.layout.calendar_view_day);
//        } else {
//
//        }
        mCalendarProperties.setItemLayoutResource(R.layout.calendar_day_picker_layout);
    }

    private void initUiElements() {

       // calendar_nav_bar = findViewById(R.id.calendar_nav_bar);
       // calendar_nav_bar.setVisibility(GONE);
        ImageView next_month = findViewById(R.id.next_month);
        next_month.setOnClickListener(onNextClickListener);

        ImageView previous_month = findViewById(R.id.previous_month);
        previous_month.setOnClickListener(onPreviousClickListener);

        month_name_text = findViewById(R.id.month_name_text);
        month_name_text.setTextColor(Color.parseColor(toolbarColorCode));

        calendar_view_pager = findViewById(R.id.calendar_view_pager);
    }

    private void initCalendar() {
        mCalendarPageAdapter = new OustCalendarPageAdapter(mContext, mCalendarProperties);

        calendar_view_pager.setAdapter(mCalendarPageAdapter);
        calendar_view_pager.addOnPageChangeListener(onPageChangeListener);

        setUpCalendarPosition(Calendar.getInstance());
    }

    private void setUpCalendarPosition(Calendar calendar) {
        DateUtils.setMidnight(calendar);

        if (mCalendarProperties.getCalendarType() == OustCalendarView.SINGLE_DAY_PICKER) {
            mCalendarProperties.setSelectedDay(calendar);
        }

        mCalendarProperties.getFirstPageCalendarDate().setTime(calendar.getTime());
        mCalendarProperties.getFirstPageCalendarDate().add(Calendar.MONTH, -FIRST_VISIBLE_PAGE);

        calendar_view_pager.setCurrentItem(FIRST_VISIBLE_PAGE);
    }

    public void setOnPreviousPageChangeListener(OnCalendarPageChangeListener listener) {
        mCalendarProperties.setOnPreviousPageChangeListener(listener);
    }

    public void setOnForwardPageChangeListener(OnCalendarPageChangeListener listener) {
        mCalendarProperties.setOnForwardPageChangeListener(listener);
    }

    public final OnClickListener onNextClickListener =
            v -> calendar_view_pager.setCurrentItem(calendar_view_pager.getCurrentItem() + 1);

    public final OnClickListener onPreviousClickListener =
            v -> calendar_view_pager.setCurrentItem(calendar_view_pager.getCurrentItem() - 1);

    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }


        @Override
        public void onPageSelected(int position) {
            Calendar calendar = (Calendar) mCalendarProperties.getFirstPageCalendarDate().clone();
            calendar.add(Calendar.MONTH, position);

            if (!isScrollingLimited(calendar, position)) {
                setHeaderName(calendar, position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private boolean isScrollingLimited(Calendar calendar, int position) {
        if (DateUtils.isMonthBefore(mCalendarProperties.getMinimumDate(), calendar)) {
            calendar_view_pager.setCurrentItem(position + 1);
            return true;
        }

        if (DateUtils.isMonthAfter(mCalendarProperties.getMaximumDate(), calendar)) {
            calendar_view_pager.setCurrentItem(position - 1);
            return true;
        }

        return false;
    }

    private void setHeaderName(Calendar calendar, int position) {
        //monthName =
        month_name_text.setText(DateUtils.getMonthAndYearDate(mContext, calendar));
        callOnPageChangeListeners(position);
    }

    // This method calls page change listeners after swipe calendar or click arrow buttons
    private void callOnPageChangeListeners(int position) {
        if (position > mCurrentPage && mCalendarProperties.getOnForwardPageChangeListener() != null) {
            mCalendarProperties.getOnForwardPageChangeListener().onChange();
        }

        if (position < mCurrentPage && mCalendarProperties.getOnPreviousPageChangeListener() != null) {
            mCalendarProperties.getOnPreviousPageChangeListener().onChange();
        }

        mCurrentPage = position;
    }


    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mCalendarProperties.setOnDayClickListener(onDayClickListener);
    }


    public void setDate(Calendar date) throws OutOfDateRangeException {
        if (mCalendarProperties.getMinimumDate() != null && date.before(mCalendarProperties.getMinimumDate())) {
            throw new OutOfDateRangeException(ErrorsMessages.OUT_OF_RANGE_MIN);
        }

        if (mCalendarProperties.getMaximumDate() != null && date.after(mCalendarProperties.getMaximumDate())) {
            throw new OutOfDateRangeException(ErrorsMessages.OUT_OF_RANGE_MAX);
        }

        setUpCalendarPosition(date);

        month_name_text.setText(DateUtils.getMonthAndYearDate(mContext, date));
        mCalendarPageAdapter.notifyDataSetChanged();
    }

    public void setDate(Date currentDate) throws OutOfDateRangeException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        setDate(calendar);
    }




    public void setEvents(List<EventDay> eventDays) {
        mCalendarProperties.setEventsEnabled(true);
        if (mCalendarProperties.getEventsEnabled()) {
            mCalendarProperties.setEventDays(eventDays);
            mCalendarPageAdapter.notifyDataSetChanged();
        }
    }


    public List<Calendar> getSelectedDates() {
        return Stream.of(mCalendarPageAdapter.getSelectedDays())
                .map(SelectedDay::getCalendar)
                .sortBy(calendar -> calendar).toList();
    }

    public void setSelectedDates(List<Calendar> selectedDates) {
        mCalendarProperties.setSelectedDays(selectedDates);
        mCalendarPageAdapter.notifyDataSetChanged();
    }


    @Deprecated
    public Calendar getSelectedDate() {
        return getFirstSelectedDate();
    }


    public Calendar getFirstSelectedDate() {
        return Stream.of(mCalendarPageAdapter.getSelectedDays())
                .map(SelectedDay::getCalendar).findFirst().get();
    }


    public Calendar getCurrentPageDate() {
        Calendar calendar = (Calendar) mCalendarProperties.getFirstPageCalendarDate().clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, calendar_view_pager.getCurrentItem());
        return calendar;
    }


    public void setMinimumDate(Calendar calendar) {
        mCalendarProperties.setMinimumDate(calendar);
    }


    public void setMaximumDate(Calendar calendar) {
        mCalendarProperties.setMaximumDate(calendar);
    }


    public void showCurrentMonthPage() {
        calendar_view_pager.setCurrentItem(calendar_view_pager.getCurrentItem()
                - DateUtils.getMonthsBetweenDates(DateUtils.getCalendar(), getCurrentPageDate()), true);
    }

    public void setDisabledDays(List<Calendar> disabledDays) {
        mCalendarProperties.setDisabledDays(disabledDays);
    }

    public void setHighlightedDays(List<Calendar> highlightedDays) {
        mCalendarProperties.setHighlightedDays(highlightedDays);
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        mCalendarProperties.setSwipeEnabled(swipeEnabled);
        calendar_view_pager.setSwipeEnabled(mCalendarProperties.getSwipeEnabled());
    }
}
