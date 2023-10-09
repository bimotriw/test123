package com.oustme.oustsdk.activity.courses.newlearnngmap;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.FeedCardActivity1;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Map;

public class NewCardLauncherActivity extends AppCompatActivity {

    private static final String TAG = "NewCardLauncher";
    private DTOCourseCard mcourseCardClass = null;
    private long courseId = 0;
    private long levelId = 0;
    private long cardId = 0;
    private int eventId = 0;
    private boolean isLauncherCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Launching Card");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        OustSdkTools.setLocale(NewCardLauncherActivity.this);
        setContentView(R.layout.activity_new_card_launcher);

        isLauncherCalled = false;
        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId",0);
        levelId = intent.getLongExtra("levelId",0);
        cardId = intent.getLongExtra("cardId",0);
        eventId = intent.getIntExtra("eventId",0);
        getLearningMap(courseId, levelId, cardId);
    }

    void getLearningMap(final long courseId, final long levelId, final long cardid){
        Log.e(TAG, "inside load data from firebase ");
        String message = "/course/course" + courseId+"/levels/"+levelId+"/cards/"+cardid;
        Log.d(TAG,"message:"+message);
        ValueEventListener learningMapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        Log.e(TAG, "got data from firebase ");
                        final Map<String, Object> cardMap = (Map<String, Object>) dataSnapshot.getValue();
                        extractCourseDataByTools(cardMap);
                    }
                    goToNextPage();

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                Log.d(TAG,"Oncancelled");
                goToNextPage();
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void extractCourseDataByTools(Map<String, Object> cardMap){
        CommonTools commonTools = new CommonTools();
        mcourseCardClass = commonTools.getCardFromMap(cardMap);
    }

    private void goToNextPage(){
        if(mcourseCardClass!=null){
            OustDataHandler.getInstance().setCourseCardClass(mcourseCardClass);
            Intent intent = new Intent(NewCardLauncherActivity.this, FeedCardActivity1.class);
            intent.putExtra("type", "card");
            intent.putExtra("isEventCard",true);
            intent.putExtra("eventId", eventId);
            intent.putExtra("courseId", courseId);
            intent.putExtra("levelId", levelId);
            intent.putExtra("cardId", cardId);

            isLauncherCalled = true;
            NewCardLauncherActivity.this.startActivity(intent);
        }else{
            OustSdkTools.showToast("Card details not found");
            NewCardLauncherActivity.this.finish();
        }
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLauncherCalled){
            NewCardLauncherActivity.this.finish();
        }
    }
}
