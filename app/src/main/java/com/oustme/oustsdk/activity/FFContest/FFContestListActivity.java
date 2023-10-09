package com.oustme.oustsdk.activity.FFContest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
import com.oustme.oustsdk.adapter.FFContest.FFContestListAdapter;
import com.oustme.oustsdk.firebase.FFContest.BasicQuestionClass;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.net.time.TimeTCPClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 16/08/17.
 */

public class FFContestListActivity extends AppCompatActivity implements View.OnClickListener, RowClickCallBack {

    private Toolbar toolbar;
    private TextView screen_name;
    private ImageView back_button;
    private ImageView contest_bannerImg;
    private ImageButton closeBtn;
    private ProgressBar contest_progressbar;
    private RecyclerView contestList;
    private TextView noconteststext;
    private TextView contentonimage;
    private List<FastestFingerContestData> fastestFingerContestDataList = new ArrayList<>();
    private ActiveUser activeUser;

    private int color;
    private int bgColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(FFContestListActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.contests_list);
        getColors();
        intiViews();
        contest_progressbar.setVisibility(View.VISIBLE);
        activeUser = OustAppState.getInstance().getActiveUser();
        setContestHistoryBanner();
        getUserFFContest();
        /*if(OustSdkTools.checkInternetStatus()){
            getInterNetTime();
        }*/
        netTime = System.currentTimeMillis();
//        OustGATools.getInstance().reportPageViewToGoogle(FFContestListActivity.this, "FFContest List Page");

    }

    TimeTCPClient client = new TimeTCPClient();
    private int retryCount = 0;
    private long netTime = 0;
    /*public void getInterNetTime(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set timeout of 60 seconds
                    client.setDefaultTimeout(20000);
                    // Connecting to time server
                    // Other time servers can be found at : http://tf.nist.gov/tf-cgi/servers.cgi#
                    // Make sure that your program NEVER queries a server more frequently than once every 4 seconds
                    String hostName="time.nist.gov";
                    if((OustPreferences.get("networkTimeProviderHostName")!=null)&&(!OustPreferences.get("networkTimeProviderHostName").isEmpty())){
                        hostName=OustPreferences.get("networkTimeProviderHostName");
                    }
                    client.connect(hostName);
                    netTime= client.getDate().getTime();
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            gotNetTime();
                        }
                    };
                    mainHandler.post(myRunnable);
                }catch (Exception e){
                    retryCount++;
                    if(retryCount<4){
                        getNetTimeFailed();
                    }
                }
            }
        });
    }*/

   /* private void getNetTimeFailed(){
        Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getInterNetTime();
            }
        };
        mainHandler.post(myRunnable);
    }*/

    private long differenceInTime = 0;

    private void gotNetTime() {
        if (netTime > 0) {
            long currentTime = System.currentTimeMillis();
            differenceInTime = (netTime - currentTime);
        }
    }

    public long getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        return (currentTime + differenceInTime);
    }


    @SuppressLint("SetTextI18n")
    private void intiViews() {
        contest_bannerImg = findViewById(R.id.contest_bannerImg);
        closeBtn = findViewById(R.id.closeBtn);
        contest_progressbar = findViewById(R.id.contest_progressBar);
        contestList = findViewById(R.id.contestList);
        noconteststext = findViewById(R.id.noconteststext);
        contentonimage = findViewById(R.id.contentonimage);
        OustSdkTools.setImage(contest_bannerImg, getResources().getString(R.string.contests));

        closeBtn.setOnClickListener(this);

        toolbar = findViewById(R.id.toolbar_lay_ffc);
        screen_name = findViewById(R.id.screen_name);
        back_button = findViewById(R.id.back_button);
        toolbar.setBackgroundColor(bgColor);
        screen_name.setTextColor(color);
        screen_name.setText("Contest");
        setSupportActionBar(toolbar);
        OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
        toolbar.setTitle("");

        back_button.setOnClickListener(view -> onBackPressed());
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setContestHistoryBanner() {
        String contestHistoryBanner = OustPreferences.get("contestHistoryBanner");
        if ((contestHistoryBanner != null) && (!contestHistoryBanner.isEmpty())) {
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(contestHistoryBanner).into(contest_bannerImg);
            } else {
                Picasso.get().load(contestHistoryBanner).networkPolicy(NetworkPolicy.OFFLINE).into(contest_bannerImg);
            }
        }
    }


    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    public void onClick(View v) {
        try {
            OustSdkTools.oustTouchEffect(v, 100);
            if (v.getId() == R.id.closeBtn) {
                this.finish();
            }
        } catch (Exception e) {
        }
    }

    private int totalContest = 0;

    public void getUserFFContest() {
        try {
            final String message = "/f3cUserContestRef/" + activeUser.getStudentKey();
            ValueEventListener myFFCListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> ffcMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String ffcKey : ffcMainMap.keySet()) {
                                    if (ffcKey != null) {
                                        boolean registerStatus = true;
                                        if ((ffcMainMap.get(ffcKey) != null)) {
                                            registerStatus = (boolean) ffcMainMap.get(ffcKey);
                                        }
                                        ffcKey = ffcKey.replace("f3c", "");
                                        long contestId = Long.parseLong(ffcKey);
                                        totalContest++;
                                        fetctFFCData(contestId, registerStatus);
                                    }
                                }
                            }
                        } else {
                            setListData(true);
                        }
                    } catch (Exception e) {
                    }
                    contest_progressbar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
            newsfeedRef.keepSynced(true);
            newsfeedRef.addListenerForSingleValueEvent(myFFCListener);

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if (!connected) {
                            setListData(false);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            };
            OustFirebaseTools.getRootRef().child(".info/connected");
            OustFirebaseTools.getRootRef().child(".info/connected").addListenerForSingleValueEvent(listener);
        } catch (Exception e) {
        }
    }


    public void fetctFFCData(final long ffcId, final boolean registerStatus) {
        try {
            final String message = "/f3cData/f3c" + ffcId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    extractFFCData(ffcId, dataSnapshot, registerStatus);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(ffcDataListener);


        } catch (Exception e) {
        }
    }


    private void extractFFCData(final long ffcId, DataSnapshot dataSnapshot, final boolean registerStatus) {
        try {
            if (dataSnapshot != null) {
                final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                if (null != ffcDataMap) {
                    try {
                        FastestFingerContestData fastestFingerContestData = new FastestFingerContestData();
                        fastestFingerContestData.setEnrolled(registerStatus);
                        fastestFingerContestData.setFfcId(ffcId);
                        fastestFingerContestData = OustSdkTools.getFastestFingerContestData(fastestFingerContestData, ffcDataMap);
                        fastestFingerContestDataList.add(fastestFingerContestData);
                        if (totalContest == fastestFingerContestDataList.size()) {
                            startFetchQData();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void startFetchQData() {
        try {
            for (int i = 0; i < fastestFingerContestDataList.size(); i++) {
                fetchQData((fastestFingerContestDataList.get(i).getFfcId()));
            }
        } catch (Exception e) {
        }
    }

    private void fetchQData(final long ffcId) {
        try {
            final String message = "/f3cQData/f3c" + ffcId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (null != ffcDataMap) {
                                List<BasicQuestionClass> basicQuestionClassList = new ArrayList<>();
                                if (ffcDataMap.get("questions") != null) {
                                    Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("questions");
                                    if (questionMap != null) {
                                        for (String key : questionMap.keySet()) {
                                            Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                            if (questionSubMap != null) {
                                                BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                                if (questionSubMap.get("qId") != null) {
                                                    basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                                }
                                                if (questionSubMap.get("sequence") != null) {
                                                    basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                                }
                                                basicQuestionClassList.add(basicQuestionClass);
                                            }
                                        }
                                    }
                                    Collections.sort(basicQuestionClassList, questionSorter);
                                }
                                List<BasicQuestionClass> basicWarmUpQuestionClassList = new ArrayList<>();
                                if (ffcDataMap.get("warmupQuestions") != null) {
                                    Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("warmupQuestions");
                                    if (questionMap != null) {
                                        for (String key : questionMap.keySet()) {
                                            Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                            if (questionSubMap != null) {
                                                BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                                if (questionSubMap.get("qId") != null) {
                                                    basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                                }
                                                if (questionSubMap.get("sequence") != null) {
                                                    basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                                }
                                                basicWarmUpQuestionClassList.add(basicQuestionClass);
                                            }
                                        }
                                    }
                                    Collections.sort(basicWarmUpQuestionClassList, questionSorter);
                                }
                                long updateChecksum = 0;
                                if (ffcDataMap.get("updateChecksum") != null) {
                                    updateChecksum = (long) ffcDataMap.get("updateChecksum");
                                }
                                boolean update = true;
                                String s1 = ("ffcupdateChecksum" + ffcId);
                                if ((updateChecksum > 0) && (OustPreferences.getTimeForNotification(s1) > 0) && (updateChecksum == OustPreferences.getTimeForNotification(s1))) {
                                    update = false;
                                }

                                List<String> qList = new ArrayList<>();
                                for (int i = 0; i < basicQuestionClassList.size(); i++) {
                                    if (basicQuestionClassList.get(i).getqId() > 0) {
                                        qList.add(("" + basicQuestionClassList.get(i).getqId()));
                                    }
                                }

                                List<String> warmUpQList = new ArrayList<>();
                                for (int i = 0; i < basicWarmUpQuestionClassList.size(); i++) {
                                    if (basicWarmUpQuestionClassList.get(i).getqId() > 0) {
                                        warmUpQList.add(("" + basicWarmUpQuestionClassList.get(i).getqId()));
                                    }
                                }

                                for (int i = 0; i < fastestFingerContestDataList.size(); i++) {
                                    if (ffcId == fastestFingerContestDataList.get(i).getFfcId()) {
                                        fastestFingerContestDataList.get(i).setqIds(qList);
                                        fastestFingerContestDataList.get(i).setWarmupQList(warmUpQList);
                                        fastestFingerContestDataList.get(i).setUpdateQuestion(update);
                                    }
                                }
                            }
                        }
                        if (totalContest == fastestFingerContestDataList.size()) {
                            setListData(true);
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(ffcDataListener);
        } catch (Exception e) {
        }
    }

    public Comparator<BasicQuestionClass> questionSorter = new Comparator<BasicQuestionClass>() {
        public int compare(BasicQuestionClass s1, BasicQuestionClass s2) {
            return ((int) s1.getSequence()) - ((int) s2.getSequence());
        }
    };


    public Comparator<FastestFingerContestData> contestSorter = new Comparator<FastestFingerContestData>() {
        public int compare(FastestFingerContestData s1, FastestFingerContestData s2) {
            if (s1.getStartTime() > s2.getStartTime()) {
                return -1;
            } else if (s1.getStartTime() < s2.getStartTime()) {
                return 1;
            }
            return 0;
        }
    };

    private void downloadQuestion(final String qId, final long updateChecksum, final long ffcId) {
        try {
            final String message = "/questions/Q" + qId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Map<String, Object> questionMap = (Map<String, Object>) dataSnapshot.getValue();
                        EncrypQuestions encrypQuestions = new EncrypQuestions();
                        if (questionMap != null) {
                            if (questionMap.get("image") != null) {
                                encrypQuestions.setImage((String) questionMap.get("image"));
                            }
                            if (questionMap.get("encryptedQuestions") != null) {
                                encrypQuestions.setEncryptedQuestions((String) questionMap.get("encryptedQuestions"));
                            }
                            DTOQuestions questions = OustSdkTools.decryptQuestion(encrypQuestions, null);
                            OustSdkTools.databaseHandler.addToRealmQuestions(questions, true);
                            String s1 = ("ffcupdateChecksum" + ffcId);
                            OustPreferences.saveTimeForNotification(s1, updateChecksum);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(ffcDataListener);
        } catch (Exception e) {
        }
    }


    private FFContestListAdapter ffContestListAdapter;

    public void setListData(boolean internetStatus) {
        try {
            contest_progressbar.setVisibility(View.GONE);
            if ((fastestFingerContestDataList != null) && (fastestFingerContestDataList.size() > 0)) {
                Collections.sort(fastestFingerContestDataList, contestSorter);
//                fastestFingerContestDataList=sortF3cDaatList();
                contestList.setVisibility(View.VISIBLE);
                noconteststext.setVisibility(View.GONE);
                ffContestListAdapter = new FFContestListAdapter(fastestFingerContestDataList, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FFContestListActivity.this);
                contestList.setLayoutManager(mLayoutManager);
                contestList.setItemAnimator(new DefaultItemAnimator());
                contestList.setAdapter(ffContestListAdapter);
            } else {
                contestList.setVisibility(View.GONE);
                noconteststext.setVisibility(View.VISIBLE);
                if (!internetStatus) {
                    noconteststext.setText(getResources().getString(R.string.no_internet_connection));
                } else {
                    noconteststext.setText(getResources().getString(R.string.no_contest));
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onMainRowClick(String name, int position) {
        contest_progressbar.setVisibility(View.VISIBLE);
        FastestFingerContestData fastestFingerContestData = fastestFingerContestDataList.get(position);
        for (int i = 0; i < fastestFingerContestData.getqIds().size(); i++) {
            if (fastestFingerContestData.isUpdateQuestion()) {
                downloadQuestion(("" + fastestFingerContestData.getqIds().get(i)), fastestFingerContestData.getUpdateChecksum(), fastestFingerContestData.getFfcId());
            }
        }
        if (netTime > 0) {
            long currentTime = getCurrentTime();
            long totalContestTime = 0;
            if (fastestFingerContestDataList.get(position).getqIds() != null) {
                totalContestTime = ((fastestFingerContestDataList.get(position).getQuestionTime() * fastestFingerContestDataList.get(position).getqIds().size()) +
                        (fastestFingerContestDataList.get(position).getRestTime() * (fastestFingerContestDataList.get(position).getqIds().size() - 1))) +
                        fastestFingerContestData.getConstructingLBTime();
            }
            Intent intent = new Intent(FFContestListActivity.this, FFContestLBActivity.class);
            if (((totalContestTime) + fastestFingerContestDataList.get(position).getStartTime()) > currentTime) {

                intent = new Intent(FFContestListActivity.this, FFcontestStartActivity.class);
            }
            Gson gson = new Gson();
            intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
            startActivity(intent);

        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
        }
        contest_progressbar.setVisibility(View.GONE);

    }

    //
//    }

}
