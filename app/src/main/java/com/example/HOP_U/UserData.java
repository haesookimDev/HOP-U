package com.example.HOP_U;

public class UserData {
    public String userName; // 사용자 이름(닉네임)
    public String uid; // 현재 사용자(로그인한)
    private String mbti;
    private int point;
    private String job;
    private String desiredJob;
    private String email;
    private int banCount;

    public UserData(){
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public void setMbti(String mbti) {
        this.mbti = mbti;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setDesiredJob(String desiredJob) {
        this.desiredJob = desiredJob;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getUid() {
        return this.uid;
    }

    public int getPoint() {
        return point;
    }

    public String getMbti() {
        return mbti;
    }

    public String getJob() {
        return job;
    }

    public String getDesiredJob() {
        return desiredJob;
    }

    public String getEmail() {
        return email;
    }

    public int getBanCount() {
        return banCount;
    }

    public void setBanCount(int banCount) {
        this.banCount = banCount;
    }
}
