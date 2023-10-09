package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;


@Keep
public class Scores {

    private boolean correct;
    private String question;
    private int questionSerialNo;
    private String questionType;
    private long score;
    private long time;
    private String userSubjectiveAns;
    private int xp;
    private String answer;
    private String grade;
    private String subject;
    private String topic;
    private String moduleId;
    private String remarks;
    private String questionMedia;

    private boolean surveyQuestion;
    private String exitStatus = "";

    public long getTime() {
        long res = 0;
        try {
            res = time;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return res;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public long getScore() {
        long res = 0;
        try {
            res = score;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return res;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {

        this.answer = getUtfStr(answer);
    }

    public int getXp() {
        try {
            return xp;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return 0;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getQuestion() {
        try {
            if (question != null) {
                return Integer.parseInt(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return 0;
    }

    public void setQuestion(int question) {
        this.question = question + "";
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getQuestionSerialNo() {
        try {
            return questionSerialNo;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return 0;
    }

    public void setQuestionSerialNo(int questionSerialNo) {
        this.questionSerialNo = questionSerialNo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getUserSubjectiveAns() {
        return userSubjectiveAns;
    }

    public void setUserSubjectiveAns(String userSubjectiveAns) {
        this.userSubjectiveAns = userSubjectiveAns;
    }

    @NonNull
    @Override
    public String toString() {
        return "Scores{" +
                "time=" + time +
                ", correct=" + correct +
                ", score_text=" + score +
                ", answer='" + answer + '\'' +
                ", xp=" + xp +
                ", question=" + question +
                ", questionType=" + questionType +
                ", questionSerialNo=" + questionSerialNo +
                ", grade='" + grade + '\'' +
                ", subject='" + subject + '\'' +
                ", topic='" + topic + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", remarks='" + remarks + '\'' +
                ", questionMedia='" + questionMedia + '\'' +
                '}';
    }

    public String getUtfStr(String s2) {
        String s1 = "";
        try {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            byte[] b = s2.getBytes();
            ByteBuffer bb = ByteBuffer.wrap(b);
            CharBuffer parsed = decoder.decode(bb);
            s1 = "" + parsed;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return s1;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getQuestionMedia() {
        return questionMedia;
    }

    public void setQuestionMedia(String questionMedia) {
        this.questionMedia = questionMedia;
    }

    public boolean isSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(boolean surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(String exitStatus) {
        this.exitStatus = exitStatus;
    }
}
