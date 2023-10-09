package com.oustme.oustsdk.activity.common;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustTagHandlerNew;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ShareFeedActivity extends AppCompatActivity {

    private DTONewFeed DTONewFeed;
    private ImageView feed_image;
    private LinearLayout share_ll;
    private RelativeLayout ll_close;
    private RelativeLayout main_layout, feed_image_ll;
    private Bitmap shareBitmap;
    private ActiveUser activeUser;
    private TextView feed_title, feed_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_feed);

        setActiveUserData();
        DTONewFeed = OustDataHandler.getInstance().getNewFeed();
        if (DTONewFeed != null) {
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.error_message));
            finish();
            return;
        }
        initViews();

    }

    private void setActiveUserData() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                OustSdkApplication.setmContext(ShareFeedActivity.this);
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        } catch (Exception e) {
        }
    }

    private void initViews() {
        try {
            feed_image = findViewById(R.id.feed_image);
            share_ll = findViewById(R.id.share_ll);
            ll_close = findViewById(R.id.ll_close);
            main_layout = findViewById(R.id.main_layout);
            feed_image_ll = findViewById(R.id.feed_image_ll);
            feed_title = findViewById(R.id.feed_title);
            feed_description = findViewById(R.id.feed_description);

            if (DTONewFeed != null && DTONewFeed.getImageUrl() != null && !DTONewFeed.getImageUrl().isEmpty()) {
                Picasso.get().load(DTONewFeed.getImageUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        feed_image.setImageBitmap(bitmap);

                        shareBitmap = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

            }

            if (DTONewFeed.getContent() != null) {
                if (DTONewFeed.getLink() != null) {
                    if (DTONewFeed.getBtntext() != null)
                        feed_description.setText(Html.fromHtml(DTONewFeed.getContent() + " <a href=\"" + DTONewFeed.getLink() + "/\">" + DTONewFeed.getBtntext() + "</a>", null, new OustTagHandlerNew()));
                    else
                        feed_description.setText(Html.fromHtml(DTONewFeed.getContent() + " <a href=\"" + DTONewFeed.getLink() + "/\">" + "</a>", null, new OustTagHandlerNew()));
                } else {
                    if (DTONewFeed.getBtntext() != null) {
                        feed_description.setText(Html.fromHtml(DTONewFeed.getContent() + DTONewFeed.getBtntext(), null, new OustTagHandlerNew()));
                    } else {
                        feed_description.setText(Html.fromHtml(DTONewFeed.getContent(), null, new OustTagHandlerNew()));
                    }
                }
            } else {
                if (DTONewFeed.getLink() != null) {
                    feed_description.setText(Html.fromHtml(" <a href=\"" + DTONewFeed.getLink() + "/\">" + DTONewFeed.getBtntext() + "</a>", null, new OustTagHandlerNew()));
                }
            }

            Spanned headerText = Html.fromHtml(DTONewFeed.getHeader());
            feed_title.setText(headerText);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        ll_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        share_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareClick();
            }
        });
    }

    private void shareClick() {
        if (DTONewFeed != null)
            setUserShareCount(DTONewFeed.getFeedId());
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        shareBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(getContentResolver(), shareBitmap, "Title", null);
//        Uri imageUri = Uri.parse(path);
//
//        String oustShareMessage= OustStrings.getString("cpmmonsharetext");
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hii");
//        shareIntent.put
// Extra(Intent.EXTRA_SUBJECT, oustShareMessage );
//        //shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//        shareIntent.setType("image/jpeg,text/plain");
//        startActivity(Intent.createChooser(shareIntent, "Share via"));
        String commonText = getResources().getString(R.string.commonsharetext);
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShareFeedActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        } else {
            if (DTONewFeed.getLink() != null && !DTONewFeed.getLink().isEmpty())
                OustShareTools.share(ShareFeedActivity.this, OustSdkTools.getInstance().getScreenShot(feed_image_ll), " " + DTONewFeed.getLink());
            else
                OustShareTools.share(ShareFeedActivity.this, OustSdkTools.getInstance().getScreenShot(feed_image_ll), " ");
        }

    }

    private void setUserShareCount(long feedId) {
        try {
            String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isShared";
            OustFirebaseTools.getRootRef().child(message1).setValue(true);
            OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

            String message = "feeds/feed" + feedId + "/numShares";
            DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
            firebase.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    int count = 0;
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                        count = 1;
                    } else {
                        currentData.setValue((Long) currentData.getValue() + 1);
                        count = (int) (long) currentData.getValue();
                    }
                    DTONewFeed.setNumShares(count);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (DatabaseError != null) {
                        Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                    } else {
                        Log.e("", "Firebase counter increment succeeded.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
