package com.paperforge.dto;

public class ImageToPdfRequestDto {
    private String pageSize = "A4"; // A4, LETTER, LEGAL, AUTO
    private String orientation = "PORTRAIT"; // PORTRAIT, LANDSCAPE
    private String fitOption = "FIT_PAGE"; // FIT_PAGE, ORIGINAL_SIZE, STRETCH
    private float margin = 10.0f; // Margin in points

    public ImageToPdfRequestDto() {
    }

    public ImageToPdfRequestDto(String pageSize, String orientation, String fitOption, float margin) {
        this.pageSize = pageSize;
        this.orientation = orientation;
        this.fitOption = fitOption;
        this.margin = margin;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getFitOption() {
        return fitOption;
    }

    public void setFitOption(String fitOption) {
        this.fitOption = fitOption;
    }

    public float getMargin() {
        return margin;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }
}
