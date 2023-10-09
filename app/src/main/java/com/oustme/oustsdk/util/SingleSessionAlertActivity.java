package com.oustme.oustsdk.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
import com.oustme.oustsdk.tools.OustSdkTools;

public class SingleSessionAlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(SingleSessionAlertActivity.this);
        setContentView(R.layout.activity_single_session_alert);
        showSingleSessionDialogue();
    }

    private void showSingleSessionDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alert_single_session, null);
        builder.setView(alertLayout);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();

        alertLayout.findViewById(R.id.linearLayoutYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendToSomeActivity(SingleSessionHandler.YES);
                SingleSessionHandler.getInstance().setDialogCallbackCode(SingleSessionHandler.YES);
                alertDialog.dismiss();
                finish();
            }
        });

        alertLayout.findViewById(R.id.linearLayoutNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendToSomeActivity(SingleSessionHandler.NO);
                SingleSessionHandler.getInstance().setDialogCallbackCode(SingleSessionHandler.NO);
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog.show();
    }

    private void sendToSomeActivity(int code) {
        Intent intent = new Intent();
        intent.setAction(SingleSessionHandler.SingleSessionAlertReceiver.ACTION);
        intent.putExtra("dataToPass", code);
        sendBroadcast(intent);
    }


}
