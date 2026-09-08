package com.paperforge.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class LibreOfficeProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(LibreOfficeProcessManager.class);
    private final String sofficeExecutable;

    public LibreOfficeProcessManager() {
        this.sofficeExecutable = findSofficeExecutable();
    }

    public boolean isLibreOfficeAvailable() {
        return sofficeExecutable != null;
    }

    public File convertDocument(File inputFile, String targetFormat, File outputDir, Path tempDir) throws IOException, InterruptedException {
        if (!isLibreOfficeAvailable()) {
            throw new IllegalStateException("LibreOffice binary (soffice) is not available on system PATH");
        }

        Path userProfileDir = tempDir.resolve("profile_" + UUID.randomUUID());
        Files.createDirectories(userProfileDir);

        try {
            List<String> command = new ArrayList<>();
            command.add(sofficeExecutable);
            command.add("--headless");
            command.add("--nodisplay");
            command.add("--nosplash");
            command.add("--norestore");
            command.add("-env:UserInstallation=file://" + userProfileDir.toAbsolutePath());
            command.add("--convert-to");
            command.add(targetFormat);
            command.add(inputFile.getAbsolutePath());
            command.add("--outdir");
            command.add(outputDir.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            logger.info("Executing LibreOffice command: {}", command);
            Process process = pb.start();

            boolean finished = process.waitFor(180, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IOException("LibreOffice document conversion timed out after 180 seconds");
            }

            if (process.exitValue() != 0) {
                throw new IOException("LibreOffice conversion failed with exit code: " + process.exitValue());
            }

            // Find converted file in outputDir
            String inputBaseName = getFileNameWithoutExtension(inputFile.getName());
            String expectedOutputName = inputBaseName + "." + targetFormat.toLowerCase();
            File convertedFile = new File(outputDir, expectedOutputName);

            if (!convertedFile.exists()) {
                // Try matching any file created in outputDir
                File[] files = outputDir.listFiles((dir, name) -> name.toLowerCase().endsWith("." + targetFormat.toLowerCase()));
                if (files != null && files.length > 0) {
                    convertedFile = files[0];
                } else {
                    throw new IOException("Converted output file not found in output directory");
                }
            }

            return convertedFile;
        } finally {
            deleteDirectoryRecursively(userProfileDir.toFile());
        }
    }

    private String findSofficeExecutable() {
        String[] candidates = {"soffice", "libreoffice"};
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            String[] pathDirs = pathEnv.split(File.pathSeparator);
            for (String dir : pathDirs) {
                for (String candidate : candidates) {
                    File file = new File(dir, candidate);
                    if (file.exists() && file.canExecute()) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }

    private String getFileNameWithoutExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(0, lastDot);
        }
        return fileName;
    }

    private void deleteDirectoryRecursively(File file) {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectoryRecursively(child);
                }
            }
        }
        file.delete();
    }
}
