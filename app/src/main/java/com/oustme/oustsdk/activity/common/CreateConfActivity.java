package com.oustme.oustsdk.activity.common;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustGATools;

public class CreateConfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conf);
//        OustGATools.getInstance().reportPageViewToGoogle(CreateConfActivity.this,"CreateConfActivity");

    }
}
