package com.oustme.oustsdk.tools.appconstants;

import android.os.Environment;

import com.oustme.oustsdk.tools.OustPreferences;

/**
 * Created by admin on 12/10/18.
 */

public abstract class AppConstants {

    public static class MediaURLConstants {
        public static String BUCKET_NAME = "img.oustme.com";
        public static String MEDIA_SOURCE_BASE_URL = "https://s3-us-west-1.amazonaws.com/img.oustme.com/";
        public static String MEDIA_SOURCE_AUDIO = "mediaUploadCard/Audio/";
        public static String MEDIA_SOURCE_IMAGE = "mediaUploadCard/Image/";
        public static String MEDIA_SOURCE_VIDEO = "mediaUploadCard/Video/";
        public static String CLOUD_FRONT_BASE_PATH = "https://di5jfel2ggs8k.cloudfront.net/";
        public static String CLOUD_FRONT_BASE_HTTPS = "https://di5jfel2ggs8k.cloudfront.net/";
        public static String BUCKET_REGION = "us-west-1";


        public static void init() {
            if (OustPreferences.get(StringConstants.S3_BASE_END) != null && !OustPreferences.get(StringConstants.S3_BASE_END).isEmpty()) {
                MEDIA_SOURCE_BASE_URL = OustPreferences.get(StringConstants.S3_BASE_END);
            }
            if (OustPreferences.get(AppConstants.StringConstants.IMG_BKT_CDN) != null && !OustPreferences.get(AppConstants.StringConstants.IMG_BKT_CDN).isEmpty()) {
                CLOUD_FRONT_BASE_PATH = OustPreferences.get(AppConstants.StringConstants.IMG_BKT_CDN);
            }
            if (OustPreferences.get(StringConstants.HTTP_IMG_BKT_CDN) != null && !OustPreferences.get(StringConstants.HTTP_IMG_BKT_CDN).isEmpty()) {
                CLOUD_FRONT_BASE_HTTPS = OustPreferences.get(StringConstants.HTTP_IMG_BKT_CDN);
            }
            if (OustPreferences.get(StringConstants.S3_BKT_REGION) != null && !OustPreferences.get(StringConstants.S3_BKT_REGION).isEmpty()) {
                BUCKET_REGION = OustPreferences.get(StringConstants.S3_BKT_REGION);
            }
            if (OustPreferences.get(AppConstants.StringConstants.IMG_BKT_NAME) != null && !OustPreferences.get(AppConstants.StringConstants.IMG_BKT_NAME).isEmpty()) {
                BUCKET_NAME = OustPreferences.get(AppConstants.StringConstants.IMG_BKT_NAME);
            }
        }
    }

    public static class IntegerConstants {
        public static int MAX_WORD_COUNT = 200;
        public static int ONE_HUNDRED_MILLI_SECONDS = 100;
        public static int ONE_FIFTY_MILLI_SECONDS = 150;
        public static int TWO_HUNDRED_MILLI_SECONDS = 200;
        public static int THREE_HUNDRED_MILLI_SECONDS = 300;
        public static int FOUR_HUNDRED_MILLI_SECONDS = 400;
        public static int FIVE_HUNDRED_MILLI_SECONDS = 500;
        public static int SIX_HUNDRED_MILLI_SECONDS = 600;
        public static int SEVEN_HUNDRED_MILLI_SECONDS = 700;
        public static int EIGHT_HUNDRED_MILLI_SECONDS = 800;
        public static int NINE_HUNDRED_MILLI_SECONDS = 900;
        public static int ONE_SECOND = 1000;
        public static int TWO_SECOND = 2000;
        public static int THREE_SECOND = 3000;
        public static int FOUR_SECOND = 4000;
        public static int FIVE_SECOND = 5000;
        public static int TEN_SECOND = 10000;
        public static int FIFTEEN_SECOND = 15000;

        //Error Code
        public static int ASSESSMENT_ALREADY_COMPLETED = 3314;
        public static int ASSESSMENT_ALREADY_COMPLETED_PLAY = 3312;
    }

    public static class StringConstants {
        public static final String FIREBASE_KEY = "QUl6YVN5RGJNZE1ndllrN2JibVhXMVZjOG5hWHBpRElUQ2I4RWJR";
        public static final String SURVEY_ATTACHED = "surveyMapped";
        public static final String SURVEY_ID = "mappedSurveyId";
        public static final String HOST_APP_NAME = "hostAppName";
        public static final String HOST_APP_LINK_DISABLED = "hostAppLinkDisabled";
        public static final String IS_APP_TUTORIAL_SHOWN = "IS_APP_TUTORIAL_SHOWN";
        public static final String IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED = "IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED";
        public static final String RESET_PASSWORD_LINK = "https://webapp2.oustme.com/#!/resetPassword";
        public static String CLOUD_FRONT_BASE_PATH = MediaURLConstants.CLOUD_FRONT_BASE_PATH;
        public static String CLOUD_FRONT_BASE_FEED = MediaURLConstants.CLOUD_FRONT_BASE_PATH + "feed/";
        public static String CLOUD_FRONT_VIDEO_BASE = MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/video/";
        public static String S3_BUCKET_IMAGE_URL = MediaURLConstants.MEDIA_SOURCE_BASE_URL + MediaURLConstants.MEDIA_SOURCE_IMAGE;
        public static final String QUERY = "QUERY";
        public static final String LEADER_BOARD_DATA = "Data";
        public static final String STUDE_KEY = "student_key";
        public static final String CATALOGUE_ID = "catalogueId";
        public static final String CATALOG_NAME = "catalogueName";
        public static final String CAT_BANNER = "categoryBanner";
        public static final String COMMENT_ABLE = "commentable";
        public static final String LIKE_ABLE = "likeable";
        public static final String SHOW_MULTIPLE_CPL = "enableMultipleCPL";
        public static final String READ_MULTILINGUAL_CPL_FROM_FIREBASE = "readMultilingualCplFromFirebase";
        public static final String MAX_DATE_RANGE = "MaxDateRangeForWorkDiary";
        public static final String FREEZE_WORK_DIARY_START_DATE = "FreezeWorkDiaryStartDate";
        public static final String CPL_LANG_CHANGE = "change_cpl_lang_text";
        public static final String IS_PREV_DATE_ALLOWED = "workdiaryAllowBackdatedEntries";
        public static final String NO_OF_BACK_DAYS_ALLOWED = "noOfBackDaysAllowed";
        public static final String LAST_DATE_OF_WORK_DIARY = "lastDateOfWorkDiaryEntryMade";
        public static final String IS_FUTURE_DATE_ALLOWED = "workdiaryAllowFuturedatedEntries";
        public static final String ENABLE_RD_WD = "enableRegisteredDateForWD";
        public static final String NOTICE_BOARD_FOLDER = "noticeBoard/";
        public static final String S3_IMAGE_FOLDER = "mediaUploadCard/Image/";
        public static final String EXP_HEADER_LIST = "exp_header_list";
        public static final String EXP_CHILD_LIST = "exp_child_list";
        public static final String SHOW_CPL_LANGUAGE_IN_NAVIGATION = "showCplLanguageInNavigation";
        public static final String BUNDLE_NAME = "bundle_name";
        public static final String SHOW_FFC_ON_LANDING_PAGE = "showFFCOnLandingPageBottomBanner";
        public static final String SHOW_CPL_ON_LANDING_PAGE = "showPlaylistOnLandingPageBottomBanner";
        public static final String SHOW_TO_DO_ON_LANDING_PAGE = "showToDoListOnLandingPageBottomBanner";
        public static final String FFC_BANNER_URL = "ffc_banner_url";
        public static final String CPL_OUST_COINS = "oust_coins";
        public static final String CPL_ENROLLED_USERS = "cpl_enrolled_users";
        public static final String FFC_START_TIME = "ffc_start_time";
        public static final String FFC_ENROLLED_USERS = "ffc_enrolled_users";
        public static final String FFC_DATA = "fastestFingerContestData";
        public static final String TOOL_BAR_COLOR_CODE = "toolbarColorCode";
        public static final String ASSESSMENT_PATH = "assessment/assessment";
        public static final String COURSE_PATH = "course/course";
        public static final String TENANT_ID = "tanentid";
        public static final String SELECTED_LANG_ID = "SELECTED_LANG_ID";
        public static final String SELECTED_LANGUAGE = "SELECTED_LANGUAGE";
        public static final String SELECTED_CPL_ID = "Selected_Cpl_Id";
        public static final String ENGLISH = "english";
        public static final String IS_LANG_SELECTED = "IS_LANG_SELECTED";
        public static final String DISABLE_TODO_ONCLICK = "enableTodoOnClick";
        public static final String IS_RATED_ON_PS = "IS_RATED_ON_PS";
        public static final String NO_TIMES_OPENED = "NO_TIMES_OPENED";
        public static final String DISABLE_LEARNING_DIARY = "learningDiaryDisabled";
        public static final String TEAM_ANALYTICS = "teamAnalytics";
        public static final String LEARNING_DIARY_DEFAULT_BANNER = "defaultLDBannerImg";
        public static String S3_BUCKET_NAME = "img.oustme.com";
        public static String CLOUD_FRONT_HLS_BASE_URL = "https://di99dr7t1ytrn.cloudfront.net/";
        //public static String LOCAL_FILE_DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files";
        public static String LOCAL_FILE_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oust";
        public static String FILE_URL = "fileURL";
        public static String FILE_NAME = "fileName";
        public static String FILE_DESTN = "fileDestn";
        public static String IS_OUST_LEARN = "isOustLearn";
        public static String IS_VIDEO = "isVideo";

        public static String FEED_COMMENT_DISABLE = "commentFeed";
        public static String isStopWatchEnable = "isStopWatchEnable";
        public static String SHOW_CPL_COURSE_TAB = "showCPLInToDoList";
        public static String SHOW_MY_TASK = "isMyTask";
        public static String IS_NEW_LEARNING_MAP_MODE = "isNewLearningMapMode";
        public static String SHOW_SURVEY_NEW_UI = "showNewSurveyUI";
        public static String SHOW_CARD_NEW_UI = "showNewCardUi";
        public static String SHOW_CATALOGUE_NEW_UI = "isNewCatalogue";
        public static String FEED_LIKE_DISABLE = "likeFeed";
        public static String FEED_SHARE_DISABLE = "shareFeed";
        public static String KATEX_DELIMITER = "$$";
        public static String USER_DATA = "userdata";
        public static String SYSTEM_NODE = "system";
        public static String HELP_SUPPORT_NODE = "HelpSupportSection";
        public static String FEED_BACK_NAME = "feedbackName";
        public static String IS_CITY_SELECTED = "IS_CITY_SELECTED";
        public static String SELECTED_CITY_ID = "SELECTED_CITY_ID";
        public static String SELECTED_CITY_NAME = "SELECTED_CITY_NAME";
        public static String SELECTED_CITY_GROUP = "SELECTED_CITY_GROUP";
        public static String IS_COURSE_LB = "isCourseLB";
        public static String OPEN_WELCOME_POPUP = "welcomePopUp";
        public static String SPECIAL_FEED_ID = "specialFeed";
        public static String BASE_URL_FROM_API = "baserURL_from_API";
        public static String SIGN_URL_BASE = "https://applicationlb-test.oustme.com/rest/services/signin/verifyTenant/{orgId}?devicePlatformName=Android";
        public static String S3_BASE_END = "s3_base_end";
        public static String HTTP_IMG_BKT_CDN = "http_img_bucket_cdn";
        public static String IMG_BKT_CDN = "img_bucket_cdn";
        public static String IMG_BKT_NAME = "img_bucket_name";
        public static String S3_BKT_REGION = "s3_Bucket_Region";
        public static String IS_FEED_CLICKED = "isClicked";
        public static String IS_FEED_VIEWED = "isFeedViewed";//isFeedViewed
        public static String FEED_COINS_ADDED = "feedCoinsAdded";//isFeedViewed

        public static String SHOW_USER_OVERALL_CREDITS = "showUserOverallCredits";
        public static String SHOW_LB_USER_LOCATION = "showLeaderboardUserLocation";
        public static String SHOW_NEW_ASSESSMENT_UI = "showNewAssessmentUI";
        public static String DEFAULT_PROFILE_PIC = "https://di5jfel2ggs8k.cloudfront.net/aimages/avatar-placeholder-male_v2.png";
        public static String TenantLogo = "TenantLogo";
        public static String CUSTOM_COURSE_BRANDING = "customizeCourseBranding";
        public static String COURSE_FINISH_ICON = "courseFinishIconImage";
        public static String COURSE_START_ICON = "courseStartIconImage";
        public static String COURSE_LEVEL_INDICATOR_ICON = "coursePinImage";
        public static String COURSE_UNLOCK_COLOR = "courseLockLevelColor";
        public static String COURSE_LOCK_COLOR = "courseUnlockLevelColor";
        public static String RM_COURSE_LOCK_COLOR = "rmCourseLockColor";
        public static String RM_USER_STATUS_COLOR = "rmUserStatusCheckColor";
        public static String COURSE_PIN_COLOR = "coursePinImageColor";
        public static String CURRENT_LEVEL_COLOR = "courseCurrentLevelColor";
        public static String COURSE_LEVEL_AUDIO_PATH = "courseLevelCompletionAudioPath";
        public static String COURSE_COMPLETION_AUDIO_PATH = "courseCompletionAudioPath";
        public static String SPLASH_AUDIO = "splashAudio";
        public static String THEME_SOUND = "themeSound";

        public static String SHOW_PAYOUT_AT_CITY_SELECTION = "showPayoutAtCitySelection";
        public static String SCORE_DISPLAY_SECOND_TIME = "displayScoreSecondTime";
        public static String SHOW_FAQ = "showFAQ";
        public static String URL_FAQ = "urlOfFAQ";
        public static String URL_TEAM_ANALYTICS = "urlOfTeamAnalytics";
        public static String TEAM_ANALYTICS_URL = "openTeamAnalytics/";
        public static String LB_RESET_PERIOD = "leaderboardResetTimePeriod";
        public static String DISABLE_USER_ON_CPL_COMPLETION = "disableUserOnCplCompletion";
        public static String DISABLE_CPL_SUCCESS_MSG = "disableCplSuccessMsg";
        public static String DISABLE_CPL_FAILURE_MSG = "disableCplFailureMsg";
        public static String DISABLE_CPL_REDIRECT_URL = "disableCplRedirectUrl";
        public static String NEW_ASSESSMENT_UI = "newAssessmentUI";
        public static String SHOW_COMMUNICATION = "showCommunication";
        public static String FEED_AUTO_CLOSE_AFTER_COMPLETION = "feedAutoCloseAfterCompletion";
        public static String DISABLE_PROFILE_EDIT = "disableProfileEdit";
        public static String IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN = "showAlwaysOnLogin";
        public static String ORG_ID_USER_ID_APP_TUTORIAL_VIEWED = "orgIdUserIdAppTutorialViewed";
        public static String SHOW_NB_POST_COMMENT = "showNBComment";
        public static String SHOW_NB_POST_LIKE = "showNBLike";
        public static String SHOW_NB_POST_SHARE = "showNBShare";
        public static String SHOW_NB_POST_CREATE = "showNBCreate";
        public static String SHOW_ZEPTO_CPL_COMPLETION_POPUP = "showZeptoCompletionPopUp";
        public static String ZEPTO_REDIRECTION_URL = "zeptoRedirectionUrl";
        public static String SHOW_MULTILINGUAL_LANGUAGE_SWITCH = "languageSwitch";

        public static void init() {
            CLOUD_FRONT_BASE_PATH = MediaURLConstants.CLOUD_FRONT_BASE_PATH;
            CLOUD_FRONT_VIDEO_BASE = MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/video/";
            S3_BUCKET_IMAGE_URL = MediaURLConstants.MEDIA_SOURCE_BASE_URL + MediaURLConstants.MEDIA_SOURCE_IMAGE;
            CLOUD_FRONT_HLS_BASE_URL = MediaURLConstants.CLOUD_FRONT_BASE_HTTPS;
        }
    }

}
