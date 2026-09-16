# Backup & Disaster Recovery Guide

Procedures for backing up PostgreSQL database records and stored document assets.

---

## 💾 PostgreSQL Database Backup

```bash
# Export PostgreSQL dump
pg_dump -h localhost -U paperforge -d paperforge -F c -b -v -f paperforge_backup.dump
```

## 📂 Storage Assets Backup

- **Local Storage**: Backup contents of `/app/storage` or persistent Docker volume `paperforge-storage`.
- **S3 Storage**: Enable S3 Bucket Versioning and cross-region replication.
