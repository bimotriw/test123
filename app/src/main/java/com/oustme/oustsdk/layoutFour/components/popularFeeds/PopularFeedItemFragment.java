package com.oustme.oustsdk.layoutFour.components.popularFeeds;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.customviews.HeavyCustomTextView;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PopularFeedItemFragment extends Fragment implements View.OnClickListener {

    public static final String FEED = "popular_feed";
    private ImageView ivBanner;
    private TextView tvTitle, tvDesc;
    private ConstraintLayout special_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.component_popular_feed_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            DTOSpecialFeed feed = getArguments().getParcelable(FEED);
            final int position = feed.getPosition();
            final DTOSpecialFeed special_feed = feed;

            ivBanner = view.findViewById(R.id.card);
            tvTitle = view.findViewById(R.id.tv_title);
            tvDesc = view.findViewById(R.id.tv_des);
            special_layout = view.findViewById(R.id.special_layout);

          /*  if (!TextUtils.isEmpty(feed.getHeader())) {
                tvTitle.setText(feed.getHeader());
                tvTitle.setVisibility(View.VISIBLE);
            } else
                tvTitle.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(feed.getContent())) {
                tvDesc.setText(feed.getContent());
                tvDesc.setVisibility(View.VISIBLE);
            } else
                tvDesc.setVisibility(View.GONE);*/

            tvTitle.setVisibility(View.GONE);
            tvDesc.setVisibility(View.GONE);

            BitmapDrawable bd = OustSdkTools.getImageDrawable(getResources().getString(R.string.mydesk));
            if (special_feed.getImageUrl() != null && !special_feed.getImageUrl().isEmpty()) {
                ivBanner.setVisibility(View.VISIBLE);
                Glide.with(Objects.requireNonNull(requireActivity())).load(special_feed.getImageUrl()).error(bd).into(ivBanner);
            } else {
                ivBanner.setVisibility(View.VISIBLE);
                ivBanner.setImageDrawable(bd);
            }

            special_layout.setOnClickListener(v -> {
                try {
                    if (special_feed.getType() != null) {
                        if (special_feed.getType().equalsIgnoreCase("FFF_CONTEXT")) {
                            Intent intent4 = new Intent(OustSdkApplication.getContext(), FFcontestStartActivity.class);
                            Gson gson = new Gson();
                            intent4.putExtra("fastestFingerContestData", gson.toJson(special_feed.getFastestFingerContestData()));
                            startActivity(intent4);
                        } else {
                            if (feed.isFeed()) {
                                initWelComePopViews(special_feed);
                            } else {
                                Intent intent = new Intent(getContext(), CplBaseActivity.class);
                                intent.putExtra("cplId", String.valueOf(special_feed.getId()));
                                startActivity(intent);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            });
        } catch (
                Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private PopupWindow mPopupWindow;
    int scrWidth = 0;
    int scrHeight = 0;
    private DrawerLayout mDrawerLayout;

    private void initWelComePopViews(final DTOSpecialFeed newFeed) {
        if (mPopupWindow != null && mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;

        scrWidth = (int) (scrWidth - 0.1 * scrWidth);
        scrHeight = (int) (scrHeight - 0.1 * scrHeight);

        // Inflate the custom layout/view

        View customView = inflater.inflate(R.layout.welcome_popup_window, null);
        mPopupWindow = new PopupWindow(
                customView,
                scrWidth,
                scrHeight
        );

        HeavyCustomTextView title = customView.findViewById(R.id.textViewFeedTitlePop);
        HeavyCustomTextView start = customView.findViewById(R.id.textViewNext);
        LinearLayout linearLayout = customView.findViewById(R.id.linearLayoutStart);
        CustomTextView desc = customView.findViewById(R.id.textViewFeedDescriptionpop);
        ImageView imageViewBg = customView.findViewById(R.id.imageViewBg);
        ImageView imageViewVlose = customView.findViewById(R.id.close);
        ImageView imageViewStart = customView.findViewById(R.id.feed_start);
        LinearLayout mLinearLayoutVideo = customView.findViewById(R.id.linearLayoutVideo);
        ImageView imageViewPlayVideo = customView.findViewById(R.id.imageViewVideoPlay);
        LinearLayout linearLayoutOverlay = customView.findViewById(R.id.linearLayoutBlackOverlay);
        LinearLayout linearLayoutContent = customView.findViewById(R.id.linearLayoutContent);


        imageViewPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newFeed.getCourseCardClass() != null && newFeed.getFeedType().equals(FeedType.COURSE_CARD_L)) {
                    OustDataHandler.getInstance().setCourseCardClass(newFeed.getCourseCardClass());
                    Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                    intent.putExtra("type", "card");
                    startActivity(intent);
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.dismiss();
                }
            }
        });


        imageViewVlose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: ");
                mPopupWindow.dismiss();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newFeed.getCourseCardClass() != null && newFeed.getFeedType().equals(FeedType.COURSE_CARD_L)) {
                    OustDataHandler.getInstance().setCourseCardClass(newFeed.getCourseCardClass());
                    Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                    intent.putExtra("type", "card");
                    startActivity(intent);
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.dismiss();
                    //  Toast.makeText(NewLandingActivity.this, "Feed is not a card type", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*if (toolbarColorCode != null) {
            start.setTextColor(Color.parseColor(toolbarColorCode));
            int appColor = Color.parseColor(toolbarColorCode);
            imageViewStart.setColorFilter(appColor);
        }*/

        if (newFeed != null) {
            if (newFeed.getHeader() != null) {
                title.setText(Html.fromHtml(newFeed.getHeader()));
                if (!newFeed.isTitleVisible()) {
                    title.setVisibility(View.GONE);
                } else {
                    title.setVisibility(View.VISIBLE);
                }
            }
            if (newFeed.getContent() != null) {
                desc.setText(Html.fromHtml(newFeed.getContent()));
                if (!newFeed.isDescVisible()) {
                    desc.setVisibility(View.GONE);
                } else {
                    desc.setVisibility(View.VISIBLE);
                }
            }
            if (newFeed.getImageUrl() != null && newFeed.getImageUrl() != "" && !newFeed.getImageUrl().equals(""))
                Picasso.get().load(newFeed.getImageUrl()).into(imageViewBg);
            if (newFeed.getmSpecialFeedStartText() != null) {
                start.setText(newFeed.getmSpecialFeedStartText());
                start.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(R.dimen.oustlayout_dimen40, 0, 0, 0);
                // imageViewStart.setLayoutParams(lp);
            } else {
                start.setVisibility(View.GONE);
            }

            if (!newFeed.isTitleVisible() && !newFeed.isDescVisible()) {
                linearLayoutContent.setVisibility(View.GONE);
                linearLayoutOverlay.setVisibility(View.GONE);
            }
        }

        special_layout.post(new Runnable() {
            @Override
            public void run() {
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mPopupWindow.showAtLocation(special_layout, Gravity.CENTER, 0, 0);
                dimBehind(mPopupWindow);
            }
        });
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.8f;
        wm.updateViewLayout(container, p);
    }

    @Override
    public void onClick(View v) {

    }

}
