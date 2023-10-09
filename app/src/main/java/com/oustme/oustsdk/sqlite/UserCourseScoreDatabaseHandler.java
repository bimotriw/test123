package com.oustme.oustsdk.sqlite;


import android.util.Log;


import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by admin on 29/07/17.
 */

public class UserCourseScoreDatabaseHandler {

    public void addUserScoreToRealm(DTOUserCourseData userCourseData, long uniqueUserId) {
        Log.d("DatabaseHandle", "addUserScoreToRealm: "+uniqueUserId);
        userCourseData.setId(uniqueUserId);
        RoomHelper.addorUpdateScoreDataClass(userCourseData);
    }

    public DTOUserCourseData getScoreById(final long id) {
        DTOUserCourseData courseData = RoomHelper.getScoreById(id);
        return courseData == null ? new DTOUserCourseData() : courseData;
    }

    public void deleteUserCourseData(final long id) {
        RoomHelper.deleteUserCourseData(id);

    }

    public boolean isAcknowledged(final int id) {
        DTOUserCourseData userCourseData = RoomHelper.getScoreById(id);
        if (userCourseData != null) {
            return userCourseData.isAcknowledged();
        }
        return false;
    }

    public long getCurrentLevel(final int id) {
        DTOUserCourseData userCourseData = RoomHelper.getScoreById(id);
        if (userCourseData != null) {
            return userCourseData.getCurrentLevel();
        }
        return 0;
    }

    public long getPresentageComplete(final int id) {
        DTOUserCourseData userCourseData = RoomHelper.getScoreById(id);
        if (userCourseData != null) {
            return userCourseData.getPresentageComplete();
        }
        return 0;
    }

    public long getTotalOc(final int id) {
        DTOUserCourseData userCourseData = RoomHelper.getScoreById(id);
        if (userCourseData != null) {
            return userCourseData.getTotalOc();
        }
        return 0;
    }


    //=========================================
    public void setTotalCards(int totalCards, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setTotalCards(totalCards);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCourseComplete(boolean courseComplete, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setCourseComplete(courseComplete);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setLastCompleteLevel(int lastCompleteLevel, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setLastCompleteLevel(lastCompleteLevel);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setLastPlayedLevel(long lastPlayedLevel, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setLastPlayedLevel(lastPlayedLevel);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCurrentCompleteLevel(int currentCompleteLevel, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setCurrentCompleteLevel(currentCompleteLevel);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCurrentLevel(int currentLevel, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setCurrentLevel(currentLevel);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAcknowledged(boolean status, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setAcknowledged(status);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setDownloading(boolean status, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setDownloading(status);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUserLevelDataDownloadStatus(boolean status, DTOUserCourseData userCourseData, int position) {
        try {
            userCourseData.getUserLevelDataList().get(position).setDownloading(status);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUserLevelDataCompletePercentage(int percentage, DTOUserCourseData userCourseData, int position) {
        try {
            userCourseData.getUserLevelDataList().get(position).setCompletePercentage(percentage);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAskedVideoStorePermission(boolean status, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setAskedVideoStorePermission(status);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setDeleteDataAfterComplete(boolean status, DTOUserCourseData userCourseData) {
        try {
            userCourseData.setDeleteDataAfterComplete(status);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUserLevelDataTimeStamp(String timeStamp, DTOUserCourseData userCourseData, int position) {
        try {
            userCourseData.getUserLevelDataList().get(position).setTimeStamp(timeStamp);
            RoomHelper.addorUpdateScoreDataClass(userCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


}
