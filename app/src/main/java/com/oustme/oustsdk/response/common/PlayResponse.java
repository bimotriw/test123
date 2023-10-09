package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.dto.DTOQuestions;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class PlayResponse extends CommonResponse implements Serializable {
    private int gameId;

    private EncrypQuestions[] encrypQuestions;

    private DTOQuestions[] questions;

    private int correctAnswerCount;

    private int wrongAnswerCount;

    private String vendorId;

    private String vendorDisplayName;

    private List<Integer> qIdList;

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorDisplayName() {
        return vendorDisplayName;
    }

    public void setVendorDisplayName(String vendorDisplayName) {
        this.vendorDisplayName = vendorDisplayName;
    }


    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public EncrypQuestions[] getEncrypQuestions() {
        return encrypQuestions;
    }

    public void setEncrypQuestions(EncrypQuestions[] encrypQuestions) {
        this.encrypQuestions = encrypQuestions;
    }

    public DTOQuestions[] getQuestions() {
        return questions;
    }

    public DTOQuestions getQuestionsByIndex(int index) {
        DTOQuestions questionData=questions[index];
        return questionData;
    }
    public void setQuestionDataByIndex(int index,DTOQuestions dtoQuestions){
        questions[index]=dtoQuestions;
    }

    public void setQuestions(DTOQuestions[] questions) {
        this.questions = questions;
    }


    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public void setCorrectAnswerCount(int correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
    }

    public int getWrongAnswerCount() {
        return wrongAnswerCount;
    }

    public void setWrongAnswerCount(int wrongAnswerCount) {
        this.wrongAnswerCount = wrongAnswerCount;
    }

    public List<Integer> getqIdList() {
        return qIdList;
    }

    public void setqIdList(List<Integer> qIdList) {
        this.qIdList = qIdList;
    }
}
