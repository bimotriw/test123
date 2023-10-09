package com.oustme.oustsdk.signin_signup;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.databinding.ActivityOustSignBinding;

public class OustSignActivity extends AppCompatActivity implements AuthListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOustSignBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_oust_sign);
        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.setViewmodel(authViewModel);
        authViewModel.setAuthListener(this);
    }

    @Override
    public void onLoginStart() {
        Toast.makeText(this, "onLoginStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "onSuccess", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(this, "onFailure", Toast.LENGTH_SHORT).show();
    }
}
