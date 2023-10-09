package com.oustme.oustsdk.tools.filters;

import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.SearchCourseCard;
import com.oustme.oustsdk.firebase.course.SearchCourseLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 17/04/17.
 */

public class LevelFilter {
    public List<SearchCourseLevel> meetCriteria(List<SearchCourseLevel> allLevel, String searchText) {
        List<SearchCourseLevel> filterLevel = new ArrayList<>();
        for (SearchCourseLevel level : allLevel) {
            if((level!=null)){
                //if level name or decr contain search text add level
                if((level.getName()!=null)&&(searchText!=null)&&(level.getName().toLowerCase().contains((searchText.toLowerCase())))||(level.getDescription()!=null)&&(level.getDescription().toLowerCase().contains((searchText.toLowerCase())))){
                    filterLevel.add(level);
                }else {
                    if (level.getSearchCourseCards() != null) {
                        List<SearchCourseCard> cardList = new ArrayList<>();
                        for (SearchCourseCard serarchCard : level.getSearchCourseCards()) {
                            if (serarchCard != null) {
                                if ((serarchCard.getName() != null) &&(searchText!=null)&& (serarchCard.getName().toLowerCase().contains((searchText.toLowerCase())))) {
                                    cardList.add(serarchCard);
                                }else if ((serarchCard.getDescription() != null) && (serarchCard.getDescription().toLowerCase().contains((searchText.toLowerCase())))) {
                                    cardList.add(serarchCard);
                                }
                            }
                        }
                        if(cardList.size()>0) {
                            level.setSearchCourseCards(cardList);
                            filterLevel.add(level);
                        }
                    }
                }
            }
        }
        return filterLevel;
    }
}
