package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.AllFavouriteCardsOfCourseActivity;
import com.oustme.oustsdk.interfaces.common.CardClickCallBack;
import com.oustme.oustsdk.response.common.FavouriteCardsCourseData;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.List;

public class FavouriteCourseAdapter extends RecyclerView.Adapter<FavouriteCourseAdapter.MyViewHolder> {
    private List<FavouriteCardsCourseData> favouriteCardsCourseData=new ArrayList<>();
    private CardClickCallBack cardClickCallBack;

    private RecyclerView recyclerView;
    private Context context;

    public FavouriteCourseAdapter(List<FavouriteCardsCourseData> favouriteCardsCourseData,Context context) {
        this.favouriteCardsCourseData=favouriteCardsCourseData;
        this.context = context;
    }

    public void setCardClickCallBack(CardClickCallBack cardClickCallBack){
        this.cardClickCallBack=cardClickCallBack;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.favouritecard_coursename, parent, false);
        return new FavouriteCourseAdapter.MyViewHolder(itemView);
    }

    public void notifyFavCardDataChange(List<FavouriteCardsCourseData> favouriteCardsCourseData) {
        this.favouriteCardsCourseData=favouriteCardsCourseData;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        recyclerView= holder.currentView.findViewById(R.id.favcard_ofcourse);
        recyclerView.setLayoutManager(new LinearLayoutManager(OustSdkApplication.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.courseName.setText(favouriteCardsCourseData.get(position).getCourseName().toUpperCase());
        List<FavCardDetails> favCardDetailList=favouriteCardsCourseData.get(position).getFavCardDetailsList();
        holder.favouriteCardOfCourseAdapter = new FavouriteCardOfCourseAdapter(favCardDetailList,favouriteCardsCourseData.get(position).getCourseName(),favouriteCardsCourseData.get(position).getCourseId());
        holder.favouriteCardOfCourseAdapter.setCardClickCallBack(cardClickCallBack);
        recyclerView.setAdapter(holder.favouriteCardOfCourseAdapter);
        if(favouriteCardsCourseData.get(position).getFavCardDetailsList().size()>2) {
            holder.seeAll.setText(context.getResources().getString(R.string.see_all));
            holder.seeall_layout.setVisibility(View.VISIBLE);
            holder.seeall_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OustSdkApplication.getContext(), AllFavouriteCardsOfCourseActivity.class);
                    intent.putExtra("courseName", "" + favouriteCardsCourseData.get(position).getCourseName());
                    intent.putExtra("courseId", "" + favouriteCardsCourseData.get(position).getCourseId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    OustSdkTools.newActivityAnimationF(intent, OustSdkApplication.getContext());
                }
            });
        }else {
            holder.seeall_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return favouriteCardsCourseData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView courseName,seeAll;
        public RelativeLayout seeall_layout;
        private View currentView;
        private FavouriteCardOfCourseAdapter favouriteCardOfCourseAdapter;
        public MyViewHolder(View itemView) {
            super(itemView);
            currentView=itemView;
            Context context=itemView.getContext();
            courseName= itemView.findViewById(R.id.favouritecard_coursename);
            seeall_layout= itemView.findViewById(R.id.seeall_layout);
            seeAll= itemView.findViewById(R.id.seeAll);
        }
    }
}
