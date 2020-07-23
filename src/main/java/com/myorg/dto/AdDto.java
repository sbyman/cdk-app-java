package com.myorg.dto;

import java.util.Arrays;

public class AdDto {
    private String id;
    private String payload;
    private String encoding;
    private String compression;
    private Long duration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AdDto{" +
                "id='" + id + '\'' +
                ", payload=" + payload +
                ", encoding='" + encoding + '\'' +
                ", compression='" + compression + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

}
