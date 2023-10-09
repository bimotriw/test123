package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
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
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class AllFavCardAdapter extends RecyclerView.Adapter<AllFavCardAdapter.MyViewHolder> {
    private Activity activity;
    private CardClickCallBack cardClickCallBack;
    private List<FavCardDetails> favCardDetailsList;
    private String courseName,courseId;
    public AllFavCardAdapter(List<FavCardDetails> favCardDetailsList, Activity activity, String courseId, String courseName) {
        this.activity=activity;
        this.favCardDetailsList=favCardDetailsList;
        this.courseId=courseId;
        this.courseName=courseName;
    }

    public void setCardClickCallBack(CardClickCallBack cardClickCallBack){
        this.cardClickCallBack=cardClickCallBack;
    }

    @Override
    public AllFavCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false);
        return new AllFavCardAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AllFavCardAdapter.MyViewHolder holder, final int position) {
//        Bitmap decodedByte=null;
        boolean isImagePresent=false;

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
        } else {
//        courseCardClass=getCardData(favouriteCardsResponseRowList.get(position).getCardId());
            if ((favCardDetailsList.get(position).getImageUrl() != null) && (!favCardDetailsList.get(position).getImageUrl().isEmpty())) {
//                decodedByte = getImage("oustlearn_" + favCardDetailsList.get(position).getImageUrl(), decodedByte);
                isImagePresent = setGifImage("oustlearn_" + favCardDetailsList.get(position).getImageUrl(), holder.imageView);
            }
            if ((favCardDetailsList.get(position).getCardTitle() != null) && (!favCardDetailsList.get(position).getCardTitle().isEmpty())) {
                holder.card_text.setText(favCardDetailsList.get(position).getCardTitle());
            } else if (favCardDetailsList.get(position).getCardDescription() != null) {
//                OustSdkTools.getSpannedContent(favCardDetailsList.get(position).getCardDescription(), holder.card_text);
            }
            if ((favCardDetailsList.get(position).isVideo() || ((favCardDetailsList.get(position).getMediaType()!=null)&&(favCardDetailsList.get(position).getMediaType()).equalsIgnoreCase("VIDEO"))) && ((favCardDetailsList.get(position).getImageUrl() != null) || (!favCardDetailsList.isEmpty()))) {
                holder.card_learningimage.setVisibility(View.GONE);
                holder.vidieo_image.setVisibility(View.VISIBLE);
                setThumbnailImage(favCardDetailsList.get(position).getImageUrl(), holder.imageView);
            } else
//                if (decodedByte != null) {
                if(isImagePresent){
                    holder.imageView.setVisibility(View.VISIBLE);
//                holder.imageView.setImageBitmap(decodedByte);
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
                    public void onAnimationStart(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        FavouriteCardsCourseData favouriteCardsCourseData=new FavouriteCardsCourseData();
                        favouriteCardsCourseData.setCourseName(courseName);
                        favouriteCardsCourseData.setCourseId(courseId);
                        favouriteCardsCourseData.setFavCardDetailsList(favCardDetailsList);
                        cardClickCallBack.onCardClick(favouriteCardsCourseData,position);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {}

                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favCardDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView vidieo_image,card_learningimage;
        ImageView imageView;
        TextView card_text;
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


//        get the bitmap of image media that we got from card media
// get the bitmap of image media that we got from card media
public Bitmap getImage(String fileName,Bitmap decodedByte){
    try {
        if((fileName!=null)&&(!fileName.isEmpty())) {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String audStr = enternalPrivateStorage.readSavedData(fileName);
            if ((audStr != null) && (!audStr.isEmpty())) {
                byte[] imageByte = Base64.decode(audStr, 0);
                decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

            }
        }
    }catch (Exception e){
        //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
    }
    return decodedByte;
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

    // get the bitmap of image media that we got from card media
    public boolean setGifImage(String fileName,ImageView imageView){
        try {
            File file =new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()){
                Uri uri=Uri.fromFile(file);
                imageView.setImageURI(uri);
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
                File file =new File(OustSdkApplication.getContext().getFilesDir(),fileName);
                if(file!=null && file.exists()){
                    Picasso.get().load(file).into(imageView);
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

}
