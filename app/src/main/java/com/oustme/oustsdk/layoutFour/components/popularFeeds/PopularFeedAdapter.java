package com.oustme.oustsdk.layoutFour.components.popularFeeds;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.GameLetActivity;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.activity.common.ZoomBaseActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.feed_ui.ui.FeedsAPICallingActivity;
import com.oustme.oustsdk.feed_ui.ui.GeneralFeedDetailScreen;
import com.oustme.oustsdk.feed_ui.ui.ImageFeedDetailScreen;
import com.oustme.oustsdk.feed_ui.ui.PublicVideoFeedCardScreen;
import com.oustme.oustsdk.feed_ui.ui.VideoCardDetailScreen;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PopularFeedAdapter extends RecyclerView.Adapter<PopularFeedAdapter.ViewHolder> {

    private List<DTOSpecialFeed> popularFeeds;
    private Context context;
    private ActiveUser activeUser;
    private FeedClickListener feedClickListener;
    private boolean isMultipleCpl;

    public PopularFeedAdapter(List<DTOSpecialFeed> feeds, Context context, ActiveUser studentKey, FeedClickListener feedClickListener, boolean isMultipleCpl) {
        this.popularFeeds = feeds;
        this.context = context;
        this.activeUser = studentKey;
        this.feedClickListener = feedClickListener;
        this.isMultipleCpl = isMultipleCpl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_popular_feed_item, parent, false);
        PopularFeedAdapter.ViewHolder viewHolder = new PopularFeedAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            DTOSpecialFeed special_feed = popularFeeds.get(position);
            if (special_feed != null) {
                try {
                    BitmapDrawable bd = OustSdkTools.getImageDrawable(context.getString(R.string.mydesk));
                    if (special_feed.getImageUrl() != null && !special_feed.getImageUrl().isEmpty()) {
                        holder.ivBanner.setVisibility(View.VISIBLE);
                        Glide.with(Objects.requireNonNull(context)).load(special_feed.getImageUrl()).error(bd).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivBanner);
                    } else {
                        holder.ivBanner.setVisibility(View.VISIBLE);
                        holder.ivBanner.setImageDrawable(bd);
                    }

                    holder.special_layout.setOnClickListener(v -> {
                        try {
                            if (special_feed.getType() != null) {
                                if (special_feed.getType().equalsIgnoreCase("FFF_CONTEXT")) {
                                    Intent intent4 = new Intent(OustSdkApplication.getContext(), FFcontestStartActivity.class);
                                    Gson gson = new Gson();
                                    intent4.putExtra("fastestFingerContestData", gson.toJson(special_feed.getFastestFingerContestData()));
                                    context.startActivity(intent4);
                                } else {
                                    Log.e("TAG", "onBindViewHolder: id--<> " + special_feed.getId());
                                    Intent intent = new Intent(context, CplBaseActivity.class);
                                    intent.putExtra("cplId", String.valueOf(special_feed.getId()));
                                    context.startActivity(intent);
                                }
                            } else if (special_feed.getFeedType().equals(FeedType.SPL_FEED)) {
                                Intent intent = new Intent(context, FeedsAPICallingActivity.class);
                                intent.putExtra("userFeed", (Parcelable) special_feed.getDtoUserFeeds());
                                intent.putExtra("FeedType", special_feed.getFeedType());
                                context.startActivity(intent);

                                /*if (special_feed.getDtoNewFeed().getFeedType().equals(FeedType.SURVEY)) {
                                    gotoSurvey(special_feed.getDtoNewFeed());
                                } else if (special_feed.getDtoNewFeed().getFeedType().equals(FeedType.COURSE_UPDATE)) {
                                    gotoCourse(special_feed.getDtoNewFeed());
                                } else if (special_feed.getDtoNewFeed().getFeedType().equals(FeedType.ASSESSMENT_PLAY)) {
                                    gotoCourse(special_feed.getDtoNewFeed());
                                } else if (special_feed.getDtoNewFeed().getFeedType().equals(FeedType.COURSE_CARD_L)) {
                                    if (special_feed.getDtoNewFeed().getCourseCardClass() != null) {
                                        if (special_feed.getDtoNewFeed().getCourseCardClass().getCardMedia() != null && special_feed.getDtoNewFeed().getCourseCardClass().getCardMedia().size() != 0) {
                                            if (special_feed.getDtoNewFeed().getCourseCardClass().getCardMedia().get(0).getMediaType() != null && !special_feed.getDtoNewFeed().getCourseCardClass().getCardMedia().get(0).getMediaType().isEmpty()) {
                                                String mediaType = special_feed.getDtoNewFeed().getCourseCardClass().getCardMedia().get(0).getMediaType();

                                                if (mediaType.equalsIgnoreCase("IMAGE") ||
                                                        mediaType.equalsIgnoreCase("GIF") || mediaType.equalsIgnoreCase("AUDIO")) {
                                                    gotoImageFeed(special_feed.getDtoNewFeed());
                                                } else {
                                                    if (special_feed.getDtoNewFeed().getCourseCardClass().getCardMedia().get(0).getMediaPrivacy() != null && special_feed.getDtoNewFeed().getCourseCardClass().getCardMedia().get(0).getMediaPrivacy().equals("PUBLIC")) {
                                                        gotoPublicVideo(special_feed.getDtoNewFeed());
                                                    } else {
                                                        gotoPrivateVideo(special_feed.getDtoNewFeed());
                                                    }
                                                }
                                            }

                                        } else {
                                            gotoGeneralFeeds(special_feed.getDtoNewFeed());
                                        }
                                    } else {
                                        gotoGeneralFeeds(special_feed.getDtoNewFeed());
                                    }
                                } else {
                                    gotoGeneralFeeds(special_feed.getDtoNewFeed());
                                }*/
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    });
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

    private void gotoGeneralFeeds(DTONewFeed feed) {
        try {
            updateFeedViewed(feed);
            feedRewardUpdate(feed);
            feedClicked(feed.getFeedId(), feed.getCplId());

            String feedType = feed.getFeedType().toString();
            if (feed.getCourseCardClass() != null) {

                OustDataHandler.getInstance().setCourseCardClass(feed.getCourseCardClass());
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                intent.putExtra("type", "card");
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(0, 0);

            } else {
                if (!feedType.isEmpty()) {
                    switch (feedType) {

                        case "APP_UPGRADE":
                            rateApp();
                            break;
                        case "GAMELET_WORDJUMBLE":
                        case "GAMELET_WORDJUMBLE_V2":
                        case "GAMELET_WORDJUMBLE_V3":
                            gotoGamelet("" + feed.getAssessmentId(), feedType);
                            break;
                        case "JOIN_MEETING":
                            joinMeeting("" + feed.getId());
                            break;
                        case "CONTENT_PLAY_LIST":
                            checkingCplExistOrNot(feed.getParentCplId());
                            break;
                        default:
                            feedIntentScreen(feed, false, false);
                            break;
                    }
                } else {
                    feedIntentScreen(feed, false, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
                            Intent intent = new Intent(context, CplBaseActivity.class);
                            intent.putExtra("cplId", String.valueOf(cpl_id));
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent);
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

    private void feedClicked(long feedId, long cplId) {
        try {
            if (feedClickListener != null) {
                feedClickListener.onFeedClick(feedId, (int) cplId);
                feedClickListener.onFeedViewed(feedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedIntentScreen(DTONewFeed feed, boolean isComment, boolean isAttach) {

        Intent feedIntent = new Intent(context, GeneralFeedDetailScreen.class);
        Bundle feedbundle = new Bundle();
        feedbundle.putParcelable("Feed", feed);
        feedbundle.putBoolean("FeedComment", isComment);
        feedbundle.putBoolean("FeedAttach", isAttach);
        if (feed.getCourseCardClass() != null) {
            feedbundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
        }

        feedbundle.putBoolean("isFeedLikeable", feed.isLikeble());
        feedIntent.putExtra("feedType", feed.getFeedType().toString());
        feedbundle.putSerializable("ActiveUser", activeUser);
        feedIntent.putExtras(feedbundle);

        ((Activity) context).startActivityForResult(feedIntent, 1444);
    }

    private void gotoPrivateVideo(DTONewFeed feed) {
        try {
            updateFeedViewed(feed);
            feedClicked(feed.getFeedId(), feed.getCplId());

            Intent feedIntent = new Intent(context, VideoCardDetailScreen.class);
            Bundle feedbundle = new Bundle();
            feedbundle.putParcelable("Feed", feed);
            feedbundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
            feedbundle.putSerializable("ActiveUser", activeUser);
            feedbundle.putBoolean("FeedComment", false);
            feedbundle.putBoolean("isFeedLikeable", feed.isLikeble());
            feedIntent.putExtras(feedbundle);
            ((Activity) context).startActivityForResult(feedIntent, 1444);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoPublicVideo(DTONewFeed feed) {
        try {
            updateFeedViewed(feed);
            feedClicked(feed.getFeedId(), feed.getCplId());

            Intent feedIntent = new Intent(context, PublicVideoFeedCardScreen.class);
            Bundle feedBundle = new Bundle();
            feedBundle.putParcelable("Feed", feed);
            feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
            feedBundle.putSerializable("ActiveUser", activeUser);
            feedBundle.putBoolean("FeedComment", false);
            feedBundle.putBoolean("isFeedLikeable", feed.isLikeble());
            feedIntent.putExtras(feedBundle);
            ((Activity) context).startActivityForResult(feedIntent, 1444);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoImageFeed(DTONewFeed feed) {
        try {
            updateFeedViewed(feed);
            feedClicked(feed.getFeedId(), feed.getCplId());

            Intent feedIntent = new Intent(context, ImageFeedDetailScreen.class);
            Bundle feedBundle = new Bundle();
            feedBundle.putParcelable("Feed", feed);
            if (feed.getCourseCardClass() != null) {
                feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
            }
            feedBundle.putSerializable("ActiveUser", activeUser);
            feedBundle.putBoolean("FeedComment", false);
            feedBundle.putBoolean("FeedAttach", false);
            feedBundle.putBoolean("isFeedLikeable", feed.isLikeble());
            feedBundle.putString("feedType", feed.getFeedType().toString());
            feedIntent.putExtras(feedBundle);
            ((Activity) context).startActivityForResult(feedIntent, 1444);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoCourse(DTONewFeed feed) {
        try {
            updateFeedViewed(feed);
            feedRewardUpdate(feed);
            feedClicked(feed.getFeedId(), feed.getCplId());

            Intent feedIntent = new Intent(context, CourseDetailScreen.class);
            Bundle feedBundle = new Bundle();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
            if (feed.getFeedType() == FeedType.COURSE_UPDATE) {
                feedBundle.putLong("CourseId", feed.getCourseId());
                feedBundle.putString("catalog_id", "" + feed.getCourseId());
                feedBundle.putString("catalog_type", "COURSE");
                feedBundle.putParcelable("Feed", feed);
                feedBundle.putString("feedType", feed.getFeedType().toString());
                feedBundle.putBoolean("FeedComment", false);
                feedBundle.putBoolean("isFeedLikeable", feed.isLikeble());
                feedBundle.putSerializable("ActiveUser", activeUser);
                feedIntent.putExtras(feedBundle);
                ((Activity) context).startActivityForResult(feedIntent, 1444);
            } else {
                feedIntent.putExtra("IsAssessment", true);
                feedIntent.putExtra("feedType", feed.getFeedType().toString());
                feedIntent.putExtra("AssessmentId", feed.getAssessmentId());

                Log.e("Feed", "inside gotoAssessment() method");
                String assessmentId = "" + feed.getAssessmentId();
                if (assessmentId.contains("COURSE")) {
                    assessmentId = assessmentId.replace("COURSE", "");
                } else if (assessmentId.contains("course")) {
                    assessmentId = assessmentId.replace("course", "");
                }
                String id = "ASSESSMENT" + feed.getAssessmentId();
                Gson gson = new GsonBuilder().create();
                Intent intent;
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                    intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                } else {
                    intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                }
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("ActiveGame", gson.toJson(setGame(activeUser)));
                intent.putExtra("ActiveUser", gson.toJson(activeUser));
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoSurvey(DTONewFeed feed) {
        try {
            updateFeedViewed(feed);
            feedRewardUpdate(feed);
            feedClicked(feed.getFeedId(), feed.getCplId());

            OustAppState.getInstance().setCurrentSurveyFeed(feed);

            Intent feedIntent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                feedIntent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
            }

            feedIntent.putExtra("surveyTitle", feed.getHeader());
            feedIntent.putExtra("assessmentId", "" + feed.getAssessmentId());
            feedIntent.putExtra("FeedID", feed.getFeedId());
            feedIntent.putExtra("courseId", feed.getMappedCourseId());
            Bundle feedbundle = new Bundle();
            feedbundle.putParcelable("Feed", feed);
            feedbundle.putBoolean("FeedComment", feed.isCommented());
            feedbundle.putBoolean("isFeedLikeable", feed.isLikeble());
            feedIntent.putExtras(feedbundle);
            ((Activity) context).startActivityForResult(feedIntent, 1444);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return popularFeeds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public static final String FEED = "popular_feed";
        private ImageView ivBanner;
        private TextView tvTitle, tvDesc;
        private ConstraintLayout special_layout;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivBanner = view.findViewById(R.id.card);
            tvTitle = view.findViewById(R.id.tv_title);
            tvDesc = view.findViewById(R.id.tv_des);
            special_layout = view.findViewById(R.id.special_layout);

        }
    }

    private void updateFeedViewed(DTONewFeed mFeed) {
        try {
            if (!mFeed.isClicked()) {
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

    private void feedRewardUpdate(DTONewFeed newFeed) {
        try {
            if (newFeed == null) {
                return;
            }

            if (newFeed.isFeedCoinsAdded()) {
                return;
            }

            if (newFeed.getFeedCoins() < 1) {
                return;
            }

            if (feedClickListener != null) {
                feedClickListener.onFeedRewardCoinsUpdate(newFeed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
        try {

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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);

        }

        return activeGame;
    }

    private void distributeCPL(long cplId) {

        String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.cpl_distribution_api);
        cplURL = HttpManager.getAbsoluteUrl(cplURL);
        cplURL = cplURL.replace("{cplId}", "" + cplId);
        String user_id = activeUser.getStudentid();
        List<String> users = new ArrayList<>();
        users.add(user_id);
        CPLDistrClass cplDistrClass = new CPLDistrClass();
        cplDistrClass.setDistributeDateTime(OustSdkTools.getDateTimeFromMilli2(SystemClock.currentThreadTimeMillis()));
        cplDistrClass.setUsers(users);
        cplDistrClass.setReusabilityAllowed(true);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(cplDistrClass);

        ApiCallUtils.doNetworkCall(Request.Method.PUT, cplURL, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null && response.optBoolean("success")) {
                    OustSdkTools.showToast(context.getResources().getString(R.string.success));
                    Log.e("TAG", "onResponse: feed - cplId --> " + cplId);
                    Intent intent = new Intent(context, CplBaseActivity.class);
                    intent.putExtra("cplId", String.valueOf(cplId));
                    context.startActivity(intent);
                } else {
                    OustSdkTools.showToast(context.getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                OustSdkTools.showToast(context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    public static class CPLDistrClass {
        List<String> users;
        boolean sendSMS;
        boolean sendEmail;
        boolean sendNotification;
        boolean onlyPANIndia;
        String distributeDateTime;
        boolean reusabilityAllowed;

        CPLDistrClass() {
        }

        public boolean isReusabilityAllowed() {
            return reusabilityAllowed;
        }

        public void setReusabilityAllowed(boolean reusabilityAllowed) {
            this.reusabilityAllowed = reusabilityAllowed;
        }
        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        public boolean isSendSMS() {
            return sendSMS;
        }

        public void setSendSMS(boolean sendSMS) {
            this.sendSMS = sendSMS;
        }

        public boolean isSendEmail() {
            return sendEmail;
        }

        public void setSendEmail(boolean sendEmail) {
            this.sendEmail = sendEmail;
        }

        public boolean isSendNotification() {
            return sendNotification;
        }

        public void setSendNotification(boolean sendNotification) {
            this.sendNotification = sendNotification;
        }

        public boolean isOnlyPANIndia() {
            return onlyPANIndia;
        }

        public void setOnlyPANIndia(boolean onlyPANIndia) {
            this.onlyPANIndia = onlyPANIndia;
        }

        public String getDistributeDateTime() {
            return distributeDateTime;
        }

        public void setDistributeDateTime(String distributeDateTime) {
            this.distributeDateTime = distributeDateTime;
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
                Intent intent = new Intent(context, GameLetActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("feedType", type);
                context.startActivity(intent);
            }
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
                context.startActivity(intent);
            } else {

                OustSdkTools.showToast(context.getResources().getString(R.string.invalid_meeting_id));
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
}
