# Monitoring & Troubleshooting Guide

Overview of Prometheus metrics, Grafana dashboards, and structured JSON logging.

---

## 📊 Prometheus & Grafana Monitoring

- **Prometheus Metrics Endpoint**: `/actuator/prometheus`
- **Component Health Check**: `/api/v1/health`
- **Grafana Dashboard JSON**: Import `docs/grafana-dashboard.json` into Grafana to visualize:
  - P95 / P99 PDF processing duration
  - Security audit event counter
  - JVM heap memory and GC pause times
  - Active background jobs queue
