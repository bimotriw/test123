package com.oustme.oustsdk.profile.fragment;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.oustme.oustsdk.profile.ViewCertificate;
import com.oustme.oustsdk.profile.adapter.CertificateListAdapter;
import com.oustme.oustsdk.profile.model.CertificateData;
import com.oustme.oustsdk.profile.model.CertificatesResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class CertificatesFragment extends Fragment {

    private RecyclerView certificate_recycler;
    private TextView no_data_text;
    //    private GifImageView gif_loader;
    private TextView userCertificateCount;
    private TextView userNameTxt;
    private RelativeLayout certificate_relativeLayout_loader;
    private ImageView branding_bg;
    private ImageView brand_loader;
    private CertificateListAdapter certificateListAdapter;
    private int totalCertificatesCount = 0;
    private ActiveUser activeUser;
    int color;
    private AchievementsViewModel achievementsViewModel;
    private ArrayList<CertificateData> certificateData = new ArrayList<>();
    private ArrayList<CertificateData> tempCertificateData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_certificates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getColors();
        initView(view);
        initListnear();
    }

    private void getColors() {
        try {
            color = OustResourceUtils.getColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initListnear() {
        showLoader();
        Bundle bundle = getArguments();
        if (achievementsViewModel != null) {
            certificateData.clear();
            tempCertificateData.clear();
            achievementsViewModel = null;
        }
        achievementsViewModel = new ViewModelProvider(this).get(AchievementsViewModel.class);
        achievementsViewModel.certificatesInitData(bundle, getContext()).observe(requireActivity(), certificatesResponse -> {
            try {
                if (certificatesResponse != null) {
                    if (certificatesResponse.getCertificateData() != null) {
                        if (certificatesResponse.getCertificateData().size() > 0) {
                            certificateData.addAll(certificatesResponse.getCertificateData());
                            Collections.sort(certificateData, CertificateData.sortByDate);
                        }

                        if (certificateData != null && certificateData.size() > 0) {
                            if (certificateData.size() > 10) {
                                for (int i = 0; i < 10; i++) {
                                    tempCertificateData.add(certificateData.get(i));
                                }
                            } else {
                                tempCertificateData.addAll(certificateData);
                            }
                            if (tempCertificateData != null && tempCertificateData.size() > 0) {
                                totalCertificatesCount = tempCertificateData.size();
                            }

                            hideLoader();
                            showData(certificatesResponse);
                        } else {
                            showNoData();
                        }
                    } else {
                        showNoData();
                    }
                } else {
                    showNoData();
                    // Log.e("TAG", "no Data Found: ");
                    /*hideLoader();
                    String userName = activeUser.getStudentKey();
                    showCountAndUserName(userName, 0);*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                showNoData();
            }
        });

    }

    private void showNoData() {
        try {
            hideLoader();
            String userName = "";
            if (activeUser != null) {
                if (activeUser.getUserDisplayName() != null && !activeUser.getUserDisplayName().isEmpty()) {
                    userName = activeUser.getUserDisplayName();
                } else {
                    userName = activeUser.getStudentid();
                }
            }
            showCountAndUserName(userName, 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showData(CertificatesResponse certificatesResponse) {
        String userName = "";
        int badgeListSize = 0;

        if (certificatesResponse != null) {

            if (certificatesResponse.getUserDisplayName() != null && !certificatesResponse.getUserDisplayName().isEmpty()) {
                userName = certificatesResponse.getUserDisplayName();
            } else {
                if (activeUser != null) {
                    if (activeUser.getUserDisplayName() != null && !activeUser.getUserDisplayName().isEmpty()) {
                        userName = activeUser.getUserDisplayName();
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
            }
        }

        if (certificatesResponse != null && certificatesResponse.getCertificateCount() != null) {
            badgeListSize = certificatesResponse.getCertificateCount();
        }
        showCountAndUserName(userName, badgeListSize);
        certificate_relativeLayout_loader.setVisibility(View.GONE);

        // Log.e("TAG", "showData: " + certificateData.size());
        certificateListAdapter = new CertificateListAdapter();
        certificateListAdapter.setCertificateListAdapter(tempCertificateData, getContext(), CertificatesFragment.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        certificate_recycler.setLayoutManager(mLayoutManager);
        certificate_recycler.setItemAnimator(new DefaultItemAnimator());
        certificate_recycler.setAdapter(certificateListAdapter);
        certificateListAdapter.notifyDataSetChanged();
    }

    private void showCountAndUserName(String userName, int badgeListSize) {
        String infoText;
        if (badgeListSize == 0) {
            infoText = getResources().getString(R.string.you_have_not_);
        } else {
            infoText = getResources().getString(R.string.you_have_achieved) + " " + badgeListSize;
        }
        String userGreetings = getResources().getString(R.string.hello_) + userName.trim() + ",";

        if (badgeListSize <= 1) {
            infoText = infoText + " " + getResources().getString(R.string.certificate_) + ".";
        } else {
            infoText = infoText + " " + getResources().getString(R.string.certificate) + ".";
        }

        userCertificateCount.setText(infoText);
        userNameTxt.setText(userGreetings);

    }

    private void initView(View view) {
        try {
            certificate_recycler = view.findViewById(R.id.certificate_recycler_view);
            no_data_text = view.findViewById(R.id.no_data_text_certificate);
            userCertificateCount = view.findViewById(R.id.certificate_count);
            userNameTxt = view.findViewById(R.id.certificate_user_name);
            certificate_relativeLayout_loader = view.findViewById(R.id.certificate_loader_layout);
            branding_bg = view.findViewById(R.id.certificate_branding_bg);
            brand_loader = view.findViewById(R.id.certificate_brand_loader);

            activeUser = OustAppState.getInstance().getActiveUser();

            userCertificateCount.setTextColor(color);
            userNameTxt.setTextColor(color);

            certificate_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!recyclerView.canScrollVertically(1)) {
                        //Log.e("TAG", "onScrollStateChanged: ");
                        if (certificateData != null) {
                            if (certificateData.size() != totalCertificatesCount) {
                                loadData();
                            }
                        }
                    }
                }
            });

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

    private void loadData() {
        try {
            if (certificateData != null && certificateData.size() > 0) {
                int size = tempCertificateData.size() + 10;
                if (certificateData.size() >= size) {
                    for (int i = 0; i < 10; i++) {
                        tempCertificateData.add(certificateData.get(i));
                    }
                } else {
                    for (int j = totalCertificatesCount; j < certificateData.size(); j++) {
                        tempCertificateData.add(certificateData.get(j));
                    }
                }
                totalCertificatesCount = tempCertificateData.size();
                certificateListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLoader() {
        try {
            certificate_relativeLayout_loader.setVisibility(View.VISIBLE);
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideLoader() {
        try {
//            gif_loader.setVisibility(View.GONE);
            certificate_relativeLayout_loader.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadCertificate(String certificateUrl, String courseName) {
        try {
            String fileName = certificateUrl.substring(certificateUrl.lastIndexOf("/") + 1);
            OustSdkTools.showToast("Downloading started");

            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(certificateUrl);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(courseName + " " + getResources().getString(R.string.certificate_));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void searchCertificateNotFound(String s) {
        try {
            if (s.equalsIgnoreCase("notFound")) {
                no_data_text.setVisibility(View.VISIBLE);
            } else {
                no_data_text.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void searchBarData(String query) {
        try {
            if (certificateData.size() == 0) {
                no_data_text.setVisibility(View.VISIBLE);
            } else {
                certificateListAdapter.getFilter().filter(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void openCertificate(String certificateUrl, String courseName) {
        try {
            //Log.e("TAG", "openCertificate: " + certificateUrl);
            Intent intent = new Intent(getContext(), ViewCertificate.class);
            intent.putExtra("certificateUrl", certificateUrl);
            intent.putExtra("courseName", courseName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}