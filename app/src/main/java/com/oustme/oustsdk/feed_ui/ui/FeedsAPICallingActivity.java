package com.oustme.oustsdk.feed_ui.ui;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.GameLetActivity;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.activity.common.ZoomBaseActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.course_ui.IntroScormCardActivity;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.model.request.CplDistributionData;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ClickedFeedRequestData;
import com.oustme.oustsdk.request.ViewedFeedData;
import com.oustme.oustsdk.response.CheckModuleDistributedOrNot;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails.FeedDetails;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedsAPICallingActivity extends Activity {

    String TAG = "FeedsAPICallingActivity";

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    boolean isAttach = false, isMultipleCpl, isComment;
    String feedType = "";
    DTOUserFeedDetails dtoUserFeedDetails = new DTOUserFeedDetails();
    ActiveUser activeUser;
    private HashMap<String, String> myDeskMap;
    private DTOUserFeeds.FeedList userFeed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_api_calling);
        try {
            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = branding_mani_layout.findViewById(R.id.branding_bg);
            branding_icon = branding_mani_layout.findViewById(R.id.brand_loader);
            branding_percentage = branding_mani_layout.findViewById(R.id.percentage_text);
            //End

            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
                } else {
                    String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                    Picasso.get().load(tenantBgImage).into(branding_bg);
                }

                File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                if (brandingLoader.exists()) {
                    Picasso.get().load(brandingLoader).into(branding_icon);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                }
            }

            Intent intent = getIntent();
            if (intent != null) {
                isComment = intent.getBooleanExtra("isComment", false);
                isAttach = intent.getBooleanExtra("FeedAttach", false);
                feedType = intent.getStringExtra("FeedType");
                isMultipleCpl = intent.getBooleanExtra("isMultipleCpl", false);
                if (intent.getExtras() != null) {
                    userFeed = intent.getExtras().getParcelable("userFeed");
                }
            }

            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            myDeskMap = (HashMap<String, String>) getIntent().getExtras().getSerializable("deskDataMap");

            if (userFeed.getFeedId() != 0) {
                branding_mani_layout.setVisibility(View.VISIBLE);
                runOnUiThread(() -> callApi(userFeed.getFeedId()));
            } else {
                branding_mani_layout.setVisibility(View.GONE);
                finish();
            }
        } catch (Exception e) {
            branding_mani_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            finish();
        }
    }

    private void callApi(int feedId) {
        String userFeedsDetails = OustSdkApplication.getContext().getResources().getString(R.string.userFeedDetails);
        userFeedsDetails = userFeedsDetails.replace("{feedId}", String.valueOf(feedId));
        userFeedsDetails = userFeedsDetails.replace("{userId}", activeUser.getStudentid());
        try {
            userFeedsDetails = HttpManager.getAbsoluteUrl(userFeedsDetails);
            ApiCallUtils.doNetworkCall(Request.Method.GET, userFeedsDetails, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: user feeds:" + response.toString());
                    Gson gson = new Gson();
                    dtoUserFeedDetails = gson.fromJson(response.toString(), DTOUserFeedDetails.class);
                    if (dtoUserFeedDetails != null) {
                        try {
                            String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + dtoUserFeedDetails.feedDetails.getFeedId();
                            ValueEventListener userFeedsData = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final Map<String, Object> userFeedsDataVal = (Map<String, Object>) snapshot.getValue();
                                    if (userFeedsDataVal != null) {
                                        if (userFeedsDataVal.get("isLiked") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setLiked((boolean) userFeedsDataVal.get("isLiked"));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setLiked(false);
                                        }
                                        if (userFeedsDataVal.get("isClicked") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setClicked((boolean) userFeedsDataVal.get("isClicked"));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setClicked(false);
                                        }
                                        if (userFeedsDataVal.get("isCommented") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setCommented((boolean) userFeedsDataVal.get("isCommented"));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setCommented(false);
                                        }
                                        if (userFeedsDataVal.get("isFeedViewed") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setFeedViewed((boolean) userFeedsDataVal.get("isFeedViewed"));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setFeedViewed(false);
                                        }
                                        if (userFeedsDataVal.get("timeStamp") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setTimeStamp(OustSdkTools.newConvertToLong(userFeedsDataVal.get("timeStamp")));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setTimeStamp(0);
                                        }
                                        if (userFeedsDataVal.get("feedCoinsAdded") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setFeedCoinsAdded((boolean) userFeedsDataVal.get("feedCoinsAdded"));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setFeedCoinsAdded(false);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e(TAG, "onCancelled: --> " + error.getMessage());
                                    branding_mani_layout.setVisibility(View.GONE);
                                    Toast.makeText(FeedsAPICallingActivity.this, getResources().getString(R.string.check_data_is_or_not), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            };
                            DatabaseReference newsFeedRef = OustFirebaseTools.getRootRef().child(message);
                            newsFeedRef.keepSynced(true);
                            newsFeedRef.addListenerForSingleValueEvent(userFeedsData);
                            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(userFeedsData, message));
                        } catch (Exception e) {
                            branding_mani_layout.setVisibility(View.GONE);
                            Toast.makeText(FeedsAPICallingActivity.this, getResources().getString(R.string.check_data_is_or_not), Toast.LENGTH_SHORT).show();
                            finish();
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }

                        try {
                            String feedDetailsMes = "/feeds/feed" + dtoUserFeedDetails.getFeedDetails().getFeedId();
                            Log.d(TAG, "getUserNewsFeed: " + feedDetailsMes);
                            ValueEventListener feedsNodeData = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final Map<String, Object> feedsNodeDataVal = (Map<String, Object>) snapshot.getValue();
                                    if (feedsNodeDataVal != null) {
                                        if (feedsNodeDataVal.get("numLikes") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setNumLikes(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numLikes"))));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setNumLikes(0);
                                        }
                                        if (feedsNodeDataVal.get("numComments") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setNumComments(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numComments"))));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setNumComments(0);
                                        }
                                        if (feedsNodeDataVal.get("numShares") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setNumShares(Math.toIntExact(OustSdkTools.newConvertToLong(feedsNodeDataVal.get("numShares"))));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setNumShares(0);
                                        }
                                        if (feedsNodeDataVal.get("shareable") != null) {
                                            dtoUserFeedDetails.getFeedDetails().setShareable((boolean) feedsNodeDataVal.get("shareable"));
                                        } else {
                                            dtoUserFeedDetails.getFeedDetails().setShareable(false);
                                        }
                                        if (dtoUserFeedDetails.getFeedDetails().getCardInfo() != null) {
                                            dtoUserFeedDetails.getFeedDetails().setCourseCardClass(BranchTools.getCardFromMap(dtoUserFeedDetails.getFeedDetails().getCardInfo()));
                                        }
                                        feedRedirect(dtoUserFeedDetails.getFeedDetails());
                                    } else {
                                        branding_mani_layout.setVisibility(View.GONE);
                                        Toast.makeText(FeedsAPICallingActivity.this, getResources().getString(R.string.check_data_is_or_not), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e(TAG, "onCancelled:: --> " + error.getMessage());
                                    branding_mani_layout.setVisibility(View.GONE);
                                    Toast.makeText(FeedsAPICallingActivity.this, getResources().getString(R.string.check_data_is_or_not), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            };
                            DatabaseReference newsFeeds = OustFirebaseTools.getRootRef().child(feedDetailsMes);
                            Query query = newsFeeds.orderByChild("timeStamp");
                            query.keepSynced(true);
                            query.addListenerForSingleValueEvent(feedsNodeData);
                            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(feedsNodeData, feedDetailsMes));
                        } catch (Exception e) {
                            branding_mani_layout.setVisibility(View.GONE);
                            Toast.makeText(FeedsAPICallingActivity.this, getResources().getString(R.string.check_data_is_or_not), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                            finish();
                        }
                    } else {
                        branding_mani_layout.setVisibility(View.GONE);
                        Toast.makeText(FeedsAPICallingActivity.this, getResources().getString(R.string.check_data_is_or_not), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    branding_mani_layout.setVisibility(View.GONE);
                    Log.d(TAG, "onResponse: Error:" + error.getMessage());
                    Toast.makeText(FeedsAPICallingActivity.this, getResources().getString(R.string.check_data_is_or_not), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedRedirect(FeedDetails feedDetails) {
        try {
            updateFeedViewed(feedDetails);
            feedDetails.setClicked(true);
            onFeedClick(feedDetails.getFeedId());
            feedRewardUpdate(feedDetails);
            String feedType = feedDetails.getType();
            if (feedDetails.getCardInfo() != null && !feedType.equalsIgnoreCase("COURSE_CARD_L")) {
                branding_mani_layout.setVisibility(View.GONE);
                OustDataHandler.getInstance().setCourseCard(feedDetails);
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                intent.putExtra("type", "card");
                intent.putExtra("Feed", feedDetails);
                intent.putExtra("timeStamp", userFeed.getDistributedOn());
                startActivityForResult(intent, 1444);
                finish();
            } else {
                if (!feedType.isEmpty() && !isComment) {
                    switch (feedType) {
                        case "APP_UPGRADE":
                            branding_mani_layout.setVisibility(View.GONE);
                            rateApp();
                            break;
                        case "GAMELET_WORDJUMBLE":
                        case "GAMELET_WORDJUMBLE_V2":
                        case "GAMELET_WORDJUMBLE_V3":
                            branding_mani_layout.setVisibility(View.GONE);
                            gotoGamelet("" + feedDetails.getDeepLinkInfo().getAssessmentId(), feedType);
                            break;
                        case "JOIN_MEETING":
                            branding_mani_layout.setVisibility(View.GONE);
                            joinMeeting("" + feedDetails.getFeedId());
                            break;
                        case "CONTENT_PLAY_LIST":
                            branding_mani_layout.setVisibility(View.GONE);
                            checkingCplExistOrNot(feedDetails.getDeepLinkInfo().getCplId());
                            break;
                        default:
                            feedIntentScreen(feedDetails, false);
                            break;
                    }
                } else {
                    feedIntentScreen(feedDetails, isComment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedIntentScreen(FeedDetails feed, boolean isComment) {
        try {
            if ((feed.getType().equalsIgnoreCase("COURSE_UPDATE")) || (feed.getType().equalsIgnoreCase("COURSE"))) {
                if (feed.getDeepLinkInfo() != null) {
                    if (feed.getDeepLinkInfo().getDistributedId() != 0) {
                        checkModuleDistributionOrNot(activeUser, feed.getDeepLinkInfo().getDistributedId() + "", "COURSE", isComment, feed);
                    } else if (feed.getDeepLinkInfo().getCourseId() != 0) {
                        checkModuleDistributionOrNot(activeUser, feed.getDeepLinkInfo().getCourseId() + "", "COURSE", isComment, feed);
                    } else {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                    }
                } else {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                }
            } else if ((feed.getType().equalsIgnoreCase("ASSESSMENT_PLAY")) || (feed.getType().equalsIgnoreCase("ASSESSMENT"))) {
                if (feed.getDeepLinkInfo() != null) {
                    if (feed.getDeepLinkInfo().getDistributedId() != 0) {
                        checkModuleDistributionOrNot(activeUser, feed.getDeepLinkInfo().getDistributedId() + "", "ASSESSMENT", isComment, feed);
                    } else if (feed.getDeepLinkInfo().getAssessmentId() != 0) {
                        checkModuleDistributionOrNot(activeUser, feed.getDeepLinkInfo().getAssessmentId() + "", "ASSESSMENT", isComment, feed);
                    } else {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                    }
                } else {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                }
            } else if (feed.getType().equalsIgnoreCase("SURVEY")) {
                Intent feedIntent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                    feedIntent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                }

                feedIntent.putExtra("surveyTitle", feed.getHeader());
                feedIntent.putExtra("assessmentId", String.valueOf(feed.getDeepLinkInfo().getDeepLinkId()));
                feedIntent.putExtra("FeedID", OustSdkTools.newConvertToLong(feed.getFeedId()));
                Bundle feedBundle = new Bundle();
                feedBundle.putParcelable("Feed", feed);
                feedBundle.putParcelable("userFeed", feed);
                feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                feedBundle.putBoolean("FeedComment", isComment);
                feedIntent.putExtras(feedBundle);
                startActivityForResult(feedIntent, 1444);
                overridePendingTransition(0, 0);
                finish();
            } else if (feed.getType().equalsIgnoreCase("GENERAL")) {
                Intent feedIntent = new Intent(FeedsAPICallingActivity.this, GeneralFeedDetailScreen.class);
                Bundle feedBundle = new Bundle();
                feedBundle.putParcelable("Feed", feed);
                feedBundle.putBoolean("FeedComment", isComment);
                feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                feedBundle.putBoolean("FeedAttach", isAttach);
                if (feed.getCourseCardClass() != null) {
                    feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
                }
                feedIntent.putExtra("feedType", feed.getType());
                feedBundle.putSerializable("ActiveUser", activeUser);
                feedIntent.putExtras(feedBundle);
                startActivityForResult(feedIntent, 1444);
                overridePendingTransition(0, 0);
                finish();
            } else if (feed.getType().equalsIgnoreCase("COURSE_CARD_L")) {
                if (feed.getCardInfo() != null && feed.getCardInfo().getCardType().equalsIgnoreCase("SCORM")) {
                    try {
                        Intent intent = new Intent(OustSdkApplication.getContext(), IntroScormCardActivity.class);
                        Bundle bundle = new Bundle();
                        Gson gson = new Gson();
                        String courseDataStr = gson.toJson(dtoUserFeedDetails.getFeedDetails().getCourseCardClass());
                        OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                        intent.putExtras(bundle);
                        intent.putExtra("type", "card");
                        intent.putExtra("Feed", feed);
                        intent.putExtra("timeStamp", userFeed.getDistributedOn());
                        startActivityForResult(intent, 1444);
                        overridePendingTransition(0, 0);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else if (feed.getCardInfo().getCardMediaList() != null && feed.getCardInfo().getCardMediaList().size() != 0) {
                    if (feed.getCardInfo().getCardMediaList().get(0).getMediaType().equalsIgnoreCase("IMAGE") || feed.getCardInfo().getCardMediaList().get(0).getMediaType().equalsIgnoreCase("GIF") || feed.getCardInfo().getCardMediaList().get(0).getMediaType().equalsIgnoreCase("AUDIO")) {
                        try {
                            Intent feedIntent = new Intent(FeedsAPICallingActivity.this, ImageFeedDetailScreen.class);
                            Bundle feedBundle = new Bundle();
                            feedBundle.putParcelable("Feed", feed);
                            if (feed.getCourseCardClass() != null) {
                                feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
                            }
                            feedBundle.putSerializable("ActiveUser", activeUser);
                            feedBundle.putBoolean("FeedComment", isComment);
                            feedBundle.putBoolean("FeedAttach", isAttach);
                            feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                            feedBundle.putString("feedType", feed.getType());
                            feedIntent.putExtras(feedBundle);
                            startActivityForResult(feedIntent, 1444);
                            overridePendingTransition(0, 0);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    } else {
                        if (feed.getCardInfo().getCardMediaList().get(0).getMediaPrivacy() != null && feed.getCardInfo().getCardMediaList().get(0).getMediaPrivacy().equalsIgnoreCase("PUBLIC")) {
                            Intent feedIntent = new Intent(FeedsAPICallingActivity.this, PublicVideoFeedCardScreen.class);
                            Bundle feedBundle = new Bundle();
                            feedBundle.putParcelable("Feed", feed);
                            feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
                            feedBundle.putSerializable("ActiveUser", activeUser);
                            feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                            feedBundle.putBoolean("FeedComment", isComment);
                            feedIntent.putExtras(feedBundle);
                            startActivityForResult(feedIntent, 1444);
                            overridePendingTransition(0, 0);
                            finish();
                        } else {
                            try {
                                Intent feedIntent = new Intent(FeedsAPICallingActivity.this, VideoCardDetailScreen.class);
                                Bundle feedBundle = new Bundle();
                                feedBundle.putParcelable("Feed", feed);
                                feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
                                feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                                feedBundle.putSerializable("ActiveUser", activeUser);
                                feedBundle.putBoolean("FeedComment", isComment);
                                feedIntent.putExtras(feedBundle);
                                startActivityForResult(feedIntent, 1444);
                                overridePendingTransition(0, 0);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                    }
                } else if (feed.getCardInfo() != null && feed.getCardInfo().getClCode().equalsIgnoreCase("CL_T")) {
                    Intent feedIntent = new Intent(FeedsAPICallingActivity.this, GeneralFeedDetailScreen.class);
                    Bundle feedBundle = new Bundle();
                    feedBundle.putParcelable("Feed", feed);
                    feedBundle.putBoolean("FeedComment", isComment);
                    feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                    feedBundle.putBoolean("FeedAttach", isAttach);
                    if (feed.getCourseCardClass() != null) {
                        feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
                    }
                    feedIntent.putExtra("feedType", feed.getType());
                    feedBundle.putSerializable("ActiveUser", activeUser);
                    feedIntent.putExtras(feedBundle);
                    startActivityForResult(feedIntent, 1444);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        } catch (Exception e) {
            finish();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkModuleDistributionOrNot(ActiveUser activeUser, String moduleId, String moduleType, boolean isComment, FeedDetails feed) {
        try {
            String tenantName = OustPreferences.get("tanentid").trim();
            String checkModuleDistributedUrl = OustSdkApplication.getContext().getResources().getString(R.string.check_module_distributedOrNot);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{orgId}", tenantName);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{userId}", activeUser.getStudentid());
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleType}", moduleType);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleId}", moduleId);

            checkModuleDistributedUrl = HttpManager.getAbsoluteUrl(checkModuleDistributedUrl);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            Log.e(TAG, "checkModuleDistributionOrNot: --> " + checkModuleDistributedUrl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, checkModuleDistributedUrl, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse: checkModuleDistributionOrNot--> " + response);
                    try {
                        if (response.optBoolean("success")) {
                            Gson gson = new Gson();
                            CheckModuleDistributedOrNot checkModuleDistributedOrNot = gson.fromJson(response.toString(), CheckModuleDistributedOrNot.class);
                            if (checkModuleDistributedOrNot != null) {
                                if (checkModuleDistributedOrNot.getDistributed()) {
                                    if (checkModuleDistributedOrNot.getModuleId() != 0) {
                                        String moduleId = String.valueOf(checkModuleDistributedOrNot.getModuleId());
                                        if (moduleType.equalsIgnoreCase("course")) {
                                            if (!isComment) {
                                                try {
                                                    Intent intent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Bundle feedBundle = new Bundle();
                                                    feedBundle.putString("catalog_type", "COURSE");
                                                    feedBundle.putParcelable("Feed", feed);
                                                    feedBundle.putBoolean("FeedComment", isComment);
                                                    feedBundle.putSerializable("ActiveUser", activeUser);
                                                    feedBundle.putSerializable("deskDataMap", myDeskMap);
                                                    intent.putExtras(feedBundle);
                                                    intent.putExtra("learningId", moduleId);
                                                    OustSdkApplication.getContext().startActivity(intent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                }
                                                finish();
                                            } else {
                                                Intent feedIntent = new Intent(FeedsAPICallingActivity.this, GeneralFeedDetailScreen.class);
                                                Bundle feedBundle = new Bundle();
                                                feedBundle.putSerializable("deskDataMap", myDeskMap);
                                                JSONArray jsonArray = new JSONArray();
                                                jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
                                                feedBundle.putLong("CourseId", OustSdkTools.newConvertToLong(checkModuleDistributedOrNot.getModuleId()));
                                                feedBundle.putString("catalog_id", "" + OustSdkTools.newConvertToLong(checkModuleDistributedOrNot.getModuleId()));
                                                feedBundle.putString("catalog_type", "COURSE");
                                                feedBundle.putParcelable("Feed", feed);
                                                feedBundle.putString("feedType", feed.getType());
                                                feedBundle.putBoolean("FeedComment", isComment);
                                                feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                                                feedBundle.putSerializable("ActiveUser", activeUser);
                                                feedBundle.putSerializable("deskDataMap", myDeskMap);
                                                feedIntent.putExtras(feedBundle);
                                                startActivityForResult(feedIntent, 1444);
                                                overridePendingTransition(0, 0);
                                                finish();
                                            }
                                        } else if (moduleType.equalsIgnoreCase("assessment")) {
                                            if (!isComment) {
                                                try {
                                                    Gson gson1 = new GsonBuilder().create();
                                                    ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
                                                    if (activeUser == null || activeUser.getStudentid() == null) {
                                                        String activeUserGet = OustPreferences.get("userdata");
                                                        activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                                                        OustAppState.getInstance().setActiveUser(activeUser);
                                                    }
                                                    Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                                    ActiveGame activeGame = new ActiveGame();
                                                    activeGame.setIsLpGame(false);
                                                    activeGame.setGameid("");
                                                    activeGame.setGames(activeUser.getGames());
                                                    activeGame.setStudentid(activeUser.getStudentid());
                                                    activeGame.setChallengerid(activeUser.getStudentid());
                                                    activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
                                                    activeGame.setChallengerAvatar(activeUser.getAvatar());
                                                    activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
                                                    activeGame.setOpponentid("Mystery");
                                                    activeGame.setOpponentDisplayName("Mystery");
                                                    activeGame.setGameType(GameType.MYSTERY);
                                                    activeGame.setGuestUser(false);
                                                    activeGame.setRematch(false);
                                                    activeGame.setGrade(activeUser.getGrade());
                                                    activeGame.setGroupId("");
                                                    activeGame.setSubject(activeUser.getSubject());
                                                    activeGame.setTopic(activeUser.getTopic());
                                                    activeGame.setLevel(activeUser.getLevel());
                                                    activeGame.setLevelPercentage(activeUser.getLevelPercentage());
                                                    activeGame.setWins(activeUser.getWins());
                                                    activeGame.setModuleId(activeUser.getModuleId());
                                                    activeGame.setModuleName(activeUser.getModuleName());
                                                    boolean isAssessmentValid = false;
                                                    if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null)
                                                            && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
                                                        for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                                                            if ((moduleId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                                                                OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                                                                isAssessmentValid = true;
                                                            }
                                                        }
                                                        if (!isAssessmentValid) {
                                                            OustSdkTools.showToast(OustStrings.getString("assessment_no_longer"));
                                                            return;
                                                        }
                                                    } else {
                                                        intent.putExtra("assessmentId", moduleId);
                                                    }
                                                    intent.putExtra("ActiveGame", gson1.toJson(activeGame));

                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    OustSdkApplication.getContext().startActivity(intent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                }
                                                finish();
                                            } else {
                                                Intent feedIntent = new Intent(FeedsAPICallingActivity.this, GeneralFeedDetailScreen.class);
                                                Bundle feedBundle = new Bundle();
                                                feedBundle.putSerializable("deskDataMap", myDeskMap);
                                                JSONArray jsonArray = new JSONArray();
                                                jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
                                                feedIntent.putExtra("timeStamp", userFeed.getDistributedOn());
                                                feedIntent.putExtra("IsAssessment", true);
                                                feedIntent.putExtra("feedType", feed.getType());
                                                feedIntent.putExtra("AssessmentId", OustSdkTools.newConvertToLong(checkModuleDistributedOrNot.getModuleId()));
                                                feedBundle.putParcelable("Feed", feed);
                                                feedBundle.putBoolean("FeedComment", true);
                                                feedBundle.putLong("timeStamp", userFeed.getDistributedOn());
                                                feedBundle.putSerializable("ActiveUser", activeUser);
                                                feedIntent.putExtras(feedBundle);
                                                startActivityForResult(feedIntent, 1444);
                                                overridePendingTransition(0, 0);
                                                finish();
                                            }
                                        } else {
                                            finish();
                                        }
                                    } else if (checkModuleDistributedOrNot.getMessage() != null && !checkModuleDistributedOrNot.getMessage().isEmpty()) {
                                        OustSdkTools.showToast(checkModuleDistributedOrNot.getMessage());
                                        finish();
                                    } else {
                                        if (moduleType.equalsIgnoreCase("course")) {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                                        } else if (moduleType.equalsIgnoreCase("assessment")) {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                                        } else if (moduleType.equalsIgnoreCase("cpl")) {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                                        } else {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                                        }
                                        finish();
                                    }
                                } else {
                                    if (checkModuleDistributedOrNot.getMessage() != null && !checkModuleDistributedOrNot.getMessage().isEmpty()) {
                                        OustSdkTools.showToast(checkModuleDistributedOrNot.getMessage());
                                    } else {
                                        if (moduleType.equalsIgnoreCase("course")) {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                                        } else if (moduleType.equalsIgnoreCase("assessment")) {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_Assessment_distribute_or_not));
                                        } else if (moduleType.equalsIgnoreCase("cpl")) {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_cpl_distribute_or_not));
                                        } else {
                                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                                        }
                                    }
                                    finish();
                                }
                            }
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                            finish();
                        }
                    } catch (Exception e) {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                        finish();
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
                    finish();
                }
            });
        } catch (Exception e) {
            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_data_is_or_not));
            finish();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateFeedViewed(FeedDetails mFeed) {
        try {
            if (!userFeed.isClicked()) { // TODO need to handle feed is clicked or not
                long feedId = mFeed.getFeedId();
                String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (DatabaseError != null) {
                            Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                        } else {
                            Log.e("", "Firebase counter increment succeeded.");
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rateApp() {
        try {
            String packageName = OustSdkApplication.getContext().getPackageName();
            Log.e("Package : ", packageName);
            if (!packageName.isEmpty()) {
                OustSdkApplication.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } else {
                OustSdkApplication.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.mili.jobsmili")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotoGamelet(String assessmentId, String type) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(FeedsAPICallingActivity.this, GameLetActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("feedType", type);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedRewardUpdate(DTOUserFeedDetails.FeedDetails newFeed) {
        try {
            if (newFeed == null) {
                Log.d("TAG", "feedUpdated: reward newFeed null");
                return;
            }

            if (newFeed.isFeedCoinsAdded()) {
                Log.d("TAG", "feedUpdated: reward coins already added");
                return;
            }

            if (newFeed.getFeedCoins() < 1) {
                Log.d("TAG", "feedUpdated: reward feedCoins is less than zero");
                return;
            }
            Log.d("TAG", "feedRewardUpdate: coins:" + newFeed.getFeedCoins());

            if (newFeed.getFeedId() > 0 && newFeed.getFeedCoins() > 0 && !newFeed.isFeedCoinsAdded()) {
                newFeed.setFeedCoinsAdded(true);
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedUpdatingServices.class);
                intent.putExtra("feedId", newFeed.getFeedId());
                intent.putExtra("feedCoins", newFeed.getFeedCoins());
                intent.putExtra("feedCoinsUpdate", true);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onFeedClick(int feedId) {
        try {
            ArrayList<ClickedFeedData> clickedFeedDataArrayList = new ArrayList<>();
            ClickedFeedData clickedFeedData = new ClickedFeedData();
            clickedFeedData.setFeedId(feedId);
            clickedFeedData.setCplId(0);
            clickedFeedData.setClickedTimestamp("" + System.currentTimeMillis());
            clickedFeedDataArrayList.add(clickedFeedData);


            ArrayList<ViewedFeedData> viewedFeedDataArrayList = new ArrayList<>();
            ViewedFeedData viewedFeedData = new ViewedFeedData();
            viewedFeedData.setFeedId(feedId);
            long milliSec = System.currentTimeMillis();
            viewedFeedData.setViewedTimestamp("" + milliSec);

            viewedFeedDataArrayList.add(viewedFeedData);
            ClickedFeedRequestData clickedFeedRequestData = new ClickedFeedRequestData();
            clickedFeedRequestData.setClickedFeedDataList(clickedFeedDataArrayList);
            clickedFeedRequestData.setViewdFeedDataList(viewedFeedDataArrayList);

            if (activeUser != null) {
                clickedFeedRequestData.setStudentid(activeUser.getStudentid());
            }
            Gson gson = new Gson();
            String str = gson.toJson(clickedFeedRequestData);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFeedClickedRequests");
            requests.add(str);

            OustPreferences.saveLocalNotificationMsg("savedFeedClickedRequests", requests);

            Intent feedService = new Intent(FeedsAPICallingActivity.this, FeedUpdatingServices.class);
            startService(feedService);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void joinMeeting(String meetingId) {
        try {
            if ((meetingId != null) && (meetingId.length() > 8) && (meetingId.length() < 12)) {
                boolean isAppInstalled = appInstalledOrNot();
                Intent intent;
                if (isAppInstalled) {
                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                    intent.putExtra("zoommeetingId", meetingId);
                    intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                    intent.putExtra("isComingThroughOust", true);
                } else {
                    intent = new Intent(OustSdkApplication.getContext(), ZoomBaseActivity.class);
                    intent.putExtra("joinMeeting", true);
                }
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            } else {

                OustSdkTools.showToast(getResources().getString(R.string.invalid_meeting_id));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean appInstalledOrNot() {
        PackageManager pm = OustSdkApplication.getContext().getPackageManager();
        try {
            pm.getPackageInfo("com.oustme.oustlive", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    private void checkingCplExistOrNot(long cpl_id) {
        try {
            String message;
            Log.e("TAG", "checkCPLDistributionOrNot: cpl id-> " + cpl_id + "  isMultipleCpl--> " + isMultipleCpl);
            if (!isMultipleCpl) {
                message = "/landingPage/" + activeUser.getStudentKey() + "/cpl/" + cpl_id;
            } else {
                message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL/" + cpl_id;
            }
            Log.e("TAG", "checkCPLDistributionOrNot: " + message);
            ValueEventListener avatarListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (null == snapshot.getValue()) {
                            distributeCPL(cpl_id);
                        } else {
                            Intent intent = new Intent(FeedsAPICallingActivity.this, CplBaseActivity.class);
                            intent.putExtra("cplId", String.valueOf(cpl_id));
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(avatarListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void distributeCPL(long cplId) {
        try {
            String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.cpl_distribution_api);
            cplURL = HttpManager.getAbsoluteUrl(cplURL);
            cplURL = cplURL.replace("{cplId}", "" + cplId);
            String user_id = activeUser.getStudentid();
            List<String> users = new ArrayList<>();
            users.add(user_id);
            CplDistributionData cplDistrClass = new CplDistributionData();
            cplDistrClass.setDistributeDateTime(OustSdkTools.getDateTimeFromMilli2(SystemClock.currentThreadTimeMillis()));
            cplDistrClass.setUsers(users);
            cplDistrClass.setReusabilityAllowed(true);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(cplDistrClass);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, cplURL, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null && response.optBoolean("success")) {
                        OustSdkTools.showToast(getResources().getString(R.string.success));
                        Log.e("TAG", "onResponse: feed - cplId --> " + cplId);
                        Intent intent = new Intent(FeedsAPICallingActivity.this, CplBaseActivity.class);
                        intent.putExtra("cplId", String.valueOf(cplId));
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                        finish();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
