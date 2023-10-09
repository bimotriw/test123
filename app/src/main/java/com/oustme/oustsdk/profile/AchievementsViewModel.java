package com.oustme.oustsdk.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.profile.model.AchievementsComponentModel;
import com.oustme.oustsdk.profile.model.CertificatesResponse;

public class AchievementsViewModel extends ViewModel {

    MutableLiveData<AchievementsComponentModel> badgeComponentModelMutableLiveData;
    LiveData<CertificatesResponse> certificatesResponseLiveData;
    AchievementsRepository achievementsRepository;
    Bundle bundleData;

    public AchievementsViewModel() {
        badgeComponentModelMutableLiveData = null;
        achievementsRepository = AchievementsRepository.getInstance();
    }

    public void initData(Bundle bundle) {
        bundleData = bundle;
        badgeComponentModelMutableLiveData = achievementsRepository.getLiveData(bundleData);
    }

    public LiveData<CertificatesResponse> certificatesInitData(Bundle bundle, Context context) {
        certificatesResponseLiveData = achievementsRepository.getCertificatesLiveData(bundleData, context);
        return certificatesResponseLiveData;
    }

    public MutableLiveData<AchievementsComponentModel> getBadgeComponentModelMutableLiveData() {

        return badgeComponentModelMutableLiveData;
    }

}
