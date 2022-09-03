package com.example.HOP_U;

public class ChatList {

    private String id;
    private String timestamp;
    private String recentChat;
    private Boolean ban;

    public ChatList() {

    }

    public ChatList(String id, String timestamp, String recentChat, Boolean ban) {
        this.id = id;
        this.timestamp = timestamp;
        this.recentChat = recentChat;
        this.ban = ban;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getBan() {
        return ban;
    }

    public void setBan(Boolean ban) {
        this.ban = ban;
    }
}
