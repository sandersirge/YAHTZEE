package projekt.yahtzee.ui.handlers;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.controller.ui.ScoreboardController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.components.DicePanel;
import projekt.yahtzee.ui.dialogs.ExitDialog;
import projekt.yahtzee.ui.dialogs.HelpDialog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles global keyboard interactions while the game board scene is active.
 */
public class KeyboardHandler {
    private static final int[] INTERACTIVE_SCORE_ROWS = {1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15};
    private static final List<String> HELP_SHORTCUT_LINES = List.of(
        "SPACE - roll dice",
        "SHIFT+SPACE or ENTER - toggle focused die",
        "A/S/D/F/G - toggle dice 1-5",
        "TAB - switch between dice and scoreboard",
        "Arrow keys - move focus (dice or scoreboard)",
        "ENTER - confirm highlighted score",
        "1-6 - score Ones through Sixes",
        "7 - score Three of a Kind",
        "8 - score Four of a Kind",
        "9 - score Full House",
        "0 - score Small Straight",
        "- (Minus) - score Large Straight",
        "= (Plus) - score Yahtzee",
        "BACKSPACE - score Chance",
        "H - show or hide this help",
        "ESC - open the exit dialog"
    );

    public static List<String> getHelpShortcutLines() {
        return HELP_SHORTCUT_LINES;
    }

    private static final Map<KeyCode, Integer> SCORE_SHORTCUTS = Map.ofEntries(
        Map.entry(KeyCode.DIGIT1, 1),
        Map.entry(KeyCode.NUMPAD1, 1),
        Map.entry(KeyCode.DIGIT2, 2),
        Map.entry(KeyCode.NUMPAD2, 2),
        Map.entry(KeyCode.DIGIT3, 3),
        Map.entry(KeyCode.NUMPAD3, 3),
        Map.entry(KeyCode.DIGIT4, 4),
        Map.entry(KeyCode.NUMPAD4, 4),
        Map.entry(KeyCode.DIGIT5, 5),
        Map.entry(KeyCode.NUMPAD5, 5),
        Map.entry(KeyCode.DIGIT6, 6),
        Map.entry(KeyCode.NUMPAD6, 6),
        Map.entry(KeyCode.DIGIT7, 9),
        Map.entry(KeyCode.NUMPAD7, 9),
        Map.entry(KeyCode.DIGIT8, 10),
        Map.entry(KeyCode.NUMPAD8, 10),
        Map.entry(KeyCode.DIGIT9, 11),
        Map.entry(KeyCode.NUMPAD9, 11),
        Map.entry(KeyCode.DIGIT0, 12),
        Map.entry(KeyCode.NUMPAD0, 12),
        Map.entry(KeyCode.MINUS, 13),
        Map.entry(KeyCode.SUBTRACT, 13),
        Map.entry(KeyCode.EQUALS, 14),
        Map.entry(KeyCode.ADD, 14),
        Map.entry(KeyCode.BACK_SPACE, 15)
    );

    private static final AtomicReference<Stage> HELP_DIALOG_STAGE = new AtomicReference<>();
    private static final AtomicReference<Stage> EXIT_DIALOG_STAGE = new AtomicReference<>();

    /**
     * Wires keyboard controls for the in-game scene.
     *
     * @param scene target scene
     * @param rollButton button used for rolling dice
     * @param exitButton button opening the exit dialog
     * @param dicePanel dice panel instance
     * @param scoreboardController scoreboard controller for focus highlights
     * @param statusLabel status label for contextual messages
     * @param playerColumns scoreboard columns per player
     * @param playerTurnCounter active player index tracker
     * @param usedCellsByPlayer list tracking picked combinations
     * @param gameController core game controller
     * @param ownerStage owner stage for dialogs
     * @param themeController theme controller for styling
     * @param soundController sound controller for button feedback
     */
    public static void setupKeyboardControls(
            Scene scene,
            Button rollButton,
            Button exitButton,
            DicePanel dicePanel,
            ScoreboardController scoreboardController,
            Label statusLabel,
            List<List<Label>> playerColumns,
            AtomicInteger playerTurnCounter,
            List<List<Integer>> usedCellsByPlayer,
            GameController gameController,
            Stage ownerStage,
            ThemeController themeController,
            SoundController soundController) {

        AtomicBoolean scoreboardFocusActive = new AtomicBoolean(false);
        AtomicInteger focusedScoreRow = new AtomicInteger(-1);
        AtomicInteger lastDiceFocusIndex = new AtomicInteger(-1);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            int activePlayerIndex = playerTurnCounter.get();

            if (gameController.getCurrentRollCount() == 0 && scoreboardFocusActive.get()) {
                scoreboardFocusActive.set(false);
                focusedScoreRow.set(-1);
                lastDiceFocusIndex.set(-1);
            }

            if (dicePanel.areCheckboxesDisabled() && dicePanel.getFocusedDieIndex() != -1) {
                dicePanel.setFocusedDieIndex(-1);
            }

            KeyCode code = event.getCode();
            Integer shortcutTarget = SCORE_SHORTCUTS.get(code);
            if (shortcutTarget != null) {
                processScoreShortcut(shortcutTarget, scoreboardFocusActive, focusedScoreRow, playerColumns,
                    playerTurnCounter, usedCellsByPlayer, gameController, scoreboardController,
                    lastDiceFocusIndex, dicePanel, event);
                return;
            }
            if (handleScoreboardKey(event, code, scoreboardFocusActive, focusedScoreRow, playerColumns,
                playerTurnCounter, usedCellsByPlayer, gameController, scoreboardController,
                dicePanel, lastDiceFocusIndex, activePlayerIndex)) {
                return;
            }

            if (handleDiceKey(event, code, dicePanel, rollButton, scoreboardFocusActive)) {
                return;
            }

            switch (code) {
                case ESCAPE:
                    toggleExitDialog(ownerStage, themeController, soundController, () -> {
                        if (exitButton != null) {
                            exitButton.fire();
                        }
                    });
                    event.consume();
                    break;

                case H:
                    toggleHelpDialog(ownerStage, themeController, soundController);
                    event.consume();
                    break;

                case A:
                    toggleSpecificDie(dicePanel, 0);
                    event.consume();
                    break;

                case S:
                    toggleSpecificDie(dicePanel, 1);
                    event.consume();
                    break;

                case D:
                    toggleSpecificDie(dicePanel, 2);
                    event.consume();
                    break;

                case F:
                    toggleSpecificDie(dicePanel, 3);
                    event.consume();
                    break;

                case G:
                    toggleSpecificDie(dicePanel, 4);
                    event.consume();
                    break;

                default:
                    break;
            }
        });
    }

    public static void toggleHelpDialog(Stage ownerStage,
                                        ThemeController themeController,
                                        SoundController soundController) {
        Stage existing = HELP_DIALOG_STAGE.get();
        if (existing != null && existing.isShowing()) {
            existing.close();
            HELP_DIALOG_STAGE.compareAndSet(existing, null);
            return;
        }
        if (ownerStage == null || themeController == null || soundController == null) return;
        Stage newHelp = HelpDialog.show(ownerStage, themeController, soundController, HELP_SHORTCUT_LINES);
        HELP_DIALOG_STAGE.set(newHelp);
        newHelp.setOnHidden(e -> HELP_DIALOG_STAGE.compareAndSet(newHelp, null));
    }

    public static void toggleExitDialog(Stage ownerStage,
                                        ThemeController themeController,
                                        SoundController soundController,
                                        Runnable onExit) {
        Stage existing = EXIT_DIALOG_STAGE.get();
        if (existing != null && existing.isShowing()) {
            existing.close();
            EXIT_DIALOG_STAGE.compareAndSet(existing, null);
            return;
        }
        if (ownerStage == null || themeController == null || soundController == null) return;
        Stage dialog = ExitDialog.show(ownerStage, themeController, soundController, onExit);
        EXIT_DIALOG_STAGE.set(dialog);
        dialog.setOnHidden(e -> EXIT_DIALOG_STAGE.compareAndSet(dialog, null));
    }

    private static boolean handleScoreboardKey(KeyEvent event,
                                               KeyCode code,
                                               AtomicBoolean scoreboardFocusActive,
                                               AtomicInteger focusedScoreRow,
                                               List<List<Label>> playerColumns,
                                               AtomicInteger playerTurnCounter,
                                               List<List<Integer>> usedCellsByPlayer,
                                               GameController gameController,
                                               ScoreboardController scoreboardController,
                                               DicePanel dicePanel,
                                               AtomicInteger lastDiceFocusIndex,
                                               int activePlayerIndex) {
        switch (code) {
            case TAB:
                if (scoreboardFocusActive.get()) {
                    scoreboardController.clearKeyboardFocus();
                    scoreboardFocusActive.set(false);
                    focusedScoreRow.set(-1);
                    restoreDiceFocus(dicePanel, lastDiceFocusIndex);
                } else if (gameController.getCurrentRollCount() > 0) {
                    rememberDiceFocus(dicePanel, lastDiceFocusIndex);
                    int initializedRow = initializeScoreFocus(focusedScoreRow, scoreboardController,
                        playerTurnCounter, usedCellsByPlayer);
                    if (initializedRow != -1) {
                        scoreboardFocusActive.set(true);
                    } else {
                        restoreDiceFocus(dicePanel, lastDiceFocusIndex);
                    }
                }
                event.consume();
                return true;

            case ENTER:
                if (scoreboardFocusActive.get() && focusedScoreRow.get() != -1) {
                    if (triggerScoreSelection(focusedScoreRow.get(), playerColumns, playerTurnCounter,
                            usedCellsByPlayer, gameController, scoreboardController)) {
                        scoreboardFocusActive.set(false);
                        focusedScoreRow.set(-1);
                    }
                    event.consume();
                    return true;
                }
                return false;

            case UP:
                if (scoreboardFocusActive.get()) {
                    int upRow = getNextAvailableRow(focusedScoreRow.get(), -1, activePlayerIndex, usedCellsByPlayer);
                    if (upRow != -1 && upRow != focusedScoreRow.get()) {
                        focusedScoreRow.set(upRow);
                        scoreboardController.setKeyboardFocus(activePlayerIndex, upRow);
                    }
                    event.consume();
                    return true;
                }
                return false;

            case DOWN:
                if (scoreboardFocusActive.get()) {
                    int downRow = getNextAvailableRow(focusedScoreRow.get(), 1, activePlayerIndex, usedCellsByPlayer);
                    if (downRow != -1 && downRow != focusedScoreRow.get()) {
                        focusedScoreRow.set(downRow);
                        scoreboardController.setKeyboardFocus(activePlayerIndex, downRow);
                    }
                    event.consume();
                    return true;
                }
                return false;

            default:
                return false;
        }
    }

    private static boolean handleDiceKey(KeyEvent event,
                                         KeyCode code,
                                         DicePanel dicePanel,
                                         Button rollButton,
                                         AtomicBoolean scoreboardFocusActive) {
        switch (code) {
            case SPACE:
                if (event.isShiftDown() && !dicePanel.areCheckboxesDisabled()) {
                    toggleCurrentDie(dicePanel);
                    event.consume();
                    return true;
                } else if (!rollButton.isDisabled()) {
                    rollButton.fire();
                    event.consume();
                    return true;
                }
                return false;

            case ENTER:
                if (!scoreboardFocusActive.get() && !dicePanel.areCheckboxesDisabled()) {
                    toggleCurrentDie(dicePanel);
                    event.consume();
                    return true;
                }
                return false;

            case LEFT:
                if (!scoreboardFocusActive.get()) {
                    moveDiceFocus(dicePanel, -1);
                    event.consume();
                    return true;
                }
                return false;

            case RIGHT:
                if (!scoreboardFocusActive.get()) {
                    moveDiceFocus(dicePanel, 1);
                    event.consume();
                    return true;
                }
                return false;

            default:
                return false;
        }
    }

    private static void processScoreShortcut(int uiIndex,
                                             AtomicBoolean scoreboardFocusActive,
                                             AtomicInteger focusedScoreRow,
                                             List<List<Label>> playerColumns,
                                             AtomicInteger playerTurnCounter,
                                             List<List<Integer>> usedCellsByPlayer,
                                             GameController gameController,
                                             ScoreboardController scoreboardController,
                                             AtomicInteger lastDiceFocusIndex,
                                             DicePanel dicePanel,
                                             KeyEvent event) {
        if (commitScoreFromShortcut(uiIndex, playerColumns, playerTurnCounter, usedCellsByPlayer,
            gameController, scoreboardController)) {
            scoreboardFocusActive.set(false);
            focusedScoreRow.set(-1);
            lastDiceFocusIndex.set(-1);
            dicePanel.setFocusedDieIndex(-1);
        }
        event.consume();
    }

    private static void rememberDiceFocus(DicePanel dicePanel, AtomicInteger lastFocusIndex) {
        if (dicePanel.areCheckboxesDisabled()) {
            lastFocusIndex.set(-1);
            dicePanel.setFocusedDieIndex(-1);
            return;
        }
        int current = dicePanel.getFocusedDieIndex();
        if (current >= 0) {
            lastFocusIndex.set(current);
            dicePanel.setFocusedDieIndex(-1);
        } else if (lastFocusIndex.get() != -1) {
            lastFocusIndex.set(-1);
        }
    }

    private static void restoreDiceFocus(DicePanel dicePanel, AtomicInteger lastFocusIndex) {
        if (dicePanel.areCheckboxesDisabled()) {
            dicePanel.setFocusedDieIndex(-1);
            lastFocusIndex.set(-1);
            return;
        }
        int totalDice = dicePanel.getKeepStatus().length;
        if (totalDice == 0) {
            lastFocusIndex.set(-1);
            return;
        }
        int stored = lastFocusIndex.getAndSet(-1);
        if (stored >= 0 && stored < totalDice) {
            dicePanel.setFocusedDieIndex(stored);
        } else if (dicePanel.getFocusedDieIndex() == -1) {
            dicePanel.setFocusedDieIndex(0);
        }
    }

    private static void toggleCurrentDie(DicePanel dicePanel) {
        if (dicePanel.areCheckboxesDisabled()) {
            return;
        }
        int totalDice = dicePanel.getKeepStatus().length;
        if (totalDice == 0) {
            return;
        }
        int current = dicePanel.getFocusedDieIndex();
        if (current < 0) {
            current = 0;
            dicePanel.setFocusedDieIndex(current);
        }
        dicePanel.toggleKeep(current);
    }

    private static void toggleSpecificDie(DicePanel dicePanel, int dieIndex) {
        if (dicePanel.areCheckboxesDisabled()) {
            return;
        }
        int totalDice = dicePanel.getKeepStatus().length;
        if (dieIndex < 0 || dieIndex >= totalDice) {
            return;
        }
        dicePanel.setFocusedDieIndex(dieIndex);
        dicePanel.toggleKeep(dieIndex);
    }

    private static void moveDiceFocus(DicePanel dicePanel, int direction) {
        if (dicePanel.areCheckboxesDisabled()) {
            return;
        }
        int totalDice = dicePanel.getKeepStatus().length;
        if (totalDice == 0) {
            return;
        }
        int current = dicePanel.getFocusedDieIndex();
        if (current < 0) {
            current = direction > 0 ? 0 : totalDice - 1;
        } else {
            current = (current + direction + totalDice) % totalDice;
        }
        dicePanel.setFocusedDieIndex(current);
    }

    private static int initializeScoreFocus(AtomicInteger focusedScoreRow,
                                            ScoreboardController scoreboardController,
                                            AtomicInteger playerTurnCounter,
                                            List<List<Integer>> usedCellsByPlayer) {
        int playerIndex = playerTurnCounter.get();
        int firstRow = findFirstAvailableRow(playerIndex, usedCellsByPlayer);
        if (firstRow != -1) {
            scoreboardController.setKeyboardFocus(playerIndex, firstRow);
            focusedScoreRow.set(firstRow);
        } else {
            scoreboardController.clearKeyboardFocus();
            focusedScoreRow.set(-1);
        }
        return firstRow;
    }

    private static int getNextAvailableRow(int currentRow,
                                           int direction,
                                           int playerIndex,
                                           List<List<Integer>> usedCellsByPlayer) {
        if (playerIndex < 0 || playerIndex >= usedCellsByPlayer.size()) {
            return -1;
        }
        List<Integer> usage = usedCellsByPlayer.get(playerIndex);
        int startIndex = indexOfRow(currentRow);
        if (startIndex == -1) {
            return findFirstAvailableRow(playerIndex, usedCellsByPlayer);
        }
        int length = INTERACTIVE_SCORE_ROWS.length;
        for (int step = 1; step <= length; step++) {
            int candidateIndex = (startIndex + (direction * step) + length) % length;
            int row = INTERACTIVE_SCORE_ROWS[candidateIndex];
            if (row < usage.size() && usage.get(row) == 0) {
                return row;
            }
        }
        return currentRow;
    }

    private static int indexOfRow(int row) {
        for (int i = 0; i < INTERACTIVE_SCORE_ROWS.length; i++) {
            if (INTERACTIVE_SCORE_ROWS[i] == row) {
                return i;
            }
        }
        return -1;
    }

    private static int findFirstAvailableRow(int playerIndex, List<List<Integer>> usedCellsByPlayer) {
        if (playerIndex < 0 || playerIndex >= usedCellsByPlayer.size()) {
            return -1;
        }
        List<Integer> usage = usedCellsByPlayer.get(playerIndex);
        for (int row : INTERACTIVE_SCORE_ROWS) {
            if (row < usage.size() && usage.get(row) == 0) {
                return row;
            }
        }
        return -1;
    }

    private static boolean triggerScoreSelection(int uiIndex,
                                                 List<List<Label>> playerColumns,
                                                 AtomicInteger playerTurnCounter,
                                                 List<List<Integer>> usedCellsByPlayer,
                                                 GameController gameController,
                                                 ScoreboardController scoreboardController) {
        if (gameController.getCurrentRollCount() == 0) {
            return false;
        }
        int playerIndex = playerTurnCounter.get();
        if (!isRowSelectable(uiIndex, playerIndex, usedCellsByPlayer)) {
            return false;
        }
        UIHelper.triggerComboClick(uiIndex, playerColumns, playerTurnCounter, usedCellsByPlayer, gameController);
        scoreboardController.clearKeyboardFocus();
        return true;
    }

    private static boolean commitScoreFromShortcut(int uiIndex,
                                                   List<List<Label>> playerColumns,
                                                   AtomicInteger playerTurnCounter,
                                                   List<List<Integer>> usedCellsByPlayer,
                                                   GameController gameController,
                                                   ScoreboardController scoreboardController) {
        return triggerScoreSelection(uiIndex, playerColumns, playerTurnCounter, usedCellsByPlayer, gameController,
            scoreboardController);
    }

    private static boolean isRowSelectable(int uiIndex,
                                           int playerIndex,
                                           List<List<Integer>> usedCellsByPlayer) {
        if (playerIndex < 0 || playerIndex >= usedCellsByPlayer.size()) {
            return false;
        }
        List<Integer> usage = usedCellsByPlayer.get(playerIndex);
        if (uiIndex < 0 || uiIndex >= usage.size()) {
            return false;
        }
        if (!isInteractiveRow(uiIndex)) {
            return false;
        }
        return usage.get(uiIndex) == 0;
    }

    private static boolean isInteractiveRow(int rowIndex) {
        for (int row : INTERACTIVE_SCORE_ROWS) {
            if (row == rowIndex) {
                return true;
            }
        }
        return false;
    }
}

