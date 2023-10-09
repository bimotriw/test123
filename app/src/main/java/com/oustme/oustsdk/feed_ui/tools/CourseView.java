package com.oustme.oustsdk.feed_ui.tools;

import com.oustme.oustsdk.commonmvp.mvp.view.BaseView;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;

import java.util.Map;

public interface CourseView extends BaseView {

    void setDownloadCourseIcon(DTOUserCourseData userCourseData);
    void setLpOcText(long mytotalOc, long totalOc);
    void downloadCourse(CourseDataClass courseDataClass);
    void startCourseDownload();
    void updateUserData(final int lpId, final CourseDataClass courseDataClass, Map<String, Object> learingMap);
    void updateLevelDownloadStatus(int noofLevels, final CourseDataClass courseDataClass, DTOUserCourseData dtoUserCourseData);
}
