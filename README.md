# PaperForge

[![Build Status](https://github.com/paperforge/paperforge/actions/workflows/ci.yml/badge.svg)](https://github.com/paperforge/paperforge/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Forge your documents.**

PaperForge is an open-source, self-hosted PDF and document processing platform. It provides a secure, modern, and privacy-first web interface, native cross-platform desktop application, and REST API for manipulating, converting, OCRing, editing, and managing documents without artificial user limits or external vendor lock-in.

---

## 🌟 Key Features

- **Document Processing:** Merge, split, rotate, crop, compress, and re-order PDF pages.
- **Conversion Engine:** Convert PDFs to/from images, Office documents (DOCX, XLSX, PPTX), HTML, and plain text.
- **OCR Engine:** Extract text and create searchable PDFs using Tesseract OCR.
- **Security & Privacy:** Redact sensitive information, add digital signatures, watermarks, password protection, and sanitization.
- **Interactive PDF Editor:** In-browser PDF editor powered by PDF.js and pdf-lib.
- **Programmatic API Keys & Scopes:** Granular scoped API keys (`pdf:read`, `pdf:write`, `ocr`, `conversion`, `admin`) with BCrypt hashed storage.
- **Security Audit Logging:** Comprehensive event monitoring with CSV export and automated retention cleanup.
- **Internationalization (i18n):** Support for 10 languages (English, Tamil, Hindi, Malayalam, Telugu, Kannada, French, German, Spanish, Arabic) with RTL layout switching.
- **Self-Hosted & Cross-Platform:** Deploy via Docker or run natively on Windows, macOS, and Linux as a Tauri desktop app.

---

## 🏗️ Architecture & Tech Stack

- **Frontend:** React, TypeScript, Vite, Mantine UI, TailwindCSS, PDF.js, i18next
- **Backend:** Java 21, Spring Boot 3.3.x, Apache PDFBox, qpdf, LibreOffice, Tesseract OCR
- **Desktop Host:** Tauri v2, Rust, Managed Spring Boot Sidecar
- **Database:** PostgreSQL (with H2 in-memory mode for development/testing)
- **Deployment:** Docker, Docker Compose, Native Tauri Bundles

---

## 🚀 Deployment & Local Running

### Option 1: Running with Docker Compose (Recommended for Web)

#### Standard Monolithic Deployment
```bash
docker compose up -d
```

#### Full-Feature Deployment (LibreOffice + Tesseract OCR + QPDF)
```bash
docker compose -f docker-compose.full.yml up -d
```

#### Hardened Read-Only Security Deployment
```bash
docker compose -f docker-compose.security.yml up -d
```
Access the web UI at `http://localhost:8080`.

---

### Option 2: Running Native Desktop App (Tauri + Rust)

#### Build Prerequisites
- Java 21 LTS
- Rust 1.75+
- Node.js 22+

#### Build and Run Desktop App
```bash
# 1. Build backend JAR
cd backend && mvn clean package -DskipTests

# 2. Build frontend static assets
cd ../frontend && npm install && npm run build

# 3. Launch Tauri Desktop App
cd ../desktop && npm run dev
```

---

### Option 3: Bare-Metal Development Setup

#### 1. Backend Service
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Backend API starts at `http://localhost:8080`. Interactive OpenAPI Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

#### 2. Frontend Web App
```bash
cd frontend
npm install
npm run dev
```
Frontend Vite server starts at `http://localhost:5173`.

---

## 📄 License & Governance

- **License:** [MIT License](LICENSE)
- **Security Policy:** [SECURITY.md](SECURITY.md)
- **Contributing Guidelines:** [CONTRIBUTING.md](CONTRIBUTING.md)
- **Code of Conduct:** [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- **Container Security:** [docker/SECURITY.md](docker/SECURITY.md)
- **Environment Variables:** [docker/ENVIRONMENT.md](docker/ENVIRONMENT.md)
