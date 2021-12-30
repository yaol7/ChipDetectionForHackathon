package com.dellemc.flink.hackthaon.pro;

import io.pravega.shaded.com.google.gson.JsonArray;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
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
}
