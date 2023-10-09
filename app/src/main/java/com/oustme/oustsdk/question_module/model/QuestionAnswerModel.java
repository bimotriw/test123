package com.oustme.oustsdk.question_module.model;

public class QuestionAnswerModel {
    String answerOption;
    String option;
    String image;
    String optionType;

    public String getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(String answerOption) {
        this.answerOption = answerOption;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /*public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }*/

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }
}
