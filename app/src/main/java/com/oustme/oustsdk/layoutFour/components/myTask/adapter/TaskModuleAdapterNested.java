package com.oustme.oustsdk.layoutFour.components.myTask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.LayoutType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TaskModuleAdapterNested extends RecyclerView.Adapter<TaskModuleAdapterNested.ViewHolder> {

    private boolean isAssending;
    private ArrayList<CommonLandingData> taskModuleList = new ArrayList<>();
    public Context context;
    private int type;
    HashMap<Long, List<CommonLandingData>> dataHashMap;
    private ArrayList<Long> keyDates;

    public TaskModuleAdapterNested(ArrayList<CommonLandingData> taskModuleList, Context context, int type, boolean isAssending) {
        this.taskModuleList = taskModuleList;
        this.context = context;
        this.type = type;
        this.isAssending = isAssending;

        getFormatedData(taskModuleList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_module_grid_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<CommonLandingData> commonLandingData = dataHashMap.get(keyDates.get(position));

        TaskModuleChildAdapter adapter = new TaskModuleChildAdapter();
        adapter.setTaskRecyclerAdapter((ArrayList<CommonLandingData>) commonLandingData, context, type);

        if (keyDates.get(position) != null) {
            try {
                long addedOn = keyDates.get(position);
                if (addedOn != 0) {
                    Date date = new Date(addedOn);
                    holder.tvDate.setText(new SimpleDateFormat("dd\nMMM", Locale.US).format(date));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        } else {
            holder.tvDate.setVisibility(View.INVISIBLE);
        }

        holder.rvDatedTask.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return keyDates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        //CardView cardDate;
        RecyclerView rvDatedTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            //cardDate = itemView.findViewById(R.id.card_date);
            rvDatedTask = itemView.findViewById(R.id.rv_dated_task);
            rvDatedTask.setLayoutManager(getLayoutManager());
            rvDatedTask.setNestedScrollingEnabled(false);
        }
    }

    private void getFormatedData(ArrayList<CommonLandingData> taskModuleList) {
        dataHashMap = new HashMap<>();

        for (CommonLandingData commonLandingData : taskModuleList) {
            long aLong = Long.parseLong(commonLandingData.getAddedOn());
            aLong = aLong / 1000 / 60 / 60 / 24;
            aLong = aLong * 1000 * 60 * 60 * 24;
            if (dataHashMap.containsKey(aLong)) {
                List<CommonLandingData> landingData = dataHashMap.get(aLong);
                landingData.add(commonLandingData);
            } else {
                List<CommonLandingData> landingData = new ArrayList<>();
                landingData.add(commonLandingData);
                dataHashMap.put(aLong, landingData);
            }
        }
        keyDates = new ArrayList<Long>(dataHashMap.keySet());
        Collections.sort(keyDates, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (isAssending)
                    return (int) (o2 - o1);
                else
                    return (int) (o1 - o2);
            }
        });

    }


    private LinearLayoutManager getLayoutManager() {
        LinearLayoutManager mLayoutManager;
        switch (type) {
            case LayoutType.GRID:
                mLayoutManager = new GridLayoutManager(context, 2);
                break;
            default:
                mLayoutManager = new LinearLayoutManager(context);
                break;
        }

        return mLayoutManager;
    }
}
