package com.oustme.oustsdk.calendar_ui.model;

import com.oustme.oustsdk.skill_ui.model.UserSkillData;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MeetingCalendar implements Serializable {

    String addedOn;
    boolean attending;
    String bannerImg;
    String thumbnailImage;
    String classTitle;
    long elementId;
    String elementIdStr;
    boolean enrolled;
    long meetingId;
    String parentNodeName;
    String timeZone;
    String trainerName;
    long availableSlots;
    String classRoomAddress;
    String createdBy;
    String createdDateTime;
    boolean deleted;
    String description;
    String eventType;
    boolean hasDistributiveContent;
    ArrayList<MediaAttachmentData> mediaAttachmentData;
    ArrayList<String> trainerEmail;
    long meetingEndTime;
    String meetingLink;
    String meetingLocationLink;
    String meetingPurpose;
    String meetingReminderText;
    long meetingStartTime;
    String profileLink;
    long reminderInterval;
    long totalEnrolled;
    long totalEnrolledCount;
    long totalUsersAttending;
    long totalUsersOnWait;
    String updateDateTime;
    long updateTimeInMillis;
    boolean waitList;
    String attendStatus;
    String trainerNote;
    boolean eventMandatory;
    long liveClassMeetingMapId;
    String meetingType;

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public boolean isAttending() {
        return attending;
    }

    public void setAttending(boolean attending) {
        this.attending = attending;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public long getElementId() {
        return elementId;
    }

    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    public String getElementIdStr() {
        return elementIdStr;
    }

    public void setElementIdStr(String elementIdStr) {
        this.elementIdStr = elementIdStr;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(long meetingId) {
        this.meetingId = meetingId;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    public void setParentNodeName(String parentNodeName) {
        this.parentNodeName = parentNodeName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public long getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(long availableSlots) {
        this.availableSlots = availableSlots;
    }

    public String getClassRoomAddress() {
        return classRoomAddress;
    }

    public void setClassRoomAddress(String classRoomAddress) {
        this.classRoomAddress = classRoomAddress;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public boolean isHasDistributiveContent() {
        return hasDistributiveContent;
    }

    public void setHasDistributiveContent(boolean hasDistributiveContent) {
        this.hasDistributiveContent = hasDistributiveContent;
    }

    public ArrayList<MediaAttachmentData> getMediaAttachmentData() {
        return mediaAttachmentData;
    }

    public void setMediaAttachmentData(ArrayList<MediaAttachmentData> mediaAttachmentData) {
        this.mediaAttachmentData = mediaAttachmentData;
    }

    public long getMeetingEndTime() {
        return meetingEndTime;
    }

    public void setMeetingEndTime(long meetingEndTime) {
        this.meetingEndTime = meetingEndTime;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getMeetingLocationLink() {
        return meetingLocationLink;
    }

    public void setMeetingLocationLink(String meetingLocationLink) {
        this.meetingLocationLink = meetingLocationLink;
    }

    public String getMeetingPurpose() {
        return meetingPurpose;
    }

    public void setMeetingPurpose(String meetingPurpose) {
        this.meetingPurpose = meetingPurpose;
    }

    public String getMeetingReminderText() {
        return meetingReminderText;
    }

    public void setMeetingReminderText(String meetingReminderText) {
        this.meetingReminderText = meetingReminderText;
    }

    public long getMeetingStartTime() {
        return meetingStartTime;
    }

    public void setMeetingStartTime(long meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public long getReminderInterval() {
        return reminderInterval;
    }

    public void setReminderInterval(long reminderInterval) {
        this.reminderInterval = reminderInterval;
    }

    public long getTotalEnrolled() {
        return totalEnrolled;
    }

    public void setTotalEnrolled(long totalEnrolled) {
        this.totalEnrolled = totalEnrolled;
    }

    public long getTotalEnrolledCount() {
        return totalEnrolledCount;
    }

    public void setTotalEnrolledCount(long totalEnrolledCount) {
        this.totalEnrolledCount = totalEnrolledCount;
    }

    public long getTotalUsersAttending() {
        return totalUsersAttending;
    }

    public void setTotalUsersAttending(long totalUsersAttending) {
        this.totalUsersAttending = totalUsersAttending;
    }

    public long getTotalUsersOnWait() {
        return totalUsersOnWait;
    }

    public void setTotalUsersOnWait(long totalUsersOnWait) {
        this.totalUsersOnWait = totalUsersOnWait;
    }

    public String getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public long getUpdateTimeInMillis() {
        return updateTimeInMillis;
    }

    public void setUpdateTimeInMillis(long updateTimeInMillis) {
        this.updateTimeInMillis = updateTimeInMillis;
    }

    public boolean isWaitList() {
        return waitList;
    }

    public void setWaitList(boolean waitList) {
        this.waitList = waitList;
    }

    public String getAttendStatus() {
        return attendStatus;
    }

    public void setAttendStatus(String attendStatus) {
        this.attendStatus = attendStatus;
    }

    public boolean isEventMandatory() {
        return eventMandatory;
    }

    public void setEventMandatory(boolean eventMandatory) {
        this.eventMandatory = eventMandatory;
    }

    @SuppressWarnings("unchecked")
    public static <T> T mergeObjects(T first, T second) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = first.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object returnValue = clazz.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(first);
            Object value2 = field.get(second);
            Object value = (value1 != null) ? value1 : value2;
            field.set(returnValue, value);
        }
        return (T) returnValue;
    }

    public long getLiveClassMeetingMapId() {
        return liveClassMeetingMapId;
    }

    public void setLiveClassMeetingMapId(long liveClassMeetingMapId) {
        this.liveClassMeetingMapId = liveClassMeetingMapId;
    }

    public ArrayList<String> getTrainerEmail() {
        return trainerEmail;
    }

    public void setTrainerEmail(ArrayList<String> trainerEmail) {
        this.trainerEmail = trainerEmail;
    }

    public String getTrainerNote() {
        return trainerNote;
    }

    public void setTrainerNote(String trainerNote) {
        this.trainerNote = trainerNote;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }
}
