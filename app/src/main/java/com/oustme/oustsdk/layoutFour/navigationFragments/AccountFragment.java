package com.oustme.oustsdk.layoutFour.navigationFragments;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.components.morefeatures.ComponentMoreFeature;
import com.oustme.oustsdk.layoutFour.components.userOverView.ActiveUserModel;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.UserDetailsApp;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountFragment extends Fragment {

    public static final String TAG = "AccountFragment";

    //View initialize
    LinearLayout container;

    //Dynamic component
    ComponentMoreFeature componentMoreFeature;
    ActiveUser activeUser;
    ActiveUserModel activeUserModel;
    //common for all base fragment
    private static final String ARG_NAV_ITEM = "navItem";
    private static final String ARG_NAV_ALL = "navAll";
    Navigation navigation;
    ArrayList<Navigation> navigationList;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    private UserDetailsApp userDetailsApp;
    Handler handler;
    //End

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(Navigation navigation, List<Navigation> navigationList) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NAV_ITEM, navigation);
        args.putParcelableArrayList(ARG_NAV_ALL, (ArrayList<? extends Parcelable>) navigationList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            navigation = getArguments().getParcelable(ARG_NAV_ITEM);
            navigationList = getArguments().getParcelableArrayList(ARG_NAV_ALL);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        container = view.findViewById(R.id.container);
        //Branding loader
        branding_mani_layout = view.findViewById(R.id.branding_main_layout);
        branding_bg = branding_mani_layout.findViewById(R.id.branding_bg);
        branding_icon = branding_mani_layout.findViewById(R.id.brand_loader);
        branding_percentage = branding_mani_layout.findViewById(R.id.percentage_text);
        //End

        try {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
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

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initData() {
        try {
            if (container != null)
                container.removeAllViews();

            componentMoreFeature = null;
            setContentComponent();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setContentComponent() {
        List<String> listContent = navigation.getContent();
        if (listContent == null)
            return;

        for (String content : listContent) {
            if ("more".equalsIgnoreCase(content)) {
                setMoreOption();
            }
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setMoreOption() {
        try {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
//            activeUserViewModel = new ViewModelProvider(this).get(ActiveUserViewModel.class);
//            activeUserViewModel.init();

            if (navigationList == null || navigationList.size() == 0) {
                branding_mani_layout.setVisibility(View.GONE);
                return;
            }
            if (componentMoreFeature == null) {
                componentMoreFeature = new ComponentMoreFeature(requireActivity(), null);
                container.addView(componentMoreFeature);
            }
            getUserFromFirebase();
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            branding_mani_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setContentComponent();
        }
    }

    public void getUserFromFirebase() {
        try {
            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                String userDetailsApi = OustSdkApplication.getContext().getResources().getString(R.string.userDetailsApi);
                userDetailsApi = userDetailsApi.replace("{studentId}", activeUser.getStudentid());
                userDetailsApi = userDetailsApi.replace("{orgId}", OustPreferences.get("tanentid").trim());
                userDetailsApi = HttpManager.getAbsoluteUrl(userDetailsApi);
                Log.d("TAG", "userDetailsApiAPI: " + userDetailsApi);
                ApiCallUtils.doNetworkCall(Request.Method.GET, userDetailsApi, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new GsonBuilder().create();
                        userDetailsApp = gson.fromJson(response.toString(), UserDetailsApp.class);
                        if (userDetailsApp.getUserData().getCertificateCount() != null) {
                            activeUser.setCertificateCount(OustSdkTools.newConvertToLong(userDetailsApp.getUserData().getCertificateCount()));
                        }
                        if (userDetailsApp.getUserData().getBadgesCount() != null) {
                            activeUser.setBadgesCount(OustSdkTools.newConvertToLong(userDetailsApp.getUserData().getBadgesCount()));
                        }
                        setLiveData(activeUser);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("userDetailAPI ", "ERROR:: ", error);
                        setLiveData(activeUser);
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            setLiveData(activeUser);
            OustSdkTools.sendSentryException(e);
            Log.e("userDetailAPI ", "ERROR: ", e);
        }
    }

    private void setLiveData(ActiveUser activeUser) {
        try {
            if (activeUserModel == null)
                activeUserModel = new ActiveUserModel();
            if (activeUser != null) {
                activeUserModel.setUrlAvatar(activeUser.getAvatar());
                activeUserModel.setUserName(activeUser.getUserDisplayName());
                activeUserModel.setBadgeModelHashMap(activeUser.getBadges());
                activeUserModel.setAchievementCount(activeUser.getCertificateCount() + activeUser.getBadgesCount());
            }
            if (componentMoreFeature == null) {
                componentMoreFeature = new ComponentMoreFeature(requireActivity(), null);
                container.addView(componentMoreFeature);
            }
            handler = new Handler(Looper.getMainLooper());
            handler.post(() -> componentMoreFeature.setData(navigationList, activeUserModel, activeUser));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}