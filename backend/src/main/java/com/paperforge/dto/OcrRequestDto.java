package com.paperforge.dto;

import java.util.List;

public class OcrRequestDto {

    private List<String> languages;
    private List<Integer> pageNumbers;
    private Boolean deskew;
    private Integer dpi;

    public OcrRequestDto() {}

    public OcrRequestDto(List<String> languages, List<Integer> pageNumbers, Boolean deskew, Integer dpi) {
        this.languages = languages;
        this.pageNumbers = pageNumbers;
        this.deskew = deskew;
        this.dpi = dpi;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<Integer> getPageNumbers() {
        return pageNumbers;
    }

    public void setPageNumbers(List<Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }

    public Boolean getDeskew() {
        return deskew;
    }

    public void setDeskew(Boolean deskew) {
        this.deskew = deskew;
    }

    public Integer getDpi() {
        return dpi;
    }

    public void setDpi(Integer dpi) {
        this.dpi = dpi;
    }
}
