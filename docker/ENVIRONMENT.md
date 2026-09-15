# PaperForge Runtime Environment Variables

This document provides a comprehensive reference of all runtime environment variables supported by PaperForge containers and deployments.

## Core Application Configuration

| Environment Variable | Description | Default Value | Required |
| :--- | :--- | :--- | :--- |
| `SERVER_PORT` | HTTP server port for backend Spring Boot application | `8080` | No |
| `PAPERFORGE_DEFAULT_USER_QUOTA_BYTES` | Default storage quota allocated per user account (in bytes) | `524288000` (500 MB) | No |
| `PAPERFORGE_MAX_CONCURRENT_JOBS` | Maximum allowed concurrent background processing jobs globally | `10` | No |
| `PAPERFORGE_JOB_TIMEOUT_SECONDS` | Timeout bound for long-running document jobs (in seconds) | `120` | No |

## Security & Defense Limits

| Environment Variable | Description | Default Value | Required |
| :--- | :--- | :--- | :--- |
| `PAPERFORGE_AUDIT_LOG_RETENTION_DAYS` | Retention window for security audit logs before automated cleanup | `90` | No |
| `PAPERFORGE_AUDIT_CLEANUP_CRON` | Cron expression for automated audit log cleanup scheduler | `0 0 3 * * ?` (3:00 AM) | No |
| `PAPERFORGE_ZIP_MAX_RATIO` | Maximum allowed uncompressed/compressed ratio for ZIP uploads | `100.0` | No |
| `PAPERFORGE_ZIP_MAX_SIZE_BYTES` | Maximum total uncompressed size for uploaded ZIP archives | `262144000` (250 MB) | No |
| `PAPERFORGE_ZIP_MAX_ENTRIES` | Maximum total entry count allowed in a single archive | `10000` | No |
| `PAPERFORGE_ZIP_MAX_DEPTH` | Maximum nested directory depth allowed in a single archive | `10` | No |

## Storage Strategy Configuration

| Environment Variable | Description | Default Value | Required |
| :--- | :--- | :--- | :--- |
| `PAPERFORGE_STORAGE_PROVIDER` | Storage provider implementation strategy (`LOCAL` or `S3`) | `LOCAL` | No |
| `AWS_S3_BUCKET` | AWS S3 / MinIO bucket name for S3 storage provider | `paperforge-documents` | If `S3` |
| `AWS_S3_REGION` | AWS S3 region name | `us-east-1` | If `S3` |
| `AWS_ACCESS_KEY_ID` | AWS S3 / MinIO access key ID | `minioadmin` | If `S3` |
| `AWS_SECRET_ACCESS_KEY` | AWS S3 / MinIO secret access key | `minioadmin` | If `S3` |
| `AWS_S3_ENDPOINT` | Custom endpoint URL for MinIO or local S3 emulator | `http://localhost:9000` | No |

## JWT Authentication Security

| Environment Variable | Description | Default Value | Required |
| :--- | :--- | :--- | :--- |
| `PAPERFORGE_JWT_SECRET` | Secret key used for signing JWT authentication tokens (256+ bit) | Default generated string | Recommended in Prod |
| `PAPERFORGE_JWT_EXPIRATION_MS` | JWT token expiration validity duration in milliseconds | `86400000` (24 Hours) | No |

## Docker Compose Quickstart

### Standard Deployment
```bash
docker compose up -d
```

### Full Feature Deployment (LibreOffice + Tesseract + QPDF)
```bash
docker compose -f docker-compose.full.yml up -d
```

### Hardened Read-Only Deployment
```bash
docker compose -f docker-compose.security.yml up -d
```
