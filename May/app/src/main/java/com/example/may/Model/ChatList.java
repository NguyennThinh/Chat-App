package com.example.may.Model;

public class ChatList {
    private String id;
    private int type;
    private long timeSend;

    public ChatList(String id, int type, long timeSend) {
        this.id = id;
        this.type = type;
        this.timeSend = timeSend;
    }

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }
}
