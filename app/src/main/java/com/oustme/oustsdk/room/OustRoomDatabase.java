package com.oustme.oustsdk.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.oustme.oustsdk.room.dto.EntityNotificationData;

@Database(entities = {EntityCourseCardClass.class, EntityCourseCardMedia.class, EntityQuestions.class
        , EntityCourseSolutionCard.class, EntityCardColorScheme.class, EntityReadMore.class
        , EntityResourceCollection.class, EntityUserCourseData.class, EntityUserLevelData.class
        , EntityUserCardData.class, EntityLevelCardCourseID.class, EntityResourseStrings.class
        , EntityResourceData.class, EntityFeedBackModel.class, EntityCplMedia.class
        , EntityCplMediaUpdateData.class, EntityCplCompletedModel.class, EntityMapDataModel.class
        , EntityLearningDiary.class, EntityPostViewData.class, EntityNewFeed.class, EntityCatalogueItemData.class
        , EntityNotificationData.class, EntityLevelCardCourseIDUpdateTime.class, EntityDownloadedOrNot.class, EntityUserFeeds.class},
        version = 138, exportSchema = false)
abstract class OustRoomDatabase extends RoomDatabase {

    abstract DaoCourseCardClass courseCardClassDao();

    abstract DaoResourceCollectionModel resourceCollectionModelDao();

    abstract DaoUserCourseData userCourseDataDao();

    abstract DaoLevelCardCourseIDModel levelCardCourseIDModelDao();

    abstract DaoQuestions questionsDao();

    abstract DaoResourseStringsModel resourseStringsModelDao();

    abstract DaoResourceDataModel resourceDataModelDao();

    abstract DaoFeedBackModel feedBackModelDao();

    abstract DaoCplMedia cplMediaDao();

    abstract DaoCplMediaUpdateData cplMediaUpdateDataDao();

    abstract DaoCplCompletedModel cplCompletedModelDao();

    abstract DaoMapDataModel mapDataModelDao();

    abstract DaoLearningDiary learningDiaryDao();

    abstract DaoPostViewData postViewDataDao();

    abstract DaoNewFeed newFeedDao();

    abstract DaoCatalogueItemData catalogueItemDataDao();

    abstract DaoNotificationItemData notificationItemDataDao();

    abstract DaoLevelUpdateTime levelUpdateTime();

    abstract DaoCheckDownloadOrNot checkDownloadOrNot();

    abstract DaoUserFeeds daoUserFeeds();
}