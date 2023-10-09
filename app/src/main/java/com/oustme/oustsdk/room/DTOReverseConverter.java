package com.oustme.oustsdk.room;


import com.oustme.oustsdk.layoutFour.data.CatalogueItemData;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.room.dto.DTOAdaptiveQuestionData;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOCplCompletedModel;
import com.oustme.oustsdk.room.dto.DTOCplMedia;
import com.oustme.oustsdk.room.dto.DTODiaryDetailsModel;
import com.oustme.oustsdk.room.dto.DTOFeedBackModel;
import com.oustme.oustsdk.room.dto.DTOHotspotPointData;
import com.oustme.oustsdk.room.dto.DTOImageChoice;
import com.oustme.oustsdk.room.dto.DTOLearningDiary;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.room.dto.DTOMapDataModel;
import com.oustme.oustsdk.room.dto.DTOMediaList;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOQuestionOption;
import com.oustme.oustsdk.room.dto.DTOQuestionOptionCategory;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOResourceData;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.room.dto.DTOVideoOverlay;
import com.oustme.oustsdk.room.dto.EntityNotificationData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.List;

public class DTOReverseConverter {


    static EntityCourseCardClass getCourseCard(DTOCourseCard dtoCourseCard) {
        EntityCourseCardClass courseCard = null;
        try {
            if (dtoCourseCard != null) {
                courseCard = new EntityCourseCardClass();

                courseCard.setCardBgImage(dtoCourseCard.getCardBgImage());
                courseCard.setCardBgColor(dtoCourseCard.getCardBgColor());
                courseCard.setCourseCardID(dtoCourseCard.getCourseCardID());
                courseCard.setCardId(dtoCourseCard.getCardId());
                courseCard.setCardLayout(dtoCourseCard.getCardLayout());
                courseCard.setCardQuestionColor(dtoCourseCard.getCardQuestionColor());
                courseCard.setCardSolutionColor(dtoCourseCard.getCardSolutionColor());
                courseCard.setCardTextColor(dtoCourseCard.getCardTextColor());
                courseCard.setCardType(dtoCourseCard.getCardType());
                courseCard.setCardTitle(dtoCourseCard.getCardTitle());
                courseCard.setQId(dtoCourseCard.getqId());
                courseCard.setXp(dtoCourseCard.getXp());
                courseCard.setSequence(dtoCourseCard.getSequence());
                courseCard.setCardMedia(getCourseCardMedia(dtoCourseCard.getCardMedia()));
                courseCard.setContent(dtoCourseCard.getContent());
                courseCard.setQuestionData(getQuestions(dtoCourseCard.getQuestionData()));
                courseCard.setChildCard(getCourseSolutionCard(dtoCourseCard.getChildCard()));
                courseCard.setClCode(dtoCourseCard.getClCode());
                courseCard.setBgImg(dtoCourseCard.getBgImg());
                courseCard.setLanguage(dtoCourseCard.getLanguage());
                courseCard.setCardColorScheme(getCardColorScheme(dtoCourseCard.getCardColorScheme()));
                courseCard.setQuestionType(dtoCourseCard.getQuestionType());
                courseCard.setQuestionCategory(dtoCourseCard.getQuestionCategory());
                courseCard.setReadMoreData(getReadMoreData(dtoCourseCard.getReadMoreData()));
                courseCard.setReadMoreCard(dtoCourseCard.isReadMoreCard());
                courseCard.setPotraitModeVideo(dtoCourseCard.isPotraitModeVideo());
                courseCard.setShareToSocialMedia(dtoCourseCard.isShareToSocialMedia());
                courseCard.setMappedLearningCardId(dtoCourseCard.getMappedLearningCardId());
                courseCard.setCaseStudyTitle(dtoCourseCard.getCaseStudyTitle());
                courseCard.setAudio(dtoCourseCard.getAudio());
                courseCard.setMandatoryViewTime(dtoCourseCard.getMandatoryViewTime());
                courseCard.setScormIndexFile(dtoCourseCard.getScormIndexFile());
                courseCard.setShowQuestionSymbolForQuestion(dtoCourseCard.isShowQuestionSymbolForQuestion());
                courseCard.setProceedOnWrong(dtoCourseCard.isProceedOnWrong());
                courseCard.setIfScormEventBased(dtoCourseCard.isIfScormEventBased());

                courseCard.setScormPlayerUrl(dtoCourseCard.getScormPlayerUrl());
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return courseCard;
    }

    static List<EntityCourseCardMedia> getCourseCardMedia(List<DTOCourseCardMedia> dtoCourseCardMedia1) {
        List<EntityCourseCardMedia> courseCardMediaList = null;
        if (dtoCourseCardMedia1 != null && dtoCourseCardMedia1.size() > 0) {
            courseCardMediaList = new ArrayList<>();

            for (DTOCourseCardMedia dtoCourseCardMedia : dtoCourseCardMedia1) {
                EntityCourseCardMedia courseCardMedia = new EntityCourseCardMedia();

                courseCardMedia.setMediaId(dtoCourseCardMedia.getMediaId());
                courseCardMedia.setData(dtoCourseCardMedia.getData());
                courseCardMedia.setGumletVideoUrl(dtoCourseCardMedia.getGumletVideoUrl());
                courseCardMedia.setMediaType(dtoCourseCardMedia.getMediaType());
                courseCardMedia.setMediaPrivacy(dtoCourseCardMedia.getMediaPrivacy());
                courseCardMedia.setMediaName(dtoCourseCardMedia.getMediaName());
                courseCardMedia.setMediaDescription(dtoCourseCardMedia.getMediaDescription());
                courseCardMedia.setFastForwardMedia(dtoCourseCardMedia.isFastForwardMedia());
                courseCardMedia.setMediaThumbnail(dtoCourseCardMedia.getMediaThumbnail());

                courseCardMediaList.add(courseCardMedia);
            }
        }
        return courseCardMediaList;
    }

    public static EntityQuestions getQuestions(DTOQuestions dtoQuestions) {

        EntityQuestions questions = null;

        if (dtoQuestions != null) {
            questions = new EntityQuestions();

            questions.setA(dtoQuestions.getA());
            questions.setB(dtoQuestions.getB());
            questions.setC(dtoQuestions.getC());
            questions.setD(dtoQuestions.getD());
            questions.setE(dtoQuestions.getE());
            questions.setF(dtoQuestions.getF());
            questions.setG(dtoQuestions.getG());

            questions.setSubject(dtoQuestions.getSubject());
            questions.setAllNoneFlag(dtoQuestions.isAllNoneFlag());
            questions.setTopic(dtoQuestions.getTopic());
            questions.setImageheight(dtoQuestions.getImageheight());

            questions.setQuestionCardId(dtoQuestions.getQuestionCardId());
            questions.setQuestionId(dtoQuestions.getQuestionId());

            questions.setAnswer(dtoQuestions.getAnswer());
            questions.setExitOption(dtoQuestions.getExitOption());
            questions.setBgImg(dtoQuestions.getBgImg());
            questions.setImage(dtoQuestions.getImage());
            questions.setImageCDNPath(dtoQuestions.getImageCDNPath());
            questions.setqVideoUrl(dtoQuestions.getqVideoUrl());
            questions.setGumletVideoUrl(dtoQuestions.getGumletVideoUrl());
            questions.setProceedOnWrong(dtoQuestions.isProceedOnWrong());
            questions.setVendorId(dtoQuestions.getVendorId());
            questions.setVendorDisplayName(dtoQuestions.getVendorDisplayName());
            questions.setMaxtime(dtoQuestions.getMaxtime());
            questions.setGrade(dtoQuestions.getGrade());
            questions.setImagewidth(dtoQuestions.getImagewidth());
            questions.setQuestion(dtoQuestions.getQuestion());
            questions.setFavourite(dtoQuestions.getFavourite());
            questions.setLikeUnlike(dtoQuestions.getLikeUnlike());
            questions.setUserFeedback(dtoQuestions.getUserFeedback());
            questions.setAnswerStatus(dtoQuestions.getAnswerStatus());
            questions.setStudentAnswer(dtoQuestions.getStudentAnswer());
            questions.setReattemptCount(dtoQuestions.getReattemptCount());
            questions.setSolution(dtoQuestions.getSolution());
            questions.setQuestionCategory(dtoQuestions.getQuestionCategory());
            questions.setQuestionType(dtoQuestions.getQuestionType());
            questions.setSkip(dtoQuestions.isSkip());
            questions.setAudio(dtoQuestions.getAudio());
            questions.setGameId(dtoQuestions.getGameId());
            questions.setFullScreenHotSpot(dtoQuestions.isFullScreenHotSpot());

            questions.setImageChoiceA(getImageChoiceData(dtoQuestions.getImageChoiceA()));
            questions.setImageChoiceB(getImageChoiceData(dtoQuestions.getImageChoiceB()));
            questions.setImageChoiceC(getImageChoiceData(dtoQuestions.getImageChoiceC()));
            questions.setImageChoiceD(getImageChoiceData(dtoQuestions.getImageChoiceD()));
            questions.setImageChoiceE(getImageChoiceData(dtoQuestions.getImageChoiceE()));
            questions.setImageChoiceAnswer(getImageChoiceData(dtoQuestions.getImageChoiceAnswer()));

            questions.setMtfLeftCol(getMtfColumnData(dtoQuestions.getMtfLeftCol()));
            questions.setMtfRightCol(getMtfColumnData(dtoQuestions.getMtfRightCol()));
            questions.setMtfAnswer(getMtfSolution(dtoQuestions.getMtfAnswer()));
            questions.setFillAnswers(getFillData(dtoQuestions.getFillAnswers()));

            questions.setHotspotDataList(getHotspotPointData(dtoQuestions.getHotspotDataList()));
            questions.setOptionCategories(getQuestionOptionCatagory(dtoQuestions.getOptionCategories()));
            questions.setOptions(getQuestionOptionData(dtoQuestions.getOptions()));
            questions.setReadMoreData(getReadMoreData(dtoQuestions.getReadMoreData()));
            questions.setRandomize(dtoQuestions.isRandomize());
            questions.setContainSubjective(dtoQuestions.isContainSubjective());
            questions.setSubjectiveQuestion(dtoQuestions.getSubjectiveQuestion());
            questions.setSurveyPointCount(dtoQuestions.getSurveyPointCount());
            questions.setMaxWordCount(dtoQuestions.getMaxWordCount());
            questions.setMinWordCount(dtoQuestions.getMinWordCount());
            questions.setExitable(dtoQuestions.isExitable());
            questions.setThumbsUpDn(dtoQuestions.isThumbsUpDn());
            questions.setAnswerValidationType(dtoQuestions.getAnswerValidationType());
            questions.setFieldType(dtoQuestions.getFieldType());
            questions.setDropdownType(dtoQuestions.getDropdownType());
            questions.setMandatory(dtoQuestions.isMandatory());
            questions.setExitMessage(dtoQuestions.getExitMessage());
            questions.setHotSpotThumbsUpShown(dtoQuestions.isHotSpotThumbsUpShown());
            questions.setHotSpotThumbsDownShown(dtoQuestions.isHotSpotThumbsDownShown());
            questions.setImageType(dtoQuestions.getImageType());
            questions.setThumbsUp(dtoQuestions.isThumbsUp());
            questions.setThumbsDown(dtoQuestions.isThumbsDown());
            questions.setShowSolution(dtoQuestions.isShowSolution());
            questions.setSolutionType(dtoQuestions.getSolutionType());
            questions.setScore(dtoQuestions.getScore());

            questions.setCorrect(dtoQuestions.isCorrect());
            questions.setDifficulty(dtoQuestions.getDifficulty());
            questions.setModuleId(dtoQuestions.getModuleId());
            questions.setModuleName(dtoQuestions.getModuleName());
            questions.setAttemptDateTime(dtoQuestions.getAttemptDateTime());
            questions.setBase64Image(dtoQuestions.getBase64Image());
            questions.setTimeTaken(dtoQuestions.getTimeTaken());
            questions.setFeedback(dtoQuestions.getFeedback());
            questions.setVideoLinks(dtoQuestions.getVideoLinks());
            questions.setTextMaterial(dtoQuestions.getTextMaterial());
            questions.setSyllabus(dtoQuestions.getSyllabus());
            questions.setShowSkipButton(dtoQuestions.isShowSkipButton());
            questions.setSkillName(dtoQuestions.getSkillName());
            questions.setShareToSocialMedia(dtoQuestions.isShareToSocialMedia());
            questions.setShowFavourite(dtoQuestions.isShowFavourite());

            questions.setMaxLabel(dtoQuestions.getMaxLabel());
            questions.setMinLabel(dtoQuestions.getMinLabel());
            questions.setStartRange(dtoQuestions.getStartRange());
            questions.setEndRange(dtoQuestions.getEndRange());
            questions.setEnableGalleryUpload(dtoQuestions.isEnableGalleryUpload());
            questions.setHint(dtoQuestions.getHint());
            questions.setRemarks(dtoQuestions.isRemarks());
            questions.setUploadMedia(dtoQuestions.isUploadMedia());
            questions.setDataSource(dtoQuestions.getDataSource());
            questions.setSurveyQuestion(dtoQuestions.isSurveyQuestion());

            questions.setVideoOverlayList(getVideoOverlayList(dtoQuestions.getListOfVideoOverlayQuestion()));
        }
        return questions;
    }


    static EntityImageChoice getImageChoiceData(DTOImageChoice dtoImageChoice) {
        EntityImageChoice imageChoice = null;
        if (dtoImageChoice != null) {
            imageChoice = new EntityImageChoice();

            imageChoice.setImageData(dtoImageChoice.getImageData());
            imageChoice.setImageFileName(dtoImageChoice.getImageFileName());
            imageChoice.setImageFileName_CDN_Path(dtoImageChoice.getImageFileName_CDN_Path());
        }
        return imageChoice;
    }


    static EntityReadMore getReadMoreData(DTOReadMore dtoReadMore) {
        EntityReadMore readMore = null;

        if (dtoReadMore != null) {
            readMore = new EntityReadMore();

            readMore.setCardId(dtoReadMore.getCardId());
            readMore.setCourseId(dtoReadMore.getCourseId());
            readMore.setData(dtoReadMore.getData());
            readMore.setGumletVideoUrl(dtoReadMore.getData());
            readMore.setDisplayText(dtoReadMore.getDisplayText());
            readMore.setLevelId(dtoReadMore.getLevelId());
            readMore.setRmId(dtoReadMore.getRmId());
            readMore.setScope(dtoReadMore.getScope());
            readMore.setType(dtoReadMore.getType());
        }

        return readMore;
    }

    static List<EntityMTFColumnData> getMtfColumnData(List<DTOMTFColumnData> dtomtfColumnDataList) {
        List<EntityMTFColumnData> columnDataList = null;
        if (dtomtfColumnDataList != null && dtomtfColumnDataList.size() > 0) {
            columnDataList = new ArrayList<>();

            for (DTOMTFColumnData dtomtfColumnData : dtomtfColumnDataList) {
                EntityMTFColumnData mtfColumnData = new EntityMTFColumnData();
                if (dtomtfColumnData != null) {
                    mtfColumnData.setMtfColData(dtomtfColumnData.getMtfColData());
                    mtfColumnData.setMtfColData_CDN(dtomtfColumnData.getMtfColData());
                    mtfColumnData.setMtfColDataId(dtomtfColumnData.getMtfColDataId());
                    mtfColumnData.setMtfColMediaType(dtomtfColumnData.getMtfColMediaType());
                    columnDataList.add(mtfColumnData);
                }
            }
        }
        return columnDataList;
    }

    static List<String> getMtfSolution(List<String> mtfSolution) {
        List<String> dtoMtfSolutions = null;
        if (mtfSolution != null && mtfSolution.size() > 0) {
            dtoMtfSolutions = new ArrayList<>();
            dtoMtfSolutions.addAll(mtfSolution);
        }
        return dtoMtfSolutions;
    }

    private static List<EntityHotspotPointData> getHotspotPointData(List<DTOHotspotPointData> dtoHotspotPointDataList) {
        List<EntityHotspotPointData> hotspotPointData = null;
        if (dtoHotspotPointDataList != null && dtoHotspotPointDataList.size() > 0) {
            hotspotPointData = new ArrayList<>();

            for (DTOHotspotPointData dtoHotspotPointData2 : dtoHotspotPointDataList) {
                EntityHotspotPointData hotspotPoint = new EntityHotspotPointData();
                hotspotPoint.setHeight(dtoHotspotPointData2.getHeight());
                hotspotPoint.setHpLabel(dtoHotspotPointData2.getHpLabel());
                hotspotPoint.setHpQuestion(dtoHotspotPointData2.getHpQuestion());
                hotspotPoint.setStartX(dtoHotspotPointData2.getStartX());
                hotspotPoint.setStartY(dtoHotspotPointData2.getStartY());
                hotspotPoint.setWidth(dtoHotspotPointData2.getWidth());
                hotspotPoint.setAnswer(dtoHotspotPointData2.isAnswer());
                hotspotPoint.setHpAdaptiveCardId(dtoHotspotPointData2.getHpAdaptiveCardId());

                hotspotPointData.add(hotspotPoint);
            }
        }
        return hotspotPointData;
    }

    private static List<EntityQuestionOption> getQuestionOptionData(List<DTOQuestionOption> dtoQuestionOptionList) {

        List<EntityQuestionOption> questionOptions = null;

        if (dtoQuestionOptionList != null) {
            questionOptions = new ArrayList<>();

            for (DTOQuestionOption dtoQuestionOption : dtoQuestionOptionList) {
                EntityQuestionOption questionOption = new EntityQuestionOption();
                questionOption.setData(dtoQuestionOption.getData());
                questionOption.setBitmapData(dtoQuestionOption.getBitmapData());
                questionOption.setOptionCategory(dtoQuestionOption.getOptionCategory());
                questionOption.setType(dtoQuestionOption.getType());
                questionOption.setData_CDN(dtoQuestionOption.getData());

                questionOptions.add(questionOption);
            }
        }

        return questionOptions;
    }

    private static List<EntityQuestionOptionCategory> getQuestionOptionCatagory(List<DTOQuestionOptionCategory> dtoQuestionOptionCategoryList) {

        List<EntityQuestionOptionCategory> categoryOptions = null;
        if (dtoQuestionOptionCategoryList != null) {
            categoryOptions = new ArrayList<>();

            for (DTOQuestionOptionCategory dtoQuestionOptionCategory : dtoQuestionOptionCategoryList) {
                EntityQuestionOptionCategory optionCategory = new EntityQuestionOptionCategory();
                optionCategory.setData(dtoQuestionOptionCategory.getData());
                optionCategory.setType(dtoQuestionOptionCategory.getType());
                optionCategory.setCode(dtoQuestionOptionCategory.getCode());
                optionCategory.setData_CDN(dtoQuestionOptionCategory.getData());

                categoryOptions.add(optionCategory);
            }
        }

        return categoryOptions;
    }

    private static List<String> getFillData(List<String> fillAnswers) {

        List<String> fillDatas = null;

        if (fillAnswers != null) {
            fillDatas = new ArrayList<>();

            for (String fillAnswer : fillAnswers) {
                fillDatas.add(fillAnswer);
            }
        }
        return fillDatas;
    }

    private static List<EntityVideoOverlay> getVideoOverlayList(List<DTOVideoOverlay> listOfVideoOverlayQuestion) {
        List<EntityVideoOverlay> entityVideoOverlays = null;
        if (listOfVideoOverlayQuestion != null) {
            entityVideoOverlays = new ArrayList<>();

            for (DTOVideoOverlay dtoVideoOverlay : listOfVideoOverlayQuestion) {
                EntityVideoOverlay videoOverlay = new EntityVideoOverlay();
                videoOverlay.setParentQuestionId(dtoVideoOverlay.getParentQuestionId());
                videoOverlay.setChildQuestionId(dtoVideoOverlay.getChildQuestionId());
                videoOverlay.setTimeDuration(dtoVideoOverlay.getTimeDuration());
                videoOverlay.setCardSequence(dtoVideoOverlay.getCardSequence());
                videoOverlay.setChildCoursecardId(dtoVideoOverlay.getChildCoursecardId());
                videoOverlay.setChildQuestionCourseCard(getCourseCard(dtoVideoOverlay.getChildQuestionCourseCard()));

                entityVideoOverlays.add(videoOverlay);
            }
        }
        return entityVideoOverlays;
    }

    private static EntityCourseSolutionCard getCourseSolutionCard(DTOCourseSolutionCard dtoCourseSolutionCard) {
        EntityCourseSolutionCard courseSolutionCard = null;
        if (dtoCourseSolutionCard != null) {
            courseSolutionCard = new EntityCourseSolutionCard();

            courseSolutionCard.setCardBgColor(dtoCourseSolutionCard.getCardBgColor());
            courseSolutionCard.setCardId(dtoCourseSolutionCard.getCardId());
            courseSolutionCard.setCardLayout(dtoCourseSolutionCard.getCardLayout());
            courseSolutionCard.setCardQuestionColor(dtoCourseSolutionCard.getCardQuestionColor());
            courseSolutionCard.setCardSolutionColor(dtoCourseSolutionCard.getCardSolutionColor());
            courseSolutionCard.setCardTextColor(dtoCourseSolutionCard.getCardTextColor());
            courseSolutionCard.setCardType(dtoCourseSolutionCard.getCardType());
            courseSolutionCard.setContent(dtoCourseSolutionCard.getContent());
            courseSolutionCard.setRewardOc(dtoCourseSolutionCard.getRewardOc());
            courseSolutionCard.setSequence(dtoCourseSolutionCard.getSequence());
            courseSolutionCard.setCardColorScheme(getCardColorScheme(dtoCourseSolutionCard.getCardColorScheme()));
        }
        return courseSolutionCard;
    }

    static EntityCardColorScheme getCardColorScheme(DTOCardColorScheme dtoCardColorScheme) {
        EntityCardColorScheme cardColorScheme = null;
        if (dtoCardColorScheme != null) {
            cardColorScheme = new EntityCardColorScheme();

            cardColorScheme.setBgImage(dtoCardColorScheme.getBgImage());
            cardColorScheme.setContentColor(dtoCardColorScheme.getContentColor());
            cardColorScheme.setIconColor(dtoCardColorScheme.getIconColor());
            cardColorScheme.setLevelNameColor(dtoCardColorScheme.getLevelNameColor());
            cardColorScheme.setOptionColor(dtoCardColorScheme.getOptionColor());
            cardColorScheme.setTitleColor(dtoCardColorScheme.getTitleColor());
        }
        return cardColorScheme;
    }


    public static EntityQuestions getQuestions(DTOAdaptiveQuestionData dtoAdaptiveQuestion) {
        EntityQuestions entityQuestions = null;
        try {
            entityQuestions = new EntityQuestions();
            if (entityQuestions != null) {
                entityQuestions.setA(dtoAdaptiveQuestion.getA());
                entityQuestions.setB(dtoAdaptiveQuestion.getB());
                entityQuestions.setC(dtoAdaptiveQuestion.getC());
                entityQuestions.setD(dtoAdaptiveQuestion.getD());
                entityQuestions.setE(dtoAdaptiveQuestion.getE());
                entityQuestions.setF(dtoAdaptiveQuestion.getF());
                entityQuestions.setG(dtoAdaptiveQuestion.getG());
                entityQuestions.setAllNoneFlag(dtoAdaptiveQuestion.isAllNoneFlag());
                entityQuestions.setAnswer(dtoAdaptiveQuestion.getAnswer());
                entityQuestions.setAnswerStatus(dtoAdaptiveQuestion.getAnswerStatus());
                entityQuestions.setAudio(dtoAdaptiveQuestion.getAudio());
                entityQuestions.setFavourite(dtoAdaptiveQuestion.getFavourite());
                entityQuestions.setGrade(dtoAdaptiveQuestion.getGrade());
                entityQuestions.setImage(dtoAdaptiveQuestion.getImage());
                entityQuestions.setImageCDNPath(dtoAdaptiveQuestion.getImageCDNPath());
                entityQuestions.setqVideoUrl(dtoAdaptiveQuestion.getqVideoUrl());
                entityQuestions.setGumletVideoUrl(dtoAdaptiveQuestion.getGumletVideoUrl());
                entityQuestions.setImagewidth(dtoAdaptiveQuestion.getImagewidth());
                entityQuestions.setImageheight(dtoAdaptiveQuestion.getImageheight());
                entityQuestions.setLikeUnlike(dtoAdaptiveQuestion.getLikeUnlike());
                entityQuestions.setMaxtime(dtoAdaptiveQuestion.getMaxtime());
                entityQuestions.setQuestion(dtoAdaptiveQuestion.getQuestion());
                entityQuestions.setQuestionType(dtoAdaptiveQuestion.getQuestionType());
                entityQuestions.setQuestionCategory(dtoAdaptiveQuestion.getQuestionCategory());
                entityQuestions.setQuestionId(dtoAdaptiveQuestion.getQuestionId());
                entityQuestions.setVendorId(dtoAdaptiveQuestion.getVendorId());
                entityQuestions.setVendorDisplayName(dtoAdaptiveQuestion.getVendorDisplayName());
                entityQuestions.setUserFeedback(dtoAdaptiveQuestion.getUserFeedback());
                entityQuestions.setTopic(dtoAdaptiveQuestion.getTopic());
                entityQuestions.setSubject(dtoAdaptiveQuestion.getSubject());
                entityQuestions.setStudentAnswer(dtoAdaptiveQuestion.getStudentAnswer());
                entityQuestions.setSolution(dtoAdaptiveQuestion.getSolution());
                entityQuestions.setSkip(dtoAdaptiveQuestion.isSkip());
                entityQuestions.setRandomize(dtoAdaptiveQuestion.isRandomize());
                entityQuestions.setReattemptCount(dtoAdaptiveQuestion.getReattemptCount());
                entityQuestions.setMaxWordCount(dtoAdaptiveQuestion.getMaxWordCount());
                entityQuestions.setMinWordCount(dtoAdaptiveQuestion.getMinWordCount());
                entityQuestions.setExitable(dtoAdaptiveQuestion.isExitable());
                entityQuestions.setExitOption(dtoAdaptiveQuestion.getExitOption());
                entityQuestions.setExitMessage(dtoAdaptiveQuestion.getExitMessage());
                entityQuestions.setMandatory(dtoAdaptiveQuestion.isMandatory());
                entityQuestions.setFieldType(dtoAdaptiveQuestion.getFieldType());
                entityQuestions.setAnswerValidationType(dtoAdaptiveQuestion.getAnswerValidationType());
                entityQuestions.setDropdownType(dtoAdaptiveQuestion.getDropdownType());
                entityQuestions.setThumbsUpDn(dtoAdaptiveQuestion.isThumbsUpDn());

                entityQuestions.setImageType(dtoAdaptiveQuestion.getImageType());
                entityQuestions.setShowSolution(dtoAdaptiveQuestion.isShowSolution());
                entityQuestions.setSolutionType(dtoAdaptiveQuestion.getSolutionType());
                entityQuestions.setHotSpotThumbsDownShown(dtoAdaptiveQuestion.isHotSpotThumbsDownShown());
                entityQuestions.setHotSpotThumbsUpShown(dtoAdaptiveQuestion.isHotSpotThumbsUpShown());
                entityQuestions.setThumbsDown(dtoAdaptiveQuestion.isThumbsDown());
                entityQuestions.setThumbsUp(dtoAdaptiveQuestion.isThumbsUp());
                entityQuestions.setScore(dtoAdaptiveQuestion.getScore());


                entityQuestions.setSubjectiveQuestion(dtoAdaptiveQuestion.getSubjectiveQuestion());
                entityQuestions.setContainSubjective(dtoAdaptiveQuestion.isContainSubjective());
                entityQuestions.setSurveyPointCount(dtoAdaptiveQuestion.getSurveyPointCount());

                entityQuestions.setImageChoiceA(getImageChoiceData(dtoAdaptiveQuestion.getImageChoiceA()));
                entityQuestions.setImageChoiceB(getImageChoiceData(dtoAdaptiveQuestion.getImageChoiceB()));
                entityQuestions.setImageChoiceC(getImageChoiceData(dtoAdaptiveQuestion.getImageChoiceC()));
                entityQuestions.setImageChoiceD(getImageChoiceData(dtoAdaptiveQuestion.getImageChoiceD()));
                entityQuestions.setImageChoiceE(getImageChoiceData(dtoAdaptiveQuestion.getImageChoiceD()));
                entityQuestions.setImageChoiceAnswer(getImageChoiceData(dtoAdaptiveQuestion.getImageChoiceAnswer()));

                entityQuestions.setReadMoreData(getReadMoreData(dtoAdaptiveQuestion.getReadMoreData()));
                entityQuestions.setMtfLeftCol(getMtfColumnData(dtoAdaptiveQuestion.getMtfLeftCol()));
                entityQuestions.setMtfRightCol(getMtfColumnData(dtoAdaptiveQuestion.getMtfRightCol()));
                entityQuestions.setMtfAnswer(getMtfSolution(dtoAdaptiveQuestion.getMtfAnswer()));

                entityQuestions.setHotspotDataList(getHotspotPointData(dtoAdaptiveQuestion.getHotspotDataList()));

                entityQuestions.setOptions(getQuestionOptionData(dtoAdaptiveQuestion.getOptions()));

                entityQuestions.setOptionCategories(getQuestionOptionCatagory(dtoAdaptiveQuestion.getOptionCategories()));


                entityQuestions.setFillAnswers(getFillData(dtoAdaptiveQuestion.getFillAnswers()));

                entityQuestions.setEnableGalleryUpload(dtoAdaptiveQuestion.isEnableGalleryUpload());
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            OustSdkTools.showToast("something went wrong!");

        }

        return entityQuestions;
    }

    public static EntityUserCourseData getUserCourseData(DTOUserCourseData dtoUserCourseData) {
        EntityUserCourseData userCourseData = null;
        if (dtoUserCourseData != null) {
            userCourseData = new EntityUserCourseData();
            userCourseData.setId(dtoUserCourseData.getId());
            userCourseData.setCurrentLevel(dtoUserCourseData.getCurrentLevel());
            userCourseData.setCurrentCard(dtoUserCourseData.getCurrentCard());
            userCourseData.setPresentageComplete(dtoUserCourseData.getPresentageComplete());
            userCourseData.setTotalOc(dtoUserCourseData.getTotalOc());
            userCourseData.setTotalCards(dtoUserCourseData.getTotalCards());
            userCourseData.setSaved(dtoUserCourseData.isSaved());
            userCourseData.setAlarmSet(dtoUserCourseData.isAlarmSet());
            userCourseData.setUserLevelDataList(getUserLevelDataList(dtoUserCourseData.getUserLevelDataList()));
            userCourseData.setCurrentCompleteLevel(dtoUserCourseData.getCurrentCompleteLevel());
            userCourseData.setLastCompleteLevel(dtoUserCourseData.getLastCompleteLevel());
            userCourseData.setCourseComplete(dtoUserCourseData.isCourseComplete());
            userCourseData.setMyCourseRating(dtoUserCourseData.getMyCourseRating());
            userCourseData.setLastPlayedLevel(dtoUserCourseData.getLastPlayedLevel());
            userCourseData.setAskedVideoStorePermission(dtoUserCourseData.isAskedVideoStorePermission());
            userCourseData.setVideoDownloadPermissionAllowed(dtoUserCourseData.isVideoDownloadPermissionAllowed());
            userCourseData.setDeleteDataAfterComplete(dtoUserCourseData.isDeleteDataAfterComplete());
            userCourseData.setDownloading(dtoUserCourseData.isDownloading());
            userCourseData.setDownloadCompletePercentage(dtoUserCourseData.getDownloadCompletePercentage());
            userCourseData.setUpdateTS(dtoUserCourseData.getUpdateTS());
            userCourseData.setAcknowledged(dtoUserCourseData.isAcknowledged());
            userCourseData.setCurrentLevelId(dtoUserCourseData.getCurrentLevelId());
            userCourseData.setMappedAssessmentPassed(dtoUserCourseData.isMappedAssessmentPassed());
            userCourseData.setBulletinLastUpdatedTime(dtoUserCourseData.getBulletinLastUpdatedTime());
            userCourseData.setCourseCompleted(dtoUserCourseData.isCourseCompleted());
            userCourseData.setAddedOn(dtoUserCourseData.getAddedOn());
            userCourseData.setEnrollmentDate(dtoUserCourseData.getEnrollmentDate());
            userCourseData.setMappedAssessmentCompleted(dtoUserCourseData.isMappedAssessmentCompleted());
        }

        return userCourseData;
    }

    private static List<EntityUserLevelData> getUserLevelDataList(List<DTOUserLevelData> dtoUserLevelDataList) {

        List<EntityUserLevelData> entityUserLevelDataList = null;

        if (dtoUserLevelDataList != null) {
            entityUserLevelDataList = new ArrayList<>();
            for (DTOUserLevelData dtoUserLevelData : dtoUserLevelDataList) {
                EntityUserLevelData userLevelData1 = new EntityUserLevelData();

                userLevelData1.setTotalTime(dtoUserLevelData.getTotalTime());
                userLevelData1.setTotalOc(dtoUserLevelData.getTotalOc());
                userLevelData1.setLevelId(dtoUserLevelData.getLevelId());
                userLevelData1.setSequece(dtoUserLevelData.getSequece());
                userLevelData1.setXp(dtoUserLevelData.getXp());
                userLevelData1.setTimeStamp(dtoUserLevelData.getTimeStamp());
                userLevelData1.setUserCardDataList(getUserCardData(dtoUserLevelData.getUserCardDataList()));
                userLevelData1.setDownloading(dtoUserLevelData.isDownloading());
                userLevelData1.setCompletePercentage(dtoUserLevelData.getCompletePercentage());
                userLevelData1.setCurrentCardNo(dtoUserLevelData.getCurrentCardNo());
                userLevelData1.setLocked(dtoUserLevelData.isLocked());
                userLevelData1.setLevelCompleted(dtoUserLevelData.isLevelCompleted());
                userLevelData1.setDownloadedAllCards(dtoUserLevelData.isDownloadedAllCards());
                userLevelData1.setIslastCardComplete(dtoUserLevelData.isLastCardComplete());

                entityUserLevelDataList.add(userLevelData1);
            }
        }
        return entityUserLevelDataList;
    }

    private static List<EntityUserCardData> getUserCardData(List<DTOUserCardData> dtoUserCardDataList) {
        List<EntityUserCardData> userCardDataList = null;

        if (dtoUserCardDataList != null) {
            userCardDataList = new ArrayList<>();
            for (DTOUserCardData dtoUserCardData : dtoUserCardDataList) {
                EntityUserCardData userCardData = new EntityUserCardData();
                userCardData.setOc(dtoUserCardData.getOc());
                userCardData.setResponceTime(dtoUserCardData.getResponceTime());
                userCardData.setCardId(dtoUserCardData.getCardId());
                userCardData.setNoofAttempt(dtoUserCardData.getNoofAttempt());
                userCardData.setCardCompleted(dtoUserCardData.isCardCompleted());
                userCardData.setCardViewInterval(dtoUserCardData.getCardViewInterval());
                userCardDataList.add(userCardData);
            }
        }

        return userCardDataList;
    }

    static EntityCplMedia getCplMedia(DTOCplMedia dtoCplMedia) {
        EntityCplMedia entityCplMedia = null;
        if (dtoCplMedia != null) {
            entityCplMedia = new EntityCplMedia();
            entityCplMedia.setId(dtoCplMedia.getId());
            entityCplMedia.setFolderPath(dtoCplMedia.getFolderPath());
            entityCplMedia.setName(dtoCplMedia.getName());
            entityCplMedia.setFileName(dtoCplMedia.getFileName());
            entityCplMedia.setCplId(dtoCplMedia.getCplId());
        }
        return entityCplMedia;
    }

    public static EntityCplCompletedModel getCplCompletedModel(DTOCplCompletedModel dtoCplCompletedModel) {
        EntityCplCompletedModel cplCompletedModel = null;
        if (dtoCplCompletedModel != null) {
            cplCompletedModel = new EntityCplCompletedModel();
            cplCompletedModel.setId(dtoCplCompletedModel.getId());
            cplCompletedModel.setCompletedOn(dtoCplCompletedModel.getCompletedOn());
            cplCompletedModel.setType(dtoCplCompletedModel.getType());
            cplCompletedModel.setCompleted(dtoCplCompletedModel.isCompleted());
            cplCompletedModel.setSubmittedToServer(dtoCplCompletedModel.isSubmittedToServer());
            cplCompletedModel.setMLearningStatus(dtoCplCompletedModel.ismLearningStatus());
            cplCompletedModel.setMRetryCount(dtoCplCompletedModel.getmRetryCount());
        }
        return cplCompletedModel;
    }

    public static EntityFeedBackModel getFeedBackModel(DTOFeedBackModel dtoFeedBackModel) {
        EntityFeedBackModel feedBackModel = null;
        if (dtoFeedBackModel != null) {
            feedBackModel = new EntityFeedBackModel();
            feedBackModel.setCountry(dtoFeedBackModel.getCountry());
            feedBackModel.setCity(dtoFeedBackModel.getCity());
            feedBackModel.setArea(dtoFeedBackModel.getArea());
            feedBackModel.setLongitude(dtoFeedBackModel.getLongitude());
            feedBackModel.setLatitude(dtoFeedBackModel.getLatitude());
            feedBackModel.setMobile(dtoFeedBackModel.getMobile());
            feedBackModel.setPurpose(dtoFeedBackModel.getPurpose());
            feedBackModel.setComments(dtoFeedBackModel.getComments());
            feedBackModel.setPhoto(dtoFeedBackModel.getPhoto());
            feedBackModel.setStudentid(dtoFeedBackModel.getStudentid());
        }
        return feedBackModel;
    }

    public static EntityUserFeeds getUserFeedsModel(DTOUserFeeds.FeedList dtoUserFeeds) {
        EntityUserFeeds entityUserFeeds = null;
        try {
            if (dtoUserFeeds != null) {
                entityUserFeeds = new EntityUserFeeds();
                entityUserFeeds.setFeedId(dtoUserFeeds.getFeedId());
                entityUserFeeds.setHeader(dtoUserFeeds.getHeader());
                entityUserFeeds.setContent(dtoUserFeeds.getContent());
                entityUserFeeds.setFeedType(dtoUserFeeds.getFeedType());
                entityUserFeeds.setBannerImg(dtoUserFeeds.getBannerImg());
                entityUserFeeds.setModuleId(dtoUserFeeds.getModuleId());
                entityUserFeeds.setModuleType(dtoUserFeeds.getModuleType());
                entityUserFeeds.setMediaType(dtoUserFeeds.getMediaType());
                entityUserFeeds.setSpecialFeed(dtoUserFeeds.isSpecialFeed());
                entityUserFeeds.setDistributedOn(dtoUserFeeds.getDistributedOn());
                entityUserFeeds.setSpecialFeed(dtoUserFeeds.isSpecialFeed());
                entityUserFeeds.setMediaData(dtoUserFeeds.getMediaData());
                entityUserFeeds.setLiked(dtoUserFeeds.isLiked());
                entityUserFeeds.setClicked(dtoUserFeeds.isClicked());
                entityUserFeeds.setFeedViewed(dtoUserFeeds.isFeedViewed());
                entityUserFeeds.setCommented(dtoUserFeeds.isCommented());
                entityUserFeeds.setShareable(dtoUserFeeds.isShareable());
                entityUserFeeds.setNumComments(dtoUserFeeds.getNumComments());
                entityUserFeeds.setNumLikes(dtoUserFeeds.getNumLikes());
                entityUserFeeds.setFileName(dtoUserFeeds.getFileName());
                entityUserFeeds.setFileType(dtoUserFeeds.getFileType());
                entityUserFeeds.setFeedExpiry(dtoUserFeeds.getFeedExpiry());
                entityUserFeeds.setImageUrl(dtoUserFeeds.getImageUrl());
                entityUserFeeds.setMediaThumbnail(dtoUserFeeds.getMediaThumbnail());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return entityUserFeeds;
    }

    public static EntityMapDataModel getMapDataModel(DTOMapDataModel dtoMapDataModel) {
        EntityMapDataModel entityMapDataModel = null;
        if (dtoMapDataModel != null) {
            entityMapDataModel = new EntityMapDataModel();
            entityMapDataModel.setId(dtoMapDataModel.getId());
            entityMapDataModel.setDataMap(dtoMapDataModel.getDataMap());
            entityMapDataModel.setLandingDataMap(dtoMapDataModel.getLandingDataMap());
        }
        return entityMapDataModel;
    }

    public static EntityLearningDiary getLearningDiary(DTOLearningDiary diaryDetailsModel) {
        EntityLearningDiary learningDiary = null;
        if (diaryDetailsModel != null) {
            learningDiary = new EntityLearningDiary();
            learningDiary.setUid(diaryDetailsModel.getUid());
            learningDiary.set_newsList(getDiaryDetailsModelList(diaryDetailsModel.get_newsList()));
        }
        return learningDiary;
    }

    private static List<EntityDiaryDetailsModel> getDiaryDetailsModelList(List<DTODiaryDetailsModel> newsList) {
        List<EntityDiaryDetailsModel> diaryDetailsModelList = null;
        if (newsList != null) {
            diaryDetailsModelList = new ArrayList<>();
            for (DTODiaryDetailsModel dtoDiaryDetailsModel : newsList) {
                diaryDetailsModelList.add(getDiaryDetailsModel(dtoDiaryDetailsModel));
            }
        }

        return diaryDetailsModelList;
    }

    private static EntityDiaryDetailsModel getDiaryDetailsModel(DTODiaryDetailsModel dtoDiaryDetailsModel) {
        EntityDiaryDetailsModel diaryDetailsModel = null;
        if (dtoDiaryDetailsModel != null) {
            diaryDetailsModel = new EntityDiaryDetailsModel();
            diaryDetailsModel.setEndTS(dtoDiaryDetailsModel.getEndTS());
            diaryDetailsModel.setStartTS(dtoDiaryDetailsModel.getStartTS());
            diaryDetailsModel.setUpdateTS(dtoDiaryDetailsModel.getUpdateTS());
            diaryDetailsModel.setEditable(dtoDiaryDetailsModel.isEditable());
            diaryDetailsModel.setIsdeleted(dtoDiaryDetailsModel.isIsdeleted());
            diaryDetailsModel.setDefaultBanner(dtoDiaryDetailsModel.getDefaultBanner());
            diaryDetailsModel.setmBanner(dtoDiaryDetailsModel.getmBanner());
            diaryDetailsModel.setApprovalStatus(dtoDiaryDetailsModel.getApprovalStatus());
            diaryDetailsModel.setType(dtoDiaryDetailsModel.getType());
            diaryDetailsModel.setFileName(dtoDiaryDetailsModel.getFileName());
            diaryDetailsModel.setFileSize(dtoDiaryDetailsModel.getFileSize());
            diaryDetailsModel.setUserLD_Id(dtoDiaryDetailsModel.getUserLD_Id());
            diaryDetailsModel.setActivityName(dtoDiaryDetailsModel.getActivityName());
            diaryDetailsModel.setComments(dtoDiaryDetailsModel.getComments());
            diaryDetailsModel.setSortingTime(dtoDiaryDetailsModel.getSortingTime());
            diaryDetailsModel.setLearningDiaryMediaDataList(getLearningDiaryMediaDataList(dtoDiaryDetailsModel.getLearningDiaryMediaDataList()));
        }
        return diaryDetailsModel;
    }

    private static List<EntityMediaList> getLearningDiaryMediaDataList(List<DTOMediaList> dtoMediaLists) {
        List<EntityMediaList> entityMediaLists = null;
        if (dtoMediaLists != null) {
            entityMediaLists = new ArrayList<>();
            for (DTOMediaList mediaList : dtoMediaLists) {
                entityMediaLists.add(getMediaList(mediaList));
            }
        }
        return entityMediaLists;
    }

    private static EntityMediaList getMediaList(DTOMediaList dtoMediaList) {
        EntityMediaList entityMediaList = null;
        if (dtoMediaList != null) {
            entityMediaList = new EntityMediaList();
            entityMediaList.setFileName(dtoMediaList.getFileName());
            entityMediaList.setFileSize(dtoMediaList.getFileSize());
            entityMediaList.setFileType(dtoMediaList.getFileType());
            entityMediaList.setIsdeleted(dtoMediaList.isIsdeleted());
            entityMediaList.setUpdateTS(dtoMediaList.getUpdateTS());
            entityMediaList.setUpdatedBy(dtoMediaList.getUpdatedBy());
            entityMediaList.setUserLdMedia_Id(dtoMediaList.getUserLdMedia_Id());
        }
        return entityMediaList;
    }

    static EntityResourceData getResourceDataModel(DTOResourceData dtoResourceData) {
        EntityResourceData resourceData = null;
        if (dtoResourceData != null) {
            resourceData = new EntityResourceData();
            resourceData.setId(dtoResourceData.getId());
            resourceData.setFilename(dtoResourceData.getFilename());
            resourceData.setTimeStamp(dtoResourceData.getTimeStamp());
            resourceData.setFile(dtoResourceData.getFile());
        }

        return resourceData;
    }


    public static EntityNewFeed getNewFeed(DTONewFeed dtoNewFeed) {
        EntityNewFeed entityNewFeed = null;
        if (dtoNewFeed != null) {
            entityNewFeed = new EntityNewFeed();

            entityNewFeed.setHeader(dtoNewFeed.getHeader());
            entityNewFeed.setContent(dtoNewFeed.getContent());
            entityNewFeed.setIcon(dtoNewFeed.getIcon());
            entityNewFeed.setTimestamp("" + dtoNewFeed.getTimestamp());
            entityNewFeed.setLink(dtoNewFeed.getLink());
            entityNewFeed.setBtntext(dtoNewFeed.getBtntext());
            entityNewFeed.setType(dtoNewFeed.getType());
            entityNewFeed.setImageUrl(dtoNewFeed.getImageUrl());

            entityNewFeed.setFeedType(dtoNewFeed.getFeedType());

            entityNewFeed.setNewBtnText(dtoNewFeed.getNewBtnText());
            entityNewFeed.setEventItd(dtoNewFeed.getEventItd());
            entityNewFeed.setModuleId(dtoNewFeed.getModuleId());
            entityNewFeed.setGroupId(dtoNewFeed.getGroupId());
            entityNewFeed.setAssessmentId(dtoNewFeed.getAssessmentId());
            entityNewFeed.setCourseId(dtoNewFeed.getCourseId());
            entityNewFeed.setId(dtoNewFeed.getId());

            entityNewFeed.setCourseCardClass(getCourseCard(dtoNewFeed.getCourseCardClass()));
            entityNewFeed.setSurveyIntroCourseCardClass(getCourseCard(dtoNewFeed.getSurveyIntroCourseCardClass()));
            entityNewFeed.setSurveyResultCourseCardClass(getCourseCard(dtoNewFeed.getSurveyResultCourseCardClass()));

            entityNewFeed.setTempSignedImage(dtoNewFeed.getTempSignedImage());
            entityNewFeed.setFeedId(dtoNewFeed.getFeedId());
            entityNewFeed.setLandingBannerMessage(dtoNewFeed.getLandingBannerMessage());
            entityNewFeed.setCommented(dtoNewFeed.isCommented());
            entityNewFeed.setLiked(dtoNewFeed.isLiked());
            entityNewFeed.setShared(dtoNewFeed.isShared());
            entityNewFeed.setFeedPriority(dtoNewFeed.getFeedPriority());
            entityNewFeed.setListenerSet(dtoNewFeed.isListenerSet());
            entityNewFeed.setNumLikes(dtoNewFeed.getNumLikes());
            entityNewFeed.setNumComments(dtoNewFeed.getNumComments());
            entityNewFeed.setNumShares(dtoNewFeed.getNumShares());
            entityNewFeed.setExpiryTime(dtoNewFeed.getExpiryTime());
            entityNewFeed.setCplId(dtoNewFeed.getCplId());
            entityNewFeed.setLocationType(dtoNewFeed.getLocationType());
            entityNewFeed.setFeedTag(dtoNewFeed.getFeedTag());
            entityNewFeed.setSharable(dtoNewFeed.isSharable());

            entityNewFeed.setDeepLinkInfo(getDeepLinkInfo(dtoNewFeed.getDeepLinkInfo()));

            entityNewFeed.setParentCplId(dtoNewFeed.getParentCplId());
            entityNewFeed.setCommentble(dtoNewFeed.isCommentble());
            entityNewFeed.setLikeble(dtoNewFeed.isLikeble());
            entityNewFeed.setMSpecialFeedStartText(dtoNewFeed.getmSpecialFeedStartText());
            entityNewFeed.setTitleVisible(dtoNewFeed.isTitleVisible());
            entityNewFeed.setDescVisible(dtoNewFeed.isDescVisible());

            entityNewFeed.setClicked(dtoNewFeed.isClicked());
            entityNewFeed.setFeedViewed(dtoNewFeed.isFeedViewed());

            entityNewFeed.setPlaying(dtoNewFeed.isPlaying());
            entityNewFeed.setVideoSource(dtoNewFeed.getVideoSource());
            entityNewFeed.setAutoPlay(dtoNewFeed.isAutoPlay());
            entityNewFeed.setFileName(dtoNewFeed.getFileName());
            entityNewFeed.setFileType(dtoNewFeed.getFileType());
            entityNewFeed.setFileType(dtoNewFeed.getFileType());
        }
        return entityNewFeed;
    }

    private static EntityDeepLinkInfo getDeepLinkInfo(DTONewFeed.DeepLinkInfo deepLinkInfo) {
        EntityDeepLinkInfo entityDeepLinkInfo = null;
        if (deepLinkInfo != null) {
            entityDeepLinkInfo = new EntityDeepLinkInfo();
            entityDeepLinkInfo.setAssessmentId(deepLinkInfo.getAssessmentId());
            entityDeepLinkInfo.setButtonLabel(deepLinkInfo.getButtonLabel());
            entityDeepLinkInfo.setCardId(deepLinkInfo.getCardId());
            entityDeepLinkInfo.setCourseId(deepLinkInfo.getCourseId());
            entityDeepLinkInfo.setContentId(deepLinkInfo.getContentId());
            entityDeepLinkInfo.setCplId(deepLinkInfo.getCplId());
        }
        return entityDeepLinkInfo;
    }


    public static List<EntityCatalogueItemData> getCatalogueItemDataList(List<CatalogueItemData> catalogueItemDataList) {
        List<EntityCatalogueItemData> entitycatalogueItemDataList = new ArrayList<>();
        try {
            if (catalogueItemDataList != null) {
                for (CatalogueItemData itemData : catalogueItemDataList) {
                    entitycatalogueItemDataList.add(getCatalogueItemData(itemData));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return entitycatalogueItemDataList;
    }

    public static EntityCatalogueItemData getCatalogueItemData(CatalogueItemData catalogueItemData) {
        EntityCatalogueItemData entityCatalogueItemData = new EntityCatalogueItemData();
        entityCatalogueItemData.setName(catalogueItemData.getName());
        entityCatalogueItemData.setIcon(catalogueItemData.getIcon());
        entityCatalogueItemData.setTrendingPoints(catalogueItemData.getTrendingPoints());
        entityCatalogueItemData.setOustCoins(catalogueItemData.getOustCoins());
        entityCatalogueItemData.setNumOfEnrolledUsers(catalogueItemData.getNumOfEnrolledUsers());
        entityCatalogueItemData.setContentId(catalogueItemData.getContentId());
        entityCatalogueItemData.setBanner(catalogueItemData.getBanner());
        entityCatalogueItemData.setDescription(catalogueItemData.getDescription());
        entityCatalogueItemData.setContentType(catalogueItemData.getContentType());
        entityCatalogueItemData.setViewStatus(catalogueItemData.getViewStatus());
        entityCatalogueItemData.setCategoryItemData(getCatalogueItemDataList(catalogueItemData.getCategoryItemData()));
        return entityCatalogueItemData;
    }

    public static List<EntityNotificationData> getNotificationItemDataList(List<NotificationResponse> notificationResponses) {
        List<EntityNotificationData> NotificationData = new ArrayList<>();
        for (NotificationResponse itemData : notificationResponses) {
            NotificationData.add(getNotificationItemData(itemData));
        }
        return NotificationData;
    }

    private static EntityNotificationData getNotificationItemData(NotificationResponse itemData) {
        EntityNotificationData entityNotificationData = new EntityNotificationData();
        entityNotificationData.setTitle(itemData.getTitle());
        entityNotificationData.setContentId(itemData.getContentId());
        entityNotificationData.setMessage(itemData.getMessage());
        entityNotificationData.setType(itemData.getType());
        entityNotificationData.setImageUrl(itemData.getTitle());
        entityNotificationData.setUpdateTime(itemData.getUpdateTime());
        entityNotificationData.setKeyValue(itemData.getKey());
        entityNotificationData.setFireBase(itemData.getFireBase());
        entityNotificationData.setRead(itemData.getRead());
        entityNotificationData.setUserId(itemData.getUserId());
        entityNotificationData.setNotificationKey(itemData.getNotificationKey());
        entityNotificationData.setCommentId(itemData.getCommentId());
        entityNotificationData.setNoticeBoardId(itemData.getNoticeBoardId());
        entityNotificationData.setReplyId(itemData.getReplyId());
        return entityNotificationData;
    }
}
