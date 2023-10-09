package com.oustme.oustsdk.question_module.fragment;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.SIX_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.categorySwipe.LinearRegression;
import com.oustme.oustsdk.categorySwipe.SwipeFlingAdapterViewNew;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.FlingListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.adapter.CategoryAnswersAdapter;
import com.oustme.oustsdk.question_module.adapter.CategoryImageTextAdapter;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOQuestionOption;
import com.oustme.oustsdk.room.dto.DTOQuestionOptionCategory;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;

public class CategoryQuestionFragment extends Fragment {

    String TAG = "CategoryQuestionFragment";

    SwipeFlingAdapterViewNew swipeCardView;
    private TextView category_righttext_layout;
    private TextView category_lefttext_layout;
    private TextView category_optionsleft;
    private ImageView category_rightimage_layout, category_leftimage_layout;
    TextView question;
    WebView question_description_webView;
    TextView all_sorted;
    TextView question_count_num;
    RelativeLayout video_lay;
    RelativeLayout media_question_container;
    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView info_type;
    ImageView expand_icon;
    RecyclerView question_answer_rv;
    RelativeLayout parent_layout;
    RecyclerView answer_recyclerview;
    //Right And Wrong questions thump and coins
    ImageView question_result_image;
    RelativeLayout animMainLayout;
    LinearLayout coinsAnimLayout;
    ImageView coinsAnimImg;
    TextView coinsAnimText;

    private MediaPlayer answerChosenPlayer;


    String solutionText;
    boolean showSolutionAnswer = true;
    boolean showSolution = false;
    boolean containSubjective = false;
    boolean isReplayMode = false;
    boolean isLevelCompleted = false;

    //Common for all modules
    LearningModuleInterface learningModuleInterface;
    CourseContentHandlingInterface courseContentHandlingInterface;
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
    boolean isCourseQuestion;
    static boolean isTimerEnd = false;
    public static boolean isCourseQuestionIsRight = true;

    String cardId;
    private long questionXp = 20;
    boolean isRandomizeQuestion;
    DTOQuestions questions;

    //additional parameters
    int color;
    boolean isAssessmentQuestion;

    boolean videoOverlay;
    boolean proceedOnWrong;
    boolean isReviewMode;

    int maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
    int minWordsCount = 0;

    Context context;
    DTOCourseSolutionCard courseSolutionCard;

    public CategoryImageTextAdapter arrayAdapter;
    private int i = 0;
    private List<DTOQuestionOption> optionData = new ArrayList<>();
    public int totalOption = 0;
    private int finalScr = 0;
    int countText = 1;

    //justify popup
    ConstraintLayout justifyPopupConstraintLayout;
    TextView justifyPopupHeader;
    EditText justifyPopupEditText;
    TextView justifyPopupLimitText;
    FrameLayout justifyPopupActionButton;
    CardView justifyCardView;
    ImageView justifyPopupCloseIcon;

    CategoryAnswersAdapter categoryAnswersAdapter;
    ArrayList<String> leftData = new ArrayList<>();
    ArrayList<String> leftDataType = new ArrayList<>();
    ArrayList<String> rightData = new ArrayList<>();
    ArrayList<String> rightDataType = new ArrayList<>();

    public CategoryQuestionFragment() {

    }

    public static CategoryQuestionFragment newInstance() {
        return new CategoryQuestionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        /*try {
            surveyFunctionsAndClicks = (SurveyFunctionsAndClicks) context;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout

        View view = inflater.inflate(R.layout.fragment_category_question, container, false);

        swipeCardView = view.findViewById(R.id.swipeCardView);
        all_sorted = view.findViewById(R.id.all_sorted);
        category_righttext_layout = view.findViewById(R.id.category_righttext_layout);
        category_rightimage_layout = view.findViewById(R.id.category_rightimage_layout);
        category_leftimage_layout = view.findViewById(R.id.category_leftimage_layout);
        category_lefttext_layout = view.findViewById(R.id.category_lefttext_layout);
        category_optionsleft = view.findViewById(R.id.category_optionsleft);
        parent_layout = view.findViewById(R.id.parent_layout);
        answer_recyclerview = view.findViewById(R.id.answer_recyclerview);

        question_base_frame = view.findViewById(R.id.question_base_frame);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        main_layout = view.findViewById(R.id.main_layout);
        question_count_num = view.findViewById(R.id.question_count_num);
        video_lay = view.findViewById(R.id.video_lay);
        media_question_container = view.findViewById(R.id.media_question_container);
        question = view.findViewById(R.id.question);
        question_description_webView = view.findViewById(R.id.description_webView);
        info_type = view.findViewById(R.id.info_type);
        expand_icon = view.findViewById(R.id.expand_icon);
        question_answer_rv = view.findViewById(R.id.question_answer_rv);
        //question_action_button = view.findViewById(R.id.question_action_button);

        question_result_image = view.findViewById(R.id.question_result_image);
        animMainLayout = view.findViewById(R.id.category_thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);

        justifyPopupConstraintLayout = view.findViewById(R.id.justify_popup_constraint_layout);
        justifyPopupEditText = view.findViewById(R.id.justify_popup_edit_text);
        justifyPopupHeader = view.findViewById(R.id.justify_popup_header);
        justifyPopupLimitText = view.findViewById(R.id.justify_popup_limit_text);
        justifyPopupActionButton = view.findViewById(R.id.justify_popup_button);
        justifyCardView = view.findViewById(R.id.justify_popup_cardView);
        justifyPopupCloseIcon = view.findViewById(R.id.justify_popup_close_button);

        String instruction = "INSTRUCTIONS - Sort each card by dragging into the correct pile. Click forward arrow when done.";
        String[] spilt = instruction.split("-");
        Spannable spannable = new SpannableString(instruction);
        spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), spilt[0].length(), instruction.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        category_optionsleft.setText(spannable);

        isTimerEnd = false;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        info_type.setVisibility(View.GONE);
        if (isCourseQuestion) {
            setQuestionData(questions);
            if (!isReviewMode) {
                parent_layout.setVisibility(View.VISIBLE);
                answer_recyclerview.setVisibility(View.GONE);
                startCategoryLayout();
            } else {
                leftData.clear();
                leftDataType.clear();
                rightData.clear();
                rightDataType.clear();
                courseContentHandlingInterface.cancelTimer();
                parent_layout.setVisibility(View.GONE);
                answer_recyclerview.setVisibility(View.VISIBLE);
                if (question != null) {
                    try {
                        leftData.add(questions.getOptionCategories().get(0).getData());
                        leftDataType.add(questions.getOptionCategories().get(0).getType());
                        rightData.add(questions.getOptionCategories().get(1).getData());
                        rightDataType.add(questions.getOptionCategories().get(1).getType());
                        for (int i = 0; i < questions.getOptions().size(); i++) {
                            if (questions.getOptionCategories().get(0).getCode().equalsIgnoreCase(questions.getOptions().get(i).getOptionCategory())) {
                                leftData.add(questions.getOptions().get(i).getData());
                                leftDataType.add(questions.getOptions().get(i).getType());
                            } else {
                                rightData.add(questions.getOptions().get(i).getData());
                                rightDataType.add(questions.getOptions().get(i).getType());
                            }
                        }
                        answer_recyclerview.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        answer_recyclerview.setLayoutManager(layoutManager);
                        categoryAnswersAdapter = new CategoryAnswersAdapter(getContext(), leftData, rightData, leftDataType, rightDataType);
                        answer_recyclerview.setAdapter(categoryAnswersAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
        }
    }

    private void startCategoryLayout() {
        try {
            if (OustSdkTools.categoryData != null)
                OustSdkTools.categoryData.clear();
            if (OustSdkTools.optionData != null)
                OustSdkTools.optionData.clear();
            OustSdkTools.categoryData = null;
            OustSdkTools.optionData = null;
            OustSdkTools.totalAttempt = 0;
            OustSdkTools.optionSelected = 0;
            optionData = new ArrayList<>();
//            category_layout.setVisibility(View.VISIBLE);
            optionData = questions.getOptions();
            List<DTOQuestionOptionCategory> categoryData = questions.getOptionCategories();
            Collections.shuffle(optionData);
            OustSdkTools.categoryData = categoryData;
            OustSdkTools.optionData = optionData;
            totalOption = optionData.size();

            String countShow = countText + "/" + totalOption;

            if (countShow.contains("/")) {
                String[] spilt = countShow.split("/");
                Spannable spannable = new SpannableString(countShow);
                spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), countShow.length(), 0);
                category_optionsleft.setText(spannable);
            } else {
                category_optionsleft.setText(countShow);
            }

            setcategories(categoryData);

//            main logic of swiping cards are in class FlingCardListener

            arrayAdapter = new CategoryImageTextAdapter(getActivity(), optionData);
            swipeCardView.setAdapter(arrayAdapter);
            swipeCardView.setFlingListener(new SwipeFlingAdapterViewNew.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    if (optionData.size() > 0) {
                        optionData.remove(0);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    if (optionData.size() == 0 && i == 0) {
                        Log.e("Category", "CategoryQuestionFragment optionData.size(): " + optionData.size() + ": about to go inside noCardsLeft");
                        noCardsLeft();
                    }
                    Log.e("Category", "CategoryQuestionFragment optionData.size(): " + optionData.size());
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    //Do something on the left!
                    //You also have access to the original object.
                    //If you want to use it just cast it (String) dataObject
                    Log.e("Category", "CategoryQuestionFragment left card exit : " + OustSdkTools.optionSelected);
                    countText++;
                    if (optionData.size() == 1) {

                        String countShow = countText + "/" + totalOption;
                        if (countShow.contains("/")) {
                            String[] spilt = countShow.split("/");
                            Spannable spannable = new SpannableString(countShow);
                            spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), countShow.length(), 0);
                            category_optionsleft.setText(spannable);
                        } else {
                            category_optionsleft.setText(countShow);
                        }
//                        category_optionsleft.setText("1 " + getActivity().getResources().getString(R.string.card_left));
                        all_sorted.setVisibility(View.VISIBLE);
                    } else if (optionData.size() == 0) {

//                        category_optionsleft.setText("0 " + getActivity().getResources().getString(R.string.card_left));

                        if (optionData.size() == 0 && i == 0) {
                            Log.e("Category", "CategoryQuestionFragment left card exit : about to go inside noCardsLeft");
                            noCardsLeft();
                        }

                    } else {
                        String countShow = countText + "/" + totalOption;
                        if (countShow.contains("/")) {
                            String[] spilt = countShow.split("/");
                            Spannable spannable = new SpannableString(countShow);
                            spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), countShow.length(), 0);
                            category_optionsleft.setText(spannable);
                        } else {
                            category_optionsleft.setText(countShow);
                        }
//                        category_optionsleft.setText(optionData.size() + " " + getActivity().getResources().getString(R.string.cards_left));
                    }
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    Log.e("Category", "CategoryQuestionFragment right card exit : " + OustSdkTools.optionSelected);
                    countText++;
                    if (optionData.size() == 1) {

                        String countShow = countText + "/" + totalOption;
                        if (countShow.contains("/")) {
                            String[] spilt = countShow.split("/");
                            Spannable spannable = new SpannableString(countShow);
                            spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), countShow.length(), 0);
                            category_optionsleft.setText(spannable);
                        } else {
                            category_optionsleft.setText(countShow);
                        }
//                        category_optionsleft.setText("1 " + getActivity().getResources().getString(R.string.card_left));
                        all_sorted.setVisibility(View.VISIBLE);
                    } else if (optionData.size() == 0) {
                        category_optionsleft.setText("0 " + getActivity().getResources().getString(R.string.card_left));

                        if (optionData.size() == 0 && i == 0) {
                            Log.e("Category", "CategoryQuestionFragment right card exit : about to go inside noCardsLeft");
                            noCardsLeft();
                        }
                    } else {
                        String countShow = countText + "/" + totalOption;
                        if (countShow.contains("/")) {
                            String[] spilt = countShow.split("/");
                            Spannable spannable = new SpannableString(countShow);
                            spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), countShow.length(), 0);
                            category_optionsleft.setText(spannable);
                        } else {
                            category_optionsleft.setText(countShow);
                        }
//                        category_optionsleft.setText(optionData.size() + " " + getActivity().getResources().getString(R.string.cards_left));
                    }

                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    if (optionData.size() == 0 && i == 0) {
                        Log.e("Category", "CategoryQuestionFragment onAdapterAboutToEmpty : about to go inside noCardsLeft");
                        noCardsLeft();
                    }
                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void noCardsLeft() {
        swipeCardView.setVisibility(View.GONE);
        category_optionsleft.setVisibility(View.GONE);
        i++;
        calculatePoints(false);

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
        try {
            File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            tempMp3.deleteOnExit();
            //cancelSound();
            answerChosenPlayer = new MediaPlayer();
            answerChosenPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            answerChosenPlayer.setDataSource(fis.getFD());
            answerChosenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            answerChosenPlayer.prepare();
            answerChosenPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setcategories(List<DTOQuestionOptionCategory> categoryData) {
        try {
            if (categoryData != null) {
                if ((categoryData.size() > 0) && (categoryData.get(0).getType().equalsIgnoreCase("text"))) {
                    category_leftimage_layout.setVisibility(View.GONE);
                    category_lefttext_layout.setVisibility(View.VISIBLE);
                    category_lefttext_layout.setText(getStrtoChars(categoryData.get(0).getData()));
                } else if ((categoryData.size() > 0) && (categoryData.get(0).getType().equalsIgnoreCase("image"))) {
                    category_lefttext_layout.setVisibility(View.GONE);
                    category_leftimage_layout.setVisibility(View.VISIBLE);
                    if (categoryData.get(0).getData() != null && !categoryData.get(0).getData().isEmpty()) {
                        setImageOptionFromFile(categoryData.get(0).getData(), category_leftimage_layout);
                    } else
                        setImageOption(categoryData.get(0).getData(), category_leftimage_layout);
                }
                if ((categoryData.size() > 1) && (categoryData.get(1).getType().equalsIgnoreCase("text"))) {
                    category_rightimage_layout.setVisibility(View.GONE);
                    category_righttext_layout.setVisibility(View.VISIBLE);
                    category_righttext_layout.setText(getStrtoChars(categoryData.get(1).getData()));
                } else if ((categoryData.size() > 1) && (categoryData.get(1).getType().equalsIgnoreCase("image"))) {
                    category_righttext_layout.setVisibility(View.GONE);
                    category_rightimage_layout.setVisibility(View.VISIBLE);
                    if (categoryData.get(1).getData() != null && !categoryData.get(1).getData().isEmpty()) {
                        setImageOptionFromFile(categoryData.get(1).getData(), category_rightimage_layout);
                    } else
                        setImageOption(categoryData.get(1).getData(), category_rightimage_layout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageOption(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                imgView.setImageDrawable(gifFromBytes);
            }
        } catch (Exception e) {
            setImageOptionA(str, imgView);
        }
    }

    public void setImageOptionFromFile(String url2, ImageView imageView) {
        try {
            String url = url2;
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
            }
            //OustSdkTools.showToast("IMAGE FROM CDNPATH");
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                //  InputStream iStream =  OustSdkApplication.getContext().getContentResolver().openInputStream(uri);
                //  byte[] inputData = getBytes(iStream);
                GifDrawable gifFromBytes = new GifDrawable(getBytes(OustSdkApplication.getContext().getContentResolver().openInputStream(uri)));
                imageView.setImageDrawable(gifFromBytes);
            } else {
                if (OustSdkTools.checkInternetStatus())
                    Picasso.get().load(url2).into(imageView);
                else {
                    OustSdkTools.showToast(getContext().getString(R.string.no_internet_connection));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            setNonGifImage(url2, imageView);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void setNonGifImage(String url2, ImageView imageView) {
        String imageURL = url2;
        String url = url2;
        File file = null;
        Log.d(TAG, "setImageOptionUrl: " + url);
        if (url != null) {
            url = OustMediaTools.removeAwsOrCDnUrl(url);
            //OustSdkTools.showToast("IMAGE FROM CDNPATH imageoptionurl");
            if (!url.contains("oustlearn_")) {
                url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            }
            file = new File(OustSdkApplication.getContext().getFilesDir(), url);
        }
        if (file != null && file.exists()) {
            Uri uri = Uri.fromFile(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (options.outWidth != -1 && options.outHeight != -1) {
                Log.d(TAG, "setImageOptionUrl: this is proper image");
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(imageURL).into(imageView);
                    if (file.exists()) {
                        file.delete();
                    }
                    downLoad(imageURL);
                } else {
                    Picasso.get().load(uri).into(imageView);
                }
            } else {
                Log.d(TAG, "setImageOptionUrl: this is not proper image");
                Picasso.get().load(imageURL).into(imageView);
                if (file.exists()) {
                    file.delete();
                }
                downLoad(imageURL);
            }
        } else {
            if (OustSdkTools.checkInternetStatus())
                Picasso.get().load(url2).into(imageView);
            else {
                OustSdkTools.showToast(getContext().getString(R.string.no_internet_connection));
            }
        }
    }

    public void setImageOptionA(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downLoad(String url) {

        Log.d(TAG, "downLoad: Media:" + CLOUD_FRONT_BASE_PATH);
        Log.d(TAG, "downLoad: Media:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
        try {

            DownloadFiles downloadFiles;
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                    //showDownloadProgress();
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
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

            String path = requireActivity().getFilesDir() + "/";
            String filename = OustMediaTools.getMediaFileName(url);
            downloadFiles.startDownLoad(url, path, filename, true, false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setQuestionData(DTOQuestions questions) {
        try {
            if (questions != null) {
                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.setQuestions(questions);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
            try {
                if (mainCourseCardClass == null) {
                    mainCourseCardClass = courseCardClass;
                }

                if (courseCardClass.getChildCard() != null) {
                    solutionText = courseCardClass.getChildCard().getContent();
                }

                if (courseCardClass.getQuestionData() != null) {
                    showSolution = courseCardClass.getQuestionData().isShowSolution();
                    isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
                    containSubjective = courseCardClass.getQuestionData().isContainSubjective();
                    if (courseCardClass.getQuestionData().getSolution() != null && !courseCardClass.getQuestionData().getSolution().isEmpty()) {
                        solutionText = courseCardClass.getQuestionData().getSolution();
                    }
                }
                if (mainCourseCardClass != null && mainCourseCardClass.getXp() == 0) {
                    mainCourseCardClass.setXp(100);
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
        } else if (isAssessmentQuestion) {
            mainCourseCardClass.setXp(questionXp);
        }
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
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

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;

    }

    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setVideoOverlay(boolean videoOverlay) {
        this.videoOverlay = videoOverlay;
    }

    private boolean isVideoOverlay() {
        return videoOverlay;
    }

    private boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }

    public void calculatePoints(boolean timeout) {
        try {
            boolean categoryRight = false;
            if (courseContentHandlingInterface != null) {
                courseContentHandlingInterface.cancelTimer();
            }
            if (!timeout) {
                if (!isAssessmentQuestion) {
                    if (OustSdkTools.totalAttempt < (totalOption + 1)) {
                        finalScr = (int) mainCourseCardClass.getXp();
                    } else {
                        int totalXp = (int) mainCourseCardClass.getXp();
                        finalScr = totalXp - ((10 * (OustSdkTools.totalAttempt - (OustSdkTools.optionSelected))));
                    }
                    if (finalScr < 0) {
                        finalScr = 0;
                    }
                    if (courseContentHandlingInterface != null) {
                        courseContentHandlingInterface.cancelTimer();
                    }
                    if ((questions.isContainSubjective())) {
                        isTimerEnd = true;
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.disableNextArrow();
                        }
                        showSubjectiveQuestionPopup(finalScr, isCourseQuestionIsRight, timeout);
                    } else {
                        isTimerEnd = true;
                        if (finalScr == 0) {
                            answerSubmit(questions.getAnswer(), finalScr, timeout, false, "");
                        } else {
                            answerSubmit(questions.getAnswer(), finalScr, timeout, true, "");
                        }
                    }
                } else {
                    if (OustSdkTools.totalAttempt == totalOption && OustSdkTools.totalAttempt == OustSdkTools.totalCategoryRight) {
                        categoryRight = true;
                    }
                    float score = 0;
                    if (OustSdkTools.totalCategoryRight != 0) {
                        float rightWeight = ((float) questionXp / (float) totalOption);
                        float weight = ((float) questionXp / (float) (totalOption * 2));
                        score = ((rightWeight * OustSdkTools.totalCategoryRight) - (weight * (OustSdkTools.totalAttempt - (OustSdkTools.totalCategoryRight))));
                        finalScr = Math.round(score);
                        if (finalScr < 0)
                            finalScr = 0;
                        if (finalScr > questionXp)
                            finalScr = (int) questionXp;
                    }

                    if (!categoryRight && OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
                        finalScr = 0;
                        answerSubmit("", 0, timeout, false, "");
                    } else {
                        answerSubmit("", Math.round(score), timeout, categoryRight, "");
                    }
                }
            } else {
                isTimerEnd = true;
                if (OustSdkTools.totalAttempt == totalOption && OustSdkTools.totalAttempt == OustSdkTools.totalCategoryRight) {
                    categoryRight = true;
                }
                float weight = (float) mainCourseCardClass.getXp() / (totalOption);
                float rightVal = weight * OustSdkTools.optionSelected;
                finalScr = Math.round(rightVal - ((10 * (OustSdkTools.totalAttempt - (OustSdkTools.optionSelected)))));

                if (finalScr < 0) {
                    finalScr = 0;
                }
                isCourseQuestionIsRight = false;
                answerSubmit(questions.getAnswer(), finalScr, timeout, categoryRight, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSubjectiveQuestionPopup(int finalScr, boolean isCourseQuestionIsRight, boolean timeout) {
        try {
            justifyPopupConstraintLayout.setVisibility(View.VISIBLE);
            learningModuleInterface.disableBackButton(true);

            if ((questions.getSubjectiveQuestion() != null) && (!questions.getSubjectiveQuestion().isEmpty())) {
                justifyPopupHeader.setText(questions.getSubjectiveQuestion());
                justifyPopupHeader.setVisibility(View.VISIBLE);
            } else {
                justifyPopupHeader.setText(OustStrings.getString("explain_your_rationale"));
            }
            maxWordsCount = questions.getMaxWordCount();
            if (maxWordsCount == 0) {
                maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
            }

            justifyPopupEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    justifyPopupEditText.setHint("");
                }
            });
            justifyPopupEditText.setOnClickListener(view -> justifyPopupEditText.setHint(""));

            justifyPopupLimitText.setText(OustStrings.getString("words_left") + (maxWordsCount));
            justifyPopupEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String str = OustSdkTools.getEmojiEncodedString(justifyPopupEditText);
                    String[] words = str.split(" ");
                    if (words.length < maxWordsCount) {
                        justifyPopupLimitText.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                        if (words.length >= minWordsCount) {
                            setJustifyPopupButtonColor(color, true);
                        } else {
                            setJustifyPopupButtonColor(getResources().getColor(R.color.overlay_container), false);
                        }
                        if ((str.isEmpty())) {
                            justifyPopupLimitText.setText(OustStrings.getString("words_left") + (maxWordsCount));
                        } else {
                            justifyPopupLimitText.setText(OustStrings.getString("words_left") + (maxWordsCount - (words.length)));
                        }
                    } else {
                        setCharLimit(justifyPopupEditText, justifyPopupEditText.getText().length());
                        setJustifyPopupButtonColor(color, true);
                        justifyPopupLimitText.setTextColor(OustSdkTools.getColorBack(R.color.reda));
                        justifyPopupLimitText.setText(OustStrings.getString("words_left") + ": 0");
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            justifyPopupActionButton.setOnClickListener(view -> {
                try {
                    String str = OustSdkTools.getEmojiEncodedString(justifyPopupEditText);
                    String[] words = str.split(" ");
                    if ((words.length < minWordsCount)) {
                    } else if ((words.length <= maxWordsCount)) {
                        learningModuleInterface.disableBackButton(false);
                        justifyPopupConstraintLayout.setVisibility(View.GONE);
                        hideKeyboard(justifyPopupEditText);
                        Log.e("TAG", "showSubjectiveQuestionPopup: " + justifyPopupEditText.getText().toString());
                        if (finalScr == 0) {
                            answerSubmit(questions.getAnswer(), finalScr, timeout, false, OustSdkTools.getEmojiEncodedString(justifyPopupEditText));
                        } else {
                            answerSubmit(questions.getAnswer(), finalScr, timeout, true, OustSdkTools.getEmojiEncodedString(justifyPopupEditText));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

            justifyPopupCloseIcon.setOnClickListener(view -> {
                try {
                    learningModuleInterface.disableBackButton(false);
                    justifyPopupConstraintLayout.setVisibility(View.GONE);
                    hideKeyboard(justifyPopupEditText);
                    if (finalScr == 0) {
                        answerSubmit(questions.getAnswer(), finalScr, timeout, false, "");
                    } else {
                        answerSubmit(questions.getAnswer(), finalScr, timeout, true, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });
        } catch (
                Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCharLimit(EditText justifyPopupEditText, int length) {
        try {
            InputFilter inputFilter = new InputFilter.LengthFilter(length);
            justifyPopupEditText.setFilters(new InputFilter[]{inputFilter});
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideKeyboard(View v) {
        try {
            if (getActivity() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setJustifyPopupButtonColor(int color, boolean isEnabled) {
        try {
            Drawable actionDrawable = Objects.requireNonNull(requireActivity()).getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(actionDrawable),
                    color
            );
            justifyPopupActionButton.setBackground(actionDrawable);
            justifyPopupActionButton.setEnabled(isEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rightAndWrongQuestionAnswer(int finalScr, boolean timeOut, String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            if (!zeroXpForQCard) {
                if (finalScr == 0) {
                    question_result_image.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                }
            } else {
                coinsAnimLayout.setVisibility(View.GONE);
            }
            if (timeOut) {
                rightAnswerSound();
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
                    wrongAnswerSound();
                    handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
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
            if (!isVideoOverlay()) {
                handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getStrtoChars(String data) {
        StringBuilder result = new StringBuilder();
        if (data != null && data.length() > 0) {
            for (int i = 0; i < data.length(); i++) {
                result.append(data.charAt(i)).append("\n");
            }
        }
        return result.toString();
    }


    private void answerSubmit(String answer, int oc, boolean isTimeOut, boolean isCorrect, String subjectiveResponse) {
        try {
            if (isReviewMode) {
                courseContentHandlingInterface.cancelTimer();
            }

            if (isCourseQuestion) {
                if (isVideoOverlay()) {
                    if (learningModuleInterface != null)
                        learningModuleInterface.setVideoOverlayAnswerAndOc(answer, subjectiveResponse, oc, isCorrect, 0, cardId);
                    new Handler().postDelayed(() -> {
                        learningModuleInterface.closeChildFragment();
                    }, 1500);
                }

                if (questions.isThumbsUpDn()) {
                    rightAndWrongQuestionAnswer(finalScr, isCorrect, answer, subjectiveResponse, oc, isCourseQuestionIsRight, 0);
                } else {
                    if (isVideoOverlay()) {
                        if (learningModuleInterface != null)
                            learningModuleInterface.closeChildFragment();
                    } else {
                        if (learningModuleInterface != null) {
                            if (isTimeOut) {
                                showSolutionAnswer = false;
                            }
                            handleNextQuestion(answer, subjectiveResponse, oc, isCourseQuestionIsRight, 0);
                        }
                    }
                }
            } else if (isAssessmentQuestion) {
                try {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.setAnswerAndOc(answer, "", (int) oc, isCorrect, 0);
                        handleNextQuestion("", "", 0, false, 0);
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

    private void wrongAnswerSound() {
        try {
            stopMediaPlayer();
            playAudio("answer_incorrect.mp3");
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

    public void setReplayMode(boolean isReplayMode) {
        this.isReplayMode = isReplayMode;
    }

    public static class FlingCardListenerNew implements View.OnTouchListener {

        private final int INVALID_POINTER_ID = -1;

        private final float objectX;
        private final float objectY;
        private final int objectH;
        private final int objectW;
        private final int parentWidth;
        private final FlingListener mFlingListener;

        private final Object dataObject;
        private final float halfWidth;
        private float BASE_ROTATION_DEGREES;

        private float aPosX;
        private float aPosY;
        private float aDownTouchX;
        private float aDownTouchY;

        // The active pointer is the one currently moving our object.
        private int mActivePointerId = INVALID_POINTER_ID;
        private View frame = null;


        private final int TOUCH_ABOVE = 0;
        private final int TOUCH_BELOW = 1;
        private int touchPosition;
        private final Object obj = new Object();
        boolean isAnimationRunning = false;
        float MAX_COS = (float) Math.cos(Math.toRadians(45));
        boolean isCardMoving = false;


//    public FlingCardListener(View frame, Object itemAtPosition, FlingListener flingListener) {
//        this(frame, itemAtPosition, 15f, flingListener);
//    }

        public FlingCardListenerNew(View frame, Object itemAtPosition, float rotation_degrees, FlingListener flingListener) {
            super();
            this.frame = frame;
            this.objectX = frame.getX();
            this.objectY = frame.getY();
            this.objectH = frame.getHeight();
            this.objectW = frame.getWidth();
            this.halfWidth = objectW / 2f;
            this.dataObject = itemAtPosition;
            this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
            this.BASE_ROTATION_DEGREES = rotation_degrees;
            this.mFlingListener = flingListener;
        }

        public boolean onTouch(View view, MotionEvent event) {
            try {
                int action = event.getAction();
                if (isTimerEnd) {
                    return true;
                }
                if ((action == MotionEvent.ACTION_DOWN) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN)) {
                    // Save the ID of this pointer
                    isCardMoving = true;
                    mActivePointerId = event.getPointerId(0);
                    Log.d("Categoryquestion mAct", "" + mActivePointerId);
                    float x = 0;
                    float y = 0;
                    boolean success = false;
                    try {
                        x = event.getX(mActivePointerId);
                        y = event.getY(mActivePointerId);
                        Log.d("Categoryquestion X", "" + x);
                        Log.d("Categoryquestion Y", "" + y);
                        success = true;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if (success) {
                        // Remember where we started
                        aDownTouchX = x;
                        aDownTouchY = y;
                        //to prevent an initial jump of the magnifier, aposX and aPosY must
                        //have the values from the magnifier frame
                        if (aPosX == 0) {
                            aPosX = frame.getX();
                            Log.d("Categoryquestion aPosX", "" + aPosX);
                        }
                        if (aPosY == 0) {
                            aPosY = frame.getY();
                            Log.d("Categoryquestion aPosY", "" + aPosY);
                        }

                        if (y < objectH / 2) {
                            touchPosition = TOUCH_ABOVE;
                            Log.d("Categoryquestion tPosA", "" + touchPosition);
                        } else {
                            touchPosition = TOUCH_BELOW;
                            Log.d("Categoryquestion tPosB", "" + touchPosition);
                        }
                    }

                    view.getParent().requestDisallowInterceptTouchEvent(true);
                } else if ((action == MotionEvent.ACTION_UP) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP)) {
                    mActivePointerId = INVALID_POINTER_ID;
                    isCardMoving = false;
                    resetCardViewOnStack();
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                } else if ((action == MotionEvent.ACTION_POINTER_DOWN) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_DOWN)) {
                } else if ((action == MotionEvent.ACTION_POINTER_UP) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_UP)) {
                    // Extract the index of the pointer that left the touch sensor
                    final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mActivePointerId = event.getPointerId(newPointerIndex);
                        Log.d("Categoryquestion mAPID", "" + mActivePointerId);
                    }
                } else if ((action == MotionEvent.ACTION_MOVE) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_MOVE)) {

                    // Find the index of the active pointer and fetch its position
                    final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    final float xMove = event.getX(pointerIndexMove);
                    final float yMove = event.getY(pointerIndexMove);

                    //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                    // Calculate the distance moved
                    final float dx = xMove - aDownTouchX;
                    final float dy = yMove - aDownTouchY;


                    // Move the frame
                    aPosX += dx;
                    aPosY += dy;

                    // calculate the rotation degrees
                    float distobjectX = aPosX - objectX;
                    float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                    if (touchPosition == TOUCH_BELOW) {
                        rotation = -rotation;
                    }

                    //in this area would be code for doing something with the view as the frame moves.
                    frame.setX(aPosX);
                    frame.setY(aPosY);
                    frame.setRotation(rotation);
                    mFlingListener.onScroll(getScrollProgressPercent());
                } else if ((action == MotionEvent.ACTION_CANCEL) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_CANCEL)) {
                    mActivePointerId = INVALID_POINTER_ID;
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    isCardMoving = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            return true;
        }

        private float getScrollProgressPercent() {
            if (movedBeyondLeftBorder()) {
                return -1f;
            } else if (movedBeyondRightBorder()) {
                return 1f;
            } else {
                float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
                return zeroToOneValue * 2f - 1f;
            }
        }

        private boolean resetCardViewOnStack() {
            if (movedBeyondLeftBorder() || movedBeyondRightBorder()) {
                Log.e("Category", "resetCardViewOnStack() left or right swipe");
                OustSdkTools.totalAttempt++;
            }
            if (movedBeyondLeftBorder()) {
                // Left Swipe
                if (OustSdkTools.isAssessmentQuestion) {
                    onSelected(true, getExitPoint(-objectW), 100);
                    mFlingListener.onScroll(-1.0f);
                    if ((OustSdkTools.categoryData != null && OustSdkTools.categoryData.size() > 0) && (OustSdkTools.optionData != null && OustSdkTools.optionData.size() > 0)) {
                        OustSdkTools.optionSelected++;
                        if (OustSdkTools.categoryData.get(0).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            OustSdkTools.totalCategoryRight++;
                        }
                    }
                } else {
                    if ((OustSdkTools.categoryData != null && OustSdkTools.categoryData.size() > 0) && (OustSdkTools.optionData != null && OustSdkTools.optionData.size() > 0)) {
                        if (OustSdkTools.categoryData.get(0).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            onSelected(true, getExitPoint(-objectW), 100);
                            mFlingListener.onScroll(-1.0f);
                            OustSdkTools.optionSelected++;
                        } else {
                            wrongAnswer();
                        }
                    }
                }

            } else if (movedBeyondRightBorder()) {
                // Right Swipe
                if (OustSdkTools.isAssessmentQuestion) {
                    onSelected(false, getExitPoint(parentWidth), 100);
                    mFlingListener.onScroll(1.0f);
                    if ((OustSdkTools.categoryData != null && OustSdkTools.categoryData.size() > 0) && (OustSdkTools.optionData != null && OustSdkTools.optionData.size() > 0)) {
                        OustSdkTools.optionSelected++;
                        if (OustSdkTools.categoryData.get(1).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            OustSdkTools.totalCategoryRight++;
                        }
                    }
                } else {
                    if ((OustSdkTools.categoryData != null && OustSdkTools.categoryData.size() > 0) && (OustSdkTools.optionData != null && OustSdkTools.optionData.size() > 0)) {
                        if (OustSdkTools.categoryData.get(1).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            onSelected(false, getExitPoint(parentWidth), 100);
                            mFlingListener.onScroll(1.0f);
                            OustSdkTools.optionSelected++;
                        } else {
                            wrongAnswer();
                        }
                    }
                }

            } else {
                wrongAnswer();
            }
            return false;
        }

        private void wrongAnswer() {
            wrongAnswerSound();
            vibrateandShake(frame);
            Log.e("Category", "resetCardViewOnStack() wrong attempt");
            float abslMoveDistance = Math.abs(aPosX - objectX);
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.animate()
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .x(objectX)
                    .y(objectY)
                    .rotation(0);
            isCourseQuestionIsRight = false;
            mFlingListener.onScroll(0.0f);
            if (abslMoveDistance < 4.0) {
                mFlingListener.onClick(dataObject);
            }
        }

        private boolean movedBeyondLeftBorder() {
            return aPosX + halfWidth < leftBorder();
        }

        private boolean movedBeyondRightBorder() {
            return aPosX + halfWidth > rightBorder();
        }


        public float leftBorder() {
            return parentWidth / 4.f;
        }

        public float rightBorder() {
            return 3 * parentWidth / 4.f;
        }


        public void onSelected(final boolean isLeft, float exitY, long duration) {
            isAnimationRunning = true;
            float exitX;
            if (isLeft) {
                exitX = -objectW - getRotationWidthOffset();
            } else {
                exitX = parentWidth + getRotationWidthOffset();
            }
            this.frame.animate()
                    .setDuration(duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .x(exitX)
                    .y(exitY)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (isLeft) {
                                Log.e("Category", "onSelected() isLeft : " + "true");
                                mFlingListener.onCardExited();
                                mFlingListener.leftExit(dataObject);
                            } else {
                                Log.e("Category", "onSelected() isLeft : " + "false");
                                mFlingListener.onCardExited();
                                mFlingListener.rightExit(dataObject);
                            }
                            isAnimationRunning = false;
                        }
                    })
                    .rotation(getExitRotation(isLeft));
        }


        /**
         * Starts a default left exit animation.
         */
        public void selectLeft() {
            if (!isAnimationRunning)
                onSelected(true, objectY, 200);
        }

        /**
         * Starts a default right exit animation.
         */
        public void selectRight() {
            if (!isAnimationRunning)
                onSelected(false, objectY, 200);
        }


        private float getExitPoint(int exitXPoint) {
            float[] x = new float[2];
            x[0] = objectX;
            x[1] = aPosX;

            float[] y = new float[2];
            y[0] = objectY;
            y[1] = aPosY;

            LinearRegression regression = new LinearRegression(x, y);

            //Your typical y = ax+b linear regression
            return (float) regression.slope() * exitXPoint + (float) regression.intercept();
        }

        private float getExitRotation(boolean isLeft) {
            float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX) / parentWidth;
            if (touchPosition == TOUCH_BELOW) {
                rotation = -rotation;
            }
            if (isLeft) {
                rotation = -rotation;
            }
            return rotation;
        }


        /**
         * When the object rotates it's width becomes bigger.
         * The maximum width is at 45 degrees.
         * <p/>
         * The below method calculates the width offset of the rotation.
         */
        private float getRotationWidthOffset() {
            return objectW / MAX_COS - objectW;
        }


        public void setRotationDegrees(float degrees) {
            this.BASE_ROTATION_DEGREES = degrees;
        }

        public boolean isTouching() {
            return this.mActivePointerId != INVALID_POINTER_ID;
        }

        public PointF getLastPoint() {
            return new PointF(this.aPosX, this.aPosY);
        }

        public void wrongAnswerSound() {
            try {
                playAudio1("answer_incorrect.mp3");
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }


        public void vibrateandShake(View v) {
            try {
                Animation shakeAnim = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.shakescreen_anim);
                v.startAnimation(shakeAnim);
                ((Vibrator) OustSdkApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
            } catch (Exception e) {
                //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
            }
        }

        private void playAudio1(final String filename) {
            try {
                File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                tempMp3.deleteOnExit();
                //cancelSound();
                MediaPlayer answerChosenPlayer = new MediaPlayer();
                answerChosenPlayer.reset();
                FileInputStream fis = new FileInputStream(tempMp3);
                answerChosenPlayer.setDataSource(fis.getFD());
                answerChosenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                answerChosenPlayer.prepare();
                answerChosenPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }

    }
}