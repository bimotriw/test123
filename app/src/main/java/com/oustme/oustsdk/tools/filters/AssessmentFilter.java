package com.oustme.oustsdk.tools.filters;

import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.common.CommonLandingData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class AssessmentFilter {
    public List<CommonLandingData> meetCriteria(List<CommonLandingData> allassessment, String searchText) {
        List<CommonLandingData> assessmentFirebaseClassList = new ArrayList<>();
        for (CommonLandingData frd : allassessment) {
            if(frd.getName()!=null) {
                if (((frd.getName() != null) && (frd.getName().toLowerCase().contains(searchText.toLowerCase()))) || ((frd.getDescription() != null) && (frd.getDescription().toLowerCase().contains(searchText.toLowerCase())))) {
                    assessmentFirebaseClassList.add(frd);
                }
            }
        }
        return assessmentFirebaseClassList;
    }
}
