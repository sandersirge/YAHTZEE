# Docker Support for Yahtzee

This directory contains Docker configuration for building, testing, and running the Yahtzee JavaFX game in containers.

## Architecture

The multi-stage `Dockerfile` provides three targets:

| Target | Purpose | Base Image |
|--------|---------|------------|
| `builder` | Compile + jlink self-contained image | `eclipse-temurin:21-jdk-jammy` |
| `ci` | Headless test runner (Xvfb) | `eclipse-temurin:21-jdk-jammy` |
| `runtime` | Game with VNC/noVNC GUI | `debian:bookworm-slim` |

## Quick Start

### 1. Run the game with VNC (recommended for all platforms)

```bash
# From the repository root:
docker compose -f docker/docker-compose.yml up yahtzee --build

# Then open in your browser:
#   http://localhost:6080/vnc.html
# Password: yahtzee (or set VNC_PASSWORD env var)
```

### 2. Run tests in Docker (CI mode)

```bash
docker compose -f docker/docker-compose.yml --profile ci run --rm yahtzee-ci
```

### 3. Build only (produce jlink image)

```bash
docker build -f docker/Dockerfile --target builder -t yahtzee:builder ..
```

## Building individual stages

```bash
# Build the runtime image
docker build -f docker/Dockerfile --target runtime -t yahtzee:dev ..

# Build the CI image
docker build -f docker/Dockerfile --target ci -t yahtzee:ci ..
```

## Data Persistence

Game data files (`settings.txt`, `tulemused.txt`, `statistics.txt`) are stored in the `/data` directory inside the container. The `docker-compose.yml` maps this to a named volume `yahtzee_data` that persists across container restarts.

```bash
# Inspect the volume
docker volume inspect docker_yahtzee_data

# Back up data
docker run --rm -v docker_yahtzee_data:/data -v $(pwd):/backup alpine \
  tar czf /backup/yahtzee-data-backup.tar.gz -C /data .
```

## Platform-Specific GUI Access

### Windows
Use the **noVNC browser interface** — no extra software needed:
```
http://localhost:6080/vnc.html
```
Alternatively install VcXsrv and set `DISPLAY=host.docker.internal:0.0`.

### Linux (X11 socket forwarding — fastest)
```bash
xhost +local:docker
docker run --rm \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -v yahtzee_data:/data \
  yahtzee:dev
```

### macOS (XQuartz)
1. Install XQuartz and enable **"Allow connections from network clients"** in Preferences → Security
2. Restart XQuartz
3. Run:
```bash
xhost + $(hostname)
docker run --rm \
  -e DISPLAY=host.docker.internal:0 \
  -v yahtzee_data:/data \
  yahtzee:dev
```

Or just use the noVNC browser interface (port 6080) — works everywhere.

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `VNC_PASSWORD` | `yahtzee` | Password for VNC connections |
| `YAHTZEE_DATA_DIR` | `/data` | Directory for persistent game data |
| `DISPLAY` | `:1` | X11 display (set automatically in container) |

## Troubleshooting

**Container exits immediately:**
Check logs with `docker logs yahtzee_dev`. Usually means JavaFX native libraries are missing — the runtime stage installs all required ones.

**Black screen in noVNC:**
The Xvfb framebuffer may not have started. Try restarting: `docker compose -f docker/docker-compose.yml restart yahtzee`

**Sound not working:**
Audio passthrough from Docker is complex and platform-dependent. The game runs silently in Docker. Sound effects play when running natively.

**Image too large:**
The runtime image is ~300-500MB. Most of that is the jlink JRE + JavaFX native libs + VNC software. This is expected for a GUI application.

