package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.HashMap;


/**
 * Created by oust on 11/27/18.
 */

@Keep
public class ShiftDataModel {
    private String shift,id,additionalInfo1,additionalInfo2,dailyIncentive,minGurantee,
    monthlyIncentive,perOrderEarning,weekendIncentive,tenureIncentive,weeklyIncentive;

    public void setNewpayoutData(HashMap<String,Object> shiftDataMap){
        try {
            if (shiftDataMap.get("firstMileDistancePay") != null) {
                this.firstMileDistancePay = (String) shiftDataMap.get("firstMileDistancePay");
            }
            if (shiftDataMap.get("firstMileEffortPay") != null) {
                this.firstMileEffortPay = (String) shiftDataMap.get("firstMileEffortPay");
            }
            if (shiftDataMap.get("lastMileDistancePay") != null) {
                this.lastMileDistancePay = (String) shiftDataMap.get("lastMileDistancePay");
            }
            if (shiftDataMap.get("lastMileEffortPay") != null) {
                this.lastMileEffortPay = (String) shiftDataMap.get("lastMileEffortPay");
            }
            if (shiftDataMap.get("orderCompletionBonus") != null) {
                this.orderCompletionBonus = (String) shiftDataMap.get("orderCompletionBonus");
            }
            if (shiftDataMap.get("touchPointPay") != null) {
                this.touchPointPay = (String) shiftDataMap.get("touchPointPay");
            }
            if (shiftDataMap.get("waitingTimePay") != null) {
                this.waitingTimePay = (String) shiftDataMap.get("waitingTimePay");
            }
            if (shiftDataMap.get("note") != null) {
                this.note = (String) shiftDataMap.get("note");
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private String firstMileDistancePay;
    private String firstMileEffortPay;
    private String lastMileDistancePay;
    private String lastMileEffortPay;
    private String orderCompletionBonus;
    private String touchPointPay;
    private String waitingTimePay;
    private String note;

    public String getFirstMileDistancePay() {
        return firstMileDistancePay;
    }

    public void setFirstMileDistancePay(String firstMileDistancePay) {
        this.firstMileDistancePay = firstMileDistancePay;
    }

    public String getFirstMileEffortPay() {
        return firstMileEffortPay;
    }

    public void setFirstMileEffortPay(String firstMileEffortPay) {
        this.firstMileEffortPay = firstMileEffortPay;
    }

    public String getLastMileDistancePay() {
        return lastMileDistancePay;
    }

    public void setLastMileDistancePay(String lastMileDistancePay) {
        this.lastMileDistancePay = lastMileDistancePay;
    }

    public String getLastMileEffortPay() {
        return lastMileEffortPay;
    }

    public void setLastMileEffortPay(String lastMileEffortPay) {
        this.lastMileEffortPay = lastMileEffortPay;
    }

    public String getOrderCompletionBonus() {
        return orderCompletionBonus;
    }

    public void setOrderCompletionBonus(String orderCompletionBonus) {
        this.orderCompletionBonus = orderCompletionBonus;
    }

    public String getTouchPointPay() {
        return touchPointPay;
    }

    public void setTouchPointPay(String touchPointPay) {
        this.touchPointPay = touchPointPay;
    }

    public String getWaitingTimePay() {
        return waitingTimePay;
    }

    public void setWaitingTimePay(String waitingTimePay) {
        this.waitingTimePay = waitingTimePay;
    }


    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdditionalInfo1() {
        return additionalInfo1;
    }

    public void setAdditionalInfo1(String additionalInfo1) {
        this.additionalInfo1 = additionalInfo1;
    }

    public String getAdditionalInfo2() {
        return additionalInfo2;
    }

    public void setAdditionalInfo2(String additionalInfo2) {
        this.additionalInfo2 = additionalInfo2;
    }

    public String getDailyIncentive() {
        return dailyIncentive;
    }

    public void setDailyIncentive(String dailyIncentive) {
        this.dailyIncentive = dailyIncentive;
    }

    public String getMinGurantee() {
        return minGurantee;
    }

    public void setMinGurantee(String minGurantee) {
        this.minGurantee = minGurantee;
    }

    public String getMonthlyIncentive() {
        return monthlyIncentive;
    }

    public void setMonthlyIncentive(String monthlyIncentive) {
        this.monthlyIncentive = monthlyIncentive;
    }

    public String getPerOrderEarning() {
        return perOrderEarning;
    }

    public void setPerOrderEarning(String perOrderEarning) {
        this.perOrderEarning = perOrderEarning;
    }

    public String getWeekendIncentive() {
        return weekendIncentive;
    }

    public void setWeekendIncentive(String weekendIncentive) {
        this.weekendIncentive = weekendIncentive;
    }

    public String getTenureIncentive() {
        return tenureIncentive;
    }

    public void setTenureIncentive(String tenureIncentive) {
        this.tenureIncentive = tenureIncentive;
    }

    public String getWeeklyIncentive() {
        return weeklyIncentive;
    }

    public void setWeeklyIncentive(String weeklyIncentive) {
        this.weeklyIncentive = weeklyIncentive;
    }

    public void setData(HashMap<String,Object> shiftDataMap){
        try {
            if (shiftDataMap.get("shiftTime") != null) {
                this.shift = (String) shiftDataMap.get("shiftTime");
            }
            if (shiftDataMap.get("additionalInfo1") != null) {
                this.additionalInfo1 = (String) shiftDataMap.get("additionalInfo1");
            }
            if (shiftDataMap.get("additionalInfo2") != null) {
                this.additionalInfo2 = (String) shiftDataMap.get("additionalInfo2");
            }
            if (shiftDataMap.get("dailyIncentive") != null) {
                this.dailyIncentive = (String) shiftDataMap.get("dailyIncentive");
            }
            if (shiftDataMap.get("minGuarantee") != null) {
                this.minGurantee = (String) shiftDataMap.get("minGuarantee");
            }
            if (shiftDataMap.get("monthlyIncentive") != null) {
                this.monthlyIncentive = (String) shiftDataMap.get("monthlyIncentive");
            }
            if (shiftDataMap.get("perOrderEarning") != null) {
                this.perOrderEarning = (String) shiftDataMap.get("perOrderEarning");
            }
            if (shiftDataMap.get("weekendIncentive") != null) {
                this.weekendIncentive = (String) shiftDataMap.get("weekendIncentive");
            }
            if (shiftDataMap.get("tenureIncentive") != null) {
                this.tenureIncentive = (String) shiftDataMap.get("tenureIncentive");
            }
            if (shiftDataMap.get("weeklyIncentive") != null) {
                this.weeklyIncentive = (String) shiftDataMap.get("weeklyIncentive");
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
