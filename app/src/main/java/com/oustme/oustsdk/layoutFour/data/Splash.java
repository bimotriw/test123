package com.oustme.oustsdk.layoutFour.data;

public class Splash
{
    private IOS iOS;

    public void setIOS(IOS iOS){
        this.iOS = iOS;
    }
    public IOS getIOS(){
        return this.iOS;
    }
}

class IOS
{
    private int updateChecksum;

    public void setUpdateChecksum(int updateChecksum){
        this.updateChecksum = updateChecksum;
    }
    public int getUpdateChecksum(){
        return this.updateChecksum;
    }
}