# YAHTZEE – JavaFX Edition

Modern graphical version of the classic Yahtzee dice game with some meme sound effects, created as the enhanced follow-up to the original CLI coursework project.

## Description

The YAHTZEE project replaces the earlier console application with a JavaFX 21 experience that features a polished interface, rich animations, immersive audio, persistent statistics, and an extensible architecture. The current version supports 1-3 players, theme switching, and keyboard shortcuts, all built on top of a modular MVC-inspired structure.

Key highlights:

- JavaFX-based scenes for main menu, setup, and game board
- Componentized UI (dice panel, score table, dialogs, handlers)
- Soundscape with hover, click, dice roll, and victory effects
- Theme controller for live style changes
- Statistics controller for result tracking
- Optional official extra rules: Yahtzee bonus (+100 per extra Yahtzee) and the Joker rule
- Gradle-managed build with Java modules

## Old Version Specs (CLI Release)

- **Interface**: Text-based console UI
- **Players**: 1-3 (manual score entry)
- **Architecture**: Single large class (~1740 lines) plus combo helpers
- **Technology**: Standard Java (no GUI libraries)
- **Features**: Core Yahtzee rules, manual persistence via text output
- **Limitations**: No animations, no sound, minimal separation of concerns

## New Version Specs (JavaFX Release)

- **Interface**: Full JavaFX GUI with animations and themed styling
- **Players**: 1-3 with automatic score calculations
- **Architecture**: Modular packages (`controller`, `model`, `ui`, `util`), 86-line entry point
- **Technology**: Java 21+, JavaFX 21 (LTS), Gradle 9.9 wrapper (also runs on the newest JDKs), Java modules
- **Features**: Theme switching, sound controller, statistics persistence, keyboard shortcuts, animated dice, score hints, optional Yahtzee bonus and Joker rule
- **Code Quality**: camelCase fields, centralized imports, single-responsibility classes, reusable components

## Setup & Run Instructions

### Prerequisites

- Java Development Kit 21 or newer
- Git (optional, for cloning)
- No separate Gradle install required (wrapper included)

### Steps

```bash
# 1. Clone the repository
git clone <repository-url>
cd YAHTZEE

# 2. Build the project
./gradlew build      # Windows: .\gradlew build

# 3. Run the application
./gradlew run        # Windows: .\gradlew run

# Optional: build a self-contained runtime image (no separate JDK needed to play)
./gradlew jlink      # Windows: .\gradlew jlink
build/jlink-image/bin/app      # Windows: build\jlink-image\bin\app.bat
```

### Extra rules

On the setup screen, **Lisareeglid** (on by default) turns on two official rules:

- **Yahtzee bonus**: every further Yahtzee scores +100 while the Yahtzee box holds 50. The bonus counts towards the lower section.
- **Joker rule**: once the Yahtzee box is filled (with 50 or 0), a further Yahtzee must go into its matching upper box if that is open. Otherwise it goes into any open lower box at full value (Full House 25, Small Straight 30, Large Straight 40). Only when the lower section is full does it scratch an upper box for 0. The status line says where it may go.

### Where the game keeps its files

Settings (`settings.txt`), the results log (`tulemused.txt`) and statistics (`statistics.txt`) are stored per user:

| Platform | Folder |
|----------|--------|
| Windows  | `%APPDATA%\Yahtzee` |
| macOS    | `~/Library/Application Support/Yahtzee` |
| Linux    | `$XDG_DATA_HOME/yahtzee` or `~/.local/share/yahtzee` |

Set `-Dyahtzee.data.dir=...` or the `YAHTZEE_DATA_DIR` environment variable to use another folder (the Docker image uses `/data`). Files from older versions, which were written to the folder the game was started from, are copied over on first start.

## Possible Future Enhancements

- Online multiplayer and lobby system
- Save/load mid-game sessions
- AI-controlled opponents for solo play
- In-app theme editor and background music
- Expanded statistics dashboard with visual analytics
- Internationalization (multi-language support)
- Accessibility improvements (screen reader hints, high contrast mode)
- Automated test suite for game logic and UI flows
- Refurbished gameboard UI
- Refactoring of code

---
**Version**: 2.0 (JavaFX Enhanced)  
**Last Updated**: December 2, 2025
