package com.oustme.oustsdk.presenter.common;

import com.oustme.oustsdk.activity.common.AcademicsSettingActivity;
import com.oustme.oustsdk.request.UserSettingRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;

/**
 * Created by shilpysamaddar on 23/03/17.
 */

public class AcademicsSettingActivityPresenter {
    private AcademicsSettingActivity view;
    private ActiveUser activeUser;
    private List<String> gradeList;

    public AcademicsSettingActivityPresenter(AcademicsSettingActivity view) {
        this.view = view;
        activeUser= OustAppState.getInstance().getActiveUser();
        setStartingData();
    }

    public void setGradeList(List<String> gradeList) {
        this.gradeList = gradeList;
    }

    private void setStartingData(){
        if(OustSdkTools.tempProfile!=null){
            view.setSavedAvatar();
        }else {
            if((activeUser.getAvatar()!=null)&&(!activeUser.getAvatar().isEmpty())){
                if(activeUser.getAvatar().startsWith("http")){
                    view.setUserAvatar(activeUser.getAvatar());
                }else {
                    view.setUserAvatar((OustStrings.getMainString("Oust_User_Avatar_link")+activeUser.getAvatar()));
                }
            }
        }
        if(activeUser.getUserDisplayName()!=null){
            view.setUserName(activeUser.getUserDisplayName());
        }
        if((activeUser.getSchoolName()!=null)){
            view.setUserSchool(activeUser.getSchoolName());
        }
        view.setGradeList(activeUser.getGrade());
    }


    public void saveBtnClick(String grade,String collenge){
        UserSettingRequest userSettingRequest=new UserSettingRequest();
        if ((grade!=null)&&(!grade.isEmpty())) {
            if((gradeList!=null)&&(gradeList.size()>0)) {
                if(gradeList.contains(grade)) {
                    userSettingRequest.setGradeStr(grade);
                }else {
                    OustSdkTools.showToast(OustStrings.getString("userGradeSelectionError"));
                    return;
                }
            }else {
                userSettingRequest.setGradeStr(grade);
            }
        }
        if ((collenge!=null)&&(!collenge.isEmpty())) {
            userSettingRequest.setSchoolName(collenge);
        }
        view.saveAllSetting(userSettingRequest,activeUser);
    }

    public void saveProcessFinish(CommonResponse commonResponse, UserSettingRequest userSettingRequest){
        if(commonResponse!=null){
            if(commonResponse.isSuccess()){
                view.showToast(OustStrings.getString("profileUpdateSuccess"));
                activeUser=OustAppState.getInstance().getActiveUser();
                if((userSettingRequest.getSchoolName()!=null)&&(!userSettingRequest.getSchoolName().isEmpty())){
                    activeUser.setSchoolName(userSettingRequest.getSchoolName());
                }
                if((userSettingRequest.getGradeStr()!=null)&&(!userSettingRequest.getGradeStr().isEmpty())){
                    activeUser.setGrade(userSettingRequest.getGradeStr());
                }
                OustAppState.getInstance().setActiveUser(activeUser);
                view.finishSettingView();
            }else {
                if(commonResponse.getPopup()!=null){
                    view.showErrorPopup(commonResponse.getPopup());
                }else if((commonResponse.getError()!=null)&&(!commonResponse.getError().isEmpty())){
                    view.showToast(commonResponse.getError());
                }else {
                    view.showToast(OustStrings.getString("updateFailureMsg"));
                }
            }
        }else {
            view.showToast(OustStrings.getString("retry_internet_msg"));
        }
    }
}
