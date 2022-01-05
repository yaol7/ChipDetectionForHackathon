package com.dellemc.flink.hackthaon.pro;

import io.pravega.shaded.com.google.gson.JsonArray;

import java.util.Date;
import java.util.List;

public class ChipMetadata {
    private int encoding_level;
    private String version;
    private String img_handle;
    private String stream;
    private int width;
    private int height;
    private String encoding_type;
    private JsonArray defects;
    private int defectsLen;
    private int frame_number;
    private Date timestamp;
    private String factory;
    private String production_line;
    private List<Float> location;

    public ChipMetadata() {
    }

    public ChipMetadata(int encoding_level, String version, String img_handle, String stream, int width,
                        int height, String encoding_type, JsonArray defects, int defectsLen, int frame_number,
                        Date timestamp, String factory, String production_line, List<Float> location) {
        this.encoding_level = encoding_level;
        this.version = version;
        this.img_handle = img_handle;
        this.stream = stream;
        this.width = width;
        this.height = height;
        this.encoding_type = encoding_type;
        this.defects = defects;
        this.defectsLen = defectsLen;
        this.frame_number = frame_number;
        this.timestamp = timestamp;
        this.factory = factory;
        this.production_line = production_line;
        this.location = location;
    }

    public int getEncoding_level() {
        return encoding_level;
    }

    public void setEncoding_level(int encoding_level) {
        this.encoding_level = encoding_level;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getImg_handle() {
        return img_handle;
    }

    public void setImg_handle(String img_handle) {
        this.img_handle = img_handle;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String topic) {
        this.stream = stream;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getEncoding_type() {
        return encoding_type;
    }

    public void setEncoding_type(String encoding_type) {
        this.encoding_type = encoding_type;
    }

    public JsonArray getDefects() {
        return defects;
    }

    public void setDefects(JsonArray defects) {
        this.defects = defects;
    }

    public int getDefectsLen() {
        return defectsLen;
    }

    public void setDefectsLen(int defectsLen) {
        this.defectsLen = defectsLen;
    }

    public int getFrame_number() {
        return frame_number;
    }

    public void setFrame_number(int frame_number) {
        this.frame_number = frame_number;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getProduction_line() {
        return production_line;
    }

    public void setProduction_line(String production_line) {
        this.production_line = production_line;
    }

    public List<Float> getLocation() {
        return location;
    }

    public void setLocation(List<Float> location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ChipMetadata{" +
                "encoding_level=" + encoding_level +
                ", version='" + version + '\'' +
                ", img_handle='" + img_handle + '\'' +
                ", stream='" + stream + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", encoding_type='" + encoding_type + '\'' +
                ", defects=" + defects +
                ", defectsLen=" + defectsLen +
                ", frame_number=" + frame_number +
                ", timestamp=" + timestamp +
                ", factory='" + factory + '\'' +
                ", production_line='" + production_line + '\'' +
                ", location=" + location +
                '}';
    }
}
