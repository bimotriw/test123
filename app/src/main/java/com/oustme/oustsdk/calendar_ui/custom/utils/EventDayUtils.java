package com.oustme.oustsdk.calendar_ui.custom.utils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.oustme.oustsdk.calendar_ui.custom.CalendarProperties;
import com.oustme.oustsdk.calendar_ui.custom.EventDay;

import java.util.Calendar;

public class EventDayUtils {


    public static boolean isEventDayWithLabelColor(Calendar day, CalendarProperties calendarProperties) {
        if (calendarProperties.getEventDays() != null || calendarProperties.getEventsEnabled()) {
            return Stream.of(calendarProperties.getEventDays()).anyMatch(eventDate ->
                    eventDate.getCalendar().equals(day) && eventDate.getLabelColor() != 0);
        }

        return false;
    }

    public static Optional<EventDay> getEventDayWithLabelColor(Calendar day, CalendarProperties calendarProperties) {
        return Stream.of(calendarProperties.getEventDays())
                .filter(eventDate -> eventDate.getCalendar().equals(day) && eventDate.getLabelColor() != 0)
                .findFirst();
    }
}
