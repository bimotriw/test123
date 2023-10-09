package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 22/01/18.
 */

@Keep
public class PreviousWinnerDetailsResponse {
    private String winnerName;
    private String winnerAvatar;
    private String solution;
    private String submitDateTime;
    private long timeTaken;
    private String question;


    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getWinnerAvatar() {
        return winnerAvatar;
    }

    public void setWinnerAvatar(String winnerAvatar) {
        this.winnerAvatar = winnerAvatar;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getSubmitDateTime() {
        return submitDateTime;
    }

    public void setSubmitDateTime(String submitDateTime) {
        this.submitDateTime = submitDateTime;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
