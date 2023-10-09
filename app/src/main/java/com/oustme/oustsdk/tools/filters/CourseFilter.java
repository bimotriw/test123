package com.oustme.oustsdk.tools.filters;

import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.course.CourseDataClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class CourseFilter {
    public List<CommonLandingData> meetCriteria(List<CommonLandingData> allcourse, String searchText) {
        List<CommonLandingData> learningPathModuleList = new ArrayList<>();
        for (CommonLandingData frd : allcourse) {
            if(frd.getName()!=null) {
                if ((frd.getName().toLowerCase().contains(searchText.toLowerCase()))) {
                    learningPathModuleList.add(frd);
                }
            }
        }
        return learningPathModuleList;
    }
    public ArrayList<CommonLandingData> meetCriteria(ArrayList<CommonLandingData> allcourse, String searchText) {
        ArrayList<CommonLandingData> learningPathModuleList = new ArrayList<>();
        for (CommonLandingData frd : allcourse) {
            if(frd.getName()!=null) {
                if ((frd.getName().toLowerCase().contains(searchText.toLowerCase()))) {
                    learningPathModuleList.add(frd);
                }
            }
        }
        return learningPathModuleList;
    }
    public List<CommonLandingData> pendingCourseMeetCriteria(List<CommonLandingData> allcourse) {
        List<CommonLandingData> learningPathModuleList = new ArrayList<>();
        for (CommonLandingData frd : allcourse) {
            if ((frd.getCompletionPercentage()<100)) {
                learningPathModuleList.add(frd);
            }
        }
        return learningPathModuleList;
    }
    public List<CommonLandingData> completedCourseMeetCriteria(List<CommonLandingData> allcourse) {
        List<CommonLandingData> learningPathModuleList = new ArrayList<>();
        for (CommonLandingData frd : allcourse) {
            if ((frd.getCompletionPercentage()==100)) {
                learningPathModuleList.add(frd);
            }
        }
        return learningPathModuleList;
    }
}
