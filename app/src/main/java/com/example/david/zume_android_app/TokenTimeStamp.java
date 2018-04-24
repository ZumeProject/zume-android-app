package com.example.david.zume_android_app;

public class TokenTimeStamp {

    public boolean getTimeDiff(long tokenTime){
        long diff = System.currentTimeMillis() - tokenTime;
        if(diff > 520000000){
            return true;
        }
        else{
            return false;
        }
    }
}

