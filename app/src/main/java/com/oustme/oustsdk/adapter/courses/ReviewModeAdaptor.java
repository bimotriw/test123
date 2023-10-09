package com.oustme.oustsdk.adapter.courses;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.SearchCourseLevel;
import com.oustme.oustsdk.interfaces.course.ReviewModeCallBack;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.List;

public class ReviewModeAdaptor extends RecyclerView.Adapter<ReviewModeAdaptor.MyViewHolder> {
    private List<SearchCourseLevel> searchCourseLevelList;
    private ReviewModeCallBack reviewModeCallBack;
    private boolean allCourseSearchMode=false;
    private RecyclerView horizantalSearcRecyclerView;
    private UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;
    private String language;


    public void setReviewModeCallBack(ReviewModeCallBack reviewModeCallBack) {
        this.reviewModeCallBack = reviewModeCallBack;
    }

    public ReviewModeAdaptor(List<SearchCourseLevel> searchCourseLevelList, boolean allCourseSearchMode, String language) {
        this.searchCourseLevelList = searchCourseLevelList;
        this.allCourseSearchMode=allCourseSearchMode;
        userCourseScoreDatabaseHandler=new UserCourseScoreDatabaseHandler();
        this.language=language;
    }

    public void notifyDateChanges(List<SearchCourseLevel> searchCourseLevelList,boolean allCourseSearchMode){
        this.searchCourseLevelList = searchCourseLevelList;
        this.allCourseSearchMode = allCourseSearchMode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchCourseLevelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView usermanual_heading,reviewmoderow_indextext;
        private RelativeLayout usermanual_mainrow,show_cardlayout,downloadlevel_iconlayout;
        private ImageView show_cardupbutton,show_carddownbutton,downloading_icon,download_icon,downloaded_icon;
        private ReviewModeHorizontalAdapter reviewModeHorizontalAdapter;
        private View currentView;
        MyViewHolder(View view) {
            super(view);
            currentView=view;
            usermanual_heading= view.findViewById(R.id.usermanual_heading);
            reviewmoderow_indextext= view.findViewById(R.id.reviewmoderow_indextext);
            usermanual_mainrow= view.findViewById(R.id.usermanual_mainrow);
            show_cardupbutton= view.findViewById(R.id.show_cardupbutton);
            show_carddownbutton= view.findViewById(R.id.show_carddownbutton);
            show_cardlayout= view.findViewById(R.id.show_cardlayout);
            downloadlevel_iconlayout= view.findViewById(R.id.downloadlevel_iconlayout);
            downloading_icon= view.findViewById(R.id.downloading_icon);
            download_icon= view.findViewById(R.id.download_icon);
            downloaded_icon= view.findViewById(R.id.downloaded_icon);

            OustSdkTools.setDownloadGifImage(downloading_icon);

        }
    }
    @Override
    public ReviewModeAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewmode_rowlayout, parent, false);
        return new ReviewModeAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewModeAdaptor.MyViewHolder holder, final int position) {
        horizantalSearcRecyclerView= holder.currentView.findViewById(R.id.horizantalSearcRecyclerView);
        setHeavyFont(holder.usermanual_heading);
        holder.usermanual_heading.setText("");
        horizantalSearcRecyclerView.setVisibility(View.GONE);
        setHeavyFont(holder.reviewmoderow_indextext);
        holder.reviewmoderow_indextext.setText((""+(position+1)));
        if(searchCourseLevelList.get(position).getName()!=null){
            holder.usermanual_heading.setText(searchCourseLevelList.get(position).getName());
        }
        if (searchCourseLevelList.get(position).isSearchMode() || allCourseSearchMode) {
            horizantalSearcRecyclerView.setVisibility(View.VISIBLE);
            holder.show_cardupbutton.setVisibility(View.VISIBLE);
            holder.show_carddownbutton.setVisibility(View.GONE);
        }else {
            holder.show_cardupbutton.setVisibility(View.GONE);
            holder.show_carddownbutton.setVisibility(View.VISIBLE);
        }
        if(!allCourseSearchMode) {
            holder.downloadlevel_iconlayout.setVisibility(View.VISIBLE);
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null) && (dtoUserCourseData.getUserLevelDataList().size() > position) &&
                    (dtoUserCourseData.getUserLevelDataList().get(position) != null)) {
                if ((dtoUserCourseData.getUserLevelDataList().get(position).getCompletePercentage() == 100)) {
                    userCourseScoreDatabaseHandler.setUserLevelDataDownloadStatus(false,dtoUserCourseData,position);
//                    holder.downloadlevel_iconlayout.setVisibility(View.GONE);
                    holder.downloading_icon.setVisibility(View.GONE);
                    holder.download_icon.setVisibility(View.GONE);
                    holder.downloaded_icon.setVisibility(View.VISIBLE);
                } else {
                    if ((dtoUserCourseData.getUserLevelDataList().get(position).isDownloading())) {
                        holder.downloading_icon.setVisibility(View.VISIBLE);
                        holder.download_icon.setVisibility(View.GONE);
                        holder.downloaded_icon.setVisibility(View.GONE);
                    } else {
                        holder.downloading_icon.setVisibility(View.GONE);
                        holder.download_icon.setVisibility(View.VISIBLE);
                        holder.downloaded_icon.setVisibility(View.GONE);
                    }
                }
            }
        }else {
            holder.downloadlevel_iconlayout.setVisibility(View.GONE);
        }
        if(holder.reviewModeHorizontalAdapter==null){
            horizantalSearcRecyclerView.setLayoutManager(new LinearLayoutManager(OustSdkApplication.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.reviewModeHorizontalAdapter = new ReviewModeHorizontalAdapter(searchCourseLevelList.get(position).getSearchCourseCards(),position,language);
            holder.reviewModeHorizontalAdapter.setReviewModeCallBack(reviewModeCallBack );
            horizantalSearcRecyclerView.setAdapter(holder.reviewModeHorizontalAdapter);
        }else {
            holder.reviewModeHorizontalAdapter.notifyDateChanges(searchCourseLevelList.get(position).getSearchCourseCards(),position);
        }
        if(allCourseSearchMode){
            holder.show_cardlayout.setVisibility(View.GONE);
        }else {
            holder.show_cardlayout.setVisibility(View.VISIBLE);
        }

        holder.usermanual_mainrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.show_cardupbutton.getVisibility()==View.GONE) {
                    holder.show_cardupbutton.setVisibility(View.VISIBLE);
                    holder.show_carddownbutton.setVisibility(View.GONE);
                }else {
                    holder.show_cardupbutton.setVisibility(View.GONE);
                    holder.show_carddownbutton.setVisibility(View.VISIBLE);
                }
                reviewModeCallBack.onMainRowClick(position);
            }
        });

        holder.downloadlevel_iconlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null) && (dtoUserCourseData.getUserLevelDataList().size() > position) &&
                        (dtoUserCourseData.getUserLevelDataList().get(position) != null)) {
                    if((!dtoUserCourseData.getUserLevelDataList().get(position).isDownloading())&&(dtoUserCourseData.getUserLevelDataList().get(position).getCompletePercentage()<100)){
                        reviewModeCallBack.onDownloadIconClick(position);
                        holder.downloading_icon.setVisibility(View.VISIBLE);
                        holder.download_icon.setVisibility(View.GONE);
                        holder.downloaded_icon.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    private void setCardAnimation(View view,int time) {
        try {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "translationX", 1000.0f, 0.0f);
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha",0.0f,1.0f);
            alphaAnim.setDuration(600);
            scaleDownX.setDuration(600);
            scaleDownX.setStartDelay((time*200));
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(alphaAnim);
            scaleDown.start();
            scaleDown.start();
        } catch (Exception e) {}
    }
    private int getId(String courseID, long levelId, long cardId){
        int newCardId=(int)cardId;
        try {

            String s2 = ""+courseID+""+levelId+""+cardId;
            newCardId=Integer.parseInt(s2);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
        return newCardId;
    }

    private void setHeavyFont(TextView tv){
        try {
            if (language == null || (language!=null && language.isEmpty()) ||
                    (language != null && !language.isEmpty() && language.equalsIgnoreCase("en"))) {
                tv.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}

//    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.searchcard_layout.getLayoutParams();
//        params.height = 0;
//                holder.searchcard_layout.setLayoutParams(params);
//holder.searchcard_layout.removeAllViews();
//        holder.reviewmoderow_indextext.setText((""+(position+1)));
//        if ((searchCourseLevelList.get(position).isSearchMode()&&(!allCourseSearchMode))||(allCourseSearchMode)) {
//        LayoutInflater mInflater = (LayoutInflater) OustSdkApplication.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//        int size = (int) OustSdkApplication.getContext().getResources().getDimension(R.dimen.oustlayout_dimen40);
//        params.height = ((int) (size * searchCourseLevelList.get(position).getSearchCourseCards().size()));
//        holder.searchcard_layout.setLayoutParams(params);
//        for (int i = 0; i < searchCourseLevelList.get(position).getSearchCourseCards().size(); i++) {
//        View convertView1 = mInflater.inflate(R.layout.reviewmode_cardrow, null);
//        TextView reviewmode_cardheading = (TextView) convertView1.findViewById(R.id.reviewmode_cardheading);
//        TextView reviewmode_cardindex=(TextView) convertView1.findViewById(R.id.reviewmode_cardindex);
//        RelativeLayout reviewmodecard_mainrow = (RelativeLayout) convertView1.findViewById(R.id.reviewmodecard_mainrow);
//        reviewmode_cardindex.setText((""+i));
//        if ((searchCourseLevelList.get(position).getSearchCourseCards().get(i).getName() != null) && (!searchCourseLevelList.get(position).getSearchCourseCards().get(i).getName().isEmpty())) {
//        reviewmode_cardheading.setText(searchCourseLevelList.get(position).getSearchCourseCards().get(i).getName());
//        } else {
//        if ((searchCourseLevelList.get(position).getSearchCourseCards().get(i).getDescription() != null) && (!searchCourseLevelList.get(position).getSearchCourseCards().get(i).getDescription().isEmpty())) {
//        reviewmode_cardheading.setText(searchCourseLevelList.get(position).getSearchCourseCards().get(i).getDescription());
//        }
//        }
//        convertView1.setY(size * (i));
//        if (lastPosition <= position) {
//        reviewmodecard_mainrow.setVisibility(View.GONE);
//        setCardAnimation(reviewmodecard_mainrow, (i));
//        }
//        reviewmode_cardindex.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(final View view) {
//        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f,0.98f);
//        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f,0.99f);
//        scaleDownX.setDuration(100);
//        scaleDownY.setDuration(100);
//        scaleDownX.setRepeatCount(1);
//        scaleDownY.setRepeatCount(1);
//        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
//        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
//        scaleDownX.setInterpolator(new DecelerateInterpolator());
//        scaleDownY.setInterpolator(new DecelerateInterpolator());
//        AnimatorSet scaleDown = new AnimatorSet();
//        scaleDown.play(scaleDownX).with(scaleDownY);
//        scaleDown.start();
//        scaleDown.addListener(new Animator.AnimatorListener() {
//@Override
//public void onAnimationStart(Animator animator) {}
//@Override
//public void onAnimationEnd(Animator animator) {
//        TextView t1=(TextView)(view);
//        int cardNo=Integer.parseInt(t1.getText().toString());
//        reviewModeCallBack.onCardClick(position,cardNo);
//        }
//@Override
//public void onAnimationCancel(Animator animator) {}
//
//@Override
//public void onAnimationRepeat(Animator animator) {}
//        });
//
//        }
//        });
//        holder.searchcard_layout.addView(convertView1);
//        }
//        if (position >= lastPosition) {
//        lastPosition = (position+1);
//        }
//        }
