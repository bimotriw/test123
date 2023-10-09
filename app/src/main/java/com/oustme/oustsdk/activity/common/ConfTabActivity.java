package com.oustme.oustsdk.activity.common;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustGATools;

public class ConfTabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_tab);
//        OustGATools.getInstance().reportPageViewToGoogle(ConfTabActivity.this,"ConfTabActivity");

    }
}
