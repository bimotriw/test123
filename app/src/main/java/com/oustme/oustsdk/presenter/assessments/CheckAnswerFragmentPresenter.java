package com.oustme.oustsdk.presenter.assessments;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.assessment.Comments;
import com.oustme.oustsdk.fragments.assessments.CheckAnswersFragment;
import com.oustme.oustsdk.request.AddCommentRequest;
import com.oustme.oustsdk.request.QuestionFavouriteRequest;
import com.oustme.oustsdk.request.QuestionFeedBack;
import com.oustme.oustsdk.request.QuestionFeedBackRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.QuestionFeedBackResponse;
import com.oustme.oustsdk.response.assessment.UserGamePoints;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by shilpysamaddar on 20/03/17.
 */

public class CheckAnswerFragmentPresenter {
    private CheckAnswersFragment view;
    public int position;
    private ActiveUser activeUser;
    private ActiveGame activeGame;
    private SubmitRequest submitRequest;
    private UserGamePoints userGamePoints;
    private GamePoints gamePoints;
    private DTOQuestions questions;
    private int totalQues=0;
    private String txtSolution;
    private List<Comments> commentsList;
    private String questionType;
    private String questionCategory;

    public CheckAnswerFragmentPresenter(CheckAnswersFragment view, int position, ActiveUser activeUser, ActiveGame activeGame,
                                        DTOQuestions questions1, SubmitRequest submitRequest, UserGamePoints userGamePoints,
                                        GamePoints gamePoints, int totalQues) {
        this.view = view;
        this.position=position;
        this.activeGame=activeGame;
        this.activeUser=activeUser;
        this.submitRequest=submitRequest;
        this.userGamePoints=userGamePoints;
        this.gamePoints=gamePoints;
        this.questions=questions1;
        this.totalQues=totalQues;
    }
    public void setStartingData(){
        setTopArrows();
        if((questions!=null)){
            if(questions.getQuestion()!=null) {
                if(!questions.getQuestionType().equals(QuestionType.FILL)) {
                    view.setQuestion(questions.getQuestion());
                } else {
                    view.setQuestion("");
                }
            }
            showSolution();
            showTopUserInfomation();
            setSourceLayoutVisibility();
            setImageQuestion();
            displayOptionsAndAnswer();
            setFavouriteButtonVisibility();
            setLikeUnlikeButtonVisibility();
        }
        view.setQuestionCount((OustStrings.getString("questCountTxt")+(position+1)+" / "+totalQues));
    }

    private void setTopArrows(){
        if (position == 0) {
            view.showTopRightArrorw();
        } else if (position == (totalQues-1)) {
            view.showTopLeftArrow();
        } else {
            view.showBothArrow();
        }
    }
    private void showSolution(){
        txtSolution=questions.getSolution();
        view.showSolutionLayout(txtSolution);
    }
    private void showTopUserInfomation(){
        if(OustAppState.getInstance().isAssessmentGame()){
            showSingleTimerBlock();
        }else {
            if ((activeGame.getGameType() == GameType.MYSTERY) || (activeGame.getGameType() == GameType.CONTACTSCHALLENGE) ||
                    (activeGame.getGameType() == GameType.CHALLENGE) || (activeGame.getGameType() == GameType.GROUP) ||
                    (activeGame.getGameType() == GameType.REMATCH)) {
                if ((activeGame.getGameType() == GameType.MYSTERY) && (!activeGame.getOpponentid().equals("Mystery"))) {
                    showDoubleTimerBlock();
                } else {
                    showSingleTimerBlock();
                }
            } else if ((activeGame.getGroupId() != null)) {
                if (!activeGame.getGroupId().isEmpty()) {
                    showSingleTimerBlock();
                } else {
                    showDoubleTimerBlock();
                }
            } else {
                showDoubleTimerBlock();
            }
        }
    }
    private void showSingleTimerBlock(){
        if((activeUser.getUserDisplayName()!=null)){
            view.setDoubleUserChallengerName(activeUser.getUserDisplayName());
        }
        if(!OustAppState.getInstance().isAssessmentGame()) {
            view.showSingleUserTimer();
            if (userGamePoints != null) {
                if ((userGamePoints.getPoints()!=null)&&(userGamePoints.getPoints().length > position)) {
                    view.setDoubleUserChellengerScore(userGamePoints.getPoints()[position].getPoint());
                    if((userGamePoints.getPoints()[position].getTime()!=null)&&
                            (!userGamePoints.getPoints()[position].getTime().isEmpty())) {
                        view.settxtTimerSingle(userGamePoints.getPoints()[position].getTime());
                    }
                }
            } else {
                if((submitRequest!=null)&&(submitRequest.getScores()!=null)) {
                    view.setDoubleUserChellengerScore(submitRequest.getScores().get(position).getScore());
                    view.settxtTimerSingle((""+submitRequest.getScores().get(position).getTime()));
                }
            }
            setOpponentAvatar();
            setOpponentName();
        }else {
            view.showAssessmentTimer();
            if (userGamePoints != null) {
                if ((userGamePoints.getPoints()!=null)&&(userGamePoints.getPoints().length > position)) {
                    view.setDoubleUserChellengerScore(userGamePoints.getPoints()[position].getPoint());
                    if((userGamePoints.getPoints()[position].getTime()!=null)&&
                            (!userGamePoints.getPoints()[position].getTime().isEmpty())) {
                        view.settxtTimerAssessment(userGamePoints.getPoints()[position].getTime());
                    }
                }
            } else {
                if((submitRequest!=null)&&(submitRequest.getScores()!=null)) {
                    view.setDoubleUserChellengerScore(submitRequest.getScores().get(position).getScore());
                    view.settxtTimerAssessment((""+submitRequest.getScores().get(position).getTime()));
                }
            }
        }
        if(questions.getTopic()!=null){
            view.setDoubleUserTopicText(questions.getTopic());
        }
        setChallengerAvatar();
        if(OustAppState.getInstance().isAssessmentGame()) {
            if(OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType()!=null) {
                if ((OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC)) {
                    view.hideScoreForAssessment();
                }
            }
        }
        if((questions.getQuestionType()!=null)) {
            if ((questions.getQuestionType().equals(QuestionType.SURVEY))) {
                view.hideScoreForAssessment();
            }
        }
    }

    private void showDoubleTimerBlock(){
        view.showDoubleUserInformation();
        if((activeUser.getUserDisplayName()!=null)){
            view.setDoubleUserChallengerName(activeUser.getUserDisplayName());
        }
        if (userGamePoints != null) {
            if ((userGamePoints.getPoints()!=null)&&(userGamePoints.getPoints().length > position)) {
                view.setDoubleUserChellengerScore(userGamePoints.getPoints()[position].getPoint());
                if((userGamePoints.getPoints()[position].getTime()!=null)&&
                        (!userGamePoints.getPoints()[position].getTime().isEmpty())) {
                    view.setDoubleUserChallengerTimeText(userGamePoints.getPoints()[position].getTime());
                }
            }
        } else {
            if((submitRequest!=null)&&(submitRequest.getScores()!=null)){
                view.setDoubleUserChellengerScore(submitRequest.getScores().get(position).getScore());
                view.setDoubleUserChallengerTimeText((""+submitRequest.getScores().get(position).getTime()));
            }
        }
        if (gamePoints!=null) {
            if((gamePoints.getPoints()!=null)&&(gamePoints.getPoints().length>position)) {
                view.setDoubleUserOppenentScore(gamePoints.getPoints()[position].getPoint());
                view.setDoubleUserOppenentTimeText(gamePoints.getPoints()[position].getTime());
            }
        }
        if(questions.getTopic()!=null){
            view.setDoubleUserTopicText(questions.getTopic());
        }
        setOpponentAvatar();
        setOpponentName();
        setChallengerAvatar();
    }
    private void setOpponentAvatar(){
        if((activeGame.getGameType()!=null)){
            switch (activeGame.getGameType()) {
                case GROUP:
                    if(activeGame.getOpponentDisplayName()!=null) {
                        view.setOpponentGroupAvatar(activeGame.getOpponentDisplayName());
                    }
                    break;
                default:
                    if((activeGame.getGroupName()!=null)&&(!activeGame.getGroupName().isEmpty())){
                        view.setOpponentGroupAvatar(activeGame.getOpponentDisplayName());
                    }else {
                        if (activeGame.getGameType()==GameType.CONTACTSCHALLENGE) {
                            if ((activeGame.getOpponentDisplayName()!=null)&&(!activeGame.getOpponentDisplayName().isEmpty())&&(!activeGame.getOpponentDisplayName().equalsIgnoreCase("null"))) {
                                view.setOpponentGroupAvatar(activeGame.getOpponentDisplayName());
                            }else {
                                view.setOpponentGroupAvatar("RE");
                            }
                        }
                        else {
                            if((activeGame.getOpponentAvatar()!=null)&&(!activeGame.getOpponentAvatar().isEmpty())){
                                if (activeGame.getOpponentAvatar().startsWith("http")) {
                                    view.setOpponenentAvatar(activeGame.getOpponentAvatar());
                                } else {
                                    view.setOpponenentAvatar((OustSdkApplication.getContext().getString(R.string.oust_user_avatar_link)+activeGame.getOpponentAvatar()));
                                }
                            }
                        }
                    }
            }
        }
    }

    private void setOpponentName(){
        if((activeGame.getOpponentDisplayName()!=null)&&(!activeGame.getOpponentDisplayName().isEmpty())&&(!activeGame.getOpponentDisplayName().equalsIgnoreCase("null"))){
            view.setOppeonentName(activeGame.getOpponentDisplayName());
        }else {
            if((activeGame.getGameType()!=null)&&(activeGame.getGameType()== GameType.CONTACTSCHALLENGE)){
                if((activeGame.getOpponentid()!=null)){
                    view.setOppeonentName(activeGame.getOpponentid().substring(4));
                }
            }
        }
    }

    private void setChallengerAvatar() {
        if ((activeGame.getGameType() != null) && (activeGame.getGameType() == GameType.ACCEPTCONTACTSCHALLENGE)) {
            if (activeGame.getChallengerDisplayName() != null) {
                view.setContactChallengerAvatar(activeGame.getChallengerDisplayName());
            }
        } else {
            if((activeUser.getAvatar()!=null)&&(!activeUser.getAvatar().isEmpty())){
                view.setChallengerAvatar(activeUser.getAvatar());
            }
        }
    }

    private void setSourceLayoutVisibility() {
        if((questions.getVendorId()!=null)&&(questions.getVendorId().isEmpty())
                &&(questions.getVendorId().equalsIgnoreCase("0"))){
            if(questions.getVendorDisplayName()!=null) {
                view.showSourceLayout(questions.getVendorDisplayName());
            }
        }else {
            view.hideSoureceLayout();
        }
    }

    private void setImageQuestion(){
        if((questions.getImage()!=null) && (!questions.getQuestionType().equals(QuestionType.FILL))){
            view.setImageForQuestion(questions.getImage());
        }
    }

    private void displayOptionsAndAnswer(){
        questionType=questions.getQuestionType();
        questionCategory=questions.getQuestionCategory();
        if((questionCategory!=null)&&(questionCategory.equals(QuestionCategory.IMAGE_CHOICE))){
            view.hideFillLayout();
            if ((questionType != null)) {
                if (questionType.equals(QuestionType.MRQ)) {
                    displayMRQImageQuestions();
                } else {
                    displayCorrectAndWrongImageAnswer();
                }
            } else {
                questionType = QuestionType.MCQ;
                displayCorrectAndWrongAnswer();
            }
        }else if(questionType.equals(QuestionType.FILL)){
            view.setFillLayout(questions);
        }else if((questionCategory!=null)&&(questionCategory.equals(QuestionCategory.MATCH))){
            view.setMatchQuestionLayoutSize(questions);
        } else {
            view.hideFillLayout();
            if ((questionType != null)) {
                if (questionType.equals(QuestionType.MRQ)) {
                    displayMRQQuestions();
                } else {
                    displayCorrectAndWrongAnswer();
                }
            } else {
                questionType = QuestionType.MCQ;
                displayCorrectAndWrongAnswer();
            }
        }
    }

    private void displayCorrectAndWrongAnswer() {
        try {
            if ((userGamePoints != null)&&(userGamePoints.getPoints()!=null)) {
                if((userGamePoints.getPoints().length>position)&&(questionType!=QuestionType.SURVEY)) {
                    if((questions.getAnswer()!=null)&&(!questions.getAnswer().isEmpty())) {
                        String ans=(URLDecoder.decode(userGamePoints.getPoints()[position].getAnswer(), "UTF-8"));
                        if (ans.equalsIgnoreCase(questions.getAnswer())) {
                            view.setOptionBRight(userGamePoints.getPoints()[position].getAnswer());
                        } else {
                            view.setOptionBRight(questions.getAnswer());
                            if ((userGamePoints.getPoints()[position].getAnswer()!=null)&&(!userGamePoints.getPoints()[position].getAnswer().isEmpty())) {
                                view.setOptionAWrong(userGamePoints.getPoints()[position].getAnswer());
                            } else {
                                view.setOptionAWrong(OustStrings.getString("questionNoAnswerTxt"));
                            }
                        }
                    }else {
                        if(userGamePoints.getPoints()[position].getAnswer()!=null) {
                            view.setOptionB(userGamePoints.getPoints()[position].getAnswer());
                        }
                    }
                }else {
                    if(userGamePoints.getPoints()[position].getAnswer()!=null) {
                        view.setOptionB(userGamePoints.getPoints()[position].getAnswer());
                    }
                }
            } else {
                if((questions.getAnswer()!=null)&&(!questions.getAnswer().isEmpty())&&(questionType!=QuestionType.SURVEY)) {
                    String ans="";
                    if((submitRequest!=null)&&(submitRequest.getScores()!=null)&&(submitRequest.getScores().size()>position)&&(submitRequest.getScores().get(position).getAnswer()!=null)) {
                        ans = (URLDecoder.decode(submitRequest.getScores().get(position).getAnswer(), "UTF-8"));
                    }
                    if (ans.equalsIgnoreCase(questions.getAnswer())) {
                        view.setOptionBRight(submitRequest.getScores().get(position).getAnswer());
                    } else {
                        view.setOptionBRight(questions.getAnswer());
                        if ((ans!=null)&&(!ans.isEmpty())) {
                            view.setOptionAWrong(submitRequest.getScores().get(position).getAnswer());
                        } else {
                            view.setOptionAWrong(OustStrings.getString("questionNoAnswerTxt"));
                        }
                    }
                }else {
                    if((submitRequest!=null)&&(submitRequest.getScores()!=null)&&(submitRequest.getScores().size()>position)&&(submitRequest.getScores().get(position).getAnswer()!=null)) {
                        view.setOptionB(submitRequest.getScores().get(position).getAnswer());
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void displayCorrectAndWrongImageAnswer() {
        try {
            if ((userGamePoints != null)&&(userGamePoints.getPoints()!=null)) {
                if((userGamePoints.getPoints().length>position)) {
                    if((questions.getImageChoiceAnswer()!=null)&&(questions.getImageChoiceAnswer().getImageFileName()!=null)) {
                        String ans=userGamePoints.getPoints()[position].getAnswer();
                        if (ans.equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName())) {
                            view.showOptionAImage(getAnswerByFileNAme(ans),1);
                        } else {
                            view.showOptionAImage(getAnswerByFileNAme(questions.getImageChoiceAnswer().getImageFileName()),1);
                            if ((userGamePoints.getPoints()[position].getAnswer()!=null)&&(!userGamePoints.getPoints()[position].getAnswer().isEmpty())) {
                                view.showOptionBImage(getAnswerByFileNAme(ans),2);
                            }
                        }
                    }else {
                        if(userGamePoints.getPoints()[position].getAnswer()!=null) {
                            view.showOptionAImage(getAnswerByFileNAme(userGamePoints.getPoints()[position].getAnswer()),1);
                        }
                    }
                }
            } else {
                if((questions.getImageChoiceAnswer()!=null)&&(questions.getImageChoiceAnswer().getImageFileName()!=null)) {
                    String ans="";
                    if((submitRequest!=null)&&(submitRequest.getScores()!=null)&&(submitRequest.getScores().size()>position)&&(submitRequest.getScores().get(position).getAnswer()!=null)) {
                        ans = submitRequest.getScores().get(position).getAnswer();
                    }
                    if (ans.equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName())) {
                        view.showOptionAImage(getAnswerByFileNAme(ans),1);
                    } else {
                        view.showOptionAImage(getAnswerByFileNAme(questions.getImageChoiceAnswer().getImageFileName()),1);
                        if ((ans!=null)&&(!ans.isEmpty())) {
                            view.showOptionBImage(getAnswerByFileNAme(ans),2);
                        }
                    }
                }else {
                    if((submitRequest!=null)&&(submitRequest.getScores()!=null)&&(submitRequest.getScores().size()>position)&&(submitRequest.getScores().get(position).getAnswer()!=null)) {
                        view.showOptionAImage(getAnswerByFileNAme(submitRequest.getScores().get(position).getAnswer()),1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String getAnswerByFileNAme(String fileName){
        String imgAnswer="";
        if((questions.getImageChoiceA()!=null)&&(questions.getImageChoiceA().getImageFileName()!=null)&&(questions.getImageChoiceA().getImageData()!=null)){
            if((questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(fileName))) {
                imgAnswer = questions.getImageChoiceA().getImageData();
                return imgAnswer;
            }
        }
        if((questions.getImageChoiceB()!=null)&&(questions.getImageChoiceB().getImageFileName()!=null)&&(questions.getImageChoiceB().getImageData()!=null)){
            if((questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(fileName))) {
                imgAnswer = questions.getImageChoiceB().getImageData();
                return imgAnswer;
            }
        }
        if((questions.getImageChoiceC()!=null)&&(questions.getImageChoiceC().getImageFileName()!=null)&&(questions.getImageChoiceC().getImageData()!=null)){
            if((questions.getImageChoiceC().getImageFileName().equalsIgnoreCase(fileName))) {
                imgAnswer = questions.getImageChoiceC().getImageData();
                return imgAnswer;
            }
        }
        if((questions.getImageChoiceD()!=null)&&(questions.getImageChoiceD().getImageFileName()!=null)&&(questions.getImageChoiceD().getImageData()!=null)){
            if((questions.getImageChoiceD().getImageFileName().equalsIgnoreCase(fileName))) {
                imgAnswer = questions.getImageChoiceD().getImageData();
                return imgAnswer;
            }
        }
        if((questions.getImageChoiceE()!=null)&&(questions.getImageChoiceE().getImageFileName()!=null)&&(questions.getImageChoiceE().getImageData()!=null)){
            if((questions.getImageChoiceE().getImageFileName().equalsIgnoreCase(fileName))) {
                imgAnswer = questions.getImageChoiceE().getImageData();
            }
        }
        return imgAnswer;
    }


    private void displayMRQImageQuestions(){
        String mrqMyAnswer="";
        String mrqAnswer=questions.getAnswer();
        try {
            mrqAnswer = questions.getImageChoiceAnswer().getImageFileName();
        } catch (Exception e) {}
        int indexNo=0;
        if(mrqAnswer==null){
            mrqAnswer="";
        }
        if ((userGamePoints != null)&&(userGamePoints.getPoints()!=null)&&
                (userGamePoints.getPoints().length>position)&&(userGamePoints.getPoints()[position].getAnswer()!=null)) {
            mrqMyAnswer=userGamePoints.getPoints()[position].getAnswer();
        } else {
            if((submitRequest!=null)&&(submitRequest.getScores()!=null)&&(submitRequest.getScores().size()>position)&&
                    (submitRequest.getScores().get(position).getAnswer()!=null)) {
                mrqMyAnswer = submitRequest.getScores().get(position).getAnswer();
            }
        }
        if((mrqAnswer!=null)&&(!mrqAnswer.isEmpty())) {
            if ((mrqAnswer.contains("A")) || (mrqAnswer.contains("a"))) {
                indexNo++;
                if((questions.getImageChoiceA()!=null)&&(questions.getImageChoiceA().getImageData()!=null)) {
                    showAnswer(indexNo, questions.getImageChoiceA().getImageData(), 1);
                }
            } else {
                if ((mrqMyAnswer.contains("A")) || (mrqMyAnswer.contains("a"))) {
                    indexNo++;
                    if((questions.getImageChoiceA()!=null)&&(questions.getImageChoiceA().getImageData()!=null)) {
                        showAnswer(indexNo, questions.getImageChoiceA().getImageData(), 2);
                    }
                }
            }
            if ((mrqAnswer.contains("B")) || (mrqAnswer.contains("b"))) {
                indexNo++;
                if((questions.getImageChoiceB()!=null)&&(questions.getImageChoiceB().getImageData()!=null)) {
                    showAnswer(indexNo, questions.getImageChoiceB().getImageData(), 1);
                }
            } else {
                if ((mrqMyAnswer.contains("B")) || (mrqMyAnswer.contains("b"))) {
                    indexNo++;
                    if((questions.getImageChoiceB()!=null)&&(questions.getImageChoiceB().getImageData()!=null)) {
                        showAnswer(indexNo, questions.getImageChoiceB().getImageData(), 2);
                    }
                }
            }
            if ((mrqAnswer.contains("C")) || (mrqAnswer.contains("c"))) {
                indexNo++;
                if((questions.getImageChoiceC()!=null)&&(questions.getImageChoiceC().getImageData()!=null)) {
                    showAnswer(indexNo, questions.getImageChoiceC().getImageData(), 1);
                }
            } else {
                if ((mrqMyAnswer.contains("C")) || (mrqMyAnswer.contains("c"))) {
                    indexNo++;
                    if((questions.getImageChoiceC()!=null)&&(questions.getImageChoiceC().getImageData()!=null)) {
                        showAnswer(indexNo, questions.getImageChoiceC().getImageData(), 2);
                    }
                }
            }
            if ((mrqAnswer.contains("D")) || (mrqAnswer.contains("d"))) {
                indexNo++;
                if((questions.getImageChoiceD()!=null)&&(questions.getImageChoiceD().getImageData()!=null)) {
                    showAnswer(indexNo, questions.getImageChoiceD().getImageData(), 1);
                }
            } else {
                if ((mrqMyAnswer.contains("D")) || (mrqMyAnswer.contains("d"))) {
                    indexNo++;
                    if((questions.getImageChoiceD()!=null)&&(questions.getImageChoiceD().getImageData()!=null)) {
                        showAnswer(indexNo, questions.getImageChoiceD().getImageData(), 2);
                    }
                }
            }
            if (((mrqAnswer.contains("E")) || (mrqAnswer.contains("e")))) {
                indexNo++;
                if((questions.getImageChoiceE()!=null)&&(questions.getImageChoiceE().getImageData()!=null)) {

                    showAnswer(indexNo, questions.getImageChoiceE().getImageData(), 1);
                }
            } else {
                if ((mrqMyAnswer.contains("E")) || (mrqMyAnswer.contains("e"))) {
                    indexNo++;
                    if((questions.getImageChoiceE()!=null)&&(questions.getImageChoiceE().getImageData()!=null)) {
                        showAnswer(indexNo, questions.getImageChoiceE().getImageData(), 2);
                    }
                }
            }
        }
    }

    private void showAnswer(int indexNo,String str,int rightWrongNo){
        if(indexNo==1){
            view.showOptionAImage(str,rightWrongNo);
        }else if(indexNo==2){
            view.showOptionBImage(str,rightWrongNo);
        }else if(indexNo==3){
            view.showOptionCImage(str,rightWrongNo);
        }else if(indexNo==4){
            view.showOptionDImage(str,rightWrongNo);
        }else if(indexNo==5){
            view.showOptionEImage(str,rightWrongNo);
        }
    }


    private void displayMRQQuestions(){
        String mrqMyAnswer="";
        String mrqAnswer=questions.getAnswer();
        if(mrqAnswer==null){
            mrqAnswer="";
        }
        if ((userGamePoints != null)&&(userGamePoints.getPoints()!=null)&&
                (userGamePoints.getPoints().length>position)&&(userGamePoints.getPoints()[position].getAnswer()!=null)) {
            mrqMyAnswer=userGamePoints.getPoints()[position].getAnswer();
        } else {
            if((submitRequest!=null)&&(submitRequest.getScores()!=null)&&(submitRequest.getScores().size()>position)&&
                    (submitRequest.getScores().get(position).getAnswer()!=null)) {
                mrqMyAnswer = submitRequest.getScores().get(position).getAnswer();
            }
        }
        if((mrqAnswer!=null)&&(!mrqAnswer.isEmpty())) {
            if ((mrqAnswer.contains("A")) || (mrqAnswer.contains("a"))) {
                view.setOptionARight(questions.getA());
            } else {
                if ((mrqMyAnswer.contains("A")) || (mrqMyAnswer.contains("a"))) {
                    view.setOptionAWrong(questions.getA());
                }
            }
            if ((mrqAnswer.contains("B")) || (mrqAnswer.contains("b"))) {
                view.setOptionBRight(questions.getB());
            } else {
                if ((mrqMyAnswer.contains("B")) || (mrqMyAnswer.contains("b"))) {
                    view.setOptionBWrong(questions.getB());
                }
            }

            if ((mrqAnswer.contains("C")) || (mrqAnswer.contains("c"))) {
                view.setOptionCRight(questions.getC());
            } else {
                if ((mrqMyAnswer.contains("C")) || (mrqMyAnswer.contains("c"))) {
                    view.setOptionCWrong(questions.getC());
                }
            }
            if ((mrqAnswer.contains("D")) || (mrqAnswer.contains("d"))) {
                view.setOptionDRight(questions.getD());
            } else {
                if ((mrqMyAnswer.contains("D")) || (mrqMyAnswer.contains("d"))) {
                    view.setOptionDWrong(questions.getD());
                }
            }

            if (((mrqAnswer.contains("E")) || (mrqAnswer.contains("e")))) {
                view.setOptionERight(questions.getE());
            } else {
                if ((mrqMyAnswer.contains("E")) || (mrqMyAnswer.contains("e"))) {
                    view.setOptionEWrong(questions.getE());
                }
            }
            if (((mrqAnswer.contains("F")) || (mrqAnswer.contains("f")))) {
                view.setOptionFRight(questions.getF());
            } else {
                if ((mrqMyAnswer.contains("F")) || (mrqMyAnswer.contains("f"))) {
                    view.setOptionFWrong(questions.getF());
                }
            }
            if (((mrqAnswer.contains("G")) || (mrqAnswer.contains("g")))) {
                view.setOptionGRight(questions.getG());
            } else {
                if ((mrqMyAnswer.contains("G")) || (mrqMyAnswer.contains("g"))) {
                    view.setOptionGWrong(questions.getG());
                }
            }
            if (mrqMyAnswer.isEmpty()) {
                view.setOptionH(OustStrings.getString("questionNoAnswerTxt"));
            }
        }else {
            if ((mrqMyAnswer.contains("A")) || (mrqMyAnswer.contains("a"))) {
                view.setOptionA(questions.getA());
            }
            if ((mrqMyAnswer.contains("B")) || (mrqMyAnswer.contains("b"))) {
                view.setOptionB(questions.getB());
            }
            if ((mrqMyAnswer.contains("C")) || (mrqMyAnswer.contains("c"))) {
                view.setOptionC(questions.getC());
            }
            if ((mrqMyAnswer.contains("D")) || (mrqMyAnswer.contains("d"))) {
                view.setOptionD(questions.getD());
            }
            if ((mrqMyAnswer.contains("E")) || (mrqMyAnswer.contains("e"))) {
                view.setOptionE(questions.getE());
            }
            if ((mrqMyAnswer.contains("F")) || (mrqMyAnswer.contains("f"))) {
                view.setOptionF(questions.getF());
            }
            if ((mrqMyAnswer.contains("G")) || (mrqMyAnswer.contains("g"))) {
                view.setOptionG(questions.getG());
            }
            if (mrqMyAnswer.isEmpty()) {
                view.setOptionH(OustStrings.getString("questionNoAnswerTxt"));
            }
        }
    }

    private void setLikeUnlikeButtonVisibility() {
        if (questions.getLikeUnlike() != null) {
            if (questions.getLikeUnlike().equals(QuestionFeedBack.like.name())) {
                view.setLikeQuestionLayout();
            } else {
                view.setUnlikeQuestionLayout();
            }
        } else {
            view.setLikeUnlike_notSet();
        }
    }

    private void setFavouriteButtonVisibility() {
        if ((questions.getFavourite() != null)) {
            if (questions.getFavourite()) {
                view.setQuestionFavorite();
            } else {
                view.setQuestionNotFavorite();
            }
        } else {
            view.setQuestionNotFavorite();
        }
    }

    public void setFavoriteBtnClick(){
        QuestionFavouriteRequest questionFavouriteRequest = new QuestionFavouriteRequest();
        questionFavouriteRequest.setQuestionId(Long.toString(questions.getQuestionId()));
        questionFavouriteRequest.setStudentid(activeUser.getStudentid());
        view.getFavourite(questionFavouriteRequest);
    }
    public void getFavoriteQuestionProcessFinish(CommonResponse commonResponse){
        if (commonResponse != null) {
            if (commonResponse.isSuccess()) {
                OustDBBuilder.setFavourite(questions,true);
                if(OustAppState.getInstance().getPlayResponse()!=null){
                    if(OustAppState.getInstance().getPlayResponse().getqIdList()!=null&&OustAppState.getInstance().getPlayResponse().getqIdList().size()>0){
                        OustDBBuilder.addorUpdateQuestion(questions);
                    }else {
                        if(OustAppState.getInstance().getPlayResponse().getQuestions()!=null) {
                            OustAppState.getInstance().getPlayResponse().setQuestionDataByIndex(position,questions);
                        }
                    }
                }
                setFavouriteButtonVisibility();
            }
        }else{
            OustSdkTools.showToast("retry_internet_msg");
        }
    }

    public void setLikeButtonClick(){
        if ((questions.getLikeUnlike() != null) && (!questions.getLikeUnlike().isEmpty()) &&
                (questions.getLikeUnlike().equals(QuestionFeedBack.like.name()))) {
            return;
        }
        QuestionFeedBackRequest questionFeedBackRequest = new QuestionFeedBackRequest();
        questionFeedBackRequest.setFeedback("");
        questionFeedBackRequest.setLikeUnlike(QuestionFeedBack.like.name());
        questionFeedBackRequest.setStudentid(activeUser.getStudentid());
        questionFeedBackRequest.setQuestionId(Long.toString(questions.getQuestionId()));
        view.setFeedback(questionFeedBackRequest);
    }
    public void getLikeUnlikeQuestionProcessFinish(QuestionFeedBackResponse questionFeedBackResponse, final QuestionFeedBackRequest questionFeedBackRequest){
        if (questionFeedBackResponse != null) {
            if (questionFeedBackResponse.getSuccess()) {
                OustDBBuilder.setLikeStatus(questions,questionFeedBackRequest.getLikeUnlike());
                if(OustAppState.getInstance().getPlayResponse()!=null){
                    if(OustAppState.getInstance().getPlayResponse().getqIdList()!=null&&OustAppState.getInstance().getPlayResponse().getqIdList().size()>0){
                        OustDBBuilder.addorUpdateQuestion(questions);
                    }else {
                        if(OustAppState.getInstance().getPlayResponse().getQuestions()!=null) {
                            OustAppState.getInstance().getPlayResponse().setQuestionDataByIndex(position,questions);
                        }
                    }
                }
                setLikeUnlikeButtonVisibility();
            }
        }
    }

    public void setUnlikeButtonClick(){
        if ((questions.getLikeUnlike() != null) && (!questions.getLikeUnlike().isEmpty())
                && (questions.getLikeUnlike().equals(QuestionFeedBack.unlike.name()))) {
            return;
        }
        view.showUnlikePopup();
    }
    public void submitButtonClicked(String incorrectQuestionText, String grammaticalErrorText, String incorrectAnswersText){
        String feedBack="";
        if(!incorrectQuestionText.isEmpty()){
            feedBack=feedBack+incorrectQuestionText+",";
        }
        if(!grammaticalErrorText.isEmpty()){
            feedBack=feedBack+grammaticalErrorText+",";
        }
        if(!incorrectAnswersText.isEmpty()){
            feedBack=feedBack+incorrectAnswersText+",";
        }
        if (feedBack.length() > 1) {
            feedBack = feedBack.substring(0, feedBack.length() - 1);
        }
        QuestionFeedBackRequest questionFeedBackRequest = new QuestionFeedBackRequest();
        questionFeedBackRequest.setFeedback(feedBack);
        questionFeedBackRequest.setStudentid(activeUser.getStudentid());
        questionFeedBackRequest.setLikeUnlike(QuestionFeedBack.unlike.name());
        questionFeedBackRequest.setQuestionId((""+questions.getQuestionId()));
        view.setFeedback(questionFeedBackRequest);
    }


    //--------------------------------------------------------------------------------
//--comment related methodes
    public void showCommentAnimationOver(){
        view.showComments((""+questions.getQuestionId()));
    }
    public void setCommentList(List<Comments> commentList){
        this.commentsList=commentList;
        if((txtSolution!=null)&&(!txtSolution.isEmpty())) {
            Comments comment1 = new Comments();
            comment1.setUserComment(txtSolution);
            comment1.setCommentType("oust");
            commentsList.add(0,comment1);
        }
        showCommentsList();
    }
    public Comparator<Comments> commentComp = new Comparator<Comments>() {
        public int compare(Comments c1, Comments c2) {
            return ((int)c2.getTimeStamp())-((int)c1.getTimeStamp());
        }
    };

    public void showCommentsList(){
        try {
            if ((commentsList != null) && (commentsList.size() > 0)) {
                Collections.sort(commentsList, commentComp);
                view.showCommentList(commentsList);
            } else {
                view.showCommentNotAddedText();
            }
        }catch (Exception e){}
    }

    public void onCommentAddButtonClicked(String comment){
        if((comment!=null)&&(!comment.isEmpty())){
            AddCommentRequest addCommentRequest = new AddCommentRequest();
            addCommentRequest.setStudentid(activeUser.getStudentid());
            addCommentRequest.setComment(comment);
            view.addCommeteApi(addCommentRequest,questions.getQuestionId());
        }else {
            view.showError(OustStrings.getString("emptycommenttext"));
        }
    }
    public void commentAddProcessFinish(CommonResponse commonResponse){
        if(commonResponse!=null) {
            if (commonResponse.isSuccess()) {
                view.commentAddedSuccessfully();
            } else {
                if (commonResponse.getPopup() != null) {
                    view.showErrorPopup(commonResponse.getPopup());
                }else if(commonResponse.getError()!=null) {
                    view.showError(commonResponse.getError());
                }else {
                    view.showError(OustStrings.getString("commentreq_failed"));
                }
            }
        }else {
            view.showError(OustStrings.getString("retry_internet_msg"));
        }
    }
}
