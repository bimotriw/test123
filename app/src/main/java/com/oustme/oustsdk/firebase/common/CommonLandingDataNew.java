package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.course.MultilingualCourse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 11/10/17.
 */
@Keep
public class CommonLandingDataNew implements Serializable{
    private String name;
    private String addedOn;
    private long oc;
    private long time;
    private String banner;
    private long completionPercentage;
    private boolean enrolled;
    private String type;
    private boolean archived;
    private long enrollCount;
    private boolean certificate;
    private boolean locked;
    private long userOc;
    private boolean isListenerSet;
    private String courseDeadline;
    private String notificationTitle;
    private String enrollNotificationContent;
    private String completeNotificationContent;
    private long reminderNotificationInterval;
    private String icon;
    private String moduleType;
    private String id;
    private String description;
    private long rating;
    private String landing_data_type;
    private String courseType;
    private List<MultilingualCourse> multilingualCourseListList;
    private long cplId;
    private String courseTags;
    private long passPercentage;
    private String mCompletionDate;
    private long catalogueId;

    private long noOfAttemptAllowedToPass=0;

    public long getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(long passPercentage) {
        this.passPercentage = passPercentage;
    }

    public String getCourseTags() {
        return courseTags;
    }

    public void setCourseTags(String courseTags) {
        this.courseTags = courseTags;
    }

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public boolean isLevelLock() {
        return levelLock;
    }

    public void setLevelLock(boolean levelLock) {
        this.levelLock = levelLock;
    }

    private boolean levelLock;

    public String getLanding_data_type() {
        return landing_data_type;
    }

    public void setLanding_data_type(String landing_data_type) {
        this.landing_data_type = landing_data_type;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserOc() {
        return userOc;
    }

    public void setUserOc(long userOc) {
        this.userOc = userOc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public long getOc() {
        return oc;
    }

    public void setOc(long oc) {
        this.oc = oc;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public long getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(long completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public long getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(long enrollCount) {
        this.enrollCount = enrollCount;
    }

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }


    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isListenerSet() {
        return isListenerSet;
    }

    public void setListenerSet(boolean listenerSet) {
        isListenerSet = listenerSet;
    }

    public String getCourseDeadline() {
        return courseDeadline;
    }

    public void setCourseDeadline(String courseDeadline) {
        this.courseDeadline = courseDeadline;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getEnrollNotificationContent() {
        return enrollNotificationContent;
    }

    public void setEnrollNotificationContent(String enrollNotificationContent) {
        this.enrollNotificationContent = enrollNotificationContent;
    }

    public String getCompleteNotificationContent() {
        return completeNotificationContent;
    }

    public void setCompleteNotificationContent(String completeNotificationContent) {
        this.completeNotificationContent = completeNotificationContent;
    }

    public long getReminderNotificationInterval() {
        return reminderNotificationInterval;
    }

    public void setReminderNotificationInterval(long reminderNotificationInterval) {
        this.reminderNotificationInterval = reminderNotificationInterval;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public List<MultilingualCourse> getMultilingualCourseListList() {
        return multilingualCourseListList;
    }

    public void setMultilingualCourseListList(List<MultilingualCourse> multilingualCourseListList) {
        this.multilingualCourseListList = multilingualCourseListList;
    }

    public String getmCompletionDate() {
        return mCompletionDate;
    }

    public void setmCompletionDate(String mCompletionDate) {
        this.mCompletionDate = mCompletionDate;
    }

    public long getNoOfAttemptAllowedToPass() {
        return noOfAttemptAllowedToPass;
    }

    public void setNoOfAttemptAllowedToPass(long noOfAttemptAllowedToPass) {
        this.noOfAttemptAllowedToPass = noOfAttemptAllowedToPass;
    }

}
