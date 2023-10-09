package com.oustme.oustsdk.question_module.course;

import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOImageChoice;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CourseQuestionHandling {

    public static DTOCourseCard randomizeOption(DTOCourseCard courseCardClass) {
        try {
            if (courseCardClass.getCardType().equalsIgnoreCase("QUESTION") && (courseCardClass.getQuestionData().isRandomize())) {
                DTOQuestions learningQuestionData = courseCardClass.getQuestionData();
                if ((learningQuestionData.getQuestionType() != null) && (learningQuestionData.getQuestionType() != QuestionType.MRQ)) {
                    if (learningQuestionData.getQuestionCategory() != null) {
                        if (learningQuestionData.getQuestionCategory().equals(QuestionCategory.IMAGE_CHOICE)) {
                            List<DTOImageChoice> optionList = new ArrayList<>();
                            if ((learningQuestionData.getImageChoiceA() != null) && (learningQuestionData.getImageChoiceA().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceA());
                            }
                            if ((learningQuestionData.getImageChoiceB() != null) && (learningQuestionData.getImageChoiceB().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceB());
                            }
                            if ((learningQuestionData.getImageChoiceC() != null) && (learningQuestionData.getImageChoiceC().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceC());
                            }
                            if ((learningQuestionData.getImageChoiceD() != null) && (learningQuestionData.getImageChoiceD().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceD());
                            }
                            Collections.shuffle(optionList);
                            int n1 = 0;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceA(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceB(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceC(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceD(optionList.get(n1));
                            }
                        } else if (learningQuestionData.getQuestionCategory().equals(QuestionCategory.MATCH)) {
//                            Collections.shuffle(learningQuestionData.getMtfLeftCol());
                            Collections.shuffle(learningQuestionData.getMtfRightCol());
                        } else {
                            List<String> optionList = new ArrayList<>();
                            if ((learningQuestionData.getA() != null)) {
                                optionList.add(learningQuestionData.getA());
                            }
                            if ((learningQuestionData.getB() != null)) {
                                optionList.add(learningQuestionData.getB());
                            }
                            if ((learningQuestionData.getC() != null)) {
                                optionList.add(learningQuestionData.getC());
                            }
                            if ((learningQuestionData.getD() != null)) {
                                optionList.add(learningQuestionData.getD());
                            }
                            if ((learningQuestionData.getE() != null)) {
                                optionList.add(learningQuestionData.getE());
                            }
                            if ((learningQuestionData.getF() != null)) {
                                optionList.add(learningQuestionData.getF());
                            }
                            if ((learningQuestionData.getG() != null)) {
                                optionList.add(learningQuestionData.getG());
                            }
                            Collections.shuffle(optionList);
                            int n1 = 0;
                            if (optionList.size() > n1) {
                                learningQuestionData.setA(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setB(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setC(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setD(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setE(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setF(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setG(optionList.get(n1));
                            }
                        }
                    }
                }
                courseCardClass.setQuestionData(learningQuestionData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return courseCardClass;
    }

}
