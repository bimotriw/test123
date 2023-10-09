package com.oustme.oustsdk.adapter.common;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.interfaces.common.NewLandingDrawerCallback;
import com.oustme.oustsdk.request.DrawerItemData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.DISABLE_LEARNING_DIARY;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.FEED_BACK_NAME;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TEAM_ANALYTICS;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class NewProfileAdapter extends RecyclerView.Adapter {
    private static final String TAG = "NewProfileAdapter";
    private Activity activity;
    private ArrayList<DrawerItemData> itemDatas;
    private ActiveUser activeUser;
    private LayoutInflater mInflater;
    private int PROFILE_MODE = 0, ITEM_DATA = 1, INTRO_DATA = 2;
    private String userDisplayName, temporaryProfileImageDisplayName;

    public NewProfileAdapter(Activity activity, ArrayList<DrawerItemData> itemDatas) {
        this.activity = activity;
        this.activeUser = OustAppState.getInstance().getActiveUser();
        mInflater = LayoutInflater.from(activity);
        this.itemDatas = itemDatas;
        totalRows = 1;
    }

    private NewLandingDrawerCallback newLandingDrawerCallback;

    public void setNewLandingDrawerCallback(NewLandingDrawerCallback newLandingDrawerCallback) {
        this.newLandingDrawerCallback = newLandingDrawerCallback;
    }

    public class ProfileTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView progresstext_val, appversion_text, userprofileImageText, profileprogresstext,
                avatarName, userId;
        private CircleImageView avatar;
        private ProgressBar profileprogress_bar;
        private RelativeLayout contentLayout;

        public ProfileTypeViewHolder(View convertView) {
            super(convertView);
            this.progresstext_val = convertView.findViewById(R.id.progresstext_val);
            this.appversion_text = convertView.findViewById(R.id.appversion_text);
            this.avatar = convertView.findViewById(R.id.avatar);
            this.userprofileImageText = convertView.findViewById(R.id.userprofileImageText);
            this.profileprogress_bar = convertView.findViewById(R.id.profileprogress_bar);
            this.profileprogresstext = convertView.findViewById(R.id.profileprogresstext);
            this.avatarName = convertView.findViewById(R.id.userName);
            this.userId = convertView.findViewById(R.id.userId);
            this.contentLayout = convertView.findViewById(R.id.contentLayout);
        }
    }

    public class DrawerItemTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView item_text;
        private RelativeLayout drawer_item_layout;

        public DrawerItemTypeViewHolder(View itemView) {
            super(itemView);
            this.item_text = itemView.findViewById(R.id.item_text);
            this.drawer_item_layout = itemView.findViewById(R.id.drawer_item_layout);

            try {
                if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
                    int color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                    StateListDrawable stateListDrawable = new StateListDrawable();
                    stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
                    stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
                    this.drawer_item_layout.setBackgroundDrawable(stateListDrawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public class DrawerIntroTypeViewHolder extends RecyclerView.ViewHolder {

        private TextView powerbyoust_text, oustlink;
        private RelativeLayout powerbyoust_textlayout;
        private ImageView oust_logo;

        public DrawerIntroTypeViewHolder(View itemView) {
            super(itemView);
            this.powerbyoust_textlayout = itemView.findViewById(R.id.powerbyoust_textlayout);
            this.powerbyoust_text = itemView.findViewById(R.id.powerbyoust_text);
            this.oustlink = itemView.findViewById(R.id.oustlink);
            this.oust_logo = itemView.findViewById(R.id.oust_logo);
            this.oust_logo.setColorFilter(Color.BLACK);
        }
    }

    public void notifyDataChange() {
        this.activeUser = OustAppState.getInstance().getActiveUser();
        totalRows = 1;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == PROFILE_MODE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header, parent, false);
            return new ProfileTypeViewHolder(view);
        } else if (viewType == ITEM_DATA) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item, parent, false);
            return new DrawerItemTypeViewHolder(view);
        } else if (viewType == INTRO_DATA) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_bottom, parent, false);
            return new DrawerIntroTypeViewHolder(view);
        }
        return null;
    }

    private int profileHeaderHeight = 0;
    private int rowHeight = 0;
    private int totalRows = 1;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            final DrawerItemData itemData = itemDatas.get(position);
            if (itemData != null) {
                if (itemData.getType() == PROFILE_MODE) {
                    if (OustPreferences.getAppInstallVariable("hideUserSetting")) {
                        ((ProfileTypeViewHolder) holder).profileprogress_bar.setVisibility(View.GONE);
                        ((ProfileTypeViewHolder) holder).progresstext_val.setVisibility(View.GONE);
                        ((ProfileTypeViewHolder) holder).profileprogresstext.setVisibility(View.GONE);
                    } else {
                        ((ProfileTypeViewHolder) holder).profileprogress_bar.setVisibility(View.VISIBLE);
                        ((ProfileTypeViewHolder) holder).progresstext_val.setVisibility(View.VISIBLE);
                        ((ProfileTypeViewHolder) holder).profileprogresstext.setVisibility(View.VISIBLE);
                    }
                    if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
                        int color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                        GradientDrawable drawable = (GradientDrawable) OustSdkApplication.getContext().getResources().getDrawable(R.drawable.friendprofileavatardrawable);
                        drawable.setStroke(3, color);
                        OustSdkTools.setLayoutBackgroudDrawable(((ProfileTypeViewHolder) holder).avatar, drawable);
                    }
                    ((ProfileTypeViewHolder) holder).profileprogress_bar.setProgress(0);
                  //  String profileCompletedText = ""+OustSdkApplication.getContext().getResources().getString(R.string.profile_completedTxt);
                   // ((ProfileTypeViewHolder) holder).profileprogresstext.setText(profileCompletedText);
                    setToolBarColor(((ProfileTypeViewHolder) holder).profileprogress_bar);
                    try {
                        PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
                        String str = pinfo.versionName;
                        if (str != null) {
                            ((ProfileTypeViewHolder) holder).appversion_text.setText("v" + str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    int progress = getProgress();
                    ((ProfileTypeViewHolder) holder).profileprogress_bar.setProgress(progress);
                    if (progress > 50) {
                        ((ProfileTypeViewHolder) holder).progresstext_val.setTextColor(OustSdkTools.getColorBack(R.color.whitea));
                    }
                    ((ProfileTypeViewHolder) holder).progresstext_val.setText(progress + "%");
                    setNewUserDisplayName(((ProfileTypeViewHolder) holder).avatarName);
                    setProfileData(((ProfileTypeViewHolder) holder).avatar, ((ProfileTypeViewHolder) holder).userprofileImageText, ((ProfileTypeViewHolder) holder).avatarName, ((ProfileTypeViewHolder) holder).userId);
                    ((ProfileTypeViewHolder) holder).avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OustSdkTools.oustTouchEffect(v, 100);
                            newLandingDrawerCallback.onSettingClick();
                        }
                    });
                    ((ProfileTypeViewHolder) holder).userprofileImageText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OustSdkTools.oustTouchEffect(v, 100);
                            newLandingDrawerCallback.onSettingClick();
                        }
                    });

                    LinearLayout.LayoutParams profileParams = (LinearLayout.LayoutParams) ((ProfileTypeViewHolder) holder).contentLayout.getLayoutParams();
                    profileHeaderHeight = profileParams.height;
                } else if (itemData.getType() == ITEM_DATA)
                {
                    ((DrawerItemTypeViewHolder) holder).item_text.setText(itemData.getTopic());
                    if (itemData.getConditionText().toUpperCase().contains("HISTORY"))
                    {
                       // ((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.historyTitle));
                        ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                    }
                    else if (itemData.getConditionText().contains("ASSESSMENT"))
                    {
                        Log.d(TAG, "onBindViewHolder: "+OustPreferences.get("userRole"));
                        if ((OustPreferences.get("userRole") != null) &&
                                ((OustPreferences.get("userRole").equalsIgnoreCase("teacher")) || (OustPreferences.get("userRole").equalsIgnoreCase("admin")) || (OustPreferences.get("userRole").equalsIgnoreCase("OUST_SUPER_ADMIN"))))
                        {

                            if (OustPreferences.getAppInstallVariable("hideAssessment")) {
                                ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                            } else {
                                totalRows++;
                                ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                            }
                        }
                       // ((DrawerItemTypeViewHolder) holder).item_text.setText(itemData.getTopic());
                    }
                    else if (itemData.getConditionText().contains("CATALOGUES")) {
                       // ((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.my_catalogue));
                        if (!OustPreferences.getAppInstallVariable("hideCatalog")) {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        } else {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        }
                    }
                    else if (itemData.getConditionText().contains("FAVOURITES")) {
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.favourites));
                        if (OustPreferences.getAppInstallVariable("disableFavorite")) {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        } else {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        }
                    }
                    else if (itemData.getConditionText().contains("DIARY"))
                    {
                        String diaryTopictext = OustPreferences.get("learningDiaryName");
                        if(diaryTopictext==null || diaryTopictext.isEmpty()){
                            diaryTopictext = OustSdkApplication.getContext().getResources().getString(R.string.my_diary);
                        }

                        ((DrawerItemTypeViewHolder) holder).item_text.setText(diaryTopictext);

                        if ((OustPreferences.getAppInstallVariable(DISABLE_LEARNING_DIARY))) {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        } else {
                            totalRows++;
                            Log.d("Learning show", "onBindViewHolder: ");
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        }
                    }
                    else if (itemData.getConditionText().contains("TEAM ANALYTICS"))
                    {

                        //((DrawerItemTypeViewHolder) holder).item_text.setText(itemData.getTopic());
                    //    ((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.team_analytics));

                        if ((OustPreferences.getAppInstallVariable(TEAM_ANALYTICS))) {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);

                        } else {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);

                        }
                    }
                    else if (itemData.getConditionText().contains("CONTESTS")) {
                        totalRows++;
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.contests));
                        ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                    }

                    else if (itemData.getConditionText().contains("ANALYTICS")) {
                        totalRows++;
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.analytics));
                    }
                    else if (itemData.getConditionText().contains("FEEDBACK"))
                    {
                        String feedBackTopictext = OustPreferences.get(FEED_BACK_NAME);
                        if(feedBackTopictext==null || feedBackTopictext.isEmpty()){
                            feedBackTopictext = OustSdkApplication.getContext().getResources().getString(R.string.help_support);
                        }

                        ((DrawerItemTypeViewHolder) holder).item_text.setText(feedBackTopictext);
                        totalRows++;
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string."historyTitle"));
                    }
                    else if (itemData.getConditionText().toUpperCase().contains("RATE")) {
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.rate_the_appText));
                        if (OustPreferences.getAppInstallVariable("isContainer")) {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        } else {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        }
                    }
                    else if(itemData.getConditionText().contains("HOST"))
                    {
                        String hostAppName = OustPreferences.get(AppConstants.StringConstants.HOST_APP_NAME);
                        if(hostAppName==null || hostAppName.isEmpty()){
                            hostAppName = OustSdkApplication.getContext().getResources().getString(R.string.host_app);
                        }

                        ((DrawerItemTypeViewHolder) holder).item_text.setText(hostAppName);

                        if ((OustPreferences.getAppInstallVariable(AppConstants.StringConstants.HOST_APP_LINK_DISABLED))) {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        } else {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        }
                    }
                    else if (itemData.getConditionText().contains("SETTINGS")) {
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.settings));
                        if (OustPreferences.getAppInstallVariable("hideUserSetting")) {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        } else {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        }
                    }  else if (itemData.getConditionText().contains("leaderboard")) {
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.leader_board_title));
                        ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                    }



                    else if (itemData.getConditionText().contains("Playlist Selection")) {

                        String cplTopictext = OustPreferences.get(AppConstants.StringConstants.CPL_LANG_CHANGE);
                        if(cplTopictext==null || cplTopictext.isEmpty()){
                            cplTopictext = OustSdkApplication.getContext().getResources().getString(R.string.playlist_selection);
                        }

                        ((DrawerItemTypeViewHolder) holder).item_text.setText(cplTopictext);

                        if (OustPreferences.getAppInstallVariable("showCplLanguageInNavigation") && OustPreferences.getTimeForNotification("parentCplId")!=0) {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        } else {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        }

                    }

                    else if (itemData.getConditionText().contains("ARCHIVED")) {
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.archived_list));
                        if ((OustPreferences.getAppInstallVariable("hideArchive")) || (OustStaticVariableHandling.getInstance().getIsNewLayout() != 0)) {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        } else {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        }
                    }
                     else if (itemData.getConditionText().contains("Report")) {
                        totalRows++;
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string."historyTitle"));
                    }
                    else if (itemData.getConditionText().contains("FAQ"))
                    {
//                        ((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string."logoutTitle"));
                        if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_FAQ)) {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        } else {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        }
                    }

                    else if (itemData.getConditionText().contains("LOGOUT"))
                    {
                        //((DrawerItemTypeViewHolder) holder).item_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.logout));
                        if (OustPreferences.getAppInstallVariable("logoutButtonEnabled")) {
                            totalRows++;
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                        } else {
                            ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.GONE);
                        }
                    } else if (itemData.getConditionText().contains("BADGES"))
                    {
                        totalRows++;
                        ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setVisibility(View.VISIBLE);
                    }

                    ((DrawerItemTypeViewHolder) holder).drawer_item_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemData.getConditionText().contains("History")) {
                                newLandingDrawerCallback.onHistoryClick();
                            } else if (itemData.getConditionText().contains("SETTING")) {
                                newLandingDrawerCallback.onSettingClick();
                            } else if (itemData.getConditionText().contains("leaderboard")) {
                                newLandingDrawerCallback.onLeaderboardClick();
                            } else if (itemData.getConditionText().contains("FEEDBACK")) {
                                newLandingDrawerCallback.clickOnFormFillIcon();
                            } else if (itemData.getConditionText().contains("CONTESTS")) {
                                newLandingDrawerCallback.clickOnContest();
                            } else if (itemData.getConditionText().contains("TEAM ANALYTICS")) {
                                newLandingDrawerCallback.clickOnTeamAnalytics();
                            } else if (itemData.getConditionText().contains("ANALYTICS")) {
                                newLandingDrawerCallback.clickOnAnalytics();
                            } else if (itemData.getConditionText().contains("ARCHIVED")) {
                                newLandingDrawerCallback.clickOnAllList();
                            } else if (itemData.getConditionText().contains("RATE")) {
                                newLandingDrawerCallback.ratetheApp();
                            } else if (itemData.getConditionText().contains("Report")) {
                                newLandingDrawerCallback.clickOnReportBug();
                            } else if (itemData.getConditionText().contains("CATALOGUES")) {
                                newLandingDrawerCallback.clickOnCatalogue();
                            } else if (itemData.getConditionText().contains("FAVOURITES")) {
                                newLandingDrawerCallback.openFavouriteCards();
                            } else if (itemData.getConditionText().contains("ASSESSMENT")) {
                                newLandingDrawerCallback.clickOnAssessmentAnalytics();
                            } else if (itemData.getConditionText().contains("LOGOUT")) {
                                newLandingDrawerCallback.onLogout();
                            } else if (itemData.getConditionText().contains("DIARY")) {
                                newLandingDrawerCallback.clickLearningDiary();
                            }  else if (itemData.getConditionText().contains("Achievement")) {
                                newLandingDrawerCallback.clickBadges();
                            } else if (itemData.getConditionText().contains("Playlist Selection")) {
                                newLandingDrawerCallback.clickOnCplLanguageSelection();
                            }else if (itemData.getConditionText().contains("FAQ")) {
                                newLandingDrawerCallback.clickOnFAQ();
                            }
                        }
                    });
                    LinearLayout.LayoutParams rowParams = (LinearLayout.LayoutParams) ((DrawerItemTypeViewHolder) holder).drawer_item_layout.getLayoutParams();
                    rowHeight = rowParams.height;
                } else if (itemData.getType() == INTRO_DATA) {
                    DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                    int scrHeight = metrics.heightPixels;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((DrawerIntroTypeViewHolder) holder).powerbyoust_textlayout.getLayoutParams();
                    if ((profileHeaderHeight > 0) && (rowHeight > 0)) {
                        if ((profileHeaderHeight + (rowHeight * (totalRows)) + params.height) < scrHeight) {
                            params.height = scrHeight - (profileHeaderHeight + (rowHeight * (totalRows)));
                            ((DrawerIntroTypeViewHolder) holder).powerbyoust_textlayout.setLayoutParams(params);
                        }
                    }
                    ((DrawerIntroTypeViewHolder) holder).powerbyoust_text.setText((itemData.getTopic()));
                    ((DrawerIntroTypeViewHolder) holder).oustlink.setText(Html.fromHtml(itemData.getSubTopic()));
                    ((DrawerIntroTypeViewHolder) holder).powerbyoust_textlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //newLandingDrawerCallback.clickOnLink();
                        }
                    });

                    ((DrawerIntroTypeViewHolder) holder).oust_logo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            newLandingDrawerCallback.clickOnLink();
                        }
                    });


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return itemDatas.size();
    }


    @Override
    public int getItemViewType(int position) {
        int type = itemDatas.get(position).getType();
        return type;
    }

    private void setToolBarColor(ProgressBar profileprogress_bar) {
        profileprogress_bar.invalidate();
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                int color = OustSdkTools.getColorBack(R.color.lgreen);
                if (OustPreferences.get("toolbarColorCode") != null) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                final LayerDrawable ld = (LayerDrawable) OustSdkApplication.getContext().getResources().getDrawable(R.drawable.custommodule_progressdrawable);
                final Drawable d1 = ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                profileprogress_bar.setProgressDrawable(ld);
            }
        } catch (Exception e) {
            int n1 = 0;
            int n2 = n1 + 4;
        }
    }


    private void setNewUserDisplayName(TextView avatarName) {
        if ((OustPreferences.get("fname") != null) && (OustPreferences.get("lname") != null)) {
            userDisplayName = OustPreferences.get("fname") + " " + OustPreferences.get("lname");
            temporaryProfileImageDisplayName = String.valueOf(OustPreferences.get("fname").charAt(0)) +
                    OustPreferences.get("lname").charAt(0);
        } else if (OustPreferences.get("fname") != null) {
            userDisplayName = OustPreferences.get("fname");
            temporaryProfileImageDisplayName = userDisplayName;
        } else if ((OustPreferences.get("lname") != null)) {
            userDisplayName = OustPreferences.get("lname");
            temporaryProfileImageDisplayName = userDisplayName;
        } else if ((OustPreferences.get("emailId") != null)) {
            userDisplayName = OustPreferences.get("emailId");
            temporaryProfileImageDisplayName = userDisplayName;
        } else if ((activeUser != null) && (activeUser.getUserDisplayName() != null)) {
            userDisplayName = activeUser.getUserDisplayName();
            temporaryProfileImageDisplayName = userDisplayName;
        } else {
            userDisplayName = OustPreferences.get("mobileNum");
            temporaryProfileImageDisplayName = userDisplayName;
        }
        avatarName.setText(userDisplayName);
    }


    private void setProfileData(ImageView avatar, TextView userprofileImageText, TextView avatarName, TextView userId) {
        try {
            if (OustSdkTools.tempProfile != null) {
                avatar.setImageBitmap(OustSdkTools.tempProfile);
            } else {
                if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                    avatar.setVisibility(View.VISIBLE);
                    userprofileImageText.setVisibility(View.GONE);
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(activeUser.getAvatar()).into(avatar);
                    } else {
                        Picasso.get().load(activeUser.getAvatar()).networkPolicy(NetworkPolicy.OFFLINE).into(avatar);
                    }
                } else {
//                    if (OustPreferences.getSavedInt("isNewLayout")==1) {
                    avatar.setVisibility(View.GONE);
                    userprofileImageText.setVisibility(View.VISIBLE);
                    if(temporaryProfileImageDisplayName!=null && !temporaryProfileImageDisplayName.isEmpty()) {
                        userprofileImageText.setText(temporaryProfileImageDisplayName.toUpperCase());
                    }
                    if(userDisplayName!=null && !userDisplayName.isEmpty()) {
                        OustSdkTools.setGroupImage(userprofileImageText, userDisplayName);
                    }
//                   }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try{
            if (null != activeUser.getStudentid()) {
                userId.setText(OustAppState.getInstance().getActiveUser().getStudentid());
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int getProgress() {
        int n1 = 0;
        try {
            if (activeUser.getFname() != null) {
                n1 += 2;
            }
            if (activeUser.getDob() > 1000) {
                n1++;
            }
            if (activeUser.getUserGender() != null) {
                n1++;
            }
            if (activeUser.getEmail() != null) {
                n1 += 2;
            }
            if (activeUser.getUserCity() != null) {
                n1++;
            }
            if (activeUser.getUserCountry() != null) {
                n1++;
            }
            if (activeUser.getUserMobile() > 1000) {
                n1 += 2;
            }
        } catch (Exception e) {
        }
        return (n1 * 10);
    }

    private void setLayoutAspectRatiosmall(RelativeLayout mainImageLayout) {
        try {
            DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
            int scrHeight = metrics.heightPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainImageLayout.getLayoutParams();
            params.height = scrHeight;
            mainImageLayout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

}
