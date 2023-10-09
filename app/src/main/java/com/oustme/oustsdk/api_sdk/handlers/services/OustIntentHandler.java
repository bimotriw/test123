package com.oustme.oustsdk.api_sdk.handlers.services;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CATALOGUE_ID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CATALOG_NAME;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CAT_BANNER;

import android.content.Intent;

import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.activity.common.newcatalogue.NewCatalogActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewCardLauncherActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewCourseLauncherActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.RegularModeLearningMapActivity;
import com.oustme.oustsdk.api_sdk.models.OustCatalogueData;
import com.oustme.oustsdk.api_sdk.models.OustEventCardData;
import com.oustme.oustsdk.api_sdk.models.OustEventCourseData;
import com.oustme.oustsdk.api_sdk.models.OustModuleData;
import com.oustme.oustsdk.api_sdk.models.OustMultilingualCourseData;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

/**
 * Created by oust on 5/6/19.
 */

public class OustIntentHandler {
    private OustModuleData moduleData;
    private OustEventCardData oustEventCardData;
    private OustCatalogueData oustCatalogueData;
    private OustEventCourseData oustEventCourseData;
    private OustMultilingualCourseData oustMultilingualCourseData;

    public OustIntentHandler(){

    }

    public OustIntentHandler(OustModuleData moduleData) {
        this.moduleData = moduleData;
    }

    public OustIntentHandler(OustEventCardData oustEventCardData){
        this.oustEventCardData = oustEventCardData;
    }

    public OustIntentHandler(OustCatalogueData oustCatalogueData){
        this.oustCatalogueData = oustCatalogueData;
    }

    public OustIntentHandler(OustEventCourseData oustEventCourseData){
        this.oustEventCourseData = oustEventCourseData;
    }

    public OustIntentHandler(OustMultilingualCourseData oustMultilingualCourseData){
        this.oustMultilingualCourseData = oustMultilingualCourseData;
    }

    public void launchNewIntent(){
        if (moduleData.getRequestType().equalsIgnoreCase("course")) {
            Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
            intent.putExtra("learningId", moduleData.getId());
            intent.putExtra("isEventLaunch", true);
            intent.putExtra("eventId", moduleData.getEventId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);

        /*} else if (moduleData.getRequestType().equalsIgnoreCase("CPL")) {
            Intent intent = new Intent(OustSdkApplication.getContext(), CplIntroActivity.class);
            //intent.putExtra("assessmentId", moduleData.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);*/

        } else if (moduleData.getRequestType().equalsIgnoreCase("Assessment")) {
            Intent intent;
            if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
                intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            }else{
                intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
            }
            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            intent.putExtra("assessmentId", moduleData.getId());
            intent.putExtra("isEventLaunch", true);
            intent.putExtra("eventId", moduleData.getEventId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);

        } else if (moduleData.getRequestType().equalsIgnoreCase("newsfeed")) {
            Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
            intent.putExtra("feedId", moduleData.getId());
            intent.putExtra("isEventLaunch", true);
            intent.putExtra("eventId", moduleData.getEventId());
            intent.putExtra("type","card");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        }

        callContentLoadedCallback();
    }

    public void launchCardIntent(){
        Intent intent = new Intent(OustSdkApplication.getContext(), NewCardLauncherActivity.class);
        intent.putExtra("courseId", oustEventCardData.getCourseId());
        intent.putExtra("levelId", oustEventCardData.getLevelId());
        intent.putExtra("cardId", oustEventCardData.getCardId());
        intent.putExtra("type","card");
        intent.putExtra("eventId", oustEventCardData.getEventId());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(intent);
        callContentLoadedCallback();
    }

    public void launchCatalogue(){
        Intent intent = new Intent(OustSdkApplication.getContext(), NewCatalogActivity.class);

        if(oustCatalogueData.getBanner()!=null){
            OustPreferences.save(CAT_BANNER, oustCatalogueData.getBanner());
            intent.putExtra(CAT_BANNER, oustCatalogueData.getBanner());
        }
        if(oustCatalogueData.getTitle()!=null) {
            OustPreferences.save(CATALOG_NAME, oustCatalogueData.getTitle());
            intent.putExtra(CATALOG_NAME, oustCatalogueData.getTitle());
        }

        OustPreferences.saveTimeForNotification(CATALOGUE_ID, oustCatalogueData.getCatalogueId());
        intent.putExtra(CATALOGUE_ID, oustCatalogueData.getCatalogueId());

        intent.putExtra("hasDeskData", false);
        //intent.putExtra("deskDataMap", myDeskInfoMap);
        OustSdkApplication.getContext().startActivity(intent);
        callContentLoadedCallback();
    }

    public void launchRegularCourse(){
        if(!OustPreferences.getAppInstallVariable("learning_card_tutorial_shown")){
            OustPreferences.saveAppInstallVariable("learning_card_tutorial_shown", true);
        }

        Intent intent = new Intent(OustSdkApplication.getContext(), RegularModeLearningMapActivity.class);
        intent.putExtra("learningId", oustEventCourseData.getCourseId()+"");
        intent.putExtra("isEventLaunch", true);
        intent.putExtra("eventId", oustEventCourseData.getEventId());
        OustSdkApplication.getContext().startActivity(intent);
        callContentLoadedCallback();
    }

    public void launchCourse(){
        if(!OustPreferences.getAppInstallVariable("learning_card_tutorial_shown")){
            OustPreferences.saveAppInstallVariable("learning_card_tutorial_shown", true);
        }
        Intent intent = new Intent(OustSdkApplication.getContext(), NewCourseLauncherActivity.class);
        intent.putExtra("learningId", ""+oustEventCourseData.getCourseId());
        intent.putExtra("isEventLaunch", true);
        intent.putExtra("eventId", oustEventCourseData.getEventId());
        OustSdkApplication.getContext().startActivity(intent);
        callContentLoadedCallback();
    }

    public void launchCPL(){
        Intent intent = new Intent(OustSdkApplication.getContext(), CplBaseActivity.class);
        intent.putExtra("isLauncher",false);
        if(moduleData!=null){
            intent.putExtra("isEventLaunch", true);
            intent.putExtra("eventId", moduleData.getEventId());
        }
        OustSdkApplication.getContext().startActivity(intent);
        callContentLoadedCallback();
    }

    public void launchMultilingualCourse(){
        if(!OustPreferences.getAppInstallVariable("learning_card_tutorial_shown")){
            OustPreferences.saveAppInstallVariable("learning_card_tutorial_shown", true);
        }

        Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
        intent.putExtra("learningId", oustMultilingualCourseData.getCourseChildId());
        intent.putExtra("multilingualId", oustMultilingualCourseData.getCourseParentId());
        intent.putExtra("isMultiLingual", true);
        intent.putExtra("rateCourse",false);
        intent.putExtra("isEventLaunch", true);
        intent.putExtra("eventId", oustMultilingualCourseData.getEventId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(intent);
    }

    private void callContentLoadedCallback(){
        if(OustStaticVariableHandling.getInstance().getOustApiListener()!=null) {
            OustStaticVariableHandling.getInstance().getOustApiListener().onOustContentLoaded();
        }
    }
}
