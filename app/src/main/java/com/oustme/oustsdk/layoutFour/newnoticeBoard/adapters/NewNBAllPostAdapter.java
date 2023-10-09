package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.extras.ClickState;
import com.oustme.oustsdk.layoutFour.navigationFragments.NewNoticeBoardFragment;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBPostDetailsActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBTopicDetailActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBPostClickCallBack;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifImageView;

public class NewNBAllPostAdapter extends RecyclerView.Adapter<NewNBAllPostAdapter.MyViewHolder> implements Filterable {
    private final String TAG = "NewNBAllPostAdapter";
    private List<NewNBPostData> nbPostDataList;
    private List<NewNBPostData> tempList;
    private Context context;
    private boolean isClicked = false;
    private int toolbarColor;
    ActiveUser activeUser;
    private GradientDrawable ld;
    private NewNBPostClickCallBack nBPostClickCallBack;
    private LinearLayout share_uri_ll;
    PopupWindow zoomImagePopup;
    private Filter fRecords;
    List<NewNBPostData> tempListData = new ArrayList<>();
    View parent;
    NewNoticeBoardFragment newNoticeBoardFragment;

    public NewNBAllPostAdapter(Context context, List<NewNBPostData> nbPostDataList, NewNBPostClickCallBack nBPostClickCallBack) {
        try {
            this.nbPostDataList = nbPostDataList;
            this.tempList = nbPostDataList;
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

    public NewNBAllPostAdapter(Context context, List<NewNBPostData> nbPostDataList, NewNoticeBoardFragment newNoticeBoardFragment) {
        try {
            this.nbPostDataList = nbPostDataList;
            this.tempList = nbPostDataList;
            this.context = context;

            this.nBPostClickCallBack = newNoticeBoardFragment;
            this.newNoticeBoardFragment = newNoticeBoardFragment;
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

    public void notifyListChnage(List<NewNBPostData> nbPostDataList) {
        try {
            this.nbPostDataList.clear();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        this.tempList = nbPostDataList;
        this.nbPostDataList = nbPostDataList;
        notifyDataSetChanged();
    }

    public void notifyChnage(List<NewNBPostData> nbPostDataList) {
        try {
            tempListData.addAll(nbPostDataList);
            this.nbPostDataList = tempListData;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return nbPostDataList == null ? 0 : nbPostDataList.size();
    }

    @Override
    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView post_img, author_img, post_attachment_img, imgLike, imgComment, imgShare, mImageViewAttachmentDownLoadIcon,
                commenter_img, send_attachment, send_audio, send_imgview, deleteImg;
        LinearLayout main_post_ll, post_attchment_ll, like_ll, nb_coin_layout, comment_ll, share_ll, first_comment_ll, post_audio_ll, play_video_btn, comment_layout;
        TextView authorNameTv, posted_on_tv, authorDesignationTv, post_title, post_desciption,
                attachment_name, attachment_size, like_count, comment_count, share_count, commenter_name,
                first_comment, readmore_btn, read_more_post_btn, nb_coins_text, designation, userRole;
        View attachment_ul, attachment_ul_2;
        private final EditText comments_et;
        private final RelativeLayout send_ll;
        private final CardView post_img_ll;
        private final SimpleExoPlayer player;
        private final StyledPlayerView simpleExoPlayerView;
        private File file;
        private String attachmentName, attachmentLocalPath;
        LinearLayout parent_layout, content_layout;
        RelativeLayout comment_box;

        MyViewHolder(View view) {
            super(view);
            parent = view;
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
            content_layout = view.findViewById(R.id.content_layout);
            parent_layout = view.findViewById(R.id.parent_layout);
            comment_box = view.findViewById(R.id.comment_box);
            designation = view.findViewById(R.id.designation);
            userRole = view.findViewById(R.id.userRole);
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
            first_comment = view.findViewById(R.id.first_comment);
            readmore_btn = view.findViewById(R.id.readmore_btn);
            read_more_post_btn = view.findViewById(R.id.read_more_post_btn);

            attachment_ul = view.findViewById(R.id.attachment_ul);
            attachment_ul_2 = view.findViewById(R.id.attachment_ul_2);
            comments_et = view.findViewById(R.id.comments_et);
            send_ll = view.findViewById(R.id.send_ll);
            simpleExoPlayerView = view.findViewById(R.id.player_view);
            mImageViewAttachmentDownLoadIcon = view.findViewById(R.id.attachment_download_icon);
            comment_layout = view.findViewById(R.id.comment_layout);

            player = new SimpleExoPlayer.Builder(OustSdkApplication.getContext()).build();
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            simpleExoPlayerView.setShowNextButton(false);
            simpleExoPlayerView.setShowFastForwardButton(false);
            simpleExoPlayerView.setShowPreviousButton(false);
            simpleExoPlayerView.setShowRewindButton(false);
            simpleExoPlayerView.setPlayer(player);
        }

        public void startPlayer() {
            try {
                player.setPlayWhenReady(false);
                player.getPlaybackState();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        public void pausePlayer() {
            try {
                player.setPlayWhenReady(false);
                player.getPlaybackState();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (nbPostDataList != null && (holder.getAdapterPosition() != -1)) {
            if (nbPostDataList.get(holder.getAdapterPosition()).hasVideo()) {
                holder.startPlayer();
                holder.setIsRecyclable(false);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (nbPostDataList != null && (holder.getAdapterPosition() != -1)) {
            if (nbPostDataList.get(holder.getAdapterPosition()).hasVideo()) {
                holder.setIsRecyclable(true);
                holder.pausePlayer();
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_post_item_2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_COMMENT)) {
                holder.comment_ll.setVisibility(View.VISIBLE);
            } else {
                holder.nb_coin_layout.setVisibility(View.GONE);
                holder.comment_ll.setVisibility(View.GONE);
            }
            if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_LIKE)) {
                holder.like_ll.setVisibility(View.GONE);
            } else {
                holder.like_ll.setVisibility(View.VISIBLE);
            }
            if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_SHARE)) {
                holder.share_ll.setVisibility(View.GONE);
            } else {
                holder.share_ll.setVisibility(View.VISIBLE);
            }

            if (nbPostDataList.get(position).getTitle() != null) {
                holder.main_post_ll.setVisibility(View.VISIBLE);
                if (nbPostDataList.get(position).hasBannerBg()) {
                    holder.post_img_ll.setVisibility(View.VISIBLE);
                    Picasso.get().load(nbPostDataList.get(position).getBannerBg()).into(holder.post_img);
                }
                holder.read_more_post_btn.setTextColor(toolbarColor);
                holder.readmore_btn.setTextColor(toolbarColor);
                holder.comments_et.setText("");
                try {
                    holder.authorNameTv.setText(WordUtils.capitalize(nbPostDataList.get(position).getCreatedBy()));
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
//            holder.send_ll.setBackgroundDrawable(ld);

                if (nbPostDataList.get(position).hasAudio()) {
                    holder.post_audio_ll.setVisibility(View.VISIBLE);
                } else {
                    holder.post_audio_ll.setVisibility(View.GONE);
                }
                ViewGroup.LayoutParams params = holder.post_img_ll.getLayoutParams();
                if (nbPostDataList.get(position).hasVideo()) {
                    holder.simpleExoPlayerView.setVisibility(View.VISIBLE);
                    holder.play_video_btn.setVisibility(View.GONE);
                    holder.post_img.setVisibility(View.GONE);
                    params.height = 400;
                    holder.post_img_ll.setLayoutParams(params);
                    Log.e(TAG, "onBindViewHolder: position->  " + position + "nbPostDataList video path--> " + nbPostDataList.get(position).getVideo());
                    playVideo(nbPostDataList.get(position).getVideo(), holder.player);
                } else {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.simpleExoPlayerView.setVisibility(View.GONE);
                    holder.play_video_btn.setVisibility(View.GONE);
                    holder.post_img.setVisibility(View.VISIBLE);
                }
                holder.post_img_ll.setLayoutParams(params);

                activeUser = OustAppState.getInstance().getActiveUser();

                setPostImgTintColor(nbPostDataList.get(position).hasLiked(), holder.imgLike, holder.like_count, R.drawable.ic_like, R.drawable.ic_likes);
                setPostImgTintColor(nbPostDataList.get(position).hasCommented(), holder.imgComment, holder.comment_count, R.drawable.ic_comment, R.drawable.ic_comments_new);
                setPostImgTintColor(nbPostDataList.get(position).hasShared(), holder.imgShare, holder.share_count, R.drawable.ic_share_new, R.drawable.ic_shares);

                if (nbPostDataList.get(position).getTitle() != null) {
                    holder.post_title.setText(nbPostDataList.get(position).getTitle());
                }
                if (nbPostDataList.get(position).getDescription() != null) {
                    holder.post_desciption.setText(nbPostDataList.get(position).getDescription());
                }

                if (nbPostDataList.get(position).getDesignation() != null) {
                    holder.designation.setText(nbPostDataList.get(position).getDesignation() + " | ");
                }

                if (nbPostDataList.get(position).getUserRole() != null) {
                    holder.userRole.setText(nbPostDataList.get(position).getUserRole());
                }

                if (nbPostDataList.get(position).getAvatar() != null) {
                    Picasso.get().load(nbPostDataList.get(position).getAvatar())
                            .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(holder.author_img);
                } else {
                    if (nbPostDataList.get(position).getNbCommentDataList() != null) {
                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbPostDataList.get(position).getNbCommentDataList().get(0).getAvatar())
                                .placeholder(R.drawable.ic_person_profile_image_nb).error(R.drawable.ic_person_profile_image_nb).into(holder.author_img);
                    }
                }

                holder.posted_on_tv.setText(OustSdkTools.getDate("" + nbPostDataList.get(position).getCreatedOn()));

                setSocialActivityCount(holder.like_count, nbPostDataList.get(position).getLikeCount());
                setSocialActivityCount(holder.comment_count, nbPostDataList.get(position).getCommentedCount());
                setSocialActivityCount(holder.share_count, nbPostDataList.get(position).getSharedCount());

                if (nbPostDataList.get(position).getNbCommentDataList() == null || nbPostDataList.get(position).getNbCommentDataList().size() == 0) {
                    holder.first_comment_ll.setVisibility(View.GONE);
                    holder.attachment_ul_2.setVisibility(View.GONE);
                } else {
                    holder.first_comment_ll.setVisibility(View.VISIBLE);
                    holder.commenter_name.setText(nbPostDataList.get(position).getNbCommentDataList().get(0).getCommentedBy());
                    holder.deleteImg.setOnClickListener(v -> clickView(v, position, "", ClickState.DELETE_COMMENT));
                }

                if (nbPostDataList.get(position).hasAttachment()) {
                    holder.post_attchment_ll.setVisibility(View.VISIBLE);
                    holder.attachment_ul.setVisibility(View.GONE);
                    if (nbPostDataList.get(position).getAttachmentsData() != null) {
                        if (nbPostDataList.get(position).getAttachmentsData().size() > 0) {
                            if (nbPostDataList.get(position).getAttachmentsData().get(0).getFileName() != null) {
                                holder.attachmentName = nbPostDataList.get(position).getAttachmentsData().get(0).getFileName();
                                holder.attachmentLocalPath = AppConstants.StringConstants.LOCAL_FILE_STORAGE_PATH;
                                try {
                                    holder.file = new File(holder.attachmentLocalPath, getMediaFileName(holder.attachmentName));
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
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
                    }
                } else {
                    holder.post_attchment_ll.setVisibility(View.GONE);
                }

                holder.comment_ll.setOnClickListener(v -> {
                    holder.comment_box.setVisibility(View.VISIBLE);
                    holder.comments_et.requestFocus();
                });

                holder.like_ll.setOnClickListener(v -> clickView(v, position, "", ClickState.LIKE));
                holder.share_ll.setOnClickListener(v -> {
                    share_uri_ll = holder.main_post_ll;
                    clickView(v, position, "", ClickState.SHARE);
                });

                holder.comments_et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() != 0) {
                            holder.send_imgview.setColorFilter(toolbarColor);
                        } else {
                            holder.send_imgview.setColorFilter(OustSdkApplication.getContext().getResources().getColor(R.color.common_grey));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                holder.send_ll.setOnClickListener(v -> {
                    String message = holder.comments_et.getText().toString();
                    if (!message.trim().isEmpty()) {
                        holder.comments_et.setText("");
                        clickView(v, position, message.trim(), ClickState.SEND_COMMENT);
                        hideKeyboard(holder.comments_et);
                    } else {
                        OustSdkTools.showToast(context.getResources().getString(R.string.type_comment_post));
                        //holder.comments_et.setError(context.getResources().getString(R.string.type_comment_post));
                    }
                });
                holder.readmore_btn.setOnClickListener(v -> clickView(v, position, "", ClickState.OPEN_DETAILS));
                holder.read_more_post_btn.setOnClickListener(v -> clickView(v, position, "", ClickState.OPEN_DETAILS));

                holder.play_video_btn.setOnClickListener(v -> clickView(v, position, "", ClickState.PLAY_VIDEO));

                holder.post_audio_ll.setOnClickListener(v -> clickView(v, position, "", ClickState.PLAY_AUDIO));

                holder.post_img_ll.setOnClickListener(v -> {
                    if (nbPostDataList.get(position).getVideo() != null && !nbPostDataList.get(position).getVideo().isEmpty()) {
                        clickView(v, position, "", ClickState.PLAY_VIDEO);
                    } else {
                        if (newNoticeBoardFragment != null) { // this is for NewNoticeboardFragment
                            newNoticeBoardFragment.closeKeyboard();
                        }
                        if (parent != null) { // this is for NewNBTopicDetailActivity
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(parent.getWindowToken(), 0);
                        }
                        gifZoomPopup(holder.post_img.getDrawable());
//                        clickView(v, position, "", ClickState.OPEN_DETAILS);
                    }
                });

                holder.content_layout.setOnClickListener(v -> {
                    if (nbPostDataList.get(position).getVideo() != null && !nbPostDataList.get(position).getVideo().isEmpty()) {
                        clickView(v, position, "", ClickState.PLAY_VIDEO);
                    } else {
                        clickView(v, position, "", ClickState.OPEN_DETAILS);
                    }
                });

                holder.parent_layout.setOnClickListener(v -> {
                    if (nbPostDataList.get(position).getVideo() != null && !nbPostDataList.get(position).getVideo().isEmpty()) {
                        clickView(v, position, "", ClickState.PLAY_VIDEO);
                    } else {
                        clickView(v, position, "", ClickState.OPEN_DETAILS);
                    }
                });

                holder.post_attchment_ll.setOnClickListener(view -> {
                    if (holder.file != null) {
                        if (holder.file.exists()) {
                            openFile(holder.file);
                        } else {
                            clickView(view, position, "downloadAttachment", ClickState.OPEN_DETAILS);
                        }
                    }
                });

                if (context instanceof NewNBTopicDetailActivity) {
                    ((NewNBTopicDetailActivity) context).showText();
                }

            } else {
                holder.main_post_ll.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    void playVideo(String videoPath, ExoPlayer player) {
        try {
            MediaSource mediaSource;
            Uri videoUri = Uri.parse(videoPath);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context);
            mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
            Log.d("playVideo", "video source path: " + videoPath);
            player.addMediaSource(mediaSource);
            player.setPlayWhenReady(false);
            player.prepare();
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
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
            Log.d(TAG, "positionis: " + position);
            isClicked = true;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
            scaleDownX.setDuration(100);
            scaleDownY.setDuration(100);
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
//        NewNBPostData nbPostData = nbPostDataList.get(position);
        NewPostViewData postViewData = new NewPostViewData(nbPostDataList.get(position).getNbId(), nbPostDataList.get(position).getId());
        postViewData.setCommentDeleteType();
        postViewData.setNbCommentData(nbPostDataList.get(position).getNbCommentDataList().get(0));
        nBPostClickCallBack.onPostCommentDelete(postViewData);
    }

    private void openAudioPostPage(int position) {
        callNoticeboardAnalyticsAPI(position);
        Intent intent = new Intent(context, NewNBPostDetailsActivity.class);
        intent.putExtra("postId", nbPostDataList.get(position).getId());
        intent.putExtra("autoPlayAudio", true);
        context.startActivity(intent);
    }

    private void callNoticeboardAnalyticsAPI(int position) {
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("noticeBoardId", nbPostDataList.get(position).getNbId());
                    jsonObject.put("postId", nbPostDataList.get(position).getId());
                    jsonObject.put("userId", activeUser.getStudentid());

                    String submitGeoChanges = OustSdkApplication.getContext().getResources().getString(R.string.noticeBoard_post_view);
                    submitGeoChanges = HttpManager.getAbsoluteUrl(submitGeoChanges);

                    ApiCallUtils.doNetworkCall(Request.Method.POST, submitGeoChanges, jsonObject, new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {
                                    if (response.get("success").toString().equalsIgnoreCase("true")) {
                                        Log.d(TAG, "noticeBoard/post/view -> response is true");
                                    } else if (response.get("success").toString().equalsIgnoreCase("false")) {
                                        Log.d(TAG, "noticeBoard/post/view -> response is false");
                                    }
                                } else {
                                    Log.d(TAG, "noticeBoard/post/view -> response is false");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("noticeBoard/post/view", "response is Error " + error);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openVideoPostPage(int position) {
        callNoticeboardAnalyticsAPI(position);
        Intent intent = new Intent(context, NewNBPostDetailsActivity.class);
        intent.putExtra("postId", nbPostDataList.get(position).getId());
        intent.putExtra("autoPlayVideo", true);
        context.startActivity(intent);
    }

    private void openPostPage(int position, String message) {
        try {
            callNoticeboardAnalyticsAPI(position);
            Intent intent = new Intent(context, NewNBPostDetailsActivity.class);
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
//            NewNBPostData nbPostData = nbPostDataList.get(position);
            nbPostDataList.get(position).setHasShared(true);
            nbPostDataList.get(position).incrementShareCount();
            notifyDataSetChanged();
            NewPostViewData postViewData = new NewPostViewData(nbPostDataList.get(position).getNbId(), nbPostDataList.get(position).getId());
            postViewData.setShareType();
            nBPostClickCallBack.onPostShare(postViewData, share_uri_ll);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void performComment(int position, String message) {
        try {
//            NewNBPostData nbPostData = nbPostDataList.get(position);

            NewNBCommentData nbCommentData = new NewNBCommentData();
            nbCommentData.setComment(message).
                    setAvatar(OustAppState.getInstance().getActiveUser().getAvatar())
                    .setUserKey(OustAppState.getInstance().getActiveUser().getStudentKey())
                    .setPostId(nbPostDataList.get(position).getId())
                    .setCommentedOn(0)
                    .setCommentedBy(OustAppState.getInstance().getActiveUser().getUserDisplayName());
            nbPostDataList.get(position).setHasCommented(true);
            nbPostDataList.get(position).incrementCommentCount();
            nbPostDataList.get(position).addOfflineComment(nbCommentData);

            notifyDataSetChanged();

            NewPostViewData postViewData = new NewPostViewData(nbPostDataList.get(position).getNbId(), nbPostDataList.get(position).getId());
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
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void performLike(int position) {
        try {
//            NewNBPostData nbPostData = nbPostDataList.get(position);
            if (!nbPostDataList.get(position).hasLiked()) {
                nbPostDataList.get(position).setHasLiked(true);
                nbPostDataList.get(position).incrementLikeCount();
            } else {
                nbPostDataList.get(position).setHasLiked(false);
                nbPostDataList.get(position).decrementLikeCount();
            }
            notifyDataSetChanged();
            NewPostViewData postViewData = new NewPostViewData(nbPostDataList.get(position).getNbId(), nbPostDataList.get(position).getId(), System.currentTimeMillis());
            postViewData.setLikeType().setLike(nbPostDataList.get(position).hasLiked());
            nBPostClickCallBack.onPostLike(postViewData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onNBPostViewedInScroll(int position) {
        if (nBPostClickCallBack != null && nbPostDataList != null && nbPostDataList.get(position) != null) {
//            NewNBPostData nbPostData = nbPostDataList.get(position);
            nBPostClickCallBack.onPostViewed(new NewPostViewData(nbPostDataList.get(position).getNbId(), nbPostDataList.get(position).getId(), System.currentTimeMillis()));
        }
    }

    public void gifZoomPopup(final Drawable gif) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams")
            View popUpView = inflater.inflate(R.layout.gifzoom_popup, null);
            zoomImagePopup = OustSdkTools.createPopUp(popUpView);
            GifImageView gifImageView = popUpView.findViewById(R.id.mainzooming_img);
            gifImageView.setImageDrawable(gif);
            ImageButton imageCloseButton = popUpView.findViewById(R.id.zooming_imgclose_btn);
            final RelativeLayout zoomLayout = popUpView.findViewById(R.id.mainzoomimg_layout);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 0.0f, 1);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 0.0f, 1);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            zoomLayout.setVisibility(View.VISIBLE);
            zoomImagePopup.setOnDismissListener(zoomImagePopup::dismiss);
            imageCloseButton.setOnClickListener(view -> {
                ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                scaleDownX1.setDuration(350);
                scaleDownY1.setDuration(350);
                scaleDownX1.setInterpolator(new DecelerateInterpolator());
                scaleDownY1.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown1 = new AnimatorSet();
                scaleDown1.play(scaleDownX1).with(scaleDownY1);
                scaleDown1.start();
                scaleDown1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        try {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            });
            popUpView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ObjectAnimator scaleDownX12 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                    ObjectAnimator scaleDownY12 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                    scaleDownX12.setDuration(350);
                    scaleDownY12.setDuration(350);
                    scaleDownX12.setInterpolator(new DecelerateInterpolator());
                    scaleDownY12.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown12 = new AnimatorSet();
                    scaleDown12.play(scaleDownX12).with(scaleDownY12);
                    scaleDown12.start();
                    scaleDown12.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void closePopWindow() {
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing())
                zoomImagePopup.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e(TAG, "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            } else {
                ArrayList<NewNBPostData> list = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (NewNBPostData item : tempList) {
                    if ((item.getTitle() != null && item.getTitle().toLowerCase().contains(constraint.toString())) ||
                            (item.getDescription() != null && item.getDescription().toLowerCase().contains(constraint.toString())) ||
                            (item.getCreatedBy() != null && item.getCreatedBy().toLowerCase().contains(constraint.toString()))) {
                        list.add(item);
                    }
                    filterResults.count = list.size();
                    filterResults.values = list;
                }
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            nbPostDataList = (ArrayList<NewNBPostData>) results.values;
            notifyDataSetChanged();
        }
    }
}
