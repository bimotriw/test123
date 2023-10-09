package com.oustme.oustsdk.calendar_ui.model;

import com.oustme.oustsdk.model.response.diary.DiaryDetailsModel;

import java.util.Comparator;

public class CalendarBaseData {

    DiaryDetailsModel diaryDetailsModel;
    CalendarCommonData calendarCommonData;
    MeetingCalendar meetingCalendar;
    boolean isEventData;
    long time;

    public DiaryDetailsModel getDiaryDetailsModel() {
        return diaryDetailsModel;
    }

    public void setDiaryDetailsModel(DiaryDetailsModel diaryDetailsModel) {
        this.diaryDetailsModel = diaryDetailsModel;
    }

    public CalendarCommonData getCalendarCommonData() {
        return calendarCommonData;
    }

    public void setCalendarCommonData(CalendarCommonData calendarCommonData) {
        this.calendarCommonData = calendarCommonData;
    }

    public MeetingCalendar getMeetingCalendar() {
        return meetingCalendar;
    }

    public void setMeetingCalendar(MeetingCalendar meetingCalendar) {
        this.meetingCalendar = meetingCalendar;
    }

    public boolean isEventData() {
        return isEventData;
    }

    public void setEventData(boolean eventData) {
        isEventData = eventData;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static Comparator<CalendarBaseData> sortByDate = (s1, s2) -> {
        if (s1.getTime() > s2.getTime())
            return 1;
        else if (s1.getTime() < s2.getTime())
            return -1;
        else if (s1.getTime() == s2.getTime())
            return 0;
        else {
            if (s1.getTime() > s2.getTime())
                return 1;
            else
                return -1;
        }
    };

}
