package com.oustme.oustsdk.skill_ui.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.oustme.oustsdk.tools.OustSdkTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SkillSubmissionRequest {

    String studentid;
    ArrayList<SkillSubmisssionDataList> skillSubmissionDataList;



    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public ArrayList<SkillSubmisssionDataList> getSkillSubmissionDataList() {
        return skillSubmissionDataList;
    }

    public void setSkillSubmissionDataList(ArrayList<SkillSubmisssionDataList> skillSubmissionDataList) {
        this.skillSubmissionDataList = skillSubmissionDataList;
    }



    public JSONObject toJSON() {

        JSONObject jo = new JSONObject();

        try {
            jo.put("studentid", studentid);
            String jsonArray = new Gson().toJson(skillSubmissionDataList);
            jo.put("skillSubmissionDataList", new JSONArray(jsonArray));
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return jo;
    }
}
