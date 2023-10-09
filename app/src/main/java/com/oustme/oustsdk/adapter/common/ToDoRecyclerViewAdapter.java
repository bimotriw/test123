package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.todoactivity.TodoListActivity;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.model.response.common.ToDoChildModel;
import com.oustme.oustsdk.model.response.common.ToDoHeaderModel;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 09/11/18.
 */

public class ToDoRecyclerViewAdapter extends  RecyclerView.Adapter<ToDoRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = "ToDoRecyclerViewAdapter";

    private List<ToDoChildModel> mItemList;
    private List<ToDoChildModel> mOriginalItemList;
    private List<ToDoHeaderModel> mTodoHeader;
    private int type;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        private ImageView rowbanner_image, rowicon_image, coins_icon;
        private TextView coursenametext, total_enrolled_count, totaloc_text, moduleprogress;
        private RelativeLayout main_layout;
        private RelativeLayout cource_mainrow, main_card_ll, module_progress_ll, bannerimage_layout;

        public MyViewHolder(View view) {
            super(view);
            cource_mainrow = view.findViewById(R.id.cource_mainrow);
            coursenametext = view.findViewById(R.id.coursenametext);
            total_enrolled_count = view.findViewById(R.id.total_enrolled_count);
            totaloc_text = view.findViewById(R.id.totaloc_text);
            rowbanner_image = view.findViewById(R.id.rowbanner_image);
            main_card_ll = view.findViewById(R.id.main_card_ll);
            rowicon_image = view.findViewById(R.id.rowicon_image);
            main_layout = view.findViewById(R.id.main_layout);
//            moduleprogress=(TextView) view.findViewById(R.id.moduleprogress);
//            module_progress_ll=(RelativeLayout) view.findViewById(R.id.module_progress_ll);
            bannerimage_layout = view.findViewById(R.id.bannerimage_layout);
             LayoutInflater mInflater = (LayoutInflater) OustSdkApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView1 = mInflater.inflate(R.layout.rotated_feedview, null);
                moduleprogress = convertView1.findViewById(R.id.moduleprogress);
                module_progress_ll = convertView1.findViewById(R.id.module_progress_ll);
                cource_mainrow.addView(convertView1);

            try {
                coins_icon = view.findViewById(R.id.coins_icon);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coins_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                }else{
                    OustSdkTools.setImage(coins_icon, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }
    }


    public ToDoRecyclerViewAdapter(Context context, List<ToDoChildModel> mItemList, int type) {
        this.mItemList = mItemList;
       // this.mTodoHeader = mToDoHeader;
        this.type = type;
        this.mContext = context;
        this.mOriginalItemList = mItemList;
    }

    @Override
    public ToDoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.landing_item_3, parent, false);

        return new ToDoRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ToDoRecyclerViewAdapter.MyViewHolder holder, int position) {
        ToDoChildModel childModel = mItemList.get(position);
        if(type==2)
        {
            holder.cource_mainrow.setVisibility(View.VISIBLE);
            CommonLandingData commonLandingData = childModel.getCommonLandingDataAssessment();
            holder.coursenametext.setVisibility(View.GONE);
            holder.rowbanner_image.setImageBitmap(null);
            holder.rowicon_image.setImageBitmap(null);
            OustSdkTools.setLayoutBackgroudDrawable(holder.main_card_ll, OustSdkTools.getImgDrawable(R.drawable.roundedcorner_litegrey));
            if ((commonLandingData.getIcon() != null) && (!commonLandingData.getIcon().isEmpty()) && (!commonLandingData.getIcon().equalsIgnoreCase("null"))) {
                holder.rowbanner_image.setVisibility(View.GONE);
                holder.rowicon_image.setVisibility(View.VISIBLE);
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(commonLandingData.getIcon()).into(holder.rowicon_image);
                } else {
                    Picasso.get().load(commonLandingData.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowicon_image);
                }
            } else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty())) {
                holder.rowicon_image.setVisibility(View.GONE);
                holder.rowbanner_image.setVisibility(View.VISIBLE);
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(commonLandingData.getBanner()).into(holder.rowbanner_image);
                } else {
                    Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                }
            }

            if (commonLandingData.getName() != null) {
                holder.coursenametext.setVisibility(View.VISIBLE);
                holder.coursenametext.setText(commonLandingData.getName());
            } else {
                holder.coursenametext.setVisibility(View.VISIBLE);
                holder.coursenametext.setText("");
            }
            if (commonLandingData.getEnrollCount() > 0) {
                holder.total_enrolled_count.setText("" + commonLandingData.getEnrollCount());
            } else {
                holder.total_enrolled_count.setText("0");
            }
            if (commonLandingData.getOc() > 0) {
                holder.totaloc_text.setText("" + commonLandingData.getOc());
            } else {
                holder.totaloc_text.setText("0");
            }


            holder.module_progress_ll.setVisibility(View.GONE);

            if (commonLandingData.getCompletionPercentage() > 0) {
                holder.moduleprogress.setText("" + commonLandingData.getCompletionPercentage() + "%\nCompleted");
            } else {
                holder.moduleprogress.setText("0%\nCompleted");
            }

        }
        if(type==3)
        {
            Log.d(TAG, "onBindViewHolder: itemsize:"+mItemList.size());
            holder.cource_mainrow.setVisibility(View.VISIBLE);
            CommonLandingData commonLandingData = childModel.getCommonLandingDataCourse();
            holder.coursenametext.setVisibility(View.GONE);
            holder.rowbanner_image.setImageBitmap(null);
            holder.rowicon_image.setImageBitmap(null);
            OustSdkTools.setLayoutBackgroudDrawable(holder.main_card_ll, OustSdkTools.getImgDrawable(R.drawable.roundedcorner_litegrey));
            if ((commonLandingData.getIcon() != null) && (!commonLandingData.getIcon().isEmpty()) && (!commonLandingData.getIcon().equalsIgnoreCase("null"))) {
                holder.rowbanner_image.setVisibility(View.GONE);
                holder.rowicon_image.setVisibility(View.VISIBLE);
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(commonLandingData.getIcon()).into(holder.rowicon_image);
                } else {
                    Picasso.get().load(commonLandingData.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowicon_image);
                }
            } else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty())) {
                holder.rowicon_image.setVisibility(View.GONE);
                holder.rowbanner_image.setVisibility(View.VISIBLE);
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(commonLandingData.getBanner()).into(holder.rowbanner_image);
                } else {
                    Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                }
            }
            if (commonLandingData.getName() != null) {
                holder.coursenametext.setVisibility(View.VISIBLE);
                holder.coursenametext.setText(commonLandingData.getName());
            } else {
                holder.coursenametext.setVisibility(View.VISIBLE);
                holder.coursenametext.setText("");
            }
            if (commonLandingData.getEnrollCount() > 0) {
                holder.total_enrolled_count.setText("" + commonLandingData.getEnrollCount());
            } else {
                holder.total_enrolled_count.setText("0");
            }
            if (commonLandingData.getOc() > 0) {
                holder.totaloc_text.setText("" + commonLandingData.getOc());
            } else {
                holder.totaloc_text.setText("0");
            }
                holder.module_progress_ll.setVisibility(View.VISIBLE);
            if (commonLandingData.getCompletionPercentage() > 0) {
                holder.moduleprogress.setText("" + commonLandingData.getCompletionPercentage() + "%\nCompleted");
            } else {
                holder.moduleprogress.setText("0%\nCompleted");
            }
        }
        holder.cource_mainrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (type == 2) {
                        ((TodoListActivity) mContext).launchActivity(mItemList.get(holder.getAdapterPosition()).getCommonLandingDataAssessment());
                    } else if (type == 3) {
                        ((TodoListActivity) mContext).launchActivity(mItemList.get(holder.getAdapterPosition()).getCommonLandingDataCourse());

                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    Log.d(TAG, "onItemClicked: "+e.getLocalizedMessage());
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        try {
            return mItemList.size();
        }catch (Exception e)
        {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d(TAG, "getItemCount: "+e.getLocalizedMessage());
            return 0;
        }
    }

}
