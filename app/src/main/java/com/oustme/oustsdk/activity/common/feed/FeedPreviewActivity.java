package com.oustme.oustsdk.activity.common.feed;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.AlertCommentsActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.activity.common.ShareFeedActivity;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustTagHandlerNew;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedPreviewActivity extends BaseActivity {

    private static final String TAG = "FeedPreviewActivity";
    private DTONewFeed mDTONewFeed;
    private TextView mTextViewTitle, mTextViewDescription, mTextViewDescription2, mTextViewDate;
    private ImageView mImageViewShare, mImageViewComment, mImageViewLike, mImageViewFeed;
    private TextView mTextViewCommentCount,mTextViewShareCount, mTextViewLikeCount;
    private int appColor;
    private Drawable d;
    private ActionBar mActionBar;
    private LinearLayout mLinearLayoutComment,mLinearLayoutShare, mLinearLayoutLike;

    @Override
    protected int getContentView() {
        return R.layout.activity_feed_preview;
    }

    @Override
    protected void initView() {
        try{
            OustSdkTools.setLocale(FeedPreviewActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mTextViewTitle = findViewById(R.id.textViewFeedTitle);
        mTextViewDescription = findViewById(R.id.textViewFeedDescription);
        mTextViewDescription2 = findViewById(R.id.textViewFeedDescription2);

        mImageViewComment = findViewById(R.id.imageViewComment);
        mImageViewLike = findViewById(R.id.imageViewLike);
        mImageViewShare = findViewById(R.id.imageViewShare);
        mImageViewFeed = findViewById(R.id.imageViewFeed);

        mTextViewDate = findViewById(R.id.newsrow_date);

        mTextViewCommentCount = findViewById(R.id.newsrow_comment);
        mTextViewLikeCount = findViewById(R.id.newsrow_like);
        mTextViewShareCount = findViewById(R.id.newsrow_share);

        mLinearLayoutComment = findViewById(R.id.newrow_commentlayout);
        mLinearLayoutLike = findViewById(R.id.newrow_likelayout);
        mLinearLayoutShare = findViewById(R.id.newrow_shareLayout);
    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        if(mActionBar!=null)
        {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            if(mDTONewFeed.getHeader()!=null && !mDTONewFeed.getHeader().isEmpty())
            {
                mActionBar.setTitle(mDTONewFeed.getHeader());
            }
            else {
                mActionBar.setTitle("Feed");
            }
            if(appColor!=0)
            {
                mActionBar.setBackgroundDrawable(new ColorDrawable(appColor));
            }
        }
    }

    boolean isLikeable, isCommentable, isSharable;
    @Override
    protected void initData() {
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        appColor = OustSdkTools.getColorBack(R.color.lgreen);
        if (toolbarColorCode != null && toolbarColorCode.length() > 0) {
            appColor = Color.parseColor(toolbarColorCode);
        }
        d = getResources().getDrawable(R.drawable.orange_roundedcorner_background);
        d.setColorFilter(appColor, PorterDuff.Mode.SRC_ATOP);
        mDTONewFeed = getIntent().getParcelableExtra("FEED_DATA");
        isCommentable = getIntent().getBooleanExtra("ISCOMMENT", true);
        isSharable = getIntent().getBooleanExtra("ISSHARE", false);
        isLikeable = getIntent().getBooleanExtra("ISLIKE", true);
        if(mDTONewFeed !=null)
        {
            if(mDTONewFeed.getHeader()!=null && !mDTONewFeed.getHeader().isEmpty())
            {
                mTextViewTitle.setText(mDTONewFeed.getHeader());
            }
            mTextViewDescription.setLinkTextColor(OustSdkTools.getColorBack(R.color.Blue));
            if(mDTONewFeed.getContent()!=null  && !hasHTMLTags(mDTONewFeed.getContent()))
            {
                if (mDTONewFeed.getBtntext() != null && !mDTONewFeed.getBtntext().isEmpty()) {
                    if (mDTONewFeed.getContent() != null) {
                        mTextViewDescription.setText(Html.fromHtml(mDTONewFeed.getContent() + " <a href=\"" + mDTONewFeed.getLink() + "/\">" + mDTONewFeed.getBtntext() + "</a>", null, new OustTagHandlerNew()));
                    } else {
                        mTextViewDescription.setText(Html.fromHtml(" <a href=\"" + mDTONewFeed.getLink() + "/\">" + mDTONewFeed.getBtntext() + "</a>", null, new OustTagHandlerNew()));
                    }
                    mTextViewDescription.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            if (!OustSdkTools.checkInternetStatus()) {
                                return;
                            }
                            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                                mUrl = "http://" + mUrl;
                            }
                            //feedClicked(feeds.get(position).getFeedId(), feeds.get(position).getCplId());
                            onnewsfeedClick(mUrl);
                        }
                    });
                } else {
                    if (mDTONewFeed.getContent() != null) {
                        mTextViewDescription.setText(Html.fromHtml(mDTONewFeed.getContent(), null, new OustTagHandlerNew()));
                    }
                    mTextViewDescription.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            if (!OustSdkTools.checkInternetStatus()) {
                                return;
                            }
                            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                                mUrl = "http://" + mUrl;
                            }
                            //feedClicked(feeds.get(position).getFeedId(), feeds.get(position).getCplId());
                            onnewsfeedClick(mUrl);
                        }
                    });
                }
                if (mDTONewFeed.getContent() != null && !mDTONewFeed.getContent().isEmpty()) {
                    mTextViewDescription.setLinkTextColor(OustSdkTools.getColorBack(R.color.Blue));
                    mTextViewDescription.setText(Html.fromHtml(mDTONewFeed.getContent(), null, new OustTagHandlerNew()));
                    mTextViewDescription.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            if (!OustSdkTools.checkInternetStatus()) {
                                return;
                            }
                            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                                mUrl = "http://" + mUrl;
                            }
                            //feedClicked(feeds.get(position).getFeedId(), feeds.get(position).getCplId());
                            onnewsfeedClick(mUrl);
                        }
                    });
                }
                mTextViewDescription.setVisibility(View.VISIBLE);
                mTextViewDescription2.setVisibility(View.GONE);
            }
            else{
                if (mDTONewFeed.getBtntext() != null && !mDTONewFeed.getBtntext().isEmpty())
                {
                    if (mDTONewFeed.getContent() != null) {
                        mTextViewDescription2.setText(Html.fromHtml(mDTONewFeed.getContent() + " <a href=\"" + mDTONewFeed.getLink() + "/\">" + mDTONewFeed.getBtntext() + "</a>", null, new OustTagHandlerNew()));
                    } else {
                        mTextViewDescription2.setText(Html.fromHtml(" <a href=\"" + mDTONewFeed.getLink() + "/\">" + mDTONewFeed.getBtntext() + "</a>", null, new OustTagHandlerNew()));
                    }
                    mTextViewDescription2.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            if (!OustSdkTools.checkInternetStatus()) {
                                return;
                            }
                            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                                mUrl = "http://" + mUrl;
                            }
                            //feedClicked(feeds.get(position).getFeedId(), feeds.get(position).getCplId());
                            onnewsfeedClick(mUrl);
                        }
                    });
                } else {
                    if (mDTONewFeed.getContent() != null) {
                        mTextViewDescription2.setText(Html.fromHtml(mDTONewFeed.getContent(), null, new OustTagHandlerNew()));
                    }
                    mTextViewDescription2.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            if (!OustSdkTools.checkInternetStatus()) {
                                return;
                            }
                            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                                mUrl = "http://" + mUrl;
                            }
                            //feedClicked(feeds.get(position).getFeedId(), feeds.get(position).getCplId());
                            onnewsfeedClick(mUrl);
                        }
                    });
                }
                if(mDTONewFeed.getContent()!=null && !mDTONewFeed.getContent().isEmpty()){
                    mTextViewDescription2.setLinkTextColor(OustSdkTools.getColorBack(R.color.Blue));
                    mTextViewDescription2.setText(Html.fromHtml(mDTONewFeed.getContent(), null, new OustTagHandlerNew()));
                    mTextViewDescription2.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            if (!OustSdkTools.checkInternetStatus()) {
                                return;
                            }
                            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                                mUrl = "http://" + mUrl;
                            }
                            //feedClicked(feeds.get(position).getFeedId(), feeds.get(position).getCplId());
                            onnewsfeedClick(mUrl);
                        }
                    });
                }
                mTextViewDescription2.setVisibility(View.VISIBLE);
                mTextViewDescription.setVisibility(View.GONE);
            }
            if(mDTONewFeed.getTimestamp()!=0)
            {
                mTextViewDate.setText(OustSdkTools.getDate(""+mDTONewFeed.getTimestamp()));
            }
            if(mDTONewFeed.getImageUrl()!=null && !mDTONewFeed.getImageUrl().isEmpty())
            {
                Picasso.get().load(mDTONewFeed.getImageUrl()).into(mImageViewFeed);
                mImageViewFeed.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "initData: feedID:"+ mDTONewFeed.getFeedId());
            setLikes();
            setShares();
            setComments();
            CommentListener();
        }
        setUpActionBar();
    }

    private void setShares() {
        if(mDTONewFeed.isSharable()) {
            mLinearLayoutShare.setVisibility(View.VISIBLE);
            if (mDTONewFeed.getNumShares() > 0) {
                mTextViewShareCount.setText("" + mDTONewFeed.getNumShares());
                if (mDTONewFeed.isShared()) {
                    mTextViewShareCount.setTextColor(appColor);
                    mImageViewShare.setColorFilter(appColor);
                    mImageViewShare.setImageResource(R.drawable.ic_shares);
                } else {
                    mTextViewShareCount.setTextColor(OustSdkApplication.getContext().getResources().getColor(R.color.ReviewbarColor));
                    mImageViewShare.setImageResource(R.drawable.ic_share_new);
                }
            } else {
                mImageViewShare.setImageResource(R.drawable.ic_share_new);
            }
        }
    }

    private void setLikes()
    {
        if (mDTONewFeed.getNumLikes() > 0) {
            mTextViewLikeCount.setText("" + mDTONewFeed.getNumLikes());
            if(mDTONewFeed.isLiked()) {
                mTextViewLikeCount.setTextColor(appColor);
                mImageViewLike.setColorFilter(appColor);
                mImageViewLike.setImageResource(R.drawable.ic_likes);
            }else {
                mTextViewLikeCount.setTextColor(OustSdkApplication.getContext().getResources().getColor(R.color.ReviewbarColor));
                mImageViewLike.setImageResource(R.drawable.ic_like);
                mImageViewLike.setColorFilter(OustSdkApplication.getContext().getResources().getColor(R.color.gray_tint));
            }
        } else {
            mImageViewLike.setImageResource(R.drawable.ic_like);
        }
    }
    private void setComments() {
        if (mDTONewFeed.getNumComments() > 0) {
            mTextViewCommentCount.setText("" + mDTONewFeed.getNumComments());
            if(mDTONewFeed.isCommented())
            {
                mTextViewCommentCount.setTextColor(appColor);
                mImageViewComment.setColorFilter(appColor);
                mImageViewComment.setImageResource(R.drawable.ic_comments_new);
            }
            else {
                mTextViewCommentCount.setTextColor(OustSdkApplication.getContext().getResources().getColor(R.color.ReviewbarColor));
                mImageViewComment.setImageResource(R.drawable.ic_comment);
            }
        } else {
            mImageViewComment.setImageResource(R.drawable.ic_comment);
        }


        boolean isOrgCommentable = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE);
        boolean isOrgLikeable = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE);
        boolean isOrgShareable = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE);


        if(isOrgCommentable && isCommentable){
            mLinearLayoutComment.setVisibility(View.VISIBLE);
        }
        else{
            mLinearLayoutComment.setVisibility(View.GONE);
        }

        if(isOrgLikeable && isLikeable){
            mLinearLayoutLike.setVisibility(View.VISIBLE);
        }
        else
        {
            mLinearLayoutLike.setVisibility(View.GONE);
        }

        if(isOrgShareable && isSharable)
        {
            mLinearLayoutShare.setVisibility(View.VISIBLE);
        }
        else
        {
            mLinearLayoutShare.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initListener() {
        mLinearLayoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: share_text");
                if (!OustStaticVariableHandling.getInstance().isShareFeedOpen()) {
                    OustStaticVariableHandling.getInstance().setShareFeedOpen(true);
                    shareListener();
                    openShareDialog(mDTONewFeed);
                }
            }
        });
        mLinearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: like");
                if (!mDTONewFeed.isLiked()) {
                   // mDTONewFeed.setLiked(true);
                    setUserLike(mDTONewFeed.getFeedId(), true);
                } else {
                   // mDTONewFeed.setLiked(false);
                    setUserLike(mDTONewFeed.getFeedId(), false);
                }
            }
        });
        mLinearLayoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: comment");
                Intent intent = new Intent(FeedPreviewActivity.this, AlertCommentsActivity.class);
                intent.putExtra("feedId", mDTONewFeed.getFeedId() + "");
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public abstract class TextViewLinkHandler extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP)
                return super.onTouchEvent(widget, buffer, event);
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                onLinkClick(link[0].getURL());
            }
            return true;
        }

        abstract public void onLinkClick(String url);
    }
    private void openShareDialog(DTONewFeed feed) {
        OustDataHandler.getInstance().setNewFeed(feed);
        Intent intent = new Intent(FeedPreviewActivity.this, ShareFeedActivity.class);
        startActivity(intent);
    }
    private void shareListener()
    {
        ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + mDTONewFeed.getFeedId() + "/" + "isShared";
        DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    if(dataSnapshot.getValue()!=null) {
                        mDTONewFeed.setShared((boolean) dataSnapshot.getValue());
                        setShares();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setUserLike(long feedId, final boolean value) {
        try {
            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isLiked";
            if(value) {
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
            }
            else
            {
                OustFirebaseTools.getRootRef().child(message1).setValue(false);
            }
            OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
            String message = "feeds/feed" + feedId + "/numLikes";
            DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
            firebase.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        if(value) {
                            currentData.setValue((Long) currentData.getValue() + 1);
                        }else if((Long) currentData.getValue()>0) {
                            currentData.setValue((Long) currentData.getValue() - 1);
                        }
                    }
                    if(!mDTONewFeed.isLiked()){
                        mDTONewFeed.setLiked(true);
                        mDTONewFeed.setNumLikes(mDTONewFeed.getNumLikes()+1);
                    }
                    else{
                        mDTONewFeed.setLiked(false);
                        mDTONewFeed.setNumLikes(mDTONewFeed.getNumLikes()-1);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLikes();
                        }
                    });
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
    private void CommentListener()
    {
        String message1 = "/feeds/feed" + mDTONewFeed.getFeedId();
        DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    Log.d(TAG, "onDataChange: commentListener");
                    try {
                        Log.d(TAG, "onDataChange: vdsg:"+dataSnapshot.toString());
                        HashMap<String, Object> newsfeedMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if(newsfeedMap.get("numComments")!=null)
                        {
                            mDTONewFeed.setNumComments((long)newsfeedMap.get("numComments"));
                        }

                        if(newsfeedMap.get("numLikes")!=null)
                        {
                            mDTONewFeed.setNumLikes((long)newsfeedMap.get("numLikes"));
                        }
                        if(newsfeedMap.get("numShares")!=null)
                        {
                            mDTONewFeed.setNumShares((long)newsfeedMap.get("numShares"));
                        }
                        if(newsfeedMap.get("shareable")!=null)
                        {
                            mDTONewFeed.setSharable((boolean)newsfeedMap.get("shareable"));
                        }
                        setComments();
                        setLikes();
                        setShares();
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private Pattern pattern = Pattern.compile(HTML_PATTERN);

    public boolean hasHTMLTags(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    private void onnewsfeedClick(String mUrl) {
        Log.d(TAG, "onnewsfeedClick: ");
        Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
        intent.putExtra("type", "url");
        intent.putExtra("mUrl", mUrl);
        startActivity(intent);
    }
}
