package com.example.fireapp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class UserClearedDustbin {

    String uid;
    private HashMap<String, Object> timestamp;
    int dustbinNo;

    public UserClearedDustbin(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getDustbinNo() {
        return dustbinNo;
    }

    public void setDustbinNo(int dustbinNo) {
        this.dustbinNo = dustbinNo;
    }
    public HashMap<String, Object> getTimestamp() {
        HashMap<String, Object> timeStamp = new HashMap<String, Object>();
        timeStamp.put("timestamp", ServerValue.TIMESTAMP);
        return timeStamp;
    }

    @Exclude
    public long getTimestampLong() {
        return (long)timestamp.get("timestamp");
    }
    public void setTimestamp(HashMap<String, Object> timestamp) {
        this.timestamp = timestamp;
    }
}
