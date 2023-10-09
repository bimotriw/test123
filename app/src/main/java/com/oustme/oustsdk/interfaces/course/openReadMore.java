package com.oustme.oustsdk.interfaces.course;

import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;
import com.oustme.oustsdk.room.dto.DTOReadMore;

public interface openReadMore {
    void adaptiveOpenReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOAdaptiveCardDataModel courseCardClass);
}
