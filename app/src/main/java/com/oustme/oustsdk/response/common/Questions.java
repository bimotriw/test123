package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.course.HotspotPointData;
import com.oustme.oustsdk.firebase.course.QuestionOptionCategoryData;
import com.oustme.oustsdk.firebase.course.QuestionOptionData;
import com.oustme.oustsdk.response.course.MTFColumnData;
import com.oustme.oustsdk.response.course.ReadMoreData;

import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class Questions {

    private String A;

    private String B;

    private String C;

    private String D;

    private String E;

    private String F;

    private String G;
    private String a,b,c,d,e,f,g;

    private String subject;

    private boolean allNoneFlag;

    private String topic;

    private String imageheight;

    private long questionId;

    private String answer;

    private String exitOption;

    private String image;
    private String bgImg;

    private String imageCDNPath;

    private String qVideoUrl;

    private String gumletVideoUrl;

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

    private String questionCategory;

    private String questionType;

    private boolean skip;

    private String audio;
    private long gameId;

    private boolean isFullScreenHotSpot;

    private ImageChoiceData imageChoiceA;
    private ImageChoiceData imageChoiceB;
    private ImageChoiceData imageChoiceC;
    private ImageChoiceData imageChoiceD;
    private ImageChoiceData imageChoiceE;
    private ImageChoiceData imageChoiceAnswer;

    List<MTFColumnData> mtfLeftCol;
    List<MTFColumnData> mtfRightCol;
    List<String> mtfAnswer;

    private List<String> fillAnswers;
    private List<HotspotPointData> hotspotDataList;


    private List<QuestionOptionCategoryData> optionCategories;

    private List<QuestionOptionData> options;

    private ReadMoreData readMoreData;

    private boolean randomize;

    private boolean containSubjective;
    private String subjectiveQuestion;
    private int surveyPointCount;

    private int maxWordCount;
    private int minWordCount;

    private boolean exitable;
    private boolean thumbsUpDn;

    private String answerValidationType,fieldType,dropdownType;
    private boolean mandatory;
    private String exitMessage;
    private boolean isHotSpotThumbsUpShown;
    private boolean isHotSpotThumbsDownShown;
    private String imageType;
    private boolean thumbsUp, thumbsDown;
    private boolean showSolution;
    private String solutionType;
    private long score;
    private boolean correct;
    private String difficulty;
    private String moduleId;
    private String moduleName;
    private String attemptDateTime;
    private String base64Image;
    private long timeTaken;
    private String feedback;
    private String videoLinks;
    private String textMaterial;
    private String syllabus;
    private boolean showSkipButton;
    private String skillName;
    private boolean shareToSocialMedia;
    private boolean showFavourite;
    private boolean proceedOnWrong;
    private boolean enableGalleryUpload;
    int startRange;
    int endRange;
    String minLabel;
    String maxLabel;

    private List<VideoOverlay> listOfVideoOverlayQuestion;

    public String getImageCDNPath() {
        return imageCDNPath;
    }

    public void setImageCDNPath(String imageCDNPath) {
        this.imageCDNPath = imageCDNPath;
    }

    public String getqVideoUrl() {
        return qVideoUrl;
    }

    public void setqVideoUrl(String qVideoUrl) {
        this.qVideoUrl = qVideoUrl;
    }

    public String getGumletVideoUrl() {
        return gumletVideoUrl;
    }

    public void setGumletVideoUrl(String gumletVideoUrl) {
        this.gumletVideoUrl = gumletVideoUrl;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }

    public boolean isThumbsUpDn() {
        return thumbsUpDn;
    }

    public void setThumbsUpDn(boolean thumbsUpDn) {
        this.thumbsUpDn = thumbsUpDn;
    }

    public String getAnswerValidationType() {
        return answerValidationType;
    }

    public void setAnswerValidationType(String answerValidationType) {
        this.answerValidationType = answerValidationType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getDropdownType() {
        return dropdownType;
    }

    public void setDropdownType(String dropdownType) {
        this.dropdownType = dropdownType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getExitOption() {
        return exitOption;
    }

    public void setExitOption(String exitOption) {
        this.exitOption = exitOption;
    }

    public boolean isExitable() {
        return exitable;
    }

    public void setExitable(boolean exitable) {
        this.exitable = exitable;
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

    public ReadMoreData getReadMoreData() {
        return readMoreData;
    }

    public void setReadMoreData(ReadMoreData readMoreData) {
        this.readMoreData = readMoreData;
    }

    public boolean isRandomize() {
        return randomize;
    }

    public void setRandomize(boolean randomize) {
        this.randomize = randomize;
    }

    public String getReattemptCount() {
        return reattemptCount;
    }

    public void setReattemptCount(String reattemptCount) {
        this.reattemptCount = reattemptCount;
    }


    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public String getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(String answerStatus) {
        this.answerStatus = answerStatus;
    }

    public String getVendorDisplayName() {
        return vendorDisplayName;
    }

    public void setVendorDisplayName(String vendorDisplayName) {
        this.vendorDisplayName = vendorDisplayName;
    }

    public boolean getAllNoneFlag() {
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

    public String getD() {
        if(a!=null)
            return d;
        else
            return D;
    }

    public void setD(String sd) {
        d=sd;
        D = sd;
    }

    public String getE() {
        if(e!=null)
            return e;
        else
            return E;
    }

    public void setE(String se) {
        e=se;
        E = se;
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

    public String getA() {
        if(a!=null)
            return a;
        else
            return A;
    }

    public void setA(String sa) {
        a=sa;
        A = sa;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getB() {
        if(b!=null)
            return b;
        else
            return B;
    }

    public void setB(String sb) {
        b=sb;
        B =sb;
    }

    public String getC() {
        if(c!=null)
            return c;
        else
            return C;
    }

    public void setC(String sc) {
        c=sc;
        C = sc;
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

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
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

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getQuestionCategory() {
        return questionCategory;
    }

    public void setQuestionCategory(String questionCategory) {
        this.questionCategory = questionCategory;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String getF() {
        if(f!=null)
            return f;
        else
            return F;
    }

    public void setF(String sf) {
        F = sf;
        f=sf;
    }

    public String getG() {
        if(g!=null)
            return g;
        else
            return G;
    }

    public void setG(String sg) {
        g=sg;
        G = sg;
    }

    public boolean isAllNoneFlag() {
        return allNoneFlag;
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

    public ImageChoiceData getImageChoiceD() {
        return imageChoiceD;
    }

    public void setImageChoiceD(ImageChoiceData imageChoiceD) {
        this.imageChoiceD = imageChoiceD;
    }

    public ImageChoiceData getImageChoiceC() {
        return imageChoiceC;
    }

    public void setImageChoiceC(ImageChoiceData imageChoiceC) {
        this.imageChoiceC = imageChoiceC;
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

    public List<String> getFillAnswers() {
        return fillAnswers;
    }

    public void setFillAnswers(List<String> fillAnswers) {
        this.fillAnswers = fillAnswers;
    }

    public List<HotspotPointData> getHotspotDataList() {
        return hotspotDataList;
    }

    public void setHotspotDataList(List<HotspotPointData> hotspotDataList) {
        this.hotspotDataList = hotspotDataList;
    }

    public int getMaxWordCount() {
        return maxWordCount;
    }

    public void setMaxWordCount(int maxWordCount) {
        this.maxWordCount = maxWordCount;
    }

    public int getMinWordCount() {
        return minWordCount;
    }

    public void setMinWordCount(int minWordCount) {
        this.minWordCount = minWordCount;
    }

    public boolean isContainSubjective() {
        return containSubjective;
    }

    public void setContainSubjective(boolean containSubjective) {
        this.containSubjective = containSubjective;
    }

    public String getSubjectiveQuestion() {
        return subjectiveQuestion;
    }

    public void setSubjectiveQuestion(String subjectiveQuestion) {
        this.subjectiveQuestion = subjectiveQuestion;
    }

    public int getSurveyPointCount() {
        return surveyPointCount;
    }

    public void setSurveyPointCount(int surveyPointCount) {
        this.surveyPointCount = surveyPointCount;
    }

    public List<VideoOverlay> getVideoOverlayList() {
        return listOfVideoOverlayQuestion;
    }

    public void setVideoOverlayList(List<VideoOverlay> listOfVideoOverlayQuestion) {
        this.listOfVideoOverlayQuestion = listOfVideoOverlayQuestion;
    }

    public boolean isFullScreenHotSpot() {
        return isFullScreenHotSpot;
    }

    public void setFullScreenHotSpot(boolean fullScreenHotSpot) {
        isFullScreenHotSpot = fullScreenHotSpot;
    }

    public boolean isHotSpotThumbsUpShown() {
        return isHotSpotThumbsUpShown;
    }

    public void setHotSpotThumbsUpShown(boolean hotSpotThumbsUpShown) {
        isHotSpotThumbsUpShown = hotSpotThumbsUpShown;
    }

    public boolean isHotSpotThumbsDownShown() {
        return isHotSpotThumbsDownShown;
    }

    public void setHotSpotThumbsDownShown(boolean hotSpotThumbsDownShown) {
        isHotSpotThumbsDownShown = hotSpotThumbsDownShown;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
        if(this.imageType!=null)
        if(this.imageType.equalsIgnoreCase("fullscreen"))
        {
            setFullScreenHotSpot(true);
        }
    }

    public boolean isThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(boolean thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public boolean isThumbsDown() {
        return thumbsDown;
    }

    public void setThumbsDown(boolean thumbsDown) {
        this.thumbsDown = thumbsDown;
    }

    public boolean isShowSolution() {
        return showSolution;
    }

    public void setShowSolution(boolean showSolution) {
        this.showSolution = showSolution;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }

    public boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }

    public boolean isEnableGalleryUpload() {
        return enableGalleryUpload;
    }

    public void setEnableGalleryUpload(boolean enableGalleryUpload) {
        this.enableGalleryUpload = enableGalleryUpload;
    }

    public int getStartRange() {
        return startRange;
    }

    public void setStartRange(int startRange) {
        this.startRange = startRange;
    }

    public int getEndRange() {
        return endRange;
    }

    public void setEndRange(int endRange) {
        this.endRange = endRange;
    }

    public String getMinLabel() {
        return minLabel;
    }

    public void setMinLabel(String minLabel) {
        this.minLabel = minLabel;
    }

    public String getMaxLabel() {
        return maxLabel;
    }

    public void setMaxLabel(String maxLabel) {
        this.maxLabel = maxLabel;
    }

    public boolean isLearningPlayNew() {
        return (this.getQuestionType() != null && (this.getQuestionType().equals(QuestionType.FILL) || this.getQuestionType().equals(QuestionType.FILL_1))) ||
                (this.getQuestionCategory() != null && (this.getQuestionCategory().equals(QuestionCategory.MATCH) ||
                        this.getQuestionCategory().equals(QuestionCategory.CATEGORY) ||
                        this.getQuestionCategory().equals(QuestionCategory.HOTSPOT)));


    }

    public boolean isMediaUploadQues(){
        return  this.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I) || this.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A) ||
                this.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V);
    }
}
