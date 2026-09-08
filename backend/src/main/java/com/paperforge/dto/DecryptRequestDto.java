package com.paperforge.dto;

public class DecryptRequestDto {
    private String password;

    public DecryptRequestDto() {
    }

    public DecryptRequestDto(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
