package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class UserBalanceState {

    private static UserBalanceState instance = null;
    private int userBalance;
    private String condition;
    private int minimumBalance;
    private List<String> mobileCarrier;
    private List<String> citis;
    private List<String> goals;


    public static UserBalanceState getInstance() {
        if(instance == null) {
            synchronized (UserBalanceState.class) {
                if(instance == null) {
                    instance = new UserBalanceState();
                }
            }
        }
        return instance;
    }

    private UserBalanceState(){
        userBalance=0;
        minimumBalance=25;
        condition="";
        mobileCarrier=new ArrayList<>();
        citis=new ArrayList<>();
    }

    public void clearAll(){
        instance = new UserBalanceState();
    }

    public int getUserBalance() {
        return userBalance;
    }

    public List<String> getCitis() {
        return citis;
    }

    public void setCitis(List<String> citis) {
        Collections.sort(citis);
        this.citis = citis;
    }

    public int getMinimumBalance() {
        return minimumBalance;
    }

    public void setUserBalance(int userBalance) {
        this.userBalance = userBalance;
    }

    public void setMinimumBalance(int minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<String> getMobileCarrier() {
        Collections.sort(mobileCarrier);
        return mobileCarrier;
    }

    public void setMobileCarrier(List<String> mobileCarrier) {
        this.mobileCarrier = mobileCarrier;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }
}
