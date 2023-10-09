package com.oustme.oustsdk.activity.common;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.GameLetActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.adapter.common.NewFeedAdapter;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.interfaces.common.NewsFeedClickCallback;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ClickedFeedRequestData;
import com.oustme.oustsdk.request.ViewTracker;
import com.oustme.oustsdk.request.ViewedFeedData;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.service.FeedBackService;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.filters.NewsFeedFilter;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._CANCELED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._FAILED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.S3_BUCKET_NAME;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class NewAlertActivity extends AppCompatActivity implements NewsFeedClickCallback, SearchView.OnQueryTextListener, LearningModuleInterface {
    private RecyclerView newsfeed_recyclerview;
    private WebView mywebView;
    private RelativeLayout eventlist_layout, event_webview_layout;
    private TextView nofeedstext;
    private Toolbar toolbar;
    private ProgressBar newsalert_loader;
    private LinearLayout newalert_framelayout;
    private MenuItem actionSearch;
    private ArrayList<String> gifCollections;
    private FrameLayout feed_card_layout;
    private ImageView close_card;
    private RelativeLayout card_layout, downloadscreen_layout, closecard_layout, feedcard_toplayout;
    private SwipeRefreshLayout swipe_refresh_layout;
    private String TAG = "New alert image ";

    private NewFeedAdapter newFeedAdapter;
    private boolean isWebviewOpen = false;
    private ActiveUser activeUser;
    private ActiveGame activeGame;
    private List<DTONewFeed> DTONewFeedList = new ArrayList<>();
    private List<DTONewFeed> newFilteredFeedList = new ArrayList<>();
    private CustomSearchView newSearchView;
    private HashMap<String, String> myDeskMap;
    private List<ClickedFeedData> clickedFeedDataList;
    private HashMap<String, ViewedFeedData> viewedFeedDataMap;
    private HashMap<String, ViewedFeedData> alreadyAddedFeedDataMap;
    private ProgressBar mProgressBarWebLoad;
    private DownloadFiles downLoadFiles;
    private ArrayList<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(NewAlertActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.fragment_newalert);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
        clickedFeedDataList = new ArrayList<>();
        createLoader();
        setToolbar();
        activeUser = OustAppState.getInstance().getActiveUser();
        getUserNewsFeed();
//        OustGATools.getInstance().reportPageViewToGoogle(NewAlertActivity.this, "Feeds Page");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alert_menu, menu);
        try {
            actionSearch = menu.findItem(R.id.action_search);
            if ((DTONewFeedList != null) && (DTONewFeedList.size() > 4)) {
                actionSearch.setVisible(true);
            } else {
                actionSearch.setVisible(false);
            }
            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();
            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(getResources().getString(R.string.search_text));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {

        newsfeed_recyclerview = findViewById(R.id.newsfeed_recyclerview);
        mywebView = findViewById(R.id.webView);
        eventlist_layout = findViewById(R.id.eventlist_layout);
        event_webview_layout = findViewById(R.id.event_webview_layout);
        nofeedstext = findViewById(R.id.nofeedstext);
        toolbar = findViewById(R.id.tabanim_toolbar);
        newsalert_loader = findViewById(R.id.newsalert_loader);
        newalert_framelayout = findViewById(R.id.newalert_framelayout);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);

        feed_card_layout = findViewById(R.id.feed_card_layout);
        close_card = findViewById(R.id.close_card);
        card_layout = findViewById(R.id.card_layout);
        downloadscreen_layout = findViewById(R.id.downloadscreen_layout);
        closecard_layout = findViewById(R.id.closecard_layout);
        feedcard_toplayout = findViewById(R.id.feedcard_toplayout);
        downloadscreen_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mProgressBarWebLoad = findViewById(R.id.progressbar_web);

    }


    private int noofListenerSetForFeed = 0;
    List<DTONewFeed> userDTONewFeedList;
    //    List<EntityNewFeed> feedList;
    private FirebaseRefClass firebaseRefClass;
    private FirebaseRefClass firebaseRefClass1;

    public void getNewsFeedsFromFirebase() {
        String message = "/newsFeed/feeds";
        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot != null) {
                        final Map<String, Object> newsfeedMap = (Map<String, Object>) dataSnapshot.getValue();
                        CommonTools commonTools = new CommonTools();
                        for (String newsId : newsfeedMap.keySet()) {
                            final HashMap<String, Object> mymap = (HashMap<String, Object>) newsfeedMap.get(newsId);
                            DTONewFeed feed1 = commonTools.getNewFeedFromMap(mymap, "");
                            feed1.setFeedId(Long.parseLong(newsId.substring(4)));
                        }
                    }
                    getAllFeeds();
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
        Query query = newsfeedRef.orderByChild("timeStamp").limitToLast(50);
        query.keepSynced(true);
        query.addValueEventListener(newsfeedListListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
        firebaseRefClass = new FirebaseRefClass(newsfeedListListener, message);
    }

    public void getUserNewsFeed() {
        try {
            String message = "/userFeed/" + activeUser.getStudentKey();
            Log.d(TAG, "getUserNewsFeed: "+message);
            ValueEventListener newsfeedListListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        noofListenerSetForFeed = 0;
                        userDTONewFeedList = new ArrayList<>();
                        if (dataSnapshot != null)
                        {
                            final Map<String, Object> newsfeedMap = (Map<String, Object>) dataSnapshot.getValue();
                            for (String newsId : newsfeedMap.keySet()) {
                                final HashMap<String, Object> mymap = (HashMap<String, Object>) newsfeedMap.get(newsId);
                                if ((mymap != null))
                                {
                                    long feedID = 0;
                                    long timeStamp = 0;
                                    if ((mymap.get("feedId") != null)) {
                                        feedID = (long) mymap.get("feedId");
                                    }
                                    if ((mymap.get("timeStamp")) != null) {
                                        timeStamp = (long) mymap.get("timeStamp");
                                    }
                                    getUserFeedDatails(("" + feedID), ("" + timeStamp));
                                }
                            }
                        }
                        else
                            {
                            getAllFeeds();
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
            Query query = newsfeedRef.orderByChild("timeStamp").limitToLast(100);
            query.keepSynced(true);
            query.addValueEventListener(newsfeedListListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
            firebaseRefClass1 = new FirebaseRefClass(newsfeedListListener, message);
            checkConnected();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkConnected() {
        try {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if (!connected) {
                            if (DTONewFeedList == null || (DTONewFeedList != null && DTONewFeedList.size() == 0)) {
                                nofeedstext.setText(getResources().getString(R.string.no_internet_connection));
                                nofeedstext.setVisibility(View.VISIBLE);
                                newsfeed_recyclerview.setVisibility(View.GONE);
                                swipe_refresh_layout.setRefreshing(false);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    if (DTONewFeedList == null || (DTONewFeedList != null && DTONewFeedList.size() == 0)) {
                        nofeedstext.setText(getResources().getString(R.string.no_internet_connection));
                        nofeedstext.setVisibility(View.VISIBLE);
                        newsfeed_recyclerview.setVisibility(View.GONE);
                        swipe_refresh_layout.setRefreshing(false);
                    }
                }
            };
            OustFirebaseTools.getRootRef().child(".info/connected");
            OustFirebaseTools.getRootRef().child(".info/connected").addListenerForSingleValueEvent(listener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserFeedDatails(final String id, final String timeStamp) {
        try {
            String message = "/feeds/feed" + id;
            noofListenerSetForFeed++;
            ValueEventListener newsfeedListListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            final HashMap<String, Object> newsfeedMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (newsfeedMap != null) {
                                CommonTools commonTools = new CommonTools();
                                DTONewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, timeStamp);
                                feed1.setFeedId(Long.parseLong(dataSnapshot.getKey().substring(4)));
                                userDTONewFeedList.add(feed1);
                            } else {
                                noofListenerSetForFeed--;
                            }
                        } else {
                            noofListenerSetForFeed--;
                        }
                    } catch (Exception e) {
                        noofListenerSetForFeed--;
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    if ((userDTONewFeedList != null) && (noofListenerSetForFeed == userDTONewFeedList.size())) {
                        getAllFeeds();
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(newsfeedListListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getAllFeeds() {
        try {
            List<DTONewFeed> allFeedList = new ArrayList<>();
            if ((userDTONewFeedList != null) && (noofListenerSetForFeed == userDTONewFeedList.size())) {
                allFeedList.addAll(userDTONewFeedList);
//                allFeedList.addAll(feedList);
                if (allFeedList.size() > 0) {
                    Collections.sort(allFeedList, DTONewFeed.newsFeedSorter);
                    Collections.sort(allFeedList, prioritySorter);
                    if (allFeedList.size() >= 4) {
                        if (actionSearch != null)
                            actionSearch.setVisible(true);
                    }
                    DTONewFeedList = allFeedList;
                    initAlerts();
                }
            }
        } catch (Exception e) {
        }
    }

    public Comparator<DTONewFeed> prioritySorter = new Comparator<DTONewFeed>() {
        public int compare(DTONewFeed s1, DTONewFeed s2) {
            return Long.valueOf(s2.getFeedPriority()).compareTo(Long.valueOf(s1.getFeedPriority()));
        }
    };

  /*  public Comparator<DTONewFeed> newsFeedSorter = new Comparator<DTONewFeed>() {
        public int compare(DTONewFeed s1, DTONewFeed s2) {
            if (s1.getTimestamp() == null) {
                return -1;
            }
            if (s2.getTimestamp() == null) {
                return 1;
            }
            if (s1.getTimestamp() == s2.getTimestamp()) {
                return 0;
            }
            return s2.getTimestamp().compareTo(s1.getTimestamp());
        }
    };*/

    public void initAlerts() {
        try {
            ids = new ArrayList<>();
            downLoadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {

                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    //OustSdkTools.showToast(message);
                    downloadscreen_layout.setVisibility(View.GONE);
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if(code==_FAILED || code==_CANCELED)
                    {
                        downloadscreen_layout.setVisibility(View.GONE);
                    }
                    else if(code==_COMPLETED)
                    {
                        saveData(new File(""), "", false);
                    }
                    downloadscreen_layout.setVisibility(View.GONE);
                }

                @Override
                public void onAddedToQueue(String id) {
                    ids.add(id);
                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {
                    if(code==_COMPLETED) {
                        String getId = ids.get(ids.indexOf(id));
                        if(getId!=null)
                        {
                            ids.remove(id);
                            saveData(new File(""), "", true);
                        }
                        downloadscreen_layout.setVisibility(View.GONE);
                    }
                }
            });
            activeUser = OustAppState.getInstance().getActiveUser();
            myDeskMap = (HashMap<String, String>) getIntent().getSerializableExtra("deskDataMap");
            nofeedstext.setText(getResources().getString(R.string.no_alerts_available));
            if (activeUser != null) {
//                DTONewFeedList = OustAppState.getInstance().getNewsFeeds();
                getImagePaths();
                createList(DTONewFeedList, false);
                OustPreferences.saveTimeForNotification("lastalerttime", (DTONewFeedList.get(0).getTimestamp()));
            } else {
                NewAlertActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            int n1 = 2;
            int n2 = n1 + 4;
        }
    }


    private void getImagePaths() {
        for (int i = 0; i < DTONewFeedList.size(); i++) {
            DTOCourseCard courseCardClass = DTONewFeedList.get(i).getCourseCardClass();
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
                downLoadFiles.startDownLoadGif(file.toString(), S3_BUCKET_NAME, imagePath, true, true);

                /*AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
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
        DTONewFeedList.get(feedNo).setTempSignedImage(signedPath);
        createList(DTONewFeedList, false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (newFeedAdapter != null) {
                newFeedAdapter.enableButton();
            }
            // createList(DTONewFeedList);
            String addedCatalogId = OustPreferences.get("catalogId");
            if (addedCatalogId != null) {
                if (myDeskMap != null) {
                    if (myDeskMap.get(addedCatalogId) == null) {
                        myDeskMap.put(addedCatalogId, "CATEGORY");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            OustSdkTools.setSnackbarElements(newalert_framelayout, NewAlertActivity.this);
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                int color = Color.parseColor(toolbarColorCode);
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                newsalert_loader.setIndeterminateDrawable(ld);
            }
            OustSdkTools.setProgressbar(newsalert_loader);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView = toolbar.findViewById(R.id.title);
            titleTextView.setText(getResources().getString(R.string.alerts).toUpperCase());
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createList(List<DTONewFeed> DTONewFeedList1, boolean isSearchList) {
        try {
            if ((DTONewFeedList1 != null) && (DTONewFeedList1.size() > 0)) {
                nofeedstext.setVisibility(View.GONE);
                newsfeed_recyclerview.setVisibility(View.VISIBLE);
                if (newFeedAdapter == null) {
                    newFeedAdapter = new NewFeedAdapter();
                    newFeedAdapter.SetNewFeedAdapter(NewAlertActivity.this, DTONewFeedList1, activeUser, NewAlertActivity.this);
                    newFeedAdapter.setNewsFeedClickCallback(NewAlertActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NewAlertActivity.this);
                    newsfeed_recyclerview.setLayoutManager(mLayoutManager);
                    newsfeed_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    newsfeed_recyclerview.setAdapter(newFeedAdapter);

//                    following line of code is added to keep analytics of how many views are seen by user
                    ViewTracker viewTracker = new ViewTracker();
                    viewTracker.setRecyclerView(newsfeed_recyclerview);
                    viewTracker.setFeedClickListener(NewAlertActivity.this);
                    viewTracker.startTracking();
                } else {
                    newFeedAdapter.notifyFeedChange(DTONewFeedList1);
                }
            } else {
                if (isSearchList) {
                    nofeedstext.setText(getResources().getString(R.string.no_match_found));
                } else {
                    nofeedstext.setText(getResources().getString(R.string.no_alerts_available));
                }
                nofeedstext.setVisibility(View.VISIBLE);
                newsfeed_recyclerview.setVisibility(View.GONE);
            }
            swipe_refresh_layout.setRefreshing(false);
            swipe_refresh_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //deligate methode called on search box text change
    @Override
    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            if (newText.isEmpty()) {
                createList(DTONewFeedList, false);
            } else {
                if ((DTONewFeedList != null) && (DTONewFeedList.size() > 0)) {
                    NewsFeedFilter cr = new NewsFeedFilter();
                    newFilteredFeedList = cr.meetCriteria(DTONewFeedList, newText);
                    createList(newFilteredFeedList, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }


    @Override
    public void onnewsfeedClick(String url) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            OustSdkTools.showProgressBar();
            mProgressBarWebLoad.setVisibility(View.GONE);
            isWebviewOpen = true;
            showFirstWebViewAnim();
            event_webview_layout.setVisibility(View.VISIBLE);
            mywebView.setWebViewClient(new MyWebViewClient());
            mywebView.getSettings().setJavaScriptEnabled(true);
            mywebView.loadUrl(url);
            mywebView.setVisibility(View.VISIBLE);
            mywebView.setWebChromeClient(new WebChromeClient()
            {
                public void onProgressChanged(WebView view, int progress) {
                    Log.d(TAG, "onProgressChanged: "+progress);
                    if (progress == 100) {
                        mProgressBarWebLoad.setVisibility(View.GONE);
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
            eventlist_layout.startAnimation(event_animmoveout);
            event_webview_layout.startAnimation(event_animmovein);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showfirstbackWebViewAnim() {
        try {
            OustSdkTools.hideProgressbar();
            eventlist_layout.bringToFront();
            Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackin);
            Animation event_animmoveout = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackout);
            eventlist_layout.startAnimation(event_animmovein);
            event_webview_layout.startAnimation(event_animmoveout);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private class MyWebViewClient extends WebViewClient {

        private ProgressBar progressBar;
        
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            //progressBar.setVisibility(View.GONE);
            Log.d(TAG, "onPageFinished: inside class");
        }


    }

    @Override
    public void gotoSurvey(String assessmentId, String surveyTitle) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(NewAlertActivity.this, SurveyDetailActivity.class);
                if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)){
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

    @Override
    public void gotoGamelet(String assessmentId, String type) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(NewAlertActivity.this, GameLetActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("feedType", type);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void feedClicked(long feedId) {
        try {
            ClickedFeedData clickedFeedData = new ClickedFeedData();
            clickedFeedData.setFeedId((int) feedId);
            clickedFeedData.setClickedTimestamp("" + System.currentTimeMillis());
            if (clickedFeedDataList != null) {
                clickedFeedDataList.add(clickedFeedData);
            } else {
                clickedFeedDataList = new ArrayList<>();
                clickedFeedDataList.add(clickedFeedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void feedViewed(long newFeedId) {
        ViewedFeedData viewedFeedData = new ViewedFeedData();
        viewedFeedData.setFeedId((int) newFeedId);
        viewedFeedData.setViewedTimestamp("" + System.currentTimeMillis());
        if (viewedFeedDataMap == null) {
            viewedFeedDataMap = new HashMap<>();
        }
        if (viewedFeedDataMap != null && !viewedFeedDataMap.containsKey("" + newFeedId)) {
            Log.e("FEED", "fed vieewd  -->" + newFeedId);
            if (alreadyAddedFeedDataMap != null && !alreadyAddedFeedDataMap.containsKey("" + newFeedId)) {
                viewedFeedDataMap.put("" + newFeedId, viewedFeedData);
            } else if (alreadyAddedFeedDataMap == null) {
                viewedFeedDataMap.put("" + newFeedId, viewedFeedData);
            }
        }
    }

    @Override
    public void onFeedViewedInScroll(int position) {
        if (newFeedAdapter != null) {
            newFeedAdapter.onFeedViewedInScroll(position);
        }
    }

    @Override
    public void gotoAssessment(String assessmentId) {
        try {
            Log.e("Feed", "inside gotoAssessment() method");
            if (assessmentId.contains("COURSE")) {
                assessmentId = assessmentId.replace("COURSE", "");
            } else if (assessmentId.contains("course")) {
                assessmentId = assessmentId.replace("course", "");
            }
            String id = "ASSESSMENT" + assessmentId;
            if (myDeskMap != null && myDeskMap.containsKey(id)) {
                Log.e("Feed", "myDeskMap != null && myDeskMap.containsKey(id)");

                if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                    Gson gson = new GsonBuilder().create();
                    Intent intent;
                    if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
                        intent = new Intent(NewAlertActivity.this, AssessmentDetailScreen.class);
                    }else{
                        intent = new Intent(NewAlertActivity.this, AssessmentPlayActivity.class);
                    }
                    //Intent intent = new Intent(NewAlertActivity.this, AssessmentDetailScreen.class);
                    boolean isAssessmentValid = false;
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
                    setGame(activeUser);
                    intent.putExtra("ActiveGame", gson.toJson(activeGame));
                    intent.putExtra("ActiveUser", gson.toJson(activeUser));
                    startActivity(intent);
                }
            } else {
                Log.e("Feed", "myDeskMap == null && myDeskMap.donotcontainsKey(id)");
                if (myDeskMap != null) {
                    Log.e("Feed", "myDeskMap != null");
                    Intent intent = new Intent(NewAlertActivity.this, CatalogInfoActivity.class);
                    intent.putExtra("catalog_id", assessmentId);
                    intent.putExtra("catalog_type", "ASSESSMENT");
                    intent.putExtra("deskmap", myDeskMap);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setGame(ActiveUser activeUser) {
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);

        }
    }


    //goto particular event if present in running node else goto event page
    @Override
    public void gototEvent(String eventID) {
//        Intent intent;
//        boolean containsEvent=false;
//        for (int i = 0; i< OustAppEvents.getInstance().getEvents().size(); i++){
//            if(eventID.equalsIgnoreCase(OustAppEvents.getInstance().getEvents().get(i).getEventId())){
//                OustAppEvents.getInstance().setSelectedEvent(OustAppEvents.getInstance().getEvents().get(i));
//                intent=new Intent(NewAlertActivity.this,EventActivity.class);
//                startActivity(intent);
//                containsEvent=true;
//            }
//        }
//        if(!containsEvent){
//            intent=new Intent(NewAlertActivity.this,OustContestActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    public void gotoCourse(String courseID) {
        if (courseID.contains("COURSE")) {
            courseID = courseID.replace("COURSE", "");
        } else if (courseID.contains("course")) {
            courseID = courseID.replace("course", "");
        }
        String id = "COURSE" + courseID;
        if (myDeskMap != null && myDeskMap.containsKey(id)) {
//            Intent intent = new Intent(NewAlertActivity.this, NewLearningMapActivity.class);
//            intent.putExtra("learningId", courseID);
//            startActivity(intent);
            readDataFromFirebaseForCourse(courseID);
        } else {
            if (myDeskMap != null) {
                Intent intent = new Intent(NewAlertActivity.this, CatalogInfoActivity.class);
                intent.putExtra("catalog_id", courseID);
                intent.putExtra("catalog_type", "COURSE");
                intent.putExtra("deskmap", myDeskMap);
                startActivity(intent);
            }
        }
    }

    public void readDataFromFirebaseForCourse(final String courseID) {
        try {
            String message = ("course/course" + courseID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            CommonTools commonTools = new CommonTools();
                            final Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                            if ((lpMap != null) && (lpMap.get("courseId") != null)) {
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonLandingData.setId("COURSE" + courseID);
                                commonLandingData = commonTools.getCourseCommonData(lpMap, commonLandingData);
                                goToCoursePage(commonLandingData, courseID);
                            }
                        }

                    } catch (Exception e) {
                        goToCoursePage(null, courseID);
                        Log.e(TAG, "caught exception inside set singelton ", e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    goToCoursePage(null, courseID);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(eventListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            goToCoursePage(null, courseID);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void goToCoursePage(CommonLandingData commonLandingData, String courseID) {
        try {
            if (commonLandingData != null) {
                if (commonLandingData.getCourseType() != null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")) {
                    Intent intent = new Intent(OustSdkApplication.getContext(), CourseMultiLingualActivity.class);
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
                    OustSdkApplication.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                    String id = commonLandingData.getId();
                    if (id.contains("COURSE"))
                        id = id.replace("COURSE", "");
                    intent.putExtra("learningId", id);
                    OustSdkApplication.getContext().startActivity(intent);
                }
            } else {
                Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                String id = commonLandingData.getId();
                if (id.contains("COURSE"))
                    id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                OustSdkApplication.getContext().startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadComplete() {
        try {
            if (courseCardClass != null) {
                downloadscreen_layout.setVisibility(View.GONE);
                Animation event_animzoomin = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.zomin);
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                transaction.replace(R.id.feed_card_layout, fragment);
                fragment.isComingFromFeed(true);
                fragment.setProgressVal(0);
                fragment.setCardttsEnabled(false);
                // TODO need to handle here
//                fragment.setCourseCardClass(courseCardClass);
                fragment.setLearningModuleInterface(NewAlertActivity.this);
                transaction.addToBackStack(null);
                transaction.commit();
                card_layout.setVisibility(View.VISIBLE);
                feedcard_toplayout.setVisibility(View.VISIBLE);
                card_layout.startAnimation(event_animzoomin);
                closecard_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getSupportFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                            changeOrientationPortrait();
                            card_layout.clearAnimation();
                            card_layout.setVisibility(View.GONE);
                            feedcard_toplayout.setVisibility(View.GONE);
                            if (newFeedAdapter != null) {
                                newFeedAdapter.enableButton();
                            }
                            return;
                        }
                    }
                });
            } else {
                //OustSdkTools.showToast("Something went wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    DTOCourseCard courseCardClass;

    @Override
    public void clickOnCard(DTOCourseCard courseCardClass) {
        this.courseCardClass = courseCardClass;
        createListOfMedia();
    }


    private List<String> mediaList;
    private List<String> pathList;
    private int noofTries = 0;

    private void createListOfMedia() {
        try {
            mediaList = new ArrayList<>();
            pathList = new ArrayList<>();
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                    if (courseCardClass.getCardMedia().get(k).getData() != null) {
                        switch (courseCardClass.getCardMedia().get(k).getMediaType()) {
                            case "IMAGE":
                                pathList.add("course/media/image/" + courseCardClass.getCardMedia().get(k).getData());
                                mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                break;
                            case "GIF":
                                pathList.add("course/media/gif/" + courseCardClass.getCardMedia().get(k).getData());
                                mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                break;
                            case "AUDIO":
                                pathList.add("course/media/audio/" + courseCardClass.getCardMedia().get(k).getData());
                                mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                        }
                    }
                }
            }
            if ((courseCardClass != null) && (courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getRmId() > 0)) {
                switch (courseCardClass.getReadMoreData().getType()) {
                    case "PDF":
                        pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                    case "IMAGE":
                        pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                }
            }
            checkMediaExist();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int mediaSize = 0;

    private void checkMediaExist() {
        mediaSize = 0;
        for (int i = 0; i < mediaList.size(); i++) {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + mediaList.get(i))) {
                mediaSize++;
                downLoad(mediaList.get(i), pathList.get(i));
            }
        }
        if (mediaSize == 0) {
            removeFile();
        }
    }

    public void removeFile() {
        if (mediaSize > 0) {
            mediaSize--;
        }
        if (mediaSize == 0) {
            downloadComplete();
        }
    }

    public void downLoad(final String fileName1, final String pathName) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                downloadscreen_layout.setVisibility(View.GONE);
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
                return;
            }
            downloadscreen_layout.setVisibility(View.VISIBLE);
            animateLoader();
            String key = pathName;
            final File file = new File(this.getFilesDir(), "oustlearn_");
            String destination = this.getFilesDir()+"/";
            downLoadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+pathName, destination, fileName1,  true, false);

           /* AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());

            if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, fileName1, false);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            noofTries++;
                            if (noofTries > 4) {
                                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg"));
                            } else {
                                downLoad(fileName1, pathName);
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if ((!OustSdkTools.checkInternetStatus())) {
                            downloadscreen_layout.setVisibility(View.GONE);
                            OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection"));
                            return;
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        noofTries++;
                        if (noofTries > 4) {
                            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg"));
                        } else {
                            downLoad(fileName1, pathName);
                        }
                    }
                });
            } else {
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setReceiver();
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;
    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        registerReceiver(myFileDownLoadReceiver, intentFilter);

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }
    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                if(intent.getAction()!=null)
                {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS))
                        {
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        }
                        else if(intent.getAction().equalsIgnoreCase(ACTION_COMPLETE))
                        {
                            saveData(new File(""), "", false);
                        downloadscreen_layout.setVisibility(View.GONE);
                        }
                        else if(intent.getAction().equalsIgnoreCase(ACTION_ERROR))
                        {
                            //OustSdkTools.showToast("Download Failed");
                            downloadscreen_layout.setVisibility(View.GONE);
                            // mDownLoadUpdateInterface.onDownLoadError(intent.getStringExtra("MSG"), _FAILED);

                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
        }
    }

    private void animateLoader() {
        downloadscreen_layout.setAlpha(0.f);
        downloadscreen_layout.setScaleX(0.f);
        downloadscreen_layout.setScaleY(0.f);
        downloadscreen_layout.animate()
                .alpha(1.f)
                .scaleX(1.f).scaleY(1.f)
                .setDuration(300)
                .start();
    }


    public void saveData(File file, String fileName1, boolean isGifFeed) {
        try {
//            byte[] bytes = FileUtils.readFileToByteArray(file);
//            String encoded = Base64.encodeToString(bytes, 0);
//            if (fileName1.contains("pdf")) {
//                byte[] b = FileUtils.readFileToByteArray(file);
//                encoded = Base64.encodeToString(b, Base64.DEFAULT);
//                Log.e("ReadMore", encoded);
//            }
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            enternalPrivateStorage.saveFile("oustlearn_" + fileName1, encoded);
//            file.delete();
            removeFile();
            if (isGifFeed) {
                if (newFeedAdapter != null) {
                    newFeedAdapter.notifyFeedChange(DTONewFeedList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            feedcard_toplayout.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            feedcard_toplayout.setVisibility(View.VISIBLE);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void changeOrientationLandscape() {
        feedcard_toplayout.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void changeOrientationUnSpecific() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public void endActivity() {
    }

    @Override
    public void restartActivity() {
    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {
    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
    }

    @Override
    public void showCourseInfo() {
    }

    @Override
    public void saveVideoMediaList(List<String> videoMediaList) {
    }

    @Override
    public void sendCourseDataToServer() {
    }

    @Override
    public void dismissCardInfo() {
    }

    @Override
    public void setFavCardDetails(List<FavCardDetails> favCardDetails) {
    }

    @Override
    public void setFavoriteStatus(boolean status) {
    }

    @Override
    public void setRMFavouriteStatus(boolean status) {
    }

    @Override
    public void setShareClicked(boolean isShareClicked) {
    }

    @Override
    public void gotoNextScreen() {
    }

    @Override
    public void gotoPreviousScreen() {
    }

    @Override
    public void changeOrientationPortrait() {
        feedcard_toplayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void gotoGroupPage(String groupid) {
    }

    @Override
    public void moduleUnlock(String moduleID) {
    }

    @Override
    public void doubleReferalPopup() {
    }

    public void readMoreDismiss() {
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            getSupportFragmentManager().popBackStack();
        }
        feedcard_toplayout.setVisibility(View.VISIBLE);
        actionSearch.setVisible(true);
    }

    @Override
    public void openReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOCourseCard courseCardClass) {
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            return;
        }
        feedcard_toplayout.setVisibility(View.GONE);
        if (actionSearch != null) {
            actionSearch.setVisible(false);
        }
        ReadmorePopupFragment readmorePopupFragment = new ReadmorePopupFragment();
        readmorePopupFragment.showLearnCard(NewAlertActivity.this, readMoreData, false, null, courseCardClass.getCardColorScheme().getBgImage(), null, this, courseCardClass, null);
        readmorePopupFragment.isComingfromFeedCard(true);
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
        transaction.add(R.id.feed_card_layout, readmorePopupFragment, "read_fragment").addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            getSupportFragmentManager().popBackStack();
            feedcard_toplayout.setVisibility(View.VISIBLE);
            actionSearch.setVisible(true);
            if (newFeedAdapter != null) {
                newFeedAdapter.enableButton();
            }
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            changeOrientationPortrait();
            card_layout.clearAnimation();
            card_layout.setVisibility(View.GONE);
            feedcard_toplayout.setVisibility(View.GONE);
            if (newFeedAdapter != null) {
                newFeedAdapter.enableButton();
            }
            return;
        }

        if (isWebviewOpen) {
            isWebviewOpen = false;
            showfirstbackWebViewAnim();
            return;
        }

        sendFeedClickedRequestToBackend();
        super.onBackPressed();
    }

    private void sendFeedClickedRequestToBackend() {
        try {
            Log.e("FEED", "feed sent");
            if ((clickedFeedDataList != null && clickedFeedDataList.size() > 0)
                    || (viewedFeedDataMap != null && viewedFeedDataMap.size() > 0)) {
                ClickedFeedRequestData clickedFeedRequestData = new ClickedFeedRequestData();
                clickedFeedRequestData.setClickedFeedDataList(clickedFeedDataList);
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
                if (requests == null) {
                    requests = new ArrayList<>();
                }
                if (requests != null) {
                    requests.add(str);
                }
                clickedFeedDataList = null;
                viewedFeedDataMap = null;
                OustPreferences.saveLocalNotificationMsg("savedFeedClickedRequests", requests);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, FeedBackService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void disableBackButton(boolean disableBackButton) {
    }

    @Override
    public void closeCourseInfoPopup() {
    }

    @Override
    public void stopTimer() {

    }

    @Override
    public void isLearnCardComplete(boolean isLearnCardComplete) {

    }

    @Override
    public void closeChildFragment() {

    }

    @Override
    public void setVideoOverlayAnswerAndOc(String userAnswer, String subjectiveResponse, int oc, boolean status, long time, String childCardId) {

    }

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {

    }

    @Override
    public void rateApp() {
        try {
            String packageName = getApplicationContext().getPackageName();
            Log.e("Package : ", packageName);
            if ((packageName != null) && (!packageName.isEmpty())) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.oustme.oustapp")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createLoader() {
        try {
            swipe_refresh_layout.setVisibility(View.VISIBLE);
            swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
//               creates loader
            swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipe_refresh_layout.setRefreshing(false);
                }
            });
//            show loader
            swipe_refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    if ((DTONewFeedList != null) && (DTONewFeedList.size() > 0)) {
                    } else {
                        swipe_refresh_layout.setRefreshing(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void joinMeeting(String meetingId) {
        if ((meetingId != null) && (meetingId.length() > 8) && (meetingId.length() < 12)) {
//            Intent intent = new Intent("oust_zoommeeting");
//            intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
//            intent.putExtra("meetingId", meetingId);
//            sendBroadcast(intent);
//            String packageName = getApplicationContext().getPackageName();
//            if((packageName.equalsIgnoreCase("com.oustme.oustlive"))){
//                Intent intent = new Intent("oust_zoommeeting");
//                intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
//                intent.putExtra("meetingId", meetingId);
//                sendBroadcast(intent);
//            }else {
            boolean isAppInstalled = appInstalledOrNot("com.oustme.oustlive");
            if (isAppInstalled) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                intent.putExtra("zoommeetingId", meetingId);
                intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                intent.putExtra("isComingThroughOust", true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ZoomBaseActivity.class);
                intent.putExtra("joinMeeting", true);
                startActivity(intent);
            }
//            }
        } else {
            newFeedAdapter.enableButton();
            OustSdkTools.showToast(getResources().getString(R.string.invalid_meeting_id));
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    @Override
    protected void onPause() {
        sendFeedClickedRequestToBackend();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(myFileDownLoadReceiver!=null)
        {
            unregisterReceiver(myFileDownLoadReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if ((firebaseRefClass != null)) {
                OustFirebaseTools.getRootRef().child(firebaseRefClass.getFirebasePath()).removeEventListener(firebaseRefClass.getEventListener());
            }
            if ((firebaseRefClass1 != null)) {
                OustFirebaseTools.getRootRef().child(firebaseRefClass1.getFirebasePath()).removeEventListener(firebaseRefClass1.getEventListener());
            }
            userDTONewFeedList = null;
        } catch (Exception e) {
        }
    }
    @Override
    public void isSurveyCompleted(boolean surveyCompleted) {

    }

    @Override
    public void onSurveyExit(String message) {

    }

    @Override
    public void handleQuestionAudio(boolean play) {

    }

    @Override
    public void handleFragmentAudio(String audioFile) {

    }
}
