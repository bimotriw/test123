package com.oustme.oustsdk.layoutFour.components.userOverView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActiveUserViewModel extends ViewModel {

    private MutableLiveData<ActiveUserModel> mActiveUser;
    private ActiveUserRepository mRepo;

    public void init() {
        mRepo = ActiveUserRepository.getInstance();
        mActiveUser = mRepo.getActiveUser();
    }

    public void updateCertificateCount() {
        mRepo.getUserFromFirebase();
    }

    public LiveData<ActiveUserModel> getmActiveUser() {
        return mActiveUser;
    }
}
