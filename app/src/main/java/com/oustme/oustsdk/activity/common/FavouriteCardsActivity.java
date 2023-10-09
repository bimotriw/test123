package com.oustme.oustsdk.activity.common;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.learningmapmodule.LearningMapModuleActivity;
import com.oustme.oustsdk.adapter.common.FavouriteCourseAdapter;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.CardClickCallBack;
import com.oustme.oustsdk.request.FavouriteCardsFilter;
import com.oustme.oustsdk.response.common.FavouriteCardsCourseData;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

public class FavouriteCardsActivity extends AppCompatActivity implements CardClickCallBack, SearchView.OnQueryTextListener{

    private Toolbar toolbar;
    private AppBarLayout favourite_appbar;
    private RecyclerView favcard_recyclerview;
    private ProgressBar favourite_loader_progressbar;
    private TextView nocard_text;
    private SwipeRefreshLayout swipe_refresh_layout;
    private FavouriteCourseAdapter favCourseAdapter;
    private ActiveUser activeUser;
    private List<FavouriteCardsCourseData> favouriteCardsCourseDataList=new ArrayList<>();
    private List<FavouriteCardsCourseData> favouriteCardsCourseDataListFiltered;
    private String courseId;
    private int numberOfFavCards=0;
    private FirebaseRefClass firebaseRefClass;
    private String updateTS;
    private MenuItem actionSearch;
    private View searchPlate;
    public CustomSearchView newSearchView;
    private RelativeLayout downloadpdf_progress_layout;
    private ProgressBar pdfdownload_progressbar;
    private DownloadFiles downloadFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(FavouriteCardsActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_favouritecards);
        try{
            activeUser= OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                Log.e("Active user", "active user is  null ");
                OustSdkApplication.setmContext(FavouriteCardsActivity.this);
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        initViews();
        createLoader();
        setToolBarColor();
        getFavDataFromFirebase();
//        OustGATools.getInstance().reportPageViewToGoogle(FavouriteCardsActivity.this,"Favourite Cards Page");

    }

    private void initViews(){
        try {
            toolbar = findViewById(R.id.tabanim_toolbar);
            favourite_appbar = findViewById(R.id.favourite_appbar);
            favcard_recyclerview = findViewById(R.id.favcard_recyclerview);
            nocard_text = findViewById(R.id.nocard_text);
            swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
            favourite_loader_progressbar= findViewById(R.id.favourite_loader_progressbar);

            pdfdownload_progressbar= findViewById(R.id.pdfdownload_progressbar);
            downloadpdf_progress_layout= findViewById(R.id.downloadpdf_progress_layout);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu, menu);
            actionSearch=menu.findItem(R.id.action_search);
            showSearchIcon();

            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();

            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(getResources().getString(R.string.search_text));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
        }catch (Exception e){
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            if(item.getItemId()==android.R.id.home) {
                onBackPressed();
            }else if(item.getItemId()==R.id.action_search) {
                actionSearch.setVisible(true);
                searchPlate = newSearchView.findViewById(R.id.search_plate);
                searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            }else {
                super.onOptionsItemSelected(item);
            }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty()) {}
        return false;
    }

    private String searchText="";
    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            if (newText.isEmpty()) {
                searchText="";
//                searchOn=false;
                addFavouriteCardInView(favouriteCardsCourseDataList);

            } else {
                searchText=newText;
//                searchOn=true;
                filterList(newText);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    private void showSearchIcon(){
        try {
//                int courseSize=0;
//                if(favouriteCardsCourseDataList!=null){
//                    courseSize=favouriteCardsCourseDataList.size();
//                }
            actionSearch.setVisible(true);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void filterList(String searchText){
        try {
            favouriteCardsCourseDataListFiltered=new ArrayList<>();
            if((favouriteCardsCourseDataList!=null)&&(favouriteCardsCourseDataList.size()>0)) {
                FavouriteCardsFilter cardsFilter = new FavouriteCardsFilter();
                favouriteCardsCourseDataListFiltered = cardsFilter.meetCriteria(favouriteCardsCourseDataList, searchText);
            }
            addFavouriteCardInView(favouriteCardsCourseDataListFiltered);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if((favouriteCardsCourseDataList!=null)&&(favouriteCardsCourseDataList.size()>0)) {
//            addFavouriteCardInView(favouriteCardsCourseDataList);
//        }
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(getApplicationContext());
        }
        if(searchText!=null && (!searchText.isEmpty())){
//                searchOn=true;
            filterList(searchText);
        } else if((favouriteCardsCourseDataList!=null)&&(favouriteCardsCourseDataList.size()>0)){
            addFavouriteCardInView(favouriteCardsCourseDataList);
        }
        String toolbarColorCode=OustPreferences.get("toolbarColorCode");
        if((toolbarColorCode!=null)&&(!toolbarColorCode.isEmpty())){
            int color = Color.parseColor(toolbarColorCode);
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
            final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
            final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
            d1.setColorFilter(color,mode);
            favourite_loader_progressbar.setIndeterminateDrawable(ld);
        }
//        OustSdkTools.setProgressbar(favourite_loader_progressbar);
    }

    private void getFavDataFromFirebase(){
        try {
//            OustSdkTools.showProgressBar();
            String message = "/userFavouriteCards/" + "user"+activeUser.getStudentKey();
            ValueEventListener allfavCardListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            favouriteCardsCourseDataList=new ArrayList<>();
                            final Map<String, Object> allfavCourseMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (allfavCourseMap != null) {
                                numberOfFavCards=0;
                                for (String coursekey : allfavCourseMap.keySet()) {
                                    courseId=coursekey.substring(6);
                                    Log.e("FAVOURITE", courseId);
                                    Object courseObject = allfavCourseMap.get(coursekey);
                                    final Map<String, Object> allfavCardMap = (Map<String, Object>) courseObject;
                                    if ((allfavCardMap.get("courseName") != null) && ((allfavCardMap.get("cards")!=null)||(allfavCardMap.get("readMore")!=null))){
                                        FavouriteCardsCourseData favouriteCardsCourseData = new FavouriteCardsCourseData();
                                        List<FavCardDetails> favCardDetailsList = new ArrayList<>();
                                        favouriteCardsCourseData.setCourseName((String) allfavCardMap.get("courseName"));
                                        favouriteCardsCourseData.setCourseId(courseId);
                                        if(allfavCardMap.get("cards")!=null){
                                            Map<String, Object> cardMap = new HashMap<>();
                                            Object o1 = allfavCardMap.get("cards");
                                            if (o1.getClass().equals(HashMap.class)) {
                                                cardMap = (Map<String, Object>) o1;
                                                if (cardMap != null) {
                                                    Map<String, Object> carddetailsMap = new HashMap<String, Object>();
                                                    for (String key : cardMap.keySet()) {
                                                        Object details = cardMap.get(key);
                                                        try {
                                                            if (details != null) {
                                                                carddetailsMap = (Map<String, Object>) details;
                                                                FavCardDetails favCardDetails = new FavCardDetails();
                                                                if (carddetailsMap.get("cardId") != null)
                                                                    favCardDetails.setCardId((String) carddetailsMap.get("cardId"));
                                                                if (carddetailsMap.get("imageUrl") != null)
                                                                    favCardDetails.setImageUrl((String) carddetailsMap.get("imageUrl"));
                                                                if (carddetailsMap.get("cardDescription") != null)
                                                                    favCardDetails.setCardDescription((String) carddetailsMap.get("cardDescription"));
                                                                if (carddetailsMap.get("cardTitle") != null)
                                                                    favCardDetails.setCardTitle((String) carddetailsMap.get("cardTitle"));
                                                                if (carddetailsMap.get("audio") != null)
                                                                    if (carddetailsMap.get("audio").getClass().equals(String.class)) {
                                                                        favCardDetails.setAudio(false);
                                                                    } else {
                                                                        favCardDetails.setAudio((boolean) carddetailsMap.get("audio"));
                                                                    }
                                                                if (carddetailsMap.get("video") != null)
                                                                    favCardDetails.setVideo((boolean) carddetailsMap.get("video"));
                                                                favCardDetailsList.add(favCardDetails);
                                                                if (carddetailsMap.get("mediaType") != null)
                                                                    favCardDetails.setMediaType((String) carddetailsMap.get("mediaType"));
                                                                numberOfFavCards++;
                                                            }
                                                        }catch (Exception e){}
                                                    }
                                                }

                                            }
                                        }
                                        if (allfavCardMap.get("readMore") != null){
                                            Map<String, Object> readmoreMap = new HashMap<>();
                                            Object o1 = allfavCardMap.get("readMore");
                                            if (o1.getClass().equals(HashMap.class)) {
                                                readmoreMap = (Map<String, Object>) o1;
                                                if (readmoreMap != null) {
                                                    Map<String, Object> rmDetailMap = new HashMap<String, Object>();
                                                    for (String key : readmoreMap.keySet()) {
                                                        Object details = readmoreMap.get(key);
                                                        try {
                                                            if (details != null) {
                                                                rmDetailMap = (Map<String, Object>) details;
                                                                FavCardDetails favCardDetails = new FavCardDetails();
                                                                if (rmDetailMap.get("cardId") != null)
                                                                    favCardDetails.setCardId((String) rmDetailMap.get("cardId"));
                                                                if (rmDetailMap.get("levelId") != null)
                                                                    favCardDetails.setLevelId((String) rmDetailMap.get("levelId"));
                                                                if (rmDetailMap.get("rmId") != null) {
                                                                    if (rmDetailMap.get("rmId").getClass().equals(String.class)) {
                                                                        favCardDetails.setRmId(Long.parseLong((String) rmDetailMap.get("rmId")));
                                                                    } else {
                                                                        favCardDetails.setRmId((long) rmDetailMap.get("rmId"));
                                                                    }
                                                                }
                                                                favCardDetails.setRMCard(true);
                                                                if (rmDetailMap.get("rmData") != null)
                                                                    favCardDetails.setRmData((String) rmDetailMap.get("rmData"));
                                                                if (rmDetailMap.get("rmScope") != null)
                                                                    favCardDetails.setRmScope((String) rmDetailMap.get("rmScope"));
                                                                if (rmDetailMap.get("rmDisplayText") != null)
                                                                    favCardDetails.setRmDisplayText((String) rmDetailMap.get("rmDisplayText"));
                                                                if (rmDetailMap.get("rmType") != null)
                                                                    favCardDetails.setRmType((String) rmDetailMap.get("rmType"));
                                                                favCardDetailsList.add(favCardDetails);
                                                                numberOfFavCards++;
                                                            }
                                                        }catch (Exception e){}
                                                    }

                                                }
                                            }
                                        }

                                        favouriteCardsCourseData.setFavCardDetailsList(favCardDetailsList);
                                        favouriteCardsCourseDataList.add(favouriteCardsCourseData);
                                    }
                                }

                            }
                        }
                        setToolBarColor();
                        addFavouriteCardInView(favouriteCardsCourseDataList);
                    } catch (Exception e) {
                        setToolBarColor();
                        addFavouriteCardInView(favouriteCardsCourseDataList);
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    Log.e("FirebaseD", "onCancelled()");
                }
            };
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(allfavCardListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            firebaseRefClass=new FirebaseRefClass(allfavCardListener,message);
        }catch (Exception e){
            addFavouriteCardInView(favouriteCardsCourseDataList);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolBarColor(){
        try{
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            TextView titleTextView= toolbar.findViewById(R.id.title);
            titleTextView.setText(getResources().getString(R.string.favourites).toUpperCase()+" ("+numberOfFavCards+") ");
            String toolbarColorCode= OustPreferences.get("toolbarColorCode");
            if((toolbarColorCode!=null)&&(!toolbarColorCode.isEmpty())){
                favourite_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
//            set the total number of cards counter to 0 as it was counting double when coming back from different activity
            numberOfFavCards=0;
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLoader(){

    }

    private void createLoader(){
        try {
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
                    if((favouriteCardsCourseDataList!=null)&&(favouriteCardsCourseDataList.size()>0)) {}else {
                        swipe_refresh_layout.setRefreshing(true);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onCardClick(FavouriteCardsCourseData favouriteCardsCourseData, int cardNo) {
        gotoCardPage(favouriteCardsCourseData,cardNo);
    }

    private CourseDataClass courseDataClass;
    private String filename;
    private void gotoCardPage(FavouriteCardsCourseData favouriteCardsCourseData,int cardNo){
        try {
            if((favouriteCardsCourseData.getFavCardDetailsList().get(cardNo).getRmType()!=null) &&
                    (favouriteCardsCourseData.getFavCardDetailsList().get(cardNo).getRmType().equalsIgnoreCase("pdf"))){
                filename=favouriteCardsCourseData.getFavCardDetailsList().get(cardNo).getRmData();
                ActivityCompat.requestPermissions(FavouriteCardsActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
            } else {
                getUpdateTSForCourse(favouriteCardsCourseData.getCourseId());
                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                for (int i = 0; i < favouriteCardsCourseData.getFavCardDetailsList().size(); i++) {
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    DTOReadMore readMoreData = new DTOReadMore();
                    if (favouriteCardsCourseData.getFavCardDetailsList().get(i).isRMCard()){
                        readMoreData.setRmId(favouriteCardsCourseData.getFavCardDetailsList().get(i).getRmId());
                        readMoreData.setData(favouriteCardsCourseData.getFavCardDetailsList().get(i).getRmData());
                        readMoreData.setDisplayText(favouriteCardsCourseData.getFavCardDetailsList().get(i).getRmDisplayText());
                        readMoreData.setLevelId(favouriteCardsCourseData.getFavCardDetailsList().get(i).getLevelId());
                        readMoreData.setScope(favouriteCardsCourseData.getFavCardDetailsList().get(i).getRmScope());
                        readMoreData.setType(favouriteCardsCourseData.getFavCardDetailsList().get(i).getRmType());
                        courseCardClass.setReadMoreCard(true);
                    }
                    if (readMoreData != null) {
                            courseCardClass.setReadMoreData(readMoreData);
                            courseCardClass.setCardId(Long.parseLong(favouriteCardsCourseData.getFavCardDetailsList().get(i).getCardId()));
                    } else {
                        courseCardClass.setCardId(Long.parseLong(favouriteCardsCourseData.getFavCardDetailsList().get(i).getCardId()));
                    }
                    if((readMoreData != null) && (readMoreData.getType()!=null) && (readMoreData.getType().equalsIgnoreCase("pdf"))) {

                    } else {
                        courseCardClassList.add(courseCardClass);
                    }
                }

                for (int i=0; i<courseCardClassList.size(); i++){
                    if(favouriteCardsCourseData.getFavCardDetailsList().get(cardNo).getCardId().equalsIgnoreCase(""+courseCardClassList.get(i).getCardId())){
                        cardNo=i;
                    }
                }

                courseDataClass.setCourseName(favouriteCardsCourseData.getCourseName());
                courseDataClass.setUpdateTs(updateTS);
                courseDataClass.setCourseId(Long.parseLong(favouriteCardsCourseData.getCourseId()));

                String s1 = "" + activeUser.getStudentKey() + "" + favouriteCardsCourseData.getCourseId();
                long courseUniqueNo = Long.parseLong(s1);
                OustStaticVariableHandling.getInstance().setCourseUniqNo(courseUniqueNo);

                CourseLevelClass courseLevelClass = new CourseLevelClass();
                courseLevelClass.setCourseCardClassList(courseCardClassList);
                courseLevelClass.setLevelName(courseDataClass.getCourseName());

                Gson gson = new Gson();
                String courseLevelStr = gson.toJson(courseLevelClass);
                String courseDataStr = gson.toJson(courseDataClass);

                Intent intent = new Intent(FavouriteCardsActivity.this, LearningMapModuleActivity.class);
//            intent.putExtra("courseLevelStr",courseLevelStr);
//            intent.putExtra("courseDataStr", courseDataStr);
                OustStaticVariableHandling.getInstance().setCourseLevelStr(courseLevelStr);
                OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                intent.putExtra("learningId", courseId);
                intent.putExtra("levelNo", 0);
                intent.putExtra("reviewModeQuestionNo", cardNo);
                intent.putExtra("isReviewMode", true);
                intent.putExtra("favCardMode", true);
                intent.putExtra("updateTS", updateTS);

//            OustAppState.getInstance().setCourseLevelClass(courseLevelClass);
                OustSdkTools.newActivityAnimationB(intent, FavouriteCardsActivity.this);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
//        FavouriteCardsActivity.this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==120){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPdf();
            }
        }
    }

    private void openPdf(){
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
                if(code==_COMPLETED)
                {
                    openPdf();
                    Log.e("PDF", "downloading file complete");
                    downloadpdf_progress_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        try {
            Log.e("PDF", "inside openPdf()");
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + filename)) {
                //initS3Client();
                Log.e("PDF", "pdf not downloaded");
                downLoad(filename, "readmore/file/" + filename);
            } else {
            if (filename != null) {
                Log.e("PDF", "file not null and downloaded");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(), OustSdkApplication.getContext().getApplicationContext().getPackageName()
                            + ".provider", OustSdkTools.getDataFromPrivateStorage(filename));
                    intent.setDataAndType(fileUri, "application/pdf");
                } else {

                    File file = OustSdkTools.getDataFromPrivateStorage(filename);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                }
                startActivity(intent);
            }
        }
        }catch (Exception e){
            Log.e("PDF", "exception occured" +e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int noofTries = 0;
    private TransferUtility transferUtility;

    private void initS3Client() {
        try {
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downLoad(final String fileName1, final String pathName) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                OustSdkTools.showToast("Internet Connection Required");
                return;
            }
            downloadpdf_progress_layout.setVisibility(View.VISIBLE);
            Log.e("PDF", "downloading file");
            String key = pathName;
            String file = FavouriteCardsActivity.this.getFilesDir()+"/";
           // final File file = new File(FavouriteCardsActivity.this.getFilesDir(),"oustlearn_"+fileName1 );
            downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+pathName, file, fileName1,true, false);
          //  downloadFiles.startDownLoad(file.toString(), S3_BUCKET_NAME, pathName, false);

            /*if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            openPdf();
                            Log.e("PDF", "downloading file complete");
                            downloadpdf_progress_layout.setVisibility(View.GONE);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            noofTries++;
                            if (noofTries > 4) {
                                Log.e("PDF", "downloading file failed");
                                OustSdkTools.showToast("Internet Connection Required");
                            } else {
                                downLoad(fileName1, pathName);
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if ((!OustSdkTools.checkInternetStatus())) {
                            return;
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        noofTries++;
                        if (noofTries > 4) {
                            Log.e("PDF", "downloading file error");
                            OustSdkTools.showToast("Internet Connection Required");
                        } else {
                            downLoad(fileName1, pathName);
                        }
                    }
                });
            } else {
            }*/
        } catch (Exception e) {
            Log.e("PDF", "downloading file exception", e);
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
        if(myFileDownLoadReceiver!=null)
        {
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
                            openPdf();
                            Log.e("PDF", "downloading file complete");
                            downloadpdf_progress_layout.setVisibility(View.GONE);
                        }
                        else if(intent.getAction().equalsIgnoreCase(ACTION_ERROR))
                        {
                         //   OustSdkTools.showToast("Download Failed");
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
    //    get the update time stamp to check if course has been updated in Mpower
    private void getUpdateTSForCourse(String courseId){
        List<CourseDataClass> allCources=OustAppState.getInstance().getNewLandingCourses();
        courseDataClass = new CourseDataClass();
        if((allCources!=null)&&(allCources.size()>0)){
            for(int i=0; i<allCources.size(); i++){
                if(courseId.equalsIgnoreCase(""+allCources.get(i).getCourseId())){
                    updateTS=allCources.get(i).getUpdateTs();
                    courseDataClass = allCources.get(i);
                }
            }
        }
    }

    private void addFavouriteCardInView(List<FavouriteCardsCourseData> favouriteCardsCourseData) {
        try {
            if ((favouriteCardsCourseData != null) && (favouriteCardsCourseData.size() > 0)) {
                nocard_text.setVisibility(View.GONE);
                favcard_recyclerview.setVisibility(View.VISIBLE);
                favCourseAdapter = new FavouriteCourseAdapter(favouriteCardsCourseData,FavouriteCardsActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OustSdkApplication.getContext(),LinearLayoutManager.VERTICAL,false);
                favcard_recyclerview.setLayoutManager(mLayoutManager);
                favcard_recyclerview.setItemAnimator(new DefaultItemAnimator());
                favCourseAdapter.setCardClickCallBack(FavouriteCardsActivity.this);
                favcard_recyclerview.setAdapter(favCourseAdapter);
            } else {
                nocard_text.setText(getResources().getString(R.string.no_favourite_card));
                nocard_text.setVisibility(View.VISIBLE);
                favcard_recyclerview.setVisibility(View.GONE);
            }
            swipe_refresh_layout.setRefreshing(false);
            swipe_refresh_layout.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(firebaseRefClass!=null){
            OustFirebaseTools.getRootRef().child(firebaseRefClass.getFirebasePath()).removeEventListener(firebaseRefClass.getEventListener());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FavouriteCardsActivity.this.overridePendingTransition(R.anim.enter,R.anim.exit);
    }
}
