package com.oustme.oustsdk.layoutFour;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.layoutFour.data.repoData.CommonLanding;

import java.util.HashMap;

public class CommonLandingViewModel extends ViewModel {
    private MutableLiveData<CommonLanding> liveData;
    private CommonLandingRepository mRepo;

    public void init(){
        if(liveData !=null){
            return;
        }
        mRepo = CommonLandingRepository.getInstance();
        liveData = mRepo.getCommonLandingData();
    }

    public MutableLiveData<CommonLanding> getCommonLandingData(){
        return liveData;
    }

}
