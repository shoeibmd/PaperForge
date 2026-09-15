# PaperForge Container Runtime Security Hardening

This document outlines the defense-in-depth container runtime security controls implemented across PaperForge Docker images and Docker Compose configurations.

## Security Control Matrix

| Security Layer | Standard (`docker-compose.yml`) | Full (`docker-compose.full.yml`) | Hardened Security (`docker-compose.security.yml`) |
| :--- | :--- | :--- | :--- |
| **User Identity** | Non-Root `paperforge` (UID 10001) | Non-Root `paperforge` (UID 10001) | Non-Root `10001:10001` strictly enforced |
| **Root Filesystem** | Writable | Writable | **`read_only: true`** |
| **Linux Capabilities** | Default | Default | **`cap_drop: [ALL]`** |
| **Privilege Escalation** | Default | Default | **`no-new-privileges: true`** |
| **PIDs Limit** | OS Default | OS Default | **`pids_limit: 512`** |
| **Memory / CPU Limits** | Uncapped | Uncapped | **2048 MB RAM / 2.0 CPUs** |
| **Temporary Storage** | Disk | Disk | **`tmpfs` mounted at `/tmp` & `/app/temp`** |
| **Log Rotation** | `max-size: 10m`, `max-file: 3` | `max-size: 10m`, `max-file: 3` | `max-size: 10m`, `max-file: 3` |

---

## Hardened Deployment Details (`docker-compose.security.yml`)

### 1. Non-Root Execution
All PaperForge containers run exclusively as unprivileged user `paperforge` (UID 10001, GID 10001). The container processes cannot gain root access on the host or inspect host filesystems outside volume mounts.

### 2. Read-Only Filesystem & `tmpfs` Memory Mounts
The root filesystem is mounted as immutable (`read_only: true`). Dynamic write operations required by Java, PDFBox, LibreOffice, and Tesseract are isolated to volatile, memory-backed `tmpfs` mounts:
- `/tmp`: Memory mount for system temp files (`exec,mode=1777,size=256M`).
- `/app/temp`: Memory mount for application temp files (`exec,mode=0700,uid=10001,gid=10001,size=512M`).
- JVM is launched with `-Djava.io.tmpdir=/app/temp` to enforce temporary file creation within `tmpfs`.

### 3. Capability Dropping & Privilege Escalation Defense
- All Linux kernel capabilities are stripped (`cap_drop: [ALL]`). No elevated raw socket or kernel capabilities are retained.
- Process security option `no-new-privileges:true` prevents child processes (such as LibreOffice or Tesseract) from gaining elevated privileges via `setuid` binaries.

### 4. Denial-of-Service & Resource Exhaustion Defense
- **PID Limit**: `pids_limit: 512` prevents malicious fork bombs while granting sufficient headroom for multithreaded PDFBox rendering and background LibreOffice/Tesseract child processes.
- **Resource Constraints**: CPU usage capped at `2.0` cores; RAM capped at `2048M`.
- **Log Rotation**: Container logs are automatically rotated at `10MB` with a maximum of `3` retained log files.

---

## Security Verification Commands

### Verify Non-Root User Execution
```bash
docker exec -it paperforge-security-app whoami
# Expected output: paperforge

docker exec -it paperforge-security-app id
# Expected output: uid=10001(paperforge) gid=10001(paperforge) groups=10001(paperforge)
```

### Verify Read-Only Root Filesystem
```bash
docker exec -it paperforge-security-app touch /root_test.txt
# Expected output: touch: /root_test.txt: Read-only file system
```

### Verify `tmpfs` Mount Writability
```bash
docker exec -it paperforge-security-app touch /app/temp/test.tmp
# Expected output: File created successfully within tmpfs
```

### Verify Dropped Capabilities & No-New-Privileges
```bash
docker inspect paperforge-security-app --format '{{json .HostConfig.CapDrop}}'
# Expected output: ["ALL"]

docker inspect paperforge-security-app --format '{{json .HostConfig.SecurityOpt}}'
# Expected output: ["no-new-privileges:true"]
```
