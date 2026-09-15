# Stage 1: Build Frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci --quiet
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Backend
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app/backend
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B
COPY backend/src ./src
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static
RUN mvn package -DskipTests -B

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="PaperForge Engineering <admin@paperforge.com>"
LABEL org.opencontainers.image.title="PaperForge"
LABEL org.opencontainers.image.description="PaperForge Document Studio — Secure Open-Source Document Processing Platform"
LABEL org.opencontainers.image.version="0.0.1-SNAPSHOT"

RUN apk add --no-cache curl fontconfig ttf-dejavu

RUN addgroup -g 10001 -S paperforge && \
    adduser -u 10001 -S paperforge -G paperforge

WORKDIR /app
RUN mkdir -p /app/temp /app/storage /app/logs && \
    chown -R paperforge:paperforge /app

COPY --from=backend-builder /app/backend/target/paperforge-backend-0.0.1-SNAPSHOT.jar /app/paperforge.jar
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh && chown paperforge:paperforge /app/entrypoint.sh

USER paperforge:paperforge

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

ENTRYPOINT ["/app/entrypoint.sh"]
