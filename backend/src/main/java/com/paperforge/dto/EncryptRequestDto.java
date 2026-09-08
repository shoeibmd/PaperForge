package com.paperforge.dto;

public class EncryptRequestDto {
    private String userPassword;
    private String ownerPassword;
    private int keyLength = 128; // 128 or 256
    private boolean allowPrinting = true;
    private boolean allowModification = false;
    private boolean allowCopy = true;
    private boolean allowFormFilling = true;

    public EncryptRequestDto() {
    }

    public EncryptRequestDto(String userPassword, String ownerPassword, int keyLength, boolean allowPrinting, boolean allowModification, boolean allowCopy, boolean allowFormFilling) {
        this.userPassword = userPassword;
        this.ownerPassword = ownerPassword;
        this.keyLength = keyLength;
        this.allowPrinting = allowPrinting;
        this.allowModification = allowModification;
        this.allowCopy = allowCopy;
        this.allowFormFilling = allowFormFilling;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public boolean isAllowPrinting() {
        return allowPrinting;
    }

    public void setAllowPrinting(boolean allowPrinting) {
        this.allowPrinting = allowPrinting;
    }

    public boolean isAllowModification() {
        return allowModification;
    }

    public void setAllowModification(boolean allowModification) {
        this.allowModification = allowModification;
    }

    public boolean isAllowCopy() {
        return allowCopy;
    }

    public void setAllowCopy(boolean allowCopy) {
        this.allowCopy = allowCopy;
    }

    public boolean isAllowFormFilling() {
        return allowFormFilling;
    }

    public void setAllowFormFilling(boolean allowFormFilling) {
        this.allowFormFilling = allowFormFilling;
    }
}
