package com.oustme.oustsdk.util;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class DialogActivity extends AppCompatActivity {
    public static final String DILOG_RETRY_ACTION = "DialogRerty";
    public static final String DILOG_FORCE_CLOSE_ACTION = "DialogRerty";
    private static final String TAG = "DialogActivity";
    private AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        /*String messageToDisplay = getResources().getString(R.string.retry_inrpt_msg");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        // Set the Alert Dialog Message
        builder.setMessage(messageToDisplay)
                .setCancelable(false)
                .setPositiveButton("Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent intent = new Intent();
                                intent.setAction(DILOG_RETRY_ACTION);
                                sendBroadcast(intent);
                                DialogActivity.this.finish();
                            }
                        });
        alert = builder.create();
        alert.show();*/


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new OustDialogFragment(() -> {
            Intent intent = new Intent();
            intent.setAction(DILOG_RETRY_ACTION);
            sendBroadcast(intent);
            DialogActivity.this.finish();
        });
        dialogFragment.setCancelable(false);
        dialogFragment.show(ft, "dialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction(DILOG_FORCE_CLOSE_ACTION);
        sendBroadcast(intent);
        Log.d(TAG,"onDestroy");
    }


    @Override
    public void finish() {
        super.finish();
        DialogActivity.this.overridePendingTransition(0,0);

    }
}
