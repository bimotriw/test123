package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.common.Questions;
import com.oustme.oustsdk.response.course.CardColorScheme;
import com.oustme.oustsdk.response.course.CourseSolutionCard;
import com.oustme.oustsdk.response.course.LearningCardType;
import com.oustme.oustsdk.response.course.LearningQuestionData;
import com.oustme.oustsdk.response.course.ReadMoreData;

import java.util.List;

/**
 * Created by admin on 06/09/16.
 */
@Keep
public class CourseCardClass {
    private String cardBgImage;
    private String cardBgColor;
    private long cardId;
    private String cardLayout;
    private String cardQuestionColor;
    private String cardSolutionColor;
    private String cardTextColor;
    private String cardType;
    private String cardTitle;
    private long qId;
    private long xp;
    private long sequence;
    private List<CourseCardMedia> cardMedia;
    private String content;
    private Questions questionData;
    private CourseSolutionCard childCard;
    private String clCode;
    private String bgImg;
    private String language;
    private CardColorScheme cardColorScheme;
    private String questionType;
    private String questionCategory;
    private ReadMoreData readMoreData;
    private boolean isReadMoreCard;
    private boolean potraitModeVideo;
    private boolean shareToSocialMedia;
    private long mappedLearningCardId;
    private String caseStudyTitle;
    private String audio;
    private long mandatoryViewTime;
    private String scormIndexFile;
    private boolean showQuestionSymbolForQuestion;
    private boolean isIfScormEventBased;
    private boolean proceedOnWrong ;

    public long getMandatoryViewTime() {
        return mandatoryViewTime;
    }

    public void setMandatoryViewTime(long mandatoryViewTime) {
        this.mandatoryViewTime = mandatoryViewTime;
    }

    public String getCardBgColor() {
        return cardBgColor;
    }

    public void setCardBgColor(String cardBgColor) {
        this.cardBgColor = cardBgColor;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getCardLayout() {
        return cardLayout;
    }

    public void setCardLayout(String cardLayout) {
        this.cardLayout = cardLayout;
    }

    public String getCardQuestionColor() {
        return cardQuestionColor;
    }

    public void setCardQuestionColor(String cardQuestionColor) {
        this.cardQuestionColor = cardQuestionColor;
    }

    public String getCardSolutionColor() {
        return cardSolutionColor;
    }

    public void setCardSolutionColor(String cardSolutionColor) {
        this.cardSolutionColor = cardSolutionColor;
    }

    public String getCardTextColor() {
        return cardTextColor;
    }

    public void setCardTextColor(String cardTextColor) {
        this.cardTextColor = cardTextColor;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public long getqId() {
        return qId;
    }

    public void setqId(long qId) {
        this.qId = qId;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public List<CourseCardMedia> getCardMedia() {
        return cardMedia;
    }

    public void setCardMedia(List<CourseCardMedia> cardMedia) {
        this.cardMedia = cardMedia;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Questions getQuestionData() {
        return questionData;
    }

    public void setQuestionData(Questions questionData) {
        this.questionData = questionData;
    }

    public CourseSolutionCard getChildCard() {
        return childCard;
    }

    public void setChildCard(CourseSolutionCard childCard) {
        this.childCard = childCard;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getClCode() {
        return clCode;
    }

    public void setClCode(String clCode) {
        this.clCode = clCode;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getCardBgImage() {
        return cardBgImage;
    }

    public void setCardBgImage(String cardBgImage) {
        this.cardBgImage = cardBgImage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public CardColorScheme getCardColorScheme() {
        return cardColorScheme;
    }

    public void setCardColorScheme(CardColorScheme cardColorScheme) {
        this.cardColorScheme = cardColorScheme;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionCategory() {
        return questionCategory;
    }

    public void setQuestionCategory(String questionCategory) {
        this.questionCategory = questionCategory;
    }

    public ReadMoreData getReadMoreData() {
        return readMoreData;
    }

    public void setReadMoreData(ReadMoreData readMoreData) {
        this.readMoreData = readMoreData;
    }

    public boolean isReadMoreCard() {
        return isReadMoreCard;
    }

    public void setReadMoreCard(boolean readMoreCard) {
        isReadMoreCard = readMoreCard;
    }

    public boolean isPotraitModeVideo() {
        return potraitModeVideo;
    }

    public void setPotraitModeVideo(boolean potraitModeVideo) {
        this.potraitModeVideo = potraitModeVideo;
    }

    public boolean isShareToSocialMedia() {
        return shareToSocialMedia;
    }

    public void setShareToSocialMedia(boolean shareToSocialMedia) {
        this.shareToSocialMedia = shareToSocialMedia;
    }

    public long getMappedLearningCardId() {
        return mappedLearningCardId;
    }

    public void setMappedLearningCardId(long mappedLearningCardId) {
        this.mappedLearningCardId = mappedLearningCardId;
    }

    public String getCaseStudyTitle() {
        return caseStudyTitle;
    }

    public void setCaseStudyTitle(String caseStudyTitle) {
        this.caseStudyTitle = caseStudyTitle;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }


    public String getScormIndexFile() {
        return scormIndexFile;
    }

    public void setScormIndexFile(String scormIndexFile) {
        this.scormIndexFile = scormIndexFile;
    }

    public boolean isShowQuestionSymbolForQuestion() {
        return showQuestionSymbolForQuestion;
    }

    public void setShowQuestionSymbolForQuestion(boolean showQuestionSymbolForQuestion) {
        this.showQuestionSymbolForQuestion = showQuestionSymbolForQuestion;
    }

    public boolean isIfScormEventBased() {
        return isIfScormEventBased;
    }

    public void setIfScormEventBased(boolean ifScormEventBased) {
        isIfScormEventBased = ifScormEventBased;
    }

    public boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }
}
