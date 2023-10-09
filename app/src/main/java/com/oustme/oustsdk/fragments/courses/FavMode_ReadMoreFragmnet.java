package com.oustme.oustsdk.fragments.courses;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 * Created by shilpysamaddar on 09/06/17.
 */
public class FavMode_ReadMoreFragmnet extends Fragment implements View.OnTouchListener {

    private RelativeLayout nointernetconnection_layout,rm_full_layout,readmore_close,rm_pdf_view,arrow_previous,arrow_forward
            ,rm_share_layout,rm_image_layout,readmore_favourite_layout,readmore_webView_layout;


    private TextView nointernetconnection_text;

    private WebView readmore_webView;

    private ImageView unfavourite,rm_image_view;

    //private PDFView pdfwebview;

    private ProgressBar rm_url_loader;

    private CourseLevelClass courseLevelClass;
    public void setCourseLevelClass(CourseLevelClass courseLevelClass){
        this.courseLevelClass=courseLevelClass;
    }

    private FavCardDetails favCardDetails=new FavCardDetails();
    private List<FavCardDetails> favCardDetailsList;
    String cardId;

    private LearningModuleInterface learningModuleInterface;
    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    private String cardBackgroundImage;
    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private int progress;
    public void setProgressVal(int progressVal){
        this.progress=progressVal;
    }

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList, String cardId){
        this.favCardDetailsList=favCardDetailsList;
        this.cardId=cardId;
    }
    private DTOCourseCard courseCardClass;
    public void setCourseCardClass(DTOCourseCard courseCardClass) {
        this.courseCardClass = courseCardClass;
    }

    private boolean isRMFavourite;
    public void isFavourite(boolean isfavouriteClicked) {
        this.isRMFavourite = isfavouriteClicked;
    }

    private boolean clickedOnPrevious;
    public void clickedOnPrevious(boolean clickedOnPrevious){
        this.clickedOnPrevious=clickedOnPrevious;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if((courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getType().equalsIgnoreCase("PDF"))) {
            if(clickedOnPrevious){
                gotoPreviousSceen();
            } else {
                gotoNextScrren();
            }
        }

        View view=inflater.inflate(R.layout.fragmnet_readmore,container,false);
        initViews(view);
        init();
        return view;
    }

    private void initViews(View view) {
        nointernetconnection_layout= view.findViewById(R.id.nointernetconnection_layout);
        rm_full_layout= view.findViewById(R.id.rm_full_layout);
        readmore_close= view.findViewById(R.id.readmore_close);
        rm_pdf_view= view.findViewById(R.id.rm_pdf_view);
        arrow_previous= view.findViewById(R.id.arrow_previous);
        arrow_forward= view.findViewById(R.id.arrow_forward);
        rm_share_layout= view.findViewById(R.id.rm_share_layout);
        rm_image_layout= view.findViewById(R.id.rm_image_layout);
        readmore_favourite_layout= view.findViewById(R.id.readmore_favourite_layout);
        readmore_webView_layout= view.findViewById(R.id.readmore_webView_layout);

        nointernetconnection_text= view.findViewById(R.id.nointernetconnection_text);
        nointernetconnection_text.setText(OustStrings.getString("nointernet_message"));

        unfavourite= view.findViewById(R.id.unfavourite);
        rm_image_view= view.findViewById(R.id.rm_image_view);

        readmore_webView= view.findViewById(R.id.readmore_webView);
        rm_url_loader= view.findViewById(R.id.rm_url_loader);

        readmore_close.setOnTouchListener(this);
        arrow_forward.setOnTouchListener(this);
        arrow_previous.setOnTouchListener(this);
        readmore_favourite_layout.setOnTouchListener(this);
        rm_share_layout.setOnTouchListener(this);
    }

    public void init(){
        setFavColor();
        showReadMorePopup();
    }

    private void setFavColor() {
        if(progress==0){
            arrow_previous.setVisibility(View.GONE);
        }
        if(progress==(courseLevelClass.getCourseCardClassList().size())-1) {
            arrow_forward.setVisibility(View.GONE);
        }
        if (isRMFavourite) {
            isRMFavourite = true;
            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
            unfavourite.setColorFilter(getResources().getColor(R.color.Orange));
        } else {
            isRMFavourite = false;
            unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
            unfavourite.setColorFilter(getResources().getColor(R.color.whitea));
        }
    }

    private void showReadMorePopup() {
        readmore_webView_layout.setVisibility(View.VISIBLE);
        //            opening read more url in webview
        DTOReadMore readMoreData = courseCardClass.getReadMoreData();
        if ((readMoreData != null) && ((readMoreData.getScope() != null) && (readMoreData.getScope().equalsIgnoreCase("public"))&&(readMoreData.getType().equalsIgnoreCase("URL_LINK")))) {
            OustSdkTools.setProgressbar(rm_url_loader);
            OustSdkTools.showProgressBar();
            readmore_webView_layout.setVisibility(View.VISIBLE);
            rm_image_layout.setVisibility(View.GONE);
            readmore_webView.setWebViewClient(new FavMode_ReadMoreFragmnet.MyWebViewClient());
            readmore_webView.getSettings().setJavaScriptEnabled(true);
            readmore_webView.loadUrl(readMoreData.getData());
            readmore_webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress > 70) {
                        OustSdkTools.hideProgressbar();
                    }
                }
            });
        }
        else if ((readMoreData != null) && ((readMoreData.getType().equalsIgnoreCase("IMAGE")))){
            readmore_webView_layout.setVisibility(View.GONE);
            rm_image_layout.setVisibility(View.VISIBLE);
            setImage("oustlearn_" + readMoreData.getData());
        }
        else if((courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getType().equalsIgnoreCase("PDF"))){
//            gotoNextScrren();
//            rm_pdf_view.setVisibility(View.VISIBLE);
//            readmore_webView_layout.setVisibility(View.GONE);
//            rm_image_layout.setVisibility(View.GONE);
//            File file= OustSdkTools.getDataFromPrivateStorage(readMoreData.getData());
//            pdfwebview.fromFile(file).load();
        }
    }

    private void  setImage(String fileName){
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if((audStr!=null)&&(!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                rm_image_view.setImageBitmap(decodedByte);
//                rm_image_layout.setVisibility(View.VISIBLE);
//            }
            File file=new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()){
                Picasso.get().load(file).into(rm_image_view);
                rm_image_layout.setVisibility(View.VISIBLE);

            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private float x1,x2;
    private float y1,y2;
    private final int MIN_DISTANCE =30;
    private boolean touchedScreen =false;

    @Override
    public boolean onTouch(View v, MotionEvent event){
        try {
            int action=event.getAction();
                if(action==MotionEvent.ACTION_DOWN) {
                    touchedScreen = true;
                    x1 = event.getX();
                    y1 = event.getY();
                }
                else if(action==MotionEvent.ACTION_UP){
                    if (touchedScreen) {
                        touchedScreen = false;
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x1 - x2;
                        float deltaY = y1 - y2;
                        if (deltaX > 0 && deltaY > 0) {
                            if (deltaX > deltaY) {
                                if (deltaX > MIN_DISTANCE) {
                                    gotoNextScrren();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else if (deltaX < 0 && deltaY > 0) {
                            if ((-deltaX) > deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    gotoPreviousSceen();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else if (deltaX < 0 && deltaY < 0) {
                            if (deltaX < deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    gotoPreviousSceen();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else if (deltaX > 0 && deltaY < 0) {
                            if (deltaX > (-deltaY)) {
                                if (deltaX > MIN_DISTANCE) {
                                    gotoNextScrren();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else {
                            detectClickedView(v);
                        }
                    }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    private void gotoNextScrren(){
        learningModuleInterface.gotoNextScreen();
    }

    private void gotoPreviousSceen(){
        learningModuleInterface.gotoPreviousScreen();
    }

    private void detectClickedView(View v){
        int id=v.getId();
        if(id==R.id.arrow_forward){
            gotoNextScrren();
        }else if(id == R.id.arrow_previous){
            gotoPreviousSceen();
        }else if(id==R.id.rm_share_layout){
            shareScreenShot();
        }else if(id==R.id.readmore_favourite_layout){
            if(!isRMFavourite) {
                isRMFavourite=true;
                unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
                unfavourite.setColorFilter(getActivity().getResources().getColor(R.color.Orange));
            } else {
                isRMFavourite=false;
                unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
                unfavourite.setColorFilter(getActivity().getResources().getColor(R.color.DarkGray));
            }
            favouriteClicked(isRMFavourite);
        }else if(id==R.id.readmore_close){
            learningModuleInterface.endActivity();
        }

    }

    private void favouriteClicked(boolean isRMFavourite) {
        try {
            learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            FavCardDetails favCardDetails = new FavCardDetails();
            this.isRMFavourite = isRMFavourite;
            if (isRMFavourite) {
                favCardDetails.setCardId("" + courseCardClass.getCardId());
                favCardDetails.setRmId(courseCardClass.getReadMoreData().getRmId());
                favCardDetails.setRmData(courseCardClass.getReadMoreData().getData());
                favCardDetails.setRMCard(true);
                favCardDetails.setRmDisplayText(courseCardClass.getReadMoreData().getDisplayText());
                favCardDetails.setRmScope(courseCardClass.getReadMoreData().getScope());
                favCardDetails.setRmType(courseCardClass.getReadMoreData().getType());

                favCardDetailsList.add(favCardDetails);
                learningModuleInterface.setFavCardDetails(favCardDetailsList);

            } else {
                learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void shareScreenShot(){
        Bitmap bitmap= OustSdkTools.getScreenShot(rm_full_layout);
        String branchLinkUrl="http://bit.ly/1xEh2HW";
        OustShareTools.shareScreenUsingIntent(getActivity(), branchLinkUrl,bitmap);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        File file = new File(Environment.getExternalStorageDirectory(),"newfile.pdf");
        if(file.exists()) {
            file.delete();
        }
        if(readmore_webView_layout.getVisibility()==View.VISIBLE){
            readmore_webView.loadUrl("about:blank");
        }
    }
}
