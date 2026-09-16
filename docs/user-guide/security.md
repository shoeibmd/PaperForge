# PDF Security & Encryption Guide

Protect and manage permissions on sensitive documents with PaperForge security tools.

---

## 🔒 Security Operations

### 1. PDF Encryption
Encrypt PDFs using AES-128 or AES-256 bit key lengths with separate user and owner passwords.
- **Endpoint**: `/api/v1/pdf/security/encrypt`

### 2. PDF Decryption
Decrypt password-protected PDFs by providing the valid password.
- **Endpoint**: `/api/v1/pdf/security/decrypt`

### 3. Watermarking & Sanitization
Apply custom text watermarks and strip hidden metadata prior to public distribution.

---

## 🔑 Programmatic API Keys

Generate scoped API keys under **API Keys** in the sidebar.
- **Scopes**: `pdf:read`, `pdf:write`, `ocr`, `conversion`, `admin`.
- **Headers**: Include `X-API-Key: pf_live_...` or `Authorization: Bearer pf_live_...`.
