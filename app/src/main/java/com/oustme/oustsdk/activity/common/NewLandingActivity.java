package com.oustme.oustsdk.activity.common;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.room.dto.DTONewFeed.newsFeedSorter;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_LANG_SELECTED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.ORG_ID_USER_ID_APP_TUTORIAL_VIEWED;
import static com.oustme.oustsdk.util.AchievementUtils.convertDate;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFContestListActivity;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentAnalyticsActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.GameLetActivity;
import com.oustme.oustsdk.activity.common.apptutorial.AppTutorialActivity;
import com.oustme.oustsdk.activity.common.feed.FeedPreviewActivity;
import com.oustme.oustsdk.activity.common.languageSelector.LanguageSelectionActivity;
import com.oustme.oustsdk.activity.common.leaderboard.activity.NewLeaderBoardActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBCommentActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBPostDetailsActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBTopicDetailActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.adapters.NBTopicAdapter;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.NBDataHandler;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.SubmitNBPostService;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.activity.common.todoactivity.CitySelection;
import com.oustme.oustsdk.activity.common.todoactivity.TodoListActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.adapter.common.BottomSheetItemRecyclerAdapter;
import com.oustme.oustsdk.adapter.common.NewFeedFilterAdapter;
import com.oustme.oustsdk.adapter.common.NewFeedVerticalFilterAdapter;
import com.oustme.oustsdk.adapter.common.NewLandingModuleAdaptera;
import com.oustme.oustsdk.adapter.common.NewLandingModuleAdapterc;
import com.oustme.oustsdk.adapter.common.NewModuleAdapter;
import com.oustme.oustsdk.adapter.common.NewProfileAdapter;
import com.oustme.oustsdk.adapter.common.NewTabFragmentPagerAdapter;
import com.oustme.oustsdk.adapter.common.SkillModuleAdapter;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.calendar_ui.model.MeetingCalendar;
import com.oustme.oustsdk.catalogue_ui.CatalogueModuleListActivity;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.customviews.FloatingActionButton;
import com.oustme.oustsdk.customviews.FloatingActionsMenu;
import com.oustme.oustsdk.customviews.HeavyCustomTextView;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.customviews.WrapContentLinearLayoutManager;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.feed_ui.adapter.FeedsRecyclerAdapter;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.feed_ui.ui.PublicVideoFeedCardScreen;
import com.oustme.oustsdk.feed_ui.ui.VideoCardDetailScreen;
import com.oustme.oustsdk.firebase.FFContest.BasicQuestionClass;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.common.BannerClass;
import com.oustme.oustsdk.firebase.common.Bannertype;
import com.oustme.oustsdk.firebase.common.CatalogDeatilData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.firebase.common.CplModelData;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.firebase.common.FirebaseDataTools;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.LandingTabData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.interfaces.common.FilterCategoryCallBack;
import com.oustme.oustsdk.interfaces.common.HideLoaderCallBack;
import com.oustme.oustsdk.interfaces.common.NewLandingDrawerCallback;
import com.oustme.oustsdk.interfaces.common.NewLandingInterface;
import com.oustme.oustsdk.interfaces.common.OnNetworkChangeListener;
import com.oustme.oustsdk.interfaces.common.OustCallBack;
import com.oustme.oustsdk.model.request.CatalogViewUpdate;
import com.oustme.oustsdk.model.response.common.AppTutorialDataModel;
import com.oustme.oustsdk.model.response.common.BottomItemModel;
import com.oustme.oustsdk.model.response.common.FeedCPLModel;
import com.oustme.oustsdk.model.response.common.SpecialFeedModel;
import com.oustme.oustsdk.model.response.common.ToDoChildModel;
import com.oustme.oustsdk.model.response.common.ToDoHeaderModel;
import com.oustme.oustsdk.my_tasks.MyTasksScreen;
import com.oustme.oustsdk.notification.NotificationActivity;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.profile.AchievementTabActivity;
import com.oustme.oustsdk.reminderNotification.ReminderNotificationManager;
import com.oustme.oustsdk.reminderNotification.ReminderNotificationUpdatingService;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ClickedFeedRequestData;
import com.oustme.oustsdk.request.DrawerItemData;
import com.oustme.oustsdk.request.SignOutRequest;
import com.oustme.oustsdk.request.ViewTracker;
import com.oustme.oustsdk.request.ViewedFeedData;
import com.oustme.oustsdk.response.common.BranchIoResponce;
import com.oustme.oustsdk.response.common.CatalogItemData;
import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.OFBModule;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.common.TabInfoData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOCplCompletedModel;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
import com.oustme.oustsdk.service.AlarmReceiverOnBoot;
import com.oustme.oustsdk.service.CourseNotificationReceiver;
import com.oustme.oustsdk.service.FeedBackService;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.service.SubmitCourseCompleteService;
import com.oustme.oustsdk.service.SubmitLevelCompleteService;
import com.oustme.oustsdk.service.SubmitRequestsService;
import com.oustme.oustsdk.skill_ui.model.CardCommonDataMap;
import com.oustme.oustsdk.skill_ui.model.CardInfo;
import com.oustme.oustsdk.skill_ui.model.UserSkillData;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.CplDataHandler;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.NetworkUtil;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.RecyclerViewItemClickSupport;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.tools.filters.NewsFeedFilter;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.util.CustomViewAlertBuilder;
import com.oustme.oustsdk.util.OustWebViewActivity;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.oustme.oustsdk.work_diary.WorkDiaryActivity;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.branch.referral.Branch;

public class NewLandingActivity extends BaseActivity implements OnNetworkChangeListener,
        NewLandingInterface, NewLandingDrawerCallback, OustCallBack, OnClickListener, SearchView.OnQueryTextListener,
        CustomSearchView.OnSearchViewCollapsedEventListener,
        CustomSearchView.OnSearchViewExpandedEventListener, HideLoaderCallBack, DataLoaderNotifier, FilterCategoryCallBack, FeedClickListener,
        NewLandingModuleAdaptera.OpenCPl {

//    public static boolean isWhiteLabeledApp;

    private static final String TAG = "NewLandingActivity";

    private LinearLayout newmainlanding_layout, ll_loading;
    private ProgressBar newlanding_loader_progressbar, learningoustcoin_progress, landingnewprogress_bar;
    private Toolbar toolbar;
    private RecyclerView drawer_listview;
    private DrawerLayout mDrawerLayout;
    private MenuItem actionSearch, action_alert, notificationAlert, action_leaderBoard;
    private RelativeLayout topLayout, main_oc_layout, learningmain_layout, playmain_layout,
            landingfrag_mainlayout, event_webview_layout, learn_play_modelayout,
            learn_play_modelayoutbacka, new_menu;
    private ImageView topbanner_imageview, coinimage_view, offlinemde_layout, lefttopbox_bgd,
            learn_play_modelayoutbacka_bgd;
    private AppBarLayout collapsing_toolbar;
    private CollapsingToolbarLayout collapsing_toolbara;
    private TextView coursecomplete_presentagetext, mycointext, currentcourse_name, my_progress_text;
    private RecyclerView addModuleGrid, playList_rv, play_skill_rv, noticeBoard_rv;
    private TextView nomodule_text, learnmode_text, playmode_text, ffcbanner_statusimg, no_data_text, nodatalabel, catalogue_label, catalogue_labelline,
            seeall_cataloguelist;
    private TryRippleView main_progress_layout;
    private SwitchCompat playmode_switch;
    private WebView mywebView;
    //    public static CustomViewPager newViewPager;
    private TabLayout tabLayout;
    private RelativeLayout ffcbanner_imglayout;
    private ImageView ffcbanner_img, bannerclose_btn;

    //user_component_activity
    LinearLayout normal_user_layout;
    View include_componemt_user;
    CircleImageView user_avatar;
    TextView user_name, coins_text, mytask, pending_count, textView_pending;//textView, pending_count, user_rank_count


    //----- Todo: initialization not done
    private ImageView newLayoutbackImage, usermain_profile, catelog_iamgea, catelog_iamgeb, catelog_iamgec, catelog_iamged;
    LinearLayout catalogue_lay;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout swiperefreshparent_layout;
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton fab_action_leaderboard, fab_action_joinmeeting;
    private FloatingActionButton action_favourite;
    private RelativeLayout fabBaseLayout, newlanding_topimagelayout;
    private RelativeLayout mainlanding_toplayout, newmainlanding_toplayout;
    private MenuItem action_gotohostapp;
    private RelativeLayout newmainlanding_topsublayout, lefttopbox;
    private TextView user_mainnametext, userprofileImageText, nocourse_text, txt_loader;
    private TextView usercointext, user_coursecompletedtext, user_coursecompletedoctext, user_coursecompletedlabelline, wjalert_bannertext,
            user_coursependingtext, user_coursependingoctext, user_coursependinglabelline, user_coursecompletedlabel, user_coursependinglabel, learningcredits_text;
    private RelativeLayout completedcourses_layout, pendingcourses_layout, see_all_catalaog, refresh_layout, wjalert_banner;
    private LinearLayout showAllcoursetextlayout, newlanding_layout, seeall_courseslayout, tabimage_subLayout;
    private RecyclerView newlandinghorizontal_recyclerviewa, newlandinghorizontal_recyclerviewb, newlandinghorizontal_recyclerviewc;
    private TextView cpl_description, cpl_head_tv, cpl_start_btn, toptab_texta, toptab_textb, toptab_textc, toptab_textd, showAllcoursetext, showAllcoursetext_line, refresh_text, refresh_text_line;
    private RelativeLayout toptab_layout, maintab_text_layout, maintab_image_layout, toptab_lineb, toptab_linea;
    private LinearLayout myDeskLayout, latestLayout, trendingLayout, bottomcatalogue_layout, bottomcatalogue_layouta, cpl_main_ll;
    private List<LandingTabData> mainLandingTabDataList;
    private List<String> subLandingTabDataList;
    private RelativeLayout catelog_layouta, catelog_layoutb, catelog_layoutc, catelog_layoutd, subtab_layout;

    private ImageView tabImgA, tabImgB, tabImgC, tabImgD, tabBigImgA, tabBigImgB, tabBigImgC, tabBigImgD;
    private RelativeLayout tabImgAIndicator, tabImgBIndicator, tabImgCIndicator, main_layout_3;
    private LinearLayout myFeedButton, myCourseButton, myChallengesButton, myNoticeBoardButton;
    private TextView subtabc_text, subtabc_textlabel, subtabb_text, subtabb_textlabel, subtaba_text, subtaba_textlabel, seeall_text;
    private View searchPlate;
    private boolean isAppTutorialShow;
    private String tenantName;

    RelativeLayout mLayoutUserLogoutPopup;
    TextView mButtonSkipLogout, mButtomLogout, mTextviewLogoutMessage;

    private NewProfileAdapter newProfileAdapter;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    //    public static boolean isEnterpriseUser = false;
    private ActiveUser activeUser;
    private ActiveGame activeGame;
    private NewTabFragmentPagerAdapter newTabFragmentPagerAdapter;
//    public static boolean isNetConnectionAvailable = true;

    private String isPlayModeEnabled, companydisplayName, panelColor, isShareEnabled;

    private PopupWindow myDeskPopUp;
    private NewModuleAdapter newModuleAdaptor;
    private FirebaseRefClass assessmentFirebaseRefClass, courseFirebaseRefClass;

    private List<OFBModule> ofbModuleList;
    private ArrayList<CatalogItemData> allCatalogItemDataArrayList;
    private ArrayList<CatalogItemData> learnCatalogItemDataArrayList;
    private ArrayList<CatalogItemData> growCatalogItemDataArrayList;
    private ArrayList<CatalogItemData> skillCatalogItemDataArrayList;
    private HashMap<String, NBTopicData> nbTopicDataHashMap;
    private ArrayList<TabInfoData> tabInfoDataArrayList;
    private ArrayList<AppTutorialDataModel> appTutorialDataModelArrayList;
    private ArrayList<String> moduleDataArrayList;
    private String toolbarColorCode;
    private String redirectIcon, userDisplayName, temporaryProfileImageDisplayName;
    private String new_landing_page_type = "All";
    private int totalModuleCount = 0, localModuleCount = 0, totalAssessmentCount = 0, localAssessmentCount = 0, totalCollectionCount = 0, localCollectionCount = 0;
    private boolean isFirstTime = true;
    private boolean haveAllCourses = false, haveAllAssignments = false, haveAllCollections = false;
    private boolean latestTrendingDataLoaded = false;
    private boolean isCoursesAvailable = false, isAssessmentAvailable = false, isCollectionAvailable = false;
    private boolean searchOn = false;
    private HashMap<String, String> myDeskInfoMap = new HashMap<>();
    private ArrayList<CommonLandingData> trendingList = new ArrayList<>();
    private ArrayList<CommonLandingData> latestList = new ArrayList<>();
    private int noofcoursestoload, noofOfAssessmentsToLoad, noofOfCollectionsToLoad;
    private HashMap<String, CommonLandingData> myDeskData = new HashMap<>();
    //for layout type 1 and 2
    private HashMap<String, CommonLandingData> myAssessmentData = new HashMap<>();
    private HashMap<String, CommonLandingData> mySurveyData = new HashMap<>();
    private String landingTag = "";
    private int noofListenerSetForFeed = 0;
    private int noofListenerSetForFeed3 = 0;
    private List<DTONewFeed> userNewFeedList;
    private boolean isLatestTrendingShown = false;
    private HashMap<String, DTONewFeed> newFeedHashMap;
    private RecyclerView filter_rv;
    private Target target;
    private boolean COURSE_ASSESSMENT_TAB_FOUND = false;
    private int defaultTab = 0;
    private final String CPL_BASE_NODE = "cpl/cpl";
    private CplCollectionData cplCollectionData;


    //for bottom sheet to do, play list and contest
    private RecyclerView mRecyclerViewBottomSheetItems;
    private List<String> mListBottomSheetItems;
    private List<BottomItemModel> mBottomItemModelList;
    private LinearLayout mLinearLayoutToDoPlaylist;
    private ImageView mImageViewContest;
    private TextView mTextViewToDoPlayListContent;
    private int mItemSelected = -1;
    private boolean isTodo, isContest, isPlayList;
    private ArrayList<ToDoHeaderModel> mExpandListHeader;
    private HashMap<String, List<ToDoChildModel>> mExpandChildList;
    private String ffcBannerURL;
    private String FFCEnrolledCount = "0";

    private View mBottomSheetBanner;
    private static boolean isLanguageScreenenabled, isCityScreenEnabled;
    private androidx.appcompat.app.AlertDialog alertDialog;
    private TextView txt_message2, txt_message1;
    private int no_of_loading = 0;

    private LinearLayout submitLayout;
    private androidx.appcompat.app.AlertDialog.Builder builder;
    private androidx.appcompat.app.AlertDialog mAlertDialogRatePopUp;
    private int mNoOfTimesOpened = 0;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DownloadFiles downloadFiles;
    private ArrayList<String> ids;
    private Map<String, Object> cplHashMapData;
    private ProgressBar mProgressbarAPICall;
    private boolean isAlertNeeded = false, isParentCplLoaded = false;
    private View view_blak_overlay;
    private LinearLayout layoutUserOverallcredits;
    private int count;
    private boolean isCPLShowOnCourse;
    private boolean isAppTutorialShown;
    private boolean isAppTutorialShownOnlyLogin;
    ViewTracker viewTracker;
    private boolean isNotificationPageOpen = false;
    private ImageView layout_bg_image;
    private RelativeLayout rl_retry_landingpage, rl_loading_landingpage;
    private Button btn_refresh;
    private boolean multipleCpl = false;

    //Skill
    ArrayList<UserSkillData> userSkillDataArrayList = new ArrayList<>();
    int userSkillDataArrayListSize = 0;
    private int tabIndex;
    boolean isBranchLink = false;
    String branchLink = null;
    String referringParamsString = null;
    //ReminderNotification
    ArrayList<CommonLandingData> allcommonLandingDataArrayList = new ArrayList<>();
    boolean isFilterAvailable = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void checkIncomingNotificationValidity() {
        try {
            String userdata = OustPreferences.get("userdata");
            if (userdata == null || userdata.isEmpty()) {
                /*Intent intent = new Intent("oust_logout");
                sendBroadcast(intent);*/
                NewLandingActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    final String CALC_PACKAGE_NAME = "com.miui.securitycenter";
    final String CALC_PACKAGE_ACITIVITY = "com.miui.permcenter.autostart.AutoStartManagementActivity";

    public void askXiomiAutoStart(String deviceManufacturer) {
        if (deviceManufacturer.equalsIgnoreCase("Xiaomi")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("To receive Oust Notifications , we need you to set Oust to Auto Start")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                OustPreferences.saveAppInstallVariable("auto_start_notif", true);
                                for (Intent intent : AUTO_START_INTENTS) {
                                    if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                                        startActivity(intent);
                                        break;
                                    }
                                }
                            } catch (ActivityNotFoundException e) {
                                Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                            }
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setTitle("Oust Notification");
            alert.show();
        }
    }

    private static final Intent[] AUTO_START_INTENTS = {
            new Intent().setComponent(new ComponentName("com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(
                    Uri.parse("mobilemanager://function/entry/AutoStart"))
    };

    //    =======================================================================
    boolean refreshButtonPressed = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        boolean isRefreshCourse = OustPreferences.getAppInstallVariable("isRefreshCourse");
        String multiLingualCourseId = OustPreferences.get("multiLingualCourseId");
        updateUserInfoFromFirebase();
        OustStaticVariableHandling.getInstance().setNbPostClicked(false);
        if (OustStaticVariableHandling.getInstance().isAppTutorialShown()) {
            OustStaticVariableHandling.getInstance().setAppTutorialShown(false);
            recreate();
        } else {

            try {
                if (isRefreshCourse && multiLingualCourseId != null) {
                    if (myDeskData != null) {
                        myDeskData.remove("COURSE" + multiLingualCourseId);
                    }
                    if (myDeskInfoMap != null) {
                        myDeskInfoMap.remove("COURSE" + multiLingualCourseId);
                    }
                    //getUserCourses();
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            try {
                isCPLShowOnCourse = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB);

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            if (!refreshButtonPressed) {
                try {
                    OustStaticVariableHandling.getInstance().setModuleClicked(false);
                    String addedCatalogId = OustPreferences.get("catalogId");
                    if (addedCatalogId != null) {
                        if (myDeskInfoMap != null) {
                            if (myDeskInfoMap.get(addedCatalogId) == null) {
                                myDeskInfoMap.put(addedCatalogId, "CATEGORY");
                            }
                        }
                    }
                    mDrawerLayout.closeDrawer(drawer_listview);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                cplShown = false;
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                   /* if (newFeedAdapter != null) {
                        newFeedAdapter.enableButton();
                    }*/
                    createLayout3CourseList(defaultCategoryId);
                    createChallengesList3();
                    if (OustStaticVariableHandling.getInstance().isShareFeedOpen()) {
                        OustStaticVariableHandling.getInstance().setShareFeedOpen(false);
//                        updateShareCount();
                    }
                }
                if (OustSdkApplication.getContext() == null) {
                    OustSdkApplication.setmContext(NewLandingActivity.this);
                }
                OustPreferences.saveTimeForNotification("oustautologout_currenttime", System.currentTimeMillis());
                try {
                    OustSdkTools.setSnackbarElements(newmainlanding_layout, NewLandingActivity.this);
                    String toolbarColorCode = OustPreferences.get("toolbarColorCode");
                    if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                        int color = OustSdkTools.getColorBack(R.color.lgreen);
                        if (OustPreferences.get("toolbarColorCode") != null) {
                            color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                        }
                        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                        final LayerDrawable ld = (LayerDrawable) getResources().getDrawable(R.drawable.progressbar_test);
                        final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                        d1.setColorFilter(color, mode);
                        newlanding_loader_progressbar.setIndeterminateDrawable(ld);
                    }
                    OustSdkTools.setProgressbar(newlanding_loader_progressbar);
                    if (newProfileAdapter != null) {
                        newProfileAdapter.notifyDataSetChanged();
                    }
                    setMyTotalProgressText();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                ffcBannerStatus();
                saveLatestTime();
                isCalledAfterGettingFirebaseToken = false;
                if (((System.currentTimeMillis() / 1000) - (OustPreferences.getTimeForNotification("lastTokenRefreshTime"))) >= 3600) {
                    swiperefreshparent_layout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(true);
                    autheticateWithFirebase(true);
                    OustPreferences.saveTimeForNotification("lastTokenRefreshTime", System.currentTimeMillis() / 1000);
                } else if (OustStaticVariableHandling.getInstance().isMultilingualChildCourseSelected()) {
                    OustStaticVariableHandling.getInstance().setMultilingualChildCourseSelected(false);
                    performRefresh();
                }
            } else {
                //sign with custom token will not work if app is put in background, so if user put app in background after hitting refresh.
                refreshButtonPressed = false;
                autheticateWithFirebase(false);
            }

            Log.d(TAG, "NewCPLDistributed:" + OustStaticVariableHandling.getInstance().isNewCplDistributed());
            if (OustStaticVariableHandling.getInstance().isNewCplDistributed()) {
                try {
                    Log.d(TAG, "onResume: new CPL distributed: ");
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    builder.setCancelable(false);

                    LayoutInflater inflater = this.getLayoutInflater();
                    View mView = inflater.inflate(R.layout.cpl_loading_progressbar, null);
                    txt_message1 = mView.findViewById(R.id.textViewLoadMsg);
                    txt_message1.setVisibility(View.VISIBLE);
                    String fetching_data = getResources().getString(R.string.fetching_data_msg);
                    txt_message1.setText(fetching_data);
                    txt_message2 = mView.findViewById(R.id.textViewLoadMsg2);
                    txt_message2.setVisibility(View.GONE);

                    builder.setView(mView);
                    alertDialog = builder.create();
                    alertDialog.show();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    anim_timer = new Timer();
                    anim_timer.scheduleAtFixedRate(anim_task, 15000, 20000);

                    //extractData();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else {
                try {
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }

                    if (anim_timer != null) {
                        no_of_loading = 0;
                        anim_timer.cancel();
                        anim_timer = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            if (OustStaticVariableHandling.getInstance().isRefreshReq()) {
                OustStaticVariableHandling.getInstance().setRefreshReq(false);
                performRefresh();
            }
        }

        Log.e(TAG, "onResume: -- isNotificationPageOpen--> " + isNotificationPageOpen);
        if (isNotificationPageOpen) {
            Log.e(TAG, "onResume: inSide notification");
            isNotificationPageOpen = false;
            initializeAllData();
        }
    }

    protected void performRefresh() {
        /*if(!OustSdkTools.checkInternetStatus()){
            OustSdkTools.showToast("Please check your internet connection and try again");
            return;
        }*/
        userSkillDataArrayList = new ArrayList<>();
        userSkillDataArrayListSize = 0;
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
            refreshLayout();
        } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
            refreshLayoutC();
        } else {
            refreshLayoutA();
        }
    }

    private void RatingAlertPopUp() {
        builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.rate_popup_dialog, null);
        submitLayout = mView.findViewById(R.id.linearLayoutNext);
        ImageView imageView = mView.findViewById(R.id.imageViewClose);
        builder.setView(mView);
        submitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS, true);
                //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.oustme.oustapp&hl=en")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.oustme.oustapp&hl=en")));
                }
                mAlertDialogRatePopUp.dismiss();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OustPreferences.saveintVar(AppConstants.StringConstants.NO_TIMES_OPENED, 0);
                mAlertDialogRatePopUp.dismiss();
            }
        });
        mAlertDialogRatePopUp = builder.create();
        mAlertDialogRatePopUp.setCancelable(false);
        mAlertDialogRatePopUp.show();
    }

/*
    private void updateShareCount() {
        try {
            DTONewFeed newFeed = OustDataHandler.getInstance().getNewFeed();
            if (newFeed != null) {
                OustDataHandler.getInstance().setNewFeed(null);
                ArrayList<DTONewFeed> feedArrayList = OustStaticVariableHandling.getInstance().getFeeds();
                if (feedArrayList != null && feedArrayList.size() > 0) {
                    for (int i = 0; i < feedArrayList.size(); i++) {
                        if (newFeed.getFeedId() == feedArrayList.get(i).getFeedId()) {
                            feedArrayList.get(i).setNumShares(newFeed.getNumShares());
                            break;
                        }
                    }
                    createList(feedArrayList, false, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
*/

    private FirebaseAuth mAuth;
    private boolean isCalledAfterGettingFirebaseToken = false;

    public void autheticateWithFirebase(final boolean isComingFromOnResume) {
        try {
            final String firebaseToken = OustPreferences.get("firebaseToken");
            if ((firebaseToken != null) && (!firebaseToken.isEmpty())) {
                if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                    FirebaseApp firebaseApp = null;

                    try {
                        firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        OustFirebaseTools.initFirebase();
                        firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    }

                    mAuth = FirebaseAuth.getInstance(firebaseApp);
                } else {
                    mAuth = FirebaseAuth.getInstance();
                }
                mAuth.signInWithCustomToken(firebaseToken)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                refreshButtonPressed = false;
                                if (task != null) {
                                    if (!task.isSuccessful()) {
                                        if (isComingFromOnResume) {
                                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                                resetNewLayout();
                                            } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                                resetLayoutC();
                                            } else {
                                                resetLayoutA();
                                            }
                                        }
                                        removeAllListener();
                                        getNewTokenForFireBase(isComingFromOnResume);
                                    } else {
                                        if (isComingFromOnResume) {
                                            swiperefreshparent_layout.setVisibility(View.GONE);
                                            swipeRefreshLayout.setVisibility(View.GONE);
                                            swipeRefreshLayout.setRefreshing(false);
                                        } else {
                                            initLanding();
                                        }
                                    }
                                }
                            }
                        });
            } else {
                removeAllListener();
                getNewTokenForFireBase(isComingFromOnResume);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeAllListener() {
        for (int i = 0; i < OustAppState.getInstance().getFirebaseRefClassList().size(); i++) {
            if (OustAppState.getInstance().getFirebaseRefClassList().get(i) != null) {
                OustFirebaseTools.getRootRef().child((OustAppState.getInstance().getFirebaseRefClassList().get(i)).getFirebasePath()).removeEventListener(OustAppState.getInstance().getFirebaseRefClassList().get(i).getEventListener());
            }
        }
    }

    private void getNewTokenForFireBase(final boolean isComingFromOnResume) {
        try {
            if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                if (OustSdkTools.checkInternetStatus()) {
                    ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                    String user_id = activeUser.getStudentid();

                    String get_fireBase_token_url = OustSdkApplication.getContext().getResources().getString(R.string.get_fireBase_token_url);
                    get_fireBase_token_url = get_fireBase_token_url.replace("{userId}", user_id);

                    get_fireBase_token_url = HttpManager.getAbsoluteUrl(get_fireBase_token_url);

                    ApiCallUtils.doNetworkCall(Request.Method.GET, get_fireBase_token_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            gotFireBaseToken(response, isComingFromOnResume);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                            OustSdkTools.showToast(getResources().getString(R.string.restart_msg));
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, get_fireBase_token_url, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            gotFireBaseToken(response, isComingFromOnResume);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                            OustSdkTools.showToast(getResources().getString(R.string.restart_msg"));
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
                } else {
                    if (isComingFromOnResume) {
                        swiperefreshparent_layout.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        initLanding();
                    }
                }
            } else {
                onLogout();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotFireBaseToken(JSONObject commonResponse, boolean isComingFromResume) {
        if (commonResponse != null) {
            if (commonResponse.optBoolean("success")) {
                Log.e("firebaseToken taken", commonResponse.toString());
                String firebaseToken = commonResponse.optString("token");
                OustPreferences.save("firebaseToken", firebaseToken);
                isCalledAfterGettingFirebaseToken = true;
                autheticateWithFirebase(false);
            } else {
                getNewTokenForFireBase(isComingFromResume);
            }
        } else {
            getNewTokenForFireBase(isComingFromResume);
        }
    }

    private void saveLatestTime() {
        try {
            long currentTimestamp = System.currentTimeMillis() / 1000;
            OustPreferences.saveTimeForNotification("lastNotificationTime", currentTimestamp);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


//======================================================================================

    //start fetching data
    private void initLanding() {
        try {

            setActiveUserData();

            Log.d(TAG, "initLanding: ");
            isAppTutorialShown = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN);
            isAppTutorialShownOnlyLogin = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN);
            if (!isAppTutorialShown && isAppTutorialShownOnlyLogin) {
                getAppTutorialData();
            } else {
                initializeAllData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initializeAllData() {
        Log.d(TAG, "initializeAllData: ");
        if (!OustSdkTools.checkInternetStatus()) {
            rl_loading_landingpage.setVisibility(View.GONE);
            rl_retry_landingpage.setVisibility(View.VISIBLE);
        }

        //rl_loading_landingpage.setVisibility(View.GONE);
        rl_retry_landingpage.setVisibility(View.GONE);

        setStartingData();
        try {
            FirebaseDataTools firebaseDataTools = new FirebaseDataTools();
            firebaseDataTools.iniitFirebaseListener();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        getUserAppConfiguration();
        getUserFFContest();
        getCplData();
        OustPreferences.saveAppInstallVariable("isLayout4", false);
        getUserCourses();
        getUserCoursesCollections();
        getUserAssessments();
        SetFABButton();


        //get locally saved profile image
        checkChildCPlDistributed();
        //getUserParentCplId();
        getLocalImage();
        //  getUserNewsFeed();

        //at start layout set normal mode
        setDefaultLayoutType();

        count = 0;
        addDrower();

        checkForValidUser();
        getOcFromFirebase();

        setToolBarColorAndIcons();

        setTopBarSize();
        loadAllBanners();
        setTopBanner();

        updateUserInfoFromFirebase();
        OustPreferences.saveAppInstallVariable("isAppInstalled", true);
        OustStaticVariableHandling.getInstance().setAppActive(true);
        OustSdkTools.sendPingApi();

        startOustServices();
        getConstants();
        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS) && mNoOfTimesOpened > 10) {
            mNoOfTimesOpened = 0;
            OustPreferences.saveintVar(AppConstants.StringConstants.NO_TIMES_OPENED, mNoOfTimesOpened);
            RatingAlertPopUp();
        }

        mytask.setBackground(OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.course_button_bg)));
    }

    private void getConstants() {
        AppConstants.MediaURLConstants.init();
        AppConstants.StringConstants.init();
        Log.d(TAG, "getConstants: MEDIA_SOURCE_BASE_URL:" + AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL);
        Log.d(TAG, "getConstants: CLOUD_FRONT_BASE_PATH:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
        Log.d(TAG, "getConstants: CLOUD_FRONT_BASE_HTTPs:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
    }

    private long parentCplId = 0;
    private boolean isChildCplFound = false;
    private boolean isParentCplFound = false;

    private void checkChildCPlDistributed() {
        try {
            String message_childCplDistributed = "/landingPage/" + activeUser.getStudentKey() + "/childCplDistributed";
            Log.d(TAG, "checkChildCPlDistributed: " + message_childCplDistributed);
            ValueEventListener childCplDistributedListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            boolean isChildCplDistributed = (boolean) snapshot.getValue();
                            if (isChildCplDistributed) {
                                OustPreferences.saveAppInstallVariable(IS_LANG_SELECTED, true);
                            } else {
                                getUserParentCplId();
                            }
                        } else {
                            getUserParentCplId();
                        }
                    } catch (Exception e) {
                        getUserParentCplId();
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //error.getMessage();
                    getUserParentCplId();
                }
            };
            addToFireBaseRefList(message_childCplDistributed, childCplDistributedListener);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            getUserParentCplId();
        }
    }

    private void getUserParentCplId() {
        Log.d(TAG, "getUserParentCplId: " + isAlertNeeded);
        try {
            String message = "/landingPage/" + activeUser.getStudentKey() + "/parentCplId";
            ValueEventListener parentCplIdListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        isParentCplLoaded = true;
                        if (null != snapshot.getValue()) {
                            isParentCplFound = true;
                            Log.d(TAG, "getUserParentCplId -- Data: " + snapshot.getValue() + " --isCityScreenEnabled:" + isCityScreenEnabled);
                            parentCplId = (long) snapshot.getValue();
                            if (parentCplId > 0) {
                                OustPreferences.saveTimeForNotification("parentCplId", parentCplId);
                                /*if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION) && OustPreferences.getTimeForNotification("parentCplId")!=0  && !OustPreferences.getAppInstallVariable("IS_LANG_SELECTED")){
                                    clickOnCplLanguageSelection();
                                }*/

                                if (isCityScreenEnabled && !OustPreferences.getAppInstallVariable("IS_CITY_SELECTED")) {
                                    if (isAlertNeeded) {
                                        openAlertPopup();
                                    } else {
                                        openCityScreen(false, false);
                                    }
                                } else {
                                    if (!isChildCplFound) {
                                        OpenLanguageScreenForMultilingual(parentCplId, false, false);
                                    }
                                }
                            } else {
                                if (isCityScreenEnabled && !OustPreferences.getAppInstallVariable("IS_CITY_SELECTED")) {
                                    openCityScreen(false, false);
                                }
                            }
                        } else {
                            if (isCityScreenEnabled && !OustPreferences.getAppInstallVariable("IS_CITY_SELECTED")) {
                                openCityScreen(false, false);
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: ");
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //error.getMessage();
                }
            };
            addToFireBaseRefList(message, parentCplIdListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openAlertPopup() {
        CustomViewAlertBuilder alertBuilder = new CustomViewAlertBuilder(NewLandingActivity.this, getResources().getString(R.string.city_change_msg), "Warning", getResources().getString(R.string.text_continue), getResources().getString(R.string.text_start_again)) {
            @Override
            public void negativeButtonClicked() {
                view_blak_overlay.setVisibility(View.GONE);
                dismiss();
                openCityScreen(false, false);
            }

            @Override
            public void positiveButtonClicked() {
                view_blak_overlay.setVisibility(View.GONE);
                dismiss();
                OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_CITY_SELECTED, true);
                OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_LANG_SELECTED, true);
                OustStaticVariableHandling.getInstance().setCplCityScreenOpen(false);
                OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(false);

                if (isPlayList && !isContest && !isTodo) {
                    setCplView();
                } else if (cplCollectionData != null && !isPlayList && !isTodo && !isContest) {
                    setCplView();
                }
            }
        };
        view_blak_overlay.setVisibility(View.VISIBLE);
        alertBuilder.show();
    }

    private void getCplData() {
        Log.e(TAG, "inside get cplData() ");
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/cpl";
            Log.e(TAG, "getCplData: " + message);
            ValueEventListener cplDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    extractCplData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "onCancelled: ");
                }
            };
            addToFireBaseRefList(message, cplDataListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addToFireBaseRefList(String message, ValueEventListener eventListener) {
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(eventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, message));
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    CommonLandingData CPLcommonLandingData;

    private void extractCplData(DataSnapshot dataSnapshot) {
        Log.d(TAG, "extractCplData: ");

        if (dataSnapshot.getValue() != null) {
            isChildCplFound = true;
            CPLcommonLandingData = new CommonLandingData();
            Object contentObject = dataSnapshot.getValue();
            if (contentObject.getClass().equals(ArrayList.class)) {
                int i = 0;
                List<Object> objectList = (List<Object>) dataSnapshot.getValue();
                cplHashMapData = new HashMap<>();
                for (Object product : objectList) {
                    if (product != null) {
                        cplHashMapData.put(i + "", product);
                    }
                    i++;
                }
                if (cplHashMapData != null) {
                    extractCourseAssessmentFromCPL(cplHashMapData);
                }

                CPLcommonLandingData = new CommonLandingData();

            } else {
                cplHashMapData = (Map<String, Object>) dataSnapshot.getValue();
                extractCourseAssessmentFromCPL(cplHashMapData);

            }
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE)) {
                isPlayList = true;
                initBottomSheetBannerData();
            }
            try {
                Object o1 = dataSnapshot.getValue();
                if (o1.getClass().equals(ArrayList.class)) {
                    List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                    cplCollectionData = new CplCollectionData(learningList);
                } else {

                    Map<String, Object> lpMainMap = new HashMap<>();
                    lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (lpMainMap != null) {
                        cplCollectionData = new CplCollectionData(lpMainMap);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                Log.d(TAG, "extractCplData: " + e.getLocalizedMessage());
            }

            try {
                if (isCPLShowOnCourse && cplHashMapData != null) {
                    if (cplCollectionData.getBanner() != null)
                        CPLcommonLandingData.setBanner(cplCollectionData.getBanner());
                    CPLcommonLandingData.setId("CPL" + cplCollectionData.getCplId());
                    CPLcommonLandingData.setType("CPL");
                    CPLcommonLandingData.setIcon(cplCollectionData.getIntroIcon());
                    CPLcommonLandingData.setName(cplCollectionData.getCplName());
                    CPLcommonLandingData.setEnrollCount(cplCollectionData.getEnrolledCount());
                    CPLcommonLandingData.setDescription(cplCollectionData.getCplDescription());
                    CPLcommonLandingData.setOc(cplCollectionData.getOustCoins());
                    CPLcommonLandingData.setUserOc(cplCollectionData.getTotalCoins());
                    CPLcommonLandingData.setAddedOn(cplCollectionData.getAssignedDate() + "");
                    CPLcommonLandingData.setCompletionPercentage(cplCollectionData.getProgress());
                    myDeskData.put("CPL" + CPLcommonLandingData.getCplId(), CPLcommonLandingData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            if (isNotNull(cplCollectionData)) {
                Log.d(TAG, "getCplData: " + "getCplExtraInfo()");
                getCplExtraInfo();
            }
        } else {
            cpl_main_ll.setVisibility(View.GONE);
            cplCollectionData = null;
            if (CPLcommonLandingData != null) {
                String Key = null;
                for (Map.Entry<String, CommonLandingData> entry : myDeskData.entrySet()) {
                    String k = entry.getKey();
                    CommonLandingData v = entry.getValue();
                    if (k.contains("CPL")) {
                        Key = k;
                    }
                }
                if (Key != null) {
                    myDeskData.remove(Key);
                    createCourseMainList();
                    newLandingModuleAdaptera.notifyDataSetChanged();
                }
            }
            //CPLcommonLandingData=null;

        }
    }

    public void extractCourseAssessmentFromCPL(Map<String, Object> mainCplData) {
        try {
            String cplkey = "";
            Map<String, Object> cplMainMap = null;
            for (Map.Entry<String, Object> entry : mainCplData.entrySet()) {
                cplMainMap = (Map<String, Object>) entry.getValue();
                cplkey = entry.getKey();
            }
            int totalModules;
            if (cplMainMap.containsKey("contentListMap")) {
                Map<String, Object> contentListMap = (Map<String, Object>) cplMainMap.get("contentListMap");
                if (contentListMap != null) {
                    totalModules = contentListMap.size();
                    DTOCplCompletedModel cplCompletedModel = new DTOCplCompletedModel();
                    HashMap<Long, CplModelData> cplModelDataHashMap = new HashMap<>();
                    for (Map.Entry<String, Object> entry : contentListMap.entrySet()) {
                        Map<String, Object> contentInfoMap = (Map<String, Object>) entry.getValue();
                        CplModelData cplModelData = new CplModelData();
                        if (contentInfoMap.containsKey("contentId")) {
                            cplModelData.setContentId(OustSdkTools.convertToLong(contentInfoMap.get("contentId")));
                            cplCompletedModel.setId(cplModelData.getContentId());
                        }
                        if (contentInfoMap.containsKey("contentType")) {
                            cplModelData.setContentType((String) contentInfoMap.get("contentType"));
                            cplCompletedModel.setType(cplModelData.getContentType());
                        }
                        if (cplModelData.getContentType() != null && cplModelData.getContentType().equalsIgnoreCase("COURSE")) {
                            if (myDeskData != null) {
                                for (int i = 0; i < myDeskData.size(); i++) {
                                    if (myDeskData.containsKey("COURSE" + cplCompletedModel.getId()) && myDeskData.get("COURSE" + cplCompletedModel.getId()).getCompletionPercentage() < 100) {
                                        myDeskData.remove("COURSE" + cplCompletedModel.getId());
                                    } else if (myDeskData.containsKey(cplCompletedModel.getId()) && myDeskData.get(cplCompletedModel.getId()).getCompletionPercentage() < 100) {
                                        myDeskData.remove(cplCompletedModel.getId());
                                    }
                                }
                                getTotalCourses();
                            }
                        } else if (cplModelData.getContentType() != null && cplModelData.getContentType().equalsIgnoreCase("ASSESSMENT")) {
                            if (myAssessmentData != null && myAssessmentData.size() > 0) {
                                for (int i = 0; i < myAssessmentData.size(); i++) {
                                    if (myAssessmentData.containsKey("ASSESSMENT" + cplCompletedModel.getId()) && myAssessmentData.get("ASSESSMENT" + cplCompletedModel.getId()).getCompletionPercentage() < 100) {
                                        myAssessmentData.remove("ASSESSMENT" + cplCompletedModel.getId());
                                        OustAppState.getInstance().setMyAssessmentList(new ArrayList<>(myAssessmentData.values()));
                                        getTotalAssessments();
                                    } else if (myAssessmentData.containsKey(cplCompletedModel.getId()) && myAssessmentData.get(cplCompletedModel.getId()).getCompletionPercentage() < 100) {
                                        myAssessmentData.remove(cplCompletedModel.getId());
                                        OustAppState.getInstance().setMyAssessmentList(new ArrayList<>(myAssessmentData.values()));
                                        getTotalAssessments();
                                    }
                                }

                            } else {
                                if (myDeskData != null) {
                                    for (int i = 0; i < myDeskData.size(); i++) {
                                        if (myDeskData.containsKey("ASSESSMENT" + cplCompletedModel.getId()) && myDeskData.get("ASSESSMENT" + cplCompletedModel.getId()).getCompletionPercentage() < 100) {
                                            myDeskData.remove("ASSESSMENT" + cplCompletedModel.getId());
                                        } else if (myDeskData.containsKey(cplCompletedModel.getId()) && myDeskData.get(cplCompletedModel.getId()).getCompletionPercentage() < 100) {
                                            myDeskData.remove(cplCompletedModel.getId());
                                        }
                                    }
                                    getTotalCourses();
                                }
                            }
                        }
                        if (contentInfoMap.containsKey("sequence")) {
                            cplModelData.setSequence(OustSdkTools.convertToLong(contentInfoMap.get("sequence")));
                        }
                        if (contentInfoMap.containsKey("assignedOn")) {
                            cplModelData.setStartDate(OustSdkTools.convertToLong(contentInfoMap.get("assignedOn")));
                        }
                        if (contentInfoMap.containsKey("completedOn")) {
                            cplModelData.setUploadedToServer(true);
                            cplModelData.setCompleted(true);
                            cplModelData.setCompletedDate(OustSdkTools.convertToLong(contentInfoMap.get("completedOn")));
                        }
                        if (contentInfoMap.containsKey("oustCoins")) {
                            cplModelData.setCplPoints(OustSdkTools.convertToLong(contentInfoMap.get("oustCoins")));
                        }
                        if (contentInfoMap.containsKey("hasDistributed")) {
                            cplModelData.setHasDistributed(OustSdkTools.convertToBoolean(contentInfoMap.get("hasDistributed")));
                        }
                        if (contentInfoMap.containsKey("pass")) {
                            cplModelData.setPass((boolean) contentInfoMap.get("pass"));
                        }
                        if (contentInfoMap.containsKey("attemptCount")) {
                            cplModelData.setAttemptCount(OustSdkTools.convertToLong(contentInfoMap.get("attemptCount")));
                        }
                        cplModelDataHashMap.put(cplModelData.getContentId(), cplModelData);
                        // RealmHelper.addorUpdateCPLData(cplCompletedModel);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCplExtraInfo() {
        String cplInfoNode = (CPL_BASE_NODE + cplCollectionData.getCplId());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    extractCplExtraInfo(dataSnapshot);
                } catch (Exception e) {
                    Log.e(TAG, "caught exception inside set singelton ", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "onCancelled: CPL base node not found");
            }
        };
        Log.d(TAG, "getCplExtraInfo: " + cplInfoNode);
        addToFireBaseRefList(cplInfoNode, eventListener);
    }

    private Timer anim_timer;

    private TimerTask anim_task = new TimerTask() {
        public void run() {
            Log.d(TAG, "Timer running");
            try {
                Message message = anim_Handler.obtainMessage(1, "Start");
                message.sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    private Handler anim_Handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            try {
                no_of_loading = no_of_loading + 1;
                if (alertDialog != null && alertDialog.isShowing()) {
                    txt_message2.setVisibility(View.VISIBLE);
                    if (no_of_loading == 1) {
                        txt_message2.setText(getResources().getString(R.string.please_wait));
                    } else if (no_of_loading == 2) {
                        txt_message2.setText(getResources().getString(R.string.network_seems_slow));
                    } else if (no_of_loading == 3) {
                        txt_message2.setText(getResources().getString(R.string.network_seems_slow_wait));
                    } else if (no_of_loading >= 5) {
                        txt_message1.setText(getResources().getString(R.string.unable_to_fetch_data));
                        txt_message2.setText(getResources().getString(R.string.please_check_your_internet_connection));
                    }
                    //Toast.makeText(NewLandingActivity.this, "Please wait. Internet connection is slow, it may take little time to load.", Toast.LENGTH_LONG).show();

                } else {
                    Log.d(TAG, "Justreturned");
                    if (anim_timer != null) {
                        no_of_loading = 0;
                        anim_timer.cancel();
                        anim_timer = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    private void extractCplExtraInfo(DataSnapshot dataSnapshot) {
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

            if (anim_timer != null) {
                no_of_loading = 0;
                anim_timer.cancel();
                anim_timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (dataSnapshot != null) {
            Map<String, Object> cplInfoMap = (Map<String, Object>) dataSnapshot.getValue();
            if (isNotNull(cplInfoMap)) {
                Log.d(TAG, "getCplData: " + "extractCplExtraInfo()");
                //Log.d(TAG, "extractCplExtraInfo: ");
                OustPreferences.saveTimeForNotification("cplNotificationFreq", OustSdkTools.convertToLong(cplInfoMap.get("notificationFrequency")));
                OustPreferences.saveAppInstallVariable("isCplBulletin", getBoolean(cplInfoMap.get("bulletinBoard")));
                OustPreferences.saveAppInstallVariable("isCplLeaderboard", getBoolean(cplInfoMap.get("leaderboard")));
                cplCollectionData.setBgImage(getString(cplInfoMap.get("bgImage")));
                cplCollectionData.setBanner(getString(cplInfoMap.get("banner")));
                cplCollectionData.setIntroBgImg(getString(cplInfoMap.get("introBgImg")));
                cplCollectionData.setIntroIcon(getString(cplInfoMap.get("introIcon")));
                cplCollectionData.setCplVersion(getString(cplInfoMap.get("version"))); //cplType
                cplCollectionData.setCplType(getString(cplInfoMap.get("cplType")));
                cplCollectionData.setEnrolledCount((long) cplInfoMap.get("enrolledUsers"));
                cplCollectionData.setOustCoins((long) cplInfoMap.get("oustCoins"));

                try {
                    if (isCPLShowOnCourse) {
                        CPLcommonLandingData = myDeskData.get("CPL" + cplCollectionData.getCplId());
                        if (CPLcommonLandingData == null)
                            CPLcommonLandingData = new CommonLandingData();
                        CPLcommonLandingData.setBanner(cplCollectionData.getBanner());
                        CPLcommonLandingData.setId("CPL" + cplCollectionData.getCplId());
                        CPLcommonLandingData.setType("CPL");
                        CPLcommonLandingData.setIcon(cplCollectionData.getIntroIcon());
                        CPLcommonLandingData.setName(cplCollectionData.getCplName());
                        CPLcommonLandingData.setEnrollCount(cplCollectionData.getEnrolledCount());
                        CPLcommonLandingData.setDescription(cplCollectionData.getCplDescription());
                        CPLcommonLandingData.setOc(cplCollectionData.getOustCoins());
                        CPLcommonLandingData.setUserOc(cplCollectionData.getTotalCoins());
                        CPLcommonLandingData.setAddedOn(cplCollectionData.getAssignedDate() + "");
                        CPLcommonLandingData.setCompletionPercentage(cplCollectionData.getProgress());
                        myDeskData.put("CPL" + CPLcommonLandingData.getCplId(), CPLcommonLandingData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                createCourseMainList();
                // cplCollectionData.setTotalCoins(Long.parseLong(cplInfoMap.get("totalCoins").toString()));
                cplCollectionData.setOustCoins(OustSdkTools.convertToLong(cplInfoMap.get("oustCoins").toString()));
                if (cplInfoMap.get("enrolledUsers") != null) {
                    cplCollectionData.setEnrolledCount(Long.parseLong(cplInfoMap.get("enrolledUsers").toString()));
                } else cplCollectionData.setEnrolledCount(0);

                Log.d(TAG, "extractCplExtraInfo:lang selected variable: " + OustPreferences.getAppInstallVariable("IS_LANG_SELECTED") + " -- isLanguageScreenenabled:" + isLanguageScreenenabled);

                if (!isCityScreenEnabled) {
                    // Language Screen enabled but not yet selected
                    if (isLanguageScreenenabled && !OustPreferences.getAppInstallVariable("IS_LANG_SELECTED") && cplCollectionData != null) {
                        if (OustPreferences.get(AppConstants.StringConstants.TENANT_ID) != null && !OustPreferences.get(AppConstants.StringConstants.TENANT_ID).equalsIgnoreCase("swiggy")) { // cplCollectionData.getCplType().equalsIgnoreCase("GENERAL")
                            Log.d(TAG, "extractCplExtraInfo: Language Screen enabled but not yet selected: it is ");
                            if (isPlayList && !isContest && !isTodo) {
                                setCplView();
                            } else if (cplCollectionData != null && !isPlayList && !isTodo && !isContest) {
                                setCplView();
                            }
                        }
                    } else if (isLanguageScreenenabled && OustPreferences.getAppInstallVariable("IS_LANG_SELECTED") && OustStaticVariableHandling.getInstance().isNewCplDistributed()) {
                        //language enabled and selected and distributed new cpl
                        if (isPlayList && !isContest && !isTodo) {
                            setCplView();
                        } else if (cplCollectionData != null && !isPlayList && !isTodo && !isContest) {
                            setCplView();
                        }
                    } else if (isLanguageScreenenabled && OustPreferences.getAppInstallVariable("IS_LANG_SELECTED")) {
                        //language enabled and selected and distributed new cpl
                        if (isPlayList && !isContest && !isTodo) {
                            setCplView();
                        } else if (cplCollectionData != null && !isPlayList && !isTodo && !isContest) {
                            setCplView();
                        }
                    } else if (!isLanguageScreenenabled) {
                        //language screen disabled
                        if (isPlayList && !isContest && !isTodo) {
                            setCplView();
                        } else if (cplCollectionData != null && !isPlayList && !isTodo && !isContest) {
                            setCplView();
                        }
                    }
                } else {
                    if (isCityScreenEnabled && OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_CITY_SELECTED)) {
                        if (isLanguageScreenenabled && OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_LANG_SELECTED)) {
                            if (isPlayList && !isContest && !isTodo) {
                                setCplView();
                            } else if (cplCollectionData != null && !isPlayList && !isTodo && !isContest) {
                                setCplView();
                            }
                        } else {
                            if (isCityScreenEnabled && !OustStaticVariableHandling.getInstance().isCplLanguageScreenOpen()) {
                                OustStaticVariableHandling.getInstance().setCplCityScreenOpen(true);
                                Intent intent = new Intent(NewLandingActivity.this, CitySelection.class);
                                intent.putExtra("allowBackPress", false);
                                intent.putExtra("FEED", false);
                                intent.putExtra("openLanguage", true);
                                startActivity(intent);

                                /*Intent intent = new Intent(NewLandingActivity.this, LanguageSelectionActivity.class);
                                intent.putExtra("CPL_ID", OustPreferences.getTimeForNotification("parentCplId"));
                                intent.putExtra("allowBackPress",false);
                                intent.putExtra("FEED", false);
                                intent.putExtra("languageList",OustPreferences.get("languageList"));
                                startActivity(intent);*/

                                if (newFeedAdapter != null) {
                                    newFeedAdapter.enableButton();
                                }
                            } else {
                                if (isPlayList && !isContest && !isTodo) {
                                    setCplView();
                                } else if (cplCollectionData != null && !isPlayList && !isTodo && !isContest) {
                                    setCplView();
                                }
                            }
                        }
                    }
                }

            } else {
                OustSdkTools.showToast("CPL not found in the Base node");
                if (myDeskData != null && cplCollectionData != null && myDeskData.containsKey("CPL" + cplCollectionData.getCplId())) {
                    //myDeskData.get("CPL" + cplCollectionData.getCplId())
                    myDeskData.remove("CPL" + cplCollectionData.getCplId());
                    cplCollectionData = null;
                }
                if (cplHashMapData != null) {
                    cplHashMapData = null;
                }
                cpl_main_ll.setVisibility(View.GONE);
            }
        } else {
            OustSdkTools.showToast("CPL not found in the Base node");
            if (myDeskData != null && cplCollectionData != null && myDeskData.containsKey("CPL" + cplCollectionData.getCplId())) {
                //myDeskData.get("CPL" + cplCollectionData.getCplId())
                myDeskData.remove("CPL" + cplCollectionData.getCplId());
                cplCollectionData = null;
                if (cplHashMapData != null) {
                    cplHashMapData = null;
                }
            }
            cpl_main_ll.setVisibility(View.GONE);
        }
    }

    protected boolean isNotNull(Object o) {
        return o != null;
    }

    protected boolean getBoolean(Object o) {
        return o != null && (boolean) o;
    }

    protected String getString(Object o) {
        return o != null ? (String) o : "";
    }

    private boolean cplShown = false;
    private int falseCount = 0;

    private void setCplView() {
        /*if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION) && OustPreferences.getTimeForNotification("parentCplId")!=0  && !OustPreferences.getAppInstallVariable("IS_LANG_SELECTED")){
            clickOnCplLanguageSelection();
        }
        */

        if (isCPLShowOnCourse) {
            cpl_main_ll.setVisibility(View.GONE);
        } else {
            try {
                Log.i("count ", "" + falseCount++);
                if (cplCollectionData != null) {
                    if (cplCollectionData.getCplVersion() != null && !cplCollectionData.getCplVersion().isEmpty() && cplCollectionData.getCplVersion().equals("V2")) {
                        if (!cplShown && !OustPreferences.getAppInstallVariable("IsAssessmentPlaying")) {
                            cplShown = true;
                            CplDataHandler.getInstance().setCplCollectionData(cplCollectionData);

                            try {
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }

                                if (anim_timer != null) {
                                    no_of_loading = 0;
                                    anim_timer.cancel();
                                    anim_timer = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                            Log.d(TAG, "setCplView: CPLIntroActivity");
                            OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                            Intent intent = new Intent(NewLandingActivity.this, CplIntroActivity.class);
                            intent.putExtra("cplId", cplCollectionData.getCplId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    }
                    if (isPlayList && (!isTodo && !isContest)) {
                        cpl_main_ll.setVisibility(View.VISIBLE);
                    } else if (!isPlayList && !isContest && !isTodo) {
                        cpl_main_ll.setVisibility(View.VISIBLE);
                    } else {
                        cpl_main_ll.setVisibility(View.GONE);
                    }

                    int color = StringUtils.isBlank(toolbarColorCode) ? OustSdkTools.getColorBack(R.color.lgreen) : Color.parseColor(toolbarColorCode);
                    GradientDrawable d1 = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_up_corners);
                    d1.setColor(color);
                    float[] radii = {10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f};
                    findViewById(R.id.cpl_head_ll).setBackgroundDrawable(d1);
                    GradientDrawable d2 = new GradientDrawable();
                    d2.setColor(color);
                    d2.setCornerRadii(radii);
                    cpl_start_btn.setBackgroundDrawable(d2);
                    cpl_head_tv.setText(cplCollectionData.getCplName());
                    cpl_description.setText(cplCollectionData.getCplDescription());
                    if (isNotNull(cplCollectionData.getCurrentCplData())) {
                        findViewById(R.id.cpl_content_ll).setVisibility(View.GONE);
                    }
                    clickOnCplView();
                } else {
                    cpl_main_ll.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void clickOnCplView() {
        cpl_main_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openCPLScreen();
            }
        });
    }

    public void openCPLScreen() {
        try {
            BottomSheetBehavior.from(cpl_main_ll).setState(BottomSheetBehavior.STATE_COLLAPSED);
            Intent intent = new Intent(NewLandingActivity.this, CplBaseActivity.class);
            intent.putExtra("bgImg", cplCollectionData.getBgImage());
            intent.putExtra("cplId", cplCollectionData.getCplId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkIfNotificationEnabled() {
        Log.e("Notification", "inside checkIfNotificationEnabled() method");
        OustPreferences.saveAppInstallVariable("NotificationPopupShown", true);

        if (NotificationManagerCompat.from(OustSdkApplication.getContext()).areNotificationsEnabled()) {
            Log.e("Notification", "notification is enabled");

            if (!OustPreferences.getAppInstallVariable("auto_start_notif")) {
                askXiomiAutoStart(android.os.Build.MANUFACTURER);
            }

        } else {
            askToTurnOnNotificarion();
        }

    }

    public void askToTurnOnNotificarion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on Notifications in order to get alerted about contests, prizes, new learning modules and more.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            Intent intent = new Intent();
                            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("android.provider.extra.APP_PACKAGE", OustSdkApplication.getContext().getPackageName());
                            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", OustSdkApplication.getContext().getPackageName());
                                intent.putExtra("app_uid", OustSdkApplication.getContext().getApplicationInfo().uid);
                            } else {
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + OustSdkApplication.getContext().getPackageName()));
                            }

                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setTitle("Oust Notification");
        alert.show();
    }


    private void getLatestTrendingData(final int mainTab, final int subTab) {
        try {
            long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
            if (catalogueId != 0) {
                String catalogue_tabdataurl = getResources().getString(R.string.catalogue_tabdataurl);
                catalogue_tabdataurl = catalogue_tabdataurl.replace("{id}", ("" + catalogueId));
                catalogue_tabdataurl = catalogue_tabdataurl.replace("{mTabId}", ("" + mainTab));
                catalogue_tabdataurl = catalogue_tabdataurl.replace("{mSubTabId}", ("" + subTab));
                catalogue_tabdataurl = HttpManager.getAbsoluteUrl(catalogue_tabdataurl);
                JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

                ApiCallUtils.doNetworkCall(Request.Method.GET, catalogue_tabdataurl, jsonParams, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                latestTrendingDataLoaded = true;
                                extractCommonData(response, mainTab, subTab);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        latestTrendingDataLoaded = false;
                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, catalogue_tabdataurl, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                latestTrendingDataLoaded = true;
                                extractCommonData(response, mainTab, subTab);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        latestTrendingDataLoaded = false;
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("api-key", OustPreferences.get("api_key"));
                            params.put("org-id", OustPreferences.get("tanentid"));
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        return params;
                    }
                };
                jsonObjReq.setShouldCache(false);
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 100000f));
                OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractCommonData(JSONObject response, int mainTab, int subTab) {
        try {
            JSONArray jsonArray = response.optJSONArray("modules");
            if (jsonArray != null && jsonArray.length() > 0) {
                List<CommonLandingData> commonLandingDatas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.optJSONObject(i);
                    CommonLandingData commonLandingData = new CommonLandingData();
                    commonLandingData.setName("" + j.optString("name"));
                    commonLandingData.setBanner("" + j.optString("banner"));
                    commonLandingData.setIcon("" + j.optString("icon"));
                    commonLandingData.setDescription("" + j.optString("description"));
                    commonLandingData.setId("" + j.optInt("contentId"));
                    commonLandingData.setType("" + j.optString("contentType"));
                    if (mainTab == 1) {
                        commonLandingData.setLanding_data_type("HandPicked");
                    } else if (mainTab == 3) {
                        commonLandingData.setLanding_data_type("Grow");
                    } else {
                        commonLandingData.setLanding_data_type("Learn");
                    }
                    commonLandingDatas.add(commonLandingData);
                }
                gotLatestTrendingData(subTab, commonLandingDatas);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotLatestTrendingData(int subTab, List<CommonLandingData> commonLandingDataList) {
        try {
            if (subTab == 2) {
                latestList.addAll(commonLandingDataList);
            } else {
                trendingList.addAll(commonLandingDataList);
            }
            if (new_landing_page_type.equals("All")) {
                setCatalogImages(allCatalogItemDataArrayList);
            } else if (new_landing_page_type.equals("Grow")) {
                setCatalogImages(growCatalogItemDataArrayList);
            } else {
                setCatalogImages(learnCatalogItemDataArrayList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void getCatalogueData(final int mainTab) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
                if (catalogueId != 0) {
                    String getcatalogue_list_url = getResources().getString(R.string.getcatalogue_list_url_v2);
                    getcatalogue_list_url = getcatalogue_list_url.replace("{id}", ("" + catalogueId));
                    getcatalogue_list_url = getcatalogue_list_url.replace("{mTabId}", ("" + mainTab));
                    getcatalogue_list_url = getcatalogue_list_url.replace("{userid}", activeUser.getStudentid());
                    getcatalogue_list_url = HttpManager.getAbsoluteUrl(getcatalogue_list_url);
                    JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

                    ApiCallUtils.doNetworkCall(Request.Method.GET, getcatalogue_list_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e("catalog image data ", "main tab " + mainTab + "   data " + response.toString());
                                if (response.optBoolean("success")) {
                                    OustPreferences.save("catalog" + mainTab, response.toString());
                                    JSONObject jsonObject = new JSONObject(OustPreferences.get("catalog" + mainTab));
                                    Log.d(TAG, "onResponse: storedData:" + jsonObject.toString());
                                    extractCatalogueData(response, mainTab);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (OustStaticVariableHandling.getInstance().isInternetConnectionOff()) {
                                extractCatalogueData(null, mainTab);
                            }
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getcatalogue_list_url, jsonParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e("catalog image data ", "main tab " + mainTab + "   data " + response.toString());
                                if (response.optBoolean("success")) {
                                    OustPreferences.save("catalog"+mainTab, response.toString());
                                    JSONObject jsonObject = new JSONObject(OustPreferences.get("catalog"+mainTab));
                                    Log.d(TAG, "onResponse: storedData:"+jsonObject.toString());
                                    extractCatalogueData(response, mainTab);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (OustStaticVariableHandling.getInstance().isInternetConnectionOff()) {
                                extractCatalogueData(null, mainTab);
                            }
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("api-key", OustPreferences.get("api_key"));
                                params.put("org-id", OustPreferences.get("tanentid"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            return params;
                        }
                    };
                    jsonObjReq.setShouldCache(false);
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 100000f));
                    OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
                } else {
                    catalog_ll3.setVisibility(View.GONE);
                }
            } else {
                try {
                    String respon = OustPreferences.get("catalog" + mainTab);
                    if (respon != null && !respon.trim().isEmpty()) {
                        extractCatalogueData(new JSONObject(respon), mainTab);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractCatalogueData(JSONObject response, int mainTab) {
        try {
            if (response != null) {
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                    if (new_landing_page_type.equalsIgnoreCase("All")) {
                        allCatalogItemDataArrayList = new ArrayList<>();
                    } else {

                        if (new_landing_page_type.equalsIgnoreCase("Skill")) {
                            skillCatalogItemDataArrayList = new ArrayList<>();

                        } else {
                            learnCatalogItemDataArrayList = new ArrayList<>();
                        }
                    }
                } else {
                    if (mainTab == 1) {
                        allCatalogItemDataArrayList = new ArrayList<>();
                    } else if (mainTab == 2) {
                        learnCatalogItemDataArrayList = new ArrayList<>();
                    } else {
                        growCatalogItemDataArrayList = new ArrayList<>();
                    }
                }
                JSONArray jsonArray = null;
                jsonArray = response.optJSONArray("categoryDataList");
                long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.optJSONObject(i);
                        CatalogItemData catalogItemData = new CatalogItemData();
                        catalogItemData.setName("" + j.optString("name"));
                        catalogItemData.setBanner("" + j.optString("banner"));
                        catalogItemData.setIcon(j.optString("icon"));
                        catalogItemData.setThumbnail(j.optString("thumbnail"));
                        catalogItemData.setDescription("" + j.optString("description"));
                        catalogItemData.setId(j.optInt("contentId"));
                        catalogItemData.setViewStatus(j.optString("viewStatus"));
                        catalogItemData.setCatalogId(catalogueId);
                        catalogItemData.setCatalogCategoryId(catalogItemData.getId());
                        catalogItemData.setCatalogContentId(catalogItemData.getId());
                        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                            if (new_landing_page_type.equalsIgnoreCase("All")) {
                                allCatalogItemDataArrayList.add(catalogItemData);
                            } else if (new_landing_page_type.equalsIgnoreCase("Skill")) {
                                skillCatalogItemDataArrayList.add(catalogItemData);
                            } else {
                                learnCatalogItemDataArrayList.add(catalogItemData);
                            }
                        } else {
                            if (mainTab == 1) {
                                allCatalogItemDataArrayList.add(catalogItemData);
                            } else if (mainTab == 2) {
                                learnCatalogItemDataArrayList.add(catalogItemData);
                            } else {
                                growCatalogItemDataArrayList.add(catalogItemData);
                            }
                        }
                    }
                }
            }
            if (new_landing_page_type.equals("All")) {
                setCatalogImages(allCatalogItemDataArrayList);
            } else if (new_landing_page_type.equals("Grow")) {
                setCatalogImages(growCatalogItemDataArrayList);
            } else if (new_landing_page_type.equalsIgnoreCase("Skill")) {
                setCatalogImages(skillCatalogItemDataArrayList);
            } else {
                setCatalogImages(learnCatalogItemDataArrayList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //======================================================================================
//initialize views
    private void initViews() {
        try {


            try {
                removeAllReminderNotification();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            layout_bg_image = findViewById(R.id.layout_bg_image);
            rl_retry_landingpage = findViewById(R.id.rl_retry_landingpage);
            rl_loading_landingpage = findViewById(R.id.rl_loading_landingpage);
            rl_loading_landingpage.setVisibility(View.VISIBLE);
            rl_retry_landingpage.setVisibility(View.GONE);
            btn_refresh = findViewById(R.id.btn_refresh);

            mBottomSheetBanner = findViewById(R.id.bottom_sheet_banner);
            mImageViewContest = findViewById(R.id.imageViewContest);
            mTextViewToDoPlayListContent = findViewById(R.id.textViewTodoPlayList);
            mLinearLayoutToDoPlaylist = findViewById(R.id.linearLayoutToDoPlaylist);
            mRecyclerViewBottomSheetItems = findViewById(R.id.viewPagerBottomSheet);
            mRecyclerViewBottomSheetItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            //end for new bottom sheet to do, playlist and contest

            //cpl progressbar
            //mViewCPLProgressBar = findViewById(R.id.cplProgressBar);

            newmainlanding_layout = findViewById(R.id.newmainlanding_layout);
            newlanding_loader_progressbar = findViewById(R.id.newlanding_loader_progressbar);
            drawer_listview = findViewById(R.id.newlanding_drawerlist);
            toolbar = findViewById(R.id.tabanim_toolbar);
            mDrawerLayout = findViewById(R.id.drawer_layout);
            cpl_main_ll = findViewById(R.id.cpl_main_ll);
            cpl_description = findViewById(R.id.cpl_description);
            cpl_head_tv = findViewById(R.id.cpl_head_tv);
            cpl_start_btn = findViewById(R.id.cpl_start_btn);
            topLayout = findViewById(R.id.topLayout);

            topbanner_imageview = findViewById(R.id.topbanner_imageview);
            collapsing_toolbar = findViewById(R.id.mainappbarlayout);
            collapsing_toolbara = findViewById(R.id.collapsing_toolbar);
            learningoustcoin_progress = findViewById(R.id.learningoustcoin_progress);
            coursecomplete_presentagetext = findViewById(R.id.coursecomplete_presentagetext);

            learningcredits_text = findViewById(R.id.learningcredits_text);
            learningcredits_text.setText(getResources().getString(R.string.learning_credit_text));
            main_layout_3 = findViewById(R.id.main_layout_3);
            main_oc_layout = findViewById(R.id.main_oc_layout);
            main_progress_layout = findViewById(R.id.main_progress_layout);
            playmode_text = findViewById(R.id.playmode_text);
            learnmode_text = findViewById(R.id.learnmode_text);
            learnmode_text.setText(getResources().getString(R.string.learn));
            playmode_text.setText(getResources().getString(R.string.play_text));
            wjalert_banner = findViewById(R.id.wjalert_banner);
            wjalert_bannertext = findViewById(R.id.wjalert_bannertext);


            wjalert_banner.setOnClickListener(this);
            main_oc_layout.setOnClickListener(this);
            main_progress_layout.setOnClickListener(this);
            playmode_text.setOnClickListener(this);
            learnmode_text.setOnClickListener(this);
            topbanner_imageview.setOnClickListener(this);


            playmode_switch = findViewById(R.id.playmode_switch);
            learningmain_layout = findViewById(R.id.learningmain_layout);
            playmain_layout = findViewById(R.id.playmain_layout);
            mywebView = findViewById(R.id.webView);
            landingfrag_mainlayout = findViewById(R.id.landingfrag_mainlayout);
            event_webview_layout = findViewById(R.id.event_webview_layout);
            learn_play_modelayout = findViewById(R.id.learn_play_modelayout);
            learn_play_modelayoutbacka = findViewById(R.id.learn_play_modelayoutbacka);
            learn_play_modelayoutbacka_bgd = findViewById(R.id.learn_play_modelayoutbacka_bgd);
            OustStaticVariableHandling.getInstance().setNewViewPager(findViewById(R.id.tabanim_viewpager));
            tabLayout = findViewById(R.id.tabanim_tabs);
            mycointext = findViewById(R.id.mycointext);

            //component_user
            normal_user_layout = findViewById(R.id.normal_user_layout);
            include_componemt_user = findViewById(R.id.include_componemt_user);
            user_avatar = include_componemt_user.findViewById(R.id.user_avatar);
            user_name = include_componemt_user.findViewById(R.id.user_name);
            coins_text = include_componemt_user.findViewById(R.id.coins_text);
            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coins_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
                } else {
                    coins_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            pending_count = include_componemt_user.findViewById(R.id.pending_count);
            textView_pending = include_componemt_user.findViewById(R.id.textView_pending);

          /*  pending_count = include_componemt_user.findViewById(R.id.pending_count);
            textView = include_componemt_user.findViewById(R.id.textView);
            user_rank_count = include_componemt_user.findViewById(R.id.user_rank_count);*/
            mytask = include_componemt_user.findViewById(R.id.mytask);
            mytask.setText(getResources().getString(R.string.my_tasks_text));
            textView_pending.setText(getResources().getString(R.string.pending_text));


            catalogue_lay = findViewById(R.id.catalogue_lay);
            newLayoutbackImage = findViewById(R.id.newLayoutbackImage);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
            swiperefreshparent_layout = findViewById(R.id.swiperefreshparent_layout);
            floatingActionsMenu = findViewById(R.id.multiple_actions);
            fab_action_leaderboard = findViewById(R.id.fab_action_leaderboard);
            action_favourite = findViewById(R.id.action_favourite);

            fab_action_joinmeeting = findViewById(R.id.fab_action_joinmeeting);
            fab_action_joinmeeting.setOnClickListener(this);

            fabBaseLayout = findViewById(R.id.fab_base_layout);
            newlanding_topimagelayout = findViewById(R.id.newlanding_topimagelayout);
            mainlanding_toplayout = findViewById(R.id.mainlanding_toplayout);
            newmainlanding_topsublayout = findViewById(R.id.newmainlanding_topsublayout);
            nocourse_text = findViewById(R.id.nocourse_text);
            txt_loader = findViewById(R.id.txt_loader);
            txt_loader.setText(getResources().getString(R.string.loading));
            ffcbanner_imglayout = findViewById(R.id.ffcbanner_imglayout);
            ffcbanner_img = findViewById(R.id.ffcbanner_img);
            lefttopbox = findViewById(R.id.lefttopbox);
            lefttopbox_bgd = findViewById(R.id.lefttopbox_bgd);
            coinimage_view = findViewById(R.id.coinimage_view);
            ffcbanner_statusimg = findViewById(R.id.ffcbanner_statusimg);
            fab_action_leaderboard.setOnClickListener(this);
            action_favourite.setOnClickListener(this);
            ffcbanner_img.setOnClickListener(this);
            fabBaseLayout.setOnClickListener(this);
            addModuleGrid = findViewById(R.id.addModuleGrid);
            nomodule_text = findViewById(R.id.nomodule_text);

            newlanding_layout = findViewById(R.id.newlanding_layout);
            catalogue_label = findViewById(R.id.catalogue_label);
            catalogue_labelline = findViewById(R.id.catalogue_labelline);
            seeall_cataloguelist = findViewById(R.id.seeall_cataloguelist);
            tabimage_subLayout = findViewById(R.id.tabimage_subLayout);
            initLayout2Views();

            mLayoutUserLogoutPopup = findViewById(R.id.layoutUserLogoutPopup);
            mButtonSkipLogout = findViewById(R.id.buttonSkipLogout);
            mButtomLogout = findViewById(R.id.buttonLogout);
            mTextviewLogoutMessage = findViewById(R.id.textviewLogoutMessage);
            mLayoutUserLogoutPopup.setVisibility(View.GONE);

            nomodule_text.setText(getResources().getString(R.string.no_module_yet));
            catalogue_label.setText(getResources().getString(R.string.catalogue_label));
            catalogue_labelline.setText(getResources().getString(R.string.catalogue_label));
            seeall_cataloguelist.setText(getResources().getString(R.string.see_all));

            OustSdkTools.setImage(topbanner_imageview, getResources().getString(R.string.challenge_and_win));
            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coinimage_view.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                } else {
                    OustSdkTools.setImage(coinimage_view, getResources().getString(R.string.coins_icon));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            OustSdkTools.setImage(lefttopbox_bgd, getResources().getString(R.string.bg_subject));
            OustSdkTools.setImage(learn_play_modelayoutbacka_bgd, getResources().getString(R.string.bg_subject));

            /*try{
                layout_bg_image.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(layout_bg_image, getResources().getString(R.string.bg_1));
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }*/

            user_avatar.setOnClickListener(v -> {
                try {
                    if (!OustPreferences.getAppInstallVariable("hideUserSetting")) {
                        Intent intent = new Intent(NewLandingActivity.this, UserSettingActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            });

            mButtomLogout.setOnClickListener(view -> {
                mLayoutUserLogoutPopup.setVisibility(View.GONE);
                startLogout();
            });

            mButtonSkipLogout.setOnClickListener(view -> {
                mLayoutUserLogoutPopup.setVisibility(View.GONE);
                //skipLogout();
            });

            view_blak_overlay = findViewById(R.id.view_blak_overlay);
            view_blak_overlay.setVisibility(View.GONE);
            layoutUserOverallcredits = findViewById(R.id.layoutUserOverallcredits);

            mytask.setOnClickListener(v -> {
                OustDataHandler.getInstance().setMyDeskData(myDeskData);
                OustDataHandler.getInstance().setMyAssessmentData(myAssessmentData);

                Intent intent = new Intent(NewLandingActivity.this, MyTasksScreen.class);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                startActivity(intent);
            });

            btn_refresh.setOnClickListener(v -> {
                Log.d(TAG, "initViews: btn_refresh");
                initializeAllData();
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private PopupWindow mPopupWindow;
    int scrWidth = 0;
    int scrHeight = 0;

    private void initWelComePopViews(final DTONewFeed newFeed) {
        try {
            if (mPopupWindow != null && mPopupWindow.isShowing())
                mPopupWindow.dismiss();
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            scrWidth = metrics.widthPixels;
            scrHeight = metrics.heightPixels;

            scrWidth = (int) (scrWidth - 0.1 * scrWidth);
            scrHeight = (int) (scrHeight - 0.1 * scrHeight);

            // Inflate the custom layout/view

            View customView = inflater.inflate(R.layout.welcome_popup_window, null);
            mPopupWindow = new PopupWindow(
                    customView,
                    scrWidth,
                    scrHeight
            );

            HeavyCustomTextView title = customView.findViewById(R.id.textViewFeedTitlePop);
            HeavyCustomTextView start = customView.findViewById(R.id.textViewNext);
            LinearLayout linearLayout = customView.findViewById(R.id.linearLayoutStart);
            CustomTextView desc = customView.findViewById(R.id.textViewFeedDescriptionpop);
            ImageView imageViewBg = customView.findViewById(R.id.imageViewBg);
            ImageView imageViewVlose = customView.findViewById(R.id.close);
            ImageView imageViewStart = customView.findViewById(R.id.feed_start);
            LinearLayout mLinearLayoutVideo = customView.findViewById(R.id.linearLayoutVideo);
            ImageView imageViewPlayVideo = customView.findViewById(R.id.imageViewVideoPlay);
            LinearLayout linearLayoutOverlay = customView.findViewById(R.id.linearLayoutBlackOverlay);
            LinearLayout linearLayoutContent = customView.findViewById(R.id.linearLayoutContent);


            imageViewPlayVideo.setOnClickListener(v -> {
                if (newFeed.getCourseCardClass() != null && newFeed.getFeedType().equals(FeedType.COURSE_CARD_L)) {
                    OustDataHandler.getInstance().setCourseCardClass(newFeed.getCourseCardClass());
                    Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                    intent.putExtra("type", "card");
                    startActivity(intent);
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.dismiss();
                }
            });


            imageViewVlose.setOnClickListener(v -> {
                Log.d(TAG, "onClick: ");
                mPopupWindow.dismiss();
            });

            linearLayout.setOnClickListener(v -> {
                if (newFeed.getCourseCardClass() != null && newFeed.getFeedType().equals(FeedType.COURSE_CARD_L)) {
                    OustDataHandler.getInstance().setCourseCardClass(newFeed.getCourseCardClass());
                    Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                    intent.putExtra("type", "card");
                    startActivity(intent);
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.dismiss();
                    //  Toast.makeText(NewLandingActivity.this, "Feed is not a card type", Toast.LENGTH_LONG).show();
                }
            });


            if (toolbarColorCode != null) {
                start.setTextColor(Color.parseColor(toolbarColorCode));
                int appColor = Color.parseColor(toolbarColorCode);
                imageViewStart.setColorFilter(appColor);
            }

            if (newFeed != null) {
                if (newFeed.getHeader() != null) {
                    title.setText(Html.fromHtml(newFeed.getHeader()));
                    if (!newFeed.isTitleVisible()) {
                        title.setVisibility(View.GONE);
                    } else {
                        title.setVisibility(View.VISIBLE);
                    }
                }
                if (newFeed.getContent() != null) {
                    desc.setText(Html.fromHtml(newFeed.getContent()));
                    if (!newFeed.isDescVisible()) {
                        desc.setVisibility(View.GONE);
                    } else {
                        desc.setVisibility(View.VISIBLE);
                    }
                }
                if (newFeed.getImageUrl() != null && newFeed.getImageUrl() != "" && !newFeed.getImageUrl().equals(""))
                    Picasso.get().load(newFeed.getImageUrl()).into(imageViewBg);
                if (newFeed.getmSpecialFeedStartText() != null) {
                    start.setText(newFeed.getmSpecialFeedStartText());
                    start.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(R.dimen.oustlayout_dimen40, 0, 0, 0);
                    // imageViewStart.setLayoutParams(lp);
                } else {
                    start.setVisibility(View.GONE);
                }

                if (!newFeed.isTitleVisible() && !newFeed.isDescVisible()) {
                    linearLayoutContent.setVisibility(View.GONE);
                    linearLayoutOverlay.setVisibility(View.GONE);
                }
            }


            DTOCourseCard courseCardClass = newFeed.getCourseCardClass();
    /*    if(courseCardClass!=null)
        {
            if ((courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) || (courseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                if ( (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                    for (int i = 0; i < courseCardClass.getCardMedia().size(); i++)
                    {
                        CourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                        if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                            switch (courseCardMedia.getMediaType()) {

                                case "VIDEO":
                                    desc.setVisibility(View.GONE);
                                    imageViewPlayVideo.setVisibility(View.VISIBLE);
                                    break;
                                case "YOUTUBE_VIDEO":
                                    String youtubeKey = courseCardMedia.getData();

                                    if (youtubeKey.contains("https://www.youtube.com/watch?v=")) {
                                        youtubeKey = youtubeKey.replace("https://www.youtube.com/watch?v=", "");
                                    }
                                    if (youtubeKey.contains("https://youtu.be/")) {
                                        youtubeKey = youtubeKey.replace("https://youtu.be/", "");
                                    }
                                    if (youtubeKey.contains("&")) {
                                        int state = youtubeKey.indexOf("&");
                                        youtubeKey = youtubeKey.substring(0, state);
                                    }
                                    desc.setVisibility(View.GONE);
                                    imageViewPlayVideo.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    }
                }
            }
        }*/


            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.showAtLocation(mDrawerLayout, Gravity.CENTER, 0, 0);
            dimBehind(mPopupWindow);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.8f;
        wm.updateViewLayout(container, p);
    }

    private Intent loginIntent;

    public void startLogout() {
        try {
            if (loginDataMap == null) {
                OustSdkTools.showToast("");
                return;
            }
            Log.e(TAG, " inside logoutAndEnterpriseLogin() ");
            loginIntent = new Intent().setComponent(new ComponentName("com.oustme.oustapp", "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));

            loginIntent.putExtra("comingFormOtherActivity", true);
            loginIntent.putExtra("mobileNum", loginDataMap.get("mobileNum"));
            loginIntent.putExtra("orgId", loginDataMap.get("orgId"));
            if (loginDataMap.get("mobileNum") != null) {
                loginIntent.putExtra("mobileNum", loginDataMap.get("mobileNum"));
                Log.e("AIRTEL", "got the data in splash inside logoutAndEnterpriseLogin()" + loginDataMap.get("mobileNum"));
            }
            if (loginDataMap.get("userId") != null) {
                loginIntent.putExtra("userId", loginDataMap.get("userId"));
            }
            if (loginDataMap.get("userIdentifier") != null) {
                loginIntent.putExtra("userIdentifier", loginDataMap.get("userIdentifier"));
            }
            if (loginDataMap.get("emailId") != null) {
                loginIntent.putExtra("emailId", loginDataMap.get("emailId"));
            }
            if (loginDataMap.get("fname") != null) {
                loginIntent.putExtra("fname", loginDataMap.get("fname"));
            }
            if (loginDataMap.get("lname") != null) {
                loginIntent.putExtra("lname", loginDataMap.get("lname"));
            }
            if (loginDataMap.get("password") != null) {
                loginIntent.putExtra("password", loginDataMap.get("password"));
            }
            if (loginDataMap.get("languagePrefix") != null) {
                loginIntent.putExtra("languagePrefix", loginDataMap.get("languagePrefix"));
            }

            if (loginDataMap.get("application") != null) {
                loginIntent.putExtra("application", loginDataMap.get("application"));
            }
            if (loginDataMap.get("applicationId") != null) {
                loginIntent.putExtra("applicationId", loginDataMap.get("applicationId"));
            }

            if (loginDataMap.get("appVersion") != null) {
                loginIntent.putExtra("appVersion", loginDataMap.get("appVersion"));
            }
            if (loginDataMap.get("userAgent") != null) {
                loginIntent.putExtra("userAgent", loginDataMap.get("userAgent"));
            }

            if (loginDataMap.get("deviceId") != null) {
                loginIntent.putExtra("deviceId", loginDataMap.get("deviceId"));
            }
            if (loginDataMap.get("tokenId") != null) {
                loginIntent.putExtra("tokenId", loginDataMap.get("tokenId"));
            }

            if (loginDataMap.get("isAuthorizationReq") != null) {
                if (loginDataMap.get("isAuthorizationReq").equalsIgnoreCase("true")) {
                    loginIntent.putExtra("isAuthorizationReq", true);
                }
            }

            onLogout();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initBottomSheetBannerData() {
        try {
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {

                mListBottomSheetItems = new ArrayList<>();
                mListBottomSheetItems.add("To Do");
                mListBottomSheetItems.add("Contest");
                mListBottomSheetItems.add("Play list");

                mBottomItemModelList = new ArrayList<>();

                BottomItemModel bottomItemModel = new BottomItemModel();
                bottomItemModel.setmContent("Complete your pending modules");
                bottomItemModel.setmTitle("To Do");
                bottomItemModel.setType(0);

                BottomItemModel bottomItemModel2 = new BottomItemModel();
                bottomItemModel2.setmContent("Complete your pending modules");
                bottomItemModel2.setmTitle("Contest");
                bottomItemModel2.setType(1);

                BottomItemModel bottomItemModel3 = new BottomItemModel();
                bottomItemModel3.setmContent("Play List and Enjoy Learning");
                bottomItemModel3.setmTitle("Play List");
                bottomItemModel3.setType(2);

                isContest = OustStaticVariableHandling.getInstance().isContestLive();
                //  Log.d(TAG, "initBottomSheetBannerData: istodo:"+isTodo+" isplay:"+isPlayList+" isContest:"+isContest);
                if (isTodo)
                    mBottomItemModelList.add(bottomItemModel);
                if (isContest)
                    mBottomItemModelList.add(bottomItemModel2);
                if (isPlayList)
                    mBottomItemModelList.add(bottomItemModel3);

                mRecyclerViewBottomSheetItems.setAdapter(new BottomSheetItemRecyclerAdapter(this, mBottomItemModelList));
                setBottomSheetItems();
            }
        } catch (Exception e) {
        }
    }

    private void initListener2() {
        RecyclerViewItemClickSupport.addTo(mRecyclerViewBottomSheetItems).setOnItemClickListener(new RecyclerViewItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if (mBottomItemModelList.get(position).getmTitle().contains("Contest")) {
                    gotoFFCContest();
                } else if (mBottomItemModelList.get(position).getmTitle().contains("To Do")) {
                    AllPendingItemsDataList();
                } else if (mBottomItemModelList.get(position).getmTitle().contains("Play")) {
                    if (cplCollectionData != null) {
                        try {
                            openCPLScreen();
                            /*BottomSheetBehavior.from(cpl_main_ll).setState(BottomSheetBehavior.STATE_COLLAPSED);
                            Intent intent = new Intent(NewLandingActivity.this, CplBaseActivity.class);
                            intent.putExtra("bgImg", cplCollectionData.getBgImage());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*/
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else if (cplCollectionData == null) {
                        Toast.makeText(NewLandingActivity.this, "There is no Play List Assigned to you", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        mLinearLayoutToDoPlaylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPendingItemsDataList();
            }
        });
        if (todo_txt_c != null) {
            todo_txt_c.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_TODO_ONCLICK)) {
                        AllPendingItemsDataList();
                    }
                }
            });
        }
    }

    private void BottomBannerVisibility(boolean value) {
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
            if (value) {
                mBottomSheetBanner.setVisibility(View.VISIBLE);
            } else {
                if (OustPreferences.get("tanentid").contains("frl")) {
                    cpl_main_ll.setVisibility(View.GONE);
                }
                mBottomSheetBanner.setVisibility(View.GONE);
            }
        }
    }

    private void setBottomSheetItems() {
        if (isTodo || isPlayList) {
            mLinearLayoutToDoPlaylist.setVisibility(View.VISIBLE);
        } else {
            mLinearLayoutToDoPlaylist.setVisibility(View.GONE);
        }

        isContest = OustStaticVariableHandling.getInstance().isContestLive();
        //only contest there
        if (isContest && !isTodo && !isPlayList) {
            mRecyclerViewBottomSheetItems.setVisibility(View.GONE);
            mImageViewContest.setVisibility(View.GONE);
            mTextViewToDoPlayListContent.setVisibility(View.GONE);
            mItemSelected = 1;

        }
        //only isTodo
        else if (isTodo && !isPlayList && !isContest) {
            ffcbanner_imglayout.setVisibility(View.GONE);
            mRecyclerViewBottomSheetItems.setVisibility(View.VISIBLE);
            mItemSelected = 0;
            mImageViewContest.setVisibility(View.GONE);
            mTextViewToDoPlayListContent.setVisibility(View.VISIBLE);
            mTextViewToDoPlayListContent.setText(mBottomItemModelList.get(0).getmContent());
            //mLinearLayoutToDoPlaylist.setVisibility(View.VISIBLE);
        } else if (!isPlayList && !isTodo && !isContest) {
            setCplView();
        }
        //only play List
        else if (isPlayList && !isTodo && !isContest) {
            ffcbanner_imglayout.setVisibility(View.GONE);
            mRecyclerViewBottomSheetItems.setVisibility(View.GONE);
            mItemSelected = 2;
            mImageViewContest.setVisibility(View.GONE);
            mTextViewToDoPlayListContent.setVisibility(View.GONE);
            mTextViewToDoPlayListContent.setText(mBottomItemModelList.get(0).getmContent());
            setCplView();
            //cpl_main_ll.setVisibility(View.VISIBLE);

            // mLinearLayoutToDoPlaylist.setVisibility(View.VISIBLE);
        } else if (isPlayList && isContest && !isTodo) {
            ffcbanner_imglayout.setVisibility(View.GONE);
            mRecyclerViewBottomSheetItems.setVisibility(View.VISIBLE);
            mImageViewContest.setVisibility(View.GONE);
            mTextViewToDoPlayListContent.setVisibility(View.VISIBLE);
            mTextViewToDoPlayListContent.setText(mBottomItemModelList.get(0).getmContent());
            //mLinearLayoutToDoPlaylist.setVisibility(View.VISIBLE);
        } else if (isContest && isTodo && !isPlayList) {
            ffcbanner_imglayout.setVisibility(View.GONE);
            mRecyclerViewBottomSheetItems.setVisibility(View.VISIBLE);
            mImageViewContest.setVisibility(View.GONE);
            mTextViewToDoPlayListContent.setVisibility(View.VISIBLE);
            mTextViewToDoPlayListContent.setText(mBottomItemModelList.get(0).getmContent());
            //  mLinearLayoutToDoPlaylist.setVisibility(View.VISIBLE);
        } else {
            ffcbanner_imglayout.setVisibility(View.GONE);
            mRecyclerViewBottomSheetItems.setVisibility(View.VISIBLE);
            mImageViewContest.setVisibility(View.GONE);
            mTextViewToDoPlayListContent.setVisibility(View.VISIBLE);
            // mTextViewToDoPlayListContent.setText(mBottomItemModelList.get(0).getmContent());
        }
        ffcBannerStatus();
    }

    private void initLayout2Views() {
        //if layout changed dynamically

        OustStaticVariableHandling.getInstance().getNewViewPager().setVisibility(View.VISIBLE);
        landingnewprogress_bar = findViewById(R.id.landingnewprogress_bar);
        my_progress_text = findViewById(R.id.my_progress_text);
        usercointext = findViewById(R.id.usercointext);

        user_coursecompletedtext = findViewById(R.id.user_coursecompletedtext);
        user_coursecompletedlabelline = findViewById(R.id.user_coursecompletedlabelline);
        user_coursependingtext = findViewById(R.id.user_coursependingtext);
        user_coursependingoctext = findViewById(R.id.user_coursependingoctext);
        user_coursependinglabelline = findViewById(R.id.user_coursependinglabelline);
        user_coursecompletedlabel = findViewById(R.id.user_coursecompletedlabel);
        user_coursependinglabel = findViewById(R.id.user_coursependinglabel);
        user_coursecompletedoctext = findViewById(R.id.user_coursecompletedoctext);
        completedcourses_layout = findViewById(R.id.completedcourses_layout);
        pendingcourses_layout = findViewById(R.id.pendingcourses_layout);
        usermain_profile = findViewById(R.id.usermain_profile);

        newmainlanding_toplayout = findViewById(R.id.newmainlanding_toplayout);
        user_mainnametext = findViewById(R.id.user_mainnametext);
        userprofileImageText = findViewById(R.id.userprofileImageText);
        bannerclose_btn = findViewById(R.id.bannerclose_btn);
        showAllcoursetextlayout = findViewById(R.id.showAllcoursetextlayout);
        showAllcoursetext = findViewById(R.id.showAllcoursetext);
        showAllcoursetext_line = findViewById(R.id.showAllcoursetext_line);
        nodatalabel = findViewById(R.id.nodatalabel);

        refresh_layout = findViewById(R.id.refresh_layouta);
        refresh_text = findViewById(R.id.refresh_texta);
        refresh_text_line = findViewById(R.id.refresh_text_linea);

        nodatalabel.setText(getResources().getString(R.string.no_data_available));
        user_coursecompletedlabel.setText(getResources().getString(R.string.completed));
        user_coursecompletedlabelline.setText(getResources().getString(R.string.completed));
        user_coursependinglabel.setText(getResources().getString(R.string.pending_text));
        user_coursependinglabelline.setText(getResources().getString(R.string.pending_text));
        my_progress_text.setText(getResources().getString(R.string.my_progress));
        showAllcoursetext.setText(getResources().getString(R.string.view_all_text));
        showAllcoursetext_line.setText(getResources().getString(R.string.view_all_text));
        refresh_text.setText(getResources().getString(R.string.refresh));
        refresh_text_line.setText(getResources().getString(R.string.refresh));
        refresh_layout.setVisibility(View.GONE);

        if (mainlanding_toplayout != null) {
            mainlanding_toplayout.setVisibility(View.GONE);
            newmainlanding_toplayout.setVisibility(View.GONE);
            newlanding_topimagelayout.setVisibility(View.GONE);
            newlanding_layout.setVisibility(View.GONE);
        }

        showAllcoursetextlayout.setOnClickListener(this);
        pendingcourses_layout.setOnClickListener(this);
        completedcourses_layout.setOnClickListener(this);
        bannerclose_btn.setOnClickListener(this);
        usermain_profile.setOnClickListener(this);
        userprofileImageText.setOnClickListener(this);
        refresh_layout.setOnClickListener(this);
        setProgressBarColor();
    }

    private void initLayout3Views() {
        if (mainlanding_toplayout != null) {
            mainlanding_toplayout.setVisibility(View.GONE);
            newmainlanding_toplayout.setVisibility(View.GONE);
            newlanding_topimagelayout.setVisibility(View.GONE);
            newlanding_layout.setVisibility(View.GONE);
        }
        landingnewprogress_bar = findViewById(R.id.landingnewprogress_bara);
        usercointext = findViewById(R.id.usercointexta);
        user_coursecompletedtext = findViewById(R.id.user_coursecompletedtexta);
        user_coursependingtext = findViewById(R.id.user_coursependingtexta);
        user_coursependingoctext = findViewById(R.id.user_coursependingoctexta);
        user_coursecompletedlabel = findViewById(R.id.user_coursecompletedlabela);
        user_coursependinglabel = findViewById(R.id.user_coursependinglabela);
        user_coursecompletedoctext = findViewById(R.id.user_coursecompletedoctexta);
        my_progress_text = findViewById(R.id.my_progress_texta);
        learningcredits_text = findViewById(R.id.learningcredits_texta);
        showAllcoursetext = findViewById(R.id.showAllcoursetexta);
        showAllcoursetext_line = findViewById(R.id.showAllcoursetext_linea);
        myDeskLayout = findViewById(R.id.my_desk_layout);
        latestLayout = findViewById(R.id.latest_layout);
        trendingLayout = findViewById(R.id.trending_layout);
        ll_loading = findViewById(R.id.ll_loading);
        completedcourses_layout = findViewById(R.id.completedcourses_layouta);
        pendingcourses_layout = findViewById(R.id.pendingcourses_layouta);
        usermain_profile = findViewById(R.id.usermain_profilea);

        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_text = findViewById(R.id.refresh_text);
        refresh_text_line = findViewById(R.id.refresh_text_line);

        newmainlanding_toplayout = findViewById(R.id.newmainlanding_toplayouta);
        user_mainnametext = findViewById(R.id.user_mainnametexta);
        userprofileImageText = findViewById(R.id.userprofileImageTexta);
        toptab_texta = findViewById(R.id.toptab_texta);
        toptab_textb = findViewById(R.id.toptab_textb);
        toptab_textc = findViewById(R.id.toptab_textc);
        toptab_layout = findViewById(R.id.toptab_layout);

        subtaba_text = findViewById(R.id.subtaba_text);
        subtaba_textlabel = findViewById(R.id.subtaba_textlabel);
        subtabb_text = findViewById(R.id.subtabb_text);
        subtabb_textlabel = findViewById(R.id.subtabb_textlabel);
        subtabc_text = findViewById(R.id.subtabc_text);
        subtabc_textlabel = findViewById(R.id.subtabc_textlabel);

        seeall_text = findViewById(R.id.seeall_text);

        seeall_text.setTextColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));

        seeall_courseslayout = findViewById(R.id.seeall_courseslayout);
        catelog_layouta = findViewById(R.id.catelog_layouta);
        catelog_layoutb = findViewById(R.id.catelog_layoutb);
        catelog_layoutc = findViewById(R.id.catelog_layoutc);
        catelog_layoutd = findViewById(R.id.catelog_layoutd);
        no_data_text = findViewById(R.id.no_data_text);
        see_all_catalaog = findViewById(R.id.see_all_catalaog);
/*
        catalogIndicatorA = findViewById(R.id.CatalogueUpdateIndicatora);
        catalogIndicatorB = findViewById(R.id.CatalogueUpdateIndicatorb);
        catalogIndicatorC = findViewById(R.id.CatalogueUpdateIndicatorc);
        catalogIndicatorD = findViewById(R.id.CatalogueUpdateIndicatord);*/

        newlandinghorizontal_recyclerviewa = findViewById(R.id.newlandinghorizontal_recyclerviewa);
        newlandinghorizontal_recyclerviewb = findViewById(R.id.newlandinghorizontal_recyclerviewb);
        newlandinghorizontal_recyclerviewc = findViewById(R.id.newlandinghorizontal_recyclerviewc);

        bottomcatalogue_layout = findViewById(R.id.bottomcatalogue_layout);
        bottomcatalogue_layouta = findViewById(R.id.bottomcatalogue_layouta);
        catelog_iamgea = findViewById(R.id.catelog_iamgea);
        catelog_iamgeb = findViewById(R.id.catelog_iamgeb);
        catelog_iamgec = findViewById(R.id.catelog_iamgec);
        catelog_iamged = findViewById(R.id.catelog_iamged);
        offlinemde_layout = findViewById(R.id.offlinemde_layout);

        tabImgA = findViewById(R.id.tab_imga);
        tabImgB = findViewById(R.id.tab_imgb);
        tabImgC = findViewById(R.id.tab_imgc);
        tabImgAIndicator = findViewById(R.id.tab_imga_indicator);
        tabImgBIndicator = findViewById(R.id.tab_imgb_indicator);
        tabImgCIndicator = findViewById(R.id.tab_imgC_indicator);

        subtab_layout = findViewById(R.id.subtab_layout);
        maintab_text_layout = findViewById(R.id.maintab_text_layout);
        maintab_image_layout = findViewById(R.id.maintab_image_layout);

        toptab_lineb = findViewById(R.id.toptab_lineb);
        toptab_linea = findViewById(R.id.toptab_linea);
        bannerclose_btn = findViewById(R.id.bannerclose_btn);
        showAllcoursetextlayout = findViewById(R.id.showAllcoursetextlayouta);

        no_data_text.setText(getResources().getString(R.string.no_data_in_module));
        showAllcoursetext.setText(getResources().getString(R.string.view_all_text));
        showAllcoursetext_line.setText(getResources().getString(R.string.view_all_text));
        learningcredits_text.setText(getResources().getString(R.string.learning_credit_text));
        user_coursependinglabel.setText(getResources().getString(R.string.pending_text));
        my_progress_text.setText(getResources().getString(R.string.my_progress));
        toptab_textb.setText(getResources().getString(R.string.learn));
        toptab_textc.setText(getResources().getString(R.string.grow));
        toptab_texta.setText(getResources().getString(R.string.to_do));
        subtaba_text.setText(getResources().getString(R.string.my_desk_text));
        subtabb_text.setText(getResources().getString(R.string.latest));
        subtabc_text.setText(getResources().getString(R.string.trending));
        seeall_text.setText(getResources().getString(R.string.see_all));
        user_coursecompletedlabel.setText(getResources().getString(R.string.completed));
        refresh_text.setText(getResources().getString(R.string.refresh));
        refresh_text_line.setText(getResources().getString(R.string.refresh));
        refresh_layout.setVisibility(View.GONE);

        showAllcoursetextlayout.setOnClickListener(this);
        pendingcourses_layout.setOnClickListener(this);
        completedcourses_layout.setOnClickListener(this);
        toptab_texta.setOnClickListener(this);
        toptab_textb.setOnClickListener(this);
        toptab_textc.setOnClickListener(this);
        subtaba_text.setOnClickListener(this);
        subtabb_text.setOnClickListener(this);
        subtabc_text.setOnClickListener(this);
        seeall_courseslayout.setOnClickListener(this);
        bannerclose_btn.setOnClickListener(this);
        catelog_layouta.setOnClickListener(this);
        catelog_layoutb.setOnClickListener(this);
        catelog_layoutc.setOnClickListener(this);
        catelog_layoutd.setOnClickListener(this);
        see_all_catalaog.setOnClickListener(this);
        usermain_profile.setOnClickListener(this);
        userprofileImageText.setOnClickListener(this);
        refresh_layout.setOnClickListener(this);


        tabImgA.setOnClickListener(this);
        tabImgB.setOnClickListener(this);
        tabImgC.setOnClickListener(this);

        myDeskLayout.setOnClickListener(this);
        latestLayout.setOnClickListener(this);
        trendingLayout.setOnClickListener(this);

        setSelectedTabColors(toptab_texta, toptab_textb, toptab_textc, 1);
        setSelectedSubTabColors(subtaba_text, subtabc_text, subtabb_text, subtaba_textlabel, subtabc_textlabel, subtabb_textlabel);
        setProgressBarColor();
    }

    private FrameLayout feed_layout;
    private RecyclerView newsfeed_recyclerview, newCourse_recyclerview, newChallenges_recyclerview, filter_vertical_rv;
    //private FeedRecyclerView newsfeed_recyclerview;
    private LinearLayout tab_layout_c;
    private RelativeLayout filter_ll, tabAspace, tabBspace, tabCspace, tabDspace, tabEspace, sub_tab_3;
    private LinearLayout catalog_ll3;
    private LinearLayout filter_category_ll, clearFilters_ll, applyfilter_ll, filter_text_ll, see_all_3;
    private TextView applyfilter_text, clearFilters_text, filter_text;
    private int filterType = 0;

    private void initLayout4Views() {
        if (mainlanding_toplayout != null) {
            mainlanding_toplayout.setVisibility(View.GONE);
            newmainlanding_toplayout.setVisibility(View.GONE);
            newlanding_topimagelayout.setVisibility(View.GONE);
            newlanding_layout.setVisibility(View.GONE);
        }
        mainlanding_toplayout.setVisibility(View.GONE);
        newlanding_topimagelayout.setVisibility(View.GONE);

        landingnewprogress_bar = findViewById(R.id.landingnewprogress_barc);
        usercointext = findViewById(R.id.usercointextc);
        user_coursecompletedtext = findViewById(R.id.user_coursecompletedtextc);
        user_coursependingtext = findViewById(R.id.user_coursependingtextc);
        user_coursependingoctext = findViewById(R.id.user_coursependingoctextc);
        user_coursecompletedlabel = findViewById(R.id.user_coursecompletedlabelc);
        user_coursependinglabel = findViewById(R.id.user_coursependinglabelc);
        user_coursecompletedoctext = findViewById(R.id.user_coursecompletedoctextc);
        my_progress_text = findViewById(R.id.my_progress_textc);
        learningcredits_text = findViewById(R.id.learningcredits_textc);
        completedcourses_layout = findViewById(R.id.completedcourses_layoutc);
        pendingcourses_layout = findViewById(R.id.pendingcourses_layoutc);
        usermain_profile = findViewById(R.id.usermain_profilec);
        swiperefreshparent_layout = findViewById(R.id.swiperefreshparent_layoutc);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layoutc);
        refresh_layout = findViewById(R.id.refresh_layoutc);
        refresh_text = findViewById(R.id.refresh_textc);
        refresh_text_line = findViewById(R.id.refresh_text_linec);
        no_data_text = findViewById(R.id.no_data_text3);

        DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
        int scrWidth = metrics.widthPixels;
        int height = (int) ((((scrWidth - OustSdkTools.getDpForPixel(16)) / 2) - (OustSdkTools.getDpForPixel(16))) * 0.94);
        try {
            LinearLayout.LayoutParams paramsNoData = (LinearLayout.LayoutParams) no_data_text.getLayoutParams();
            paramsNoData.height = height;
            no_data_text.setLayoutParams(paramsNoData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        newmainlanding_toplayout = findViewById(R.id.newmainlanding_toplayoutc);
        user_mainnametext = findViewById(R.id.user_mainnametextc);
        userprofileImageText = findViewById(R.id.userprofileImageTextc);
        newmainlanding_toplayout.setVisibility(View.VISIBLE);
        collapsing_toolbar = findViewById(R.id.mainappbarlayoutc);
        filter_category_ll = findViewById(R.id.filter_category_ll);
        clearFilters_ll = findViewById(R.id.clearFilters_ll);
        applyfilter_ll = findViewById(R.id.applyfilter_ll);
        feed_layout = findViewById(R.id.feed_layout);
        newsfeed_recyclerview = findViewById(R.id.alert_rv);
        newCourse_recyclerview = findViewById(R.id.course_rv);
        newChallenges_recyclerview = findViewById(R.id.challenges_rv);
        playList_rv = findViewById(R.id.playList_rv);
        play_skill_rv = findViewById(R.id.play_skill_rv);
        noticeBoard_rv = findViewById(R.id.noticeBoard_rv);
        todo_txt_c = findViewById(R.id.todo_txt_c);

        tab_layout_c = findViewById(R.id.tab_layout_c);
        filter_ll = findViewById(R.id.filter_ll);
        clearFilters_text = findViewById(R.id.clearFilters_text);
        applyfilter_text = findViewById(R.id.applyfilter_text);
        filter_text = findViewById(R.id.filter_text);
        filter_text_ll = findViewById(R.id.filter_text_ll);
        sub_tab_3 = findViewById(R.id.sub_tab_3);
        see_all_3 = findViewById(R.id.see_all_3);


        tabImgA = findViewById(R.id.tab_img_3a);
        tabImgB = findViewById(R.id.tab_img_3b);
        tabImgC = findViewById(R.id.tab_img_3c);
        tabImgD = findViewById(R.id.tab_img_3d);

        toptab_texta = findViewById(R.id.tab_text_3a);
        toptab_textb = findViewById(R.id.tab_text_3b);
        toptab_textc = findViewById(R.id.tab_text_3c);
        toptab_textd = findViewById(R.id.tab_text_3d);

        tabBigImgA = findViewById(R.id.tabBigImgA);
        tabBigImgB = findViewById(R.id.tabBigImgB);
        tabBigImgC = findViewById(R.id.tabBigImgC);
        tabBigImgD = findViewById(R.id.tabBigImgD);

        tabEspace = findViewById(R.id.tabEspace);
        tabDspace = findViewById(R.id.tabDspace);
        tabCspace = findViewById(R.id.tabCspace);
        tabBspace = findViewById(R.id.tabBspace);
        tabAspace = findViewById(R.id.tabAspace);

        ll_loading = findViewById(R.id.ll_loading3);

        catelog_layouta = findViewById(R.id.catelog_layouta3);
        catelog_layoutb = findViewById(R.id.catelog_layoutb3);
        catelog_layoutc = findViewById(R.id.catelog_layoutc3);
        catelog_layoutd = findViewById(R.id.catelog_layoutd3);

        catelog_iamgea = findViewById(R.id.catelog_iamgea3);
        catelog_iamgeb = findViewById(R.id.catelog_iamgeb3);
        catelog_iamgec = findViewById(R.id.catelog_iamgec3);
        catelog_iamged = findViewById(R.id.catelog_iamged3);

        RelativeLayout.LayoutParams paramsA = (RelativeLayout.LayoutParams) catelog_iamgea.getLayoutParams();
        paramsA.height = height;
        catelog_iamgea.setLayoutParams(paramsA);

        RelativeLayout.LayoutParams paramsB = (RelativeLayout.LayoutParams) catelog_iamgeb.getLayoutParams();
        paramsB.height = height;
        catelog_iamgeb.setLayoutParams(paramsB);

        RelativeLayout.LayoutParams paramsC = (RelativeLayout.LayoutParams) catelog_iamgec.getLayoutParams();
        paramsC.height = height;
        catelog_iamgec.setLayoutParams(paramsC);

        RelativeLayout.LayoutParams paramsD = (RelativeLayout.LayoutParams) catelog_iamged.getLayoutParams();
        paramsD.height = height;
        catelog_iamged.setLayoutParams(paramsD);


        /*LinearLayout.LayoutParams paramsA = (LinearLayout.LayoutParams) catelog_layouta.getLayoutParams();
        paramsA.height = height;
        catelog_layouta.setLayoutParams(paramsA);

        LinearLayout.LayoutParams paramsB = (LinearLayout.LayoutParams) catelog_layoutb.getLayoutParams();
        paramsB.height = height;
        catelog_layoutb.setLayoutParams(paramsB);

        LinearLayout.LayoutParams paramsC = (LinearLayout.LayoutParams) catelog_layoutc.getLayoutParams();
        paramsC.height = height;
        catelog_layoutc.setLayoutParams(paramsC);

        LinearLayout.LayoutParams paramsD = (LinearLayout.LayoutParams) catelog_layoutd.getLayoutParams();
        paramsD.height = height;
        catelog_layoutd.setLayoutParams(paramsD);*/


        offlinemde_layout = findViewById(R.id.offlinemde_layout3);
        catalog_ll3 = findViewById(R.id.catalog_ll3);
        see_all_catalaog = findViewById(R.id.see_all_catalaog3);

        catalogue_label = findViewById(R.id.catalogue_label3);
        catalogue_labelline = findViewById(R.id.catalogue_labelline3);
        seeall_cataloguelist = findViewById(R.id.seeall_cataloguelist3);
        bottomcatalogue_layout = findViewById(R.id.bottomcatalogue_layout3);
        bottomcatalogue_layouta = findViewById(R.id.bottomcatalogue_layouta3);

        catalogue_label.setText(getResources().getString(R.string.catalogue_label));
        catalogue_labelline.setText(getResources().getString(R.string.catalogue_label));
        seeall_cataloguelist.setText(getResources().getString(R.string.see_all));

        TextView tv_see_all = findViewById(R.id.tv_see_all);
        TextView tv_todo_underline = findViewById(R.id.tv_todo_underline);

        //  tv_see_all.setTextColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));
        //  todo_txt_c.setTextColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));
        // tv_todo_underline.setBackgroundColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));

        tv_see_all.setTextColor(getResources().getColor(R.color.common_grey));
        tv_see_all.setText(getResources().getString(R.string.see_all));
        todo_txt_c.setTextColor(getResources().getColor(R.color.common_grey));
        tv_todo_underline.setTextColor(getResources().getColor(R.color.common_grey));


        learningcredits_text.setText(getResources().getString(R.string.learning_credit_text));
        user_coursependinglabel.setText(getResources().getString(R.string.pending_text));
        my_progress_text.setText(getResources().getString(R.string.my_progress));
        user_coursecompletedlabel.setText(getResources().getString(R.string.completed));
        refresh_text.setText(getResources().getString(R.string.refresh));
        refresh_text_line.setText(getResources().getString(R.string.refresh));
        clearFilters_text.setText(getResources().getString(R.string.clear_all));
        applyfilter_text.setText(getResources().getString(R.string.apply_filter));
        filter_text.setText(getResources().getString(R.string.filter_text));

        catelog_layouta.setOnClickListener(this);
        catelog_layoutb.setOnClickListener(this);
        catelog_layoutc.setOnClickListener(this);
        catelog_layoutd.setOnClickListener(this);
        see_all_catalaog.setOnClickListener(this);

        myFeedButton = findViewById(R.id.myfeed_button);
        myCourseButton = findViewById(R.id.myCourse_button);
        myChallengesButton = findViewById(R.id.mychallenges_button);
        myNoticeBoardButton = findViewById(R.id.myNoticeBorad_button);

        filter_rv = findViewById(R.id.filter_rv);
        filter_vertical_rv = findViewById(R.id.filter_vertical_rv);

        setButtonColors(myFeedButton, myCourseButton, myChallengesButton, myNoticeBoardButton);

        refresh_layout.setOnClickListener(this);
        completedcourses_layout.setOnClickListener(this);
        pendingcourses_layout.setOnClickListener(this);
        usermain_profile.setOnClickListener(this);
        userprofileImageText.setOnClickListener(this);
        setProgressBarColor();
        hideAlertIcon();
        sub_tab_3.setVisibility(View.VISIBLE);

        filter_text_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_category_ll.getVisibility() == View.VISIBLE) {
                    filter_category_ll.setVisibility(View.GONE);
                } else {
                    filter_category_ll.setVisibility(View.VISIBLE);
                }
            }
        });

        myCourseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTab(1);
            }
        });
        myFeedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTab(0);
            }
        });
        myChallengesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTab(2);
            }
        });
        myNoticeBoardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTab(3);
            }
        });

        see_all_3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playList_rv.getVisibility() == View.VISIBLE) {
                    launchCatalogActivity(playListCategoryId);
                } else if (play_skill_rv.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(NewLandingActivity.this, CatalogListActivity.class);
                    intent.putExtra("hasBanner", false);
                    intent.putExtra("filter_type", "SKILL");
                    intent.putExtra("topDisplayName", "" + getResources().getString(R.string.latest_skills));
                    startActivity(intent);
                } else
                    getPendingList3();
            }
        });

        applyfilter_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_category_ll.setVisibility(View.GONE);
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(filterType, "");
                Log.e(TAG, "updateShareCount: feedArrayList 2-> " + OustStaticVariableHandling.getInstance().getFeeds().size());
//                createList(OustStaticVariableHandling.getInstance().getFeeds(), false, true);
            }
        });

        clearFilters_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_category_ll.setVisibility(View.GONE);
                filterType = 0;
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(filterType, "");
                if (newFeedVerticalFilterAdapter != null)
                    newFeedVerticalFilterAdapter.updateAdapter(filterType, "");
                //    createList(OustStaticVariableHandling.getInstance().getFeeds(), false, true);
                if (newFeedAdapter != null) {
                    OustPreferences.saveintVar("filterTypeFeed", 0);
                    newsfeed_recyclerview.setVisibility(View.VISIBLE);
                    newsfeed_recyclerview.removeAllViews();
                    newFeedAdapter.notifyFeedChange(null, false);
                }
            }
        });
        initListener2();

        swiperefreshparent_layout.setVisibility(View.VISIBLE);
        tab_layout_c.setVisibility(View.VISIBLE);
        showLoader();
    }

    private long defaultCategoryId = 0;
    private String currentCourseTag = "";
    private TextView todo_txt_c;
    private long playListCategoryId = 0;
    private String currentTabName = "";


    private void clickTab(int i) {
        try {
            feed_layout.setVisibility(View.GONE);
            catalog_ll3.setVisibility(View.GONE);
            play_skill_rv.setVisibility(View.GONE);
            playList_rv.setVisibility(View.GONE);
            noticeBoard_rv.setVisibility(View.GONE);
            catalogue_lay.setVisibility(View.GONE);
            newCourse_recyclerview.setVisibility(View.GONE);
            newChallenges_recyclerview.setVisibility(View.GONE);
            tabIndex = i;

            if (tabInfoDataArrayList.get(i).getIndexName().contains("FEED")) {
                currentTabName = "FEEDS";
                feed_layout.setVisibility(View.VISIBLE);
                no_data_text.setText(getResources().getString(R.string.no_feed_assigned));
                no_data_text.setVisibility(View.VISIBLE);
                newsfeed_recyclerview.setVisibility(View.GONE);
                if (newsfeed_recyclerview.getVisibility() == View.GONE) {
                    catalog_ll3.setVisibility(View.GONE);
                    sub_tab_3.setVisibility(View.GONE);
                    if (isFilterAvailable) {
                        filter_ll.setVisibility(View.VISIBLE);
                    } else {
                        filter_ll.setVisibility(View.GONE);
                    }
                    hideAlertIcon();
                    ll_loading.setVisibility(View.GONE);
                    newCourse_recyclerview.setVisibility(View.GONE);
                    newChallenges_recyclerview.setVisibility(View.GONE);
                    newsfeed_recyclerview.setVisibility(View.VISIBLE);
                    playList_rv.setVisibility(View.GONE);
                    noticeBoard_rv.setVisibility(View.GONE);
                    // BottomBannerVisibility(tabInfoDataArrayList.get(i).isShowTodo());
                }
                setButtonColors(i);
                if (newFeedHashMap.size() > 0) {
                    no_data_text.setVisibility(View.GONE);
                } else {
                    no_data_text.setText(getResources().getString(R.string.no_feed_assigned));
                    no_data_text.setVisibility(View.VISIBLE);
                    newsfeed_recyclerview.setVisibility(View.GONE);
                }
            } else if (tabInfoDataArrayList.get(i).getIndexName().contains("COURSE")) {
                currentTabName = "COURSE";
                if (newCourse_recyclerview.getVisibility() == View.GONE) {
                    if ((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!tabInfoDataArrayList.get(i).isHideCatalogue())) {
                        catalog_ll3.setVisibility(View.VISIBLE);
                    } else {
                        catalog_ll3.setVisibility(View.GONE);
                    }
                    sub_tab_3.setVisibility(View.VISIBLE);
                    todo_txt_c.setVisibility(View.VISIBLE);
                    todo_txt_c.setText(getResources().getString(R.string.to_do));
                    filter_category_ll.setVisibility(View.GONE);
                    filter_ll.setVisibility(View.GONE);
                    ll_loading.setVisibility(View.GONE);
                    see_all_3.setVisibility(View.VISIBLE);
                    hideAlertIcon();
                    newCourse_recyclerview.setVisibility(View.VISIBLE);
                    newChallenges_recyclerview.setVisibility(View.GONE);
                    newsfeed_recyclerview.setVisibility(View.GONE);
                    playList_rv.setVisibility(View.GONE);
                    noticeBoard_rv.setVisibility(View.GONE);
                    todo_txt_c.setTextColor(getResources().getColor(R.color.common_grey));
                    catalogue_labelline.setTextColor(getResources().getColor(R.color.common_grey));
                }
                sendFeedClickedRequestToBackend();
                setButtonColors(i);
                if (tabInfoDataArrayList.get(i).getTabTags() != null) {
                    currentCourseTag = tabInfoDataArrayList.get(i).getTabTags();
                    reviewCoursesWithTag();
                } else {
                    currentCourseTag = null;
                }

                if (totalCoursesCount3 > 0) {
                    no_data_text.setVisibility(View.GONE);
                } else {
                    // sub_tab_3.setVisibility(View.GONE);
                    no_data_text.setText(getResources().getString(R.string.no_course_assign));
                    no_data_text.setVisibility(View.VISIBLE);
                    see_all_3.setVisibility(View.GONE);
                    newCourse_recyclerview.setVisibility(View.GONE);
                }
                long catalogueTabId = tabInfoDataArrayList.get(i).getCatalogueTabId() == 0 ? (i + 1) : tabInfoDataArrayList.get(i).getCatalogueTabId();
                getCatalogueData3(catalogueTabId, null);

                if (((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!tabInfoDataArrayList.get(i).isHideCatalogue())) || no_data_text.getVisibility() == View.VISIBLE) {
                    showBanner();
                }


            } else if (tabInfoDataArrayList.get(i).getIndexName().contains("PLAYLIST")) {
                currentTabName = "PLAYLIST";
                if (playList_rv.getVisibility() == View.GONE) {
                    if ((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!tabInfoDataArrayList.get(i).isHideCatalogue())) {
                        catalog_ll3.setVisibility(View.VISIBLE);
                    } else {
                        catalog_ll3.setVisibility(View.GONE);
                    }
                    sub_tab_3.setVisibility(View.VISIBLE);
                    todo_txt_c.setVisibility(View.GONE);
                    todo_txt_c.setText(getResources().getString(R.string.play_list));
                    filter_category_ll.setVisibility(View.GONE);
                    filter_ll.setVisibility(View.GONE);
                    ll_loading.setVisibility(View.GONE);
                    see_all_3.setVisibility(View.GONE);
                    hideAlertIcon();
                    newCourse_recyclerview.setVisibility(View.GONE);
                    newChallenges_recyclerview.setVisibility(View.GONE);
                    newsfeed_recyclerview.setVisibility(View.GONE);
                    playList_rv.setVisibility(View.VISIBLE);
                }
                sendFeedClickedRequestToBackend();
                setButtonColors(i);
                long catalogueTabId = tabInfoDataArrayList.get(i).getCatalogueTabId() == 0 ? (i + 1) : tabInfoDataArrayList.get(i).getCatalogueTabId();
                if (playListData == null) {
                    getCategoryData(catalogueTabId, tabInfoDataArrayList.get(i).getCategoryId());
                }
                if (playListData != null && playListData.size() > 0) {
                    no_data_text.setVisibility(View.GONE);
                } else {
                    sub_tab_3.setVisibility(View.GONE);
                    no_data_text.setText(getResources().getString(R.string.no_playlist_available));
                    no_data_text.setVisibility(View.VISIBLE);
                }
            } else if (tabInfoDataArrayList.get(i).getIndexName().contains("PLAY")) {
                currentTabName = "PLAY";
                if (play_skill_rv.getVisibility() == View.GONE) {
                    if ((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!tabInfoDataArrayList.get(i).isHideCatalogue())) {
                        catalog_ll3.setVisibility(View.VISIBLE);
                    } else {
                        catalog_ll3.setVisibility(View.GONE);
                    }
                    sub_tab_3.setVisibility(View.VISIBLE);
                    todo_txt_c.setText(getResources().getString(R.string.latest_skills));
                    filter_category_ll.setVisibility(View.GONE);
                    filter_ll.setVisibility(View.GONE);
                    ll_loading.setVisibility(View.GONE);
                    todo_txt_c.setVisibility(View.VISIBLE);
                    see_all_3.setVisibility(View.VISIBLE);
                    hideAlertIcon();
                    newCourse_recyclerview.setVisibility(View.GONE);
                    newChallenges_recyclerview.setVisibility(View.GONE);
                    newsfeed_recyclerview.setVisibility(View.GONE);
                    playList_rv.setVisibility(View.GONE);
                    noticeBoard_rv.setVisibility(View.GONE);
                    play_skill_rv.setVisibility(View.VISIBLE);
                    getUserSkillData();
                }
                sendFeedClickedRequestToBackend();
                setButtonColors(i);
                long catalogueTabId = tabInfoDataArrayList.get(i).getCatalogueTabId() == 0 ? (i + 1) : tabInfoDataArrayList.get(i).getCatalogueTabId();
                //getCatalogueData3(catalogueTabId,"SKILL");
                new_landing_page_type = "SKILL";

                if (skillCatalogItemDataArrayList == null || skillCatalogItemDataArrayList.size() == 0)
                    getCatalogueData((int) catalogueTabId);
                else
                    setCatalogImages(skillCatalogItemDataArrayList);


                if (((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!tabInfoDataArrayList.get(i).isHideCatalogue())) || no_data_text.getVisibility() == View.VISIBLE) {
                    showBanner();
                }
                //  getCategoryData(tabInfoDataArrayList.get(i).getCatalogueTabId(), tabInfoDataArrayList.get(i).getCategoryId());
                if (userSkillDataArrayList != null && userSkillDataArrayList.size() > 0) {
                    sub_tab_3.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                } else {
                    sub_tab_3.setVisibility(View.GONE);
                    no_data_text.setText(getResources().getString(R.string.add_skills_start));
                    no_data_text.setVisibility(View.VISIBLE);
                }
            } else if (tabInfoDataArrayList.get(i).getIndexName().contains("NOTICEBOARD")) {
                currentTabName = "NOTICEBOARD";
                if (noticeBoard_rv.getVisibility() == View.GONE) {
                    catalog_ll3.setVisibility(View.GONE);
                    sub_tab_3.setVisibility(View.VISIBLE);
                    todo_txt_c.setVisibility(View.VISIBLE);
                    todo_txt_c.setText(getResources().getString(R.string.notice_board));
                    filter_category_ll.setVisibility(View.GONE);
                    filter_ll.setVisibility(View.GONE);
                    ll_loading.setVisibility(View.GONE);
                    see_all_3.setVisibility(View.GONE);
                    hideAlertIcon();
                    newCourse_recyclerview.setVisibility(View.GONE);
                    newChallenges_recyclerview.setVisibility(View.GONE);
                    newsfeed_recyclerview.setVisibility(View.GONE);
                    playList_rv.setVisibility(View.GONE);
                    noticeBoard_rv.setVisibility(View.VISIBLE);
                }
                sendFeedClickedRequestToBackend();
                setButtonColors(i);

                if (nbTopicDataHashMap == null) {
                    getUserNBTopicData();
                }

            } else {
                currentTabName = "ASSESSMENT";
                if (newChallenges_recyclerview.getVisibility() == View.GONE) {
                    if ((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!tabInfoDataArrayList.get(i).isHideCatalogue())) {
                        catalog_ll3.setVisibility(View.VISIBLE);
                    } else {
                        catalog_ll3.setVisibility(View.GONE);
                    }
                    sub_tab_3.setVisibility(View.VISIBLE);
                    todo_txt_c.setVisibility(View.VISIBLE);
                    todo_txt_c.setText(getResources().getString(R.string.to_do));
                    filter_category_ll.setVisibility(View.GONE);
                    filter_ll.setVisibility(View.GONE);
                    ll_loading.setVisibility(View.GONE);
                    hideAlertIcon();
                    newCourse_recyclerview.setVisibility(View.GONE);
                    see_all_3.setVisibility(View.VISIBLE);
                    newChallenges_recyclerview.setVisibility(View.VISIBLE);
                    newsfeed_recyclerview.setVisibility(View.GONE);
                    playList_rv.setVisibility(View.GONE);
                    noticeBoard_rv.setVisibility(View.GONE);
                }
                sendFeedClickedRequestToBackend();
                setButtonColors(i);
                if (totalChallengesCount3 > 0) {
                    no_data_text.setVisibility(View.GONE);
                } else {
                    sub_tab_3.setVisibility(View.GONE);
                    no_data_text.setVisibility(View.VISIBLE);
                    no_data_text.setText(getResources().getString(R.string.no_assessment_assigned));
                }
                long catalogueTabId = tabInfoDataArrayList.get(i).getCatalogueTabId() == 0 ? (i + 1) : tabInfoDataArrayList.get(i).getCatalogueTabId();
                getCatalogueData3(catalogueTabId, null);

                if (((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!tabInfoDataArrayList.get(i).isHideCatalogue())) || no_data_text.getVisibility() == View.VISIBLE) {
                    showBanner();
                }
            }
            BottomBannerVisibility(tabInfoDataArrayList.get(i).isShowTodo());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserNBTopicData() {
        Log.e(TAG, "inside getUserCourses() ");
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/noticeBoard";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Log.d(TAG, "onDataChange: getUserNBTopicData");
                        if (dataSnapshot.getValue() != null) {
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        NBTopicData nbTopicData = new NBTopicData();
                                        nbTopicData.init(lpMap);
                                        updateNBDataMap(nbTopicData);
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String nBKey : lpMainMap.keySet()) {
                                    Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(nBKey);
                                    if (lpMap != null) {
                                        NBTopicData nbTopicData = new NBTopicData();
                                        nbTopicData.init(lpMap);
                                        updateNBDataMap(nbTopicData);
                                    }
                                }
                            }

                            getAllNBTopicData();
                        } else {
                            createNoticeBoardAdapter(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            courseFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void updateNBDataMap(NBTopicData nbTopicData) {
        if (nbTopicDataHashMap == null)
            nbTopicDataHashMap = new HashMap<>();
        if (nbTopicDataHashMap.containsKey("" + nbTopicData.getId())) {
            nbTopicData.setKeySet(nbTopicDataHashMap.get("" + nbTopicData.getId()).isKeySet());
        } else {
            nbTopicDataHashMap.put(nbTopicData.getId() + "", nbTopicData);
        }
    }

    private int totalNBTopicCount = 0;

    private void getAllNBTopicData() {
        if (nbTopicDataHashMap != null && nbTopicDataHashMap.size() > 0) {
            for (String key : nbTopicDataHashMap.keySet()) {
                if (!nbTopicDataHashMap.get(key).isKeySet()) {
                    nbTopicDataHashMap.get(key).setKeySet(true);
                    try {
                        final String message = "/noticeBoard/noticeBoard" + nbTopicDataHashMap.get(key).getId();
                        ValueEventListener myassessmentListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Log.d(TAG, "onDataChange: getAllNBTopicData");
                                    if (dataSnapshot.getValue() != null) {
                                        totalNBTopicCount++;
                                        Object o1 = dataSnapshot.getValue();
                                        Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                        if (lpMainMap.containsKey("nbId")) {
                                            long id = OustSdkTools.convertToLong(lpMainMap.get("nbId"));
                                            if (nbTopicDataHashMap.containsKey(id + "")) {
                                                NBTopicData nbTopicData = nbTopicDataHashMap.get("" + id);
                                                nbTopicDataHashMap.put("" + id, nbTopicData.setExtraNBData(nbTopicData, lpMainMap));
                                            }
                                        }

                                        createToalNbData();

                                    } else {
                                        totalNBTopicCount++;
                                        createToalNbData();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                totalNBTopicCount++;
                                createToalNbData();
                            }
                        };
                        DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
                        Query query = gameHistoryRef.orderByChild("addedOn");
                        query.keepSynced(true);
                        query.addValueEventListener(myassessmentListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else {
                    totalNBTopicCount++;
                    createToalNbData();
                }
            }
        } else {
            createToalNbData();
        }

    }

    List<NBTopicData> nbTopicDataArrayList;

    private void createToalNbData() {
        if (totalNBTopicCount >= nbTopicDataHashMap.size()) {
            nbTopicDataArrayList = new ArrayList<NBTopicData>(nbTopicDataHashMap.values());
            if (nbTopicDataArrayList != null && nbTopicDataArrayList.size() > 0) {
                Collections.sort(nbTopicDataArrayList, nbTopicDataComparator);
            }
            createNoticeBoardAdapter(nbTopicDataArrayList);
        }
    }

    public Comparator<NBTopicData> nbTopicDataComparator = new Comparator<NBTopicData>() {
        public int compare(NBTopicData s1, NBTopicData s2) {
            return Long.valueOf(s2.getAssignedOn()).compareTo(Long.valueOf(s1.getAssignedOn()));
        }
    };

    private NBTopicAdapter nbTopicAdapter;

    private void createNoticeBoardAdapter(List<NBTopicData> nbTopicDataArrayList) {
        Log.e(TAG, "createNoticeBoardAdapter: " + nbTopicDataArrayList.size());
        no_data_text.setVisibility(View.GONE);
        if (noticeBoard_rv.getVisibility() == View.VISIBLE) {
            hideLoader();
        }
        if (nbTopicDataArrayList != null && nbTopicDataArrayList.size() > 0) {
            no_data_text.setVisibility(View.GONE);
            if (nbTopicAdapter == null) {
                Log.e(TAG, "createNoticeBoardAdapter:  null adapter");
                nbTopicAdapter = new NBTopicAdapter(NewLandingActivity.this, nbTopicDataArrayList);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                noticeBoard_rv.setLayoutManager(mLayoutManager);

                noticeBoard_rv.setItemAnimator(new DefaultItemAnimator());

                noticeBoard_rv.setAdapter(nbTopicAdapter);
            } else {
                if (isSearchOn) {
                    nbTopicAdapter.notifyListChnage(nbTopicDataArrayList);
                } else {
                    nbTopicAdapter.notifyListChnage(nbTopicDataArrayList);
                }
            }
        } else {
            if (!searchOn) {
                no_data_text.setText(getResources().getString(R.string.no_module_yet));
            } else {
                no_data_text.setText(getResources().getString(R.string.no_topics_available));
            }
            if (noticeBoard_rv.getVisibility() == View.VISIBLE) {
                no_data_text.setVisibility(View.VISIBLE);
                noticeBoard_rv.setVisibility(View.GONE);
            }
        }
    }

    private void getCategoryData(long catalogueTabId, long categoryId) {
        playListCategoryId = categoryId;
        if (categoryId > 0 && catalogueTabId > 0) {
            String catalog_content_url = getResources().getString(R.string.contentCateogory_url);
            catalog_content_url = catalog_content_url.replace("{catalogueId}", ("" + catalogueTabId));
            catalog_content_url = catalog_content_url.replace("{ccId}", ("" + categoryId));
            catalog_content_url = HttpManager.getAbsoluteUrl(catalog_content_url);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

            ApiCallUtils.doNetworkCall(Request.Method.GET, catalog_content_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    try {
                        if (response.optBoolean("success")) {
                            extractPlayListData(response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, catalog_content_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    try {
                        if (response.optBoolean("success")) {
                            extractPlayListData(response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        }
    }

    private ArrayList<CommonLandingData> playListData;

    private void extractPlayListData(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("categoryItemDataList");

        if (jsonArray != null && jsonArray.length() > 0) {
            playListData = new ArrayList<>();
            sub_tab_3.setVisibility(View.VISIBLE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject j = jsonArray.optJSONObject(i);
                CommonLandingData commonLandingData = new CommonLandingData();
                commonLandingData.setName("" + j.optString("name"));
                commonLandingData.setBanner("" + j.optString("banner"));
                commonLandingData.setIcon("" + j.optString("icon"));
                commonLandingData.setDescription("" + j.optString("description"));
                commonLandingData.setId("" + j.optInt("contentId"));
                commonLandingData.setType("" + j.optString("contentType"));
                commonLandingData.setEnrollCount(j.optLong("numOfEnrolledUsers"));
                commonLandingData.setOc(j.optLong("oustCoins"));
                playListData.add(commonLandingData);
            }
        }

        createPlayListList3();
    }

    private void createPlayListList3() {
        try {
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                if ((searchOn) && (searchText != null) && (!searchText.isEmpty())) {
                    filterPlayListData(searchText);
                } else {
                    createPlaylistAdapter3(playListData);
                    setMyTotalProgressText();
                    setMyTotalOcText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void filterPlayListData(String searchText) {
        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if (playListData != null && playListData.size() > 0) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.meetCriteria(playListData, searchText).get();
            }
            createPlaylistAdapter3(new ArrayList<CommonLandingData>(myDeskFilterdData));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private NewLandingModuleAdaptera newLandingPlayListAdaptera;

    private void createPlaylistAdapter3(ArrayList<CommonLandingData> playListDatas) {
        no_data_text.setVisibility(View.GONE);
        if (playList_rv.getVisibility() == View.VISIBLE) {
            hideLoader();
        }
        if (playListDatas != null && playListDatas.size() > 0) {
            no_data_text.setVisibility(View.GONE);
            if (newLandingPlayListAdaptera == null) {
                newLandingPlayListAdaptera = new NewLandingModuleAdaptera(playListDatas);
                newLandingPlayListAdaptera.setContext(NewLandingActivity.this);
                newLandingPlayListAdaptera.setMyDeskData(false);
                boolean hideCatalogue = isHideCatalogueforTab("PLAYLIST");
                if ((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!hideCatalogue)) {
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    playList_rv.setLayoutManager(mLayoutManager);
                    newLandingPlayListAdaptera.setAdapterHorizontal(true);
                } else {
                    GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
                    playList_rv.setLayoutManager(mLayoutManager);
                }
                playList_rv.setItemAnimator(new DefaultItemAnimator());
                newLandingPlayListAdaptera.setmyDeskDataMap(myDeskInfoMap);
                newLandingPlayListAdaptera.setDataLoaderNotifier(NewLandingActivity.this);
                playList_rv.setAdapter(newLandingPlayListAdaptera);
                playList_rv.smoothScrollToPosition(0);

            } else {
                if (isSearchOn) {
                    newLandingPlayListAdaptera.notifyListChnage(playListDatas, totalModuleCount);
                } else {
                    newLandingPlayListAdaptera.notifyListChnage(playListDatas, totalModuleCount);
                }
            }
        } else {
            if (!searchOn)
                no_data_text.setText(getResources().getString(R.string.no_module_yet));
            else
                no_data_text.setText(getResources().getString(R.string.no_playlist_available));
            if (playList_rv.getVisibility() == View.VISIBLE) {
                no_data_text.setVisibility(View.VISIBLE);
                playList_rv.setVisibility(View.GONE);
            }
        }
    }

    private void reviewCoursesWithTag() {
        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            CommonLandingFilter commonLandingFilter1 = new CommonLandingFilter();
            myDeskFilterdData = commonLandingFilter1.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
            createCourseList3(new ArrayList<CommonLandingData>(myDeskFilterdData));
        } catch (Exception e) {

        }

    }

    private void getCatalogueData3(long catalogueTabId, String type) {
        if (catalogueTabId == 1) {
            new_landing_page_type = "All";
            if (allCatalogItemDataArrayList != null) {
                setCatalogImages(allCatalogItemDataArrayList);
            } else {
                getCatalogueData((int) catalogueTabId);
            }
        } else /*if (catalogueTabId == 2) */ {
            if (type != null) {
                new_landing_page_type = "SKILL";
                if (skillCatalogItemDataArrayList == null || skillCatalogItemDataArrayList.size() == 0)
                    getCatalogueData((int) catalogueTabId);
                else
                    setCatalogImages(skillCatalogItemDataArrayList);
            } else {
                new_landing_page_type = "Learn";
                if (learnCatalogItemDataArrayList == null || learnCatalogItemDataArrayList.size() == 0)
                    getCatalogueData((int) catalogueTabId);
                else
                    setCatalogImages(learnCatalogItemDataArrayList);
            }

        }/* else if (catalogueTabId == 3) {
            new_landing_page_type = "Learn";
            if (learnCatalogItemDataArrayList == null || learnCatalogItemDataArrayList.size() == 0)
                getCatalogueData((int) catalogueTabId);
            else
                setCatalogImages(learnCatalogItemDataArrayList);
        }*/
    }

    private NewFeedFilterAdapter newFeedFilterAdapter;
    private NewFeedVerticalFilterAdapter newFeedVerticalFilterAdapter;
    ArrayList<FilterCategory> filterCategories = new ArrayList<>();
    private String filterTag;

    private void setFilterItems() {
        if (filterCategories != null && filterCategories.size() > 0) {
            isFilterAvailable = true;
            filter_ll.setVisibility(View.VISIBLE);
            Collections.sort(filterCategories, feedCategorySorter);
        }
        newFeedFilterAdapter = new NewFeedFilterAdapter(this, filterCategories, this);
        WrapContentLinearLayoutManager mLayoutManager1 = new WrapContentLinearLayoutManager(NewLandingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        filter_rv.setLayoutManager(mLayoutManager1);
        newFeedFilterAdapter.setSelectionPos(0);
        filter_rv.setAdapter(newFeedFilterAdapter);
        ArrayList<FilterCategory> verticalfilterCategories = new ArrayList<>();
        if (filterCategories != null && filterCategories.size() > 3) {
            filter_text_ll.setVisibility(View.VISIBLE);
            for (int i = 3; i < filterCategories.size(); i++) {
                verticalfilterCategories.add(filterCategories.get(i));
            }
            newFeedVerticalFilterAdapter = new NewFeedVerticalFilterAdapter(this, verticalfilterCategories, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewLandingActivity.this);
            filter_vertical_rv.setLayoutManager(mLayoutManager);
            filter_vertical_rv.setItemAnimator(new DefaultItemAnimator());
            newFeedVerticalFilterAdapter.setSelectionPos(0);
            filter_vertical_rv.setAdapter(newFeedVerticalFilterAdapter);
        } else {
            filter_category_ll.setVisibility(View.GONE);
            filter_text_ll.setVisibility(View.GONE);
        }

    }

    private void setButtonColors(int index) {
        if (index == 0) {
            setButtonColors(myFeedButton, myCourseButton, myChallengesButton, myNoticeBoardButton);
        } else if (index == 1) {
            setButtonColors(myCourseButton, myFeedButton, myChallengesButton, myNoticeBoardButton);
        } else if (index == 2) {
            setButtonColors(myChallengesButton, myFeedButton, myCourseButton, myNoticeBoardButton);
        } else {
            setButtonColors(myNoticeBoardButton, myChallengesButton, myFeedButton, myCourseButton);
        }
    }

    private void setButtonColors(LinearLayout l1, LinearLayout l2, LinearLayout l3, LinearLayout l4) {
        try {
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if (toolbarColorCode != null && toolbarColorCode.length() > 0) {
                color = Color.parseColor(toolbarColorCode);
            }
            int width = (int) getResources().getDimension(R.dimen.oustlayout_dimen2);
            GradientDrawable d1 = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_corner);
            d1.setStroke(width, color);
            l1.setBackgroundDrawable(d1);
            l2.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedcorner_litegrey));
            l3.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedcorner_litegrey));
            l4.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedcorner_litegrey));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideAlertIcon() {
        try {
            if (action_alert != null)
                action_alert.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //=====================================================================================================
    //top menu related methods
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newlandingmenu, menu);

        actionSearch = menu.findItem(R.id.action_search);
        Drawable searchDrawable = getResources().getDrawable(R.drawable.ic_landing_search);
        actionSearch.setIcon(OustResourceUtils.setDefaultDrawableColor(searchDrawable, getResources().getColor(R.color.white)));

        action_alert = menu.findItem(R.id.action_alert);
        Drawable alertDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
        action_alert.setIcon(OustResourceUtils.setDefaultDrawableColor(alertDrawable, getResources().getColor(R.color.white)));

        notificationAlert = menu.findItem(R.id.notification_alert);
        Drawable notificationDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
        notificationAlert.setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, getResources().getColor(R.color.white)));
        notificationAlert.setVisible(true);

        action_leaderBoard = menu.findItem(R.id.action_leaderBoard);
        Drawable leaderBoardDrawable = getResources().getDrawable(R.drawable.ic_landing_leader_board);
        action_leaderBoard.setIcon(OustResourceUtils.setDefaultDrawableColor(leaderBoardDrawable, getResources().getColor(R.color.white)));

        action_gotohostapp = menu.findItem(R.id.action_gotohostapp);
       /* Drawable hostDrawable = getResources().getDrawable(R.drawable.ic_landing_search);
        action_gotohostapp.setIcon(OustResourceUtils.setDefaultDrawableColor(hostDrawable,getResources().getColor(R.color.white)));*/

        if (OustPreferences.getAppInstallVariable("disableToolbarLB")) {
            action_leaderBoard.setVisible(false);
        } else {
            action_leaderBoard.setVisible(true);
        }
        try {
            // SearchView searchView = (SearchView) actionSearch.getActionView();

            OustStaticVariableHandling.getInstance().setNewSearchView((CustomSearchView) actionSearch.getActionView());
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnQueryTextListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnSearchViewCollapsedEventListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnSearchViewExpandedEventListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setQueryHint(getResources().getString(R.string.search_text));
            OustStaticVariableHandling.getInstance().getNewSearchView().setVisibility(View.VISIBLE);
            OustStaticVariableHandling.getInstance().getNewSearchView().requestFocusFromTouch();
            showSearchIcon();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setHostAppIcon();
        getAllFeeds(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, hide action items related to the content view
        mDrawerLayout.isDrawerOpen(drawer_listview);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        try {
            int id = item.getItemId();
            if (id == R.id.action_search) {
                actionSearch.setVisible(true);
                showAlertBanner(false);
                searchPlate = OustStaticVariableHandling.getInstance().getNewSearchView().findViewById(R.id.search_plate);
                searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            } else if (id == R.id.action_alert) {
                //action_alert.setIcon(R.mipmap.new_alerta);
                wjalert_banner.setVisibility(View.GONE);
                showingAlertBanner = false;
                List<DTONewFeed> allFeedList = new ArrayList<>(userNewFeedList);
                Collections.sort(allFeedList, newsFeedSorter);
                OustAppState.getInstance().setNewsFeeds(allFeedList);
                Intent intent = new Intent(this, NewAlertActivity.class);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                startActivity(intent);
            } else if (id == R.id.action_leaderBoard) {
                Intent intent1 = new Intent(this, NewLeaderBoardActivity.class);
                //Intent intent1 = new Intent(this, EnterpriseLeaderboard.class);
                startActivity(intent1);
            } else if (id == R.id.action_gotohostapp) {
                try {
                    Log.e(TAG, "redirect package:" + OustPreferences.get("redirectAppPackage"));
                    if (OustPreferences.get("redirectAppPackage") != null) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(OustPreferences.get("redirectAppPackage"));
                        startActivity(launchIntent);
                        OustFirebaseTools.resetFirebase();
                        OustAppState.getInstance().clearAll();
                        this.finish();
                        System.exit(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else if (id == R.id.notification_alert) {
                Drawable notificationDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
                notificationAlert.setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, getResources().getColor(R.color.white)));
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("hasDeskData", false);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                intent.putExtra("overAllCatalogue", true);
                startActivity(intent);
            } else {
                if (!isRefreshing) {
                    if (mDrawerLayout.isDrawerOpen(drawer_listview)) {
                        mDrawerLayout.closeDrawer(drawer_listview);
                    } else {
                        mDrawerLayout.openDrawer(drawer_listview);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    private void setPreferenceData() {
        try {
            Intent intent = getIntent();
            isPlayModeEnabled = intent.getStringExtra("isPlayModeEnabled");
            isBranchLink = intent.getBooleanExtra("isBranchLink", false);
            branchLink = intent.getStringExtra("branchLink");
            referringParamsString = intent.getStringExtra("referringParamsString");
            companydisplayName = intent.getStringExtra("companydisplayName");
            isShareEnabled = intent.getStringExtra("isShareEnabled");
            panelColor = intent.getStringExtra("panelColor");
            if (OustStaticVariableHandling.getInstance().isContainerApp()) {
                OustPreferences.saveAppInstallVariable("isContainer", true);
            }
            if (((isPlayModeEnabled != null) && (!isPlayModeEnabled.isEmpty())) && (isPlayModeEnabled.equalsIgnoreCase("true"))) {
                OustPreferences.saveAppInstallVariable("isPlayModeEnabled", true);
                Log.e("LANDING playmode", "............. " + isPlayModeEnabled);
            } else {
                OustPreferences.saveAppInstallVariable("isPlayModeEnabled", false);
            }

            if (((isShareEnabled != null) && (!isShareEnabled.isEmpty())) && (isShareEnabled.equalsIgnoreCase("true"))) {
                OustPreferences.saveAppInstallVariable("isShareEnabled", true);
                Log.e("LANDING isShareEnabled", "............. " + isShareEnabled);
            } else {
                OustPreferences.saveAppInstallVariable("isShareEnabled", false);
            }
            if ((companydisplayName != null) && (!companydisplayName.isEmpty())) {
                OustPreferences.save("companydisplayName", companydisplayName);
            }
            if ((panelColor != null) && (!panelColor.isEmpty())) {
                OustPreferences.save("toolbarColorCode", panelColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void updateUserInfoFromFirebase() {
        String message = "/users/" + OustAppState.getInstance().getActiveUser().getStudentKey();
        ValueEventListener avatarListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        String avatarLink = snapshot.getValue().toString();
                        if (!avatarLink.isEmpty()) {
                            if (!avatarLink.startsWith("http")) {
                                avatarLink = null;
                            }
                        }
                        if ((avatarLink != null) && (!avatarLink.isEmpty())) {
                            OustPreferences.save("UserAvatar", avatarLink);
                        } else {
                            OustPreferences.clear("UserAvatar");
                        }
                        activeUser.setAvatar(avatarLink);
                        OustAppState.getInstance().getActiveUser().setAvatar(avatarLink);
                        newProfileAdapter.notifyDataChange();
                        setProfile();
                    } else {
                        activeUser.setAvatar(null);
                        OustAppState.getInstance().getActiveUser().setAvatar(null);
                        OustPreferences.clear("UserAvatar");
                        newProfileAdapter.notifyDataChange();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        OustFirebaseTools.getRootRef().child(message + "/avatar").keepSynced(true);
        OustFirebaseTools.getRootRef().child(message + "/avatar").addValueEventListener(avatarListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(avatarListener, message + "/avatar"));

        ValueEventListener displaynameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        OustPreferences.save("UserDisplayName", snapshot.getValue().toString());
                        OustAppState.getInstance().getActiveUser().setUserDisplayName(snapshot.getValue().toString());
                        activeUser.setUserDisplayName(snapshot.getValue().toString());
                        newProfileAdapter.notifyDataChange();
                        setNewUserDisplayName();
                        setProfile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        OustFirebaseTools.getRootRef().child(message + "/displayName").keepSynced(true);
        OustFirebaseTools.getRootRef().child(message + "/displayName").addValueEventListener(displaynameListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(displaynameListener, message + "/displayName"));
    }

    private void setTopBarSize() {
        int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen8);
        setLayoutAspectRatiosmall(topLayout, size);
    }

    private void setLayoutAspectRatiosmall(RelativeLayout mainImageLayout, int size) {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int imageWidth = (scrWidth) - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainImageLayout.getLayoutParams();
            float h = (imageWidth * 0.3f);
            params.height = ((int) h);
            mainImageLayout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTopTabImageLAyout(LinearLayout mainImageLayout, int size) {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int imageWidth = (scrWidth / 3);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainImageLayout.getLayoutParams();
            float h = (imageWidth * 0.460f);
            params.height = ((int) h);
            mainImageLayout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setToolBarColorAndIcons() {
        Log.e(TAG, "Inside toolbar color method");
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
            getSupportActionBar().setTitle("");
            TextView titleTextView = toolbar.findViewById(R.id.title);
            final ImageView toolbardleftimage = findViewById(R.id.toolbardleftimage);
            final ImageView toolbardleftimageb = findViewById(R.id.toolbardleftimageb);

            //set toolbar display name or logo
            if ((OustPreferences.get("panelLogo") != null) && (!OustPreferences.get("panelLogo").isEmpty())) {
                Log.e(TAG, "got logo from preferences ");
//                titleTextView.setText("");
                titleTextView.setVisibility(View.GONE);
                toolbardleftimageb.setImageBitmap(null);
                if (OustSdkTools.checkInternetStatus()) {
                    final String path = OustPreferences.get("panelLogo");
                    final Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.d(TAG, "Bitmap Loaded");
                            toolbardleftimageb.setImageBitmap(bitmap);
                            toolbardleftimageb.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            if (!callOnlyOnce) {
                                Log.d(TAG, "Bitmap Failed to load");
                                onImageBitmapSetFailed(toolbardleftimageb);
                            }
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.d(TAG, "Bitmap prepare load");
                        }
                    };
                    Picasso.get().load(path).into(target);
                    toolbardleftimageb.setTag(target);

                } else {
                    Picasso.get().load(OustPreferences.get("panelLogo")).networkPolicy(NetworkPolicy.OFFLINE).into(toolbardleftimageb);
                    toolbardleftimageb.setVisibility(View.VISIBLE);
                }
            } else {
                toolbardleftimage.setVisibility(View.GONE);
                toolbardleftimageb.setVisibility(View.GONE);
                if ((OustPreferences.get("companydisplayName") != null) && (!OustPreferences.get("companydisplayName").isEmpty())) {
                    titleTextView.setText(OustPreferences.get("companydisplayName"));
                } else {
                    titleTextView.setText(getResources().getString(R.string.landing_heading));
                }
            }
            //set toolbar color
            setNewUserDisplayName();
            if (toolbarColorCode == null || toolbarColorCode.isEmpty()) {
                toolbarColorCode = OustPreferences.get("toolbarColorCode");
            }
            mytask.setBackground(OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.course_button_bg)));

            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
                //setting progress bar color
                int color = OustSdkTools.getColorBack(R.color.lgreen);
                if (OustPreferences.get("toolbarColorCode") != null) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
                newlanding_loader_progressbar.invalidate();
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                newlanding_loader_progressbar.setIndeterminateDrawable(ld);
                OustSdkTools.setProgressbar(newlanding_loader_progressbar);
                setProgressBarColor();

//                String newTransparentColor = toolbarColorCode.replace("#", "");
//                newTransparentColor = "#90" + newTransparentColor;
                int color1 = Color.parseColor(toolbarColorCode);
                floatingActionsMenu.setmAddButtonColorNormal(color1);
                floatingActionsMenu.setmAddButtonColorPressed(color1);
                fab_action_leaderboard.setColorNormal(color1);
                fab_action_leaderboard.setColorPressed(color1);
                fab_action_joinmeeting.setColorNormal(color1);
                fab_action_joinmeeting.setColorPressed(color1);
                action_favourite.setColorNormal(color1);
                action_favourite.setColorPressed(color1);
            }
            if ((OustPreferences.get("userAreaBgImg") != null) && (!OustPreferences.get("userAreaBgImg").isEmpty())) {
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(OustPreferences.get("userAreaBgImg")).placeholder(R.drawable.landingtop_imageback).into(newLayoutbackImage);
                } else {
                    Picasso.get().load(OustPreferences.get("userAreaBgImg")).placeholder(R.drawable.landingtop_imageback).networkPolicy(NetworkPolicy.OFFLINE).into(newLayoutbackImage);
                }
            } else {
                newLayoutbackImage.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.landingtop_imageback));
            }
        } catch (Exception e) {
            Log.e("AIRTEL", "Exception is here", e);
        }
    }

    private boolean callOnlyOnce = false;

    private void onImageBitmapSetFailed(ImageView toolbardleftimageb) {
        callOnlyOnce = true;
        toolbardleftimageb.setVisibility(View.VISIBLE);
        if (OustSdkTools.checkInternetStatus()) {
            Picasso.get().load(OustPreferences.get("panelLogo")).into(toolbardleftimageb);
        } else {
            Picasso.get().load(OustPreferences.get("panelLogo")).networkPolicy(NetworkPolicy.OFFLINE).into(toolbardleftimageb);
        }
    }

    private void setNewUserDisplayName() {
        if ((OustPreferences.get("fname") != null) &&
                (OustPreferences.get("lname") != null)) {
            userDisplayName = OustPreferences.get("fname") + " " + OustPreferences.get("lname");
            temporaryProfileImageDisplayName = String.valueOf(OustPreferences.get("fname").charAt(0)) +
                    OustPreferences.get("lname").charAt(0);
            Log.e(TAG, "User display name fname + lname " + userDisplayName);
        } else if (OustPreferences.get("fname") != null) {
            userDisplayName = OustPreferences.get("fname");
            temporaryProfileImageDisplayName = userDisplayName;
            Log.e(TAG, "User display name fname " + userDisplayName);
        } else if ((OustPreferences.get("lname") != null)) {
            userDisplayName = OustPreferences.get("lname");
            temporaryProfileImageDisplayName = userDisplayName;
            Log.e(TAG, "User display name lname " + userDisplayName);
        } else if ((OustPreferences.get("emailId") != null)) {
            userDisplayName = OustPreferences.get("emailId");
            temporaryProfileImageDisplayName = userDisplayName;
            Log.e(TAG, "User display name emailId " + userDisplayName);
        } else if ((activeUser != null) && (activeUser.getUserDisplayName() != null)) {
            userDisplayName = activeUser.getUserDisplayName();
            temporaryProfileImageDisplayName = userDisplayName;
        } else {
            userDisplayName = OustPreferences.get("mobileNum");
            temporaryProfileImageDisplayName = userDisplayName;
            Log.e(TAG, "User display name mobileNum " + userDisplayName);
        }
        Log.e(TAG, "User display name  " + userDisplayName);
        user_mainnametext.setText(userDisplayName);
        user_name.setText(userDisplayName);
    }

    private void setProgressBarColor() {
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            landingnewprogress_bar.invalidate();
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if (toolbarColorCode != null) {
                color = Color.parseColor(toolbarColorCode);
            }
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
            LayerDrawable ld = (LayerDrawable) OustSdkApplication.getContext().getResources().getDrawable(R.drawable.landing_progressbar);
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() >= 2) {
                ld = (LayerDrawable) OustSdkApplication.getContext().getResources().getDrawable(R.drawable.custommodule_progressdrawable);
            }
            final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
            d1.setColorFilter(color, mode);
            landingnewprogress_bar.setProgressDrawable(ld);
            Log.e(TAG, "set landingnewprogress_bar " + ld);
            setMyTotalOcText();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void addDrower() {
        try {
            getDrawerData();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            drawer_listview.setLayoutManager(linearLayoutManager);
            drawer_listview.setItemAnimator(new DefaultItemAnimator());
            newProfileAdapter = new NewProfileAdapter(NewLandingActivity.this, itemDatas);
            newProfileAdapter.setNewLandingDrawerCallback(NewLandingActivity.this);
            drawer_listview.setAdapter(newProfileAdapter);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");

            // Getting reference to the ActionBarDrawerToggle
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.menu, 0, 0) {
                public void onDrawerClosed(View view) {
                }

                public void onDrawerOpened(View drawerView) {
                }
            };
            mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private ArrayList<DrawerItemData> itemDatas;
    private int PROFILE_MODE = 0, ITEM_DATA = 1, INTRO_DATA = 2;

    private void getDrawerData() {
        itemDatas = new ArrayList<>();

        count++;

        DrawerItemData itemData1 = new DrawerItemData();
        String profile = getResources().getString(R.string.profile_text);
        itemData1.setTopic(profile.toUpperCase());
        itemData1.setConditionText("PROFILE");
        itemData1.setType(PROFILE_MODE);
        itemDatas.add(itemData1);

        DrawerItemData itemData2 = new DrawerItemData();
        String history = getResources().getString(R.string.history);
        itemData2.setConditionText("HISTORY");
        itemData2.setTopic(history.toUpperCase());
        itemData2.setType(ITEM_DATA);
        itemDatas.add(itemData2);

        DrawerItemData itemData11 = new DrawerItemData();
        String my_assessment = getResources().getString(R.string.my_assessment);
        itemData11.setTopic(my_assessment.toUpperCase());
        itemData11.setType(ITEM_DATA);
        itemData11.setConditionText("MY ASSESSMENTS");
        itemDatas.add(itemData11);

        DrawerItemData itemData14 = new DrawerItemData();
        String catalogues = getResources().getString(R.string.catalogue_label);
        itemData14.setTopic(catalogues.toUpperCase());
        itemData14.setType(ITEM_DATA);
        itemData14.setConditionText("CATALOGUES");
        itemDatas.add(itemData14);


        DrawerItemData itemData15 = new DrawerItemData();
        String favourites = getResources().getString(R.string.favourites);
        itemData15.setTopic(favourites.toUpperCase());
        itemData15.setType(ITEM_DATA);
        itemData15.setConditionText("FAVOURITES");
        itemDatas.add(itemData15);

        if (!(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY))) {
            DrawerItemData itemData77 = new DrawerItemData();
            String my_diary = getResources().getString(R.string.my_diary);
            itemData77.setTopic(my_diary.toUpperCase());
            itemData77.setType(ITEM_DATA);
            itemData77.setConditionText("MY DIARY");
            itemDatas.add(itemData77);
        }

        DrawerItemData badgeItem = new DrawerItemData();
        String my_diary = getResources().getString(R.string.achievements);
        badgeItem.setTopic(my_diary.toUpperCase());
        badgeItem.setType(ITEM_DATA);
        badgeItem.setConditionText("Achievement");
        itemDatas.add(badgeItem);

       /* if (OustAppState.getInstance().getActiveUser() != null &&
                OustAppState.getInstance().getActiveUser().getBadges() != null &&
                OustAppState.getInstance().getActiveUser().getBadges().size() != 0) {

            DrawerItemData badgeItem = new DrawerItemData();
            String my_diary = getResources().getString(R.string.badges);
            badgeItem.setTopic(my_diary.toUpperCase());
            badgeItem.setType(ITEM_DATA);
            badgeItem.setConditionText("BADGES");
            itemDatas.add(badgeItem);

        }*/

        if ((OustPreferences.getAppInstallVariable(AppConstants.StringConstants.TEAM_ANALYTICS))) {
            DrawerItemData teamAnalytics = new DrawerItemData();
            teamAnalytics.setTopic(getResources().getString(R.string.team_analytics));
            if (OustPreferences.get("teamAnalyticsName") != null && !OustPreferences.get("teamAnalyticsName").isEmpty()) {
                teamAnalytics.setTopic(OustPreferences.get("teamAnalyticsName"));
            }

            teamAnalytics.setConditionText("TEAM ANALYTICS");
            teamAnalytics.setType(ITEM_DATA);
            itemDatas.add(teamAnalytics);
        }

        DrawerItemData itemData6 = new DrawerItemData();
        String contests = getResources().getString(R.string.contests);
        itemData6.setTopic(contests.toUpperCase());
        itemData6.setConditionText("CONTESTS");
        itemData6.setType(ITEM_DATA);
        itemDatas.add(itemData6);


        DrawerItemData itemDataHost = new DrawerItemData();
        String host_app = getResources().getString(R.string.host_app);
        itemDataHost.setTopic(host_app.toUpperCase());
        itemDataHost.setConditionText("HOST APP");
        itemDataHost.setType(ITEM_DATA);
        itemDatas.add(itemDataHost);


        DrawerItemData itemData7 = new DrawerItemData();
        String analytics = getResources().getString(R.string.analytics);
        itemData7.setTopic(analytics.toUpperCase());
        itemData7.setType(ITEM_DATA);
        itemData7.setConditionText("ANALYTICS");
        itemDatas.add(itemData7);

        DrawerItemData itemData16 = new DrawerItemData();
        String faq = getResources().getString(R.string.faq);
        itemData16.setTopic(faq.toUpperCase());
        itemData16.setConditionText("FAQ");
        itemData16.setType(ITEM_DATA);
        itemDatas.add(itemData16);

        DrawerItemData itemData5 = new DrawerItemData();
        String feedback = getResources().getString(R.string.feedback_text);
        itemData5.setTopic(feedback.toUpperCase());
        itemData5.setConditionText("FEEDBACK");
        itemData5.setType(ITEM_DATA);
        itemDatas.add(itemData5);


        DrawerItemData itemData9 = new DrawerItemData();
        String rateApp = getResources().getString(R.string.rate_the_appText);
        itemData9.setTopic(rateApp.toUpperCase());
        itemData9.setConditionText("RATE THE APP");
        itemData9.setType(ITEM_DATA);
        itemDatas.add(itemData9);


        DrawerItemData itemData3 = new DrawerItemData();
        String settings = getResources().getString(R.string.settings);
        itemData3.setTopic(settings.toUpperCase());
        itemData3.setType(ITEM_DATA);
        itemData3.setConditionText("SETTINGS");
        itemDatas.add(itemData3);


        DrawerItemData itemData4 = new DrawerItemData();
        itemData4.setTopic(getResources().getString(R.string.leaderboard_text));
        itemData4.setType(ITEM_DATA);
        itemData4.setConditionText("leaderboard");
        itemDatas.add(itemData4);


        DrawerItemData itemData67 = new DrawerItemData();
        itemData67.setTopic(getResources().getString(R.string.playlist_selection));
        itemData67.setConditionText("Playlist Selection");
        itemData67.setType(ITEM_DATA);
        itemDatas.add(itemData67);


        DrawerItemData itemData8 = new DrawerItemData();
        itemData8.setTopic(getResources().getString(R.string.archived_list).toUpperCase());
        itemData8.setConditionText("ARCHIVED LIST");
        itemData8.setType(ITEM_DATA);
        itemDatas.add(itemData8);


        if (Locale.getDefault().getLanguage().equals("en")) {
            DrawerItemData itemData10 = new DrawerItemData();
            String report_problem = getResources().getString(R.string.report_problem);
            itemData10.setTopic(report_problem);
            itemData10.setConditionText("Report a Problem");
            itemData10.setType(ITEM_DATA);
            itemDatas.add(itemData10);
        }


        DrawerItemData itemData12 = new DrawerItemData();
        String logout = getResources().getString(R.string.logout);
        itemData12.setTopic(logout.toUpperCase());
        itemData12.setConditionText("LOGOUT");
        itemData12.setType(ITEM_DATA);
        itemDatas.add(itemData12);

        DrawerItemData itemData13 = new DrawerItemData();
        String powered_by_oust = getResources().getString(R.string.powered_by_oust);
        itemData13.setTopic(powered_by_oust);
        itemData13.setConditionText(powered_by_oust);
        itemData13.setSubTopic("<u>www.oustlabs.com</u>");
        itemData13.setType(INTRO_DATA);
        itemDatas.add(itemData13);

    }

    private void setDefaultLayoutType() {
        OustStaticVariableHandling.getInstance().setIsNewLayout(0);
        OustPreferences.saveintVar("isNewLayout", 0);
        OustStaticVariableHandling.getInstance().setAppInForebackground(true);
    }

    private void SetFABButton() {
        try {
            fab_action_leaderboard.setIconDrawable(OustSdkTools.getImgDrawable(R.mipmap.leaderboards));
            String leaderborad = getResources().getString(R.string.leader_board_title);
            fab_action_leaderboard.setTitle("" + leaderborad);

//            fab_action_joinmeeting.setIconDrawable(OustSdkTools.getImgDrawable(R.drawable.joinmeeting_landingicon));
            fab_action_joinmeeting.setTitle(getResources().getString(R.string.join_meeting));

            action_favourite.setIconDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
            String favourite = getResources().getString(R.string.favourites);
            action_favourite.setTitle(favourite);
            setFabButtonVisibility();
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isRefreshing) {
                        if (floatingActionsMenu.isExpanded()) {
                            fabBaseLayout.setVisibility(View.GONE);
                        } else {
                            fabBaseLayout.setVisibility(View.VISIBLE);
                        }
                        floatingActionsMenu.toggle();
                    }
                }
            };
            floatingActionsMenu.setAddButtonClickListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFabButtonVisibility() {
        try {
            if (OustPreferences.getAppInstallVariable("disableFabMenu")) {
                floatingActionsMenu.setVisibility(View.GONE);
            } else {
                floatingActionsMenu.setVisibility(View.VISIBLE);

                if (OustPreferences.getAppInstallVariable("disableOrgLeaderboard")) {
                    fab_action_leaderboard.setVisibility(View.GONE);
                    if (OustPreferences.getAppInstallVariable("disableFavCard")) {
                        if (!OustPreferences.getAppInstallVariable("liveTraining")) {
                            fab_action_joinmeeting.setVisibility(View.GONE);
                            fab_action_leaderboard.setVisibility(View.GONE);
                            action_favourite.setVisibility(View.GONE);
                            floatingActionsMenu.setVisibility(View.GONE);
                        } else {
                            fab_action_joinmeeting.setVisibility(View.VISIBLE);
                            fab_action_leaderboard.setVisibility(View.GONE);
                            action_favourite.setVisibility(View.GONE);
                            floatingActionsMenu.setVisibility(View.VISIBLE);
                        }
                    } else {
                        action_favourite.setVisibility(View.VISIBLE);
                        floatingActionsMenu.setVisibility(View.VISIBLE);
                        if (!OustPreferences.getAppInstallVariable("liveTraining")) {
                            fab_action_joinmeeting.setVisibility(View.GONE);
                        } else {
                            fab_action_joinmeeting.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    floatingActionsMenu.setVisibility(View.VISIBLE);
                    fab_action_leaderboard.setVisibility(View.VISIBLE);
                    if (OustPreferences.getAppInstallVariable("disableFavCard")) {
                        action_favourite.setVisibility(View.GONE);
                        if (!OustPreferences.getAppInstallVariable("liveTraining")) {
                            fab_action_joinmeeting.setVisibility(View.GONE);
                        } else {
                            fab_action_joinmeeting.setVisibility(View.VISIBLE);
                        }
                    } else {
                        action_favourite.setVisibility(View.VISIBLE);
                        if (!OustPreferences.getAppInstallVariable("liveTraining")) {
                            fab_action_joinmeeting.setVisibility(View.GONE);
                        } else {
                            fab_action_joinmeeting.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            if (OustPreferences.getAppInstallVariable("disableToolbarLB")) {
                if (action_leaderBoard != null) {
                    action_leaderBoard.setVisible(false);
                }
            } else {
                this.invalidateOptionsMenu();
                if (action_leaderBoard != null) {
                    action_leaderBoard.setVisible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //======================================================================
    private void showLoader() {
        try {
            swipeRefreshLayout.bringToFront();
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.setRefreshing(true);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void hideMainScreenLoader() {
        hideLoader();
    }

    private void hideLoader() {
        try {
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        swiperefreshparent_layout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    //======================================================================================
    /*public BranchCallback branchCallback = new BranchCallback() {
        @Override
        public void onAppResumed() {
            initBranch();
        }
    };
*/

    public void initBranch() {
        try {
            //if(!OustStaticVariableHandling.getInstance().isAppActive()) {
            OustStaticVariableHandling.getInstance().setAppActive(true);
            //Branch branch = Branch.getInstance();


            Uri linkData = this.getIntent().getData();
            if (isBranchLink) {
                isBranchLink = false;
                if (branchLink != null && !branchLink.isEmpty()) {
                    linkData = Uri.parse(branchLink);
                    branchLink = null;
                }


            }

            Log.e(TAG, "ReferringParams " + referringParamsString);


            if (referringParamsString != null && !referringParamsString.isEmpty()) {

                branchIOHandle(referringParamsString);

            } else {
                Branch.sessionBuilder(this).withCallback((referringParams, error) -> {
                    if (error == null) {

                        if (referringParams != null) {
                            Log.e(TAG, "ReferringParams branch  " + referringParams);
                            branchIOHandle(referringParams.toString());
                        }


                    } else {
                        Log.i("BRANCH SDK", error.getMessage());
                    }
                }).withData(linkData).init();
            }

           /* branch.initSession(new Branch.BranchReferralInitListener() {
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error == null) {
                        try {
                            if ((referringParams != null) && (!(referringParams.toString()).isEmpty())) {
                                Log.e("BranchIO", "branch io data " + referringParams.toString());
                                JSONObject jsonObject = new JSONObject(referringParams.toString());
                                if (jsonObject != null &&
                                        jsonObject.has("+clicked_branch_link") &&
                                        jsonObject.getString("+clicked_branch_link").equalsIgnoreCase("true")) {
                                    Log.e(TAG, "BranchIO Data: " + jsonObject.toString());
                                    OustPreferences.save("refereingParam", (referringParams.toString()));
                                    BranchIoResponce branchIoResponse = gson.fromJson(referringParams.toString(), BranchIoResponce.class);
                                    String tenantId = OustPreferences.get("tanentid");
                                    if (branchIoResponse.getOrgId() != null && branchIoResponse.getOrgId().equalsIgnoreCase(tenantId)) {
                                        if ((branchIoResponse.getAssessmentId() != null) && (!branchIoResponse.getAssessmentId().isEmpty())) {
                                            BranchTools.gotoAssessment(branchIoResponse.getAssessmentId());
                                        } else if ((branchIoResponse.getCourseId() != null) && (!branchIoResponse.getCourseId().isEmpty())) {
                                            BranchTools.gotoCoursePage(branchIoResponse.getCourseId());
                                        } else if ((branchIoResponse.getCollectionId() != null) && (!branchIoResponse.getCollectionId().isEmpty())) {
                                            BranchTools.gotoCollectionPage(branchIoResponse.getCollectionId());
                                        } else if ((branchIoResponse.getOrgId() != null &&
                                                !branchIoResponse.getOrgId().isEmpty()) &&
                                                (branchIoResponse.getUserId() != null && !branchIoResponse.getUserId().isEmpty())) {

                                            Log.d(TAG, "BranchIO - New OrgId:" + branchIoResponse.getOrgId() + " --- TanentId:" + tenantId);
                                            if (!branchIoResponse.getOrgId().equalsIgnoreCase(tenantId)) {
                                                branchIoResponse = OustSdkTools.decryptBranchData(branchIoResponse, null);
                                                fillLoginDataMapFromBranchIO(branchIoResponse);
                                            }
                                        }
                                    }

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else {
                        Log.e("BranchIO", "branch io data null");
                    }
                }
            }, this.getIntent().getData(), this);*/
            //}
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private Map<String, String> loginDataMap;

    public void fillLoginDataMapFromBranchIO(BranchIoResponce branchIoResponse) {
        try {
            if ((branchIoResponse.getEncryptedData() != null) && (!branchIoResponse.getEncryptedData().isEmpty())) {
                Log.e("------------", branchIoResponse.getEncryptedData());
                branchIoResponse = OustSdkTools.decryptBranchData(branchIoResponse, null);
            }
            loginDataMap = new HashMap<>();
            loginDataMap.put("orgId", branchIoResponse.getOrgId());
            Log.e("------------", branchIoResponse.getOrgId());
            if (branchIoResponse.getMobileNum() != null) {
                Log.e("Mobile------------", branchIoResponse.getMobileNum());
                OustPreferences.save("mobileNum", branchIoResponse.getMobileNum());
                loginDataMap.put("mobileNum", branchIoResponse.getMobileNum());
            }
            if (branchIoResponse.getUserId() != null) {
                loginDataMap.put("userIdentifier", branchIoResponse.getUserId());
                loginDataMap.put("userId", branchIoResponse.getUserId());
            }
            if (branchIoResponse.getEmailId() != null) {
                loginDataMap.put("emailId", branchIoResponse.getEmailId());
            }
            if (branchIoResponse.getFname() != null) {
                loginDataMap.put("fname", branchIoResponse.getFname());
            }
            if (branchIoResponse.getLname() != null) {
                loginDataMap.put("lname", branchIoResponse.getLname());
            }
            if (branchIoResponse.getPassword() != null) {
                loginDataMap.put("password", branchIoResponse.getPassword());
            }
            if (branchIoResponse.getLanguagePrefix() != null) {
                loginDataMap.put("languagePrefix", branchIoResponse.getLanguagePrefix());
            }
            if (branchIoResponse.getApplication() != null) {
                loginDataMap.put("application", branchIoResponse.getApplication());
            }
            if (branchIoResponse.getApplicationId() != null) {
                loginDataMap.put("applicationId", branchIoResponse.getApplicationId());
            }
            if (branchIoResponse.getAppVersion() != null) {
                loginDataMap.put("appVersion", branchIoResponse.getAppVersion());
            }
            if (branchIoResponse.getUserAgent() != null) {
                loginDataMap.put("userAgent", branchIoResponse.getUserAgent());
            }
            if (branchIoResponse.getDeviceId() != null) {
                loginDataMap.put("deviceId", branchIoResponse.getDeviceId());
            }
            if (branchIoResponse.getTokenId() != null) {
                loginDataMap.put("tokenId", branchIoResponse.getTokenId());
            }
            if (branchIoResponse.getAuthorizationReq()) {
                loginDataMap.put("isAuthorizationReq", "true");
            } else {
                loginDataMap.put("isAuthorizationReq", "false");
            }
            if (branchIoResponse.getProfileImage() != null) {
                long timestamp = System.currentTimeMillis() / 1000;
                long lastUpdateTime = OustPreferences.getTimeForNotification("lastavatarupdatetime");
                if (((timestamp - lastUpdateTime) > 85000) || (lastUpdateTime == 0)) {
                    OustPreferences.saveTimeForNotification("lastavatarupdatetime", timestamp);
                    OustPreferences.save("localImagePathFormTanent", branchIoResponse.getProfileImage());
                }
            }
            showLogoutScreen(branchIoResponse.getOrgId());
        } catch (Exception e) {
            Log.e(TAG, "Error while fetching Data from Branch IO", e);
        }
    }

    public void showLogoutScreen(String orgName) {
        Log.d(TAG, "showLogoutScreen:" + orgName);

        String text = OustStrings.getString("branchio_logout_message");
        text = text.replace("tenant", "" + orgName);
        Log.d(TAG, "" + text);
        mTextviewLogoutMessage.setText(text);
        mLayoutUserLogoutPopup.setVisibility(View.VISIBLE);
        mLayoutUserLogoutPopup.bringToFront();
    }

    private void setStartingData() {
        try {
            //static variable to differentiatate bet free and enterprise app
            OustStaticVariableHandling.getInstance().setAppActive(true);
            OustStaticVariableHandling.getInstance().setEnterpriseUser(true);
            OustAppState.getInstance().setNewLandingCourses(new ArrayList<CourseDataClass>());
            OustPreferences.saveAppInstallVariable("isAppInstalled", true);

            //initialize branch io with listener on this page
            //initialize branch io with listener on this page
            //   MyLifeCycleHandler.branchCallback = branchCallback;

            //set inetrnet change listener in this class
            NetworkUtil.setOnNetworkChangeListener(this);

            removeNotifications();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isAlarmSet = false;

    private void removeNotifications() {
        try {
            //remove  notification count on app icon
            OustPreferences.saveintVar("notificationbadgecount", 0);
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
            if (!isAlarmSet) {
                Intent alarmIntent = new Intent(NewLandingActivity.this, CourseNotificationReceiver.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                AlarmManager manager = (AlarmManager) NewLandingActivity.this.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                manager.setRepeating(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis() + (30 * 1000)), 30 * 1000, pendingIntent);
                ComponentName receiver = new ComponentName(NewLandingActivity.this, AlarmReceiverOnBoot.class);
                PackageManager pm = NewLandingActivity.this.getPackageManager();
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getLocalImage() {
        try {
            if ((OustPreferences.get("localImagePathFormTanent") != null) && (!OustPreferences.get("localImagePathFormTanent").isEmpty())) {
                if (ContextCompat.checkSelfPermission(NewLandingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(OustPreferences.get("localImagePathFormTanent"), options);
                    if (bitmap.getHeight() > bitmap.getWidth()) {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
                    } else {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight(), bitmap.getHeight());
                    }
                    if ((bitmap.getWidth() > 250) && (bitmap.getHeight() > 250)) {
                        OustSdkTools.tempProfile = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
                    } else {
                        OustSdkTools.tempProfile = bitmap;
                    }
                    Intent intent3 = new Intent(NewLandingActivity.this, SubmitCourseCompleteService.class);
                    intent3.setClassName("com.oustme.oustsdk", "com.oustme.oustsdk.service");
                    startService(intent3);
                } else {
                    ActivityCompat.requestPermissions(NewLandingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setActiveUserData() {
        try {
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            //final Intent CallingIntent = getIntent();

            if (OustAppState.getInstance().getActiveUser() == null || OustAppState.getInstance().getActiveUser().getStudentKey() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }

            /*String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);*/

            activeUser = OustAppState.getInstance().getActiveUser();
            String activeGameData = OustPreferences.get("activeGameData");
            if (null != activeGameData) {
                activeGame = OustSdkTools.getActiveGameData(activeGameData);
                if (activeGame != null) {
                    activeUser.setSubject(activeGame.getSubject());
                    activeUser.setTopic(activeGame.getTopic());
                    activeUser.setGrade(activeGame.getGrade());
                    activeUser.setModuleName(activeGame.getModuleName());
                    activeUser.setModuleId(activeGame.getModuleId());
                }
            } else {
                activeUser.setTopic("");
            }

            if (OustPreferences.get("UserAvatar") != null) {
                activeUser.setAvatar(OustPreferences.get("UserAvatar"));
            }
            if (OustPreferences.get("UserDisplayName") != null) {
                activeUser.setUserDisplayName(OustPreferences.get("UserDisplayName"));
            }
            if (OustPreferences.get("OC") != null) {
                activeUser.setAvailableOCCount(OustPreferences.get("OC"));
            }
            OustAppState.getInstance().setActiveUser(activeUser);

            Gson gsonString = new Gson();
            OustPreferences.save("userdata", gsonString.toJson(OustAppState.getInstance().getActiveUser()));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String notificationContestId;


    private void checkWhetherComingFormNotification() {
        Log.e(TAG, "checkWhetherComingFormNotification");
        OustPreferences.saveTimeForNotification("lastNotificationOpenedTime", (System.currentTimeMillis() / 1000));
        final Intent CallingIntent = getIntent();
        notificationContestId = CallingIntent.getStringExtra("contestId");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("Notification", " NewLandingActivity inside checkWhetherComingFormNotification()");
                    String assessmentId = CallingIntent.getStringExtra("assessmentId");
                    String courseId = CallingIntent.getStringExtra("courseId");
                    String collectionId = CallingIntent.getStringExtra("collectionId");

                    String noticeBoardId = CallingIntent.getStringExtra("noticeBoardId");
                    String catalogueId = CallingIntent.getStringExtra("catalogue_id");
                    String fffc_Id = CallingIntent.getStringExtra("fastest_finger_first_contest_id");
                    String cpl_id = CallingIntent.getStringExtra("cpl_id");
                    String feedId = CallingIntent.getStringExtra("feedId");

                    zoom_meetingId = CallingIntent.getStringExtra("zoom_meetingId");
                    boolean isFeedDistributed = CallingIntent.getBooleanExtra("isFeedDistributed", false);

                    int nbId = CallingIntent.getIntExtra("nbId", 0);
                    int postId = CallingIntent.getIntExtra("postId", 0);
                    int commentId = CallingIntent.getIntExtra("commentId", 0);
                    int replyId = CallingIntent.getIntExtra("replyId", 0);


                    Log.d(TAG, "NoticeBoardId--> " + "postId:-> " + postId + " commentId " + commentId + " nbId " + nbId + " replyId " + replyId);
                    Log.d(TAG, "CourseId--> " + courseId);
                    Log.d(TAG, "assessmentId assessmentId--> " + assessmentId);
                    Log.d(TAG, "collectionId--> " + collectionId);
                    Log.d(TAG, "isFeedDistributed--> " + isFeedDistributed);
                    Log.d(TAG, "collection-feed-Id--> " + feedId);
                    Log.d(TAG, "noticeBoardId--> " + noticeBoardId);
                    Log.d(TAG, "catalogueId --> " + catalogueId);

                    if ((courseId != null) && (!courseId.isEmpty())) {
                        isNotificationPageOpen = true;
                        if (activeUser != null && activeUser.getStudentKey() != null && !activeUser.getStudentKey().isEmpty()) {
//                            BranchTools.checkCourseExistOrNot(courseId, activeUser.getStudentKey());
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                        }
                    } else if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                        isNotificationPageOpen = true;
                        if (activeUser != null && activeUser.getStudentKey() != null && !activeUser.getStudentKey().isEmpty()) {
//                            BranchTools.checkAssessmentExistOrNot(assessmentId, activeUser.getStudentKey());
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                        }
                    } else if ((collectionId != null) && (!collectionId.isEmpty())) {
                        isNotificationPageOpen = true;
                        BranchTools.gotoCollectionPage(collectionId);
                    } else if ((zoom_meetingId != null) && (!zoom_meetingId.isEmpty())) {
                        isNotificationPageOpen = true;
                        startJoiningMeetingAfterSomeTime();
                    } else if (isFeedDistributed) {
                        readDataFromFirebaseForFeed(feedId);
                    } else if (replyId != 0) {
                        isNotificationPageOpen = true;
                        Intent intent = new Intent(NewLandingActivity.this, NBCommentActivity.class);
                        intent.putExtra("nbId", (long) nbId);
                        intent.putExtra("postId", (long) postId);
                        intent.putExtra("commentId", (long) commentId);
                        startActivity(intent);
                    } else if (commentId != 0) {
                        isNotificationPageOpen = true;
                        Intent intent = new Intent(NewLandingActivity.this, NBPostDetailsActivity.class);
                        intent.putExtra("nbId", (long) nbId);
                        intent.putExtra("postId", (long) postId);
                        intent.putExtra("commentId", (long) commentId);
                        startActivity(intent);
                    } else if (postId != 0) {
                        isNotificationPageOpen = true;
                        Intent intent = new Intent(NewLandingActivity.this, NBPostDetailsActivity.class);
                        intent.putExtra("nbId", (long) nbId);
                        intent.putExtra("postId", (long) postId);
                        startActivity(intent);
                    } else if (noticeBoardId != null && !noticeBoardId.isEmpty()) {
                        isNotificationPageOpen = true;
                        readDataFromFirebaseForNB(noticeBoardId);
                    } else if (catalogueId != null && !catalogueId.isEmpty()) {
                        OustPreferences.saveTimeForNotification("catalogueId", Long.parseLong(catalogueId));
                        seeAllCatalog();
                    } else if (cpl_id != null && !cpl_id.isEmpty()) {
                        checkCPLDistributionOrNot(cpl_id);
                    } else if (fffc_Id != null && !fffc_Id.isEmpty()) {
                        BranchTools.getUserFFContest(activeUser.getStudentKey(), activeUser.getAvatar()
                                , activeUser.getUserDisplayName(), fffc_Id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }, 1100);
    }

    private void checkCPLDistributionOrNot(String cpl_id) {
        try {
            String message;
            Log.e("TAG", "checkCPLDistributionOrNot: cpl id-> " + cpl_id);
            if (multipleCpl) {
                message = "/landingPage/" + activeUser.getStudentKey() + "/cpl/" + cpl_id;
            } else {
                message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL/" + cpl_id;
            }
            ValueEventListener avatarListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                           /* String cplId = (String) snapshot.getValue();
                            if (cplId != null && !cplId.isEmpty()) {
                                if (cplId.equalsIgnoreCase(cpl_id)) {*/
                            Intent intent = new Intent(NewLandingActivity.this, CplIntroActivity.class);
                            intent.putExtra("cplId", cpl_id);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                               /* } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.check_cpl_distribute_or_not));
                                }
                            }*/
                        } else {
                            OustSdkTools.showToast(getResources().getString(R.string.check_cpl_distribute_or_not));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(avatarListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void readDataFromFirebaseForFeed(String feedId) {
        Log.d(TAG, "readDataFromFirebaseForFeed: " + feedId);
        String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId;
        Log.d(TAG, "getUserNewsFeed: " + message);
        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot != null) {
                        Log.d(TAG, "UserFeed onDataChange: " + dataSnapshot.getValue());

                        final HashMap<String, Object> mymap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if ((mymap != null)) {
                            long feedID = 0;
                            long timeStamp = 0;
                            boolean isClicked = true, isUserFeedViewed = false, isFeedCoinsAdded = false;
                            if ((mymap.get("feedId") != null)) {
                                feedID = (long) mymap.get("feedId");
                            }
                            if ((mymap.get("timeStamp")) != null) {
                                timeStamp = (long) mymap.get("timeStamp");
                            }
                            if (mymap.get(AppConstants.StringConstants.IS_FEED_CLICKED) != null) {
                                isClicked = (boolean) mymap.get(AppConstants.StringConstants.IS_FEED_CLICKED);
                            }

                            if (mymap.get(AppConstants.StringConstants.IS_FEED_VIEWED) != null) {
                                isUserFeedViewed = (boolean) mymap.get(AppConstants.StringConstants.IS_FEED_VIEWED);
                            }

                            if (mymap.get(AppConstants.StringConstants.FEED_COINS_ADDED) != null) {
                                isFeedCoinsAdded = (boolean) mymap.get(AppConstants.StringConstants.FEED_COINS_ADDED);
                            }

                            readUserFeedDetails(("" + feedID), ("" + timeStamp), 1, isClicked, isUserFeedViewed, isFeedCoinsAdded);

                        } else {
                            OustSdkTools.showToast("Feed is not distributed to this user");
                        }

                    } else {
                        OustSdkTools.showToast("Feed is not distributed to this user");
                    }
                } catch (Exception e) {
                    OustSdkTools.showToast(getResources().getString(R.string.no_feeds_available));
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                OustSdkTools.showToast(getResources().getString(R.string.no_feeds_available));
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(newsfeedListListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void readUserFeedDetails(final String id, final String timeStamp,
                                     final int layout_type, final boolean isClicked, final boolean isUserFeedViewd, final boolean isFeedCoinsAddedd) {
        String message = "/feeds/feed" + id;

        Log.d(TAG, "readUserFeedDetails: ");


        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(TAG, "Feed onDataChange: " + dataSnapshot.getValue());
                        String key = dataSnapshot.getKey();
                        key = key.replace("feed", "");
                        final HashMap<String, Object> newsfeedMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (newsfeedMap != null) {
                            CommonTools commonTools = new CommonTools();
                            //DTONewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, timeStamp);
                            DTONewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, new DTONewFeed());
                            //NewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, timeStamp);
                            //NewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, newFeed);
                            if ((timeStamp != null) && (!timeStamp.isEmpty())) {
                                feed1.setTimestamp(Long.parseLong(timeStamp));
                            }

                            feed1.setFeedViewed(isUserFeedViewd);
                            feed1.setClicked(isClicked);
                            /*NewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, timeStamp);
                            feed1.setClicked(isClicked);
                            feed1.setFeedViewed(isUserFeedViewd);*/
                            feed1.setFeedId(Long.parseLong(key));
                            feed1.setFeedCoinsAdded(isFeedCoinsAddedd);
                            //feed1.setFeedType();

                            gotoFeedPage(feed1);
                        } else {
                            OustSdkTools.showToast(getResources().getString(R.string.not_able_load_module));
                        }
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.not_able_load_module));
                    }
                } catch (Exception e) {
                    OustSdkTools.showToast(getResources().getString(R.string.problem_loading_module));
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(newsfeedListListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void gotoFeedPage(DTONewFeed mFeed) {
        if ((mFeed.getFeedType() != null)) {

            checkFeedRewardUpdate(mFeed);
            Log.d(TAG, "getFeedType() " + mFeed.getFeedType());
            switch (mFeed.getFeedType()) {
                case DOUBLE_REFERRAL:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    break;
                case CONTENT_UPDATE:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    break;
                case EVENT_UPDATE:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    break;
                case GROUP_UPDATE:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    break;
                case ASSESSMENT_PLAY:
                    Log.d(TAG, "getAssessmentId() " + mFeed.getAssessmentId());
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
//                    BranchTools.checkAssessmentExistOrNot("" + mFeed.getAssessmentId(), activeUser.getStudentKey());
                    break;
                case COURSE_UPDATE:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());

                    readDataFromFirebaseForCourse("" + mFeed.getCourseId(), activeUser.getStudentKey());
                    //gotoCourse("" + mFeed.getCourseId());
                    break;
                case SURVEY:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    gotoSurvey("" + mFeed.getAssessmentId(), mFeed.getHeader());
                    OustAppState.getInstance().setCurrentSurveyFeed(mFeed);
                    break;
                case GAMELET_WORDJUMBLE:
                case GAMELET_WORDJUMBLE_V2:
                case GAMELET_WORDJUMBLE_V3:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    String feedType = String.valueOf(mFeed.getFeedType());
                    gotoGamelet("" + mFeed.getAssessmentId(), feedType);
                    break;
                /*case JOIN_MEETING:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    //joinMeeting("" + mFeed.getId());
                    break;*/

                case CONTENT_PLAY_LIST:
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    cplFeedClicked(mFeed.getParentCplId());
                    break;

                case GENERAL:
                    String mUrl = mFeed.getLink();
                    if ((mUrl != null) && (!mUrl.isEmpty())) {
                        if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                            mUrl = "http://" + mUrl;
                        }
                        onnewsfeedClick(mUrl);
                    } else {
                        //OustSdkTools.showToast("Feed url is not found");
                        if (mFeed.getCourseCardClass() != null) {
                            clickOnCard(mFeed.getCourseCardClass(), mFeed);
                        } else {
                            BranchTools.gotoFeedPage("" + mFeed.getFeedId(), activeUser);
                        }
                    }
                    break;
                default: {
                    onFeedClick(mFeed.getFeedId(), (int) mFeed.getCplId());
                    //BranchTools.gotoFeedPage(""+mFeed.getFeedId());

                    if (mFeed.getCourseCardClass() != null) {
                        clickOnCard(mFeed.getCourseCardClass(), mFeed);
                    } else {
                        Intent intent = new Intent(NewLandingActivity.this, FeedPreviewActivity.class);
                        intent.putExtra("ISCOMMENT", mFeed.isCommentble());
                        intent.putExtra("ISLIKE", mFeed.isLikeble());
                        intent.putExtra("ISSHARE", mFeed.isSharable());
                        intent.putExtra("FEED_DATA", mFeed);
                        startActivity(intent);
                    }
                }
            }
        } else {
            Log.e("Feed", "getFeedType()==null");
        }
    }

    public void gotoSurvey(String assessmentId, String surveyTitle) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(NewLandingActivity.this, SurveyDetailActivity.class);
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                    intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                }
                intent.putExtra("surveyTitle", surveyTitle);
                intent.putExtra("assessmentId", assessmentId);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotoGamelet(String assessmentId, String type) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(NewLandingActivity.this, GameLetActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("feedType", type);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void clickOnCard(DTOCourseCard courseCardClass, DTONewFeed mFeed) {
        Log.d(TAG, "clickOnCard: ");
        //if (courseCardClass != null) {
        if (courseCardClass.getCardMedia() != null && courseCardClass.getCardMedia().size() != 0) {
            if (courseCardClass.getCardMedia().get(0).getMediaType() != null && !courseCardClass.getCardMedia().get(0).getMediaType().isEmpty()) {
                String mediaType = courseCardClass.getCardMedia().get(0).getMediaType();
                if (mediaType.equalsIgnoreCase("IMAGE") || mediaType.equalsIgnoreCase("GIF") || mediaType.equalsIgnoreCase("AUDIO")) {
                    Log.d("TAG", "getItemViewType: ");
                    //return general_feed;
                    OustDataHandler.getInstance().setCourseCardClass(courseCardClass);
                    Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                    intent.putExtra("type", "card");
                    startActivity(intent);
                } else {

                    if (courseCardClass.getCardMedia().get(0).getMediaPrivacy() != null && courseCardClass.getCardMedia().get(0).getMediaPrivacy().equals("PUBLIC")) {
                        //return youtube_video_feed;
                        Intent feedIntent = new Intent(context, PublicVideoFeedCardScreen.class);
                        Bundle feedbundle = new Bundle();
                        feedbundle.putParcelable("Feed", mFeed);
                        feedbundle.putString("CardData", new Gson().toJson(mFeed.getCourseCardClass()));
                        feedbundle.putSerializable("ActiveUser", activeUser);
                        feedbundle.putBoolean("FeedComment", false);
                        feedbundle.putBoolean("isFeedLikeable", mFeed.isLikeble());
                        feedIntent.putExtras(feedbundle);
                        startActivity(feedIntent);
                        //((Activity) context).startActivityForResult(feedIntent,1444);

                    } else {
                        //return video_feed;
                        Intent feedIntent = new Intent(context, VideoCardDetailScreen.class);
                        Bundle feedbundle = new Bundle();
                        feedbundle.putParcelable("Feed", mFeed);
                        feedbundle.putString("CardData", new Gson().toJson(mFeed.getCourseCardClass()));
                        feedbundle.putSerializable("ActiveUser", activeUser);
                        feedbundle.putBoolean("FeedComment", false);
                        feedIntent.putExtras(feedbundle);
                        startActivity(feedIntent);
                        //((Activity) context).startActivityForResult(feedIntent, 1444);
                    }
                }
            }
        }
    }

    private void onnewsfeedClick(String mUrl) {
        Log.d(TAG, "onnewsfeedClick: " + mUrl);
        Intent intent = new Intent(NewLandingActivity.this, FeedCardActivity.class);
        intent.putExtra("type", "url");
        intent.putExtra("mUrl", mUrl);
        startActivity(intent);
    }

    HashMap<String, NBTopicData> nbTopicDataHashMap_1 = new HashMap<>();

    private void readDataFromFirebaseForNB(final String nb_Id) {
        Log.d(TAG, "readDataFromFirebaseForNB: " + nb_Id);

        try {
            final String message = "/noticeBoard/noticeBoard" + nb_Id;
            ValueEventListener myNBListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            //Object o1 = dataSnapshot.getValue();
                            Log.d(TAG, "noticeboard data: " + dataSnapshot.getValue());
                            Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            NBTopicData nbTopicData = new NBTopicData();

                            if (lpMainMap.containsKey("nbId")) {
                                long id = OustSdkTools.convertToLong(lpMainMap.get("nbId"));
                                nbTopicData.setId(id);
                                nbTopicData.setExtraNBData(nbTopicData, lpMainMap);
                            }

                            gotoNBPage(nbTopicData);

                        } else {
                            Log.d(TAG, "noticeboard not found: ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "noticeboard onCancelled: ");
                }
            };

            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myNBListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoNBPage(final NBTopicData nbTopicData) {
        try {
            Log.d(TAG, "gotoNBPage: ");
            NBDataHandler.getInstance().setNbTopicData(nbTopicData);
            Intent intent = new Intent(NewLandingActivity.this, NBTopicDetailActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void readDataFromFirebaseForCourse(final String courseID, String studentKey) {
        try {
            Log.d(TAG, "readDataFromFirebaseForCourse: " + courseID);
            String message = ("course/course" + courseID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            CommonTools commonTools = new CommonTools();
                            final Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                            if ((lpMap != null) && (lpMap.get("courseId") != null)) {
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonLandingData.setId("COURSE" + courseID);
                                commonLandingData = commonTools.getCourseCommonData(lpMap, commonLandingData);
                                if (commonLandingData != null) {
                                    if (commonLandingData.getMode() != null && commonLandingData.getMode().equalsIgnoreCase("ARCHIVED")) {
                                        removeReminderNotification(commonLandingData);
                                    } else {
//                                        BranchTools.checkCourseExistOrNot(courseID, studentKey);
                                        //gotoCourse(commonLandingData, courseID);
                                    }
                                }

                            }
                        } else {
                            Log.e(TAG, "Course not found");
                            OustSdkTools.showToast(getResources().getString(R.string.not_able_load_module));
                        }

                    } catch (Exception e) {
                        //gotoCourse(null, courseID);
//                        BranchTools.checkCourseExistOrNot(courseID, studentKey);
                        Log.e(TAG, "caught exception inside set singelton ", e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //gotoCourse(null, courseID);
//                    BranchTools.checkCourseExistOrNot(courseID, studentKey);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(eventListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
//            BranchTools.checkCourseExistOrNot(courseID, studentKey);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoCourse(CommonLandingData commonLandingData, String courseID) {
        try {
            Log.d(TAG, "gotoCourse: " + commonLandingData.getId());
            if (commonLandingData != null) {
                Log.d(TAG, "gotoCourse: commanlanding data not null");
                if (commonLandingData.getCourseType() != null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")) {
                    Intent intent = new Intent(NewLandingActivity.this, CourseMultiLingualActivity.class);
                    List<MultilingualCourse> multilingualCourseList = new ArrayList<>();
                    multilingualCourseList = commonLandingData.getMultilingualCourseListList();
                    String id = commonLandingData.getId();
                    if (id.contains("COURSE"))
                        id = id.replace("COURSE", "");
                    intent.putExtra("learningId", id);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("multilingualDataList", (Serializable) multilingualCourseList);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(NewLandingActivity.this, NewLearningMapActivity.class);
                    String id = commonLandingData.getId();
                    if (id.contains("COURSE"))
                        id = id.replace("COURSE", "");
                    intent.putExtra("learningId", id);
                    intent.putExtra("REGULAR_MODE", true);
                    startActivity(intent);
                }
            } else {
                Log.d(TAG, "gotoCourse: commanlanding data null");
                Intent intent = new Intent(NewLandingActivity.this, NewLearningMapActivity.class);
                String id = courseID;
                if (id.contains("COURSE"))
                    id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                intent.putExtra("REGULAR_MODE", true);
                startActivity(intent);
            }
        } catch (Exception e) {
        }
    }


    //===================================================================================================
    //set top landingdata
    private void setTopMainTabData() {
        try {
            if ((mainLandingTabDataList != null) && (mainLandingTabDataList.size() > 0)) {
                if (mainLandingTabDataList.get(0).getType().equals("TEXT")) {
                    maintab_image_layout.setVisibility(View.GONE);
                    maintab_text_layout.setVisibility(View.VISIBLE);
                    toptab_texta.setVisibility(View.VISIBLE);
                    toptab_texta.setText(mainLandingTabDataList.get(0).getName());
                    if (mainLandingTabDataList.size() > 1) {
                        toptab_textb.setVisibility(View.VISIBLE);
                        toptab_textb.setText(mainLandingTabDataList.get(1).getName());
                    }
                    if (mainLandingTabDataList.size() > 2) {
                        toptab_lineb.setVisibility(View.VISIBLE);
                        toptab_linea.setVisibility(View.VISIBLE);
                        toptab_textc.setVisibility(View.VISIBLE);
                        toptab_textc.setText(mainLandingTabDataList.get(2).getName());
                    }
                }
            } else {
                maintab_text_layout.setVisibility(View.GONE);
                maintab_image_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setTopSubTabData() {
        try {
            subtab_layout.setVisibility(View.VISIBLE);
            if ((subLandingTabDataList != null) && (subLandingTabDataList.size() > 0)) {
                subtaba_text.setText(subLandingTabDataList.get(0));
                if (subLandingTabDataList.size() > 1) {
                    subtabb_text.setText(subLandingTabDataList.get(1));
                }
                if (subLandingTabDataList.size() > 2) {
                    subtabc_text.setText(subLandingTabDataList.get(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //======================================================================================


    public void getUserCourses() {
        Log.e(TAG, "inside getUserCourses() ");
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/course";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            //mCoursesCompletedList = new ArrayList<>();
                            haveAllCourses = true;
                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() < 2) {
                                if (OustPreferences.getAppInstallVariable("hideCourse")) {
                                    return;
                                }
                                setPagerAdapter(getResources().getString(R.string.courses_text));
                            }
                            Object o1 = dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (myDeskInfoMap != null && myDeskInfoMap.size() != 0) {
                                myDeskInfoMap.remove("COURSE" + OustPreferences.get("removeMultiLingual"));
                            }
                            if (myDeskData != null && myDeskData.size() != 0) {
                                myDeskData.remove("COURSE" + OustPreferences.get("removeMultiLingual"));
                            }
                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    final Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        if (lpMap.get("elementId") != null) {
                                            String id = "COURSE" + i;
                                            myDeskInfoMap.put(id, "COURSE");
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                commonLandingData.setType("COURSE");
                                                commonLandingData.setId(id);

                                                if (lpMap.get("completionPercentage") != null) {
                                                    String percentage = lpMap.get("completionPercentage").toString();
                                                    if (percentage.contains("100")) {
                                                        commonLandingData.setCompletionPercentage(100);
                                                    }
                                                }
                                                myDeskData.put(id, commonLandingData);
                                            } else {
                                                CommonLandingData commonLandingData = new CommonLandingData();
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                commonLandingData.setType("COURSE");
                                                commonLandingData.setId(id);
                                                if (lpMap.get("completionPercentage") != null) {
                                                    String percentage = lpMap.get("completionPercentage").toString();
                                                    if (percentage.contains("100")) {
                                                        commonLandingData.setCompletionPercentage(100);
                                                    }
                                                }
                                                myDeskData.put(id, commonLandingData);
                                            }
                                        }
                                    }
                                }
                                //getCompletedCoursesData();
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String courseKey : lpMainMap.keySet()) {
                                    if ((lpMainMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(courseKey);
                                        if (lpMap != null) {
                                            if (lpMap.get("elementId") != null) {
                                                String id = "COURSE" + courseKey;
                                                myDeskInfoMap.put(id, "COURSE");
                                                if (myDeskData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myDeskData.get(id);
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    commonLandingData.setType("COURSE");
                                                    commonLandingData.setId(id);
                                                    if (lpMap.get("completionPercentage") != null) {
                                                        String percentage = lpMap.get("completionPercentage").toString();
                                                        if (percentage.contains("100")) {
                                                            commonLandingData.setCompletionPercentage(100);
                                                        }
                                                    }
                                                    myDeskData.put(id, commonLandingData);
                                                } else {
                                                    CommonLandingData commonLandingData = new CommonLandingData();
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    commonLandingData.setType("COURSE");
                                                    commonLandingData.setId(id);
                                                    if (lpMap.get("completionPercentage") != null) {
                                                        String percentage = lpMap.get("completionPercentage").toString();
                                                        if (percentage.contains("100")) {
                                                            commonLandingData.setCompletionPercentage(100);
                                                        }
                                                    }
                                                    myDeskData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    }
                                }
                                // getCompletedCoursesData();
                            }

                            getUserEvents();


                        } else {
                            haveAllCourses = true;
                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                createLayout3CourseList(defaultCategoryId);
                            } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                if (OustStaticVariableHandling.getInstance().isInternetConnectionOff()) {
                                    no_data_text.setText(getResources().getString(R.string.no_internet_data));
                                } else {
                                    no_data_text.setText(getResources().getString(R.string.no_data_available));
                                }
                                no_data_text.setVisibility(View.VISIBLE);
                                hideLoader();
                                createMainList();
                            } else {
                                OustDataHandler.getInstance().setPaginationReachToEnd(true);
                                createCourseMainList();
                            }
                            getUserEvents();
                        }
                        // mCompletedCoursesList = new ArrayList<>();

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    isCoursesAvailable = true;
                    setCollectiveListener();
                    //getCompletedCoursesData();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            courseFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createLayout3CourseList(long categoryId) {
        try {
            if (cplHashMapData != null) {
                extractCourseAssessmentFromCPL(cplHashMapData);
            }
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                if (haveAllCourses) {
                    getTotalCourses();
                    Collections.sort(OustAppState.getInstance().getMyDeskList(), sortByDate);
                    if ((searchOn) && (searchText != null) && (!searchText.isEmpty())) {
                        filterNewLandingCourse(searchText);
                    } else {
                        List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
                        CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                        myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
                        initialPendingModuleCount = myDeskFilterdData.size();
                        totalCoursesCount3 = initialPendingModuleCount;
                        if (myDeskFilterdData.size() == 0) {
                            if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                                getNextData("COURSE");
                            } else {
                                createNewCourseList3(myDeskFilterdData);
                            }
                        } else {
                            createNewCourseList3(myDeskFilterdData);
                        }
                    }
                    setMyTotalProgressText();
                    setMyTotalOcText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createNewCourseList3(List<CommonLandingData> myDeskFilterdData) {
        try {
            if ((myDeskFilterdData != null)) {
                showSearchIcon();
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                    createCourseList3(filterListByLearnType(myDeskFilterdData));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "inside createCourseList() exception caught", e);
        }
    }

    // Generic function to find the index of an element in an object array in Java
    public static int findCourse(List<CommonLandingData> commonLandingData, String id) {

        for (int i = 0; i < commonLandingData.size(); i++)
            if (commonLandingData.get(i).getId().equalsIgnoreCase(id))
                return i;

        return -1;
    }

    private NewLandingModuleAdaptera newLandingModuleAdaptera;

    private void createCourseList3(ArrayList<CommonLandingData> commonLandingData) {
        try {
            if (!currentTabName.equals("FEEDS")) {
                no_data_text.setVisibility(View.GONE);
            }

            if (commonLandingData.size() < 3) {
                sub_tab_3.setVisibility(View.GONE);
            }
            if (newCourse_recyclerview.getVisibility() == View.VISIBLE) {
                hideLoader();
            }
            if (currentCourseTag != null && currentCourseTag.length() > 0) {
                commonLandingData = new CommonLandingFilter().getTagCourses(commonLandingData, currentCourseTag);
            }
            if (commonLandingData != null && commonLandingData.size() > 0) {
                if (currentTabName.equals("COURSE") && newCourse_recyclerview.getVisibility() == View.GONE) {
                    newCourse_recyclerview.setVisibility(View.VISIBLE);
                }
                no_data_text.setVisibility(View.GONE);
                if (newLandingModuleAdaptera == null) {
                    newLandingModuleAdaptera = new NewLandingModuleAdaptera(commonLandingData);
                    newLandingModuleAdaptera.setContext(NewLandingActivity.this);
                    //newCourse_recyclerview.setNestedScrollingEnabled(false);
                    boolean hideCatalogue = isHideCatalogueforTab("COURSE");
                    if ((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!hideCatalogue)) {
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        newCourse_recyclerview.setLayoutManager(mLayoutManager);
                        newLandingModuleAdaptera.setAdapterHorizontal(true);
                    } else {
                        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
                        newCourse_recyclerview.setLayoutManager(mLayoutManager);
                    }
                    newCourse_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    newLandingModuleAdaptera.setmyDeskDataMap(myDeskInfoMap);
                    newLandingModuleAdaptera.setDataLoaderNotifier(NewLandingActivity.this);
                    newCourse_recyclerview.setAdapter(newLandingModuleAdaptera);
                    newCourse_recyclerview.smoothScrollToPosition(0);
                } else {
                    if (isSearchOn) {
                        newLandingModuleAdaptera.notifyListChnage(commonLandingData, totalModuleCount);
                    } else {
                        newLandingModuleAdaptera.notifyListChnage(commonLandingData, totalModuleCount);
                    }
                }
            } else {
                if (searchOn) {
                    no_data_text.setText(getResources().getString(R.string.no_match_found));
                } else {
                    if (!currentTabName.equals("FEEDS")) {
                        no_data_text.setText(getResources().getString(R.string.no_module_yet));
                    }

                    if (currentTabName.equals("COURSE")) {
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                    newCourse_recyclerview.setVisibility(View.GONE);
                }
                if (newCourse_recyclerview.getVisibility() == View.VISIBLE) {
                    no_data_text.setVisibility(View.VISIBLE);
                    newCourse_recyclerview.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //check for hide catalogue flag
    private boolean isHideCatalogueforTab(String tabType) {
        boolean hideCatalogue = false;
        if (tabInfoDataArrayList != null) {
            for (int i = 0; i < tabInfoDataArrayList.size(); i++) {
                if (tabInfoDataArrayList.get(i).getIndexName().contains(tabType)) {
                    hideCatalogue = tabInfoDataArrayList.get(i).isHideCatalogue();
                    break;
                }
            }
        }
        return hideCatalogue;
    }

    public void getUserAssessments() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/assessment";
            Log.d(TAG, "getUserAssessments: " + message);
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        int assesmentcount = 0;
                        int tot = (int) dataSnapshot.getChildrenCount();

                        int pend = tot - assesmentcount;
                        if (dataSnapshot.getValue() != null) {
                            //mAssessmentCompletedList = new ArrayList<>();
                            haveAllAssignments = true;
                            Object o1 = dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() != 2 && !COURSE_ASSESSMENT_TAB_FOUND) {
                                if (OustPreferences.getAppInstallVariable("hideAssessment")) {
                                    //Log.d(TAG, "onDataChange: hideassessment");
                                    return;
                                }
                                setPagerAdapter(getResources().getString(R.string.challenges_text));
                                if (o1.getClass().equals(ArrayList.class)) {
                                    List<Object> assessmentList = (List<Object>) dataSnapshot.getValue();
                                    for (int i = 0; i < assessmentList.size(); i++) {
                                        final Map<String, Object> assessmentMap = (Map<String, Object>) assessmentList.get(i);
                                        if (assessmentMap != null) {
                                            if (assessmentMap.get("elementId") != null) {
                                                String id = "ASSESSMENT" + i;
                                                myDeskInfoMap.put(id, "ASSESSMENT");
                                                if (myAssessmentData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myAssessmentData.get(id);
                                                    commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                    commonLandingData.setType("ASSESSMENT");
                                                    commonLandingData.setId(id);
                                                    if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                        if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                            long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));

                                                            if (completePercentage != 0) {
                                                                commonLandingData.setCompletionPercentage(100);
                                                            }
                                                        }
                                                    }
                                                    myAssessmentData.put(id, commonLandingData);
                                                } else {
                                                    CommonLandingData commonLandingData = new CommonLandingData();
                                                    commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                    commonLandingData.setType("ASSESSMENT");
                                                    commonLandingData.setId(id);
                                                    if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                        if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                            long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));

                                                            if (completePercentage != 0) {
                                                                commonLandingData.setCompletionPercentage(100);
                                                            }
                                                        }
                                                    }
                                                    myAssessmentData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    }
                                } else if (o1.getClass().equals(HashMap.class)) {
                                    //  Log.d(TAG, "onDataChange: inside Hashmap:");
                                    Map<String, Object> assessmentMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                    for (String courseKey : assessmentMainMap.keySet()) {
                                        //Log.d(TAG, "onDataChange: coursekey:"+assessmentMainMap.get(courseKey));
                                        if ((assessmentMainMap.get(courseKey) != null)) {
                                            final Map<String, Object> assessmentMap = (Map<String, Object>) assessmentMainMap.get(courseKey);
                                            if (assessmentMap != null) {
                                                if (assessmentMap.get("elementId") != null) {
                                                    String id = "ASSESSMENT" + courseKey;
                                                    int remainder = id.length() % 2;
                                                    myDeskInfoMap.put(id, "ASSESSMENT");
                                                    if (myAssessmentData.get(id) != null) {
                                                        CommonLandingData commonLandingData = myAssessmentData.get(id);
                                                        commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                        commonLandingData.setType("ASSESSMENT");
                                                        commonLandingData.setId(id);
                                                        if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                            if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                                long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));

                                                                if (completePercentage != 0) {
                                                                    commonLandingData.setCompletionPercentage(100);
                                                                }
                                                            }
                                                        }
                                                        myAssessmentData.put(id, commonLandingData);
                                                    } else {
                                                        CommonLandingData commonLandingData = new CommonLandingData();
                                                        commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                        commonLandingData.setType("ASSESSMENT");
                                                        commonLandingData.setId(id);
                                                        if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                            if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                                long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));
                                                                if (completePercentage != 0) {
                                                                    commonLandingData.setCompletionPercentage(100);
                                                                }
                                                            }
                                                        }
                                                        myAssessmentData.put(id, commonLandingData);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "onDataChange: either hashmap nor arraylist");
                                }
                            } else {
                                if (o1.getClass().equals(ArrayList.class)) {
                                    List<Object> assessmentList = (List<Object>) dataSnapshot.getValue();
                                    Log.d(TAG, "onDataChange: else assessmentListsize:" + assessmentList.size());
                                    for (int i = 0; i < assessmentList.size(); i++) {
                                        Gson gson = new Gson();
                                        final Map<String, Object> assessmentMap = (Map<String, Object>) assessmentList.get(i);
                                        if (assessmentMap != null) {
                                            if (assessmentMap.get("elementId") != null) {
                                                String id = "ASSESSMENT" + i;
                                                myDeskInfoMap.put(id, "ASSESSMENT");
                                                if (myDeskData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myDeskData.get(id);
                                                    commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                    commonLandingData.setType("ASSESSMENT");
                                                    commonLandingData.setId(id);
                                                    if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                        if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                            long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));
                                                            if (completePercentage != 0) {
                                                                commonLandingData.setCompletionPercentage(100);
                                                            }
                                                        }
                                                    }
                                                    myDeskData.put(id, commonLandingData);
                                                } else {
                                                    CommonLandingData commonLandingData = new CommonLandingData();
                                                    commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                    commonLandingData.setType("ASSESSMENT");
                                                    commonLandingData.setId(id);
                                                    if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                        if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                            long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));
                                                            if (completePercentage != 0) {
                                                                commonLandingData.setCompletionPercentage(100);
                                                            }
                                                        }
                                                    }
                                                    myDeskData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    }
                                } else if (o1.getClass().equals(HashMap.class)) {
                                    Map<String, Object> assessmentMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                    for (String courseKey : assessmentMainMap.keySet()) {
                                        Gson gson = new Gson();
                                        if ((assessmentMainMap.get(courseKey) != null)) {
                                            final Map<String, Object> assessmentMap = (Map<String, Object>) assessmentMainMap.get(courseKey);
                                            if (assessmentMap != null) {
                                                if (assessmentMap.get("elementId") != null) {
                                                    String id = "ASSESSMENT" + courseKey;
                                                    int remainder = id.length() % 2;
                                                    myDeskInfoMap.put(id, "ASSESSMENT");
                                                    if (myDeskData.get(id) != null) {
                                                        CommonLandingData commonLandingData = myDeskData.get(id);
                                                        commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                        commonLandingData.setType("ASSESSMENT");
                                                        commonLandingData.setId(id);
                                                        if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                            if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                                long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));
                                                                if (completePercentage != 0) {
                                                                    commonLandingData.setCompletionPercentage(100);
                                                                }
                                                            }
                                                        }
                                                        myDeskData.put(id, commonLandingData);
                                                    } else {
                                                        CommonLandingData commonLandingData = new CommonLandingData();
                                                        commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                        commonLandingData.setType("ASSESSMENT");
                                                        commonLandingData.setId(id);
                                                        if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                            if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                                long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));
                                                                if (completePercentage != 0) {
                                                                    commonLandingData.setCompletionPercentage(100);
                                                                }
                                                            }
                                                        }
                                                        myDeskData.put(id, commonLandingData);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //getCompletedAssessmentData();
                        } else {
                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                Log.d(TAG, "onDataChange: isNewLayout 3");
                                haveAllAssignments = true;
                                createChallengesList3();
                            } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                haveAllAssignments = true;
                                Log.d(TAG, "onDataChange: isNewLayout 2");
                                createMainList();
                            } else {
                                Log.d(TAG, "onDataChange: MainList");
                                createAssessmentMainList();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    isAssessmentAvailable = true;
                    setAssessmentListener();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("ERROR", "" + error.getDetails());
                    Log.e(TAG, "onCancelled: " + error.getMessage());
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            assessmentFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserCoursesCollections() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn";
            Log.d(TAG, "getUserCoursesCollections: " + message);
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        //  Log.d(TAG, "onDataChange: UserCoursesCollections:"+dataSnapshot.getChildrenCount());
                        if (dataSnapshot.getValue() != null) {
                            haveAllCollections = true;
                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() < 2) {
                                if (OustPreferences.getAppInstallVariable("hideCourse")) {
                                    return;
                                }
                                setPagerAdapter(getResources().getString(R.string.courses_text));
                            }
                            haveAllCollections = false;
                            Object o1 = dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    final Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        if (lpMap.get("elementId") != null) {
                                            String id = "COLLECTION" + i;
                                            myDeskInfoMap.put(id, "COLLECTION");
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                commonLandingData.setType("COLLECTION");
                                                commonLandingData.setId(id);
                                                myDeskData.put(id, commonLandingData);
                                            } else {
                                                CommonLandingData commonLandingData = new CommonLandingData();
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                commonLandingData.setType("COLLECTION");
                                                commonLandingData.setId(id);
                                                myDeskData.put(id, commonLandingData);
                                            }
                                        }
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String courseKey : lpMainMap.keySet()) {
                                    if ((lpMainMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(courseKey);
                                        if (lpMap != null) {
                                            if (lpMap.get("elementId") != null) {
                                                String id = "COLLECTION" + courseKey;
                                                myDeskInfoMap.put(id, "COLLECTION");
                                                if (myDeskData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myDeskData.get(id);
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    commonLandingData.setType("COLLECTION");
                                                    commonLandingData.setId(id);
                                                    myDeskData.put(id, commonLandingData);
                                                } else {
                                                    CommonLandingData commonLandingData = new CommonLandingData();
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    commonLandingData.setType("COLLECTION");
                                                    commonLandingData.setId(id);
                                                    myDeskData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                haveAllCollections = true;
                                createMainList();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    isCollectionAvailable = true;
                    setCollectiveListener();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            courseFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCollectiveListener() {
        Log.e(TAG, "inside set Singleton Listener()");
        try {
            if (((isCollectionAvailable) && (isCoursesAvailable) &&
                    (isAssessmentAvailable) && OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) ||
                    ((isCollectionAvailable) && (isCoursesAvailable) && ((OustStaticVariableHandling.getInstance().getIsNewLayout() < 2) || (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3)))) {
                OustPreferences.saveintVar("totalCourseAvailable", myDeskData.size());
                ArrayList<CommonLandingData> moduleList = new ArrayList<CommonLandingData>(myDeskData.values());
                Collections.sort(moduleList, sortByDate);
                final int[] index = new int[]{0};
                noofcoursestoload = 0;
                noofOfCollectionsToLoad = 0;
                noofOfAssessmentsToLoad = 0;
                int totalCount = moduleList.size();
                totalCount = myDeskData.size();
                //TODO: check totalModuleCount and localModuleCount
                if ((localModuleCount + 10) < totalCount) {
                    totalModuleCount = 10 + localModuleCount;
                } else if ((localModuleCount) <= totalCount) {
                    totalModuleCount = totalCount;
                    OustDataHandler.getInstance().setPaginationReachToEnd(true);
                    OustDataHandler.getInstance().setAllCoursesLoaded(true);
                }
                for (int i = 0; i < totalCount; i++) {
                    String key = moduleList.get(i).getId();
                    if ((key.contains("COURSE")) && (!myDeskData.get(key).isListenerSet())) {
                        myDeskData.get(key).setListenerSet(true);
                        key = key.replace("COURSE", "");
                        String msg1 = ("course/course" + key);
                        noofcoursestoload++;
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot student) {
                                try {
                                    if (null != student.getValue()) {
                                        CommonTools commonTools = new CommonTools();

                                        index[0]++;
                                        final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                                        if ((lpMap != null) && (lpMap.get("courseId") != null)) {
                                            String id = ("COURSE" + ((long) lpMap.get("courseId")));
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getCourseCommonData(lpMap, commonLandingData);
                                                if (commonLandingData != null) {
                                                    if (commonLandingData.getMode() != null && commonLandingData.getMode().equalsIgnoreCase("ARCHIVED")) {
                                                        myDeskData.remove(id);
                                                        removeReminderNotification(commonLandingData);
                                                    } else if (commonLandingData.getCplId() > 0) {
                                                        myDeskData.remove(id);
                                                        removeReminderNotification(commonLandingData);
                                                    } else {
                                                        if ((commonLandingData.getCompletionPercentage() < 100) &&
                                                                commonLandingData.getCompletionDeadline() != null &&
                                                                !commonLandingData.getCompletionDeadline().isEmpty() &&
                                                                !commonLandingData.getCompletionDeadline().equals("0")) {
                                                            long completioNDeadline = Long.parseLong(commonLandingData.getCompletionDeadline());
                                                            long currentTime = System.currentTimeMillis();
                                                            //Log.d(TAG, "onDataChange: completioNDeadline:"+completioNDeadline+" --- currentTime:"+currentTime);
                                                            if (completioNDeadline >= currentTime) {
                                                                reminderNotification(commonLandingData);
                                                                myDeskData.put(id, commonLandingData);
                                                            } else {
                                                                if (commonLandingData.isShowPastDeadlineModulesOnLandingPage()) {
                                                                    reminderNotification(commonLandingData);
                                                                    myDeskData.put(id, commonLandingData);
                                                                } else {
                                                                    Log.d(TAG, "onDataChange: courseId:" + id + " --- course removed for deadline");
                                                                    myDeskData.remove(id);
                                                                    removeReminderNotification(commonLandingData);
                                                                }
                                                            }
                                                        } else {
                                                            reminderNotification(commonLandingData);
                                                            myDeskData.put(id, commonLandingData);
                                                        }
                                                    }

                                                }

                                            }
                                        }
                                    } else {
                                        noofcoursestoload--;
                                        totalModuleCount--;
                                        String courseID = student.getKey();
                                        courseID = "COURSE" + (courseID.replace("course", ""));
                                        myDeskData.remove(courseID);
                                        myDeskInfoMap.remove(courseID);
                                    }
                                    OustPreferences.saveintVar("totalCourseAvailable", myDeskData.size());
                                    if ((index[0] >= noofcoursestoload)) {
                                        haveAllCourses = true;
                                        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                            createLayout3CourseList(defaultCategoryId);
                                        } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                            createMainList();
                                        } else {
                                            createCourseMainList();
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "caught exception inside set singelton ", e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        };
                        Log.e(TAG, "firebase  link -->" + msg1);
                        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                    } else if ((key.contains("MEETINGCALENDAR")) && (!myDeskData.get(key).isListenerSet())) {
                        myDeskData.get(key).setListenerSet(true);
                        key = key.replace("MEETINGCALENDAR", "");
                        String msg1 = ("/meeting/meeting" + key);
                        noofcoursestoload++;
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot student) {
                                try {
                                    if (null != student.getValue()) {
                                        CommonTools commonTools = new CommonTools();

                                        index[0]++;
                                        final Map<String, Object> meetingBaseData = (Map<String, Object>) student.getValue();
                                        if (meetingBaseData != null) {
                                            Gson gson = new Gson();
                                            JsonElement meetingBaseElement = gson.toJsonTree(meetingBaseData);
                                            MeetingCalendar meetingCalendarBase = gson.fromJson(meetingBaseElement, MeetingCalendar.class);

                                            String id = ("MEETINGCALENDAR" + meetingCalendarBase.getMeetingId());

                                            CommonLandingData commonLandingData = myDeskData.get(id);
                                            if (commonLandingData != null) {
                                                commonLandingData.setName(meetingCalendarBase.getClassTitle());
                                                commonLandingData.setType(meetingCalendarBase.getEventType());
                                                commonLandingData.setIcon(meetingCalendarBase.getThumbnailImage());
                                                commonLandingData.setBanner(meetingCalendarBase.getBannerImg());
                                                commonLandingData.setStartTime(meetingCalendarBase.getMeetingStartTime());
                                                commonLandingData.setEndTime(meetingCalendarBase.getMeetingEndTime());
                                                commonLandingData.setTimeZone(meetingCalendarBase.getTimeZone());
                                                commonLandingData.setEnrollCount(meetingCalendarBase.getTotalEnrolledCount());
                                                commonLandingData.setId(id);
                                                commonLandingData.setMeetingCalendar(meetingCalendarBase);
                                                if (meetingCalendarBase.getMeetingEndTime() != 0 &&
                                                        System.currentTimeMillis() < meetingCalendarBase.getMeetingEndTime()) {
                                                    myDeskData.put(id, commonLandingData);
                                                } else {
                                                    myDeskData.remove(id);
                                                }

                                            }

                                        }
                                    } else {
                                        noofcoursestoload--;
                                        totalModuleCount--;
                                        String courseID = student.getKey();
                                        courseID = "MEETINGCALENDAR" + (courseID.replace("meeting", ""));
                                        myDeskData.remove(courseID);
                                        myDeskInfoMap.remove(courseID);
                                    }
                                    OustPreferences.saveintVar("totalCourseAvailable", myDeskData.size());
                                    if ((index[0] >= noofcoursestoload)) {
                                        haveAllCourses = true;
                                        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                            createLayout3CourseList(defaultCategoryId);
                                        } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                            createMainList();
                                        } else {
                                            createCourseMainList();
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "caught exception inside set singelton ", e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        };
                        Log.e(TAG, "firebase  link -->" + msg1);
                        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                    } else if ((key.contains("COLLECTION")) && (!myDeskData.get(key).isListenerSet())) {
                        myDeskData.get(key).setListenerSet(true);
                        key = key.replace("COLLECTION", "");
                        String msg1 = ("courseCollection/courseColn" + key);
                        noofOfCollectionsToLoad++;
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot student) {
                                try {
                                    if (null != student.getValue()) {
                                        CommonTools commonTools = new CommonTools();

                                        index[0]++;
                                        final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                                        if ((lpMap != null) && (lpMap.get("courseColnId") != null)) {
                                            String id = ("COLLECTION" + ((long) lpMap.get("courseColnId")));
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getAssessmentCommonData(lpMap, commonLandingData);
                                                myDeskData.put(id, commonLandingData);
                                            }
                                        }
                                    } else {
                                        noofOfCollectionsToLoad--;
                                        totalModuleCount--;
                                        String collectionId = student.getKey();
                                        collectionId = "COLLECTION" + collectionId.replace("collection", "");
                                        myDeskData.remove(collectionId);
                                        myDeskInfoMap.remove(collectionId);
                                    }
                                    OustPreferences.saveintVar("totalCourseAvailable", myDeskData.size());
                                    if ((index[0] >= noofOfCollectionsToLoad)) {
                                        haveAllCollections = true;
                                        if (OustStaticVariableHandling.getInstance().getIsNewLayout() < 2) {
                                            createCourseMainList();
                                        } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                            createMainList();
                                        }
                                        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                            createLayout3CourseList(defaultCategoryId);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "caught exception inside set singelton ", e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // createCourseList(allCources);
                            }
                        };
                        Log.e(TAG, "firebase  link -->" + msg1);
                        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                    } else if ((key.contains("ASSESSMENT")) && (!myDeskData.get(key).isListenerSet())) {
                        myDeskData.get(key).setListenerSet(true);
                        key = key.replace("ASSESSMENT", "");
                        String msg1 = ("assessment/assessment" + key);
                        noofOfAssessmentsToLoad++;
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot student) {
                                try {
                                    if (null != student.getValue()) {
                                        CommonTools commonTools = new CommonTools();
                                        //Log.e(TAG, "got data from firebase of assessment level ");
                                        Gson gson = new Gson();
                                        index[0]++;
                                        final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                                        if ((lpMap != null) && (lpMap.get("assessmentId") != null)) {
                                            String id = ("ASSESSMENT" + ((long) lpMap.get("assessmentId")));
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getAssessmentCommonData(lpMap, commonLandingData);

                                                if (commonLandingData.getCplId() > 0) {
                                                    myDeskData.remove(id);
                                                    removeReminderNotification(commonLandingData);
                                                } else if ((commonLandingData.getCompletionPercentage() < 100) &&
                                                        commonLandingData.getCompletionDeadline() != null &&
                                                        !commonLandingData.getCompletionDeadline().isEmpty() &&
                                                        !commonLandingData.getCompletionDeadline().equals("0")) {
                                                    long completioNDeadline = Long.parseLong(commonLandingData.getCompletionDeadline());
                                                    long currentTime = System.currentTimeMillis();
                                                    //Log.d(TAG, "onDataChange: completioNDeadline:"+completioNDeadline+" --- currentTime:"+currentTime);
                                                    if (completioNDeadline >= currentTime) {
                                                        reminderNotification(commonLandingData);
                                                        myDeskData.put(id, commonLandingData);
                                                    } else {
                                                        if (commonLandingData.isShowPastDeadlineModulesOnLandingPage()) {
                                                            reminderNotification(commonLandingData);
                                                            myDeskData.put(id, commonLandingData);
                                                        } else {
                                                            //Log.d(TAG, "onDataChange: assessmentId:" + id + " --- assessment removed for deadline");
                                                            myDeskData.remove(id);
                                                            removeReminderNotification(commonLandingData);
                                                        }
                                                    }
                                                } else {
                                                    reminderNotification(commonLandingData);
                                                    myDeskData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    } else {
                                        noofOfAssessmentsToLoad--;
                                        totalModuleCount--;
                                        String assessmentId = student.getKey();
                                        assessmentId = "ASSESSMENT" + assessmentId.replace("assessment", "");
                                        myDeskData.remove(assessmentId);
                                        myDeskInfoMap.remove(assessmentId);
                                    }
                                    if ((index[0] >= noofOfAssessmentsToLoad)) {
                                        haveAllAssignments = true;
                                        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                            createLayout3CourseList(defaultCategoryId);
                                        } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                            createMainList();
                                        } else {
                                            createCourseMainList();
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "caught exception inside set singelton ", e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        };
                        //Log.e(TAG, "firebase 1 link -->" + msg1);
                        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                    }
                }
            }
            //getCompletedCoursesData();
            //getCompletedAssessmentData();
        } catch (Exception e) {
            Log.e(TAG, "caught exception in setCourseSingletonListener " + e);
        }
    }

    private void setAssessmentListener() {
        Log.e(TAG, "inside set assessment Singleton Listener()");
        try {
            final int[] index = new int[]{0};
            noofOfAssessmentsToLoad = 0;
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2 || COURSE_ASSESSMENT_TAB_FOUND) {
                setCollectiveListener();
            } else {
                OustPreferences.saveintVar("totalAssessmentAvailable", myAssessmentData.size());
                OustAppState.getInstance().setMyAssessmentList(new ArrayList<CommonLandingData>(myAssessmentData.values()));
                int totalCount = myAssessmentData.size();
                int gettingAssessmentBaseDataCalled = 0;
                if ((localAssessmentCount + 10) < totalCount) {
                    totalAssessmentCount = 10 + localAssessmentCount;
                } else if ((totalAssessmentCount) <= totalCount) {
                    OustDataHandler.getInstance().setAllAssessmentLoaded(true);
                    totalAssessmentCount = totalCount;
                }

                Collections.sort(OustAppState.getInstance().getMyAssessmentList(), sortByDate);
                for (int i = 0; i < totalAssessmentCount; i++) {
                    gettingAssessmentBaseDataCalled = 1;
                    String key = OustAppState.getInstance().getMyAssessmentList().get(i).getId();
                    if ((key.contains("ASSESSMENT")) && (!myAssessmentData.get(key).isListenerSet())) {
                        gettingAssessmentBaseDataCalled = 2;
                        if (!myAssessmentData.get(key).isListenerSet()) {
                            myAssessmentData.get(key).setListenerSet(true);
                            key = key.replace("ASSESSMENT", "");
                            String msg1 = ("assessment/assessment" + key);
                            noofOfAssessmentsToLoad++;
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot student) {
                                    try {
                                        if (null != student.getValue()) {
                                            CommonTools commonTools = new CommonTools();
                                            //Log.e(TAG, "got data from firebase of assessment level ");
                                            index[0]++;
                                            final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                                            if ((lpMap != null) && (lpMap.get("assessmentId") != null)) {
                                                String id = ("ASSESSMENT" + ((long) lpMap.get("assessmentId")));
                                                if (myAssessmentData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myAssessmentData.get(id);
                                                    commonTools.getAssessmentCommonData(lpMap, commonLandingData);

                                                    if (commonLandingData.getCplId() > 0) {
                                                        myAssessmentData.remove(id);
                                                        removeReminderNotification(commonLandingData);
                                                    } else if ((commonLandingData.getCompletionPercentage() < 100)
                                                            && commonLandingData.getCompletionDeadline() != null &&
                                                            !commonLandingData.getCompletionDeadline().isEmpty() &&
                                                            !commonLandingData.getCompletionDeadline().equals("0")) {
                                                        long completioNDeadline = Long.parseLong(commonLandingData.getCompletionDeadline());
                                                        long currentTime = System.currentTimeMillis();
                                                        //Log.d(TAG, "onDataChange: completioNDeadline:"+completioNDeadline+" --- currentTime:"+currentTime);
                                                        if (completioNDeadline >= currentTime) {
                                                            reminderNotification(commonLandingData);
                                                            myAssessmentData.put(id, commonLandingData);
                                                        } else {
                                                            if (commonLandingData.isShowPastDeadlineModulesOnLandingPage()) {
                                                                reminderNotification(commonLandingData);
                                                                myAssessmentData.put(id, commonLandingData);
                                                            } else {
                                                                //Log.d(TAG, "onDataChange: assessmentId:" + id + " --- assessment removed for deadline");
                                                                myAssessmentData.remove(id);
                                                                removeReminderNotification(commonLandingData);
                                                            }
                                                        }
                                                    } else {
                                                        reminderNotification(commonLandingData);
                                                        myAssessmentData.put(id, commonLandingData);
                                                    }

                                                    //reminderNotification(commonLandingData);
                                                    //myAssessmentData.put(id, commonLandingData);
                                                }
                                            }
                                        } else {
                                            noofOfAssessmentsToLoad--;
                                            totalAssessmentCount--;
                                            String assessmentId = student.getKey();
                                            assessmentId = "ASSESSMENT" + assessmentId.replace("assessment", "");
                                            myAssessmentData.remove(assessmentId);
                                            myDeskInfoMap.remove(assessmentId);
                                        }
                                        OustPreferences.saveintVar("totalAssessmentAvailable", myAssessmentData.size());
                                        if ((index[0] >= noofOfAssessmentsToLoad)) {
                                            setAssessmentListData();
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "caught exception inside set singelton ", e);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                }
                            };
                            Log.e(TAG, "firebase  link -->" + msg1);
                            OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                            OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                        }
                    }
                }
                if (gettingAssessmentBaseDataCalled == 1 && totalAssessmentCount > 0) {
                    setAssessmentListData();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "caught exception in setCourseSingletonListener " + e);
        }
    }

    private void setAssessmentListData() {
        OustAppState.getInstance().setMyAssessmentList(new ArrayList<CommonLandingData>(myAssessmentData.values()));
        Collections.sort(OustAppState.getInstance().getMyAssessmentList(), sortByDate);
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
            createChallengesList3();
        } else
            createAssessmentMainList();
    }

    @Override
    protected void onPause() {
        try {
            sendFeedClickedRequestToBackend();
            removeUndistributeData();
            Intent feedService = new Intent(NewLandingActivity.this, ReminderNotificationUpdatingService.class);
            startService(feedService);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        super.onPause();
    }

    private int totalCoursesCount3 = 0, totalChallengesCount3 = 0;

    private void createChallengesList3() {
        try {
            if (cplHashMapData != null) {
                extractCourseAssessmentFromCPL(cplHashMapData);
            }
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                if (haveAllAssignments) {
                    Collections.sort(OustAppState.getInstance().getMyAssessmentList(), sortByDate);
                    getTotalAssessments();
                    if ((searchOn) && (searchText != null) && (!searchText.isEmpty())) {
                        filterNewLandingChallenge(searchText);
                    } else {
                        List<CommonLandingData> myChallengeFilterdData = new ArrayList<>();
                        CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                        myChallengeFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyAssessmentList()).get();
                        totalChallengesCount3 = myChallengeFilterdData.size();
                        if (myChallengeFilterdData.size() == 0) {
                            if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                                createNewChallengeList3(myChallengeFilterdData);
                                getNextData("");
                            } else {
                                createNewChallengeList3(myChallengeFilterdData);
                            }
                        } else {
                            createNewChallengeList3(myChallengeFilterdData);
                        }
                        setMyTotalProgressText();
                        setMyTotalOcText();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createNewChallengeList3(List<CommonLandingData> myChallengeFilterdData) {

        try {
            if ((myChallengeFilterdData != null)) {
                showSearchIcon();
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                    createChallengeList3(filterListByLearnType(myChallengeFilterdData));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "inside createCourseList() exception caught", e);
        }

    }

    private NewLandingModuleAdaptera newLandingChallengeAdaptera;

    private void createChallengeList3(ArrayList<CommonLandingData> commonLandingData) {
        try {
            if (!currentTabName.equals("FEEDS")) {
                no_data_text.setVisibility(View.GONE);
            }
            if (newChallenges_recyclerview.getVisibility() == View.VISIBLE) {
                hideLoader();
            }
            if (commonLandingData != null && commonLandingData.size() > 0) {
                no_data_text.setVisibility(View.GONE);
                if (currentTabName.equals("ASSESSMENT") && newChallenges_recyclerview.getVisibility() == View.GONE) {
                    newChallenges_recyclerview.setVisibility(View.VISIBLE);
                }
                if (newLandingChallengeAdaptera == null) {
                    newLandingChallengeAdaptera = new NewLandingModuleAdaptera(commonLandingData);
                    newLandingChallengeAdaptera.setContext(NewLandingActivity.this);
                    boolean hideCatalogue = isHideCatalogueforTab("ASSESSMENT");
                    if ((OustPreferences.getTimeForNotification("catalogueId") > 0) && (!hideCatalogue)) {
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        newChallenges_recyclerview.setLayoutManager(mLayoutManager);
                        newLandingChallengeAdaptera.setAdapterHorizontal(true);
                    } else {
                        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
                        newChallenges_recyclerview.setLayoutManager(mLayoutManager);
                    }
                    newChallenges_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    newLandingChallengeAdaptera.setmyDeskDataMap(myDeskInfoMap);
                    newLandingChallengeAdaptera.setDataLoaderNotifier(NewLandingActivity.this);
                    newChallenges_recyclerview.setAdapter(newLandingChallengeAdaptera);
                    newChallenges_recyclerview.smoothScrollToPosition(0);

                } else {
                    if (isSearchOn) {
                        newLandingChallengeAdaptera.notifyListChnage(commonLandingData, totalModuleCount);
                    } else {
                        newLandingChallengeAdaptera.notifyListChnage(commonLandingData, totalModuleCount);
                    }
                }
            } else {
                if (!searchOn) {
                    if (!currentTabName.equals("FEEDS")) {
                        no_data_text.setText(getResources().getString(R.string.no_module_yet));
                    }
                    newChallenges_recyclerview.setVisibility(View.GONE);
                    if (currentTabName.equals("ASSESSMENT")) {
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                    if (commonLandingData != null) {
                        if (newLandingChallengeAdaptera != null) {
                            newLandingChallengeAdaptera.notifyListChnage(commonLandingData, totalModuleCount);
                        }
                    }
                } else
                    no_data_text.setText(getResources().getString(R.string.noassessment_found));
                if (newChallenges_recyclerview.getVisibility() == View.VISIBLE) {
                    no_data_text.setVisibility(View.VISIBLE);
                    newChallenges_recyclerview.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private View getTabIndicator(final String title, int no) {
        View view = LayoutInflater.from(this).inflate(R.layout.newlanding_tab, null);
        TextView tv = view.findViewById(R.id.tabText);
        if (no == 0) {
            tv.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
        }
        tv.setText(title);
        return view;
    }

    private void showErrorMessage() {
        if ((!isAssessmentAvailable) && (!isCoursesAvailable) && (!isCollectionAvailable)) {
            hideLoader();
            nocourse_text.setVisibility(View.VISIBLE);
            nocourse_text.setText(getResources().getString(R.string.no_course_assign));
        }
    }

    private NewLandingModuleAdapterc newLandingModuleHorizontalAdaptera;
    private NewLandingModuleAdapterc newLandingModuleHorizontalAdapterb;
    private NewLandingModuleAdapterc newLandingModuleHorizontalAdapterc;
    private int initialPendingModuleCount = 0;


    private void createMainList() {
        try {
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                if (haveAllCourses && haveAllAssignments) {
                    getTotalCourses();
                    Collections.sort(OustAppState.getInstance().getMyDeskList(), sortByDate);
                    if ((searchOn) && (searchText != null) && (!searchText.isEmpty())) {
                        filterList(searchText);
                    } else {
                        List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
                        CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                        myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
                        initialPendingModuleCount = myDeskFilterdData.size();
                        if (myDeskFilterdData.size() == 0) {
                            if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                                getNextData("");
                            } else {
                                createNewCourseList(myDeskFilterdData);
                            }
                        } else {
                            createNewCourseList(myDeskFilterdData);
                        }
                    }
                    isRefreshing = false;
                    setRefreshLayoutVisibility();
                    setMyTotalProgressText();
                    setMyTotalOcText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createCourseMainList() {
        try {
            if ((oustTabCount == 0) && (myDeskData.size() == 0)) {
                nodatalabel.setVisibility(View.VISIBLE);
                hideLoader();
            } else {
                hideLoader();
                nodatalabel.setVisibility(View.GONE);
                getTotalCourses();
                OustPreferences.saveintVar("courseAdapterSize", totalModuleCount);
                setMyTotalProgressText();
                setMyTotalOcText();
                setCourseFragmentData(4);
                setRefreshLayoutVisibility();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getTotalCourses() {
        ArrayList<CommonLandingData> courseList = new ArrayList<CommonLandingData>(myDeskData.values());
        Collections.sort(courseList, sortByDate);
        if (totalModuleCount < courseList.size()) {
            courseList = (new ArrayList<CommonLandingData>(courseList));
        }
        OustAppState.getInstance().setMyDeskList(courseList);
        showSearchIcon();
    }

    private void createAssessmentMainList() {
        if ((oustTabCount == 0) && (myDeskData.size() == 0)) {
            nodatalabel.setVisibility(View.VISIBLE);
            hideLoader();
        } else {
            hideLoader();
            nodatalabel.setVisibility(View.GONE);
            getTotalAssessments();
            //OustAppState.getInstance().setMyAssessmentList(new ArrayList<CommonLandingData>(myAssessmentData.values()));
            setMyTotalProgressText();
            setMyTotalOcText();
            setAssessmentFragmentData(4);
        }
    }

    private void getTotalAssessments() {
        ArrayList<CommonLandingData> assessmentList = OustAppState.getInstance().getMyAssessmentList();
        if (assessmentList.size() > totalAssessmentCount) {
            assessmentList = (new ArrayList<CommonLandingData>(assessmentList.subList(0, totalAssessmentCount)));
        }
        OustAppState.getInstance().setMyAssessmentList(assessmentList);//A19BIFp9
    }

    private int oustTabCount = 0;

    private void setPagerAdapter(String tabStr) {
        try {
            if ((tabLayout != null)) {
                if (oustTabCount < 2) {
                    hideLoader();
                    String toolbarColorCode = OustPreferences.get("toolbarColorCode");
                    if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));
                    }
                    nocourse_text.setVisibility(View.GONE);
                    boolean containTab = false;
                    if (tabLayout.getTabCount() > 0) {
                        for (int j = 0; j < tabLayout.getTabCount(); j++) {
                            TextView t = tabLayout.getTabAt(j).getCustomView().findViewById(R.id.tabText);
                            if (t.getText().toString().equalsIgnoreCase(tabStr)) {
                                containTab = true;
                                break;
                            }
                        }
                    }
                    if (!containTab) {
                        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabIndicator(tabStr, oustTabCount)));
                        if (oustTabCount == 0) {
                            newTabFragmentPagerAdapter = new NewTabFragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), NewLandingActivity.this, tabStr);
                            newTabFragmentPagerAdapter.setDataLoaderNotifier(this);
                            OustStaticVariableHandling.getInstance().getNewViewPager().setAdapter(newTabFragmentPagerAdapter);
                            OustStaticVariableHandling.getInstance().getNewViewPager().addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    OustStaticVariableHandling.getInstance().getNewViewPager().setCurrentItem(tab.getPosition(), true);
                                    ((TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {
                                    if (tabLayout.getTabCount() > tab.getPosition()) {
                                        ((TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                                    }
                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {
                                }

                            });
                        } else {
                            newTabFragmentPagerAdapter.notifyChanges((oustTabCount + 1), tabStr);
                        }
                        oustTabCount++;
                    }
                    if (oustTabCount > 1) {
                        tabLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFragmentData(int filterType) {
        try {
            if ((newTabFragmentPagerAdapter.getCourseFragment() == null) && (newTabFragmentPagerAdapter.getAssessmentFragment() == null)) {
                newTabFragmentPagerAdapter.notifyChanges(1, "");
            }
            if (newTabFragmentPagerAdapter.getCourseFragment() != null) {
                newTabFragmentPagerAdapter.getCourseFragment().filterData(filterType);
            }
            if (newTabFragmentPagerAdapter.getAssessmentFragment() != null) {
                newTabFragmentPagerAdapter.getAssessmentFragment().filterData(filterType);
            }
        } catch (Exception e) {
        }
    }

    private void setAssessmentFragmentData(int filterType) {
        try {
            if ((newTabFragmentPagerAdapter.getCourseFragment() == null) && (newTabFragmentPagerAdapter.getAssessmentFragment() == null)) {
                newTabFragmentPagerAdapter.notifyChanges(1, "");
            }
            if (newTabFragmentPagerAdapter.getAssessmentFragment() != null) {
                newTabFragmentPagerAdapter.getAssessmentFragment().filterData(filterType);
            }
        } catch (Exception e) {
        }
    }

    private void setCourseFragmentData(int filterType) {
        try {
            if ((newTabFragmentPagerAdapter.getCourseFragment() == null) && (newTabFragmentPagerAdapter.getAssessmentFragment() == null)) {
                newTabFragmentPagerAdapter.notifyChanges(1, "");
            }
            if (newTabFragmentPagerAdapter.getCourseFragment() != null) {
                newTabFragmentPagerAdapter.getCourseFragment().filterData(filterType);
            }
        } catch (Exception e) {
        }
    }


    public Comparator<CommonLandingData> sortByDate = new Comparator<CommonLandingData>() {
        public int compare(CommonLandingData s1, CommonLandingData s2) {
            if (s2.getAddedOn() == null) {
                return -1;
            }
            if (s1.getAddedOn() == null) {
                return 1;
            }
            if (s1.getAddedOn().equals(s2.getAddedOn())) {
                return 0;
            }
            return s2.getAddedOn().compareTo(s1.getAddedOn());
        }
    };

    private void showSearchIcon() {
        if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 4) && (actionSearch != null)) {
            actionSearch.setVisible(true);
            showAlertBanner(false);
        }
    }

    //deligate methode called on search box text change

    @Override
    public boolean onQueryTextSubmit(String newText) {
        if (!newText.isEmpty()) {
        }
        return false;
    }

    private String searchText = "";


    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            filterType = 0;
            if (newFeedFilterAdapter != null) {
                newFeedFilterAdapter.updateAdapter(0, "");
            }
            if (newFeedVerticalFilterAdapter != null)
                newFeedVerticalFilterAdapter.updateAdapter(0, "");
            searchText = newText;
            if (newText.isEmpty()) {
                searchOn = false;
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                    enableLayout3Buttons();
                    setCurrentTabVisibility();
                    OustStaticVariableHandling.getInstance().setFilterAllowed(true);
                    if (newCourse_recyclerview.getVisibility() == View.VISIBLE) {
                        List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
                        CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                        myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
                        createCourseList3(new ArrayList<CommonLandingData>(myDeskFilterdData));
                    } else if (newChallenges_recyclerview.getVisibility() == View.VISIBLE) {
                        List<CommonLandingData> myAssessmentFilterdData = new ArrayList<>();
                        CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                        myAssessmentFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyAssessmentList()).get();
                        createChallengeList3(new ArrayList<CommonLandingData>(myAssessmentFilterdData));
                    } else if (newsfeed_recyclerview.getVisibility() == View.VISIBLE) {
                        getAllAlertsForLayout3();
                        //createList(newFeedList, false, true);
                    } else if (playList_rv.getVisibility() == View.VISIBLE) {
                        createPlayListList3();
                        // createPlayListList3();
                    } else if (noticeBoard_rv.getVisibility() == View.VISIBLE) {
                        if (nbTopicDataArrayList != null && nbTopicDataArrayList.size() > 0) {
                            createNoticeBoardAdapter(nbTopicDataArrayList);
                        }
                    }
                } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                    List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
                    CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                    myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
                    createNewCourseList(myDeskFilterdData);
                } else {
                    try {
                        if (newTabFragmentPagerAdapter != null) {
                            newTabFragmentPagerAdapter.getCourseFragment().onSearchText("");
                            //newTabFragmentPagerAdapter.getAssessmentFragment().onSearchText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            } else {
                searchOn = true;
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                    disableLayout3Buttons();
                    OustStaticVariableHandling.getInstance().setFilterAllowed(false);

                    setCurrentTabVisibility();

                    if (newCourse_recyclerview.getVisibility() == View.VISIBLE) {
                        filterNewLandingCourse(newText);
                    } else if (newChallenges_recyclerview.getVisibility() == View.VISIBLE) {
                        filterNewLandingChallenge(newText);
                    } else if (newsfeed_recyclerview.getVisibility() == View.VISIBLE) {
                        filterNewLandingFeed(newText);
                    } else if (playList_rv.getVisibility() == View.VISIBLE) {
                        filterPlayListData(newText);
                    } else if (noticeBoard_rv.getVisibility() == View.VISIBLE) {
                        filterNewNoticeBoardData(newText);
                    }
                } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                    filterList(newText);
                } else {
                    try {
                        if (newTabFragmentPagerAdapter != null) {
                            newTabFragmentPagerAdapter.getCourseFragment().onSearchText(newText);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    private void filterNewNoticeBoardData(String newText) {
        try {
            List<NBTopicData> noticeBoardFilterData = new ArrayList<>();
            if (nbTopicDataArrayList != null && nbTopicDataArrayList.size() > 0) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                noticeBoardFilterData = commonLandingFilter.noticeBoardCriteria(nbTopicDataArrayList, newText).get();
            }
            createNoticeBoardAdapter(noticeBoardFilterData);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCurrentTabVisibility() {
        if (currentTabName.equals("COURSE")) {
            newCourse_recyclerview.setVisibility(View.VISIBLE);
        } else if (currentTabName.equals("FEEDS")) {
            newsfeed_recyclerview.setVisibility(View.VISIBLE);
        } else if (currentTabName.equals("PLAYLIST")) {
            playList_rv.setVisibility(View.VISIBLE);
        } else if (currentTabName.equals("ASSESSMENT")) {
            newChallenges_recyclerview.setVisibility(View.VISIBLE);
        } else if (currentTabName.equals("NOTICEBOARD")) {
            noticeBoard_rv.setVisibility(View.VISIBLE);
        }
    }

    private void disableLayout3Buttons() {
        try {
            myChallengesButton.setEnabled(false);
            myCourseButton.setEnabled(false);
            myFeedButton.setEnabled(false);
        } catch (Exception e) {
        }
    }

    private void enableLayout3Buttons() {
        try {
            myChallengesButton.setEnabled(true);
            myCourseButton.setEnabled(true);
            myFeedButton.setEnabled(true);
        } catch (Exception e) {
        }
    }

    private void filterNewLandingFeed(String newText) {
        try {
            ArrayList<DTONewFeed> allFeeds = getTotalFeeds();
            NewsFeedFilter cr = new NewsFeedFilter();
            ArrayList<DTONewFeed> newFilteredFeedList = cr.meetCriteria(allFeeds, newText);
            Log.e(TAG, "updateShareCount: feedArrayList 3-> " + newFilteredFeedList.size());
            createList(newFilteredFeedList, true, false);
            if (newFilteredFeedList != null && newFilteredFeedList.size() == 0) {
                if (!OustDataHandler.getInstance().isAllFeedsLoaded()) {
                    getNextData("Alerts");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void filterNewLandingChallenge(String newText) {
        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyAssessmentList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.meetCriteria(OustAppState.getInstance().getMyAssessmentList(), searchText).get();
            }
            createChallengeList3(new ArrayList<CommonLandingData>(myDeskFilterdData));
            if (myDeskFilterdData.size() == 0) {
                if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                    getNextData("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void filterNewLandingCourse(String newText) {
        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.meetCriteria(OustAppState.getInstance().getMyDeskList(), searchText).get();
            }
            createCourseList3(new ArrayList<CommonLandingData>(myDeskFilterdData));
            if (myDeskFilterdData.size() == 0) {
                if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                    getNextData("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void filterList(String searchText) {
        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.meetCriteria(OustAppState.getInstance().getMyDeskList(), searchText).get();
            }
            createNewCourseList(myDeskFilterdData);
            if (myDeskFilterdData.size() == 0) {
                if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                    getNextData("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public ArrayList<CommonLandingData> filterListByGrowType
            (List<CommonLandingData> commonLandingDataArrayList) {
        try {
            ArrayList<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((commonLandingDataArrayList != null) && (commonLandingDataArrayList.size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getSortedDataByGrowType(commonLandingDataArrayList);
                return myDeskFilterdData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    public ArrayList<CommonLandingData> filterListByLearnType
            (List<CommonLandingData> commonLandingDataArrayList) {
        try {
            ArrayList<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((commonLandingDataArrayList != null) && (commonLandingDataArrayList.size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getSortedDataByLearnType(commonLandingDataArrayList);
            }
            return myDeskFilterdData;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    public void filterPendingList() {
        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
            }
            createNewCourseList(myDeskFilterdData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getPendingList() {
        try {
            ArrayList<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getpendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList());
            }
            if (myDeskFilterdData != null && myDeskFilterdData.size() > 0) {
                OustDataHandler.getInstance().saveData(myDeskFilterdData);
                OustDataHandler.getInstance().setMyDeskData(myDeskData);
                Intent intent = new Intent(NewLandingActivity.this, CatalogListActivity.class);
                intent.putExtra("hasBanner", false);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                intent.putExtra("filter_type", "Pending");
                intent.putExtra("topDisplayName", "" + getResources().getString(R.string.pending_modules_text));
                startActivity(intent);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.no_modules_available));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void getCompletedList() {
        try {
            Log.d(TAG, "getCompletedList: ");
            ArrayList<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getcompletedDataMeetCriteria(OustAppState.getInstance().getMyDeskList());
            }
            if ((myDeskFilterdData != null && myDeskFilterdData.size() > 0) || allCourseSize > 0) {
                OustDataHandler.getInstance().saveData(myDeskFilterdData);
                OustDataHandler.getInstance().setMyDeskData(myDeskData);
                Intent intent = new Intent(NewLandingActivity.this, CatalogListActivity.class);
                intent.putExtra("hasBanner", false);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                intent.putExtra("filter_type", "Complete");
                intent.putExtra("topDisplayName", "" + getResources().getString(R.string.completed_modules_text));
                startActivity(intent);
            } else {
                OustSdkTools.showToast("" + getResources().getString(R.string.no_modules_available));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void filterCompletedList() {
        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.completedDataMeetCriteria(OustAppState.getInstance().getMyDeskList());
            }
            createNewCourseList(myDeskFilterdData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isSearchOn = false;

    @Override
    public void isSearchOn(boolean isSearchOn) {
        this.isSearchOn = isSearchOn;
    }

    @Override
    public void onSearchViewCollapsed() {
        try {
            searchOn = false;
            isSearchOn = false;
            OustStaticVariableHandling.getInstance().setNewLandingSearch(false);
            showBanner();
            CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
            List<CommonLandingData> deskFilterData = commonLandingFilter.getpendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList());
            createNewCourseList(deskFilterData);
            createNewCourseList(deskFilterData);
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                enableLayout3Buttons();
                OustStaticVariableHandling.getInstance().setFilterAllowed(true);
                if (newCourse_recyclerview.getVisibility() == View.VISIBLE) {
                    List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
                    CommonLandingFilter commonLandingFilter1 = new CommonLandingFilter();
                    myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
                    createCourseList3(new ArrayList<CommonLandingData>(myDeskFilterdData));
                } else if (newChallenges_recyclerview.getVisibility() == View.VISIBLE) {
                    List<CommonLandingData> myAssessmentFilterdData = new ArrayList<>();
                    CommonLandingFilter commonLandingFilter1 = new CommonLandingFilter();
                    myAssessmentFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyAssessmentList()).get();
                    createChallengeList3(new ArrayList<CommonLandingData>(myAssessmentFilterdData));
                } else if (newsfeed_recyclerview.getVisibility() == View.VISIBLE) {
                    getAllAlertsForLayout3();
                    //createList(newFeedList, false, true);
                } else if (playList_rv.getVisibility() == View.VISIBLE) {
                    createPlayListList3();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onSearchViewExpanded() {
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
        } else {
            showAllCourseUI();
        }
        isSearchOn = true;
        OustStaticVariableHandling.getInstance().setNewLandingSearch(true);
        hideBanner();
    }

    public void createNewCourseList(List<CommonLandingData> commonLandingDataList) {
        try {
            Log.e(TAG, "inside createCourseList() ");
            if ((commonLandingDataList != null)) {
                showSearchIcon();
                hideLoader();
                nocourse_text.setVisibility(View.GONE);
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
//                    hideLoader();
                    if (new_landing_page_type.equals("Learn")) {
                        createHorizontalLista(filterListByLearnType(commonLandingDataList));
                    } else if (new_landing_page_type.equals("Grow")) {
                        createHorizontalLista(filterListByGrowType(commonLandingDataList));
                    } else {
                        createHorizontalLista(commonLandingDataList);
                    }
                }
            } else {

            }
        } catch (Exception e) {
            Log.e(TAG, "inside createCourseList() exception caught", e);
        }
    }

    //newlandinghorizontal_recyclerview
    private void createHorizontalLista(final List<CommonLandingData> commonLandingDataList) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        try {
            if (commonLandingDataList != null && commonLandingDataList.size() > 0) {
                ll_loading.setVisibility(View.GONE);
                int adapterSize = 0;
                if (newlandinghorizontal_recyclerviewb.getVisibility() != View.VISIBLE && newlandinghorizontal_recyclerviewc.getVisibility() != View.VISIBLE && !isLatestTrendingShown) {
                    newlandinghorizontal_recyclerviewa.setVisibility(View.VISIBLE);
                    no_data_text.setVisibility(View.GONE);
                    landingTag = "mydesk";
                }
                if (newLandingModuleHorizontalAdaptera == null) {
                    newLandingModuleHorizontalAdaptera = new NewLandingModuleAdapterc(commonLandingDataList);
                    WrapContentLinearLayoutManager mLayoutManager1 = new WrapContentLinearLayoutManager(NewLandingActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    newlandinghorizontal_recyclerviewa.setLayoutManager(mLayoutManager1);
                    newlandinghorizontal_recyclerviewa.setItemAnimator(new DefaultItemAnimator());
                    newLandingModuleHorizontalAdaptera.setAdapterSizeLimit(totalModuleCount);
                    newLandingModuleHorizontalAdaptera.setmyDeskDataMap(myDeskInfoMap);
                    newLandingModuleHorizontalAdaptera.setDataLoaderNotifier(NewLandingActivity.this);
                    newlandinghorizontal_recyclerviewa.setAdapter(newLandingModuleHorizontalAdaptera);
                } else {
//                newlandinghorizontal_recyclerviewa.getRecycledViewPool().clear();
                    if (isSearchOn) {
                        newLandingModuleHorizontalAdaptera.notifyListChnage(commonLandingDataList, totalModuleCount);
                    } else {
                        newLandingModuleHorizontalAdaptera.notifyListChnage(commonLandingDataList, totalModuleCount);
                    }
                }
            } else {
                landingTag = "mydesk";
                newlandinghorizontal_recyclerviewa.setVisibility(View.GONE);
                if (OustStaticVariableHandling.getInstance().isInternetConnectionOff()) {
                    no_data_text.setText(getResources().getString(R.string.no_internet_data));
                } else {
                    no_data_text.setText(getResources().getString(R.string.no_data_available));
                }
                no_data_text.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void createHorizontalListb(List<CommonLandingData> commonLandingDataList) {
        try {
            landingTag = "latest";
            isLatestTrendingShown = true;
            if (commonLandingDataList != null && commonLandingDataList.size() > 0) {
                newlandinghorizontal_recyclerviewb.setVisibility(View.VISIBLE);
                no_data_text.setVisibility(View.GONE);
                if (newLandingModuleHorizontalAdapterb == null) {
                    newLandingModuleHorizontalAdapterb = new NewLandingModuleAdapterc(commonLandingDataList);
                    WrapContentLinearLayoutManager mLayoutManager1 = new WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    newlandinghorizontal_recyclerviewb.setLayoutManager(mLayoutManager1);
                    newlandinghorizontal_recyclerviewb.setItemAnimator(new DefaultItemAnimator());
                    newLandingModuleHorizontalAdapterb.setMyDeskData(false);
                    newLandingModuleHorizontalAdapterb.setAdapterSizeLimit(0);
                    newLandingModuleHorizontalAdapterb.setmyDeskDataMap(myDeskInfoMap);
                    newlandinghorizontal_recyclerviewb.setAdapter(newLandingModuleHorizontalAdapterb);
                } else {
                    newLandingModuleHorizontalAdapterb.notifyListChnage(commonLandingDataList, 0);
                }
            } else {
                newlandinghorizontal_recyclerviewb.setVisibility(View.GONE);
                if (OustStaticVariableHandling.getInstance().isInternetConnectionOff()) {
                    no_data_text.setText(getResources().getString(R.string.no_internet_data));
                } else {
                    no_data_text.setText(getResources().getString(R.string.no_data_available));
                }
                no_data_text.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createHorizontalListc(List<CommonLandingData> commonLandingDataList) {
        try {
            landingTag = "trending";
            isLatestTrendingShown = true;
            if (commonLandingDataList != null && commonLandingDataList.size() > 0) {
                newlandinghorizontal_recyclerviewc.setVisibility(View.VISIBLE);
                no_data_text.setVisibility(View.GONE);
                if (newLandingModuleHorizontalAdapterc == null) {
                    newLandingModuleHorizontalAdapterc = new NewLandingModuleAdapterc(commonLandingDataList);
                    WrapContentLinearLayoutManager mLayoutManager1 = new WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    newlandinghorizontal_recyclerviewc.setLayoutManager(mLayoutManager1);
                    newlandinghorizontal_recyclerviewc.setItemAnimator(new DefaultItemAnimator());
                    newLandingModuleHorizontalAdapterc.setMyDeskData(false);
                    newLandingModuleHorizontalAdapterc.setmyDeskDataMap(myDeskInfoMap);
                    newLandingModuleHorizontalAdapterc.setAdapterSizeLimit(0);
                    newlandinghorizontal_recyclerviewc.setAdapter(newLandingModuleHorizontalAdapterc);
                } else {
                    newLandingModuleHorizontalAdapterc.notifyListChnage(commonLandingDataList, 0);
                }
            } else {
                newlandinghorizontal_recyclerviewc.setVisibility(View.GONE);
                if (OustStaticVariableHandling.getInstance().isInternetConnectionOff()) {
                    no_data_text.setText(getResources().getString(R.string.no_internet_data));
                } else {
                    no_data_text.setText(getResources().getString(R.string.no_data_available));
                }
                no_data_text.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//======================================================================================

  /*  public void getNewsFeedsFromFirebase() {
        String message = "/newsFeed/feeds";
        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                feedList = new ArrayList<>();
                try {
                    if (dataSnapshot != null) {
                        final Map<String, Object> newsfeedMap = (Map<String, Object>) dataSnapshot.getValue();
                        CommonTools commonTools = new CommonTools();
                        for (String newsId : newsfeedMap.keySet()) {
                            final HashMap<String, Object> mymap = (HashMap<String, Object>) newsfeedMap.get(newsId);
                            NewFeed feed1 = commonTools.getNewFeedFromMap(mymap, "");
                            // feedList.add(feed1);
                        }
                    }
                    getAllFeeds(true);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
        Query query = newsfeedRef.orderByChild("timeStamp").limitToLast(3);
        query.keepSynced(true);
        query.addValueEventListener(newsfeedListListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
    }*/

    public void getUserNewsFeed() {
        String message = "/userFeed/" + activeUser.getStudentKey();
        Log.d(TAG, "getUserNewsFeed: " + message);
        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    noofListenerSetForFeed = 0;
                    userNewFeedList = new ArrayList<>();
                    if (dataSnapshot != null) {
                        final Map<String, Object> newsfeedMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (newsfeedMap != null && newsfeedMap.size() != 0) {
                            for (String newsId : newsfeedMap.keySet()) {
                                final HashMap<String, Object> mymap = (HashMap<String, Object>) newsfeedMap.get(newsId);
                                if ((mymap != null)) {
                                    long feedID = 0;
                                    long timeStamp = 0;
                                    boolean isClicked = true, isUserFeedViewed = false, isFeedCoinsAdded = false;
                                    if ((mymap.get("feedId") != null)) {
                                        feedID = (long) mymap.get("feedId");
                                    }
                                    if ((mymap.get("timeStamp")) != null) {
                                        timeStamp = (long) mymap.get("timeStamp");
                                    }
                                    if (mymap.get(AppConstants.StringConstants.IS_FEED_CLICKED) != null) {
                                        isClicked = (boolean) mymap.get(AppConstants.StringConstants.IS_FEED_CLICKED);
                                    } else {
                                        isClicked = true;
                                    }

                                    if (mymap.get(AppConstants.StringConstants.IS_FEED_VIEWED) != null) {
                                        isUserFeedViewed = (boolean) mymap.get(AppConstants.StringConstants.IS_FEED_VIEWED);
                                    }

                                    if (mymap.get(AppConstants.StringConstants.FEED_COINS_ADDED) != null) {
                                        isFeedCoinsAdded = (boolean) mymap.get(AppConstants.StringConstants.FEED_COINS_ADDED);
                                    }

                                    getUserFeedDatails(("" + feedID), ("" + timeStamp), 1, isClicked, isUserFeedViewed, isFeedCoinsAdded);

                                }
                            }
                        } else {
                            isFilterAvailable = false;
                            filter_ll.setVisibility(View.GONE);
                        }
                    } else {
                        isFilterAvailable = false;
                        filter_ll.setVisibility(View.GONE);
                    }
                    getAllFeeds(true);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
        Query query = newsfeedRef.orderByChild("timeStamp").limitToLast(100);
        query.keepSynced(true);
        query.addValueEventListener(newsfeedListListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
        alertFirebaseRefClass = new FirebaseRefClass(newsfeedListListener, message);
    }

    private void getUserFeedDatails(final String id, final String timeStamp,
                                    final int layout_type, final boolean isClicked, final boolean isUserFeedViewed, final boolean isFeedCoinsAdded) {
        String message = "/feeds/feed" + id;
        if (layout_type == 3) {
            noofListenerSetForFeed3++;
        } else
            noofListenerSetForFeed++;

        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        String key = dataSnapshot.getKey();
                        key = key.replace("feed", "");
                        final HashMap<String, Object> newsfeedMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (newsfeedMap != null) {
                            CommonTools commonTools = new CommonTools();
                            DTONewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, new DTONewFeed());
                            if ((timeStamp != null) && (!timeStamp.isEmpty())) {
                                feed1.setTimestamp(Long.parseLong(timeStamp));
                            }

                            feed1.setFeedViewed(isUserFeedViewed);
                            feed1.setClicked(isClicked);
                            /*NewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, timeStamp);
                            feed1.setClicked(isClicked);
                            feed1.setFeedViewed(isUserFeedViewed);*/
                            feed1.setFeedId(Long.parseLong(key));
                            feed1.setFeedCoinsAdded(isFeedCoinsAdded);
                            long curTime = System.currentTimeMillis();
                            if (layout_type == 3) {
                               /* if(feed1.getExpiryTime()!=0){
                                    if (feed1.getExpiryTime() >=curTime)
                                    {
                                        userNewFeedList3.add(feed1);
                                    }
                                }
                                else
                                {*/
                                userNewFeedList3.add(feed1);
                                //  }
                            } else {
                             /*   if(feed1.getExpiryTime()!=0)
                                {
                                    if (feed1.getExpiryTime() >=curTime)
                                    {
                                        userNewFeedList3.add(feed1);
                                    }
                                }
                                else {*/
                                userNewFeedList.add(feed1);
                                // }
                            }
                        } else {
                            decreaseFeedListenerCount(layout_type);
                        }
                    } else {
                        decreaseFeedListenerCount(layout_type);
                    }
                } catch (Exception e) {
                    decreaseFeedListenerCount(layout_type);
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if (layout_type == 3) {
                    if (noofListenerSetForFeed3 == userNewFeedList3.size()) {
                        getAllAlertsForLayout3();
                    }
                } else {
                    if (noofListenerSetForFeed == userNewFeedList.size()) {
                        getAllFeeds(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(newsfeedListListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void decreaseFeedListenerCount(int layout_type) {
        if (layout_type == 3) {
            noofListenerSetForFeed3--;
        } else
            noofListenerSetForFeed--;
    }

    @Override
    public void showAlertIcon() {
        getAllFeeds(false);
    }

    private void getAllFeeds(boolean isComingFromFirebase) {
        try {
            List<DTONewFeed> allFeedList = new ArrayList<>();
            if ((userNewFeedList != null)) {
                allFeedList.addAll(userNewFeedList);
                if (allFeedList.size() > 0) {
                    if (action_alert != null) {
                        if (OustStaticVariableHandling.getInstance().getIsNewLayout() != 3) {
                            action_alert.setVisible(true);
                        }
                        Collections.sort(allFeedList, newsFeedSorter);
                        long lastalerttime = OustPreferences.getTimeForNotification("lastalerttime");
                        if ((allFeedList.get(0).getTimestamp() != 0)) {
                            if (lastalerttime != (allFeedList.get(0).getTimestamp())) {
                                action_alert.setIcon(R.drawable.alertreddot_new);
                                if ((isComingFromFirebase) || ((wjalert_banner.getVisibility() == View.VISIBLE))) {
                                    showAlertBannerMessage(allFeedList.get(0));
                                }
                            }
                        }
                    }
                } else {
                    if (action_alert != null) {
                        wjalert_banner.setVisibility(View.GONE);
                        action_alert.setVisible(false);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void showAlertBannerMessage(DTONewFeed feed) {
        try {
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() != 3) {
                String message = "New word jumble available!";
                if ((feed.getLandingBannerMessage() != null) && (!feed.getLandingBannerMessage().isEmpty())) {
                    message = feed.getLandingBannerMessage();
                }
                if ((feed.getFeedType() == FeedType.GAMELET_WORDJUMBLE) || (feed.getFeedType() == FeedType.GAMELET_WORDJUMBLE_V2) || (feed.getFeedType() == FeedType.GAMELET_WORDJUMBLE_V3)) {
                    wjalert_bannertext.setText(message);
                    showAlertBanner(true);
                } else if (showingAlertBanner) {
                    wjalert_bannertext.setText(message);
                    showAlertBanner(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean showingAlertBanner = false;


    private void showAlertBanner(boolean showBanner) {
        try {
            if ((wjalert_banner.getVisibility() == View.VISIBLE) || (showBanner)) {
                showingAlertBanner = true;
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) wjalert_banner.getLayoutParams();
                wjalert_banner.setPivotX(params.width);
                wjalert_banner.setPivotY(0);
                if ((actionSearch.isVisible()) && (action_gotohostapp.isVisible())) {
                    wjalert_banner.setX(scrWidth - params.width - (dpToPx(103)));
                } else if ((!actionSearch.isVisible()) && (!action_gotohostapp.isVisible())) {
                    wjalert_banner.setX(scrWidth - params.width - (dpToPx(8)));
                } else {
                    wjalert_banner.setX(scrWidth - params.width - (dpToPx(55)));
                }
                if (wjalert_banner.getVisibility() == View.GONE) {
                    wjalert_banner.setVisibility(View.VISIBLE);
                    showOptionWithAnimA(wjalert_banner);
                }
            }
        } catch (Exception e) {
        }
    }

    private void showOptionWithAnimA(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.3f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.3f, 1.0f);
        ObjectAnimator scaleDownZ = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1.0f);
        scaleDownX.setDuration(250);
        scaleDownY.setDuration(250);
        scaleDownZ.setDuration(250);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY).with(scaleDownZ);
        scaleDown.start();
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /*  public Comparator<DTONewFeed> newsFeedSorter = (s1, s2) -> s2.getTimestamp().compareTo(s1.getTimestamp());*/


    public Comparator<FilterCategory> feedCategorySorter = new Comparator<FilterCategory>() {
        public int compare(FilterCategory s1, FilterCategory s2) {
            return Integer.valueOf(s1.getCategoryType()).compareTo(Integer.valueOf(s2.getCategoryType()));
        }
    };

    private boolean isValidateUserFirstTime = true;

    private void checkForValidUser() {
        isValidateUserFirstTime = true;
        String keyMsg = "userValidity/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/invalid";
        ValueEventListener avatarListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        boolean inValid = (boolean) snapshot.getValue();
                        if (inValid) {
                            isUserValidInDB();

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
        OustFirebaseTools.getRootRef().child(keyMsg).keepSynced(true);
        OustFirebaseTools.getRootRef().child(keyMsg).addValueEventListener(avatarListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(avatarListener, keyMsg));
    }


    private void isUserValidInDB() {
        try {

            String checkUserValidity_url = OustSdkApplication.getContext().getResources().getString(R.string.checkUserValidity_url);
            checkUserValidity_url = HttpManager.getAbsoluteUrl(checkUserValidity_url) + OustAppState.getInstance().getActiveUser().getStudentid();

            ApiCallUtils.doNetworkCall(Request.Method.GET, checkUserValidity_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {

                        if (response.has("Validity") || response.has("validity")) {
                            try {
                                if ((response.has("Validity") && response.getBoolean("Validity")) || (response.has("validity") && response.getBoolean("validity"))) {
                                    OustPreferences.clear("saveduserdatalist");
                                    onLogout();
                                    if (isValidateUserFirstTime) {
                                        isValidateUserFirstTime = false;
                                        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)) {
                                            OustSdkTools.showToast("You are marked as Invalid user, Please contact your system administrator");
                                        }
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //=======================================================================================================================================

    private void getCatalogueId() {
        try {
            String message = "/landingPage/" + activeUser.getStudentKey() + "/catalogueId";
            ValueEventListener avatarListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            long catalogueId = (long) snapshot.getValue();
                            if (catalogueId > 0) {
                                OustPreferences.saveTimeForNotification("catalogueId", catalogueId);
                                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2)
                                    getCatalogueData(1);
                            }
                        } else {
                            OustPreferences.saveTimeForNotification("catalogueId", 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(avatarListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void getAppTutorialData() {
        String path = "/system/appTutorial";
        Log.d(TAG, "getAppTutorialData: " + path);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        ArrayList<AppTutorialDataModel> appTutorialList = (ArrayList<AppTutorialDataModel>) dataSnapshot.getValue();
                        appTutorialDataModelArrayList = new ArrayList<>();
                        if (appTutorialList.size() > 0 && !isAppTutorialShown & isAppTutorialShownOnlyLogin) {
                            for (int i = 0; i < appTutorialList.size(); i++) {
                                AppTutorialDataModel tabInfoData = new AppTutorialDataModel();
                                Map<Object, Object> tabInfoMap = (Map<Object, Object>) appTutorialList.get(i);
                                tabInfoData.setMediaId((long) tabInfoMap.get("mediaId"));
                                tabInfoData.setMediaType((String) tabInfoMap.get("mediaType"));
                                tabInfoData.setMediaUrl((String) tabInfoMap.get("mediaUrl"));
                                appTutorialDataModelArrayList.add(tabInfoData);
                            }
                            Intent intent = new Intent(NewLandingActivity.this, AppTutorialActivity.class);
                            intent.putParcelableArrayListExtra("DATA", appTutorialDataModelArrayList);
                            startActivity(intent);
                        } else {
                            initializeAllData();
                        }
                    } else {
                        initializeAllData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    initializeAllData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + path);
                //May need it - 2 commented lines
                /*OustPreferences.saveAppInstallVariable(IS_APP_TUTORIAL_SHOWN, true);
                OustStaticVariableHandling.getInstance().setAppTutorialShown(true);*/

                //Unable to access firebase. Proceeding...
                OustSdkTools.showToast("Unable to access firebase. Proceeding...");
                initializeAllData();
            }
        };
        OustFirebaseTools.getRootRef().child(path).keepSynced(true);
        OustFirebaseTools.getRootRef().child(path).addListenerForSingleValueEvent(valueEventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(valueEventListener, path));
    }

    private String CourseStartIconURL;
    private String courseFinishIconURL;
    private String courseLevelIconURL;

    private void getUserAppConfiguration() {
        Log.e(TAG, "inside getUserAppConfiguration method");
        String message = "/system/appConfig";
        Log.d(TAG, "getUserAppConfiguration: " + message);
        ValueEventListener appConfigListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        Map<String, Object> appConfigMap = (Map<String, Object>) snapshot.getValue();
                        if (appConfigMap.get("isPlayModeEnabled") != null) {
                            OustPreferences.saveAppInstallVariable("isPlayModeEnabled", (boolean) appConfigMap.get("isPlayModeEnabled"));
                            getPlayGameData();
                        } else {
                            OustPreferences.saveAppInstallVariable("isPlayModeEnabled", false);
                        }
                        if (appConfigMap.get(AppConstants.StringConstants.CAT_BANNER) != null) {
                            OustPreferences.save(AppConstants.StringConstants.CAT_BANNER, (String) appConfigMap.get(AppConstants.StringConstants.CAT_BANNER));
                        }
                        if (appConfigMap.get("logoutButtonEnabled") != null) {
                            OustPreferences.saveAppInstallVariable("logoutButtonEnabled", (boolean) appConfigMap.get("logoutButtonEnabled"));
                        } else {
                            OustPreferences.saveAppInstallVariable("logoutButtonEnabled", false);
                        }
                        if (appConfigMap.get("showGoalSetting") != null) {
                            OustPreferences.saveAppInstallVariable("showGoalSetting", (boolean) appConfigMap.get("showGoalSetting"));
                        } else {
                            OustPreferences.saveAppInstallVariable("showGoalSetting", false);
                        }
                        if (appConfigMap.get("rewardPageLink") != null) {
                            OustPreferences.save("rewardpagelink", (String) appConfigMap.get("rewardPageLink"));
                        } else {
                            OustPreferences.clear("rewardpagelink");
                        }
                        if (appConfigMap.get("companydisplayName") != null) {
                            OustPreferences.save("companydisplayName", (String) appConfigMap.get("companydisplayName"));
                        }
                        if (appConfigMap.get("panelColor") != null) {
                            OustPreferences.save("toolbarColorCode", (String) appConfigMap.get("panelColor"));
                            toolbarColorCode = (String) appConfigMap.get("panelColor");
                            Log.e(TAG, "got panel color from appConfigMap" + appConfigMap.get("panelColor"));
                        } else {
                            OustPreferences.save("toolbarColorCode", "#ff01b5a2");
                            toolbarColorCode = "#ff01b5a2";
                            Log.e(TAG, "THIS IS NOT map data color" + "toolbarColorCode #ff01b5a2");
                        }
                        if (appConfigMap.get("contestHistoryBanner") != null) {
                            OustPreferences.save("contestHistoryBanner", (String) appConfigMap.get("contestHistoryBanner"));
                        } else {
                            OustPreferences.clear("contestHistoryBanner");
                        }
                        if (appConfigMap.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER) != null) {
                            OustPreferences.save(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER, (String) appConfigMap.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER));
                        }

                        if (appConfigMap.get("panelLogo") != null) {
                            OustPreferences.save("panelLogo", ((String) appConfigMap.get("panelLogo")));
                            Log.e(TAG, "got panel logo " + appConfigMap.get("panelLogo"));
                        } else {
                            OustPreferences.clear("panelLogo");
                        }
                        if (appConfigMap.get("secondaryColor") != null) {
                            OustPreferences.save("secondaryColor", (String) appConfigMap.get("secondaryColor"));
                            Log.e(TAG, "got secondary Color from appConfigMap" + (String) appConfigMap.get("secondaryColor"));
                        } else {
                            OustPreferences.save("secondaryColor", "#FE9738");
                            Log.e(TAG, "THIS IS NOT map data color" + "secondaryColor #FE9738");
                        }
                        if (appConfigMap.get("treasuryColor") != null) {
                            OustPreferences.save("treasuryColor", (String) appConfigMap.get("treasuryColor"));
                            Log.e(TAG, "got treasury Color from appConfigMap" + (String) appConfigMap.get("treasuryColor"));
                        } else {
                            OustPreferences.save("treasuryColor", "#46C7FA");
                            Log.e(TAG, "THIS IS NOT map data color" + "treasuryColor #46C7FA");
                        }
                        setToolBarColorAndIcons();

                        if (appConfigMap.get("resetPasswordParamater") != null) {
                            OustPreferences.save("resetPasswordParamater", ((String) appConfigMap.get("resetPasswordParamater")));
                            Log.e(TAG, "got resetPasswordParameter:" + appConfigMap.get("resetPasswordParamater"));
                        } else {
                            OustPreferences.clear("resetPasswordParamater");
                        }

                        if (appConfigMap.get("autoLogout") != null) {
                            OustPreferences.saveAppInstallVariable("oustautoLogout", ((boolean) appConfigMap.get("autoLogout")));
                        } else {
                            OustPreferences.saveAppInstallVariable("oustautoLogout", false);
                        }
                        if (appConfigMap.get("autoLogoutTimeout") != null) {
                            OustPreferences.saveTimeForNotification("oustautoLogoutTimeout", ((long) appConfigMap.get("autoLogoutTimeout")));
                        } else {
                            OustPreferences.saveTimeForNotification("oustautoLogoutTimeout", 0);
                        }
                        if (appConfigMap.get("restrictUserImageEdit") != null) {
                            OustPreferences.saveAppInstallVariable("restrictUserImageEdit", ((boolean) appConfigMap.get("restrictUserImageEdit")));
                        } else {
                            OustPreferences.saveAppInstallVariable("restrictUserImageEdit", false);
                        }

                        if (appConfigMap.get("userAreaBgImg") != null) {
                            OustPreferences.save("userAreaBgImg", (String) appConfigMap.get("userAreaBgImg"));
                            setLayoutAspectRatiosmall(newmainlanding_topsublayout, 0);
                        } else {
                            OustPreferences.clear("userAreaBgImg");
                        }
                        if (appConfigMap.get("redirectIcon") != null) {
                            redirectIcon = ((String) appConfigMap.get("redirectIcon"));
                            OustPreferences.save("redirectIcon", redirectIcon);
                            Log.e(TAG, "redirect icon " + redirectIcon);
                            setHostAppIcon();
                        } else {
                            OustPreferences.clear("redirectIcon");
                            setHostAppIcon();
                        }
                        if (appConfigMap.get("redirectAppPackage") != null) {
                            OustPreferences.save("redirectAppPackage", ((String) appConfigMap.get("redirectAppPackage")));
                        } else {
                            OustPreferences.clear("redirectAppPackage");
                        }
                        if (isFirstTime) {
                            if (appConfigMap.get("layout") != null) {
                                if (((String) appConfigMap.get("layout")).equalsIgnoreCase("LAYOUT_1")) {
                                    OustStaticVariableHandling.getInstance().setIsNewLayout(1);
                                    initLayout2Views();
                                    getUserNewsFeed();
                                } else if (((String) appConfigMap.get("layout")).equalsIgnoreCase("LAYOUT_2")) {
                                    OustStaticVariableHandling.getInstance().setIsNewLayout(2);
                                    initLayout3Views();
                                    getCatalogueId();
                                    getUserNewsFeed();
                                    bannerclose_btn.setVisibility(View.VISIBLE);
                                } else {
                                    //} else if (((String) appConfigMap.get("layout")).equalsIgnoreCase("LAYOUT_3")) {
                                    Log.d(TAG, "onDataChange: layout 3");
                                    OustStaticVariableHandling.getInstance().setIsNewLayout(3);
                                    initLayout4Views();
                                    getCatalogueId();
                                    newFeedHashMap = new HashMap<>();
                                    getAllUserNewsFeed();
                                    main_layout_3.setVisibility(View.VISIBLE);
                                }
                            }
                            OustPreferences.saveintVar("isNewLayout", OustStaticVariableHandling.getInstance().getIsNewLayout());
                            setNewLayout();
                            isFirstTime = false;
                        }
                        if (appConfigMap.get("liveTraining") != null) {
                            OustPreferences.saveAppInstallVariable("liveTraining", ((boolean) appConfigMap.get("liveTraining")));
                        } else {
                            OustPreferences.clear("liveTraining");
                        }

                        if (appConfigMap.get(AppConstants.StringConstants.THEME_SOUND) != null) {
                            OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.THEME_SOUND, ((boolean) appConfigMap.get(AppConstants.StringConstants.THEME_SOUND)));
                        } else {
                            OustPreferences.clear(AppConstants.StringConstants.THEME_SOUND);
                        }

                        if (appConfigMap.get("features") != null) {
                            Map<String, Object> featuresMap = (Map<String, Object>) appConfigMap.get("features");
                            if (featuresMap != null) {

                                if (featuresMap.get("catalogueName") != null) {
                                    String catalogueName = OustSdkTools.convertToStr(featuresMap.get("catalogueName"));
                                    if (catalogueName != null && !catalogueName.equals("catalogueName")) {
                                        OustPreferences.save("catalogueName", catalogueName);
                                        catalogue_label.setText(catalogueName);
                                        catalogue_labelline.setText(catalogueName);
                                    }
                                } else {
                                    catalogue_label.setText(getResources().getString(R.string.catalogue_label));
                                    catalogue_labelline.setText(getResources().getString(R.string.catalogue_label));
                                    OustPreferences.clear("catalogueName");
                                    OustPreferences.save("catalogueNameStatic", getResources().getString(R.string.catalogue_label));
                                }

                                if (featuresMap.get("teamAnalyticsName") != null) {
                                    String teamAnalyticsName = OustSdkTools.convertToStr(featuresMap.get("teamAnalyticsName"));
                                    if (teamAnalyticsName != null) {
                                        OustPreferences.save("teamAnalyticsName", teamAnalyticsName);
                                    }
                                } else {
                                    OustPreferences.clear("teamAnalyticsName");
                                }

                                if (featuresMap.get("disableUser") != null) {
                                    Log.d(TAG, "Yes has disableUser");
                                    OustPreferences.saveAppInstallVariable("disableUser", ((boolean) featuresMap.get("disableUser")));
                                } else {
                                    Log.d(TAG, "No disableUser");
                                    OustPreferences.clear("disableUser");
                                }

                                if (featuresMap.get("showNewNoticeboardUI") != null) {
                                    OustPreferences.saveAppInstallVariable("showNewNoticeboardUI", ((boolean) featuresMap.get("showNewNoticeboardUI")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("showNewNoticeboardUI", false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_MULTIPLE_CPL) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_MULTIPLE_CPL)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL, false);
                                }

                                if (featuresMap.get("sendPushNotifications") != null) {
                                    Log.d(TAG, "Yes has sendPushNotifications");
                                    OustPreferences.saveAppInstallVariable("sendPushNotifications", ((boolean) featuresMap.get("sendPushNotifications")));
                                } else {
                                    Log.d(TAG, "No sendPushNotifications");
                                    OustPreferences.saveAppInstallVariable("sendPushNotifications", true);
                                }

                                if (featuresMap.get("showCorn") != null) {
                                    Log.d(TAG, "Yes has showCorn");
                                    OustPreferences.saveAppInstallVariable("showCorn", ((boolean) featuresMap.get("showCorn")));
                                } else {
                                    Log.d(TAG, "No showCorn");
                                    OustPreferences.clear("showCorn");
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_MY_TASK) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_MY_TASK, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_MY_TASK)));
                                    boolean isMyTask = ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_MY_TASK));
                                    if (isMyTask) {
                                        include_componemt_user.setVisibility(View.VISIBLE);
                                        normal_user_layout.setVisibility(View.GONE);
                                    } else {
                                        normal_user_layout.setVisibility(View.VISIBLE);
                                        include_componemt_user.setVisibility(View.GONE);
                                       /* include_componemt_user.setVisibility(View.VISIBLE);
                                        normal_user_layout.setVisibility(View.GONE);*/
                                    }
                                } else {
                                    normal_user_layout.setVisibility(View.VISIBLE);
                                    include_componemt_user.setVisibility(View.GONE);
                                  /*  include_componemt_user.setVisibility(View.VISIBLE);
                                    normal_user_layout.setVisibility(View.GONE);*/
                                }
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB)));
                                    isCPLShowOnCourse = (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB);
                                } else {
                                    isCPLShowOnCourse = false;
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.DISABLE_PROFILE_EDIT) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_PROFILE_EDIT, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_PROFILE_EDIT)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_PROFILE_EDIT, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_COMMUNICATION) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_COMMUNICATION, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_COMMUNICATION)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_COMMUNICATION, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.isStopWatchEnable) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.isStopWatchEnable, ((boolean) featuresMap.get(AppConstants.StringConstants.isStopWatchEnable)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.isStopWatchEnable, false);
                                }
                                if (featuresMap.get(AppConstants.StringConstants.FEED_COMMENT_DISABLE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_COMMENT_DISABLE)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.FEED_LIKE_DISABLE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_LIKE_DISABLE)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE, false);
                                }


                                if (featuresMap.get(AppConstants.StringConstants.FEED_SHARE_DISABLE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_SHARE_DISABLE)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.MAX_DATE_RANGE) != null) {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE, ((long) featuresMap.get(AppConstants.StringConstants.MAX_DATE_RANGE)));
                                } else {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE, 0);
                                }


                                if (featuresMap.get(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE, ((boolean) featuresMap.get(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE, true);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY) != null) {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY, ((long) featuresMap.get(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY)));
                                } else {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY, 0);
                                }


                                /*if (featuresMap.get(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME, ((boolean) featuresMap.get(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME, true);
                                }*/
                                OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME, false);


                                if (featuresMap.get(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED) != null) {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED, ((long) featuresMap.get(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED)));
                                } else {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED, 0);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.HOST_APP_LINK_DISABLED) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.HOST_APP_LINK_DISABLED, ((boolean) featuresMap.get(AppConstants.StringConstants.HOST_APP_LINK_DISABLED)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.HOST_APP_LINK_DISABLED, true);
                                }

                                //isPreviousDateAllowed
                                if (featuresMap.get(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED) != null) {
                                    Log.d(TAG, "Yes has AppConstants.StringConstants.IS_PREV_DATE_ALLOWED");
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED, ((boolean) featuresMap.get(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED, false);
                                }


                                if (featuresMap.get(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED) != null) {
                                    Log.d(TAG, "Yes has AppConstants.StringConstants.IS_PREV_DATE_ALLOWED");
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED, ((boolean) featuresMap.get(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.ENABLE_RD_WD) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.ENABLE_RD_WD, ((boolean) featuresMap.get(AppConstants.StringConstants.ENABLE_RD_WD)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.ENABLE_RD_WD, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.OPEN_WELCOME_POPUP) != null) {
                                    Log.d(TAG, "Yes has AppConstants.StringConstants.OPEN_WELCOME_POPUP:");
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.OPEN_WELCOME_POPUP, ((boolean) featuresMap.get(AppConstants.StringConstants.OPEN_WELCOME_POPUP)));
                                    if (((boolean) featuresMap.get(AppConstants.StringConstants.OPEN_WELCOME_POPUP))) {
                                        getSpecialFeedId();
                                    }
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.OPEN_WELCOME_POPUP, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN, ((boolean) featuresMap.get(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN, false);
                                }

                                if (featuresMap.get("enableVideoDownload") != null) {
                                    OustPreferences.saveAppInstallVariable("enableVideoDownload", ((boolean) featuresMap.get("enableVideoDownload")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("enableVideoDownload", false);
                                }

                                if (featuresMap.get("disableCourse") != null) {
                                    OustPreferences.saveAppInstallVariable("hideCourse", ((boolean) featuresMap.get("disableCourse")));
                                } else {
                                    OustPreferences.clear("hideCourse");
                                }

                                if (featuresMap.get("enableGalleryUpload") != null) {
                                    OustPreferences.saveAppInstallVariable("enableGalleryUpload", ((boolean) featuresMap.get("enableGalleryUpload")));
                                } else {
                                    OustPreferences.clear("enableGalleryUpload");
                                }


                                if (featuresMap.get("disableCatalogue") != null) {
                                    OustPreferences.saveAppInstallVariable("hideCatalog", ((boolean) featuresMap.get("disableCatalogue")));
                                } else {
                                    OustPreferences.clear("hideCatalog");
                                }
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION);
                                }
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_FAQ) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FAQ, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_FAQ));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.SHOW_FAQ);
                                }
                                if (featuresMap.get(AppConstants.StringConstants.URL_FAQ) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.URL_FAQ, (String) featuresMap.get(AppConstants.StringConstants.URL_FAQ));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.URL_FAQ);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.TEAM_ANALYTICS) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.TEAM_ANALYTICS, ((boolean) featuresMap.get(AppConstants.StringConstants.TEAM_ANALYTICS)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.TEAM_ANALYTICS, false);
                                }


                                if (featuresMap.get(AppConstants.StringConstants.URL_TEAM_ANALYTICS) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.URL_TEAM_ANALYTICS, (String) featuresMap.get(AppConstants.StringConstants.URL_TEAM_ANALYTICS));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.URL_TEAM_ANALYTICS);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.DISABLE_CPL_REDIRECT_URL) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.DISABLE_CPL_REDIRECT_URL, (String) featuresMap.get(AppConstants.StringConstants.DISABLE_CPL_REDIRECT_URL));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.DISABLE_CPL_REDIRECT_URL);
                                }

                                if (featuresMap.get("disableAssessment") != null) {
                                    OustPreferences.saveAppInstallVariable("hideAssessment", ((boolean) featuresMap.get("disableAssessment")));
                                } else {
                                    OustPreferences.clear("hideAssessment");
                                }

                                if (featuresMap.get("disableCplLogout") != null) {
                                    OustPreferences.saveAppInstallVariable("disableCplLogout", ((boolean) featuresMap.get("disableCplLogout")));
                                } else {
                                    OustPreferences.clear("disableCplLogout");
                                }

                                if (featuresMap.get("disableCplIntroIcon") != null) {
                                    OustPreferences.saveAppInstallVariable("disableCplIntroIcon", ((boolean) featuresMap.get("disableCplIntroIcon")));
                                } else {
                                    OustPreferences.clear("disableCplIntroIcon");
                                }

                                if (featuresMap.get("disableCplTitle") != null) {
                                    OustPreferences.saveAppInstallVariable("disableCplTitle", ((boolean) featuresMap.get("disableCplTitle")));
                                } else {
                                    OustPreferences.clear("disableCplTitle");
                                }

                                if (featuresMap.get("learningDiaryName") != null) {
                                    String learningDiaryName = OustSdkTools.convertToStr(featuresMap.get("learningDiaryName"));
                                    if (learningDiaryName != null && !learningDiaryName.isEmpty()) {
                                        OustPreferences.save("learningDiaryName", learningDiaryName);
                                    } else {
                                        OustPreferences.save("learningDiaryName", getResources().getString(R.string.my_diary));
                                    }

                                } else {
                                    OustPreferences.clear("learningDiaryName");
                                }

                                if (featuresMap.get(AppConstants.StringConstants.HOST_APP_NAME) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.HOST_APP_NAME, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.HOST_APP_NAME)));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.HOST_APP_NAME);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.FEED_BACK_NAME) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.FEED_BACK_NAME, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.FEED_BACK_NAME)));
                                } else {
                                    OustPreferences.save(AppConstants.StringConstants.FEED_BACK_NAME, getResources().getString(R.string.help_support));
                                }

                                if (featuresMap.get(AppConstants.StringConstants.CPL_LANG_CHANGE) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.CPL_LANG_CHANGE, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.CPL_LANG_CHANGE)));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.CPL_LANG_CHANGE);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING) != null) {
                                    try {
                                        Map<String, Object> customCourseBranding = (Map<String, Object>) featuresMap.get(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING);
                                        if (customCourseBranding != null) {
                                            if (customCourseBranding.get(AppConstants.StringConstants.COURSE_START_ICON) != null) {
                                                CourseStartIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_START_ICON);
                                                String fileName = OustMediaTools.getMediaFileName(CourseStartIconURL);
                                                OustPreferences.save("DEFAULT_START_ICON", fileName);
                                                OustPreferences.save(AppConstants.StringConstants.COURSE_START_ICON, CourseStartIconURL);
                                                downloadImages(CourseStartIconURL, fileName);

                                            } else {
                                                OustPreferences.clear("DEFAULT_START_ICON");
                                                OustPreferences.clear(AppConstants.StringConstants.COURSE_START_ICON);

                                            }
                                            if (customCourseBranding.get(AppConstants.StringConstants.COURSE_FINISH_ICON) != null) {
                                                courseFinishIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_FINISH_ICON);
                                                String fileName = OustMediaTools.getMediaFileName(courseFinishIconURL);
                                                OustPreferences.save("DEFAULT_FINISH_ICON", fileName);
                                                OustPreferences.save(AppConstants.StringConstants.COURSE_FINISH_ICON, courseFinishIconURL);
                                                downloadImages(courseFinishIconURL, fileName);
                                            } else {
                                                OustPreferences.clear("DEFAULT_FINISH_ICON");
                                                OustPreferences.clear(AppConstants.StringConstants.COURSE_FINISH_ICON);

                                            }
                                            if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON) != null) {
                                                courseLevelIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
                                                String fileName = OustMediaTools.getMediaFileName(courseLevelIconURL);
                                                OustPreferences.save("DEFAULT_INDICATOR_ICON", fileName);
                                                OustPreferences.save(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON, courseLevelIconURL);
                                                downloadImages(courseLevelIconURL, fileName);
                                            } else {
                                                OustPreferences.clear("DEFAULT_INDICATOR_ICON");
                                                OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
                                            }


                                            if (customCourseBranding.get(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH) != null) {
                                                String urlPath = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH);
                                                String fileName = OustMediaTools.getMediaFileName(urlPath);
                                                OustPreferences.save("DEFAULT_COURSE_AUDIO", fileName);
                                                OustPreferences.save(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH, urlPath);
                                                downloadImages(urlPath, fileName);

                                            } else {
                                                OustPreferences.clear("DEFAULT_COURSE_AUDIO");
                                                OustPreferences.clear(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH);
                                            }

                                            if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH) != null) {
                                                String urlPath = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH);
                                                String fileName = OustMediaTools.getMediaFileName(urlPath);
                                                OustPreferences.save("DEFAULT_LEVEL_AUDIO", fileName);
                                                OustPreferences.save(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH, urlPath);
                                                downloadImages(urlPath, fileName);

                                            } else {
                                                OustPreferences.clear("DEFAULT_LEVEL_AUDIO");
                                                OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH);
                                            }


                                            if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LOCK_COLOR) != null) {
                                                OustPreferences.save(AppConstants.StringConstants.COURSE_LOCK_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LOCK_COLOR));
                                            } else {
                                                OustPreferences.clear(AppConstants.StringConstants.COURSE_LOCK_COLOR);

                                            }
                                            if (customCourseBranding.get(AppConstants.StringConstants.CURRENT_LEVEL_COLOR) != null) {
                                                OustPreferences.save(AppConstants.StringConstants.CURRENT_LEVEL_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.CURRENT_LEVEL_COLOR));
                                            } else {
                                                OustPreferences.clear(AppConstants.StringConstants.CURRENT_LEVEL_COLOR);

                                            }
                                            if (customCourseBranding.get(AppConstants.StringConstants.COURSE_UNLOCK_COLOR) != null) {
                                                OustPreferences.save(AppConstants.StringConstants.COURSE_UNLOCK_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_UNLOCK_COLOR));
                                            } else {
                                                OustPreferences.clear(AppConstants.StringConstants.COURSE_UNLOCK_COLOR);

                                            }
                                            if (customCourseBranding.get(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR) != null) {
                                                OustPreferences.save(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR));
                                            } else {
                                                OustPreferences.clear(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR);

                                            }

                                            if (customCourseBranding.get(AppConstants.StringConstants.RM_USER_STATUS_COLOR) != null) {
                                                OustPreferences.save(AppConstants.StringConstants.RM_USER_STATUS_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.RM_USER_STATUS_COLOR));
                                            } else {
                                                OustPreferences.clear(AppConstants.StringConstants.RM_USER_STATUS_COLOR);

                                            }

                                        }
                                    } catch (Exception e) {
                                        //boolean val = (boolean) featuresMap.get(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING);
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }

                                    //OustPreferences.save(AppConstants.StringConstants.CPL_LANG_CHANGE, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.CPL_LANG_CHANGE)));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING);
                                }


                                //disableCourseReviewMode
                                if (featuresMap.get("disableCourseReviewMode") != null) {
                                    OustPreferences.saveAppInstallVariable("disableCourseReviewMode", ((boolean) featuresMap.get("disableCourseReviewMode")));
                                } else {
                                    OustPreferences.clear("disableCourseReviewMode");
                                }
                                if (featuresMap.get("disableFavCard") != null) {
                                    OustPreferences.saveAppInstallVariable("disableFavCard", ((boolean) featuresMap.get("disableFavCard")));
                                } else {
                                    OustPreferences.clear("disableFavCard");
                                }
                                if (featuresMap.get("disableArchive") != null) {
                                    OustPreferences.saveAppInstallVariable("hideArchive", ((boolean) featuresMap.get("disableArchive")));
                                } else {
                                    OustPreferences.clear("hideArchive");
                                }

                                if (featuresMap.get(AppConstants.StringConstants.DISABLE_LEARNING_DIARY) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_LEARNING_DIARY)));
                                    Log.d(TAG, "onDataChange: DISABLE_LEARNING_DIARY:" + OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY, true);
                                }


                                if (featuresMap.get("disableCplClose") != null) {
                                    OustPreferences.saveAppInstallVariable("hideCplCloseBtn", ((boolean) featuresMap.get("disableCplClose")));
                                } else {
                                    OustPreferences.clear("hideCplCloseBtn");
                                }

                                if (featuresMap.get("disableUserSeeting") != null) {
                                    OustPreferences.saveAppInstallVariable("hideUserSetting", ((boolean) featuresMap.get("disableUserSeeting")));
                                } else {
                                    OustPreferences.clear("hideUserSetting");
                                }
                                if (featuresMap.get("disableCourseLBShare") != null) {
                                    OustPreferences.saveAppInstallVariable("restrictCourseLeaderboardShare", ((boolean) featuresMap.get("disableCourseLBShare")));
                                } else {
                                    OustPreferences.clear("restrictCourseLeaderboardShare");
                                }
                                if (featuresMap.get("disableOrgLeaderboard") != null) {
                                    OustPreferences.saveAppInstallVariable("disableOrgLeaderboard", ((boolean) featuresMap.get("disableOrgLeaderboard")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("disableOrgLeaderboard", false);
                                }

                                if (featuresMap.get("disableFabMenu") != null) {
                                    OustPreferences.saveAppInstallVariable("disableFabMenu", ((boolean) featuresMap.get("disableFabMenu")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("disableFabMenu", false);
                                }
                                if (featuresMap.get("disableToolbarLB") != null) {
                                    OustPreferences.saveAppInstallVariable("disableToolbarLB", ((boolean) featuresMap.get("disableToolbarLB")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("disableToolbarLB", true);
                                }

                                if (featuresMap.get("disableFavorite") != null) {
                                    OustPreferences.saveAppInstallVariable("disableFavorite", ((boolean) featuresMap.get("disableFavorite")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("disableFavorite", false);
                                }

                                if (featuresMap.get("hideAllCourseLeaderBoard") != null) {
                                    OustPreferences.saveAppInstallVariable("hideAllCourseLeaderBoard", ((boolean) featuresMap.get("hideAllCourseLeaderBoard")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("hideAllCourseLeaderBoard", false);
                                }

                                if (featuresMap.get("hideAllAssessmentLeaderBoard") != null) {
                                    OustPreferences.saveAppInstallVariable("hideAllAssessmentLeaderBoard", ((boolean) featuresMap.get("hideAllAssessmentLeaderBoard")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("hideAllAssessmentLeaderBoard", false);
                                }

                                if (featuresMap.get("hideCourseBulletin") != null) {
                                    OustPreferences.saveAppInstallVariable("hideCourseBulletin", ((boolean) featuresMap.get("hideCourseBulletin")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("hideCourseBulletin", false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.DISABLE_TODO_ONCLICK) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_TODO_ONCLICK, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_TODO_ONCLICK)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_TODO_ONCLICK, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.DISABLE_CPL_SUCCESS_MSG) != null) {
                                    String msg = OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.DISABLE_CPL_SUCCESS_MSG));
                                    if (msg != null) {
                                        OustPreferences.save(AppConstants.StringConstants.DISABLE_CPL_SUCCESS_MSG, msg);
                                        Log.d(TAG, "DISABLE_CPL_SUCCESS_MSG: " + msg);
                                    }
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.DISABLE_CPL_SUCCESS_MSG);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.DISABLE_CPL_FAILURE_MSG) != null) {
                                    String msg = OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.DISABLE_CPL_FAILURE_MSG));
                                    if (msg != null) {
                                        OustPreferences.save(AppConstants.StringConstants.DISABLE_CPL_FAILURE_MSG, msg);
                                        Log.d(TAG, "DISABLE_CPL_FAILURE_MSG: " + msg);
                                    }
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.DISABLE_CPL_FAILURE_MSG);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION) != null) {
                                    Log.d(TAG, "Yes has disableUserOnCplCompletion");
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)));
                                } else {
                                    Log.d(TAG, "No disableUserOnCplCompletion");
                                    OustPreferences.clear(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.LB_RESET_PERIOD) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.LB_RESET_PERIOD, (String) featuresMap.get(AppConstants.StringConstants.LB_RESET_PERIOD));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.LB_RESET_PERIOD);
                                }

                                if (featuresMap.get("showCityListScreenForCPL") != null) {
                                    isCityScreenEnabled = (boolean) featuresMap.get("showCityListScreenForCPL");
                                    Log.d(TAG, "showCityListScreenForCPL: " + isCityScreenEnabled);
                                    if (isCityScreenEnabled && !OustPreferences.getAppInstallVariable("IS_CITY_SELECTED")) {
                                        //getUserParentCplId(true);
                                        //openCityScreen(false, false);
                                        isAlertNeeded = true;
                                        if (isParentCplLoaded) {
                                            if (OustPreferences.getTimeForNotification("parentCplId") != 0) {
                                                isAlertNeeded = false;
                                                openAlertPopup();
                                            }
                                        }
                                    } else if (isCityScreenEnabled && OustPreferences.getAppInstallVariable("IS_CITY_SELECTED") && cplCollectionData == null) {
                                        Log.d(TAG, "onDataChange: please wait till you");
                                    }
                                } else {
                                    isCityScreenEnabled = false;
                                }

                                if (!isCityScreenEnabled) {
                                    if (featuresMap.get("showLanguageScreen") != null) {
                                        isLanguageScreenenabled = (boolean) featuresMap.get("showLanguageScreen");
                                        if ((boolean) featuresMap.get("showLanguageScreen") && !OustPreferences.getAppInstallVariable("IS_LANG_SELECTED")) {
                                            if (featuresMap.get("cplIdForLanguage") != null) {
                                                OpenLanguageScreen((long) featuresMap.get("cplIdForLanguage"), false, false);
                                            }
                                        } else if ((boolean) featuresMap.get("showLanguageScreen") && OustPreferences.getAppInstallVariable("IS_LANG_SELECTED") && cplCollectionData == null) {
                                            Log.d(TAG, "onDataChange: please wait till you");
                                        }
                                    }
                                } else {
                                    isLanguageScreenenabled = true;
                                }

                                if (!isLanguageScreenenabled) {
                                    checkIfNotificationEnabled();
                                }
                                // feature to test showing FFC on Landing Page
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)));
                                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)) {
                                        isContest = true;
                                    } else {
                                        isContest = false;
                                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);
                                    }
                                    initBottomSheetBannerData();
                                } else {
                                    isContest = false;
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);
                                }

                                // feature to test showing CPL on Landing Page
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE)));
                                    isPlayList = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE);
                                    initBottomSheetBannerData();
                                } else {
                                    isPlayList = false;
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, false);
                                }

                                // feature to test showing To do on Landing Page
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE)));
                                    isTodo = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE);
                                    initBottomSheetBannerData();
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_USER_OVERALL_CREDITS) != null) {
                                    boolean isShowOverallCredits = (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_USER_OVERALL_CREDITS);
                                    if (isShowOverallCredits) {
                                        layoutUserOverallcredits.setVisibility(View.VISIBLE);
                                        user_coursecompletedoctext.setVisibility(View.VISIBLE);
                                        user_coursependingoctext.setVisibility(View.VISIBLE);
                                    } else {
                                        layoutUserOverallcredits.setVisibility(View.GONE);
                                        user_coursecompletedoctext.setVisibility(View.GONE);
                                        user_coursependingoctext.setVisibility(View.GONE);
                                    }
                                } else {
                                    layoutUserOverallcredits.setVisibility(View.VISIBLE);
                                    user_coursecompletedoctext.setVisibility(View.VISIBLE);
                                    user_coursependingoctext.setVisibility(View.VISIBLE);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) != null) {
                                    boolean isShow = (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_LB_USER_LOCATION);
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION, isShow);
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION, false);
                                }
                                Log.d(TAG, "onDataChange: USER location:" + OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION));

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, false);
                                }
                                OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, true);
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_CARD_NEW_UI) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CARD_NEW_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CARD_NEW_UI));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CARD_NEW_UI, false);
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI, false);
                                }
                                // Log.d(TAG, "onDataChange: SHOW_NEW_ASSESSMENT_UI:"+OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI));
                            }
                        }


                        if (appConfigMap.get("notificationData") != null) {
                            Map<String, Object> notificationMap = (Map<String, Object>) appConfigMap.get("notificationData");
                            if (notificationMap != null) {
                                if (notificationMap.get("content") != null) {
                                    OustPreferences.save("notificationContent", ((String) notificationMap.get("content")));
                                } else {
                                    OustPreferences.clear("notificationContent");
                                }
                                if (notificationMap.get("title") != null) {
                                    OustPreferences.save("notificationTitle", ((String) notificationMap.get("title")));
                                } else {
                                    OustPreferences.clear("notificationTitle");
                                }
                                if (notificationMap.get("interval") != null) {
                                    OustPreferences.saveTimeForNotification("notificationInterval", ((long) notificationMap.get("interval")));
                                } else {
                                    OustPreferences.clear("notificationInterval");
                                }
                            }
                        }
                        moduleDataArrayList = new ArrayList<>();
                        tabInfoDataArrayList = new ArrayList<>();
                        if (appConfigMap.get("layoutInfo") != null) {
                            Map<Object, Object> layoutInfoData = (Map<Object, Object>) appConfigMap.get("layoutInfo");
                            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                                if (layoutInfoData.get("LAYOUT_2") != null) {
                                    Map<Object, Object> newLandingPageData = (Map<Object, Object>) layoutInfoData.get("LAYOUT_2");
                                    if (newLandingPageData.get("moduleSection") != null) {
                                        ArrayList<Object> moduleArray = (ArrayList<Object>) newLandingPageData.get("moduleSection");
                                        if (moduleArray != null && moduleArray.size() > 0) {
                                            for (int i = 0; i < moduleArray.size(); i++) {
                                                Map<Object, Object> moduleMap = (Map<Object, Object>) moduleArray.get(i);
                                                if (moduleMap != null) {
                                                    if (moduleMap.get("label") != null)
                                                        moduleDataArrayList.add((String) moduleMap.get("label"));
                                                }
                                            }
                                        }
                                    }
                                    if (newLandingPageData.get("tabSection") != null) {
                                        ArrayList<Object> tabSectionMap = (ArrayList<Object>) newLandingPageData.get("tabSection");
                                        if (tabSectionMap != null && tabSectionMap.size() > 0) {
                                            for (int i = 0; i < tabSectionMap.size(); i++) {
                                                TabInfoData tabInfoData = new TabInfoData();
                                                Map<Object, Object> tabInfoMap = (Map<Object, Object>) tabSectionMap.get(i);
                                                if (tabInfoMap.get("label") != null)
                                                    tabInfoData.setLabel((String) tabInfoMap.get("label"));
                                                if (tabInfoMap.get("type") != null)
                                                    tabInfoData.setType((String) tabInfoMap.get("type"));
                                                if (tabInfoMap.get("image") != null)
                                                    tabInfoData.setImage((String) tabInfoMap.get("image"));
                                                if (tabInfoMap.get("showTodo") != null)
                                                    tabInfoData.setShowTodo((boolean) tabInfoMap.get("showTodo"));
                                                tabInfoDataArrayList.add(tabInfoData);
                                            }
                                        }
                                    }
                                }
                                setTabTextandColor();
                            } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                                if (layoutInfoData.get("LAYOUT_3") != null) {
                                    Map<Object, Object> newLandingPageData = (Map<Object, Object>) layoutInfoData.get("LAYOUT_3");
                                    if (newLandingPageData.get("defaultTab") != null)
                                        defaultTab = (int) (long) newLandingPageData.get("defaultTab");
                                    if (newLandingPageData.get("tabSection") != null) {
                                        Log.d(TAG, "onDataChange: ");
                                        ArrayList<Object> tabSectionMap = (ArrayList<Object>) newLandingPageData.get("tabSection");
                                        if (tabSectionMap != null && tabSectionMap.size() > 0) {
                                            for (int i = 0; i < tabSectionMap.size(); i++) {
                                                TabInfoData tabInfoData = new TabInfoData();
                                                Map<Object, Object> tabInfoMap = (Map<Object, Object>) tabSectionMap.get(i);
                                                if (tabInfoMap.get("label") != null)
                                                    tabInfoData.setLabel((String) tabInfoMap.get("label"));
                                                if (tabInfoMap.get("type") != null)
                                                    tabInfoData.setType((String) tabInfoMap.get("type"));
                                                if (tabInfoMap.get("indexName") != null) {
                                                    String indexName = (String) tabInfoMap.get("indexName");
                                                    if (indexName.equalsIgnoreCase("COURSE_AND_ASSESSMENT"))
                                                        COURSE_ASSESSMENT_TAB_FOUND = true;
                                                    tabInfoData.setIndexName(indexName);
                                                }
                                                if (tabInfoMap.get("showTodo") != null) {
                                                    tabInfoData.setShowTodo((boolean) tabInfoMap.get("showTodo"));
                                                }

                                                if (tabInfoMap.get("tabTag") != null)
                                                    tabInfoData.setTabTags((String) tabInfoMap.get("tabTag"));
                                                if (tabInfoMap.get("image") != null)
                                                    tabInfoData.setImage((String) tabInfoMap.get("image"));

                                                if (tabInfoMap.get("categoryId") != null)
                                                    tabInfoData.setCategoryId((long) tabInfoMap.get("categoryId"));

                                                if (tabInfoMap.get("catalogueTabId") != null)
                                                    tabInfoData.setCatalogueTabId((long) tabInfoMap.get("catalogueTabId"));
                                                if (tabInfoMap.get("catalogueType") != null) {
                                                    tabInfoData.setCatalogueType((String) tabInfoMap.get("catalogueType"));
                                                }
                                                if (tabInfoMap.get("hideCatalogue") != null)
                                                    tabInfoData.setHideCatalogue((Boolean) tabInfoMap.get("hideCatalogue"));

                                                tabInfoDataArrayList.add(tabInfoData);
                                            }

                                        }
                                    }
                                    if (newLandingPageData.get("filterCategory") != null) {
                                        ArrayList<Object> filterCategoryMap = (ArrayList<Object>) newLandingPageData.get("filterCategory");
                                        if (filterCategoryMap != null && filterCategoryMap.size() > 0) {
                                            filterCategories = new ArrayList<>();
                                            for (int i = 0; i < filterCategoryMap.size(); i++) {
                                                FilterCategory filterCategory = new FilterCategory();
                                                Map<Object, Object> filterInfoMap = (Map<Object, Object>) filterCategoryMap.get(i);
                                                if (filterInfoMap.get("label") != null)
                                                    filterCategory.setCategoryName((String) filterInfoMap.get("label"));
                                                if (filterInfoMap.get("type") != null)
                                                    filterCategory.setCategoryType((int) (long) filterInfoMap.get("type"));

                                                filterCategories.add(filterCategory);
                                            }
                                        }
                                    }
                                    setFilterItems();
                                    setTabTextAndImages3();
                                }
                            }
                        }

                        showAlertBanner(false);
                        if (newProfileAdapter != null) {
                            newProfileAdapter.notifyDataChange();
                        }
                    }
                    addDrower();
                    setFabButtonVisibility();
                    Log.e(TAG, "about to reach setToolBArCOLOR method");
                } catch (Exception e) {
                    Log.e(TAG, "Error while saving app config data", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "onCancelled: " + message);
                Log.e("Error", error + "");
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(appConfigListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(appConfigListener, message));
    }

    private void getSpecialFeedId() {
        String message = "landingPage/" + activeUser.getStudentKey() + "/specialFeed";
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SpecialFeedModel specialFeedModel = dataSnapshot.getValue(SpecialFeedModel.class);
                if (specialFeedModel != null && specialFeedModel.getFeedId() != 0) {
                    getSpecialFeedData(specialFeedModel.getFeedId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        OustFirebaseTools.getRootRef().child(message).addValueEventListener(valueEventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(valueEventListener, message));
    }

    DTONewFeed newFeed = null;

    private void getSpecialFeedData(final long id) {
        String message = "/feeds/feed" + id;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final HashMap<String, Object> newsfeedMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (newsfeedMap != null) {
                        newFeed = new DTONewFeed();
                        Log.d(TAG, "onDataChange: news feed");
                        CommonTools commonTools = new CommonTools();
                        newFeed = commonTools.getNewFeedFromMap(newsfeedMap, newFeed);
                        newFeed.setFeedId(Long.parseLong(id + ""));
                        initWelComePopViews(newFeed);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        OustFirebaseTools.getRootRef().child(message).addValueEventListener(valueEventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(valueEventListener, message));

    }

    private void setTabTextAndImages3() {
        try {
            if (tabInfoDataArrayList != null && tabInfoDataArrayList.size() > 0) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int tabInfoSize = tabInfoDataArrayList.size();
                if (tabInfoSize == 2) {
                    float reqWidth = scrWidth * (43.5f) / 100;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            (int) reqWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                    myFeedButton.setLayoutParams(params);
                    myCourseButton.setLayoutParams(params);
                    float spaceWidth = scrWidth * (4.3f) / 100;
                    LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                            (int) spaceWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                    tabAspace.setLayoutParams(spaceParams);
                    tabBspace.setLayoutParams(spaceParams);
                    tabEspace.setLayoutParams(spaceParams);
                }

                if (tabInfoSize >= 3) {
                    float reqWidth = scrWidth * (28.9f) / 100;
                    float spaceWidth = 0f;
                    if (tabInfoSize == 4) {
                        spaceWidth = scrWidth * (3f) / 100;
                    } else
                        spaceWidth = scrWidth * (3.3f) / 100;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            (int) reqWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                    myFeedButton.setLayoutParams(params);
                    myCourseButton.setLayoutParams(params);
                    myChallengesButton.setLayoutParams(params);

                    LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                            (int) spaceWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                    tabAspace.setLayoutParams(spaceParams);
                    tabBspace.setLayoutParams(spaceParams);
                    tabCspace.setLayoutParams(spaceParams);
                    tabDspace.setLayoutParams(spaceParams);
                    if (tabInfoDataArrayList.size() == 4) {
                        myNoticeBoardButton.setLayoutParams(params);
                        tabEspace.setLayoutParams(spaceParams);
                    }
                }

                for (int i = 0; i < tabInfoDataArrayList.size(); i++) {

                    TabInfoData tabInfoData = tabInfoDataArrayList.get(i);
                    if (tabInfoData.getType().equalsIgnoreCase("Image")) {

                        if (tabInfoDataArrayList.size() == 1) {
                            myFeedButton.setVisibility(View.GONE);
                        }

                        if (tabInfoDataArrayList.size() == 2) {
                            tabEspace.setVisibility(View.VISIBLE);
                        }

                        if (i == 0) {
                            tabBigImgA.setVisibility(View.VISIBLE);
                            toptab_texta.setVisibility(View.GONE);
                            tabImgA.setVisibility(View.GONE);

                            try {

                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabBigImgA);
                                else {
                                    myFeedButton.setVisibility(View.GONE);
                                    tabAspace.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                        } else if (i == 1) {
                            tabBigImgB.setVisibility(View.VISIBLE);
                            toptab_textb.setVisibility(View.GONE);
                            tabImgB.setVisibility(View.GONE);


                            try {

                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabBigImgB);
                                else {
                                    myCourseButton.setVisibility(View.GONE);
                                    tabBspace.setVisibility(View.GONE);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                        } else if (i == 2) {
                            tabBigImgC.setVisibility(View.VISIBLE);
                            toptab_textc.setVisibility(View.GONE);
                            tabImgC.setVisibility(View.GONE);


                            try {
                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabBigImgC);
                                else {
                                    myChallengesButton.setVisibility(View.GONE);
                                    tabCspace.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                        } else if (i == 3) {
                            tabBigImgD.setVisibility(View.VISIBLE);
                            toptab_textd.setVisibility(View.GONE);
                            tabImgD.setVisibility(View.GONE);
                            try {
                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabBigImgD);
                                else {
                                    myNoticeBoardButton.setVisibility(View.GONE);
                                    tabEspace.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                    } else {

                        String tabName = tabInfoData.getLabel();

                        if (tabInfoDataArrayList.size() == 1) {
                            myFeedButton.setVisibility(View.GONE);
                        }
                        if (tabInfoDataArrayList.size() == 2) {
                            tabEspace.setVisibility(View.VISIBLE);
                        }
                        if (i == 0) {
                            tabBigImgA.setVisibility(View.GONE);
                            toptab_texta.setVisibility(View.VISIBLE);
                            tabImgA.setVisibility(View.VISIBLE);
                            if (tabName != null && !tabName.isEmpty()) {
                                toptab_texta.setText(tabName);
                            }

                            try {

                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabImgA);
                                else
                                    tabImgA.setVisibility(View.GONE);

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                        } else if (i == 1) {
                            tabBigImgB.setVisibility(View.GONE);
                            toptab_textb.setVisibility(View.VISIBLE);
                            tabImgB.setVisibility(View.VISIBLE);

                            if (tabName != null && !tabName.isEmpty())
                                toptab_textb.setText(tabName);
                            else {
                                myCourseButton.setVisibility(View.GONE);
                                tabBspace.setVisibility(View.GONE);
                            }
                            try {
                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabImgB);
                                else
                                    tabImgB.setVisibility(View.GONE);

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                        } else if (i == 2) {
                            tabBigImgC.setVisibility(View.GONE);
                            toptab_textc.setVisibility(View.VISIBLE);
                            tabImgC.setVisibility(View.VISIBLE);
                            if (tabName != null && !tabName.isEmpty()) {
                                toptab_textc.setText(tabName);
                            } else {
                                myChallengesButton.setVisibility(View.GONE);
                                tabCspace.setVisibility(View.GONE);
                            }

                            try {
                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabImgC);
                                else
                                    tabImgC.setVisibility(View.GONE);


                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }


                        } else if (i == 3) {
                            tabBigImgD.setVisibility(View.GONE);
                            toptab_textd.setVisibility(View.VISIBLE);
                            tabImgD.setVisibility(View.VISIBLE);

                            if (tabName != null && !tabName.isEmpty()) {
                                toptab_textd.setText(tabName);
                                myNoticeBoardButton.setVisibility(View.VISIBLE);
                                tabDspace.setVisibility(View.VISIBLE);
                            } else {
                                myNoticeBoardButton.setVisibility(View.GONE);
                                tabDspace.setVisibility(View.GONE);
                            }

                            try {
                                if (tabInfoData.getImage() != null && !tabInfoData.getImage().isEmpty())
                                    Picasso.get().load(tabInfoData.getImage()).into(tabImgD);
                                else
                                    tabImgD.setVisibility(View.GONE);

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                        }

                    }

                }

             /*   if (tabInfoDataArrayList.get(0).getType().equalsIgnoreCase("Image")) {
                    tabBigImgA.setVisibility(View.VISIBLE);
                    tabBigImgB.setVisibility(View.VISIBLE);
                    tabBigImgC.setVisibility(View.VISIBLE);
                    toptab_texta.setVisibility(View.GONE);
                    toptab_textb.setVisibility(View.GONE);
                    toptab_textc.setVisibility(View.GONE);
                    toptab_textd.setVisibility(View.GONE);
                    tabImgB.setVisibility(View.GONE);
                    tabImgA.setVisibility(View.GONE);
                    tabImgC.setVisibility(View.GONE);
                    tabImgD.setVisibility(View.GONE);
                    if (tabInfoDataArrayList.size() == 1) {
                        myFeedButton.setVisibility(View.GONE);
                    }
                    try {
                        if (tabInfoDataArrayList.get(0) != null && tabInfoDataArrayList.get(0).getImage() != null && !tabInfoDataArrayList.get(0).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(0).getImage()).into(tabBigImgA);
                        else {
                            myFeedButton.setVisibility(View.GONE);
                            tabAspace.setVisibility(View.GONE);
                        }
                        if (tabInfoDataArrayList.size() > 1 && tabInfoDataArrayList.get(1) != null && tabInfoDataArrayList.get(1).getImage() != null && !tabInfoDataArrayList.get(1).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(1).getImage()).into(tabBigImgB);
                        else {
                            myCourseButton.setVisibility(View.GONE);
                            tabBspace.setVisibility(View.GONE);
                        }
                        if (tabInfoDataArrayList.size() > 2 && tabInfoDataArrayList.get(2) != null && tabInfoDataArrayList.get(2).getImage() != null && !tabInfoDataArrayList.get(2).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(2).getImage()).into(tabBigImgC);
                        else {
                            myChallengesButton.setVisibility(View.GONE);
                            tabCspace.setVisibility(View.GONE);
                        }
                        if (tabInfoDataArrayList.size() > 3 && tabInfoDataArrayList.get(3) != null && tabInfoDataArrayList.get(3).getImage() != null && !tabInfoDataArrayList.get(3).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(3).getImage()).into(tabBigImgD);
                        else {
                            myNoticeBoardButton.setVisibility(View.GONE);
                            tabEspace.setVisibility(View.GONE);
                        }
                        if (tabInfoDataArrayList.size() == 2) {
                            tabEspace.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                else {
                    Log.e("FeedTab", "feed type " + tabInfoDataArrayList.get(0).getType());
                    tabBigImgA.setVisibility(View.GONE);
                    tabBigImgB.setVisibility(View.GONE);
                    tabBigImgC.setVisibility(View.GONE);
                    tabBigImgD.setVisibility(View.GONE);
                    toptab_texta.setVisibility(View.VISIBLE);
                    toptab_textb.setVisibility(View.VISIBLE);
                    toptab_textc.setVisibility(View.VISIBLE);
                    toptab_textd.setVisibility(View.VISIBLE);
                    tabImgB.setVisibility(View.VISIBLE);
                    tabImgA.setVisibility(View.VISIBLE);
                    tabImgC.setVisibility(View.VISIBLE);
                    tabImgD.setVisibility(View.VISIBLE);
                    if (tabInfoDataArrayList.get(0) != null)
                        toptab_texta.setText("" + tabInfoDataArrayList.get(0).getLabel());
                    if (tabInfoDataArrayList.size() > 1 && tabInfoDataArrayList.get(1) != null)
                        toptab_textb.setText("" + tabInfoDataArrayList.get(1).getLabel());
                    else {
                        myCourseButton.setVisibility(View.GONE);
                        tabBspace.setVisibility(View.GONE);
                    }
                    if (tabInfoDataArrayList.size() > 2 && tabInfoDataArrayList.get(2) != null) {
                        toptab_textc.setText("" + tabInfoDataArrayList.get(2).getLabel());
                    } else {
                        myChallengesButton.setVisibility(View.GONE);
                        tabCspace.setVisibility(View.GONE);
                    }
                    if (tabInfoDataArrayList.size() > 3 && tabInfoDataArrayList.get(3) != null) {
                        toptab_textd.setText("" + tabInfoDataArrayList.get(3).getLabel());
                        myNoticeBoardButton.setVisibility(View.VISIBLE);
                        tabDspace.setVisibility(View.VISIBLE);
                    } else {
                        myNoticeBoardButton.setVisibility(View.GONE);
                        tabDspace.setVisibility(View.GONE);
                    }
                    if (tabInfoDataArrayList.size() == 1) {
                        myFeedButton.setVisibility(View.GONE);
                    }
                    if (tabInfoDataArrayList.size() == 2) {
                        tabEspace.setVisibility(View.VISIBLE);
                    }

                    try {
                        if (tabInfoDataArrayList.get(0) != null && tabInfoDataArrayList.get(0).getImage() != null && !tabInfoDataArrayList.get(0).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(0).getImage()).into(tabImgA);
                        else
                            tabImgA.setVisibility(View.GONE);
                        if (tabInfoDataArrayList.get(1) != null && tabInfoDataArrayList.get(1).getImage() != null && !tabInfoDataArrayList.get(1).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(1).getImage()).into(tabImgB);
                        else
                            tabImgB.setVisibility(View.GONE);
                        if (tabInfoDataArrayList.get(2) != null && tabInfoDataArrayList.get(2).getImage() != null && !tabInfoDataArrayList.get(2).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(2).getImage()).into(tabImgC);
                        else
                            tabImgC.setVisibility(View.GONE);
                        if (tabInfoDataArrayList.get(3) != null && tabInfoDataArrayList.get(3).getImage() != null && !tabInfoDataArrayList.get(3).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(3).getImage()).into(tabImgD);
                        else
                            tabImgD.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }

                }*/


                if (defaultTab < tabInfoDataArrayList.size())
                    clickTab(defaultTab);

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void OpenLanguageScreenForMultilingual(long cplId, boolean allowBackPress, boolean isFeed) {
        Log.d(TAG, "OpenLanguageScreenForMultilingual: " + cplId);
        OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(true);
        Intent intent = new Intent(NewLandingActivity.this, LanguageSelectionActivity.class);
        intent.putExtra("CPL_ID", cplId);
        intent.putExtra("allowBackPress", allowBackPress);
        intent.putExtra("FEED", isFeed);
        startActivity(intent);
    }

    private void OpenLanguageScreen(long cplId, boolean allowBackPress, boolean isFeed) {
        Log.d(TAG, "OpenLanguageScreen: " + OustStaticVariableHandling.getInstance().isCplLanguageScreenOpen());
        if (!OustStaticVariableHandling.getInstance().isCplLanguageScreenOpen()) {
            OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(true);
            Intent intent = new Intent(NewLandingActivity.this, LanguageSelectionActivity.class);
            intent.putExtra("CPL_ID", cplId);
            intent.putExtra("allowBackPress", allowBackPress);
            intent.putExtra("FEED", isFeed);
            startActivity(intent);
            if (newFeedAdapter != null) {
                newFeedAdapter.enableButton();
            }
        }
    }

    private void openCityScreen(boolean allowBackPress, boolean isFeed) {
        Log.d(TAG, "openCityScreen: ");
        if (!OustStaticVariableHandling.getInstance().isCplCityScreenOpen()) {
            OustStaticVariableHandling.getInstance().setCplCityScreenOpen(true);
            Intent intent = new Intent(NewLandingActivity.this, CitySelection.class);
            intent.putExtra("allowBackPress", allowBackPress);
            intent.putExtra("FEED", isFeed);
            startActivity(intent);
            if (newFeedAdapter != null) {
                newFeedAdapter.enableButton();
            }
        }
    }

    private void setNewLayout() {
        try {
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                landingfrag_mainlayout.setVisibility(View.VISIBLE);
                mainlanding_toplayout.setVisibility(View.GONE);
                newmainlanding_toplayout.setVisibility(View.VISIBLE);
                newlanding_topimagelayout.setVisibility(View.GONE);
                newlanding_layout.setVisibility(View.VISIBLE);
                setSelectedSubTabColors(subtaba_text, subtabc_text, subtabb_text, subtaba_textlabel, subtabc_textlabel, subtabb_textlabel);
                setCatalogueLabelColor();
                if (!OustSdkTools.checkInternetStatus()) {
                    OustStaticVariableHandling.getInstance().setInternetConnectionOff(true);
                } else {
                    OustStaticVariableHandling.getInstance().setInternetConnectionOff(false);
                    getLatestTrendingData(1, 2);
                    getLatestTrendingData(1, 3);
                    getLatestTrendingData(2, 2);
                    getLatestTrendingData(2, 3);
                    getLatestTrendingData(3, 2);
                    getLatestTrendingData(3, 3);
                }
            } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 1) {
                landingfrag_mainlayout.setVisibility(View.VISIBLE);
                findViewById(R.id.newmainlanding_toplayouta).setVisibility(View.GONE);
                mainlanding_toplayout.setVisibility(View.GONE);
                newmainlanding_toplayout.setVisibility(View.VISIBLE);
                newlanding_topimagelayout.setVisibility(View.GONE);
                newlanding_layout.setVisibility(View.GONE);
            } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 0) {
                landingfrag_mainlayout.setVisibility(View.VISIBLE);
                newlanding_topimagelayout.setVisibility(View.VISIBLE);
                collapsing_toolbara.setVisibility(View.VISIBLE);
                mainlanding_toplayout.setVisibility(View.VISIBLE);
                newmainlanding_toplayout.setVisibility(View.GONE);
                newlanding_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCatalogueLabelColor() {
        try {
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                color = Color.parseColor(toolbarColorCode);
            }
            if (toptab_layout != null)
                toptab_layout.setBackgroundColor(color);
            catalogue_label.setTextColor(color);
            catalogue_labelline.setBackgroundColor(color);
        } catch (Exception e) {
        }
    }

    ImageView catalogIndicatorA, catalogIndicatorB, catalogIndicatorC, catalogIndicatorD;

    private void setCatalogImages(List<CatalogItemData> list) {
        try {
            if (list != null) {
                setCatalogueImageRatio();
                catalogue_lay.setVisibility(View.VISIBLE);
                catalogIndicatorA = findViewById(R.id.CatalogueUpdateIndicatora);
                catalogIndicatorB = findViewById(R.id.CatalogueUpdateIndicatorb);
                catalogIndicatorC = findViewById(R.id.CatalogueUpdateIndicatorc);
                catalogIndicatorD = findViewById(R.id.CatalogueUpdateIndicatord);
                if ((list.size() > 0)) {
                    //catalogue_labelline.setVisibility(View.VISIBLE);
                    catalogue_label.setVisibility(View.VISIBLE);
                    catelog_iamgea.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        catalogIndicatorA.setElevation(10f);
                        catalogIndicatorB.setElevation(10f);
                        catalogIndicatorC.setElevation(10f);
                        catalogIndicatorD.setElevation(10f);
                    }
                    catalogIndicatorA.bringToFront();
                    catalogIndicatorB.bringToFront();
                    catalogIndicatorC.bringToFront();
                    catalogIndicatorD.bringToFront();

                    if (list.get(0).getViewStatus().equalsIgnoreCase("NEW") || list.get(0).getViewStatus().equalsIgnoreCase("UPDATE")) {
                        catalogIndicatorA.setVisibility(View.VISIBLE);
                    } else {
                        catalogIndicatorA.setVisibility(View.GONE);
                    }

                    offlinemde_layout.setVisibility(View.GONE);
                    loadCatalogImage(list.get(0), catelog_iamgea);
                    if (list.size() > 1) {
                        catelog_iamgeb.setVisibility(View.VISIBLE);
                        if (list.get(1).getViewStatus().equalsIgnoreCase("NEW") || list.get(1).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            catalogIndicatorB.setVisibility(View.VISIBLE);
                        } else {
                            catalogIndicatorB.setVisibility(View.GONE);
                        }

                        loadCatalogImage(list.get(1), catelog_iamgeb);

                        if (list.size() > 2) {
                            catelog_iamgec.setVisibility(View.VISIBLE);
                            if (list.get(2).getViewStatus().equalsIgnoreCase("NEW") || list.get(2).getViewStatus().equalsIgnoreCase("UPDATE")) {
                                catalogIndicatorC.setVisibility(View.VISIBLE);
                            } else {
                                catalogIndicatorC.setVisibility(View.GONE);
                            }
                            loadCatalogImage(list.get(2), catelog_iamgec);
                            if (list.size() > 3) {
                                catelog_iamged.setVisibility(View.VISIBLE);
                                if (list.get(3).getViewStatus().equalsIgnoreCase("NEW") || list.get(3).getViewStatus().equalsIgnoreCase("UPDATE")) {
                                    catalogIndicatorD.setVisibility(View.VISIBLE);
                                } else {
                                    catalogIndicatorD.setVisibility(View.GONE);
                                }
                                loadCatalogImage(list.get(3), catelog_iamged);
                            } else {
                                catelog_iamged.setVisibility(View.GONE);
                                catalogIndicatorD.setVisibility(View.GONE);
                            }
                        } else {
                            catelog_iamged.setVisibility(View.GONE);
                            catelog_iamgec.setVisibility(View.GONE);
                            catalogIndicatorC.setVisibility(View.GONE);
                            catalogIndicatorD.setVisibility(View.GONE);
                        }
                    } else {
                        catelog_iamged.setVisibility(View.GONE);
                        catelog_iamgec.setVisibility(View.GONE);
                        catelog_iamgeb.setVisibility(View.GONE);
                        catalogIndicatorB.setVisibility(View.GONE);
                        catalogIndicatorC.setVisibility(View.GONE);
                        catalogIndicatorD.setVisibility(View.GONE);

                    }
                } else {
                    catelog_iamged.setVisibility(View.GONE);
                    catelog_iamgec.setVisibility(View.GONE);
                    catelog_iamgeb.setVisibility(View.GONE);
                    catelog_iamgea.setVisibility(View.GONE);
                    catalogIndicatorA.setVisibility(View.GONE);
                    catalogIndicatorB.setVisibility(View.GONE);
                    catalogIndicatorC.setVisibility(View.GONE);
                    catalogIndicatorD.setVisibility(View.GONE);
                }
                if (list != null && list.size() > 4) {
                    see_all_catalaog.setVisibility(View.VISIBLE);
                } else {
                    see_all_catalaog.setVisibility(View.GONE);
                }
            } else {
                if (OustStaticVariableHandling.getInstance().isInternetConnectionOff()) {
                    catelog_iamgea.setVisibility(View.GONE);
                    catelog_iamgeb.setVisibility(View.GONE);
                    catelog_iamgec.setVisibility(View.GONE);
                    catelog_iamged.setVisibility(View.GONE);

                    //catalogue_labelline.setVisibility(View.VISIBLE);
                    catalogue_label.setVisibility(View.VISIBLE);
                    offlinemde_layout.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.offline_catalog));
                    offlinemde_layout.setVisibility(View.VISIBLE);
                } else {
                    catalogue_labelline.setVisibility(View.GONE);
                    catalogue_label.setVisibility(View.GONE);
                    catelog_iamgea.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadCatalogImage(CatalogItemData catalogItemData, ImageView catalog_image) {
        try {
            if (catalogItemData.getBanner() != null && !catalogItemData.getBanner().isEmpty() && !catalogItemData.getBanner().equalsIgnoreCase("null")) {
                Picasso.get().load(catalogItemData.getBanner()).into(catalog_image);
            } else if (catalogItemData.getIcon() != null && !catalogItemData.getIcon().isEmpty() && !catalogItemData.getBanner().equalsIgnoreCase("null")) {
                Picasso.get().load(catalogItemData.getIcon()).into(catalog_image);
            } else if (catalogItemData.getThumbnail() != null && !catalogItemData.getThumbnail().isEmpty() && !catalogItemData.getBanner().equalsIgnoreCase("null")) {
                Picasso.get().load(catalogItemData.getThumbnail()).into(catalog_image);
            } else {
                Glide.with(this).load(R.drawable.ic_catalogue_illustration).into(catalog_image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCatalogueImageRatio() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int imageLayoutHeight = (int) ((scrWidth / 2) * 0.52);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomcatalogue_layout.getLayoutParams();
            params.height = imageLayoutHeight;
            bottomcatalogue_layout.setLayoutParams(params);
            bottomcatalogue_layouta.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e); //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    private void setTabTextandColor() {
        try {
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
                color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }
            Drawable d = getResources().getDrawable(
                    R.drawable.roundcorner_red);
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            tabImgAIndicator.setBackgroundDrawable(d);
            tabImgBIndicator.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
            tabImgCIndicator.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));

            if (tabInfoDataArrayList != null && tabInfoDataArrayList.size() > 0) {
                toptab_texta.setText("" + tabInfoDataArrayList.get(0).getLabel());
                toptab_textb.setText("" + tabInfoDataArrayList.get(1).getLabel());
                toptab_textc.setText("" + tabInfoDataArrayList.get(2).getLabel());
                if (tabInfoDataArrayList.get(0).getType().equals("IMAGE")) {
                    maintab_image_layout.setVisibility(View.VISIBLE);
                    setTopTabImageLAyout(tabimage_subLayout, 10);
                    maintab_text_layout.setVisibility(View.GONE);
                    try {
                        if (tabInfoDataArrayList.get(0) != null && tabInfoDataArrayList.get(0).getImage() != null && !tabInfoDataArrayList.get(0).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(0).getImage()).into(tabImgA);
                        if (tabInfoDataArrayList.get(1) != null && tabInfoDataArrayList.get(1).getImage() != null && !tabInfoDataArrayList.get(1).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(1).getImage()).into(tabImgB);
                        if (tabInfoDataArrayList.get(2) != null && tabInfoDataArrayList.get(2).getImage() != null && !tabInfoDataArrayList.get(2).getImage().isEmpty())
                            Picasso.get().load(tabInfoDataArrayList.get(2).getImage()).into(tabImgC);
                    } catch (Exception e) {
                    }
                } else {
                    maintab_text_layout.setVisibility(View.VISIBLE);
                    maintab_image_layout.setVisibility(View.GONE);
                }
            }
            if (moduleDataArrayList != null && moduleDataArrayList.size() > 0) {
                subtaba_text.setText("" + moduleDataArrayList.get(0));
                subtabb_text.setText("" + moduleDataArrayList.get(1));
                subtabc_text.setText("" + moduleDataArrayList.get(2));
                subtab_layout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setProfile() {
        try {
            if (OustSdkTools.tempProfile != null) {
                usermain_profile.setImageBitmap(OustSdkTools.tempProfile);
                user_avatar.setImageBitmap(OustSdkTools.tempProfile);
            } else {
                if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                    OustPreferences.save("avatarUrl", activeUser.getAvatar());
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(activeUser.getAvatar()).error(R.drawable.ic_user_avatar).into(usermain_profile);
                        Picasso.get().load(activeUser.getAvatar()).error(R.drawable.ic_user_avatar).into(user_avatar);
                    } else {
                        Picasso.get().load(activeUser.getAvatar()).error(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(usermain_profile);
                        Picasso.get().load(activeUser.getAvatar()).error(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(user_avatar);
                    }
                } else {
                    Log.e(TAG, "profile image display name " + activeUser.getUserDisplayName());
                    usermain_profile.setVisibility(View.GONE);
                    userprofileImageText.setVisibility(View.VISIBLE);
                    userprofileImageText.setText(temporaryProfileImageDisplayName.toUpperCase());
                    user_avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_avatar));
                    OustSdkTools.setGroupImage(userprofileImageText, userDisplayName);
                    Log.e(TAG, "profile image display name " + userDisplayName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setHostAppIcon() {
        try {
            //action_gotohostapp
            String redirectIcon = OustPreferences.get("redirectIcon");
            if ((redirectIcon != null) && (!redirectIcon.isEmpty())) {
                Log.e(TAG, "action_gotohostapp:" + action_gotohostapp);
                if (action_gotohostapp != null) {
                    action_gotohostapp.setVisible(true);
                    target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Drawable d = new BitmapDrawable(getResources(), bitmap);
                            action_gotohostapp.setVisible(true);
                            action_gotohostapp.setIcon(d);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            action_gotohostapp.setVisible(false);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            action_gotohostapp.setVisible(false);
                        }
                    };
                    Log.e(TAG, "inside setHostIcon: icon image:" + this.redirectIcon);
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(this.redirectIcon).into(target);
                    } else {
                        Picasso.get().load(this.redirectIcon).networkPolicy(NetworkPolicy.OFFLINE).into(target);
                    }
                }
            } else {
                if (action_gotohostapp != null) {
                    action_gotohostapp.setVisible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e(TAG, "Caught exception in setHostAppIcon()", e);
        }
    }

    private void getOcFromFirebase() {
        try {
            String message = "users/" + activeUser.getStudentKey();
            ValueEventListener ocListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        if (null != snapshot.getValue()) {
                            Object o1 = snapshot.getValue();
                            String oc = "";
                            if (o1.getClass().equals(String.class)) {
                                oc = snapshot.getValue().toString();
                            } else if (o1.getClass().equals(Long.class)) {
                                oc = ("" + ((long) snapshot.getValue()));
                            }
                            activeUser.setAvailableOCCount(oc);
                            OustAppState.getInstance().getActiveUser().setAvailableOCCount(oc);
                            setMyTotalOcText();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message + "/oc").keepSynced(true);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ocListener, message + "/oc"));
            OustFirebaseTools.getRootRef().child(message + "/oc").addValueEventListener(ocListener);

            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ocListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void hideBanner() {
        try {
            collapsing_toolbar.setExpanded(false, true);
            wjalert_banner.setVisibility(View.GONE);
            showingAlertBanner = false;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showBanner() {
        try {
            collapsing_toolbar.setActivated(true);
            collapsing_toolbar.setExpanded(true, true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showPendingCoursesUI() {
        try {
            showAllcoursetextlayout();
            sethilightedColor(user_coursependingtext);
            sethilightedColor(user_coursependinglabel);
            sethilightedColor(user_coursependingoctext);
            user_coursecompletedlabel.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            user_coursecompletedtext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            user_coursecompletedoctext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
        } catch (Exception e) {
        }
    }

    @Override
    public void showAllCourseUI() {
        try {
            hideAllcoursetextlayout();
            user_coursecompletedlabel.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            user_coursecompletedtext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            user_coursependinglabel.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            user_coursependingtext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            user_coursecompletedoctext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            user_coursependingoctext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
        } catch (Exception e) {
        }
    }

    @Override
    public void setProgressText(List<AssessmentFirebaseClass> ofbModuleList) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setOcText() {
        setMyTotalProgressText();
    }

    private void setMyTotalOcText() {
        try {
            mycointext.setText("00");
            usercointext.setText("0/0");
            user_coursependingoctext.setText(String.format("0 %s", getResources().getString(R.string.credits_text)));
            user_coursecompletedoctext.setText(String.format("0 %s", getResources().getString(R.string.credits_text)));
            if (activeUser != null && activeUser.getAvailableOCCount() != null) {
                mycointext.setText("" + activeUser.getAvailableOCCount());
            }
            int myTotalOc = 0;
            int totalOc = 0;
            if (myDeskData != null) {
                for (String key : myDeskData.keySet()) {
                    if (myDeskData.get(key).getCompletionPercentage() == 100) {
                        myTotalOc += myDeskData.get(key).getUserOc();
                    }
                    totalOc += myDeskData.get(key).getOc();
                }
            }
            // mycointext.setText("" + myTotalOc);
            landingnewprogress_bar.invalidate();
            if (myTotalOc > totalOc) {
                totalOc = myTotalOc;
                usercointext.setText(" " + totalOc + "/" + totalOc + " ");
                user_coursependingoctext.setText("0 " + getResources().getString(R.string.credits_text));
                user_coursecompletedoctext.setText("(" + totalOc + " " + getResources().getString(R.string.credits) + ")");
                coins_text.setText(String.valueOf(totalOc));
                landingnewprogress_bar.setMax(totalOc);
                landingnewprogress_bar.setProgress(totalOc);
            } else {
                String myOc, totalOustCoin;
                if (myTotalOc > 100000) {
                    myOc = 10000 + "+";
                } else {
                    myOc = myTotalOc + "";
                }
                if (totalOc > 10000) {
                    totalOustCoin = 10000 + "+";
                } else {
                    totalOustCoin = totalOc + "";
                }

                //usercointext.setText("1500000000"+"/" + "1500000000 ");
                usercointext.setText(" " + myTotalOc + "/" + totalOc + " ");
                user_coursecompletedoctext.setText("(" + myTotalOc + " " + getResources().getString(R.string.credits) + ")");
                coins_text.setText(String.valueOf(myTotalOc));
                landingnewprogress_bar.setProgress(0);
                landingnewprogress_bar.setMax(totalOc);
                landingnewprogress_bar.setProgress(myTotalOc);
                user_coursependingoctext.setText("(" + (totalOc - myTotalOc) + " " + getResources().getString(R.string.credits) + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int allCourseSize;

    private void setMyTotalProgressText() {
        try {
            if ((myDeskData != null)) {
                int pendingCourseSize = 0;
                int pendingAssessmentSize = 0;
                allCourseSize = myDeskData.size();
                //Log.d(TAG, "setMyTotalProgressText: allcourse size:"+allCourseSize);
                int completeCourseSize = 0;
                if (allCourseSize <= 4) {
                    showAllcoursetextlayout.setVisibility(View.GONE);
                }
                for (String key : myDeskData.keySet()) {
                    if (myDeskData.get(key).getCompletionPercentage() > 99) {
                        completeCourseSize++;
                        //Log.d(TAG, "setMyTotalProgressText: completeCurseText:"+completeCourseSize);
                    }
                }
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                    pendingCourseSize = allCourseSize - completeCourseSize;
                    OustPreferences.saveintVar("coursePendingCount", pendingCourseSize);
                } else {
                    int completedAssessment = 0;
                    pendingCourseSize = allCourseSize - completeCourseSize;
                    allCourseSize += myAssessmentData.size();
                    for (String key : myAssessmentData.keySet()) {
                        if (myAssessmentData.get(key).getCompletionPercentage() > 99) {
                            completeCourseSize++;
                            completedAssessment++;
                        }
                    }
                    //save data for offline notification
                    pendingAssessmentSize = (allCourseSize - completeCourseSize) - pendingCourseSize;
                    OustPreferences.saveintVar("coursePendingCount", pendingCourseSize);
                    OustPreferences.saveintVar("assessmentPendingCount", pendingAssessmentSize);

                }
                if (completeCourseSize > allCourseSize) {
                    completeCourseSize = allCourseSize;
                }
                float f1 = (((float) (completeCourseSize)) / ((float) (allCourseSize))) * 100;
                learningoustcoin_progress.setMax((10000));
                ObjectAnimator animation = ObjectAnimator.ofInt(learningoustcoin_progress, "progress", (((int) f1) * 100));
                animation.setDuration(1000);
                animation.start();
                coursecomplete_presentagetext.setText(((int) f1) + "%");
                user_coursecompletedtext.setText(("" + completeCourseSize));
                user_coursependingtext.setText("" + (allCourseSize - completeCourseSize));
                pending_count.setText(String.valueOf((allCourseSize - completeCourseSize)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setToolBarColor() {
        try {
            setSupportActionBar(toolbar);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView = toolbar.findViewById(R.id.title);
            if ((OustPreferences.get("companydisplayName") != null) && (!OustPreferences.get("companydisplayName").isEmpty())) {
                titleTextView.setText(OustPreferences.get("companydisplayName"));
            } else {
                titleTextView.setText(getResources().getString(R.string.landing_heading));
            }
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
                //setting progress bar color
                int color = OustSdkTools.getColorBack(R.color.lgreen);
                if (OustPreferences.get("toolbarColorCode") != null) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                newlanding_loader_progressbar.setIndeterminateDrawable(ld);
                OustSdkTools.setProgressbar(newlanding_loader_progressbar);
            }
        } catch (Exception e) {
            int n1 = 0;
            int n2 = n1 + 5;
        }
    }

    //====================================================================================================
//top banner related methodes
    private BannerClass topBanner;

    private void loadAllBanners() {
        String message = "landingPage/" + activeUser.getStudentKey() + "/banner/bannerData";
        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        final Map<String, Object> bannerSubMap = (Map<String, Object>) dataSnapshot.getValue();
                        try {
                            topBanner = new BannerClass();
                            if (bannerSubMap.get("assessmentId") != null) {
                                topBanner.setAssessmentId((long) bannerSubMap.get("assessmentId"));
                            }
                            if (bannerSubMap.get("courseId") != null) {
                                topBanner.setCourseId((long) bannerSubMap.get("courseId"));
                            }
                            if (bannerSubMap.get("contestId") != null) {
                                topBanner.setContestId((String) bannerSubMap.get("contestId"));
                            }
                            if (bannerSubMap.get("bannerType") != null) {
                                try {
                                    topBanner.setBannerType(Bannertype.valueOf((String) bannerSubMap.get("bannerType")));
                                } catch (Exception e) {
                                }
                            }
                            if (bannerSubMap.get("bannerImg") != null) {
                                topBanner.setBannerImg((String) bannerSubMap.get("bannerImg"));
                            }
                            if (bannerSubMap.get("link") != null) {
                                topBanner.setLink((String) bannerSubMap.get("link"));
                            }
                            if (bannerSubMap.get("weightage") != null) {
                                topBanner.setWeightage((long) bannerSubMap.get("weightage"));
                            }
                            boolean isValidBanner = false;
                            if (bannerSubMap.get("hidden") != null) {
                                isValidBanner = ((Boolean) bannerSubMap.get("hidden"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        setTopBanner();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
        newsfeedRef.keepSynced(true);
        newsfeedRef.addValueEventListener(newsfeedListListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
    }

    private void setTopBanner() {
        try {
            if ((topBanner != null) && (topBanner.getBannerImg() != null) && (!topBanner.getBannerImg().isEmpty())) {
                setBannerImage(topbanner_imageview, topBanner.getBannerImg());
            } else {
                OustSdkTools.setImage(topbanner_imageview, getResources().getString(R.string.challenge_and_win));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForBannerType() {
        try {
            Bannertype bannertype;
            if ((topBanner.getBannerType() != null)) {
                bannertype = topBanner.getBannerType();
                if (bannertype == Bannertype.ASSESSMENT) {
                    if (topBanner.getAssessmentId() > 0) {
                        OustSdkTools.oustTouchEffect(topbanner_imageview, 100);
                        gotoAssessment("" + topBanner.getAssessmentId());
                    }
                } else if (bannertype == Bannertype.COURSE) {
                    if (topBanner.getCourseId() > 0) {
                        gotoCoursePage("" + topBanner.getCourseId());
                        OustSdkTools.oustTouchEffect(topbanner_imageview, 100);
                    }
                } else if (bannertype == Bannertype.EXTERNAL) {
                    if ((topBanner.getLink() != null) && (!topBanner.getLink().isEmpty())) {
                        OustSdkTools.oustTouchEffect(topbanner_imageview, 100);
                        showWebView(topBanner.getLink());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoAssessment(String assessmentId) {
        try {
            boolean isAssessmentValid = false;
            setGame(activeUser);
            Intent intent;
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                intent = new Intent(NewLandingActivity.this, AssessmentDetailScreen.class);
            } else {
                intent = new Intent(NewLandingActivity.this, AssessmentPlayActivity.class);
            }
            //Intent intent = new Intent(NewLandingActivity.this, AssessmentDetailScreen.class);
            Gson gson = new Gson();
            if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null) && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
                for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                    if ((assessmentId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                        OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                        isAssessmentValid = true;
                    }
                }
                if (!isAssessmentValid) {
                    OustSdkTools.showToast(getResources().getString(R.string.assessment_no_longer));
                    return;
                }
            } else {
                intent.putExtra("assessmentId", assessmentId);
            }
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoCoursePage(String courseId) {
        try {
            Intent intent = new Intent(NewLandingActivity.this, NewLearningMapActivity.class);
            intent.putExtra("learningId", courseId);
            intent.putExtra("REGULAR_MODE", true);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //======================================================================================================
//show webview related methodes
    private boolean isWebviewOpen = false;

    private void showWebView(String url) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            OustSdkTools.showProgressBar();
            isWebviewOpen = true;
            showFirstWebViewAnim();
            event_webview_layout.setVisibility(View.VISIBLE);
            mywebView.setWebViewClient(new MyWebViewClient());
            mywebView.getSettings().setJavaScriptEnabled(true);
            mywebView.loadUrl(url);
            mywebView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress > 97) {
                        OustSdkTools.hideProgressbar();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showFirstWebViewAnim() {
        try {
            event_webview_layout.setVisibility(View.VISIBLE);
            event_webview_layout.bringToFront();
            Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(),
                    R.anim.event_animmovein);
            Animation event_animmoveout = AnimationUtils.loadAnimation(OustSdkApplication.getContext(),
                    R.anim.event_animmoveout);
            landingfrag_mainlayout.startAnimation(event_animmoveout);
            event_webview_layout.startAnimation(event_animmovein);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showfirstbackWebViewAnim() {
        try {
            OustSdkTools.hideProgressbar();
            landingfrag_mainlayout.bringToFront();
            Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackin);
            Animation event_animmoveout = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackout);
            landingfrag_mainlayout.startAnimation(event_animmovein);
            event_webview_layout.startAnimation(event_animmoveout);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void getNextData(String type) {
        try {
            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                if (ll_loading != null) {
                    if (newlandinghorizontal_recyclerviewa != null && newlandinghorizontal_recyclerviewa.getVisibility() == View.VISIBLE)
                        ll_loading.setVisibility(View.VISIBLE);
                }
                int totalCount = OustAppState.getInstance().getMyDeskList().size();
                if ((localModuleCount + 10) <= totalCount) {
                    localModuleCount = localModuleCount + 10;
                } else if ((localModuleCount) <= totalCount) {
                    localModuleCount = totalCount;
                }
                setCollectiveListener();
            } else {
                if (type.equalsIgnoreCase("Alerts")) {
                    setNewFeedListener();
                    if (!OustDataHandler.getInstance().isAllFeedsLoaded()) {
                        if (newsfeed_recyclerview.getVisibility() == View.VISIBLE) {
                            if (ll_loading != null) {
                                ll_loading.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (ll_loading != null) {
                            ll_loading.setVisibility(View.GONE);
                        }
                    }
                } else if (type.equals("COURSE") || type.equals("COLLECTION") || COURSE_ASSESSMENT_TAB_FOUND) {
                    int totalCount = OustAppState.getInstance().getMyDeskList().size();
                    if ((localModuleCount + 10) <= totalCount) {
                        localModuleCount = localModuleCount + 10;
                    } else if ((localModuleCount) <= totalCount) {
                        localModuleCount = totalCount;
                    }
                    setCollectiveListener();
                    if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                        if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                            if (newCourse_recyclerview.getVisibility() == View.VISIBLE) {
                                if (ll_loading != null)
                                    ll_loading.setVisibility(View.VISIBLE);
                                else
                                    ll_loading.setVisibility(View.GONE);
                            } else {
                                ll_loading.setVisibility(View.GONE);
                            }
                        } else {
                            ll_loading.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                        int totalCount = myAssessmentData.size();
                        if (totalAssessmentCount != totalCount) {
                            if ((localAssessmentCount + 10) <= totalCount) {
                                localAssessmentCount = localAssessmentCount + 10;
                            } else if ((localAssessmentCount) <= totalCount) {
                                localAssessmentCount = totalCount;
                            }
                            setAssessmentListener();
                            if (!OustDataHandler.getInstance().isAllAssessmentLoaded()) {
                                if (newChallenges_recyclerview.getVisibility() == View.VISIBLE) {
                                    if (ll_loading != null)
                                        ll_loading.setVisibility(View.VISIBLE);
                                    else
                                        ll_loading.setVisibility(View.GONE);
                                } else {
                                    ll_loading.setVisibility(View.GONE);
                                }
                            } else {
                                ll_loading.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        int totalCount = myAssessmentData.size();
                        if (totalAssessmentCount != totalCount) {
                            if ((localAssessmentCount + 10) <= totalCount) {
                                localAssessmentCount = localAssessmentCount + 10;
                            } else if ((localAssessmentCount) <= totalCount) {
                                localAssessmentCount = totalCount;
                            }
                            setAssessmentListener();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void filterCategoryFeeds(int pos, int type, String label) {
        try {
            filterType = pos;
            if (newFeedVerticalFilterAdapter != null)
                newFeedVerticalFilterAdapter.updateAdapter(pos, "");
            newsfeed_recyclerview.removeAllViews();
            if (type == 1) {
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(pos, "");
                filter_category_ll.setVisibility(View.GONE);

            } else if (type == 2 && pos == 31) {
                filterType = 31;
                for (int j = 0; j < filterCategories.size(); j++) {
                    if (filterCategories.get(j).getCategoryType() == 31 && filterCategories.get(j).getCategoryName().equals(label))
                        filterTag = filterCategories.get(j).getCategoryName();
                }
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(filterType, label);
                if (newFeedVerticalFilterAdapter != null)
                    newFeedVerticalFilterAdapter.updateAdapter(filterType, label);

            } else if (type == 1 && pos == 31) {
                filterType = 31;
                for (int j = 0; j < filterCategories.size(); j++) {
                    if (filterCategories.get(j).getCategoryType() == 31 && filterCategories.get(j).getCategoryName().equals(label))
                        filterTag = filterCategories.get(j).getCategoryName();
                }
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(filterType, label);
                if (newFeedVerticalFilterAdapter != null)
                    newFeedVerticalFilterAdapter.updateAdapter(filterType, label);

            } else if (type == 2) {
                filter_category_ll.setVisibility(View.GONE);
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(filterType, "");

            } else if (type == 3) {
                filterType = 0;
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(filterType, "");
                if (newFeedVerticalFilterAdapter != null)
                    newFeedVerticalFilterAdapter.updateAdapter(filterType, "");

            } else if (type == 5) {
                if (newFeedFilterAdapter != null)
                    newFeedFilterAdapter.updateAdapter(filterType, "");
                if (newFeedVerticalFilterAdapter != null)
                    newFeedVerticalFilterAdapter.updateAdapter(filterType, "");

            }
            if (filterType == 0) {
                OustPreferences.saveintVar("filterTypeFeed", 0);
                no_data_text.setVisibility(View.GONE);
                newsfeed_recyclerview.setVisibility(View.VISIBLE);
                newsfeed_recyclerview.removeAllViews();
                newFeedAdapter.notifyFeedChange(null, false);

            } else {
                Log.e(TAG, "updateShareCount: feedArrayList 4-> " + OustStaticVariableHandling.getInstance().getFeeds().size());
//                createList(OustStaticVariableHandling.getInstance().getFeeds(), false, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void launchCPL(String s) {
        openCPLScreen();
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    //===================================================================================================
//play mode related methodes
    @Override
    public void onMainRowClick(int posotion) {
        try {
            if (ofbModuleList != null) {
                initModuleView(ofbModuleList.get(posotion));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onIconClick(int position) {
    }

    private void getPlayGameData() {
        try {
            Log.e("Landing Page", "isPlayMode" + OustPreferences.getAppInstallVariable("isPlayModeEnabled"));
            if ((OustPreferences.getAppInstallVariable("isPlayModeEnabled"))) {
                Log.e("Landing Page", "isPlayMode true, fething data from firebase");
                fetchMyDeskFromFirebase();
                detectStateChanges();
                learn_play_modelayoutbacka.setVisibility(View.VISIBLE);
                learn_play_modelayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("Landing Page", "isPlayMode exception" + OustPreferences.getAppInstallVariable("isPlayModeEnabled"));
        }
    }

    private boolean isLearnMode = true;

    private void detectStateChanges() {
        learningmain_layout.bringToFront();
        isLearnMode = true;
        playmode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showPlayLayout();
                } else {
                    showLearnLayout();
                }
            }
        });
    }

    private void showPlayLayout() {
        try {
            if (isLearnMode) {
                isLearnMode = false;
                playmain_layout.bringToFront();
                Animation event_animmovein = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.event_animmovein);
                Animation event_animmoveout = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.event_animmoveout);
                learningmain_layout.startAnimation(event_animmoveout);
                playmain_layout.startAnimation(event_animmovein);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLearnLayout() {
        try {
            if (!isLearnMode) {
                isLearnMode = true;
                learningmain_layout.bringToFront();
                Animation event_animmovein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.event_animbackin);
                Animation event_animmoveout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.event_animbackout);
                learningmain_layout.startAnimation(event_animmovein);
                playmain_layout.startAnimation(event_animmoveout);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void fetchMyDeskFromFirebase() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/module";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            List<Object> moduleObjectList = new ArrayList<>();
                            Map<String, Object> moduleMainMap = new HashMap<>();
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(HashMap.class)) {
                                final int[] index = new int[]{0};
                                moduleMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                ofbModuleList = new ArrayList<>();
                                if (moduleMainMap != null) {
                                    for (String moduleKey : moduleMainMap.keySet()) {
                                        if ((moduleMainMap.get(moduleKey) != null)) {
                                            final Map<String, Object> moduleSubMap = (Map<String, Object>) moduleMainMap.get(moduleKey);
                                            if (moduleSubMap != null) {
                                                final OFBModule ofbModule = new OFBModule();
                                                if (moduleSubMap.get("elementIdStr") != null) {
                                                    ofbModule.setModuleId((String) moduleSubMap.get("elementIdStr"));
                                                }
                                                if (moduleSubMap.get("addedOn") != null) {
                                                    ofbModule.setAddedOn((String) moduleSubMap.get("addedOn"));
                                                }
                                                if (moduleSubMap.get("expertise") != null) {
                                                    ofbModule.setExpertise((String) moduleSubMap.get("expertise"));
                                                }
                                                if (moduleSubMap.get("userCompletionPercentage") != null) {
                                                    Object o3 = moduleSubMap.get("userCompletionPercentage");
                                                    if (o3.getClass().equals(Long.class)) {
                                                        long s3 = (long) o3;
                                                        ofbModule.setPercentageComp(("" + o3));
                                                    } else if (o3.getClass().equals(String.class)) {
                                                        ofbModule.setPercentageComp((String) o3);
                                                    } else if (o3.getClass().equals(Double.class)) {
                                                        Double s3 = (Double) o3;
                                                        long l = (new Double(s3)).longValue();
                                                        ofbModule.setPercentageComp(("" + l));
                                                    }
                                                }
                                                if (moduleSubMap.get("weightage") != null) {
                                                    ofbModule.setWeight((long) moduleSubMap.get("weightage"));
                                                }
                                                ofbModuleList.add(ofbModule);
                                                String msg1 = ("system/modules/" + moduleKey);
                                                ValueEventListener eventListener = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot student) {
                                                        try {
                                                            index[0]++;
                                                            if (null != student.getValue()) {
                                                                final Map<String, Object> moduleMap = (Map<String, Object>) student.getValue();
                                                                String currentId = "";
                                                                int moduleIndex = 0;
                                                                if (moduleMap.get("moduleId") != null) {
                                                                    currentId = (String) moduleMap.get("moduleId");
                                                                }
                                                                for (int n = 0; n < ofbModuleList.size(); n++) {
                                                                    if (ofbModuleList.get(n).getModuleId().equalsIgnoreCase(currentId)) {
                                                                        moduleIndex = n;
                                                                    }
                                                                }
                                                                try {
                                                                    if (null != moduleMap) {
                                                                        if (moduleMap.get("bgImage") != null) {
                                                                            ofbModuleList.get(moduleIndex).setBgImage((String) moduleMap.get("bgImage"));
                                                                        }
                                                                        if (moduleMap.get("grade") != null) {
                                                                            String gradeA = (String) moduleMap.get("grade");
                                                                            if ((gradeA != null) && (!gradeA.isEmpty())) {
                                                                                ofbModuleList.get(moduleIndex).setGrade(gradeA);
                                                                            }
                                                                        }
                                                                        if (moduleMap.get("moduleName") != null) {
                                                                            ofbModuleList.get(moduleIndex).setModuleName((String) moduleMap.get("moduleName"));
                                                                        }
                                                                        if (moduleMap.get("vendorName") != null) {
                                                                            ofbModuleList.get(moduleIndex).setVendorName((String) moduleMap.get("vendorName"));
                                                                        }
                                                                        if (moduleMap.get("topic") != null) {
                                                                            ofbModuleList.get(moduleIndex).setTopic((String) moduleMap.get("topic"));
                                                                        }
                                                                        if (moduleMap.get("teacherName") != null) {
                                                                            ofbModuleList.get(moduleIndex).setTeacherName((String) moduleMap.get("teacherName"));
                                                                        }
                                                                        if (moduleMap.get("subject") != null) {
                                                                            ofbModuleList.get(moduleIndex).setSubject((String) moduleMap.get("subject"));
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                }
                                                            }
                                                            if (index[0] >= ofbModuleList.size()) {
                                                                Collections.sort(ofbModuleList, moduleSortWeight);
                                                                createMyMOduleList();
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                    }
                                                };
                                                OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                                                OustFirebaseTools.getRootRef().child(msg1).addListenerForSingleValueEvent(eventListener);
                                            }
                                        }
                                    }
                                }
                            } else if (o1.getClass().equals(ArrayList.class)) {
                                final int[] index = new int[]{0};
                                moduleObjectList = (List<Object>) dataSnapshot.getValue();
                                ofbModuleList = new ArrayList<>();
                                if (moduleObjectList != null) {
                                    for (int moduleKeyNo = 0; moduleKeyNo < moduleObjectList.size(); moduleKeyNo++) {
                                        if ((moduleObjectList.get(moduleKeyNo) != null)) {
                                            final Map<String, Object> moduleSubMap = (Map<String, Object>) moduleObjectList.get(moduleKeyNo);
                                            if (moduleSubMap != null) {
                                                final OFBModule ofbModule = new OFBModule();
                                                if (moduleSubMap.get("elementIdStr") != null) {
                                                    ofbModule.setModuleId((String) moduleSubMap.get("elementIdStr"));
                                                }
                                                if (moduleSubMap.get("addedOn") != null) {
                                                    ofbModule.setAddedOn((String) moduleSubMap.get("addedOn"));
                                                }
                                                if (moduleSubMap.get("expertise") != null) {
                                                    ofbModule.setExpertise((String) moduleSubMap.get("expertise"));
                                                }
                                                if (moduleSubMap.get("userCompletionPercentage") != null) {
                                                    Object o3 = moduleSubMap.get("userCompletionPercentage");
                                                    if (o3.getClass().equals(Long.class)) {
                                                        long s3 = (long) o3;
                                                        ofbModule.setPercentageComp(("" + o3));
                                                    } else if (o3.getClass().equals(String.class)) {
                                                        ofbModule.setPercentageComp((String) o3);
                                                    } else if (o3.getClass().equals(Double.class)) {
                                                        Double s3 = (Double) o3;
                                                        long l = (new Double(s3)).longValue();
                                                        ofbModule.setPercentageComp(("" + l));
                                                    }
                                                }
                                                if (moduleSubMap.get("weightage") != null) {
                                                    ofbModule.setWeight((long) moduleSubMap.get("weightage"));
                                                }
                                                ofbModuleList.add(ofbModule);
                                                String msg1 = ("system/modules/" + moduleKeyNo);
                                                ValueEventListener eventListener = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot student) {
                                                        try {
                                                            index[0]++;
                                                            if (null != student.getValue()) {
                                                                final Map<String, Object> moduleMap = (Map<String, Object>) student.getValue();
                                                                String currentId = "";
                                                                int moduleIndex = 0;
                                                                if (moduleMap.get("moduleId") != null) {
                                                                    currentId = (String) moduleMap.get("moduleId");
                                                                }
                                                                for (int n = 0; n < ofbModuleList.size(); n++) {
                                                                    if (ofbModuleList.get(n).getModuleId().equalsIgnoreCase(currentId)) {
                                                                        moduleIndex = n;
                                                                    }
                                                                }
                                                                try {
                                                                    if (null != moduleMap) {
                                                                        if (moduleMap.get("bgImage") != null) {
                                                                            ofbModuleList.get(moduleIndex).setBgImage((String) moduleMap.get("bgImage"));
                                                                        }
                                                                        if (moduleMap.get("grade") != null) {
                                                                            String gradeA = (String) moduleMap.get("grade");
                                                                            if ((gradeA != null) && (!gradeA.isEmpty())) {
                                                                                ofbModuleList.get(moduleIndex).setGrade(gradeA);
                                                                            }

                                                                        }
                                                                        if (moduleMap.get("moduleName") != null) {
                                                                            ofbModuleList.get(moduleIndex).setModuleName((String) moduleMap.get("moduleName"));
                                                                        }
                                                                        if (moduleMap.get("vendorName") != null) {
                                                                            ofbModuleList.get(moduleIndex).setVendorName((String) moduleMap.get("vendorName"));
                                                                        }
                                                                        if (moduleMap.get("topic") != null) {
                                                                            ofbModuleList.get(moduleIndex).setTopic((String) moduleMap.get("topic"));
                                                                        }
                                                                        if (moduleMap.get("teacherName") != null) {
                                                                            ofbModuleList.get(moduleIndex).setTeacherName((String) moduleMap.get("teacherName"));
                                                                        }
                                                                        if (moduleMap.get("subject") != null) {
                                                                            ofbModuleList.get(moduleIndex).setSubject((String) moduleMap.get("subject"));
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                }
                                                            }
                                                            if (index[0] >= ofbModuleList.size()) {
                                                                Collections.sort(ofbModuleList, moduleSortWeight);
                                                                createMyMOduleList();
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {

                                                    }
                                                };
                                                OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                                                OustFirebaseTools.getRootRef().child(msg1).addListenerForSingleValueEvent(eventListener);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            createMyMOduleList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(myassessmentListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<OFBModule> moduleSortWeight = new Comparator<OFBModule>() {
        public int compare(OFBModule s1, OFBModule s2) {
            return (int) s2.getWeight() - (int) s1.getWeight();
        }
    };

    public void createMyMOduleList() {
        try {
            if ((ofbModuleList != null) && (ofbModuleList.size() > 0)) {
                nomodule_text.setVisibility(View.GONE);
                addModuleGrid.setVisibility(View.VISIBLE);
                if (newModuleAdaptor == null) {
                    newModuleAdaptor = new NewModuleAdapter(ofbModuleList);
                    GridLayoutManager mLayoutManager = new GridLayoutManager(NewLandingActivity.this, 2);
                    newModuleAdaptor.setOustCallBack(NewLandingActivity.this);
                    addModuleGrid.setLayoutManager(mLayoutManager);
                    addModuleGrid.setItemAnimator(new DefaultItemAnimator());
                    addModuleGrid.setAdapter(newModuleAdaptor);
                } else {
                    newModuleAdaptor.dataChanged(ofbModuleList);
                }
            } else {
                addModuleGrid.setVisibility(View.GONE);
                nomodule_text.setVisibility(View.VISIBLE);
                nomodule_text.setText(getResources().getString(R.string.no_module_yet));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void initModuleView(final OFBModule ofbModule) {
        try {
            final View popUpView = NewLandingActivity.this.getLayoutInflater().inflate(R.layout.newmydeskpopup, null); // inflating popup layout
            myDeskPopUp = OustSdkTools.createPopUp(popUpView);
            TextView moduleName = popUpView.findViewById(R.id.moduleName);
            TextView userSubject = popUpView.findViewById(R.id.txtSubject);
            TextView userGrade = popUpView.findViewById(R.id.txtGrade);

            Button friendChallengeBtn = popUpView.findViewById(R.id.friendChallengeBtn);
            Button MisteryChallengeBtn = popUpView.findViewById(R.id.mystpryChallengebtn);
            ImageButton popUpCloseBtn = popUpView.findViewById(R.id.closeButton);
            TextView modulePercent = popUpView.findViewById(R.id.modulePercentTxt);
            TextView moduleExpert = popUpView.findViewById(R.id.moduleExpert);
            TextView topicName = popUpView.findViewById(R.id.topicName);
            TextView vendorName = popUpView.findViewById(R.id.vendorName);
            ImageView topImage = popUpView.findViewById(R.id.topImage);
            ImageView lead_img = popUpView.findViewById(R.id.lead_img);
            TextView modulePercentText = popUpView.findViewById(R.id.modulePercentText);
            TextView defaultText = popUpView.findViewById(R.id.txtDefaultText);
            RelativeLayout moduleleaderboard_btn = popUpView.findViewById(R.id.moduleleaderboard_btn);
            RelativeLayout deskbtn_layout = popUpView.findViewById(R.id.deskbtn_layout);
            moduleleaderboard_btn.setVisibility(View.VISIBLE);

            modulePercentText.setText(getResources().getString(R.string.complete));
            defaultText.setText(getResources().getString(R.string.module_challenge_friend));
            MisteryChallengeBtn.setText(getResources().getString(R.string.challenge_mystery));
            friendChallengeBtn.setText(getResources().getString(R.string.challenge_friend));
            OustSdkTools.setImage(lead_img, OustStrings.getString("learning_leaderboard"));


            if (null != ofbModule.getModuleName()) {
                moduleName.setText(ofbModule.getModuleName());
            }
            if ((null != ofbModule.getSubject()) && (!ofbModule.getSubject().isEmpty())) {
                userSubject.setText(String.format("%s%s", getResources().getString(R.string.moduleSubjectTxt), ofbModule.getSubject()));
            }
            if ((null != ofbModule.getGrade()) && (!ofbModule.getGrade().isEmpty()) && (!ofbModule.getGrade().equalsIgnoreCase("0"))) {
                if (Character.isDigit(ofbModule.getGrade().charAt(0))) {
                    userGrade.setText(String.format("%s%s", getResources().getString(R.string.moduleGradeTxt), ofbModule.getGrade()));
                } else {
                    userGrade.setText(String.format("%s%s", getResources().getString(R.string.moduleExamTxt), ofbModule.getGrade()));
                }
            }
            if (null != ofbModule.getVendorName()) {
                if (!ofbModule.getVendorName().isEmpty()) {
                    vendorName.setText(String.format("%s%s", getResources().getString(R.string.moduleVendorTxt), ofbModule.getVendorName()));
                }
            }
            if (null != ofbModule.getPercentageComp()) {
                modulePercent.setText(String.format("%s%%", ofbModule.getPercentageComp()));
            }
            if (ofbModule.getExpertise() != null) {
                moduleExpert.setText(ofbModule.getExpertise());
            }
            if (null != ofbModule.getTopic()) {
                topicName.setText(ofbModule.getTopic());
            }
            if (null != ofbModule.getBgImage()) {
                BitmapDrawable bd = OustSdkTools.getImageDrawable(getResources().getString(R.string.default_mydesk_1));
                if (!ofbModule.getBgImage().isEmpty()) {
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(ofbModule.getBgImage())
                                .error(bd).into(topImage);
                    } else {
                        Picasso.get().load(ofbModule.getBgImage())
                                .error(bd).networkPolicy(NetworkPolicy.OFFLINE).into(topImage);
                    }

                } else {
                    topImage = popUpView.findViewById(R.id.topImage);
                    OustSdkTools.setDrawableToImageView(topImage, bd);
                }
            }

            popUpCloseBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDeskPopUp.dismiss();
                }
            });
            MisteryChallengeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDeskPopUp.dismiss();
                    clickonMyModulesMysteryChallengeBtn(ofbModule);

                }
            });
            friendChallengeBtn.setVisibility(View.GONE);
            moduleleaderboard_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!OustSdkTools.checkInternetStatus()) {
                        return;
                    }
                    Intent intent = new Intent(NewLandingActivity.this, EventLeaderboardActivity.class);
                    intent.putExtra("moduleid", ofbModule.getModuleId());
                    intent.putExtra("modulename", ofbModule.getModuleName());
                    intent.putExtra("modulebackimage", ofbModule.getBgImage());
                    myDeskPopUp.dismiss();
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void clickonMyModulesMysteryChallengeBtn(OFBModule ofbModule) {
        callGamePlayView(ofbModule.getGrade(), ofbModule.getTopic(), ofbModule.getModuleId(),
                ofbModule.getSubject(), ofbModule.getModuleName());
        startMysteryChallenge();
    }

    private void callGamePlayView(String grade, String topic, String moduleId, String
            subject, String moduleName) {
        activeUser = OustAppState.getInstance().getActiveUser();
        if (null != grade)
            activeUser.setGrade(grade);
        if (null != topic)
            activeUser.setTopic(topic);
        if (null != moduleId)
            activeUser.setModuleId(moduleId);
        if (null != subject)
            activeUser.setSubject(subject);
        if (null != moduleName)
            activeUser.setModuleName(moduleName);
        setGame(activeUser);
        Gson gson = new Gson();
        OustPreferences.save("activeGameData", gson.toJson(activeGame));
    }

    private void setGame(ActiveUser activeUser) {
        try {
            if (activeGame == null) {
                activeGame = new ActiveGame();
            }
            activeGame.setGameid("");
            activeGame.setGames(activeUser.getGames());
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
            activeGame.setChallengerAvatar(activeUser.getAvatar());
            activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
            activeGame.setOpponentid("Mystery");
            activeGame.setOpponentDisplayName("Mystery");
            activeGame.setGameType(GameType.MYSTERY);
            activeGame.setGuestUser(false);
            activeGame.setRematch(false);
            activeGame.setGrade(activeUser.getGrade());
            activeGame.setGroupId("");
            activeGame.setSubject(activeUser.getSubject());
            activeGame.setTopic(activeUser.getTopic());
            activeGame.setLevel(activeUser.getLevel());
            activeGame.setLevelPercentage(activeUser.getLevelPercentage());
            activeGame.setWins(activeUser.getWins());
            activeGame.setModuleId(activeUser.getModuleId());
            activeGame.setModuleName(activeUser.getModuleName());
            activeGame.setIsLpGame(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startMysteryChallenge() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            Gson gson = new Gson();
            Intent intent = new Intent(NewLandingActivity.this, PlayActivity.class);
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("ActiveUser", gson.toJson(activeUser));
            startActivity(intent);
        } catch (Exception e) {
        }
    }

//======================================================================================


    private void setBannerImage(ImageView img1, String imgLink) {
        try {
            if (img1 != null) {
                if ((imgLink != null) && (!imgLink.isEmpty())) {
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(imgLink).into(img1);
                    } else {
                        Picasso.get().load(imgLink).networkPolicy(NetworkPolicy.OFFLINE).into(img1);
                    }
                } else {
                    OustSdkTools.setImage(img1, getResources().getString(R.string.challenge_and_win));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //    ===========================================================================================================
//    Drawer Call Back Methods

    @Override
    public void onLogout() {
        try {
            String signOutUrl = OustSdkApplication.getContext().getResources().getString(R.string.signout);
            signOutUrl = HttpManager.getAbsoluteUrl(signOutUrl);
            String userName = activeUser.getStudentid();
            String institutional_id = OustPreferences.get("tanentid");

            SignOutRequest signOutRequest = new SignOutRequest();
            if ((OustPreferences.get("gcmToken") != null)) {
                signOutRequest.setDeviceToken(OustPreferences.get("gcmToken"));
            }
            signOutRequest.setDeviceIdentity("android");
            signOutRequest.setStudentid(userName);
            signOutRequest.setInstitutionLoginId(institutional_id);
            signOutRequest.setDevicePlatformName("android");
            String authToken = OustPreferences.get("authToken");
            if (!TextUtils.isEmpty(authToken)) {
                signOutRequest.setAuthToken(authToken);

                final Gson gson = new Gson();
                String jsonParams = gson.toJson(signOutRequest);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, signOutUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success"))
                            localLogout();
                        ApiCallUtils.setIsLoggedOut(true);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "unable to logout");
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)
                            localLogout();
                        else
                            Toast.makeText(NewLandingActivity.this, "Somthing went wrong, unable to logout", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawer(drawer_listview);
                        OustSdkTools.hideProgressbar();
                    }
                });
            } else {
                localLogout();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        OustSdkTools.showProgressBar();
    }

    private void localLogout() {
        try {
            if (!OustLogDetailHandler.getInstance().isUserForcedOut()) {
                isAppTutorialShow = OustPreferences.getAppInstallVariable(IS_APP_TUTORIAL_SHOWN);
                tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                removeAllReminderNotification();
                OustDataHandler.getInstance().resetData();
                OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
                OustAppState.getInstance().setLandingPageLive(false);
                OustStaticVariableHandling.getInstance().setAppActive(false);
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<DTOUserFeeds.FeedList>());

                mDrawerLayout.closeDrawer(drawer_listview);
                OustSdkTools.showProgressBar();
                OustSdkTools.clearAlldataAndlogout();
                OustPreferences.saveAppInstallVariable("LOGOUT", true);
                if (isAppTutorialShow) {
                    if (activeUser != null) {
                        String tempValue = tenantName + "_" + activeUser.getStudentKey() + "_" + isAppTutorialShow;
                        OustPreferences.save(ORG_ID_USER_ID_APP_TUTORIAL_VIEWED, tempValue);
                    }
                }
                try {
                    Intent intent = new Intent().setComponent(new ComponentName("com.oustme.oustapp",
                            "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (loginIntent != null) {
                        intent = loginIntent;
                    }
                    if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        startActivity(intent);
                    }
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                }

                //Intent intent = new Intent("oust_logout");
                //sendBroadcast(intent);
                Branch.getInstance().logout();
                NewLandingActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void ratetheApp() {
        mDrawerLayout.closeDrawer(drawer_listview);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.oustme.oustapp")));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onLeaderboardClick() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onHistoryClick() {
    }

    @Override
    public void onSettingClick() {
        try {
            if (!OustPreferences.getAppInstallVariable("hideUserSetting")) {
                mDrawerLayout.closeDrawer(drawer_listview);
                Intent intent = new Intent(NewLandingActivity.this, UserSettingActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void chaneIcon(int no) {
    }

    @Override
    public void clickOnFormFillIcon() {
        try {
            mDrawerLayout.closeDrawer(drawer_listview);
            Intent intent = new Intent(NewLandingActivity.this, FormFillingActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void clickOnAllList() {
        mDrawerLayout.closeDrawer(drawer_listview);
        try {
            Intent intent = new Intent(NewLandingActivity.this, ArchiveListActivity.class);
            NewLandingActivity.this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void clickOnAssessmentAnalytics() {
        Intent intent = new Intent(NewLandingActivity.this, AssessmentAnalyticsActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickOnLink() {
        mDrawerLayout.closeDrawer(drawer_listview);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://www.oustlabs.com"));
        startActivity(i);
    }

    @Override
    public void openFavouriteCards() {
        mDrawerLayout.closeDrawer(drawer_listview);
        Intent intent = new Intent(this, FavouriteCardsActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickOnContest() {
        try {
            mDrawerLayout.closeDrawer(drawer_listview);
            Intent intent = new Intent(OustSdkApplication.getContext(), FFContestListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void clickOnReportBug() {
        try {
            mDrawerLayout.closeDrawer(drawer_listview);
            Intent intent = new Intent(OustSdkApplication.getContext(), ReportProblemListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void clickOnAnalytics() {
        try {
            mDrawerLayout.closeDrawer(drawer_listview);
            Intent intent = new Intent(NewLandingActivity.this, UserAnalyticsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void clickOnCatalogue() {
        seeAllCatalog();
    }

    @Override
    public void clickCplClose() {

    }

    @Override
    public void clickLearningDiary() {
        //Intent intent = new Intent(this, LearningDiaryActivity.class);
        Intent intent = new Intent(this, WorkDiaryActivity.class);
        intent.putExtra("avatar", activeUser.getAvatar());
        intent.putExtra("Name", activeUser.getUserDisplayName());
        intent.putExtra("studentId", activeUser.getStudentid());
        startActivity(intent);
    }

    @Override
    public void clickBadges() {
        //Intent intent = new Intent(this, LearningDiaryActivity.class);
        Intent intent = new Intent(this, AchievementTabActivity.class);
        intent.putExtra("avatar", activeUser.getAvatar());
        intent.putExtra("Name", activeUser.getUserDisplayName());
        intent.putExtra("studentId", activeUser.getStudentid());
        startActivity(intent);
    }

    @Override
    public void clickOnCplLanguageSelection() {
        if (OustPreferences.getTimeForNotification("parentCplId") != 0) {
            OpenLanguageScreen(OustPreferences.getTimeForNotification("parentCplId"), true, false);
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_cpl_language_selection_available));
        }
    }

    @Override
    public void clickOnFAQ() {
        mDrawerLayout.closeDrawer(drawer_listview);
        Intent intent = new Intent(this, OustWebViewActivity.class);
        intent.putExtra(OustWebViewActivity.WEBURL, OustPreferences.get(AppConstants.StringConstants.URL_FAQ));
        startActivity(intent);
    }

    @Override
    public void clickOnTeamAnalytics() {

        mDrawerLayout.closeDrawer(drawer_listview);
        if (activeUser != null) {
            Intent intent = new Intent(this, OustWebViewActivity.class);
            String userId = activeUser.getStudentid();
            String orgId = OustPreferences.get("tanentid");
            String url = OustPreferences.get(AppConstants.StringConstants.URL_TEAM_ANALYTICS);
            if (url != null && !url.isEmpty()) {
                url = url + "/" + orgId + "/" + userId;
            }
            //Log.e(TAG,HttpManager.getUrlForWebApp(TEAM_ANALYTICS_URL+""+orgId+"/"+userId,orgId)+" - Webapp Team analytics");
            intent.putExtra(OustWebViewActivity.WEBURL, url);
            startActivity(intent);
        }


    }

    //===========================================================================================================
    //start Oust Servces
    private void startOustServices() {
        try {
            Intent intent2 = new Intent(NewLandingActivity.this, SubmitRequestsService.class);
            startService(intent2);
            Intent intent3 = new Intent(NewLandingActivity.this, SubmitCourseCompleteService.class);
            intent3.setClassName("com.oustme.oustsdk", "com.oustme.oustsdk.service");
            startService(intent3);
            Intent intent4 = new Intent(NewLandingActivity.this, SubmitLevelCompleteService.class);
            startService(intent4);
            Intent intent5 = new Intent(NewLandingActivity.this, FeedBackService.class);
            startService(intent5);
            Intent intent6 = new Intent(NewLandingActivity.this, SubmitNBPostService.class);
            startService(intent6);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //===========================================================================================================

    @Override
    public void onClick(View v) {
        int id = v.getId();
        try {
            if (id == R.id.main_oc_layout) {
                String ocPageLink = OustPreferences.get("rewardpagelink");
                if ((ocPageLink != null) && (!ocPageLink.isEmpty())) {
                    if (!OustSdkTools.checkInternetStatus()) {
                        return;
                    }
                    OustSdkTools.oustTouchEffect(v, 100);
                    showWebView(ocPageLink);
                }
            } else if (id == R.id.main_progress_layout) {
                try {
                    TryRippleView main_progress_layout = (TryRippleView) v;
                    if (!OustSdkTools.checkInternetStatus()) {
                        return;
                    }
                    main_progress_layout.setOnRippleCompleteListener(new TryRippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(TryRippleView rippleView) {
                            Intent intent = new Intent(NewLandingActivity.this, UserAnalyticsActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            } else if (id == R.id.playmode_text) {
                playmode_switch.setChecked(true);
            } else if (id == R.id.learnmode_text) {
                playmode_switch.setChecked(false);
            } else if (id == R.id.fab_action_joinmeeting) {
                startZoom(true);
                cancleFabLayout();
            } else if (id == R.id.fab_action_leaderboard) {
                try {
                    if (!OustSdkTools.checkInternetStatus()) {
                        return;
                    }
                    Intent intent = new Intent(NewLandingActivity.this, NewLeaderBoardActivity.class);
                    startActivity(intent);
                    cancleFabLayout();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else if (id == R.id.action_favourite) {
                Intent intent = new Intent(NewLandingActivity.this, FavouriteCardsActivity.class);
                startActivity(intent);
                cancleFabLayout();
            } else if (id == R.id.bannerclose_btn) {
                hideBannerWithAnimation();
            } else if (id == R.id.ffcbanner_img) {
                gotoFFCContest();
            } else if (id == R.id.topbanner_imageview) {
                checkForBannerType();
            } else if (id == R.id.fab_base_layout) {
                cancleFabLayout();
            } else if ((id == R.id.completedcourses_layout)) {
                if (!isSearchOn) {
                    Log.d(TAG, "onClick: totalCourseAvailable: " + myDeskData.size());
                    OustPreferences.saveintVar("totalCourseAvailable", myDeskData.size());
                    showAllcoursetextlayout();
                    sethilightedColor(user_coursecompletedtext);
                    sethilightedColor(user_coursecompletedlabel);
                    sethilightedColor(user_coursecompletedoctext);
                    user_coursependinglabel.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                    user_coursependingtext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                    user_coursependingoctext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                    setFragmentData(1);
                }
            } else if ((id == R.id.completedcourses_layouta)) {
                if (!isRefreshing) {
                    OustSdkTools.oustTouchEffect(completedcourses_layout, 100);
                    getCompletedList();
                }
            } else if ((id == R.id.completedcourses_layoutc)) {
                if (!isRefreshing) {
                    OustSdkTools.oustTouchEffect(completedcourses_layout, 100);
                    getCompletedList3();
                }
            } else if ((id == R.id.pendingcourses_layout)) {
                if (!isSearchOn) {
                    showAllcoursetextlayout();
                    sethilightedColor(user_coursependingtext);
                    sethilightedColor(user_coursependinglabel);
                    sethilightedColor(user_coursependingoctext);
                    user_coursecompletedlabel.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                    user_coursecompletedtext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                    user_coursecompletedoctext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                    setFragmentData(2);
                    Log.d(TAG, "onClick: pending layout:");
                }
            } else if (id == R.id.pendingcourses_layouta) {
                if (!isRefreshing) {
                    Log.d(TAG, "onClick: pending layout A:");
                    OustSdkTools.oustTouchEffect(pendingcourses_layout, 100);
                    getPendingList();
                }
            } else if (id == R.id.pendingcourses_layoutc) {
                if (!isRefreshing) {
                    Log.d(TAG, "onClick: pending layout: C");
                    OustSdkTools.oustTouchEffect(pendingcourses_layout, 100);
                    getPendingList3();
                }
            } else if ((id == R.id.showAllcoursetextlayout) || (id == R.id.showAllcoursetextlayouta)) {
                hideAllcoursetextlayout();
                user_coursecompletedlabel.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                user_coursecompletedtext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                user_coursependinglabel.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                user_coursependingtext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                user_coursecompletedoctext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                user_coursependingoctext.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                    setSelectedSubTabColors(subtaba_text, subtabc_text, subtabb_text, subtaba_textlabel, subtabc_textlabel, subtabb_textlabel);
                    showMyDeskData();
                } else {
                    setFragmentData(0);
                }
            } else if ((id == R.id.usermain_profile) || (id == R.id.usermain_profilea) || (id == R.id.usermain_profilec)) {
                onSettingClick();
            } else if ((id == R.id.userprofileImageText) || (id == R.id.userprofileImageTexta) || (id == R.id.userprofileImageTextc)) {
                onSettingClick();
            } else if (id == R.id.toptab_texta) {
                showMyDeskData();
                setSelectedSubTabColors(subtaba_text, subtabc_text, subtabb_text, subtaba_textlabel, subtabc_textlabel, subtabb_textlabel);
                setSelectedTabColors(toptab_texta, toptab_textb, toptab_textc, 1);
                createNewCourseList(OustAppState.getInstance().getMyDeskList());
            } else if (id == R.id.toptab_textb) {
                showMyDeskData();
                setSelectedSubTabColors(subtaba_text, subtabc_text, subtabb_text, subtaba_textlabel, subtabc_textlabel, subtabb_textlabel);
                setSelectedTabColors(toptab_textb, toptab_texta, toptab_textc, 2);
                filterCompletedList();
            } else if (id == R.id.toptab_textc) {
                showMyDeskData();
                setSelectedSubTabColors(subtaba_text, subtabc_text, subtabb_text, subtaba_textlabel, subtabc_textlabel, subtabb_textlabel);
                setSelectedTabColors(toptab_textc, toptab_textb, toptab_texta, 3);
                filterPendingList();
            } else if (id == R.id.trending_layout || id == R.id.subtabc_text) {
                if (new_landing_page_type.equals("All")) {
                    if (allCatalogItemDataArrayList == null) {
                        getCatalogueData(1);
                    }
                } else if (new_landing_page_type.equals("Learn")) {
                    if (learnCatalogItemDataArrayList == null) {
                        getCatalogueData(2);
                    }
                } else {
                    if (growCatalogItemDataArrayList == null) {
                        getCatalogueData(3);
                    }
                }
                if (!latestTrendingDataLoaded) {
                    loadLatestTrendingData();
                }
                OustSdkTools.oustTouchEffect(trendingLayout, 100);
                showTrendingData();
                setSelectedSubTabColors(subtabc_text, subtabb_text, subtaba_text, subtabc_textlabel, subtabb_textlabel, subtaba_textlabel);
            } else if (id == R.id.latest_layout || id == R.id.subtabb_text) {
                if (new_landing_page_type.equals("All")) {
                    if (allCatalogItemDataArrayList == null) {
                        getCatalogueData(1);
                    }
                } else if (new_landing_page_type.equals("Learn")) {
                    if (learnCatalogItemDataArrayList == null) {
                        getCatalogueData(2);
                    }
                } else {
                    if (growCatalogItemDataArrayList == null) {
                        getCatalogueData(3);
                    }
                }
                if (!latestTrendingDataLoaded) {
                    loadLatestTrendingData();
                }
                OustSdkTools.oustTouchEffect(latestLayout, 100);
                showLatestData();
                setSelectedSubTabColors(subtabb_text, subtabc_text, subtaba_text, subtabb_textlabel, subtabc_textlabel, subtaba_textlabel);
            } else if (id == R.id.my_desk_layout || id == R.id.subtaba_text) {
                clickOnMyDesk();
            } else if (id == R.id.seeall_courseslayout) {
                OustSdkTools.oustTouchEffect(seeall_courseslayout, 100);
                seeAllCourses();
            } else if (id == R.id.catelog_layouta || id == R.id.catelog_layouta3) {
                if (new_landing_page_type.equals("All")) {
                    if ((allCatalogItemDataArrayList != null) && (allCatalogItemDataArrayList.size() > 0)) {
                        if (allCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("NEW") || allCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            allCatalogItemDataArrayList.get(0).setViewStatus("SEEN");
                            if (catalogIndicatorA != null && catalogIndicatorA.getVisibility() == View.VISIBLE) {
                                catalogIndicatorA.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(allCatalogItemDataArrayList.get(0).getId());
                    }
                } else if (new_landing_page_type.equals("Grow")) {
                    if ((growCatalogItemDataArrayList != null) && (growCatalogItemDataArrayList.size() > 0)) {
                        if (growCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("NEW") || growCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            growCatalogItemDataArrayList.get(0).setViewStatus("SEEN");
                            if (catalogIndicatorA != null && catalogIndicatorA.getVisibility() == View.VISIBLE) {
                                catalogIndicatorA.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(growCatalogItemDataArrayList.get(0).getId());
                    }
                } else if (new_landing_page_type.equalsIgnoreCase("SKILL")) {
                    if ((skillCatalogItemDataArrayList != null) && (skillCatalogItemDataArrayList.size() > 0)) {
                        if (skillCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("NEW") || skillCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            skillCatalogItemDataArrayList.get(0).setViewStatus("SEEN");
                            if (catalogIndicatorA != null && catalogIndicatorA.getVisibility() == View.VISIBLE) {
                                catalogIndicatorA.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(skillCatalogItemDataArrayList.get(0).getId());
                    }
                } else {
                    if ((learnCatalogItemDataArrayList != null) && (learnCatalogItemDataArrayList.size() > 0)) {
                        if (learnCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("NEW") || learnCatalogItemDataArrayList.get(0).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            learnCatalogItemDataArrayList.get(0).setViewStatus("SEEN");
                            if (catalogIndicatorA != null && catalogIndicatorA.getVisibility() == View.VISIBLE) {
                                catalogIndicatorA.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(learnCatalogItemDataArrayList.get(0).getId());
                    }
                }
            } else if (id == R.id.catelog_layoutb || id == R.id.catelog_layoutb3) {
                if (new_landing_page_type.equals("All")) {
                    if ((allCatalogItemDataArrayList != null) && (allCatalogItemDataArrayList.size() > 1)) {
                        if (allCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("NEW") || allCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            allCatalogItemDataArrayList.get(1).setViewStatus("SEEN");
                            if (catalogIndicatorB != null && catalogIndicatorB.getVisibility() == View.VISIBLE) {
                                catalogIndicatorB.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(allCatalogItemDataArrayList.get(1).getId());
                    }
                } else if (new_landing_page_type.equals("Grow")) {
                    if ((growCatalogItemDataArrayList != null) && (growCatalogItemDataArrayList.size() > 1)) {
                        if (growCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("NEW") || growCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            growCatalogItemDataArrayList.get(1).setViewStatus("SEEN");
                            if (catalogIndicatorB != null && catalogIndicatorB.getVisibility() == View.VISIBLE) {
                                catalogIndicatorB.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(growCatalogItemDataArrayList.get(1).getId());
                    }
                } else if (new_landing_page_type.equalsIgnoreCase("SKILL")) {
                    if ((skillCatalogItemDataArrayList != null) && (skillCatalogItemDataArrayList.size() > 0)) {
                        if (skillCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("NEW") || skillCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            skillCatalogItemDataArrayList.get(1).setViewStatus("SEEN");
                            if (catalogIndicatorA != null && catalogIndicatorA.getVisibility() == View.VISIBLE) {
                                catalogIndicatorA.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(skillCatalogItemDataArrayList.get(1).getId());
                    }
                } else {
                    if ((learnCatalogItemDataArrayList != null) && (learnCatalogItemDataArrayList.size() > 1)) {
                        if (learnCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("NEW") || learnCatalogItemDataArrayList.get(1).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            learnCatalogItemDataArrayList.get(1).setViewStatus("SEEN");
                            if (catalogIndicatorB != null && catalogIndicatorB.getVisibility() == View.VISIBLE) {
                                catalogIndicatorB.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(learnCatalogItemDataArrayList.get(1).getId());
                    }
                }
            } else if (id == R.id.catelog_layoutc || id == R.id.catelog_layoutc3) {
                if (new_landing_page_type.equals("All")) {
                    if ((allCatalogItemDataArrayList != null) && (allCatalogItemDataArrayList.size() > 2)) {
                        if (allCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("NEW") || allCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            allCatalogItemDataArrayList.get(2).setViewStatus("SEEN");
                            if (catalogIndicatorC != null && catalogIndicatorC.getVisibility() == View.VISIBLE) {
                                catalogIndicatorC.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(allCatalogItemDataArrayList.get(2).getId());
                    }
                } else if (new_landing_page_type.equals("Grow")) {
                    if ((growCatalogItemDataArrayList != null) && (growCatalogItemDataArrayList.size() > 2)) {
                        if (growCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("NEW") || growCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            growCatalogItemDataArrayList.get(2).setViewStatus("SEEN");
                            if (catalogIndicatorC != null && catalogIndicatorC.getVisibility() == View.VISIBLE) {
                                catalogIndicatorC.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(growCatalogItemDataArrayList.get(2).getId());
                    }
                } else if (new_landing_page_type.equalsIgnoreCase("SKILL")) {
                    if ((skillCatalogItemDataArrayList != null) && (skillCatalogItemDataArrayList.size() > 0)) {
                        if (skillCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("NEW") || skillCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            skillCatalogItemDataArrayList.get(2).setViewStatus("SEEN");
                            if (catalogIndicatorA != null && catalogIndicatorA.getVisibility() == View.VISIBLE) {
                                catalogIndicatorA.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(skillCatalogItemDataArrayList.get(2).getId());
                    }
                } else {
                    if ((learnCatalogItemDataArrayList != null) && (learnCatalogItemDataArrayList.size() > 2)) {
                        launchCatalogActivity(learnCatalogItemDataArrayList.get(2).getId());
                        if (learnCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("NEW") || learnCatalogItemDataArrayList.get(2).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            learnCatalogItemDataArrayList.get(2).setViewStatus("SEEN");
                            if (catalogIndicatorC != null && catalogIndicatorC.getVisibility() == View.VISIBLE) {
                                catalogIndicatorC.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            } else if (id == R.id.catelog_layoutd || id == R.id.catelog_layoutd3) {
                if (new_landing_page_type.equals("All")) {
                    if ((allCatalogItemDataArrayList != null) && (allCatalogItemDataArrayList.size() > 3)) {
                        if (allCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("NEW") || allCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            allCatalogItemDataArrayList.get(3).setViewStatus("SEEN");
                            if (catalogIndicatorD != null && catalogIndicatorD.getVisibility() == View.VISIBLE) {
                                catalogIndicatorD.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(allCatalogItemDataArrayList.get(3).getId());
                    }
                } else if (new_landing_page_type.equals("Grow")) {
                    if ((growCatalogItemDataArrayList != null) && (growCatalogItemDataArrayList.size() > 3)) {
                        if (growCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("NEW") || growCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            growCatalogItemDataArrayList.get(3).setViewStatus("SEEN");
                            if (catalogIndicatorD != null && catalogIndicatorD.getVisibility() == View.VISIBLE) {
                                catalogIndicatorD.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(growCatalogItemDataArrayList.get(3).getId());
                    }
                } else if (new_landing_page_type.equalsIgnoreCase("SKILL")) {
                    if ((skillCatalogItemDataArrayList != null) && (skillCatalogItemDataArrayList.size() > 0)) {
                        if (skillCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("NEW") || skillCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            skillCatalogItemDataArrayList.get(3).setViewStatus("SEEN");
                            if (catalogIndicatorA != null && catalogIndicatorA.getVisibility() == View.VISIBLE) {
                                catalogIndicatorA.setVisibility(View.GONE);
                            }
                        }
                        launchCatalogActivity(skillCatalogItemDataArrayList.get(3).getId());
                    }
                } else {
                    if ((learnCatalogItemDataArrayList != null) && (learnCatalogItemDataArrayList.size() > 3)) {
                        if (learnCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("NEW") || learnCatalogItemDataArrayList.get(3).getViewStatus().equalsIgnoreCase("UPDATE")) {
                            learnCatalogItemDataArrayList.get(3).setViewStatus("SEEN");
                            if (catalogIndicatorD != null && catalogIndicatorD.getVisibility() == View.VISIBLE) {
                                catalogIndicatorD.setVisibility(View.GONE);
                                // updateViewStatus(learnCatalogItemDataArrayList.get(3));
                            }
                        }
                        launchCatalogActivity(learnCatalogItemDataArrayList.get(3).getId());
                    }
                }
            } else if (id == R.id.see_all_catalaog || id == R.id.see_all_catalaog3) {
                OustSdkTools.oustTouchEffect(see_all_catalaog, 100);
                seeAllCatalog();
            } else if (id == R.id.tab_imga) {
                clickOnTabImagea();

            } else if (id == R.id.tab_imgb) {
                imageTabSelected(tabImgBIndicator, tabImgAIndicator, tabImgCIndicator);
                myDeskLayout.setVisibility(View.GONE);
                if (new_landing_page_type.equals("Grow") || new_landing_page_type.equals("All")) {
                    if (learnCatalogItemDataArrayList != null) {
                        setCatalogImages(learnCatalogItemDataArrayList);
                    } else {
                        if (!OustSdkTools.checkInternetStatus()) {
                            OustStaticVariableHandling.getInstance().setInternetConnectionOff(true);
                        } else {
                            OustStaticVariableHandling.getInstance().setInternetConnectionOff(false);
                        }
                        if (!latestTrendingDataLoaded) {
                            loadLatestTrendingData();
                        }
                        new_landing_page_type = "Learn";
                        getCatalogueData(2);
                    }
                    new_landing_page_type = "Learn";
                    sortAllDataByLearn();
                }
            } else if (id == R.id.tab_imgc) {
                myDeskLayout.setVisibility(View.GONE);
                imageTabSelected(tabImgCIndicator, tabImgAIndicator, tabImgBIndicator);
                if (new_landing_page_type.equals("Learn") || new_landing_page_type.equals("All")) {
                    if ((growCatalogItemDataArrayList != null)) {
                        setCatalogImages(growCatalogItemDataArrayList);
                    } else {
                        if (!OustSdkTools.checkInternetStatus()) {
                            OustStaticVariableHandling.getInstance().setInternetConnectionOff(true);
                        } else {
                            OustStaticVariableHandling.getInstance().setInternetConnectionOff(false);
                        }
                        if (!latestTrendingDataLoaded) {
                            loadLatestTrendingData();
                        }
                        new_landing_page_type = "Grow";
                        getCatalogueData(3);
                    }
                    new_landing_page_type = "Grow";
                    sortAllDataByGrowth();
                }
            } else if (id == R.id.refresh_layout) {
                isRefreshing = true;
                refresh_layout.setClickable(false);
                refresh_layout.setVisibility(View.GONE);
                refreshLayout();
            } else if (id == R.id.wjalert_banner) {
                action_alert.setIcon(R.mipmap.new_alerta);
                wjalert_banner.setVisibility(View.GONE);
                showingAlertBanner = false;
                List<DTONewFeed> allFeedList = new ArrayList<>();
                allFeedList.addAll(userNewFeedList);
//                allFeedList.addAll(feedList);
                Collections.sort(allFeedList, newsFeedSorter);
                OustAppState.getInstance().setNewsFeeds(allFeedList);
                Intent intent = new Intent(this, NewAlertActivity.class);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                startActivity(intent);
            } else if (id == R.id.refresh_layouta) {
                isRefreshing = true;
                refresh_layout.setClickable(false);
                refresh_layout.setVisibility(View.GONE);
                refreshLayoutA();
            } else if (id == R.id.refresh_layoutc) {
                isRefreshing = true;
                refresh_layout.setClickable(false);
                refresh_layout.setVisibility(View.GONE);
                refreshLayoutC();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoFFCContest() {
        if (isBannerSmall) {
            showBannerWithAnimation();
            return;
        } else if (fastestFingerContestData != null) {
            Intent intent4 = new Intent(NewLandingActivity.this, FFcontestStartActivity.class);
            Gson gson = new Gson();
            intent4.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
            startActivity(intent4);
        } else if (fastestFingerContestData == null) {
            Toast.makeText(NewLandingActivity.this, "There is No Contest Assigned to You", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViewStatus(CatalogItemData commonLandingData) {
        if (commonLandingData.getViewStatus() != null && (commonLandingData.getViewStatus().equalsIgnoreCase("NEW") || commonLandingData.getViewStatus().equalsIgnoreCase("UPDATE"))) {
            //  commonLandingDataList.get(pos).setViewStatus("SEEN");
            ActiveUser activeUser;
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            CatalogViewUpdate catalogViewUpdate = new CatalogViewUpdate();
            catalogViewUpdate.setCatalogId(commonLandingData.getCatalogId());
            // catalogViewUpdate.setContentType(commonLandingData.getType());
            catalogViewUpdate.setContentId(commonLandingData.getCatalogContentId());
            catalogViewUpdate.setCategoryId(commonLandingData.getCatalogCategoryId());
            catalogViewUpdate.setStudentid(activeUser.getStudentid());

            String url = OustSdkApplication.getContext().getResources().getString(R.string.catalog_view_update);
            final Gson mGson = new Gson();
            url = HttpManager.getAbsoluteUrl(url);
            String jsonParams = mGson.toJson(catalogViewUpdate);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    // OustSdkTools.showToast("CPL Distributed Successfully");
                    // mProgressbarAPICall.setVisibility(View.GONE);
                    //notifyDataSetChanged();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //  OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                    //  mProgressbarAPICall.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: onError:" + error.getLocalizedMessage());
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // OustSdkTools.showToast("CPL Distributed Successfully");
                    // mProgressbarAPICall.setVisibility(View.GONE);
                    //notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                    //  mProgressbarAPICall.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: onError:"+error.getLocalizedMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        }
    }

    private void getCompletedList3() {
        try {

            ArrayList<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            ArrayList<CommonLandingData> myAssessmentFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getcompletedDataMeetCriteria(OustAppState.getInstance().getMyDeskList());
            }
            if ((OustAppState.getInstance().getMyAssessmentList() != null) && (OustAppState.getInstance().getMyAssessmentList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myAssessmentFilterdData = commonLandingFilter.getcompletedDataMeetCriteria(OustAppState.getInstance().getMyAssessmentList());
            }
            myDeskFilterdData.addAll(myAssessmentFilterdData);
            if ((myDeskFilterdData != null && myDeskFilterdData.size() > 0) || allCourseSize > 0) {
                OustDataHandler.getInstance().saveData(myDeskFilterdData);
                OustDataHandler.getInstance().setMyDeskData(myDeskData);
                OustDataHandler.getInstance().setMyAssessmentData(myAssessmentData);
                Intent intent = new Intent(NewLandingActivity.this, CatalogListActivity.class);
                intent.putExtra("hasBanner", false);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                intent.putExtra("filter_type", "Complete");
                intent.putExtra("topDisplayName", "" + getResources().getString(R.string.completed_modules_text));
//                List<CommonLandingData> values = new ArrayList<CommonLandingData>(mCompletedCoursesList.values());
//                intent.putExtra("COMPLETED_MODULES", (Serializable) values);
                startActivity(intent);
            } else {
                OustSdkTools.showToast("" + getResources().getString(R.string.no_modules_available));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //this is inly for FRL to do
    private void AllPendingItemsDataList() {

        try {
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            List<CommonLandingData> myAssessmentFilterdData = new ArrayList<>();
            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getpendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList());
            }
            if ((OustAppState.getInstance().getMyAssessmentList() != null) && (OustAppState.getInstance().getMyAssessmentList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myAssessmentFilterdData = commonLandingFilter.getpendingDataMeetCriteria(OustAppState.getInstance().getMyAssessmentList());
            }

            mExpandListHeader = new ArrayList<ToDoHeaderModel>();

            List<ToDoChildModel> mToDoChildAssessmentList = new ArrayList<>();
            List<ToDoChildModel> mToDoChildCourseList = new ArrayList<>();
            List<ToDoChildModel> mContestList = new ArrayList<>();
            List<ToDoChildModel> mPlayList = new ArrayList<>();

            Log.d(TAG, "AllPendingItemsDataList: isLive:" + OustStaticVariableHandling.getInstance().isContestLive());
            if (fastestFingerContestData != null && OustStaticVariableHandling.getInstance().isContestLive()) {
                ToDoHeaderModel contestHeader = new ToDoHeaderModel();
                contestHeader.setType(0);
                contestHeader.setHasBanner(true);
                contestHeader.setTitle("Contest");
                contestHeader.setmUrl(ffcBannerURL);
                ToDoChildModel contestChild = new ToDoChildModel();
                contestChild.setFfcUsersCount("" + fastestFingerContestData.getUserCount());
                contestChild.setFfcEnrolledCount(fastestFingerContestData.getEnrolledCount() + "");

                if (fastestFingerContestData.getStartTime() != 0)
                    contestChild.setFfcStartTime(OustSdkTools.getDateTimeFromMilli(fastestFingerContestData.getStartTime()));
                mContestList.add(contestChild);
                mExpandListHeader.add(contestHeader);
            } else {
                isContest = false;
            }

            if (cplCollectionData != null) {
                ToDoHeaderModel playListHeader = new ToDoHeaderModel();
                playListHeader.setType(1);
                playListHeader.setHasBanner(true);
                playListHeader.setTitle("Play List");

                ToDoChildModel playListChild = new ToDoChildModel();
                //Log.d(TAG, "AllPendingItemsDataList: cplCollectionData.getOustCoins():"+cplCollectionData.getOustCoins());
                playListChild.setCPLOustCoinsCount(String.valueOf(cplCollectionData.getOustCoins()));
                playListChild.setCPLUsersCount("" + cplCollectionData.getEnrolledCount());
                playListChild.setCPLTitle(cplCollectionData.getCplName());
                playListChild.setCplId(cplCollectionData.getCplId());
                playListChild.setCPLDescription(cplCollectionData.getCplDescription());
                playListChild.setCplBanner(cplCollectionData.getBanner());
                mPlayList.add(playListChild);
                mExpandListHeader.add(playListHeader);
            } else {
                isPlayList = false;
            }


            if (myDeskFilterdData != null)
                for (int i = 0; i < myDeskFilterdData.size(); i++) {
                    ToDoChildModel toDoChildModel = new ToDoChildModel();
                    toDoChildModel.setCommonLandingDataCourse(myDeskFilterdData.get(i));
                    mToDoChildCourseList.add(toDoChildModel);
                }

            if (myAssessmentFilterdData != null)
                for (int i = 0; i < myAssessmentFilterdData.size(); i++) {
                    ToDoChildModel toDoChildModel = new ToDoChildModel();
                    toDoChildModel.setCommonLandingDataAssessment(myAssessmentFilterdData.get(i));
                    mToDoChildAssessmentList.add(toDoChildModel);
                }
            if (mToDoChildAssessmentList.size() > 0) {
                String header = "Assessments";//mToDoChildAssessmentList.get(0).getCommonLandingDataAssessment().getType();
                ToDoHeaderModel toDoHeaderModel = new ToDoHeaderModel();
                toDoHeaderModel.setTitle(header);
                toDoHeaderModel.setType(2);
                toDoHeaderModel.setHasBanner(false);
                if (isTodo)
                    mExpandListHeader.add(toDoHeaderModel);
            }

            if (mToDoChildCourseList.size() > 0) {
                String header = "Courses";//mToDoChildCourseList.get(0).getCommonLandingDataCourse().getType();
                ToDoHeaderModel toDoHeaderModel = new ToDoHeaderModel();
                toDoHeaderModel.setTitle(header);
                toDoHeaderModel.setType(3);
                toDoHeaderModel.setHasBanner(false);
                if (isTodo)
                    mExpandListHeader.add(toDoHeaderModel);
            }

            boolean sendContestData = false, sendPlayListData = false, sendTodoData = false;
            if (isContest || (fastestFingerContestData != null && OustStaticVariableHandling.getInstance().isContestLive())) {
                sendContestData = true;
            }
            if (isPlayList || cplCollectionData != null) {
                sendPlayListData = true;
            }


            mExpandChildList = new HashMap<>();
            if (sendContestData && sendPlayListData && isTodo) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mContestList);
                mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mPlayList);
                Log.d(TAG, "AllPendingItemsDataList: asse:" + mToDoChildAssessmentList.size());
                if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(2).getTitle(), mToDoChildAssessmentList);
                    mExpandChildList.put(mExpandListHeader.get(3).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() == 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(2).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() == 0) {
                    mExpandChildList.put(mExpandListHeader.get(2).getTitle(), mToDoChildAssessmentList);
                }
            } else if (isTodo && sendContestData && !isPlayList) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mContestList);
                if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                    mExpandChildList.put(mExpandListHeader.get(2).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() == 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() == 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                }
            } else if (isTodo && sendPlayListData && !isContest) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mPlayList);

                if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                    mExpandChildList.put(mExpandListHeader.get(2).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() == 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() == 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                }

            } else if (sendContestData && isTodo && !isPlayList && cplCollectionData != null) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mPlayList);
                if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                    mExpandChildList.put(mExpandListHeader.get(2).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() == 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() == 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                }
            } else if (!isContest && isTodo && !isPlayList && cplCollectionData != null) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mPlayList);
                if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                    mExpandChildList.put(mExpandListHeader.get(2).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() == 0 && mToDoChildCourseList.size() > 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildCourseList);
                } else if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() == 0) {
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildAssessmentList);
                }
            } else if (isTodo && !isPlayList && !isContest) {
                if (mToDoChildAssessmentList.size() > 0 && mToDoChildCourseList.size() == 0) {
                    if (mExpandListHeader.get(0) != null)
                        mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mToDoChildAssessmentList);
                } else if (mToDoChildCourseList.size() > 0 && mToDoChildAssessmentList.size() == 0) {
                    if (mExpandListHeader != null)
                        mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mToDoChildCourseList);
                } else {
                    mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mToDoChildAssessmentList);
                    mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mToDoChildCourseList);
                }
            } else if (isContest && !isPlayList && !isTodo) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mContestList);
            } else if (isPlayList && !isContest && !isTodo) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mPlayList);
            } else if (isPlayList && isContest && !isTodo) {
                mExpandChildList.put(mExpandListHeader.get(0).getTitle(), mContestList);
                mExpandChildList.put(mExpandListHeader.get(1).getTitle(), mPlayList);
            }

            Intent intent = new Intent(this, TodoListActivity.class);
            intent.putExtra("deskDataMap", myDeskInfoMap);
            intent.putExtra(AppConstants.StringConstants.EXP_CHILD_LIST, mExpandChildList);
            Bundle bundle = new Bundle();

            //if flag is off but FFC is assigned to user then we need to show that
            if (!isContest) {
                if (fastestFingerContestData != null && fastestFingerContestData.getEndTime() > System.currentTimeMillis()) {
                    bundle.putBoolean(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, true);
                }
            } else {
                bundle.putBoolean(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, isContest);
            }

            //if flag is off but CPL is assigned to user then we need to show that
            if (!isPlayList) {
                if (cplCollectionData != null) {
                    bundle.putBoolean(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, true);
                }
            } else {
                bundle.putBoolean(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, isPlayList);
            }
            bundle.putBoolean(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE, isTodo);
            bundle.putString(AppConstants.StringConstants.FFC_BANNER_URL, ffcBannerURL);
            bundle.putParcelableArrayList(AppConstants.StringConstants.EXP_HEADER_LIST, mExpandListHeader);
            Gson gson = new Gson();
            intent.putExtra(AppConstants.StringConstants.FFC_DATA, gson.toJson(fastestFingerContestData));
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getPendingList3() {
        try {
            ArrayList<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            ArrayList<CommonLandingData> myAssessmentFilterdData = new ArrayList<>();

            if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getpendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList());
            }
            if ((OustAppState.getInstance().getMyAssessmentList() != null) && (OustAppState.getInstance().getMyAssessmentList().size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myAssessmentFilterdData = commonLandingFilter.getpendingDataMeetCriteria(OustAppState.getInstance().getMyAssessmentList());
            }

            myDeskFilterdData.addAll(myAssessmentFilterdData);
            if (myDeskFilterdData.size() > 0) {

                OustDataHandler.getInstance().saveData(myDeskFilterdData);
                OustDataHandler.getInstance().setMyDeskData(myDeskData);
                OustDataHandler.getInstance().setMyAssessmentData(myAssessmentData);

                Intent intent = new Intent(NewLandingActivity.this, CatalogListActivity.class);
                intent.putExtra("hasBanner", false);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                intent.putExtra("filter_type", "Pending");
                intent.putExtra("topDisplayName", "" + getResources().getString(R.string.pending_modules_text));
                startActivity(intent);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.no_modules_available));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void refreshLayoutC() {
        try {
            resetLayoutC();
            swiperefreshparent_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            tab_layout_c.setVisibility(View.GONE);
            filter_ll.setVisibility(View.GONE);
            filter_category_ll.setVisibility(View.GONE);
            filterType = 0;
            OustStaticVariableHandling.getInstance().setFilterAllowed(true);
            refreshButtonPressed = true;
            enableLayout3Buttons();
            removeAllListener();
            autheticateWithFirebase(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetLayoutC() {
        action_alert.setVisible(false);
        actionSearch.setVisible(false);
        landingnewprogress_bar.setProgress(0);
        user_coursecompletedtext.setText("0");
        user_coursependingtext.setText("0");
        user_coursependingoctext.setText("0 (" + getResources().getString(R.string.credits) + ")");
        user_coursecompletedoctext.setText("0 (" + getResources().getString(R.string.credits) + ")");
        newsfeed_recyclerview.setVisibility(View.GONE);
        sendFeedClickedRequestToBackend();
        newCourse_recyclerview.setVisibility(View.GONE);
        sub_tab_3.setVisibility(View.GONE);
        playList_rv.setVisibility(View.GONE);
        noticeBoard_rv.setVisibility(View.GONE);
        newChallenges_recyclerview.setVisibility(View.GONE);
        no_data_text.setVisibility(View.GONE);
        COURSE_ASSESSMENT_TAB_FOUND = false;
        if (newFeedAdapter != null) {
            newFeedAdapter.notifyFeedChange(null, false);
            newFeedAdapter = null;
        }
        if (newLandingModuleAdaptera != null) {
            newLandingModuleAdaptera.notifyListChnage(null, 0);
            newLandingModuleAdaptera = null;
        }
        if (newLandingPlayListAdaptera != null) {
            newLandingPlayListAdaptera.notifyListChnage(null, 0);
            newLandingPlayListAdaptera = null;
        }
        if (newLandingChallengeAdaptera != null) {
            newLandingChallengeAdaptera.notifyListChnage(null, 0);
            newLandingChallengeAdaptera = null;
        }
        if (nbTopicAdapter != null) {
            nbTopicAdapter.notifyListChnage(null);
            nbTopicAdapter = null;
        }
        newFeedFilterAdapter = null;
        newFeedVerticalFilterAdapter = null;
        resetAllData();
    }

    private boolean isRefreshing = false;

    private void refreshLayout() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
                return;
            }

            userSkillDataArrayList = new ArrayList<>();
            userSkillDataArrayListSize = 0;
            resetNewLayout();
            OustDataHandler.getInstance().setPaginationReachToEnd(false);
            swiperefreshparent_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            refreshButtonPressed = true;
            removeAllListener();
            autheticateWithFirebase(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetNewLayout() {
        try {
            clickOnTabImagea();
            clickOnMyDesk();
            OustDataHandler.getInstance().setPaginationReachToEnd(false);
            wjalert_banner.setVisibility(View.GONE);
            newlandinghorizontal_recyclerviewa.setVisibility(View.GONE);
            newlandinghorizontal_recyclerviewb.setVisibility(View.GONE);
            newlandinghorizontal_recyclerviewc.setVisibility(View.GONE);

            maintab_image_layout.setVisibility(View.GONE);

            catalogue_labelline.setVisibility(View.GONE);
            catalogue_label.setVisibility(View.GONE);


            subtab_layout.setVisibility(View.GONE);
            catelog_iamgea.setVisibility(View.GONE);
            catelog_iamgea.setImageBitmap(null);
            catelog_iamgeb.setVisibility(View.GONE);
            catelog_iamgeb.setImageBitmap(null);
            catelog_iamgec.setVisibility(View.GONE);
            catelog_iamgec.setImageBitmap(null);
            catelog_iamged.setVisibility(View.GONE);
            catelog_iamged.setImageBitmap(null);

            action_alert.setVisible(false);
            actionSearch.setVisible(false);
            showAlertBanner(false);

            offlinemde_layout.setVisibility(View.GONE);
            ll_loading.setVisibility(View.GONE);

            landingnewprogress_bar.setProgress(0);

            user_coursecompletedtext.setText("0");
            user_coursependingtext.setText("0");

            user_coursependingoctext.setText("0 (" + getResources().getString(R.string.credits) + ")");
            user_coursecompletedoctext.setText("0 (" + getResources().getString(R.string.credits) + ")");

            resetAllData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void refreshLayoutA() {
        try {

            resetLayoutA();

            swiperefreshparent_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            refreshButtonPressed = true;
            removeAllListener();
            autheticateWithFirebase(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetLayoutA() {
        try {
            action_alert.setVisible(false);
            actionSearch.setVisible(false);
            landingnewprogress_bar.setProgress(0);
            wjalert_banner.setVisibility(View.GONE);
            user_coursecompletedtext.setText("0");
            user_coursependingtext.setText("0");

            user_coursependingoctext.setText("0 (" + getResources().getString(R.string.credits) + ")");
            user_coursecompletedoctext.setText("0 (" + getResources().getString(R.string.credits) + ")");

            if (newTabFragmentPagerAdapter != null) {
                newTabFragmentPagerAdapter.notifyChanges(0, "");
            }
            tabLayout.removeAllTabs();
            oustTabCount = 0;
            OustAppState.getInstance().setChallengeFragmentInit(false);
            resetAllData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setRefreshLayoutVisibility() {
        Log.e("refresh all", "refresh");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    isRefreshing = false;
                    refresh_layout.setClickable(true);
                    refresh_layout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }
        }, 5000);
    }

    private void resetAllData() {
        try {
            ofbModuleList = new ArrayList<>();
            allCatalogItemDataArrayList = null;
            nbTopicDataHashMap = null;
            totalNBTopicCount = 0;
            learnCatalogItemDataArrayList = null;
            growCatalogItemDataArrayList = null;
            tabInfoDataArrayList = new ArrayList<>();
            moduleDataArrayList = new ArrayList<>();
            filterCategories = new ArrayList<>();
            playListData = null;
            defaultTab = 0;
            toolbarColorCode = "";
            new_landing_page_type = "All";
            totalModuleCount = 0;
            localModuleCount = 0;
            totalAssessmentCount = 0;
            localAssessmentCount = 0;
            totalCollectionCount = 0;
            localCollectionCount = 0;
            localFeedCount = 0;
            allCourseSize = 0;
            isFirstTime = true;
            haveAllCourses = false;
            haveAllAssignments = false;
            haveAllCollections = false;
            feedDataInit = false;
            latestTrendingDataLoaded = false;
            isCoursesAvailable = false;
            isAssessmentAvailable = false;
            isCollectionAvailable = false;
            myDeskInfoMap = new HashMap<>();
            trendingList = new ArrayList<>();
            latestList = new ArrayList<>();
            noofcoursestoload = 0;
            noofOfAssessmentsToLoad = 0;
            noofOfCollectionsToLoad = 0;
            cplShown = false;
            myDeskData = new HashMap<>();
            //for layout type 1 and 2
            myAssessmentData = new HashMap<>();
            landingTag = "";
            noofListenerSetForFeed = 0;
            userNewFeedList = new ArrayList<>();
//            feedList = new ArrayList<>();
            isLatestTrendingShown = false;
            OustAppState.getInstance().setMyDeskList(new ArrayList<CommonLandingData>());
            OustDataHandler.getInstance().setAllCoursesLoaded(false);
            OustDataHandler.getInstance().setAllAssessmentLoaded(false);
            OustDataHandler.getInstance().setAllFeedsLoaded(false);
            clearNotificationIntentDataOnRefresh();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void clearNotificationIntentDataOnRefresh() {
        try {
            getIntent().removeExtra("contestId");
            getIntent().removeExtra("zoom_meetingId");
            getIntent().removeExtra("collectionId");
            getIntent().removeExtra("courseId");
            getIntent().removeExtra("assessmentId");
            getIntent().removeExtra("isFeedDistributed");
        } catch (Exception e) {
        }
    }

    private void clickOnMyDesk() {
        try {
            OustSdkTools.oustTouchEffect(myDeskLayout, 100);
            isLatestTrendingShown = false;
            showMyDeskData();
            setSelectedSubTabColors(subtaba_text, subtabc_text, subtabb_text, subtaba_textlabel, subtabc_textlabel, subtabb_textlabel);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void clickOnTabImagea() {
        try {
            imageTabSelected(tabImgAIndicator, tabImgBIndicator, tabImgCIndicator);
            myDeskLayout.setVisibility(View.VISIBLE);
            if (new_landing_page_type.equals("Grow") || new_landing_page_type.equals("Learn")) {
                if (allCatalogItemDataArrayList != null) {
                    setCatalogImages(allCatalogItemDataArrayList);
                } else {
                    if (!OustSdkTools.checkInternetStatus()) {
                        OustStaticVariableHandling.getInstance().setInternetConnectionOff(true);
                    } else {
                        OustStaticVariableHandling.getInstance().setInternetConnectionOff(false);
                    }
                    if (!latestTrendingDataLoaded) {
                        loadLatestTrendingData();
                    }
                    new_landing_page_type = "All";
                    getCatalogueData(1);
                }
                new_landing_page_type = "All";
                sortAllDataByAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void loadLatestTrendingData() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getLatestTrendingData(1, 2);
                    getLatestTrendingData(1, 3);

                    getLatestTrendingData(2, 2);
                    getLatestTrendingData(2, 3);

                    getLatestTrendingData(3, 2);
                    getLatestTrendingData(3, 3);
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void imageTabSelected(RelativeLayout r1, RelativeLayout r2, RelativeLayout
            r3) {
        try {
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
                color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }
            Drawable d = getResources().getDrawable(
                    R.drawable.roundcorner_red);
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            r1.setBackgroundDrawable(d);
            r2.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
            r3.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
        } catch (Exception e) {
        }
    }

    private void sortAllDataByLearn() {
        setNewLandingListVisibility();
        if (newlandinghorizontal_recyclerviewa.getVisibility() == View.VISIBLE) {
            newlandinghorizontal_recyclerviewa.setVisibility(View.GONE);
            newlandinghorizontal_recyclerviewb.setVisibility(View.VISIBLE);
            setSelectedSubTabColors(subtabb_text, subtabc_text, subtaba_text, subtabb_textlabel, subtabc_textlabel, subtaba_textlabel);
            createHorizontalListb(filterListByLearnType(latestList));
        } else if (newlandinghorizontal_recyclerviewb.getVisibility() == View.VISIBLE) {
            createHorizontalListb(filterListByLearnType(latestList));
        } else if (newlandinghorizontal_recyclerviewc.getVisibility() == View.VISIBLE) {
            createHorizontalListc(filterListByLearnType(trendingList));
        }
    }

    private void setNewLandingListVisibility() {
        if (landingTag.equals("mydesk")) {
            newlandinghorizontal_recyclerviewa.setVisibility(View.VISIBLE);
        } else if (landingTag.equals("latest")) {
            newlandinghorizontal_recyclerviewb.setVisibility(View.VISIBLE);
        } else if (landingTag.equals("trending")) {
            newlandinghorizontal_recyclerviewc.setVisibility(View.VISIBLE);
        }
    }

    private void sortAllDataByAll() {
        try {
            setNewLandingListVisibility();
            if (newlandinghorizontal_recyclerviewa.getVisibility() == View.VISIBLE) {
                createHorizontalLista((OustAppState.getInstance().getMyDeskList()));
            } else if (newlandinghorizontal_recyclerviewb.getVisibility() == View.VISIBLE) {
                createHorizontalListb((latestList));
            } else if (newlandinghorizontal_recyclerviewc.getVisibility() == View.VISIBLE) {
                createHorizontalListc((trendingList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sortAllDataByGrowth() {
        try {
            setNewLandingListVisibility();
            if (newlandinghorizontal_recyclerviewa.getVisibility() == View.VISIBLE) {
                newlandinghorizontal_recyclerviewa.setVisibility(View.GONE);
                newlandinghorizontal_recyclerviewb.setVisibility(View.VISIBLE);
                setSelectedSubTabColors(subtabb_text, subtabc_text, subtaba_text, subtabb_textlabel, subtabc_textlabel, subtaba_textlabel);
                createHorizontalListb(filterListByGrowType(latestList));
            } else if (newlandinghorizontal_recyclerviewb.getVisibility() == View.VISIBLE) {
                createHorizontalListb(filterListByGrowType(latestList));
            } else if (newlandinghorizontal_recyclerviewc.getVisibility() == View.VISIBLE) {
                createHorizontalListc(filterListByGrowType(trendingList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void launchCatalogActivity(long catalog_id) {
        Log.d(TAG, "launchCatalogActivity: ");
        try {
          /*  if (OustSdkTools.checkInternetStatus()) {

            }*/
            Intent intent = new Intent(this, CatalogListActivity.class);

            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI)) {
                intent = new Intent(this, CatalogueModuleListActivity.class);
            }

            //

            intent.putExtra("catalog_id", catalog_id);
            intent.putExtra("hasBanner", true);
            intent.putExtra("deskDataMap", myDeskInfoMap);
            //intent.putExtra("topDisplayName", "Pending Modules");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void seeAllCatalog() {
        try {
            Log.d(TAG, "seeAllCatalog: ");
            if (OustSdkTools.checkInternetStatus() && myDeskInfoMap != null) {
                Log.e("TAG", "gotoCataloguePage landing page: myDesk--> " + myDeskInfoMap.size());

                //Intent intent = new Intent(this, CatalogDetailListActivity.class);
                // old Layout 3
                Intent intent = new Intent(this, CatalogDetailListActivity.class);

                // new Layout 4
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI)) {
                    intent = new Intent(this, CatalogueModuleListActivity.class);
                }
                intent.putExtra("hasDeskData", false);
                intent.putExtra("deskDataMap", myDeskInfoMap);
                intent.putExtra("overAllCatalogue", true);
                Log.d(TAG, "seeAllCatalog: myDeskInfoMap: " + myDeskInfoMap.size());
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLatestData() {
        try {
            if (new_landing_page_type.equals("Learn")) {
                createHorizontalListb(filterListByLearnType(latestList));
            } else if (new_landing_page_type.equals("Grow")) {
                createHorizontalListb(filterListByGrowType(latestList));
            } else {
                createHorizontalListb(filterListByHandPickedType(latestList));
            }
            newlandinghorizontal_recyclerviewa.setVisibility(View.GONE);
            newlandinghorizontal_recyclerviewc.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showTrendingData() {
        try {
            newlandinghorizontal_recyclerviewa.setVisibility(View.GONE);
            newlandinghorizontal_recyclerviewb.setVisibility(View.GONE);
            if (new_landing_page_type.equals("Learn")) {
                createHorizontalListc(filterListByLearnType(trendingList));
            } else if (new_landing_page_type.equals("Grow")) {
                createHorizontalListc(filterListByGrowType(trendingList));
            } else {
                createHorizontalListc(filterListByHandPickedType(trendingList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private List<CommonLandingData> filterListByHandPickedType
            (ArrayList<CommonLandingData> commonLandingDataArrayList) {
        try {
            ArrayList<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            if ((commonLandingDataArrayList != null) && (commonLandingDataArrayList.size() > 0)) {
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                myDeskFilterdData = commonLandingFilter.getSortedDataByHandPickedType(commonLandingDataArrayList);
                return myDeskFilterdData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    private void showMyDeskData() {
        try {
            newlandinghorizontal_recyclerviewb.setVisibility(View.GONE);
            newlandinghorizontal_recyclerviewc.setVisibility(View.GONE);
            List<CommonLandingData> myDeskFilterdData = new ArrayList<>();
            CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
            myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
            if (new_landing_page_type.equals("Learn")) {
                createHorizontalLista(filterListByLearnType(myDeskFilterdData));
            } else if (new_landing_page_type.equals("Grow")) {
                createHorizontalLista(filterListByGrowType(myDeskFilterdData));
            } else {
                createHorizontalLista(myDeskFilterdData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void seeAllCourses() {
        try {
            HashMap<String, CatalogDeatilData> catalogDeatilDataArrayList = new HashMap<>();
            if (new_landing_page_type.equals("All")) {
                CatalogDeatilData mydeskcatalogDeatilData = new CatalogDeatilData();
                if ((moduleDataArrayList.size() > 0) && (moduleDataArrayList.get(0) != null)) {
                    mydeskcatalogDeatilData.setTitle(moduleDataArrayList.get(0));
                } else {
                    mydeskcatalogDeatilData.setTitle("My Desk");
                }
                mydeskcatalogDeatilData.setId(0);
                mydeskcatalogDeatilData.setType("My Desk");
                CommonLandingFilter commonLandingFilter = new CommonLandingFilter();
                List<CommonLandingData> myDeskFilterdData = commonLandingFilter.pendingDataMeetCriteria(OustAppState.getInstance().getMyDeskList()).get();
                mydeskcatalogDeatilData.setCommonLandingDatas(((ArrayList<CommonLandingData>) myDeskFilterdData));
                catalogDeatilDataArrayList.put("My Desk", mydeskcatalogDeatilData);
            }

            CatalogDeatilData latestCatalogDeatilData = new CatalogDeatilData();
            if ((moduleDataArrayList.size() > 1) && (moduleDataArrayList.get(1) != null)) {
                latestCatalogDeatilData.setTitle(moduleDataArrayList.get(1));
            } else {
                latestCatalogDeatilData.setTitle("Latest");
            }
            latestCatalogDeatilData.setId(1);
            latestCatalogDeatilData.setType("Latest");
            if (new_landing_page_type.equals("All")) {
                latestCatalogDeatilData.setCommonLandingDatas((ArrayList<CommonLandingData>) filterListByHandPickedType(latestList));
            } else if (new_landing_page_type.equals("Learn")) {
                latestCatalogDeatilData.setCommonLandingDatas(filterListByLearnType(latestList));
            } else {
                latestCatalogDeatilData.setCommonLandingDatas(filterListByGrowType(latestList));
            }
            catalogDeatilDataArrayList.put("Latest", latestCatalogDeatilData);


            CatalogDeatilData trendingCatalogDeatilData = new CatalogDeatilData();
            if ((moduleDataArrayList.size() > 2) && (moduleDataArrayList.get(2) != null)) {
                trendingCatalogDeatilData.setTitle(moduleDataArrayList.get(2));
            } else {
                trendingCatalogDeatilData.setTitle("Trending");
            }
            trendingCatalogDeatilData.setId(2);
            trendingCatalogDeatilData.setType("Trending");
            if (new_landing_page_type.equals("All")) {
                trendingCatalogDeatilData.setCommonLandingDatas((ArrayList<CommonLandingData>) filterListByHandPickedType(trendingList));
            } else if (new_landing_page_type.equals("Learn")) {
                trendingCatalogDeatilData.setCommonLandingDatas(filterListByLearnType(trendingList));
            } else {
                trendingCatalogDeatilData.setCommonLandingDatas(filterListByGrowType(trendingList));
            }
            catalogDeatilDataArrayList.put("Trending", trendingCatalogDeatilData);

            OustDataHandler.getInstance().saveCollectionData(catalogDeatilDataArrayList);
            OustDataHandler.getInstance().setMyDeskData(myDeskData);
            Intent intent = new Intent(NewLandingActivity.this, CatalogDetailListActivity.class);
            intent.putExtra("hasDeskData", true);
            intent.putExtra("deskDataMap", myDeskInfoMap);
            startActivity(intent);
        } catch (Exception e) {
        }
    }


    private void setSelectedTabColors(TextView t1, TextView t2, TextView t3, int positionOfTodoText) {
        try {
            if (toolbarColorCode == null || toolbarColorCode.isEmpty()) {
                toolbarColorCode = OustPreferences.get("toolbarColorCode");
            }
            Log.d(TAG, "setSelectedTabColors: color:" + toolbarColorCode);
            mytask.setBackground(OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.course_button_bg)));
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                color = Color.parseColor(toolbarColorCode);
            }
            toptab_layout.setBackgroundColor(color);
            t1.setTextColor(OustSdkTools.getColorBack(R.color.whitelight));
            t1.setBackgroundColor(color);
            t2.setTextColor(color);
            t2.setBackgroundColor(OustSdkTools.getColorBack(R.color.whitelight));
            t3.setTextColor(color);
            t3.setBackgroundColor(OustSdkTools.getColorBack(R.color.whitelight));

            /*switch (positionOfTodoText)
            {
                case 1:
                    Log.d(TAG, "setSelectedTabColors: positoon 1");
                   t1.setTextColor(color);
                    break;
                case 2:
                    Log.d(TAG, "setSelectedTabColors: positoon 2");
                    t2.setTextColor(color);
                    break;
                case 3:
                    Log.d(TAG, "setSelectedTabColors: positoon 3");
                    t3.setTextColor(color);
                    break;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setSelectedSubTabColors(TextView t1, TextView t2, TextView
            t3, TextView
                                                 l1, TextView l2, TextView l3) {
        try {
            if (toolbarColorCode == null || toolbarColorCode.isEmpty()) {
                toolbarColorCode = OustPreferences.get("toolbarColorCode");
            }
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                color = Color.parseColor(toolbarColorCode);
            }
            if (toptab_layout != null)
                toptab_layout.setBackgroundColor(color);
            t1.setTextColor(color);
            l1.setBackgroundColor(color);
            t1.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            t2.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            t3.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            t2.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            l2.setBackgroundColor(OustSdkTools.getColorBack(R.color.Gray));
            t3.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            l3.setBackgroundColor(OustSdkTools.getColorBack(R.color.Gray));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sethilightedColor(TextView textView) {
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                textView.setTextColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideAllcoursetextlayout() {
        Animation event_animmoveout = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.event_animbackout);
        showAllcoursetextlayout.startAnimation(event_animmoveout);
        event_animmoveout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAllcoursetextlayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void showAllcoursetextlayout() {
        if (showAllcoursetextlayout.getVisibility() == View.GONE) {
            showAllcoursetextlayout.setVisibility(View.VISIBLE);
            Animation event_animbackin = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.event_animmovein);
            showAllcoursetextlayout.startAnimation(event_animbackin);
        }
    }

    private void cancleFabLayout() {
        if (floatingActionsMenu != null) {
            if (floatingActionsMenu.isExpanded()) {
                floatingActionsMenu.collapse();
                fabBaseLayout.setVisibility(View.GONE);
            }
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    //    public boolean isContainerApp=false;
    @Override
    public void onBackPressed() {
        try {
            if (mDrawerLayout.isDrawerOpen(drawer_listview)) {
                mDrawerLayout.closeDrawer(drawer_listview);
                return;
            }
            if (isWebviewOpen) {
                isWebviewOpen = false;
                showfirstbackWebViewAnim();
                return;
            }
            if (floatingActionsMenu != null && floatingActionsMenu.isExpanded()) {
                cancleFabLayout();
                return;
            }
            if (OustStaticVariableHandling.getInstance().getNewViewPager().getCurrentItem() != 0) {
                OustStaticVariableHandling.getInstance().getNewViewPager().setCurrentItem(0);
                return;
            }
            OustAppState.getInstance().setLandingPageOpen(false);
            if (OustStaticVariableHandling.getInstance().isContainerApp()) {
                if (doubleBackToExitPressedOnce) {
                    OustFirebaseTools.resetFirebase();
                    OustAppState.getInstance().clearAll();
                    this.finish();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }
                    System.exit(0);
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.exit_on_back), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                OustFirebaseTools.resetFirebase();
                OustAppState.getInstance().clearAll();
                NewLandingActivity.this.finish();
            }
            sendFeedClickedRequestToBackend();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFeedClickedRequestToBackend() {
        try {
            String clickFeedList = OustPreferences.get("clickFeedList");
            clickedFeedDataArrayList = OustSdkTools.getClickedFeedList(clickFeedList);

            String viewFeedList = OustPreferences.get("viewFeedLIst");
            viewedFeedDataMap = OustSdkTools.getViewedFeed(viewFeedList);

            if ((clickedFeedDataArrayList != null && clickedFeedDataArrayList.size() > 0)
                    || (viewedFeedDataMap != null && viewedFeedDataMap.size() > 0)) {


                ClickedFeedRequestData clickedFeedRequestData = new ClickedFeedRequestData();
                clickedFeedRequestData.setClickedFeedDataList(clickedFeedDataArrayList);
                if (viewedFeedDataMap != null && viewedFeedDataMap.size() > 0) {
                    if (alreadyAddedFeedDataMap != null) {
                        Map tmp = new HashMap(viewedFeedDataMap);
                        tmp.keySet().removeAll(alreadyAddedFeedDataMap.keySet());
                        alreadyAddedFeedDataMap.putAll(tmp);
                    } else {
                        alreadyAddedFeedDataMap = new HashMap(viewedFeedDataMap);
                    }

                    ArrayList<ViewedFeedData> viewedFeedDataArrayList = new ArrayList<ViewedFeedData>(viewedFeedDataMap.values());
                    clickedFeedRequestData.setViewdFeedDataList(viewedFeedDataArrayList);
                }
                if (activeUser != null) {
                    clickedFeedRequestData.setStudentid(activeUser.getStudentid());
                }
                Gson gson = new Gson();
                String str = gson.toJson(clickedFeedRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFeedClickedRequests");
                requests.add(str);
               /* clickedFeedDataArrayList = null;
                viewedFeedDataMap = null;*/
                OustPreferences.saveLocalNotificationMsg("savedFeedClickedRequests", requests);
              /*  Intent intent = new Intent(Intent.ACTION_SYNC, null, this, FeedBackService.class);
                OustSdkApplication.getContext().startService(intent);*/
                Intent feedService = new Intent(NewLandingActivity.this, FeedUpdatingServices.class);
                startService(feedService);

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onChange(String status) {
    }

    @Override
    protected void onDestroy() {
        ThreadPoolProvider.getInstance().shutDown();
//        isAppActive = false;
        Log.d(TAG, "onDestroy called");
        try {
            final ImageView toolbardleftimageb = findViewById(R.id.toolbardleftimageb);
            toolbardleftimageb.setImageBitmap(null);
            toolbardleftimageb.setTag(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        myDeskData = new HashMap<>();
        OustDataHandler.getInstance().resetData();
        OustAppState.getInstance().setLandingPageOpen(false);
        OustAppState.getInstance().setLandingPageLive(false);
        OustStaticVariableHandling.getInstance().setAppActive(false);
        OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<DTOUserFeeds.FeedList>());
        OustPreferences.saveTimeForNotification("lastNotificationOpenedTime", 0);
        sendFeedClickedRequestToBackend();
        super.onDestroy();
    }

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing List
/*
        String selectedLanguage = LanguagePreferences.get("appSelectedLanguage");
        if(selectedLanguage!=null&&!selectedLanguage.isEmpty()){
            Locale myLocale = new Locale(selectedLanguage);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            OustPreferences.save("appSelectedLanguage", selectedLanguage);
        }
*/

        return R.layout.activity_newlanding;
    }

   /* @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
    }*/

    @Override
    protected void initView() {
        try {
            Log.d(TAG, "initView: ");
            OustAppState.getInstance().setLandingPageLive(true);
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(getApplicationContext());
            }
            try {
                OustSdkTools.setLocale(NewLandingActivity.this);
                mNoOfTimesOpened = OustPreferences.getSavedInt(AppConstants.StringConstants.NO_TIMES_OPENED);
                mNoOfTimesOpened++;
                OustPreferences.saveintVar(AppConstants.StringConstants.NO_TIMES_OPENED, mNoOfTimesOpened);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            OustPreferences.save("yay_text", getResources().getString(R.string.yay_text));
            OustPreferences.save("have_earned", getResources().getString(R.string.have_earned));
            OustPreferences.save("coins_text", getResources().getString(R.string.coins_text));

            //Log.e("Landing Page", "app initialized" + "  " + OustSdkApplication.getContext());
            checkIncomingNotificationValidity();
            //Fabric.with(getApplicationContext(), new Crashlytics());
            //OustSdkTools.initializeRealm(NewLandingActivity.this);

            setPreferenceData();
            OustStaticVariableHandling.getInstance().setContestLive(false);
            allcommonLandingDataArrayList = OustStaticVariableHandling.getInstance().getReminderNotification();
            initViews();
            //initilizing listeners for bottomsheet items
            initListener2();
            initBottomSheetBannerData();
            //end for bottom sheet items

            if (((System.currentTimeMillis() / 1000) - (OustPreferences.getTimeForNotification("lastTokenRefreshTime"))) >= 3600) {
                OustFirebaseTools.initFirebase();
                OustPreferences.saveTimeForNotification("lastTokenRefreshTime", System.currentTimeMillis() / 1000);
                swiperefreshparent_layout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                showLoader();
                autheticateWithFirebase(false);
            } else {
                swiperefreshparent_layout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                showLoader();
                initLanding();
                Log.d(TAG, "OustStaticVariableHandling.getInstance().isForceUpgradePopupVisible()->" + OustStaticVariableHandling.getInstance().isForceUpgradePopupVisible());
            }

            if (!OustStaticVariableHandling.getInstance().isForceUpgradePopupVisible()) {
                Log.d(TAG, "Notification --- oldTIme:" + OustPreferences.getTimeForNotification("lastNotificationOpenedTime")
                        + " -- CurrentTIme:" + (System.currentTimeMillis() / 1000));
                //if (OustPreferences.getTimeForNotification("lastNotificationOpenedTime") == 0 || ((System.currentTimeMillis() / 1000) - (OustPreferences.getTimeForNotification("lastNotificationOpenedTime")) > 200)) {
                checkWhetherComingFormNotification();
                //}
            }
            multipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);

            initBranch();
            getFireBaseNotification();
//            OustGATools.getInstance().reportPageViewToGoogle(NewLandingActivity.this, "Oust Landing Page");

            this.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration configuration) {

                }

                @Override
                public void onLowMemory() {
                    Log.d(TAG, "onLowMemory: ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getFireBaseNotification() {
        ArrayList<NotificationResponse> fireBaseNotifications = new ArrayList<>();
        activeUser = OustAppState.getInstance().getActiveUser();
        String message = "/landingPage/" + activeUser.getStudentKey() + "/pushNotifications";
        ValueEventListener notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        if (snapshot.getValue() instanceof ArrayList) {
                            final ArrayList<Object> notificationListData = (ArrayList<Object>) snapshot.getValue();
                            ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                            for (Object module : notificationListData) {
                                final Object notificationData = module;
                                Gson gson = new Gson();
                                JsonElement notificationElement = gson.toJsonTree(notificationData);
                                NotificationResponse notificationResponse = gson.fromJson(notificationElement, NotificationResponse.class);
                                if (notificationResponse != null) {
                                    notificationResponseList.add(notificationResponse);
                                }
                            }
                            ArrayList<NotificationResponse> roomDbNotifications;
                            roomDbNotifications = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
                            fireBaseNotifications.clear();
                            if (notificationResponseList.size() > 0) {
                                if (roomDbNotifications.size() > 0) {
                                    for (int i = 0; i < roomDbNotifications.size(); i++) {
                                        if (roomDbNotifications.get(i).getFireBase() != null) {
                                            if (roomDbNotifications.get(i).getFireBase()) {
                                                fireBaseNotifications.add(roomDbNotifications.get(i));
                                            }
                                        }
                                    }
                                    if (notificationResponseList.size() > fireBaseNotifications.size()) {
                                        if (notificationAlert != null) {
                                            notificationAlert.setIcon(R.drawable.alertreddot_new);
                                        }
                                        // show
                                    } else {
                                        Log.e("TAG", "onDataChange--: " + "false");
                                    }
                                } else {
                                    if (notificationAlert != null) {
                                        notificationAlert.setIcon(R.drawable.alertreddot_new);
                                    }
                                    // show Notification icon
                                }
                            } else {
                                // hide Notification icon
                            }
                        } else if (snapshot.getValue() instanceof HashMap) {
                            final HashMap<Long, Object> notificationListData = (HashMap<Long, Object>) snapshot.getValue();
                            ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                            for (DataSnapshot module : snapshot.getChildren()) {
                                final Object notificationData = module.getValue();
                                Gson gson = new Gson();
                                JsonElement notificationElement = gson.toJsonTree(notificationData);
                                NotificationResponse notificationResponse = gson.fromJson(notificationElement, NotificationResponse.class);
                                if (notificationResponse != null) {
                                    notificationResponseList.add(notificationResponse);
                                }
                            }
                            ArrayList<NotificationResponse> roomDbNotifications = new ArrayList<>();
                            roomDbNotifications.clear();
                            roomDbNotifications = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
                            Log.e("TAG", "onDataChange roomDbNotifications: " + roomDbNotifications.size());
                            Log.e("TAG", "onDataChange notificationResponseList: " + notificationResponseList.size());
                            fireBaseNotifications.clear();
                            if (notificationResponseList.size() > 0) {
                                if (roomDbNotifications.size() > 0) {
                                    for (int i = 0; i < roomDbNotifications.size(); i++) {
                                        if (roomDbNotifications.get(i).getFireBase() != null) {
                                            if (roomDbNotifications.get(i).getFireBase()) {
                                                fireBaseNotifications.add(roomDbNotifications.get(i));
                                            }
                                        }
                                    }
                                    Log.e("TAG", "onDataChange fireBaseNotifications: " + fireBaseNotifications.size());
                                    if (notificationResponseList.size() > fireBaseNotifications.size()) {
                                        if (notificationAlert != null) {
                                            notificationAlert.setIcon(R.drawable.alertreddot_new);
                                        }
                                        // show
                                    } else {
                                        Log.e("TAG", "onDataChange--: " + "false");
                                    }
                                } else {
                                    if (notificationAlert != null) {
                                        notificationAlert.setIcon(R.drawable.alertreddot_new);
                                    }
                                    // show Notification icon
                                }
                            } else {
                                // hide Notification icon
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).orderByChild("updateTime").addValueEventListener(notificationListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(notificationListener, message));
    }

    @Override
    protected void initData() {
      /*  checkIncomingNotificationValidity();
        OustSdkTools.initializeRealm(NewLandingActivity.this);
        //Fabric.with(getApplicationContext(), new Crashlytics());
        OustSdkTools.initializeRealm(NewLandingActivity.this);
        setPreferenceData();
        OustStaticVariableHandling.getInstance().setContestLive(false);
        initViews();
        //initilizing listeners for bottomsheet items
        initListener2();
        initBottomSheetBannerData();
        //end for bottom sheet items

        if (((System.currentTimeMillis() / 1000) - (OustPreferences.getTimeForNotification("lastTokenRefreshTime"))) >= 3600) {
            OustFirebaseTools.initFirebase();
            OustPreferences.saveTimeForNotification("lastTokenRefreshTime", System.currentTimeMillis() / 1000);
            swiperefreshparent_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            showLoader();
            autheticateWithFirebase(false);
        } else {
            swiperefreshparent_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            showLoader();
            initLanding();
            if (!OustStaticVariableHandling.getInstance().isForceUpgradePopupVisible()) {
                checkWhetherComingFormNotification();
            }
        }
        initBranch();
        OustGATools.getInstance().reportPageViewToGoogle(NewLandingActivity.this, "Oust Landing Page");
*/
    }

    @Override
    protected void initListener() {

    }

    //=============================================================================
    //f3c related methodes
    private FastestFingerContestData fastestFingerContestData;

    public void getUserFFContest() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/f3c";
            Log.d(TAG, "getUserFFContest: " + message);
            ValueEventListener myFFCListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        if (dataSnapshot.getValue() != null) {
                            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)) {
                                isContest = true;

                            }
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> ffcMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (ffcMap.get("elementId") != null) {
                                    long contestId = (long) ffcMap.get("elementId");
                                    //
                                    if (fastestFingerContestData == null) {
                                        fastestFingerContestData = new FastestFingerContestData();
                                    }
                                    int lastContestId = OustPreferences.getSavedInt("lastContestTime");
                                    if ((lastContestId > 0) && (lastContestId != contestId)) {
                                        OustPreferences.clear("contestScore");
                                    }
                                    OustPreferences.saveintVar("lastContestTime", (int) contestId);
                                    if (fastestFingerContestData.getFfcId() != contestId) {
                                        fastestFingerContestData = new FastestFingerContestData();
                                        fastestFingerContestData.setFfcId(contestId);
                                        OustStaticVariableHandling.getInstance().setContestOver(false);
                                        removeFFCDataListener();
                                        fetctFFCData(("" + fastestFingerContestData.getFfcId()));
                                        fetchQData(("" + fastestFingerContestData.getFfcId()));
                                    }
                                    if (ffcMap.get("enrolled") != null) {
                                        fastestFingerContestData.setEnrolled((boolean) ffcMap.get("enrolled"));
                                        ffcBannerStatus();
                                    }

                                    Log.e("PushNotification", "" + OustPreferences.getAppInstallVariable("sendPushNotifications"));
                                    if (OustPreferences.getAppInstallVariable("sendPushNotifications")) {
                                        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                        Intent f3cAlarmIntent = new Intent(context, CourseNotificationReceiver.class);
//                                        PendingIntent f3cPendingIntent = PendingIntent.getService(context, 0, f3cAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        PendingIntent f3cPendingIntent;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            f3cPendingIntent = PendingIntent.getService(context, 0, f3cAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                                        } else {
                                            f3cPendingIntent = PendingIntent.getService(context, 0, f3cAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        }
                                        Calendar f3cCalendar = Calendar.getInstance();
                                        f3cCalendar.setTimeInMillis(System.currentTimeMillis());
                                        manager.setRepeating(AlarmManager.RTC_WAKEUP, f3cCalendar.getTimeInMillis(), 30 * 1000, f3cPendingIntent);
                                    }


                                }
                            }
                            initBottomSheetBannerData();
                        } else {
                            //Log.d(TAG, "onDataChange: no f3c data");
                            isContest = false;
                            OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);
                            initBottomSheetBannerData();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };


            DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
            newsfeedRef.keepSynced(true);
            newsfeedRef.addValueEventListener(myFFCListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myFFCListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getFFCEnrolldedUsersCount(String contestId) {
        final String path = "/f3cEnrolledUserCount/f3c" + contestId + "/participants";
        DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    fastestFingerContestData.setEnrolledCount((long) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error:");
            }
        });
    }

    private FirebaseRefClass ffcDataRefClass;
    private FirebaseRefClass ffcQDataRefClass;

    private void removeFFCDataListener() {
        try {
            if (ffcDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcDataRefClass.getFirebasePath()).removeEventListener(ffcDataRefClass.getEventListener());
            }
            if (ffcQDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcQDataRefClass.getFirebasePath()).removeEventListener(ffcQDataRefClass.getEventListener());
            }
        } catch (Exception e) {
        }
    }

    public void fetctFFCData(String ffcId) {
        try {
            final String message = "/f3cData/f3c" + ffcId;
            Log.d(TAG, "fetctFFCData: " + message);
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    extractFFCData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            ffcDataRefClass = new FirebaseRefClass(ffcDataListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ffcDataListener, message));

            getFFCEnrolldedUsersCount(ffcId);

        } catch (Exception e) {
        }
    }

    private void extractFFCData(DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot != null) {
                final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                if (null != ffcDataMap) {
                    try {
                        fastestFingerContestData = OustSdkTools.getFastestFingerContestData(fastestFingerContestData, ffcDataMap);
                        setF3cBannerSize();
                        long bannerHideTimeNo = 1;
                        if (ffcDataMap.get("bannerHideTime") != null) {
                            bannerHideTimeNo = (long) ffcDataMap.get("bannerHideTime");
                        }
                        long bannerHideTime = (bannerHideTimeNo * (86400000));
                        if ((System.currentTimeMillis() - fastestFingerContestData.getStartTime()) > bannerHideTime) {
                        } else {
                            OustStaticVariableHandling.getInstance().setContestLive(true);
                            //showFFcBanner();
//                            if ((notificationContestId != null) && (!notificationContestId.isEmpty()) && (notificationContestId.equalsIgnoreCase(("" + fastestFingerContestData.getFfcId())))) {
//                                notificationContestId="";
//                                Intent intent = new Intent(NewLandingActivity.this, FFcontestStartActivity.class);
//                                Gson gson = new Gson();
//                                intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
//                                startActivity(intent);
//                            }
                        }
                        setContestNotificationData(ffcDataMap);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setContestNotificationData(Map<String, Object> ffcDataMap) {
        try {
            if (OustStaticVariableHandling.getInstance().isContestLive()) {
                String contestnotification_message = OustPreferences.get("contestnotification_message");
                Gson gson = new Gson();
                ContestNotificationMessage contestNotificationMessage;
                if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                    contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                    if (contestNotificationMessage.getContestID() != fastestFingerContestData.getFfcId()) {
                        contestNotificationMessage = new ContestNotificationMessage();
                    }
                } else {
                    contestNotificationMessage = new ContestNotificationMessage();
                }
                contestNotificationMessage.setContestID((fastestFingerContestData.getFfcId()));
                contestNotificationMessage.setStartTime(fastestFingerContestData.getStartTime());
                contestNotificationMessage.setContestName(fastestFingerContestData.getName());
                contestNotificationMessage.setStudentId(activeUser.getStudentid());
                contestNotificationMessage.setAvatar(activeUser.getAvatar());
                contestNotificationMessage.setDisplayName(activeUser.getUserDisplayName());
                contestNotificationMessage.setJoinBanner(fastestFingerContestData.getJoinBanner());
                contestNotificationMessage.setPlayBanner(fastestFingerContestData.getPlayBanner());
                contestNotificationMessage.setRrBanner(fastestFingerContestData.getRrBanner());
                contestNotificationMessage.setRegistered(fastestFingerContestData.isEnrolled());
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
                ffcBannerStatus();
            } else {
                OustPreferences.clear("contestnotification_message");
            }
        } catch (Exception e) {
        }
    }

    private void ffcBannerStatus() {
        try {
            Log.d(TAG, "ffcBannerStatus: " + OustStaticVariableHandling.getInstance().isContestLive());
            if (OustStaticVariableHandling.getInstance().isContestLive()) {
                String contestnotification_message = OustPreferences.get("contestnotification_message");
                ffcbanner_statusimg.setVisibility(View.GONE);
                if ((fastestFingerContestData.isEnrolled())) {
                    //cpl_main_ll.setVisibility(View.GONE);
                    if ((fastestFingerContestData.getPlayBanner() != null) && (!fastestFingerContestData.getPlayBanner().isEmpty())) {
                        showFFcBanner(fastestFingerContestData.getPlayBanner());
                    } else {
                        if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                            showFFcBanner(fastestFingerContestData.getJoinBanner());
                        }
                    }
                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    showFFcBanner(fastestFingerContestData.getRrBanner());
                                }
                            }
                        }
                    }
                } else {
                    if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                        showFFcBanner(fastestFingerContestData.getJoinBanner());
                    }
                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    showFFcBanner(fastestFingerContestData.getRrBanner());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showFFcBanner(String bannerStr) {
//            if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
//                ffcbanner_imglayout.setVisibility(View.GONE);
//            } else {
        Log.d(TAG, "showFFcBanner: " + bannerStr);
        ffcBannerURL = bannerStr;
        if ((bannerStr != null) && (!bannerStr.isEmpty()) && isContest && !isPlayList && !isTodo) {
            ffcbanner_imglayout.setVisibility(View.VISIBLE);
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(bannerStr).into(ffcbanner_img);
            } else {
                Picasso.get().load(bannerStr).networkPolicy(NetworkPolicy.OFFLINE).into(ffcbanner_img);
            }
        } else if ((bannerStr != null) && (!bannerStr.isEmpty()) && !isContest && fastestFingerContestData != null && !isPlayList && !isTodo) {

            ffcbanner_imglayout.setVisibility(View.VISIBLE);
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(bannerStr).into(ffcbanner_img);
            } else {
                Picasso.get().load(bannerStr).networkPolicy(NetworkPolicy.OFFLINE).into(ffcbanner_img);
            }
        }
//            }
    }

    private void setF3cBannerSize() {
        int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen8);
        setLayoutAspectRatiosmall(ffcbanner_imglayout, 0);
    }

    private void fetchQData(String ffcId) {
        try {
            final String message = "/f3cQData/f3c" + ffcId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (null != ffcDataMap) {
                                List<BasicQuestionClass> basicQuestionClassList = new ArrayList<>();
                                if (ffcDataMap.get("questions") != null) {
                                    Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("questions");
                                    if (questionMap != null) {
                                        for (String key : questionMap.keySet()) {
                                            Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                            if (questionSubMap != null) {
                                                BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                                if (questionSubMap.get("qId") != null) {
                                                    basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                                }
                                                if (questionSubMap.get("sequence") != null) {
                                                    basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                                }
                                                basicQuestionClassList.add(basicQuestionClass);
                                            }
                                        }
                                    }
                                    Collections.sort(basicQuestionClassList, questionSorter);
                                }
                                List<BasicQuestionClass> basicWarmUpQuestionClassList = new ArrayList<>();
                                if (ffcDataMap.get("warmupQuestions") != null) {
                                    Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("warmupQuestions");
                                    if (questionMap != null) {
                                        for (String key : questionMap.keySet()) {
                                            Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                            if (questionSubMap != null) {
                                                BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                                if (questionSubMap.get("qId") != null) {
                                                    basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                                }
                                                if (questionSubMap.get("sequence") != null) {
                                                    basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                                }
                                                basicWarmUpQuestionClassList.add(basicQuestionClass);
                                            }
                                        }
                                    }
                                    Collections.sort(basicWarmUpQuestionClassList, questionSorter);
                                }
                                long updateChecksum = 0;
                                if (ffcDataMap.get("updateChecksum") != null) {
                                    updateChecksum = (long) ffcDataMap.get("updateChecksum");
                                }
                                boolean update = true;
                                if ((updateChecksum > 0) && (OustPreferences.getTimeForNotification("updateChecksum") > 0) && (updateChecksum == OustPreferences.getTimeForNotification("updateChecksum"))) {
                                    update = false;
                                }
                                List<String> qList = new ArrayList<>();
                                for (int i = 0; i < basicQuestionClassList.size(); i++) {
                                    if (basicQuestionClassList.get(i).getqId() > 0) {
                                        if (update) {
                                            downloadQuestion(("" + basicQuestionClassList.get(i).getqId()), updateChecksum);
                                        }
                                        qList.add(("" + basicQuestionClassList.get(i).getqId()));
                                    }
                                }
                                fastestFingerContestData.setqIds(qList);
                                List<String> warmUpQList = new ArrayList<>();
                                for (int i = 0; i < basicWarmUpQuestionClassList.size(); i++) {
                                    if (basicWarmUpQuestionClassList.get(i).getqId() > 0) {
                                        if (update) {
                                            downloadQuestion(("" + basicWarmUpQuestionClassList.get(i).getqId()), updateChecksum);
                                        }
                                        warmUpQList.add(("" + basicWarmUpQuestionClassList.get(i).getqId()));
                                    }
                                }
                                fastestFingerContestData.setWarmupQList(warmUpQList);
                                if ((notificationContestId != null) && (!notificationContestId.isEmpty()) && (notificationContestId.equalsIgnoreCase(("" + fastestFingerContestData.getFfcId())))) {
                                    notificationContestId = "";
                                    Intent intent = new Intent(NewLandingActivity.this, FFcontestStartActivity.class);
                                    Gson gson = new Gson();
                                    intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
                                    startActivity(intent);
                                }
                                setContestNotificationData(ffcDataMap);
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            ffcQDataRefClass = new FirebaseRefClass(ffcDataListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ffcDataListener, message));
        } catch (Exception e) {
        }
    }

    public Comparator<BasicQuestionClass> questionSorter = new Comparator<BasicQuestionClass>() {
        public int compare(BasicQuestionClass s1, BasicQuestionClass s2) {
            return ((int) s1.getSequence()) - ((int) s2.getSequence());
        }
    };

    private void downloadQuestion(final String qId, final long updateChecksum) {
        try {
            final String message = "/questions/Q" + qId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Map<String, Object> questionMap = (Map<String, Object>) dataSnapshot.getValue();
                        EncrypQuestions encrypQuestions = new EncrypQuestions();
                        if (questionMap != null) {
                            if (questionMap.get("image") != null) {
                                encrypQuestions.setImage((String) questionMap.get("image"));
                            }
                            if (questionMap.get("encryptedQuestions") != null) {
                                encrypQuestions.setEncryptedQuestions((String) questionMap.get("encryptedQuestions"));
                            }
                            DTOQuestions questions = OustSdkTools.decryptQuestion(encrypQuestions, null);
                            try {
                                if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                                    questions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                                    questions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                                    questions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                                    questions.setQuestionType(QuestionType.LONG_ANSWER);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            OustSdkTools.databaseHandler.addToRealmQuestions(questions, true);
                            OustPreferences.saveTimeForNotification("updateChecksum", updateChecksum);

                            OustPreferences.saveTimeForNotification("updateChecksum", updateChecksum);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(ffcDataListener);
        } catch (Exception e) {
        }
    }

    private void showBannerWithAnimation() {
        isBannerSmall = false;
        bannerclose_btn.setVisibility(View.VISIBLE);
        ffcbanner_imglayout.setPivotX(0);
        ffcbanner_imglayout.setPivotY(ffcbanner_imglayout.getHeight());
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ffcbanner_imglayout, "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ffcbanner_imglayout, "scaleY", 0.3f, 1.0f);
        scaleDownX.setDuration(250);
        scaleDownY.setDuration(250);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    private boolean isBannerSmall = false;

    private void hideBannerWithAnimation() {
        isBannerSmall = true;
        bannerclose_btn.setVisibility(View.GONE);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ffcbanner_imglayout, "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ffcbanner_imglayout, "scaleY", 1.0f, 0.3f);
        ffcbanner_imglayout.setPivotX(0);
        ffcbanner_imglayout.setPivotY(ffcbanner_imglayout.getHeight());
        scaleDownX.setDuration(250);
        scaleDownY.setDuration(250);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }


    private void startZoom(final boolean joinMeeting) {
        joinMeeting();
    }

    private String zoom_meetingId;

    private void joinMeeting() {
        Intent intent = new Intent(this, ZoomBaseActivity.class);
        intent.putExtra("joinMeeting", true);
        startActivity(intent);
    }

    private void startJoiningMeetingAfterSomeTime() {
        try {
            if ((zoom_meetingId != null) && (!zoom_meetingId.isEmpty())) {
                if ((zoom_meetingId.length() > 8) && (zoom_meetingId.length() < 12)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                boolean isAppInstalled = appInstalledOrNot("com.oustme.oustlive");
                                if (isAppInstalled) {
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                                    intent.putExtra("zoommeetingId", zoom_meetingId);
                                    intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                                    intent.putExtra("isComingThroughOust", true);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(NewLandingActivity.this, ZoomBaseActivity.class);
                                    intent.putExtra("joinMeeting", true);
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }, 2000);
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.invalid_meeting_id));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }


    private FirebaseRefClass alertFirebaseRefClass;
    private ArrayList<DTONewFeed> newFeedList = new ArrayList<>();
    ArrayList<DTONewFeed> allFeedList;
    private ArrayList<DTONewFeed> filteredFeedList;
    //private NewLandingAlertAdapter newFeedAdapter;
    private FeedsRecyclerAdapter newFeedAdapter;
    private List<DTONewFeed> userNewFeedList3;
    private ArrayList<Long> removedFeedPosition;
    private ArrayList<Long> expiredFeedPosition = new ArrayList<>();
    int position = 0;

    public void getAllUserNewsFeed() {
        try {
            String message = "/userFeed/" + activeUser.getStudentKey();
            ValueEventListener newsfeedListListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        noofListenerSetForFeed3 = 0;
                        userNewFeedList3 = new ArrayList<>();
                        removedFeedPosition = new ArrayList<>();
                        position = 0;
                        if (dataSnapshot != null) {
                            final Map<String, Object> newsfeedMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (newsfeedMap != null && newsfeedMap.size() != 0) {
                                for (String newsId : newsfeedMap.keySet()) {
                                    final HashMap<String, Object> mymap = (HashMap<String, Object>) newsfeedMap.get(newsId);
                                    if ((mymap != null)) {
                                        DTONewFeed newFeed = new DTONewFeed();
                                        long feedID = 0;
                                        long timeStamp = 0;
                                        long feedExpiry = 0;
                                        if ((mymap.get("feedId") != null)) {
                                            feedID = (long) mymap.get("feedId");
                                            newFeed.setFeedId(feedID);

                                            //  Log.d(TAG, "onDataChange: myfeedID"+feedID);
                                        }
                                        if ((mymap.get("timeStamp")) != null) {
                                            timeStamp = (long) mymap.get("timeStamp");
                                            newFeed.setTimestamp(timeStamp);
                                        }
                                        if ((mymap.get("feedExpiry")) != null) {
                                            feedExpiry = OustSdkTools.convertToLong((mymap.get("feedExpiry")));
                                            newFeed.setExpiryTime(feedExpiry);
                                        }
                                        if ((mymap.get("isLiked")) != null) {
                                            newFeed.setLiked((boolean) mymap.get("isLiked"));
                                        }
                                        if ((mymap.get("isShared")) != null) {
                                            newFeed.setShared((boolean) mymap.get("isShared"));
                                        }
                                        if ((mymap.get("isCommented")) != null) {
                                            newFeed.setCommented((boolean) mymap.get("isCommented"));
                                        }
                                        if (mymap.get(AppConstants.StringConstants.IS_FEED_CLICKED) != null) {
                                            newFeed.setClicked((boolean) mymap.get(AppConstants.StringConstants.IS_FEED_CLICKED));
                                        } else {
                                            newFeed.setClicked(true);
                                        }

                                        if (mymap.get(AppConstants.StringConstants.IS_FEED_VIEWED) != null) {
                                            newFeed.setFeedViewed((boolean) mymap.get(AppConstants.StringConstants.IS_FEED_VIEWED));
                                        }

                                        if (mymap.get(AppConstants.StringConstants.FEED_COINS_ADDED) != null) {
                                            newFeed.setFeedCoinsAdded((boolean) mymap.get(AppConstants.StringConstants.FEED_COINS_ADDED));
                                        }

                                        boolean skipFeed = false;
                                        if (mymap.get("userCompletionPercentage") != null) {
                                            long userCompletion = OustSdkTools.convertToLong(mymap.get("userCompletionPercentage"));
                                            if (userCompletion >= 100) {
                                                skipFeed = true;
                                                removedFeedPosition.add(feedID);
                                            }
                                        }
                                        if (!skipFeed && timeStamp != 0) {
                                            userNewFeedList3.add(newFeed);
                                        }

                                        position++;
                                        //newFeedHashMap.put(feedID + "", newFeed);
                                    }
                                }
                                sortAllFeeds();
                            } else {
                                isFilterAvailable = false;
                                filter_ll.setVisibility(View.GONE);
                                getAllAlertsForLayout3();
                            }
                        } else {
                            isFilterAvailable = false;
                            filter_ll.setVisibility(View.GONE);
                            getAllAlertsForLayout3();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                }
            };
            DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
            Query query = newsfeedRef.orderByChild("timeStamp");

            query.keepSynced(true);
            query.addValueEventListener(newsfeedListListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
            // alertFirebaseRefClass = new FirebaseRefClass(newsfeedListListener, message);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean feedDataInit = false;

    private void sortAllFeeds() {
        for (int i = 0; i < userNewFeedList3.size(); i++) {
            newFeedHashMap.put("" + userNewFeedList3.get(i).getFeedId(), userNewFeedList3.get(i));
        }
        if (localFeedCount > 2) {
            feedDataInit = true;
        }

        try {
            if (newFeedHashMap.size() == 0) {
                isFilterAvailable = false;
                filter_ll.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


        setNewFeedListener();
    }

    private int localFeedCount = 0;
    private int noofFeedsLoaded;

    private void setNewFeedListener() {
        int totalCount = newFeedHashMap.size();
        if (!feedDataInit) {
            if (localFeedCount + 10 < totalCount) {
                localFeedCount += 10;
            } else {
                localFeedCount = totalCount;
                OustDataHandler.getInstance().setAllFeedsLoaded(true);
            }
        } else {
            feedDataInit = false;
        }
        noofListenerSetForFeed3 = 0;
        noofFeedsLoaded = 0;
        ArrayList<DTONewFeed> newFeeds = new ArrayList<DTONewFeed>(newFeedHashMap.values());
        Collections.sort(newFeeds, newsFeedSorter);

        int startCount = (int) (((Math.ceil((localFeedCount * 1.0) / 10)) - 1) * 10);
        for (int i = startCount; i < localFeedCount; i++) {
            DTONewFeed newFeed = newFeeds.get(i);
            if (!newFeed.isListenerSet()) {
                noofListenerSetForFeed3++;
                String message = "/feeds/feed" + newFeed.getFeedId();
                Log.d(TAG, "setNewFeedListener: " + message);
                ValueEventListener newsfeedListListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() != null) {
                                noofFeedsLoaded++;
                                String key = dataSnapshot.getKey();
                                key = key.replace("feed", "");
                                DTONewFeed feed1 = newFeedHashMap.get(key);
                                feed1.setListenerSet(true);
                                long feedExpiry = 0;
                                feedExpiry = feed1.getExpiryTime();

                                final HashMap<String, Object> newsfeedMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                if (newsfeedMap != null) {
                                    Log.d(TAG, "onDataChange: news feed");
                                    CommonTools commonTools = new CommonTools();
                                    feed1 = commonTools.getNewFeedFromMap(newsfeedMap, feed1);
                                    feed1.setFeedId(Long.parseLong(key));
                                    //Log.d(TAG, "onDataChange parentCPL Id: " + feed1.getParentCplId());
                                    if (feedExpiry != 0) {
                                        feed1.setExpiryTime(feedExpiry);
                                    }
                                    newFeedHashMap.put(key, feed1);

                                    /*if (feed1.getFeedTag() != null)
                                        Log.d(TAG, "onDataChange: feedtag:" + feed1.getFeedTag());*/
                                }
                            } else {
                                noofListenerSetForFeed3--;
                            }
                        } catch (Exception e) {
                            noofListenerSetForFeed3--;
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        if (noofFeedsLoaded >= noofListenerSetForFeed3) {

                            if (!searchOn) {
                                getAllAlertsForLayout3();
                            } else
                                filterNewLandingFeed(searchText);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError DatabaseError) {
                    }
                };
                OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(newsfeedListListener);
                OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            }
        }
    }

    private void getAllAlertsForLayout3() {
        try {
            if (action_alert != null)
                action_alert.setVisible(false);
            allFeedList = new ArrayList<>();
            allFeedList = new ArrayList<>(newFeedHashMap.values());
            if (allFeedList.size() > 0) {
                Collections.sort(allFeedList, newsFeedSorter);
                int startCount = (int) (((Math.ceil((localFeedCount * 1.0) / 10)) - 1) * 10);
                allFeedList = new ArrayList<>(allFeedList.subList(startCount, localFeedCount));
                if (allFeedList.size() >= 4) {
                    if (actionSearch != null)
                        actionSearch.setVisible(true);
                }
                newFeedList = allFeedList;
                initAlerts();
                isFilterAvailable = true;
            } else {

                //  no_data_text.setText(getResources().getString(R.string.no_feed_assigned"));
                if (newsfeed_recyclerview.getVisibility() == View.VISIBLE) {
                    no_data_text.setVisibility(View.VISIBLE);
                    newsfeed_recyclerview.setVisibility(View.GONE);
                    no_data_text.setText(getResources().getString(R.string.no_feed_assigned));
                    no_data_text.setVisibility(View.VISIBLE);
                }
                isFilterAvailable = false;
                filter_ll.setVisibility(View.GONE);
                hideLoader();
            }
            setRefreshLayoutVisibility();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private ArrayList<DTONewFeed> getTotalFeeds() {
        ArrayList<DTONewFeed> allFeedList = new ArrayList<>();
        try {
            allFeedList = new ArrayList<DTONewFeed>(newFeedHashMap.values());
            if (allFeedList != null && allFeedList.size() > 0) {
                int startCount = (int) (((Math.ceil((localFeedCount * 1.0) / 10)) - 1) * 10);
                allFeedList = new ArrayList<>(allFeedList.subList(startCount, localFeedCount));
                Collections.sort(allFeedList, newsFeedSorter);
                Collections.sort(allFeedList, prioritySorter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return allFeedList;
    }

    public Comparator<DTONewFeed> prioritySorter = new Comparator<DTONewFeed>() {
        public int compare(DTONewFeed s1, DTONewFeed s2) {
            return Long.valueOf(s2.getFeedPriority()).compareTo(Long.valueOf(s1.getFeedPriority()));
        }
    };

    public void initAlerts() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser != null) {
//                newFeedList = OustAppState.getInstance().getNewsFeeds();
                // getImagePaths();
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.GONE);
                }
                OustPreferences.saveTimeForNotification("lastalerttime", (newFeedList.get(0).getTimestamp()));
                Log.e(TAG, "updateShareCount: feedArrayList 5-> " + newFeedList.size());
                createList(newFeedList, false, true);


            } else {
                try {
                    isRefreshing = true;
                    refresh_layout.setClickable(false);
                    refresh_layout.setVisibility(View.GONE);
                    refreshLayoutC();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //int n1 = 2;
            //int n2 = n1 + 4;
        }
    }


    private void getImagePaths() {
        ids = new ArrayList<>();
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {

            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                // OustSdkTools.showToast(message);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {

            }

            @Override
            public void onAddedToQueue(String id) {
                ids.add(id);
            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {
                if (code == _COMPLETED) {
                    String getId = ids.get(ids.indexOf(id));
                    if (getId != null) {
                        ids.remove(id);
                        updateGifDownloaded(true);
                    }
                }
            }
        });
        for (int i = 0; i < newFeedList.size(); i++) {
            DTOCourseCard courseCardClass = newFeedList.get(i).getCourseCardClass();
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                for (int j = 0; j < courseCardClass.getCardMedia().size(); j++) {
                    DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(j);
                    if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                        if (courseCardMedia.getMediaType().equals("IMAGE")) {
                            getSignedUrl("course/media/image/" + courseCardMedia.getData(), i);
                        } else if (courseCardMedia.getMediaType().equals("GIF")) {
                            downloadFeedGif("course/media/gif/" + courseCardMedia.getData(), courseCardMedia.getData());
                        }
                    }
                }
            }
        }
    }

    private void downloadFeedGif(String imagePath, final String filename) {
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + filename)) {
                final File file = new File(getFilesDir(), "oustlearn_" + filename);
                downloadFiles.startDownLoadGif(file.toString(), AppConstants.StringConstants.S3_BUCKET_NAME, imagePath, true, true);

               /* AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
                s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
                TransferUtility transferUtility = new TransferUtility(s3, this);
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", imagePath, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, filename, true);
                        } else if (state == TransferState.FAILED) {
                            Log.e(TAG, "doiwnload failed");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        ex.printStackTrace();
                    }
                });*/
            }

        } catch (Exception e) {
            Log.e(TAG, "downloadImage" + e.getMessage());
        }
    }

    private void downloadImages(String imagePath, final String filename) {
        try {
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {

                }

                @Override
                public void onDownLoadError(String message, int errorCode) {

                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {

                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });

            //final File file = new File(getFilesDir(), "oustlearn_" + filename);

            downloadFiles.startDownLoad(imagePath, getFilesDir() + "/", filename, true, false);

            //downloadFiles.startDownLoad(file.toString(), S3_BUCKET_NAME,resPath, false, true );

            //downloadFiles.startDownLoad(file.toString(), S3_BUCKET_NAME, imagePath, false, true);

               /* AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
                s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
                TransferUtility transferUtility = new TransferUtility(s3, this);
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", imagePath, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, filename, true);
                        } else if (state == TransferState.FAILED) {
                            Log.e(TAG, "doiwnload failed");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        ex.printStackTrace();
                    }
                });*/

        } catch (Exception e) {
            Log.e(TAG, "downloadImage" + e.getMessage());
        }
    }

    private void getSignedUrl(String objectKey, int feedNo) {
        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += (2000000);
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(AppConstants.MediaURLConstants.BUCKET_NAME, objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
        generatePresignedUrlRequest.setExpiration(expiration);
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        if (url != null) {
            url.toString().replaceAll("https://", "http://");
        }
        String signedPath = url.toString();
        newFeedList.get(feedNo).setTempSignedImage(signedPath);
        Log.e(TAG, "updateShareCount: feedArrayList 5-> " + newFeedList.size());
        createList(newFeedList, false, false);
    }

    public void saveData(File file, String fileName1, boolean isGifFeed) {
        try {
            removeFile();
            if (isGifFeed) {
                if (newFeedAdapter != null) {
                    newFeedAdapter.notifyFeedChange(newFeedList, false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateGifDownloaded(boolean isGifFeed) {
        try {
            removeFile();
            if (isGifFeed) {
                if (newFeedAdapter != null) {
                    newFeedAdapter.notifyFeedChange(newFeedList, false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeFile() {
//        if (mediaSize > 0) {
//            mediaSize--;
//        }
//        if (mediaSize == 0) {
//            downloadComplete();
//        }
    }

    private ArrayList<ClickedFeedData> clickedFeedDataArrayList;
    private ArrayList<ViewedFeedData> viewedFeedDataArrayList;
    private HashMap<String, ViewedFeedData> viewedFeedDataMap;
    private HashMap<String, ViewedFeedData> alreadyAddedFeedDataMap;

    private void createList(ArrayList<DTONewFeed> newFeedList1, boolean isSearchList, boolean isSortReq) {
        if (currentTabName.equals("FEEDS")) {
            no_data_text.setVisibility(View.GONE);
            newsfeed_recyclerview.setVisibility(View.VISIBLE);
        }

        if (!isFilterAvailable) {
            filter_ll.setVisibility(View.GONE);
        }
        expiredFeedPosition = new ArrayList<>();
        ArrayList<DTONewFeed> newFeedListCopy = new ArrayList<>(newFeedList1);
        for (int i = 0; i < newFeedListCopy.size(); i++) {
            if ((newFeedListCopy.get(i).getExpiryTime() != 0 && newFeedListCopy.get(i).getExpiryTime() < System.currentTimeMillis()) || newFeedListCopy.get(i).getFeedId() == 0) {
                expiredFeedPosition.add(newFeedListCopy.get(i).getFeedId());
                newFeedList1.remove(newFeedListCopy.get(i));
            }
        }

        sendExpiredFeeds(expiredFeedPosition);
        hideLoader();
        hideAlertIcon();
        try {
            newsfeed_recyclerview.setVisibility(View.VISIBLE);
            if (newFeedList1.size() > 0) {

                OustPreferences.saveintVar("filterTypeFeed", filterType);
                OustPreferences.save("filterTagFeed", filterTag);

                if (!isSearchList) {

                    if (filterType > 0) {
                        isFilterAvailable = true;

                        newsfeed_recyclerview.removeAllViews();

                        newFeedList1 = new NewsFeedFilter().meetFilterCriteria(newFeedList1, filterType, filterTag);
                        if (newFeedList1 != null && newFeedList1.size() == 0) {
                            no_data_text.setVisibility(View.VISIBLE);
                            no_data_text.setText(getResources().getString(R.string.no_feed_found));
                            newsfeed_recyclerview.setVisibility(View.GONE);
                        }
                    }
                }
                if (isSortReq) {
                    Collections.sort(newFeedList1, newsFeedSorter);
                    Collections.sort(newFeedList1, prioritySorter);
                }
                if (newFeedAdapter == null) {
                    Log.e(TAG, "createList: if condition" + myDeskInfoMap.size());
                    newFeedAdapter = new FeedsRecyclerAdapter();
                    newFeedAdapter.setFeedsRecyclerAdapter(newFeedList1, activeUser, initGlide(), NewLandingActivity.this, multipleCpl);
                    newFeedAdapter.setMyDeskMap(myDeskInfoMap);
                    // newFeedAdapter.setClickedFeedDataArrayList(clickedFeedDataArrayList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewLandingActivity.this);
                    newsfeed_recyclerview.setLayoutManager(mLayoutManager);
                    newsfeed_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    newsfeed_recyclerview.setAdapter(newFeedAdapter);

//                    following line of code is added to keep analytics of how many views are seen by user
                    viewTracker = new ViewTracker();
                    viewTracker.setRecyclerView(newsfeed_recyclerview);
                    viewTracker.setFeedClickListener(NewLandingActivity.this);
                    viewTracker.setFeedFromLanding(true, NewLandingActivity.this);
                    viewTracker.startTracking();

                    OustPreferences.saveAppInstallVariable("feedScroll", true);
                } else {
                    if (searchOn && isSearchList) {
                        Log.e(TAG, "createList: search-> " + newFeedList1.size());
                        newFeedAdapter.notifySearchFeedChange(newFeedList1, true);
                    } else {
                        Log.e(TAG, "createList: search else ->" + newFeedList1.size());
                        newFeedAdapter.removedFeedPosition(removedFeedPosition);
                        newFeedAdapter.notifyFeedChange(newFeedList1, true);
                    }

                    if (OustPreferences.getAppInstallVariable("newFeed")) {
                        new Handler().postDelayed(this::refreshViews, 1000);
                        OustPreferences.saveAppInstallVariable("newFeed", false);
                    }


                    // newFeedAdapter.notifyDataSetChanged();
                }
            } else {
                if (isSearchList) {
                    Log.e(TAG, "createList: if - else search-> ");
                    // newFeedAdapter.notifyFeedChange(newFeedList1, true);
                    no_data_text.setText(getResources().getString(R.string.no_feed_found));
                    if (newsfeed_recyclerview.getVisibility() == View.VISIBLE) {
                        no_data_text.setVisibility(View.VISIBLE);
                        newsfeed_recyclerview.setVisibility(View.GONE);
                    }
                } else {
                    ArrayList<DTOUserFeeds.FeedList> existingFeedList = OustStaticVariableHandling.getInstance().getFeeds();
                    Log.e(TAG, "createList: else - else existingFeedList-> " + existingFeedList.size());
                    if (existingFeedList != null && existingFeedList.size() != 0) {
                        newsfeed_recyclerview.setVisibility(View.VISIBLE);
                    } else {
                        newsfeed_recyclerview.setVisibility(View.GONE);
                        if (isSearchList) {
                            // newFeedAdapter.notifyFeedChange(newFeedList1, true);
                            no_data_text.setText(getResources().getString(R.string.no_feed_found));
                        } else {
                            no_data_text.setText(getResources().getString(R.string.no_feed_assigned));
                        }
                        no_data_text.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onFeedClick(long newFeedId, int cplId) {
        ClickedFeedData clickedFeedData = new ClickedFeedData();
        clickedFeedData.setFeedId((int) newFeedId);
        clickedFeedData.setCplId(cplId);
        clickedFeedData.setClickedTimestamp("" + System.currentTimeMillis());
        String clickFeedList = OustPreferences.get("clickFeedList");
        clickedFeedDataArrayList = OustSdkTools.getClickedFeedList(clickFeedList);
        if (clickedFeedDataArrayList == null) {
            clickedFeedDataArrayList = new ArrayList<>();
        }
        clickedFeedDataArrayList.add(clickedFeedData);

        OustPreferences.save("clickFeedList", new Gson().toJson(clickedFeedDataArrayList));
    }

    @Override
    public void onFeedViewed(long newFeedId) {
        ViewedFeedData viewedFeedData = new ViewedFeedData();
        viewedFeedData.setFeedId((int) newFeedId);
        long milliSec = System.currentTimeMillis();
        viewedFeedData.setViewedTimestamp("" + milliSec);
        String viewFeedList = OustPreferences.get("viewFeedLIst");
        try {
            viewedFeedDataMap = OustSdkTools.getViewedFeed(viewFeedList);
            if (viewedFeedDataMap == null) {
                viewedFeedDataMap = new HashMap<>();
            } else if (viewedFeedDataMap.isEmpty()) {
                viewedFeedDataMap = new HashMap<>();
            }
            String objectKey = newFeedId + "" + new SimpleDateFormat("MMMddyyyyHH:mm:ss", Locale.getDefault()).format(new Date(milliSec));
            viewedFeedDataMap.put("" + objectKey, viewedFeedData);
            OustPreferences.save("viewFeedLIst", new Gson().toJson(viewedFeedDataMap));
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //viewedFeedDataMap = null;
        }

        // sendFeedClickedRequestToBackend();
    }

    @Override
    public void onFeedViewedOnScroll(int position) {
        if (newFeedAdapter != null) {
            newFeedAdapter.onFeedViewedInScroll(position);
        }
    }

    @Override
    public void cplFeedClicked(long cplId) {
        if (cplCollectionData != null) {
            if (!cplCollectionData.getCplId().equalsIgnoreCase("" + cplId)) {
                if (feedCPLModelHashMap.containsKey("" + cplId)) {
                    if (feedCPLModelHashMap.get("" + cplId).getCplType().equalsIgnoreCase("MULTILINGUAL")) {
                        OpenLanguageScreen(cplId, true, true);
                    } else /*if(feedCPLModelHashMap.get("" + cplId).getCplType().equalsIgnoreCase("GENERAL")) */ {
                        distributeCPL(cplId);
                    }

                }
            } else {
                //OustSdkTools.showToast("CPL Already distributed");
                openCPLScreen();
            }
            newFeedAdapter.enableButton();
            //distributeCPL(cplId);
            //OpenLanguageScreen(cplId, true);
        } else {
            if (feedCPLModelHashMap.containsKey("" + cplId)) {
                if (feedCPLModelHashMap.get("" + cplId).getCplType().equalsIgnoreCase("MULTILINGUAL")) {
                    OpenLanguageScreen(cplId, true, true);
                } else/* if (feedCPLModelHashMap.get("" + cplId).getCplType().equalsIgnoreCase("GENERAL"))*/ {
                    distributeCPL(cplId);
                }

            }
            newFeedAdapter.enableButton();
        }
        newFeedAdapter.enableButton();
    }

    HashMap<String, FeedCPLModel> feedCPLModelHashMap = new HashMap<>();

    @Override
    public void checkFeedData(final long cplId, final long feedId) {
        Log.d(TAG, "checkFeedData: " + cplId);
        String cplInfoNode = (CPL_BASE_NODE + cplId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot != null) {
                        Map<String, Object> cplInfoMap = (Map<String, Object>) dataSnapshot.getValue();
                        FeedCPLModel feedCPLModel = new FeedCPLModel();
                        feedCPLModel.setCplType(getString(cplInfoMap.get("cplType")));
                        feedCPLModel.setVersion(getString(cplInfoMap.get("version")));
                        feedCPLModelHashMap.put("" + cplId, feedCPLModel);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "caught exception inside set singelton ", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
        addToFireBaseRefList(cplInfoNode, eventListener);

    }

    @Override
    public void onRemoveVideo(long newFeedId) {


    }

    @Override
    public void onPlayVideo(int position, int lastPos) {

        if (newFeedAdapter != null)
            newFeedAdapter.playVideo(position);
    }

    @Override
    public void refreshViews() {

        if (viewTracker != null) {
            viewTracker.startNewFeed();
        }

        OustPreferences.saveAppInstallVariable("feedScroll", true);

    }

    @Override
    public void onFeedRewardCoinsUpdate(DTONewFeed newFeed) {
        Log.d(TAG, "onFeedRewardCoinsUpdate: ");
        try {
            if (newFeed != null && newFeed.getFeedId() > 0 && newFeed.getFeedCoins() > 0 && !newFeed.isFeedCoinsAdded()) {
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedUpdatingServices.class);
                intent.putExtra("feedId", newFeed.getFeedId());
                intent.putExtra("feedCoins", newFeed.getFeedCoins());
                intent.putExtra("feedCoinsUpdate", true);
                startService(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkFeedRewardUpdate(DTONewFeed newFeed) {
        try {
            if (newFeed == null) {
                Log.d("TAG", "feedUpdated: reward newfeed null");
                return;
            }

            if (newFeed.isFeedCoinsAdded()) {
                Log.d("TAG", "feedUpdated: reward coins already added");
                return;
            }

            if (newFeed.getFeedCoins() < 1) {
                Log.d("TAG", "feedUpdated: reward feedcoins is less than zero");
                return;
            }
            Log.d("TAG", "feedRewardUpdate: coins:" + newFeed.getFeedCoins());

            onFeedRewardCoinsUpdate(newFeed);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void distributeCPL(long cplId) {
        mProgressbarAPICall = findViewById(R.id.progressbarAPICall);
        String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.cpl_distribution_api);
        final Gson mGson = new Gson();
        cplURL = HttpManager.getAbsoluteUrl(cplURL);
        cplURL = cplURL.replace("{cplId}", "" + cplId);
        mProgressbarAPICall.setVisibility(View.VISIBLE);

        String user_id = activeUser.getStudentid();
        List<String> users = new ArrayList<>();
        users.add(user_id);
        CPLDistrClass cplDistrClass = new CPLDistrClass();
        cplDistrClass.setDistributeDateTime(OustSdkTools.getDateTimeFromMilli2(SystemClock.currentThreadTimeMillis()));
        cplDistrClass.setUsers(users);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(cplDistrClass);

        ApiCallUtils.doNetworkCall(Request.Method.PUT, cplURL, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                OustSdkTools.showToast("CPL Distributed Successfully");
                mProgressbarAPICall.setVisibility(View.GONE);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                mProgressbarAPICall.setVisibility(View.GONE);
                Log.d(TAG, "onErrorResponse: onError:" + error.getLocalizedMessage());
            }
        });


        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, cplURL, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                OustSdkTools.showToast("CPL Distributed Successfully");
                mProgressbarAPICall.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                mProgressbarAPICall.setVisibility(View.GONE);
                Log.d(TAG, "onErrorResponse: onError:"+error.getLocalizedMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

    }

    public class CPLDistrClass {
        List<String> users;
        boolean sendSMS;
        boolean sendEmail;
        boolean sendNotification;
        boolean onlyPANIndia;
        String distributeDateTime;

        public CPLDistrClass(List<String> users, boolean sendSMS, boolean sendEmail, boolean sendNotification, boolean onlyPANIndia, String distributeDateTime) {
            this.users = users;
            this.sendSMS = sendSMS;
            this.sendEmail = sendEmail;
            this.sendNotification = sendNotification;
            this.onlyPANIndia = onlyPANIndia;
            this.distributeDateTime = distributeDateTime;
        }

        public CPLDistrClass() {
        }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        public boolean isSendSMS() {
            return sendSMS;
        }

        public void setSendSMS(boolean sendSMS) {
            this.sendSMS = sendSMS;
        }

        public boolean isSendEmail() {
            return sendEmail;
        }

        public void setSendEmail(boolean sendEmail) {
            this.sendEmail = sendEmail;
        }

        public boolean isSendNotification() {
            return sendNotification;
        }

        public void setSendNotification(boolean sendNotification) {
            this.sendNotification = sendNotification;
        }

        public boolean isOnlyPANIndia() {
            return onlyPANIndia;
        }

        public void setOnlyPANIndia(boolean onlyPANIndia) {
            this.onlyPANIndia = onlyPANIndia;
        }

        public String getDistributeDateTime() {
            return distributeDateTime;
        }

        public void setDistributeDateTime(String distributeDateTime) {
            this.distributeDateTime = distributeDateTime;
        }
    }

//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        Log.e("low memory","Low memory !");
//        finish();
//    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1444) {
            if (data != null) {
                if (newFeedAdapter != null && Objects.requireNonNull(data).getExtras() != null) {

                    long position = data.getLongExtra("FeedPosition", 0);
                    long feedComment = data.getLongExtra("FeedComment", 0);
                    boolean isFeedChange = data.getBooleanExtra("isFeedChange", false);
                    boolean isFeedRemove = data.getBooleanExtra("FeedRemove", false);

                    if (position != 0 && feedComment != 0 && isFeedChange) {
                        newFeedAdapter.modifyItem(position, feedComment);
                    }

                    if (position != 0 && isFeedRemove && isFeedChange) {

                        newFeedAdapter.removeItem(position);
                    }
                    newFeedAdapter.notifyDataSetChanged();
                    OustPreferences.saveAppInstallVariable("feedNot", false);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getUserSkillData() {
        try {
            String node = "/landingPage/" + activeUser.getStudentKey() + "/soccerSkill";
            ValueEventListener skillListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        final Map<String, Object> skillProgessDataList = (Map<String, Object>) dataSnapshot.getValue();
                        try {
                            if (skillProgessDataList != null) {
                                userSkillDataArrayList = new ArrayList<>();
                                userSkillDataArrayListSize = 0;
                                for (String skillId : skillProgessDataList.keySet()) {

                                    final HashMap<String, Object> skillProgessData = (HashMap<String, Object>) skillProgessDataList.get(skillId);
                                    Gson gson = new Gson();
                                    JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                    UserSkillData userSkillData = gson.fromJson(skillElement, UserSkillData.class);

                                    String node = "/soccerSkill/soccerSkill" + userSkillData.getSoccerSkillId();
                                    ValueEventListener skillListner = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {

                                                final Map<String, Object> skillProgessData = (Map<String, Object>) dataSnapshot.getValue();
                                                try {
                                                    if (skillProgessData != null) {


                                                        Gson gson = new Gson();
                                                        HashMap<String, Object> cardCommonDataMap;
                                                        CardCommonDataMap cardCommonDataMapObject = new CardCommonDataMap();
                                                        if (skillProgessData.get("cardCommonDataMap") != null) {
                                                            cardCommonDataMap = (HashMap<String, Object>) skillProgessData.get("cardCommonDataMap");

                                                            if (cardCommonDataMap != null) {
                                                                for (String id : cardCommonDataMap.keySet()) {

                                                                    if (id.equals("attemptCount")) {
                                                                        cardCommonDataMapObject.setAttemptCount((long) cardCommonDataMap.get("attemptCount"));

                                                                    } else if (id.equals("userBestScore")) {
                                                                        cardCommonDataMapObject.setUserBestScore((long) cardCommonDataMap.get("userBestScore"));
                                                                    } else {
                                                                        try {
                                                                            long cardId = Long.parseLong(id);
                                                                            if (cardId != 0) {
                                                                                final HashMap<String, CardInfo> cardInfoHashMap = (HashMap<String, CardInfo>) cardCommonDataMap.get(id);
                                                                                JsonElement cardElement = gson.toJsonTree(cardInfoHashMap);
                                                                                CardInfo cardInfo = gson.fromJson(cardElement, CardInfo.class);
                                                                                cardCommonDataMapObject.setCardInfo(cardInfo);

                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                        }
                                                                    }


                                                                }
                                                            }


                                                        }
                                                        JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                                        UserSkillData userSkillData2 = gson.fromJson(skillElement, UserSkillData.class);
                                                        userSkillData2.setCardCommonDataMap(cardCommonDataMapObject);

                                                        UserSkillData mergeObject = UserSkillData.mergeObjects(userSkillData2, userSkillData);
                                                        mergeObject.setEnrolled(userSkillData.isEnrolled());
                                                        mergeObject.setVerifyFlag(userSkillData.isVerifyFlag());
                                                        mergeObject.setRedFlag(userSkillData.isRedFlag());
                                                        mergeObject.setIdpTargetScore(userSkillData.getIdpTargetScore());
                                                        mergeObject.setUpdateTimeInMillis(userSkillData.getUpdateTimeInMillis());
                                                        userSkillDataArrayListSize++;
                                                        userSkillDataArrayList.add(mergeObject);

                                                        if (userSkillDataArrayListSize == skillProgessDataList.size()) {
                                                            skillDataFetch();
                                                        } else {
                                                            todo_txt_c.setVisibility(View.GONE);
                                                            see_all_3.setVisibility(View.GONE);
                                                        }


                                                    } else {
                                                        userSkillDataArrayListSize++;
                                                        if (userSkillDataArrayListSize == skillProgessDataList.size()) {
                                                            skillDataFetch();
                                                        } else {
                                                            todo_txt_c.setVisibility(View.GONE);
                                                            see_all_3.setVisibility(View.GONE);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                    userSkillDataArrayListSize++;
                                                    if (userSkillDataArrayListSize == skillProgessDataList.size()) {
                                                        skillDataFetch();
                                                    } else {
                                                        todo_txt_c.setVisibility(View.GONE);
                                                        see_all_3.setVisibility(View.GONE);
                                                    }
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                userSkillDataArrayListSize++;
                                                if (userSkillDataArrayListSize == skillProgessDataList.size()) {
                                                    skillDataFetch();
                                                } else {
                                                    todo_txt_c.setVisibility(View.GONE);
                                                    see_all_3.setVisibility(View.GONE);
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            userSkillDataArrayListSize++;
                                            if (userSkillDataArrayListSize == skillProgessDataList.size()) {
                                                skillDataFetch();
                                            } else {
                                                todo_txt_c.setVisibility(View.GONE);
                                                see_all_3.setVisibility(View.GONE);
                                            }

                                        }
                                    };
                                    OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
                                    OustFirebaseTools.getRootRef().child(node).keepSynced(true);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

  /*  public void getRankersData() {

        String leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.get_lb_url_v2);
        final Gson mGson = new Gson();
        leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL + "/" + activeUser.getStudentid());


        ApiCallUtils.doNetworkCall(Request.Method.GET, leaderBoardURL, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResult: " + response.toString());
                    if (response.getBoolean("success")) {
                        leaderBoardResponse leaderBoardResponseObject = new leaderBoardResponse();
                        leaderBoardResponseObject = mGson.fromJson(response.toString(), leaderBoardResponse.class);


                        if (leaderBoardResponseObject.getLeaderBoardDataList() != null && leaderBoardResponseObject.getLeaderBoardDataList().size() != 0) {
                            ArrayList<LeaderBoardDataRow> mOriginalList2 = new ArrayList<>(leaderBoardResponseObject.getLeaderBoardDataList());
                            LeaderBoardDataRow mCurrentUserData = mOriginalList2.get(mOriginalList2.size() - 1);
                            if (mCurrentUserData != null) {
                                if (mCurrentUserData.getRank() != null && !mCurrentUserData.getRank().equalsIgnoreCase("0")) {
                                    user_rank_count.setText(mCurrentUserData.getRank());
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }*/

    private void sendExpiredFeeds(ArrayList<Long> removedFeedPosition) {
        if (removedFeedPosition == null || removedFeedPosition.size() < 1) {
            Log.d(TAG, "sendExpiredFeeds: No expiry feed found to update");
            return;
        }
        String postFeedRemove = OustSdkApplication.getContext().getResources().getString(R.string.post_feed_remove);
        JSONArray feedList = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        for (Long aLong : removedFeedPosition) {
            feedList.put(aLong);
        }
        try {
            jsonObject.put("feedIdList", feedList);
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        postFeedRemove = HttpManager.getAbsoluteUrl(postFeedRemove + "/" + activeUser.getStudentid());


        ApiCallUtils.doNetworkCall(Request.Method.PUT, postFeedRemove, jsonObject, new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void reminderNotification(CommonLandingData commonLandingData) {

        try {
            if (!OustPreferences.getAppInstallVariable("sendPushNotifications")) {
                return;
            }
            if (commonLandingData != null) {

                ArrayList<CommonLandingData> commonLandingDataArrayList = new ArrayList<>();
                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                String alertTitle = commonLandingData.getName();
                String alertType = String.valueOf(GCMType.COURSE_REMINDER);
                String alertContent = "Hello " + activeUser.getUserDisplayName() + ", Reminder to please complete " + commonLandingData.getName();
                String courseid = commonLandingData.getId();
                String contentType = commonLandingData.getType();
                int requestCode = 0;
                int contentId = 0;
                if (courseid.toUpperCase().contains("COURSE")) {
                    courseid = courseid.toUpperCase().replace("COURSE", "");
                    requestCode = Integer.parseInt("1" + courseid);
                    contentId = Integer.parseInt(courseid);
                } else if (courseid.toUpperCase().contains("ASSESSMENT")) {
                    courseid = courseid.toUpperCase().replace("ASSESSMENT", "");
                    requestCode = Integer.parseInt("2" + courseid);
                    contentId = Integer.parseInt(courseid);
                    alertType = String.valueOf(GCMType.ASSESSMENT_REMINDER);
                } /*else if (courseid.toUpperCase().contains("ASSESSMENT")) {
                    courseid = courseid.toUpperCase().replace("ASSESSMENT", "");
                    requestCode = Integer.parseInt("2" + courseid);
                }*/

                if (commonLandingData.getReminderNotificationInterval() != 0 && commonLandingData.getCompletionPercentage() < 100) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR, (int) commonLandingData.getReminderNotificationInterval());
                    boolean deadlineOver = false;

                    if (commonLandingData.getCompletionDeadline() != null && !commonLandingData.getCompletionDeadline().isEmpty()) {
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String aa = convertDate(commonLandingData.getCompletionDeadline(), "yyyy-MM-dd HH:mm:ss");
                        Date completionDate = simpleDateFormat.parse(aa);
                        assert completionDate != null;
                        String contentDeadLine = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(completionDate);
                        if (calendar.getTimeInMillis() < completionDate.getTime()) {
                            alertContent = alertContent + " before " + contentDeadLine;
                        } else {
                            deadlineOver = true;
                        }
                    }

                    Intent intent = new Intent(this, ReminderNotificationManager.class);
                    intent.putExtra("alertTitle", alertTitle);
                    intent.putExtra("alertContent", alertContent);
                    intent.putExtra("alertImage", commonLandingData.getIcon());
                    intent.putExtra("alertType", alertType);
                    intent.putExtra("alertId", contentId);
                    intent.putExtra("requestCode", requestCode);
                    intent.putExtra("time", commonLandingData.getCompletionDeadline());

                    PendingIntent pendingIntentOnCreate;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntentOnCreate = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                    } else {
                        pendingIntentOnCreate = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
                    }

                    boolean isAlready = (pendingIntentOnCreate != null);
                    if (!isAlready && !deadlineOver) {

                        commonLandingDataArrayList.add(commonLandingData);
                        String reminderData = new Gson().toJson(commonLandingDataArrayList);
                        OustPreferences.save("reminderData", reminderData);
                        OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);

//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent pendingIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                        } else {
                            pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        }
                        assert alarmMgr != null;
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), commonLandingData.getReminderNotificationInterval() * 3600000, pendingIntent);
                        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  180000, pendingIntent);
                    }
                    if (deadlineOver) {
                        if (isAlready) {
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            PendingIntent pendingIntent;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            }
                            assert alarmMgr != null;
                            alarmMgr.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }
                    }
                } else if (commonLandingData.getCompletionPercentage() >= 100) {
                    removeReminderNotification(commonLandingData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void skillDataFetch() {
        todo_txt_c.setVisibility(View.VISIBLE);
        see_all_3.setVisibility(View.VISIBLE);
        sub_tab_3.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Collections.sort(userSkillDataArrayList,
                    Comparator.comparingLong(UserSkillData::getUpdateTimeInMillis));
        }
        Collections.reverse(userSkillDataArrayList);
        SkillModuleAdapter skillModuleAdapter = new SkillModuleAdapter();
        skillModuleAdapter.setAdapterHorizontal(true);
        skillModuleAdapter.setSkillModuleAdapter(userSkillDataArrayList, NewLandingActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewLandingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        play_skill_rv.setLayoutManager(mLayoutManager);
        play_skill_rv.setItemAnimator(new DefaultItemAnimator());
        play_skill_rv.setAdapter(skillModuleAdapter);


        no_data_text.setVisibility(View.GONE);
        play_skill_rv.setVisibility(View.VISIBLE);

    }

    private void removeReminderNotification(CommonLandingData commonLandingData) {
        try {

            if (commonLandingData != null) {
                ArrayList<CommonLandingData> commonLandingDataArrayList = OustStaticVariableHandling.getInstance().getReminderNotification();
                int requestCode = 0;
                String courseid = commonLandingData.getId();
                if (courseid.toUpperCase().contains("COURSE")) {
                    courseid = courseid.toUpperCase().replace("COURSE", "");
                    requestCode = Integer.parseInt("1" + courseid);
                } else if (courseid.toUpperCase().contains("ASSESSMENT")) {
                    courseid = courseid.toUpperCase().replace("ASSESSMENT", "");
                    requestCode = Integer.parseInt("2" + courseid);
                }
                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(NewLandingActivity.this, ReminderNotificationManager.class);
                PendingIntent pendingIntentOnCreate;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntentOnCreate = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntentOnCreate = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
                }

                boolean isAlready = (pendingIntentOnCreate != null);
                if (isAlready) {
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    PendingIntent pendingIntent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getBroadcast(NewLandingActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    }
                    assert alarmMgr != null;
                    alarmMgr.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
                if (commonLandingDataArrayList != null && commonLandingDataArrayList.size() != 0) {
                    String reminderData = new Gson().toJson(commonLandingDataArrayList);
                    if (commonLandingDataArrayList.contains(commonLandingData)) {
                        commonLandingDataArrayList.remove(commonLandingData);

                        OustPreferences.save("reminderData", reminderData);
                        OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);

                    } else {
                        int position = findCommonLandingData(commonLandingDataArrayList, commonLandingData.getId());
                        if (position >= 0) {

                            commonLandingDataArrayList.remove(position);
                            OustPreferences.save("reminderData", reminderData);
                            OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public static int findCommonLandingData(ArrayList<CommonLandingData> a, String id) {

        for (int i = 0; i < a.size(); i++)
            if (a.get(i).getId().equalsIgnoreCase(id))
                return i;

        return -1;
    }

    private void removeUndistributeData() {
        try {
            if (allcommonLandingDataArrayList != null && allcommonLandingDataArrayList.size() != 0) {

                ArrayList<CommonLandingData> commonLandingDataArrayList = OustStaticVariableHandling.getInstance().getReminderNotification();

                if (commonLandingDataArrayList != null && commonLandingDataArrayList.size() != 0) {

                    for (CommonLandingData commonLandingData : allcommonLandingDataArrayList) {

                        int position = findCommonLandingData(commonLandingDataArrayList, commonLandingData.getId());
                        if (position < 0) {
                            removeReminderNotification(commonLandingData);
                        }

                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void removeAllReminderNotification() {
        try {

            ArrayList<CommonLandingData> commonLandingDataArrayList = OustStaticVariableHandling.getInstance().getReminderNotification();

            if (commonLandingDataArrayList != null && commonLandingDataArrayList.size() != 0) {

                for (CommonLandingData commonLandingData : commonLandingDataArrayList) {
                    String reminderData = new Gson().toJson(commonLandingDataArrayList);
                    if (commonLandingDataArrayList.contains(commonLandingData)) {
                        commonLandingDataArrayList.remove(commonLandingData);

                        OustPreferences.save("reminderData", reminderData);
                        String requestId = commonLandingData.getId().replaceAll("[^0-9]", "");
                        int requestCode = Integer.parseInt("1" + requestId);

                        Intent intent = new Intent(OustSdkApplication.getContext(), ReminderNotificationManager.class);
                        PendingIntent pendingIntentIsAlready;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntentIsAlready = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                        } else {
                            pendingIntentIsAlready = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
                        }
                        boolean isAlready = (pendingIntentIsAlready != null);
                        AlarmManager alarmMgr = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                        if (isAlready) {
                            PendingIntent pendingIntent;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            }
                            assert alarmMgr != null;
                            alarmMgr.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }
                        OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);

                    } else {
                        int position = findCommonLandingData(commonLandingDataArrayList, commonLandingData.getId());
                        if (position >= 0) {

                            commonLandingDataArrayList.remove(position);
                            OustPreferences.save("reminderData", reminderData);
                            String requestId = commonLandingData.getId().replaceAll("[^0-9]", "");
                            int requestCode = Integer.parseInt("1" + requestId);

                            Intent intent = new Intent(OustSdkApplication.getContext(), ReminderNotificationManager.class);
                            boolean isAlready = (PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null);
                            AlarmManager alarmMgr = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                            if (isAlready) {
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                assert alarmMgr != null;
                                alarmMgr.cancel(pendingIntent);
                                pendingIntent.cancel();
                            }
                            OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserEvents() {
        String node = "/landingPage/" + activeUser.getStudentKey() + "/meetingCalendar";
        ValueEventListener meetingCalendarListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    final Map<String, Object> meetingUserDataMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (meetingUserDataMap != null) {
                        for (String meetingId : meetingUserDataMap.keySet()) {

                            final HashMap<String, Object> meetingUserData = (HashMap<String, Object>) meetingUserDataMap.get(meetingId);
                            Gson gson = new Gson();
                            JsonElement meetingCalendarElement = gson.toJsonTree(meetingUserData);
                            MeetingCalendar meetingCalendar = gson.fromJson(meetingCalendarElement, MeetingCalendar.class);

                            if (meetingCalendar != null) {

                                String id = "MEETINGCALENDAR" + meetingCalendar.getMeetingId();
                                myDeskInfoMap.put(id, "COURSE");
                                if (myDeskData.get(id) != null) {
                                    CommonLandingData commonLandingData = myDeskData.get(id);
                                    if (commonLandingData != null) {
                                        commonLandingData.setName(meetingCalendar.getClassTitle());
                                        commonLandingData.setType(meetingCalendar.getEventType());
                                        commonLandingData.setIcon(meetingCalendar.getThumbnailImage());
                                        commonLandingData.setBanner(meetingCalendar.getBannerImg());
                                        commonLandingData.setAddedOn(meetingCalendar.getAddedOn());
                                        commonLandingData.setStartTime(meetingCalendar.getMeetingStartTime());
                                        commonLandingData.setEndTime(meetingCalendar.getMeetingEndTime());
                                        commonLandingData.setTimeZone(meetingCalendar.getTimeZone());
                                        commonLandingData.setId(id);
                                        commonLandingData.setMeetingCalendar(meetingCalendar);
                                        myDeskData.put(id, commonLandingData);
                                        if (meetingCalendar.getAttendStatus() != null) {

                                            if (
                                                    meetingCalendar.getAttendStatus().equalsIgnoreCase("NOT_ATTENDING") ||
                                                            meetingCalendar.getAttendStatus().equalsIgnoreCase("Not interested")) {
                                                commonLandingData.setCompletionPercentage(100);
                                                myDeskData.remove(id);
                                            }
                                        }

                                        if (meetingCalendar.getMeetingEndTime() < System.currentTimeMillis()) {
                                            commonLandingData.setCompletionPercentage(100);
                                            myDeskData.remove(id);
                                        }
                                    }
                                } else {
                                    CommonLandingData commonLandingData = new CommonLandingData();
                                    commonLandingData.setName(meetingCalendar.getClassTitle());
                                    commonLandingData.setType(meetingCalendar.getEventType());
                                    commonLandingData.setIcon(meetingCalendar.getThumbnailImage());
                                    commonLandingData.setBanner(meetingCalendar.getBannerImg());
                                    commonLandingData.setAddedOn(meetingCalendar.getAddedOn());
                                    commonLandingData.setStartTime(meetingCalendar.getMeetingStartTime());
                                    commonLandingData.setEndTime(meetingCalendar.getMeetingEndTime());
                                    commonLandingData.setTimeZone(meetingCalendar.getTimeZone());
                                    commonLandingData.setId(id);
                                    commonLandingData.setMeetingCalendar(meetingCalendar);
                                    myDeskData.put(id, commonLandingData);
                                    if (meetingCalendar.getAttendStatus() != null) {

                                        if (
                                                meetingCalendar.getAttendStatus().equalsIgnoreCase("NOT_ATTENDING") ||
                                                        meetingCalendar.getAttendStatus().equalsIgnoreCase("Not interested")) {
                                            commonLandingData.setCompletionPercentage(100);
                                            myDeskData.remove(id);
                                        }
                                    }

                                    if (meetingCalendar.getMeetingEndTime() < System.currentTimeMillis()) {
                                        commonLandingData.setCompletionPercentage(100);
                                        myDeskData.remove(id);
                                    }
                                }
                            }
                        }
                    }
                    isCoursesAvailable = true;
                    setCollectiveListener();

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        DatabaseReference meetingRef = OustFirebaseTools.getRootRef().child(node);
        Query query = meetingRef.orderByChild("addedOn");
        query.keepSynced(true);
        query.addValueEventListener(meetingCalendarListner);
    }

    private void branchIOHandle(String referringParamsString) {

        try {
            final Gson gson = new GsonBuilder().create();
            String tenantId = OustPreferences.get("tanentid");
            if (activeUser != null) {
                //   getRankersData();
                Branch.getInstance().setIdentity(activeUser.getStudentKey() + "_" + tenantId);
            }

            if ((referringParamsString != null) && (!(referringParamsString).isEmpty())) {
                Log.e("BranchIO", "branch io data " + referringParamsString);
                JSONObject jsonObject = new JSONObject(referringParamsString);
                if (jsonObject.has("+clicked_branch_link") && jsonObject.getString("+clicked_branch_link").equalsIgnoreCase("true")) {
                    Log.e(TAG, "BranchIO Data: " + jsonObject.toString());
                    OustPreferences.save("refereingParam", (referringParamsString));
                    BranchIoResponce branchIoResponse = gson.fromJson(referringParamsString, BranchIoResponce.class);

                    if (branchIoResponse.getOrgId() != null && branchIoResponse.getOrgId().equalsIgnoreCase(tenantId)) {
                        if (branchIoResponse.getModuleType() != null && !branchIoResponse.getModuleType().isEmpty()) {

                            if (branchIoResponse.getModuleType().equalsIgnoreCase("NormalCourse") || branchIoResponse.getModuleType().equalsIgnoreCase("MicroLearning")) {
//                                BranchTools.checkCourseExistOrNot(branchIoResponse.getCourseId(), activeUser.getStudentKey());
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("MultilingualCourse")) {

                                String parentCourseId = branchIoResponse.getCourseId();
                                String node = "/landingPage/" + activeUser.getStudentKey() + "/course";
                                Query query = OustFirebaseTools.getRootRef().child(node).orderByChild("parentCourseId").equalTo(Long.parseLong(parentCourseId)).limitToFirst(1);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.getValue() != null) {

                                                Map<String, Object> childCourse = (Map<String, Object>) dataSnapshot.getValue();
                                                if (childCourse != null) {
                                                    for (String key : childCourse.keySet()) {
                                                        Map<String, Object> childCourseNode = (Map<String, Object>) childCourse.get(key);
                                                        if (childCourseNode != null) {
                                                            if (childCourseNode.get("elementId") != null) {
                                                                BranchTools.gotoCoursePage("" + childCourseNode.get("elementId"));
                                                            } else {
                                                                BranchTools.gotoCoursePage(parentCourseId);
                                                            }
                                                        }
                                                        break;
                                                    }

                                                } else {
                                                    BranchTools.gotoCoursePage(parentCourseId);
                                                }

                                            } else {
                                                BranchTools.gotoCoursePage(parentCourseId);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                            BranchTools.gotoCoursePage(parentCourseId);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(NewLandingActivity.this, "" + getResources().getString(R.string.firebase_no_data_error), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("QUANTITATIVE")) {
//                                BranchTools.checkAssessmentExistOrNot(branchIoResponse.getAssessmentId(), activeUser.getStudentKey());
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("FEED")) {
                                BranchTools.gotoFeedPage(branchIoResponse.getFeedId(), activeUser);
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("SURVEY")) {
                                BranchTools.gotoSurvey(branchIoResponse.getAssessmentId(), 0);
                            }

                        } else {
                            if ((branchIoResponse.getAssessmentId() != null) && (!branchIoResponse.getAssessmentId().isEmpty())) {
//                                BranchTools.checkAssessmentExistOrNot(branchIoResponse.getAssessmentId(), activeUser.getStudentKey());
                            } else if ((branchIoResponse.getCourseId() != null) && (!branchIoResponse.getCourseId().isEmpty())) {
//                                BranchTools.checkCourseExistOrNot(branchIoResponse.getCourseId(), activeUser.getStudentKey());
                            } else if ((branchIoResponse.getCollectionId() != null) && (!branchIoResponse.getCollectionId().isEmpty())) {
                                BranchTools.gotoCollectionPage(branchIoResponse.getCollectionId());
                            } else if ((branchIoResponse.getOrgId() != null &&
                                    !branchIoResponse.getOrgId().isEmpty()) &&
                                    (branchIoResponse.getUserId() != null && !branchIoResponse.getUserId().isEmpty())) {

                                Log.d(TAG, "BranchIO - New OrgId:" + branchIoResponse.getOrgId() + " --- TanentId:" + tenantId);
                                if (!branchIoResponse.getOrgId().equalsIgnoreCase(tenantId)) {
                                    branchIoResponse = OustSdkTools.decryptBranchData(branchIoResponse, null);
                                    fillLoginDataMapFromBranchIO(branchIoResponse);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
