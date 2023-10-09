package com.oustme.oustsdk.activity.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.oustme.oustsdk.adapter.common.AllFavCardAdapter;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.CardClickCallBack;
import com.oustme.oustsdk.response.common.FavouriteCardsCourseData;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.ReadMoreData;
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
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;


/**
 * Created by shilpysamaddar on 16/05/17.
 */

public class AllFavouriteCardsOfCourseActivity extends AppCompatActivity implements CardClickCallBack {
    private RecyclerView allcards_recyclerview;
    private Toolbar toolbar;
    private AppBarLayout favouritecards_appbar;
    private TextView nocard_text;
    private SwipeRefreshLayout swipe_refresh_layout;
    private AllFavCardAdapter allFavCardAdapter;
    private String courseName, CourseId;
    private ActiveUser activeUser;
    private String courseId;
    private String updateTS;
    private List<FavouriteCardsCourseData> favouriteCardsCourseDataList = new ArrayList<>();
    private RelativeLayout downloadpdf_progress_layout;
    private DownloadFiles downloadFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(AllFavouriteCardsOfCourseActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_allfavouritecardsofclass);
        try{
            activeUser= OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                Log.e("Active user", "active user is  null ");
                OustSdkApplication.setmContext(AllFavouriteCardsOfCourseActivity.this);
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
        Intent intent = getIntent();
        courseName = intent.getStringExtra("courseName");
        courseId = intent.getStringExtra("courseId");
        Log.e("Favourite", courseId);
        setToolBarColor();
        getFavDataFromFirebase();
//        OustGATools.getInstance().reportPageViewToGoogle(AllFavouriteCardsOfCourseActivity.this, "See All Page of favourite Card");

    }

    private void initViews() {
        allcards_recyclerview = findViewById(R.id.allcards_recyclerview);
        toolbar = findViewById(R.id.tabanim_toolbar);
        favouritecards_appbar = findViewById(R.id.favouritecards_appbar);
        nocard_text = findViewById(R.id.nocard_text);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        downloadpdf_progress_layout= findViewById(R.id.downloadpdf_progress_layout);
    }

    private void setToolBarColor() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            TextView titleTextView = toolbar.findViewById(R.id.title);
            titleTextView.setText(courseName.toUpperCase());
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                favouritecards_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getFavDataFromFirebase() {
        try {
            String message = "/userFavouriteCards/" + "user" + activeUser.getStudentKey();
            ValueEventListener allfavCard = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            final Map<String, Object> allfavCourseMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (allfavCourseMap != null) {

                                for (String coursekey : allfavCourseMap.keySet()) {
//                                courseId=coursekey.substring(6);
                                    Log.e("FAVOURITE", courseId);
                                    Object courseObject = allfavCourseMap.get(coursekey);
                                    final Map<String, Object> allfavCardMap = (Map<String, Object>) courseObject;
                                    if ((allfavCardMap.get("courseName") != null) && ((allfavCardMap.get("cards") != null) || (allfavCardMap.get("readMore") != null))) {
                                        FavouriteCardsCourseData favouriteCardsCourseData = new FavouriteCardsCourseData();
                                        List<FavCardDetails> favCardDetailsList = new ArrayList<>();
                                        favouriteCardsCourseData.setCourseName((String) allfavCardMap.get("courseName"));
                                        favouriteCardsCourseData.setCourseId(courseId);

                                        if (allfavCardMap.get("cards") != null) {
                                            Map<String, Object> cardMap = new HashMap<>();
                                            Object o1 = allfavCardMap.get("cards");
                                            if (o1.getClass().equals(HashMap.class)) {
                                                cardMap = (Map<String, Object>) o1;
                                                if (cardMap != null) {
                                                    Map<String, Object> carddetailsMap = new HashMap<String, Object>();
                                                    for (String key : cardMap.keySet()) {
                                                        Object details = cardMap.get(key);
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
                                                            if (carddetailsMap.get("mediaType") != null)
                                                                favCardDetails.setMediaType((String) carddetailsMap.get("mediaType"));
                                                            favCardDetailsList.add(favCardDetails);
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                        if (allfavCardMap.get("readMore") != null) {
                                            Map<String, Object> readmoreMap = new HashMap<>();
                                            Object o1 = allfavCardMap.get("readMore");
                                            if (o1.getClass().equals(HashMap.class)) {
                                                readmoreMap = (Map<String, Object>) o1;
                                                if (readmoreMap != null) {
                                                    Map<String, Object> rmDetailMap = new HashMap<String, Object>();
                                                    for (String key : readmoreMap.keySet()) {
                                                        Object details = readmoreMap.get(key);
                                                        if (details != null) {
                                                            rmDetailMap = (Map<String, Object>) details;
                                                            FavCardDetails favCardDetails = new FavCardDetails();
                                                            if (rmDetailMap.get("cardId") != null)
                                                                favCardDetails.setCardId((String) rmDetailMap.get("cardId"));
                                                            if (rmDetailMap.get("levelId") != null)
                                                                favCardDetails.setLevelId((String) rmDetailMap.get("levelId"));
                                                            if (rmDetailMap.get("rmId") != null) {
//                                                                favCardDetails.setRmId((Long) rmDetailMap.get("rmId"));
//                                                            favCardDetails.setRMCard(true);
                                                                if (rmDetailMap.get("rmId").getClass().equals(String.class)) {
                                                                    favCardDetails.setRmId(Long.parseLong((String) rmDetailMap.get("rmId")));
                                                                    favCardDetails.setRMCard(true);
                                                                } else {
                                                                    favCardDetails.setRmId((long) rmDetailMap.get("rmId"));
                                                                    favCardDetails.setRMCard(true);
                                                                }
                                                            }

                                                            if (rmDetailMap.get("rmData") != null)
                                                                favCardDetails.setRmData((String) rmDetailMap.get("rmData"));
                                                            if (rmDetailMap.get("rmScope") != null)
                                                                favCardDetails.setRmScope((String) rmDetailMap.get("rmScope"));
                                                            if (rmDetailMap.get("rmDisplayText") != null)
                                                                favCardDetails.setRmDisplayText((String) rmDetailMap.get("rmDisplayText"));
                                                            if (rmDetailMap.get("rmType") != null)
                                                                favCardDetails.setRmType((String) rmDetailMap.get("rmType"));
                                                            favCardDetailsList.add(favCardDetails);
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                        favouriteCardsCourseData.setFavCardDetailsList(favCardDetailsList);
                                        favouriteCardsCourseDataList.add(favouriteCardsCourseData);
                                    }
                                }
                                getFavouriteCourseAndCard(favouriteCardsCourseDataList);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    Log.e("FirebaseD", "onCancelled()");
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(allfavCard);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addCardsOfCourse(List<FavCardDetails> favCardDetailsList) {
        nocard_text.setVisibility(View.GONE);
        allcards_recyclerview.setVisibility(View.VISIBLE);
        if (allFavCardAdapter == null) {
            allFavCardAdapter = new AllFavCardAdapter(favCardDetailsList, AllFavouriteCardsOfCourseActivity.this, courseId, courseName);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(AllFavouriteCardsOfCourseActivity.this, 2);
            allcards_recyclerview.setLayoutManager(mLayoutManager);
            allcards_recyclerview.setItemAnimator(new DefaultItemAnimator());
            allFavCardAdapter.setCardClickCallBack(AllFavouriteCardsOfCourseActivity.this);
            allcards_recyclerview.setAdapter(allFavCardAdapter);
        }
    }

    private void getFavouriteCourseAndCard(List<FavouriteCardsCourseData> favouriteCardsCourseDataList) {
        try {
            if (favouriteCardsCourseDataList != null) {
                for (int i = 0; i < favouriteCardsCourseDataList.size(); i++) {
                    if (courseName.equalsIgnoreCase(favouriteCardsCourseDataList.get(i).getCourseName())) {
                        addCardsOfCourse(favouriteCardsCourseDataList.get(i).getFavCardDetailsList());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onCardClick(FavouriteCardsCourseData favouriteCardsCourseData, int cardNo) {
        gotoCardPage(favouriteCardsCourseData, cardNo);
    }


    private CourseDataClass courseDataClass;
    private String filename;
    private void gotoCardPage(FavouriteCardsCourseData favouriteCardsCourseData,int cardNo){
        try {
            if((favouriteCardsCourseData.getFavCardDetailsList().get(cardNo).getRmType()!=null) &&
                    (favouriteCardsCourseData.getFavCardDetailsList().get(cardNo).getRmType().equalsIgnoreCase("pdf"))){
                filename=favouriteCardsCourseData.getFavCardDetailsList().get(cardNo).getRmData();
                ActivityCompat.requestPermissions(AllFavouriteCardsOfCourseActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
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

                Intent intent = new Intent(AllFavouriteCardsOfCourseActivity.this, LearningMapModuleActivity.class);
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
                OustSdkTools.newActivityAnimationB(intent, AllFavouriteCardsOfCourseActivity.this);
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
        try {
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
                    if(code==_COMPLETED){
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

            String path = AllFavouriteCardsOfCourseActivity.this.getFilesDir()+"/";
            downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+pathName, path, fileName1, true, false);

            final File file = new File(AllFavouriteCardsOfCourseActivity.this.getFilesDir(),"oustlearn_"+fileName1 );
            //downloadFiles.startDownLoad(file.toString(), S3_BUCKET_NAME, pathName, false, true);

           /*
            if (file != null) {
                TransferObserver transferObserver = transferUtility.download(S3_BUCKET_NAME, key, file);
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


    //    get the update time stamp to check if course has been updated in Mpower
    private void getUpdateTSForCourse(String courseId) {
        List<CourseDataClass> allCources = OustAppState.getInstance().getNewLandingCourses();
        courseDataClass = new CourseDataClass();
        if ((allCources != null) && (allCources.size() > 0)) {
            for (int i = 0; i < allCources.size(); i++) {
                if (courseId.equalsIgnoreCase("" + allCources.get(i).getCourseId())) {
                    updateTS = allCources.get(i).getUpdateTs();
                    courseDataClass = allCources.get(i);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent=new Intent(AllFavouriteCardsOfCourseActivity.this, FavouriteCardsActivity.class);
//        OustSdkTools.newActivityAnimationE(intent,AllFavouriteCardsOfCourseActivity.this);
//        AllFavouriteCardsOfCourseActivity.this.finish();

        AllFavouriteCardsOfCourseActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);

//        Intent intent=new Intent(this,FavouriteCardsActivity.class);
//        startActivity(intent);
//        OustSdkTools.newActivityAnimationE(intent,AllFavouriteCardsOfCourseActivity.this);
//        AllFavouriteCardsOfCourseActivity.this.finish();
    }
}
