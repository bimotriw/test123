package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

import java.util.Comparator;
import java.util.List;

@Keep
public class DTOCourseCard {
    private String cardBgImage;
    private String cardBgColor;
    private String courseCardID;
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
    private List<DTOCourseCardMedia> cardMedia;
    private String content;
    private DTOQuestions questionData;
    private DTOCourseSolutionCard childCard;
    private String clCode;
    private String bgImg;
    private String language;
    private DTOCardColorScheme cardColorScheme;
    private String questionType;
    private String questionCategory;
    private DTOReadMore readMoreData;
    private boolean isReadMoreCard;
    private boolean potraitModeVideo;
    private boolean shareToSocialMedia;
    private long mappedLearningCardId;
    private String caseStudyTitle;
    private String audio;
    private long mandatoryViewTime;
    private String scormIndexFile;
    private boolean showQuestionSymbolForQuestion;
    private boolean proceedOnWrong;
    private boolean isIfScormEventBased;
    private String scormPlayerUrl;

    public String getCardBgImage() {
        return cardBgImage;
    }

    public void setCardBgImage(String cardBgImage) {
        this.cardBgImage = cardBgImage;
    }

    public String getCardBgColor() {
        return cardBgColor;
    }

    public void setCardBgColor(String cardBgColor) {
        this.cardBgColor = cardBgColor;
    }

    public String getCourseCardID() {
        return courseCardID;
    }

    public void setCourseCardID(String courseCardID) {
        this.courseCardID = courseCardID;
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

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
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

    public List<DTOCourseCardMedia> getCardMedia() {
        return cardMedia;
    }

    public void setCardMedia(List<DTOCourseCardMedia> cardMedia) {
        this.cardMedia = cardMedia;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DTOQuestions getQuestionData() {
        return questionData;
    }

    public void setQuestionData(DTOQuestions questionData) {
        this.questionData = questionData;
    }

    public DTOCourseSolutionCard getChildCard() {
        return childCard;
    }

    public void setChildCard(DTOCourseSolutionCard childCard) {
        this.childCard = childCard;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DTOCardColorScheme getCardColorScheme() {
        return cardColorScheme;
    }

    public void setCardColorScheme(DTOCardColorScheme cardColorScheme) {
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

    public DTOReadMore getReadMoreData() {
        return readMoreData;
    }

    public void setReadMoreData(DTOReadMore readMoreData) {
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

    public long getMandatoryViewTime() {
        return mandatoryViewTime;
    }

    public void setMandatoryViewTime(long mandatoryViewTime) {
        this.mandatoryViewTime = mandatoryViewTime;
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

    public static Comparator<DTOCourseCard> newsCardSorter = (o1, o2) -> Long.compare(o1.getSequence(), o2.getSequence());

    public String getScormPlayerUrl() {
        return scormPlayerUrl;
    }

    public void setScormPlayerUrl(String scormPlayerUrl) {
        this.scormPlayerUrl = scormPlayerUrl;
    }
}
