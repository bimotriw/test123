package com.oustme.oustsdk.question_module.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.SIX_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.customviews.CustomTouchIndicatorClass;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.HotspotPointData;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOHotspotPointData;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HotSpotQuestionFragment extends Fragment {

    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView question_count_num;
    RelativeLayout video_lay;
    RelativeLayout media_question_container;
    TextView question;
    WebView question_description_webView;
    TextView info_type;
    CardView question_card;
    TextView option_text;
    ImageView hot_spot_image;
    CustomTouchIndicatorClass hot_spot_touch;
    NestedScrollView hotSpotScrollView;

    //Right And Wrong questions thump and coins
    ImageView question_result_image;
    RelativeLayout animMainLayout;
    LinearLayout coinsAnimLayout;
    ImageView coinsAnimImg;
    TextView coinsAnimText;
    RelativeLayout hotspot_label_layout;


    //Data
    Context context;
    int color;
    LearningModuleInterface learningModuleInterface;
    CourseContentHandlingInterface courseContentHandlingInterface;
    AssessmentContentHandlingInterface assessmentContentHandler;
    DTOCourseCard mainCourseCardClass;
    CourseLevelClass courseLevelClass;
    int questionNo;
    CourseDataClass courseDataClass;
    String courseId;
    String courseName;
    boolean zeroXpForQCard;
    boolean isQuestionImageShown;
    String backgroundImage;
    List<FavCardDetails> favouriteCardList;
    boolean isQuestionTTSEnabled;
    boolean isRMFavourite;
    boolean isCourseCompleted;
    boolean isReviewMode;
    boolean isExamMode;
    boolean isCourseQuestion;
    boolean isAssessmentQuestion;
    boolean isVideoOverlay;
    String solutionText;
    String cardId;
    long questionXp;
    boolean isRandomizeQuestion;
    DTOQuestions questions;
    DTOCourseSolutionCard courseSolutionCard;
    boolean showSolutionAnswer = true;
    boolean showSolution = false;
    boolean containSubjective = false;


    //Related to hotspot
    String attemptsLeftString;
    int attemptLeft = 5;
    int currentPageNo = 0;
    List<DTOHotspotPointData> hotspotPointDataList = new ArrayList<>();
    boolean[] touchPoint;
    Bitmap hotspotImage;
    float bitmapActualW, bitmapActualH;
    int scrWidth, scrHeight;
    float bitmapW, bitmapH;
    boolean isThirdFourthHotSpotImage = false;
    int hotSpotImageWidth, hotSpotImageHeight;
    int actualImageWidth, actualImageHeight;
    float deviceXFactor, deviceYFactor;
    boolean shouldDisplayRightWrong = false;
    float x11, x21;
    float y11, y21;
    final int MIN_DISTANCE = 50;
    boolean hotSpotComplete = false;
    boolean isReplayMode = false;
    boolean isLevelCompleted = false;
    int noOfTimesClicked;
    boolean isHotSpotThumbsUpShown = true;
    boolean isHotSpotThumbsDownShown = true;
    private boolean popupShownOnce = false;
    Bitmap hotspotimage;

    //related to score calculation
    int finalScr = 0;
    boolean isHotSpotAnswerSelectedRight;
    private int hotSpotWrongCount = 0;
    private int hotSpotRightCount = 0;
    private float hotspotMarks = 0;
    boolean showNavigateArrow;
    boolean isSurveyQuestion, proceedOnWrong;
    private Scores score;
    private int cardCount;
    private float ratioW = 0, ratioH = 0;
    int height_toolbar, width_toolbar, height_bottom_bar;
    private LayoutInflater mInflater;

    private MediaPlayer answerChosenPlayer;


    public HotSpotQuestionFragment() {
        // Required empty public constructor
    }

    public static HotSpotQuestionFragment newInstance() {
        return new HotSpotQuestionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hot_spot_question, container, false);

        question_base_frame = view.findViewById(R.id.question_base_frame);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        main_layout = view.findViewById(R.id.main_layout);
        question_count_num = view.findViewById(R.id.question_count_num);
        video_lay = view.findViewById(R.id.video_lay);
        media_question_container = view.findViewById(R.id.media_question_container);
        question = view.findViewById(R.id.question);
        question_description_webView = view.findViewById(R.id.description_webView);
        info_type = view.findViewById(R.id.info_type);
        question_card = view.findViewById(R.id.question_card_hotspot);
        option_text = view.findViewById(R.id.option_text);
        hot_spot_image = view.findViewById(R.id.hot_spot_image);
        hot_spot_touch = view.findViewById(R.id.hot_spot_touch);
        hotSpotScrollView = view.findViewById(R.id.hotspot_scroll_view);

        question_result_image = view.findViewById(R.id.question_result_image);
        animMainLayout = view.findViewById(R.id.thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);
        hotspot_label_layout = view.findViewById(R.id.hotspot_label_layout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        getWidth();
        setQuestionData(questions);
    }

    //course module
    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public void setCourseData(CourseDataClass courseDataClass) {
        try {
            this.courseDataClass = courseDataClass;
            this.courseId = "" + courseDataClass.getCourseId();
            this.courseName = "" + courseDataClass.getCourseName();
            this.zeroXpForQCard = courseDataClass.isZeroXpForQCard();
            this.isQuestionImageShown = courseDataClass.isShowQuestionSymbolForQuestion();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setBgImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setFavouriteCardList(List<FavCardDetails> favouriteCardList) {
        this.favouriteCardList = favouriteCardList;
        if (this.favouriteCardList == null) {
            this.favouriteCardList = new ArrayList<>();
        }
    }

    public void setQuestionTTSEnabled(boolean isQuestionTTSEnabled) {
        this.isQuestionTTSEnabled = isQuestionTTSEnabled;
    }

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }

    public void setCourseCompleted(boolean isCourseCompleted) {
        this.isCourseCompleted = isCourseCompleted;
    }

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    public void setExamMode(boolean isExamMode) {
        this.isExamMode = isExamMode;
    }

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;
    }

    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setAssessmentContentHandler(AssessmentContentHandlingInterface assessmentContentHandler) {
        this.assessmentContentHandler = assessmentContentHandler;
    }

    public void setVideoOverlay(boolean isVideoOverlay) {
        this.isVideoOverlay = isVideoOverlay;
    }

    public boolean isVideoOverlay() {
        return isVideoOverlay;
    }

    //All modules
    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setMainCourseCardClass(DTOCourseCard courseCardClass) {
        try {
            int savedCardID = (int) courseCardClass.getCardId();
            cardId = "" + savedCardID;
            this.questionXp = courseCardClass.getXp();
            courseSolutionCard = courseCardClass.getChildCard();
            this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);

            if (mainCourseCardClass != null && mainCourseCardClass.getXp() == 0) {
                mainCourseCardClass.setXp(100);
            }
            try {
                if (mainCourseCardClass == null) {
                    mainCourseCardClass = courseCardClass;
                }
                if (courseCardClass.getChildCard() != null) {
                    solutionText = courseCardClass.getChildCard().getContent();
                }

                if (courseCardClass.getQuestionData() != null) {
                    isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
                    showSolution = courseCardClass.getQuestionData().isShowSolution();
                    containSubjective = courseCardClass.getQuestionData().isContainSubjective();
                    if (courseCardClass.getQuestionData().getSolution() != null && !courseCardClass.getQuestionData().getSolution().isEmpty()) {
                        solutionText = courseCardClass.getQuestionData().getSolution();
                    }
                }

                if (isRandomizeQuestion) {
                    if (isCourseQuestion) {
                        CourseQuestionHandling.randomizeOption(mainCourseCardClass);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            mainCourseCardClass = courseCardClass;
        }
        questions = mainCourseCardClass.getQuestionData();
        if (questions == null) {
            if (learningModuleInterface != null) {
                learningModuleInterface.endActivity();
            }
        }

        if (isCourseQuestion) {
            if (mainCourseCardClass.getBgImg() != null && !mainCourseCardClass.getBgImg().isEmpty()) {
                questions.setBgImg(mainCourseCardClass.getBgImg());
            }
        }
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    private void setQuestionData(DTOQuestions questions) {
        try {
            if (questions != null) {
                isThirdFourthHotSpotImage = !questions.isFullScreenHotSpot();
                isHotSpotThumbsDownShown = questions.isThumbsDown();
                isHotSpotThumbsUpShown = questions.isThumbsUp();
                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.setQuestions(questions);
                    if (isReviewMode) {
                        courseContentHandlingInterface.cancelTimer();
                    }
                }

                if (assessmentContentHandler != null) {
                    if (isReviewMode) {
                        assessmentContentHandler.cancelTimerForReview();
                        assessmentContentHandler.showBottomBar();
                    }
                }
                if (mInflater == null) {
                    mInflater = (LayoutInflater) requireActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                }

                if (questions.getQuestion() != null && !questions.getQuestion().isEmpty()) {
                    if (questions.getQuestion().contains("<li>") || questions.getQuestion().contains("</li>") ||
                            questions.getQuestion().contains("<ol>") || questions.getQuestion().contains("</ol>") ||
                            questions.getQuestion().contains("<p>") || questions.getQuestion().contains("</p>")) {
                        question_description_webView.setVisibility(View.VISIBLE);
                        question.setVisibility(View.GONE);
                        question_description_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = OustSdkTools.getDescriptionHtmlFormat(questions.getQuestion());
                        final WebSettings webSettings = question_description_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(18);
                        question_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        question.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            question.setText(Html.fromHtml(questions.getQuestion(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            question.setText(Html.fromHtml(questions.getQuestion()));
                        }
                    }
                } else {
                    question.setVisibility(View.GONE);
                    question_description_webView.setVisibility(View.GONE);
                }


                if ((questions.getBgImg() != null) && (!questions.getBgImg().isEmpty())) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(questions.getBgImg())
                                .apply(new RequestOptions().override(720, 1280))
                                .into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else if (backgroundImage != null && !backgroundImage.isEmpty()) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage)
                                .apply(new RequestOptions().override(720, 1280))
                                .into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                if (questions.getHotspotDataList() != null) {
                    hotspotPointDataList = questions.getHotspotDataList();
                    touchPoint = new boolean[hotspotPointDataList.size()];
                    hot_spot_touch.setList(hotspotPointDataList, touchPoint);
                    if (isReviewMode) {
                        hot_spot_touch.setThumbShow(true, isHotSpotThumbsDownShown);
                    } else {
                        hot_spot_touch.setThumbShow(isHotSpotThumbsUpShown, isHotSpotThumbsDownShown);
                    }
                    currentPageNo = 0;
                    downloadHotspotImage();
                }
                if (isThirdFourthHotSpotImage) {
                    question_card.setVisibility(View.VISIBLE);
                    info_type.setTextColor(Objects.requireNonNull(requireActivity()).getResources().getColor(R.color.error_incorrect));
                    if (!isReviewMode) {
                        info_type.setVisibility(View.VISIBLE);
                        option_text.setVisibility(View.VISIBLE);
                        setAttemptLeft();
                        showHotSpotOptionAnim();
                    }
                } else {
                    info_type.setVisibility(View.GONE);
                    option_text.setVisibility(View.GONE);
                    question_card.setVisibility(View.GONE);
                }

                if (!isReviewMode) {
                    showHotSpotLabelAnim();
                    touchListenerForHotSpot();
                } else {
                    setSpots();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setSpots() {
        ratioW = bitmapW / bitmapActualW;
        ratioH = bitmapH / bitmapActualH;
        for (int i = 0; i < hotspotPointDataList.size(); i++) {
            touchPoint[i] = true;
            float w = ((hotspotPointDataList.get(i).getStartX() + (hotspotPointDataList.get(i).getWidth() / 2)));
            float h = ((hotspotPointDataList.get(i).getStartY() + (hotspotPointDataList.get(i).getHeight() / 2)));
            DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
            hotspotPointData.setStartX(((int) (w * ratioW)));
            hotspotPointData.setStartY(((int) (h * ratioH)));
            hotspotPointData.setWidth(80);
            hot_spot_touch.rightPoint(hotspotPointData, i, touchPoint);
            addLabel(((int) (w * ratioW)), ((int) (h * ratioH)), hotspotPointDataList.get(i).getHpLabel());
        }
    }

    private void addLabel(int x, int y, String label) {
        View hotspotlabelview = mInflater.inflate(R.layout.hotspot_label, null);
        TextView textView = (TextView) hotspotlabelview.findViewById(R.id.hotspot_labeltext);
        KatexView katexViewMaths = hotspotlabelview.findViewById(R.id.hotspot_labeltextMaths);
        if (label.contains(KATEX_DELIMITER)) {
            katexViewMaths.setText(label);
            textView.setVisibility(GONE);
            katexViewMaths.setVisibility(VISIBLE);
        } else {
            textView.setText(label);
            textView.setVisibility(VISIBLE);
            katexViewMaths.setVisibility(GONE);
        }

        int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen100);
        int startPoint = (x - size);
        if (startPoint < 0) {
            startPoint = 0;
        }
        int size1 = (int) getResources().getDimension(R.dimen.oustlayout_dimen16);
        int maxW = (scrWidth) - size1;
        if (startPoint > scrWidth) {
            startPoint = 0;
        }
        if ((startPoint + size) > maxW) {
            startPoint = (maxW - (2 * size));
        }
        hotspotlabelview.setX(startPoint);
        hotspotlabelview.setY((y + 35));
        hotspot_label_layout.addView(hotspotlabelview);
    }

    private void downloadHotspotImage() {
        String fileURL = null;
        File file = null;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());

            String url = "oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
            file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            hotspotimage = BitmapFactory.decodeFile(fileURL, bmOptions);
            String media = questions.getImageCDNPath();

            new Thread(() -> {
                try {
                    if (hotspotimage == null) {
                        try (InputStream is = new URL(questions.getImageCDNPath()).openStream()) {
                            hotspotimage = BitmapFactory.decodeStream(is);
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            if (hotspotimage == null) {
                Handler handler = new Handler(Looper.getMainLooper());
                String finalFileURL = fileURL;
                File finalFile = file;
                handler.postDelayed(() -> {
                    try {
                        Log.d("HotspotquestionNoError2", " \nfileURL: " + finalFileURL + " \nhotSpot: " + hotspotimage + " \nfile: " + finalFile);
                        hotspotimage = BitmapFactory.decodeFile(finalFileURL, bmOptions);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                }, 300);
            }

            Log.d("HotspotquestionNoError3", " \nfileURL: " + fileURL + " \nhotSpot: " + hotspotimage + " \nfile: " + file);
            if (hotspotimage == null) {
                try {
                    hotspotimage = BitmapFactory.decodeFile(fileURL, bmOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            if (hotspotimage == null) {
                try {
                    URL url1 = new URL(questions.getImageCDNPath());
                    hotspotimage = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            Log.d("HotspotquestionNoError4", " \nfileURL: " + fileURL + " \nhotSpot: " + hotspotimage.getHeight() + " \nfilURL: " + questions.getImageCDNPath());

            if (hotspotimage == null) {
                getBitmapFromURL(questions.getImageCDNPath());
            }

            Handler handler = new Handler(Looper.getMainLooper());
            File finalFile1 = file;
            if (scrHeight < 1280) {
                Log.d("Hotspot inside 1280", "Yes");
                if (isReviewMode) {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        if (hotspotimage != null && options.outWidth != -1 && options.outHeight != -1) {
                            setHotSpotImageSize(hotspotimage);
                        } else {
                            if (OustSdkTools.checkInternetStatus()) {
                                if (OustMediaTools.isAwsOrCDnUrl(media)) {
                                    if (finalFile1.exists()) {
                                        boolean fileDelete = finalFile1.delete();
                                        Log.d("HotSpot", "File delete " + fileDelete);
                                    }
                                } else {
                                    if (finalFile1.exists()) {
                                        boolean fileDelete = finalFile1.delete();
                                        Log.d("HotSpot", "File delete " + fileDelete);
                                    }
                                }
                                downLoad(questions.getImageCDNPath());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else {
                    handler.postDelayed(() -> {
                        try {
//                    hot_spot_image.setImageBitmap(hotspotimage);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            if (hotspotimage != null && options.outWidth != -1 && options.outHeight != -1) {
                                setHotSpotImageSize(hotspotimage);
                            } else {
                                if (OustSdkTools.checkInternetStatus()) {
                                    if (OustMediaTools.isAwsOrCDnUrl(media)) {
                                        if (finalFile1.exists()) {
                                            boolean fileDelete = finalFile1.delete();
                                            Log.d("HotSpot", "File delete " + fileDelete);
                                        }
                                    } else {
                                        if (finalFile1.exists()) {
                                            boolean fileDelete = finalFile1.delete();
                                            Log.d("HotSpot", "File delete " + fileDelete);
                                        }
                                    }
                                    downLoad(questions.getImageCDNPath());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }, 500);
                }
            } else {
                if (isThirdFourthHotSpotImage || isReviewMode) {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        if (hotspotimage != null && options.outWidth != -1 && options.outHeight != -1) {
                            setHotSpotImageSize(hotspotimage);
                        } else {
                            if (OustSdkTools.checkInternetStatus()) {
                                if (OustMediaTools.isAwsOrCDnUrl(media)) {
                                    if (finalFile1.exists()) {
                                        boolean fileDelete = finalFile1.delete();
                                        Log.d("HotSpot", "File delete " + fileDelete);
                                    }
                                } else {
                                    if (finalFile1.exists()) {
                                        boolean fileDelete = finalFile1.delete();
                                        Log.d("HotSpot", "File delete " + fileDelete);
                                    }
                                }
                                downLoad(questions.getImageCDNPath());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else {
                    handler.postDelayed(() -> {
                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            if (hotspotimage != null && options.outWidth != -1 && options.outHeight != -1) {
                                setHotSpotImageSize(hotspotimage);
                            } else {
                                if (OustSdkTools.checkInternetStatus()) {
                                    if (OustMediaTools.isAwsOrCDnUrl(media)) {
                                        if (finalFile1.exists()) {
                                            boolean fileDelete = finalFile1.delete();
                                            Log.d("HotSpot", "File delete " + fileDelete);
                                        }
                                    } else {
                                        if (finalFile1.exists()) {
                                            boolean fileDelete = finalFile1.delete();
                                            Log.d("HotSpot", "File delete " + fileDelete);
                                        }
                                    }
                                    downLoad(questions.getImageCDNPath());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }, 500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("Hotspotquestion1", "error while decoding" + e.getMessage() + " \nfileURL: " + fileURL + " \nhotSpot: " + hotspotimage + " \nfile: " + file);
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.d("HotspotquestionNoError5", "" + myBitmap);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("HotspotquestionNoError6", "" + "null came");
            return null;
        }
    }

    public void downLoad(String url) {

        try {
            DownloadFiles downloadFiles;
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                    //showDownloadProgress();
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    Log.d("HotSpot", "onDownLoadError: Message:" + message + " errorCode:" + errorCode);
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

            String path = Objects.requireNonNull(requireActivity()).getFilesDir() + "/";
            String filename = OustMediaTools.getMediaFileName(url);
            downloadFiles.startDownLoad(url, path, filename, true, false);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setHotSpotImageSize(Bitmap hotSpotImage) {
        try {
            if (hotSpotImage == null) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
                hotSpotImage = BitmapFactory.decodeFile(fileURL, bmOptions);
            }
            this.hotspotImage = hotSpotImage;

            if (questions.getImagewidth() != null) {
                bitmapActualW = Float.parseFloat(questions.getImagewidth());
                if (bitmapActualW == 0) {
                    bitmapActualW = hotSpotImage.getWidth();
                }
            }
            if (questions.getImageheight() != null) {
                bitmapActualH = Float.parseFloat(questions.getImageheight());
                if (bitmapActualH == 0) {
                    bitmapActualH = hotSpotImage.getHeight();
                }
            }

            bitmapW = (scrWidth); /*720*/ /*1280*/
            bitmapH = bitmapW * ((float) hotSpotImage.getHeight() / (float) hotSpotImage.getWidth());

//            if (isThirdFourthHotSpotImage) {
            hot_spot_image.setScaleType(ImageView.ScaleType.FIT_XY);
//            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hot_spot_image.getLayoutParams();

            if (!isThirdFourthHotSpotImage) {
                params.height = ((int) bitmapH) - getStatusBarHeight() - height_toolbar - height_bottom_bar;
            } else {
                params.height = ((int) bitmapH);
            }
//            params.height = ((int) bitmapH) - 40;
            params.width = (int) bitmapW;

            hot_spot_image.setLayoutParams(params);
            hot_spot_image.setImageBitmap(hotspotImage);
            hot_spot_image.setVisibility(VISIBLE);
            hot_spot_image.setImageBitmap(hotspotImage);


            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) hot_spot_touch.getLayoutParams();
//            params1.height = ((int) bitmapH);
            if (!isThirdFourthHotSpotImage) {
                params1.height = ((int) bitmapH) - getStatusBarHeight() - height_toolbar - height_bottom_bar;
            } else {
                params1.height = ((int) bitmapH);
            }

            params1.width = (int) bitmapW;

            hot_spot_touch.setLayoutParams(params1);
            hot_spot_touch.setVisibility(VISIBLE);
            hot_spot_touch.setReviewmode();

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) hotspot_label_layout.getLayoutParams();
            if (isThirdFourthHotSpotImage) {
                params2.height = (int) bitmapH;
            } else {
                params2.height = ((int) bitmapH) - getStatusBarHeight() - height_toolbar - height_bottom_bar;
            }
            params2.width = (int) bitmapW;

            hotspot_label_layout.setLayoutParams(params2);

            //TODO: handle hotspot label

            if (isThirdFourthHotSpotImage) {
                normalHotspotImageSetting();
            } else {
                fullScreenHotspotImageSetting();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("Hotspotquestion3", "error while decoding" + e.getMessage());
            downloadHotspotImage();
        }
    }

    private void normalHotspotImageSetting() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        actualImageWidth = hotspotImage.getWidth();
        actualImageHeight = hotspotImage.getHeight();

        hotSpotImageWidth = displayMetrics.widthPixels;
        hotSpotImageHeight = hotSpotImageWidth * actualImageHeight / actualImageWidth;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hot_spot_image.getLayoutParams();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
        hotspotImage = BitmapFactory.decodeFile(fileURL, bmOptions);


        int apiStartX = hotspotPointDataList.get(0).getStartX();
        int apiStartY = hotspotPointDataList.get(0).getStartY();

        int apIRectangleWidth = hotspotPointDataList.get(0).getWidth();
        int apiRectangleHeight = hotspotPointDataList.get(0).getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = hotSpotImageWidth / (float) actualImageWidth;

        int yAdjustment = (displayMetrics.heightPixels - getStatusBarHeight() - hotSpotImageHeight) / 2;
        final int newStartX = (int) (apiStartX * deviceXFactor);
        final int newStartY = (int) (apiStartY * deviceYFactor);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * deviceXFactor);
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * deviceYFactor);

        int[] endpoints = calculateEndPointsForNormalHotspot(hotspotImage, hotspotPointDataList.get(0));

        hot_spot_touch.drawRect(endpoints[0], endpoints[1], endpoints[2], endpoints[3], false);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        Log.e("TAG", "getStatusBarHeight: " + resourceId);
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int[] calculateEndPointsForNormalHotspot(Bitmap hotspotIcon, DTOHotspotPointData hotspotPointData) {
        int apiStartX = hotspotPointData.getStartX();
        int apiStartY = hotspotPointData.getStartY();

        int apIRectangleWidth = hotspotPointData.getWidth();
        int apiRectangleHeight = hotspotPointData.getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = deviceXFactor;
        this.deviceXFactor = deviceXFactor;
        this.deviceYFactor = deviceYFactor;

        int yAdjustment = (scrHeight - getStatusBarHeight() - hotSpotImageHeight) / 2;
        float minx = (apiStartX * deviceXFactor);
        float minY = (apiStartY * deviceYFactor);

        float maxY = ((apiStartY + apiRectangleHeight) * deviceYFactor);
        float maxX = ((apiStartX + apIRectangleWidth) * deviceXFactor);

        int[] endPoints = new int[4];
        endPoints[0] = (int) (minx);
        endPoints[1] = (int) minY;
        endPoints[2] = (int) maxX;
        endPoints[3] = (int) maxY;
        return endPoints;
    }

    private void fullScreenHotspotImageSetting() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        actualImageWidth = hotspotImage.getWidth();
        actualImageHeight = hotspotImage.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
        if (hotspotImage == null)
            hotspotImage = BitmapFactory.decodeFile(fileURL, bmOptions);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hot_spot_image.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = main_layout.getHeight() - getStatusBarHeight() - height_toolbar - height_bottom_bar;
        hot_spot_image.setLayoutParams(params);
        hot_spot_touch.setReviewmode();

        hotSpotImageWidth = displayMetrics.widthPixels;
        hotSpotImageHeight = main_layout.getHeight();

        int apiStartX = hotspotPointDataList.get(0).getStartX();
        int apiStartY = hotspotPointDataList.get(0).getStartY();

        int apIRectangleWidth = hotspotPointDataList.get(0).getWidth();
        int apiRectangleHeight = hotspotPointDataList.get(0).getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = hotSpotImageHeight / (float) actualImageHeight;
        float multiplyingFactor = 0;

        if (deviceYFactor > deviceXFactor) {
            multiplyingFactor = deviceXFactor;
        } else {
            multiplyingFactor = deviceYFactor;
        }
//            multiplyingFactor = Math.min(deviceYFactor, deviceXFactor);

        int yAdjustment = (main_layout.getHeight() - (int) (actualImageHeight * multiplyingFactor)) / 2;
        int xAdjustment = (displayMetrics.widthPixels - (int) (actualImageWidth * multiplyingFactor)) / 2;


        final int newStartX = (int) (apiStartX * multiplyingFactor) + xAdjustment;
        final int newStartY = (int) ((apiStartY * multiplyingFactor) + yAdjustment);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * multiplyingFactor) + xAdjustment;
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * multiplyingFactor) + yAdjustment;
        //  int newChangedEndY = newEndY+(int)(newEndY*0.5);

        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) hot_spot_image.getLayoutParams();
        bitmapH = bitmapW * ((float) hotspotImage.getHeight() / (float) hotspotImage.getWidth());

        if (multiplyingFactor < 1) {
            multiplyingFactor = 1.0f;
        }
        params3.height = (int) (multiplyingFactor * hotSpotImageHeight);
        params3.width = (int) (multiplyingFactor * hotSpotImageWidth);

        params3.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params3.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        hot_spot_image.setLayoutParams(params3);


        int[] endpoints = calculateEndPointsForFullScreenHotspot(hotspotImage, hotspotPointDataList.get(0));
        hot_spot_image.setImageBitmap(hotspotImage);
    }

    private int[] calculateEndPointsForFullScreenHotspot(Bitmap hotspotIcon, DTOHotspotPointData hotspotPointData) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        actualImageWidth = hotspotIcon.getWidth();
        actualImageHeight = hotspotIcon.getHeight();

        hotSpotImageWidth = displayMetrics.widthPixels;
        hotSpotImageHeight = main_layout.getHeight();

        int apiStartX = hotspotPointData.getStartX();
        int apiStartY = hotspotPointData.getStartY();

        int apIRectangleWidth = hotspotPointData.getWidth();
        int apiRectangleHeight = hotspotPointData.getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = hotSpotImageHeight / (float) actualImageHeight;
        float multiplyingFactor = 0;

//        multiplyingFactor = Math.min(deviceYFactor, deviceXFactor);
        if (deviceYFactor > deviceXFactor) {
            multiplyingFactor = deviceXFactor;
        } else {
            multiplyingFactor = deviceYFactor;
        }
        this.deviceYFactor = this.deviceXFactor = multiplyingFactor;

        int yAdjustment = (main_layout.getHeight() - (int) (actualImageHeight * multiplyingFactor)) / 2;
        int xAdjustment = (displayMetrics.widthPixels - (int) (actualImageWidth * multiplyingFactor)) / 2;

        final int newStartX = (int) (apiStartX * multiplyingFactor) + xAdjustment;
        final int newStartY = (int) ((apiStartY * multiplyingFactor) + yAdjustment);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * multiplyingFactor) + xAdjustment;
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * multiplyingFactor) + yAdjustment;

        int[] endPoints = new int[4];
        endPoints[0] = newStartX;
        endPoints[1] = newStartY;
        endPoints[2] = newEndX;
        endPoints[3] = newEndY;
        return endPoints;
    }

    private void showHotSpotOptionAnim() {
        try {
            if (hotspotPointDataList.size() > currentPageNo) {
                if (hotspotPointDataList.get(currentPageNo).getHpQuestion() != null) {
                    if (hotspotPointDataList.get(currentPageNo).getHpQuestion().contains(KATEX_DELIMITER)) {
                        //TODO: handle katex method
                        option_text.setVisibility(View.VISIBLE);
                        info_type.setVisibility(View.VISIBLE);
                        question_card.setVisibility(View.VISIBLE);
                        option_text.setTextColor(getResources().getColor(R.color.primary_text));
                        option_text.setText(hotspotPointDataList.get(currentPageNo).getHpQuestion() + ". " + hotspotPointDataList.get(currentPageNo).getHpLabel());
                        option_text.setVisibility(View.VISIBLE);
                        question.setVisibility(View.GONE);
                        question_description_webView.setVisibility(View.GONE);
                    } else {
                        option_text.setVisibility(View.VISIBLE);
                        info_type.setVisibility(View.VISIBLE);
                        question_card.setVisibility(View.VISIBLE);
//                        option_text.setText("" + (currentPageNo + 1) + ". " + hotspotPointDataList.get(currentPageNo).getHpLabel());
                        option_text.setText(hotspotPointDataList.get(currentPageNo).getHpQuestion() + ". " + hotspotPointDataList.get(currentPageNo).getHpLabel());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAttemptLeft() {
        try {
            if (attemptLeft > 0) {
                attemptsLeftString = attemptLeft + " " + Objects.requireNonNull(requireActivity()).getResources().getString(R.string.attempt_left_text);
                info_type.setText(attemptsLeftString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void touchListenerForHotSpot() {
        try {
            hot_spot_image.setOnTouchListener((view, event) -> {
                shouldDisplayRightWrong = (!isThirdFourthHotSpotImage && hotspotPointDataList.size() > 1);
                if (!hotSpotComplete) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        x11 = event.getX();
                        y11 = event.getY();
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        x21 = event.getX();
                        y21 = event.getY();
                        float deltaX = x11 - x21;
                        float deltaY = y11 - y21;
                        if (deltaX > 0 && deltaY > 0) {
                            if (deltaX > deltaY) {
                                if (deltaX > MIN_DISTANCE) {
                                    if (learningModuleInterface != null && hotSpotComplete)
                                        if (isVideoOverlay()) {
                                            learningModuleInterface.closeChildFragment();
                                        } else {
                                            handleNextQuestion("", "", 0, false, 0);
                                        }
                                }
                            }
                        } else if (deltaX < 0 && deltaY > 0) {
                            if ((-deltaX) > deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    if (learningModuleInterface != null)
                                        if (!isVideoOverlay())
                                            learningModuleInterface.gotoPreviousScreen();
                                }
                            }

                        } else if (deltaX < 0 && deltaY < 0) {
                            if (deltaX < deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    if (learningModuleInterface != null)
                                        if (!isVideoOverlay())
                                            learningModuleInterface.gotoPreviousScreen();
                                }
                            }
                        } else if (deltaX > 0 && deltaY < 0) {
                            if (deltaX > (-deltaY)) {
                                if (deltaX > MIN_DISTANCE) {
                                    if (learningModuleInterface != null && hotSpotComplete)
                                        if (isVideoOverlay()) {
                                            learningModuleInterface.closeChildFragment();
                                        } else {
                                            handleNextQuestion("", "", 0, false, 0);
                                        }
                                }
                            }
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (attemptLeft > 0) {
                            if (currentPageNo <= (hotspotPointDataList.size() - 1) && !hotSpotComplete) {
                                float touchX = (int) event.getX();
                                float touchY = (int) event.getY();
                                boolean rightTouch = false;
                                int touchNO = -1;

                                float minx, minY, maxX, maxY;

                                int[] endPoints;
                                if (!isThirdFourthHotSpotImage) {
                                    endPoints = calculateEndPointsForFullScreenHotspot(hotspotImage, hotspotPointDataList.get(currentPageNo));
                                } else {
                                    endPoints = calculateEndPointsForNormalHotspot(hotspotImage, hotspotPointDataList.get(currentPageNo));
                                }
                                minx = (float) endPoints[0];
                                minY = (float) endPoints[1];
                                maxX = (float) endPoints[2];
                                maxY = (float) endPoints[3];

                                if ((touchX > (minx)) && (touchX < (maxX))) {
                                    if ((touchY > (minY)) && (touchY < (maxY))) {
                                        if (!touchPoint[currentPageNo]) {
                                            touchNO = currentPageNo;
                                            touchPoint[currentPageNo] = true;
                                        }
                                        rightTouch = true;
                                    }
                                }

                                if (!isThirdFourthHotSpotImage && hotspotPointDataList.size() > 1) {
                                    for (int i = 0; i < hotspotPointDataList.size(); i++) {
                                        endPoints = calculateEndPointsForFullScreenHotspot(hotspotImage, hotspotPointDataList.get(i));
                                        minx = (float) endPoints[0];
                                        minY = (float) endPoints[1];
                                        maxX = (float) endPoints[2];
                                        maxY = (float) endPoints[3];
                                        if ((touchX > (minx)) && (touchX < (maxX))) {
                                            if ((touchY > (minY)) && (touchY < (maxY))) {
                                                noOfTimesClicked++;
                                                if (!isHotSpotAnswerSelectedRight) {
                                                    if (hotspotPointDataList.get(i).isAnswer()) {
                                                        isHotSpotAnswerSelectedRight = true;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (isHotSpotAnswerSelectedRight) {
                                        calculateHotspotPointsForMCQ(false, true);
                                        return true;
                                    }
                                    if ((noOfTimesClicked) > hotspotPointDataList.size()) {
                                        calculateHotspotPointsForMCQ(false, false);
                                        return true;
                                    }
                                    return true;
                                }
                                if (isCourseQuestion) {
                                    if (!rightTouch) {
                                        vibrateAndShake(hot_spot_image);
                                        hotSpotWrongCount++;
                                        showSolutionAnswer = false;
                                        wrongAnswerSound();

                                        HotspotPointData hotspotPointData = new HotspotPointData();
                                        hotspotPointData.setStartX((int) touchX);
                                        hotspotPointData.setStartY((int) touchY);
                                        hotspotPointData.setWidth(60);
                                        if (isHotSpotThumbsDownShown) {
                                            if (!shouldDisplayRightWrong) {
                                                hot_spot_touch.wrongPoint(hotspotPointData);
                                            }
                                        }
                                        attemptLeft--;
                                        if (attemptLeft == 0) {
                                            touchPoint[currentPageNo] = true;
                                            float w = ((hotspotPointDataList.get(currentPageNo).getStartX() + ((hotspotPointDataList.get(currentPageNo).getWidth() * 1.0f) / 2)));
                                            float h = ((hotspotPointDataList.get(currentPageNo).getStartY() + ((hotspotPointDataList.get(currentPageNo).getHeight() * 1.0f) / 2)));
                                            Log.e("TAG", "touchListenerForHotSpot:--> " + shouldDisplayRightWrong);
                                            if (!shouldDisplayRightWrong) {
                                                showRightPointWithOption(true, ((int) (w * deviceXFactor)), ((int) (h * deviceYFactor)));
                                            }
                                        }
                                        setAttemptLeft();
                                    } else {
                                        if (touchNO != (-1)) {
                                            hotSpotRightCount++;
                                            DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
                                            hotspotPointData.setStartX((int) touchX);
                                            hotspotPointData.setStartY((int) touchY);
                                            hotspotPointData.setWidth(80);
                                            if (isHotSpotThumbsUpShown) {
                                                hot_spot_touch.rightPoint(hotspotPointData, touchNO, touchPoint);
                                            }
                                            if ((currentPageNo + 1) < hotspotPointDataList.size()) {
                                                rightAnswerSound();
                                            }
                                            if (!shouldDisplayRightWrong) {
                                                showRightPointWithOption(false, (int) touchX, (int) touchY);
                                            }
                                        }
                                    }
                                } else if (!hotSpotComplete) {
                                    if (!rightTouch) {
                                        hotSpotWrongCount++;
                                    } else {
                                        hotSpotRightCount++;
                                    }
                                    touchPoint[currentPageNo] = true;
                                    DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
                                    hotspotPointData.setStartX((int) touchX);
                                    hotspotPointData.setStartY((int) touchY);
                                    hotspotPointData.setWidth(80);
                                    hot_spot_touch.assessmentPoint(hotspotPointData, currentPageNo, true, currentPageNo, touchPoint);
                                    if (!shouldDisplayRightWrong) {
                                        showRightPointWithOption(false, (int) touchX, (int) touchY);
                                    }
                                }
                            }
                        } else {
                            info_type.setText((Objects.requireNonNull(requireActivity()).getResources().getString(R.string.no_attempt_left)));
                        }
                    }
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void calculateHotspotPointsForMCQ(boolean timeout, boolean rightOrWrong) {
        try {
            hotSpotComplete = true;
            boolean isCorrect = false;
            if (!isAssessmentQuestion) {
                isCorrect = true;
                finalScr = Math.round(hotspotMarks);
            } else {
                float weight = (float) mainCourseCardClass.getXp() / (hotspotPointDataList.size());
                float rightVal = weight * hotSpotRightCount;
                finalScr = Math.round(rightVal);
            }

            if (isAssessmentQuestion && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())
                    && (hotSpotRightCount != hotspotPointDataList.size() || timeout)) {
                finalScr = 0;
                isCorrect = false;
            }

            if (hotSpotRightCount == hotspotPointDataList.size()) {
                isCorrect = true;
            }

            if (finalScr < 0) {
                finalScr = 0;
            } else if (finalScr > mainCourseCardClass.getXp()) {
                finalScr = (int) mainCourseCardClass.getXp();
            }

            noOfTimesClicked = noOfTimesClicked - 1;
            if (noOfTimesClicked > hotspotPointDataList.size()) {
                finalScr = 0;
            } else {
                finalScr = (int) (mainCourseCardClass.getXp() - ((float) noOfTimesClicked * mainCourseCardClass.getXp() / (hotspotPointDataList.size())));
            }
            Log.d("TAG", "calculateHotspotPointsForMCQ: final:" + finalScr + " -- iscorrect:" + isCorrect);
            if (timeout) {
                showSolutionAnswer = false;
                answerSubmit("", 0, true, false);
            } else {
                if (isCourseQuestion) {
                    isCorrect = rightOrWrong;
                }
                answerSubmit(questions.getAnswer(), finalScr, false, isCorrect);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void calculateHotspotPointsForOptions(boolean timeout) {
        try {
            hotSpotComplete = true;
            boolean isCorrect = false;
            if (isCourseQuestion) {
                isCorrect = true;
                finalScr = Math.round(hotspotMarks);
                Log.e("TAG", "calculateHotspotPointsForOptions: - if-> " + finalScr);
            } else {
                float weight = (float) mainCourseCardClass.getXp() / (hotspotPointDataList.size());
                float rightVal = weight * hotSpotRightCount;
                finalScr = Math.round(rightVal);
                Log.e("TAG", "calculateHotspotPointsForOptions: - else-> " + finalScr);
            }

            if (isAssessmentQuestion && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())
                    && (hotSpotRightCount != hotspotPointDataList.size() || timeout)) {
                finalScr = 0;
                isCorrect = false;
            }

            if (hotSpotRightCount == hotspotPointDataList.size()) {
                isCorrect = true;
            }

            if (finalScr < 0) {
                finalScr = 0;
            } else if (finalScr > mainCourseCardClass.getXp()) {
                finalScr = (int) mainCourseCardClass.getXp();
            }
            Log.e("TAG", "calculateHotspotPointsForOptions: question count -> " + currentPageNo);
            Log.e("TAG", "calculateHotspotPointsForOptions: question isCorrect -> " + isCorrect);
            if (timeout) {
                answerSubmit("", 0, true, false);
            } else {
                if (isCourseQuestion) {
                    if (hotspotMarks <= 0) {
                        isCorrect = false;
                    } else {
                        isCorrect = true;
                    }
                }
                answerSubmit(questions.getAnswer(), finalScr, false, isCorrect);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void vibrateAndShake(View v) {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            v.startAnimation(shakeAnim);
            ((Vibrator) Objects.requireNonNull(requireActivity()).getSystemService(Context.VIBRATOR_SERVICE)).vibrate(ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void wrongAnswerSound() {
        try {
            stopMediaPlayer();
            playAudio("answer_incorrect.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void rightAnswerSound() {
        try {
            stopMediaPlayer();
            playAudio("answer_correct.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playAudio(final String filename) {
        new Handler().post(() -> {
            try {
                File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                tempMp3.deleteOnExit();
//                answerChosenPlayer = new MediaPlayer();
                answerChosenPlayer.reset();
                FileInputStream fis = new FileInputStream(tempMp3);
                answerChosenPlayer.setDataSource(fis.getFD());
                answerChosenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                answerChosenPlayer.prepare();
                answerChosenPlayer.setOnPreparedListener(HotSpotQuestionFragment::onPrepared);
                answerChosenPlayer.start();
                answerChosenPlayer.setOnCompletionListener(HotSpotQuestionFragment::onCompletion);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });
    }

    private static void onPrepared(MediaPlayer mp) {

    }

    private static void onCompletion(MediaPlayer mediaPlayer) {

    }

    private void showRightPointWithOption(boolean maxLimitOver, int x, int y) {
        try {
            if (!isAssessmentQuestion) {
                calculatePointForIndividualHotspot();
                DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
                hotspotPointData.setStartX(x);
                hotspotPointData.setStartY(y);
                hotspotPointData.setWidth(80);
                if (isHotSpotThumbsUpShown) {
                    hot_spot_touch.rightPoint(hotspotPointData, currentPageNo, touchPoint);
                }
                hot_spot_touch.rightPoint(hotspotPointData, currentPageNo, touchPoint);
            }

            if ((currentPageNo + 1) == hotspotPointDataList.size()) {
                currentPageNo++;
                calculateHotspotPointsForOptions(false);
            } else {
                currentPageNo++;
                hideHotSpotOptionAnim(maxLimitOver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void calculatePointForIndividualHotspot() {
        try {
            Log.d("TAG", "calculatePointForIndividualHotspot: " + hotSpotRightCount + " --- xp:" + mainCourseCardClass.getXp() + " --- size:" + hotspotPointDataList.size());
            if (hotSpotRightCount > 0) {
                float weight = (float) mainCourseCardClass.getXp() / (hotspotPointDataList.size());
                float rightVal = weight * hotSpotRightCount;
                hotspotMarks += rightVal - ((10 * hotSpotWrongCount));
            }
            hotSpotRightCount = 0;
            hotSpotWrongCount = 0;

            Log.d("TAG", "calculatePointForIndividualHotspot: hotspotMarks:" + hotspotMarks);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void answerSubmit(String answer, int oc, boolean isTimeOut, boolean isCorrect) {
        try {
            if (courseContentHandlingInterface != null) {
                courseContentHandlingInterface.cancelTimer();
            }
            if (!popupShownOnce) {
                popupShownOnce = true;
                if (questions != null) {
                    //TODO: handle contain subjective
                    if (isVideoOverlay()) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setVideoOverlayAnswerAndOc(answer, "", oc, isCorrect, 0, cardId);
                        }
                    } else {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setAnswerAndOc(answer, "", oc, isCorrect, 0);
                        }
                    }
                    if (!isAssessmentQuestion) {
                        rightWrongFlipAnimation(isCorrect, answer, "", oc, isCorrect, 0);
                    } else {
                        if (isVideoOverlay()) {
                            if (learningModuleInterface != null)
                                learningModuleInterface.closeChildFragment();
                        } else {
                            if (learningModuleInterface != null) {
                                handleNextQuestion("", "", 0, false, 0);
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

    public void rightWrongFlipAnimation(boolean status1, String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            if (!zeroXpForQCard) {
                if (finalScr == 0) {
                    question_result_image.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                } else {
                    question_result_image.setVisibility(View.VISIBLE);
                    coinsAnimLayout.setVisibility(VISIBLE);
                }
            } else {
                coinsAnimLayout.setVisibility(View.GONE);
            }
            if (status1) {
                rightAnswerSound();
                if (isVideoOverlay()) {
                    question_result_image.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
                    new Handler().postDelayed(() -> {
                        try {
                            learningModuleInterface.closeChildFragment();
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }, 1500);
                }
                animMainLayout.setVisibility(View.VISIBLE);
                if (animMainLayout.getVisibility() == View.VISIBLE) {
                    OustSdkTools.setImage(coinsAnimImg, OustSdkApplication.getContext().getResources().getString(R.string.newxp_img));
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(question_result_image, "scaleX", 0.0f, 1);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(question_result_image, "scaleY", 0.0f, 1);
                    scaleDownX.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
                    scaleDownY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
                    scaleDownX.setInterpolator(new DecelerateInterpolator());
                    scaleDownY.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown = new AnimatorSet();
                    scaleDown.play(scaleDownX).with(scaleDownY);
                    scaleDown.start();
                    scaleDown.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (zeroXpForQCard) {
                                //TODO: handle show solution popup
                                if (!isVideoOverlay()) {
                                    handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
                                }
                            } else {
                                animateOcCoins(userAns, subjectiveResponse, oc, status, time);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    try {
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            } else {
                try {
                    showSolutionAnswer = false;
                    animMainLayout.setVisibility(View.VISIBLE);
                    question_result_image.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                    vibrateAndShake(hot_spot_image);
                    wrongAnswerSound();
                    showSolutionAnswer = false;
                    if (isVideoOverlay()) {
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.handleScreenTouchEvent(false);
                        }
                        question_result_image.setVisibility(View.VISIBLE);
                        OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                        learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", 0, false, 0, cardId);
                        new Handler().postDelayed(() -> {
                            try {
                                if (proceedOnWrong) {
                                    if (learningModuleInterface != null) {
                                        learningModuleInterface.closeChildFragment();
                                    }
                                } else {
                                    if (learningModuleInterface != null) {
                                        learningModuleInterface.wrongAnswerAndRestartVideoOverlay();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }, 500);
                    } else {
                        handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void animateOcCoins(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            ValueAnimator animator1 = new ValueAnimator();
            if ((finalScr > 0)) {
                if (answerChosenPlayer == null) {
                    answerChosenPlayer = new MediaPlayer();
                }
                isLevelCompleted = isReplayMode || OustStaticVariableHandling.getInstance().isLevelCompleted();
                if (isCourseCompleted || isLevelCompleted) {
                } else {
                    playAudio("coins.mp3");
                }
            }
            if (isCourseCompleted || isLevelCompleted) {
                coinsAnimImg.setVisibility(View.GONE);
                coinsAnimText.setVisibility(View.GONE);
            }
            animator1.setObjectValues(0, (int) (finalScr));
            animator1.setDuration(SIX_HUNDRED_MILLI_SECONDS);
            animator1.addUpdateListener(valueAnimator -> {
                if (isCourseCompleted || isLevelCompleted) {
                    coinsAnimImg.setVisibility(View.GONE);
                    coinsAnimText.setVisibility(View.GONE);
                } else {
                    coinsAnimImg.setVisibility(View.VISIBLE);
                    coinsAnimText.setVisibility(View.VISIBLE);
                    coinsAnimText.setText("" + (((int) valueAnimator.getAnimatedValue())));
                }
            });
            animator1.start();
            animator1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (isCourseCompleted || isLevelCompleted) {
                        coinsAnimImg.setVisibility(View.GONE);
                        coinsAnimText.setVisibility(View.GONE);
                    } else {
                        coinsAnimImg.setVisibility(View.VISIBLE);
                        coinsAnimText.setVisibility(View.VISIBLE);
                        coinsAnimText.setText("" + finalScr);
                    }
                    //TODO: handle show solution animation
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            new Handler().postDelayed(() -> {
                try {
                    if (!isVideoOverlay()) {
                        handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopMediaPlayer() {
        try {
            if (answerChosenPlayer == null) {
                answerChosenPlayer = new MediaPlayer();
            }
            if (answerChosenPlayer.isPlaying()) {
                answerChosenPlayer.stop();
            }
            answerChosenPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideHotSpotOptionAnim(boolean timeout) {
        try {
            if (!isAssessmentQuestion) {
                if (!timeout) {
                    option_text.setTextColor(OustSdkTools.getColorBack(R.color.progress_correct));
                } else {
                    option_text.setTextColor(OustSdkTools.getColorBack(R.color.error_incorrect));
                }
            }
            new Handler().postDelayed(this::showHotSpotLabelAnim, 500);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showHotSpotLabelAnim() {
        try {
            if (hotspotPointDataList.size() > currentPageNo) {
                if (hotspotPointDataList.get(currentPageNo).getHpQuestion() != null) {
                    if (hotspotPointDataList.get(currentPageNo).getHpQuestion().contains(KATEX_DELIMITER)) {
                        option_text.setVisibility(View.VISIBLE);
                        info_type.setVisibility(View.VISIBLE);
                        question_card.setVisibility(View.VISIBLE);
                        option_text.setTextColor(getResources().getColor(R.color.primary_text));
//                        option_text.setText("" + (currentPageNo + 1) + ". " + hotspotPointDataList.get(currentPageNo).getHpQuestion());
                        option_text.setText(hotspotPointDataList.get(currentPageNo).getHpQuestion() + ". " + hotspotPointDataList.get(currentPageNo).getHpLabel());
                        option_text.setVisibility(View.VISIBLE);
                        question.setVisibility(View.GONE);
                        question_description_webView.setVisibility(View.GONE);
                    } else {
                        option_text.setTextColor(OustSdkTools.getColorBack(R.color.primary_text));
                        option_text.setVisibility(View.VISIBLE);
//                        option_text.setText("" + (currentPageNo + 1) + ". " + hotspotPointDataList.get(currentPageNo).getHpQuestion());
                        option_text.setText(hotspotPointDataList.get(currentPageNo).getHpQuestion() + ". " + hotspotPointDataList.get(currentPageNo).getHpLabel());
                    }
                }
            }
            attemptLeft = 5;
            if (!isAssessmentQuestion) {
                setAttemptLeft();
            } else {
                info_type.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleNextQuestion(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            new Handler().postDelayed(() -> {
                animMainLayout.setVisibility(View.GONE);
                if (isCourseQuestion) {
                    if (!isVideoOverlay()) {
                        if (solutionText != null && !solutionText.isEmpty()) {
                            if (showSolution) {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.showSolutionPopUP(solutionText, showSolutionAnswer, false, true, userAns, subjectiveResponse, oc, status, time);
                                }
                            } else if (!showSolutionAnswer) {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.showSolutionPopUP(solutionText, false, false, true, userAns, subjectiveResponse, oc, status, time);
                                }
                            } else {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.handleScreenTouchEvent(true);
                                    courseContentHandlingInterface.setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                                }
                            }
                        } else {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.handleScreenTouchEvent(true);
                                courseContentHandlingInterface.setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                            }
                        }
                        showSolutionAnswer = true;
                    }
                } else {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.gotoNextScreen();
                    }
                }
            }, FOUR_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setShowNavigateArrow(boolean showNavigationArrow) {
        this.showNavigateArrow = showNavigationArrow;
    }

    public void setAssessmentQuestion(boolean isAssessmentQuestion) {
        this.isAssessmentQuestion = isAssessmentQuestion;
    }

    public void setSurveyQuestion(boolean isSurveyQuestion) {
        this.isSurveyQuestion = isSurveyQuestion;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }

    public void setAssessmentScore(Scores scores) {
        this.score = scores;
    }

    public void setTotalCards(int cardCount) {
        this.cardCount = cardCount;
    }

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    public void setToolbarHeight(int width_toolbar, int height_toolbar, int height_bottom_bar) {
        try {
            this.width_toolbar = width_toolbar;
            this.height_toolbar = height_toolbar;
            this.height_bottom_bar = height_bottom_bar;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleTimerEnd() {
        try {
            hotSpotComplete = true;
            Log.e("TAG", "handleTimerEnd: shouldDisplayRightWrong-> " + shouldDisplayRightWrong);
            if (!shouldDisplayRightWrong) {
                calculateHotspotPointsForMCQ(true, false);
            } else {
                calculateHotspotPointsForOptions(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setReplayMode(boolean isReplayMode) {
        this.isReplayMode = isReplayMode;
    }
}
