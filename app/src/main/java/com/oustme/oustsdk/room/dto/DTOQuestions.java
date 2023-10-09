package com.oustme.oustsdk.room.dto;

import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;

import java.util.List;

public class DTOQuestions {

    private String A;

    private String B;

    private String C;

    private String D;

    private String E;

    private String F;

    private String G;

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

    private String questionCardId;

    private long questionId;

    private String answer;

    private String exitOption;

    private String image;

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

    private DTOImageChoice imageChoiceA;

    private DTOImageChoice imageChoiceB;

    private DTOImageChoice imageChoiceC;

    private DTOImageChoice imageChoiceD;

    private DTOImageChoice imageChoiceE;

    private DTOImageChoice imageChoiceAnswer;


    List<DTOMTFColumnData> mtfLeftCol;

    List<DTOMTFColumnData> mtfRightCol;

    List<String> mtfAnswer;

    private List<String> fillAnswers;

    private List<DTOHotspotPointData> hotspotDataList;

    private List<DTOQuestionOptionCategory> optionCategories;

    private List<DTOQuestionOption> options;

    private DTOReadMore readMoreData;

    private boolean randomize;
    private boolean proceedOnWrong;

    private boolean containSubjective;
    private String subjectiveQuestion;
    private int surveyPointCount;

    private int maxWordCount;
    private int minWordCount;

    private boolean exitable;
    private boolean thumbsUpDn;

    private String answerValidationType, fieldType, dropdownType;
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
    private boolean enableGalleryUpload;
    int startRange;
    int endRange;
    String minLabel;
    String maxLabel;
    String hint;
    boolean remarks;
    boolean uploadMedia;

    private String bgImg;
    private String dataSource;
    private boolean surveyQuestion;

    private List<DTOVideoOverlay> listOfVideoOverlayQuestion;

    public String getA() {
        if (a != null)
            return a;
        else
            return A;
    }

    public void setA(String a) {
        this.a = a;
        this.A = a;
    }

    public String getB() {
        if (b != null)
            return b;
        else
            return B;
    }

    public void setB(String b) {
        this.b = b;
        this.B = b;
    }

    public String getC() {
        if (c != null)
            return c;
        else
            return C;
    }

    public void setC(String c) {
        this.c = c;
        this.C = c;
    }

    public String getD() {
        if (d != null)
            return d;
        else
            return D;
    }

    public void setD(String d) {
        this.d = d;
        this.D = d;
    }

    public String getE() {
        if (e != null)
            return e;
        else
            return E;
    }

    public void setE(String e) {
        this.e = e;
        this.E = e;
    }

    public String getF() {
        if (f != null)
            return f;
        else
            return F;
    }

    public void setF(String f) {
        this.f = f;
        this.F = f;
    }

    public String getG() {
        if (g != null)
            return g;
        else
            return G;
    }

    public void setG(String g) {
        this.g = g;
        this.G = g;
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

    public String getQuestionCardId() {
        return questionCardId;
    }

    public void setQuestionCardId(String questionCardId) {
        this.questionCardId = questionCardId;
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

    public String getExitOption() {
        return exitOption;
    }

    public void setExitOption(String exitOption) {
        this.exitOption = exitOption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageCDNPath() {
        return imageCDNPath;
    }

    public void setImageCDNPath(String imageCDNPath) {
        this.imageCDNPath = imageCDNPath;
    }

    public String getQVideoUrl() {
        return qVideoUrl;
    }

    public void setQVideoUrl(String qVideoUrl) {
        this.qVideoUrl = qVideoUrl;
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

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public boolean isFullScreenHotSpot() {
        return isFullScreenHotSpot;
    }

    public void setFullScreenHotSpot(boolean fullScreenHotSpot) {
        isFullScreenHotSpot = fullScreenHotSpot;
    }

    public DTOImageChoice getImageChoiceA() {
        return imageChoiceA;
    }

    public void setImageChoiceA(DTOImageChoice imageChoiceA) {
        this.imageChoiceA = imageChoiceA;
    }

    public DTOImageChoice getImageChoiceB() {
        return imageChoiceB;
    }

    public void setImageChoiceB(DTOImageChoice imageChoiceB) {
        this.imageChoiceB = imageChoiceB;
    }

    public DTOImageChoice getImageChoiceC() {
        return imageChoiceC;
    }

    public void setImageChoiceC(DTOImageChoice imageChoiceC) {
        this.imageChoiceC = imageChoiceC;
    }

    public DTOImageChoice getImageChoiceD() {
        return imageChoiceD;
    }

    public void setImageChoiceD(DTOImageChoice imageChoiceD) {
        this.imageChoiceD = imageChoiceD;
    }

    public DTOImageChoice getImageChoiceE() {
        return imageChoiceE;
    }

    public void setImageChoiceE(DTOImageChoice imageChoiceE) {
        this.imageChoiceE = imageChoiceE;
    }

    public DTOImageChoice getImageChoiceAnswer() {
        return imageChoiceAnswer;
    }

    public void setImageChoiceAnswer(DTOImageChoice imageChoiceAnswer) {
        this.imageChoiceAnswer = imageChoiceAnswer;
    }

    public int getStartRange() {
        return startRange;
    }

    public int getEndRange() {
        return endRange;
    }

    public void setEndRange(int endRange) {
        this.endRange = endRange;
    }

    public void setStartRange(int startRange) {
        this.startRange = startRange;
    }

    public List<DTOMTFColumnData> getMtfLeftCol() {
        return mtfLeftCol;
    }

    public void setMtfLeftCol(List<DTOMTFColumnData> mtfLeftCol) {
        this.mtfLeftCol = mtfLeftCol;
    }

    public List<DTOMTFColumnData> getMtfRightCol() {
        return mtfRightCol;
    }

    public void setMtfRightCol(List<DTOMTFColumnData> mtfRightCol) {
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

    public List<DTOHotspotPointData> getHotspotDataList() {
        return hotspotDataList;
    }

    public void setHotspotDataList(List<DTOHotspotPointData> hotspotDataList) {
        this.hotspotDataList = hotspotDataList;
    }

    public List<DTOQuestionOptionCategory> getOptionCategories() {
        return optionCategories;
    }

    public void setOptionCategories(List<DTOQuestionOptionCategory> optionCategories) {
        this.optionCategories = optionCategories;
    }

    public List<DTOQuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<DTOQuestionOption> options) {
        this.options = options;
    }

    public DTOReadMore getReadMoreData() {
        return readMoreData;
    }

    public void setReadMoreData(DTOReadMore readMoreData) {
        this.readMoreData = readMoreData;
    }

    public boolean isRandomize() {
        return randomize;
    }

    public void setRandomize(boolean randomize) {
        this.randomize = randomize;
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

    public boolean isExitable() {
        return exitable;
    }

    public void setExitable(boolean exitable) {
        this.exitable = exitable;
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

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
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
        if (this.imageType != null)
            if (this.imageType.equalsIgnoreCase("fullscreen")) {
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

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getAttemptDateTime() {
        return attemptDateTime;
    }

    public void setAttemptDateTime(String attemptDateTime) {
        this.attemptDateTime = attemptDateTime;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(String videoLinks) {
        this.videoLinks = videoLinks;
    }

    public String getTextMaterial() {
        return textMaterial;
    }

    public void setTextMaterial(String textMaterial) {
        this.textMaterial = textMaterial;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public boolean isShowSkipButton() {
        return showSkipButton;
    }

    public void setShowSkipButton(boolean showSkipButton) {
        this.showSkipButton = showSkipButton;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public boolean isShareToSocialMedia() {
        return shareToSocialMedia;
    }

    public void setShareToSocialMedia(boolean shareToSocialMedia) {
        this.shareToSocialMedia = shareToSocialMedia;
    }

    public boolean isShowFavourite() {
        return showFavourite;
    }

    public void setShowFavourite(boolean showFavourite) {
        this.showFavourite = showFavourite;
    }

    public boolean isEnableGalleryUpload() {
        return enableGalleryUpload;
    }

    public void setEnableGalleryUpload(boolean enableGalleryUpload) {
        this.enableGalleryUpload = enableGalleryUpload;
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

    public List<DTOVideoOverlay> getListOfVideoOverlayQuestion() {
        return listOfVideoOverlayQuestion;
    }

    public void setListOfVideoOverlayQuestion(List<DTOVideoOverlay> listOfVideoOverlayQuestion) {
        this.listOfVideoOverlayQuestion = listOfVideoOverlayQuestion;
    }

    public boolean isLearningPlayNew() {
        return (this.getQuestionType() != null && (this.getQuestionType().equals(QuestionType.FILL) ||
                this.getQuestionType().equals(QuestionType.FILL_1))) ||
                (this.getQuestionCategory() != null && (this.getQuestionCategory().equals(QuestionCategory.MATCH) ||
                        this.getQuestionCategory().equals(QuestionCategory.CATEGORY) ||
                        this.getQuestionCategory().equals(QuestionCategory.HOTSPOT)));


    }

    public boolean isMediaUploadQues() {
        return this.getQuestionType() != null && this.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I) ||
                this.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A) ||
                this.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V);
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

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isRemarks() {
        return remarks;
    }

    public void setRemarks(boolean remarks) {
        this.remarks = remarks;
    }

    public boolean isUploadMedia() {
        return uploadMedia;
    }

    public void setUploadMedia(boolean uploadMedia) {
        this.uploadMedia = uploadMedia;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(boolean surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }
}