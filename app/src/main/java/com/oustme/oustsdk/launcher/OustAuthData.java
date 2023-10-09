package com.oustme.oustsdk.launcher;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 06/04/17.
 */

@Keep
public class OustAuthData {
    String orgId=null;
    String username=null;
    String password=null;
    String language=null;
    String fName=null;
    String lName=null;

    public OustAuthData() {}

    public OustAuthData(String orgId, String username, String password) {
        this.orgId = orgId;
        this.username = username;
        this.password = password;
    }

    public OustAuthData(String orgId, String username, String password, String language) {
        this.orgId = orgId;
        this.username = username;
        this.password = password;
        this.language = language;
    }

    public OustAuthData(String orgId, String username) {
        this.orgId = orgId;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }
}
