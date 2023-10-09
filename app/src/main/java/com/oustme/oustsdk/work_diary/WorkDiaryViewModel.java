package com.oustme.oustsdk.work_diary;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.work_diary.model.WorkDiaryComponentModule;

public class WorkDiaryViewModel extends ViewModel {

    private MutableLiveData<WorkDiaryComponentModule> workDiaryComponentModuleMutableLiveData;
    Bundle bundleData;

    public void init(Bundle bundle, boolean isMultipleCpl){

      /*  if(workDiaryComponentModuleMutableLiveData!=null){
            return;
        }*/
        workDiaryComponentModuleMutableLiveData = null;

        bundleData = bundle;
        WorkDiaryRepository workDiaryRepository = WorkDiaryRepository.getInstance();
        workDiaryComponentModuleMutableLiveData = workDiaryRepository.getLiveData(bundleData,isMultipleCpl);
    }

    public MutableLiveData<WorkDiaryComponentModule> getBaseComponentModuleMutableLiveData(){
        return workDiaryComponentModuleMutableLiveData;
    }

}
