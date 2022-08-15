package com.example.may.Model;

/**
 * Created by Nguyễn Phúc Thịnh on 5/11/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class ItemChat {
    private int id;
    private int imgItem;
    private String itemName;

    public ItemChat(int id, int imgItem, String itemName) {
        this.id = id;
        this.imgItem = imgItem;
        this.itemName = itemName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImgItem() {
        return imgItem;
    }

    public void setImgItem(int imgItem) {
        this.imgItem = imgItem;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
