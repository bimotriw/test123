package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

@Keep
public class QuestionFeedBackResponse {
    private String error;

    private String popup;

    private String userDisplayName;

    private Boolean success;

    public String getError ()
    {
        return error;
    }

    public void setError (String error)
    {
        this.error = error;
    }

    public String getPopup ()
    {
        return popup;
    }

    public void setPopup (String popup)
    {
        this.popup = popup;
    }

    public String getUserDisplayName ()
    {
        return userDisplayName;
    }

    public void setUserDisplayName (String userDisplayName)
    {
        this.userDisplayName = userDisplayName;
    }

    public Boolean getSuccess ()
    {
        return success;
    }

    public void setSuccess (Boolean success)
    {
        this.success = success;
    }

    @Override
    public String toString()
    {
        return "QuestionFeedBackResponse [error = "+error+", popup = "+popup+", userDisplayName = "+userDisplayName+", success = "+success+"]";
    }
}
