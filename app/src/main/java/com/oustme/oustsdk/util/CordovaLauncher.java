package com.oustme.oustsdk.util;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clevertap.android.sdk.CleverTapAPI;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.cordovacall.CordovaCallActivity;

public class CordovaLauncher extends AppCompatActivity {

    EditText etOrgid;
    EditText etUserName;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if(clevertapDefaultInstance != null) {
            clevertapDefaultInstance.pushEvent("CordovaLauncher activity");
        }

        setContentView(R.layout.activity_cordova_launcher);

        btnLogin = findViewById(R.id.btn_login);
        etUserName = findViewById(R.id.et_username);
        etOrgid = findViewById(R.id.et_orgid);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orgid = String.valueOf(etOrgid.getText());
                String userName = String.valueOf(etUserName.getText());

                if(!TextUtils.isEmpty(orgid) && !TextUtils.isEmpty(userName)) {
                    Intent intent = new Intent(CordovaLauncher.this, CordovaCallActivity.class);
                    intent.putExtra("USERNAME", userName);
                    intent.putExtra("ORGID", orgid);
                    startActivity(intent);
                }
            }
        });
    }
}