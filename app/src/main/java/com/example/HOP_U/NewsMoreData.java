package com.example.HOP_U;

import java.util.List;

public class NewsMoreData {
    private String title;
    private List<Integer> listResId;

    public NewsMoreData(String title, List<Integer> listResId) {
        this.title = title;
        this.listResId = listResId;
    }

    public List<Integer> getListResId() {
        return listResId;
    }

    public String getTitle() {
        return title;
    }
}
