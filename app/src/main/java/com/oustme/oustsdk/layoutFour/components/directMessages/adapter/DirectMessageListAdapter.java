package com.oustme.oustsdk.layoutFour.components.directMessages.adapter;

import static com.oustme.oustsdk.util.AchievementUtils.convertDate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.components.directMessages.DirectMessageActivity;
import com.oustme.oustsdk.layoutFour.data.response.directMessageResponse.UserMessageList;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;

public class DirectMessageListAdapter extends RecyclerView.Adapter<DirectMessageListAdapter.ViewModel> implements Filterable {

    ArrayList<UserMessageList> userMessageLists = new ArrayList<>();
    ArrayList<UserMessageList> tempUserMessageLists = new ArrayList<>();
    Context context;
    SelectInBoxPosition selectInBoxPosition;
    SearchModuleCount searchModuleCount;
    private Filter fRecords;

    public void setInBoxListAdapter(ArrayList<UserMessageList> inboxDataResponses, Context applicationContext, SearchModuleCount searchModuleCount) {
        this.userMessageLists = inboxDataResponses;
        this.tempUserMessageLists = inboxDataResponses;
        this.context = applicationContext;
        this.selectInBoxPosition = new DirectMessageActivity();
        this.searchModuleCount = searchModuleCount;
    }

    @Override
    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e("TAG", "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = userMessageLists;
                filterResults.count = userMessageLists.size();
            } else {
                ArrayList<UserMessageList> list = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (UserMessageList item : userMessageLists) {
                    if (item.getMessageTitle().toLowerCase().contains(constraint.toString()) ||
                            item.getMessageTitle().toLowerCase().contains(constraint.toString())) {
                        list.add(item);
                    }
                    filterResults.count = list.size();
                    filterResults.values = list;
                }
            }
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tempUserMessageLists = (ArrayList<UserMessageList>) results.values;
            if (searchModuleCount != null) {
                searchModuleCount.searchModuleCount(tempUserMessageLists);
            }
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.direct_message_list, parent, false);

        return new ViewModel(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewModel holder, int position) {
        holder.bingView(tempUserMessageLists.get(position));
    }

    @Override
    public int getItemCount() {
        return tempUserMessageLists.size();
    }

    public class ViewModel extends RecyclerView.ViewHolder {
        private final CardView inBoxCardView;
        private final TextView inBoxHeading;
        private final TextView inBoxDate;
        private final TextView inBoxBody;

        public ViewModel(@NonNull View itemView) {
            super(itemView);
            inBoxCardView = itemView.findViewById(R.id.inBox_card_view);
            inBoxHeading = itemView.findViewById(R.id.inBox_heading);
            inBoxDate = itemView.findViewById(R.id.inBox_date);
            inBoxBody = itemView.findViewById(R.id.inBox_body);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bingView(UserMessageList userMessageList) {
            try {
                if (userMessageList.getRead()) {
                    inBoxCardView.setCardElevation(4);
                    inBoxHeading.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        inBoxCardView.setBackground(context.getDrawable(R.drawable.bg_dialog_rounded_padded_white));
                    } else {
                        inBoxCardView.setBackgroundColor(OustSdkTools.getColorBack(R.color.white));
                    }
                } else {
                    inBoxCardView.setCardElevation(4);
                    inBoxHeading.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        inBoxCardView.setBackground(context.getDrawable(R.drawable.bg_dialog_rounded_padded_gray));
                    } else {
                        inBoxCardView.setBackgroundColor(OustSdkTools.getColorBack(R.color.notification_bg_gray));
                    }
                }
                inBoxHeading.setText(Html.fromHtml(userMessageList.getMessageTitle()));
                inBoxBody.setText(Html.fromHtml(userMessageList.getMessageBody()));
                String convertedDate;
                if (userMessageList.getUpdatedAt() != null) {
                    convertedDate = convertDate(String.valueOf(userMessageList.getUpdatedAt()), "MMM dd, yyyy hh:mm aa"); /*yyyy-MM-dd HH:mm:ss*/
                } else if (userMessageList.getCreatedAt() != null) {
                    convertedDate = convertDate(String.valueOf(userMessageList.getCreatedAt()), "MMM dd, yyyy hh:mm aa"); /*yyyy-MM-dd HH:mm:ss*/
                } else {
                    long time = System.currentTimeMillis();
                    convertedDate = convertDate(String.valueOf(time), "MMM dd, yyyy hh:mm aa"); /*yyyy-MM-dd HH:mm:aa*/
                }
                inBoxDate.setText(convertedDate);

                inBoxCardView.setOnClickListener(view1 -> {
                    selectInBoxPosition.selectInBoxPosition(userMessageList);
                    selectInBoxPosition.redirectToMessageDetailScreen(userMessageList);
                });
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public interface SelectInBoxPosition {
        void selectInBoxPosition(UserMessageList userMessageList);

        void redirectToMessageDetailScreen(UserMessageList userMessageList);
    }

    public interface SearchModuleCount {
        void searchModuleCount(ArrayList<UserMessageList> userMessageLists);
    }
}

