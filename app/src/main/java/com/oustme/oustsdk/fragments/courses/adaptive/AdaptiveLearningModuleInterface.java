package com.oustme.oustsdk.fragments.courses.adaptive;

import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;

import java.util.List;

public interface AdaptiveLearningModuleInterface {
    void updatedSelectedQid(long selectedQid);
    void cardAbstractList(List<DTOAdaptiveCardDataModel> cardDataModel);
    void cardMappingData(List<Integer> cardMappedIds);
    void nextScreen(boolean isAdaptive , long selectedCardId);
    void nextScreen();
    void previousScreen();
    void updatePoints(long points);
    void levelComplete();
    void updateCardResponseData(LearningCardResponceData updateCardResponseData);
    void restart();
}
