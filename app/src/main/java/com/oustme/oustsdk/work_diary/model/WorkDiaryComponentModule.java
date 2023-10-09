package com.oustme.oustsdk.work_diary.model;

import com.oustme.oustsdk.tools.ActiveUser;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkDiaryComponentModule {

    ArrayList<WorkDiaryFilterModel> workDiaryFilterModelArrayList;
    ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList;
    ArrayList<WorkDiaryDetailsModel> workDiaryDetailsManualArrayList;
    ActiveUser activeUser;
    long latestEntryInMill;
    private HashMap<String, HashMap<String, ArrayList<WorkDiaryDetailsModel>>> workDiaryBaseHashMap;

    public ArrayList<WorkDiaryFilterModel> getWorkDiaryFilterModelArrayList() {
        return workDiaryFilterModelArrayList;
    }

    public void setWorkDiaryFilterModelArrayList(ArrayList<WorkDiaryFilterModel> workDiaryFilterModelArrayList) {
        this.workDiaryFilterModelArrayList = workDiaryFilterModelArrayList;
    }

    public ArrayList<WorkDiaryDetailsModel> getWorkDiaryDetailsModelArrayList() {
        return workDiaryDetailsModelArrayList;
    }

    public void setWorkDiaryDetailsModelArrayList(ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList) {
        this.workDiaryDetailsModelArrayList = workDiaryDetailsModelArrayList;
    }

    public ArrayList<WorkDiaryDetailsModel> getWorkDiaryDetailsManualArrayList() {
        return workDiaryDetailsManualArrayList;
    }

    public void setWorkDiaryDetailsManualArrayList(ArrayList<WorkDiaryDetailsModel> workDiaryDetailsManualArrayList) {
        this.workDiaryDetailsManualArrayList = workDiaryDetailsManualArrayList;
    }

    public HashMap<String, HashMap<String, ArrayList<WorkDiaryDetailsModel>>> getWorkDiaryBaseHashMap() {
        return workDiaryBaseHashMap;
    }

    public void setWorkDiaryBaseHashMap(HashMap<String, HashMap<String, ArrayList<WorkDiaryDetailsModel>>> workDiaryBaseHashMap) {
        this.workDiaryBaseHashMap = workDiaryBaseHashMap;
    }

    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public long getLatestEntryInMill() {
        return latestEntryInMill;
    }

    public void setLatestEntryInMill(long latestEntryInMill) {
        this.latestEntryInMill = latestEntryInMill;
    }
}

