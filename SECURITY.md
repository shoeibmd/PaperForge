# Security Policy

PaperForge takes security seriously. As a self-hosted document processing application that handles sensitive files, we are committed to maintaining a high security standard.

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.0.x   | :white_check_mark: |

## Reporting a Vulnerability

**Please do NOT report security vulnerabilities through public GitHub issues.**

Instead, please report security issues privately:
1. Email security reports to `security@paperforge.org` (or contact core maintainers via private disclosure channel).
2. Include a detailed description of the vulnerability, steps to reproduce, and proof-of-concept (PoC) if available.
3. Allow up to 48 hours for an initial response acknowledging receipt.

### What to Expect
- **Acknowledgement:** Within 48 hours.
- **Triage & Assessment:** Within 5 business days.
- **Patch Release & Disclosure:** We aim to release a patch and issue a security advisory within 30 days for confirmed critical/high severity vulnerabilities.

## Security Practices in PaperForge

- **Data Hygiene:** Temporary files generated during document processing are isolated and securely erased immediately after completion.
- **Path Traversal Protection:** Input filenames and storage paths are sanitized to prevent directory traversal attacks.
- **Command Injection Prevention:** Native CLI tools (e.g. qpdf, LibreOffice, Tesseract) are invoked safely without passing unsanitized user inputs to shell commands.
- **No Unintended Retention:** PaperForge does not store document content or user files beyond active processing requirements unless explicitly configured.
