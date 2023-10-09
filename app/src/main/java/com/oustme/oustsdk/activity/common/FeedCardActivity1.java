package com.oustme.oustsdk.activity.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.api_sdk.utils.ApiConstants;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.interfaces.common.NewsFeedProgressListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.launcher.OustLauncher;
import com.oustme.oustsdk.model.request.FeedProgressModel;
import com.oustme.oustsdk.request.SubmitCourseCardRequestData;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.service.SubmitRequestsService;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;


public class FeedCardActivity1 extends AppCompatActivity implements LearningModuleInterface, NewsFeedProgressListener {

    private static final String TAG = "FeedCardActivity";
    private Toolbar toolbar;
    private WebView mWebView;
    private RelativeLayout webView_ll, downloadscreen_layout, card_layout, feedcard_toplayout, closecard_layout, image_ll;
    private FrameLayout feed_card_layout;
    private DTOCourseCard courseCardClass;
    private ProgressBar mProgressBarWebLoad;
    private ImageButton zooming_imgclose_btn;
    private GifImageView mainzooming_img;
    private String feedId = "";
    private boolean prgressSent = false;
    private DownloadFiles downLoadFiles;
    private boolean isEventCard = false;
    private int eventId = 0;
    private long courseId=0, levelId=0, cardId=0;
    private boolean isVideoCard = false;
    private ImageView close_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_feed_card);
        initViews();
        //setToolbar();
        getIntentData();

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

    private void initViews() {
        Log.d(TAG, "initViews: feedcard");
        toolbar = findViewById(R.id.tabanim_toolbar);
        mWebView = findViewById(R.id.webView);
        webView_ll = findViewById(R.id.webView_ll);
        downloadscreen_layout = findViewById(R.id.downloadscreen_layout);
        card_layout = findViewById(R.id.card_layout);
        feedcard_toplayout = findViewById(R.id.feedcard_toplayout);
        closecard_layout = findViewById(R.id.closecard_layout);
        close_card = findViewById(R.id.close_card);
        feed_card_layout = findViewById(R.id.feed_card_layout);
        image_ll = findViewById(R.id.image_ll);
        mainzooming_img = findViewById(R.id.mainzooming_img);
        zooming_imgclose_btn = findViewById(R.id.zooming_imgclose_btn);

        mProgressBarWebLoad = findViewById(R.id.progressbar_web);
        closecard_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        zooming_imgclose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(isEventCard && !isVideoCard) {
            //updateFeedProgress(100);
            updateFeedProgressNew(100, 0, true);
        }
        finish();
    }

    private void getIntentData() {
        Log.d(TAG, "getIntentData: ");
        if (getIntent() != null) {
            String type = "";
            String mUrl = "";
            type = getIntent().getStringExtra("type");

            feedId = getIntent().getStringExtra("feedId");
            isEventCard = getIntent().getBooleanExtra("isEventCard",false);
            eventId = getIntent().getIntExtra("eventId",0);

            if(getIntent().hasExtra("courseId")) {
                courseId = getIntent().getLongExtra("courseId", 0);
            }
            if(getIntent().hasExtra("levelId")) {
                levelId = getIntent().getLongExtra("levelId", 0);
            }
            if(getIntent().hasExtra("cardId")) {
                cardId = getIntent().getLongExtra("cardId", 0);
            }

            if(!isEventCard) {
                setToolbar();
            }else{
                Log.d(TAG, "setToolbar: no titlebar");
                toolbar.setVisibility(View.GONE);
                try {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().hide();
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
            if (type.equalsIgnoreCase("card")) {
                loadCardData();
            } else if (type.equalsIgnoreCase("url")) {
                mUrl = getIntent().getStringExtra("mUrl");
                openUrl(mUrl);
            } else if (type.equalsIgnoreCase("image")) {
                changeOrientationUnSpecific();
                String imageUrl = getIntent().getStringExtra("image");
                loadImage(imageUrl);
            }

        } else {
            OustSdkTools.showToast(getResources().getString(R.string.error_message));
        }
    }

    private void loadCardData() {
        courseCardClass = OustDataHandler.getInstance().getCourseCardClass();
        OustDataHandler.getInstance().setCourseCardClass(null);
        if (courseCardClass == null) {
            if (feedId != null && !feedId.isEmpty()) {
                OustDataHandler.getInstance().setFeedId(feedId);
                getFeedData();
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.error_message));
                finish();
            }
        } else {
            openFeedCard();
        }

    }

    private void getFeedData() {
        String message = "/feeds/feed" + feedId;
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
                            DTONewFeed feed1 = commonTools.getNewFeedFromMap(newsfeedMap, "");
                            feed1.setFeedId(Long.parseLong(key));
                            courseCardClass = feed1.getCourseCardClass();
                            if (courseCardClass != null) {
                                openFeedCard();
                            } else {
                                if (OustLauncher.getInstance().getOustModuleCallBack() != null) {
                                    OustLauncher.getInstance().getOustModuleCallBack().onModuleFailed(null);
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
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(newsfeedListListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void loadImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            image_ll.setVisibility(View.VISIBLE);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        mainzooming_img.setImageBitmap(bitmap);
                        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(mainzooming_img);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.get().load(imageUrl).into(target);

        }
    }

    private void openUrl(String mUrl) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            OustSdkTools.showProgressBar();
            mProgressBarWebLoad.setVisibility(View.VISIBLE);
            showFirstWebViewAnim();
            mWebView.setWebViewClient(new MyWebViewClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(mUrl);
            mWebView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress == 100) {
                        OustSdkTools.hideProgressbar();
                        mProgressBarWebLoad.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoNextScreen() {

    }

    @Override
    public void gotoPreviousScreen() {

    }

    @Override
    public void changeOrientationPortrait() {
        Log.d(TAG,"ChangeOientationPortrait");
        feedcard_toplayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void changeOrientationLandscape() {
        Log.d(TAG,"changeOrientationLandscape");
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
    public void openReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOCourseCard courseCardClass) {
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            return;
        }
        feedcard_toplayout.setVisibility(View.GONE);
        ReadmorePopupFragment readmorePopupFragment = new ReadmorePopupFragment();
        readmorePopupFragment.showLearnCard(this, readMoreData, false, null, courseCardClass.getCardColorScheme().getBgImage(), null, this, courseCardClass, null);
        readmorePopupFragment.isComingfromFeedCard(true);
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
        transaction.add(R.id.feed_card_layout, readmorePopupFragment, "read_fragment").addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void readMoreDismiss() {
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            getSupportFragmentManager().popBackStack();
        }
        feedcard_toplayout.setVisibility(View.VISIBLE);
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

   /* public void updateFeedProgress(int progress, long timeInterval, boolean isCompleted) {
        Log.d(TAG, "Progress:" + progress);
        createCardResponse(progress, timeInterval, isCompleted);
    }*/

    private void createCardResponse(int progress, long timeInterval, boolean isCompleted) {
        Log.d(TAG, "createCardResponse: ");
        if ((feedId != null || isEventCard) && !prgressSent && progress > 0) {
            prgressSent = true;
            Date date = new Date();
            long l1 = date.getTime();

            LearningCardResponceData learningCardResponceData = new LearningCardResponceData();
            learningCardResponceData.setCourseId((int) courseId);
            learningCardResponceData.setCourseLevelId((int) levelId);
            learningCardResponceData.setCourseCardId((int) cardId);
            learningCardResponceData.setXp(0);
            learningCardResponceData.setCorrect(false);
            //learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
            learningCardResponceData.setCardSubmitDateTime("" + l1);

            learningCardResponceData.setVideoCompletionPercentage(progress + "%");
            learningCardResponceData.setCardViewInterval(timeInterval);
            learningCardResponceData.setCardCompleted(isCompleted);

            sendCardSubmitRequest(learningCardResponceData);
        }
    }

    public void updateFeedProgress(int progress) {
        Log.d(TAG, "Progress:" + progress);
        if ((feedId != null || isEventCard) && !prgressSent && progress > 0) {
            prgressSent = true;
            sendProgressToBAckEnd(progress);
        }
    }

    @Override
    public void updateFeedProgressNew(int progress, long timeInterval, boolean isCompleted) {
        Log.d(TAG, "updateFeedProgressNew ---- Progress:" + progress+" --- time;"+timeInterval+" --- completed:"+isCompleted);
        createCardResponse(progress, timeInterval, isCompleted);
    }

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    private void openFeedCard() {
        card_layout.setVisibility(View.VISIBLE);
        createListOfMedia();
    }

    private void showFirstWebViewAnim() {
        try {
            webView_ll.setVisibility(View.VISIBLE);
            webView_ll.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolbar() {
        try {
            Log.d(TAG, "setToolbar: no exception");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView = toolbar.findViewById(R.id.title);
            titleTextView.setText(OustStrings.getString("newlandingalert_heading").toUpperCase());
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
                mProgressBarWebLoad.getIndeterminateDrawable()
                        .setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
            }
        } catch (Exception e) {
            Log.d(TAG, "setToolbar: exception");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (isEventCard) {
                toolbar.setVisibility(View.GONE);
            }
        }
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
        downLoadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {

            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                //OustSdkTools.showToast(message);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    removeFile();
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
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
                OustSdkTools.showToast(OustStrings.getString("noInternetConnectionMsg"));
                return;
            }
            downloadscreen_layout.setVisibility(View.VISIBLE);
            animateLoader();

            final File file2 = new File(this.getFilesDir(), "oustlearn_" + fileName1);
            String destn = this.getFilesDir() + "/";
            if (!file2.exists()) {
                downLoadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destn, fileName1, true, false);
            }

           /* AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            String key = pathName;
            final File file = new File(this.getFilesDir(), "oustlearn_" + fileName1);
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
                                OustSdkTools.showToast(OustStrings.getString("networkfail_msg"));
                            } else {
                                downLoad(fileName1, pathName);
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if ((!OustSdkTools.checkInternetStatus())) {
                            downloadscreen_layout.setVisibility(View.GONE);
                            OustSdkTools.showToast(OustStrings.getString("noInternetConnectionMsg"));
                            return;
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        noofTries++;
                        if (noofTries > 4) {
                            OustSdkTools.showToast(OustStrings.getString("networkfail_msg"));
                        } else {
                            downLoad(fileName1, pathName);
                        }
                    }
                });
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

    @Override
    protected void onStop() {
        super.onStop();
        if (myFileDownLoadReceiver != null) {
            unregisterReceiver(myFileDownLoadReceiver);
        }
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
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            removeFile();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            // OustSdkTools.showToast("Download Failed");
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
            removeFile();
        } catch (Exception e) {
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

                /*if ((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionType().equals(QuestionType.FILL)) ||
                        (courseCardClass.getQuestionType().equals(QuestionType.FILL_1)) ||
                        ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                        ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                        ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                    LearningPlayFragmentNew fragment = new LearningPlayFragmentNew();
                    transaction.replace(R.id.feed_card_layout, fragment);

                    fragment.isComingFromFeed(true);
                    fragment.setLearningModuleInterface(FeedCardActivity.this);
                    fragment.setLearningcard_progressVal(0);
                    fragment.setZeroXpForQCard(false);
                    fragment.setCardBackgroundImage(courseCardClass.getCardBgImage());
                    fragment.setIsRMFavourite(false);
                    fragment.setMainCourseCardClass(courseCardClass);
                    fragment.setNewsFeedProgressListener(this);
                    transaction.commit();
                }
                if ((courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) || (courseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {

                }*/

                isVideoCard = courseCardClass != null && courseCardClass.getCardMedia() != null && courseCardClass.getCardMedia().size() > 0 && courseCardClass.getCardMedia().get(0) != null && courseCardClass.getCardMedia().get(0).getMediaType() != null && courseCardClass.getCardMedia().get(0).getMediaType().equalsIgnoreCase("VIDEO");
                ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                transaction.replace(R.id.feed_card_layout, fragment);
                fragment.isComingFromFeed(true);
                fragment.setProgressVal(0);
                fragment.setCardttsEnabled(false);
                // TODO need to handle here
//                fragment.setCourseCardClass(courseCardClass);
                fragment.setLearningModuleInterface(FeedCardActivity1.this);
                fragment.setNewsFeedProgressListener(this);
                transaction.addToBackStack(null);
                fragment.setRegularMode(true);
                transaction.commit();
                card_layout.setVisibility(View.VISIBLE);
                feedcard_toplayout.setVisibility(View.VISIBLE);
                card_layout.startAnimation(event_animzoomin);
                if(isEventCard){
                    closecard_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    close_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.getBackStackEntryCount() > 0) {
                                if(isEventCard && !isVideoCard) {
                                    //updateFeedProgress(100);
                                    updateFeedProgressNew(100, 0, true);
                                }
                                fm.popBackStack();
                                changeOrientationPortrait();
                                card_layout.clearAnimation();
                                card_layout.setVisibility(View.GONE);
                                feedcard_toplayout.setVisibility(View.GONE);
                            }
                            finish();
                        }
                    });
                }else{
                    closecard_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.getBackStackEntryCount() > 0) {
                                if(isEventCard && !isVideoCard) {
                                    //updateFeedProgress(100);
                                    updateFeedProgressNew(100, 0, true);
                                }
                                fm.popBackStack();
                                changeOrientationPortrait();
                                card_layout.clearAnimation();
                                card_layout.setVisibility(View.GONE);
                                feedcard_toplayout.setVisibility(View.GONE);
                            }
                            finish();
                        }
                    });
                }

            } else {
                //OustSdkTools.showToast("Something went wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCardSubmitRequest(LearningCardResponceData learningCardResponceData) {
        try {
            Log.d(TAG, "sendCardSubmitRequest: ");
            ArrayList<LearningCardResponceData> learningCardResponceDataList = new ArrayList<>();
            learningCardResponceDataList.add(learningCardResponceData);

            String activeUserGet = OustPreferences.get("userdata");
            final String userId = OustSdkTools.getActiveUserData(activeUserGet).getStudentid();

            SubmitCourseCardRequestData submitCourseCardRequestData = new SubmitCourseCardRequestData();
            submitCourseCardRequestData.setStudentid(userId);
            submitCourseCardRequestData.setUserCardResponse(learningCardResponceDataList);

            String gcmToken = OustPreferences.get("gcmToken");
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitCourseCardRequestData.setDeviceToken(gcmToken);
            }
            Gson gson = new Gson();
            String str = gson.toJson(submitCourseCardRequestData);
            Log.d(TAG, "sendCardSubmitRequest: "+str);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
            if (requests != null) {
                requests.add(str);
            }
            OustPreferences.saveLocalNotificationMsg("savedSubmitRequest", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitRequestsService.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendProgressToBAckEnd(final int progress) {
        try {
            String feedProgressUrl = "";
            String activeUserGet = OustPreferences.get("userdata");
            final String userId = OustSdkTools.getActiveUserData(activeUserGet).getStudentid();

            if (isEventCard) {
                feedProgressUrl = ApiConstants.PUT_EVENT_STATUS_URL;
                Map<String, Object> postParam = new HashMap<>();
                try {
                    postParam.put("eventId", eventId);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, feedProgressUrl, new JSONObject(postParam), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success")) {
                            Log.d("response", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "" + error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            //params.put("api-key", OustPreferences.get("api_key"));
                            params.put("org-id", OustPreferences.get("tanentid"));
                            params.put("userId", userId);
                            if (!isVideoCard) {
                                params.put("status", "COMPLETED");
                            } else {
                                params.put("status", "" + (progress + "%"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        return params;
                    }
                };
                Log.d(TAG, "" + jsonObjReq.getHeaders().toString());
                jsonObjReq.setShouldCache(false);
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

            } else {
                JSONObject jsonObject = null;
                feedProgressUrl = getResources().getString(R.string.news_feed_progress_url);
                feedProgressUrl = feedProgressUrl.replace("{feedId}", feedId);
                feedProgressUrl = HttpManager.getAbsoluteUrl(feedProgressUrl);

                Gson gson = new Gson();
                FeedProgressModel feedProgressModel = new FeedProgressModel(progress, Integer.parseInt(feedId));
                try {
                    jsonObject = new JSONObject(gson.toJson(feedProgressModel));
                    jsonObject.put("userId", userId);
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, feedProgressUrl, OustSdkTools.getRequestObject(jsonObject.toString()), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success")) {
                            Log.d("response", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "" + error);
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
                OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
