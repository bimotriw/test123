package com.oustme.oustsdk.tools;


import com.oustme.oustsdk.response.assessment.AssessmentAnalyticsData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 07/02/17.
 */

public class AssessmentDataFilter {
    public List<AssessmentAnalyticsData> meetCriteria(List<AssessmentAnalyticsData> allassessment, String searchText) {
        List<AssessmentAnalyticsData> assessmentAnalyticsDataList = new ArrayList<>();
        for (AssessmentAnalyticsData frd : allassessment) {
            if((frd.getAssessmentName().toLowerCase().contains(searchText.toLowerCase()))||((frd.getAssessmentName()!=null)&&(frd.getAssessmentName().toLowerCase().contains(searchText.toLowerCase())))){
                assessmentAnalyticsDataList.add(frd);
            }
        }
        return assessmentAnalyticsDataList;
    }
}
