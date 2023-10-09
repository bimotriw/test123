package com.oustme.oustsdk.signin_signup;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {

    String email = null;
    String password = null;
    AuthListener authListener = null;

    public void onLoginButtonClicked(View view) {
        authListener.onLoginStart();
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
            authListener.onFailure("Invalid username or password");
        }
        authListener.onSuccess();
        return;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthListener getAuthListener() {
        return authListener;
    }

    public void setAuthListener(AuthListener authListener) {
        this.authListener = authListener;
    }
}
