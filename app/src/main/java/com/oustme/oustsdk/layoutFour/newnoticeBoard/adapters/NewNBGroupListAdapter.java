package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBMembersList;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBGroupData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.List;

public class NewNBGroupListAdapter extends RecyclerView.Adapter<NewNBGroupListAdapter.MyViewholder> {
    private final String TAG = "NewNBGroupListAdapter";
    List<NewNBGroupData> nbGroupDataList;
    Context context;
    private long nbId = 0;

    public NewNBGroupListAdapter(NewNBMembersList newNBMembersList, ArrayList<NewNBGroupData> nbGroupDataList) {
        this.context = newNBMembersList;
        this.nbGroupDataList = nbGroupDataList;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_mem_list_item_2, parent, false);
        return new MyViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        try {
            NewNBGroupData nbGroupDataList1 = nbGroupDataList.get(position);
//            final NewNBPostData nbPostData = nbPostDataList.get(position);

//            holder.user_name.setText(nbGroupDataList.get(position).getGroupName());
//            holder.department.setText(nbGroupDataList.get(position).getCreatorId());

            holder.user_name.setText(nbGroupDataList1.getGroupName());
            holder.department.setText(nbGroupDataList1.getCreatorId());

            holder.main_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewNBMembersList.class);
                    intent.putExtra("groupId", nbGroupDataList.get(position).getGroupId());
                    intent.putExtra("nbId", nbId);
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return nbGroupDataList.size();
    }

    public void setNbId(long nbId) {
        this.nbId = nbId;
    }

    public static class MyViewholder extends RecyclerView.ViewHolder {
        LinearLayout main_ll;
        TextView user_name, department;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            department = itemView.findViewById(R.id.department);
            main_ll = itemView.findViewById(R.id.main_ll);
        }
    }
}
