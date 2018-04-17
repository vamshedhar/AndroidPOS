package com.example.vamshedhar.androidpos.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class CreatedTimeObject implements Serializable {
    HashMap<String, Object> createdTime;

    public CreatedTimeObject() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.createdTime = timestampNow;
    }

    public HashMap<String, Object> getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(HashMap<String, Object> createdTime) {
        this.createdTime = createdTime;
    }

    @Exclude
    public long getCreateTimestamp(){
        return (long) createdTime.get("timestamp");
    }
}
