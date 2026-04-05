
> **Baseline**: Yahtzee v2.0 (JavaFX Enhanced) — Java 21, JavaFX 21, Gradle 8.5, JPMS, jlink distribution.  
> **Last audited**: 2026-04-04  
> **Tracks**: 0 — Housekeeping · 1 — Docker · 2 — CI/CD · 3 — Web Version

---

## Table of Contents

1. [Current State Assessment](#current-state-assessment)
2. [Track 0 — Housekeeping & Code Quality](#track-0--housekeeping--code-quality)
3. [Track 1 — Docker Support](#track-1--docker-support)
4. [Track 2 — DevOps / CI-CD](#track-2--devops--ci-cd)
5. [Track 3 — Web Version (Separate Repository)](#track-3--web-version-separate-repository)
6. [Tech Decisions](#tech-decisions)
7. [Timeline / Roadmap](#timeline--roadmap)

---

## Current State Assessment

### Strengths

| Area          | Observation                                                                                               |
|---------------|-----------------------------------------------------------------------------------------------------------|
| Architecture  | Clean 4-layer MVC separation; `GameController` is JavaFX-free — extractable as a library                  |
| Model layer   | `Die`, `Player`, `Combination` hierarchy are pure Java; `Die.setCurrentValue()` exists for test injection |
| UI decoupling | All CSS inline in `ThemeController`; no hard FXML dependency (scenes built programmatically)              |
| Persistence   | Read/write UTF-8 consistently in `StatisticsController`; simple key=value format is parse-friendly        |
| Build         | Gradle wrapper included; jlink task produces self-contained distribution ZIP                              |

### Identified Gaps

| #    | File / Location              | Gap                                                                                                                                           | Severity | Status                    |
|------|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|----------|---------------------------|
| G-01 | `src/test/`                  | Completely empty — zero test coverage                                                                                                         | Critical | ✅ FIXED — 169 tests       |
| G-02 | `module-info.java`           | Only `projekt.yahtzee` opened/exported; sub-packages unreachable via reflection                                                               | High     | ✅ FIXED                   |
| G-03 | `GameConstants.java:188`     | `getTitleFont()` / `getCellFont()` etc. instantiate `javafx.scene.text.Font` — requires live JavaFX toolkit; blocks unit testing of constants | High     | ✅ FIXED → `UIFonts.java`  |
| G-04 | `ResultsFileManager.java:30` | `new FileWriter(file, true)` without explicit `StandardCharsets.UTF_8` arg                                                                    | Medium   | ✅ FIXED                   |
| G-05 | `Player.java:103`            | `getUsedCombos()` returns live `int[]` — mutation by callers corrupts state                                                                   | Medium   | ⬜ TODO                    |
| G-06 | `build.gradle:48`            | `${buildDir}` is deprecated since Gradle 7; use `layout.buildDirectory`                                                                       | Medium   | ✅ FIXED                   |
| G-07 | `GameConstants.java:149,152` | `MAX_ROLLS_PER_TURN` and `MAX_ROLLS` are identical duplicate constants                                                                        | Low      | ✅ FIXED                   |
| G-08 | `ThemeController.java`       | ~310 lines of CSS strings in Java; no `.css` stylesheet files                                                                                 | Low      | ⬜ Optional                |
| G-09 | `GameConstants.java:72`      | `FONT_FAMILY = "Comfortaa"` — font not bundled as resource; silently falls back                                                               | Low      | ⬜ TODO                    |
| G-10 | `GameConstants.java:158–160` | Data files written to CWD (`settings.txt`, etc.) — breaks in Docker / packaged installs                                                       | Low      | ⬜ TODO                    |
| G-11 | `build.gradle`               | No Checkstyle, SpotBugs, or Javadoc tasks                                                                                                     | Low      | ⚠️ Javadoc added          |
| G-12 | Root                         | No `.gitignore` found                                                                                                                         | Low      | ✅ FIXED (already existed) |

---

## Track 0 — Housekeeping & Code Quality

### Goal

Eliminate all identified gaps, establish a testing baseline, and put every contributor on equal footing with linting, Javadoc generation, and a proper `.gitignore` — before any infrastructure work begins.

### Milestones

| ID   | Milestone                                          | Target   |
|------|----------------------------------------------------|----------|
| M0-A | Test suite bootstrapped with ≥ 70 % model coverage | Week 1   |
| M0-B | Build hygiene: `.gitignore`, Checkstyle, Javadoc   | Week 1   |
| M0-C | Code defects (G-03 → G-10) resolved                | Week 2   |
| M0-D | All CI checks green on `main` branch               | Week 2   |

### Tasks

#### 0.1 — Test Suite Bootstrap

- [x] **[S]** Add `src/test/java/projekt/yahtzee/model/DieTest.java`  
  Test `roll()` output is in `[1,6]`; test `setCurrentValue()` and `compareTo()`.

- [x] **[S]** Add `src/test/java/projekt/yahtzee/model/PlayerTest.java`  
  Test `rollDice()` keeps/rolls correct dice; test `addRolledCombo()` increments count; test `compareTo()` on score ordering.

- [x] **[M]** Add `src/test/java/projekt/yahtzee/model/combos/CombinationTest.java`  
  Parameterised JUnit 5 tests for all 9 combo implementations: `Chance`, `FourOfKind`, `FullHouse`, `LargeStraight`, `Numbers`, `SmallStraight`, `ThreeOfKind`, `YahtzeeCombination`.  
  Use `@ParameterizedTest` + `@MethodSource` for dice value fixtures.

- [x] **[M]** Add `src/test/java/projekt/yahtzee/controller/game/GameControllerTest.java`  
  Test `addPlayer()`, `rollDice()`, `saveScore()`, `isGameFinished()`, `getWinners()`, bonus threshold logic (upper ≥ 63 → +35).

- [x] **[S]** Add `src/test/java/projekt/yahtzee/model/combos/CombinationRegistryTest.java`  
  Assert 13 combos registered; assert `isUpperSection()` returns correct boolean for indices 0–5.

- [x] **[S]** Add JUnit Jupiter params dependency to `build.gradle`:
  ```
  testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}")
  ```

- [ ] **[M]** Add TestFX + Monocle for optional headless UI smoke tests (defer to after Track 1 Docker):
  ```
  // build.gradle — test block addition
  testImplementation 'org.testfx:testfx-core:4.0.18'
  testImplementation 'org.testfx:testfx-junit5:4.0.18'
  testImplementation 'org.testfx:openjfx-monocle:jdk-12.0.1+2'
  ```

#### 0.2 — Fix G-03: Decouple JavaFX Font/Color from `GameConstants`

- [x] **[M]** Split `GameConstants.java` into two files:
  - Keep `GameConstants.java` for all non-JavaFX constants (sizes, text, rules, file paths, CSS strings, spacing).
  - Add `projekt.yahtzee.util.UIFonts.java` containing only the `Font`/`Color`-returning static methods (`getTitleFont()`, `getCellFont()`, etc.).
  - Update all call sites (search for `GameConstants.getTitle`, `GameConstants.getCell`, etc.).
  - Result: `GameConstants` becomes testable without a JavaFX toolkit.

#### 0.3 — Fix G-05: Defensive copy in `Player.getUsedCombos()`

- [ ] **[S]** In `Player.java`, change `getUsedCombos()` to return `Arrays.copyOf(usedCombos, usedCombos.length)`.  
  Update `GameController.saveScore()` accordingly — it currently mutates the returned array directly; replace with a dedicated `markComboUsed(int index)` method on `Player`.

#### 0.4 — Fix G-04: Explicit charset in `ResultsFileManager`

- [x] **[S]** In `ResultsFileManager.saveResults()`, replace  
  `new FileWriter(GameConstants.RESULTS_FILE, true)` with  
  `Files.newBufferedWriter(Paths.get(GameConstants.RESULTS_FILE), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)`.

#### 0.5 — Fix G-06: Deprecated Gradle `buildDir`

- [x] **[S]** In `build.gradle`, replace:
  ```
  imageZip = project.file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip")
  ```
  with:
  ```
  imageZip = layout.buildDirectory.file("distributions/app-${javafx.platform.classifier}.zip").get().asFile
  ```

#### 0.6 — Fix G-07: Remove duplicate constant

- [x] **[S]** In `GameConstants.java`, delete `MAX_ROLLS = 3`; update any remaining call sites to use `MAX_ROLLS_PER_TURN`.

#### 0.7 — Fix G-09: Bundle the Comfortaa font

- [ ] **[S]** Download `Comfortaa-Regular.ttf` (OFL-licensed) and place it at  
  `src/main/resources/projekt/yahtzee/fonts/Comfortaa-Regular.ttf`.
- [ ] **[S]** In `YahtzeeApplication.start()`, load font before first scene:
  ```
  Font.loadFont(ResourceLoader.get("fonts/Comfortaa-Regular.ttf"), 14);
  ```

#### 0.8 — Fix G-10: Platform-agnostic data directory

- [ ] **[M]** Add `projekt.yahtzee.util.AppDataDirectory.java` with a static `resolve(String filename)` method:
  - Linux/Mac: `$HOME/.local/share/yahtzee/<filename>`
  - Windows: `%APPDATA%\Yahtzee\<filename>`
  - Override-able via system property `-Dyahtzee.data.dir=/custom/path` (useful for Docker volume mounts).
  ```
  public static Path resolve(String filename) {
      String override = System.getProperty("yahtzee.data.dir",
          System.getenv().getOrDefault("YAHTZEE_DATA_DIR", null));
      if (override != null) return Paths.get(override, filename);
      String os = System.getProperty("os.name", "").toLowerCase();
      String home = System.getProperty("user.home");
      if (os.contains("win")) {
          String appData = System.getenv().getOrDefault("APPDATA", home);
          return Paths.get(appData, "Yahtzee", filename);
      }
      return Paths.get(home, ".local", "share", "yahtzee", filename);
  }
  ```
- [ ] **[S]** Replace all three `Paths.get(GameConstants.RESULTS_FILE)` / `STATISTICS_FILE` / `SETTINGS_FILE` occurrences in `ResultsFileManager`, `StatisticsController`, and `ThemeController` with `AppDataDirectory.resolve(...)`.

#### 0.9 — Fix G-02: `module-info.java` completeness

- [x] **[S]** Update `module-info.java` to export/open all necessary sub-packages:
  ```
  module projekt.yahtzee {
      requires transitive javafx.controls;
      requires javafx.fxml;
      requires javafx.media;

      exports projekt.yahtzee;
      exports projekt.yahtzee.model;
      exports projekt.yahtzee.model.combos;
      exports projekt.yahtzee.controller.game;
      exports projekt.yahtzee.controller.data;
      exports projekt.yahtzee.controller.ui;
      exports projekt.yahtzee.util;

      opens projekt.yahtzee to javafx.fxml;
      opens projekt.yahtzee.ui.scenes to javafx.fxml;
  }
  ```

#### 0.10 — Build hygiene

- [x] **[S]** Add `.gitignore` at repository root (already existed, updated with Docker entries)
  ```
  .gradle/
  build/
  *.class
  *.jar
  settings.txt
  tulemused.txt
  statistics.txt
  .idea/
  *.iml
  out/
  ```

- [ ] **[M]** Add Checkstyle to `build.gradle`:
  ```
  plugins { id 'checkstyle' }
  checkstyle {
      toolVersion = '10.14.2'
      configFile = file("config/checkstyle/checkstyle.xml")
      ignoreFailures = false
  }
  ```
  Add a `config/checkstyle/checkstyle.xml` based on Google Java Style (line-length adjusted to 120).

- [ ] **[M]** Add SpotBugs:
  ```
  plugins { id 'com.github.spotbugs' version '6.0.9' }
  spotbugs { toolVersion = '4.8.3'; ignoreFailures = false }
  ```

- [x] **[S]** Add Javadoc task configuration:
  ```
  javadoc {
      options.encoding = 'UTF-8'
      options.memberLevel = JavadocMemberLevel.PUBLIC
  }
  ```

- [x] **[S]** Bump `version` in `build.gradle` from `'1.0-SNAPSHOT'` to `'1.1.0'`.

#### 0.11 — Optional: Extract ThemeController CSS to stylesheets

- [ ] **[L]** Add `src/main/resources/projekt/yahtzee/themes/dark.css` and `light.css`.  
  Move all inline CSS strings from `ThemeController.java` into themed CSS classes (e.g., `.score-cell`, `.score-cell:hover`, `.total-row`).  
  Apply via `scene.getStylesheets().setAll(...)` when theme toggles.  
  Reduces `ThemeController` to ~40 lines of stylesheet-path management.

### Dependencies

- JUnit 5.10.0 (already in `build.gradle`)
- Checkstyle 10.14.2 (new)
- SpotBugs 4.8.3 / Gradle plugin 6.0.9 (new)
- TestFX 4.0.18 + Monocle (optional, for UI tests)
- Comfortaa font (OFL-licensed, free download)

### Acceptance Criteria

- [ ] `./gradlew test` passes with ≥ 70 % line coverage on `model/` and `controller/game/`
- [ ] `./gradlew checkstyleMain` produces zero violations
- [ ] `./gradlew spotbugsMain` produces zero high-priority bugs
- [ ] `./gradlew javadoc` completes without errors
- [ ] `./gradlew build` completes without deprecation warnings
- [ ] `.gitignore` prevents `*.txt` data files and `build/` from being committed
- [ ] `GameConstants` class has no `import javafx.*` statements

---

## Track 1 — Docker Support

### Goal

Containerize the JavaFX desktop application for three scenarios: (1) headless CI build-and-test, (2) developer-accessible GUI via VNC/browser, (3) self-contained end-user distribution image using the jlink output. Provide a `docker-compose.yml` for local dev and document the X11/display forwarding approach on each OS.

### Key Challenge: JavaFX Requires a Display

JavaFX renders to native windows via Glass/Prism. In a headless Docker container there is no display server. Three strategies exist:

| Strategy                   | Use Case                                  | Tool                                   |
|----------------------------|-------------------------------------------|----------------------------------------|
| Xvfb (virtual framebuffer) | CI headless builds and TestFX tests       | `xvfb-run`                             |
| VNC server (TigerVNC)      | Dev GUI access from any browser via noVNC | `tigervnc-standalone-server` + `novnc` |
| X11 socket forwarding      | Local dev on Linux/Mac host               | Mount `/tmp/.X11-unix`                 |

### Milestones

| ID   | Milestone                                            | Target  |
|------|------------------------------------------------------|---------|
| M1-A | Multi-stage `Dockerfile` (compile + jlink)           | Week 3  |
| M1-B | `Dockerfile` runtime stage — jlink image + VNC       | Week 3  |
| M1-C | `docker-compose.yml` for dev (VNC + data volume)     | Week 3  |
| M1-D | `Dockerfile.ci` — headless Xvfb test image           | Week 4  |
| M1-E | Documentation: README section on Docker usage per OS | Week 4  |

### Tasks

#### 1.1 — Multi-stage `Dockerfile` (Build + jlink + Runtime)

- [x] **[M]** Add `docker/Dockerfile` at repository root with three stages:

```dockerfile
# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon  # warm cache

COPY src/ src/
RUN ./gradlew jlink --no-daemon -Pjavafx.platform=linux

# ─── Stage 2: Runtime (jlink image + VNC) ─────────────────────────────────────
FROM debian:bookworm-slim AS runtime

# Display + audio dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    tigervnc-standalone-server \
    novnc \
    websockify \
    libgl1 libgtk-3-0 libxtst6 libxrender1 libxi6 \
    && rm -rf /var/lib/apt/lists/*

# Copy jlink self-contained image (no JDK needed)
COPY --from=builder /app/build/image /opt/yahtzee

# Data volume mount point (settings.txt, tulemused.txt, statistics.txt)
RUN mkdir -p /data
ENV YAHTZEE_DATA_DIR=/data

# VNC startup script
COPY docker/scripts/start-vnc.sh /start-vnc.sh
RUN chmod +x /start-vnc.sh

EXPOSE 5900 6080
VOLUME ["/data"]
ENTRYPOINT ["/start-vnc.sh"]
```

- [x] **[S]** Add `docker/scripts/start-vnc.sh`:

```bash
#!/bin/bash
set -e

# Set VNC password (default: yahtzee — override via VNC_PASSWORD env var)
mkdir -p ~/.vnc
echo "${VNC_PASSWORD:-yahtzee}" | vncpasswd -f > ~/.vnc/passwd
chmod 600 ~/.vnc/passwd

# Start TigerVNC on :1
tigervncserver :1 -geometry 1280x800 -depth 24 -PasswordFile ~/.vnc/passwd
export DISPLAY=:1

# Start noVNC websocket proxy on port 6080 (browser access)
websockify --web /usr/share/novnc/ 6080 localhost:5900 &

# Launch Yahtzee via jlink launcher
exec /opt/yahtzee/bin/app -Dyahtzee.data.dir=/data
```

- [ ] **[S]** Note on JavaFX platform classifier: when building inside `eclipse-temurin:21-jdk-jammy` (Linux), `javafx.platform.classifier` resolves to `linux` automatically. The jlink output lands at:
  ```
  /app/build/image/        ← extracted jlink image directory
  /app/build/image/bin/app ← launcher script
  ```

#### 1.2 — CI Headless Dockerfile (Xvfb Only)

- [x] **[S]** Add `docker/Dockerfile.ci` (merged as `ci` stage in main Dockerfile):

```dockerfile
FROM eclipse-temurin:21-jdk-jammy AS ci

RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb libgl1 libgtk-3-0 libxtst6 libxrender1 libxi6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .
RUN chmod +x gradlew

# Run tests headlessly via Xvfb
CMD xvfb-run --auto-servernum --server-args='-screen 0 1280x800x24' \
    ./gradlew test --no-daemon \
    -Dtestfx.headless=true \
    -Dprism.order=sw \
    -Dprism.verbose=true \
    -Djava.awt.headless=true
```

#### 1.3 — `docker-compose.yml` for Dev

- [x] **[M]** Add `docker/docker-compose.yml`:

```yaml
version: '3.9'

services:
  yahtzee:
    build:
      context: ..
      dockerfile: docker/Dockerfile
      target: runtime
    image: yahtzee:dev
    container_name: yahtzee_dev
    ports:
      - "5900:5900"    # VNC direct connect (use a VNC client)
      - "6080:6080"    # noVNC browser: http://localhost:6080/vnc.html
    volumes:
      - yahtzee_data:/data
    environment:
      - DISPLAY=:1
      - YAHTZEE_DATA_DIR=/data
      - VNC_PASSWORD=yahtzee
    restart: unless-stopped

  yahtzee-build:
    build:
      context: ..
      dockerfile: docker/Dockerfile
      target: builder
    image: yahtzee:builder
    profiles: ["build"]
    volumes:
      - ../build:/app/build
      - gradle_cache:/root/.gradle

volumes:
  yahtzee_data:
  gradle_cache:
```

#### 1.4 — Data File Path Integration (depends on Track 0, Task 0.8)

- [ ] **[S]** Once `AppDataDirectory.java` is implemented, ensure the Docker entrypoint sets `YAHTZEE_DATA_DIR=/data` and `AppDataDirectory.resolve()` reads it:
  ```
  String dataDir = System.getProperty("yahtzee.data.dir",
      System.getenv().getOrDefault("YAHTZEE_DATA_DIR", null));
  ```
  The `/data` path in the container maps to the `yahtzee_data` named volume, persisting results across restarts.

#### 1.5 — X11 Socket Forwarding (Local Dev — Linux/Mac)

- [x] **[S]** Document in `docker/README-docker.md`:

```bash
# ── Linux host ──────────────────────────────────────────
xhost +local:docker
docker run --rm \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -v yahtzee_data:/data \
  yahtzee:dev

# ── Mac host (XQuartz) ──────────────────────────────────
# 1. Install XQuartz; enable "Allow connections from network clients"
# 2. Run: xhost + $(hostname)
# 3. docker run with -e DISPLAY=host.docker.internal:0

# ── Windows host ────────────────────────────────────────
# Option A: Use VcXsrv (set DISPLAY=host.docker.internal:0.0)
# Option B: Use WSLg with X11 socket forwarding
# Option C: Just use noVNC in your browser at http://localhost:6080/vnc.html
```

#### 1.6 — `.dockerignore`

- [x] **[S]** Add `.dockerignore` at repository root:
  ```
  .gradle/
  build/
  .idea/
  *.iml
  out/
  settings.txt
  tulemused.txt
  statistics.txt
  docker/
  *.md
  ```

### Dependencies

- Docker Engine 24+ (BuildKit enabled)
- Docker Compose v2
- Base images: `eclipse-temurin:21-jdk-jammy`, `debian:bookworm-slim`
- Track 0 Task 0.8 (`AppDataDirectory`) must be done before Task 1.4

### Acceptance Criteria

- [ ] `docker build -f docker/Dockerfile --target runtime -t yahtzee:dev .` completes in < 5 minutes
- [ ] `docker compose -f docker/docker-compose.yml up yahtzee` starts; game UI is visible via noVNC at `localhost:6080/vnc.html`
- [ ] Data files persist across container restarts via the `yahtzee_data` named volume
- [ ] `docker run yahtzee:ci` runs `./gradlew test` without a physical display and exits 0 (once tests exist from Track 0)
- [ ] Runtime image size: < 500 MB
- [ ] No credentials baked into any image layer

---

## Track 2 — DevOps / CI-CD

### Goal

Automate build, test, lint, packaging, Docker image publication to GHCR, and release management on every PR and tag push using GitHub Actions. The pipeline must be reproducible, fast (< 10 min for the main flow), and gate merges behind quality checks.

### Milestones

| ID   | Milestone                                                 | Target |
|------|-----------------------------------------------------------|--------|
| M2-A | `ci.yml` — build + test + lint on every PR                | Week 4 |
| M2-B | `package.yml` — jlink ZIP artifact upload on `main` merge | Week 5 |
| M2-C | `docker.yml` — Docker build + GHCR push                   | Week 5 |
| M2-D | `release.yml` — tag-triggered release with changelog      | Week 6 |
| M2-E | Branch protection rules + status checks documented        | Week 6 |

### Repository / Branch Strategy

```
main          ← protected; only squash-merge from PRs
develop       ← integration branch (optional for larger teams)
feature/*     ← short-lived feature branches
release/v*    ← release candidate branches
```

### Tasks

#### 2.1 — Base Workflow: CI on Pull Requests

- [x] **[M]** Add `.github/workflows/ci.yml`:

```yaml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

env:
  JAVA_VERSION: '21'

jobs:
  build-and-test:
    name: Build & Test (JDK ${{ matrix.java }})
    runs-on: ubuntu-22.04
    strategy:
      matrix:
        java: ['21']

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK ${{ matrix.java }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java }}
          distribution: temurin
          cache: gradle

      - name: Install display + JavaFX native dependencies
        run: |
          sudo apt-get update
          sudo apt-get install -y xvfb libgl1 libgtk-3-0 libxtst6 libxrender1 libxi6

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Compile
        run: ./gradlew compileJava --no-daemon

      - name: Run tests (headless via Xvfb)
        run: |
          xvfb-run --auto-servernum --server-args='-screen 0 1280x800x24' \
            ./gradlew test --no-daemon \
            -Dtestfx.headless=true \
            -Dprism.order=sw \
            -Djava.awt.headless=true

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results-jdk${{ matrix.java }}
          path: build/reports/tests/test/

  lint:
    name: Lint (Checkstyle + SpotBugs)
    runs-on: ubuntu-22.04
    needs: build-and-test

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: temurin
          cache: gradle
      - run: chmod +x gradlew
      - name: Checkstyle
        run: ./gradlew checkstyleMain --no-daemon
      - name: SpotBugs
        run: ./gradlew spotbugsMain --no-daemon
      - name: Upload Checkstyle report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: checkstyle-report
          path: build/reports/checkstyle/
      - name: Upload SpotBugs report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: spotbugs-report
          path: build/reports/spotbugs/
```

#### 2.2 — jlink Packaging Workflow

- [x] **[M]** Add `.github/workflows/package.yml`:

```yaml
name: Package (jlink)

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  jlink:
    name: jlink Distribution
    runs-on: ubuntu-22.04

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: temurin
          cache: gradle
      - run: chmod +x gradlew

      - name: Install JavaFX native dependencies
        run: |
          sudo apt-get update
          sudo apt-get install -y libgl1 libgtk-3-0 libxtst6

      - name: Build jlink image
        run: ./gradlew jlink --no-daemon

      - name: Upload jlink ZIP
        uses: actions/upload-artifact@v4
        with:
          name: yahtzee-jlink-linux
          path: build/distributions/app-linux.zip
          retention-days: 30

      - name: Upload fat JAR
        uses: actions/upload-artifact@v4
        with:
          name: yahtzee-fat-jar
          path: build/libs/YAHTZEE-*.jar
          retention-days: 30
```

#### 2.3 — Docker Build & GHCR Push

- [x] **[M]** Add `.github/workflows/docker.yml`:

```yaml
name: Docker

on:
  push:
    branches: [main]
    tags: ['v*.*.*']
  workflow_dispatch:

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository_owner }}/yahtzee

jobs:
  docker-build-push:
    name: Build & Push Docker Image
    runs-on: ubuntu-22.04
    permissions:
      contents: read
      packages: write

    steps:
      - uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to GHCR
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Extract Docker metadata (tags + labels)
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=ref,event=branch
            type=semver,pattern={{version}}
            type=semver,pattern={{major}}.{{minor}}
            type=sha,prefix=sha-

      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          file: docker/Dockerfile
          target: runtime
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

#### 2.4 — Release Workflow (Tag-Triggered)

- [x] **[M]** Add `.github/workflows/release.yml`:

```yaml
name: Release

on:
  push:
    tags: ['v*.*.*']

jobs:
  release:
    name: Create GitHub Release
    runs-on: ubuntu-22.04
    permissions:
      contents: write
      packages: write

    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0        # full history for changelog

      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: temurin
          cache: gradle

      - run: chmod +x gradlew

      - name: Install JavaFX native dependencies
        run: sudo apt-get update && sudo apt-get install -y libgl1 libgtk-3-0

      - name: Build jlink distribution
        run: ./gradlew jlink --no-daemon

      - name: Generate changelog (commits since last tag)
        id: changelog
        run: |
          PREV_TAG=$(git describe --tags --abbrev=0 HEAD^ 2>/dev/null || echo "")
          if [ -z "$PREV_TAG" ]; then
            LOG=$(git log --oneline)
          else
            LOG=$(git log ${PREV_TAG}..HEAD --oneline)
          fi
          echo "CHANGELOG<<EOF" >> $GITHUB_OUTPUT
          echo "$LOG"           >> $GITHUB_OUTPUT
          echo "EOF"            >> $GITHUB_OUTPUT

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          name: Yahtzee ${{ github.ref_name }}
          body: |
            ## Changes
            ${{ steps.changelog.outputs.CHANGELOG }}
          files: |
            build/distributions/app-linux.zip
            build/libs/YAHTZEE-*.jar
          draft: false
          prerelease: ${{ contains(github.ref_name, '-rc') || contains(github.ref_name, '-beta') }}
```

#### 2.5 — Dependency Review & Security Scanning

- [ ] **[S]** Add `dependency-review.yml` to scan dependency changes on PRs:
  ```yaml
  name: Dependency Review
  on: [pull_request]
  jobs:
    dependency-review:
      runs-on: ubuntu-22.04
      steps:
        - uses: actions/checkout@v4
        - uses: actions/dependency-review-action@v4
  ```

- [x] **[S]** Enable GitHub Dependabot for Gradle via `.github/dependabot.yml`:
  ```yaml
  version: 2
  updates:
    - package-ecosystem: gradle
      directory: "/"
      schedule:
        interval: weekly
      open-pull-requests-limit: 5
  ```

#### 2.6 — Branch Protection Rules (Manual — GitHub UI)

- [ ] **[S]** On `main`:
  - Require status checks: `build-and-test`, `lint`
  - Require 1 approving review
  - Dismiss stale reviews on new push
  - Require linear history (squash merge only)

#### 2.7 — Caching Strategy

- [ ] **[S]** Gradle cache is handled by `actions/setup-java@v4` with `cache: gradle`.  
  Docker layer cache via `type=gha` (already in workflow 2.3).  
  Estimated CI runtime: **Compile ~2 min + Tests ~3 min + Lint ~2 min = ~7 min total**.

### Dependencies

- GitHub repository with Actions enabled
- GHCR package registry (auto-enabled for GitHub accounts)
- `GITHUB_TOKEN` secret (auto-provided by Actions — no manual setup needed)
- Track 0 (Checkstyle + SpotBugs + tests) must be done before lint/test jobs pass green
- Track 1 `docker/Dockerfile` must exist before Docker workflow runs

### Acceptance Criteria

- [ ] Every PR to `main` triggers `ci.yml`; failure blocks merge via branch protection
- [ ] `./gradlew test` passes in CI with Xvfb (no physical display required)
- [ ] Checkstyle and SpotBugs jobs produce HTML reports as downloadable artifacts
- [ ] `git tag v1.1.0 && git push --tags` triggers a GitHub Release with jlink ZIP attached
- [ ] Docker image `ghcr.io/<owner>/yahtzee:main` is pullable after merge to main
- [ ] `docker pull ghcr.io/<owner>/yahtzee:v1.1.0` and game starts via noVNC at port 6080

---

## Track 3 — Web Version (Separate Repository)

### Goal

Deliver a browser-playable Yahtzee with real-time multiplayer. Reuse the existing pure-Java game logic (`model/` + `controller/game/`) as a shared library `yahtzee-core`. Build a Spring Boot REST + WebSocket backend and a React/TypeScript frontend, backed by PostgreSQL, deployed via Docker Compose with Nginx reverse-proxying.

### New Repository: `yahtzee-web`

```
yahtzee-web/
├── yahtzee-core/               ← Extracted Java library (zero JavaFX)
│   ├── build.gradle
│   └── src/main/java/projekt/yahtzee/core/
│       ├── model/              ← Die, Player, Rollable (copied from desktop)
│       ├── combos/             ← All 9 combos + Combination + CombinationRegistry
│       ├── controller/         ← GameController (pure Java, no JavaFX imports)
│       └── util/               ← GameRules.java (constants only, no JavaFX)
│
├── backend/                    ← Spring Boot 3.2 application
│   ├── build.gradle
│   ├── Dockerfile
│   └── src/main/java/projekt/yahtzee/web/
│       ├── YahtzeeWebApplication.java
│       ├── config/             ← WebSocketConfig, SecurityConfig, CorsConfig
│       ├── api/                ← REST controllers + DTOs
│       ├── websocket/          ← STOMP message handlers
│       ├── service/            ← GameSessionService, StatisticsService
│       ├── domain/             ← JPA entities (GameSession, PlayerRecord, etc.)
│       └── repository/         ← Spring Data JPA repositories
│
├── frontend/                   ← React 18 + TypeScript application
│   ├── package.json
│   ├── vite.config.ts
│   ├── Dockerfile
│   └── src/
│       ├── components/         ← DicePanel, ScoreTable, GameBoard, Dialogs
│       ├── pages/              ← MainMenu, GameSetup, GameBoard, Statistics
│       ├── hooks/              ← useWebSocket, useGameState, useSound
│       ├── services/           ← api.ts (Axios), websocket.ts (SockJS/STOMP)
│       ├── store/              ← Zustand state slices
│       └── types/              ← GameState, Player, Combination TypeScript types
│
├── nginx/
│   └── nginx.conf
├── docker-compose.yml
├── docker-compose.dev.yml
└── .github/workflows/
    ├── ci-backend.yml
    ├── ci-frontend.yml
    └── release.yml
```

### Milestones

| ID   | Milestone                                          | Target     |
|------|----------------------------------------------------|------------|
| M3-A | `yahtzee-core` library extracted and passing tests | Week 5–6   |
| M3-B | Backend: REST game session API functional          | Week 6–7   |
| M3-C | Backend: WebSocket real-time multiplayer           | Week 7–8   |
| M3-D | Frontend: single-player playable in browser        | Week 8–9   |
| M3-E | Frontend: real-time multiplayer lobby + game       | Week 9–11  |
| M3-F | PostgreSQL persistence + statistics API            | Week 10–11 |
| M3-G | Docker Compose full stack + Nginx                  | Week 11–12 |
| M3-H | CI/CD for web repo                                 | Week 12    |

### Tasks

#### 3.1 — Extract `yahtzee-core` Library

- [ ] **[M]** Add `yahtzee-core/build.gradle`:
  ```
  plugins {
      id 'java-library'
  }
  group = 'projekt.yahtzee'
  version = '1.0.0'
  sourceCompatibility = '21'
  dependencies {
      testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.0'
      testImplementation 'org.junit.jupiter:junit-jupiter-params:5.10.0'
      testRuntimeOnly   'org.junit.jupiter:junit-jupiter-engine:5.10.0'
  }
  test { useJUnitPlatform() }
  ```

- [ ] **[M]** Copy the following from the desktop project into `yahtzee-core/src/main/java/`:
  - `projekt/yahtzee/core/model/Die.java`
  - `projekt/yahtzee/core/model/Player.java`
  - `projekt/yahtzee/core/model/Rollable.java`
  - `projekt/yahtzee/core/model/combos/Combination.java` + all 8 concrete combos
  - `projekt/yahtzee/core/model/combos/CombinationRegistry.java`
  - `projekt/yahtzee/core/controller/GameController.java`
  - `projekt/yahtzee/core/util/GameRules.java` — new file, non-JavaFX constants only

- [ ] **[S]** Verify zero `import javafx.*` statements in any copied file.

- [ ] **[S]** Inject `Random` into `Die` constructor for full testability:
  ```
  public Die(Random rng) { this.rng = rng; }
  public Die() { this(new Random()); }
  public void roll() { currentValue = rng.nextInt(6) + 1; }
  ```

- [ ] **[S]** Wire as Gradle composite build in `yahtzee-web/settings.gradle`:
  ```
  rootProject.name = 'yahtzee-web'
  includeBuild 'yahtzee-core'
  include 'backend', 'frontend'
  ```

#### 3.2 — Backend: Spring Boot Project Setup

- [ ] **[M]** Add `backend/build.gradle`:
  ```
  plugins {
      id 'org.springframework.boot' version '3.2.4'
      id 'io.spring.dependency-management' version '1.1.4'
      id 'java'
  }
  group = 'projekt.yahtzee.web'
  version = '1.0.0'
  sourceCompatibility = '21'

  dependencies {
      implementation project(':yahtzee-core')
      implementation 'org.springframework.boot:spring-boot-starter-web'
      implementation 'org.springframework.boot:spring-boot-starter-websocket'
      implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
      implementation 'org.springframework.boot:spring-boot-starter-validation'
      implementation 'org.postgresql:postgresql'
      implementation 'org.flywaydb:flyway-core'
      testImplementation 'org.springframework.boot:spring-boot-starter-test'
      testImplementation 'com.h2database:h2'
  }
  ```

- [ ] **[M]** Add `backend/src/main/resources/application.yml`:
  ```yaml
  spring:
    datasource:
      url: ${DB_URL:jdbc:postgresql://localhost:5432/yahtzee}
      username: ${DB_USER:yahtzee}
      password: ${DB_PASS:yahtzee}
    jpa:
      hibernate:
        ddl-auto: validate
    flyway:
      enabled: true
      locations: classpath:db/migration

  server:
    port: 8080

  yahtzee:
    max-inactive-minutes: 60
  ```

#### 3.3 — Backend: Database Schema (Flyway Migrations)

- [ ] **[M]** Add `backend/src/main/resources/db/migration/V1__init.sql`:

```
CREATE TABLE game_sessions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    finished_at TIMESTAMP,
    status      VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    max_players INT NOT NULL DEFAULT 3
);

CREATE TABLE players (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    game_session_id      UUID NOT NULL REFERENCES game_sessions(id),
    name                 VARCHAR(64) NOT NULL,
    join_order           INT NOT NULL,
    total_score          INT NOT NULL DEFAULT 0,
    upper_section_score  INT NOT NULL DEFAULT 0,
    lower_section_score  INT NOT NULL DEFAULT 0,
    bonus_awarded        BOOLEAN NOT NULL DEFAULT FALSE,
    used_combos          INT[] NOT NULL DEFAULT ARRAY_FILL(0, ARRAY[13])
);

CREATE TABLE turns (
    id              SERIAL PRIMARY KEY,
    player_id       UUID NOT NULL REFERENCES players(id),
    combo_index     INT NOT NULL,
    combo_name      VARCHAR(64) NOT NULL,
    points_earned   INT NOT NULL,
    turn_number     INT NOT NULL,
    played_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE global_statistics (
    player_name  VARCHAR(64) PRIMARY KEY,
    games_played INT NOT NULL DEFAULT 0,
    wins         INT NOT NULL DEFAULT 0,
    high_score   INT NOT NULL DEFAULT 0,
    total_score  BIGINT NOT NULL DEFAULT 0
);
```

#### 3.4 — Backend: REST API

- [ ] **[M]** Add `backend/.../api/GameSessionController.java`:

| Method   | Endpoint                    | Description                                                |
|----------|-----------------------------|------------------------------------------------------------|
| `POST`   | `/api/games`                | Start a new game session; returns `{ gameId, joinCode }`   |
| `GET`    | `/api/games/{gameId}`       | Get full game state                                        |
| `POST`   | `/api/games/{gameId}/join`  | Join a game with `{ playerName }`                          |
| `POST`   | `/api/games/{gameId}/start` | Start game (host only)                                     |
| `GET`    | `/api/games`                | List active/recent games (paginated)                       |

- [ ] **[M]** Add `backend/.../api/GameActionController.java`:

| Method  | Endpoint                              | Body                        | Description                                     |
|---------|---------------------------------------|-----------------------------|-------------------------------------------------|
| `POST`  | `/api/games/{gameId}/roll`            | `{ keptDice: [0,1,0,0,1] }` | Roll non-kept dice; returns updated dice values |
| `POST`  | `/api/games/{gameId}/score`           | `{ comboIndex: 7 }`         | Save score for current player                   |
| `GET`   | `/api/games/{gameId}/possible-scores` | —                           | Returns score hints for current dice            |

- [ ] **[M]** Add `backend/.../api/StatisticsController.java`:

| Method   | Endpoint                       | Description                                              |
|----------|--------------------------------|----------------------------------------------------------|
| `GET`    | `/api/statistics`              | Global statistics (total games, high score, top players) |
| `GET`    | `/api/statistics/{playerName}` | Per-player statistics                                    |
| `GET`    | `/api/results`                 | Paginated game history                                   |

- [ ] **[S]** Add DTOs using Java 21 records:
  ```
  public record CreateGameRequest(int maxPlayers) {}
  public record RollRequest(int[] keptDice) {}
  public record ScoreRequest(int comboIndex) {}
  public record GameStateResponse(
      UUID gameId, String status,
      List<PlayerStateDto> players,
      int currentPlayerIndex,
      int rollCount,
      List<Integer> diceValues
  ) {}
  ```

#### 3.5 — Backend: WebSocket (STOMP)

- [ ] **[M]** Add `WebSocketConfig.java`:
  ```
  @Configuration
  @EnableWebSocketMessageBroker
  public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
      @Override
      public void configureMessageBroker(MessageBrokerRegistry config) {
          config.enableSimpleBroker("/topic", "/queue");
          config.setApplicationDestinationPrefixes("/app");
      }
      @Override
      public void registerStompEndpoints(StompEndpointRegistry registry) {
          registry.addEndpoint("/ws")
                  .setAllowedOriginPatterns("*")
                  .withSockJS();
      }
  }
  ```

- [ ] **[M]** STOMP topic/destination contract:

| Destination                | Direction       | Payload             | Description                                    |
|----------------------------|-----------------|---------------------|------------------------------------------------|
| `/app/games/{id}/roll`     | Client → Server | `RollRequest`       | Roll dice command                              |
| `/app/games/{id}/score`    | Client → Server | `ScoreRequest`      | Save score command                             |
| `/topic/games/{id}`        | Server → All    | `GameStateResponse` | Full state broadcast after any action          |
| `/topic/games/{id}/events` | Server → All    | `GameEvent`         | Lightweight event (player joined, score saved) |
| `/queue/errors`            | Server → User   | `ErrorResponse`     | Per-user error messages                        |

- [ ] **[S]** Use `SimpMessagingTemplate.convertAndSend("/topic/games/{id}", state)` to broadcast after every action so all browser tabs stay in sync.

#### 3.6 — Backend: Game Session Service

- [ ] **[M]** `GameSessionService.java` responsibilities:
  - Hold active `GameController` instances in `ConcurrentHashMap<UUID, GameController>` (in-memory, ≤ 60-minute sessions)
  - On creation: instantiate `GameController` from `yahtzee-core`, persist `GameSession` entity to PostgreSQL
  - On every action: mutate in-memory state → persist to `turns` table → broadcast via WebSocket
  - On game end: persist final scores → update `global_statistics` → clear from in-memory map
  - Scheduled cleanup: `@Scheduled(fixedRate = 60_000)` evicts sessions older than `max-inactive-minutes`

#### 3.7 — Frontend: Project Setup

- [ ] **[M]** Initialise frontend:
  ```bash
  cd yahtzee-web/frontend
  npm create vite@latest . -- --template react-ts
  npm install
  npm install @stomp/stompjs sockjs-client axios zustand
  npm install -D tailwindcss postcss autoprefixer @types/sockjs-client eslint
  npx tailwindcss init -p
  ```

- [ ] **[S]** Add `frontend/src/services/api.ts`:
  ```
  import axios from 'axios';
  export const api = axios.create({ baseURL: '/api', timeout: 10_000 });
  ```

- [ ] **[M]** Add `frontend/src/services/websocket.ts`:
  ```
  import { Client } from '@stomp/stompjs';
  import SockJS from 'sockjs-client';

  export function createGameClient(
      gameId: string,
      onState: (state: GameState) => void
  ): Client {
      return new Client({
          webSocketFactory: () => new SockJS('/ws'),
          onConnect: () => {
              client.subscribe(`/topic/games/${gameId}`, msg => {
                  onState(JSON.parse(msg.body));
              });
          },
      });
  }
  ```

#### 3.8 — Frontend: Pages and Components

- [ ] **[M]** `pages/MainMenu.tsx` — Start game, join by code input, statistics link.

- [ ] **[M]** `pages/GameSetup.tsx` — Player count (1–4 for web), player name inputs, "New Game" button → shows `joinCode` to share with friends.

- [ ] **[L]** `pages/GameBoard.tsx` — Primary game view:
  - `components/DicePanel.tsx` — 5 clickable dice (SVG assets), click-to-toggle keep
  - `components/ScoreTable.tsx` — 13-row scoreboard per player; highlight possible scores in green
  - `components/GameControls.tsx` — Roll button (disabled when roll count = 3), turn/roll indicator
  - `components/PlayerList.tsx` — Active player highlight, live score totals
  - Subscribes to `/topic/games/{id}`; re-renders entirely on each WebSocket message.

- [ ] **[M]** `pages/Statistics.tsx` — Fetch `GET /api/statistics`; display leaderboard, total games, high score.

- [ ] **[M]** `hooks/useGameState.ts`:
  ```
  export function useGameState(gameId: string) {
      const [state, setState] = useState<GameState | null>(null);
      useEffect(() => {
          api.get(`/games/${gameId}`).then(r => setState(r.data));
          const client = createGameClient(gameId, setState);
          client.activate();
          return () => { client.deactivate(); };
      }, [gameId]);
      return state;
  }
  ```

#### 3.9 — Nginx Reverse Proxy

- [ ] **[M]** Add `nginx/nginx.conf`:
  ```nginx
  upstream backend { server backend:8080; }

  server {
      listen 80;

      # Proxy REST API
      location /api/ {
          proxy_pass         http://backend;
          proxy_set_header   Host $host;
          proxy_set_header   X-Real-IP $remote_addr;
      }

      # Proxy WebSocket (upgrade required)
      location /ws {
          proxy_pass         http://backend;
          proxy_http_version 1.1;
          proxy_set_header   Upgrade $http_upgrade;
          proxy_set_header   Connection "upgrade";
          proxy_read_timeout 86400;
      }

      # Serve React SPA (all other routes → index.html)
      location / {
          root   /usr/share/nginx/html;
          try_files $uri $uri/ /index.html;
      }
  }
  ```

#### 3.10 — Docker Compose (Full Stack)

- [ ] **[M]** Add `docker-compose.yml`:

```yaml
version: '3.9'

services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: yahtzee
      POSTGRES_USER: yahtzee
      POSTGRES_PASSWORD: ${DB_PASS:-yahtzee}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U yahtzee"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./backend
    image: yahtzee-backend:latest
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/yahtzee
      DB_USER: yahtzee
      DB_PASS: ${DB_PASS:-yahtzee}
    depends_on:
      postgres:
        condition: service_healthy
    ports:
      - "8080:8080"

  frontend:
    build:
      context: ./frontend
      target: production
    image: yahtzee-frontend:latest
    depends_on: [backend]

  nginx:
    image: nginx:1.25-alpine
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf:ro
    ports:
      - "80:80"
    depends_on: [frontend, backend]

volumes:
  postgres_data:
```

- [ ] **[S]** Add `docker-compose.dev.yml` override for hot reload:
  ```yaml
  services:
    frontend:
      build:
        target: dev
      volumes:
        - ./frontend/src:/app/src
      ports:
        - "5173:5173"
    backend:
      environment:
        SPRING_JPA_HIBERNATE_DDL_AUTO: update
  ```

#### 3.11 — Backend `Dockerfile` (Multi-stage)

- [ ] **[M]** Add `backend/Dockerfile`:
  ```dockerfile
  FROM eclipse-temurin:21-jdk-jammy AS builder
  WORKDIR /workspace
  COPY ../yahtzee-core /workspace/yahtzee-core
  WORKDIR /workspace/backend
  COPY gradlew gradlew.bat build.gradle settings.gradle ./
  COPY gradle/ gradle/
  RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
  COPY src/ src/
  RUN ./gradlew bootJar --no-daemon

  FROM eclipse-temurin:21-jre-jammy AS runtime
  WORKDIR /app
  COPY --from=builder /workspace/backend/build/libs/*.jar app.jar
  ENTRYPOINT ["java", "-jar", "app.jar"]
  ```

#### 3.12 — Frontend `Dockerfile` (Multi-stage)

- [ ] **[M]** Add `frontend/Dockerfile`:
  ```dockerfile
  FROM node:20-alpine AS dev
  WORKDIR /app
  COPY package*.json ./
  RUN npm ci
  COPY . .
  CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]

  FROM dev AS builder
  RUN npm run build

  FROM nginx:1.25-alpine AS production
  COPY --from=builder /app/dist /usr/share/nginx/html
  EXPOSE 80
  ```

#### 3.13 — CI/CD for Web Repository

- [ ] **[M]** Add `.github/workflows/ci-backend.yml`:
  ```yaml
  name: Backend CI
  on: [push, pull_request]
  jobs:
    test:
      runs-on: ubuntu-22.04
      services:
        postgres:
          image: postgres:16-alpine
          env:
            POSTGRES_DB: yahtzee_test
            POSTGRES_USER: yahtzee
            POSTGRES_PASSWORD: yahtzee
          ports: ['5432:5432']
          options: --health-cmd pg_isready --health-interval 10s --health-timeout 5s --health-retries 5
      steps:
        - uses: actions/checkout@v4
        - uses: actions/setup-java@v4
          with: { java-version: '21', distribution: temurin, cache: gradle }
        - run: cd backend && chmod +x gradlew && ./gradlew test --no-daemon
          env:
            DB_URL: jdbc:postgresql://localhost:5432/yahtzee_test
            DB_USER: yahtzee
            DB_PASS: yahtzee
  ```

- [ ] **[M]** Add `.github/workflows/ci-frontend.yml`:
  ```yaml
  name: Frontend CI
  on: [push, pull_request]
  jobs:
    test:
      runs-on: ubuntu-22.04
      steps:
        - uses: actions/checkout@v4
        - uses: actions/setup-node@v4
          with: { node-version: '20', cache: npm, cache-dependency-path: frontend/package-lock.json }
        - run: cd frontend && npm ci && npm run type-check && npm run lint && npm run build
  ```

- [ ] **[S]** Add scripts to `frontend/package.json`:
  ```
  "scripts": {
    "type-check": "tsc --noEmit",
    "lint": "eslint src --ext .ts,.tsx --max-warnings 0"
  }
  ```

#### 3.14 — Feature Parity Roadmap (Web vs Desktop)

| Feature              | Desktop           | Web v1.0                      | Web v2.0           |
|----------------------|-------------------|-------------------------------|--------------------|
| 1–3 players local    | ✅                 | ✅ (1 browser)                 | —                  |
| Online multiplayer   | ❌                 | ✅ (2–4 players via WebSocket) | —                  |
| 13 combinations      | ✅                 | ✅ (via yahtzee-core)          | —                  |
| Upper section bonus  | ✅                 | ✅                             | —                  |
| Score hints          | ✅                 | ✅                             | —                  |
| Sound effects        | ✅ (MP3)           | ⬜ (Web Audio API)             | ✅                  |
| Theme switching      | ✅                 | ⬜ (Tailwind dark mode)        | ✅                  |
| Statistics dashboard | ✅ (flat file)     | ✅ (PostgreSQL)                | —                  |
| Game history         | ✅ (tulemused.txt) | ✅ (PostgreSQL, paginated API) | —                  |
| Save/load mid-game   | ❌                 | ❌                             | ✅ (Redis sessions) |
| AI opponents         | ❌                 | ❌                             | ✅                  |
| Lobby / join code    | ❌                 | ✅                             | —                  |
| Mobile-responsive    | N/A               | ⬜                             | ✅                  |
| Spectator mode       | ❌                 | ❌                             | ✅                  |

### Dependencies

- Java 21, Spring Boot 3.2, PostgreSQL 16, Flyway 10
- Node.js 20 LTS, React 18, TypeScript 5, Vite 5, Tailwind CSS 3
- Zustand 4, @stomp/stompjs 7, sockjs-client 1.6, Axios 1.x
- Track 0 Task 0.3 (`markComboUsed` refactor on `Player`) recommended before extraction
- Track 0 Task 0.2 (JavaFX decoupling) required before `yahtzee-core` extraction

### Acceptance Criteria

- [ ] `yahtzee-core` has ≥ 80 % test coverage and zero `javafx.*` imports
- [ ] `docker compose up` starts all 4 services; game is playable at `localhost` on port 80
- [ ] Two browser tabs complete a full 13-round multiplayer game with live score updates
- [ ] `GET /api/statistics` returns correct data after 3 completed games
- [ ] `postgres_data` volume survives container restart; game history persists
- [ ] Frontend `npm run type-check` and `npm run lint` pass with zero errors/warnings
- [ ] Backend CI job (with real PostgreSQL service container) passes on every PR

---

## Tech Decisions

### TD-1: Display Strategy for Docker (Xvfb vs VNC vs X11)

**Decision**: Xvfb for CI, TigerVNC + noVNC for dev GUI.  
**Rationale**: `xvfb-run` is a single-command wrapper, adds zero interactive overhead, and is the standard in GitHub Actions Ubuntu runners. TigerVNC + noVNC provides universal browser-based GUI access without requiring a local X11 client — making the dev image portable to Windows developers who can just open a browser at port 6080.

### TD-2: TestFX + Monocle vs Xvfb for UI Testing

**Decision**: TestFX + Monocle (`-Dprism.order=sw`) as primary; Xvfb as fallback for native-rendering integration tests.  
**Rationale**: Monocle runs in-process, is deterministic, and requires no external process. It is Oracle's official headless strategy for JavaFX unit tests. Xvfb adds process overhead and is non-deterministic for pixel-level assertions.

### TD-3: GitHub Actions vs GitLab CI

**Decision**: GitHub Actions.  
**Rationale**: The project's README references `git clone <repository-url>`, consistent with GitHub. GitHub Actions has native GHCR integration, generous free tier for public repos, and the `setup-java` action has built-in Gradle cache support.

### TD-4: Spring Boot vs Quarkus for Web Backend

**Decision**: Spring Boot 3.2.  
**Rationale**: Spring Boot has the most complete STOMP/WebSocket support (`spring-websocket`), the broadest Spring Data JPA ecosystem, and the largest community. Quarkus's faster cold-start is irrelevant for a stateful game server with long-lived WebSocket connections.

### TD-5: Zustand vs Redux Toolkit for Frontend State

**Decision**: Zustand.  
**Rationale**: Game state is a single object received wholesale from the WebSocket. Redux Toolkit's dispatch/action pattern adds unnecessary boilerplate. Zustand's `set(state => ...)` maps directly to "replace state with WebSocket message".

### TD-6: PostgreSQL vs SQLite for Web Backend

**Decision**: PostgreSQL 16.  
**Rationale**: SQLite has no concurrent write support — it would serialize or deadlock under simultaneous WebSocket game actions from multiple players. PostgreSQL supports row-level locking and enables future horizontal scaling. H2 in-memory is used for unit tests only.

### TD-7: `yahtzee-core` as Gradle Composite Build vs Published Library

**Decision**: Gradle composite build (`includeBuild 'yahtzee-core'` in root `settings.gradle`).  
**Rationale**: Avoids managing a Maven Central or GitHub Packages publication. Changes to `yahtzee-core` are immediately reflected in the backend without a publishing step. Can be promoted to a published artifact later if consumed by additional projects.

### TD-8: CSS Stylesheets vs Inline Styles in ThemeController

**Decision**: Extract to `.css` files (Track 0 Task 0.11 — optional/low priority).  
**Rationale**: Inline CSS strings in Java have no IDE validation and are hard to maintain. However, this is cosmetic and lower priority than testing and CI. Deferring keeps the Track 0 scope focused.

---

## Timeline / Roadmap

```
PHASE         WEEK  1    2    3    4    5    6    7    8    9   10   11   12
──────────────────────────────────────────────────────────────────────────────
Track 0       [████████████]
  Tests          [█████]
  Gaps G03-10       [█████]
  Build hygiene  [████████]

Track 1                   [████████]
  Dockerfile         [████████]
  Compose               [████]
  Docs                     [████]

Track 2                        [████████]
  ci.yml                [████]
  package.yml               [████]
  docker.yml                [████]
  release.yml                   [████]

Track 3 (Web)                     [████████████████████████]
  yahtzee-core               [████]
  Backend REST                    [████████]
  Backend WS                          [████████]
  Frontend setup                           [████]
  Frontend gameboard                       [████████]
  Frontend multiplayer                          [████████]
  PostgreSQL + stats                       [████████]
  Docker Compose + Nginx                             [████]
  Web CI/CD                                          [████]
──────────────────────────────────────────────────────────
```

### Effort Summary

| Track                                 | Total Effort Estimate   |
|---------------------------------------|-------------------------|
| Track 0 — Housekeeping & Code Quality | ~3–4 dev-days           |
| Track 1 — Docker Support              | ~3–4 dev-days           |
| Track 2 — CI/CD Workflows             | ~2–3 dev-days           |
| Track 3 — Web Version (full)          | ~25–35 dev-days         |
| **Total**                             | **~33–46 dev-days**     |

> Effort labels used throughout: **[S]** = < 1 hour · **[M]** = 2–4 hours · **[L]** = 1–2 days

### Recommended Execution Order

1. **Track 0 first** — Tests and clean code are prerequisites for CI green checks and for safely extracting `yahtzee-core`.
2. **Track 1 in parallel with Track 0 (Week 2)** — Dockerfile work doesn't block code quality fixes.
3. **Track 2 after Track 0 is complete** — CI workflows need passing tests and lint to be meaningful gates.
4. **Track 3 after Track 0 is complete** — The `yahtzee-core` extraction requires G-03 (JavaFX decoupling) and G-05 (defensive copy) to be fixed first to guarantee a clean, testable library with no desktop baggage.

