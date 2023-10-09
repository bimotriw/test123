package com.oustme.oustsdk.downloadHandler;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import android.widget.Toast;

import java.io.File;

public class DownloadHandler {

    //TODO: this class to handle download files


    public DownloadHandler() {
    }

    public  void downloadManager(String downloadUrl, String fileName, Context context) {


        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription(fileName);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        long ref = downloadManager.enqueue(request);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);


        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadReference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.i("DownloadHandler", "Download completed");


                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadReference);

                Cursor cur = downloadManager.query(query);

                if (cur.moveToFirst()) {
                    int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);


                    if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                        String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Toast.makeText(context, "File has been downloaded successfully.", Toast.LENGTH_SHORT).show();

                    } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                        int columnReason = cur.getColumnIndex(DownloadManager.COLUMN_REASON);
                        int reason = cur.getInt(columnReason);
                        switch (reason) {

                            case DownloadManager.ERROR_FILE_ERROR:
                                Toast.makeText(context, "Download Failed.File is corrupt.", Toast.LENGTH_LONG).show();
                                break;
                            case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                Toast.makeText(context, "Download Failed.Http Error Found.", Toast.LENGTH_LONG).show();
                                break;
                            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                Toast.makeText(context, "Download Failed due to insufficient space in internal storage", Toast.LENGTH_LONG).show();
                                break;

                            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                Toast.makeText(context, "Download Failed. Http Code Error Found.", Toast.LENGTH_LONG).show();
                                break;
                            case DownloadManager.ERROR_UNKNOWN:
                                Toast.makeText(context, "Download Failed.", Toast.LENGTH_LONG).show();
                                break;
                            case DownloadManager.ERROR_CANNOT_RESUME:
                                Toast.makeText(context, "ERROR_CANNOT_RESUME", Toast.LENGTH_LONG).show();
                                break;
                            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                Toast.makeText(context, "ERROR_TOO_MANY_REDIRECTS", Toast.LENGTH_LONG).show();
                                break;
                            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                Toast.makeText(context, "ERROR_DEVICE_NOT_FOUND", Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                }
            }

        };


        context.getApplicationContext().registerReceiver(receiver, filter);
    }
}
