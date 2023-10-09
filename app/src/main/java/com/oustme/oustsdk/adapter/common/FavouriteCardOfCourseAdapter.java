package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.CardClickCallBack;
import com.oustme.oustsdk.response.common.FavouriteCardsCourseData;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by shilpysamaddar on 15/05/17.
 */

public class FavouriteCardOfCourseAdapter extends RecyclerView.Adapter<FavouriteCardOfCourseAdapter.MyViewHolder> {

    List<FavCardDetails> favCardDetailsList;
    String courseName,courseId;
    private CardClickCallBack cardClickCallBack;


    public FavouriteCardOfCourseAdapter(List<FavCardDetails> favCardDetailsList, String courseName, String courseId) {
        this.favCardDetailsList=favCardDetailsList;
        this.courseName=courseName;
        this.courseId=courseId;
    }

    public void setCardClickCallBack(CardClickCallBack cardClickCallBack){
        this.cardClickCallBack=cardClickCallBack;
    }

    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.favouritecard, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if(favCardDetailsList.get(position).isRMCard()){
            holder.vidieo_image.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.card_learningimage.setVisibility(View.VISIBLE);
            if(favCardDetailsList.get(position).getRmType().equalsIgnoreCase("PDF")){
                OustSdkTools.setImage(holder.card_learningimage, OustSdkApplication.getContext().getResources().getString(R.string.pdf));
            } if(favCardDetailsList.get(position).getRmType().equalsIgnoreCase("URL_LINK")){
                OustSdkTools.setImage(holder.card_learningimage, OustSdkApplication.getContext().getResources().getString(R.string.url));
            } if(favCardDetailsList.get(position).getRmType().equalsIgnoreCase("IMAGE")){
                OustSdkTools.setImage(holder.card_learningimage, OustSdkApplication.getContext().getResources().getString(R.string.rm_imagetype));
            }
            holder.card_text.setText(OustStrings.getString("read_more"));
        } else{
            boolean isImagePresent = false;
            holder.imageView.setVisibility(View.GONE);
            if ((favCardDetailsList.get(position).getImageUrl() != null) && (!favCardDetailsList.get(position).getImageUrl().isEmpty())) {
                isImagePresent = setGifImage("oustlearn_" + favCardDetailsList.get(position).getImageUrl(), holder.imageView);
            }
            if ((favCardDetailsList.get(position).getCardTitle() != null) && (!favCardDetailsList.get(position).getCardTitle().isEmpty())) {
                holder.card_text.setText(favCardDetailsList.get(position).getCardTitle());
            }
            if ((favCardDetailsList.get(position).isVideo() || ((favCardDetailsList.get(position).getMediaType()!=null)&&(favCardDetailsList.get(position).getMediaType()).equalsIgnoreCase("VIDEO"))) && ((favCardDetailsList.get(position).getImageUrl() != null) || (!favCardDetailsList.isEmpty()))) {
                holder.card_learningimage.setVisibility(View.GONE);
                holder.vidieo_image.setVisibility(View.VISIBLE);
                setThumbnailImage(favCardDetailsList.get(position).getImageUrl(), holder.imageView);
            } else if (isImagePresent) {
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                holder.vidieo_image.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.GONE);
                holder.card_learningimage.setVisibility(View.VISIBLE);
            }
        }
        holder.mainRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f,0.94f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f,0.96f);
                scaleDownX.setDuration(150);
                scaleDownY.setDuration(150);
                scaleDownX.setRepeatCount(1);
                scaleDownY.setRepeatCount(1);
                scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                scaleDownX.setInterpolator(new DecelerateInterpolator());
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
                scaleDown.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        FavouriteCardsCourseData favouriteCardsCourseData=new FavouriteCardsCourseData();
                        favouriteCardsCourseData.setCourseName(courseName);
                        favouriteCardsCourseData.setCourseId(courseId);
                        favouriteCardsCourseData.setFavCardDetailsList(favCardDetailsList);
                        cardClickCallBack.onCardClick(favouriteCardsCourseData,position);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favCardDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView vidieo_image,card_learningimage;
        TextView card_text;
        ImageView imageView;
        RelativeLayout mainRow;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.card_image);
//            card_title= (TextView) itemView.findViewById(R.id.card_title);
            card_text= itemView.findViewById(R.id.card_text);
            vidieo_image= itemView.findViewById(R.id.card_videoicon);
            mainRow= itemView.findViewById(R.id.card_mainrow);
            card_learningimage= itemView.findViewById(R.id.card_learningimage);
            OustSdkTools.setImage(vidieo_image, OustSdkApplication.getContext().getResources().getString(R.string.challenge));
            OustSdkTools.setImage(card_learningimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
        }
    }

    // get the bitmap of image media that we got from card media
    // get the bitmap of image media that we got from card media
    public boolean setGifImage(String fileName,ImageView imageView){
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if((audStr!=null)&&(!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                GifDrawable gifFromBytes = new GifDrawable(imageByte);
//                imageView.setImageDrawable(gifFromBytes);
//                imageView.setVisibility(View.VISIBLE);
//                return true;
//            }

            File file = new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                imageView.setImageURI(uri);
                imageView.setVisibility(View.VISIBLE);
                return true;
            }
        }catch (Exception e){
            boolean isimage=setImage(fileName,imageView);
            return isimage;
        }
        return false;
    }

    public boolean setImage(String fileName,ImageView imageView){
        try {
            if((fileName!=null)&&(!fileName.isEmpty())) {
//                EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//                String audStr = enternalPrivateStorage.readSavedData(fileName);
//                if ((audStr != null) && (!audStr.isEmpty())) {
//                    byte[] imageByte = Base64.decode(audStr, 0);
//                    BitmapFactory.Options options=new BitmapFactory.Options();
//                    options.inSampleSize = 4;
//                    InputStream input = new ByteArrayInputStream(imageByte);
//                    Bitmap bitmap = BitmapFactory.decodeStream(input,null,options);
//                    imageView.setImageBitmap(bitmap);
//                    imageView.setVisibility(View.VISIBLE);
//                    return true;
//                }
                File file=new File(OustSdkApplication.getContext().getFilesDir(),fileName);
                if(file!=null && file.exists()){
                    Picasso.get().load(file).into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public void setThumbnailImage(String imagePath,ImageView imageView){
        try {
            imageView.setVisibility(View.VISIBLE);
            if((imagePath!=null)&&(!imagePath.isEmpty())){
                if((OustSdkTools.checkInternetStatus())&&(OustStaticVariableHandling.getInstance().isNetConnectionAvailable())){
                    Picasso.get().load(imagePath).into(imageView);
                }else {
                    Picasso.get().load(imagePath).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
                }
            }
        }catch (Exception e){
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

}
