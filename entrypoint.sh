#!/bin/sh
set -e

echo "Starting PaperForge Application Services..."

# Ensure JVM receives SIGTERM for graceful shutdown
exec java \
  -Djava.security.egd=file:/dev/./urandom \
  -Duser.timezone=UTC \
  -Dfile.encoding=UTF-8 \
  -XX:+UseG1GC \
  -XX:MaxRAMPercentage=75.0 \
  -jar /app/paperforge.jar "$@"
