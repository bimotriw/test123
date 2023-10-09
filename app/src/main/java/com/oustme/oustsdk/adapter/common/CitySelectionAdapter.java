package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.todoactivity.CitySelection;
import com.oustme.oustsdk.request.CityDataModel;

import java.util.ArrayList;

public class CitySelectionAdapter extends RecyclerView.Adapter<CitySelectionAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CityDataModel> cityDataModels;
    int row_index = -1;
    public CitySelectionAdapter(Context context, ArrayList<CityDataModel> cityDataModels){
        this.context = context;
        this.cityDataModels = cityDataModels;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CityDataModel cityDataModel = cityDataModels.get(position);
        if(cityDataModel !=null && cityDataModel.getName()!=null ){
            holder.textView_city.setText(cityDataModel.getName());
        }

        holder.layout_city_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof CitySelection) {
                    ((CitySelection)context).citySelectionCall(position, cityDataModel);
                    row_index=position;
                    holder.textView_city.setBackgroundColor(context.getResources().getColor(R.color.black_transparent));
                    holder.textView_city.setTextColor(context.getResources().getColor(R.color.whitea));
                    notifyDataSetChanged();
                }
            }
        });

        if(row_index==position){
            holder.layout_city_item.setBackgroundColor(context.getResources().getColor(R.color.black_transparent));
            holder.textView_city.setBackgroundColor(context.getResources().getColor(R.color.black_transparent));
            holder.textView_city.setTextColor(context.getResources().getColor(R.color.whitea));
        } else {
            holder.layout_city_item.setBackgroundColor(context.getResources().getColor(R.color.DarkGray_a));
            holder.textView_city.setBackgroundColor(context.getResources().getColor(R.color.whitea));
            holder.textView_city.setTextColor(context.getResources().getColor(R.color.black_transparent));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return cityDataModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_city;
        private ConstraintLayout layout_city_item;
        public MyViewHolder(View view) {
            super(view);
            textView_city = view.findViewById(R.id.textViewCity);
            layout_city_item = view.findViewById(R.id.layout_city_item);
        }
    }

}
