package com.paperforge.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class QpdfProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(QpdfProcessManager.class);
    private static final long PROCESS_TIMEOUT_SECONDS = 120;

    public boolean isQpdfAvailable() {
        try {
            List<String> command = List.of("qpdf", "--version");
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
            logger.debug("qpdf check failed: {}", e.getMessage());
            return false;
        }
    }

    public void optimizePdf(File inputFile, File outputFile, String level, Boolean linearize, Boolean stripMetadata) throws IOException {
        if (!isQpdfAvailable()) {
            throw new IllegalStateException("QPDF utility is not installed or available on PATH");
        }

        List<String> command = new ArrayList<>();
        command.add("qpdf");

        String levelUpper = level != null ? level.toUpperCase() : "BALANCED";

        switch (levelUpper) {
            case "LOW" -> {
                command.add("--object-streams=generate");
                command.add("--compression-level=3");
            }
            case "HIGH" -> {
                command.add("--object-streams=generate");
                command.add("--compression-level=9");
                command.add("--recompress-flate");
            }
            case "CUSTOM" -> {
                command.add("--object-streams=generate");
                command.add("--compression-level=6");
            }
            default -> { // BALANCED
                command.add("--object-streams=generate");
                command.add("--compression-level=6");
            }
        }

        if (Boolean.TRUE.equals(linearize) || "BALANCED".equals(levelUpper) || "HIGH".equals(levelUpper)) {
            command.add("--linearize");
        }

        command.add(inputFile.getAbsolutePath());
        command.add(outputFile.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(command);

        try {
            Process process = pb.start();
            boolean finished = process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new IOException("QPDF process timed out after " + PROCESS_TIMEOUT_SECONDS + " seconds");
            }

            if (process.exitValue() != 0 && process.exitValue() != 3) { // QPDF exit code 3 = warnings only
                String errorMsg = new String(process.getErrorStream().readAllBytes());
                throw new IOException("QPDF process failed with exit code " + process.exitValue() + ": " + errorMsg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("QPDF process was interrupted", e);
        }
    }
}
