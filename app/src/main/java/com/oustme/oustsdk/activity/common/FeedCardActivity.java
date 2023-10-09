package com.oustme.oustsdk.activity.common;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.IntroCardActivity;
import com.oustme.oustsdk.course_ui.IntroScormCardActivity;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FeedCardActivity extends AppCompatActivity implements LearningModuleInterface {

    private static final String TAG = "FeedCardActivity";
    private Toolbar toolbar;
    private WebView mWebView;
    private RelativeLayout webView_ll;
    private RelativeLayout downloadscreen_layout;
    private RelativeLayout card_layout;
    private RelativeLayout feedcard_toplayout;
    private RelativeLayout image_ll;
    private LinearLayout newalert_framelayout;
    private DTOCourseCard courseCardClass;
    private DTONewFeed feed;
    private DTOUserFeedDetails.FeedDetails feedDetails;
    private ProgressBar mProgressBarWebLoad;
    private GifImageView mainzooming_img;
    private DownloadFiles downLoadFiles;
    private boolean isFeedIntroCardActivity = false;
    private List<String> mediaList;
    private List<String> pathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_card);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews();
        setToolbar();
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
        isFeedIntroCardActivity = false;
        toolbar = findViewById(R.id.tabanim_toolbar);
        mWebView = findViewById(R.id.webView);
        webView_ll = findViewById(R.id.webView_ll);
        downloadscreen_layout = findViewById(R.id.downloadscreen_layout);
        card_layout = findViewById(R.id.card_layout);
        feedcard_toplayout = findViewById(R.id.feedcard_toplayout);
        RelativeLayout closeCard_layout = findViewById(R.id.closecard_layout);
        newalert_framelayout = findViewById(R.id.newalert_framelayout);
        image_ll = findViewById(R.id.image_ll);
        mainzooming_img = findViewById(R.id.mainzooming_img);
        ImageButton zooming_imgclose_btn = findViewById(R.id.zooming_imgclose_btn);

        mProgressBarWebLoad = findViewById(R.id.progressbar_web);
        closeCard_layout.setOnClickListener(view -> finish());
        zooming_imgclose_btn.setOnClickListener(v -> finish());

    }

    @Override
    public void onBackPressed() {
        backScreen();
    }

    private void backScreen() {
        try {
            /*if (courseCardClass != null) {
                Intent data = new Intent();
                data.putExtra("FeedPosition", courseCardClass.getCardId());
                data.putExtra("FeedComment", courseCardClass);
                data.putExtra("isFeedChange", isFeedChange);
                setResult(1444, data);
            }*/
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getIntentData() {
        Log.d(TAG, "getIntentData: ");
        if (getIntent() != null) {
            String type;
            String mUrl;
            type = getIntent().getStringExtra("type");
            if (type != null) {
                if (type.equalsIgnoreCase("card")) {
                    Bundle feedBundle = getIntent().getExtras();
                    if (feedBundle != null) {
                        feedDetails = feedBundle.getParcelable("Feed");
                    }
                    courseCardClass = OustDataHandler.getInstance().getCourseCardClass();
                    OustDataHandler.getInstance().setCourseCardClass(null);
                    if (courseCardClass == null) {
                        OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        finish();
                    }
                    openFeedCard();
                } else if (type.equalsIgnoreCase("url")) {
                    newalert_framelayout.setVisibility(View.VISIBLE);
                    mUrl = getIntent().getStringExtra("mUrl");
                    openUrl(mUrl);
                } else if (type.equalsIgnoreCase("image")) {
                    changeOrientationUnSpecific();
                    newalert_framelayout.setVisibility(View.VISIBLE);
                    String imageUrl = getIntent().getStringExtra("image");
                    loadImage(imageUrl);
                }
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.error_message));
        }
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
        feedcard_toplayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {

    }

    private static class MyWebViewClient extends WebViewClient {
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
            //  titleTextView.setText(OustStrings.getString("newlandingalert_heading").toUpperCase());
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
        }
    }

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
        try {
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
                File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + mediaList.get(i));
                if (!file.exists()) {
                    mediaSize++;
                    downLoad(mediaList.get(i), pathList.get(i));
                }
            }

            if (mediaSize < 1) {
                removeFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeFile() {
        try {
            if (mediaSize > 0) {
                mediaSize--;
            }
            if (mediaSize == 0) {
                downloadComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void downLoad(final String fileName1, final String pathName) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                downloadscreen_layout.setVisibility(View.GONE);
                OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                return;
            }
            downloadscreen_layout.setVisibility(View.VISIBLE);
            animateLoader();

            final File file2 = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + fileName1);
            String destn = this.getFilesDir() + "/";
            if (!file2.exists()) {
                downLoadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destn, fileName1, true, false);
            }

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
              /*  if (courseCardClass.getCardType().equalsIgnoreCase("SCORM")) {
                    isFeedIntroCardActivity = false;
                    FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                    ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                    transaction.replace(R.id.feed_card_layout, fragment);
                    fragment.isComingFromFeed(true);
                    fragment.setProgressVal(0);
                    fragment.setCardttsEnabled(false);
                    fragment.setCourseCardClass(courseCardClass);
                    fragment.setLearningModuleInterface(FeedCardActivity.this);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    card_layout.setVisibility(View.VISIBLE);
                    feedcard_toplayout.setVisibility(View.VISIBLE);
                    //card_layout.startAnimation(event_animzoomin);
                    closecard_layout.setOnClickListener(v -> onBackPressed());
                } else {*/
                isFeedIntroCardActivity = true;
                if (courseCardClass.getCardType() != null && !courseCardClass.getCardType().isEmpty()) {
                    Intent intent;
                    if (courseCardClass.getCardType().equalsIgnoreCase("SCORM")) {
                        intent = new Intent(FeedCardActivity.this, IntroScormCardActivity.class);
                    } else {
                        intent = new Intent(FeedCardActivity.this, IntroCardActivity.class);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Feed", feedDetails);
                    intent.putExtras(bundle);
                    Gson gson = new Gson();
                    String courseDataStr = gson.toJson(courseCardClass);
                    OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FeedCardActivity.this, IntroCardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Feed", feedDetails);
                    intent.putExtras(bundle);
                    Gson gson = new Gson();
                    String courseDataStr = gson.toJson(courseCardClass);
                    OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                    startActivity(intent);
                }
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFeedIntroCardActivity) {
            onBackPressed();
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
