package com.paperforge.dto;

import java.util.List;

public class PdfToImageRequestDto {
    private String format = "PNG"; // PNG, JPEG, WEBP
    private int dpi = 150; // 72 - 300 DPI
    private float quality = 0.9f; // 0.1 - 1.0 (for JPEG/WEBP)
    private List<Integer> pageNumbers; // 1-indexed. If empty/null, render all pages.

    public PdfToImageRequestDto() {
    }

    public PdfToImageRequestDto(String format, int dpi, float quality, List<Integer> pageNumbers) {
        this.format = format;
        this.dpi = dpi;
        this.quality = quality;
        this.pageNumbers = pageNumbers;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public List<Integer> getPageNumbers() {
        return pageNumbers;
    }

    public void setPageNumbers(List<Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }
}
