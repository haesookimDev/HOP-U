package com.example.HOP_U;

public class RecentChat {
    private String id;
    private String recentChat;

    public RecentChat(){

    }

    public RecentChat(String id, String recentChat){
        this.id = id;
        this.recentChat = recentChat;
    }

    public String getRecentChat() {
        return recentChat;
    }

    public void setRecentChat(String recentChat) {
        this.recentChat = recentChat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
