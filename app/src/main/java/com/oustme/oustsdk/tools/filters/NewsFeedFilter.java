package com.oustme.oustsdk.tools.filters;

import android.util.Log;

import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class NewsFeedFilter {
    private static final String TAG = "NewsFeedFilter";
    public List<DTONewFeed> meetCriteria(List<DTONewFeed> allassessment, String searchText) {
        List<DTONewFeed> DTONewFeedList = new ArrayList<>();
        try {
            for (DTONewFeed frd : allassessment) {
                if (((frd.getHeader() != null) && (searchText != null) && frd.getHeader().toLowerCase().contains(searchText.toLowerCase())) || ((frd.getContent() != null) && (frd.getContent().toLowerCase().contains(searchText.toLowerCase())))) {
                    DTONewFeedList.add(frd);
                }
                if((!DTONewFeedList.contains(frd)) && frd.getCourseCardClass()!=null && searchText!=null){
                    DTOCourseCard courseCardClass=frd.getCourseCardClass();
                    if(courseCardClass.getCardTitle().toLowerCase().contains(searchText.toLowerCase()) || courseCardClass.getContent().toLowerCase().contains(searchText.toLowerCase())){
                        DTONewFeedList.add(frd);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return DTONewFeedList;
    }


    public ArrayList<DTONewFeed> meetCriteria(ArrayList<DTONewFeed> allassessment, String searchText) {
        ArrayList<DTONewFeed> DTONewFeedList = new ArrayList<>();
        try {
            for (DTONewFeed frd : allassessment) {
                if (((frd.getHeader() != null) && (searchText != null) && frd.getHeader().toLowerCase().contains(searchText.toLowerCase())) || ((frd.getContent() != null) && (frd.getContent().toLowerCase().contains(searchText.toLowerCase())))) {
                    DTONewFeedList.add(frd);
                }
                if((!DTONewFeedList.contains(frd)) && frd.getCourseCardClass()!=null && searchText!=null){
                    DTOCourseCard courseCardClass=frd.getCourseCardClass();
                    if(courseCardClass.getCardTitle().toLowerCase().contains(searchText.toLowerCase()) || courseCardClass.getContent().toLowerCase().contains(searchText.toLowerCase())){
                        DTONewFeedList.add(frd);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return DTONewFeedList;
    }

    public ArrayList<DTONewFeed> meetFilterCriteria(ArrayList<DTONewFeed> allfeeds, int type, String tag) {
        String filterStr = "";
        if (type == 10) {
            filterStr = "COURSE_UPDATE";
        } else if (type == 11) {
            filterStr = "ASSESSMENT";
        } else if (type == 12) {
            filterStr = "GAMELET_WORDJUMBLE";
        } else if (type == 13) {
            filterStr = "SURVEY";
        } else if (type == 5) {
            filterStr = "GENERAL";
        }
        else if(type == 31)
        {
            //filterStr = "Ram";
            Log.d(TAG, "meetFilterCriteria: ");
        }
        ArrayList<DTONewFeed> DTONewFeedList = new ArrayList<>();
        if (filterStr!= null && filterStr.length() > 0)
        {
            try {
                for (DTONewFeed frd : allfeeds) {
                    if (frd.getFeedType() != null) {
                        if (frd.getFeedType().name().contains(filterStr)) {
                            DTONewFeedList.add(frd);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } else {
            long currentTime = System.currentTimeMillis();
            if (type == 0) {
                return allfeeds;
            } else if (type == 1) {
                try {
                    for (DTONewFeed frd : allfeeds) {
                        if (frd != null && frd.getExpiryTime() > 0) {
                            if (frd.getExpiryTime() > currentTime) {
                                DTONewFeedList.add(frd);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else if (type == 2) {
                try {
                    for (DTONewFeed frd : allfeeds) {
                        if (frd != null && frd.getExpiryTime() > 0) {
                            if (frd.getExpiryTime() < currentTime) {
                                DTONewFeedList.add(frd);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else if (type == 21) {
                try {
                    for (DTONewFeed frd : allfeeds) {
                        if (frd != null && frd.getLocationType() != null && frd.getLocationType().equalsIgnoreCase("Regional")) {
                            DTONewFeedList.add(frd);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
            else if (type == 22)
            {
                try {
                    for (DTONewFeed frd : allfeeds) {
                        if (frd != null && frd.getLocationType() != null && frd.getLocationType().equalsIgnoreCase("National")) {
                            DTONewFeedList.add(frd);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            else if (type == 31)
            {
                try {
                    for (DTONewFeed frd : allfeeds) {
                        if (frd != null && frd.getFeedTag() != null && frd.getFeedTag().contains(tag)) {
                            DTONewFeedList.add(frd);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }
        return DTONewFeedList;
    }
}
