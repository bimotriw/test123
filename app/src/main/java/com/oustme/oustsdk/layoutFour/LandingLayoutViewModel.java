package com.oustme.oustsdk.layoutFour;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.layoutFour.data.LandingLayout;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.response.ParentCplDistributionResponse;

import java.util.ArrayList;

public class LandingLayoutViewModel extends ViewModel {

    private MutableLiveData<LandingLayout> listBottomNav;
    private LandingLayoutRepository mRepo;

    public void init(Context context) {
        if (listBottomNav != null) {
            return;
        }
        mRepo = LandingLayoutRepository.getInstance(context);
        listBottomNav = mRepo.getLandingLayout();
    }

    public MutableLiveData<LandingLayout> getBottomNavList() {
        return listBottomNav;
    }

    public MutableLiveData<ArrayList<NotificationResponse>> getNotificationCount() {
        return mRepo.getNotificationContRepository();
    }
    public MutableLiveData<ParentCplDistributionResponse> checkParentCplDistributesOrNot() {
        return mRepo.checkParentCplDistributesOrNot();
    }

    public MutableLiveData<Long> getDirectMessageCount() {
        return mRepo.getDirectMessageCountRepository();
    }
}
