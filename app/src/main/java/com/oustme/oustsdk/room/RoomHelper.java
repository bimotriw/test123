package com.oustme.oustsdk.room;


import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.layoutFour.data.CatalogueItemData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.room.dto.DTOAdaptiveQuestionData;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCplCompletedModel;
import com.oustme.oustsdk.room.dto.DTOCplMedia;
import com.oustme.oustsdk.room.dto.DTOCplMediaUpdateData;
import com.oustme.oustsdk.room.dto.DTODiaryDetailsModel;
import com.oustme.oustsdk.room.dto.DTOFeedBackModel;
import com.oustme.oustsdk.room.dto.DTOLearningDiary;
import com.oustme.oustsdk.room.dto.DTOMapDataModel;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOResourceData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;

import java.util.ArrayList;
import java.util.List;

public class RoomHelper {

    private static final String TAG = "RoomHelper";


    /*  EntityResourceCollection  */

    public static void addorUpdateResourceCollectionModel(EntityResourceCollection resourceCollectionModel) {
        OustDBBuilder.addorUpdateResourceCollectionModel(resourceCollectionModel);
    }

    public static EntityResourceCollection getResourceCollectionModel(int id) {
        return OustDBBuilder.getResourceCollectionModel(id);
    }


    public static void updateResourceCollectionModel(EntityResourceCollection resCollectData, String resList, long updateTime) {
        OustDBBuilder.updateResourceCollectionModel(resCollectData, resList, updateTime);
    }

    public static int getResourceClassCount() {
        return OustDBBuilder.getResourceClassCount();
    }

    /*  EntityResourceCollection  */


    /*  EntityQuestions  */

    public static DTOQuestions getFfcQuestionById(long questionId) {
        return OustDBBuilder.getFfcQuestionById(questionId);
    }

    public static DTOQuestions getQuestionById(long questionId) {
        return OustDBBuilder.getQuestionById(questionId);
    }

    public static void addorUpdateFfcQuestion(DTOQuestions questions) {
        OustDBBuilder.addorUpdateFfcQuestion(questions);
    }

    public static void addorUpdateQuestion(final DTOQuestions questions) {
        OustDBBuilder.addorUpdateQuestion(questions);
    }

    public static void setLikeStatus(DTOQuestions questions, String status) {
        OustDBBuilder.setLikeStatus(questions, status);
    }

    public static void setFavourite(DTOQuestions questions, boolean status) {
        OustDBBuilder.setFavourite(questions, status);
    }

    public static void deleteQuestion(long questionId) {
        OustDBBuilder.deleteQuestion(questionId);
    }


    public static void addorUpdateFfcQuestion(DTOAdaptiveQuestionData questions) {
        OustDBBuilder.addorUpdateFfcQuestion(questions);
    }

    public static void addorUpdateQuestion(DTOAdaptiveQuestionData questions) {
        OustDBBuilder.addorUpdateQuestion(questions);
    }
    /*  EntityQuestions  */


    /*  EntityResourseStrings  */

    public static EntityResourseStrings getResourceStringModel(String languagePerfix) {
        return OustDBBuilder.getResourceStringModel(languagePerfix);
    }

    public static void addorUpdateResourceStringModel(EntityResourseStrings resourseStringsModel) {
        OustDBBuilder.addorUpdateResourceStringModel(resourseStringsModel);
    }

    /*  EntityResourseStrings  */


    /*  EntityResourceData  */

    public static DTOResourceData getResourceDataModel(String filename) {
        DTOResourceData dtoResourceData = OustDBBuilder.getResourceDataModel(filename);
        return dtoResourceData;
    }

    public static void addorUpdateResourceDataModel(final DTOResourceData resourceDataModel) {
        OustDBBuilder.addorUpdateResourceDataModel(resourceDataModel);
    }

    public static boolean isResourceDataModel(String fileName) {
        return OustDBBuilder.isResourceDataModel(fileName);
    }

    /*  EntityResourceData  */


    /*  EntityFeedBackModel  */

    public static void addFeedToRealm(DTOFeedBackModel dtoFeedBackModel) {
        OustDBBuilder.getBuilder().getOustDatabase().feedBackModelDao().insertFeedBackModelDao(DTOReverseConverter.getFeedBackModel(dtoFeedBackModel));
    }

    public static List<DTOFeedBackModel> getAllFeeds() {

        return OustDBBuilder.getAllFeeds();
    }

    public static void deleteFeeds(DTOFeedBackModel entityFeedBackModel) {
        OustDBBuilder.deleteFeeds(entityFeedBackModel);
    }

    /*  EntityFeedBackModel  */


    /*  CourseCardClass  */

    public static void addOrUpdateCourseCard(DTOCourseCard courseCardClass) {
        OustDBBuilder.addorUpdateCourseCard(courseCardClass);
    }

    public static void deleteCourseCard(long cardId) {
        OustDBBuilder.deleteCourseCard(cardId);
    }

    public static DTOCourseCard getCourseCardByCardId(long cardId) {
        return OustDBBuilder.getCourseCardByCardId(cardId);
    }

    public static List<EntityCourseCardClass> getCourseCard(long cardId) {
        return OustDBBuilder.getCourseCard(cardId);
    }

    public static void deleteCourseCardClass(long cardId) {
//        OustDBBuilder.getCourseCard(cardId);
        OustDBBuilder.deleteCourseCardClass(cardId);
    }

    /*  CourseCardClass  */

    /*  EntityUserCourseData  */

    public static DTOUserCourseData getScoreById(long id) {
        return OustDBBuilder.getScoreById(id);
    }

    public static void addorUpdateScoreDataClass(DTOUserCourseData userCourseData) {
        OustDBBuilder.addorUpdateScoreDataClass(userCourseData);
    }

    public static void deleteUserCourseData(long id) {
        OustDBBuilder.deleteUserCourseData(id);
    }

    public static void setCurrentCardNumber(DTOUserCourseData userCourseData, int level, int cardNo) {
        OustDBBuilder.setCurrentCardNumber(userCourseData, level, cardNo);
    }

    /*  EntityUserCourseData  */


    /*  EntityLevelCardCourseID  */

    public static void getAllCourseInLevel(long levelId) {
        OustDBBuilder.getAllCourseInLevel(levelId);
    }

    public static void addLevelCourseCardMap(final EntityLevelCardCourseID levelCardCourseIDModel) {
        OustDBBuilder.addLevelCourseCardMap(levelCardCourseIDModel);
    }

    public static List<EntityLevelCardCourseID> getLevelCourseCardMap(long levelId) {
        return OustDBBuilder.getLevelCourseCardMap(levelId);
    }

    public static void deleteLevelCourseCardMap(int levelId) {
        OustDBBuilder.deleteLevelCourseCardMap(levelId);
    }

    public static boolean checkMapTableExist(int levelId) {
        return OustDBBuilder.checkMapTableExist(levelId);
    }

    /*  EntityLevelCardCourseID  */

    // handled here for card edited
    public static void addLevelCourseCardUpdateTimeMap(final CourseLevelClass courseLevelClassList, String studentKey) {
        OustDBBuilder.addLevelCourseCardUpdateTimeMap(courseLevelClassList,studentKey);
    }

    public static List<EntityLevelCardCourseIDUpdateTime> getLevelCourseCardUpdateTimeMap(int levelId) {
        return OustDBBuilder.getLevelCourseUpdateTimeMap(levelId);
    }

    // handled here for downloaded or Not
    public static void addDownloadedOrNot(final int courseId, boolean downloadedOrNot, int percentage) {
        OustDBBuilder.addDownloadedOrNot(courseId, downloadedOrNot, percentage);
    }

    public static ArrayList<EntityDownloadedOrNot> getDownloadedOrNotS() {
        return OustDBBuilder.getDownloadedOrNot();
    }

    public static void downloadedOrNot(long courseId) {
        OustDBBuilder.downloadedOrNot(courseId);
    }


    /*  EntityCplMedia  */

    public static void addorUpdateCplMediaDataModel(final DTOCplMedia cplMediaData) {
        OustDBBuilder.addorUpdateCplMediaDataModel(DTOReverseConverter.getCplMedia(cplMediaData));
    }

    public static void updateCplMediaFile(String id, String fileName, String folderpath) {
        OustDBBuilder.updateCplMediaFile(id, fileName, folderpath);
    }

    public static ArrayList<DTOCplMedia> getCplMediaFiles(String cplId) {
        return DTOConverter.getCplMediaList(OustDBBuilder.getCplMediaFiles(cplId));
    }


    /*  EntityCplMedia  */


    /*  EntityCplMediaUpdateData  */

    public static void addorUpdateCplMediaModel(final String cplId, final long updateTime) {
        OustDBBuilder.addorUpdateCplMediaModel(cplId, updateTime);
    }

    public static DTOCplMediaUpdateData getCplUpdateModel(final String cplId) {
        return OustDBBuilder.getCplUpdateModel(cplId);
    }

    /*  EntityCplMediaUpdateData  */

    /*  EntityCplCompletedModel  */

    public static DTOCplCompletedModel getCPLDataById(long id) {
        return OustDBBuilder.getCPLDataById(id);
    }

    public static void addorUpdateCPLData(final DTOCplCompletedModel dtoCplCompletedModel) {
        OustDBBuilder.addorUpdateCPLData(dtoCplCompletedModel);
    }

    /*  EntityCplCompletedModel  */

    /*  EntityMapDataModel  */

    public static void addorUpdateMapDataModel(final DTOMapDataModel dtoMapDataModel) {
        OustDBBuilder.addorUpdateMapDataModel(dtoMapDataModel);
    }

    /*  EntityMapDataModel  */

    /*  EntityLearningDiary  */

    public static void addorUpdateLearningDiary(DTOLearningDiary diaryDetailsModel, List<DTODiaryDetailsModel> diaryDetailsModelList) {
        OustDBBuilder.addorUpdateLearningDiary(diaryDetailsModel, diaryDetailsModelList);
    }

    public static DTOLearningDiary getLearningDiaryById(String id) {
        return OustDBBuilder.getLearningDiaryById(id);
    }
    /*  EntityLearningDiary  */


    /*  EntityPostViewData  */

    public static void addorUpdateNBPostDataModel(final PostViewData postViewData) {
        OustDBBuilder.addorUpdateNBPostDataModel(postViewData);
    }

    public static List<PostViewData> getTypeBasedPostViewData(String type) {
        return OustDBBuilder.getTypeBasedPostViewData(type);
    }

    public static List<PostViewData> getPostViewDataById(long nbId, long postId) {
        return OustDBBuilder.getPostViewDataById(nbId, postId);
    }

    public static List<PostViewData> getPostViewReplyDataByPostId(long postId, long commentId) {
        return OustDBBuilder.getPostViewReplyDataByPostId(postId, commentId);
    }

    public static List<PostViewData> getPostViewDeletedReplyDataByPostId(long postId, long commentId) {
        return OustDBBuilder.getPostViewDeletedReplyDataByPostId(postId, commentId);
    }

    //

    public static void newAddorUpdateNBPostDataModel(final NewPostViewData postViewData) {
        OustDBBuilder.newAddorUpdateNBPostDataModel(postViewData);
    }

    public static List<NewPostViewData> newGetTypeBasedPostViewData(String type) {
        return OustDBBuilder.newGetTypeBasedPostViewData(type);
    }

    public static List<NewPostViewData> newGetPostViewDataById(long nbId, long postId) {
        return OustDBBuilder.newGetPostViewDataById(nbId, postId);
    }

    public static List<NewPostViewData> newGetPostViewReplyDataByPostId(long postId, long commentId) {
        return OustDBBuilder.newGetPostViewReplyDataByPostId(postId, commentId);
    }

    public static List<NewPostViewData> newGetPostViewDeletedReplyDataByPostId(long postId, long commentId) {
        return OustDBBuilder.newGetPostViewDeletedReplyDataByPostId(postId, commentId);
    }

    /*  EntityPostViewData  */

    /*  EntityNewFeed  */

    public static List<DTONewFeed> getNewFeeds() {
        return OustDBBuilder.getNewFeeds();
    }

    public static void addorUpdateNewFeed(List<DTONewFeed> dtoNewFeeds) {
        OustDBBuilder.addorUpdateNewFeed(dtoNewFeeds);
    }

    /*  EntityNewFeed  */

    /*  EntityCatalogueItemData  */

    public static List<CatalogueItemData> getEntityCatalogueItemDatas(long catalogueId) {
        return OustDBBuilder.getEntityCatalogueItemDatas(catalogueId);
    }

    public static void addorUpdateCatalogueItemData(List<CatalogueItemData> catalogueItemDatas, long catalogueId) {
        OustDBBuilder.insertCatalogueItemData(catalogueItemDatas, catalogueId);
    }

    /*  EntityCatalogueItemData  */


    /*  notification data  */
    public static void addNotificationData(List<NotificationResponse> notificationResponses) {
        OustDBBuilder.addNotificationData(notificationResponses);
    }

    public static List<NotificationResponse> getNotificationsData(String studentKey) {
        return OustDBBuilder.getNotifications(studentKey);
    }

    public static Boolean getNotificationById(String keyValue) {
        return OustDBBuilder.getNotificationById(keyValue);
    }

    public static Boolean getNotificationByContentId(String keyValue) {
        return OustDBBuilder.getNotificationByContentId(keyValue);
    }

    public static int updateNotificationReadData(String isRead, String contentId) {
        return OustDBBuilder.updateReadNotification(isRead, contentId);
    }

    public static int updateReadFireBaseNotificationData(String isRead, String keyId) {
        return OustDBBuilder.updateReadNotificationByContentId(isRead, keyId);
    }

    public static int deleteOfflineNotifications(String contentId) {
        return OustDBBuilder.deleteOfflineNotifications(contentId);
    }

    public static void deleteNotificationTable() {
        OustDBBuilder.deleteNotificationTable();
    }
    /*  notification data  */

    public static void addFeedsToRealm(DTOUserFeeds.FeedList feed) {
        OustDBBuilder.addorUpdateUserFeed(feed);
    }
    public static List<DTOUserFeeds.FeedList> getAllUserFeeds(){
        return OustDBBuilder.getAllUserFeeds();
    }

    public static void deleterAllUserFeeds(){
        OustDBBuilder.deleteAllUserFeedsData();
    }



}