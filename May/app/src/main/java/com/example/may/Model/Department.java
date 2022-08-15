package com.example.may.Model;

public class Department {
    private String id;
    private String imgDepartment;
    private String name;
    private String idManager;
    private String mission;
    private int slParticipant;
    private long createDate;

    public Department(String id, String imgDepartment, String name, String idManager, String mission, int slParticipant, long createDate) {
        this.id = id;
        this.imgDepartment = imgDepartment;
        this.name = name;
        this.idManager = idManager;
        this.mission = mission;
        this.slParticipant = slParticipant;
        this.createDate = createDate;
    }

    public Department() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgDepartment() {
        return imgDepartment;
    }

    public void setImgDepartment(String imgDepartment) {
        this.imgDepartment = imgDepartment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdManager() {
        return idManager;
    }

    public void setIdManager(String idManager) {
        this.idManager = idManager;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public int getSlParticipant() {
        return slParticipant;
    }

    public void setSlParticipant(int slParticipant) {
        this.slParticipant = slParticipant;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
