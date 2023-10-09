package com.oustme.oustsdk.room;

import com.oustme.oustsdk.layoutFour.data.CatalogueItemData;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOCplCompletedModel;
import com.oustme.oustsdk.room.dto.DTOCplMedia;
import com.oustme.oustsdk.room.dto.DTOCplMediaUpdateData;
import com.oustme.oustsdk.room.dto.DTODiaryDetailsModel;
import com.oustme.oustsdk.room.dto.DTOFeedBackModel;
import com.oustme.oustsdk.room.dto.DTOHotspotPointData;
import com.oustme.oustsdk.room.dto.DTOImageChoice;
import com.oustme.oustsdk.room.dto.DTOLearningDiary;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
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

class DTOConverter {


    static DTOCourseCard getCourseCard(EntityCourseCardClass courseCard) {
        DTOCourseCard dtoCourseCard = null;
        try {
            if (courseCard != null) {
                dtoCourseCard = new DTOCourseCard();

                dtoCourseCard.setCardBgImage(courseCard.getCardBgImage());
                dtoCourseCard.setCardBgColor(courseCard.getCardBgColor());
                dtoCourseCard.setCourseCardID(courseCard.getCourseCardID());
                dtoCourseCard.setCardId(courseCard.getCardId());
                dtoCourseCard.setCardLayout(courseCard.getCardLayout());
                dtoCourseCard.setCardQuestionColor(courseCard.getCardQuestionColor());
                dtoCourseCard.setCardSolutionColor(courseCard.getCardSolutionColor());
                dtoCourseCard.setCardTextColor(courseCard.getCardTextColor());
                dtoCourseCard.setCardType(courseCard.getCardType());
                dtoCourseCard.setCardTitle(courseCard.getCardTitle());
                dtoCourseCard.setqId(courseCard.getQId());
                dtoCourseCard.setXp(courseCard.getXp());
                dtoCourseCard.setSequence(courseCard.getSequence());
                dtoCourseCard.setCardMedia(getCourseCardMedia(courseCard.getCardMedia()));
                dtoCourseCard.setContent(courseCard.getContent());
                dtoCourseCard.setQuestionData(getQuestions(courseCard.getQuestionData()));
                dtoCourseCard.setChildCard(getCourseSolutionCard(courseCard.getChildCard()));
                dtoCourseCard.setClCode(courseCard.getClCode());
                dtoCourseCard.setBgImg(courseCard.getBgImg());
                dtoCourseCard.setLanguage(courseCard.getLanguage());
                dtoCourseCard.setCardColorScheme(getCardColorScheme(courseCard.getCardColorScheme()));
                dtoCourseCard.setQuestionType(courseCard.getQuestionType());
                dtoCourseCard.setQuestionCategory(courseCard.getQuestionCategory());
                dtoCourseCard.setReadMoreData(getReadMoreData(courseCard.getReadMoreData()));
                dtoCourseCard.setReadMoreCard(courseCard.isReadMoreCard());
                dtoCourseCard.setPotraitModeVideo(courseCard.isPotraitModeVideo());
                dtoCourseCard.setShareToSocialMedia(courseCard.isShareToSocialMedia());
                dtoCourseCard.setMappedLearningCardId(courseCard.getMappedLearningCardId());
                dtoCourseCard.setCaseStudyTitle(courseCard.getCaseStudyTitle());
                dtoCourseCard.setAudio(courseCard.getAudio());
                dtoCourseCard.setMandatoryViewTime(courseCard.getMandatoryViewTime());
                dtoCourseCard.setScormIndexFile(courseCard.getScormIndexFile());
                dtoCourseCard.setShowQuestionSymbolForQuestion(courseCard.isShowQuestionSymbolForQuestion());
                dtoCourseCard.setProceedOnWrong(courseCard.isProceedOnWrong());
                dtoCourseCard.setIfScormEventBased(courseCard.isIfScormEventBased());

                dtoCourseCard.setScormPlayerUrl(courseCard.getScormPlayerUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return dtoCourseCard;
    }

    static List<DTOCourseCardMedia> getCourseCardMedia(List<EntityCourseCardMedia> courseCardMediaList) {
        List<DTOCourseCardMedia> cardMedia = null;
        if (courseCardMediaList != null && courseCardMediaList.size() > 0) {
            cardMedia = new ArrayList<>();
            for (EntityCourseCardMedia courseCardMedia : courseCardMediaList) {
                DTOCourseCardMedia dtoCourseCardMedia = new DTOCourseCardMedia();

                dtoCourseCardMedia.setMediaId(courseCardMedia.getMediaId());
                dtoCourseCardMedia.setData(courseCardMedia.getData());
                dtoCourseCardMedia.setGumletVideoUrl(courseCardMedia.getGumletVideoUrl());
                dtoCourseCardMedia.setMediaType(courseCardMedia.getMediaType());
                dtoCourseCardMedia.setMediaPrivacy(courseCardMedia.getMediaPrivacy());
                dtoCourseCardMedia.setMediaName(courseCardMedia.getMediaName());
                dtoCourseCardMedia.setMediaDescription(courseCardMedia.getMediaDescription());
                dtoCourseCardMedia.setFastForwardMedia(courseCardMedia.isFastForwardMedia());
                dtoCourseCardMedia.setMediaThumbnail(courseCardMedia.getMediaThumbnail());


                cardMedia.add(dtoCourseCardMedia);
            }
        }
        return cardMedia;
    }

    static DTOQuestions getQuestions(EntityQuestions questions) {
        DTOQuestions dtoQuestions = null;

        if (questions != null) {
            dtoQuestions = new DTOQuestions();

            dtoQuestions.setA(questions.getA());
            dtoQuestions.setB(questions.getB());
            dtoQuestions.setC(questions.getC());
            dtoQuestions.setD(questions.getD());
            dtoQuestions.setE(questions.getE());
            dtoQuestions.setF(questions.getF());
            dtoQuestions.setG(questions.getG());

            dtoQuestions.setSubject(questions.getSubject());
            dtoQuestions.setAllNoneFlag(questions.isAllNoneFlag());
            dtoQuestions.setTopic(questions.getTopic());
            dtoQuestions.setImageheight(questions.getImageheight());

            dtoQuestions.setQuestionCardId(questions.getQuestionCardId());
            dtoQuestions.setQuestionId(questions.getQuestionId());

            dtoQuestions.setAnswer(questions.getAnswer());
            dtoQuestions.setExitOption(questions.getExitOption());
            dtoQuestions.setBgImg(questions.getBgImg());
            dtoQuestions.setImage(questions.getImage());
            dtoQuestions.setImageCDNPath(questions.getImageCDNPath());
            dtoQuestions.setqVideoUrl(questions.getqVideoUrl());
            dtoQuestions.setGumletVideoUrl(questions.getGumletVideoUrl());
            dtoQuestions.setProceedOnWrong(questions.isProceedOnWrong());
            dtoQuestions.setVendorId(questions.getVendorId());
            dtoQuestions.setVendorDisplayName(questions.getVendorDisplayName());
            dtoQuestions.setMaxtime(questions.getMaxtime());
            dtoQuestions.setGrade(questions.getGrade());
            dtoQuestions.setImagewidth(questions.getImagewidth());
            dtoQuestions.setQuestion(questions.getQuestion());
            dtoQuestions.setFavourite(questions.getFavourite());
            dtoQuestions.setLikeUnlike(questions.getLikeUnlike());
            dtoQuestions.setUserFeedback(questions.getUserFeedback());
            dtoQuestions.setAnswerStatus(questions.getAnswerStatus());
            dtoQuestions.setStudentAnswer(questions.getStudentAnswer());
            dtoQuestions.setReattemptCount(questions.getReattemptCount());
            dtoQuestions.setSolution(questions.getSolution());
            dtoQuestions.setQuestionCategory(questions.getQuestionCategory());
            dtoQuestions.setQuestionType(questions.getQuestionType());
            dtoQuestions.setSkip(questions.isSkip());
            dtoQuestions.setAudio(questions.getAudio());
            dtoQuestions.setGameId(questions.getGameId());
            dtoQuestions.setFullScreenHotSpot(questions.isFullScreenHotSpot());

            dtoQuestions.setImageChoiceA(getImageChoiceData(questions.getImageChoiceA()));
            dtoQuestions.setImageChoiceB(getImageChoiceData(questions.getImageChoiceB()));
            dtoQuestions.setImageChoiceC(getImageChoiceData(questions.getImageChoiceC()));
            dtoQuestions.setImageChoiceD(getImageChoiceData(questions.getImageChoiceD()));
            dtoQuestions.setImageChoiceE(getImageChoiceData(questions.getImageChoiceE()));
            dtoQuestions.setImageChoiceAnswer(getImageChoiceData(questions.getImageChoiceAnswer()));

            dtoQuestions.setMtfLeftCol(getMtfColumnData(questions.getMtfLeftCol()));
            dtoQuestions.setMtfRightCol(getMtfColumnData(questions.getMtfRightCol()));
            dtoQuestions.setMtfAnswer(getMtfSolution(questions.getMtfAnswer()));
            dtoQuestions.setFillAnswers(getFillData(questions.getFillAnswers()));

            dtoQuestions.setHotspotDataList(getHotspotPointData(questions.getHotspotDataList()));
            dtoQuestions.setOptionCategories(getQuestionOptionCatagory(questions.getOptionCategories()));
            dtoQuestions.setOptions(getQuestionOptionData(questions.getOptions()));
            dtoQuestions.setReadMoreData(getReadMoreData(questions.getReadMoreData()));
            dtoQuestions.setRandomize(questions.isRandomize());
            dtoQuestions.setContainSubjective(questions.isContainSubjective());
            dtoQuestions.setSubjectiveQuestion(questions.getSubjectiveQuestion());
            dtoQuestions.setSurveyPointCount(questions.getSurveyPointCount());
            dtoQuestions.setMaxWordCount(questions.getMaxWordCount());
            dtoQuestions.setMinWordCount(questions.getMinWordCount());
            dtoQuestions.setExitable(questions.isExitable());
            dtoQuestions.setThumbsUpDn(questions.isThumbsUpDn());
            dtoQuestions.setAnswerValidationType(questions.getAnswerValidationType());
            dtoQuestions.setFieldType(questions.getFieldType());
            dtoQuestions.setDropdownType(questions.getDropdownType());
            dtoQuestions.setMandatory(questions.isMandatory());
            dtoQuestions.setExitMessage(questions.getExitMessage());
            dtoQuestions.setHotSpotThumbsUpShown(questions.isHotSpotThumbsUpShown());
            dtoQuestions.setHotSpotThumbsDownShown(questions.isHotSpotThumbsDownShown());
            dtoQuestions.setImageType(questions.getImageType());
            dtoQuestions.setThumbsUp(questions.isThumbsUp());
            dtoQuestions.setThumbsDown(questions.isThumbsDown());
            dtoQuestions.setShowSolution(questions.isShowSolution());
            dtoQuestions.setSolutionType(questions.getSolutionType());
            dtoQuestions.setScore(questions.getScore());

            dtoQuestions.setCorrect(questions.isCorrect());
            dtoQuestions.setDifficulty(questions.getDifficulty());
            dtoQuestions.setModuleId(questions.getModuleId());
            dtoQuestions.setModuleName(questions.getModuleName());
            dtoQuestions.setAttemptDateTime(questions.getAttemptDateTime());
            dtoQuestions.setBase64Image(questions.getBase64Image());
            dtoQuestions.setTimeTaken(questions.getTimeTaken());
            dtoQuestions.setFeedback(questions.getFeedback());
            dtoQuestions.setVideoLinks(questions.getVideoLinks());
            dtoQuestions.setTextMaterial(questions.getTextMaterial());
            dtoQuestions.setSyllabus(questions.getSyllabus());
            dtoQuestions.setShowSkipButton(questions.isShowSkipButton());
            dtoQuestions.setSkillName(questions.getSkillName());
            dtoQuestions.setShareToSocialMedia(questions.isShareToSocialMedia());
            dtoQuestions.setShowFavourite(questions.isShowFavourite());

            dtoQuestions.setStartRange(questions.getStartRange());
            dtoQuestions.setEndRange(questions.getEndRange());
            dtoQuestions.setMaxLabel(questions.getMaxLabel());
            dtoQuestions.setMinLabel(questions.getMinLabel());
            dtoQuestions.setHint(questions.getHint());
            dtoQuestions.setRemarks(questions.isRemarks());
            dtoQuestions.setUploadMedia(questions.isUploadMedia());
            dtoQuestions.setDataSource(questions.getDataSource());
            dtoQuestions.setSurveyQuestion(questions.isSurveyQuestion());

            dtoQuestions.setEnableGalleryUpload(questions.isEnableGalleryUpload());

            dtoQuestions.setListOfVideoOverlayQuestion(getVideoOverlayList(questions.getVideoOverlayList()));
        }
        return dtoQuestions;
    }


    static DTOImageChoice getImageChoiceData(EntityImageChoice imgChoiceData) {
        DTOImageChoice dtoImageChoice = null;

        if (imgChoiceData != null) {
            dtoImageChoice = new DTOImageChoice();
            dtoImageChoice.setImageData(imgChoiceData.getImageData());
            dtoImageChoice.setImageFileName(imgChoiceData.getImageFileName());
            dtoImageChoice.setImageFileName_CDN_Path(imgChoiceData.getImageFileName_CDN_Path());
        }
        return dtoImageChoice;
    }


    static DTOReadMore getReadMoreData(EntityReadMore readMoreData) {

        DTOReadMore dtoReadMore = null;

        if (readMoreData != null) {
            dtoReadMore = new DTOReadMore();
            dtoReadMore.setCardId(readMoreData.getCardId());
            dtoReadMore.setCourseId(readMoreData.getCourseId());
            dtoReadMore.setData(readMoreData.getData());
            dtoReadMore.setDisplayText(readMoreData.getDisplayText());
            dtoReadMore.setLevelId(readMoreData.getLevelId());
            dtoReadMore.setRmId(readMoreData.getRmId());
            dtoReadMore.setScope(readMoreData.getScope());
            dtoReadMore.setType(readMoreData.getType());
        }

        return dtoReadMore;
    }

    static List<DTOMTFColumnData> getMtfColumnData(List<EntityMTFColumnData> columnDataList) {
        List<DTOMTFColumnData> dtomtfColumnDataList = null;

        if (columnDataList != null && columnDataList.size() > 0) {
            dtomtfColumnDataList = new ArrayList<>();

            for (EntityMTFColumnData mtfColumnData : columnDataList) {
                DTOMTFColumnData dtomtfColumnData = new DTOMTFColumnData();
                if (mtfColumnData != null) {
                    dtomtfColumnData.setMtfColData(mtfColumnData.getMtfColData());
                    dtomtfColumnData.setMtfColData_CDN(mtfColumnData.getMtfColData());
                    dtomtfColumnData.setMtfColDataId(mtfColumnData.getMtfColDataId());
                    dtomtfColumnData.setMtfColMediaType(mtfColumnData.getMtfColMediaType());
                    dtomtfColumnDataList.add(dtomtfColumnData);
                }
            }
        }
        return dtomtfColumnDataList;
    }

    static List<String> getMtfSolution(List<String> dtoMtfSolutions) {
        List<String> mtfSolution = null;
        if (dtoMtfSolutions != null && dtoMtfSolutions.size() > 0) {
            mtfSolution = new ArrayList<>();
            mtfSolution.addAll(dtoMtfSolutions);
        }
        return mtfSolution;
    }

    private static List<DTOHotspotPointData> getHotspotPointData(List<EntityHotspotPointData> hotspotDataList) {
        List<DTOHotspotPointData> dtoHotspotPointData = null;
        if (hotspotDataList != null && hotspotDataList.size() > 0) {
            dtoHotspotPointData = new ArrayList<>();

            for (EntityHotspotPointData hotspotPointData : hotspotDataList) {
                DTOHotspotPointData hotspotPoint = new DTOHotspotPointData();
                hotspotPoint.setHeight(hotspotPointData.getHeight());
                hotspotPoint.setHpLabel(hotspotPointData.getHpLabel());
                hotspotPoint.setHpQuestion(hotspotPointData.getHpQuestion());
                hotspotPoint.setStartX(hotspotPointData.getStartX());
                hotspotPoint.setStartY(hotspotPointData.getStartY());
                hotspotPoint.setWidth(hotspotPointData.getWidth());
                hotspotPoint.setAnswer(hotspotPointData.isAnswer());
                hotspotPoint.setHpAdaptiveCardId(hotspotPointData.getHpAdaptiveCardId());

                dtoHotspotPointData.add(hotspotPoint);
            }
        }
        return dtoHotspotPointData;
    }

    private static List<DTOQuestionOption> getQuestionOptionData(List<EntityQuestionOption> options) {

        List<DTOQuestionOption> dtoQuestionOptions = null;

        if (options != null) {
            dtoQuestionOptions = new ArrayList<>();

            for (EntityQuestionOption option : options) {
                DTOQuestionOption dtoQuestionOption = new DTOQuestionOption();
                dtoQuestionOption.setData(option.getData());
                dtoQuestionOption.setBitmapData(option.getBitmapData());
                dtoQuestionOption.setOptionCategory(option.getOptionCategory());
                dtoQuestionOption.setType(option.getType());
                dtoQuestionOption.setData_CDN(option.getData());

                dtoQuestionOptions.add(dtoQuestionOption);
            }
        }

        return dtoQuestionOptions;
    }

    private static List<DTOQuestionOptionCategory> getQuestionOptionCatagory(List<EntityQuestionOptionCategory> optionCategories) {
        List<DTOQuestionOptionCategory> categoryOptions = null;
        if (optionCategories != null) {
            categoryOptions = new ArrayList<>();

            for (EntityQuestionOptionCategory questionOptionCategory : optionCategories) {
                DTOQuestionOptionCategory option = new DTOQuestionOptionCategory();
                option.setData(questionOptionCategory.getData());
                option.setType(questionOptionCategory.getType());
                option.setCode(questionOptionCategory.getCode());
                option.setData_CDN(questionOptionCategory.getData());

                categoryOptions.add(option);
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


    private static List<DTOVideoOverlay> getVideoOverlayList(List<EntityVideoOverlay> videoOverlayList) {
        List<DTOVideoOverlay> dtoVideoOverlays = null;
        if (videoOverlayList != null) {
            dtoVideoOverlays = new ArrayList<>();

            for (EntityVideoOverlay entityVideoOverlay : videoOverlayList) {
                DTOVideoOverlay dtoVideoOverlay = new DTOVideoOverlay();
                dtoVideoOverlay.setParentQuestionId(entityVideoOverlay.getParentQuestionId());
                dtoVideoOverlay.setChildQuestionId(entityVideoOverlay.getChildQuestionId());
                dtoVideoOverlay.setTimeDuration(entityVideoOverlay.getTimeDuration());
                dtoVideoOverlay.setCardSequence(entityVideoOverlay.getCardSequence());
                dtoVideoOverlay.setChildCoursecardId(entityVideoOverlay.getChildCoursecardId());
                dtoVideoOverlay.setChildQuestionCourseCard(getCourseCard(entityVideoOverlay.getChildQuestionCourseCard()));

                dtoVideoOverlays.add(dtoVideoOverlay);
            }

        }
        return dtoVideoOverlays;
    }

    private static DTOCourseSolutionCard getCourseSolutionCard(EntityCourseSolutionCard courseSolutionCard) {
        DTOCourseSolutionCard dtoCourseSolutionCard = null;
        if (courseSolutionCard != null) {
            dtoCourseSolutionCard = new DTOCourseSolutionCard();

            dtoCourseSolutionCard.setCardBgColor(courseSolutionCard.getCardBgColor());
            dtoCourseSolutionCard.setCardId(courseSolutionCard.getCardId());
            dtoCourseSolutionCard.setCardLayout(courseSolutionCard.getCardLayout());
            dtoCourseSolutionCard.setCardQuestionColor(courseSolutionCard.getCardQuestionColor());
            dtoCourseSolutionCard.setCardSolutionColor(courseSolutionCard.getCardSolutionColor());
            dtoCourseSolutionCard.setCardTextColor(courseSolutionCard.getCardTextColor());
            dtoCourseSolutionCard.setCardType(courseSolutionCard.getCardType());
            dtoCourseSolutionCard.setContent(courseSolutionCard.getContent());
            dtoCourseSolutionCard.setRewardOc(courseSolutionCard.getRewardOc());
            dtoCourseSolutionCard.setSequence(courseSolutionCard.getSequence());
            dtoCourseSolutionCard.setCardColorScheme(getCardColorScheme(courseSolutionCard.getCardColorScheme()));
        }
        return dtoCourseSolutionCard;
    }

    static DTOCardColorScheme getCardColorScheme(EntityCardColorScheme cardColorScheme) {
        DTOCardColorScheme dtoCardColorScheme = null;
        if (cardColorScheme != null) {
            dtoCardColorScheme = new DTOCardColorScheme();

            dtoCardColorScheme.setBgImage(cardColorScheme.getBgImage());
            dtoCardColorScheme.setContentColor(cardColorScheme.getContentColor());
            dtoCardColorScheme.setIconColor(cardColorScheme.getIconColor());
            dtoCardColorScheme.setLevelNameColor(cardColorScheme.getLevelNameColor());
            dtoCardColorScheme.setOptionColor(cardColorScheme.getOptionColor());
            dtoCardColorScheme.setTitleColor(cardColorScheme.getTitleColor());
        }
        return dtoCardColorScheme;
    }


    public static List<DTOCourseCard> getCourseCardList(List<EntityCourseCardClass> courseCardClasses) {

        List<DTOCourseCard> dtoCourseCards = null;
        if (courseCardClasses != null) {
            dtoCourseCards = new ArrayList<>();

            for (EntityCourseCardClass courseCardClass : courseCardClasses) {
                dtoCourseCards.add(getCourseCard(courseCardClass));
            }
        }
        return dtoCourseCards;
    }


    public static DTOUserCourseData getUserCourseData(EntityUserCourseData userCourseData) {
        DTOUserCourseData dtoUserCourseData = null;
        if (userCourseData != null) {
            dtoUserCourseData = new DTOUserCourseData();
            dtoUserCourseData.setId(userCourseData.getId());
            dtoUserCourseData.setCurrentLevel(userCourseData.getCurrentLevel());
            dtoUserCourseData.setCurrentCard(userCourseData.getCurrentCard());
            dtoUserCourseData.setPresentageComplete(userCourseData.getPresentageComplete());
            dtoUserCourseData.setTotalOc(userCourseData.getTotalOc());
            dtoUserCourseData.setTotalCards(userCourseData.getTotalCards());
            dtoUserCourseData.setSaved(userCourseData.isSaved());
            dtoUserCourseData.setAlarmSet(userCourseData.isAlarmSet());
            dtoUserCourseData.setUserLevelDataList(getUserLevelDataList(userCourseData.getUserLevelDataList()));
            dtoUserCourseData.setCurrentCompleteLevel(userCourseData.getCurrentCompleteLevel());
            dtoUserCourseData.setLastCompleteLevel(userCourseData.getLastCompleteLevel());
            dtoUserCourseData.setCourseComplete(userCourseData.isCourseComplete());
            dtoUserCourseData.setMyCourseRating(userCourseData.getMyCourseRating());
            dtoUserCourseData.setLastPlayedLevel(userCourseData.getLastPlayedLevel());
            dtoUserCourseData.setAskedVideoStorePermission(userCourseData.isAskedVideoStorePermission());
            dtoUserCourseData.setVideoDownloadPermissionAllowed(userCourseData.isVideoDownloadPermissionAllowed());
            dtoUserCourseData.setDeleteDataAfterComplete(userCourseData.isDeleteDataAfterComplete());
            dtoUserCourseData.setDownloading(userCourseData.isDownloading());
            dtoUserCourseData.setDownloadCompletePercentage(userCourseData.getDownloadCompletePercentage());
            dtoUserCourseData.setUpdateTS(userCourseData.getUpdateTS());
            dtoUserCourseData.setAcknowledged(userCourseData.isAcknowledged());
            dtoUserCourseData.setCurrentLevelId(userCourseData.getCurrentLevelId());
            dtoUserCourseData.setMappedAssessmentPassed(userCourseData.isMappedAssessmentPassed());
            dtoUserCourseData.setBulletinLastUpdatedTime(userCourseData.getBulletinLastUpdatedTime());
            dtoUserCourseData.setCourseCompleted(userCourseData.isCourseCompleted());
            dtoUserCourseData.setAddedOn(userCourseData.getAddedOn());
            dtoUserCourseData.setEnrollmentDate(userCourseData.getEnrollmentDate());
            dtoUserCourseData.setMappedAssessmentCompleted(userCourseData.isMappedAssessmentCompleted());
        }

        return dtoUserCourseData;
    }

    private static List<DTOUserLevelData> getUserLevelDataList(List<EntityUserLevelData> userLevelDataList) {

        List<DTOUserLevelData> dtoUserLevelDataList = null;

        if (userLevelDataList != null) {
            dtoUserLevelDataList = new ArrayList<>();
            for (EntityUserLevelData userLevelData : userLevelDataList) {
                DTOUserLevelData dtoUserLevelData = new DTOUserLevelData();

                dtoUserLevelData.setTotalTime(userLevelData.getTotalTime());
                dtoUserLevelData.setTotalOc(userLevelData.getTotalOc());
                dtoUserLevelData.setLevelId(userLevelData.getLevelId());
                dtoUserLevelData.setSequece(userLevelData.getSequece());
                dtoUserLevelData.setXp(userLevelData.getXp());
                dtoUserLevelData.setTimeStamp(userLevelData.getTimeStamp());
                dtoUserLevelData.setUserCardDataList(getUserCardData(userLevelData.getUserCardDataList()));
                dtoUserLevelData.setDownloading(userLevelData.isDownloading());
                dtoUserLevelData.setCompletePercentage(userLevelData.getCompletePercentage());
                dtoUserLevelData.setCurrentCardNo(userLevelData.getCurrentCardNo());
                dtoUserLevelData.setLocked(userLevelData.isLocked());
                dtoUserLevelData.setDownloadedAllCards(userLevelData.isDownloadedAllCards());
                dtoUserLevelData.setIslastCardComplete(userLevelData.isIslastCardComplete());
                dtoUserLevelData.setLevelCompleted(userLevelData.isLevelCompleted());

                dtoUserLevelDataList.add(dtoUserLevelData);
            }
        }
        return dtoUserLevelDataList;
    }

    private static List<DTOUserCardData> getUserCardData(List<EntityUserCardData> userCardDataList) {
        List<DTOUserCardData> dtoUserCardDataList = null;

        if (userCardDataList != null) {
            dtoUserCardDataList = new ArrayList<>();
            for (EntityUserCardData userCardData : userCardDataList) {
                DTOUserCardData dtoUserCardData = new DTOUserCardData();
                dtoUserCardData.setOc(userCardData.getOc());
                dtoUserCardData.setResponceTime(userCardData.getResponceTime());
                dtoUserCardData.setCardId(userCardData.getCardId());
                dtoUserCardData.setNoofAttempt(userCardData.getNoofAttempt());
                dtoUserCardData.setCardCompleted(userCardData.isCardCompleted());
                dtoUserCardData.setCardViewInterval(userCardData.getCardViewInterval());
                dtoUserCardDataList.add(dtoUserCardData);
            }
        }

        return dtoUserCardDataList;
    }


    static ArrayList<DTOCplMedia> getCplMediaList(ArrayList<EntityCplMedia> cplMediaFiles) {
        ArrayList<DTOCplMedia> dtoCplMediaList = null;
        if (cplMediaFiles != null) {
            dtoCplMediaList = new ArrayList<>();
            for (EntityCplMedia entityCplMedia : cplMediaFiles) {
                DTOCplMedia dtoCplMedia = getCplMedia(entityCplMedia);
                if (dtoCplMedia != null)
                    dtoCplMediaList.add(dtoCplMedia);
            }
        }
        return dtoCplMediaList;
    }

    private static DTOCplMedia getCplMedia(EntityCplMedia cplMediaData) {
        DTOCplMedia entityCplMedia = null;
        if (cplMediaData != null) {
            entityCplMedia = new DTOCplMedia();
            entityCplMedia.setId(cplMediaData.getId());
            entityCplMedia.setFolderPath(cplMediaData.getFolderPath());
            entityCplMedia.setName(cplMediaData.getName());
            entityCplMedia.setFileName(cplMediaData.getFileName());
            entityCplMedia.setCplId(cplMediaData.getCplId());
        }
        return entityCplMedia;
    }

    public static DTOCplMediaUpdateData getCplMediaUpdateData(EntityCplMediaUpdateData cplUpdateModel) {
        DTOCplMediaUpdateData dtoCplMediaUpdateData = null;
        if (cplUpdateModel != null) {
            dtoCplMediaUpdateData = new DTOCplMediaUpdateData();
            dtoCplMediaUpdateData.setCplId(cplUpdateModel.getCplId());
            dtoCplMediaUpdateData.setUpdateTime(cplUpdateModel.getUpdateTime());
        }
        return dtoCplMediaUpdateData;
    }

    public static DTOCplCompletedModel getCplCompletedModel(EntityCplCompletedModel cplCompletedModel) {
        DTOCplCompletedModel dtoCplCompletedModel = null;
        if (cplCompletedModel != null) {
            dtoCplCompletedModel = new DTOCplCompletedModel();
            dtoCplCompletedModel.setId(cplCompletedModel.getId());
            dtoCplCompletedModel.setCompletedOn(cplCompletedModel.getCompletedOn());
            dtoCplCompletedModel.setType(cplCompletedModel.getType());
            dtoCplCompletedModel.setCompleted(cplCompletedModel.isCompleted());
            dtoCplCompletedModel.setSubmittedToServer(cplCompletedModel.isSubmittedToServer());
            dtoCplCompletedModel.setmLearningStatus(cplCompletedModel.isMLearningStatus());
            dtoCplCompletedModel.setmRetryCount(cplCompletedModel.getMRetryCount());
        }
        return dtoCplCompletedModel;
    }

    public static List<DTOFeedBackModel> getFeedBackList(List<EntityFeedBackModel> entityFeedBackModelList) {
        List<DTOFeedBackModel> dtoFeedBackModelList = null;
        if (entityFeedBackModelList != null) {
            dtoFeedBackModelList = new ArrayList<>();
            for (EntityFeedBackModel entityFeedBackModel : entityFeedBackModelList) {
                DTOFeedBackModel dtoFeedBackModel = getFeedBackModel(entityFeedBackModel);
                if (dtoFeedBackModel != null)
                    dtoFeedBackModelList.add(dtoFeedBackModel);
            }
        }

        return dtoFeedBackModelList;
    }

    private static DTOFeedBackModel getFeedBackModel(EntityFeedBackModel entityFeedBackModel) {
        DTOFeedBackModel dtoFeedBackModel = null;
        if (entityFeedBackModel != null) {
            dtoFeedBackModel = new DTOFeedBackModel();
            dtoFeedBackModel.setCountry(entityFeedBackModel.getCountry());
            dtoFeedBackModel.setCity(entityFeedBackModel.getCity());
            dtoFeedBackModel.setArea(entityFeedBackModel.getArea());
            dtoFeedBackModel.setLongitude(entityFeedBackModel.getLongitude());
            dtoFeedBackModel.setLatitude(entityFeedBackModel.getLatitude());
            dtoFeedBackModel.setMobile(entityFeedBackModel.getMobile());
            dtoFeedBackModel.setPurpose(entityFeedBackModel.getPurpose());
            dtoFeedBackModel.setComments(entityFeedBackModel.getComments());
            dtoFeedBackModel.setPhoto(entityFeedBackModel.getPhoto());
            dtoFeedBackModel.setStudentid(entityFeedBackModel.getStudentid());
        }
        return dtoFeedBackModel;
    }

    public static DTOLearningDiary getlearningDiary(EntityLearningDiary learningDiary) {
        DTOLearningDiary dtoDearningDiary = null;
        if (learningDiary != null) {
            dtoDearningDiary = new DTOLearningDiary();
            dtoDearningDiary.setUid(learningDiary.getUid());
            dtoDearningDiary.set_newsList(getDiaryDetailsModelList(learningDiary.get_newsList()));
        }
        return dtoDearningDiary;
    }

    private static List<DTODiaryDetailsModel> getDiaryDetailsModelList(List<EntityDiaryDetailsModel> newsList) {
        List<DTODiaryDetailsModel> diaryDetailsModelList = null;
        if (newsList != null) {
            diaryDetailsModelList = new ArrayList<>();
            for (EntityDiaryDetailsModel diaryDetailsModel : newsList) {
                diaryDetailsModelList.add(getDiaryDetailsModel(diaryDetailsModel));
            }
        }

        return diaryDetailsModelList;
    }

    private static DTODiaryDetailsModel getDiaryDetailsModel(EntityDiaryDetailsModel diaryDetailsModel) {
        DTODiaryDetailsModel dtoDiaryDetailsModel = null;
        if (diaryDetailsModel != null) {
            dtoDiaryDetailsModel = new DTODiaryDetailsModel();
            dtoDiaryDetailsModel.setEndTS(diaryDetailsModel.getEndTS());
            dtoDiaryDetailsModel.setStartTS(diaryDetailsModel.getStartTS());
            dtoDiaryDetailsModel.setUpdateTS(diaryDetailsModel.getUpdateTS());
            dtoDiaryDetailsModel.setEditable(diaryDetailsModel.isEditable());
            dtoDiaryDetailsModel.setIsdeleted(diaryDetailsModel.isIsdeleted());
            dtoDiaryDetailsModel.setDefaultBanner(diaryDetailsModel.getDefaultBanner());
            dtoDiaryDetailsModel.setmBanner(diaryDetailsModel.getmBanner());
            dtoDiaryDetailsModel.setApprovalStatus(diaryDetailsModel.getApprovalStatus());
            dtoDiaryDetailsModel.setType(diaryDetailsModel.getType());
            dtoDiaryDetailsModel.setFileName(diaryDetailsModel.getFileName());
            dtoDiaryDetailsModel.setFileSize(diaryDetailsModel.getFileSize());
            dtoDiaryDetailsModel.setUserLD_Id(diaryDetailsModel.getUserLD_Id());
            dtoDiaryDetailsModel.setActivityName(diaryDetailsModel.getActivityName());
            dtoDiaryDetailsModel.setComments(diaryDetailsModel.getComments());
            dtoDiaryDetailsModel.setSortingTime(diaryDetailsModel.getSortingTime());
            dtoDiaryDetailsModel.setLearningDiaryMediaDataList(getLearningDiaryMediaDataList(diaryDetailsModel.getLearningDiaryMediaDataList()));
        }
        return dtoDiaryDetailsModel;
    }

    private static List<DTOMediaList> getLearningDiaryMediaDataList(List<EntityMediaList> mediaLists) {
        List<DTOMediaList> dtoMediaLists = null;
        if (mediaLists != null) {
            dtoMediaLists = new ArrayList<>();
            for (EntityMediaList mediaList : mediaLists) {
                dtoMediaLists.add(getMediaList(mediaList));
            }
        }
        return dtoMediaLists;
    }

    private static DTOMediaList getMediaList(EntityMediaList mediaList) {
        DTOMediaList dtoMediaList = null;
        if (mediaList != null) {
            dtoMediaList = new DTOMediaList();
            dtoMediaList.setFileName(mediaList.getFileName());
            dtoMediaList.setFileSize(mediaList.getFileSize());
            dtoMediaList.setFileType(mediaList.getFileType());
            dtoMediaList.setIsdeleted(mediaList.isIsdeleted());
            dtoMediaList.setUpdateTS(mediaList.getUpdateTS());
            dtoMediaList.setUpdatedBy(mediaList.getUpdatedBy());
            dtoMediaList.setUserLdMedia_Id(mediaList.getUserLdMedia_Id());
        }
        return dtoMediaList;
    }

    public static DTOResourceData getResourceDataModel(EntityResourceData entityResourceData) {
        DTOResourceData dtoResourceData = null;
        if (entityResourceData != null) {
            dtoResourceData = new DTOResourceData();
            dtoResourceData.setId(entityResourceData.getId());
            dtoResourceData.setFilename(entityResourceData.getFilename());
            dtoResourceData.setTimeStamp(entityResourceData.getTimeStamp());
            dtoResourceData.setFile(entityResourceData.getFile());
        }
        return dtoResourceData;
    }

    public static DTONewFeed getNewFeed(EntityNewFeed entityNewFeed) {
        DTONewFeed dtoNewFeed = null;
        if (entityNewFeed != null) {
            dtoNewFeed = new DTONewFeed();

            dtoNewFeed.setHeader(entityNewFeed.getHeader());
            dtoNewFeed.setContent(entityNewFeed.getContent());
            dtoNewFeed.setIcon(entityNewFeed.getIcon());
            dtoNewFeed.setTimestamp(Long.parseLong(entityNewFeed.getTimestamp()));
            dtoNewFeed.setLink(entityNewFeed.getLink());
            dtoNewFeed.setBtntext(entityNewFeed.getBtntext());
            dtoNewFeed.setType(entityNewFeed.getType());
            dtoNewFeed.setImageUrl(entityNewFeed.getImageUrl());

            dtoNewFeed.setFeedType(entityNewFeed.getFeedType());

            dtoNewFeed.setNewBtnText(entityNewFeed.getNewBtnText());
            dtoNewFeed.setEventItd(entityNewFeed.getEventItd());
            dtoNewFeed.setModuleId(entityNewFeed.getModuleId());
            dtoNewFeed.setGroupId(entityNewFeed.getGroupId());
            dtoNewFeed.setAssessmentId(entityNewFeed.getAssessmentId());
            dtoNewFeed.setCourseId(entityNewFeed.getCourseId());
            dtoNewFeed.setId(entityNewFeed.getId());

            dtoNewFeed.setCourseCardClass(getCourseCard(entityNewFeed.getCourseCardClass()));
            dtoNewFeed.setSurveyIntroCourseCardClass(getCourseCard(entityNewFeed.getSurveyIntroCourseCardClass()));
            dtoNewFeed.setSurveyResultCourseCardClass(getCourseCard(entityNewFeed.getSurveyResultCourseCardClass()));

            dtoNewFeed.setTempSignedImage(entityNewFeed.getTempSignedImage());
            dtoNewFeed.setFeedId(entityNewFeed.getFeedId());
            dtoNewFeed.setLandingBannerMessage(entityNewFeed.getLandingBannerMessage());
            dtoNewFeed.setCommented(entityNewFeed.isCommented());
            dtoNewFeed.setLiked(entityNewFeed.isLiked());
            dtoNewFeed.setShared(entityNewFeed.isShared());
            dtoNewFeed.setFeedPriority(entityNewFeed.getFeedPriority());
            dtoNewFeed.setListenerSet(entityNewFeed.isListenerSet());
            dtoNewFeed.setNumLikes(entityNewFeed.getNumLikes());
            dtoNewFeed.setNumComments(entityNewFeed.getNumComments());
            dtoNewFeed.setNumShares(entityNewFeed.getNumShares());
            dtoNewFeed.setExpiryTime(entityNewFeed.getExpiryTime());
            dtoNewFeed.setCplId(entityNewFeed.getCplId());
            dtoNewFeed.setLocationType(entityNewFeed.getLocationType());
            dtoNewFeed.setFeedTag(entityNewFeed.getFeedTag());
            dtoNewFeed.setSharable(entityNewFeed.isSharable());

            //distributedId
            dtoNewFeed.setDistributedId(entityNewFeed.getDistributedId());

            dtoNewFeed.setDeepLinkInfo(getDeepLinkInfo(entityNewFeed.getDeepLinkInfo()));

            dtoNewFeed.setParentCplId(entityNewFeed.getParentCplId());
            dtoNewFeed.setCommentble(entityNewFeed.isCommentble());
            dtoNewFeed.setLikeble(entityNewFeed.isLikeble());
            dtoNewFeed.setmSpecialFeedStartText(entityNewFeed.getMSpecialFeedStartText());
            dtoNewFeed.setTitleVisible(entityNewFeed.isTitleVisible());
            dtoNewFeed.setDescVisible(entityNewFeed.isDescVisible());
            dtoNewFeed.setClicked(entityNewFeed.isClicked());
            dtoNewFeed.setFeedViewed(entityNewFeed.isFeedViewed());
            dtoNewFeed.setPlaying(entityNewFeed.isPlaying());
            dtoNewFeed.setVideoSource(entityNewFeed.getVideoSource());
            dtoNewFeed.setAutoPlay(entityNewFeed.isAutoPlay());
            dtoNewFeed.setFileName(entityNewFeed.getFileName());
            dtoNewFeed.setFileType(entityNewFeed.getFileType());
            dtoNewFeed.setFileType(entityNewFeed.getFileType());
        }
        return dtoNewFeed;
    }

    private static DTONewFeed.DeepLinkInfo getDeepLinkInfo(EntityDeepLinkInfo entityDeepLinkInfo) {
        DTONewFeed.DeepLinkInfo deepLinkInfo = null;
        if (entityDeepLinkInfo != null) {
            deepLinkInfo = new DTONewFeed.DeepLinkInfo();
            deepLinkInfo.setAssessmentId(entityDeepLinkInfo.getAssessmentId());
            deepLinkInfo.setButtonLabel(entityDeepLinkInfo.getButtonLabel());
            deepLinkInfo.setCardId(entityDeepLinkInfo.getCardId());
            deepLinkInfo.setCourseId(entityDeepLinkInfo.getCourseId());
            deepLinkInfo.setContentId(entityDeepLinkInfo.getContentId());
            deepLinkInfo.setCplId(entityDeepLinkInfo.getCplId());
        }
        return deepLinkInfo;
    }

    public static List<DTONewFeed> getNewFeed(List<EntityNewFeed> entityNewFeeds) {
        List<DTONewFeed> dtoNewFeeds = new ArrayList<>();
        for (EntityNewFeed entityNewFeed : entityNewFeeds) {
            DTONewFeed dtoNewFeed = getNewFeed(entityNewFeed);
            if (dtoNewFeed != null) {
                dtoNewFeeds.add(dtoNewFeed);
            }
        }
        return dtoNewFeeds;
    }

    public static List<DTOUserFeeds.FeedList> getAllUserFeeds(List<EntityUserFeeds> entityNewFeeds) {
        List<DTOUserFeeds.FeedList> dtoNewFeeds = new ArrayList<>();
        for (EntityUserFeeds entityNewFeed : entityNewFeeds) {
            DTOUserFeeds.FeedList dtoNewFeed = getAllUserFeed(entityNewFeed);
            if (dtoNewFeed != null) {
                dtoNewFeeds.add(dtoNewFeed);
            }
        }
        return dtoNewFeeds;
    }

    private static DTOUserFeeds.FeedList getAllUserFeed(EntityUserFeeds entityNewFeed) {
        DTOUserFeeds.FeedList entityUserFeeds = null;
        try {
            if (entityNewFeed != null) {
                entityUserFeeds = new DTOUserFeeds.FeedList();
                entityUserFeeds.setFeedId(entityNewFeed.getFeedId());
                entityUserFeeds.setHeader(entityNewFeed.getHeader());
                entityUserFeeds.setContent(entityNewFeed.getContent());
                entityUserFeeds.setFeedType(entityNewFeed.getFeedType());
                entityUserFeeds.setBannerImg(entityNewFeed.getBannerImg());
                entityUserFeeds.setModuleId(entityNewFeed.getModuleId());
                entityUserFeeds.setModuleType(entityNewFeed.getModuleType());
                entityUserFeeds.setMediaType(entityNewFeed.getMediaType());
                entityUserFeeds.setSpecialFeed(entityNewFeed.isSpecialFeed());
                entityUserFeeds.setDistributedOn(entityNewFeed.getDistributedOn());
                entityUserFeeds.setSpecialFeed(entityNewFeed.isSpecialFeed());
                entityUserFeeds.setMediaData(entityNewFeed.getMediaData());
                entityUserFeeds.setLiked(entityNewFeed.isLiked());
                entityUserFeeds.setClicked(entityNewFeed.isClicked());
                entityUserFeeds.setFeedViewed(entityNewFeed.isFeedViewed());
                entityUserFeeds.setCommented(entityNewFeed.isCommented());
                entityUserFeeds.setShareable(entityNewFeed.isShareable());
                entityUserFeeds.setNumComments(entityNewFeed.getNumComments());
                entityUserFeeds.setNumLikes(entityNewFeed.getNumLikes());
                entityUserFeeds.setFileName(entityNewFeed.getFileName());
                entityUserFeeds.setFileType(entityNewFeed.getFileType());
                entityUserFeeds.setFeedExpiry(entityNewFeed.getFeedExpiry());
                entityUserFeeds.setImageUrl(entityNewFeed.getImageUrl());
                entityUserFeeds.setMediaThumbnail(entityNewFeed.getMediaThumbnail());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return entityUserFeeds;
    }


    static List<CatalogueItemData> getCatalogueItemDataList(List<EntityCatalogueItemData> entityCatalogueItemDataList) {
        List<CatalogueItemData> catalogueItemDataList = new ArrayList<>();
        for (EntityCatalogueItemData itemData : entityCatalogueItemDataList) {
            catalogueItemDataList.add(getCatalogueItemData(itemData));
        }
        return catalogueItemDataList;
    }

    static CatalogueItemData getCatalogueItemData(EntityCatalogueItemData entityCatalogueItemData) {
        CatalogueItemData catalogueItemData = new CatalogueItemData();
        catalogueItemData.setName(entityCatalogueItemData.getName());
        catalogueItemData.setIcon(entityCatalogueItemData.getIcon());
        catalogueItemData.setTrendingPoints(entityCatalogueItemData.getTrendingPoints());
        catalogueItemData.setOustCoins(entityCatalogueItemData.getOustCoins());
        catalogueItemData.setNumOfEnrolledUsers(entityCatalogueItemData.getNumOfEnrolledUsers());
        catalogueItemData.setContentId(entityCatalogueItemData.getContentId());
        catalogueItemData.setBanner(entityCatalogueItemData.getBanner());
        catalogueItemData.setDescription(entityCatalogueItemData.getDescription());
        catalogueItemData.setContentType(entityCatalogueItemData.getContentType());
        catalogueItemData.setViewStatus(entityCatalogueItemData.getViewStatus());
        catalogueItemData.setCategoryItemData((ArrayList<CatalogueItemData>) getCatalogueItemDataList(entityCatalogueItemData.getCategoryItemData()));
        return catalogueItemData;
    }

    public static List<NotificationResponse> getNotificationData(List<EntityNotificationData> notificationData) {
        List<NotificationResponse> notificationResponses = new ArrayList<>();
        for (EntityNotificationData entityNotificationData : notificationData) {
            NotificationResponse response = getNotificationRoomData(entityNotificationData);
            if (response != null) {
                notificationResponses.add(response);
            }
        }
        return notificationResponses;
    }

    private static NotificationResponse getNotificationRoomData(EntityNotificationData entityNotificationData) {
        NotificationResponse notificationResponse = null;
        if (entityNotificationData != null) {
            notificationResponse = new NotificationResponse();

            notificationResponse.setType(entityNotificationData.getType());
            notificationResponse.setContentId(entityNotificationData.getContentId());
            notificationResponse.setImageUrl(entityNotificationData.getImageUrl());
            notificationResponse.setMessage(entityNotificationData.getMessage());
            notificationResponse.setTitle(entityNotificationData.getTitle());
            notificationResponse.setUpdateTime(entityNotificationData.getUpdateTime());
            notificationResponse.setKey(entityNotificationData.getKeyValue());
            notificationResponse.setFireBase(entityNotificationData.getFireBase());
            notificationResponse.setRead(entityNotificationData.getRead());
            notificationResponse.setUserId(entityNotificationData.getUserId());
            notificationResponse.setNotificationKey(entityNotificationData.getNotificationKey());
            notificationResponse.setCommentId(entityNotificationData.getCommentId());
            notificationResponse.setNoticeBoardId(entityNotificationData.getNoticeBoardId());
            notificationResponse.setReplyId(entityNotificationData.getReplyId());
        }
        return notificationResponse;
    }
}
