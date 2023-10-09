package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.course.HotspotPointData;
import com.oustme.oustsdk.firebase.course.QuestionOptionCategoryData;
import com.oustme.oustsdk.firebase.course.QuestionOptionData;
import com.oustme.oustsdk.response.common.ImageChoiceData;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;

import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class LearningQuestionData {

    private String a;

    private String b;

    private String c;

    private String d;

    private String e;

    private String f;

    private String g;


    private String subject;

    private boolean allNoneFlag;

    private String topic;

    private String imageheight;

    private long questionId;

    private String answer;

    private String image;

    private String vendorId;

    private String vendorDisplayName;

    private long maxtime;

    private String grade;

    private String imagewidth;

    private String question;

    private Boolean favourite;

    private String likeUnlike;

    private String userFeedback;

    private String answerStatus;

    private String studentAnswer;

    private String reattemptCount;

    private String solution;

    private ReadMoreData readMoreData;

    private QuestionCategory questionCategory;

    private QuestionType questionType;

    private List<QuestionOptionCategoryData> optionCategories;

    private List<QuestionOptionData> options;

    private boolean skip;

    List<MTFColumnData> mtfLeftCol;//mtfRightCol
    List<MTFColumnData> mtfRightCol;
    List<String> mtfAnswer;

    private ImageChoiceData imageChoiceA;
    private ImageChoiceData imageChoiceB;
    private ImageChoiceData imageChoiceC;
    private ImageChoiceData imageChoiceD;
    private ImageChoiceData imageChoiceE;
    private ImageChoiceData imageChoiceAnswer;
    private String audio;
    private boolean randomize;
    private List<HotspotPointData> hotspotDataList;




    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isAllNoneFlag() {
        return allNoneFlag;
    }

    public void setAllNoneFlag(boolean allNoneFlag) {
        this.allNoneFlag = allNoneFlag;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getImageheight() {
        return imageheight;
    }

    public void setImageheight(String imageheight) {
        this.imageheight = imageheight;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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

    public long getMaxtime() {
        return maxtime;
    }

    public void setMaxtime(long maxtime) {
        this.maxtime = maxtime;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getImagewidth() {
        return imagewidth;
    }

    public void setImagewidth(String imagewidth) {
        this.imagewidth = imagewidth;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public String getLikeUnlike() {
        return likeUnlike;
    }

    public void setLikeUnlike(String likeUnlike) {
        this.likeUnlike = likeUnlike;
    }

    public String getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(String userFeedback) {
        this.userFeedback = userFeedback;
    }

    public String getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(String answerStatus) {
        this.answerStatus = answerStatus;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public String getReattemptCount() {
        return reattemptCount;
    }

    public void setReattemptCount(String reattemptCount) {
        this.reattemptCount = reattemptCount;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public QuestionCategory getQuestionCategory() {
        return questionCategory;
    }

    public void setQuestionCategory(QuestionCategory questionCategory) {
        this.questionCategory = questionCategory;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public ImageChoiceData getImageChoiceA() {
        return imageChoiceA;
    }

    public void setImageChoiceA(ImageChoiceData imageChoiceA) {
        this.imageChoiceA = imageChoiceA;
    }

    public ImageChoiceData getImageChoiceB() {
        return imageChoiceB;
    }

    public void setImageChoiceB(ImageChoiceData imageChoiceB) {
        this.imageChoiceB = imageChoiceB;
    }

    public ImageChoiceData getImageChoiceC() {
        return imageChoiceC;
    }

    public void setImageChoiceC(ImageChoiceData imageChoiceC) {
        this.imageChoiceC = imageChoiceC;
    }

    public ImageChoiceData getImageChoiceD() {
        return imageChoiceD;
    }

    public void setImageChoiceD(ImageChoiceData imageChoiceD) {
        this.imageChoiceD = imageChoiceD;
    }

    public ImageChoiceData getImageChoiceE() {
        return imageChoiceE;
    }

    public void setImageChoiceE(ImageChoiceData imageChoiceE) {
        this.imageChoiceE = imageChoiceE;
    }

    public ImageChoiceData getImageChoiceAnswer() {
        return imageChoiceAnswer;
    }

    public void setImageChoiceAnswer(ImageChoiceData imageChoiceAnswer) {
        this.imageChoiceAnswer = imageChoiceAnswer;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public List<MTFColumnData> getMtfLeftCol() {
        return mtfLeftCol;
    }

    public void setMtfLeftCol(List<MTFColumnData> mtfLeftCol) {
        this.mtfLeftCol = mtfLeftCol;
    }

    public List<MTFColumnData> getMtfRightCol() {
        return mtfRightCol;
    }

    public void setMtfRightCol(List<MTFColumnData> mtfRightCol) {
        this.mtfRightCol = mtfRightCol;
    }

    public List<String> getMtfAnswer() {
        return mtfAnswer;
    }

    public void setMtfAnswer(List<String> mtfAnswer) {
        this.mtfAnswer = mtfAnswer;
    }

    public boolean isRandomize() {
        return randomize;
    }

    public void setRandomize(boolean randomize) {
        this.randomize = randomize;
    }

    public ReadMoreData getReadMoreData() {
        return readMoreData;
    }

    public void setReadMoreData(ReadMoreData readMoreData) {
        this.readMoreData = readMoreData;
    }

    public List<QuestionOptionCategoryData> getOptionCategories() {
        return optionCategories;
    }

    public void setOptionCategories(List<QuestionOptionCategoryData> optionCategories) {
        this.optionCategories = optionCategories;
    }

    public List<QuestionOptionData> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionData> options) {
        this.options = options;
    }

    public List<HotspotPointData> getHotspotDataList() {
        return hotspotDataList;
    }

    public void setHotspotDataList(List<HotspotPointData> hotspotDataList) {
        this.hotspotDataList = hotspotDataList;
    }
}
