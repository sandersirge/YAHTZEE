# ─── Stage 1: Build (compile + jlink) ─────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy Gradle wrapper and build config first (layer caching for dependencies)
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy source and build jlink image
COPY src/ src/
RUN ./gradlew jlink --no-daemon

# ─── Stage 2: CI (headless test runner) ──────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS ci

RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb libgl1 libgtk-3-0 libxtst6 libxrender1 libxi6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/
COPY src/ src/
RUN chmod +x gradlew

CMD xvfb-run --auto-servernum --server-args='-screen 0 1280x800x24' \
    ./gradlew test --no-daemon \
    -Dtestfx.headless=true \
    -Dprism.order=sw \
    -Dprism.verbose=true \
    -Djava.awt.headless=true

# ─── Stage 3: Runtime (jlink image + VNC for GUI access) ─────────────────────
FROM debian:bookworm-slim AS runtime

# Install display server, VNC, and JavaFX native dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb \
    tigervnc-standalone-server \
    novnc \
    websockify \
    libgl1 \
    libgtk-3-0 \
    libxtst6 \
    libxrender1 \
    libxi6 \
    procps \
    && rm -rf /var/lib/apt/lists/*

# Copy the self-contained jlink image (no JDK required at runtime)
COPY --from=builder /app/build/jlink-image /opt/yahtzee

# Data volume mount point for persistent files (settings, results, statistics)
RUN mkdir -p /data
ENV YAHTZEE_DATA_DIR=/data

# Copy the VNC startup script
COPY docker/scripts/start-vnc.sh /start-vnc.sh
RUN chmod +x /start-vnc.sh

# VNC direct port + noVNC browser port
EXPOSE 5900 6080

VOLUME ["/data"]

ENTRYPOINT ["/start-vnc.sh"]

