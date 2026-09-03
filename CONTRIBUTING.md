# Contributing to PaperForge

Thank you for your interest in contributing to PaperForge! We welcome contributions from the community.

## Developer Certificate of Origin (DCO)

All commits must include a `Signed-off-by` line indicating agreement with the Developer Certificate of Origin (DCO).
You can sign your commit automatically using `git commit -s`.

```
Signed-off-by: Your Name <your.email@example.com>
```

## Getting Started

1. **Fork and Clone:** Fork the repository on GitHub and clone your fork locally.
2. **Set Up Development Environment:**
   - Java 21 LTS
   - Node.js 20 LTS & npm
   - Maven 3.9+
3. **Create a Feature Branch:** `git checkout -b feature/my-new-feature`

## Development Standards

### Code Style
- **Backend (Java):** Follow standard Java conventions and Google / Spring Java style.
- **Frontend (TypeScript/React):** Use ESLint and Prettier standards. Strict TypeScript mode must pass without errors.
- **Branding:** Use **PaperForge** exclusively. Never reference legacy or external project names in code, comments, or documentation.

### Commit Messages
Follow standard conventional commits:
- `feat: add PDF watermarking feature`
- `fix: resolve page count calculation bug`
- `docs: update setup instructions`

## Submitting a Pull Request

1. Ensure all backend tests pass: `cd backend && mvn clean verify`
2. Ensure frontend typechecks and lints pass: `cd frontend && npm run typecheck && npm run lint`
3. Push your branch and open a Pull Request targeting `main`.
4. Ensure all CI workflow checks pass.
