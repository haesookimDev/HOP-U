package com.example.HOP_U;

public class CheeringData {
    private String name;
    private String cheer;
    private String uId;

    public CheeringData(String name, String cheer, String uId){
        this.name = name;
        this.cheer = cheer;
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public String getCheer() {
        return cheer;
    }

    public String getuId() {
        return uId;
    }
}
