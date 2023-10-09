package com.oustme.oustsdk.tools.filters;

import com.oustme.oustsdk.firebase.course.CourseCollectionData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class CourseCollectionFilter {
    public List<CourseCollectionData> meetCriteria(List<CourseCollectionData> courseCollectionDataList, String searchText) {
        List<CourseCollectionData> collectionDatas = new ArrayList<>();
        for (CourseCollectionData courseCollectionData : courseCollectionDataList) {
            if(courseCollectionData.getName()!=null) {
                if (((courseCollectionData.getName() != null) &&(searchText!=null)&&(courseCollectionData.getName().toLowerCase().contains(searchText.toLowerCase())))) {
                    collectionDatas.add(courseCollectionData);
                }
            }
        }
        return collectionDatas;
    }
}
