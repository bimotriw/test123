package com.oustme.oustsdk.fragments.courses.adaptive;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.feed_ui.ui.VideoCardDetailScreen;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.request.AddFavReadMoreRequestData;
import com.oustme.oustsdk.response.course.CardReadMore;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardResponce;
import com.oustme.oustsdk.response.course.LearningCardType;
import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.service.SubmitFavouriteCardRequestService;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._CANCELED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._FAILED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;


///**
// * Created by shilpysamaddar on 15/06/17.
// */
//
public class AdaptiveReadmorePopupFragment extends Fragment {
    private LinearLayout moduleoverview_toplayout, learningcard_tuta, learningcard_tutb, bottomswipe_view, mainanim_layout;
    private TextView learningcard_coursename, cardprogress_text, downloadvideo_text;
    private ScrollView learningcard_tutscrollview;
    private HtmlTextView solution_headertext, solution_desc, learningtop_title, learningtop_desc;
    private ImageView solutionMain_imagea, solutionMain_imageb, youtubemainImage, videothumbnail_image, learning_halfimgage, learning_fullimgage,
            gyan_backgroundimage, questionmore_btn, gyan_arrowfoword, gyan_arrowback, video_expandbtn, optionmain_speaker,
            video_stopbtn, start_videobutton, unfavourite,video_landscape_zoom;
    private YouTubePlayerView learningtut_videoframelayout;
    private RelativeLayout learnngtut_mainimagelayout, learningtut_videolayout, quesvideoLayout, moduleoverview_animviewa, halfimage_layout, readmore_favourite_layout,
            fullimage_layout, video_progressbar_layout, rm_share_layout, readmore_webView_layout, readmore_close, rm_card_layout, rm_image_layout,
            main_readmore_layout, readmore_header, rm_main_layout, readmore_bottom_layout;
    private GifImageView downloadvideo_icon;
    //    public static TextView learningquiz_timertext;
    private PlayerView simpleExoPlayerView;
    private ProgressBar video_progressbar;
    private MediaPlayer mediaPlayer;
    private ProgressBar learningcard_videoloader;
    private Activity activity;
    private WebView readmore_webView;
    private DTOAdaptiveCardDataModel maincourseCardClass;

    private FavCardDetails favCardDetails = new FavCardDetails();
    private boolean isRMFavourite = false;
    private DTOReadMore readMoreData;
    private String courseId;
    private ImageView rm_image_view, video_icon;
    private Activity rm_activity;
    private ProgressBar rm_url_loader;

    private Handler myHandler;
    boolean containImage = false;
    private DTOCourseCard courseCardClass;
    private String cardBackgroundImage;
    private TransferUtility transferUtility;
    boolean isCardLinkFavourite = false;
    boolean isCardLinkFavouritePrev = false;
    private int noofTries = 0;
    private MediaController media_Controller;
    private int finalTime, currentTime;
    private String courseName;
    ActiveUser activeUser;
    private List<FavCardDetails> favCardDetailsList;
    private LearningModuleInterface learningModuleInterface;

    private String TAG = "ReadMorePOpup";
    private Drawable newlp_downloaded_drawable;
    private File tempVideoFileName;
    private DownloadFiles downloadFiles;
    private boolean isFileDownloadingSent;

    public void showLearnCard(final Activity activity, DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, List<FavCardDetails> favCardDetailsList, LearningModuleInterface learningModuleInterface, DTOAdaptiveCardDataModel courseCardClass1, String courseName1) {

//        initModuleViewFragment();
        this.activity = activity;
        courseId = courseId1;
        isRMFavourite = isRMFavourite1;
        rm_activity = activity;
        this.readMoreData = readMoreData;
        cardBackgroundImage = cardBackgroundImage1;
        this.favCardDetailsList = favCardDetailsList;
        this.learningModuleInterface = learningModuleInterface;
        this.maincourseCardClass = courseCardClass1;
        this.courseName = courseName1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OustStaticVariableHandling.getInstance().setLearningShareClicked(true);
        super.onCreate(savedInstanceState);
    }

    boolean isComingfromFeedCard;

    public void isComingfromFeedCard(boolean isComingfromFeedCard) {
        this.isComingfromFeedCard = isComingfromFeedCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.readmore_popup, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView(view);
        OustSdkTools.isReadMoreFragmentVisible = true;
        setStartingData();
        return view;
    }

    public void isFavourite(boolean isfavouriteClicked) {
        this.isRMFavourite = isfavouriteClicked;
    }


    private void initView(View popUpView) {
        moduleoverview_toplayout = popUpView.findViewById(R.id.moduleoverview_toplayout);
        learningcard_coursename = popUpView.findViewById(R.id.learningcard_coursename);
        learningcard_tutscrollview = popUpView.findViewById(R.id.learningcard_tutscrollview);
        solution_headertext = popUpView.findViewById(R.id.solution_headertext);
        solutionMain_imagea = popUpView.findViewById(R.id.solutionMain_imagea);
        learningcard_tuta = popUpView.findViewById(R.id.learningcard_tuta);
        solutionMain_imageb = popUpView.findViewById(R.id.solutionMain_imageb);
        learningcard_tutb = popUpView.findViewById(R.id.learningcard_tutb);
        bottomswipe_view = popUpView.findViewById(R.id.bottomswipe_view);
        learningtut_videoframelayout = popUpView.findViewById(R.id.learningtut_videoframelayout);
        learnngtut_mainimagelayout = popUpView.findViewById(R.id.learnngtut_mainimagelayout);
        youtubemainImage = popUpView.findViewById(R.id.youtubemainImage);
        learningtut_videolayout = popUpView.findViewById(R.id.learningtut_videolayout);
        quesvideoLayout = popUpView.findViewById(R.id.quesvideoLayout);
        simpleExoPlayerView = popUpView.findViewById(R.id.video_player_view);
        videothumbnail_image = popUpView.findViewById(R.id.videothumbnail_image);
        video_progressbar = popUpView.findViewById(R.id.video_progressbar);
        solution_desc = popUpView.findViewById(R.id.solution_desc);
        moduleoverview_animviewa = popUpView.findViewById(R.id.moduleoverview_animviewa);
        halfimage_layout = popUpView.findViewById(R.id.halfimage_layout);
        learning_halfimgage = popUpView.findViewById(R.id.learning_halfimgage);
        learningtop_title = popUpView.findViewById(R.id.learningtop_title);
        learningtop_desc = popUpView.findViewById(R.id.learningtop_desc);
        fullimage_layout = popUpView.findViewById(R.id.fullimage_layout);
        learning_fullimgage = popUpView.findViewById(R.id.learning_fullimgage);
        gyan_backgroundimage = popUpView.findViewById(R.id.gyan_backgroundimage);
        mainanim_layout = popUpView.findViewById(R.id.mainanim_layout);
        cardprogress_text = popUpView.findViewById(R.id.cardprogress_text);
        questionmore_btn = popUpView.findViewById(R.id.questionmore_btn);
        gyan_arrowfoword = popUpView.findViewById(R.id.gyan_arrowfoword);
        gyan_arrowback = popUpView.findViewById(R.id.gyan_arrowback);
        video_progressbar_layout = popUpView.findViewById(R.id.video_progressbar_layout);
        video_expandbtn = popUpView.findViewById(R.id.video_expandbtn);
        optionmain_speaker = popUpView.findViewById(R.id.optionmain_speaker);
        video_stopbtn = popUpView.findViewById(R.id.video_stopbtn);
        start_videobutton = popUpView.findViewById(R.id.start_videobutton);
        video_landscape_zoom= popUpView.findViewById(R.id.video_landscape_zoom);
        learningcard_videoloader = popUpView.findViewById(R.id.learningcard_videoloader);
        downloadvideo_icon = popUpView.findViewById(R.id.downloadvideo_icon);
        downloadvideo_text = popUpView.findViewById(R.id.downloadvideo_text);
        rm_main_layout = popUpView.findViewById(R.id.rm_main_layout);
        readmore_bottom_layout = popUpView.findViewById(R.id.readmore_bottom_layout);
        video_icon = popUpView.findViewById(R.id.video_icon);

        main_readmore_layout = popUpView.findViewById(R.id.main_readmore_layout);
        readmore_header = popUpView.findViewById(R.id.readmore_header);
        readmore_close = popUpView.findViewById(R.id.readmore_close);
        readmore_webView_layout = popUpView.findViewById(R.id.readmore_webView_layout);
        rm_share_layout = popUpView.findViewById(R.id.rm_share_layout);
        unfavourite = popUpView.findViewById(R.id.unfavourite);
        readmore_favourite_layout = popUpView.findViewById(R.id.readmore_favourite_layout);
        readmore_webView = popUpView.findViewById(R.id.readmore_webView);
        rm_image_layout = popUpView.findViewById(R.id.rm_image_layout);
        rm_image_view = popUpView.findViewById(R.id.rm_image_view);
        rm_card_layout = popUpView.findViewById(R.id.rm_card_layout);
        rm_url_loader = popUpView.findViewById(R.id.rm_url_loader);
        OustStaticVariableHandling.getInstance().setLearningquiz_timertext(popUpView.findViewById(R.id.learningquiz_timertext));

        newlp_downloaded_drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
        OustSdkTools.setImage(gyan_backgroundimage, OustSdkApplication.getContext().getString(R.string.bg_1));
        OustSdkTools.setImage(youtubemainImage, OustSdkApplication.getContext().getString(R.string.image));
        OustSdkTools.setImage(video_icon, OustSdkApplication.getContext().getString(R.string.challenge));
        OustSdkTools.setImage(start_videobutton, OustSdkApplication.getContext().getString(R.string.challenge));
        OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getString(R.string.newlp_notdownload));
        OustSdkTools.setImage(solutionMain_imagea, OustSdkApplication.getContext().getString(R.string.courses_bg));
        OustSdkTools.setImage(solutionMain_imageb, OustSdkApplication.getContext().getString(R.string.mydesk));
    }

    private void setStartingData() {
        if (readMoreData.getType() != null && readMoreData.getType().equalsIgnoreCase("CARD_LINK")) {
            if (!isComingfromFeedCard) {
                getFavouriteCardsFromFirebase();
            }
        }
        if (isComingfromFeedCard) {
            rm_share_layout.setVisibility(View.GONE);
            readmore_favourite_layout.setVisibility(View.GONE);
        }
//        if(!isRMFavourite) {
//            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
//            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.DarkGray));
//        } else {
//            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
//            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.Orange));
//        }
        if (isRMFavourite) {
            isRMFavourite = true;
            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
            unfavourite.setColorFilter(getActivity().getResources().getColor(R.color.Orange));
            unfavourite.setColorFilter(getResources().getColor(R.color.Orange));
        } else {
            isRMFavourite = false;
            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
            unfavourite.setColorFilter(getResources().getColor(R.color.DarkGray));
        }

        initS3Client();
        if (readMoreData.getType() != null && readMoreData.getType().equalsIgnoreCase("CARD_LINK")) {
//            courseCardClass=new CourseCardClass();
//            courseCardClass.setReadMoreData(readMoreData);
            downloadData(readMoreData.getData());
        } else if (readMoreData.getType() != null && readMoreData.getType().equalsIgnoreCase("IMAGE")) {
            learningModuleInterface.changeOrientationUnSpecific();
            allMediadownloadOver();
        } else {
            allMediadownloadOver();
        }
    }

    private void initS3Client() {
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
    }

    private void getFavouriteCardsFromFirebase() {
        activeUser = OustAppState.getInstance().getActiveUser();
        try {
            String message = "/userFavouriteCards/" + "user" + activeUser.getStudentKey() + "/course" + courseId + "/cards/card" + readMoreData.getData();
            Log.e(TAG, "getFavouriteCardsFromFirebase() " + message);
            ValueEventListener allfavCard = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            final Map<String, Object> allfavCardMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (allfavCardMap != null) {
                                favCardDetails = new FavCardDetails();
                                if (allfavCardMap.get("cardId") != null)
                                    favCardDetails.setCardId((String) allfavCardMap.get("cardId"));
                                if (allfavCardMap.get("imageUrl") != null)
                                    favCardDetails.setCardDescription((String) allfavCardMap.get("imageUrl"));
                                if (allfavCardMap.get("cardDescription") != null)
                                    favCardDetails.setCardDescription((String) allfavCardMap.get("cardDescription"));
                                if (allfavCardMap.get("cardTitle") != null)
                                    favCardDetails.setCardTitle((String) allfavCardMap.get("cardTitle"));
                                if (allfavCardMap.get("audio") != null)
                                    favCardDetails.setAudio((boolean) allfavCardMap.get("audio"));
                                if (allfavCardMap.get("video") != null)
                                    favCardDetails.setVideo((boolean) allfavCardMap.get("video"));
                            }
                            checkforFavouriteOfCardLink(favCardDetails.getCardId());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    Log.e(TAG, String.valueOf(DatabaseError));
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(allfavCard);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkforFavouriteOfCardLink(String cardId) {
        if (readMoreData.getData().equals(cardId)) {
            isCardLinkFavourite = true;
            isCardLinkFavouritePrev = true;
        } else isCardLinkFavourite = false;
        setFavouriteUnfavourite();
    }


    private void downloadData(String cardId) {
        int savedCardID = Integer.parseInt(cardId);
        courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
        if ((courseCardClass == null)) {
            if ((!OustSdkTools.checkInternetStatus())) {
                return;
            }
            downloadCard(Long.parseLong(cardId));
        } else {
            createListOfMedia();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
            }
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                optionmain_speaker.setAnimation(null);
            }
            File file = new File(Environment.getExternalStorageDirectory(), "newfile.pdf");
            if (file.exists()) {
                file.delete();
            }
            resetAllData();
        } catch (Exception e) {
            Log.e("EXCEPTION", "inside Destroy ", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadCard(final long cardId) {
        Log.d(TAG, "downloadCard: readmore");
        long levelId = 0;
        int courseId = 0;

        String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCardUrl_V2);
        //String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCard_url);
        getCardUrl = getCardUrl.replace("cardId", String.valueOf(cardId));
        getCardUrl = getCardUrl.replace("{courseId}", String.valueOf(courseId));
        getCardUrl = getCardUrl.replace("{levelId}", String.valueOf(levelId));
        try {
            getCardUrl = HttpManager.getAbsoluteUrl(getCardUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getCardUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    LearningCardResponce learningCardResponce = gson.fromJson(response.toString(), LearningCardResponce.class);
                    cardDownloadOver(learningCardResponce, cardId);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(OustStrings.getString("error_message"));
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getCardUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    LearningCardResponce learningCardResponce = gson.fromJson(response.toString(), LearningCardResponce.class);
                    cardDownloadOver(learningCardResponce, cardId);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(OustStrings.getString("error_message"));
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int noofAttempt = 0;

    public void cardDownloadOver(LearningCardResponce learningCardResponce, final long cardId) {
        try {
            if ((learningCardResponce != null) && (learningCardResponce.isSuccess())) {
                courseCardClass = learningCardResponce.getCard();
                if ((courseCardClass.getXp() == 0)) {
                    courseCardClass.setXp(100);
                }
                OustSdkTools.databaseHandler.addCardDataClass(courseCardClass, ((int) cardId));
                createListOfMedia();
            } else {
                noofAttempt++;
                if (noofAttempt < 3) {
                    downloadCard(cardId);
                } else {
                    //download error message
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private List<String> mediaList = new ArrayList<>();
    private List<String> pathList = new ArrayList<>();
    private List<String> videoMediaList = new ArrayList<>();
    private List<String> videoPathList = new ArrayList<>();

    public void createListOfMedia() {
        if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
            for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                if (courseCardClass.getCardMedia().get(k).getData() != null) {
                    switch (courseCardClass.getCardMedia().get(k).getMediaType()) {
                        case "AUDIO":
                            pathList.add("course/media/audio/" + courseCardClass.getCardMedia().get(k).getData());
                            mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                            break;
                        case "IMAGE":
                            pathList.add("course/media/image/" + courseCardClass.getCardMedia().get(k).getData());
                            mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                            break;
                        case "GIF":
                            pathList.add("course/media/gif/" + courseCardClass.getCardMedia().get(k).getData());
                            mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                            break;
                        case "VIDEO":
                            if (courseCardClass.getCardMedia().get(k).getMediaPrivacy() != null && courseCardClass.getCardMedia().get(k).getMediaPrivacy().equalsIgnoreCase("private")) {
                                videoPathList.add("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                videoMediaList.add(courseCardClass.getCardMedia().get(k).getData());
                            }
                            break;
                    }
                }
            }
        }
        if ((courseCardClass != null) && ((courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getScope() != null) && (courseCardClass.getReadMoreData().getScope().equalsIgnoreCase("private")))) {
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
    }


    private boolean conatinVideo = false;

    private void checkMediaExist() {
        mediaSize = 0;
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {

            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                if(errorCode==_FAILED || errorCode==_CANCELED)
                {
                    allMediadownloadOver();
                }
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                removeFile();
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        if (mediaList.size() > 0) {
            for (int i = 0; i < mediaList.size(); i++) {
                EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + mediaList.get(i))) {
                    mediaSize++;
                    downLoad(mediaList.get(i), pathList.get(i));
                }
            }
        }
        if (mediaSize == 0) {
            if ((videoMediaList.size() == 0)) {
                removeFile();
            }
        }
        if (videoMediaList.size() > 0) {
            conatinVideo = true;
        }
//        checkVideoMediaExist();
        if(mediaSize == 0) {
            allMediadownloadOver();
        }
    }


    private void setFavouriteUnfavourite() {
        if (isCardLinkFavourite) {
            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.Orange));
        } else {
            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.DarkGray));
        }
    }


    public void downLoad(final String fileName1, final String pathName) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
//                showNoInternetLayout();
                return;
            }
            String key = pathName;
            String destination = getActivity().getFilesDir()+"/";
            downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+pathName, destination, fileName1,true , false);
            isFileDownloadingSent = true;
            /*if (file != null) {
                TransferObserver transferObserver = transferUtility.download(AppConstants.MediaURLConstants.BUCKET_NAME, key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, fileName1);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            noofTries++;
                            if (noofTries > 2) {
//                                showNetworkErrorMessage();
                                allMediadownloadOver();
                            } else {
                                downLoad(fileName1, pathName);
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if ((!OustSdkTools.checkInternetStatus())) {
//                            showNoInternetLayout();
                            return;
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        noofTries++;
                        if (noofTries > 4) {
//                            showNetworkErrorMessage();
                            allMediadownloadOver();
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
    public void onStart() {
        super.onStart();
        setReceiver();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(myFileDownLoadReceiver!=null)
        {
            if(getActivity()!=null)
            {
                getActivity().unregisterReceiver(myFileDownLoadReceiver);
            }
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;
    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        if(getActivity()!=null)
        {
            getActivity().registerReceiver(myFileDownLoadReceiver, intentFilter);
        }

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }
    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                if(intent.getAction()!=null) {
                    if (isFileDownloadingSent) {
                        try {
                            if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                                setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")));
                                //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                                String path = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/" + videoFileName;
                                final File finalFile = new File(path);
                                tempVideoFileName.renameTo(finalFile);
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                                // OustSdkTools.showToast("Download Failed");
                                tempVideoFileName.delete();

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
    }

    public void saveData(File file, String fileName1) {
        try {
//            byte[] bytes = FileUtils.readFileToByteArray(file);
//            String encoded = Base64.encodeToString(bytes, 0);
//            if(fileName1.contains("pdf")){
//                byte[] b= FileUtils.readFileToByteArray(file);
//                encoded = Base64.encodeToString(b,Base64.DEFAULT);
//                Log.e("ReadMore", encoded);
//            }
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            enternalPrivateStorage.saveFile("oustlearn_"+fileName1, encoded);
//            file.delete();
            removeFile();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int mediaSize = 0;

    public void removeFile() {
        try {
            if (mediaSize > 0) {
                mediaSize--;
            }
            if(mediaSize == 0) {
                allMediadownloadOver();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setReadmoreImage(String fileName) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if((audStr!=null)&&(!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                rm_image_view.setImageBitmap(decodedByte);
//                rm_image_layout.setVisibility(View.VISIBLE);
//            }
            OustSdkTools.setImageA(rm_image_view,fileName);
            rm_image_layout.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    private boolean isWebViewOpen = false;

    private void showWebView(WebView readmore_webView, DTOReadMore readMoreData) {
        try {
//            opening read more url in webview

            if ((readMoreData != null) && ((readMoreData.getScope() != null) && (readMoreData.getScope().equalsIgnoreCase("public")))) {
                isWebViewOpen = true;
                learningModuleInterface.changeOrientationUnSpecific();
                rm_url_loader.setVisibility(View.VISIBLE);
                rm_main_layout.setVisibility(View.VISIBLE);
                readmore_webView.setWebViewClient(new MyWebViewClient());
                readmore_webView.clearCache(true);
                readmore_webView.getSettings().setUseWideViewPort(true);
                readmore_webView.setInitialScale(1);
                readmore_webView.getSettings().setBuiltInZoomControls(true);
                readmore_webView.clearHistory();
                readmore_webView.getSettings().setAllowFileAccess(true);
                readmore_webView.getSettings().setDomStorageEnabled(true);
                readmore_webView.getSettings().setJavaScriptEnabled(true);
                readmore_webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                readmore_webView.getSettings().setLoadWithOverviewMode(true);
                readmore_webView.getSettings().setUseWideViewPort(true);
                readmore_webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                readmore_webView.loadUrl(readMoreData.getData());
                readmore_webView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress > 94) {
                            rm_url_loader.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                isWebViewOpen = true;
                rm_url_loader.setVisibility(View.VISIBLE);
                learningModuleInterface.changeOrientationUnSpecific();
                readmore_webView.setWebViewClient(new MyWebViewClient());
                readmore_webView.getSettings().setJavaScriptEnabled(true);
                readmore_webView.loadUrl("https://www.oustme.com");
                readmore_webView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress > 97) {
                            OustSdkTools.hideProgressbar();
                            rm_url_loader.setVisibility(View.GONE);
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openWebViewForSDCard(WebView readmore_webView, DTOReadMore readMoreData) {
        try {
            isWebViewOpen = true;
            learningModuleInterface.changeOrientationUnSpecific();
            rm_url_loader.setVisibility(View.VISIBLE);
            rm_main_layout.setVisibility(View.VISIBLE);
            readmore_webView.setWebViewClient(new MyWebViewClient());
            readmore_webView.getSettings().setJavaScriptEnabled(true);
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.d(TAG, "No SDCARD");
            } else {
                String[] rv = getExternalStorageDirectories();
                boolean isFileAvailableOnSdCard = false;
                if (rv != null && rv.length > 0) {
                    for (int i = 0; i < rv.length; i++) {
                        File file = new File(rv[i] + "/OustHtml");
                        if (file.isDirectory() && file.exists()) {
                            readmore_webView.loadUrl("file:///" + rv[i] + "/OustHtml/" + readMoreData.getData());
                            isFileAvailableOnSdCard = true;
                        }
                    }
                }
                if (!isFileAvailableOnSdCard) {
                    readmore_webView.loadUrl("file:///" + Environment.getExternalStorageDirectory() + "/OustHtml/"  + readMoreData.getData());
                }
            }

            readmore_webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress > 94) {
                        rm_url_loader.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String[] getExternalStorageDirectories() {
        try {
            List<String> results = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //Method 1 for KitKat & above
                File[] externalDirs = OustSdkApplication.getContext().getExternalFilesDirs(null);

                for (File file : externalDirs) {
                    String path = file.getPath().split("/Android")[0];

                    boolean addPath = false;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        addPath = Environment.isExternalStorageRemovable(file);
                    } else {
                        addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                    }

                    if (addPath) {
                        results.add(path);
                    }
                }
            }

            if (results.isEmpty()) {
                //Method 2 for all versions
                // better variation of: http://stackoverflow.com/a/40123073/5002496
                String output = "";
                try {
                    final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                            .redirectErrorStream(true).start();
                    process.waitFor();
                    final InputStream is = process.getInputStream();
                    final byte[] buffer = new byte[1024];
                    while (is.read(buffer) != -1) {
                        output = output + new String(buffer);
                    }
                    is.close();
                } catch (final Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if (!output.trim().isEmpty()) {
                    String[] devicePoints = output.split("\n");
                    for (String voldPoint : devicePoints) {
                        results.add(voldPoint.split(" ")[2]);
                    }
                }
            }

            //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int i = 0; i < results.size(); i++) {
                    if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
//                    Log.d(LOG_TAG, results.get(i) + " might not be extSDcard");
                        results.remove(i--);
                    }
                }
            } else {
                for (int i = 0; i < results.size(); i++) {
                    if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
//                    Log.d(LOG_TAG, results.get(i)+" might not be extSDcard");
                        results.remove(i--);
                    }
                }
            }

            String[] storageDirectories = new String[results.size()];
            for (int i = 0; i < results.size(); i++) storageDirectories[i] = results.get(i);

            return storageDirectories;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    private final Pattern DIR_SEPARATOR = Pattern.compile("/");

    public String[] getSDCardPathWihoutHardCode() {
        // Final set of paths
        final Set<String> rv = new HashSet<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            //fix of empty raw emulated storage on marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                File[] files = OustSdkApplication.getContext().getExternalFilesDirs(null);
                for (File file : files) {
                    if (file == null) continue;
                    String applicationSpecificAbsolutePath = file.getAbsolutePath();
                    String emulatedRootPath = applicationSpecificAbsolutePath.substring(0, applicationSpecificAbsolutePath.indexOf("Android/data"));
                    rv.add(emulatedRootPath);
                }
            } else {
                // Device has physical external storage; use plain paths.
                if (TextUtils.isEmpty(rawExternalStorage)) {
                    // EXTERNAL_STORAGE undefined; falling back to default.
                    rv.addAll(Arrays.asList(getPhysicalPaths()));
                } else {
                    rv.add(rawExternalStorage);
                }
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }

        return rv.toArray(new String[rv.size()]);
    }

    private String[] getPhysicalPaths() {
        return new String[]{
                "/storage/sdcard0",
                "/storage/sdcard1",                 //Motorola Xoom
                "/storage/extsdcard",               //Samsung SGS3
                "/storage/sdcard0/external_sdcard", //User request
                "/mnt/extsdcard",
                "/mnt/sdcard/external_sd",          //Samsung galaxy family
                "/mnt/external_sd",
                "/mnt/media_rw/sdcard1",            //4.4.2 on CyanogenMod S3
                "/removable/microsd",               //Asus transformer prime
                "/mnt/emmc",
                "/storage/external_SD",             //LG
                "/storage/ext_sd",                  //HTC One Max
                "/storage/removable/sdcard1",       //Sony Xperia Z1
                "/data/sdext",
                "/data/sdext2",
                "/data/sdext3",
                "/data/sdext4",
                "/sdcard1",                         //Sony Xperia Z
                "/sdcard2",                         //HTC One M8s
                "/storage/microsd"                  //ASUS ZenFone 2
        };
    }

    public String getSDCardPath() {
        String sSDpath = "";
        File fileCur = null;
        for (String sPathCur : Arrays.asList("MicroSD", "microsd", "external_SD", "external_sd", "sdcard1", "ext_card", "external_sd", "ext_sd", "sdcard0", "extsdcard", "external", "extSdCard", "externalSdCard")) // external sdcard
        {

            fileCur = new File("/mnt/", sPathCur);
            if (fileCur.isDirectory() && fileCur.canWrite()) {
                sSDpath = fileCur.getAbsolutePath();
                break;
            }
            if (sSDpath.isEmpty()) {
                fileCur = new File("/storage/", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                }
            }
            if (sSDpath.isEmpty()) {
                fileCur = new File("/storage/emulated", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                }
            }
        }
        return sSDpath;
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    boolean isAllMediaDoanloadOver = false;
    private void allMediadownloadOver() {
        if(!isAllMediaDoanloadOver) {
            isAllMediaDoanloadOver=true;
            if (readMoreData.getType() != null && readMoreData.getType().equalsIgnoreCase("IMAGE")) {
                rm_card_layout.setVisibility(View.GONE);
                readmore_webView_layout.setVisibility(View.GONE);
                rm_image_layout.setVisibility(View.VISIBLE);
                setReadmoreImage("oustlearn_" + readMoreData.getData());
            } else if (readMoreData.getType() != null && readMoreData.getType().equalsIgnoreCase("CARD_LINK")) {
//            learningquiz_timertext.setVisibility(View.VISIBLE);
                OustStaticVariableHandling.getInstance().getLearningquiz_timertext().setVisibility(View.VISIBLE);
                initModuleViewFragment();
//            showLearnCard.showLearnCard(rm_activity,popUpView,courseCardClass,cardBackgroundImage);
            } else if (readMoreData.getType() != null && readMoreData.getType().equalsIgnoreCase("SD_CARD")) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) main_readmore_layout.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                main_readmore_layout.setLayoutParams(params);
                readmore_webView_layout.setVisibility(View.VISIBLE);
                rm_card_layout.setVisibility(View.GONE);
                rm_image_layout.setVisibility(View.GONE);
                openWebViewForSDCard(readmore_webView, readMoreData);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) main_readmore_layout.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                main_readmore_layout.setLayoutParams(params);
                readmore_webView_layout.setVisibility(View.VISIBLE);
                rm_card_layout.setVisibility(View.GONE);
                rm_image_layout.setVisibility(View.GONE);
                showWebView(readmore_webView, readMoreData);
            }
            readmore_favourite_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (readMoreData.getType() != null && readMoreData.getType().equalsIgnoreCase("CARD_LINK")) {
                        if (!isCardLinkFavourite) {
                            isCardLinkFavourite = true;
                            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
                            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.Orange));
                        } else {
                            isCardLinkFavourite = false;
                            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
                            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.DarkGray));
                        }
                    } else {
                        if (!isRMFavourite) {
                            isRMFavourite = true;
                            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
                            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.Orange));
                        } else {
                            isRMFavourite = false;
                            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
                            unfavourite.setColorFilter(rm_activity.getResources().getColor(R.color.DarkGray));
                        }

                        makeFavorite(isRMFavourite);
                    }
                }

            });

            rm_share_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OustStaticVariableHandling.getInstance().setLearningShareClicked(true);
                    Bitmap bitmap = OustSdkTools.getScreenShot(rm_main_layout);
                    String branchLinkUrl = "http://bit.ly/1xEh2HW";
                    OustShareTools.shareScreenUsingIntent(rm_activity, branchLinkUrl, bitmap);
                }
            });

            readmore_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    learningModuleInterface.changeOrientationPortrait();
                    OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
                    if (readmore_webView_layout.getVisibility() == View.VISIBLE) {
                        readmore_webView.loadUrl("about:blank");
                    }
                    OustSdkTools.isReadMoreFragmentVisible = false;
                    learningModuleInterface.readMoreDismiss();
                }
            });
        }
    }

    private void makeFavorite(boolean isRMFavourite) {
        try {
            learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            FavCardDetails favCardDetails = new FavCardDetails();
            this.isRMFavourite = isRMFavourite;
            if (isRMFavourite) {
                favCardDetails.setCardId("" + maincourseCardClass.getCardId());
                favCardDetails.setRmId(maincourseCardClass.getReadMoreData().getRmId());
                favCardDetails.setRmData(maincourseCardClass.getReadMoreData().getData());
                favCardDetails.setRMCard(true);
                favCardDetails.setRmDisplayText(maincourseCardClass.getReadMoreData().getDisplayText());
                favCardDetails.setRmScope(maincourseCardClass.getReadMoreData().getScope());
                favCardDetails.setRmType(maincourseCardClass.getReadMoreData().getType());

                favCardDetailsList.add(favCardDetails);
                learningModuleInterface.setFavCardDetails(favCardDetailsList);

            } else {
                learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onPause() {
        setDataToFirebase();
        if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
            time = simpleExoPlayerView.getPlayer().getCurrentPosition();
            simpleExoPlayerView.getPlayer().release();
            simpleExoPlayerView.setPlayer(null);
            Log.e("-------", "onPause");
        }
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    private void setDataToFirebase() {
        if ((isCardLinkFavourite) && (!isCardLinkFavouritePrev)) {
            checkforMedia();
            Map<String, Object> favCardDetails = new HashMap<>();
            favCardDetails.put("audio", isAudio);
            favCardDetails.put("video", isVideo);
            favCardDetails.put("cardDescription", courseCardClass.getContent());
            favCardDetails.put("cardId", "" + courseCardClass.getCardId());
            favCardDetails.put("cardTitle", courseCardClass.getCardTitle());
            favCardDetails.put("imageUrl", imageUrl);

//                    adding the levelId at the end in card details
            favCardDetails.put("levelId", "" + readMoreData.getLevelId());


            String message = "/userFavouriteCards" + "/user" + activeUser.getStudentKey() + "/course" + courseId + "/cards" + "/card" + readMoreData.getData();
            OustFirebaseTools.getRootRef().child(message).setValue(favCardDetails);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);

            if (courseName != null) {
                String message1 = "/userFavouriteCards" + "/user" + activeUser.getStudentKey() + "/course" + courseId + "/courseName";
                OustFirebaseTools.getRootRef().child(message1).setValue(courseName);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
            }
        }
        if ((!isCardLinkFavourite) && (isCardLinkFavouritePrev)) {
            sendUnFavCardToServer();
            String message = "/userFavouriteCards" + "/user" + activeUser.getStudentKey() + "/course" + courseId + "/cards" + "/card" + readMoreData.getData();
            OustFirebaseTools.getRootRef().child(message).setValue(null);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        }
    }

    boolean isAudio = false;
    boolean isVideo = false;
    String imageUrl;

    private void checkforMedia() {
        for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
            if (courseCardClass.getCardMedia().get(i).getMediaType().equalsIgnoreCase("Audio")) {
                isAudio = true;
            }
            if (courseCardClass.getCardMedia().get(i).getMediaType().equalsIgnoreCase("Video")) {
                isVideo = true;
            }
            if (courseCardClass.getCardMedia().get(i).getMediaThumbnail() != null) {
                imageUrl = courseCardClass.getCardMedia().get(i).getMediaThumbnail();
            }
            if (courseCardClass.getCardMedia().get(i).getData() != null) {
                imageUrl = courseCardClass.getCardMedia().get(i).getData();
            }
        }
        sendFavDataToServer();
    }

    private void sendFavDataToServer() {
        try {
            List<CardReadMore> cardReadMoreList = new ArrayList<>();
            CardReadMore rms = new CardReadMore();
            rms.setCardId(Integer.parseInt(readMoreData.getCardId()));
            rms.setRmId((int) readMoreData.getRmId());
            cardReadMoreList.add(rms);
            AddFavReadMoreRequestData addFavRMRequestData = new AddFavReadMoreRequestData();
            addFavRMRequestData.setRmIds(cardReadMoreList);
            addFavRMRequestData.setStudentid(activeUser.getStudentid());

            Gson gson = new Gson();
            String str = gson.toJson(addFavRMRequestData);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFavouriteRMRequests");
            if (requests == null) {
                requests = new ArrayList<>();
            }
            if (requests != null) {
                requests.add(str);
            }
            OustPreferences.saveLocalNotificationMsg("savedFavouriteRMRequests", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, rm_activity, SubmitFavouriteCardRequestService.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendUnFavCardToServer() {
        try {
            List<CardReadMore> cardReadMoreList = new ArrayList<>();
            CardReadMore rms = new CardReadMore();
            rms.setCardId(Integer.parseInt(readMoreData.getCardId()));
            rms.setRmId((int) readMoreData.getRmId());
            cardReadMoreList.add(rms);
            AddFavReadMoreRequestData addUnFavRMRequestData = new AddFavReadMoreRequestData();
            addUnFavRMRequestData.setRmIds(cardReadMoreList);
            addUnFavRMRequestData.setStudentid(activeUser.getStudentid());

            Gson gson = new Gson();
            String str = gson.toJson(addUnFavRMRequestData);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedUnfavouriteRMRequests");
            if (requests == null) {
                requests = new ArrayList<>();
            }
            if (requests != null) {
                requests.add(str);
            }
            OustPreferences.saveLocalNotificationMsg("savedUnfavouriteRMRequests", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, rm_activity, SubmitFavouriteCardRequestService.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
//    ======================================================================================================

    public void initModuleViewFragment() {
        try {
            setFontStyle();
            detectCardLayoutAndSetData();
            setColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setFontStyle() {
        if ((courseCardClass != null) && (courseCardClass.getLanguage() != null) && (courseCardClass.getLanguage().equals("en"))) {
            learningcard_coursename.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            learningtop_title.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            solution_headertext.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            learningtop_desc.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            solution_desc.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        }
    }

    private void detectCardLayoutAndSetData() {
        if (courseCardClass != null && courseCardClass.getClCode() != null) {
            if ((courseCardClass.getClCode().equals(LearningCardType.CL_T_VI)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_VI_A))) {
                fullimage_layout.setVisibility(View.VISIBLE);
                setFullImageRatio();
                showAllMediaFullImage();
                setTopContent();
                setTopContentTitle();
            } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_VI)) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_A))) {
                fullimage_layout.setVisibility(View.VISIBLE);
                setFullImageRatio();
                showAllMediaFullImage();
                setContent();
                setContentTitle();
            } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T_A)))) {
                halfimage_layout.setVisibility(View.VISIBLE);
                setHalfImageRatio();
                showAllMediaHalfImage();
                setContent();
                setContentTitle();
            } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_T_3_4_VI)) || ((courseCardClass.getClCode().equals(LearningCardType.CL_T_3_4_VI_A)))) {
                halfimage_layout.setVisibility(View.VISIBLE);
                setHalfImageRatio();
                showAllMediaHalfImage();
                setTopContent();
                setTopContentTitle();
            } else {
                showAllMedia();
                if ((courseCardClass.getClCode().equals(LearningCardType.CL_T_I)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_I_A)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_VD))) {
                    setTopContent();
                    setTopContentTitle();
                } else {
                    setContent();
                    setContentTitle();
                }
            }
        } else {
            showAllMedia();
            setContent();
            setContentTitle();
        }
    }

    private void setHalfImageRatio() {
        try {
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) learning_halfimgage.getLayoutParams();
            float h = (scrWidth * 1.15f);
            int h1 = (int) h;
            params.height = h1;
            learning_halfimgage.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFullImageRatio() {
        try {
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) learning_fullimgage.getLayoutParams();
            float h = (scrWidth * 1.5f);
            int h1 = (int) h;
            params.height = h1;
            learning_fullimgage.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setColors() {
        try {
            DTOCardColorScheme cardColorScheme = courseCardClass.getCardColorScheme();
            if (cardColorScheme != null) {
                if ((cardColorScheme.getIconColor() != null) && (!cardColorScheme.getIconColor().isEmpty())) {
                    questionmore_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
//                    optionmain_speaker.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
//                    gyan_arrowfoword.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
//                    gyan_arrowback.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                }
                if ((cardColorScheme.getLevelNameColor() != null) && (!cardColorScheme.getLevelNameColor().isEmpty())) {
                    learningcard_coursename.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                }
                if ((cardColorScheme.getTitleColor() != null) && (!cardColorScheme.getTitleColor().isEmpty())) {
                    learningtop_title.setTextColor(Color.parseColor(cardColorScheme.getTitleColor()));
                    solution_headertext.setTextColor(Color.parseColor(cardColorScheme.getTitleColor()));
                }
                if ((cardColorScheme.getContentColor() != null) && (!cardColorScheme.getContentColor().isEmpty())) {
                    learningtop_desc.setTextColor(Color.parseColor(cardColorScheme.getContentColor()));
                    solution_desc.setTextColor(Color.parseColor(cardColorScheme.getContentColor()));
                }
                if ((cardColorScheme.getBgImage() != null) && (!cardColorScheme.getBgImage().isEmpty())) {
                    setBackgroundImage(cardColorScheme.getBgImage());
                } else {
                    if ((cardBackgroundImage != null) && (!cardBackgroundImage.isEmpty())) {
                        setBackgroundImage(cardBackgroundImage);
                    }
                }
            } else {
                if ((cardBackgroundImage != null) && (!cardBackgroundImage.isEmpty())) {
                    setBackgroundImage(cardBackgroundImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setBackgroundImage(String bgImageUrl) {
        try {
            Drawable bg_drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.bg_1));
            if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                Picasso.get().load(bgImageUrl).placeholder(bg_drawable).error(bg_drawable).into(gyan_backgroundimage);
            } else {
                Picasso.get().load(bgImageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(bg_drawable).error(bg_drawable).into(gyan_backgroundimage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setContent() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty())) {
                solution_desc.setVisibility(View.VISIBLE);
                if (courseCardClass.getReadMoreData() != null && courseCardClass.getReadMoreData().getDisplayText() != null && courseCardClass.getReadMoreData().getType() != null && courseCardClass.getReadMoreData().getType().equalsIgnoreCase("pdf")) {
                    solution_desc.setHtml(courseCardClass.getContent() + " <br/> <a href=http://www.oustme.com>" + courseCardClass.getReadMoreData().getDisplayText() + "</a>");
                    solution_desc.setLinkTextColor(OustSdkTools.getColorBack(R.color.white_pressed));
                    solution_desc.setMovementMethod(new AdaptiveReadmorePopupFragment.TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            try {
                                if (OustSdkTools.canDisplayPdf(activity)) {
                                    OustStaticVariableHandling.getInstance().setLearningShareClicked(true);
                                    int sdk = Build.VERSION.SDK_INT;
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    if (sdk > Build.VERSION_CODES.M) {
                                        Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(), OustSdkApplication.getContext().getApplicationContext().getPackageName() + ".provider", OustSdkTools.getDataFromPrivateStorage(courseCardClass.getReadMoreData().getData()));
                                        intent.setDataAndType(fileUri, "application/pdf");
                                    } else {

                                        File file = OustSdkTools.getDataFromPrivateStorage(courseCardClass.getReadMoreData().getData());
                                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                                    }

                                    activity.startActivity(intent);
                                } else {
                                    OustSdkTools.showToast(OustStrings.getString("nopdftext"));
                                }
                            } catch (Exception e) {
                                OustSdkTools.showToast(OustStrings.getString("nopdftext"));
                            }
                        }
                    });
                } else solution_desc.setHtml(courseCardClass.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public abstract class TextViewLinkHandler extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    onLinkClick(link[0].getURL());
                    return false;
                }
            }
            return true;
        }

        abstract public void onLinkClick(String url);
    }

    private void setContentTitle() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty()) && (courseCardClass.getCardTitle() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                solution_headertext.setHtml(courseCardClass.getCardTitle());
//                OustSdkTools.getSpannedContent(courseCardClass.getCardTitle(),solution_headertext);
                solution_headertext.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTopContent() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty()) && (!courseCardClass.getContent().isEmpty())) {
                learningtop_desc.setVisibility(View.VISIBLE);
                learningtop_desc.setHtml(courseCardClass.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTopContentTitle() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                learningtop_title.setHtml(courseCardClass.getCardTitle());
                learningtop_title.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private Spanned getSpannedContent(String content) {
        String s2 = content.trim();
        try {
            while (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        Spanned s1 = Html.fromHtml(s2, null, new OustTagHandler());
        return s1;
    }


    //-----------------------------------------------------------------------------------------------------

    private int imageNO = 0;
    private boolean playAudio = true;

    private void showAllMedia() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                    DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                    if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                        switch (courseCardMedia.getMediaType()) {
                            case "GIF":
                                if (imageNO == 0) {
                                    imageNO++;
                                    setGifImage("oustlearn_" + courseCardMedia.getData());
                                } else if (imageNO == 1) {
                                    setGifImageSecond("oustlearn_" + courseCardMedia.getData());
                                }
                                break;
                            case "IMAGE":
                                if (imageNO == 0) {
                                    imageNO++;
                                    setImage("oustlearn_" + courseCardMedia.getData());
                                    favCardDetails.setImageUrl(courseCardMedia.getData());
                                } else if (imageNO == 1) {
                                    setImageA("oustlearn_" + courseCardMedia.getData());
                                    favCardDetails.setImageUrl(courseCardMedia.getData());
                                }
                                break;
                            case "VIDEO":
                                playAudio = false;
                                fastForwardMedia = courseCardMedia.isFastForwardMedia();
                                favCardDetails.setVideo(true);
                                if (!courseCardClass.isPotraitModeVideo()) {
                                    learningModuleInterface.changeOrientationUnSpecific();
                                } else {
                                    video_landscape_zoom.setVisibility(View.VISIBLE);
                                    video_landscape_zoom.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (!OustStaticVariableHandling.getInstance().isVideoFullScreen()) {
                                                OustStaticVariableHandling.getInstance().setVideoFullScreen(true);
                                                setLandscapeVid(true);
                                                setPotraitVideoRatioFullScreen();
                                            } else {
                                                OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                                                setPotraitVid(true);
                                                setPotraitVideoRatio();
                                            }
                                        }
                                    });
                                }
                                if (courseCardMedia.getMediaPrivacy() != null && courseCardMedia.getMediaPrivacy().equalsIgnoreCase("private")) {
                                    streamStoredVideo(courseCardMedia.getData());
                                } else {
                                    if (OustSdkTools.checkInternetStatus()) {
                                        path = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL+"course/media/video/" + courseCardMedia.getData();
                                        startVedioPlayer(path);
                                    } else {
                                        OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                                    }
                                }
                                if ((courseCardMedia.getMediaThumbnail() != null) && (!courseCardMedia.getMediaThumbnail().isEmpty())) {
                                    setThumbnailImage(courseCardMedia.getMediaThumbnail());
                                    favCardDetails.setImageUrl(courseCardMedia.getMediaThumbnail());
                                    favCardDetails.setVideo(true);
                                } else {
                                    videothumbnail_image.setVisibility(View.GONE);
                                    simpleExoPlayerView.setVisibility(View.VISIBLE);
                                }
                                break;
                            case "YOUTUBE_VIDEO":
                                playAudio = false;
                                learningModuleInterface.changeOrientationUnSpecific();
                                favCardDetails.setVideo(true);
                                youtubeKey = courseCardMedia.getData();
                                if (youtubeKey.contains("https://www.youtube.com/watch?v=")) {
                                    youtubeKey = youtubeKey.replace("https://www.youtube.com/watch?v=", "");
                                }
                                if (youtubeKey.contains("https://youtu.be/")) {
                                    youtubeKey = youtubeKey.replace("https://youtu.be/", "");
                                }
                                if (youtubeKey.contains("&")) {
                                    int position = youtubeKey.indexOf("&");
                                    youtubeKey = youtubeKey.substring(0, position);
                                }
                                showYoutTubeLayout();
                                break;
                            case "AUDIO":
                                playDownloadedAudioQues("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setAudio(true);
                                break;
                        }
                    }
                }
            }
            if ((audioFileName == null) || ((audioFileName != null) && (audioFileName.isEmpty()))) {
                if (playAudio) {
                    speakAudio();
                }
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setThumbnailImage(String imagePath) {
        try {
            try {
                DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videothumbnail_image.getLayoutParams();
                float h = (scrWidth * 0.56f);
                params.height = (int) h;
                videothumbnail_image.setLayoutParams(params);
                videothumbnail_image.setVisibility(View.VISIBLE);
            }catch (Exception e){
                //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
            }
            if ((imagePath != null) && (!imagePath.isEmpty())) {
                Drawable bg_drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.bg_1));
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(imagePath).placeholder(bg_drawable).error(bg_drawable).into(videothumbnail_image);
                } else {
                    Picasso.get().load(imagePath).placeholder(bg_drawable).error(bg_drawable).networkPolicy(NetworkPolicy.OFFLINE).into(videothumbnail_image);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private boolean isCardttsEnabled;

    public void setCardttsEnabled(boolean cardttsEnabled) {
        isCardttsEnabled = cardttsEnabled;
    }

    private void speakAudio() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String speakStr = "";
                    if (courseCardClass != null) {
                        if (courseCardClass.getCardTitle() != null) {
                            Spanned s1 = getSpannedContent(courseCardClass.getCardTitle());
                            speakStr += (s1.toString());
                            speakStr += ". ";
                        }
                        if (courseCardClass.getContent() != null) {
                            Spanned s1 = getSpannedContent(courseCardClass.getContent());
                            speakStr += (s1.toString());
                        }
                        if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isCardttsEnabled) {
                            optionmain_speaker.setVisibility(View.VISIBLE);
//                            if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                            optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                            speakString(speakStr);
//                            } else {
//                                optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
//                                if (OustSdkTools.textToSpeech != null) {
//                                    OustSdkTools.stopSpeech();
//                                }
//                                optionmain_speaker.setAnimation(null);
//                            }
                        } else {
                            optionmain_speaker.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }, 1200);
    }


    public void setGifImage(String fileName) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                GifDrawable gifFromBytes = new GifDrawable(imageByte);
//                solutionMain_imagea.setImageDrawable(gifFromBytes);
//                learningcard_tuta.setVisibility(View.VISIBLE);
//            }
            File file =new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()){
                Picasso.get().load(file).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        solutionMain_imagea.setImageBitmap(bitmap);
                        learningcard_tuta.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setImage(String fileName) {
        try {
            containImage = true;
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                solutionMain_imagea.setImageBitmap(decodedByte);
//                learningcard_tuta.setVisibility(View.VISIBLE);
//            }
            File file =new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()){
                Picasso.get().load(file).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        solutionMain_imagea.setImageBitmap(bitmap);
                        learningcard_tuta.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setGifImageSecond(String fileName) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                GifDrawable gifFromBytes = new GifDrawable(imageByte);
//                solutionMain_imageb.setImageDrawable(gifFromBytes);
//                learningcard_tutb.setVisibility(View.VISIBLE);
//            }
            File file =new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()){
                Picasso.get().load(file).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        solutionMain_imageb.setImageBitmap(bitmap);
                        learningcard_tutb.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setImageA(String fileName) {
        try {
            containImage = true;
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                solutionMain_imageb.setImageBitmap(decodedByte);
//                learningcard_tutb.setVisibility(View.VISIBLE);
//            }
            File file =new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()){
                Picasso.get().load(file).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        solutionMain_imageb.setImageBitmap(bitmap);
                        learningcard_tutb.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //---------------------------------------------------------------------------------------------------

    private void resetAllData() {
        myHandler = null;
        courseCardClass = null;
        mediaPlayer = null;
        OustSdkTools.isReadMoreFragmentVisible = false;
    }

    private String audioFileName;

    private void playDownloadedAudioQues(final String filename) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    audioFileName = filename;
                    mediaPlayer = new MediaPlayer();
                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                    String audStr = enternalPrivateStorage.readSavedData(filename);
                    if ((audStr != null) && (!audStr.isEmpty())) {
                        byte[] audBytes = Base64.decode(audStr, 0);
                        // create temp file that will hold byte array
                        File tempMp3 = File.createTempFile(filename, null, activity.getCacheDir());
                        tempMp3.deleteOnExit();
                        FileOutputStream fos = new FileOutputStream(tempMp3);
                        fos.write(audBytes);
                        fos.close();
                        optionmain_speaker.setVisibility(View.VISIBLE);

                        // resetting mediaplayer instance to evade problems
                        mediaPlayer.reset();

                        FileInputStream fis = new FileInputStream(tempMp3);
                        mediaPlayer.setDataSource(fis.getFD());
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        scaleanim = AnimationUtils.loadAnimation(activity, R.anim.learning_audioscaleanim);
                        optionmain_speaker.startAnimation(scaleanim);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                musicComplete = true;
                                optionmain_speaker.setAnimation(null);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                }
            }
        });

    }

    private boolean musicComplete = false;
    private Animation scaleanim;

    private void restartAudio() {

        musicComplete = false;
        myHandler = new Handler();
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer = new MediaPlayer();
                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                    String audStr = enternalPrivateStorage.readSavedData(audioFileName);
                    if ((audStr != null) && (!audStr.isEmpty())) {
                        byte[] audBytes = Base64.decode(audStr, 0);
                        // create temp file that will hold byte array
                        File tempMp3 = File.createTempFile(audioFileName, null, activity.getCacheDir());
                        tempMp3.deleteOnExit();
                        FileOutputStream fos = new FileOutputStream(tempMp3);
                        fos.write(audBytes);
                        fos.close();
                        optionmain_speaker.setVisibility(View.VISIBLE);

                        // resetting mediaplayer instance to evade problems
                        mediaPlayer.reset();

                        FileInputStream fis = new FileInputStream(tempMp3);
                        mediaPlayer.setDataSource(fis.getFD());
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

//                myHandler.postDelayed(UpdateSongTime, 100);
                        scaleanim = AnimationUtils.loadAnimation(activity, R.anim.learning_audioscaleanim);
                        optionmain_speaker.startAnimation(scaleanim);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                musicComplete = true;
                                optionmain_speaker.setAnimation(null);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                }
            }
        });

    }

    //-----------------------------------------------------------------------------------------------------


    private void detectClickedView(View v) {
        int id = v.getId();
        if (id == R.id.optionmain_speaker) {
            if ((audioFileName == null) || ((audioFileName != null) && (audioFileName.isEmpty()))) {
                try {
                    String speakStr = "";
                    if (courseCardClass.getCardTitle() != null) {
                        Spanned s1 = getSpannedContent(courseCardClass.getCardTitle());
                        speakStr += (s1.toString());
                        speakStr += ". ";
                    }
                    if (courseCardClass.getContent() != null) {
                        Spanned s1 = getSpannedContent(courseCardClass.getContent());
                        speakStr += (s1.toString());
                    }
                    if (isAudioPlaying) {
                        isAudioPlaying=false;
                        optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
                            optionmain_speaker.setAnimation(null);
                        }
                    } else {
                        isAudioPlaying=true;
                        optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                        speakString(speakStr);
                    }
                } catch (Exception e) {
                }
            } else {
                if (musicComplete) {
                    OustSdkTools.oustTouchEffect(v, 50);
                    if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                        mediaPlayer.stop();
                    }
                    restartAudio();
                } else {
                    if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                        mediaPlayer.pause();
                        optionmain_speaker.setAnimation(null);
                        optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                    } else if ((mediaPlayer != null)) {
                        mediaPlayer.start();
                        try {
                            optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                            scaleanim = AnimationUtils.loadAnimation(activity, R.anim.learning_audioscaleanim);
                            optionmain_speaker.startAnimation(scaleanim);
                        } catch (Exception e) {
                        }
                    }
                }
            }

        }
    }

    private boolean isAudioPlaying=true;

    //---------------------------------
    private void showAllMediaFullImage() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                    DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                    if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                        switch (courseCardMedia.getMediaType()) {
                            case "GIF":
                                setGifFullImage("oustlearn_" + courseCardMedia.getData());
                                break;
                            case "IMAGE":
                                setFullImage("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setImageUrl(courseCardMedia.getData());
                                break;
                            case "AUDIO":
                                playDownloadedAudioQues("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setAudio(true);
                                break;
                        }
                    }
                }
                if ((audioFileName == null) || ((audioFileName != null) && (audioFileName.isEmpty()))) {
                    speakAudio();
                }
            }
            if ((audioFileName == null) || ((audioFileName != null) && (audioFileName.isEmpty()))) {
                speakAudio();
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setGifFullImage(String fileName) {
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String audStr = enternalPrivateStorage.readSavedData(fileName);
            if ((audStr != null) && (!audStr.isEmpty())) {
                byte[] imageByte = Base64.decode(audStr, 0);
                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                learning_fullimgage.setImageDrawable(gifFromBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setFullImage(String fileName) {
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String audStr = enternalPrivateStorage.readSavedData(fileName);
            if ((audStr != null) && (!audStr.isEmpty())) {
                byte[] imageByte = Base64.decode(audStr, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                learning_fullimgage.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void showAllMediaHalfImage() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                    DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                    if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                        switch (courseCardMedia.getMediaType()) {
                            case "GIF":
                                setGifHalfImage("oustlearn_" + courseCardMedia.getData());
                                break;
                            case "IMAGE":
                                setHalfImage("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setImageUrl(courseCardMedia.getData());
                                break;
                            case "AUDIO":
                                playDownloadedAudioQues("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setAudio(true);
                                break;
                        }
                    }
                }
                if ((audioFileName == null) || ((audioFileName != null) && (audioFileName.isEmpty()))) {
                    speakAudio();
                }
            }
            if ((audioFileName == null) || ((audioFileName != null) && (audioFileName.isEmpty()))) {
                speakAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setGifHalfImage(String fileName) {
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String audStr = enternalPrivateStorage.readSavedData(fileName);
            if ((audStr != null) && (!audStr.isEmpty())) {
                byte[] imageByte = Base64.decode(audStr, 0);
                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                learning_halfimgage.setImageDrawable(gifFromBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setHalfImage(String fileName) {
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String audStr = enternalPrivateStorage.readSavedData(fileName);
            if ((audStr != null) && (!audStr.isEmpty())) {
                byte[] imageByte = Base64.decode(audStr, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                learning_halfimgage.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void speakString(String str) {
        try {
            str = str.replaceAll("[_]+", "    ");
            int n1 = str.length();
            if (n1 > 100)
                n1 = 100;
            boolean isHindi = false;
            for (int i = 0; i < n1; i++) {
                int no = str.charAt(i);
                if (no > 2300 && n1 < 3000) {
                    isHindi = true;
                    break;
                }
            }
            TextToSpeech textToSpeech = OustSdkTools.getSpeechEngin();
            if (isHindi) {
                textToSpeech = OustSdkTools.getHindiSpeechEngin();
            }
            if (textToSpeech != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            float count = str.length() / 20;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(optionmain_speaker, "scaleX", 1, 0.75f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(optionmain_speaker, "scaleY", 1, 0.75f);
            scaleDownX.setDuration(1000);
            scaleDownY.setDuration(1000);
            scaleDownX.setRepeatCount((int) count);
            scaleDownY.setRepeatCount((int) count);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //============================================================================================================
    //video related methode
    public YouTubePlayer videoPlayer;
    private String youtubeKey = "";
    //private YouTubePlayerSupportFragment youTubePlayerFragment;

    private void showYoutTubeLayout() {
        try {
            learningtut_videolayout.setVisibility(View.VISIBLE);
            Drawable default_img = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.image));
            Picasso.get().load("http://img.youtube.com/vi/" + youtubeKey + "/default.jpg").placeholder(default_img).error(default_img).into(youtubemainImage);
            favCardDetails.setImageUrl("http://img.youtube.com/vi/" + youtubeKey + "/default.jpg");
            favCardDetails.setVideo(true);
            learnngtut_mainimagelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isPackageInstalled = true;
//                    OustSdkTools.showToast("");
//                    try {
//                        PackageInfo pinf=getActivity().getPackageManager().getPackageInfo("com.google.android.youtube", 0);
//                        String versionName = pinf.versionName;
//                        versionName=versionName.substring(0,2);
//                        int versionNo=Integer.parseInt(versionName);
//                        if(versionNo<11){
//                            confirminstallYoutubePopup(true);
//                        }else {
//                            isPackageInstalled = true;
//                        }
//                    }catch (Exception e){
//                        confirminstallYoutubePopup(false);
//                    }
//                    if(isPackageInstalled) {
//                        OustSdkTools.stopSpeech();
//                        learnngtut_mainimagelayout.setVisibility(View.GONE);
//                        learningtut_videoframelayout.setVisibility(View.VISIBLE);
//                        Handler mHandler=new Handler();
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                initYouTubeView();
//                            }
//                        });
//                    }
                }
            });
            //if audio is running stop and hide
            if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                mediaPlayer.stop();
            }
            optionmain_speaker.setVisibility(View.GONE);
            audioFileName = null;
            optionmain_speaker.setAnimation(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void confirminstallYoutubePopup(boolean isUpdate) {
        try {
            View popUpView = getActivity().getLayoutInflater().inflate(R.layout.ask_infopopup, null);
            final PopupWindow installYoutubePopup = OustSdkTools.createPopUp(popUpView);
            final LinearLayout infopopup_animlayout = popUpView.findViewById(R.id.infopopup_animlayout);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            if (isUpdate) {
                popupContent.setText(OustStrings.getString("update_youtube"));
            } else {
                popupContent.setText(OustStrings.getString("install_youtube"));
            }
            btnYes.setText(OustStrings.getString("yes"));
            btnNo.setText(OustStrings.getString("no"));

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installYoutubePopup.dismiss();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.google.android.youtube")));
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installYoutubePopup.dismiss();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installYoutubePopup.dismiss();
                }
            });
            OustSdkTools.popupAppearEffect(infopopup_animlayout);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean fullScreen = false, isYoutubeInitialized = false;

    /*private void initYouTubeView() {
        try {
            if (youTubePlayerFragment == null) {
                isYoutubeInitialized = true;
                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                transaction.replace(R.id.learningtut_videoframelayout, youTubePlayerFragment).commit();
                youTubePlayerFragment.initialize(youtubeKey, new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                        if (!wasRestored) {
                            videoPlayer = player;
                            videoPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                            videoPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);

                            videoPlayer.loadVideo(youtubeKey);
                            videoPlayer.play();
                            videoPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                                @Override
                                public void onFullscreen(boolean _isFullScreen) {
                                    fullScreen = _isFullScreen;
                                    if (fullScreen) {
                                        setLandscapeVid(false);
                                    } else {
                                        setPotraitVid(false);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                        // YouTube error
                        String errorMessage = error.toString();
                        Log.d("errorMessage:", errorMessage);
                    }
                });
            } else {
                youTubePlayerFragment.onResume();
            }
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        if (fullScreen) {
                            videoPlayer.setFullscreen(false);
                            return true;
                        }
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }*/

    private void setLandscapeVid(boolean isConfigChange) {
        try {
            if (!courseCardClass.isPotraitModeVideo()) {
                if (!isConfigChange) {
                    learningModuleInterface.changeOrientationLandscape();
                    //learningModuleInterface.changeOrientationUnSpecific();
                }
            }
            moduleoverview_toplayout.setVisibility(View.GONE);
            solution_headertext.setVisibility(View.GONE);
            solution_desc.setVisibility(View.GONE);
            learningcard_tuta.setVisibility(View.GONE);
            readmore_bottom_layout.setVisibility(View.GONE);
            readmore_header.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                mediaPlayer.stop();
            }
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                optionmain_speaker.setAnimation(null);
            }
            /*if (youTubePlayerFragment != null) {
                youTubePlayerFragment.onDestroy();
            }*/
            /*if (videoPlayer != null) {
                videoPlayer.setPlaybackEventListener(null);
                videoPlayer.release();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void setPotraitVid(boolean isConfigChange) {
        try {
            if (!isConfigChange) {
                learningModuleInterface.changeOrientationPortrait();
                //learningModuleInterface.changeOrientationUnSpecific();
            }
            readmore_header.setVisibility(View.VISIBLE);
//            moduleoverview_toplayout.setVisibility(View.VISIBLE);
            if ((courseCardClass != null) && (courseCardClass.getCardTitle() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                solution_headertext.setVisibility(View.VISIBLE);
            }
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty())) {
                solution_desc.setVisibility(View.VISIBLE);
            }
            if (containImage) {
                learningcard_tuta.setVisibility(View.VISIBLE);
            }
            readmore_bottom_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }


    }

    //------------------------------------------------------------------------------------------------------
    //s3 video stream

    private String videoFileName;

    private void streamStoredVideo(final String videoFileName1) {
        try {
            quesvideoLayout.setVisibility(View.VISIBLE);
            this.videoFileName = videoFileName1;
            start_videobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkVideoMediaExist();
                }
            });
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String path = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/" + videoFileName;
                File file = new File(path);
                if (!file.exists()) {
                    if (isVideoDownloading(videoFileName)) {
                        downloadvideo_icon.setVisibility(View.VISIBLE);
                        File gif_file = OustSdkTools.getImageFile(OustSdkApplication.getContext().getResources().getString(R.string.white_to_green_new));
                        if (gif_file != null) {
                            GifDrawable gifFromResource = new GifDrawable(gif_file);
                            downloadvideo_icon.setImageDrawable(gifFromResource);
                        }
                    } else {
                        setDownloadBtn();
                    }
                } else {
                    OustPreferences.clear(videoFileName);
                    downloadvideo_icon.setVisibility(View.VISIBLE);
                    if (newlp_downloaded_drawable != null) {
                        downloadvideo_icon.setImageDrawable(newlp_downloaded_drawable);
                    }
                }
            } else {
                setDownloadBtn();
            }
            //if audio is running stop and hide
            if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                mediaPlayer.stop();
            }
            optionmain_speaker.setVisibility(View.GONE);
            audioFileName = null;
            optionmain_speaker.setAnimation(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setDownloadBtn() {
        downloadvideo_icon.setVisibility(View.VISIBLE);
        downloadvideo_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startDownloadingVideo(true);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                }
            }
        });
    }

    private void startDownloadingVideo(boolean showNoInternetMessage) {
        if (OustSdkTools.checkInternetStatus()) {
            if (!isVideoDownloding) {
                isVideoDownloding = true;
                downloadVideo();
            }
        } else {
            if (showNoInternetMessage) {
                OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
            }
        }
    }

    private boolean isVideoDownloding = false;

    private void checkVideoMediaExist() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                path = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/" + videoFileName;
                File file = new File(path);
                if (!file.exists()) {
                    startPlayingSignedUrlVideo();
                } else {
                    start_videobutton.setVisibility(View.GONE);
                    startVedioPlayer(path);
                }
            } else {
                startPlayingSignedUrlVideo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startPlayingSignedUrlVideo() {
        if (OustSdkTools.checkInternetStatus()) {
            path = getSignedUrl(("course/media/video/" + videoFileName));
            startVedioPlayer(path);
            start_videobutton.setVisibility(View.GONE);
        } else {
            OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
        }
    }

    private boolean fastForwardMedia;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (simpleExoPlayerView != null || simpleExoPlayer != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (isWebViewOpen) {
                    readmore_header.setVisibility(View.GONE);
                    readmore_bottom_layout.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) main_readmore_layout.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    main_readmore_layout.setLayoutParams(params);
                } else {
                    setLandscapeVid(true);
                    setLandscapeVideoRation();
                }
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (isWebViewOpen) {
                    readmore_header.setVisibility(View.VISIBLE);
                    readmore_bottom_layout.setVisibility(View.VISIBLE);
//                    RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) main_readmore_layout.getLayoutParams();
//                    int size=(int)OustSdkApplication.getContext().getResources().getDimension(R.dimen.oustlayout_dimen10);
//                    params.setMargins(size,size,size,size);
//                    main_readmore_layout.setLayoutParams(params);
                } else {

                }
            }
        } else
            super.onConfigurationChanged(newConfig);
    }

    private SimpleExoPlayer simpleExoPlayer;
    long time = 0;
    String path = null;

    private void startVedioPlayer(String path) {
//        path="https://s3-us-west-1.amazonaws.com/img.oustme.com/course/media/video/1BZ7tLjAumhzxXB.mkv";
        setPotraitVideoRatio();
        videothumbnail_image.setVisibility(View.GONE);
        simpleExoPlayerView.setVisibility(View.VISIBLE);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        //simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        simpleExoPlayer = new SimpleExoPlayer.Builder(getActivity()).build();
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource;
        Uri videoUri = null;
//        Uri videoUri = Uri.parse("https://s3-us-west-1.amazonaws.com/img.oustme.com/course/media/video/1BZ7tLjAumhzxXB.mkv");
//        Uri videoUri=Uri.fromFile(new File(path));
        //Uri videoUri=Uri.parse(path);
        File file = new File(path);
        if (file.exists()) {
            videoUri = Uri.fromFile(file);
            Log.e("Player", "" + videoUri);
            DataSpec dataSpec = new DataSpec(videoUri);
            final FileDataSource fileDataSource = new FileDataSource();
            try {
                fileDataSource.open(dataSpec);
            } catch (FileDataSource.FileDataSourceException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            DataSource.Factory factory = new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    return fileDataSource;
                }
            };
            //videoSource = new ExtractorMediaSource(fileDataSource.getUri(), factory, extractorsFactory, null, null);
            videoSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(fileDataSource.getUri()));
        } else {
            videoUri = Uri.parse(path);
            DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory("readmore_exo_player");
            //videoSource = new ExtractorMediaSource(videoUri, defaultHttpDataSourceFactory, extractorsFactory, null, null);
            videoSource = new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
        }
        simpleExoPlayer.seekTo(time);
        simpleExoPlayerView.setPlayer(simpleExoPlayer);
        simpleExoPlayer.setMediaSource(videoSource);
        simpleExoPlayer.prepare();
        ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.e("-------", "onTracksChanged");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.e("-------", "onLoadingChanged----" + isLoading);
                if (isLoading) {
                    Animation rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
                    learningcard_videoloader.startAnimation(rotateAnim);
                    learningcard_videoloader.setVisibility(View.VISIBLE);
                } else {
                    learningcard_videoloader.clearAnimation();
                    learningcard_videoloader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e("-------", "onPlayerStateChanged-----" + playWhenReady);
                if (learningcard_videoloader.getVisibility() == View.VISIBLE) {
                    learningcard_videoloader.clearAnimation();
                    learningcard_videoloader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("-------", "onPlayerError");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }


            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.e("-------", "onPlaybackParametersChanged");
            }

            @Override
            public void onSeekProcessed() {

            }
        };
        simpleExoPlayer.addListener(eventListener);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    public void setPotraitVideoRatio() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrWidth = metrics.widthPixels;
        int scrHeight = metrics.heightPixels;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
        float h = (scrWidth * 0.70f);
        params.width = scrWidth;
        params.height = (int) h;
        simpleExoPlayerView.setLayoutParams(params);
    }

    public void setPotraitVideoRatioFullScreen() {
        if (simpleExoPlayerView != null) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            float h = metrics.heightPixels;
            params.width = main_readmore_layout.getWidth();
            params.height = main_readmore_layout.getHeight() -((int)(getResources().getDimension(R.dimen.oustlayout_dimen10)));
            simpleExoPlayerView.setLayoutParams(params);
        }
    }

    private void setLandscapeVideoRation() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrWidth = metrics.widthPixels;
        int scrHeight = metrics.heightPixels;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
        //float h = (scrHeight * 0.f);
        int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen25);
//        if(courseCardClass.isPotraitModeVideo()){
//            params.height = (scrHeight - size);
//        }else {
//        params.height = (scrWidth - size);
//        }
        params.height = scrHeight - size;
        params.width = scrWidth;
        simpleExoPlayerView.setLayoutParams(params);
        simpleExoPlayerView.setPadding(0, 15, 0, 0);
        if (simpleExoPlayerView.getVisibility() == View.GONE) {
            videothumbnail_image.setLayoutParams(params);
        }
    }

    private Runnable UpdateVideoTime = new Runnable() {
        public void run() {
            if (currentTime > 0) {
                hideLoader();
            }
//            finalTime = video_player_view.getDuration();
//            currentTime = video_player_view.getCurrentPosition();
//            video_progressbar.setMax(finalTime);
//            video_progressbar.setProgress((int)currentTime);
//            myHandler.postDelayed(this, 100);
        }
    };

    public void hideLoader() {
        try {
            simpleExoPlayerView.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
            learningcard_videoloader.setVisibility(View.GONE);
            simpleExoPlayerView.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getSignedUrl(String objectKey) {
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
        return url.toString();
    }

    public void downloadVideo() {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                return;
            }
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                    setDownloadingPercentage(Integer.parseInt(progress));
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    // OustSdkTools.showToast(message);
                    tempVideoFileName.delete();
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if(code==_COMPLETED)
                    {
                        String path=Environment.getExternalStorageDirectory()+"/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/"+videoFileName;
                        final File finalFile = new File(path);
                        tempVideoFileName.renameTo(finalFile);
                    }
                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });
            File gif_file = OustSdkTools.getImageFile(OustSdkApplication.getContext().getResources().getString(R.string.white_to_green_new));
            if (gif_file != null) {
                GifDrawable gifFromResource = new GifDrawable(gif_file);
                downloadvideo_icon.setImageDrawable(gifFromResource);
            }

            //String StoragePath= AppConstants.StringConstants.LOCAL_FILE_DOWNLOAD_PATH;
            String StoragePath = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files";
            tempVideoFileName = new File(path);
            String downLoadPath = AppConstants.StringConstants.CLOUD_FRONT_VIDEO_BASE+videoFileName;
            downloadFiles.startDownLoad(downLoadPath, StoragePath, videoFileName,false, false);
            // downloadFiles.startDownLoad(path, S3_BUCKET_NAME, "course/media/video/" + videoFileName, false);
           /* Intent intent = new Intent(getActivity(), DownloadVideoService.class);
            intent.putExtra("videoFileName", videoFileName);
            getActivity().startService(intent);*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private MyReadmoreVideoDownloadReceiver receiver;

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MyReadmoreVideoDownloadReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyReadmoreVideoDownloadReceiver();
        getActivity().registerReceiver(receiver, filter);
        if (path != null && (!path.isEmpty())) {
            startVedioPlayer(path);
        }
    }


    public class MyReadmoreVideoDownloadReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "patil";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setDownloadingPercentage();
            } catch (Exception e) {
                //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
            }
        }
    }

    private void setDownloadingPercentage(int percentage) {
        try {
            if (percentage > 0) {
                if (percentage == 100) {
                    //OustPreferences.clear(videoFileName);
                    downloadvideo_text.setText("");
//                    if(currentTime>0) {
//                        checkVideoMediaExist();
//                    }
                    downloadvideo_icon.setImageDrawable(newlp_downloaded_drawable);
                } else {
                    downloadvideo_text.setText("" + (percentage) + "%");
                }
            }
        } catch (Exception e) {
        }
    }
    private void setDownloadingPercentage() {
        try {
            int percentage = OustPreferences.getSavedInt(videoFileName);
            if (percentage > 0) {
                if (percentage == 100) {
                    OustPreferences.clear(videoFileName);
                    downloadvideo_text.setText("");
//                    if(currentTime>0) {
//                        checkVideoMediaExist();
//                    }
                    downloadvideo_icon.setImageDrawable(newlp_downloaded_drawable);
                } else {
                    downloadvideo_text.setText("" + (percentage) + "%");
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean isVideoDownloading(String videoFileName) {
        int percentage = OustPreferences.getSavedInt(videoFileName);
        return percentage > 0;
    }

}