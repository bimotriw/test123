package com.oustme.oustsdk.question_module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;

public class CategoryAnswersAdapter extends RecyclerView.Adapter<CategoryAnswersAdapter.ViewHolder> {
    Context context;
    ArrayList<String> leftData = new ArrayList<>();
    ArrayList<String> leftDataType = new ArrayList<>();
    ArrayList<String> rightData = new ArrayList<>();
    ArrayList<String> rightDataType = new ArrayList<>();
    int tempPosition = 0;
    int tempRightPosition = 0;
    int totalCount = 0;

    public CategoryAnswersAdapter(Context context, ArrayList<String> leftData, ArrayList<String> rightData, ArrayList<String> leftDataType, ArrayList<String> rightDataType) {
        this.context = context;
        this.leftData = leftData;
        this.leftDataType = leftDataType;
        this.rightData = rightData;
        this.rightDataType = rightDataType;
        this.totalCount = leftData.size() + rightData.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_answer, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (leftDataType.get(position).equalsIgnoreCase("IMAGE")) {
                holder.category_left_text.setVisibility(View.GONE);
                holder.category_left_image.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(leftData.get(position))
                        .into(holder.category_left_image);
            } else {
                holder.category_left_text.setVisibility(View.VISIBLE);
                holder.category_left_image.setVisibility(View.GONE);
                holder.category_left_text.setText(leftData.get(position));

            }

            if (rightDataType.get(position).equalsIgnoreCase("IMAGE")) {
                holder.category_right_text.setVisibility(View.GONE);
                holder.category_right_image.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(rightData.get(position))
                        .into(holder.category_right_image);
            } else {
                holder.category_right_text.setVisibility(View.VISIBLE);
                holder.category_right_image.setVisibility(View.GONE);
                holder.category_right_text.setText(rightData.get(position));

            }

            /*try {
                if (position >= 1) {
                    tempPosition++;
                    holder.left_count.setText(tempPosition + "/" + ((leftData.size() + rightData.size()) - 2));
                }else if(){

                }
                if (position >= (totalCount / 2)) {
                    tempPosition++;
                    holder.left_count.setText(tempPosition + "/" + ((leftData.size() + rightData.size()) - 2));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return leftData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout left_parent, right_parent;
        RelativeLayout category_left_layout, category_right_layout;
        TextView category_left_text, category_right_text, left_count, right_count;
        ImageView category_left_image, category_right_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            left_parent = itemView.findViewById(R.id.left_parent);
            right_parent = itemView.findViewById(R.id.right_parent);
            category_left_layout = itemView.findViewById(R.id.category_left_layout);
            category_right_layout = itemView.findViewById(R.id.category_right_layout);
            category_left_text = itemView.findViewById(R.id.category_left_text);
            category_right_text = itemView.findViewById(R.id.category_right_text);
            category_left_image = itemView.findViewById(R.id.category_left_image);
            category_right_image = itemView.findViewById(R.id.category_right_image);
            left_count = itemView.findViewById(R.id.left_count);
            right_count = itemView.findViewById(R.id.right_count);
        }
    }
}
