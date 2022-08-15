package com.example.may.Model;

public class Log {
    private String idUser;
    private String log;
    private long time;

    public Log(String idUser, String log, long time) {
        this.idUser = idUser;
        this.log = log;
        this.time = time;
    }

    public Log() {
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
