package com.oustme.oustsdk.adapter.FFContest;

import android.content.Intent;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseQuestionResponse;
import com.oustme.oustsdk.firebase.FFContest.FFCFirebaseResponse;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 04/08/17.
 */

public class FFContestLBAdaptor extends RecyclerView.Adapter<FFContestLBAdaptor.MyViewHolder> {
    List<FFCFirebaseResponse> ffcFirebaseResponseList;
    List<FFCFirebaseQuestionResponse> ffcFirebaseQuestionResponseList;
    private boolean isQuestionLB=false;
    private String textColor;
    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    private RowClickCallBack rowClickCallBack;
    public void setRowClickCallBack(RowClickCallBack rowClickCallBack) {
        this.rowClickCallBack = rowClickCallBack;
    }

    public FFContestLBAdaptor(List<FFCFirebaseResponse> ffcFirebaseResponseList, List<FFCFirebaseQuestionResponse> ffcFirebaseQuestionResponseList, boolean isQuestionLB) {
        this.ffcFirebaseResponseList = ffcFirebaseResponseList;
        this.ffcFirebaseQuestionResponseList=ffcFirebaseQuestionResponseList;
        this.isQuestionLB=isQuestionLB;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout ffclbrow_indexlayout,ffclb_bestresultlayout,ffclb_myresultlayout,ffclb_mainlayout,ffclb_submainlayout;
        private TextView ffclbrow_index,ffclbrow_bestusername,ffclbrow_myname,ffclbrow_mytime,ffclbrow_bestusertime,ffclbrow_bestusertimelabel,nouser_text,
                ffclbrow_mytimelabel,ffclbrow_bestusernamea;
        private ImageView ffclb_bestuseravatar,ffclb_myavatar,ffclb_mylbicon;
        public MyViewHolder(View view) {
            super(view);
            ffclbrow_indexlayout= view.findViewById(R.id.ffclbrow_indexlayout);
            ffclb_bestresultlayout= view.findViewById(R.id.ffclb_bestresultlayout);
            ffclb_myresultlayout= view.findViewById(R.id.ffclb_myresultlayout);
            ffclbrow_bestusernamea= view.findViewById(R.id.ffclbrow_bestusernamea);

            ffclbrow_index= view.findViewById(R.id.ffclbrow_index);
            ffclbrow_bestusername= view.findViewById(R.id.ffclbrow_bestusername);
            ffclbrow_myname= view.findViewById(R.id.ffclbrow_myname);
            ffclbrow_mytime= view.findViewById(R.id.ffclbrow_mytime);

            ffclb_bestuseravatar= view.findViewById(R.id.ffclb_bestuseravatar);
            ffclb_myavatar= view.findViewById(R.id.ffclb_myavatar);
            ffclbrow_bestusertime= view.findViewById(R.id.ffclbrow_bestusertime);
            ffclb_mainlayout= view.findViewById(R.id.ffclb_mainlayout);
            ffclb_submainlayout= view.findViewById(R.id.ffclb_submainlayout);
            ffclbrow_bestusertimelabel= view.findViewById(R.id.ffclbrow_bestusertimelabel);
            nouser_text= view.findViewById(R.id.nouser_text);
            ffclb_mylbicon= view.findViewById(R.id.ffclb_mylbicon);
            ffclbrow_mytimelabel= view.findViewById(R.id.ffclbrow_mytimelabel);
            OustSdkTools.setImage(ffclb_mylbicon, OustSdkApplication.getContext().getResources().getString(R.string.trophyyello));

            ffclbrow_bestusertimelabel.setText(OustStrings.getString("sec"));

            if ((textColor!= null) && (!textColor.isEmpty())) {
                int color = Color.parseColor(textColor);
                ffclbrow_index.setTextColor(color);
                ffclbrow_bestusername.setTextColor(color);
                ffclbrow_myname.setTextColor(color);
                ffclbrow_mytime.setTextColor(color);
                ffclbrow_bestusertime.setTextColor(color);
                ffclbrow_bestusertimelabel.setTextColor(color);
                nouser_text.setTextColor(color);
                ffclbrow_mytimelabel.setTextColor(color);
            }

        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ffclb_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            holder.ffclbrow_index.setText(""+(position+1));
            if(!isQuestionLB) {
                //overall leaderboard
                holder.ffclb_myresultlayout.setVisibility(View.GONE);
                holder.ffclb_bestresultlayout.setVisibility(View.VISIBLE);
                if((ffcFirebaseResponseList!=null)&&(ffcFirebaseResponseList.size()>position)&&(ffcFirebaseResponseList.get(position)!=null)) {
                    setLBdata(holder.ffclb_bestresultlayout,holder.ffclb_bestuseravatar,holder.ffclbrow_bestusernamea,holder.ffclbrow_bestusertime,
                            ffcFirebaseResponseList.get(position).getAvatar(),ffcFirebaseResponseList.get(position).getDisplayName(),
                            ffcFirebaseResponseList.get(position).getAverageTime());
                    holder.ffclbrow_bestusertime.setText("");
                    holder.ffclbrow_bestusername.setVisibility(View.GONE);
                    holder.ffclbrow_bestusertime.setVisibility(View.GONE);
                    holder.ffclbrow_bestusertimelabel.setVisibility(View.GONE);
                    holder.ffclbrow_bestusernamea.setVisibility(View.VISIBLE);
                }
                if(ffcFirebaseResponseList.get(position).getUserId().equals(OustAppState.getInstance().getActiveUser().getStudentid())){
                    OustSdkTools.setLayoutBackgroud(holder.ffclb_mainlayout, R.color.reda);
                }else {
                    OustSdkTools.setLayoutBackgroud(holder.ffclb_mainlayout, R.color.QuizBgGraya);
                }
                holder.ffclb_bestuseravatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoUserProfile( ffcFirebaseResponseList.get(position).getUserId(),ffcFirebaseResponseList.get(position).getDisplayName(),
                                ffcFirebaseResponseList.get(position).getAvatar());
                    }
                });
            }else {
                holder.ffclbrow_mytimelabel.setText(OustStrings.getString("sec"));
                holder.ffclb_mylbicon.setVisibility(View.GONE);
                if((ffcFirebaseQuestionResponseList!=null)&&(ffcFirebaseQuestionResponseList.size()>position)&&(ffcFirebaseQuestionResponseList.get(position)!=null)) {
                    if(ffcFirebaseQuestionResponseList.get(position).getUserId()!=null) {
                        holder.nouser_text.setVisibility(View.GONE);
                        setLBdata(holder.ffclb_bestresultlayout, holder.ffclb_bestuseravatar, holder.ffclbrow_bestusername, holder.ffclbrow_bestusertime,
                                ffcFirebaseQuestionResponseList.get(position).getAvatar(),
                                ffcFirebaseQuestionResponseList.get(position).getDisplayName(),
                                ffcFirebaseQuestionResponseList.get(position).getReponseTime());
                        holder.ffclb_bestuseravatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gotoUserProfile( ffcFirebaseQuestionResponseList.get(position).getUserId(),ffcFirebaseQuestionResponseList.get(position).getDisplayName(),
                                        ffcFirebaseQuestionResponseList.get(position).getAvatar());
                            }
                        });
                        holder.ffclb_myresultlayout.setVisibility(View.GONE);
                        FFCFirebaseResponse ffcFirebaseResponse=ffcFirebaseResponseList.get(position);

                        if ((ffcFirebaseResponse!= null)&&(ffcFirebaseResponse.getUserId()!=null)) {
                            if (ffcFirebaseResponse.getUserId().equalsIgnoreCase(ffcFirebaseQuestionResponseList.get(position).getUserId())) {
                                //if user is topper
                                OustSdkTools.setLayoutBackgroud(holder.ffclb_mainlayout, R.color.light_red);
                                OustSdkTools.setLayoutBackgroud(holder.ffclb_submainlayout,R.color.light_red);
                                OustSdkTools.setLayoutBackgroud(holder.ffclbrow_indexlayout, R.color.light_red);
                                holder.ffclb_mylbicon.setVisibility(View.VISIBLE);
                            } else {
                                holder.ffclb_myresultlayout.setVisibility(View.VISIBLE);
                                setLBdata(holder.ffclb_myresultlayout, holder.ffclb_myavatar, holder.ffclbrow_myname, holder.ffclbrow_mytime,
                                        ffcFirebaseResponse.getAvatar(),
                                        ffcFirebaseResponse.getDisplayName(),
                                        ffcFirebaseResponse.getAverageTime());
                                if(!ffcFirebaseResponse.isCorrect()){
                                    if((ffcFirebaseResponse.getAnswer()!=null)&&(!ffcFirebaseResponse.getAnswer().isEmpty())) {
                                        holder.ffclbrow_mytime.setText(OustStrings.getString("wrong"));
                                    }else {
                                        holder.ffclbrow_mytime.setText(OustStrings.getString("not_attempted"));
                                    }
                                    holder.ffclbrow_mytime.setTextColor(OustSdkTools.getColorBack(R.color.clear_red));
                                    holder.ffclbrow_mytime.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                                    holder.ffclbrow_mytimelabel.setText("");
                                }
                            }
                        }
                    }else {
                        holder.ffclb_myresultlayout.setVisibility(View.GONE);
                        holder.ffclb_bestresultlayout.setVisibility(View.GONE);
                        holder.nouser_text.setVisibility(View.VISIBLE);
                    }
                }
                holder.ffclb_mainlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rowClickCallBack.onMainRowClick("",position);
                    }
                });
            }
        }catch(Exception e) {
        }
    }


    private void setLBdata(RelativeLayout layout,ImageView imageView,TextView nameText, TextView timeText ,String imgStr,String name,long time){
        if ((imgStr!= null)&&(!imgStr.isEmpty())&&(!imgStr.contains("null"))) {
            OustSdkTools.setImage(imageView,OustSdkApplication.getContext().getResources().getString(R.string.maleavatar));
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(imgStr).into(imageView);
            } else {
                Picasso.get().load(imgStr).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
            }
        }else {
            OustSdkTools.setImage(imageView,OustSdkApplication.getContext().getResources().getString(R.string.maleavatar));
        }
        if (name!= null) {
            nameText.setText(name);
        }
        if (time > 0) {
            layout.setVisibility(View.VISIBLE);
            timeText.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            if((textColor!=null)&&(!textColor.isEmpty())){
                int color = Color.parseColor(textColor);
                timeText.setTextColor(color);
            }else {
                timeText.setTextColor(OustSdkTools.getColorBack(R.color.whitelight));
            }
//            timeText.setText());
            String s1=String.format("%03d",(time%1000));
            timeText.setText("" + (time/ 1000) + "." +s1);
        }else {
            timeText.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if(isQuestionLB){
            if (ffcFirebaseQuestionResponseList == null) {
                return 0;
            }
            return ffcFirebaseQuestionResponseList.size();
        }else {
            if (ffcFirebaseResponseList == null) {
                return 0;
            }
            return ffcFirebaseResponseList.size();
        }
    }

    private void gotoUserProfile(String studentId,String name, String avatar){
        try {
            if (!studentId.equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), UserProfileActivity.class);
                intent.putExtra("avatar",avatar);
                intent.putExtra("name", name);
                intent.putExtra("studentId", studentId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            }
        }catch (Exception e){}
    }

    public void dataChanged(List<FFCFirebaseResponse> ffcFirebaseResponseList, List<FFCFirebaseQuestionResponse> ffcFirebaseQuestionResponseList){
        try {
            this.ffcFirebaseResponseList = ffcFirebaseResponseList;
            this.ffcFirebaseQuestionResponseList=ffcFirebaseQuestionResponseList;
            notifyDataSetChanged();
        }catch (Exception e){
        }
    }
}
