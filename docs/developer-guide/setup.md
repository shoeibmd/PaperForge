# Developer Local Setup Guide

Step-by-step instructions for setting up PaperForge development environment locally.

---

## 💻 Prerequisites

- **Java 21 LTS**
- **Node.js 22 LTS**
- **Maven 3.9+**
- **Docker** (optional, for local PostgreSQL / MinIO testing)

---

## 🛠️ Step-by-Step Setup

### 1. Clone & Build Backend
```bash
git clone https://github.com/paperforge/paperforge.git
cd paperforge/backend
mvn clean install
mvn spring-boot:run
```

### 2. Build & Run Frontend
```bash
cd ../frontend
npm install
npm run dev
```

### 3. Run Test Suites
```bash
# Backend Tests
cd ../backend && mvn clean test

# Frontend Unit Tests
cd ../frontend && npm run test

# Frontend E2E Tests
cd ../frontend && npx playwright test
```
