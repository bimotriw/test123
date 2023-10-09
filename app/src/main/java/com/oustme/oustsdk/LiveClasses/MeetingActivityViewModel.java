package com.oustme.oustsdk.LiveClasses;

import androidx.lifecycle.ViewModel;

public class MeetingActivityViewModel extends ViewModel {
    private final MeetingActivityRepository meetingActivityRepository;

    public MeetingActivityViewModel() {
        meetingActivityRepository = MeetingActivityRepository.getInstance();
    }

    public void initData(long liveClassId, long liveClassMeetingMapId, String userId, String eventType, String eventValue, String orgId) {
        meetingActivityRepository.saveMetingEvent(liveClassId, liveClassMeetingMapId, userId, eventType, eventValue, orgId);
    }
}
