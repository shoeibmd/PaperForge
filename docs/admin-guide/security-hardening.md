# Production Security Hardening Guide

Security best practices for hardening PaperForge production deployments.

---

## 🔒 Security Checklist

1. **Enforce Non-Root Execution**: Ensure containers run as UID 10001 (`paperforge`).
2. **Read-Only Root Filesystem**: Deploy using `docker-compose.security.yml` with `read_only: true` and `tmpfs` mounts.
3. **Drop Linux Capabilities**: Strip container privileges (`cap_drop: [ALL]`).
4. **TLS Termination**: Place NGINX or Caddy reverse proxy in front of PaperForge with valid TLS certificates.
5. **Database Least Privilege**: Grant DML/DDL permissions strictly on the `paperforge` database schema.
