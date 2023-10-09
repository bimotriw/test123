package com.oustme.oustsdk.tools;

import android.text.TextUtils;
import android.util.Log;


import com.oustme.oustsdk.firebase.course.QuestionOptionCategoryData;
import com.oustme.oustsdk.firebase.course.QuestionOptionData;
import com.oustme.oustsdk.request.LearningPathData;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.common.Questions;
import com.oustme.oustsdk.response.course.MTFColumnData;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.room.dto.DTOQuestionOption;
import com.oustme.oustsdk.room.dto.DTOQuestionOptionCategory;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.List;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS;

/**
 * Created by oust on 1/17/19.
 */

public class OustMediaTools {
    public static String getMediaFileName(String mediaPath) {
        String fileName = null;
        try {
            String[] mediaStrs = mediaPath.split("/");
            fileName = mediaStrs[mediaStrs.length - 1];

            if (fileName.length() > 50) {
                fileName = fileName.substring(0, 50);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            fileName = "test";
        }

        //Log.d("Filename", "getMediaFileName: "+fileName);
        return fileName;
    }

    public static String removeAwsOrCDnUrl(String mediaPath) {
        if (mediaPath.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
            mediaPath = mediaPath.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
        } else if (mediaPath.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
            mediaPath = mediaPath.replace(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH, "");
        } else if (mediaPath.contains(CLOUD_FRONT_BASE_HTTPS)) {
            mediaPath = mediaPath.replace(CLOUD_FRONT_BASE_HTTPS, "");
        }
        return mediaPath;
    }

    public static boolean isAwsOrCDnUrl(String mediaPath) {
        boolean containsAws = false;
        if (mediaPath.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
            containsAws = true;
        } else if (mediaPath.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH) || mediaPath.contains(CLOUD_FRONT_BASE_HTTPS)) {
            containsAws = true;
        }
        return containsAws;
    }

    public static void prepareMediaList(List<String> mediaList, DTOQuestions q) {
        if (q != null) {
            Log.d("question index", "question id " + q.getQuestionId());

            if (q.getImageCDNPath() != null) {
                addToList(mediaList, q.getImageCDNPath());
            }
            if (q.getImageChoiceA() != null) {
                if (q.getImageChoiceA().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceA().getImageData());
                }
            }
            if (q.getImageChoiceB() != null) {
                if (q.getImageChoiceB().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceB().getImageData());
                }
            }
            if (q.getImageChoiceC() != null) {
                if (q.getImageChoiceC().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceC().getImageData());
                }
            }
            if (q.getImageChoiceD() != null) {
                if (q.getImageChoiceD().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceD().getImageData());
                }
            }
            if (q.getImageChoiceE() != null) {
                if (q.getImageChoiceE().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceE().getImageData());
                }
            }
            if (q.getImageChoiceAnswer() != null && !q.getQuestionType().equals(QuestionType.MRQ)) {
                if (q.getImageChoiceAnswer().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceAnswer().getImageData());
                }
            }
            if (q.getMtfLeftCol() != null) {
                List<DTOMTFColumnData> mtfColumnDatas = q.getMtfLeftCol();
                for (int i = 0; i < mtfColumnDatas.size(); i++) {
                    if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                        if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                            addToList(mediaList, mtfColumnDatas.get(i).getMtfColData());
                        }
                    }
                }
            }
            if (q.getMtfRightCol() != null) {
                List<DTOMTFColumnData> mtfColumnDatas = q.getMtfRightCol();
                for (int i = 0; i < mtfColumnDatas.size(); i++) {
                    if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                        if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                            addToList(mediaList, mtfColumnDatas.get(i).getMtfColData());
                        }
                    }
                }
            }
            if (q.getOptionCategories() != null) {
                List<DTOQuestionOptionCategory> optionCategories = q.getOptionCategories();
                for (int i = 0; i < optionCategories.size(); i++) {
                    if (optionCategories.get(i) != null && optionCategories.get(i).getData() != null) {
                        if (!optionCategories.get(i).getType().equalsIgnoreCase("TEXT")) {
                            addToList(mediaList, optionCategories.get(i).getData());
                        }
                    }
                }
            }
            if (q.getOptions() != null) {
                List<DTOQuestionOption> options = q.getOptions();
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i) != null && options.get(i).getData() != null) {
                        if (!options.get(i).getType().equalsIgnoreCase("TEXT")) {
                            addToList(mediaList, options.get(i).getData());
                        }
                    }
                }
            }
            if (q.getA() != null) {
                if (q.getImageChoiceA() != null && q.getImageChoiceA().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceA().getImageData());
                }
            }
            if (q.getB() != null) {
                if (q.getImageChoiceB() != null && q.getImageChoiceB().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceB().getImageData());
                }
            }
            if (q.getC() != null) {
                if (q.getImageChoiceC() != null && q.getImageChoiceC().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceC().getImageData());
                }
            }
            if (q.getD() != null) {
                if (q.getImageChoiceD() != null && q.getImageChoiceD().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceD().getImageData());
                }
            }
            if (q.getE() != null) {
                if (q.getImageChoiceE() != null && q.getImageChoiceE().getImageData() != null) {
                    addToList(mediaList, q.getImageChoiceE().getImageData());
                }
            }

            if (q.getAudio() != null) {
                addToList(mediaList, CLOUD_FRONT_BASE_HTTPS + "qaudio/" + q.getAudio());
            }
        }
    }

    public static void addToList(List<String> mediaList, String data) {
        Log.d("MediaList", "addToList: " + data);
        mediaList.add(data);
    }

    public static void prepareMediaList(List<String> mediaList, List<String> pathList, DTOQuestions q) {
        try {
            if (q != null) {
                if (q.getImageCDNPath() != null && !TextUtils.isEmpty(q.getImageCDNPath())) {
                    mediaList.add(q.getImageCDNPath());
                    pathList.add("");
                }
                if (q.getImageChoiceA() != null) {
                    if (q.getImageChoiceA().getImageData() != null) {
                        mediaList.add(q.getImageChoiceA().getImageData());
                        pathList.add("");
                    }
                }
                if (q.getImageChoiceB() != null) {
                    if (q.getImageChoiceB().getImageData() != null) {
                        mediaList.add(q.getImageChoiceB().getImageData());
                        pathList.add("");
                    }
                }
                if (q.getImageChoiceC() != null) {
                    if (q.getImageChoiceC().getImageData() != null) {
                        mediaList.add(q.getImageChoiceC().getImageData());
                        pathList.add("");
                    }
                }
                if (q.getImageChoiceD() != null) {
                    if (q.getImageChoiceD().getImageData() != null) {
                        mediaList.add(q.getImageChoiceD().getImageData());
                        pathList.add("");
                    }
                }
                if (q.getImageChoiceE() != null) {
                    if (q.getImageChoiceE().getImageData() != null) {
                        mediaList.add(q.getImageChoiceE().getImageData());
                        pathList.add("");
                    }
                }

                if (q.getMtfLeftCol() != null) {
                    List<DTOMTFColumnData> mtfColumnDatas = q.getMtfLeftCol();
                    for (int i = 0; i < mtfColumnDatas.size(); i++) {
                        if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                            if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                                pathList.add("");
                            }
                        }
                    }
                }
                if (q.getMtfRightCol() != null) {
                    List<DTOMTFColumnData> mtfColumnDatas = q.getMtfRightCol();
                    for (int i = 0; i < mtfColumnDatas.size(); i++) {
                        if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                            if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                                pathList.add("");
                            }
                        }
                    }
                }
                if (q.getOptionCategories() != null) {
                    List<DTOQuestionOptionCategory> optionCategories = q.getOptionCategories();
                    for (int i = 0; i < optionCategories.size(); i++) {
                        if (optionCategories.get(i) != null && optionCategories.get(i).getData() != null) {
                            if (!optionCategories.get(i).getType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(optionCategories.get(i).getData());
                                pathList.add("");
                            }
                        }
                    }
                }
                if (q.getOptions() != null) {
                    List<DTOQuestionOption> options = q.getOptions();
                    for (int i = 0; i < options.size(); i++) {
                        if (options.get(i) != null && options.get(i).getData() != null) {
                            if (!options.get(i).getType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(options.get(i).getData());
                                pathList.add("");
                            }
                        }
                    }
                    if (q.getA() != null) {
                        if (q.getImageChoiceA() != null && q.getImageChoiceA().getImageData() != null) {
                            addToList(mediaList, q.getImageChoiceA().getImageData());
                            pathList.add("");
                        }
                    }
                    if (q.getB() != null) {
                        if (q.getImageChoiceB() != null && q.getImageChoiceB().getImageData() != null) {
                            addToList(mediaList, q.getImageChoiceB().getImageData());
                            pathList.add("");
                        }
                    }
                    if (q.getC() != null) {
                        if (q.getImageChoiceC() != null && q.getImageChoiceC().getImageData() != null) {
                            addToList(mediaList, q.getImageChoiceC().getImageData());
                            pathList.add("");
                        }
                    }
                    if (q.getD() != null) {
                        if (q.getImageChoiceD() != null && q.getImageChoiceD().getImageData() != null) {
                            addToList(mediaList, q.getImageChoiceD().getImageData());
                            pathList.add("");
                        }
                    }
                    if (q.getE() != null) {
                        if (q.getImageChoiceE() != null && q.getImageChoiceE().getImageData() != null) {
                            addToList(mediaList, q.getImageChoiceE().getImageData());
                            pathList.add("");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public static void prepareMediaListAdaptive(List<String> mediaList, List<String> pathList, DTOQuestions q) {
        if (q.getImageCDNPath() != null) {
            mediaList.add(q.getImageCDNPath());
            pathList.add("");
        }
        if (q.getImageChoiceA() != null) {
            if (q.getImageChoiceA().getImageData() != null) {
                mediaList.add(q.getImageChoiceA().getImageData());
                pathList.add("");
            }
        }
        if (q.getImageChoiceB() != null) {
            if (q.getImageChoiceB().getImageData() != null) {
                mediaList.add(q.getImageChoiceB().getImageData());
                pathList.add("");
            }
        }
        if (q.getImageChoiceC() != null) {
            if (q.getImageChoiceC().getImageData() != null) {
                mediaList.add(q.getImageChoiceC().getImageData());
                pathList.add("");
            }
        }
        if (q.getImageChoiceD() != null) {
            if (q.getImageChoiceD().getImageData() != null) {
                mediaList.add(q.getImageChoiceD().getImageData());
                pathList.add("");
            }
        }
        if (q.getImageChoiceE() != null) {
            if (q.getImageChoiceE().getImageData() != null) {
                mediaList.add(q.getImageChoiceE().getImageData());
                pathList.add("");
            }
        }
        if (q.getImageChoiceAnswer() != null && !q.getQuestionType().equals(QuestionType.MRQ)) {
            if (q.getImageChoiceAnswer().getImageData() != null) {
                mediaList.add(q.getImageChoiceAnswer().getImageData());
                pathList.add("");
            }
        }
        if (q.getMtfLeftCol() != null) {
            List<DTOMTFColumnData> mtfColumnDatas = q.getMtfLeftCol();
            for (int i = 0; i < mtfColumnDatas.size(); i++) {
                if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                    if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                        pathList.add("");
                    }
                }
            }
        }
        if (q.getMtfRightCol() != null) {
            List<DTOMTFColumnData> mtfColumnDatas = q.getMtfRightCol();
            for (int i = 0; i < mtfColumnDatas.size(); i++) {
                if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                    if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                        pathList.add("");
                    }
                }
            }
        }
        if (q.getOptionCategories() != null) {
            List<DTOQuestionOptionCategory> optionCategories = q.getOptionCategories();
            for (int i = 0; i < optionCategories.size(); i++) {
                if (optionCategories.get(i) != null && optionCategories.get(i).getData() != null) {
                    if (!optionCategories.get(i).getType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(optionCategories.get(i).getData());
                        pathList.add("");
                    }
                }
            }
        }
        if (q.getOptions() != null) {
            List<DTOQuestionOption> options = q.getOptions();
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i) != null && options.get(i).getData() != null) {
                    if (!options.get(i).getType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(options.get(i).getData());
                        pathList.add("");
                    }
                }
            }
        }
        if (q.getA() != null) {
            if (q.getImageChoiceA() != null && q.getImageChoiceA().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceA().getImageData());
                pathList.add("");
            }
        }
        if (q.getB() != null) {
            if (q.getImageChoiceB() != null && q.getImageChoiceB().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceB().getImageData());
                pathList.add("");
            }
        }
        if (q.getC() != null) {
            if (q.getImageChoiceC() != null && q.getImageChoiceC().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceC().getImageData());
                pathList.add("");
            }
        }
        if (q.getD() != null) {
            if (q.getImageChoiceD() != null && q.getImageChoiceD().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceD().getImageData());
                pathList.add("");
            }
        }
        if (q.getE() != null) {
            if (q.getImageChoiceE() != null && q.getImageChoiceE().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceE().getImageData());
                pathList.add("");
            }
        }
    }


    public static void prepareMediaList(List<String> mediaList, Questions q) {
        if (q.getImageCDNPath() != null) {
            mediaList.add(q.getImageCDNPath());
        }
        if (q.getImageChoiceA() != null) {
            if (q.getImageChoiceA().getImageData() != null) {
                mediaList.add(q.getImageChoiceA().getImageData());
            }
            if (q.getImageChoiceB() != null && q.getImageChoiceB().getImageData() != null) {
                mediaList.add(q.getImageChoiceB().getImageData());
            }
            if (q.getImageChoiceC() != null && q.getImageChoiceC().getImageData() != null) {
                mediaList.add(q.getImageChoiceC().getImageData());
            }
            if (q.getImageChoiceD() != null && q.getImageChoiceD().getImageData() != null) {
                mediaList.add(q.getImageChoiceD().getImageData());
            }
            if (q.getImageChoiceE() != null && q.getImageChoiceE().getImageData() != null) {
                mediaList.add(q.getImageChoiceE().getImageData());
            }
            if (q.getImageChoiceAnswer() != null && q.getImageChoiceAnswer().getImageData() != null) {
                if (q.getImageChoiceAnswer() != null && !q.getQuestionType().equals(QuestionType.MRQ)) {
                    mediaList.add(q.getImageChoiceAnswer().getImageData());
                }
            }
        }
        if (q.getMtfLeftCol() != null) {
            List<MTFColumnData> mtfColumnDatas = q.getMtfLeftCol();
            for (int i = 0; i < mtfColumnDatas.size(); i++) {
                if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                    if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                    }
                }
            }
        }
        if (q.getMtfRightCol() != null) {
            List<MTFColumnData> mtfColumnDatas = q.getMtfRightCol();
            for (int i = 0; i < mtfColumnDatas.size(); i++) {
                if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                    if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                    }
                }
            }
        }
        if (q.getOptionCategories() != null) {
            List<QuestionOptionCategoryData> optionCategories = q.getOptionCategories();
            for (int i = 0; i < optionCategories.size(); i++) {
                if (optionCategories.get(i) != null && optionCategories.get(i).getData() != null) {
                    if (!optionCategories.get(i).getType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(optionCategories.get(i).getData());
                    }
                }
            }
        }
        if (q.getOptions() != null) {
            List<QuestionOptionData> options = q.getOptions();
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i) != null && options.get(i).getData() != null) {
                    if (!options.get(i).getType().equalsIgnoreCase("TEXT")) {
                        mediaList.add(options.get(i).getData());
                    }
                }
            }
        }
        if (q.getA() != null) {
            if (q.getImageChoiceA() != null && q.getImageChoiceA().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceA().getImageData());
            }
        }
        if (q.getB() != null) {
            if (q.getImageChoiceB() != null && q.getImageChoiceB().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceB().getImageData());
            }
        }
        if (q.getC() != null) {
            if (q.getImageChoiceC() != null && q.getImageChoiceC().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceC().getImageData());
            }
        }
        if (q.getD() != null) {
            if (q.getImageChoiceD() != null && q.getImageChoiceD().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceD().getImageData());
            }
        }
        if (q.getE() != null) {
            if (q.getImageChoiceE() != null && q.getImageChoiceE().getImageData() != null) {
                addToList(mediaList, q.getImageChoiceE().getImageData());
            }
        }
    }


    public static void PrepareMediaListForDownload(List<LearningPathData> mediaList, List<String> pathList, DTOQuestions q) {
        LearningPathData learningPathData = new LearningPathData();
        if (q.getImageCDNPath() != null) {
            learningPathData.setPathData(q.getImageCDNPath());
            //learningPathData.setFileName(q.get);
            //mediaList.add(q.getImageCDNPath());
            mediaList.add(learningPathData);
            pathList.add("");
        }
        if (q.getImageChoiceA() != null) {
            if (q.getImageChoiceA().getImageData() != null) {
                learningPathData.setPathData(q.getImageChoiceA().getImageData());
                mediaList.add(learningPathData);
                pathList.add("");
            }
        }
        if (q.getImageChoiceB() != null) {
            if (q.getImageChoiceB().getImageData() != null) {
                learningPathData.setPathData(q.getImageChoiceB().getImageData());
                mediaList.add(learningPathData);
                pathList.add("");
            }
        }
        if (q.getImageChoiceC() != null) {
            if (q.getImageChoiceC().getImageData() != null) {
                learningPathData.setPathData(q.getImageChoiceC().getImageData());
                mediaList.add(learningPathData);
                pathList.add("");
            }
        }
        if (q.getImageChoiceD() != null) {
            if (q.getImageChoiceD().getImageData() != null) {
                learningPathData.setPathData(q.getImageChoiceD().getImageData());
                mediaList.add(learningPathData);
                pathList.add("");
            }
        }
        if (q.getImageChoiceE() != null) {
            if (q.getImageChoiceE().getImageData() != null) {
                learningPathData.setPathData(q.getImageChoiceE().getImageData());
                mediaList.add(learningPathData);
                pathList.add("");
            }
        }
        if (q.getImageChoiceAnswer() != null && !q.getQuestionType().equals(QuestionType.MRQ)) {
            if (q.getImageChoiceAnswer().getImageData() != null) {
                learningPathData.setPathData(q.getImageChoiceAnswer().getImageData());
                mediaList.add(learningPathData);
                pathList.add("");
            }
        }
        if (q.getMtfLeftCol() != null) {
            List<DTOMTFColumnData> mtfColumnDatas = q.getMtfLeftCol();
            for (int i = 0; i < mtfColumnDatas.size(); i++) {
                if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                    if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                        learningPathData.setPathData(mtfColumnDatas.get(i).getMtfColData());
                        mediaList.add(learningPathData);
                        //mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                        pathList.add("");
                    }
                }
            }
        }
        if (q.getMtfRightCol() != null) {
            List<DTOMTFColumnData> mtfColumnDatas = q.getMtfRightCol();
            for (int i = 0; i < mtfColumnDatas.size(); i++) {
                if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                    if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                        learningPathData.setPathData(mtfColumnDatas.get(i).getMtfColData());
                        mediaList.add(learningPathData);
                        pathList.add("");
                    }
                }
            }
        }
        if (q.getOptionCategories() != null) {
            List<DTOQuestionOptionCategory> optionCategories = q.getOptionCategories();
            for (int i = 0; i < optionCategories.size(); i++) {
                if (optionCategories.get(i) != null && optionCategories.get(i).getData() != null) {
                    if (!optionCategories.get(i).getType().equalsIgnoreCase("TEXT")) {
                        learningPathData.setPathData(optionCategories.get(i).getData());
                        mediaList.add(learningPathData);
                        //mediaList.add(optionCategories.get(i).getData());
                        pathList.add("");
                    }
                }
            }
        }
        if (q.getOptions() != null) {
            List<DTOQuestionOption> options = q.getOptions();
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i) != null && options.get(i).getData() != null) {
                    if (!options.get(i).getType().equalsIgnoreCase("TEXT")) {
                        learningPathData.setPathData(options.get(i).getData());
                        mediaList.add(learningPathData);
                        //mediaList.add(options.get(i).getData());
                        pathList.add("");
                    }
                }
            }
        }
    }
}
