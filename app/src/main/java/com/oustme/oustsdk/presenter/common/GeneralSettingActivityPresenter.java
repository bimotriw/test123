package com.oustme.oustsdk.presenter.common;

import android.util.Base64;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.GeneralSettingActivity;
import com.oustme.oustsdk.firebase.common.UserBalanceState;
import com.oustme.oustsdk.request.UserSettingRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustCommonUtils;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by shilpysamaddar on 23/03/17.
 */

public class GeneralSettingActivityPresenter {
    private final GeneralSettingActivity view;

    private final ActiveUser activeUser;
    private ActiveUser tempActiveUser;
    private boolean hasTanentId = false;
    private String GenderSelected;
    private long dobTimeStamp;
    private String imageString;


    public GeneralSettingActivityPresenter(GeneralSettingActivity view) {
        this.view = view;
        activeUser = OustAppState.getInstance().getActiveUser();
        tempActiveUser = new ActiveUser();
    }

    public void setTanentIdStatus(String tanentID) {
        if ((tanentID != null) && (!tanentID.isEmpty())) {
            hasTanentId = true;
        }
    }

    public void setStartingData() {
        setMainUserAvatar();
        if ((activeUser.getUserDisplayName() != null)) {
            view.setUserName(activeUser.getUserDisplayName());
        }
        if (activeUser.getDob() > 1000) {
            view.setUserDOB(activeUser.getDob());
        }
        if (activeUser.getUserGender() != null) {
            if (activeUser.getUserGender().equalsIgnoreCase("male")) {
                view.setGenderAsMale();
            } else if (activeUser.getUserGender().equalsIgnoreCase("female")) {
                view.setGenderAsFemale();
            } else if (activeUser.getUserGender().equalsIgnoreCase("Transgender")) {
                view.setGenderAsTransgender();
            } else if (activeUser.getUserGender().equalsIgnoreCase("Other")) {
                view.setGenderAsOther();
            }
        }
        if (activeUser.isDomainNameAuthentication()) {
            view.setDisableEmail();
        }
        if (activeUser.getEmail() != null) {
            view.setEmail(activeUser.getEmail());
        }
        view.setCity(activeUser.getUserCity());
        if (activeUser.getUserCountry() != null) {
            if (activeUser.getUserCountry().equalsIgnoreCase("in")) {
                view.setUsersCountry("india");
            } else {
                view.setUsersCountry(activeUser.getUserCountry());
            }
        }
        view.setUserMobileNo(activeUser.getUserMobile());
        if ((UserBalanceState.getInstance().getCitis() != null) && (UserBalanceState.getInstance().getCitis().size() > 0)) {
            view.setCityListToBeEntered(UserBalanceState.getInstance().getCitis());
        }
        view.setUserDateOfBirthCalander();
    }

    private void setMainUserAvatar() {
        if ((OustSdkTools.tempProfile != null)) {
            view.setSavedUserAvatar();
        } else {
            String avatar = activeUser.getAvatar();
            if ((avatar != null) && (!avatar.isEmpty())) {
                view.setUserAvatar(avatar);
            }
        }
    }

    public void setTypeOfOustUser(String loginType) {
        if (loginType != null) {
            if (loginType.equalsIgnoreCase("GoogleApp")) {
                view.setTypeOfUser(OustStrings.getString("googleText"), OustSdkTools.getColorBack(R.color.Orange));
            } else if (loginType.equalsIgnoreCase("FacebookApp")) {
                view.setTypeOfUser(OustStrings.getString("FacebookApp"), OustSdkTools.getColorBack(R.color.Blue));
            } else {
                if (hasTanentId) {
                    view.setTypeOfUser(OustStrings.getString("oustLoginText"), OustSdkTools.getColorBack(R.color.LiteGreen));
                    view.showChangeAvatarIcon();
                }
            }
        }
    }

    public void choosePicButtonClick() {
        if (hasTanentId) {
            view.checkForStoragePermission();
        }
    }

    public void saveSelectedDob(Long timeStamp) {
        this.dobTimeStamp = timeStamp;
        view.setUserDOB(timeStamp);
    }

    public void saveImageString(String imgStr) {
        if ((imgStr != null) && (!imgStr.isEmpty())) {
            this.imageString = imgStr;
//            setUserInformationToSave(null, null, null, null, null, null, null, null);
        }
    }

    public void saveButtonClicked(int selectedId, int maleId, int femaleId, int transId, int otherId) {
        if (selectedId == maleId) {
            GenderSelected = "Male";
        } else if (selectedId == femaleId) {
            GenderSelected = "Female";
        } else if (selectedId == transId) {
            GenderSelected = "Transgender";
        } else if (selectedId == otherId) {
            GenderSelected = "Other";
        }
    }

    public void setUserInformationToSave(String name, String dob, String userMail, String city, String country, String mob, String password, String confirmpassword) {
        try {
            tempActiveUser = new ActiveUser();
            UserSettingRequest userSettingRequest = new UserSettingRequest();
//            if ((name != null) && (!name.isEmpty())) {
            if (name == null) {
                name = "";
            }
            String tempStr = name;
            tempActiveUser.setUserDisplayName(tempStr);
            tempActiveUser.setFname(tempStr);
            userSettingRequest.setFname(tempStr);
            tempActiveUser.setlName(null);
            userSettingRequest.setLname(null);

            if (dobTimeStamp > 1000) {
                tempActiveUser.setDob(dobTimeStamp);
                userSettingRequest.setDob("" + dobTimeStamp);
            }

            if ((userMail != null) && (!userMail.isEmpty())) {
                if (OustSdkTools.isValidEmail(userMail.trim())) {
                    tempActiveUser.setEmail(userMail);
                    userSettingRequest.setEmail(userMail);
                } else {
                    OustSdkTools.showToast(OustStrings.getString("enter_valid_mail"));
                    return;
                }
            } else {
                if (userMail == null) {
                    userMail = "";
                }
                tempActiveUser.setEmail(userMail);
                userSettingRequest.setEmail(userMail);
            }
//            if ((city != null) && (!city.isEmpty())) {
            if (city == null) {
                city = "";
            }
            tempActiveUser.setUserCity(city);
            userSettingRequest.setCity(city);
//            }
//            if ((country != null) && (!country.isEmpty())) {
            if (country == null) {
                country = "";
            }
            tempActiveUser.setUserCountry(country);
            userSettingRequest.setCountry(country);
//            }
            try {
                if ((mob != null) && (!mob.isEmpty())) {
                    if ((mob.length() < 6) || (mob.length() > 13)) {
                        OustSdkTools.showToast(OustStrings.getString("enter_valid_mobile"));
                        return;
                    }
                } else {
                    if (mob == null) {
                        mob = "";
                    }
                }
                tempActiveUser.setUserMobile(Long.parseLong(mob));
                userSettingRequest.setPhone("" + mob);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            if (GenderSelected != null) {
                tempActiveUser.setUserGender(GenderSelected);
                userSettingRequest.setGender(GenderSelected);
            }
            if ((imageString != null) && (imageString.length() > 10)) {
                userSettingRequest.setAvatarImgData(imageString);
            }
            if ((password != null) && (!password.isEmpty())) {
                if ((password.length() < 4)) {
                    view.setPasswordError();
                    return;
                } else {
                    if ((confirmpassword != null) && (confirmpassword.equals(password))) {
                        try {
                            String encryptedPassword = OustCommonUtils.getMD5EncodedString(password);
                            userSettingRequest.setPassword(encryptedPassword);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else {
                        view.setConfirmPasswordError();
                        return;
                    }
                }
            }
            view.saveUserInfo(userSettingRequest, activeUser);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getEncryptPassword(String encryptedValue) {
        try {
            String AES_ENCRYPTION_KEY = "OUSTMESECUREDKEY";
            Key key = new SecretKeySpec(AES_ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            String AES_KEY = "AES";
            Cipher c = Cipher.getInstance(AES_KEY);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(encryptedValue.getBytes());
            encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return encryptedValue;
    }

    public void saveProcessFinish(CommonResponse commonResponse) {
        if (commonResponse != null) {
            if (commonResponse.isSuccess()) {
                view.showToast(OustStrings.getString("profileUpdateSuccess"));
//                if((tempActiveUser.getFname()!=null)&&(!tempActiveUser.getFname().isEmpty())){
                activeUser.setFname(tempActiveUser.getFname());
//                }
//                if((tempActiveUser.getlName()!=null)&&(!tempActiveUser.getlName().isEmpty())){
                activeUser.setlName(tempActiveUser.getlName());
//                }
                if ((tempActiveUser.getDob() > 1000)) {
                    activeUser.setDob(tempActiveUser.getDob());
                }
//                if((tempActiveUser.getEmail()!=null)&&(!tempActiveUser.getEmail().isEmpty())){
                activeUser.setEmail(tempActiveUser.getEmail());
//                }
//                if((tempActiveUser.getUserCity()!=null)&&(!tempActiveUser.getUserCity().isEmpty())){
                activeUser.setUserCity(tempActiveUser.getUserCity());
//                }
//                if((tempActiveUser.getUserCountry()!=null)&&(!tempActiveUser.getUserCountry().isEmpty())){
                activeUser.setUserCountry(tempActiveUser.getUserCountry());
//                }
//                if((tempActiveUser.getUserMobile()>1000)){
                activeUser.setUserMobile(tempActiveUser.getUserMobile());
//                }
//                if((tempActiveUser.getUserGender()!=null)&&(!tempActiveUser.getUserGender().isEmpty())){
                activeUser.setUserGender(tempActiveUser.getUserGender());
//                }
                activeUser.setAvatar(OustAppState.getInstance().getActiveUser().getAvatar());
                OustAppState.getInstance().setActiveUser(activeUser);
                Gson gsonString = new Gson();
                OustPreferences.save("userdata", gsonString.toJson(activeUser));
                view.finishSettingView();
            } else {
                if (commonResponse.getPopup() != null) {
                    view.showErrorPopup(commonResponse.getPopup());
                } else if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
                    view.showToast(commonResponse.getError());
                } else {
                    view.showToast(OustStrings.getString("updateFailureMsg"));
                }
            }
        } else {
            view.showToast(OustStrings.getString("retry_internet_msg"));
        }
    }

    public void checkValidPassword(String password, String confirmPassword) {
        if ((password.length() < 4)) {
            view.setPasswordError();
        } else {
            if ((confirmPassword != null) && (!confirmPassword.isEmpty())) {
                if ((!password.equals(confirmPassword))) {
                    view.setConfirmPasswordError();
                }
            } else {
                view.removeConfirmPasswordError();
            }
        }
    }

    public void checkPasswordValidity(String password) {
        if ((password != null) && (!password.isEmpty())) {
            if (password.length() < 4) {
                view.setPasswordError();
            }
        } else {
            view.removeError();
        }
    }

}
