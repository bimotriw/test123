package com.oustme.oustsdk.tools;

import com.oustme.oustsdk.firebase.common.CplModelData;

/**
 * Created by oust on 8/24/18.
 */

public class CplStaticVariableHandler {
    private static CplStaticVariableHandler single_instance = null;

    // private constructor restricted to this class itself
    private CplStaticVariableHandler() {}

    // static method to create instance of Singleton class
    public static CplStaticVariableHandler getInstance() {
        if (single_instance == null)
            single_instance = new CplStaticVariableHandler();

        return single_instance;
    }

    private boolean isCplSuccessModuleGot = false;
    private boolean isCplSuccessModuleOpen=false;
    private boolean isCplVisible=false;
    private CplModelData cplModelData;

    public boolean isCplSuccessModuleOpen() {
        return isCplSuccessModuleOpen;
    }

    public void setCplSuccessModuleOpen(boolean cplSuccessModuleOpen) {
        isCplSuccessModuleOpen = cplSuccessModuleOpen;
    }

    public boolean isCplVisible() {
        return isCplVisible;
    }

    public void setCplVisible(boolean cplVisible) {
        isCplVisible = cplVisible;
    }

    public CplModelData getCplModelData() {
        return cplModelData;
    }

    public void setCplModelData(CplModelData cplModelData) {
        this.cplModelData = cplModelData;
    }

    public boolean isCplSuccessModuleGot() {
        return isCplSuccessModuleGot;
    }

    public void setCplSuccessModuleGot(boolean cplSuccessModuleGot) {
        isCplSuccessModuleGot = cplSuccessModuleGot;
    }
}
