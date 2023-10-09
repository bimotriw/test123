package com.oustme.oustsdk.tools;

import android.util.Log;


import com.google.gson.Gson;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 11/10/17.
 */

public class CommonTools {
    private static final String TAG = "CommonTools";

    public void setContestNotificationData(Map<String, Object> ffcDataMap, FastestFingerContestData fastestFingerContestData) {
        try {
            String contestnotification_message = OustPreferences.get("contestnotification_message");
            Gson gson = new Gson();
            ContestNotificationMessage contestNotificationMessage;
            if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
            } else {
                contestNotificationMessage = new ContestNotificationMessage();
            }
            contestNotificationMessage.setContestID((fastestFingerContestData.getFfcId()));
            contestNotificationMessage.setBanner(fastestFingerContestData.getBanner());
            contestNotificationMessage.setStartTime(fastestFingerContestData.getStartTime());
            contestNotificationMessage.setContestName(fastestFingerContestData.getName());
            contestNotificationMessage.setStudentId(OustAppState.getInstance().getActiveUser().getStudentid());
            contestNotificationMessage.setAvatar(OustAppState.getInstance().getActiveUser().getAvatar());
            contestNotificationMessage.setDisplayName(OustAppState.getInstance().getActiveUser().getUserDisplayName());
            if (ffcDataMap.get("greaterThan24") != null) {
                Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("greaterThan24");
                if (subMap != null) {
                    if (subMap.get("frequency") != null) {
                        long frequency = (long) subMap.get("frequency");
                        if ((subMap.get("message") != null) && (frequency > 0)) {
                            contestNotificationMessage.setGreater24Time(86400 / frequency);
                            contestNotificationMessage.setGreater24Message((String) subMap.get("message"));
                        }
                    }
                }
            }
            if (ffcDataMap.get("lessThan24") != null) {
                Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lessThan24");
                if (subMap != null) {
                    if (subMap.get("frequency") != null) {
                        long frequency = (long) subMap.get("frequency");
                        if ((subMap.get("message") != null) && (frequency > 0)) {
                            contestNotificationMessage.setGreatehourTime(86400 / frequency);
                            contestNotificationMessage.setGreatehourMessage((String) subMap.get("message"));
                        }
                    }
                }
            }
            long lastMinute = 0;
            if (ffcDataMap.get("lastMinute") != null) {
                Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lastMinute");
                if (subMap != null) {
                    if (subMap.get("message") != null) {
                        contestNotificationMessage.setLastMinuteMessage((String) subMap.get("message"));
                    }
                    if (subMap.get("minutes") != null) {
                        contestNotificationMessage.setLastMinuteTime(((long) subMap.get("minutes") * 60));
                        lastMinute = ((long) subMap.get("minutes") * 60);
                    }
                }
            }
            if (ffcDataMap.get("lessThanHour") != null) {
                Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lessThanHour");
                if (subMap != null) {
                    if (subMap.get("frequency") != null) {
                        long frequency = (long) subMap.get("frequency");
                        if ((subMap.get("message") != null) && (frequency > 0)) {
                            contestNotificationMessage.setLesshourTime((3600 - lastMinute) / frequency);
                            contestNotificationMessage.setLesshourMessage((String) subMap.get("message"));
                        }
                    }
                }
            }
            if (ffcDataMap.get("LBReadyMessage") != null) {
                contestNotificationMessage.setLBReadyMessage((String) ffcDataMap.get("LBReadyMessage"));
            }
            if (fastestFingerContestData.getqIds() != null) {
                long totalContestTime = ((fastestFingerContestData.getQuestionTime() * fastestFingerContestData.getqIds().size()) +
                        (fastestFingerContestData.getRestTime() * (fastestFingerContestData.getqIds().size() - 1)));
                contestNotificationMessage.setLeaderboardNotificationTime(((totalContestTime + fastestFingerContestData.getConstructingLBTime()) / 1000));
                contestNotificationMessage.setTotalContestTime((totalContestTime / 1000));
            }
            if (ffcDataMap.get("contestStartMessage") != null) {
                contestNotificationMessage.setContestStartMessage((String) ffcDataMap.get("contestStartMessage"));
            }

            String contestnotification_message1 = gson.toJson(contestNotificationMessage);
            OustPreferences.save("contestnotification_message", contestnotification_message1);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public DTONewFeed getNewFeedFromMap(HashMap<String, Object> mymap, String timeStamp1) {
        DTONewFeed feed1 = new DTONewFeed();
        try {
            if (feed1 == null) {
                feed1 = new DTONewFeed();
            }

            if (mymap.get("feedTag") != null) {
                feed1.setFeedTag((String) mymap.get("feedTag"));
            }

            if (mymap.get("header") != null) {
                feed1.setHeader((String) mymap.get("header"));
            }
            if ((timeStamp1 != null) && (!timeStamp1.isEmpty())) {
                feed1.setTimestamp(Long.parseLong(timeStamp1));
            } else {
                if (mymap.get("timeStamp") != null) {
                    feed1.setTimestamp((Long) mymap.get("timeStamp"));
                }
            }
            if (mymap.get("weightage") != null) {
                feed1.setFeedPriority((long) mymap.get("weightage"));
            } else {
                feed1.setFeedPriority(0);
            }
            if (mymap.get("landingBannerMessage") != null) {
                feed1.setLandingBannerMessage((String) mymap.get("landingBannerMessage"));
            }
            if (mymap.get("content") != null) {
                feed1.setContent((String) mymap.get("content"));
            }
            if (mymap.get("icon") != null) {
                feed1.setIcon((String) mymap.get("icon"));
            }
            if (mymap.get("imageUrl") != null) {
                feed1.setImageUrl((String) mymap.get("imageUrl"));
            }
            if (mymap.get("type") != null) {
                try {
                    feed1.setFeedType(FeedType.valueOf((String) mymap.get("type")));
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (mymap.get("numLikes") != null) {
                feed1.setNumLikes((long) mymap.get("numLikes"));
            }
            if (mymap.get("numComments") != null) {
                feed1.setNumComments((long) mymap.get("numComments"));
            }
            if (mymap.get("numShares") != null) {
                feed1.setNumShares((long) mymap.get("numShares"));
            }
            if (mymap.get("shareable") != null) {
                feed1.setSharable((boolean) mymap.get("shareable"));
            }

            if (mymap.get("SpecialFeedStartText") != null) {
                feed1.setmSpecialFeedStartText((String) mymap.get("SpecialFeedStartText"));
            }

            if (mymap.get(AppConstants.StringConstants.COMMENT_ABLE) != null) {
                feed1.setCommentble((boolean) mymap.get(AppConstants.StringConstants.COMMENT_ABLE));
            } else {
                feed1.setCommentble(true);
            }

            if (mymap.get(AppConstants.StringConstants.LIKE_ABLE) != null) {
                feed1.setLikeble((boolean) mymap.get(AppConstants.StringConstants.LIKE_ABLE));
            } else {
                feed1.setLikeble(true);
            }

            if (mymap.get("isDescVisible") != null) {
                feed1.setDescVisible((boolean) mymap.get("isDescVisible"));
            } else {
                feed1.setDescVisible(true);
            }

            if (mymap.get("isTitleVisible") != null) {
                feed1.setTitleVisible((boolean) mymap.get("isTitleVisible"));
            } else {
                feed1.setTitleVisible(true);
            }

            if (mymap.get("feedExpiry") != null) {
                feed1.setExpiryTime(OustSdkTools.convertToLong(mymap.get("feedExpiry")));
            }
            if (mymap.get("locationType") != null) {
                feed1.setLocationType((String) mymap.get("locationType"));
            }
            Map<String, Object> deepLinkInfo = (Map<String, Object>) mymap.get("deepLinkInfo");
            if (deepLinkInfo != null) {
                if (deepLinkInfo.get("buttonLabel") != null) {
                    feed1.setNewBtnText((String) deepLinkInfo.get("buttonLabel"));
                }
                if (deepLinkInfo.get("eventCode") != null) {
                    feed1.setEventItd((String) deepLinkInfo.get("eventCode"));
                }
                if (deepLinkInfo.get("moduleId") != null) {
                    feed1.setModuleId((String) deepLinkInfo.get("moduleId"));
                }
                if (deepLinkInfo.get("groupId") != null) {
                    feed1.setGroupId((String) deepLinkInfo.get("groupId"));
                }
                if (deepLinkInfo.get("cardId") != null) {
                    long id = (long) deepLinkInfo.get("cardId");
                    if (id == 0) {
                        if (deepLinkInfo.get("id") != null) {
                            id = Long.parseLong((String) deepLinkInfo.get("id"));
                        }
                    }
                    feed1.setId(id);
                }
                if (deepLinkInfo.get("assessmentId") != null) {
                    try {
                        feed1.setAssessmentId((long) deepLinkInfo.get("assessmentId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                if (deepLinkInfo.get("courseId") != null) {
                    try {
                        feed1.setCourseId((long) deepLinkInfo.get("courseId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                if (deepLinkInfo.get("cplId") != null) {
                    try {
                        feed1.setParentCplId((long) deepLinkInfo.get("cplId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
            Map<String, Object> cardInfo = (Map<String, Object>) mymap.get("cardInfo");
            feed1.setCourseCardClass(getCardFromMap(cardInfo));

            Map<String, Object> butMap = (Map<String, Object>) mymap.get("button");
            if (butMap != null) {
                if (butMap.get("btnText") != null) {
                    feed1.setBtntext((String) butMap.get("btnText"));
                }
                if (butMap.get("btnActionURI") != null) {
                    feed1.setLink((String) butMap.get("btnActionURI"));
                }
            }
            if (mymap.get("cplId") != null) {
                feed1.setCplId(OustSdkTools.convertToLong(mymap.get("cplId")));
            }


            if (mymap.get("mappedCourseId") != null) {
                feed1.setMappedCourseId(OustSdkTools.convertToLong(mymap.get("mappedCourseId")));
            }

            if (mymap.get("fileName") != null) {
                feed1.setFileName((String) mymap.get("fileName"));
            }

            if (mymap.get("feedViewCount") != null) {
                feed1.setFeedViewCount(OustSdkTools.convertToLong(mymap.get("feedViewCount")));
            }

            if (mymap.get("fileType") != null) {
                feed1.setFileType((String) mymap.get("fileType"));
            }

            if (mymap.get("fileSize") != null) {
                feed1.setFileSize((String) mymap.get("fileSize"));
            }

            if (mymap.get("feedCoins") != null) {
                feed1.setFeedCoins(OustSdkTools.convertToLong(mymap.get("feedCoins")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return feed1;
    }

    public DTONewFeed getSpecialFeedFromMap(HashMap<String, Object> mymap, DTONewFeed feed1) {
        try {
            Log.d(TAG, "getSpecialFeedFromMap: ");
            if (mymap.get("feedTag") != null) {
                feed1.setFeedTag((String) mymap.get("feedTag"));
            }

            if (mymap.get("header") != null) {
                feed1.setHeader((String) mymap.get("header"));
            }
            if (mymap.get("timeStamp") != null) {
                feed1.setTimestamp((Long) mymap.get("timeStamp"));
            }
            if (mymap.get("weightage") != null) {
                feed1.setFeedPriority((long) mymap.get("weightage"));
            } else {
                feed1.setFeedPriority(0);
            }
            if (mymap.get("landingBannerMessage") != null) {
                feed1.setLandingBannerMessage((String) mymap.get("landingBannerMessage"));
            }
            if (mymap.get("content") != null) {
                feed1.setContent((String) mymap.get("content"));
            }
            if (mymap.get("icon") != null) {
                feed1.setIcon((String) mymap.get("icon"));
            }
            if (mymap.get("imageUrl") != null) {
                feed1.setImageUrl((String) mymap.get("imageUrl"));
            }
            if (mymap.get("type") != null) {
                try {
                    feed1.setFeedType(FeedType.valueOf((String) mymap.get("type")));
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (mymap.get("numLikes") != null) {
                feed1.setNumLikes((long) mymap.get("numLikes"));
            }
            if (mymap.get("numComments") != null) {
                feed1.setNumComments((long) mymap.get("numComments"));
            }
            if (mymap.get("numShares") != null) {
                feed1.setNumShares((long) mymap.get("numShares"));
            }
            if (mymap.get("shareable") != null) {
                feed1.setSharable((boolean) mymap.get("shareable"));
            }

            if (mymap.get("SpecialFeedStartText") != null) {
                feed1.setmSpecialFeedStartText((String) mymap.get("SpecialFeedStartText"));
            }

            if (mymap.get(AppConstants.StringConstants.COMMENT_ABLE) != null) {
                feed1.setCommentble((boolean) mymap.get(AppConstants.StringConstants.COMMENT_ABLE));
            } else {
                feed1.setCommentble(true);
            }

            if (mymap.get(AppConstants.StringConstants.LIKE_ABLE) != null) {
                feed1.setLikeble((boolean) mymap.get(AppConstants.StringConstants.LIKE_ABLE));
            } else {
                feed1.setLikeble(true);
            }

            if (mymap.get("isDescVisible") != null) {
                feed1.setDescVisible((boolean) mymap.get("isDescVisible"));
            } else {
                feed1.setDescVisible(true);
            }

            if (mymap.get("isTitleVisible") != null) {
                feed1.setTitleVisible((boolean) mymap.get("isTitleVisible"));
            } else {
                feed1.setTitleVisible(true);
            }

            if (mymap.get("feedExpiry") != null) {
                feed1.setExpiryTime(OustSdkTools.convertToLong(mymap.get("feedExpiry")));
            }
            if (mymap.get("locationType") != null) {
                feed1.setLocationType((String) mymap.get("locationType"));
            }
            Map<String, Object> deepLinkInfo = (Map<String, Object>) mymap.get("deepLinkInfo");
            if (deepLinkInfo != null) {
                if (deepLinkInfo.get("buttonLabel") != null) {
                    feed1.setNewBtnText((String) deepLinkInfo.get("buttonLabel"));
                    feed1.setmSpecialFeedStartText((String) deepLinkInfo.get("buttonLabel"));
                }
                if (deepLinkInfo.get("eventCode") != null) {
                    feed1.setEventItd((String) deepLinkInfo.get("eventCode"));
                }
                if (deepLinkInfo.get("moduleId") != null) {
                    feed1.setModuleId((String) deepLinkInfo.get("moduleId"));
                }
                if (deepLinkInfo.get("groupId") != null) {
                    feed1.setGroupId((String) deepLinkInfo.get("groupId"));
                }
                if (deepLinkInfo.get("cardId") != null) {
                    long id = (long) deepLinkInfo.get("cardId");
                    if (id == 0) {
                        if (deepLinkInfo.get("id") != null) {
                            id = Long.parseLong((String) deepLinkInfo.get("id"));
                        }
                    }
                    feed1.setId(id);
                }
                if (deepLinkInfo.get("assessmentId") != null) {
                    try {
                        feed1.setAssessmentId((long) deepLinkInfo.get("assessmentId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                if (deepLinkInfo.get("courseId") != null) {
                    try {
                        feed1.setCourseId((long) deepLinkInfo.get("courseId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                if (deepLinkInfo.get("cplId") != null) {
                    try {
                        feed1.setParentCplId((long) deepLinkInfo.get("cplId"));
                        Log.d(TAG, "getSpecialFeedFromMap: " + feed1.getParentCplId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
            Map<String, Object> cardInfo = (Map<String, Object>) mymap.get("cardInfo");
            feed1.setCourseCardClass(getCardFromMap(cardInfo));

            Map<String, Object> butMap = (Map<String, Object>) mymap.get("button");
            if (butMap != null) {
                if (butMap.get("btnText") != null) {
                    feed1.setBtntext((String) butMap.get("btnText"));
                }
                if (butMap.get("btnActionURI") != null) {
                    feed1.setLink((String) butMap.get("btnActionURI"));
                }
            }
            if (mymap.get("cplId") != null) {
                feed1.setCplId(OustSdkTools.convertToLong(mymap.get("cplId")));
            }


            if (mymap.get("mappedCourseId") != null) {
                feed1.setMappedCourseId(OustSdkTools.convertToLong(mymap.get("mappedCourseId")));
            }

            if (mymap.get("fileName") != null) {
                feed1.setFileName((String) mymap.get("fileName"));
            }

            if (mymap.get("feedViewCount") != null) {
                feed1.setFeedViewCount(OustSdkTools.convertToLong(mymap.get("feedViewCount")));
            }

            if (mymap.get("fileType") != null) {
                feed1.setFileType((String) mymap.get("fileType"));
            }

            if (mymap.get("fileSize") != null) {
                feed1.setFileSize((String) mymap.get("fileSize"));
            }

            if (mymap.get("feedCoins") != null) {
                feed1.setFeedCoins(OustSdkTools.convertToLong(mymap.get("feedCoins")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return feed1;
    }

    public DTONewFeed getNewFeedFromMap(HashMap<String, Object> mymap, DTONewFeed feed1) {
        try {
            if (feed1 == null) {
                feed1 = new DTONewFeed();
            }

            if (mymap.get("feedTag") != null) {
                feed1.setFeedTag((String) mymap.get("feedTag"));
            }

            if (mymap.get("header") != null) {
                feed1.setHeader((String) mymap.get("header"));
            }
            if (mymap.get("timeStamp") != null) {
                feed1.setTimestamp((Long) mymap.get("timeStamp"));
            }
            if (mymap.get("weightage") != null) {
                feed1.setFeedPriority((long) mymap.get("weightage"));
            } else {
                feed1.setFeedPriority(0);
            }
            if (mymap.get("landingBannerMessage") != null) {
                feed1.setLandingBannerMessage((String) mymap.get("landingBannerMessage"));
            }
            if (mymap.get("content") != null) {
                feed1.setContent((String) mymap.get("content"));
            }
            if (mymap.get("icon") != null) {
                feed1.setIcon((String) mymap.get("icon"));
            }
            if (mymap.get("imageUrl") != null) {
                feed1.setImageUrl((String) mymap.get("imageUrl"));
            }
            if (mymap.get("type") != null) {
                try {
                    feed1.setFeedType(FeedType.valueOf((String) mymap.get("type")));
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (mymap.get("numLikes") != null) {
                feed1.setNumLikes((long) mymap.get("numLikes"));
            }
            if (mymap.get("numComments") != null) {
                feed1.setNumComments((long) mymap.get("numComments"));
            }
            if (mymap.get("numShares") != null) {
                feed1.setNumShares((long) mymap.get("numShares"));
            }
            if (mymap.get("shareable") != null) {
                feed1.setSharable((boolean) mymap.get("shareable"));
            }

            if (mymap.get("SpecialFeedStartText") != null) {
                feed1.setmSpecialFeedStartText((String) mymap.get("SpecialFeedStartText"));
            }

            if (mymap.get(AppConstants.StringConstants.COMMENT_ABLE) != null) {
                feed1.setCommentble((boolean) mymap.get(AppConstants.StringConstants.COMMENT_ABLE));
            } else {
                feed1.setCommentble(true);
            }

            if (mymap.get(AppConstants.StringConstants.LIKE_ABLE) != null) {
                feed1.setLikeble((boolean) mymap.get(AppConstants.StringConstants.LIKE_ABLE));
            } else {
                feed1.setLikeble(true);
            }

            if (mymap.get("isDescVisible") != null) {
                feed1.setDescVisible((boolean) mymap.get("isDescVisible"));
            } else {
                feed1.setDescVisible(true);
            }

            if (mymap.get("markFeedForAnnouncement") != null) {
                feed1.setMarkFeedForAnnouncement((boolean) mymap.get("markFeedForAnnouncement"));
            } else {
                feed1.setMarkFeedForAnnouncement(false);
            }

            if (mymap.get("isTitleVisible") != null) {
                feed1.setTitleVisible((boolean) mymap.get("isTitleVisible"));
            } else {
                feed1.setTitleVisible(true);
            }

            if (mymap.get("feedExpiry") != null) {
                feed1.setExpiryTime(OustSdkTools.convertToLong(mymap.get("feedExpiry")));
            }
            if (mymap.get("locationType") != null) {
                feed1.setLocationType((String) mymap.get("locationType"));
            }
            Map<String, Object> deepLinkInfo = (Map<String, Object>) mymap.get("deepLinkInfo");
            if (deepLinkInfo != null) {
                if (deepLinkInfo.get("buttonLabel") != null) {
                    feed1.setNewBtnText((String) deepLinkInfo.get("buttonLabel"));
                }
                if (deepLinkInfo.get("eventCode") != null) {
                    feed1.setEventItd((String) deepLinkInfo.get("eventCode"));
                }
                if (deepLinkInfo.get("moduleId") != null) {
                    feed1.setModuleId((String) deepLinkInfo.get("moduleId"));
                }
                if (deepLinkInfo.get("groupId") != null) {
                    feed1.setGroupId((String) deepLinkInfo.get("groupId"));
                }
                if (deepLinkInfo.get("cardId") != null) {
                    long id = (long) deepLinkInfo.get("cardId");
                    if (id == 0) {
                        if (deepLinkInfo.get("id") != null) {
                            id = Long.parseLong((String) deepLinkInfo.get("id"));
                        }
                    }
                    feed1.setId(id);
                }
                if (deepLinkInfo.get("assessmentId") != null) {
                    try {
                        feed1.setAssessmentId((long) deepLinkInfo.get("assessmentId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                if (deepLinkInfo.get("courseId") != null) {
                    try {
                        feed1.setCourseId((long) deepLinkInfo.get("courseId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                if (deepLinkInfo.get("cplId") != null) {
                    try {
                        feed1.setParentCplId((long) deepLinkInfo.get("cplId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
            Map<String, Object> cardInfo = (Map<String, Object>) mymap.get("cardInfo");
            feed1.setCourseCardClass(getCardFromMap(cardInfo));

            Map<String, Object> butMap = (Map<String, Object>) mymap.get("button");
            if (butMap != null) {
                if (butMap.get("btnText") != null) {
                    feed1.setBtntext((String) butMap.get("btnText"));
                }
                if (butMap.get("btnActionURI") != null) {
                    feed1.setLink((String) butMap.get("btnActionURI"));
                }
            }
            if (mymap.get("cplId") != null) {
                feed1.setCplId(OustSdkTools.convertToLong(mymap.get("cplId")));
            }

            if (mymap.get("mappedCourseId") != null) {
                feed1.setMappedCourseId(OustSdkTools.convertToLong(mymap.get("mappedCourseId")));
            }

            if (mymap.get("fileName") != null) {
                feed1.setFileName((String) mymap.get("fileName"));
            }

            if (mymap.get("feedViewCount") != null) {
                feed1.setFeedViewCount(OustSdkTools.convertToLong(mymap.get("feedViewCount")));
            }

            if (mymap.get("fileType") != null) {
                feed1.setFileType((String) mymap.get("fileType"));
            }

            if (mymap.get("fileSize") != null) {
                feed1.setFileSize((String) mymap.get("fileSize"));
            }

            if (mymap.get("feedCoins") != null) {
                feed1.setFeedCoins(OustSdkTools.convertToLong(mymap.get("feedCoins")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return feed1;
    }

    public DTOCourseCard getCardFromMap(Map<String, Object> cardInfo) {
        DTOCourseCard courseCardClass = new DTOCourseCard();
        if (cardInfo != null) {
            if (cardInfo.get("bgImg") != null) {
                courseCardClass.setBgImg((String) cardInfo.get("bgImg"));
            }
            if (cardInfo.get("cardBgColor") != null) {
                courseCardClass.setCardBgColor((String) cardInfo.get("cardBgColor"));
            }
            if (cardInfo.get("cardQuestionColor") != null) {
                courseCardClass.setCardQuestionColor((String) cardInfo.get("cardQuestionColor"));
            }
            if (cardInfo.get("cardSolutionColor") != null) {
                courseCardClass.setCardSolutionColor((String) cardInfo.get("cardSolutionColor"));
            }
            if (cardInfo.get("cardTextColor") != null) {
                courseCardClass.setCardTextColor((String) cardInfo.get("cardTextColor"));
            }
            String cardType = "";
            if (cardInfo.get("cardType") != null) {
                cardType = (String) cardInfo.get("cardType");
            }
            courseCardClass.setCardType(cardType);
            if (cardInfo.get("clCode") != null) {
                courseCardClass.setClCode((String) cardInfo.get("clCode"));
            }
            if (cardInfo.get("content") != null) {
                courseCardClass.setContent((String) cardInfo.get("content"));
            }

            if (cardInfo.get("cardTitle") != null) {
                courseCardClass.setCardTitle((String) cardInfo.get("cardTitle"));
            }

            if (cardInfo.get("mandatoryViewTime") != null) {
                courseCardClass.setMandatoryViewTime(OustSdkTools.newConvertToLong(cardInfo.get("mandatoryViewTime")));
            }

            if (cardInfo.get("cardLayout") != null) {
                courseCardClass.setCardLayout((String) cardInfo.get("cardLayout"));
            }
            if (cardInfo.get("qId") != null) {
                courseCardClass.setqId(OustSdkTools.newConvertToLong(cardInfo.get("qId")));
            }
            if (cardInfo.get("cardId") != null) {
                courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardInfo.get("cardId")));
            }
            if (cardInfo.get("language") != null) {
                courseCardClass.setLanguage((String) cardInfo.get("language"));
            }
            if (cardInfo.get("xp") != null) {
                courseCardClass.setXp(OustSdkTools.newConvertToLong(cardInfo.get("xp")));
            }
            if (cardInfo.get("shareToSocialMedia") != null) {
                courseCardClass.setShareToSocialMedia((Boolean) cardInfo.get("shareToSocialMedia"));
            }
            if (cardInfo.get("potraitModeVideo") != null) {
                courseCardClass.setPotraitModeVideo((Boolean) cardInfo.get("potraitModeVideo"));
            }
            if (cardInfo.get("scormIndexFile") != null) {
                courseCardClass.setScormIndexFile((String) cardInfo.get("scormIndexFile"));
            }
            List<DTOCourseCardMedia> courseCardMediaList = new ArrayList<>();
            if (cardInfo.get("cardMedia") != null) {
                List<Object> mediaMap = (List<Object>) cardInfo.get("cardMedia");
                if (mediaMap != null) {
                    for (int k = 0; k < mediaMap.size(); k++) {
                        if (mediaMap.get(k) != null) {
                            final Map<String, Object> mediasubMap = (Map<String, Object>) mediaMap.get(k);
                            if (mediasubMap != null) {
                                DTOCourseCardMedia courseCardMedia = new DTOCourseCardMedia();
                                if (mediasubMap.get("data") != null) {
                                    courseCardMedia.setData((String) mediasubMap.get("data"));
                                }
                                if (mediasubMap.get("gumletVideoUrl") != null) {
                                    courseCardMedia.setGumletVideoUrl((String) mediasubMap.get("gumletVideoUrl"));
                                }
                                if (mediasubMap.get("mediaType") != null) {
                                    courseCardMedia.setMediaType((String) mediasubMap.get("mediaType"));
                                }
                                if (mediasubMap.get("fastForwardMedia") != null) {
                                    courseCardMedia.setFastForwardMedia((Boolean) mediasubMap.get("fastForwardMedia"));
                                }
                                if (mediasubMap.get("mediaPrivacy") != null) {
                                    courseCardMedia.setMediaPrivacy((String) mediasubMap.get("mediaPrivacy"));
                                }
                                if (mediasubMap.get("mediaThumbnail") != null) {
                                    courseCardMedia.setMediaThumbnail((String) mediasubMap.get("mediaThumbnail"));
                                }
                                courseCardMediaList.add(courseCardMedia);
                            }
                        }
                    }
                }
            }
            courseCardClass.setCardMedia(courseCardMediaList);
            if ((courseCardClass.getXp() == 0)) {
                courseCardClass.setXp(100);
            }
            Map<String, Object> cardColorScheme = (Map<String, Object>) cardInfo.get("cardColorScheme");
            if (cardColorScheme != null) {
                DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                if (cardColorScheme.get("contentColor") != null) {
                    cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                }
                if (cardColorScheme.get("bgImage") != null) {
                    cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                }
                if (cardColorScheme.get("iconColor") != null) {
                    cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                }
                if (cardColorScheme.get("levelNameColor") != null) {
                    cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                }
                if (cardColorScheme.get("optionColor") != null) {
                    cardColorScheme1.setOptionColor((String) cardColorScheme.get("optionColor"));
                }
                if (cardColorScheme.get("titleColor") != null) {
                    cardColorScheme1.setTitleColor((String) cardColorScheme.get("titleColor"));
                }
                courseCardClass.setCardColorScheme(cardColorScheme1);
            }

            try {
                Map<String, Object> readmoremap = (Map<String, Object>) cardInfo.get("readMoreData");
                if (readmoremap != null) {
                    DTOReadMore readMoreData = new DTOReadMore();
                    if (readmoremap.get("data") != null) {
                        readMoreData.setData((String) readmoremap.get("data"));
                    }
                    if (readmoremap.get("displayText") != null) {
                        readMoreData.setDisplayText((String) readmoremap.get("displayText"));
                    }
                    if (readmoremap.get("rmId") != null) {
                        try {
                            Object object = readmoremap.get("rmId");
                            if (object.getClass().equals(String.class)) {
                                readMoreData.setRmId(Long.parseLong((String) object));
                            } else if (object.getClass().equals(Long.class)) {
                                readMoreData.setRmId((long) object);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                    if (readmoremap.get("scope") != null) {
                        readMoreData.setScope((String) readmoremap.get("scope"));
                    }
                    if (readmoremap.get("type") != null) {
                        readMoreData.setType((String) readmoremap.get("type"));
                    }
                    courseCardClass.setReadMoreData(readMoreData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            return courseCardClass;
        }
        return null;
    }

    public CommonLandingData getCourseLandingData(Map<String, Object> lpMap, CommonLandingData commonLandingData) {
        try {
            //here
            if (lpMap.get("elementId") != null) {
                commonLandingData.setModuleId(OustSdkTools.newConvertToLong(lpMap.get("elementId")));
            }
            if (lpMap.get("addedOn") != null) {
                commonLandingData.setAddedOn((String) lpMap.get("addedOn"));
            }
            if (lpMap.get("locked") != null) {
                commonLandingData.setLocked((boolean) lpMap.get("locked"));
            }
            if (lpMap.get("enrolled") != null) {
                commonLandingData.setEnrolled((boolean) lpMap.get("enrolled"));
            }
            if (lpMap.get("name") != null) {
                commonLandingData.setName((String) lpMap.get("name"));
            }
            long userOC = 0;
            long totalCourseOC = 0;
            if (lpMap.get("userOC") != null) {
                userOC = OustSdkTools.newConvertToLong(lpMap.get("userOC"));
            }
            if (lpMap.get("totalCourseOC") != null) {
                totalCourseOC = OustSdkTools.newConvertToLong(lpMap.get("totalCourseOC"));
            }
            if (totalCourseOC == 0) {
                totalCourseOC = userOC;
            }
            commonLandingData.setUserOc(userOC);
            commonLandingData.setOc(totalCourseOC);
            if (lpMap.get("enrolled") != null) {
                commonLandingData.setEnrolled((boolean) lpMap.get("enrolled"));
            }
            if (lpMap.get("new_landing_type") != null) {
                commonLandingData.setLanding_data_type((String) lpMap.get("new_landing_type"));
            } else {
                commonLandingData.setLanding_data_type("Learn");
            }
            if (lpMap.get("completionPercentage") != null) {
                Object o3 = lpMap.get("completionPercentage");
                if (o3.getClass().equals(Long.class)) {
                    commonLandingData.setCompletionPercentage((long) o3);
                } else if (o3.getClass().equals(String.class)) {
                    String s3 = (String) o3;
                    commonLandingData.setCompletionPercentage(Long.parseLong(s3));
                } else if (o3.getClass().equals(Double.class)) {
                    Double s3 = (Double) o3;
                    long l = (new Double(s3)).longValue();
                    commonLandingData.setCompletionPercentage(l);
                }
            }/*else{
                    //For assessment -
                    if(lpMap.get("completionDate") != null){
                        commonLandingData.setCompletionPercentage(100);
                    }else if(lpMap.get("passed") != null && (boolean)lpMap.get("passed")){
                        commonLandingData.setCompletionPercentage(100);
                    }
                }*/

            if (lpMap.get("courseDeadline") != null) {
                commonLandingData.setCourseDeadline((String) lpMap.get("courseDeadline"));
            }
            if (lpMap.get("completionDeadline") != null) {
                commonLandingData.setCourseDeadline((String) lpMap.get("completionDeadline"));
                commonLandingData.setCompletionDeadline((String) lpMap.get("completionDeadline"));
            }

            if (lpMap.get("contentPlayListId") != null) {
                commonLandingData.setCplId(OustSdkTools.newConvertToLong(lpMap.get("contentPlayListId")));
            } else {
                commonLandingData.setCplId(0);
            }

        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return commonLandingData;
    }


    public CommonLandingData getCourseLandingDataForCPLv1(Map<String, Object> lpMap, CommonLandingData commonLandingData) {
        try {
            Log.d(TAG, "getCourseLandingDataForCPL: ");
            //here
            if (lpMap.get("elementId") != null) {
                commonLandingData.setModuleId(OustSdkTools.convertToLong(lpMap.get("elementId")));
            }
            if (lpMap.get("contentId") != null) {
                commonLandingData.setModuleId(OustSdkTools.convertToLong(lpMap.get("contentId")));
            }
            if (lpMap.get("addedOn") != null) {
                commonLandingData.setAddedOn((String) lpMap.get("addedOn"));
            }
            if (lpMap.get("locked") != null) {
                commonLandingData.setLocked((boolean) lpMap.get("locked"));
            }
            if (lpMap.get("enrolled") != null) {
                commonLandingData.setEnrolled((boolean) lpMap.get("enrolled"));
            }
            if (lpMap.get("name") != null) {
                commonLandingData.setName((String) lpMap.get("name"));
            }
            long userOC = 0;
            long totalCourseOC = 0;
            if (lpMap.get("userOC") != null) {
                userOC = OustSdkTools.convertToLong(lpMap.get("userOC"));
            }
            if (lpMap.get("totalCourseOC") != null) {
                totalCourseOC = OustSdkTools.convertToLong(lpMap.get("totalCourseOC"));
            }
            if (totalCourseOC == 0) {
                totalCourseOC = userOC;
            }
            commonLandingData.setUserOc(userOC);
            commonLandingData.setOc(totalCourseOC);
            if (lpMap.get("enrolled") != null) {
                commonLandingData.setEnrolled((boolean) lpMap.get("enrolled"));
            }
            if (lpMap.get("new_landing_type") != null) {
                commonLandingData.setLanding_data_type((String) lpMap.get("new_landing_type"));
            } else {
                commonLandingData.setLanding_data_type("Learn");
            }
            if (lpMap.get("completionPercentage") != null) {
                Object o3 = lpMap.get("completionPercentage");
                if (o3 != null) {
                    if (o3.getClass().equals(Long.class)) {
                        commonLandingData.setCompletionPercentage((OustSdkTools.convertToLong(o3)));
                    } else if (o3.getClass().equals(String.class)) {
                        String s3 = (String) o3;
                        commonLandingData.setCompletionPercentage(Long.parseLong(s3));
                    } else if (o3.getClass().equals(Double.class)) {
                        Double s3 = (Double) o3;
                        long l = (new Double(s3)).longValue();
                        commonLandingData.setCompletionPercentage(l);
                    }
                }
            } else {
                //For assessment - cpl v1
                if (lpMap.get("completionDate") != null) {
                    commonLandingData.setCompletionPercentage(100);
                } else if (lpMap.get("passed") != null && (boolean) lpMap.get("passed")) {
                    commonLandingData.setCompletionPercentage(100);
                }
            }

            if (lpMap.get("courseDeadline") != null) {
                commonLandingData.setCourseDeadline((String) lpMap.get("courseDeadline"));
            }
            if (lpMap.get("completionDeadline") != null) {
                commonLandingData.setCourseDeadline((String) lpMap.get("completionDeadline"));
                commonLandingData.setCompletionDeadline((String) lpMap.get("completionDeadline"));
            }

            if (lpMap.get("contentPlayListId") != null) {
                commonLandingData.setCplId(OustSdkTools.convertToLong(lpMap.get("contentPlayListId")));
            } else {
                commonLandingData.setCplId(0);
            }

        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return commonLandingData;
    }

    public CommonLandingData getCourseCommonData(Map<String, Object> lpMap, CommonLandingData commonLandingData) {
        try {
            //both layout3 & layout4
            if (lpMap.get("courseName") != null) {
                commonLandingData.setName((String) lpMap.get("courseName"));
            } else {
                commonLandingData.setName("");
            }

            if (lpMap.get("mode") != null) {
                commonLandingData.setMode((String) lpMap.get("mode"));
            }

            if (lpMap.get("certificate") != null) {
                commonLandingData.setCertificate((boolean) lpMap.get("certificate"));
            }
            /*if (lpMap.get("courseDeadline") != null) {
                commonLandingData.setCourseDeadline((String) lpMap.get("courseDeadline"));
            }
            if (lpMap.get("completionDeadline") != null) {
                commonLandingData.setCourseDeadline((String) lpMap.get("completionDeadline"));
                commonLandingData.setCompletionDeadline((String) lpMap.get("completionDeadline"));
            }*/
            if (lpMap.get("icon") != null) {
                commonLandingData.setIcon((String) lpMap.get("icon"));
            }
            if (lpMap.get("bgImg") != null) {
                commonLandingData.setBanner((String) lpMap.get("bgImg"));
            }
            if (lpMap.get("numEnrolledUsers") != null) {
                commonLandingData.setEnrollCount(OustSdkTools.convertToLong(lpMap.get("numEnrolledUsers")));
            }
            if (lpMap.get("contentDuration") != null) {
                commonLandingData.setTime(OustSdkTools.convertToLong(lpMap.get("contentDuration")));
            }
            if (lpMap.get("notificationTitle") != null) {
                commonLandingData.setNotificationTitle((String) lpMap.get("notificationTitle"));
            }
            if (lpMap.get("enrollReminderNotificationContent") != null) {
                commonLandingData.setEnrollNotificationContent((String) lpMap.get("enrollReminderNotificationContent"));
            }
            if (lpMap.get("courseType") != null) {
                commonLandingData.setCourseType((String) lpMap.get("courseType"));
            }
            if (lpMap.get("completeReminderNotificationContent") != null) {
                commonLandingData.setCompleteNotificationContent((String) lpMap.get("completeReminderNotificationContent"));
            }
            if (lpMap.get("completeReminderNotificationInterval") != null) {
                commonLandingData.setReminderNotificationInterval(OustSdkTools.convertToLong(lpMap.get("completeReminderNotificationInterval")));
            }
            if (lpMap.get("new_landing_type") != null) {
                String landing_type = (String) lpMap.get("new_landing_type");
                if (landing_type != null && !landing_type.isEmpty()) {
                    commonLandingData.setLanding_data_type((String) lpMap.get("new_landing_type"));
                } else {
                    commonLandingData.setLanding_data_type("Learn");
                }
            } else {
                commonLandingData.setLanding_data_type("Learn");
            }
            if (lpMap.get("levelLock") != null) {
                commonLandingData.setLevelLock((boolean) lpMap.get("levelLock"));
            }
            if (lpMap.get("rating") != null) {
                try {
                    long rating = OustSdkTools.newConvertToLong(lpMap.get("rating"));
                    commonLandingData.setRating(rating);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (lpMap.get("courseTags") != null) {
                commonLandingData.setCourseTags((String) lpMap.get("courseTags"));
            }

            if (lpMap.get("levels") != null) {
                Object o1 = lpMap.get("levels");
                if (o1.getClass().equals(ArrayList.class)) {
                    List<Object> objectList = (List<Object>) o1;
                    commonLandingData.setCourseLevelsSize(objectList.size());
                } else if (o1.getClass().equals(HashMap.class)) {
                    final Map<String, Object> objectMap = (Map<String, Object>) o1;
                    commonLandingData.setCourseLevelsSize(objectMap.size());
                }

            }
            Object o1 = lpMap.get("multiLangCourseDataList");

            if (lpMap.get("totalOc") != null) {
                commonLandingData.setTotalOc(OustSdkTools.convertToLong(lpMap.get("totalOc")));
            }

            if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                List<Object> multiLangCourses = (List<Object>) lpMap.get("multiLangCourseDataList");
                List<MultilingualCourse> multiLangCourseList = new ArrayList<>();
                if (multiLangCourses != null && multiLangCourses.size() > 0) {
                    int length = multiLangCourses.size();
                    for (int i = 0; i < length; i++) {
                        if (multiLangCourses.get(i) != null) {
                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) multiLangCourses.get(i);
                            MultilingualCourse multilingualCourse = new MultilingualCourse();
                            multilingualCourse.setCourseId(OustSdkTools.newConvertToLong(levelMap.get("courseId")));
                            multilingualCourse.setLangId(OustSdkTools.newConvertToLong(levelMap.get("langId")));
                            multiLangCourseList.add(multilingualCourse);
                        }
                        commonLandingData.setMultilingualCourseListList(multiLangCourseList);
                    }
                }
            } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                Map<String, Object> multiLangCourses = (Map<String, Object>) lpMap.get("multiLangCourseDataList");
                List<MultilingualCourse> multiLangCourseList = new ArrayList<>();
                if (multiLangCourses != null && multiLangCourses.size() > 0) {
                    for (String s1 : multiLangCourses.keySet()) {
                        if (multiLangCourses.get(s1) != null) {
                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) multiLangCourses.get(s1);
                            MultilingualCourse multilingualCourse = new MultilingualCourse();
                            multilingualCourse.setCourseId(OustSdkTools.newConvertToLong(levelMap.get("courseId")));
                            multilingualCourse.setLangId(OustSdkTools.newConvertToLong(levelMap.get("langId")));
                            multiLangCourseList.add(multilingualCourse);
                        }
                    }
                    commonLandingData.setMultilingualCourseListList(multiLangCourseList);
                }
            } else if (o1 != null) {
                Map<String, Object> multiLangCourses = (Map<String, Object>) lpMap.get("multiLangCourseDataList");
                List<MultilingualCourse> multiLangCourseList = new ArrayList<>();
                if (multiLangCourses != null && multiLangCourses.size() > 0) {
                    for (String s1 : multiLangCourses.keySet()) {
                        if (multiLangCourses.get(s1) != null) {
                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) multiLangCourses.get(s1);
                            MultilingualCourse multilingualCourse = new MultilingualCourse();
                            multilingualCourse.setCourseId(OustSdkTools.newConvertToLong(levelMap.get("courseId")));
                            multilingualCourse.setLangId(OustSdkTools.newConvertToLong(levelMap.get("langId")));
                            multiLangCourseList.add(multilingualCourse);
                        }
                    }
                    commonLandingData.setMultilingualCourseListList(multiLangCourseList);
                }
            }

            if (lpMap.get("description") != null) {
                commonLandingData.setDescription((String) lpMap.get("description"));
            }
            if (lpMap.get("cplId") != null) {
                commonLandingData.setCplId(OustSdkTools.convertToLong(lpMap.get("cplId")));
            }

            if (lpMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                commonLandingData.setDefaultPastDeadlineCoinsPenaltyPercentage(OustSdkTools.convertToLong(lpMap.get("defaultPastDeadlineCoinsPenaltyPercentage")));
            }
            if (lpMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                commonLandingData.setShowPastDeadlineModulesOnLandingPage((boolean) lpMap.get("showPastDeadlineModulesOnLandingPage"));
            }
            if (lpMap.get("showNudgeMessage") != null) {
                commonLandingData.setShowNudgeMessage((boolean) lpMap.get("showNudgeMessage"));
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } catch (ClassCastException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("COURSE", "error occured in getCourseCommonData()", e);
        }
        return commonLandingData;
    }

    public CommonLandingData getAssessmentCommonData(Map<String, Object> lpMap, CommonLandingData commonLandingData) {
        try {
            if (lpMap.get("name") != null) {
                commonLandingData.setName((String) lpMap.get("name"));
            } else {
                commonLandingData.setName("");
            }
            if (lpMap.get("description") != null) {
                commonLandingData.setDescription((String) lpMap.get("description"));
            } else {
                commonLandingData.setDescription("");
            }

            if (lpMap.get("certificate") != null) {
                commonLandingData.setCertificate((boolean) lpMap.get("certificate"));
            }
            if (lpMap.get("banner") != null) {
                commonLandingData.setBanner((String) lpMap.get("banner"));
            }
            if (lpMap.get("enrolledCount") != null) {
                commonLandingData.setEnrollCount(OustSdkTools.newConvertToLong(lpMap.get("enrolledCount")));
            }

            if (lpMap.get("numQuestions") != null) {
                commonLandingData.setNumQuestions(OustSdkTools.newConvertToLong(lpMap.get("numQuestions")));
            }
            if (lpMap.get("contentDuration") != null) {
                commonLandingData.setTime(OustSdkTools.newConvertToLong(lpMap.get("contentDuration")));
            }
            if (lpMap.get("notificationTitle") != null) {
                commonLandingData.setNotificationTitle((String) lpMap.get("notificationTitle"));
            }
            if (lpMap.get("enrollReminderNotificationContent") != null) {
                commonLandingData.setEnrollNotificationContent((String) lpMap.get("enrollReminderNotificationContent"));
            }
            if (lpMap.get("completeReminderNotificationContent") != null) {
                commonLandingData.setCompleteNotificationContent((String) lpMap.get("completeReminderNotificationContent"));
            }
            if (lpMap.get("completeReminderNotificationInterval") != null) {
                commonLandingData.setReminderNotificationInterval(OustSdkTools.newConvertToLong(lpMap.get("completeReminderNotificationInterval")));
            }
            if (lpMap.get("icon") != null) {
                commonLandingData.setIcon((String) lpMap.get("icon"));
            }
            if (lpMap.get("new_landing_type") != null) {
                commonLandingData.setLanding_data_type((String) lpMap.get("new_landing_type"));
            } else {
                commonLandingData.setLanding_data_type("Learn");
            }
            if (lpMap.get("rewardOC") != null) {
                commonLandingData.setTotalOc(OustSdkTools.convertToLong(lpMap.get("rewardOC")));
            }

            if (lpMap.get("rating") != null) {
                try {
                    String rating = (String) lpMap.get("rating");
                    commonLandingData.setRating(Integer.parseInt(rating));
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (lpMap.get("cplId") != null) {
                commonLandingData.setCplId(OustSdkTools.convertToLong(lpMap.get("cplId")));
            }
            if (lpMap.get("passPercentage") != null) {
                commonLandingData.setPassPercentage(OustSdkTools.convertToLong(lpMap.get("passPercentage")));
            }

            if (lpMap.get("noOfAttemptAllowedToPass") != null) {
                commonLandingData.setNoOfAttemptAllowedToPass(OustSdkTools.convertToLong(lpMap.get("noOfAttemptAllowedToPass")));
            }

            if (lpMap.get("courseAssociated") != null) {
                commonLandingData.setCourseAssociated((boolean) lpMap.get("courseAssociated"));
            } else {
                commonLandingData.setCourseAssociated(false);
            }

            if (lpMap.get("mappedCourseId") != null) {
                commonLandingData.setMappedCourseId(OustSdkTools.newConvertToLong(lpMap.get("mappedCourseId")));
            }

            /*if (lpMap.get("completionDeadline") != null) {
                commonLandingData.setCourseDeadline((String) lpMap.get("completionDeadline"));
                commonLandingData.setCompletionDeadline((String) lpMap.get("completionDeadline"));
            }*/

            if (lpMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                commonLandingData.setDefaultPastDeadlineCoinsPenaltyPercentage(OustSdkTools.convertToLong(lpMap.get("defaultPastDeadlineCoinsPenaltyPercentage")));
            }
            if (lpMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                commonLandingData.setShowPastDeadlineModulesOnLandingPage((boolean) lpMap.get("showPastDeadlineModulesOnLandingPage"));
            }

        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return commonLandingData;
    }

    public static CourseDataClass extractCourseData(final int learningPathId, Map<String, Object> learingMap) {
        CourseDataClass courseDataClass = new CourseDataClass();
        try {
            if (learingMap.get("courseId") != null) {
                Log.d(TAG, "extractCourseData: courseID" + learingMap.get("courseId"));
                courseDataClass.setCourseId(OustSdkTools.convertToLong(learingMap.get("courseId")));
            }
            if (learingMap.get("autoDownload") != null) {
                courseDataClass.setAutoDownload((boolean) (learingMap.get("autoDownload")));
            }
            if (learingMap.get("autoPlay") != null) {
                courseDataClass.setAutoPlay((boolean) (learingMap.get("autoPlay")));
            }
            if (learingMap.get("courseName") != null) {
                Log.d(TAG, "extractCourseData: coyrsename:" + learingMap.get("courseName"));
                courseDataClass.setCourseName((String) learingMap.get("courseName"));
            }
            if (learingMap.get("updateTS") != null) {
                Log.d(TAG, "extractCourseData: updateTS:" + learingMap.get("updateTS"));
                courseDataClass.setUpdateTs((String) learingMap.get("updateTS"));
            }
            if (learingMap.get("bgImg") != null) {
                courseDataClass.setBgImg((String) learingMap.get("bgImg"));
            }
            if (learingMap.get("disableCourseCompleteAudio") != null) {
                courseDataClass.setDisableCourseCompleteAudio((Boolean) learingMap.get("disableCourseCompleteAudio"));
            }

            if (learingMap.get("showVirtualCurrency") != null) {
                courseDataClass.setShowVirtualCoins((boolean) (learingMap.get("showVirtualCurrency")));
            } else {
                courseDataClass.setShowVirtualCoins(true);
            }

            if (learingMap.get("mappedAssessmentId") != null) {
                courseDataClass.setMappedAssessmentId(OustSdkTools.convertToLong(learingMap.get("mappedAssessmentId")));
            }
            if (learingMap.get("salesMode") != null) {
                courseDataClass.setSalesMode((boolean) learingMap.get("salesMode"));
            }
            if (learingMap.get("regularMode") != null) {
                boolean isRegularMode = (boolean) learingMap.get("regularMode");
                Log.d(TAG, "IsRegularMode:" + isRegularMode);
                courseDataClass.setRegularMode(isRegularMode);
            } else {
                courseDataClass.setRegularMode(false);
            }

            if (learingMap.get("disableLevelCompletePopup") != null) {
                courseDataClass.setDisableLevelCompletePopup((boolean) learingMap.get("disableLevelCompletePopup"));
            } else {
                courseDataClass.setDisableLevelCompletePopup(false);
            }

            if (learingMap.get("language") != null) {
                courseDataClass.setLanguage((String) learingMap.get("language"));
            }
            if (learingMap.get("disableReviewMode") != null) {
                courseDataClass.setDisableReviewMode((boolean) learingMap.get("disableReviewMode"));
            }

            if (learingMap.get("courseDisableBackButton") != null) {
                courseDataClass.setDisableBackButton((boolean) learingMap.get("courseDisableBackButton"));
            } else {
                courseDataClass.setDisableBackButton(false);
            }

            if (learingMap.get("cplId") != null) {
                long cplId = OustSdkTools.convertToLong(learingMap.get("cplId"));
                OustPreferences.saveTimeForNotification("cplId_course", cplId);
            } else {
                OustPreferences.saveTimeForNotification("cplId_course", 0);
            }
            if (learingMap.get("mappedAssessmentDetails") != null) {
                Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learingMap.get("mappedAssessmentDetails");
                if (mappedAssessmentMap.get("name") != null) {
                    courseDataClass.setMappedAssessmentName((String) mappedAssessmentMap.get("name"));
                }
                if (mappedAssessmentMap.get("id") != null) {
                    courseDataClass.setMappedAssessmentId(OustSdkTools.convertToLong(mappedAssessmentMap.get("id")));
                }
            }
            if (learingMap.get("zeroXpForLCard") != null) {
                courseDataClass.setZeroXpForLCard((boolean) learingMap.get("zeroXpForLCard"));
            }
            if (learingMap.get("zeroXpForQCard") != null) {
                courseDataClass.setZeroXpForQCard((boolean) learingMap.get("zeroXpForQCard"));
            }
            if (learingMap.get("hideLeaderboard") != null) {
                courseDataClass.setHideLeaderBoard((boolean) learingMap.get("hideLeaderboard"));
            } else {
                courseDataClass.setHideLeaderBoard(false);
            }

            if (learingMap.get("showQuesInReviewMode") != null) {
                courseDataClass.setShowQuesInReviewMode((boolean) learingMap.get("showQuesInReviewMode"));
            }

            if (learingMap.get("hideBulletinBoard") != null) {
                courseDataClass.setHideBulletinBoard((boolean) learingMap.get("hideBulletinBoard"));
            } else {
                courseDataClass.setHideBulletinBoard(false);
            }

            if (learingMap.get("fullScreenPotraitModeVideo") != null) {
                courseDataClass.setFullScreenPotraitModeVideo(((boolean) learingMap.get("fullScreenPotraitModeVideo")));
            }
            if (learingMap.get("descriptionCard") != null) {
                Log.d(TAG, "extractCourseData: card info:");
                //cardInfo = (Map<String, Object>) learingMap.get("descriptionCard");
                //isAnyIntroPopupVisible = true;
                //Log.i("description ", cardInfo.toString());
            }
            if (learingMap.get("ackPopup") != null) {
                Map<Object, Object> desclaimerMap = (Map<Object, Object>) learingMap.get("ackPopup");
                /*courseDesclaimerData = new CourseDesclaimerData();
                if (desclaimerMap.get("body") != null) {
                    courseDesclaimerData.setContent((String) desclaimerMap.get("body"));
                }

                if (desclaimerMap.get("buttonLabel") != null) {
                    courseDesclaimerData.setBtnText((String) desclaimerMap.get("buttonLabel"));
                }

                if (desclaimerMap.get("checkBoxText") != null) {
                    courseDesclaimerData.setCheckBoxText((String) desclaimerMap.get("checkBoxText"));
                }

                if (desclaimerMap.get("header") != null) {
                    courseDesclaimerData.setHeader((String) desclaimerMap.get("header"));
                }*/
            }

            //tips
            if (learingMap.get("tips") != null) {
                List<String> hintList = new ArrayList<>();
                Map<String, String> hintMap = new HashMap<>();
                Object o1 = learingMap.get("tips");
                if (o1.getClass().equals(HashMap.class)) {
                    hintMap = (Map<String, String>) o1;
                    for (String key : hintMap.keySet()) {
                        hintList.add(hintMap.get(key));
                    }
                } else if (o1.getClass().equals(ArrayList.class)) {
                    hintList = (ArrayList<String>) o1;
                }
                OustAppState.getInstance().setHintMessages(hintList);
            }
            if (learingMap.get("certificate") != null) {
                courseDataClass.setCertificate((boolean) learingMap.get("certificate"));
            }
            if (learingMap.get("enableBadge") != null) {
                courseDataClass.setEnableBadge((boolean) learingMap.get("enableBadge"));
            }
            if (learingMap.get("disableScreenShot") != null) {
                courseDataClass.setDisableScreenShot((boolean) learingMap.get("disableScreenShot"));
            }
            if (learingMap.get("lpBgImageNew") != null) {
                courseDataClass.setLpBgImage((String) learingMap.get("lpBgImageNew"));
            }
            if (learingMap.get("isTTSEnabledQC") != null) {
                courseDataClass.setQuesttsEnabled((boolean) learingMap.get("isTTSEnabledQC"));
            }
            if (learingMap.get("isTTSEnabledLC") != null) {
                courseDataClass.setCardttsEnabled((boolean) learingMap.get("isTTSEnabledLC"));
            }
            if (learingMap.get("icon") != null) {
                courseDataClass.setIcon((String) learingMap.get("icon"));
            }
            if (learingMap.get("startFromLastLevel") != null) {
                courseDataClass.setStartFromLastLevel((boolean) learingMap.get("startFromLastLevel"));
            }
            if (learingMap.get("totalOc") != null) {
                courseDataClass.setTotalOc(OustSdkTools.convertToLong(learingMap.get("totalOc")));
                Log.e(TAG, "total oc from firebase course node" + learingMap.get("totalOc"));
            }
            if (learingMap.get("removeDataAfterCourseCompletion") != null) {
                Object o4 = learingMap.get("removeDataAfterCourseCompletion");
                if (o4.getClass().equals(String.class)) {
                    courseDataClass.setRemoveDataAfterCourseCompletion(Boolean.valueOf((String) learingMap.get("removeDataAfterCourseCompletion")));
                } else {
                    courseDataClass.setRemoveDataAfterCourseCompletion((boolean) learingMap.get("removeDataAfterCourseCompletion"));
                }
            }
            long courseTotalOc = 0;
            Object o1 = learingMap.get("levels");
            if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                List<Object> levelsList = (List<Object>) learingMap.get("levels");
                List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                if (levelsList != null) {
                    for (int i = 0; i < levelsList.size(); i++) {
                        if (levelsList.get(i) != null) {
                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                            courseLevelClass.setLevelId(i);
                            courseLevelClass.setLpId(learningPathId);
                            if (levelMap.get("levelDescription") != null) {
                                courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                            }
                            if (levelMap.get("downloadStratergy") != null) {
                                courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                            }
                            if (levelMap.get("levelMode") != null) {
                                courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                            }
                            if (levelMap.get("levelName") != null) {
                                courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                            }
                            if (levelMap.get("xp") != null) {
                                courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
                            }
                            if (levelMap.get("levelThumbnail") != null) {
                                courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                            }
                            if (levelMap.get("hidden") != null) {
                                courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                            }
                            if (levelMap.get("sequence") != null) {
                                courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                            }
                            if (levelMap.get("totalOc") != null) {
                                courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                courseTotalOc += (OustSdkTools.convertToLong(levelMap.get("totalOc")));
                            }
                            if (levelMap.get("updateTime") != null) {
                                courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                            }

                            courseLevelClass.setLevelLock(false);
                            if (levelMap.get("levelLock") != null) {
                                courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                            }
                            Log.e("Level Lock", "Level number unlocked " + i + " " + courseLevelClass.isLevelLock());
                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                            if (levelMap.get("cards") != null) {
                                Object o2 = levelMap.get("cards");
                                if (o2.getClass().equals(HashMap.class)) {
                                    Map<String, Object> cardMap = (Map<String, Object>) levelMap.get("cards");
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }

                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                } else if (o2.getClass().equals(ArrayList.class)) {
                                    List<Object> cardList = (List<Object>) levelMap.get("cards");
                                    if (cardList != null) {
                                        for (int l = 0; l < cardList.size(); l++) {
                                            if (cardList.get(l) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }

                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                }
                            }
                            Collections.sort(courseCardClassList, courseCardSorter);
                            courseLevelClass.setCourseCardClassList(courseCardClassList);
                            courseLevelClassList.add(courseLevelClass);
                        }
                    }
                    Collections.sort(courseLevelClassList, courseLevelSorter);
                    try {
                        if (courseLevelClassList.get(0).getSequence() == 0 && !courseLevelClassList.get(0).isLevelLock()) {
                            for (int q = 0; q < courseLevelClassList.size(); q++) {
                                courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    courseDataClass.setCourseLevelClassList(courseLevelClassList);
                    //presenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColnId);
                }
            } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                Map<String, Object> levelsList = (Map<String, Object>) learingMap.get("levels");
                List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                if (levelsList != null) {
                    for (String s1 : levelsList.keySet()) {
                        if (levelsList.get(s1) != null) {
                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(s1);
                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                            courseLevelClass.setLevelId(Integer.parseInt(s1));
                            courseLevelClass.setLpId(learningPathId);
                            if (levelMap.get("levelDescription") != null) {
                                courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                            }
                            if (levelMap.get("downloadStratergy") != null) {
                                courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                            }
                            if (levelMap.get("xp") != null) {
                                courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
                            }
                            if (levelMap.get("levelMode") != null) {
                                courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                            }
                            if (levelMap.get("levelThumbnail") != null) {
                                courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                            }
                            if (levelMap.get("levelName") != null) {
                                courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                            }
                            if (levelMap.get("hidden") != null) {
                                courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                            }
                            if (levelMap.get("sequence") != null) {
                                courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                            }
                            if (levelMap.get("totalOc") != null) {
                                courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                courseTotalOc += (OustSdkTools.convertToLong(levelMap.get("totalOc")));
                            }
                            if (levelMap.get("updateTime") != null) {
                                courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                            }
                            courseLevelClass.setLevelLock(false);
                            if (levelMap.get("levelLock") != null) {
                                courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                            }
                            Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                            if (levelMap.get("cards") != null) {
                                Object o2 = levelMap.get("cards");
                                if (o2.getClass().equals(HashMap.class)) {
                                    Map<String, Object> cardMap = (Map<String, Object>) o2;
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                } else if (o2.getClass().equals(ArrayList.class)) {
                                    List<Object> cardList = (List<Object>) levelMap.get("cards");
                                    if (cardList != null) {
                                        for (int l = 0; l < cardList.size(); l++) {
                                            if (cardList.get(l) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }

                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                }
                            }
                            Collections.sort(courseCardClassList, courseCardSorter);
                            courseLevelClass.setCourseCardClassList(courseCardClassList);
                            courseLevelClassList.add(courseLevelClass);
                        }
                    }
                    Collections.sort(courseLevelClassList, courseLevelSorter);
                    try {
                        if (courseLevelClassList.get(0).getSequence() == 0) {
                            for (int q = 0; q < courseLevelClassList.size(); q++) {
                                courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    courseDataClass.setCourseLevelClassList(courseLevelClassList);

                    //presenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColnId);
                }
            } else {
                Map<String, Object> levelsList = (Map<String, Object>) learingMap.get("levels");
                List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                if (levelsList != null) {
                    for (String s1 : levelsList.keySet()) {
                        if (levelsList.get(s1) != null) {
                            //Map<String, Object> featuresFromJson = new Gson().fromJson(levelsList.get(s1), new TypeToken<Map<String, Object>>() {}.getType());
                            final Map<String, Object> levelMap = (Map<String, Object>) levelsList.get(s1);
                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                            courseLevelClass.setLevelId(Integer.parseInt(s1));
                            courseLevelClass.setLpId(learningPathId);
                            if (levelMap.get("levelDescription") != null) {
                                courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                            }
                            if (levelMap.get("downloadStratergy") != null) {
                                courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                            }
                            if (levelMap.get("xp") != null) {
                                courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
                            }
                            if (levelMap.get("levelMode") != null) {
                                courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                            }
                            if (levelMap.get("levelThumbnail") != null) {
                                courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                            }
                            if (levelMap.get("levelName") != null) {
                                courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                            }
                            if (levelMap.get("hidden") != null) {
                                courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                            }
                            if (levelMap.get("sequence") != null) {
                                courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                            }
                            if (levelMap.get("totalOc") != null) {
                                courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                courseTotalOc += (OustSdkTools.convertToLong(levelMap.get("totalOc")));
                            }
                            if (levelMap.get("updateTime") != null) {
                                courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                            }
                            courseLevelClass.setLevelLock(false);
                            if (levelMap.get("levelLock") != null) {
                                courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                            }

                            Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                            if (levelMap.get("cards") != null) {
                                Object o2 = levelMap.get("cards");
                                if (o2.getClass().equals(HashMap.class)) {
                                    Map<String, Object> cardMap = (Map<String, Object>) o2;
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
//                                                if(cardsubMap.get("clCode")!=null){
//                                                    try{
//                                                        courseCardClass.setClCode(LearningCardType.valueOf((String) cardsubMap.get("clCode")));
//                                                    }catch (Exception e){}
//                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                } else if (o2.getClass().equals(ArrayList.class)) {
                                    List<Object> cardList = (List<Object>) levelMap.get("cards");
                                    if (cardList != null) {
                                        for (int l = 0; l < cardList.size(); l++) {
                                            if (cardList.get(l) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }

                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                } else {
                                    Map<String, Object> cardMap = (Map<String, Object>) o2;
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                }
                            }
                            Collections.sort(courseCardClassList, courseCardSorter);
                            courseLevelClass.setCourseCardClassList(courseCardClassList);
                            courseLevelClassList.add(courseLevelClass);
                        }
                    }
                    Collections.sort(courseLevelClassList, courseLevelSorter);
                    try {
                        if (courseLevelClassList.get(0).getSequence() == 0) {
                            for (int q = 0; q < courseLevelClassList.size(); q++) {
                                courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    courseDataClass.setCourseLevelClassList(courseLevelClassList);
                    //presenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColnId);
                }

            }
            //loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return courseDataClass;
    }

    public static Comparator<DTOUserLevelData> courseUserCardSorter = (s1, s2) -> (int) s1.getSequece() - (int) s2.getSequece();


    public static Comparator<DTOCourseCard> courseCardSorter = (s1, s2) -> (int) s1.getSequence() - (int) s2.getSequence();

    public static Comparator<CourseLevelClass> courseLevelSorter = (s1, s2) -> (int) s1.getSequence() - (int) s2.getSequence();

}
