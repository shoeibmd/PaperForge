package com.paperforge.security;

import java.util.UUID;

public class FilenameSanitizer {

    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "paperforge_document_" + UUID.randomUUID() + ".pdf";
        }

        // Remove NUL bytes, carriage returns, newlines, and tabs
        String sanitized = filename.replaceAll("[\\r\\n\\t\\u0000]", "");

        // Normalize backslashes to forward slashes for cross-platform path handling
        sanitized = sanitized.replace('\\', '/');

        // Extract base filename after last slash
        int lastSlash = sanitized.lastIndexOf('/');
        if (lastSlash >= 0) {
            sanitized = sanitized.substring(lastSlash + 1);
        }

        // Strip dangerous characters except alphanumeric, dot, underscore, dash
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Prevent leading dots (hidden files / relative dots)
        while (sanitized.startsWith(".")) {
            sanitized = sanitized.substring(1);
        }

        if (sanitized.isEmpty()) {
            return "paperforge_document_" + UUID.randomUUID() + ".pdf";
        }

        return sanitized;
    }
}
