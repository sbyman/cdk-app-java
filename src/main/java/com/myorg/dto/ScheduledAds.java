package com.myorg.dto;

public class ScheduledAds {
    private String adId;
    private Long streamStartTime;
    private Long streamEndTime;

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public Long getStreamStartTime() {
        return streamStartTime;
    }

    public void setStreamStartTime(Long streamStartTime) {
        this.streamStartTime = streamStartTime;
    }

    public Long getStreamEndTime() {
        return streamEndTime;
    }

    public void setStreamEndTime(Long streamEndTime) {
        this.streamEndTime = streamEndTime;
    }

    @Override
    public String toString() {
        return "ScheduledAds{" +
                "adId='" + adId + '\'' +
                ", streamStartTime=" + streamStartTime +
                ", streamEndTime=" + streamEndTime +
                '}';
    }
}
