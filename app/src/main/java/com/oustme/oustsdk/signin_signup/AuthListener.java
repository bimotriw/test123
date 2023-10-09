package com.oustme.oustsdk.signin_signup;

public interface AuthListener {

    void onLoginStart();
    void onSuccess();
    void onFailure(String msg);
}
