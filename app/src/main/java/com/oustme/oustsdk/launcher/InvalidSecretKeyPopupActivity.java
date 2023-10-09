package com.oustme.oustsdk.launcher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.tools.OustSdkTools;


/**
 * Created by shilpysamaddar on 27/04/17.
 */

public class InvalidSecretKeyPopupActivity extends AppCompatActivity implements View.OnClickListener {
    TextView errorheading, errormsg;
    Button okbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            OustSdkTools.setLocale(InvalidSecretKeyPopupActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_invalidsecretkey);

        initViews();
        okbtn.setOnClickListener(this);

    }

    private void initViews() {
        errorheading = findViewById(R.id.errorheading);
        errormsg = findViewById(R.id.errormsg);
        okbtn = findViewById(R.id.okbtn);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.okbtn) {
            InvalidSecretKeyPopupActivity.this.finish();
        }
    }
}
