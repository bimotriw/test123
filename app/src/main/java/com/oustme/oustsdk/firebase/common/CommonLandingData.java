package com.oustme.oustsdk.firebase.common;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.oustme.oustsdk.calendar_ui.model.MeetingCalendar;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

/**
 * Created by admin on 11/10/17.
 */

@Keep
public class CommonLandingData implements Parcelable {
    private int commonId;
    private long moduleId;
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
    private long numQuestions;
    private boolean certificate;
    private boolean locked;
    private long userOc;
    private boolean isListenerSet;
    private String courseDeadline;
    private String completionDeadline;
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
    private int courseLevelsSize;
    private long passPercentage;
    private long totalOc;
    private String mCompletionDate;
    private String mode;
    private long categoryId;
    private boolean courseAssociated;
    private long mappedCourseId;
    private long startTime;
    private long endTime;
    private String timeZone;
    private boolean hideDate;
    private MeetingCalendar meetingCalendar;
    private boolean showNudgeMessage;
    // Fastest fingers fist
    private FastestFingerContestData fastestFingerContestData;
    private boolean recurring;
    private String enrolledDateTime;
    private Boolean activeChildCpl;
    private Integer parentCplId;

    private String distributedId;

    protected CommonLandingData(Parcel in) {
        commonId = in.readInt();
        name = in.readString();
        addedOn = in.readString();
        oc = in.readLong();
        time = in.readLong();
        banner = in.readString();
        completionPercentage = in.readLong();
        enrolled = in.readByte() != 0;
        type = in.readString();
        archived = in.readByte() != 0;
        enrollCount = in.readLong();
        certificate = in.readByte() != 0;
        locked = in.readByte() != 0;
        userOc = in.readLong();
        isListenerSet = in.readByte() != 0;
        courseDeadline = in.readString();
        notificationTitle = in.readString();
        enrollNotificationContent = in.readString();
        completeNotificationContent = in.readString();
        reminderNotificationInterval = in.readLong();
        icon = in.readString();
        moduleType = in.readString();
        id = in.readString();
        description = in.readString();
        rating = in.readLong();
        landing_data_type = in.readString();
        courseType = in.readString();
        cplId = in.readLong();
        courseTags = in.readString();
        courseLevelsSize = in.readInt();
        passPercentage = in.readLong();
        totalOc = in.readLong();
        mCompletionDate = in.readString();
        courseAssociated = in.readByte() != 0;
        mappedCourseId = in.readLong();
        viewStatus = in.readString();
        catalogId = in.readLong();
        catalogCategoryId = in.readLong();
        catalogContentId = in.readLong();
        noOfAttemptAllowedToPass = in.readLong();
        levelLock = in.readByte() != 0;
    }

    public CommonLandingData() {
    }

    public static final Creator<CommonLandingData> CREATOR = new Creator<CommonLandingData>() {
        @Override
        public CommonLandingData createFromParcel(Parcel in) {
            return new CommonLandingData(in);
        }

        @Override
        public CommonLandingData[] newArray(int size) {
            return new CommonLandingData[size];
        }
    };

    public long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(long totalOc) {
        this.totalOc = totalOc;
    }

    private String viewStatus = "";
    private long catalogId;
    private long catalogCategoryId;
    private long catalogContentId;

    private long noOfAttemptAllowedToPass = 0;

    private long defaultPastDeadlineCoinsPenaltyPercentage;
    private boolean showPastDeadlineModulesOnLandingPage;

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

    public int getCourseLevelsSize() {
        return courseLevelsSize;
    }

    public void setCourseLevelsSize(int courseLevelsSize) {
        this.courseLevelsSize = courseLevelsSize;
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

    public long getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(long numQuestions) {
        this.numQuestions = numQuestions;
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

    public String getCompletionDeadline() {
        return completionDeadline;
    }

    public void setCompletionDeadline(String completionDeadline) {
        this.completionDeadline = completionDeadline;
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

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isCourseAssociated() {
        return courseAssociated;
    }

    public void setCourseAssociated(boolean courseAssociated) {
        this.courseAssociated = courseAssociated;
    }

    public long getMappedCourseId() {
        return mappedCourseId;
    }

    public void setMappedCourseId(long mappedCourseId) {
        this.mappedCourseId = mappedCourseId;
    }

    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
    }

    public long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(long catalogId) {
        this.catalogId = catalogId;
    }

    public long getCatalogCategoryId() {
        return catalogCategoryId;
    }

    public void setCatalogCategoryId(long catalogCategoryId) {
        this.catalogCategoryId = catalogCategoryId;
    }

    public long getCatalogContentId() {
        return catalogContentId;
    }

    public void setCatalogContentId(long catalogContentId) {
        this.catalogContentId = catalogContentId;
    }

    public int getCommonId() {
        return commonId;
    }

    public void setCommonId(int commonId) {
        this.commonId = commonId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isHideDate() {
        return hideDate;
    }

    public void setHideDate(boolean hideDate) {
        this.hideDate = hideDate;
    }

    public MeetingCalendar getMeetingCalendar() {
        return meetingCalendar;
    }

    public void setMeetingCalendar(MeetingCalendar meetingCalendar) {
        this.meetingCalendar = meetingCalendar;
    }

    public static Comparator<CommonLandingData> sortByDate = (s1, s2) -> {
        if (s2.getAddedOn() == null) {
            return -1;
        }
        if (s1.getAddedOn() == null) {
            return 1;
        }
        if (s1.getAddedOn().equals(s2.getAddedOn())) {
            return 0;
        }
        return s1.getAddedOn().compareTo(s2.getAddedOn());
    };

    public static Comparator<String> descendingSortForKey = (s1, s2) -> {
        if (s2 == null) {
            return -1;
        }
        if (s1 == null) {
            return 1;
        }
        if (s1.equals(s2)) {
            return 0;
        }
        return s1.compareTo(s2);//descending order
    };

    public static Comparator<String> ascendingSortForKey = (s1, s2) -> {
        if (s2 == null) {
            return -1;
        }
        if (s1 == null) {
            return 1;
        }
        if (s2.equals(s1)) {
            return 0;
        }
        return s2.compareTo(s1);//ascending order
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(commonId);
        dest.writeString(name);
        dest.writeString(addedOn);
        dest.writeLong(oc);
        dest.writeLong(time);
        dest.writeString(banner);
        dest.writeLong(completionPercentage);
        dest.writeByte((byte) (enrolled ? 1 : 0));
        dest.writeString(type);
        dest.writeByte((byte) (archived ? 1 : 0));
        dest.writeLong(enrollCount);
        dest.writeByte((byte) (certificate ? 1 : 0));
        dest.writeByte((byte) (locked ? 1 : 0));
        dest.writeLong(userOc);
        dest.writeByte((byte) (isListenerSet ? 1 : 0));
        dest.writeString(courseDeadline);
        dest.writeString(notificationTitle);
        dest.writeString(enrollNotificationContent);
        dest.writeString(completeNotificationContent);
        dest.writeLong(reminderNotificationInterval);
        dest.writeString(icon);
        dest.writeString(moduleType);
        dest.writeString(id);
        dest.writeString(description);
        dest.writeLong(rating);
        dest.writeString(landing_data_type);
        dest.writeString(courseType);
        dest.writeLong(cplId);
        dest.writeString(courseTags);
        dest.writeInt(courseLevelsSize);
        dest.writeLong(passPercentage);
        dest.writeLong(totalOc);
        dest.writeString(mCompletionDate);
        dest.writeByte((byte) (courseAssociated ? 1 : 0));
        dest.writeLong(mappedCourseId);
        dest.writeString(viewStatus);
        dest.writeLong(catalogId);
        dest.writeLong(catalogCategoryId);
        dest.writeLong(catalogContentId);
        dest.writeLong(noOfAttemptAllowedToPass);
        dest.writeByte((byte) (levelLock ? 1 : 0));
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public long getDefaultPastDeadlineCoinsPenaltyPercentage() {
        return defaultPastDeadlineCoinsPenaltyPercentage;
    }

    public void setDefaultPastDeadlineCoinsPenaltyPercentage(long defaultPastDeadlineCoinsPenaltyPercentage) {
        this.defaultPastDeadlineCoinsPenaltyPercentage = defaultPastDeadlineCoinsPenaltyPercentage;
    }

    public boolean isShowPastDeadlineModulesOnLandingPage() {
        return showPastDeadlineModulesOnLandingPage;
    }

    public void setShowPastDeadlineModulesOnLandingPage(boolean showPastDeadlineModulesOnLandingPage) {
        this.showPastDeadlineModulesOnLandingPage = showPastDeadlineModulesOnLandingPage;
    }

    public boolean isShowNudgeMessage() {
        return showNudgeMessage;
    }

    public void setShowNudgeMessage(boolean showNudgeMessage) {
        this.showNudgeMessage = showNudgeMessage;
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

    public FastestFingerContestData getFastestFingerContestData() {
        return fastestFingerContestData;
    }

    public void setFastestFingerContestData(FastestFingerContestData fastestFingerContestData) {
        this.fastestFingerContestData = fastestFingerContestData;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getEnrolledDateTime() {
        return enrolledDateTime;
    }

    public void setEnrolledDateTime(String enrolledDateTime) {
        this.enrolledDateTime = enrolledDateTime;
    }

    public String getDistributedId() {
        return distributedId;
    }

    public void setDistributedId(String distributedId) {
        this.distributedId = distributedId;
    }

    public Boolean getActiveChildCpl() {
        return activeChildCpl;
    }

    public void setActiveChildCpl(Boolean activeChildCpl) {
        this.activeChildCpl = activeChildCpl;
    }

    public Integer getParentCplId() {
        return parentCplId;
    }

    public void setParentCplId(Integer parentCplId) {
        this.parentCplId = parentCplId;
    }
}
