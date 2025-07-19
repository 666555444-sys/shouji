package com.example.shoujixiufu.model;

/**
 * 案例数据模型
 */
public class CaseModel {
    private String id;
    private String title;
    private String date;
    private String description;
    private String imageUrl;

    public CaseModel(String id, String title, String date, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 