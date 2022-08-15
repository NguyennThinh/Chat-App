package com.example.may.Model;

import java.io.Serializable;

public class Group implements Serializable {
    private String groupID;
    private String groupName;
    private String description;
    private String groupImage;
    private String userCreate;
    private int slParticipant;
    private long createDate;
    private int type;


    public Group() {
    }

    public Group(String groupID,
                 String groupName,
                 String description,
                 String groupImage,
                 String userCreate,
                 int slParticipant,
                 long createDate,
                 int type) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.description = description;
        this.groupImage = groupImage;
        this.userCreate = userCreate;
        this.slParticipant = slParticipant;
        this.createDate = createDate;
        this.type = type;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(String userCreate) {
        this.userCreate = userCreate;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
