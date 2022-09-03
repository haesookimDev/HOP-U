package com.example.HOP_U;

public class DialogData {
    private String name;
    private String number;
    private String memo;

    public DialogData(String number, String name, String memo) {
        this.number = number;
        this.name = name;
        this.memo = memo;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getMemo() {
        return memo;
    }

}
