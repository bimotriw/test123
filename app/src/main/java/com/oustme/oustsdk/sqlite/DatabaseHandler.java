package com.oustme.oustsdk.sqlite;

import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOAdaptiveQuestionData;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class DatabaseHandler {

    // Adding new contact

    public void addCardDataClass(DTOCourseCard courseCardClass, int cardId) {

        RoomHelper.addOrUpdateCourseCard(courseCardClass);

    }

    public DTOCourseCard getCardClass(int cardId) {
        return RoomHelper.getCourseCardByCardId(cardId);
    }

    // handled here for card edited
    public void deleteCardClass(int cardId) {
        RoomHelper.deleteCourseCard(cardId);
    }

    public void addToRealmQuestions(DTOQuestions questionData, boolean isFfcQuestion) {

        if (isFfcQuestion) {
            RoomHelper.addorUpdateFfcQuestion(questionData);
        } else {
            RoomHelper.addorUpdateQuestion(questionData);
        }
    }

    public DTOQuestions getQuestionById(long qId, boolean isFfcQuestion) {
        DTOQuestions questions = new DTOQuestions();
        if (isFfcQuestion) {
            questions = RoomHelper.getFfcQuestionById(qId);
        } else {
            questions = RoomHelper.getQuestionById(qId);
        }
        return questions;
    }


    public void addToRealmQuestionsAdaptive(DTOAdaptiveQuestionData questionData, boolean isFfcQuestion) {
        if (isFfcQuestion) {
            RoomHelper.addorUpdateFfcQuestion(questionData);
        } else {
            RoomHelper.addorUpdateQuestion(questionData);
        }
    }

}
