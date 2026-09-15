package com.paperforge.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipBombDetector {

    private final double maxRatio;
    private final long maxUncompressedSizeBytes;
    private final int maxEntriesCount;
    private final int maxDirectoryDepth;

    public ZipBombDetector(
            @Value("${PAPERFORGE_ZIP_MAX_RATIO:100.0}") double maxRatio,
            @Value("${PAPERFORGE_ZIP_MAX_SIZE_BYTES:262144000}") long maxUncompressedSizeBytes, // 250 MB
            @Value("${PAPERFORGE_ZIP_MAX_ENTRIES:10000}") int maxEntriesCount,
            @Value("${PAPERFORGE_ZIP_MAX_DEPTH:10}") int maxDirectoryDepth) {
        this.maxRatio = maxRatio;
        this.maxUncompressedSizeBytes = maxUncompressedSizeBytes;
        this.maxEntriesCount = maxEntriesCount;
        this.maxDirectoryDepth = maxDirectoryDepth;
    }

    public void inspectZipStream(InputStream inputStream, long compressedSizeBytes) throws SecurityException {
        int entryCount = 0;
        long totalUncompressedBytes = 0;

        try (ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(inputStream))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];

            while ((entry = zipIn.getNextEntry()) != null) {
                entryCount++;
                if (entryCount > maxEntriesCount) {
                    throw new SecurityException("Zip archive contains too many entries (max " + maxEntriesCount + ")");
                }

                String name = entry.getName();
                int depth = countDepth(name);
                if (depth > maxDirectoryDepth) {
                    throw new SecurityException("Zip archive path depth exceeds limit (" + depth + " > " + maxDirectoryDepth + "): " + name);
                }

                if (name.contains("..") || name.startsWith("/")) {
                    throw new SecurityException("Zip archive entry contains suspicious path traversal sequence: " + name);
                }

                if (!entry.isDirectory()) {
                    int bytesRead;
                    while ((bytesRead = zipIn.read(buffer)) != -1) {
                        totalUncompressedBytes += bytesRead;

                        if (totalUncompressedBytes > maxUncompressedSizeBytes) {
                            throw new SecurityException("Zip archive uncompressed size exceeds limit (max " + (maxUncompressedSizeBytes / 1024 / 1024) + " MB)");
                        }

                        if (compressedSizeBytes > 0) {
                            double currentRatio = (double) totalUncompressedBytes / (double) compressedSizeBytes;
                            if (currentRatio > maxRatio) {
                                throw new SecurityException("Zip archive compression expansion ratio (" + String.format("%.1f", currentRatio) + ":1) exceeds max allowable (" + maxRatio + ":1)");
                            }
                        }
                    }
                }
                zipIn.closeEntry();
            }
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityException("Failed to safely inspect ZIP archive: " + e.getMessage(), e);
        }
    }

    private int countDepth(String path) {
        if (path == null) return 0;
        String[] parts = path.split("[/\\\\]+");
        int count = 0;
        for (String p : parts) {
            if (!p.isEmpty() && !p.equals(".")) {
                count++;
            }
        }
        return count;
    }
}
