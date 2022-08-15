package com.example.may.Model;

import java.util.List;

public class UserChatParent {
    private String dateSend;
    private List<UserChat> arrUserChat;

    public UserChatParent(String dateSend, List<UserChat> arrUserChat) {
        this.dateSend = dateSend;
        this.arrUserChat = arrUserChat;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public List<UserChat> getArrUserChat() {
        return arrUserChat;
    }

    public void setArrUserChat(List<UserChat> arrUserChat) {
        this.arrUserChat = arrUserChat;
    }
}
