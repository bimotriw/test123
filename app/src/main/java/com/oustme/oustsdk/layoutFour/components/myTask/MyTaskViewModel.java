package com.oustme.oustsdk.layoutFour.components.myTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.firebase.common.CommonLandingData;

import java.util.HashMap;

public class MyTaskViewModel extends ViewModel {

    private MutableLiveData<HashMap<String, CommonLandingData>> taskMap;

    public void init() {
        MyTaskRepository mRepo = new MyTaskRepository();
        taskMap = mRepo.getTaskMap();
    }

    public MutableLiveData<HashMap<String, CommonLandingData>> getTaskMap() {
        return taskMap;
    }
}
