package com.example.may.Model;

public class CompanyChat {
    private String idSender;
    private String message;
    private long timeSend;
    private String type;
    private String fileName;
    private String fileSize;

    public CompanyChat(String idSender, String message, long timeSend, String type, String fileName, String fileSize) {
        this.idSender = idSender;
        this.message = message;
        this.timeSend = timeSend;
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public CompanyChat() {
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
