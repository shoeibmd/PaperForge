# Production Deployment Guide

Guide for deploying PaperForge in production environments using Docker Compose or Kubernetes.

---

## 🐋 Docker Compose Deployment Variants

### 1. Standard Web Deployment
```bash
docker compose up -d
```

### 2. Full Feature Deployment (LibreOffice + Tesseract OCR + QPDF)
```bash
docker compose -f docker-compose.full.yml up -d
```

### 3. Hardened Security Deployment (Read-Only Root FS)
```bash
docker compose -f docker-compose.security.yml up -d
```

---

## ☸️ Kubernetes Deployment

Deploy PaperForge to Kubernetes using Deployment and Service manifests targeting image `ghcr.io/paperforge/paperforge:latest` on port `8080`.
Ensure liveness and readiness probes query `/api/v1/health`.
