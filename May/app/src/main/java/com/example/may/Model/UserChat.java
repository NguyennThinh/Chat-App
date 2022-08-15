package com.example.may.Model;

public class UserChat {
    private String idMessage;
    private String idSender;
    private String idReceiver;
    private String message;
    private String dateSend;
    private long timeSend;
    private String type;
    private String fileName;
    private String fileSize;
    private boolean seen;

    public UserChat(String idMessage,
                    String idSender,
                    String idReceiver,
                    String message,
                    String dateSend,
                    long timeSend,
                    String type,
                    String fileName,
                    String fileSize,
                    boolean seen) {
        this.idMessage = idMessage;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.message = message;
        this.dateSend = dateSend;
        this.timeSend = timeSend;
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.seen = seen;
    }

    public UserChat() {
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
