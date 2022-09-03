package com.example.HOP_U;

import com.google.firebase.Timestamp;

public class trialCommentData {
    private String uid;
    private String name;
    private String comment;
    private String prosAndCons;
    private Timestamp timestamp;

    public trialCommentData(String uid, String name, String comment, String prosAndCons, Timestamp timestamp){
        this.uid = uid;
        this.name = name;
        this.comment = comment;
        this.prosAndCons = prosAndCons;
        this.timestamp = timestamp;

    }

    public String getUid() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public String getComment() {
        return this.comment;
    }

    public String getProsAndCons() {
        return this.prosAndCons;
    }

    public Timestamp getTimestamp() {return this.timestamp;}
}
