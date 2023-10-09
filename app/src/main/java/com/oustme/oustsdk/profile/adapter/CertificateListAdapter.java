package com.oustme.oustsdk.profile.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.profile.fragment.CertificatesFragment;
import com.oustme.oustsdk.profile.model.CertificateData;

import java.util.ArrayList;

import static com.oustme.oustsdk.util.AchievementUtils.convertDate;

public class CertificateListAdapter extends RecyclerView.Adapter<CertificateListAdapter.ViewHolder> implements Filterable {
    private ArrayList<CertificateData> certificateData;
    private ArrayList<CertificateData> tempList;
    private Context context;
    private Fragment fragment;
    private Filter fRecords;


    public void setCertificateListAdapter(ArrayList<CertificateData> certificate, Context context,
                                          CertificatesFragment certificatesFragment) {
        this.certificateData = certificate;
        this.context = context;
        this.fragment = certificatesFragment;
        this.tempList = certificate;
    }

    @Override
    public int getItemCount() {
        return tempList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.certificate_card_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(tempList.get(position));
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

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e("TAG", "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = certificateData;
                filterResults.count = certificateData.size();
            } else {
                ArrayList<CertificateData> list = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (CertificateData item : certificateData) {
                    if (item.getCourseName().toLowerCase().contains(constraint.toString())) {
                        list.add(item);
                    }
                    filterResults.count = list.size();
                    filterResults.values = list;
                }
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                tempList = (ArrayList<CertificateData>) results.values;
                notifyDataSetChanged();
                ((CertificatesFragment) fragment).searchCertificateNotFound("");
            } else {
                tempList = (ArrayList<CertificateData>) results.values;
                notifyDataSetChanged();
                ((CertificatesFragment) fragment).searchCertificateNotFound("notFound");
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView contentName;
        private TextView date;
        private ImageView imgDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.certificate_image);
            contentName = itemView.findViewById(R.id.content_name_certificate);
            date = itemView.findViewById(R.id.certificate_date);
            imgDownload = itemView.findViewById(R.id.certificate_download);
        }

        public void bindView(CertificateData certificateData) {
            Glide.with(context).load(certificateData.getCertificateThumbnail())
                    .error(context.getResources().getDrawable(R.drawable.trophy_cup_popup)).into(imageView);
            contentName.setText(certificateData.getCourseName());
            String convertedDate = convertDate(String.valueOf(certificateData.getCertificateDate()), "dd MMM yyyy");
            date.setText(convertedDate);
            imgDownload.setOnClickListener(view -> {
                imgDownload.setEnabled(false);
                new Handler().postDelayed(() -> imgDownload.setEnabled(true), 2000);
                ((CertificatesFragment) fragment).downloadCertificate(certificateData.getCertificateUrl(), certificateData.getCourseName());
            });

            imageView.setOnClickListener(view -> {
                ((CertificatesFragment) fragment).openCertificate(certificateData.getCertificateUrl(), certificateData.getCourseName());
            });
        }
    }
}

