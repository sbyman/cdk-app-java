package com.myorg.dto;

import java.util.Map;

public class Location {
    private String address;
    private String floor;
    private Map<String, String> optionalInfo;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Map<String, String> getOptionalInfo() {
        return optionalInfo;
    }

    public void setOptionalInfo(Map<String, String> optionalInfo) {
        this.optionalInfo = optionalInfo;
    }

    @Override
    public String toString() {
        return "Location{" +
                "address='" + address + '\'' +
                ", floor='" + floor + '\'' +
                ", optionalInfo=" + optionalInfo +
                '}';
    }
}
