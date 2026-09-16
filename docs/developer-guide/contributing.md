# PaperForge Contributing Guidelines

Thank you for contributing to PaperForge!

---

## 📜 Rules of Contribution

1. **Branding Rule**: Use "PaperForge" exclusively. Never introduce legacy software branding.
2. **Security First**: Ensure all user input is sanitized, file uploads checked for zip bombs, and secrets never committed.
3. **Pull Request Process**:
   - Create a topic branch from `main`.
   - Ensure all 87+ backend tests (`mvn clean verify`) and Vitest component tests (`npm run test`) pass cleanly.
   - Submit PR with clear description of changes.
