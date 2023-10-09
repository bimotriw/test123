package com.oustme.oustsdk.layoutFour.components.leaderBoard;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.model.leaderBoardResponse;
import com.oustme.oustsdk.layoutFour.components.dialogFragments.GroupFilterDialogFragment;
import com.oustme.oustsdk.layoutFour.components.dialogFragments.SortFilterDialogFragment;
import com.oustme.oustsdk.layoutFour.components.leaderBoard.adapter.LeaderBoardAdapter;
import com.oustme.oustsdk.layoutFour.data.response.GroupDataList;
import com.oustme.oustsdk.layoutFour.data.response.LeaderBoardDataList;
import com.oustme.oustsdk.layoutFour.data.response.LeaderBoardResponse;
import com.oustme.oustsdk.layoutFour.interfaces.LeaderBoardCallBacks;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComponentLeaderBoard extends LinearLayout {

    LinearLayout data_layout;
    LinearLayout overall_data_layout;
    RelativeLayout rank_2_layout;
    CircleImageView rank_2_user;
    TextView rank_2_text;
    RelativeLayout rank_1_layout;
    CircleImageView rank_1_user;
    TextView rank_1_text;
    RelativeLayout rank_3_layout;
    CircleImageView rank_3_user;
    TextView rank_3_text;
    TextView leader_board_for_tv;
    LinearLayout user_data_lay;
    TextView self_user_rank;
    CircleImageView self_user;
    TextView self_user_name;
    TextView self_user_score;
    RecyclerView leader_board_rv;
    View no_data_layout;
    ImageView no_image;
    TextView no_data_content;
    CircleImageView rank_1, rank_2, rank_3;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    int color;
    boolean isFilterImplemented;

    ArrayList<GroupDataList> groupDataLists;
    ArrayList<GroupDataList> selectedGroupDataLists;

    private FragmentManager fragmentManager;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));

    String contentType;
    String user_image;
    String user_ranks;
    String user_scores;
    String user_name;
    LeaderBoardCallBacks leaderBoardCallBacks;
    LeaderBoardAdapter adapter;
    private ActiveUser activeUser;

    Context context;

    public ComponentLeaderBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_leader_board, this, true);

        initViews();
    }

    private void initViews() {
        try {
            data_layout = findViewById(R.id.data_layout);
            overall_data_layout = findViewById(R.id.overall_data_layout);
            rank_2_layout = findViewById(R.id.rank_2_layout);
            rank_2_user = findViewById(R.id.rank_2_user);
            rank_2_text = findViewById(R.id.rank_2_text);
            rank_1_layout = findViewById(R.id.rank_1_layout);
            rank_1_user = findViewById(R.id.rank_1_user);
            rank_1_text = findViewById(R.id.rank_1_text);
            rank_3_layout = findViewById(R.id.rank_3_layout);
            rank_3_user = findViewById(R.id.rank_3_user);
            rank_3_text = findViewById(R.id.rank_3_text);
            leader_board_for_tv = findViewById(R.id.leader_board_for_tv);
            user_data_lay = findViewById(R.id.user_data_lay);
            self_user_rank = findViewById(R.id.self_user_rank);
            self_user = findViewById(R.id.self_user);
            self_user_name = findViewById(R.id.self_user_name);
            self_user_score = findViewById(R.id.self_user_score);
            leader_board_rv = findViewById(R.id.leader_board_rv);
            no_data_layout = findViewById(R.id.no_data_layout);
            rank_1 = findViewById(R.id.rank_1);
            rank_2 = findViewById(R.id.rank_2);
            rank_3 = findViewById(R.id.rank_3);
            no_image = no_data_layout.findViewById(R.id.no_image);
            no_data_content = no_data_layout.findViewById(R.id.no_data_content);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            getColors();
            try {
                rank_1_text.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.leaderboard_rank_2), OustPreferences.get("toolbarColorCode")));
                rank_2_text.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.leaderboard_rank_2), OustPreferences.get("secondaryColor")));
                rank_3_text.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.leaderboard_rank_2), OustPreferences.get("treasuryColor")));
                user_data_lay.setBackgroundColor(OustSdkTools.getColorBack(R.color.grey_b));

                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                String tenantId = OustPreferences.get("tanentid");

                if (tenantId != null && !tenantId.isEmpty()) {
                    File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                            ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                    if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                        Picasso.get().load(brandingBg).into(branding_bg);
                    } else {
                        String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                        Picasso.get().load(tenantBgImage).into(branding_bg);
                    }

                    File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                    if (brandingLoader.exists()) {
                        Picasso.get().load(brandingLoader).into(branding_icon);
                    } else {
                        String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                        Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Drawable setBorderColor(Drawable drawable, String color) {
        if (color != null && drawable != null) {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(drawable),
                    Color.parseColor(color)
            );
        }
        return drawable;
    }

    private void getColors() {
        color = OustResourceUtils.getColors();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCallBackForView(LeaderBoardCallBacks leaderBoardCallBacks) {
        this.leaderBoardCallBacks = leaderBoardCallBacks;
    }

    public void setData(LeaderBoardResponse leaderBoardResponse) {
        try {
            if (leaderBoardResponse != null) {
                if (leaderBoardResponse.getLeaderBoardDataList() != null && leaderBoardResponse.getLeaderBoardDataList().size() != 0) {
                    branding_mani_layout.setVisibility(GONE);
                    if (leaderBoardResponse.getLeaderBoardDataList().size() > 4) {
                        data_layout.setVisibility(VISIBLE);
                        no_data_layout.setVisibility(GONE);
                        leaderBoardCallBacks.hideFilterSort(true);
                        ArrayList<LeaderBoardDataList> leaderBoardDataArrayList = leaderBoardResponse.getLeaderBoardDataList();
                        if (!leaderBoardResponse.isFilterImplemented()) {
                            overall_data_layout.setVisibility(VISIBLE);
                            isFilterImplemented = false;
                            if ((leaderBoardDataArrayList.size() - 1) >= 1) {
                                String rank1_user_image = leaderBoardDataArrayList.get(0).getAvatar();
                                String rank1_user_rank = leaderBoardDataArrayList.get(0).getRank() + "";
                                if (rank1_user_image != null && !rank1_user_image.isEmpty()) {
                                    if (rank1_user_image.startsWith("http") || rank1_user_image.startsWith("https")) {
                                        Picasso.get().load(rank1_user_image)
                                                .placeholder(bd_loading).error(bd)
                                                .into(rank_1_user);
                                    } else {
                                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + rank1_user_image)
                                                .placeholder(bd_loading).error(bd).into(rank_1_user);
                                    }
                                }
                                rank_1.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.leaderboard_rank_2), OustPreferences.get("toolbarColorCode")));
                                rank_1_text.setText(rank1_user_rank);
                            }
                            if ((leaderBoardDataArrayList.size() - 1) >= 2) {
                                String rank2_user_image = leaderBoardDataArrayList.get(1).getAvatar();
                                String rank2_user_rank = leaderBoardDataArrayList.get(1).getRank() + "";
                                if (rank2_user_image != null && !rank2_user_image.isEmpty()) {
                                    if (rank2_user_image.startsWith("http") || rank2_user_image.startsWith("https")) {
                                        Picasso.get().load(rank2_user_image)
                                                .placeholder(bd_loading).error(bd)
                                                .into(rank_2_user);
                                    } else {
                                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + rank2_user_image)
                                                .placeholder(bd_loading).error(bd).into(rank_2_user);
                                    }
                                }
                                rank_2.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.leaderboard_rank_2), OustPreferences.get("secondaryColor")));
                                rank_2_text.setText(rank2_user_rank);
                            } else {
                                rank_2_layout.setVisibility(View.GONE);
                                rank_3_layout.setVisibility(View.GONE);
                            }
                            if ((leaderBoardDataArrayList.size() - 1) >= 3) {
                                String rank3_user_image = leaderBoardDataArrayList.get(2).getAvatar();
                                String rank3_user_rank = leaderBoardDataArrayList.get(2).getRank() + "";

                                if (rank3_user_image != null && !rank3_user_image.isEmpty()) {
                                    if (rank3_user_image.startsWith("http") || rank3_user_image.startsWith("https")) {
                                        Picasso.get().load(rank3_user_image)
                                                .placeholder(bd_loading).error(bd)
                                                .into(rank_3_user);
                                    } else {
                                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + rank3_user_image)
                                                .placeholder(bd_loading).error(bd).into(rank_3_user);
                                    }
                                }
                                rank_3.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.leaderboard_rank_2), OustPreferences.get("treasuryColor")));
                                rank_3_text.setText(rank3_user_rank);
                            } else {
                                rank_3_layout.setVisibility(View.GONE);
                            }

                            if (contentType.equalsIgnoreCase("AssessmentLeaderBoard")) {
                                if (leaderBoardDataArrayList.size() > 0) {
                                    for (int i = 0; i < leaderBoardDataArrayList.size(); i++) {
                                        if (leaderBoardDataArrayList.get(i).getUserid().equalsIgnoreCase(activeUser.getStudentid())) {
                                            user_image = leaderBoardDataArrayList.get(i).getAvatar();
                                            user_ranks = "" + leaderBoardDataArrayList.get(i).getRank() + "";
                                            Log.e("TAG", "setData: --userCoins-->" + leaderBoardDataArrayList.get(i).getScore());
                                            //OustPreferences.save("earnedUserCoins", String.valueOf(leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getScore()));
                                            user_scores = "" + OustSdkTools.formatMilliinFormat(leaderBoardDataArrayList.get(i).getScore()) + "";
                                            user_name = "" + leaderBoardDataArrayList.get(i).getDisplayName() + "";
                                            leaderBoardDataArrayList.remove(i);
                                        }
                                    }
                                } else {
                                    user_data_lay.setVisibility(View.GONE);
                                }
                            } else {
                                user_image = leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getAvatar();
                                user_ranks = "" + leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getRank() + "";
                                Log.e("TAG", "setData: --userCoins-->" + leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getScore());
                                //OustPreferences.save("earnedUserCoins", String.valueOf(leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getScore()));
                                user_scores = "" + OustSdkTools.formatMilliinFormat(leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getScore()) + "";
                                user_name = "" + leaderBoardDataArrayList.get(leaderBoardDataArrayList.size() - 1).getDisplayName() + "";
                            }

                            if (leaderBoardDataArrayList.size() < 4) {
                                user_data_lay.setVisibility(View.GONE);
                            }
                            if (user_image != null && !user_image.isEmpty()) {
                                if (user_image.startsWith("http") || user_image.startsWith("https")) {
                                    Picasso.get().load(user_image)
                                            .placeholder(bd_loading).error(bd)
                                            .into(self_user);
                                } else {
                                    Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + user_image)
                                            .placeholder(bd_loading).error(bd).into(self_user);
                                }
                            }
                            Log.e("TAG", "setData: " + user_scores);
                            self_user_rank.setText(user_ranks);
                            self_user_score.setText(user_scores);
                            self_user_name.setText(user_name);
                        } else {
                            overall_data_layout.setVisibility(GONE);
                            isFilterImplemented = true;
                        }
                        try {
                            leaderBoardDataArrayList.remove(leaderBoardDataArrayList.size() - 1);
                            if (leaderBoardDataArrayList.size() != 0) {
                                adapter = new LeaderBoardAdapter();
                                adapter.setLeaderBoardAdapter(leaderBoardDataArrayList, getContext(), "filter");
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                leader_board_rv.setLayoutManager(mLayoutManager);
                                leader_board_rv.setItemAnimator(new DefaultItemAnimator());
                                leader_board_rv.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else {
                        leaderBoardCallBacks.hideFilterSort(false);
                        data_layout.setVisibility(GONE);
                        no_data_layout.setVisibility(VISIBLE);
                        no_image.setImageResource(R.drawable.ic_leaderboard_illustration);
                        no_data_content.setText(getResources().getString(R.string.no_leaderboard_assessment_content));
                    }

                } else {
                    no_data_layout.setVisibility(VISIBLE);
                    no_image.setImageResource(R.drawable.ic_leaderboard_illustration);
                    no_data_content.setText(getResources().getString(R.string.no_ranks_leader_board));
                    data_layout.setVisibility(GONE);
                    branding_mani_layout.setVisibility(GONE);
                }
            } else {
                no_data_layout.setVisibility(VISIBLE);
                no_image.setImageResource(R.drawable.ic_leaderboard_illustration);
                no_data_content.setText(getResources().getString(R.string.no_ranks_leader_board));
                data_layout.setVisibility(GONE);
                branding_mani_layout.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setSort(FragmentManager sort) {
        this.fragmentManager = sort;
    }

    public void setFilter(FragmentManager fragmentManager, ArrayList<GroupDataList> groupDataLists, boolean isFilterImplemented) {
        try {
            this.fragmentManager = fragmentManager;
            if (groupDataLists != null && groupDataLists.size() != 0 && !isFilterImplemented) {
                this.groupDataLists = new ArrayList<>();
                GroupDataList groupData = new GroupDataList();
                groupData.setGroupName("All");
                groupData.setGroupId(0);
                this.groupDataLists.add(groupData);
                this.groupDataLists.addAll(groupDataLists);
                selectedGroupDataLists = this.groupDataLists;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void openDialogFragment() {
        try {
            GroupFilterDialogFragment dialogFragment = GroupFilterDialogFragment.newInstance(groupDataLists, selectedGroupDataLists, this::filterByGroup);
            dialogFragment.setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
            dialogFragment.show(fragmentManager, "Filter Dialog");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void openDialogFragmentSort() {
        try {
            String[] positionsData = {"RANK", "NAME", "SCORE"};
            SortFilterDialogFragment sortFilterDialogFragment = SortFilterDialogFragment.newInstance(positionsData, this::filterByGroup1);
            sortFilterDialogFragment.setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
            sortFilterDialogFragment.show(fragmentManager, "Filter Dialog");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void filterByGroup1(int position) {
        try {
            OustStaticVariableHandling.getInstance().setSortPosition(position);
            leaderBoardCallBacks.onClickListener(position);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void filterByGroup(ArrayList<GroupDataList> filterGroupDataLists) {
        try {
            if (filterGroupDataLists != null && filterGroupDataLists.size() != 0) {
                this.selectedGroupDataLists = filterGroupDataLists;
                GroupDataList filterGroup = filterGroupDataLists.get(0);
                if (filterGroup != null) {
                    leaderBoardCallBacks.groupFilterData(filterGroup);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleFocus() {
        try {
//            leader_board_rv.scrollTo(0, 0);
            overall_data_layout.setVisibility(VISIBLE);
            leader_board_rv.setVisibility(VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeSearch() {
        try {
            if (!isFilterImplemented) {
                overall_data_layout.setVisibility(VISIBLE);
                leader_board_rv.setVisibility(VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleSearch(String query) {
        try {
            leader_board_rv.setVisibility(VISIBLE);
            if (adapter != null) {
                adapter.getFilter().filter(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setSortData(ArrayList<LeaderBoardDataList> leaderBoardDataRowList) {
        adapter = new LeaderBoardAdapter();
        adapter.setLeaderBoardAdapter(leaderBoardDataRowList, getContext(), "sort");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        leader_board_rv.setLayoutManager(mLayoutManager);
        leader_board_rv.setItemAnimator(new DefaultItemAnimator());
        leader_board_rv.setAdapter(adapter);
    }

}
