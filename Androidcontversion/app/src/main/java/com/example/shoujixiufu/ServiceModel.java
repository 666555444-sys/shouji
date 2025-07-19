package com.example.shoujixiufu;

public class ServiceModel {
    private int iconResId;
    private String title;

    public ServiceModel(int iconResId, String title) {
        this.iconResId = iconResId;
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }
} 