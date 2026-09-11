package com.paperforge.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TesseractProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(TesseractProcessManager.class);
    private static final long PROCESS_TIMEOUT_SECONDS = 180;

    public boolean isTesseractAvailable() {
        try {
            List<String> command = List.of("tesseract", "--version");
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0) {
                return true;
            }
            if (!finished) {
                process.destroyForcibly();
            }
            return false;
        } catch (Exception e) {
            logger.debug("Tesseract check failed: {}", e.getMessage());
            return false;
        }
    }

    public void executeOcr(File inputImageFile, File outputFileBase, String languageString, boolean deskew) throws IOException {
        if (!isTesseractAvailable()) {
            throw new IllegalStateException("Tesseract OCR engine is not installed or available on PATH");
        }

        List<String> command = new ArrayList<>();
        command.add("tesseract");
        command.add(inputImageFile.getAbsolutePath());
        command.add(outputFileBase.getAbsolutePath());

        if (languageString != null && !languageString.isBlank()) {
            command.add("-l");
            command.add(languageString);
        }

        if (deskew) {
            command.add("--deskew");
        }

        command.add("pdf");

        ProcessBuilder pb = new ProcessBuilder(command);
        File tempUserDir = Files.createTempDirectory("paperforge_tesseract_user_").toFile();
        pb.environment().put("TMPDIR", tempUserDir.getAbsolutePath());

        try {
            Process process = pb.start();
            boolean finished = process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new IOException("Tesseract OCR process timed out after " + PROCESS_TIMEOUT_SECONDS + " seconds");
            }

            if (process.exitValue() != 0) {
                String errorMsg = new String(process.getErrorStream().readAllBytes());
                throw new IOException("Tesseract OCR process failed with exit code " + process.exitValue() + ": " + errorMsg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Tesseract OCR process was interrupted", e);
        } finally {
            if (tempUserDir.exists()) {
                File[] files = tempUserDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        f.delete();
                    }
                }
                tempUserDir.delete();
            }
        }
    }
}
