package com.paperforge.dto;

import java.util.List;

public class RotateRequestDto {
    private int angle; // 90, 180, 270
    private List<Integer> pageNumbers; // 1-indexed page numbers. If null or empty, rotate all pages.

    public RotateRequestDto() {
    }

    public RotateRequestDto(int angle, List<Integer> pageNumbers) {
        this.angle = angle;
        this.pageNumbers = pageNumbers;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public List<Integer> getPageNumbers() {
        return pageNumbers;
    }

    public void setPageNumbers(List<Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }
}
