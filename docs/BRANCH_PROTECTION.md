# PaperForge Branch Protection & CI/CD Deployment Governance

This document defines the required GitHub repository branch protection rules, mandatory status checks, deployment environment gates, and secret management guidelines for PaperForge.

---

## 🛡️ Main Branch Protection Rules (`main`)

To protect code quality, security, and production stability, the `main` branch must be configured with the following branch protection settings in GitHub Repository Settings (`Settings` -> `Branches` -> `Branch protection rules`):

### 1. Require Pull Request Reviews Before Merging
- **Require approvals**: At least 1 code review approval from a repository maintainer.
- **Dismiss stale pull request approvals when new commits are pushed**: Enabled.
- **Require review from Code Owners**: Enabled (if `CODEOWNERS` is present).

### 2. Require Status Checks to Pass Before Merging
Enable **Require status checks to pass before merging** and search for the following mandatory CI status check jobs:
- `Frontend Quality Checks & Build / Frontend Lint & Typecheck`
- `Frontend Quality Checks & Build / Frontend Vitest Component Tests`
- `Backend Quality Checks & Build / Backend Build & JaCoCo Coverage`
- `Docker Build & Trivy Vulnerability Scan / Docker Build & Trivy Vulnerability Scan`
- `E2E Playwright Browser Tests / E2E Playwright Browser Tests`
- `Secret Detection (Gitleaks)`

### 3. Enforce Strictly Linear History & Signed Commits
- **Require linear history**: Enabled (precludes merge commits, enforces clean rebase/squash).
- **Require signed commits**: Recommended for maintainers.

---

## 🔐 GitHub Secrets Management

All production keys, registry credentials, and deployment tokens must be stored in GitHub Repository Secrets (`Settings` -> `Secrets and variables` -> `Actions`). **NEVER hardcode secrets in code or workflow files.**

| Secret Name | Description | Scope |
| :--- | :--- | :--- |
| `GITHUB_TOKEN` | Automatically provided by GitHub Actions | Workflows, GHCR publishing, Releases |
| `PAPERFORGE_PROD_DB_URL` | Production PostgreSQL connection string | Staging / Prod Deployment Workflows |
| `PAPERFORGE_PROD_DB_PASSWORD` | Production PostgreSQL password | Staging / Prod Deployment Workflows |
| `PAPERFORGE_JWT_SECRET` | Production JWT signing secret key | Staging / Prod Deployment Workflows |

---

## 🚀 Environment Deployment Approval Gates

PaperForge uses GitHub Actions Environments (`Settings` -> `Environments`) to enforce approval gates across deployment targets:

1. **`development`**:
   - Deployed automatically on every merge to `main`.
2. **`staging`**:
   - Requires success of all CI quality checks and security scans.
3. **`production`**:
   - Requires manual approval from at least 1 designated release manager.
   - Restricted to tagged release commits (`v*.*.*`).
