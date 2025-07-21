package com.example.shoujixiufu;

import java.io.Serializable;

public class Order implements Serializable {
    private String id;
    private String title;
    private String status;
    private String orderNumber;
    private String orderTime;
    private double amount;
    private String serviceType;
    private String deviceModel;

    public Order() {
    }

    public Order(String id, String title, String status, String orderNumber, String orderTime, double amount, String serviceType, String deviceModel) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.orderNumber = orderNumber;
        this.orderTime = orderTime;
        this.amount = amount;
        this.serviceType = serviceType;
        this.deviceModel = deviceModel;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
} 