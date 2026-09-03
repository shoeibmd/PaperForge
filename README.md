# PaperForge

**Forge your documents.**

PaperForge is an open-source, self-hosted PDF and document processing platform. It provides a secure, modern, and privacy-first web interface and REST API for manipulating, converting, OCRing, editing, and managing documents without artificial user limits or external vendor lock-in.

---

## 🌟 Key Features

- **Document Processing:** Merge, split, rotate, crop, compress, and re-order PDF pages.
- **Conversion Engine:** Convert PDFs to/from images, Office documents (DOCX, XLSX, PPTX), HTML, and plain text.
- **OCR Engine:** Extract text and create searchable PDFs using Tesseract OCR.
- **Security & Privacy:** Redact sensitive information, add digital signatures, watermarks, password protection, and sanitization.
- **Self-Hosted & Scalable:** Designed to run via Docker or bare metal with zero telemetry, unlimited user capacity, and local or object storage support (S3/MinIO).

---

## 🏗️ Architecture & Tech Stack

- **Frontend:** React, TypeScript, Vite, Mantine UI, TailwindCSS, PDF.js
- **Backend:** Java 21, Spring Boot 3.3.x, Apache PDFBox, qpdf, LibreOffice, Tesseract OCR
- **Database:** PostgreSQL (with H2 in-memory mode for development/testing)
- **Deployment:** Docker, Docker Compose

---

## 🚀 Quick Start

### Running with Docker Compose
```bash
docker compose up -d
```
Access the web UI at `http://localhost:3000` (or `http://localhost:8080` if running monolithic container).

### Local Development Setup

#### Prerequisites
- Java 21 LTS
- Node.js 20+ LTS
- Maven 3.9+
- npm 10+

#### 1. Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
The backend REST API will start at `http://localhost:8080`.

#### 2. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
The frontend Vite server will start at `http://localhost:5173` with automated API proxying to `http://localhost:8080`.

---

## 📄 License & Governance

- **License:** [MIT License](LICENSE)
- **Security Policy:** [SECURITY.md](SECURITY.md)
- **Contributing Guidelines:** [CONTRIBUTING.md](CONTRIBUTING.md)
- **Code of Conduct:** [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
