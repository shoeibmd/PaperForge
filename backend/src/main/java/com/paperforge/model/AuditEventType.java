package com.paperforge.model;

public enum AuditEventType {
    // Auth
    LOGIN,
    LOGOUT,
    LOGIN_FAILED,
    PASSWORD_CHANGED,

    // User Management
    USER_CREATED,
    USER_DELETED,
    USER_DISABLED,
    ROLE_CHANGED,

    // API Keys
    API_KEY_CREATED,
    API_KEY_REVOKED,
    API_KEY_USED,
    API_KEY_EXPIRED,
    API_KEY_AUTH_SUCCESS,
    API_KEY_AUTH_FAILED,

    // File Operations
    FILE_UPLOADED,
    FILE_PROCESSED,
    FILE_DOWNLOADED,
    FILE_DELETED,

    // Core PDF Operations
    MERGE_PDF_REQUEST,
    SPLIT_PDF_REQUEST,
    ROTATE_PDF_REQUEST,
    DELETE_PAGES_REQUEST,
    EXTRACT_PAGES_REQUEST,
    REORDER_PAGES_REQUEST,
    CROP_PDF_REQUEST,
    METADATA_UPDATE_REQUEST,
    ENCRYPT_PDF_REQUEST,
    DECRYPT_PDF_REQUEST,
    DECRYPT_FAILED_INVALID_PASSWORD,
    CONVERT_TO_PDF_REQUEST,
    CONVERT_FROM_PDF_REQUEST,
    PDF_TO_IMAGE_REQUEST,
    IMAGE_TO_PDF_REQUEST,
    OCR_REQUEST,
    COMPRESS_PDF_REQUEST,

    // Job Operations
    JOB_SUBMITTED,
    JOB_COMPLETED,
    JOB_FAILED,
    JOB_CANCELLED,

    // Admin
    ADMIN_ACTION,

    // Security & Infrastructure
    RATE_LIMIT_EXCEEDED,
    INVALID_SIGNATURE,
    PATH_TRAVERSAL_ATTEMPT,
    SYSTEM_EVENT;

    public static AuditEventType fromStringOrDefault(String value) {
        if (value == null) return SYSTEM_EVENT;
        try {
            return AuditEventType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SYSTEM_EVENT;
        }
    }
}
