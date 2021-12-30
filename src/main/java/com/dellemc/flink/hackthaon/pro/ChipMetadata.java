package com.dellemc.flink.hackthaon.pro;

import io.pravega.shaded.com.google.gson.JsonArray;
import java.util.Date;

public class ChipMetadata {
    private int height;
    private String host;
    private String topic;
    private int width;
    private String img_handle;
    private int channels;
    private int defectsLen;
    private int frame_number;
    private Date timestamp;
    private String encoding_type;
    private int encoding_level;
    private String version;
    private JsonArray defects;

    public ChipMetadata() {
    }

    public ChipMetadata(int height, String host, String topic, int width, String img_handle, int channels, int defectsLen, int frame_number,
                        Date timestamp, String encoding_type, int encoding_level, String version, JsonArray defects) {
        this.height = height;
        this.host = host;
        this.topic = topic;
        this.width = width;
        this.img_handle = img_handle;
        this.channels = channels;
        this.defectsLen = defectsLen;
        this.frame_number = frame_number;
        this.timestamp = timestamp;
        this.encoding_type = encoding_type;
        this.encoding_level = encoding_level;
        this.version = version;
        this.defects = defects;
    }

    public int getHeight() {
        return height;
    }

    public String getHost() {
        return host;
    }

    public String getTopic() {
        return topic;
    }

    public int getWidth() {
        return width;
    }

    public String getImg_handle() {
        return img_handle;
    }

    public int getChannels() {
        return channels;
    }

    public int getDefectsLen() {
        return defectsLen;
    }

    public int getFrame_number() {
        return frame_number;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getEncoding_type() {
        return encoding_type;
    }

    public int getEncoding_level() {
        return encoding_level;
    }

    public String getVersion() {
        return version;
    }

    public JsonArray getDefects() {
        return defects;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setImg_handle(String img_handle) {
        this.img_handle = img_handle;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setDefectsLen(int defectsLen) {
        this.defectsLen = defectsLen;
    }

    public void setFrame_number(int frame_number) {
        this.frame_number = frame_number;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setEncoding_type(String encoding_type) {
        this.encoding_type = encoding_type;
    }

    public void setEncoding_level(int encoding_level) {
        this.encoding_level = encoding_level;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDefects(JsonArray defects) {
        this.defects = defects;
    }

    @Override
    public String toString() {
        return "ChipMetadata{" +
                "height=" + height +
                ", host='" + host + '\'' +
                ", topic='" + topic + '\'' +
                ", width=" + width +
                ", img_handle='" + img_handle + '\'' +
                ", channels=" + channels +
                ", defectsLen=" + defectsLen +
                ", frame_number=" + frame_number +
                ", timestamp=" + timestamp +
                ", encoding_type='" + encoding_type + '\'' +
                ", encoding_level=" + encoding_level +
                ", version='" + version + '\'' +
                ", defects=" + defects +
                '}';
    }
}
