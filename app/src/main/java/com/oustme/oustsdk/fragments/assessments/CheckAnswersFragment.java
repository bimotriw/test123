package com.oustme.oustsdk.fragments.assessments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.adapter.assessments.CommentsAdapter;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.customviews.QuestionScalledImage;
import com.oustme.oustsdk.customviews.ScaleImageView;
import com.oustme.oustsdk.firebase.assessment.Comments;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.presenter.assessments.CheckAnswerFragmentPresenter;
import com.oustme.oustsdk.request.AddCommentRequest;
import com.oustme.oustsdk.request.QuestionFavouriteRequest;
import com.oustme.oustsdk.request.QuestionFeedBackRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.QuestionFeedBackResponse;
import com.oustme.oustsdk.response.assessment.UserGamePoints;
import com.oustme.oustsdk.response.common.OustFillData;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustCommonUtils;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

public class CheckAnswersFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout baseLayout,srcLayout,figureLayout,shareBtnLayout,opponentBackgroundLayout,
            challengerBackgroundLayout,webviewbackground,closeLayout,commentsDataLayout,commentBoxLayout,
            commentlist_layout,addsolution_layout,commentsLayout,shareBtnsLayout,favouriteLayout,
            likeLayout,unLikeLayout;
    private LinearLayout leftArrowLayout,rightArrowLayout;
    private Button optionA,optionB,optionC,optionD,optionE,optionF,optionG,optionH;
    private ScaleImageView imgQuestion;
    private ImageButton favouriteButton,likeButton,unLikeButton,closePageButton
            ,imgLike,imgFavourite,imgUnLike,shareImage;
    private TextView txtQuestion,txtSrc,txtSource,txtShare,txtFav,txtTimerSingle,txtTimerAssemmsnet,
            txtChallengerName,txtOpponentName,txtTopic,txtTimer,txtTimer1,txtChallengerScore,
            txtOpponentScore,txtChallengerAvatar,groupAvatar,txtLike,txtQuestionCount,
            txtUnLike,nocomment_text,txtComment,txtRegister;
    private CircleImageView challengerAvatarImg,opponentAvatarImg;
    private WebView solution_webview;
    private ScrollView scrollView;
    private RecyclerView commentRecyclerView;
    private EditText commentsTxtBox,commentsStartTxtBox;
    private ProgressBar loader_commentsData;
    private QuestionScalledImage imageques_optionA,imageques_optionB,imageques_optionC,imageques_optionD,
            imageques_optionE,imageques_optionF;
    private ImageView rightArrow1,imgComment,registerImage,addCommentBtn,addStartCommentBtn;
    private RelativeLayout fill_blanks_layout;

    private TextView  match_option_a_left_text, match_option_b_left_text,
            match_option_c_left_text, match_option_d_left_text, match_option_a_right_text,
            match_option_b_right_text, match_option_c_right_text, match_option_d_right_text;

    private ImageView match_option_a_left_image, match_option_b_left_image, match_option_c_left_image,
            match_option_d_left_image, match_option_a_right_image, match_option_b_right_image, match_option_c_right_image,
            match_option_d_right_image;

    private RelativeLayout match_option_a_left_layout, match_option_b_left_layout, match_option_c_left_layout,
            match_option_d_left_layout, match_option_a_right_layout, match_option_b_right_layout,
            match_option_c_right_layout, match_option_d_right_layout, matchfollowing_layout;



    CheckBox grammaticalErrorCBox, incorrectAnswersCBox, incorrectQuestionCBox;
    String grammaticalErrorText = "", incorrectAnswersText = "", incorrectQuestionText = "";

    private ActiveUser activeUser;

    private PopupWindow sharepopup;

    private Button submitButton;
    private ImageButton closeButton;
    private PopupWindow unLikePopUp;
    private String txtSolution;
    private FirebaseRefClass usercommentsListenerClass;
    private CommentsAdapter commentsAdapter;
    private CheckAnswerFragmentPresenter checkAnswerFragmentPresenter;
    private boolean isCommentsShowing=false;

    int scrWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.checkanswer, container, false);
            initViews(view);
            initCheckAnswersView();
            return view;
    }

    private void initViews(View view) {
        try {
            baseLayout = view.findViewById(R.id.baseLayout);
            leftArrowLayout = view.findViewById(R.id.leftArrowLayout);
            rightArrowLayout = view.findViewById(R.id.rightArrowLayout);
            srcLayout = view.findViewById(R.id.srcLayout);
            optionA = view.findViewById(R.id.optionA);
            optionB = view.findViewById(R.id.optionB);
            optionC = view.findViewById(R.id.optionC);
            optionD = view.findViewById(R.id.optionD);
            optionE = view.findViewById(R.id.optionE);
            optionF = view.findViewById(R.id.optionF);
            optionG = view.findViewById(R.id.optionG);
            optionH = view.findViewById(R.id.optionH);
            imgQuestion = view.findViewById(R.id.imgQuestion);
            figureLayout = view.findViewById(R.id.figureLayout);
            shareBtnLayout = view.findViewById(R.id.shareBtnsLayout);
            favouriteButton = view.findViewById(R.id.imgFavourite);
            likeButton = view.findViewById(R.id.imgLike);
            unLikeButton = view.findViewById(R.id.imgUnLike);
            shareImage= view.findViewById(R.id.shareImage);
            txtQuestionCount = view.findViewById(R.id.txtQuestionCountSingle);
            txtQuestion = view.findViewById(R.id.txtQuestion);
            txtSrc = view.findViewById(R.id.src);
            txtSource = view.findViewById(R.id.txtSource);
            txtShare = view.findViewById(R.id.txtShare);
            txtFav = view.findViewById(R.id.txtFav);
            txtTimerSingle = view.findViewById(R.id.txtTimerSingle);
            txtTimerAssemmsnet = view.findViewById(R.id.txtTimerAssemmsnet);
            txtChallengerName = view.findViewById(R.id.challengerName);
            txtOpponentName = view.findViewById(R.id.opponentName);
            txtTopic = view.findViewById(R.id.topic);
            txtTimer = view.findViewById(R.id.txtTimer);
            txtTimer1 = view.findViewById(R.id.txtTimer1);
            txtChallengerScore = view.findViewById(R.id.challengerScore);
            txtOpponentScore = view.findViewById(R.id.opponentScore);
            challengerAvatarImg = view.findViewById(R.id.challengerAvatarImg);
            opponentAvatarImg = view.findViewById(R.id.opponentAvatarImg);
            groupAvatar = view.findViewById(R.id.groupAvatarImg);
            txtChallengerAvatar = view.findViewById(R.id.txtChallengerAvatar);
            opponentBackgroundLayout = view.findViewById(R.id.opponentBackgroundLayout);
            challengerBackgroundLayout = view.findViewById(R.id.challengerbackground);
            webviewbackground = view.findViewById(R.id.webviewbackground);
            solution_webview = view.findViewById(R.id.solution_webview);
            scrollView = view.findViewById(R.id.scrollView);
            closeLayout = view.findViewById(R.id.closeLayout);
            txtLike = view.findViewById(R.id.txtLike);
            txtUnLike = view.findViewById(R.id.txtUnLike);
            commentsDataLayout = view.findViewById(R.id.commentsDataLayout);
            commentRecyclerView = view.findViewById(R.id.commentsList);
            nocomment_text = view.findViewById(R.id.nocomment_text);
            txtComment = view.findViewById(R.id.txtComment);
            commentBoxLayout = view.findViewById(R.id.commentBoxLayout);
            commentsTxtBox = view.findViewById(R.id.commentsTxtBox);
            loader_commentsData = view.findViewById(R.id.loader_commentsData);
            commentlist_layout = view.findViewById(R.id.commentlist_layout);
            addsolution_layout = view.findViewById(R.id.addsolution_layout);
            commentsStartTxtBox = view.findViewById(R.id.commentsStartTxtBox);
            addStartCommentBtn = view.findViewById(R.id.addStartCommentBtn);
            commentsLayout = view.findViewById(R.id.commentsLayout);
            imageques_optionA = view.findViewById(R.id.imageques_optionA);
            imageques_optionB = view.findViewById(R.id.imageques_optionB);
            imageques_optionC = view.findViewById(R.id.imageques_optionC);
            imageques_optionD = view.findViewById(R.id.imageques_optionD);
            imageques_optionE = view.findViewById(R.id.imageques_optionE);
            imageques_optionF = view.findViewById(R.id.imageques_optionF);
            shareBtnsLayout = view.findViewById(R.id.shareBtnsLayout);
            favouriteLayout = view.findViewById(R.id.favouriteLayout);
            likeLayout = view.findViewById(R.id.likeLayout);
            unLikeLayout = view.findViewById(R.id.unLikeLayout);
            closePageButton = view.findViewById(R.id.closePageButton);
            addCommentBtn = view.findViewById(R.id.addCommentBtn);
            imgComment = view.findViewById(R.id.imgComment);
            imgLike = view.findViewById(R.id.imgLike);
            imgFavourite = view.findViewById(R.id.imgFavourite);
            imgUnLike = view.findViewById(R.id.imgUnLike);
            rightArrow1= view.findViewById(R.id.rightArrow1);
            registerImage= view.findViewById(R.id.registerImage);
            fill_blanks_layout= view.findViewById(R.id.fill_blanks_layout);

            matchfollowing_layout = view.findViewById(R.id.matchfollowing_layout);
            match_option_a_left_text = view.findViewById(R.id.match_option_a_left_text);
            match_option_b_left_text = view.findViewById(R.id.match_option_b_left_text);
            match_option_c_left_text = view.findViewById(R.id.match_option_c_left_text);
            match_option_d_left_text = view.findViewById(R.id.match_option_d_left_text);
            match_option_a_right_text = view.findViewById(R.id.match_option_a_right_text);
            match_option_b_right_text = view.findViewById(R.id.match_option_b_right_text);
            match_option_c_right_text = view.findViewById(R.id.match_option_c_right_text);
            match_option_d_right_text = view.findViewById(R.id.match_option_d_right_text);
            match_option_a_left_image = view.findViewById(R.id.match_option_a_left_image);
            match_option_b_left_image = view.findViewById(R.id.match_option_b_left_image);
            match_option_c_left_image = view.findViewById(R.id.match_option_c_left_image);
            match_option_d_left_image = view.findViewById(R.id.match_option_d_left_image);
            match_option_a_right_image = view.findViewById(R.id.match_option_a_right_image);
            match_option_b_right_image = view.findViewById(R.id.match_option_b_right_image);
            match_option_c_right_image = view.findViewById(R.id.match_option_c_right_image);
            match_option_d_right_image = view.findViewById(R.id.match_option_d_right_image);
            match_option_a_left_layout = view.findViewById(R.id.match_option_a_left_layout);
            match_option_b_left_layout = view.findViewById(R.id.match_option_b_left_layout);
            match_option_c_left_layout = view.findViewById(R.id.match_option_c_left_layout);
            match_option_d_left_layout = view.findViewById(R.id.match_option_d_left_layout);
            match_option_a_right_layout = view.findViewById(R.id.match_option_a_right_layout);
            match_option_b_right_layout = view.findViewById(R.id.match_option_b_right_layout);
            match_option_c_right_layout = view.findViewById(R.id.match_option_c_right_layout);
            match_option_d_right_layout = view.findViewById(R.id.match_option_d_right_layout);

            OustSdkTools.setImage(shareImage, OustSdkApplication.getContext().getResources().getString(R.string.forward_btn));
            OustSdkTools.setImage(rightArrow1,getActivity().getResources().getString(R.string.arrowforward));
            OustSdkTools.setImage(imgComment,getActivity().getResources().getString(R.string.solutions));
            OustSdkTools.setImage(registerImage,getActivity().getResources().getString(R.string.share_text));
            OustSdkTools.setImage(addCommentBtn,getActivity().getResources().getString(R.string.send));
            OustSdkTools.setImage(addStartCommentBtn,getActivity().getResources().getString(R.string.send));
            OustSdkTools.setImage(addStartCommentBtn,getActivity().getResources().getString(R.string.share_text));
            commentsStartTxtBox.setHint(getResources().getString(R.string.addsolutionhind_text));
            commentsTxtBox.setHint((getResources().getString(R.string.addsolutionhind_text)));
            txtComment.setText(OustStrings.getString("solutionTxt"));
//            submitButton= (Button) view.findViewById(R.id.submitButton);
//            closeButton= (ImageButton) view.findViewById(R.id.closeButton);

//            submitButton.setOnClickListener(this);
//            closeButton.setOnClickListener(this);
            shareBtnsLayout.setOnClickListener(this);
            shareImage.setOnClickListener(this);
            favouriteLayout.setOnClickListener(this);
            likeLayout.setOnClickListener(this);
            unLikeLayout.setOnClickListener(this);
            commentsLayout.setOnClickListener(this);
            opponentBackgroundLayout.setOnClickListener(this);
            closePageButton.setOnClickListener(this);
            addCommentBtn.setOnClickListener(this);
            addStartCommentBtn.setOnClickListener(this);
            imgComment.setOnClickListener(this);
            imgLike.setOnClickListener(this);
            imgFavourite.setOnClickListener(this);
            imgUnLike.setOnClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public void initCheckAnswersView() {
        try {
            Bundle bundle = getArguments();
            String message = bundle.getString("EXTRA_MESSAGE");
            int position = Integer.parseInt(message);
            activeUser = OustAppState.getInstance().getActiveUser();
            Gson gson = new GsonBuilder().create();
            ActiveGame activeGame =gson.fromJson(bundle.getString("activeGame"),ActiveGame.class);
            SubmitRequest submitRequest = gson.fromJson(bundle.getString("submitRequest"),SubmitRequest.class);
            GamePoints gamePoints = gson.fromJson(bundle.getString("gamePoints"),GamePoints.class);
            UserGamePoints userGamePoints = gson.fromJson(bundle.getString("userGamePoints"),UserGamePoints.class);
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            scrWidth = metrics.widthPixels;
            DTOQuestions questions=new DTOQuestions();
            int totalQuestions=0;
            if((OustAppState.getInstance().getPlayResponse().getqIdList()!=null)&&(OustAppState.getInstance().getPlayResponse().getqIdList().size()>0)){
                questions= OustDBBuilder.getQuestionById(OustAppState.getInstance().getPlayResponse().getqIdList().get(position));
                totalQuestions=OustAppState.getInstance().getPlayResponse().getqIdList().size();
            }else {
                questions = OustAppState.getInstance().getPlayResponse().getQuestionsByIndex(position);
                totalQuestions=OustAppState.getInstance().getPlayResponse().getQuestions().length;
            }
            checkAnswerFragmentPresenter=new CheckAnswerFragmentPresenter(this,position,activeUser,activeGame,questions, submitRequest,userGamePoints,gamePoints,totalQuestions);
            checkAnswerFragmentPresenter.setStartingData();
            setTextStyle();
            createBackButton();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    private void createBackButton(){
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(isCommentsShowing) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        startCommentHideAnimation();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setTextStyle(){
        try {
            closeLayout.bringToFront();
            txtFav.setText(OustStrings.getString("favTxt"));
            txtLike.setText(OustStrings.getString("likeTxt"));
            txtUnLike.setText(OustStrings.getString("unlikeTxt"));
            txtSrc.setText(OustStrings.getString("src"));
            txtRegister.setText(getResources().getString(R.string.register_text));
            txtShare.setText(OustStrings.getString("askFriendTxt"));
            txtComment.setText(OustStrings.getString("solutionTxt"));
            commentsStartTxtBox.setHint(getResources().getString(R.string.addsolutionhind_text));
            commentsTxtBox.setHint(getResources().getString(R.string.addsolutionhind_text));
        }catch (Exception e){

        }
    }

    public void setQuestion(String question1){
        try {
            Spanned question = Html.fromHtml(question1);
            txtQuestion.setText(question);
        }catch (Exception e){}
    }
    public void setQuestionCount(String str){
        txtQuestionCount.setText(str);
    }

    public void showTopRightArrorw(){
        rightArrowLayout.setVisibility(View.VISIBLE);
        leftArrowLayout.setVisibility(View.GONE);
    }
    public void showTopLeftArrow(){
        leftArrowLayout.setVisibility(View.VISIBLE);
        rightArrowLayout.setVisibility(View.GONE);
    }
    public void showBothArrow(){
        leftArrowLayout.setVisibility(View.VISIBLE);
        rightArrowLayout.setVisibility(View.VISIBLE);
    }

    public void showSolutionLayout(String str){
        try {
            this.txtSolution = str;
            shareBtnLayout.setVisibility(View.VISIBLE);
        }catch (Exception e){}
    }



    public void showDoubleUserInformation(){
        txtTimer.setVisibility(View.VISIBLE);
        txtTimer1.setVisibility(View.VISIBLE);
        txtTimerSingle.setVisibility(View.GONE);
    }

    public void setDoubleUserChallengerName(String name){
        txtChallengerName.setText(name);
    }

    public void setDoubleUserChellengerScore(long point){
        txtChallengerScore.setText(""+point);
    }

    public void setDoubleUserChallengerTimeText(String time){
        try {
            txtTimer.setText(OustCommonUtils.secondsToString(Integer.parseInt(String.valueOf(time))));
        }catch (Exception e){}
    }

    public void setDoubleUserTopicText(String topic){
        txtTopic.setText(topic);
    }

    public void setDoubleUserOppenentScore(long point){
        txtOpponentScore.setText(""+point);
    }

    public void setDoubleUserOppenentTimeText(String time){
        try {
            txtTimer1.setText(OustCommonUtils.secondsToString(Integer.parseInt(String.valueOf(time))));
        }catch (Exception e){}
    }

    public void setOpponentGroupAvatar(String name){
        groupAvatar.setVisibility(View.VISIBLE);
        OustSdkTools.setGroupsIconInActivity(groupAvatar,name);
    }

    public void setOpponenentAvatar(String avatar){
        try {
            opponentBackgroundLayout.setVisibility(View.VISIBLE);
            BitmapDrawable bd=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
            BitmapDrawable bd_loading=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
            Picasso.get().load(avatar)
                    .placeholder(bd_loading).error(bd)
                    .into(opponentAvatarImg);
        }catch (Exception e){}
    }

    public void setOppeonentName(String name){
        txtOpponentName.setVisibility(View.VISIBLE);
        txtOpponentName.setText(name);
    }

    public void setContactChallengerAvatar(String name){
        txtChallengerAvatar.setVisibility(View.VISIBLE);
        OustSdkTools.setGroupsIconInActivity(txtChallengerAvatar,name);
    }

    public void setChallengerAvatar(String avatar){
        try {
            challengerBackgroundLayout.setVisibility(View.VISIBLE);
            if (OustSdkTools.tempProfile == null) {
                Picasso.get().load(avatar).into(challengerAvatarImg);
            } else {
                challengerAvatarImg.setImageBitmap(OustSdkTools.tempProfile);
            }
        }catch (Exception e){}
    }


    public void showSingleUserTimer(){
        txtTimer.setVisibility(View.GONE);
        txtTimer1.setVisibility(View.GONE);
        txtTimerAssemmsnet.setVisibility(View.GONE);
        txtTimerSingle.setVisibility(View.VISIBLE);
    }
    public void showAssessmentTimer(){
        txtTimerAssemmsnet.setVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.GONE);
        txtTimer1.setVisibility(View.GONE);
        txtTimerSingle.setVisibility(View.GONE);
    }

    public void hideScoreForAssessment(){
        txtChallengerScore.setVisibility(View.GONE);
        commentsLayout.setVisibility(View.GONE);
        txtTimer1.setVisibility(View.GONE);
    }

    public void settxtTimerSingle(String timrStr){
        try {
            txtTimerSingle.setText(OustCommonUtils.secondsToString(Integer.parseInt(timrStr)));
        }catch (Exception e){}
    }

    public void settxtTimerAssessment(String timrStr){
        try {
            txtTimerAssemmsnet.setText(OustCommonUtils.secondsToString(Integer.parseInt(timrStr)));
        }catch (Exception e){}
    }

    public void showSourceLayout(String vender){
        try {
            srcLayout.setVisibility(View.VISIBLE);
            txtSrc.setVisibility(View.VISIBLE);
            txtSource.setVisibility(View.VISIBLE);
            txtSource.setText(vender);
        }catch (Exception e){}
    }
    public void hideSoureceLayout(){
        srcLayout.setVisibility(View.GONE);
        txtSrc.setVisibility(View.GONE);
        txtSource.setVisibility(View.GONE);
    }

    public void setImageForQuestion(String imageStr) {
        try {
            byte[] decodedString = Base64.decode(imageStr, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            figureLayout.setVisibility(View.VISIBLE);
            imgQuestion.setVisibility(View.VISIBLE);
            imgQuestion.setImageBitmap(decodedByte);
        }catch (Exception e){}
    }

    public void setLikeUnlike_notSet(){
        try {
            likeButton.setBackgroundResource(R.drawable.like);
            unLikeButton.setBackgroundResource(R.drawable.incorrect);
        }catch (Exception e){}
    }
    public void setLikeQuestionLayout(){
        try {
            likeButton.setBackgroundResource(R.drawable.like_green_icon);
            unLikeButton.setBackgroundResource(R.drawable.incorrect);
        }catch (Exception e){}
    }
    public void setUnlikeQuestionLayout(){
        try {
            unLikeButton.setBackgroundResource(R.drawable.unlike_red_icon);
            likeButton.setBackgroundResource(R.drawable.like);
        }catch (Exception e){}
    }

    public void setQuestionFavorite(){
        try {
            favouriteButton.setBackgroundResource(R.drawable.fill_heart);
        }catch (Exception e){}
    }

    public void setQuestionNotFavorite(){
        try {
            favouriteButton.setBackgroundResource(R.drawable.favorite);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //============================================================================================================
    //btn click
    @Override
    public void onClick(View view) {
        int id=view.getId();

        if(id== R.id.submitButton) {
            setSubmitButtonClick();
        }
        else if(id==R.id.closeButton) {
            if ((unLikePopUp != null) && (unLikePopUp.isShowing())) {
                unLikePopUp.dismiss();
            }
        }
        else if((id==R.id.shareBtnsLayout)|| (id==R.id.shareImage)) {
            OustSdkTools.oustTouchEffect(view, 100);
            shareButtonClick();
        }
        else if((id==R.id.favouriteLayout)||(id==R.id.imgFavourite)){
            OustSdkTools.oustTouchEffect(view,100);
            favouriteButtonClick();
        }
        else if ((id==R.id.likeLayout)||(id==R.id.imgLike)){
            OustSdkTools.oustTouchEffect(view,100);
            likeButtonClick();
        }
        else if ((id==R.id.unLikeLayout)||(id==R.id.imgUnLike)){
            OustSdkTools.oustTouchEffect(view,100);
            unLikeButtonClick();
        }
        else if ((id==R.id.commentsLayout)||(id==R.id.imgComment)){
            OustSdkTools.oustTouchEffect(view,100);
            startCommentShowAnimation();
        }
        else if(id==R.id.closePageButton) {
            OustSdkTools.oustTouchEffect(view, 100);
            if(isCommentsShowing) {
                startCommentHideAnimation();
                return;
            }
            getActivity().finish();
        }
        else if(id==R.id.submitButton) {
            OustSdkTools.oustTouchEffect(view, 100);
            setSubmitButtonClick();
        }
        else if(id==R.id.closeButton) {
            OustSdkTools.oustTouchEffect(view, 100);
            if((unLikePopUp!=null)&&(unLikePopUp.isShowing())){
                unLikePopUp.dismiss();
            }
        }
        else if(id==R.id.addCommentBtn) {
            OustSdkTools.oustTouchEffect(view, 100);
            try {
                OustSdkTools.setProgressbar(loader_commentsData);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentsTxtBox.getWindowToken(), 0);
                checkAnswerFragmentPresenter.onCommentAddButtonClicked(commentsTxtBox.getText().toString());
            }catch (Exception e){}
        }
        else if(id==R.id.addStartCommentBtn) {
            OustSdkTools.oustTouchEffect(view, 100);
            try {
                OustSdkTools.setProgressbar(loader_commentsData);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentsStartTxtBox.getWindowToken(), 0);
                checkAnswerFragmentPresenter.onCommentAddButtonClicked(commentsStartTxtBox.getText().toString());
            }catch (Exception e){}
        }

}

    private void favouriteButtonClick() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            checkAnswerFragmentPresenter.setFavoriteBtnClick();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void shareButtonClick(){

        if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE")!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        }else {
            OustShareTools.shareScreenAndBranchIo(getActivity(), baseLayout, getResources().getString(R.string.oust_share_text), OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId()+"");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 123) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        shareButtonClick();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void getFavourite(QuestionFavouriteRequest questionFavouriteRequest) {
        String favouriteUrl = OustSdkApplication.getContext().getResources().getString(R.string.favourite);

        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(questionFavouriteRequest);

        try {
            favouriteUrl = HttpManager.getAbsoluteUrl(favouriteUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, favouriteUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    getFavouriteProcessFinish(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    getFavouriteProcessFinish(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, favouriteUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    getFavouriteProcessFinish(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getFavouriteProcessFinish(null);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void getFavouriteProcessFinish(final CommonResponse commonResponse) {
        checkAnswerFragmentPresenter.getFavoriteQuestionProcessFinish(commonResponse);
    }

    private void likeButtonClick() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            checkAnswerFragmentPresenter.setLikeButtonClick();
        }catch (Exception e){}
    }

    public void setFeedback(final QuestionFeedBackRequest questionFeedBackRequest) {
        String feedBackUrl = OustSdkApplication.getContext().getResources().getString(R.string.feedback);

        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(questionFeedBackRequest);

        try {
            feedBackUrl = HttpManager.getAbsoluteUrl(feedBackUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, feedBackUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    QuestionFeedBackResponse questionFeedBackResponses=gson.fromJson(response.toString(),QuestionFeedBackResponse.class);
                    setFeedbackProcessFinish(questionFeedBackResponses,questionFeedBackRequest);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    getFavouriteProcessFinish(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, feedBackUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    QuestionFeedBackResponse questionFeedBackResponses=gson.fromJson(response.toString(),QuestionFeedBackResponse.class);
                    setFeedbackProcessFinish(questionFeedBackResponses,questionFeedBackRequest);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getFavouriteProcessFinish(null);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setFeedbackProcessFinish(final QuestionFeedBackResponse questionFeedBackResponse, final QuestionFeedBackRequest questionFeedBackRequest) {
        checkAnswerFragmentPresenter.getLikeUnlikeQuestionProcessFinish(questionFeedBackResponse,questionFeedBackRequest);
    }


    private void unLikeButtonClick() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            checkAnswerFragmentPresenter.setUnlikeButtonClick();
        }catch (Exception e){}
    }

    public void showUnlikePopup() {
        try {
            final View popUpView = getActivity().getLayoutInflater().inflate(R.layout.unlike_popup, null); // inflating popup layout
            unLikePopUp = OustSdkTools.createPopUp(popUpView);
            grammaticalErrorCBox = popUpView.findViewById(R.id.grammaticalErrorCBox);
            incorrectAnswersCBox = popUpView.findViewById(R.id.incorrectAnswersCBox);
            incorrectQuestionCBox = popUpView.findViewById(R.id.incorrectQuestionCBox);
            grammaticalErrorCBox.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            incorrectAnswersCBox.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            incorrectQuestionCBox.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            submitButton = popUpView.findViewById(R.id.submitButton);
            closeButton = popUpView.findViewById(R.id.closeButton);
            submitButton.setOnClickListener(this);
            closeButton.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setSubmitButtonClick(){
        try {
            if (incorrectQuestionCBox.isChecked()) {
                incorrectQuestionText = incorrectQuestionCBox.getText().toString();
            }
            if (grammaticalErrorCBox.isChecked()) {
                grammaticalErrorText = grammaticalErrorCBox.getText().toString();
            }
            if (incorrectAnswersCBox.isChecked()) {
                incorrectAnswersText = incorrectAnswersCBox.getText().toString();
            }
            checkAnswerFragmentPresenter.submitButtonClicked(incorrectQuestionText, grammaticalErrorText, incorrectAnswersText);
            if ((unLikePopUp != null) && (unLikePopUp.isShowing())) {
                unLikePopUp.dismiss();
            }
        }catch (Exception e){}
    }
    public void showError(String str){
        OustSdkTools.showToast(str);
    }

    //================================================================================================================
    public void setOptionARight(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setRightAnswer(optionA);
                optionA.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionAWrong(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setWrongAnswer(optionA);
                optionA.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionA(String answer){
        try {
            optionA.setVisibility(View.VISIBLE);
            optionA.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
            optionA.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
        }catch (Exception e){}
    }

    public void setOptionBRight(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setRightAnswer(optionB);
                optionB.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionBWrong(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setWrongAnswer(optionB);
                optionB.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionB(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                optionB.setVisibility(View.VISIBLE);
                optionB.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }

    public void setOptionCRight(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setRightAnswer(optionC);
                optionC.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionCWrong(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setWrongAnswer(optionC);
                optionC.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionC(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                optionC.setVisibility(View.VISIBLE);
                optionC.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }

    public void setOptionDRight(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setRightAnswer(optionD);
                optionD.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionDWrong(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setWrongAnswer(optionD);
                optionD.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionD(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                optionD.setVisibility(View.VISIBLE);
                optionD.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }

    public void setOptionERight(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setRightAnswer(optionE);
                optionE.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionEWrong(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setWrongAnswer(optionE);
                optionE.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionE(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                optionE.setVisibility(View.VISIBLE);
                optionE.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }

    public void setOptionFRight(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setRightAnswer(optionF);
                optionF.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionFWrong(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setWrongAnswer(optionF);
                optionF.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionF(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                optionF.setVisibility(View.VISIBLE);
                optionF.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }

    public void setOptionGRight(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setRightAnswer(optionG);
                optionG.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionGWrong(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                setWrongAnswer(optionG);
                optionG.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    public void setOptionG(String answer){
        try {
            if((answer!=null)&&(!answer.isEmpty())) {
                optionG.setVisibility(View.VISIBLE);
                optionG.setText(Html.fromHtml(URLDecoder.decode(answer, "UTF-8")));
            }
        }catch (Exception e){}
    }
    private void setRightAnswer(Button option){
        try {
            option.setVisibility(View.VISIBLE);
            option.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
            option.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.good, 0);
        }catch (Exception e){}
    }
    private void setWrongAnswer(Button option){
        try {
            option.setVisibility(View.VISIBLE);
            option.setBackgroundColor(OustSdkTools.getColorBack(R.color.Orange));
            option.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.wrong, 0);
        }catch (Exception e){}
    }
    public void setOptionH(String answer){
        optionH.setVisibility(View.VISIBLE);
        optionH.setBackgroundColor(OustSdkTools.getColorBack(R.color.Orange));
        optionH.setText(answer);
    }

    public void showOptionAImage(String mainStr,int roghtOption){
        if((mainStr!=null)&&(!mainStr.isEmpty())) {
            imageques_optionA.setVisibility(View.VISIBLE);
            imageques_optionA.setMainBiutmap(mainStr, scrWidth, roghtOption, true);
        }
    }
    public void showOptionBImage(String mainStr,int roghtOption){
        if((mainStr!=null)&&(!mainStr.isEmpty())) {
            imageques_optionB.setVisibility(View.VISIBLE);
            imageques_optionB.setMainBiutmap(mainStr, scrWidth, roghtOption, true);
        }
    }
    public void showOptionCImage(String mainStr,int roghtOption){
        if((mainStr!=null)&&(!mainStr.isEmpty())) {
            imageques_optionC.setVisibility(View.VISIBLE);
            imageques_optionC.setMainBiutmap(mainStr, scrWidth, roghtOption, true);
        }
    }
    public void showOptionDImage(String mainStr,int roghtOption){
        if((mainStr!=null)&&(!mainStr.isEmpty())) {
            imageques_optionD.setVisibility(View.VISIBLE);
            imageques_optionD.setMainBiutmap(mainStr, scrWidth, roghtOption, true);
        }
    }
    public void showOptionEImage(String mainStr,int roghtOption){
        try {
            if ((mainStr != null) && (!mainStr.isEmpty())) {
                imageques_optionE.setVisibility(View.VISIBLE);
                imageques_optionE.setMainBiutmap(mainStr, scrWidth, roghtOption, true);
            }
        }catch (Exception e){}
    }

    //methodes to show solutions
    private void showSolution(){
        try {
            webviewbackground.setVisibility(View.VISIBLE);
            solution_webview.setVisibility(View.VISIBLE);
            solution_webview.bringToFront();
            solution_webview.getSettings().setJavaScriptEnabled(true);
            solution_webview.loadDataWithBaseURL("", txtSolution, "text/html", "UTF-8", "");
            solution_webview.setInitialScale(200);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, scrollView.getBottom());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //===============================================================================================================
//comment related methodes
    private void startCommentShowAnimation(){
        try {
            OustSdkTools.setProgressbar(loader_commentsData);
            commentsDataLayout.setVisibility(View.VISIBLE);
            Animation event_animmovein = AnimationUtils.loadAnimation(getActivity(), R.anim.event_animbottomin);
            commentsDataLayout.startAnimation(event_animmovein);
            commentsDataLayout.bringToFront();
            closeLayout.bringToFront();
            isCommentsShowing = true;
            event_animmovein.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    checkAnswerFragmentPresenter.showCommentAnimationOver();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            commentsTxtBox.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (isCommentsShowing) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            startCommentHideAnimation();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }catch (Exception e){}
    }
    public void showComments(final String questionId){
        try {
            if(!OustSdkTools.checkInternetStatus()){
                return;
            }
            if (usercommentsListenerClass == null) {
                OustSdkTools.showProgressBar();
                getCommentFromFirebase(questionId);
            } else {
                checkAnswerFragmentPresenter.showCommentsList();
            }
        }catch (Exception e){}
    }

    private void startCommentHideAnimation(){
        try {
            Animation event_animmoveout = AnimationUtils.loadAnimation(getActivity(), R.anim.event_animbottomout);
            closeLayout.bringToFront();
            commentsDataLayout.startAnimation(event_animmoveout);
            isCommentsShowing = false;
            event_animmoveout.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    scrollView.bringToFront();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(commentsStartTxtBox.getWindowToken(), 0);
        }catch (Exception e){}
    }
    private void getCommentFromFirebase(final String questionId){
        String message = "questionComments/"+questionId+"/comments/";
        ValueEventListener commentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comments> commentsList = new ArrayList<>();
                try {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        try {
                            if (data.getValue() != null) {
                                Map<String, Object> commentObjectMap = (Map<String, Object>) data.getValue();
                                Comments oustComments = new Comments();
                                oustComments.setCommentType("user");
                                if (commentObjectMap.get("comment") != null) {
                                    oustComments.setUserComment((String) commentObjectMap.get("comment"));
                                }
                                if (commentObjectMap.get("timeStamp") != null) {
                                    Object o1 = commentObjectMap.get("timeStamp");
                                    if (o1.getClass().equals(String.class)) {
                                        oustComments.setTimeStamp(Long.parseLong((String) o1));
                                    } else if (o1.getClass().equals(Long.class)) {
                                        oustComments.setTimeStamp(((Long) o1));
                                    }
                                }
                                if (commentObjectMap.get("userId") != null) {
                                    oustComments.setUserId((String) commentObjectMap.get("userId"));
                                }
                                if (commentObjectMap.get("userDisplayName") != null) {
                                    oustComments.setUserName((String) commentObjectMap.get("userDisplayName"));
                                }
                                if (commentObjectMap.get("userAvatar") != null) {
                                    oustComments.setUserAvatar((String) commentObjectMap.get("userAvatar"));
                                }
                                if (commentObjectMap.get("userKey") != null) {
                                    Object o1 = commentObjectMap.get("userKey");
                                    if (o1.getClass().equals(String.class)) {
                                        oustComments.setUserKey(((String) o1));
                                    } else if (o1.getClass().equals(Long.class)) {
                                        oustComments.setUserKey(("" + o1));
                                    }
                                }
                                commentsList.add(oustComments);
                            }
                        } catch (Exception e) {
                        }
                    }
                }catch (Exception e){}
                OustSdkTools.hideProgressbar();
                checkAnswerFragmentPresenter.setCommentList(commentsList);
            }
            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                OustSdkTools.hideProgressbar();
                checkAnswerFragmentPresenter.setCommentList(null);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(commentsListener);
        usercommentsListenerClass = new FirebaseRefClass(commentsListener,message);
    }

    public void showCommentList(List<Comments> commentsList){
        try {
            OustSdkTools.hideProgressbar();
            addsolution_layout.setVisibility(View.GONE);
            commentlist_layout.setVisibility(View.VISIBLE);
            commentRecyclerView.setVisibility(View.VISIBLE);
            nocomment_text.setVisibility(View.GONE);
            if(commentsAdapter==null) {
                commentsAdapter = new CommentsAdapter(getActivity(), commentsList, checkAnswerFragmentPresenter);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                commentRecyclerView.setLayoutManager(mLayoutManager);
                commentRecyclerView.setItemAnimator(new DefaultItemAnimator());
                commentRecyclerView.setAdapter(commentsAdapter);
            }else {
                commentsAdapter.notifyListChange(commentsList);
            }
        }catch (Exception e){}
    }

    public void showCommentNotAddedText(){
        OustSdkTools.hideProgressbar();
        try {
            commentRecyclerView.setVisibility(View.GONE);
            addsolution_layout.setVisibility(View.VISIBLE);
            commentlist_layout.setVisibility(View.GONE);
            commentsStartTxtBox.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (isCommentsShowing) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            startCommentHideAnimation();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }catch (Exception e){}
    }

    public void commentAddedSuccessfully(){
        commentsTxtBox.setText("");
    }


    public void addCommeteApi(AddCommentRequest addCommentRequest, long questionId){

        OustSdkTools.showProgressBar();

        String addCommentUrl = OustSdkApplication.getContext().getResources().getString(R.string.comment_url);
        addCommentUrl=addCommentUrl.replace("{qId}",(""+questionId));

        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(addCommentRequest);

            try {
                addCommentUrl = HttpManager.getAbsoluteUrl(addCommentUrl);

                ApiCallUtils.doNetworkCall(Request.Method.POST, addCommentUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CommonResponse commonResponses=gson.fromJson(response.toString(),CommonResponse.class);
                        addCommentProcessOver(commonResponses);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        addCommentProcessOver(null);
                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, addCommentUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CommonResponse commonResponses=gson.fromJson(response.toString(),CommonResponse.class);
                        addCommentProcessOver(commonResponses);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        addCommentProcessOver(null);
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
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
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
    }

    public void addCommentProcessOver(final CommonResponse commonResponse){
        try {
            Handler mainHandler = new Handler(getActivity().getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    OustSdkTools.hideProgressbar();
                    checkAnswerFragmentPresenter.commentAddProcessFinish(commonResponse);
                }
            };
            mainHandler.post(myRunnable);
        }catch (Exception e){}
    }

    public void showErrorPopup(Popup popup){
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(getActivity(), PopupActivity.class);
            startActivity(intent);
        }catch (Exception e){}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (usercommentsListenerClass != null) {
                OustFirebaseTools.getRootRef().child(usercommentsListenerClass.getFirebasePath()).removeEventListener(usercommentsListenerClass.getEventListener());
                usercommentsListenerClass=null;
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideFillLayout(){
        fill_blanks_layout.setVisibility(View.GONE);
    }

    private List<String> answerStrs;
    public int totalOption = 0;
    private List<View> fill_views ;
    private List<OustFillData> emptyViews ;
    private List<String> realAnsStrs ;
    private List<OustFillData> answerView;
    private List<PointF> fillAnswersPoint;
    private int maxlength=0;
    private LayoutInflater mInflater;
    public void setFillLayout(DTOQuestions questions){
        try {
            fill_blanks_layout.setVisibility(View.VISIBLE);
            fill_blanks_layout.removeAllViews();
           answerStrs = new ArrayList<>();
           totalOption = 0;
            fill_views = new ArrayList<>();
           emptyViews = new ArrayList<>();
            realAnsStrs = new ArrayList<>();
           answerView = new ArrayList<>();
            fillAnswersPoint = new ArrayList<>();
            if (mInflater == null) {
                mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            }
            Spanned s1 = getSpannedContent(questions.getQuestion());
            String fillQuestion = (s1.toString().trim());
            fillQuestion=fillQuestion.replace("_.","_ .");
            fillQuestion=fillQuestion.replace("_,","_ ,");
            String[] strings = fillQuestion.split(" ");
            String[] ansStrs = new String[strings.length];
            String[] options = new String[questions.getAnswer().split("#").length];
            options=questions.getAnswer().split("#");
            Collections.shuffle(Arrays.asList(options));

            for(int i=0; i<options.length; i++){
                Spanned s3 = getSpannedContent(options[i]);
                answerStrs.add(s3.toString().trim());
                if(maxlength<s3.toString().trim().length()){
                    maxlength=s3.toString().length();
                }
                totalOption++;
            }

            String dummyStr="  ";
            for(int j=0;j<maxlength;j++){
                dummyStr+="_";
            }
            int n1 = 0;
            int fillIndex = 1;
            int length = 0;
            int x = 0;
            int y = 0;
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen70);

            while (n1 < strings.length) {
                length += strings[n1].length();
                if (strings[n1].contains("____")) {
                    if (strings.length <= ansStrs.length) {
                        if (ansStrs.length > n1) {
                            realAnsStrs.add(ansStrs[n1]);
                        }
                    } else {
                        if (ansStrs.length > 0) {
                            realAnsStrs.add(ansStrs[0]);
                        }
                    }
                    View fillTextView1 = mInflater.inflate(R.layout.fill_emptylayout, null);
                    fillTextView1.setId(100 + fillIndex);
                    fillIndex++;
                    TextView fill_mainlayoutb = fillTextView1.findViewById(R.id.fill_mainlayoutb);
                    fill_mainlayoutb.setText(dummyStr);
                    fill_views.add(fillTextView1);
                    OustFillData oustFillData = new OustFillData();
                    oustFillData.setView(fillTextView1);
                    oustFillData.setIndex(n1);
                    emptyViews.add(oustFillData);
                } else {
                    View fillTextView = mInflater.inflate(R.layout.fill_text_layout, null);
                    TextView textView1 = fillTextView.findViewById(R.id.fill_text);
                    textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    textView1.setText(" " + strings[n1]);
                    fill_views.add(fillTextView);
                }
                n1++;
            }
            x = 20;
            y = (int) getResources().getDimension(R.dimen.oustlayout_dimen30);
            for (int i = 0; i < fill_views.size(); i++) {
                View view = fill_views.get(i);
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int totalW = metrics.widthPixels - 10;
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((x + viewW) < totalW) {
                    view.setX(x);
                    view.setY(y);
                } else {
                    x = 20;
                    y += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
                    view.setX(x);
                    view.setY(y);
                }
                x += viewW;
                fill_blanks_layout.addView(view);
            }

            for (int j = 0; j < answerStrs.size(); j++) {
                View fillTextView = mInflater.inflate(R.layout.fill_textanswer_layout, null);
                fillTextView.setId(1000 + j);
                TextView textView1 = fillTextView.findViewById(R.id.fill_text);
                TextView index_text = fillTextView.findViewById(R.id.index_text);
                index_text.setText("" + j);
                textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                textView1.setText(answerStrs.get(j));
                RelativeLayout fill_answerback = fillTextView.findViewById(R.id.fill_answerback);
                TextView dummytext= fillTextView.findViewById(R.id.dummytext);
                dummytext.setText(dummyStr);
                OustSdkTools.setLayoutBackgroud(fill_answerback,R.drawable.fill_wrong);
                OustFillData oustFillData = new OustFillData();
                oustFillData.setView(fillTextView);
                oustFillData.setIndex(j);
                answerView.add(oustFillData);
            }
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen80);
            x = (int) ((float) (0.02 * scrWidth));
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int totalW = metrics.widthPixels - 10;
            fillAnswersPoint = new ArrayList<>();
            for (int i = 0; i < answerView.size(); i++) {
                View view = answerView.get(i).getView();
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((x + viewW) < totalW) {
                    view.setX(x);
                    view.setY(y);
                } else {
                    x = (int) ((float) (0.02 * scrWidth));
                    y += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
                    view.setX(x);
                    view.setY(y);
                }
                fillAnswersPoint.add(new PointF(x, y));
                x += viewW + ((int) ((float) (0.01 * scrWidth)));
                fill_blanks_layout.addView(view);
            }
            LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams) fill_blanks_layout.getLayoutParams();
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen80);
            layoutParams.height=y;
            fill_blanks_layout.setLayoutParams(layoutParams);
            setViews(dummyStr,questions);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    private void setViews(String dummyStr,DTOQuestions questions){
        for (int idx = 0; idx < emptyViews.size(); idx++) {
            for (int i = 0; i < answerStrs.size(); i++) {
                if(answerStrs.get(i).equals(questions.getFillAnswers().get(idx))) {
                    if(answerView.get(i).getView()!=null) {
                        RelativeLayout fill_answerback = answerView.get(i).getView().findViewById(R.id.fill_answerback);
                        TextView dummytext= answerView.get(i).getView().findViewById(R.id.dummytext);
                        dummytext.setText(dummyStr);
                        OustSdkTools.setLayoutBackgroud(fill_answerback,R.drawable.fill_right);
                        answerView.get(i).getView().setX((int) emptyViews.get(idx).getView().getX());
                        answerView.get(i).getView().setY((int) emptyViews.get(idx).getView().getY()-2);
                        answerView.get(i).setView(null);
                        break;
                    }
                }
            }
        }
    }
    private Spanned getSpannedContent(String content){
        String s2=content.trim();
        try {
            while (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
        }catch (Exception e){}
        Spanned s1= Html.fromHtml(s2,null,new OustTagHandler());
        return s1;
    }
    
//    ========================================================================================
//    match the following

    private List<String> rightChoiceIds=new ArrayList<>();
    private List<String> leftChoiceIds=new ArrayList<>();
    public void setMatchQuestionLayoutSize(DTOQuestions questions){
        try{
            matchfollowing_layout.setVisibility(View.VISIBLE);
            int size=(int)getResources().getDimension(R.dimen.oustlayout_dimen13);
            int imageWidth=(scrWidth/2)-size;
            float h = (imageWidth * 0.57f);
            int h1 = (int) h;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) match_option_a_left_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_a_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_b_left_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_b_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_c_left_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_c_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_d_left_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_d_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_a_right_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_a_right_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_b_right_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_b_right_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_c_right_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_c_right_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_d_right_layout.getLayoutParams();
            params.height = h1;
            params.width=imageWidth;
            match_option_d_right_layout.setLayoutParams(params);

            List<DTOMTFColumnData> mtfRightColMain=questions.getMtfRightCol();
            List<DTOMTFColumnData> mtfRightCol=new ArrayList<>();
            List<String> answerList = questions.getMtfAnswer();
            List<DTOMTFColumnData> mtfLeftCol=questions.getMtfLeftCol();

            for(int i=0;i<mtfLeftCol.size();i++){
                for(int j=0;j<mtfRightColMain.size();j++){
                    String ansStr=""+mtfLeftCol.get(i).getMtfColDataId()+","+mtfRightColMain.get(j).getMtfColDataId();
                    for(int k=0;k<answerList.size();k++){
                        if(answerList.get(k).contains(ansStr)){
                            mtfRightCol.add(mtfRightColMain.get(j));
                        }
                    }
                }
            }
            if((mtfLeftCol!=null)&&(mtfLeftCol.size()>0)){
                if((mtfLeftCol.size()>0)&&(mtfLeftCol.get(0)!= null)&&(mtfLeftCol.get(0).getMtfColData()!=null)) {
                    setMatchOption(match_option_a_left_image, match_option_a_left_text, mtfLeftCol.get(0));
                    match_option_a_left_layout.setVisibility(View.VISIBLE);
                    showOptionWithAnimA(match_option_a_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(0).getMtfColDataId());
                }
                if((mtfLeftCol.size()>1)&&(mtfLeftCol.get(1)!= null)&&(mtfLeftCol.get(1).getMtfColData()!=null)) {
                    setMatchOption(match_option_b_left_image, match_option_b_left_text, mtfLeftCol.get(1));
                    showOptionWithAnimA(match_option_b_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(1).getMtfColDataId());
                }
                if((mtfLeftCol.size()>2)&&(mtfLeftCol.get(2)!= null)&&(mtfLeftCol.get(2).getMtfColData()!=null)) {
                    setMatchOption(match_option_c_left_image, match_option_c_left_text, mtfLeftCol.get(2));
                    showOptionWithAnimA(match_option_c_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(2).getMtfColDataId());
                }
                if((mtfLeftCol.size()>3)&&(mtfLeftCol.get(3)!= null)&&(mtfLeftCol.get(3).getMtfColData()!=null)) {
                    setMatchOption(match_option_d_left_image, match_option_d_left_text, mtfLeftCol.get(3));
                    showOptionWithAnimA(match_option_d_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(3).getMtfColDataId());
                }
            }
            if((mtfRightCol!=null)&&(mtfRightCol.size()>0)){
                if((mtfRightCol.size()>0)&&(mtfRightCol.get(0)!= null)&&(mtfRightCol.get(0).getMtfColData()!=null)) {
                    setMatchOption(match_option_a_right_image, match_option_a_right_text, mtfRightCol.get(0));
                    showOptionWithAnimA(match_option_a_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(0).getMtfColDataId());
                }
                if((mtfRightCol.size()>1)&&(mtfRightCol.get(1)!= null)&&(mtfRightCol.get(1).getMtfColData()!=null)) {
                    setMatchOption(match_option_b_right_image, match_option_b_right_text, mtfRightCol.get(1));
                    showOptionWithAnimA(match_option_b_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(1).getMtfColDataId());
                }
                if((mtfRightCol.size()>2)&&(mtfRightCol.get(2)!= null)&&(mtfRightCol.get(2).getMtfColData()!=null)) {
                    setMatchOption(match_option_c_right_image, match_option_c_right_text, mtfRightCol.get(2));
                    showOptionWithAnimA(match_option_c_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(2).getMtfColDataId());
                }
                if((mtfRightCol.size()>3)&&(mtfRightCol.get(3)!= null)&&(mtfRightCol.get(3).getMtfColData()!=null)) {
                    setMatchOption(match_option_d_right_image, match_option_d_right_text, mtfRightCol.get(3));
                    showOptionWithAnimA(match_option_d_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(3).getMtfColDataId());
                }
            }
        }catch (Exception e){}
    }

    private void showOptionWithAnimA(View view){
        view.setVisibility(View.VISIBLE);
    }

    private void setMatchOption(ImageView imageView,TextView textView,DTOMTFColumnData mtfColumnData){
        try{
            if(mtfColumnData.getMtfColMediaType()!=null){
                if(mtfColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")){
                    setImageOption(mtfColumnData.getMtfColData(),imageView);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }else if(mtfColumnData.getMtfColMediaType().equalsIgnoreCase("AUDIO")){}else {
                    OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(),textView);
                    imageView.setVisibility(View.GONE);
                    textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    textView.setVisibility(View.VISIBLE);
                }
            }else {
                OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(),textView);
                textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){}
    }
    public void setImageOption(String str, ImageView imgView){
        try {
            if((str!=null)&&(!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                imgView.setImageDrawable(gifFromBytes);
            }
        }catch (Exception e){
            setImageOptionA(str,imgView);
        }
    }
    public void setImageOptionA(String str, ImageView imgView){
        try {
            if((str!=null)&&(!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    
}
