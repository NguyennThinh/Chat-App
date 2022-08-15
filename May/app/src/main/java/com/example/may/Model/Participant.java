package com.example.may.Model;

import java.io.Serializable;

public class Participant implements Serializable {
    private String id;
    private int role;
    private long partDate;

    public Participant(String id, int role, long partDate) {
        this.id = id;
        this.role = role;
        this.partDate = partDate;
    }

    public Participant() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public long getPartDate() {
        return partDate;
    }

    public void setPartDate(long partDate) {
        this.partDate = partDate;
    }
}
