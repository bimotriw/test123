package com.oustme.oustsdk.question_module.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.question_module.fragment.DropDownQuestionFragment;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;

public class DropDownListAdapter extends RecyclerView.Adapter<DropDownListAdapter.ViewModel> implements Filterable {

    private Context context;
    ValueFilter valueFilter;
    DropDownListListener dropDownListListener;
    boolean isReviewMode = false;
    boolean isExamMode = false;
    boolean isFirstTime = true;
    Scores scores;
    private ArrayList<String> dropDownList = new ArrayList<>();
    private ArrayList<String> tempDropDownList = new ArrayList<>();
    private ArrayList<String> totalDropDownList = new ArrayList<>();

    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bottom_filter_dialog_list_dialog_item, parent, false);

        return new ViewModel(itemView);
    }

    public void setData(ArrayList<String> tempDropDownList, Context context, DropDownQuestionFragment dropDownQuestionFragment, boolean isReviewMode) {
        this.context = context;
        this.dropDownListListener = dropDownQuestionFragment;
        this.tempDropDownList = tempDropDownList;
        this.dropDownList = tempDropDownList;
        this.isReviewMode = isReviewMode;
    }

    public void setTotalData(ArrayList<String> dropDownList, Context context, Scores score) {
        this.totalDropDownList = dropDownList;
        this.context = context;
        this.scores = score;
    }

    public void setExamMode(boolean isExamMode) {
        this.isExamMode = isExamMode;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewModel holder, int position) {
        holder.onBindView(tempDropDownList.get(position));

        if (tempDropDownList.get(position) != null && !tempDropDownList.get(position).isEmpty()) {
            holder.txt.setText("" + tempDropDownList.get(position));

            if (scores != null && isExamMode && isFirstTime) {
                if (scores.getAnswer() != null && scores.getAnswer().equals(tempDropDownList.get(position))) {
                    holder.txt.setTypeface(null, Typeface.BOLD);
                    holder.optionChoose.setVisibility(View.VISIBLE);
                    holder.optionChoose.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_filled_tick));
                } else {
                    holder.txt.setTypeface(null, Typeface.NORMAL);
                    holder.txt.setTextColor(context.getResources().getColor(R.color.textBlack));
                    holder.optionChoose.setVisibility(View.GONE);
                }

            }

            if (!isFirstTime) {
                if (OustStaticVariableHandling.getInstance().getSortPosition() == position) {
                    if (holder.optionChoose.getVisibility() == View.VISIBLE) {
                        holder.txt.setTypeface(null, Typeface.NORMAL);
                        holder.txt.setTextColor(context.getResources().getColor(R.color.textBlack));
                        holder.optionChoose.setVisibility(View.GONE);
                    } else {
                        holder.txt.setTypeface(null, Typeface.BOLD);
                        holder.optionChoose.setVisibility(View.VISIBLE);
                        holder.optionChoose.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_filled_tick));
                    }
                } else {
                    holder.txt.setTypeface(null, Typeface.NORMAL);
                    holder.txt.setTextColor(context.getResources().getColor(R.color.textBlack));
                    holder.optionChoose.setVisibility(View.GONE);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return tempDropDownList.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<String> filterList = new ArrayList<>();
                for (int i = 0; i < totalDropDownList.size(); i++) {
                    if ((totalDropDownList.get(i).toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(totalDropDownList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = dropDownList.size();
                results.values = dropDownList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            tempDropDownList = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }

    public class ViewModel extends RecyclerView.ViewHolder {
        TextView txt;
        ImageView optionChoose;

        public ViewModel(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.dialog_option);
            optionChoose = itemView.findViewById(R.id.dialog_option_chosen);
        }

        public void onBindView(String text) {

            itemView.setOnClickListener(v -> {
                try {
                    isFirstTime = false;
                    if (OustStaticVariableHandling.getInstance().getSortPosition() >= 0) {
                        notifyItemChanged(OustStaticVariableHandling.getInstance().getSortPosition());
                    }
                    OustStaticVariableHandling.getInstance().setSortPosition(getAdapterPosition());
                    notifyItemChanged(OustStaticVariableHandling.getInstance().getSortPosition());
                    if (dropDownListListener != null) {
                        dropDownListListener.onItemClicked(text);
                        OustStaticVariableHandling.getInstance().setSortPosition(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            });
        }
    }

    public interface DropDownListListener {
        void onItemClicked(String position);
    }
}
