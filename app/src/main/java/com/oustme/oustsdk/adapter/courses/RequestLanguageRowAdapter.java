package com.oustme.oustsdk.adapter.courses;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;

import java.util.ArrayList;

public class RequestLanguageRowAdapter extends RecyclerView.Adapter<RequestLanguageRowAdapter.MyViewHolder> {

    private RowClickCallBack rowClickCallBack;
    private ArrayList<String> localcountrylang=new ArrayList<String>();
    private int lastSelectedPosition=-1;
    public RequestLanguageRowAdapter(ArrayList<String> localcountrylang,RowClickCallBack rowClickCallBack) {
        this.localcountrylang=localcountrylang;
        this.rowClickCallBack=rowClickCallBack;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.requestlang_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.lang_text.setText(localcountrylang.get(position));
        holder.select_lang.setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return localcountrylang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout requestlang_main_layout;
        private RadioButton select_lang;
        private TextView lang_text;
        public MyViewHolder(View itemView) {
            super(itemView);
            requestlang_main_layout= itemView.findViewById(R.id.requestlang_main_layout);
            select_lang= itemView.findViewById(R.id.select_lang);
            lang_text= itemView.findViewById(R.id.lang_text);

            select_lang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    rowClickCallBack.onMainRowClick(localcountrylang.get(lastSelectedPosition),lastSelectedPosition);
                }
            });
        }
    }
}
