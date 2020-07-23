package com.myorg.dto;

import java.util.List;

public class TotemDto {
    private String id;
    private String imei;
    private String firmware;
    private String customerId;
    private Location location;
    private List<ScheduledAds> adSchedule;
    private String model;
    private Hardware hardware;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public List<ScheduledAds> getAdSchedule() {
        return adSchedule;
    }

    public void setAdSchedule(List<ScheduledAds> adSchedule) {
        this.adSchedule = adSchedule;
    }

    @Override
    public String toString() {
        return "TotemDto{" +
                "id='" + id + '\'' +
                ", imei='" + imei + '\'' +
                ", firmware='" + firmware + '\'' +
                ", customerId='" + customerId + '\'' +
                ", location=" + location +
                ", scheduledAdsList=" + adSchedule +
                ", model='" + model + '\'' +
                ", hardware=" + hardware +
                '}';
    }
}
