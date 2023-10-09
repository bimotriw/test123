package com.oustme.oustsdk.activity.rough;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.api_sdk.impl.OustApiLauncher;

public class RapidoTestingActivity extends AppCompatActivity {

    private final String orgId = "rapido";
    private Button mBtnInitResources;
    private TextView mtxtShowStatus;
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Rapido", "onCreate: ");
        setContentView(R.layout.activity_rapido_testing);

        mBtnInitResources = findViewById(R.id.mBtnInitResources);
        mtxtShowStatus = findViewById(R.id.mtxtShowStatus);
        imageview= findViewById(R.id.imageview);

        mBtnInitResources.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                OustApiLauncher.newInstance().initResources(RapidoTestingActivity.this, orgId, null);
            }
        });

    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int width=imageview.getWidth();
        int height=imageview.getHeight();
        Log.d("Rapido ", "Window focus: height->"+height+" -- width:"+width);

        int height1 = imageview.getLayoutParams().height;
        int width1 = imageview.getLayoutParams().width;

        Log.d("Rapido", "onCreate: height->"+height1+" -- width:"+width1);

        imageview.getLayoutParams().height = 100;
        imageview.getLayoutParams().width = 100;

        //System.out.println("Height:"+height+" --- width:"+width);
    }*/
}
