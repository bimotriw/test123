package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.common.FavouriteCardsCourseData;
import com.oustme.oustsdk.response.course.FavCardDetails;

import java.util.ArrayList;
import java.util.List;

@Keep
public class FavouriteCardsFilter {
    public List<FavouriteCardsCourseData> meetCriteria(List<FavouriteCardsCourseData> allfavouriteCardsCourse, String searchText){
        List<FavouriteCardsCourseData> favouriteCardsCourseDatas=new ArrayList<>();
        for(int i=0; i<allfavouriteCardsCourse.size(); i++){
            FavouriteCardsCourseData favouriteCardsCourseData=new FavouriteCardsCourseData();
            List<FavCardDetails> favCardDetailsList=new ArrayList<>();
            for(int j=0; j<allfavouriteCardsCourse.get(i).getFavCardDetailsList().size(); j++){
                if(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).getCardTitle()!=null){
                    if (allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).getCardTitle().toLowerCase().contains(searchText.toLowerCase())){
                        FavCardDetails favCardDetails=new FavCardDetails();
                        favCardDetails.setCardTitle(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).getCardTitle());
                        favCardDetails.setCardDescription(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).getCardDescription());
                        favCardDetails.setLevelId(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).getLevelId());
                        favCardDetails.setVideo(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).isVideo());
                        favCardDetails.setAudio(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).isAudio());
                        favCardDetails.setImageUrl(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).getImageUrl());
                        favCardDetails.setCardId(allfavouriteCardsCourse.get(i).getFavCardDetailsList().get(j).getCardId());
                        favCardDetailsList.add(favCardDetails);
                    }
                }
            }
           if((favouriteCardsCourseData!=null) && (favCardDetailsList!=null && favCardDetailsList.size()>0)) {
               favouriteCardsCourseData.setCourseId(allfavouriteCardsCourse.get(i).getCourseId());
               favouriteCardsCourseData.setCourseName(allfavouriteCardsCourse.get(i).getCourseName());
               favouriteCardsCourseData.setFavCardDetailsList(favCardDetailsList);

               favouriteCardsCourseDatas.add(favouriteCardsCourseData);
           }
        }
        return favouriteCardsCourseDatas;
    }

}
