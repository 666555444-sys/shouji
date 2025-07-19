package com.example.shoujixiufu;

public class FeatureModel {
    private int iconResId;
    private String title;
    private String description;
    private boolean isPremium;
    private String badgeText;

    public FeatureModel(int iconResId, String title, String description, boolean isPremium, String badgeText) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
        this.isPremium = isPremium;
        this.badgeText = badgeText;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public String getBadgeText() {
        return badgeText;
    }
} 