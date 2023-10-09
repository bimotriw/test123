package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 16/03/17.
 */

@Keep
public class Points {
    private String topic;

    private int point;

    private String time;

    private boolean correct;

    private String subject;

    private String answer;

    private String grade;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


    @Override
    public String toString() {
        return "Points{" +
                "topic='" + topic + '\'' +
                ", point=" + point +
                ", time='" + time + '\'' +
                ", correct=" + correct +
                ", subject='" + subject + '\'' +
                ", answer='" + answer + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }
}
