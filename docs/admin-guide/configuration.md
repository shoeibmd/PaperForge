# Complete Configuration Reference

Reference table of all `PAPERFORGE_*` environment variables.

---

| Variable Name | Description | Default | Required |
| :--- | :--- | :--- | :--- |
| `SERVER_PORT` | HTTP Server Port | `8080` | No |
| `PAPERFORGE_DEFAULT_USER_QUOTA_BYTES` | User Storage Quota | `524288000` (500 MB) | No |
| `PAPERFORGE_MAX_CONCURRENT_JOBS` | Max Parallel Background Jobs | `10` | No |
| `PAPERFORGE_JOB_TIMEOUT_SECONDS` | Job Execution Timeout | `120` | No |
| `PAPERFORGE_AUDIT_LOG_RETENTION_DAYS` | Audit Log Retention | `90` | No |
| `PAPERFORGE_ZIP_MAX_RATIO` | Zip Bomb Expansion Ratio Cap | `100.0` | No |
| `PAPERFORGE_ZIP_MAX_SIZE_BYTES` | Zip Bomb Uncompressed Size Cap | `262144000` (250 MB) | No |
| `PAPERFORGE_DB_URL` | PostgreSQL Database JDBC URL | `jdbc:postgresql://localhost:5432/paperforge` | In Prod |
| `PAPERFORGE_DB_USER` | PostgreSQL Username | `paperforge` | In Prod |
| `PAPERFORGE_DB_PASSWORD` | PostgreSQL Password | `<your-password>` | In Prod |
| `PAPERFORGE_JWT_SECRET` | JWT Signing Key (256-bit) | Default Generated Key | In Prod |
