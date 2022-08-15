package com.example.may.Model;

public class Attendance {
    private String id_user;
    private int status;
    private long time;

    public Attendance(String id_user, int status, long time) {
        this.id_user = id_user;
        this.status = status;
        this.time = time;
    }

    public Attendance() {
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
