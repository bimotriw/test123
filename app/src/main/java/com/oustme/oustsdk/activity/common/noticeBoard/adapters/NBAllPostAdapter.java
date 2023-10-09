package com.oustme.oustsdk.activity.common.noticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBPostDetailsActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBPostClickCallBack;
import com.oustme.oustsdk.activity.common.noticeBoard.extras.ClickState;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import io.reactivex.annotations.NonNull;

/**
 * Created by oust on 2/21/19.
 */

public class NBAllPostAdapter extends RecyclerView.Adapter<NBAllPostAdapter.MyViewHolder> {
    private final String TAG = "NBAllPostAdapterextends";
    private List<NBPostData> nbPostDataList;
    private Context context;
    private boolean isClicked = false;
    private int toolbarColor;
    ActiveUser activeUser;
    private GradientDrawable ld;
    private NBPostClickCallBack nBPostClickCallBack;
    private int visiblePos = 0;
    private LinearLayout share_uri_ll;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));

    public NBAllPostAdapter(Context context, List<NBPostData> nbPostDataList, NBPostClickCallBack nBPostClickCallBack) {
        try {
            this.nbPostDataList = nbPostDataList;
            this.context = context;

            this.nBPostClickCallBack = nBPostClickCallBack;
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                this.toolbarColor = Color.parseColor(toolbarColorCode);
            } else {
                this.toolbarColor = context.getResources().getColor(R.color.lgreen);
            }
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
            this.ld = (GradientDrawable) context.getResources().getDrawable(R.drawable.greenlite_round_textview);
            this.ld.setColorFilter(toolbarColor, mode);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void notifyListChnage(List<NBPostData> nbPostDataList) {
        Log.d(TAG, "notifyListChnage: " + nbPostDataList.size());
        try {
            this.nbPostDataList.clear();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        this.nbPostDataList = nbPostDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return nbPostDataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView post_img, author_img, post_attachment_img, imgLike, imgComment, imgShare, mImageViewAttachmentDownLoadIcon,
                commenter_img, send_attachment, send_audio, send_imgview, deleteImg;
        private LinearLayout main_post_ll, post_attchment_ll, like_ll, nb_coin_layout, comment_ll, share_ll, first_comment_ll, post_audio_ll, play_video_btn;
        private TextView authorNameTv, posted_on_tv, authorDesignationTv, post_title, post_desciption,
                attachment_name, attachment_size, like_count, comment_count, share_count, commenter_name, commented_on,
                first_comment, readmore_btn, read_more_post_btn, nb_coins_text;
        private View attachment_ul, attachment_ul_2;
        private EditText comments_et;
        private RelativeLayout send_ll, video_parent_ll;
        private CardView post_img_ll;
        private StyledPlayerView simpleExoPlayerView;
        private File file;
        private String attachmentName, attachmentLocalPath;


        MyViewHolder(View view) {
            super(view);
            post_img = view.findViewById(R.id.post_img);
            main_post_ll = view.findViewById(R.id.main_post_ll);
            author_img = view.findViewById(R.id.author_img);
            post_attachment_img = view.findViewById(R.id.post_attachment_img);
            imgLike = view.findViewById(R.id.imgLike);
            imgComment = view.findViewById(R.id.imgComment);
            imgShare = view.findViewById(R.id.imgShare);
            commenter_img = view.findViewById(R.id.commenter_img);
            send_attachment = view.findViewById(R.id.send_attachment);
            send_audio = view.findViewById(R.id.send_audio);
            send_imgview = view.findViewById(R.id.send_imgview);
            deleteImg = view.findViewById(R.id.deleteImg);
            post_img_ll = view.findViewById(R.id.post_img_ll);
            post_attchment_ll = view.findViewById(R.id.post_attchment_ll);
            nb_coins_text = view.findViewById(R.id.nb_coins_text);
            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    nb_coins_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
                } else {
                    nb_coins_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            nb_coin_layout = view.findViewById(R.id.nb_coin_layout);
            like_ll = view.findViewById(R.id.like_ll);
            comment_ll = view.findViewById(R.id.comment_ll);
            share_ll = view.findViewById(R.id.share_ll);
            first_comment_ll = view.findViewById(R.id.first_comment_ll);
            post_audio_ll = view.findViewById(R.id.post_audio_ll);
            play_video_btn = view.findViewById(R.id.play_video_btn);

            authorNameTv = view.findViewById(R.id.authorNameTv);
            posted_on_tv = view.findViewById(R.id.posted_on_tv);
            authorDesignationTv = view.findViewById(R.id.authorDesignationTv);
            post_title = view.findViewById(R.id.post_title);
            post_desciption = view.findViewById(R.id.post_desciption);
            attachment_name = view.findViewById(R.id.attachment_name);
            attachment_size = view.findViewById(R.id.attachment_size);
            like_count = view.findViewById(R.id.like_count);
            comment_count = view.findViewById(R.id.comment_count);
            share_count = view.findViewById(R.id.share_count);
            commenter_name = view.findViewById(R.id.commenter_name);
            commented_on = view.findViewById(R.id.commented_on);
            first_comment = view.findViewById(R.id.first_comment);
            readmore_btn = view.findViewById(R.id.readmore_btn);
            read_more_post_btn = view.findViewById(R.id.read_more_post_btn);

            attachment_ul = view.findViewById(R.id.attachment_ul);
            attachment_ul_2 = view.findViewById(R.id.attachment_ul_2);
            comments_et = view.findViewById(R.id.comments_et);
            send_ll = view.findViewById(R.id.send_ll);
            video_parent_ll = view.findViewById(R.id.video_parent_ll);
            simpleExoPlayerView = view.findViewById(R.id.player_view);
            mImageViewAttachmentDownLoadIcon = view.findViewById(R.id.attachment_download_icon);
        }

    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_post_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
//            final NBPostData nbPostData = nbPostDataList.get(position);
            if (nbPostDataList.get(position).hasBannerBg()) {
                Picasso.get().load(nbPostDataList.get(position).getBannerBg()).into(holder.post_img);
            }

            OustSdkTools.setImage(holder.send_imgview, context.getResources().getString(R.string.send));
            holder.read_more_post_btn.setTextColor(toolbarColor);
            holder.readmore_btn.setTextColor(toolbarColor);
            holder.comments_et.setText("");

            holder.send_ll.setBackgroundDrawable(ld);

            if (nbPostDataList.get(position).hasAudio()) {
                holder.post_audio_ll.setVisibility(View.VISIBLE);
            } else {
                holder.post_audio_ll.setVisibility(View.GONE);
            }

            if (nbPostDataList.get(position).hasVideo()) {
                //holder.simpleExoPlayerView.setVisibility(View.VISIBLE);
                holder.play_video_btn.setVisibility(View.VISIBLE);
            } else {
                holder.simpleExoPlayerView.setVisibility(View.GONE);
                holder.play_video_btn.setVisibility(View.GONE);
            }
            activeUser = OustAppState.getInstance().getActiveUser();
            if (nbPostDataList.get(position).getNbPostRewardOC() != 0) {

                if (nbPostDataList.get(position).getCreatedBy() != null && activeUser != null) {

                    if (!nbPostDataList.get(position).getCreatedBy().isEmpty() && activeUser.getStudentid().equalsIgnoreCase(nbPostDataList.get(position).getCreatedBy())) {

                        holder.nb_coin_layout.setVisibility(View.VISIBLE);
                        holder.nb_coins_text.setText(String.valueOf(nbPostDataList.get(position).getNbPostRewardOC()));

                    }
                }

            }

            if (!nbPostDataList.get(position).hasAttachment()) {
                holder.post_attchment_ll.setVisibility(View.GONE);
            } else {
                holder.post_attchment_ll.setVisibility(View.VISIBLE);
                holder.attachment_ul.setVisibility(View.GONE);
                if (nbPostDataList.get(position).getAttachmentsData() != null) {
                    if (nbPostDataList.get(position).getAttachmentsData().get(0).getFileName() != null) {
                        holder.attachmentName = nbPostDataList.get(position).getAttachmentsData().get(0).getFileName();
                    }
                    holder.attachmentLocalPath = AppConstants.StringConstants.LOCAL_FILE_STORAGE_PATH;
                    holder.file = new File(holder.attachmentLocalPath, getMediaFileName(holder.attachmentName));
                    if (nbPostDataList.get(position).getAttachmentsData().get(0).getFileName() != null)
                        holder.attachment_name.setText(getMediaFileName(holder.attachmentName));
                    if (nbPostDataList.get(position).getAttachmentsData().get(0).getSizeInString() != null) {
                        holder.attachment_size.setText(nbPostDataList.get(position).getAttachmentsData().get(0).getSizeInString());
                    }
                    if (holder.file.exists()) {
                        holder.mImageViewAttachmentDownLoadIcon.setColorFilter(toolbarColor);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.mImageViewAttachmentDownLoadIcon.setColorFilter(context.getColor(R.color.whitea));
                        } else {
                            holder.mImageViewAttachmentDownLoadIcon.setColorFilter(Color.parseColor("#ffffff"));
                        }
                        Log.d(TAG, "onBindViewHolder: attachment not yet downloaded:");
                    }
                }
            }

            setPostImgTintColor(nbPostDataList.get(position).hasLiked(), holder.imgLike, holder.like_count, R.drawable.ic_like, R.drawable.ic_likes);
            setPostImgTintColor(nbPostDataList.get(position).hasCommented(), holder.imgComment, holder.comment_count, R.drawable.ic_comment, R.drawable.ic_comments_new);
            setPostImgTintColor(nbPostDataList.get(position).hasShared(), holder.imgShare, holder.share_count, R.drawable.ic_share_new, R.drawable.ic_shares);

            if (nbPostDataList.get(position).getTitle() != null) {
                holder.post_title.setText(nbPostDataList.get(position).getTitle());
            }
            if (nbPostDataList.get(position).getDescription() != null) {
                holder.post_desciption.setText(nbPostDataList.get(position).getDescription());
            }
            holder.posted_on_tv.setText(OustSdkTools.getDate("" + nbPostDataList.get(position).getCreatedOn()));

            setSocialActivityCount(holder.like_count, nbPostDataList.get(position).getLikeCount());
            setSocialActivityCount(holder.comment_count, nbPostDataList.get(position).getCommentedCount());
            setSocialActivityCount(holder.share_count, nbPostDataList.get(position).getSharedCount());

            if (nbPostDataList.get(position).getNbCommentDataList() == null || nbPostDataList.get(position).getNbCommentDataList().size() == 0) {
                holder.first_comment_ll.setVisibility(View.GONE);
                holder.attachment_ul_2.setVisibility(View.GONE);
            } else {
                NBCommentData nbCommentData = nbPostDataList.get(position).getNbCommentDataList().get(0);
                holder.first_comment_ll.setVisibility(View.VISIBLE);
//                if(nbCommentData.getAvatar()!=null)
//                {
//                    Picasso.get().load(nbCommentData.getAvatar()).error(R.drawable.g).into(holder.commenter_img);
//                }
                if (nbCommentData.hasAvatar()) {
                    Picasso.get().load(nbCommentData.getAvatar())
                            .placeholder(bd_loading).error(bd).into(holder.commenter_img);
                } else {
                    Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbCommentData.getAvatar())
                            .placeholder(bd_loading).error(bd).into(holder.commenter_img);
                }
//                if(nbCommentData.getId()==0){
//                    holder.deleteImg.setVisibility(View.VISIBLE);
//                }else{
//                    holder.deleteImg.setVisibility(View.GONE);
//                }


                holder.commenter_name.setText(nbCommentData.getCommentedBy());
                holder.commented_on.setText(OustSdkTools.getDate(nbCommentData.getCommentedOn() + ""));
                holder.first_comment.setText("" + nbCommentData.getComment());
                if (nbCommentData.getNbReplyData() != null && nbCommentData.getNbReplyData().size() == 1) {
                    holder.readmore_btn.setText("" + nbCommentData.getNbReplyData().size() + " " + context.getResources().getString(R.string.reply));
                } else if (nbCommentData.getNbReplyData() != null && nbCommentData.getNbReplyData().size() > 1) {
                    holder.readmore_btn.setText("" + nbCommentData.getNbReplyData().size() + " " + context.getResources().getString(R.string.replies));
                } else {
                    holder.readmore_btn.setText(context.getResources().getString(R.string.see_all));
                }
            }

            holder.like_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, position, "", ClickState.LIKE);
                }
            });
            holder.share_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share_uri_ll = holder.main_post_ll;
                    clickView(v, position, "", ClickState.SHARE);
                }
            });
            holder.send_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = holder.comments_et.getText().toString();
                    if (!message.trim().isEmpty()) {
                        holder.comments_et.setText("");
                        clickView(v, position, message.trim(), ClickState.SEND_COMMENT);
                        hideKeyboard(holder.comments_et);
                    } else {
                        OustSdkTools.showToast(context.getResources().getString(R.string.type_comment_post));
                        //holder.comments_et.setError(context.getResources().getString(R.string.type_comment_post));
                    }
                }
            });
            holder.readmore_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, position, "", ClickState.OPEN_DETAILS);
                }
            });
            holder.read_more_post_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, position, "", ClickState.OPEN_DETAILS);
                }
            });

            holder.play_video_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, position, "", ClickState.PLAY_VIDEO);
                }
            });

            holder.post_audio_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, position, "", ClickState.PLAY_AUDIO);
                }
            });

            holder.post_img_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nbPostDataList.get(position).getVideo() != null && !nbPostDataList.get(position).getVideo().isEmpty()) {
                        clickView(v, position, "", ClickState.PLAY_VIDEO);
                    } else {
                        clickView(v, position, "", ClickState.OPEN_DETAILS);
                    }
                }
            });
            holder.post_attchment_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.file != null) {
                        if (holder.file.exists()) {
                            openFile(holder.file);
                        } else {
                            clickView(view, position, "downloadAttachment", ClickState.OPEN_DETAILS);
                        }
                    }
                }
            });

            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickView(v, position, "", ClickState.DELETE_COMMENT);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public static String getMediaFileName(String mediaPath) {
        String fileName = null;
        try {
            String[] mediaStrs = mediaPath.split("/");
            fileName = mediaStrs[mediaStrs.length - 1];
        } catch (Exception e) {

        }
        return fileName;
    }

    private void setPostImgTintColor(boolean change, ImageView img, TextView tv, int ic_no_select, int ic_select) {
        if (change) {
            if (ic_select != 0)
                img.setImageResource(ic_select);
            img.setColorFilter(toolbarColor);
            tv.setTextColor(toolbarColor);
        } else {
            if (ic_no_select != 0)
                img.setImageResource(ic_no_select);
            img.setColorFilter(OustSdkApplication.getContext().getResources().getColor(R.color.common_grey));
            tv.setTextColor(OustSdkApplication.getContext().getResources().getColor(R.color.common_grey));
        }
    }

    private void setSocialActivityCount(TextView tv, long count) {
        if (count == 0) {
            tv.setText("");
        } else {
            tv.setText("" + count);
        }
    }


    private void clickView(View view, final int position, final String message, final ClickState clickState) {
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
                    if (clickState == ClickState.LIKE) {
                        performLike(position);
                    } else if (clickState == ClickState.SEND_COMMENT) {
                        performComment(position, message);
                    } else if (clickState == ClickState.SHARE) {
                        performShare(position);
                    } else if (clickState == ClickState.OPEN_DETAILS) {
                        boolean storagePermission = OustSdkTools.checkPermission(view.getContext());
                        if (storagePermission) {
                            openPostPage(position, message);
                        }
                    } else if (clickState == ClickState.PLAY_VIDEO) {
                        boolean storagePermission = OustSdkTools.checkPermission(view.getContext());
                        if (storagePermission) {
                            openVideoPostPage(position);
                        }
                    } else if (clickState == ClickState.PLAY_AUDIO) {
                        boolean storagePermission = OustSdkTools.checkPermission(view.getContext());
                        if (storagePermission) {
                            openAudioPostPage(position);
                        }
                    } else if (clickState == ClickState.DELETE_COMMENT) {
                        performCommentDelete(position);
                    }
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

    private void performCommentDelete(int position) {
        NBPostData nbPostData = nbPostDataList.get(position);
        PostViewData postViewData = new PostViewData(nbPostData.getNbId(), nbPostData.getId());
        postViewData.setCommentDeleteType();
        postViewData.setNbCommentData(nbPostData.getNbCommentDataList().get(0));
        nBPostClickCallBack.onPostCommentDelete(postViewData);
    }

    private void openAudioPostPage(int position) {
        Intent intent = new Intent(context, NBPostDetailsActivity.class);
        intent.putExtra("postId", nbPostDataList.get(position).getId());
        intent.putExtra("autoPlayAudio", true);
        context.startActivity(intent);
    }

    private void openVideoPostPage(int position) {
        Intent intent = new Intent(context, NBPostDetailsActivity.class);
        intent.putExtra("postId", nbPostDataList.get(position).getId());
        intent.putExtra("autoPlayVideo", true);
        context.startActivity(intent);
    }

    private void openPostPage(int position, String message) {
        try {
            Intent intent = new Intent(context, NBPostDetailsActivity.class);
            intent.putExtra("postId", nbPostDataList.get(position).getId());
            if (message.equalsIgnoreCase("downloadAttachment")) {
                intent.putExtra("downloadAttachment", true);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void performShare(int position) {
        try {
            NBPostData nbPostData = nbPostDataList.get(position);
            nbPostData.setHasShared(true);
            nbPostData.incrementShareCount();
            notifyDataSetChanged();
            PostViewData postViewData = new PostViewData(nbPostData.getNbId(), nbPostData.getId());
            postViewData.setShareType();
            nBPostClickCallBack.onPostShare(postViewData, share_uri_ll);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void performComment(int position, String message) {
        try {
            NBPostData nbPostData = nbPostDataList.get(position);

            NBCommentData nbCommentData = new NBCommentData();
            nbCommentData.setComment(message).
                    setAvatar(OustAppState.getInstance().getActiveUser().getAvatar())
                    .setUserKey(OustAppState.getInstance().getActiveUser().getStudentKey())
                    .setPostId(nbPostData.getId())
                    .setCommentedOn(0)
                    .setCommentedBy(OustAppState.getInstance().getActiveUser().getUserDisplayName());
            nbPostData.setHasCommented(true);
            nbPostData.incrementCommentCount();
            nbPostData.addOfflineComment(nbCommentData);

            notifyDataSetChanged();

            PostViewData postViewData = new PostViewData(nbPostData.getNbId(), nbPostData.getId());
            postViewData.setCommentType().setNbCommentData(nbCommentData);
            nBPostClickCallBack.onPostComment(postViewData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openFile(File url) {
        try {
            Uri uri = Uri.fromFile(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".csv")) {
                intent.setDataAndType(uri, "text/csv");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //nbPostDetailsView.openAttachment(intent);
        } catch (ActivityNotFoundException e) {
            // nbPostDetailsView.onErrorWhileOpeningFile();
            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideKeyboard(EditText comments_et) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(comments_et.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    private void performLike(int position) {
        try {
            NBPostData nbPostData = nbPostDataList.get(position);
            if (!nbPostData.hasLiked()) {
                nbPostData.setHasLiked(true);
                nbPostData.incrementLikeCount();
            } else {
                nbPostData.setHasLiked(false);
                nbPostData.decrementLikeCount();
            }
            notifyDataSetChanged();
            PostViewData postViewData = new PostViewData(nbPostData.getNbId(), nbPostData.getId(), System.currentTimeMillis());
            postViewData.setLikeType().setLike(nbPostData.hasLiked());
            nBPostClickCallBack.onPostLike(postViewData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onNBPostViewedInScroll(int position) {
        visiblePos = position;
        if (nBPostClickCallBack != null && nbPostDataList != null && nbPostDataList.get(position) != null) {
            NBPostData nbPostData = nbPostDataList.get(position);
            nBPostClickCallBack.onPostViewed(new PostViewData(nbPostData.getNbId(), nbPostData.getId(), System.currentTimeMillis()));
        }
    }
}
