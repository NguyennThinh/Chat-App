package com.example.may.Model;

public class Position {
    private String id;
    private String name;

    public Position(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Position() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
