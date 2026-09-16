# PaperForge Quick Start Guide

Get up and running with **PaperForge Document Studio** in under 5 minutes using Docker Compose.

---

## ⚡ 3-Step Docker Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/paperforge/paperforge.git
cd paperforge
```

### 2. Launch Container Stack
```bash
docker compose up -d
```

### 3. Open PaperForge
Open your web browser and navigate to:
```
http://localhost:8080
```

---

## 💻 Desktop Application Quick Start

To run PaperForge as a native desktop application on Windows, macOS, or Linux:

### 1. Build Backend Sidecar & Frontend
```bash
cd backend && mvn clean package -DskipTests
cd ../frontend && npm install && npm run build
```

### 2. Launch Tauri Desktop App
```bash
cd ../desktop && npm run dev
```

---

## 🛠️ Next Steps

- Explore PDF operations in the [PDF Core Guide](pdf-core.md).
- Learn about document security in the [Security Guide](security.md).
- Set up programmatic API access in the [API Keys Guide](security.md#api-keys).
