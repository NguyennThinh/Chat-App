package com.example.may.Model;

import java.util.List;

public class GroupChatParent {
    private String dateSend;
    private List<GroupChat> arrGroupChat;

    public GroupChatParent(String dateSend, List<GroupChat> arrGroupChat) {
        this.dateSend = dateSend;
        this.arrGroupChat = arrGroupChat;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public List<GroupChat> getArrGroupChat() {
        return arrGroupChat;
    }

    public void setArrGroupChat(List<GroupChat> arrGroupChat) {
        this.arrGroupChat = arrGroupChat;
    }
}
