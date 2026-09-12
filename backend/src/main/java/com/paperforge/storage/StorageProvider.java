package com.paperforge.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface StorageProvider {

    void saveFile(StorageCategory category, String relativePath, InputStream inputStream) throws IOException;

    InputStream getFile(StorageCategory category, String relativePath) throws IOException;

    boolean deleteFile(StorageCategory category, String relativePath) throws IOException;

    boolean exists(StorageCategory category, String relativePath) throws IOException;

    List<String> listFiles(StorageCategory category, String relativeDirectoryPath) throws IOException;

    long getStorageUsageBytes(StorageCategory category, String relativeDirectoryPath) throws IOException;
}
