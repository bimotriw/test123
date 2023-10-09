package com.oustme.oustsdk.room;


import android.util.Log;


import androidx.room.Room;

import com.google.gson.Gson;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.layoutFour.data.CatalogueItemData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.room.dto.DTOAdaptiveQuestionData;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCplCompletedModel;
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
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.room.dto.EntityNotificationData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OustDBBuilder {
    private final static String TAG = "OustDBBuilder";
    private final static String DB_NAME = "db_task";
    static private OustDBBuilder builder;
    static private OustRoomDatabase oustDatabase;

    public static OustDBBuilder getBuilder() {
        if (builder == null)
            builder = new OustDBBuilder();
        return builder;
    }

    OustRoomDatabase getOustDatabase() {
        if (oustDatabase == null) {
            oustDatabase = Room.databaseBuilder(OustSdkApplication.getContext(),
                            OustRoomDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return oustDatabase;
    }

    /*  EntityResourceCollection  */

    static void addorUpdateResourceCollectionModel(final EntityResourceCollection resourceCollectionModel) {
        OustDBBuilder.getBuilder().getOustDatabase().resourceCollectionModelDao().insertResourceCollectionModel(resourceCollectionModel);
    }

    static EntityResourceCollection getResourceCollectionModel(int id) {
        EntityResourceCollection resourceCollectionModel = OustDBBuilder.getBuilder().getOustDatabase().resourceCollectionModelDao().findResourceCollectionModel(id);
        return resourceCollectionModel;
    }

    static void updateResourceCollectionModel(EntityResourceCollection resCollectData, String resList, long updateTime) {

        resCollectData.setTimeStamp(updateTime);
        resCollectData.setResStrlist(resList);
        OustDBBuilder.getBuilder().getOustDatabase().resourceCollectionModelDao().insertResourceCollectionModel(resCollectData);
    }

    static int getResourceClassCount() {
        long count = OustDBBuilder.getBuilder().getOustDatabase().resourceCollectionModelDao().getCount();
        return (int) count;
    }

    /*  EntityResourceCollection  */

    /*  EntityQuestions  */

    static DTOQuestions getFfcQuestionById(long questionId) {
        String quesCardId = OustPreferences.get("tanentid") + "_ffc" + questionId;
        Log.d(TAG, "getQuestionById: " + quesCardId);
        EntityQuestions questions = OustDBBuilder.getBuilder().getOustDatabase().questionsDao().findQuestions(quesCardId);
        return DTOConverter.getQuestions(questions);
    }

    public static DTOQuestions getQuestionById(long questionId) {
        String quesCardId = OustPreferences.get("tanentid") + "" + questionId;
        EntityQuestions questions = OustDBBuilder.getBuilder().getOustDatabase().questionsDao().findQuestions(quesCardId);
        return DTOConverter.getQuestions(questions);
    }

    static void addorUpdateFfcQuestion(final DTOQuestions questions) {
        String quesCardId = OustPreferences.get("tanentid") + "_ffc" + questions.getQuestionId();
        questions.setQuestionCardId(quesCardId);
        EntityQuestions entityQuestions = DTOReverseConverter.getQuestions(questions);
        OustDBBuilder.getBuilder().getOustDatabase().questionsDao().instertQuestions(entityQuestions);
    }

    public static void addorUpdateQuestion(final DTOQuestions questions) {
        String quesCardId = OustPreferences.get("tanentid") + "" + questions.getQuestionId();
        questions.setQuestionCardId(quesCardId);
        EntityQuestions entityQuestions = DTOReverseConverter.getQuestions(questions);
        OustDBBuilder.getBuilder().getOustDatabase().questionsDao().instertQuestions(entityQuestions);
    }

    public static void setLikeStatus(DTOQuestions questions, String status) {
        questions.setLikeUnlike(status);
        EntityQuestions entityQuestions = DTOReverseConverter.getQuestions(questions);
        OustDBBuilder.getBuilder().getOustDatabase().questionsDao().instertQuestions(entityQuestions);
    }

    public static void setFavourite(DTOQuestions questions, boolean status) {
        questions.setFavourite(status);
        EntityQuestions entityQuestions = DTOReverseConverter.getQuestions(questions);
        OustDBBuilder.getBuilder().getOustDatabase().questionsDao().instertQuestions(entityQuestions);
    }

    static void deleteQuestion(long questionId) {
        try {
            String quesCardId = OustPreferences.get("tanentid") + "" + questionId;
            OustDBBuilder.getBuilder().getOustDatabase().questionsDao().deleteQuestions(quesCardId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    static void addorUpdateFfcQuestion(DTOAdaptiveQuestionData questions) {
        String quesCardId = OustPreferences.get("tanentid") + "_ffc" + questions.getQuestionId();
        EntityQuestions entityQuestions = DTOReverseConverter.getQuestions(questions);
        entityQuestions.setQuestionCardId(quesCardId);
        OustDBBuilder.getBuilder().getOustDatabase().questionsDao().instertQuestions(entityQuestions);
    }

    static void addorUpdateQuestion(DTOAdaptiveQuestionData questions) {
        String quesCardId = OustPreferences.get("tanentid") + "" + questions.getQuestionId();
        EntityQuestions entityQuestions = DTOReverseConverter.getQuestions(questions);
        entityQuestions.setQuestionCardId(quesCardId);
        OustDBBuilder.getBuilder().getOustDatabase().questionsDao().instertQuestions(entityQuestions);
    }

    /*  EntityQuestions  */

    /*  EntityResourseStrings  */

    static EntityResourseStrings getResourceStringModel(String languagePerfix) {
        EntityResourseStrings stringsModel = OustDBBuilder.getBuilder().getOustDatabase().resourseStringsModelDao().findResourseStringsModel(languagePerfix);
        return stringsModel;
    }

    static void addorUpdateResourceStringModel(final EntityResourseStrings resourseStringsModel) {
        OustDBBuilder.getBuilder().getOustDatabase().resourseStringsModelDao().insertResourseStringsModel(resourseStringsModel);
    }

    /*  EntityResourseStrings  */

    /* UserFeeds */

    public static void addorUpdateUserFeed(DTOUserFeeds.FeedList feed) {
        EntityUserFeeds entityUserFeeds = DTOReverseConverter.getUserFeedsModel(feed);
        OustDBBuilder.getBuilder().getOustDatabase().daoUserFeeds().insertEntityUserFeed(entityUserFeeds);
    }

    public static List<DTOUserFeeds.FeedList> getAllUserFeeds() {
        List<EntityUserFeeds> entityUserFeeds = OustDBBuilder.getBuilder().getOustDatabase().daoUserFeeds().getEntityAllUserFeeds();
        return DTOConverter.getAllUserFeeds(entityUserFeeds);
    }

    public static void deleteAllUserFeedsData() {
        OustDBBuilder.getBuilder().getOustDatabase().daoUserFeeds().deleteEntityUserFeeds();
    }

    /* UserFeeds */

    /*  CourseCardClass  */

    static void addorUpdateCourseCard(DTOCourseCard dtoCourseCard) {
        dtoCourseCard.setCourseCardID(OustPreferences.get("tanentid") + "" + dtoCourseCard.getCardId());
        EntityCourseCardClass courseCard = DTOReverseConverter.getCourseCard(dtoCourseCard);
        OustDBBuilder.getBuilder().getOustDatabase().courseCardClassDao().insertCourseCardClass(courseCard);
    }

    static void deleteCourseCard(long cardId) {
        OustDBBuilder.getBuilder().getOustDatabase().courseCardClassDao().deleteCourseCardClass(cardId);
    }

    static DTOCourseCard getCourseCardByCardId(long cardId) {
        EntityCourseCardClass courseCardClass = OustDBBuilder.getBuilder().getOustDatabase().courseCardClassDao().findCourseCardClass(cardId);
        return DTOConverter.getCourseCard(courseCardClass);
    }

    static List<EntityCourseCardClass> getCourseCard(long cardId) {
        List<EntityCourseCardClass> courseCardClasses = OustDBBuilder.getBuilder().getOustDatabase().courseCardClassDao().findCourseCardClassByCourseId(cardId);
        return courseCardClasses;
    }

    public static void deleteCourseCardClass(long cardId) {
        EntityCourseCardClass courseCardClass = null;

        String courseCardid = OustPreferences.get("tanentid") + "" + cardId;
        List<EntityCourseCardClass> courseCardClassList = getCourseCard(cardId);
        try {
            if (courseCardClassList.size() > 0) {
                for (int i = 0; i < courseCardClassList.size(); i++) {
                    courseCardClass = courseCardClassList.get(i);
                    if (courseCardClass != null) {
                        try {
                            List<EntityCourseCardMedia> courseCardMedia = new ArrayList<>();
                            courseCardMedia = courseCardClass.getCardMedia();
                            if (courseCardMedia != null && courseCardMedia.size() > 0) {
                                for (int j = 0; j < courseCardMedia.size(); j++) {
                                    if (courseCardMedia.get(i).getData() != null) {
                                        File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + courseCardMedia.get(i).getData());
                                        if (file.exists()) {
                                            Log.d(TAG, "deleteCourseCardClass2: deletingfile:" + "oustlearn_" + courseCardMedia.get(i).getData());
                                            file.delete();
                                        }
                                    }
                                    if (courseCardMedia.get(i).getGumletVideoUrl() != null) {
                                        File file = new File(OustSdkApplication.getContext().getFilesDir(), courseCardMedia.get(i).getGumletVideoUrl());
                                        if (file.exists()) {
                                            Log.d(TAG, "deleteCourseCardClass2: deletingfile:" + courseCardMedia.get(i).getGumletVideoUrl());
                                            file.delete();
                                        }
                                    }
                                }
                            }

                            if (courseCardClass.getQuestionCategory() != null && courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.VIDEO_OVERLAY)) {
                                Log.d(TAG, "deleteCourseCardClass2: Videooverlay pending");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                    }
                    deleteCourseCard(courseCardClass.getCardId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
    /*  CourseCardClass  */

    /*  EntityUserCourseData  */

    public static DTOUserCourseData getScoreById(long id) {
        EntityUserCourseData userCourseData = OustDBBuilder.getBuilder().getOustDatabase().userCourseDataDao().findUserCourseDataById(id);
        return DTOConverter.getUserCourseData(userCourseData);
    }

    public static void addorUpdateScoreDataClass(DTOUserCourseData dtoUserCourseData) {
        OustDBBuilder.getBuilder().getOustDatabase().userCourseDataDao().insertUserCourseData(DTOReverseConverter.getUserCourseData(dtoUserCourseData));
    }

    static void deleteUserCourseData(long id) {
        OustDBBuilder.getBuilder().getOustDatabase().userCourseDataDao().deleteUserCourseData(id);
    }

    static void setCurrentCardNumber(DTOUserCourseData dtoUserCourseData, int level, int cardNo) {
        try {
            dtoUserCourseData.getUserLevelDataList().get(level).setCurrentCardNo((cardNo));
            OustDBBuilder.getBuilder().getOustDatabase().userCourseDataDao().insertUserCourseData(DTOReverseConverter.getUserCourseData(dtoUserCourseData));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    /*  EntityUserCourseData  */

    /*  EntityLevelCardCourseID  */

    static void getAllCourseInLevel(long levelId) {

        List<EntityLevelCardCourseID> levelCourseCardMap = getLevelCourseCardMap(levelId);
        if (levelCourseCardMap.size() > 0) {
            for (int i = 0; i < levelCourseCardMap.size(); i++) {
                Log.d(TAG, "deleteAllCourseInLevel: levelID:" + levelCourseCardMap.get(i).getLevelId());
                Log.d(TAG, "deleteAllCourseInLevel: CardID:" + levelCourseCardMap.get(i).getCardId());
                Log.d(TAG, "deleteAllCourseInLevel: CourseID:" + levelCourseCardMap.get(i).getCourseId());
                deleteCourseCardClass(levelCourseCardMap.get(i).getCardId());
                deleteUserLevelData(levelCourseCardMap.get(i).getCourseId(), levelCourseCardMap.get(i).getLevelId());
            }
        }
    }

    private static void deleteUserLevelData(long courseId, long levelId) {
        try {
            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String s1 = "" + activeUser.getStudentKey() + "" + courseId;
//            int courseUniqNo = Integer.parseInt(s1);
            long courseUniqNo = Long.parseLong(s1);

            DTOUserCourseData dtoUserCourseData = getScoreById(courseUniqNo);
            List<DTOUserLevelData> userLevelDataList = dtoUserCourseData.getUserLevelDataList();
            for (DTOUserLevelData userLevelData : userLevelDataList) {
                if (levelId == userLevelData.getLevelId()) {
//                    userLevelDataList.remove(userLevelData);
                    userLevelData.setCompletePercentage(0);
                    break;
                }
            }
            dtoUserCourseData.setUserLevelDataList(userLevelDataList);
            addorUpdateScoreDataClass(dtoUserCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    static void addLevelCourseCardMap(final EntityLevelCardCourseID levelCardCourseIDModel) {
        OustDBBuilder.getBuilder().getOustDatabase().levelCardCourseIDModelDao().insertLevelCardCourseIDModel(levelCardCourseIDModel);
    }

    static List<EntityLevelCardCourseID> getLevelCourseCardMap(final long levelId) {
        List<EntityLevelCardCourseID> levelCardCourseIDModels = OustDBBuilder.getBuilder().getOustDatabase().levelCardCourseIDModelDao().getAllLevelCardCourseIDModel(levelId);
        return levelCardCourseIDModels;
    }

    static void deleteLevelCourseCardMap(final int levelId) {
        OustDBBuilder.getBuilder().getOustDatabase().levelCardCourseIDModelDao().deleteLevelCardCourseIDModel(levelId);
    }

    static boolean checkMapTableExist(final long levelId) {
        List<EntityLevelCardCourseID> levelCardCourseIDModels = OustDBBuilder.getBuilder().getOustDatabase().levelCardCourseIDModelDao().getAllLevelCardCourseIDModel(levelId);
        if (levelCardCourseIDModels.size() > 0) {
            return true;
        } else
            return false;
    }

    /*  EntityLevelCardCourseID  */

    // handled here for card edited
    static void addLevelCourseCardUpdateTimeMap(final CourseLevelClass courseLevelClassList, String studentKey) {
        EntityLevelCardCourseIDUpdateTime entityLevelCardCourseIDUpdateTime = new EntityLevelCardCourseIDUpdateTime();
        entityLevelCardCourseIDUpdateTime.setCourseId(courseLevelClassList.getLpId());
        entityLevelCardCourseIDUpdateTime.setLevelId((int) courseLevelClassList.getLevelId());
        entityLevelCardCourseIDUpdateTime.setStudentKey(studentKey);
        entityLevelCardCourseIDUpdateTime.setUpdateTime(OustSdkTools.convertToLong(courseLevelClassList.getRefreshTimeStamp()));
        OustDBBuilder.getBuilder().getOustDatabase().levelUpdateTime().insertLevelCardCourseUpdateTimeIDModel(entityLevelCardCourseIDUpdateTime);
    }

    static List<EntityLevelCardCourseIDUpdateTime> getLevelCourseUpdateTimeMap(final int levelId) {
        return OustDBBuilder.getBuilder().getOustDatabase().levelUpdateTime().getAllLevelCardCourseUpdateTimeIDModel(levelId);
    }

    /*  CplMediaData  */

    static void addorUpdateCplMediaDataModel(EntityCplMedia cplMediaData) {
        OustDBBuilder.getBuilder().getOustDatabase().cplMediaDao().insertCplMedia(cplMediaData);
    }

    static void updateCplMediaFile(String id, String fileName, String folderpath) {
        EntityCplMedia cplMediaById = OustDBBuilder.getBuilder().getOustDatabase().cplMediaDao().getCplMediaById(id);
        cplMediaById.setFileName(fileName);
        cplMediaById.setFolderPath(folderpath);
        addorUpdateCplMediaDataModel(cplMediaById);
    }

    static ArrayList<EntityCplMedia> getCplMediaFiles(String cplId) {
        String tnt_cplId = OustPreferences.get("tanentid") + "" + cplId;
        List<EntityCplMedia> cplMediaList = OustDBBuilder.getBuilder().getOustDatabase().cplMediaDao().getCplMedias(tnt_cplId);

        return (ArrayList<EntityCplMedia>) cplMediaList;
    }

    /*  CplMediaData  */


    public static void addDownloadedOrNot(int courseId, boolean downloadedOrNot, int percentage) {
        EntityDownloadedOrNot entityDownloadedOrNot = new EntityDownloadedOrNot();
        entityDownloadedOrNot.setCourseId(courseId);
        entityDownloadedOrNot.setPercentage(percentage);
        entityDownloadedOrNot.setDownloadedOrNot(downloadedOrNot);
        OustDBBuilder.getBuilder().getOustDatabase().checkDownloadOrNot().insertDownloadedOrNot(entityDownloadedOrNot);
    }

    public static ArrayList<EntityDownloadedOrNot> getDownloadedOrNot() {
        List<EntityDownloadedOrNot> entityDownloadedOrNot = OustDBBuilder.getBuilder().getOustDatabase().checkDownloadOrNot().getDownloadedOrNotS();
        return (ArrayList<EntityDownloadedOrNot>) entityDownloadedOrNot;
    }

    public static void downloadedOrNot(long courseId) {
        OustDBBuilder.getBuilder().getOustDatabase().checkDownloadOrNot().deleteDownloadedOrNot(courseId);
    }

    /*  EntityCplMediaUpdateData  */

    static void addorUpdateCplMediaModel(String cplId, long updateTime) {
        EntityCplMediaUpdateData cplMediaUpdateData = new EntityCplMediaUpdateData();
        cplMediaUpdateData.setCplId(OustPreferences.get("tanentid") + cplId);
        cplMediaUpdateData.setUpdateTime(updateTime);
        OustDBBuilder.getBuilder().getOustDatabase().cplMediaUpdateDataDao().addOrUpdateCplMediaUpdateData(cplMediaUpdateData);
    }

    static DTOCplMediaUpdateData getCplUpdateModel(String cplId) {
        String tntCplId = OustPreferences.get("tanentid") + "" + cplId;
        EntityCplMediaUpdateData cplUpdateModel = OustDBBuilder.getBuilder().getOustDatabase().cplMediaUpdateDataDao().getCplUpdateModel(tntCplId);
        return DTOConverter.getCplMediaUpdateData(cplUpdateModel);
    }

    /*  EntityCplMediaUpdateData  */


    /*  EntityCplCompletedModel  */

    static DTOCplCompletedModel getCPLDataById(long id) {
        EntityCplCompletedModel cplCompletedModel = OustDBBuilder.getBuilder().getOustDatabase().cplCompletedModelDao().getCPLDataById(id);
        return DTOConverter.getCplCompletedModel(cplCompletedModel);
    }

    public static void addorUpdateCPLData(final DTOCplCompletedModel dtoCplCompletedModel) {
        EntityCplCompletedModel cplCompletedModel = DTOReverseConverter.getCplCompletedModel(dtoCplCompletedModel);
        OustDBBuilder.getBuilder().getOustDatabase().cplCompletedModelDao().insertCplCompletedModel(cplCompletedModel);
    }

    /*  EntityCplCompletedModel  */


    /*  EntityFeedBackModel  */

    public static void addFeedToRealm(DTOFeedBackModel dtoFeedBackModel) {
        OustDBBuilder.getBuilder().getOustDatabase().feedBackModelDao().insertFeedBackModelDao(DTOReverseConverter.getFeedBackModel(dtoFeedBackModel));
    }

    public static List<DTOFeedBackModel> getAllFeeds() {
        List<EntityFeedBackModel> entityFeedBackModelList = OustDBBuilder.getBuilder().getOustDatabase().feedBackModelDao().getAllFeedBack();
        return DTOConverter.getFeedBackList(entityFeedBackModelList);
    }

    static void deleteFeeds(DTOFeedBackModel entityFeedBackModel) {
        EntityFeedBackModel feedBackModel = DTOReverseConverter.getFeedBackModel(entityFeedBackModel);
        OustDBBuilder.getBuilder().getOustDatabase().feedBackModelDao().deleteFeedBack(feedBackModel.getId());
    }

    /*  EntityFeedBackModel  */


    /*  EntityMapDataModel  */

    static void addorUpdateMapDataModel(final DTOMapDataModel dtoMapDataModel) {
        OustDBBuilder.getBuilder().getOustDatabase().mapDataModelDao().addOrUpdateMapDataModel(DTOReverseConverter.getMapDataModel(dtoMapDataModel));
    }

    /*  EntityMapDataModel  */


    /*  EntityLearningDiary  */

    static void addorUpdateLearningDiary(DTOLearningDiary diaryDetailsModel, List<DTODiaryDetailsModel> diaryDetailsModelList) {
        diaryDetailsModel.set_newsList(diaryDetailsModelList);
        EntityLearningDiary learningDiary = DTOReverseConverter.getLearningDiary(diaryDetailsModel);
        OustDBBuilder.getBuilder().getOustDatabase().learningDiaryDao().insertLearningDiary(learningDiary);
    }

    static DTOLearningDiary getLearningDiaryById(final String id) {
        EntityLearningDiary learningDiary = OustDBBuilder.getBuilder().getOustDatabase().learningDiaryDao().getLearningDiary(id);
        return DTOConverter.getlearningDiary(learningDiary);
    }

    /*  EntityLearningDiary  */


    /*  EntityPostViewData  */


    static void addorUpdateNBPostDataModel(PostViewData postViewData) {
        /*Realm realm = Realm.getDefaultInstance();
        try {
            if (postViewData.getType().equals("like")) {
                RealmQuery<RealmPostViewData> query = realm.where(RealmPostViewData.class).equalTo("nbId", postViewData.getNbId()).equalTo("postid", postViewData.getPostid()).equalTo("type", "like");
                RealmResults<RealmPostViewData> results = query.findAll();
                realm.beginTransaction();
                results.deleteAllFromRealm();
                realm.commitTransaction();
            }
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(postViewData);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            realm.close();
        }*/
    }

    static List<PostViewData> getTypeBasedPostViewData(String type) {
        ArrayList<PostViewData> postViewDataList = new ArrayList<>();

        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                PostViewData postViewData = gson.fromJson(request, PostViewData.class);
                if (postViewData != null && postViewData.getType().equalsIgnoreCase(type)) {
                    postViewDataList.add(postViewData);
                }
            }
        }
        return postViewDataList;
    }


    static List<PostViewData> getPostViewDataById(long nbId, long postId) {
        ArrayList<PostViewData> postViewDataList = new ArrayList<>();
        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                PostViewData postViewData = gson.fromJson(request, PostViewData.class);
                if (postViewData.getPostid() == postId && postViewData.getNbId() == nbId) {
                    postViewDataList.add(postViewData);
                }
            }
        }

        return postViewDataList;
    }


    static List<PostViewData> getPostViewReplyDataByPostId(long postId, long commentId) {
        ArrayList<PostViewData> postViewDataList = new ArrayList<>();
        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                PostViewData postViewData = gson.fromJson(request, PostViewData.class);
                if (postViewData != null && postViewData.getPostid() == postId && postViewData.isPostTypeReply() && postViewData.getNbReplyData() != null) {
                    if (postViewData.getNbReplyData().getCommentId() == commentId) {
                        postViewDataList.add(postViewData);
                    }
                }
            }
        }
        return postViewDataList;
    }


    static List<PostViewData> getPostViewDeletedReplyDataByPostId(long postId, long commentId) {
        ArrayList<PostViewData> postViewDataList = new ArrayList<>();
        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                PostViewData postViewData = gson.fromJson(request, PostViewData.class);
                if (postViewData != null && postViewData.getPostid() == postId && postViewData.isPostTypeReplyDelete() && postViewData.getNbReplyData() != null) {
                    if (postViewData.getNbReplyData().getCommentId() == commentId) {
                        postViewDataList.add(postViewData);
                    }
                }
            }
        }
        return postViewDataList;
    }

    //

    static void newAddorUpdateNBPostDataModel(NewPostViewData postViewData) {
        /*Realm realm = Realm.getDefaultInstance();
        try {
            if (postViewData.getType().equals("like")) {
                RealmQuery<RealmPostViewData> query = realm.where(RealmPostViewData.class).equalTo("nbId", postViewData.getNbId()).equalTo("postid", postViewData.getPostid()).equalTo("type", "like");
                RealmResults<RealmPostViewData> results = query.findAll();
                realm.beginTransaction();
                results.deleteAllFromRealm();
                realm.commitTransaction();
            }
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(postViewData);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            realm.close();
        }*/
    }

    static List<NewPostViewData> newGetTypeBasedPostViewData(String type) {
        ArrayList<NewPostViewData> postViewDataList = new ArrayList<>();

        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                NewPostViewData postViewData = gson.fromJson(request, NewPostViewData.class);
                if (postViewData != null && postViewData.getType().equalsIgnoreCase(type)) {
                    postViewDataList.add(postViewData);
                }
            }
        }
        return postViewDataList;
    }


    static List<NewPostViewData> newGetPostViewDataById(long nbId, long postId) {
        ArrayList<NewPostViewData> postViewDataList = new ArrayList<>();
        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                NewPostViewData postViewData = gson.fromJson(request, NewPostViewData.class);
                if (postViewData.getPostid() == postId && postViewData.getNbId() == nbId) {
                    postViewDataList.add(postViewData);
                }
            }
        }

        return postViewDataList;
    }


    static List<NewPostViewData> newGetPostViewReplyDataByPostId(long postId, long commentId) {
        ArrayList<NewPostViewData> postViewDataList = new ArrayList<>();
        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                NewPostViewData postViewData = gson.fromJson(request, NewPostViewData.class);
                if (postViewData != null && postViewData.getPostid() == postId && postViewData.isPostTypeReply() && postViewData.getNbReplyData() != null) {
                    if (postViewData.getNbReplyData().getCommentId() == commentId) {
                        postViewDataList.add(postViewData);
                    }
                }
            }
        }
        return postViewDataList;
    }


    static List<NewPostViewData> newGetPostViewDeletedReplyDataByPostId(long postId, long commentId) {
        ArrayList<NewPostViewData> postViewDataList = new ArrayList<>();
        List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedNBRequests");
        Gson gson = new Gson();
        if (requests != null && requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                String request = requests.get(i);
                NewPostViewData postViewData = gson.fromJson(request, NewPostViewData.class);
                if (postViewData != null && postViewData.getPostid() == postId && postViewData.isPostTypeReplyDelete() && postViewData.getNbReplyData() != null) {
                    if (postViewData.getNbReplyData().getCommentId() == commentId) {
                        postViewDataList.add(postViewData);
                    }
                }
            }
        }
        return postViewDataList;
    }

    /*  EntityPostViewData  */


    /*  EntityResourceData  */

    static boolean isResourceDataModel(String fileName) {
        EntityResourceData entityResourceData = OustDBBuilder.getBuilder().getOustDatabase().resourceDataModelDao().getResourceDataModel(fileName);
        return entityResourceData.getFilename().length() > 0;
    }

    static DTOResourceData getResourceDataModel(String filename) {
        EntityResourceData entityResourceData = OustDBBuilder.getBuilder().getOustDatabase().resourceDataModelDao().getResourceDataModel(filename);
        DTOResourceData resourceDataModel = DTOConverter.getResourceDataModel(entityResourceData);
        return resourceDataModel;
    }

    static void addorUpdateResourceDataModel(final DTOResourceData resourceDataModel) {
        EntityResourceData entityResourceData = DTOReverseConverter.getResourceDataModel(resourceDataModel);
        OustDBBuilder.getBuilder().getOustDatabase().resourceDataModelDao().addorUpdateResourceDataModel(entityResourceData);
    }

    /*  EntityResourceData  */

    /*  EntityNewFeed  */

    static List<DTONewFeed> getNewFeeds() {
        List<EntityNewFeed> entityNewFeeds = OustDBBuilder.getBuilder().getOustDatabase().newFeedDao().getEntityNewFeeds();
        return DTOConverter.getNewFeed(entityNewFeeds);
    }

    static void addorUpdateNewFeed(List<DTONewFeed> dtoNewFeeds) {
        OustDBBuilder.getBuilder().getOustDatabase().newFeedDao().deleteEntityNewFeeds();
        DaoNewFeed daoNewFeed = OustDBBuilder.getBuilder().getOustDatabase().newFeedDao();
        for (DTONewFeed dtoNewFeed : dtoNewFeeds) {
            daoNewFeed.insertEntityNewFeed(DTOReverseConverter.getNewFeed(dtoNewFeed));
        }
    }

    /*  EntityNewFeed  */

    /*  EntityCatalogueItemData  */

    static List<CatalogueItemData> getEntityCatalogueItemDatas(long catalogueId) {
        List<EntityCatalogueItemData> entityCatalogueItemDataList = OustDBBuilder.getBuilder().getOustDatabase().catalogueItemDataDao().getEntityCatalogueItemDatas(catalogueId);
        return DTOConverter.getCatalogueItemDataList(entityCatalogueItemDataList);
    }

    static void insertCatalogueItemData(List<CatalogueItemData> catalogueItemDatas, long catalogueId) {

        List<EntityCatalogueItemData> catalogueItemDataList = DTOReverseConverter.getCatalogueItemDataList(catalogueItemDatas);
        for (int i = 0; i < catalogueItemDataList.size(); i++) {
            catalogueItemDataList.get(i).setParentId(catalogueId);
            OustDBBuilder.getBuilder().getOustDatabase().catalogueItemDataDao().insertCatalogueItemData(catalogueItemDataList.get(i));
        }
    }

    /*  EntityCatalogueItemData  */

    /*  notification data  */

    public static void addNotificationData(List<NotificationResponse> notificationResponses) {
        try {
            List<EntityNotificationData> notificationData = DTOReverseConverter.getNotificationItemDataList(notificationResponses);
            for (int i = 0; i < notificationData.size(); i++) {
                OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().insertNotificationItemData(notificationData.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static List<NotificationResponse> getNotifications(String studentKey) {
        List<EntityNotificationData> notificationData = OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().getNotificationData(studentKey);
        return DTOConverter.getNotificationData(notificationData);
    }

    public static boolean getNotificationById(String keyValue) {
        boolean dataExistOrNot = false;
        try {
            EntityNotificationData entityResourceData = OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().getNotificationById(keyValue);
            if (entityResourceData != null) {
                dataExistOrNot = entityResourceData.getContentId().length() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return dataExistOrNot;
    }

    public static boolean getNotificationByContentId(String contentId) {
        boolean dataExistOrNot = false;
        try {
            EntityNotificationData entityResourceData =
                    OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().getNotificationByContentId(contentId);
            if (entityResourceData != null && entityResourceData.getContentId() != null && !entityResourceData.getContentId().isEmpty()) {
                dataExistOrNot = entityResourceData.getContentId().length() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return dataExistOrNot;
    }

    public static int updateReadNotification(String isRead, String contentId) {
        int dataUpdateOrNot = 0;
        try {
            dataUpdateOrNot = OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().upDateNotificationReadStatus(isRead, contentId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return dataUpdateOrNot;
    }

    public static int updateReadNotificationByContentId(String isRead, String keyId) {
        int dataUpdateOrNot = 0;
        try {
            dataUpdateOrNot = OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().upDateReadFireBaseNotificationStatus(isRead, keyId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return dataUpdateOrNot;
    }

    public static int deleteOfflineNotifications(String CotentId) {
        int dataUpdateOrNot = 0;
        try {
            dataUpdateOrNot = OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().deleteOfflineNotifications(CotentId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return dataUpdateOrNot;
    }

    public static void deleteNotificationTable() {
        OustDBBuilder.getBuilder().getOustDatabase().notificationItemDataDao().deleteEntityNotificationData();
    }
    /*  notification data  */

}