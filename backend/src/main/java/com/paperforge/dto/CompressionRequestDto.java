package com.paperforge.dto;

public class CompressionRequestDto {

    private String level; // LOW, BALANCED, HIGH, CUSTOM
    private Integer targetDpi; // 72, 150, 200, 300
    private Float imageQuality; // 0.1 to 1.0
    private Boolean stripMetadata;
    private Boolean linearize;

    public CompressionRequestDto() {}

    public CompressionRequestDto(String level, Integer targetDpi, Float imageQuality, Boolean stripMetadata, Boolean linearize) {
        this.level = level;
        this.targetDpi = targetDpi;
        this.imageQuality = imageQuality;
        this.stripMetadata = stripMetadata;
        this.linearize = linearize;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getTargetDpi() {
        return targetDpi;
    }

    public void setTargetDpi(Integer targetDpi) {
        this.targetDpi = targetDpi;
    }

    public Float getImageQuality() {
        return imageQuality;
    }

    public void setImageQuality(Float imageQuality) {
        this.imageQuality = imageQuality;
    }

    public Boolean getStripMetadata() {
        return stripMetadata;
    }

    public void setStripMetadata(Boolean stripMetadata) {
        this.stripMetadata = stripMetadata;
    }

    public Boolean getLinearize() {
        return linearize;
    }

    public void setLinearize(Boolean linearize) {
        this.linearize = linearize;
    }
}
