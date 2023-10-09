package com.oustme.oustsdk.activity.common.noticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.extras.ClickState;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.NBPostData;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

/**
 * Created by oust on 2/18/19.
 */

public class NBCreatePostAttachmentsAdapter extends RecyclerView.Adapter<NBCreatePostAttachmentsAdapter.MyViewHolder> {
    private final String TAG = "NBTopicAdapter";
    List<NBPostData.NBPostAttachmentsData> mNBPostAttachment;
    private Context context;
    private boolean isClicked = false;
    private SelectFileToDelete mListener;
    private File ImageFileName;
    private List<Boolean> isAttachmentList;
    private VideoRequestHandler videoRequestHandler;
    Picasso picassoInstance;
    private Uri uri;
    private List<Bitmap> thumNails;

    public NBCreatePostAttachmentsAdapter(Context context, List<NBPostData.NBPostAttachmentsData> mNBPostAttachment, File imageFileName, List<Boolean> isAttachmentList, List<Bitmap> thumnai) {
        this.mNBPostAttachment = mNBPostAttachment;
        this.context = context;
        mListener = (SelectFileToDelete) context;
        this.ImageFileName = imageFileName;
        this.isAttachmentList = isAttachmentList;
        this.thumNails = thumnai;
    }

    @Override
    public int getItemCount() {
        if (mNBPostAttachment == null) {
            return 0;
        }
        return mNBPostAttachment.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView, imageViewClose;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageViewPreview);
            imageViewClose = view.findViewById(R.id.imageViewClose);
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
                color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }
            Drawable d = context.getResources().getDrawable(R.drawable.greenlite_round_textview);
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            //new_post_count.setBackgroundDrawable(d);
        }

    }

    private void clickView(View view, final int position, final ClickState clickState) {
        if (!isClicked) {
            isClicked = true;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
            scaleDownX.setDuration(150);
            scaleDownY.setDuration(150);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    isClicked = false;

                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_attachment_preview, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(NBCreatePostAttachmentsAdapter.MyViewHolder holder, final int position) {
        try {
            NBPostData.NBPostAttachmentsData attachment = mNBPostAttachment.get(position);
            if (attachment != null) {
                if (attachment.getFileType().equalsIgnoreCase("IMAGE") && attachment.getFileName() != null) {
                    holder.imageView.setImageBitmap(thumNails.get(position));
                } else if (attachment.getFileType().equalsIgnoreCase("VIDEO")) {
                    holder.imageView.setImageBitmap(thumNails.get(position));
                } else if (attachment.getFileType().equalsIgnoreCase("AUDIO")) {
                    try {
                        holder.imageView.setImageResource(R.drawable.ic_attachaudio);
                        holder.imageView.setColorFilter(Color.parseColor(OustPreferences.get(TOOL_BAR_COLOR_CODE)));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (attachment.getFileType().equalsIgnoreCase("OTHER")) {
                    try {
                        Drawable drawable = context.getDrawable(R.drawable.paper);
                        drawable.setColorFilter(Color.parseColor(OustPreferences.get(TOOL_BAR_COLOR_CODE)), PorterDuff.Mode.SRC_IN);
                        holder.imageView.setImageDrawable(drawable);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                holder.imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickItem(position);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onClickItem(int position) {
        if (mListener != null) {
            mListener.selectedPosition(position);
        }
    }

    public interface SelectFileToDelete {
        void selectedPosition(int position);
    }

    public class VideoRequestHandler extends RequestHandler {
        public String SCHEME_VIDEO = "video";

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_VIDEO.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            return new Result(bm, Picasso.LoadedFrom.DISK);
        }
    }

}
