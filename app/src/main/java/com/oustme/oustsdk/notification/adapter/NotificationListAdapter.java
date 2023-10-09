package com.oustme.oustsdk.notification.adapter;

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
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewModel> implements Filterable {

    private Context context;
    private ArrayList<NotificationResponse> notificationResponses;
    private ArrayList<NotificationResponse> tempList;
    private Filter fRecords;
    private SelectNotification mListener;


    public void setNotificationListAdapter(ArrayList<NotificationResponse> notificationResponseHashMap,
                                           Context applicationContext, SelectNotification selectNotification) {
        this.context = applicationContext;
        this.notificationResponses = notificationResponseHashMap;
        this.tempList = notificationResponseHashMap;
        this.mListener = selectNotification;
    }

    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list, parent, false);

        return new ViewModel(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewModel holder, int position) {
        holder.bindView(tempList.get(position));
    }

    @Override
    public int getItemCount() {
        return tempList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeItem(int position) {
        tempList.remove(position);
        notifyDataSetChanged();
    }

    public NotificationResponse getData(int position) {
        return tempList.get(position);
    }


    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e("TAG", "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = notificationResponses;
                filterResults.count = notificationResponses.size();
            } else {
                ArrayList<NotificationResponse> list = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (NotificationResponse item : notificationResponses) {
                    if (item.getMessage().toLowerCase().contains(constraint.toString()) ||
                            item.getTitle().toLowerCase().contains(constraint.toString())) {
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
            tempList = (ArrayList<NotificationResponse>) results.values;
            if (mListener != null) {
                mListener.searchModuleCount(tempList);
            }
            notifyDataSetChanged();
        }
    }


    public class ViewModel extends RecyclerView.ViewHolder {
        private final TextView heading;
        private final TextView body;
        private final TextView date;
        private final CardView notificationCardView;

        public ViewModel(@NonNull View itemView) {
            super(itemView);
            notificationCardView = itemView.findViewById(R.id.notification_card_view);
            heading = itemView.findViewById(R.id.notification_heading);
            body = itemView.findViewById(R.id.notification_body);
            date = itemView.findViewById(R.id.notification_date);
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
        public void bindView(NotificationResponse notificationResponse) {
            if (notificationResponse.getRead()) {
                notificationCardView.setCardElevation(4);
                heading.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationCardView.setBackground(context.getDrawable(R.drawable.bg_dialog_rounded_padded_gray));
                } else {
                    notificationCardView.setBackgroundColor(OustSdkTools.getColorBack(R.color.notification_bg_gray));
                }
            } else {
                notificationCardView.setCardElevation(4);
                heading.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationCardView.setBackground(context.getDrawable(R.drawable.bg_dialog_rounded_padded_white));
                } else {
                    notificationCardView.setBackgroundColor(OustSdkTools.getColorBack(R.color.white));
                }
            }
            heading.setText(Html.fromHtml(notificationResponse.getTitle()));
            body.setText(Html.fromHtml(notificationResponse.getMessage()));
            String convertedDate;
            if (notificationResponse.getUpdateTime() != null) {
                convertedDate = convertDate(notificationResponse.getUpdateTime(), "MMM dd, yyyy hh:mm aa"); /*yyyy-MM-dd HH:mm:ss*/
            } else {
                long time = System.currentTimeMillis();
                convertedDate = convertDate(String.valueOf(time), "yyyy-MM-dd hh:mm aa");
            }
            date.setText(convertedDate);

            notificationCardView.setOnClickListener(view1 -> {
                if (notificationResponse.getFireBase()) {
                    RoomHelper.updateReadFireBaseNotificationData("false", notificationResponse.getKey());
                } else {
                    RoomHelper.updateNotificationReadData("false", notificationResponse.getContentId());
                }
                notifyDataSetChanged();
                mListener.selectedPosition(notificationResponse);
            });
        }
    }

    public interface SelectNotification {
        void selectedPosition(NotificationResponse notificationResponse);

        void searchModuleCount(ArrayList<NotificationResponse> userMessageLists);
    }

}
