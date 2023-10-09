package com.oustme.oustsdk.adapter.assessments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.fragments.assessments.CheckAnswersFragment;
import com.oustme.oustsdk.tools.OustAppState;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

public class CheckAnswerViewAdapter  extends FragmentStatePagerAdapter {

    Bundle bundle;
    Gson gson;
    public final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public final String ActiveUser = "activeUser";
    public final String ActiveGame = "activeGame";
    public final String CreateGameResponse = "createGameResponse";
    public final String PlayResponse = "playResponse";
    public final String SubmitRequest = "submitRequest";
    public final String GamePoints = "gamePoints";
    public final String UserGamePoints = "userGamePoints";

    String activeUser,activeGame, createGameResponse,submitRequest, gamePoints, userGamePoints;

    public CheckAnswerViewAdapter(FragmentManager supportFragmentManager, String activeUser, String activeGame, String createGameResponse, String submitRequest, String gamePoints, String userGamePoints) {
        super(supportFragmentManager);
        this.activeUser=activeUser;
        this.activeGame=activeGame;
        this.createGameResponse=createGameResponse;
        this.submitRequest=submitRequest;
        this.gamePoints=gamePoints;
        this.userGamePoints=userGamePoints;
    }

    @Override
    public Fragment getItem(int position) {
        gson = new GsonBuilder().create();

        bundle = new Bundle(9);
        bundle.putString(EXTRA_MESSAGE, position+"");
        bundle.putString(ActiveUser, activeUser);
        bundle.putString(ActiveGame, activeGame);
        bundle.putString(CreateGameResponse, createGameResponse);
        bundle.putString(SubmitRequest, submitRequest);
        bundle.putString(GamePoints,gamePoints);
        bundle.putString(UserGamePoints, userGamePoints);

        CheckAnswersFragment checkAnswersFragment = new CheckAnswersFragment();
        checkAnswersFragment.setArguments(bundle);
        return checkAnswersFragment;
    }
    @Override
    public int getCount() {
        int n1=0;
        if((OustAppState.getInstance().getPlayResponse().getqIdList()!=null)&&(OustAppState.getInstance().getPlayResponse().getqIdList().size()>0)){
            n1=OustAppState.getInstance().getPlayResponse().getqIdList().size();
        }else if((OustAppState.getInstance().getPlayResponse()!=null)&&(OustAppState.getInstance().getPlayResponse().getQuestions()!=null)) {
            n1=OustAppState.getInstance().getPlayResponse().getQuestions().length;
        }
        return n1;
    }

}
