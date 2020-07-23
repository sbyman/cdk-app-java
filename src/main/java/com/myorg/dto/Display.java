package com.myorg.dto;

public class Display {
    private int height;
    private int width;
    private int resolution;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    @Override
    public String toString() {
        return "Display{" +
                "height=" + height +
                ", length=" + width +
                ", resolution=" + resolution +
                '}';
    }
}
