package com.oustme.oustsdk.profile.fragment;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.profile.AchievementsViewModel;
import com.oustme.oustsdk.profile.adapter.BadgeListAdapter;
import com.oustme.oustsdk.profile.model.AchievementsComponentModel;
import com.oustme.oustsdk.profile.model.BadgeModel;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BadgeFragment extends Fragment {

    private RecyclerView badge_recycler;
    private TextView no_data_text;
    private TextView userBadgesCount;
    private TextView userBadgeName;
    private ActiveUser activeUser;
    private RelativeLayout badges_relativeLayout_loader;
    private ImageView branding_bg;
    private ImageView brand_loader;
    int color;

    //MVVM
    private AchievementsViewModel achievementsViewModel;
    private AchievementsComponentModel achievementsComponentModel;
    private HashMap<Long, BadgeModel> badgeModelHashMap;
    private BadgeListAdapter badgeListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_badge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getColors();
        initView(view);
        initData();
    }

    protected void initView(View view) {

        try {
            badge_recycler = view.findViewById(R.id.badge_recycler);
            userBadgesCount = view.findViewById(R.id.badge_count);
            userBadgeName = view.findViewById(R.id.badge_user_name);
            no_data_text = view.findViewById(R.id.no_data_text_badge);
            badges_relativeLayout_loader = view.findViewById(R.id.badge_loader_layout);
            branding_bg = view.findViewById(R.id.badge_branding_bg);
            brand_loader = view.findViewById(R.id.badge_brand_loader);

            userBadgeName.setTextColor(color);
            userBadgesCount.setTextColor(color);

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
                    Picasso.get().load(brandingLoader).into(brand_loader);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(brand_loader);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    protected void initData() {

        try {
            //initialize adapter
            activeUser = OustAppState.getInstance().getActiveUser();
            Bundle bundle = getArguments();
            if (achievementsViewModel != null) {
                getViewModelStore().clear();
                achievementsViewModel = null;
                this.achievementsComponentModel = null;
            }
            showLoader();
            achievementsViewModel = new ViewModelProvider(this).get(AchievementsViewModel.class);
            achievementsViewModel.initData(bundle);
            hideLoader();
            String userName = "";
            if (activeUser != null) {
                if (activeUser.getUserDisplayName() != null && !activeUser.getUserDisplayName().isEmpty()) {
                    userName = activeUser.getUserDisplayName();
                } else {
                    userName = activeUser.getStudentid();
                }
            }
            setUserNameAndBadgeCount(userName, 0);
            achievementsViewModel.getBadgeComponentModelMutableLiveData().observe(Objects.requireNonNull(requireActivity()), badgeComponentModule -> {
                if (badgeComponentModule != null) {
                    this.achievementsComponentModel = badgeComponentModule;
                    setData(badgeComponentModule);
                } else {
                    hideLoader();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void getColors() {
        try {
            color = OustResourceUtils.getColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void showLoader() {
        try {
            badges_relativeLayout_loader.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                try {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(brand_loader, "scaleX", 1.0f, 0.85f);
                    scaleDownX.setDuration(1200);
                    scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
                    scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownX.setInterpolator(new LinearInterpolator());
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(brand_loader, "scaleY", 1.0f, 0.85f);
                    scaleDownY.setDuration(1200);
                    scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
                    scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownY.setInterpolator(new LinearInterpolator());
                    scaleDownY.start();
                    scaleDownX.start();

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }, FIVE_HUNDRED_MILLI_SECONDS);
            /*gif_loader.setVisibility(View.VISIBLE);
            gif_loader.bringToFront();*/

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void hideLoader() {
        try {
            badges_relativeLayout_loader.setVisibility(View.GONE);
//            gif_loader.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setData(AchievementsComponentModel achievementsComponentModel) {
        try {
            if (achievementsComponentModel != null) {
                String userName = "";
                int badgeListSize = 0;
                if (achievementsComponentModel.getActiveUser() != null) {
                    if (achievementsComponentModel.getActiveUser().getUserDisplayName() != null && !achievementsComponentModel.getActiveUser().getUserDisplayName().isEmpty()) {
                        userName = achievementsComponentModel.getActiveUser().getUserDisplayName();
                    } else {
                        if (activeUser != null) {
                            if (activeUser.getUserDisplayName() != null && !activeUser.getUserDisplayName().isEmpty()) {
                                userName = activeUser.getUserDisplayName();
                            } else {
                                userName = activeUser.getStudentid();
                            }
                        }
                    }
                }

                if (achievementsComponentModel.getBadgeModelHashMap() != null) {
                    badgeModelHashMap = achievementsComponentModel.getBadgeModelHashMap();
                    badgeListSize = badgeModelHashMap.size();
                }
                setUserNameAndBadgeCount(userName, badgeListSize);

                hideLoader();
                if (badgeModelHashMap != null && badgeModelHashMap.size() != 0) {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        badgeModelHashMap = badgeModelHashMap.entrySet().stream().
                                sorted(valueComparator).
                                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (e1, e2) -> e1, LinkedHashMap::new));
                    }

                    no_data_text.setVisibility(View.GONE);
                    badges_relativeLayout_loader.setVisibility(View.GONE);
                    badge_recycler.setVisibility(View.VISIBLE);
                    badge_recycler.removeAllViews();

                    badgeListAdapter = new BadgeListAdapter();
                    badgeListAdapter.setBadgeListAdapter(badgeModelHashMap, getContext(), BadgeFragment.this);

                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
                    badge_recycler.setLayoutManager(mLayoutManager);
                    badge_recycler.setItemAnimator(new DefaultItemAnimator());
                    badge_recycler.setAdapter(badgeListAdapter);
                } else {

                    no_data_text.setVisibility(View.VISIBLE);
                    badge_recycler.setVisibility(View.GONE);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setUserNameAndBadgeCount(String userName, int badgeListSize) {
        String infoText;
        if (badgeListSize == 0) {
            infoText = getResources().getString(R.string.you_have_not_);
        } else {
            infoText = getResources().getString(R.string.you_have_achieved) + " " + badgeListSize;
        }
        String userGreetings = getResources().getString(R.string.hello_) + userName.trim() + ",";

        if (badgeListSize <= 1) {
            infoText = infoText + " " + getContext().getResources().getString(R.string.badge) + ".";
        } else {
            infoText = infoText + " " + getContext().getResources().getString(R.string.badges) + ".";
        }

        userBadgesCount.setText(infoText);
        userBadgeName.setText(userGreetings);
    }

    public Comparator<Map.Entry<Long, BadgeModel>> valueComparator = (s1, s2) -> {
        if (Long.parseLong(s1.getValue().getCompletedOn()) == 0) {
            return -1;
        }
        if (Long.parseLong(s2.getValue().getCompletedOn()) == 0) {
            return 1;
        }
        if (Long.parseLong(s1.getValue().getCompletedOn()) == Long.parseLong(s2.getValue().getCompletedOn())) {
            return 0;
        }
        return Long.compare(Long.parseLong(s2.getValue().getCompletedOn()), Long.parseLong(s1.getValue().getCompletedOn()));
    };

    public void searchBadgeNotFound(String i) {
        if (i.equals("notFound")) {
            no_data_text.setVisibility(View.VISIBLE);
            badge_recycler.setVisibility(View.GONE);
        } else {
            no_data_text.setVisibility(View.GONE);
            badge_recycler.setVisibility(View.VISIBLE);
        }
    }

    public void searchBarData(String newText) {
        if (badgeListAdapter != null) {
            badgeListAdapter.getFilter().filter(newText);
        }
    }
}