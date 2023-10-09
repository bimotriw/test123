package com.oustme.oustsdk.tools;

import com.google.gson.annotations.SerializedName;

public class UserDetailsApp {
    @SerializedName("success")
    private boolean success;
    @SerializedName("exceptionData")
    private String exceptionData;
    @SerializedName("error")
    private String error;
    @SerializedName("userDisplayName")
    private String userDisplayName;
    @SerializedName("errorCode")
    private int errorCode;
    @SerializedName("popup")
    private String popup;
    private UserAppDetails userAppDetails;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
        this.exceptionData = exceptionData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public UserAppDetails getUserData() {
        return userAppDetails;
    }

    public void setUserData(UserAppDetails userData) {
        this.userAppDetails = userData;
    }

    public class UserAppDetails {
        private String badgesCount;

        private String phone;

        private String imageUrl;

        private String rank;

        private String certificateCount;

        private String email;

        private String username;

        public String getBadgesCount() {
            return badgesCount;
        }

        public void setBadgesCount(String badgesCount) {
            this.badgesCount = badgesCount;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getCertificateCount() {
            return certificateCount;
        }

        public void setCertificateCount(String certificateCount) {
            this.certificateCount = certificateCount;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }


}
