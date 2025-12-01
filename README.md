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
- **Technology**: Java 17+, JavaFX 21, Gradle 8.5, Java modules
- **Features**: Theme switching, sound controller, statistics persistence, keyboard shortcuts, animated dice, score hints
- **Code Quality**: camelCase fields, centralized imports, single-responsibility classes, reusable components

## Setup & Run Instructions

### Prerequisites

- Java Development Kit 17 or newer (JavaFX 21 compatible)
- Git (optional, for cloning)
- No separate Gradle install required (wrapper included)

### Steps

```bash
# 1. Clone the repository
git clone <repository-url>
cd CLI_YAHTZEE/YAHTZEE

# 2. Build the project
./gradlew build      # Windows: .\gradlew build

# 3. Run the application
./gradlew run        # Windows: .\gradlew run

# Optional: create a runnable JAR
./gradlew jar        # Windows: .\gradlew jar
java -jar build/libs/YAHTZEE.jar
```

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
