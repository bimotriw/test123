package com.oustme.oustsdk.tools;

/**
 * Created by oust on 9/7/18.
 */

public class OustLogDetailHandler {

    private static OustLogDetailHandler single_instance = null;

    // private constructor restricted to this class itself
    private OustLogDetailHandler() {}

    // static method to create instance of Singleton class
    public static OustLogDetailHandler getInstance() {
        if (single_instance == null)
            single_instance = new OustLogDetailHandler();

        return single_instance;
    }


    private boolean isUserForcedOut;

    public boolean isUserForcedOut() {
        return isUserForcedOut;
    }

    public void setUserForcedOut(boolean userForcedOut) {
        isUserForcedOut = userForcedOut;
    }
}
