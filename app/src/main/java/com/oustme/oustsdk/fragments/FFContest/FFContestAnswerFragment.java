package com.oustme.oustsdk.fragments.FFContest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseQuestionResponse;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseResponse;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.interfaces.common.FFContestAnswerCallback;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by admin on 04/08/17.
 */

public class FFContestAnswerFragment extends Fragment implements View.OnClickListener{

    private FastestFingerContestData fastestFingerContestData;
    public void setFastestFingerContestData(FastestFingerContestData fastestFingerContestData) {
        this.fastestFingerContestData = fastestFingerContestData;
    }
    private RelativeLayout ffclb_bestresultlayout,ffclb_myresultlayout;
    private TextView ffclbrow_bestusername,ffclbrow_myname,ffclbrow_mytime,ffclbrow_bestusertime,
            ffclbrow_bestusertimelabel,ffclbrow_mytimelabel,confirm_titletxt,ffc_resistertext,mytime_text,
            best_result_text;
    private ImageView ffclb_bestuseravatar,ffclb_myavatar;

    private FFContestAnswerCallback ffContestAnswerCallback;
    public void setFfContestAnswerCallback(FFContestAnswerCallback ffContestAnswerCallback) {
        this.ffContestAnswerCallback = ffContestAnswerCallback;
    }

    private FFCFirebaseResponse ffcFirebaseResponse;
    private FFCFirebaseQuestionResponse ffcFirebaseQuestionResponse;
    public void setFfcFirebaseResponse(FFCFirebaseResponse ffcFirebaseResponse) {
        this.ffcFirebaseResponse = ffcFirebaseResponse;
    }
    public void setFfcFirebaseQuestionResponse(FFCFirebaseQuestionResponse ffcFirebaseQuestionResponse) {
        this.ffcFirebaseQuestionResponse = ffcFirebaseQuestionResponse;
    }

    private int questionNo=0,totalQuestion;
    public void setQuestionNo(int questionNo,int totalQuestion) {
        this.questionNo = questionNo;
        this.totalQuestion=totalQuestion;
    }

    private ActiveUser activeUser;

    private ImageView ffcplay_questionimage,imageoptionA,imageoptionB,imageoptionC,imageoptionD,imageoptionE,imageoptionF,ffcstart_backimage;

    private RelativeLayout textchoise_mainlayouta,textchoise_mainlayoutb,textchoise_mainlayoutc,textchoise_mainlayoutd,
            textchoise_mainlayoute,textchoise_mainlayoutf,textchoise_sublayouta,textchoise_sublayoutb,textchoise_sublayoutc,textchoise_sublayoutd,
            textchoise_sublayoute,textchoise_sublayoutf,ffcplay_intermediatelayout,ffcplay_mainquestionlayout,textchoise_confirmlayout,mainimage_optionlayouta,
            mainimage_optionlayoutb,mainimage_optionlayoutc,mainimage_optionlayoutd,mainimage_optionlayoute;

    private HtmlTextView ffcplay_questiontext;

    private LinearLayout textchoise_layout,imagechoise_layout,gotopreviousscreen_mainbtn,gotonextscreen_mainbtn;

    private TextView textchoise_a,textchoise_b,textchoise_c,textchoise_d,textchoise_e,textchoise_f, imagechoise_labela,imagechoise_labelb,imagechoise_labelc,
            imagechoise_labeld,imagechoise_labele,cardprogress_text,textchoise_labela,textchoise_labelb,textchoise_labelc,textchoise_labeld,textchoise_labele,textchoise_labelf;


    private DTOQuestions questions;
    private int scrWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_ffcanswer, container, false);
        activeUser= OustAppState.getInstance().getActiveUser();
        initViews(view);
        getWidth();
        DTOQuestions dtoQuestions = RoomHelper.getFfcQuestionById(Integer.parseInt(fastestFingerContestData.getqIds().get(questionNo)));
        questions= dtoQuestions;
        showQuestion();
        setListeners();
        cardprogress_text.setText(""+(questionNo+1)+"/"+totalQuestion);
        if((questionNo+1)==totalQuestion){
            gotonextscreen_mainbtn.setVisibility(View.GONE);
        }else if(questionNo==0){
            gotopreviousscreen_mainbtn.setVisibility(View.GONE);
        }
        setTopUserInfo();
        return view;
    }

    private void setTopUserInfo(){
        ffclbrow_mytimelabel.setText(OustStrings.getString("sec"));
        if((ffcFirebaseQuestionResponse!=null)&&(ffcFirebaseQuestionResponse.getUserId()!=null)) {
            ffclb_bestresultlayout.setVisibility(View.VISIBLE);
            setLBdata(ffclb_bestresultlayout, ffclb_bestuseravatar, ffclbrow_bestusername, ffclbrow_bestusertime,
                    ffcFirebaseQuestionResponse.getAvatar(),
                    ffcFirebaseQuestionResponse.getDisplayName(),
                    ffcFirebaseQuestionResponse.getReponseTime());
            ffclb_myresultlayout.setVisibility(View.GONE);
            if ((ffcFirebaseResponse!= null)) {
                if (ffcFirebaseResponse.getUserId().equalsIgnoreCase(ffcFirebaseQuestionResponse.getUserId())) {
                    //if user is topper
                } else {
                    ffclb_myresultlayout.setVisibility(View.VISIBLE);
                    setLBdata(ffclb_myresultlayout, ffclb_myavatar, ffclbrow_myname, ffclbrow_mytime,
                            ffcFirebaseResponse.getAvatar(),
                            ffcFirebaseResponse.getDisplayName(),
                            ffcFirebaseResponse.getAverageTime());
                    if(!ffcFirebaseResponse.isCorrect()){
                        ffclbrow_mytime.setTextColor(OustSdkTools.getColorBack(R.color.clear_red));
                        ffclbrow_mytime.setTypeface(OustSdkTools.getTypefaceLight());
                        ffclbrow_mytimelabel.setText("");
                        ffclbrow_mytime.setVisibility(View.VISIBLE);
                        if((ffcFirebaseResponse.getAnswer()!=null)&&(!ffcFirebaseResponse.getAnswer().isEmpty())) {
                            ffclbrow_mytime.setText(OustStrings.getString("wrong"));
                        }else {
                            ffclbrow_mytime.setText(OustStrings.getString("not_attempted"));
                        }
                    }
                }
            }
        }else {
            if ((ffcFirebaseResponse!= null)&&(ffcFirebaseResponse.getUserId()!=null)) {
                ffclb_myresultlayout.setVisibility(View.VISIBLE);
                setLBdata(ffclb_myresultlayout, ffclb_myavatar, ffclbrow_myname, ffclbrow_mytime,
                        ffcFirebaseResponse.getAvatar(),
                        ffcFirebaseResponse.getDisplayName(),
                        ffcFirebaseResponse.getAverageTime());
                if(!ffcFirebaseResponse.isCorrect()){
                    if((ffcFirebaseResponse.getAnswer()!=null)&&(!ffcFirebaseResponse.getAnswer().isEmpty())) {
                        ffclbrow_mytime.setText(OustStrings.getString("wrong"));
                    }else {
                        ffclbrow_mytime.setText(OustStrings.getString("not_attempted"));
                    }
                    ffclbrow_mytimelabel.setText("");
                    ffclbrow_mytime.setTypeface(OustSdkTools.getTypefaceLight());
                    ffclbrow_mytime.setTextColor(OustSdkTools.getColorBack(R.color.clear_red));
                }
            }else {
                ffclb_bestresultlayout.setVisibility(View.GONE);
            }
        }
    }

    private void setLBdata(RelativeLayout layout,ImageView imageView,TextView nameText, TextView timeText ,String imgStr,String name,long time){
        try {
            if ((imgStr != null) && (!imgStr.isEmpty()) && (!imgStr.contains("null"))) {
                BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.maleavatar));
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(imgStr).placeholder(bd).error(bd).into(imageView);
                } else {
                    Picasso.get().load(imgStr).placeholder(bd).error(bd).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
                }
            } else {
                OustSdkTools.setImage(imageView, OustSdkApplication.getContext().getResources().getString(R.string.maleavatar));
            }
            if (name != null) {
                nameText.setText(name);
            }
            if (time > 0) {
                layout.setVisibility(View.VISIBLE);
                timeText.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                timeText.setTextColor(OustSdkTools.getColorBack(R.color.whitelight));
                timeText.setText("" + (time / 1000) + "." + (time % 1000));
            } else {
                timeText.setText("");
            }
        }catch (Exception e){}
    }

    private void getWidth(){
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
    }

    private void initViews(View view){
        textchoise_mainlayouta= view.findViewById(R.id.textchoise_mainlayouta);
        textchoise_mainlayoutb= view.findViewById(R.id.textchoise_mainlayoutb);
        textchoise_mainlayoutc= view.findViewById(R.id.textchoise_mainlayoutc);
        textchoise_mainlayoutd= view.findViewById(R.id.textchoise_mainlayoutd);
        textchoise_mainlayoute= view.findViewById(R.id.textchoise_mainlayoute);
        textchoise_mainlayoutf= view.findViewById(R.id.textchoise_mainlayoutf);

        textchoise_sublayouta= view.findViewById(R.id.textchoise_sublayouta);
        textchoise_sublayoutb= view.findViewById(R.id.textchoise_sublayoutb);
        textchoise_sublayoutc= view.findViewById(R.id.textchoise_sublayoutc);
        textchoise_sublayoutd= view.findViewById(R.id.textchoise_sublayoutd);
        textchoise_sublayoute= view.findViewById(R.id.textchoise_sublayoute);
        textchoise_sublayoutf= view.findViewById(R.id.textchoise_sublayoutf);

        mainimage_optionlayouta= view.findViewById(R.id.mainimage_optionlayouta);
        mainimage_optionlayoutb= view.findViewById(R.id.mainimage_optionlayoutb);
        mainimage_optionlayoutc= view.findViewById(R.id.mainimage_optionlayoutc);
        mainimage_optionlayoutd= view.findViewById(R.id.mainimage_optionlayoutd);
        mainimage_optionlayoute= view.findViewById(R.id.mainimage_optionlayoute);


        ffcplay_intermediatelayout= view.findViewById(R.id.ffcplay_intermediatelayout);
        ffcplay_mainquestionlayout= view.findViewById(R.id.ffcplay_mainquestionlayout);
        textchoise_confirmlayout= view.findViewById(R.id.textchoise_confirmlayout);
        imagechoise_layout= view.findViewById(R.id.imagechoise_layout);

        textchoise_layout= view.findViewById(R.id.textchoise_layout);

        textchoise_a= view.findViewById(R.id.textchoise_a);
        textchoise_b= view.findViewById(R.id.textchoise_b);
        textchoise_c= view.findViewById(R.id.textchoise_c);
        textchoise_d= view.findViewById(R.id.textchoise_d);
        textchoise_e= view.findViewById(R.id.textchoise_e);
        textchoise_f= view.findViewById(R.id.textchoise_f);

        imagechoise_labela= view.findViewById(R.id.imagechoise_labela);
        imagechoise_labelb= view.findViewById(R.id.imagechoise_labelb);
        imagechoise_labelc= view.findViewById(R.id.imagechoise_labelc);
        imagechoise_labeld= view.findViewById(R.id.imagechoise_labeld);
        imagechoise_labele= view.findViewById(R.id.imagechoise_labele);
        cardprogress_text= view.findViewById(R.id.cardprogress_text);

        ffcplay_questiontext= view.findViewById(R.id.ffcplay_questiontext);
        ffcplay_questiontext.setTypeface(OustSdkTools.getAvenirLTStdHeavy());

        ffcplay_questionimage= view.findViewById(R.id.ffcplay_questionimage);

        imageoptionA= view.findViewById(R.id.imageoptionA);
        imageoptionB= view.findViewById(R.id.imageoptionB);
        imageoptionC= view.findViewById(R.id.imageoptionC);
        imageoptionD= view.findViewById(R.id.imageoptionD);
        imageoptionE= view.findViewById(R.id.imageoptionE);
        imageoptionF= view.findViewById(R.id.imageoptionF);
        ffcstart_backimage= view.findViewById(R.id.ffcstart_backimage);

        gotopreviousscreen_mainbtn= view.findViewById(R.id.gotopreviousscreen_mainbtn);
        gotonextscreen_mainbtn= view.findViewById(R.id.gotonextscreen_mainbtn);

        ffclb_bestresultlayout= view.findViewById(R.id.ffclb_bestresultlayout);
        ffclb_myresultlayout= view.findViewById(R.id.ffclb_myresultlayout);

        ffclbrow_bestusername= view.findViewById(R.id.ffclbrow_bestusername);
        ffclb_bestuseravatar= view.findViewById(R.id.ffclb_bestuseravatar);
        ffclbrow_bestusertime= view.findViewById(R.id.ffclbrow_bestusertime);
        ffclbrow_bestusertimelabel= view.findViewById(R.id.ffclbrow_bestusertimelabel);
        best_result_text= view.findViewById(R.id.best_result_text);

        confirm_titletxt= view.findViewById(R.id.confirm_titletxt);
        ffc_resistertext= view.findViewById(R.id.ffc_resistertext);

        ffclbrow_myname= view.findViewById(R.id.ffclbrow_myname);
        ffclbrow_mytime= view.findViewById(R.id.ffclbrow_mytime);
        ffclb_myavatar= view.findViewById(R.id.ffclb_myavatar);
        mytime_text= view.findViewById(R.id.mytime_text);
        ffclbrow_mytimelabel= view.findViewById(R.id.ffclbrow_mytimelabel);
        textchoise_labela= view.findViewById(R.id.textchoise_labela);
        textchoise_labelb= view.findViewById(R.id.textchoise_labelb);
        textchoise_labelc= view.findViewById(R.id.textchoise_labelc);
        textchoise_labeld= view.findViewById(R.id.textchoise_labeld);
        textchoise_labele= view.findViewById(R.id.textchoise_labele);
        textchoise_labelf= view.findViewById(R.id.textchoise_labelf);

        setFont();
        setImages();
        customizeUI();
    }

    private void setFont(){
        ffclbrow_bestusertimelabel.setText(OustStrings.getString("sec"));
        ffclbrow_mytime.setText(OustStrings.getString("sec"));
        best_result_text.setText(OustStrings.getString("best_result"));
        mytime_text.setText(OustStrings.getString("my_result"));
        confirm_titletxt.setText(OustStrings.getString("confirm"));
        ffc_resistertext.setText(OustStrings.getString("confirm"));
    }

    private void setImages() {
        BitmapDrawable bd=OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.contests));

        OustSdkTools.setDrawableToImageView(imageoptionA,bd);
        OustSdkTools.setDrawableToImageView(imageoptionB,bd);
        OustSdkTools.setDrawableToImageView(imageoptionC,bd);
        OustSdkTools.setDrawableToImageView(imageoptionD,bd);
        OustSdkTools.setDrawableToImageView(imageoptionE,bd);
        OustSdkTools.setDrawableToImageView(imageoptionF,bd);

    }

    private void customizeUI(){
        try {
            if((fastestFingerContestData.getBgImage()!=null)&&(!fastestFingerContestData.getBgImage().isEmpty())){
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(fastestFingerContestData.getBgImage()).into(ffcstart_backimage);
                } else {
                    Picasso.get().load(fastestFingerContestData.getBgImage()).networkPolicy(NetworkPolicy.OFFLINE).into(ffcstart_backimage);
                }
            }
            if((fastestFingerContestData.getQuestionTxtColor()!=null)&&(!fastestFingerContestData.getQuestionTxtColor().isEmpty())){
                int color= Color.parseColor(fastestFingerContestData.getQuestionTxtColor());
                ffcplay_questiontext.setTextColor(color);
                ffclbrow_bestusername.setTextColor(color);
                ffclbrow_myname.setTextColor(color);
                ffclbrow_mytime.setTextColor(color);
                ffclbrow_bestusertime.setTextColor(color);
                ffclbrow_bestusertimelabel.setTextColor(color);
                ffclbrow_mytimelabel.setTextColor(color);
            }
            if((fastestFingerContestData.getChoiceTxtColor()!=null)&&(!fastestFingerContestData.getChoiceTxtColor().isEmpty())){
                int color=Color.parseColor(fastestFingerContestData.getChoiceTxtColor());
                textchoise_a.setTextColor(color);
                textchoise_b.setTextColor(color);
                textchoise_c.setTextColor(color);
                textchoise_d.setTextColor(color);
                textchoise_e.setTextColor(color);
                textchoise_f.setTextColor(color);
                textchoise_labela.setTextColor(color);
                textchoise_labelb.setTextColor(color);
                textchoise_labelc.setTextColor(color);
                textchoise_labeld.setTextColor(color);
                textchoise_labele.setTextColor(color);
                textchoise_labelf.setTextColor(color);
                imagechoise_labela.setTextColor(color);
                imagechoise_labelb.setTextColor(color);
                imagechoise_labelc.setTextColor(color);
                imagechoise_labeld.setTextColor(color);
                imagechoise_labele.setTextColor(color);
            }
            int color = OustSdkTools.getColorBack(R.color.whitelight);
            if ((fastestFingerContestData.getQuestionTxtColor() != null) && (!fastestFingerContestData.getQuestionTxtColor().isEmpty())) {
                color = Color.parseColor(fastestFingerContestData.getQuestionTxtColor());
            }
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionA, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionB, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionC, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionD, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionE, null);
            imagechoise_labela.setTextColor(color);
            imagechoise_labelb.setTextColor(color);
            imagechoise_labelc.setTextColor(color);
            imagechoise_labeld.setTextColor(color);
            imagechoise_labele.setTextColor(color);

            GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_grayback);
            GradientDrawable drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_whiteback);
            if ((fastestFingerContestData.getChoiceBgColorDark() != null) && (!fastestFingerContestData.getChoiceBgColorDark().isEmpty()) &&
                    (fastestFingerContestData.getChoiceBgColorLight() != null) && (!fastestFingerContestData.getChoiceBgColorLight().isEmpty())) {
                color = Color.parseColor(fastestFingerContestData.getChoiceBgColorDark());
                drawable.setColor(color);
                int color1 = Color.parseColor(fastestFingerContestData.getChoiceBgColorLight());
                drawable1.setColor(color1);
            }
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayouta, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayouta, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutb, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutb, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutc, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutc, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutd, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutd, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoute, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoute, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutf, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutf, drawable1);
        }catch (Exception e){}
    }


    private void setListeners(){
        gotonextscreen_mainbtn.setOnClickListener(this);
        gotopreviousscreen_mainbtn.setOnClickListener(this);
    }

    private void showQuestion(){
        showQuestionImage();
        String questionType=questions.getQuestionType();
        if(questionType!=null){
            questionType="MCQ";
        }
        if(questions!=null) {
            if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equalsIgnoreCase("IMAGE_CHOICE"))) {
                if ((questionType.equalsIgnoreCase("MCQ")) || (questionType.equalsIgnoreCase("TRUE_FALSE"))) {
                    showMCQImageOptions();
                }
            } else {
                if ((questionType.equalsIgnoreCase("MCQ")) || (questionType.equalsIgnoreCase("TRUE_FALSE"))) {
                    showMCQTextOptions();
                }
            }
        }
    }

    private void showQuestionImage(){
        try{
            if(questions.getQuestion()!=null) {
                ffcplay_questiontext.setHtml(questions.getQuestion());
                ffcplay_questiontext.setVisibility(View.VISIBLE);
            }
            String str = questions.getImage();
            if((str!=null)&&(!str.isEmpty())) {
                ffcplay_questionimage.setVisibility(View.VISIBLE);
                setImageOptionA(str,ffcplay_questionimage);
            }
        }catch (Exception e){}
    }

    private void setImageOptionA(String str, ImageView imgView){
        try {
            if((str!=null)&&(!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
        }catch (Exception e){
        }
    }


    private void showMCQTextOptions(){
        try {
            textchoise_layout.setVisibility(View.VISIBLE);
            if ((questions.getA() != null)&&(!questions.getA().isEmpty())&&(!questions.getA().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getA(),textchoise_a);
                textchoise_mainlayouta.setVisibility(View.VISIBLE);
                setTextBack(textchoise_mainlayouta,textchoise_sublayouta);
                if((questions.getAnswer()!=null)&&(questions.getAnswer().equalsIgnoreCase(questions.getA()))){
                    setRightTextAnswerBack(textchoise_mainlayouta);
                }else {
                    setWrongTextAnswerBack(textchoise_mainlayouta,questions.getA());
                }
            }
            if ((questions.getB() != null)&&(!questions.getB().isEmpty())&&(!questions.getB().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getB(),textchoise_b);
                textchoise_mainlayoutb.setVisibility(View.VISIBLE);
                setTextBack(textchoise_mainlayoutb,textchoise_sublayoutb);
                if((questions.getAnswer()!=null)&&(questions.getAnswer().equalsIgnoreCase(questions.getB()))){
                    setRightTextAnswerBack(textchoise_mainlayoutb);
                }else {
                    setWrongTextAnswerBack(textchoise_mainlayoutb,questions.getB());
                }
            }
            if ((questions.getC() != null)&&(!questions.getC().isEmpty())&&(!questions.getC().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getC(),textchoise_c);
                textchoise_mainlayoutc.setVisibility(View.VISIBLE);
                setTextBack(textchoise_mainlayoutc,textchoise_sublayoutc);
                if((questions.getAnswer()!=null)&&(questions.getAnswer().equalsIgnoreCase(questions.getC()))){
                    setRightTextAnswerBack(textchoise_mainlayoutc);
                }else {
                    setWrongTextAnswerBack(textchoise_mainlayoutc,questions.getC());
                }
            }
            if ((questions.getD() != null)&&(!questions.getD().isEmpty())&&(!questions.getD().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getD(),textchoise_d);
                textchoise_mainlayoutd.setVisibility(View.VISIBLE);
                setTextBack(textchoise_mainlayoutd,textchoise_sublayoutd);
                if((questions.getAnswer()!=null)&&(questions.getAnswer().equalsIgnoreCase(questions.getD()))){
                    setRightTextAnswerBack(textchoise_mainlayoutd);
                }else {
                    setWrongTextAnswerBack(textchoise_mainlayoutd,questions.getD());
                }
            }
            if ((questions.getE() != null)&&(!questions.getE().isEmpty())&&(!questions.getE().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getE(),textchoise_e);
                textchoise_mainlayoute.setVisibility(View.VISIBLE);
                setTextBack(textchoise_mainlayoute,textchoise_sublayoute);
                if((questions.getAnswer()!=null)&&(questions.getAnswer().equalsIgnoreCase(questions.getE()))){
                    setRightTextAnswerBack(textchoise_mainlayoute);
                }else {
                    setWrongTextAnswerBack(textchoise_mainlayoute,questions.getE());
                }
            }
            if ((questions.getF() != null)&&(!questions.getF().isEmpty())&&(!questions.getF().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getF(),textchoise_f);
                textchoise_mainlayoutf.setVisibility(View.VISIBLE);
                setTextBack(textchoise_mainlayoutf,textchoise_sublayoutf);
                if((questions.getAnswer()!=null)&&(questions.getAnswer().equalsIgnoreCase(questions.getF()))){
                    setRightTextAnswerBack(textchoise_mainlayoutf);
                }else {
                    setWrongTextAnswerBack(textchoise_mainlayoutf,questions.getF());
                }
            }
            textchoise_confirmlayout.setVisibility(View.GONE);
        }catch (Exception e){
        }
    }

    private void showMCQImageOptions(){
        try {
            imagechoise_layout.setVisibility(View.VISIBLE);
            if ((questions.getImageChoiceA() != null)&&(questions.getImageChoiceA().getImageData()!=null)&&(questions.getImageChoiceA().getImageFileName()!=null)) {
                mainimage_optionlayouta.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionA);
                setImageOptionA(questions.getImageChoiceA().getImageData(), imageoptionA);
                if((questions.getImageChoiceAnswer()!=null)&&(questions.getImageChoiceAnswer().getImageFileName()!=null)&&
                        (questions.getImageChoiceAnswer().getImageFileName().equalsIgnoreCase(questions.getImageChoiceA().getImageFileName()))){
                    setRightImageAnswerBack(imageoptionA,imagechoise_labela);
                }else {
                    setWrongImageAnswerBack(imageoptionA,imagechoise_labela,questions.getImageChoiceA().getImageFileName());
                }
            }
            if ((questions.getImageChoiceB() != null)&&(questions.getImageChoiceB().getImageData()!=null)&&(questions.getImageChoiceB().getImageFileName()!=null)) {
                mainimage_optionlayoutb.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionB);
                setImageOptionA(questions.getImageChoiceB().getImageData(), imageoptionB);
                if((questions.getImageChoiceAnswer()!=null)&&(questions.getImageChoiceAnswer().getImageFileName()!=null)&&
                        (questions.getImageChoiceAnswer().getImageFileName().equalsIgnoreCase(questions.getImageChoiceB().getImageFileName()))){
                    setRightImageAnswerBack(imageoptionB,imagechoise_labelb);
                }else {
                    setWrongImageAnswerBack(imageoptionB,imagechoise_labelb,questions.getImageChoiceB().getImageFileName());
                }
            }
            if ((questions.getImageChoiceC() != null)&&(questions.getImageChoiceC().getImageData()!=null)&&(questions.getImageChoiceC().getImageFileName()!=null)) {
                mainimage_optionlayoutc.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionC);
                setImageOptionA(questions.getImageChoiceC().getImageData(), imageoptionC);
                if((questions.getImageChoiceAnswer()!=null)&&(questions.getImageChoiceAnswer().getImageFileName()!=null)&&
                        (questions.getImageChoiceAnswer().getImageFileName().equalsIgnoreCase(questions.getImageChoiceC().getImageFileName()))){
                    setRightImageAnswerBack(imageoptionC,imagechoise_labelc);
                }else {
                    setWrongImageAnswerBack(imageoptionC,imagechoise_labelc,questions.getImageChoiceC().getImageFileName());
                }
            }
            if ((questions.getImageChoiceD() != null)&&(questions.getImageChoiceD().getImageData()!=null)&&(questions.getImageChoiceD().getImageFileName()!=null)) {
                mainimage_optionlayoutd.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionD);
                setImageOptionA(questions.getImageChoiceD().getImageData(), imageoptionD);
                if((questions.getImageChoiceAnswer()!=null)&&(questions.getImageChoiceAnswer().getImageFileName()!=null)&&
                        (questions.getImageChoiceAnswer().getImageFileName().equalsIgnoreCase(questions.getImageChoiceD().getImageFileName()))){
                    setRightImageAnswerBack(imageoptionD,imagechoise_labeld);
                }else {
                    setWrongImageAnswerBack(imageoptionD,imagechoise_labeld,questions.getImageChoiceD().getImageFileName());
                }
            }
            if ((questions.getImageChoiceE() != null)&&(questions.getImageChoiceE().getImageData()!=null)&&(questions.getImageChoiceE().getImageFileName()!=null)) {
                mainimage_optionlayoute.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionE);
                setImageOptionA(questions.getImageChoiceE().getImageData(), imageoptionE);
                if((questions.getImageChoiceAnswer()!=null)&&(questions.getImageChoiceAnswer().getImageFileName()!=null)&&
                        (questions.getImageChoiceAnswer().getImageFileName().equalsIgnoreCase(questions.getImageChoiceE().getImageFileName()))){
                    setRightImageAnswerBack(imageoptionE,imagechoise_labele);
                }else {
                    setWrongImageAnswerBack(imageoptionE,imagechoise_labele,questions.getImageChoiceE().getImageFileName());
                }
            }
        }catch (Exception e){
        }
    }

    private void setTextBack(RelativeLayout mainLayout,RelativeLayout sublayout){
        GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_whiteback);
        GradientDrawable drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_grayback);
        if ((fastestFingerContestData.getChoiceBgColorDark() != null) && (!fastestFingerContestData.getChoiceBgColorDark().isEmpty()) &&
                (fastestFingerContestData.getChoiceBgColorLight() != null) && (!fastestFingerContestData.getChoiceBgColorLight().isEmpty())) {
            int color = Color.parseColor(fastestFingerContestData.getChoiceBgColorDark());
            drawable1.setColor(color);
            int color1 = Color.parseColor(fastestFingerContestData.getChoiceBgColorLight());
            drawable.setColor(color1);
        }else {
            drawable1.setColor(OustSdkTools.getColorBack(R.color.LiteGray));
        }
        OustSdkTools.setLayoutBackgroudDrawable(sublayout,drawable);
        OustSdkTools.setLayoutBackgroudDrawable(mainLayout,drawable1);
    }

    private void setWrongTextAnswerBack(RelativeLayout mainLayout,String option){
        if (((ffcFirebaseResponse!= null)&&(!ffcFirebaseResponse.isCorrect()))&&((ffcFirebaseResponse.getAnswer()!=null)&&(ffcFirebaseResponse.getAnswer().equalsIgnoreCase(option)))) {
            GradientDrawable drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_darkgaryback);
            drawable1.setColor(OustSdkTools.getColorBack(R.color.clear_red));
            OustSdkTools.setLayoutBackgroudDrawable(mainLayout,drawable1);
        }
    }
    private void setRightTextAnswerBack(RelativeLayout mainLayout){
        GradientDrawable drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_grayback);
        drawable1.setColor(OustSdkTools.getColorBack(R.color.LiteGreen));
        OustSdkTools.setLayoutBackgroudDrawable(mainLayout,drawable1);
    }

    private void setWrongImageAnswerBack(ImageView imageView,TextView textView, String option){
        if (((ffcFirebaseResponse!= null)&&(!ffcFirebaseResponse.isCorrect()))&&((ffcFirebaseResponse.getAnswer()!=null)&&
                (ffcFirebaseResponse.getAnswer().equalsIgnoreCase(option)))){
            GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_selectback);
            drawable.setStroke(2,OustSdkTools.getColorBack(R.color.clear_red));
            textView.setTextColor(OustSdkTools.getColorBack(R.color.clear_red));
            imageView.setImageDrawable(drawable);
        }else {
            textView.setTextColor(OustSdkTools.getColorBack(R.color.whitelight));
        }
    }
    private void setRightImageAnswerBack(ImageView imageView,TextView textView){
        if ((fastestFingerContestData.getChoiceBgColorSelectedDark() != null) && (!fastestFingerContestData.getChoiceBgColorSelectedDark().isEmpty()) &&
                (fastestFingerContestData.getChoiceBgColorSelectedLight() != null) && (!fastestFingerContestData.getChoiceBgColorSelectedLight().isEmpty())) {
            int textColor=OustSdkTools.getColorBack(R.color.splash_screencolora);
            textView.setTextColor(textColor);
            GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_selectback);
            int color = Color.parseColor(fastestFingerContestData.getChoiceBgColorSelectedDark());
            drawable.setStroke(2,color);
            OustSdkTools.setLayoutBackgroudDrawable(imageView, drawable);
            textView.setTextColor(color);
        }else {
            textView.setTextColor(OustSdkTools.getColorBack(R.color.splash_screencolora));
            OustSdkTools.setLayoutBackgroudDrawable(imageView, OustSdkTools.getImgDrawable(R.drawable.ffc_selectback));
        }
    }

    private void setLayoutAspectRatiosmall(ImageView layout){
        try {
            int size=(int)getResources().getDimension(R.dimen.oustlayout_dimen25);
            int imageWidth=(scrWidth/2)-size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            float h = (imageWidth * 0.563f);
            int h1 = (int) h;
            params.height = h1;
            params.width=imageWidth;
            layout.setLayoutParams(params);
        }catch (Exception e){
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.gotopreviousscreen_mainbtn){
            ffContestAnswerCallback.gotoPreviousSlide();
        }else if(view.getId()==R.id.gotonextscreen_mainbtn){
            ffContestAnswerCallback.gotoNextSlide();
        }
    }
}
