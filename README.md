# PaperForge

[![Build Status](https://github.com/paperforge/paperforge/actions/workflows/ci.yml/badge.svg)](https://github.com/paperforge/paperforge/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Forge your documents with privacy and precision.**

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

## 📖 Comprehensive Documentation Index

### 👤 User Guides (`docs/user-guide/`)
- [Quick Start Guide](docs/user-guide/quick-start.md)
- [PDF Core Operations](docs/user-guide/pdf-core.md)
- [PDF Security & Encryption](docs/user-guide/security.md)
- [Document Conversion](docs/user-guide/conversion.md)
- [OCR Processing](docs/user-guide/ocr.md)
- [PDF Compression](docs/user-guide/compression.md)
- [Interactive PDF Editor](docs/user-guide/editor.md)
- [Frequently Asked Questions (FAQ)](docs/user-guide/faq.md)

### 🛠️ Administrator Guides (`docs/admin-guide/`)
- [Production Deployment](docs/admin-guide/deployment.md)
- [Environment Configuration Reference](docs/admin-guide/configuration.md)
- [Production Security Hardening](docs/admin-guide/security-hardening.md)
- [Monitoring & Grafana Setup](docs/admin-guide/monitoring.md)
- [Backup & Disaster Recovery](docs/admin-guide/backup-recovery.md)

### 💻 Developer Guides (`docs/developer-guide/`)
- [System Architecture & Sequence Diagrams](docs/developer-guide/architecture.md)
- [Local Developer Setup](docs/developer-guide/setup.md)
- [Contributing Guidelines](docs/developer-guide/contributing.md)

---

## ⚡ 3-Command Quick Start

```bash
git clone https://github.com/paperforge/paperforge.git
cd paperforge
docker compose up -d
```
Access the PaperForge web studio at `http://localhost:8080`.

---

## 📄 License & Governance

- **License:** [MIT License](LICENSE)
- **Security Policy:** [SECURITY.md](SECURITY.md)
- **Contributing Guidelines:** [CONTRIBUTING.md](CONTRIBUTING.md)
- **Code of Conduct:** [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
