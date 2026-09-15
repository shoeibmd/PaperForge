# PaperForge Desktop Application Architecture & Build Guide

PaperForge Desktop provides a native cross-platform desktop application for Windows, macOS, and Linux using **Tauri v2** and **Rust**, bundling the React frontend and launching the Java Spring Boot backend as a managed sidecar process on port `8081`.

## Architecture Overview

```
PaperForge Desktop
├── React Webview Frontend (bundled from frontend/dist)
├── Rust Native Host (desktop/src-tauri)
│   ├── Sidecar Process Manager (sidecar.rs -> Java Spring Boot on port 8081)
│   ├── Native File Dialogs
│   └── OS File Association Handler (.pdf)
└── Spring Boot Backend Sidecar (paperforge-backend.jar)
```

## System Requirements

1. **Java Runtime Environment**: Java 21 LTS or later installed on the target machine (or bundled JRE directory).
2. **Rust & Cargo**: Rust 1.75+ for compiling the Tauri native host.
3. **Node.js**: Node.js 20+ for frontend compilation.

---

## Building PaperForge Desktop

### Step 1: Build the Backend Sidecar JAR
```bash
cd backend
mvn clean package -DskipTests
```

### Step 2: Build the Frontend Assets
```bash
cd frontend
npm run build
```

### Step 3: Compile and Package the Desktop App
```bash
cd desktop
npm run build
```

The compiled native installer binaries will be generated in:
- **Windows**: `desktop/src-tauri/target/release/bundle/msi/` or `bundle/nsis/`
- **macOS**: `desktop/src-tauri/target/release/bundle/dmg/` or `bundle/macos/`
- **Linux**: `desktop/src-tauri/target/release/bundle/deb/` or `bundle/appimage/`

---

## OS Integrations

- **File Associations**: Automatically registers PaperForge Desktop as the default handler for `.pdf` files.
- **Sidecar Lifecycle**: Automatically launches Spring Boot on port `8081` on app launch and terminates child process on window close (`SIGTERM`).
- **Offline Mode**: Operates completely offline with zero external network dependencies.
