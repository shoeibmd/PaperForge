package com.paperforge.storage;

public enum StorageCategory {
    USERS("users"),
    JOBS("jobs"),
    TEMPORARY("temporary"),
    EXPORTS("exports"),
    BACKUPS("backups");

    private final String folderName;

    StorageCategory(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
