package projekt.yahtzee.ui.components;

import javafx.animation.PauseTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import projekt.yahtzee.controller.data.StatisticsController;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.controller.ui.ScoreboardController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.model.Player;
import projekt.yahtzee.ui.dialogs.GameEndDialog;
import projekt.yahtzee.util.GameConstants;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles scoreboard cell click and hover interactions.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public class ScoreCellHandler {
    private static final int[] NON_INTERACTIVE_ROWS = {0, 7, 8, 16, 17};

    private ScoreCellHandler() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Determines whether the given UI row index is a non-interactive row
     * (header, bonus, upper sum, lower sum, or total).
     *
     * @param uiIndex the row index to check
     * @return {@code true} if the row is non-interactive
     */
    public static boolean isNonInteractiveRow(int uiIndex) {
        for (int row : NON_INTERACTIVE_ROWS) {
            if (row == uiIndex) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wires click handlers for every scoreboard cell and resolves scoring actions.
     *
    * @param playerColumns scoreboard columns per player
    * @param playerUsedCombinations tracking list marking used combinations
    * @param activePlayerIndex index of the active player
    * @param diceValues current dice values
     * @param gameController controller managing game logic
     * @param scoreboardController controller rendering scoreboard updates
     * @param dicePanel dice panel component
     * @param rollCounterLabel roll counter label
     * @param statusLabel status label for user messages
     * @param primaryStage primary stage
     * @param themeController controller providing styles
     * @param soundController controller playing sound effects
     * @param statisticsController controller updating statistics
     * @param onGameEnd callback fired when the game finishes
     */
    public static void setupClickHandlers(
            List<List<Label>> playerColumns,
            List<List<Integer>> playerUsedCombinations,
            AtomicInteger activePlayerIndex,
            List<Integer> diceValues,
            GameController gameController,
            ScoreboardController scoreboardController,
            DicePanel dicePanel,
            Label rollCounterLabel,
            Label statusLabel,
            Stage primaryStage,
            ThemeController themeController,
            SoundController soundController,
            StatisticsController statisticsController,
            Runnable onGameEnd) {
        
        // Attach click handlers to each player header cell.
        for (List<Label> playerColumn : playerColumns) {
            Label nameCell = playerColumn.getFirst();
            nameCell.setOnMouseClicked(e -> {
                // Play either the pan-hit or bonk sound at random.
                soundController.playRandomPlayerNameClickSound();
            });
        }
        
        // Attach click handlers to the scoreboard cells.
        for (int playerIndex = 0; playerIndex < playerColumns.size(); playerIndex++) {
            final List<Label> playerColumn = playerColumns.get(playerIndex);

            for (int cellIndex = 0; cellIndex < playerColumn.size(); cellIndex++) {
                Label scoreCell = playerColumn.get(cellIndex);
                final int uiIndex = cellIndex;
                
                // Skip header and calculated rows, but add vine-boom click handler
                if (isNonInteractiveRow(uiIndex)) {
                    // Add the vine boom sound to non-interactive rows except the header.
                    if (uiIndex != 0) {
                        scoreCell.setOnMouseClicked(e ->
                            soundController.playClip(soundController.getVineBoomSound()));
                    }
                    continue;
                }
                
                scoreCell.setCursor(Cursor.HAND);
                scoreCell.setOnMouseClicked(actionEvent -> {
                    int currentPlayerIdx = activePlayerIndex.get();
                    if (currentPlayerIdx < 0 || currentPlayerIdx >= playerColumns.size()) {
                        return;
                    }
                    List<Label> activePlayerColumn = playerColumns.get(currentPlayerIdx);
                    List<Integer> activePlayerCombos = playerUsedCombinations.get(currentPlayerIdx);
                    
                    // Only allow clicks on active player's cells
                    if (playerColumn != activePlayerColumn) {
                        // Play the "ah hell nah" clip.
                        soundController.playClip(soundController.getInvalidClickSound());
                        return;
                    }
                    
                    // Prevent clicking before first roll
                    if (gameController.getCurrentRollCount() == 0) {
                        // Play the violin screech clip.
                        soundController.playClip(soundController.getViolinScreechSound());
                        statusLabel.setText("Veereta kõigepealt täringuid!");
                        return;
                    }
                    
                    // Calculate combo index
                    int comboIndex;
                    if (uiIndex >= 1 && uiIndex <= 6) {
                        comboIndex = uiIndex - 1;
                    } else if (uiIndex >= 9 && uiIndex <= 15) {
                        comboIndex = uiIndex - 3;
                    } else {
                        return;
                    }
                    
                    // Prevent clicks on already used combinations.
                    if (activePlayerCombos.get(uiIndex) == 1) {
                        // Play the violin screech clip.
                        soundController.playClip(soundController.getViolinScreechSound());
                        return;
                    }
                    
                    Player currentPlayer = gameController.getPlayers().get(currentPlayerIdx);
                    int earnedPoints = gameController.saveScore(comboIndex, diceValues);
                    
                    // Choose an appropriate sound effect based on the score.
                    if (earnedPoints > 0) {
                        boolean isYahtzeeCombo = comboIndex == 11;
                        if (isYahtzeeCombo && earnedPoints == 50) {
                            soundController.playYahtzeeCelebration();
                        } else {
                            soundController.playRandomPositiveScoreSound();
                        }
                    } else {
                        // Zero score: play one of the failure clips at random.
                        soundController.playRandomZeroScoreSound();
                    }
                    
                    activePlayerCombos.set(uiIndex, 1);
                    
                    // Clear preview scores using index-based access to avoid repeated lookups.
                    for (int labelIndex = 0; labelIndex < activePlayerColumn.size(); labelIndex++) {
                        if (isNonInteractiveRow(labelIndex)) {
                            continue;
                        }
                        if (labelIndex != uiIndex && activePlayerCombos.get(labelIndex) == 0) {
                            activePlayerColumn.get(labelIndex).setText(GameConstants.SCORE_PLACEHOLDER);
                        }
                    }
                    
                    scoreCell.setText(earnedPoints == 0 ? GameConstants.SCORE_INVALID : String.valueOf(earnedPoints));
                    
                    // Visual highlight for the selected cell.
                    scoreCell.setStyle(themeController.getScoreCellSelectedStyle());
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(ev -> scoreCell.setStyle(themeController.getScoreCellUsedStyle()));
                    pause.play();
                    
                    // Update sums and totals
                    activePlayerColumn.get(8).setText(String.valueOf(currentPlayer.getUpperSectionScore()));
                    
                    if (currentPlayer.isUpperSectionBonusAwarded() && activePlayerColumn.get(7).getText().equals(GameConstants.SCORE_PLACEHOLDER)) {
                        activePlayerColumn.get(7).setText("35");
                    }
                    
                    activePlayerColumn.get(16).setText(String.valueOf(currentPlayer.getLowerSectionScore()));
                    activePlayerColumn.get(17).setText(String.valueOf(currentPlayer.getTotalScore()));
                    
                    // Check if the game is finished.
                    if (gameController.isGameFinished()) {
                        GameEndDialog endDialog = new GameEndDialog(
                            primaryStage, themeController, soundController, statisticsController, gameController, onGameEnd);
                        endDialog.handleGameEnd(statusLabel);
                        return;
                    }
                    
                    // Move to the next player.
                    gameController.resetRollCount();
                    gameController.nextPlayer();
                    
                    // Increment and wrap around if needed.
                    int nextPlayerIndex = (currentPlayerIdx + 1) % gameController.getPlayers().size();
                    activePlayerIndex.set(nextPlayerIndex);
                    
                    // Highlight the new active player.
                    scoreboardController.highlightPlayer(activePlayerIndex.get());
                    
                    rollCounterLabel.setText("0/3");
                    int playerNum = activePlayerIndex.get() + 1;
                    String playerName = gameController.getPlayers().get(activePlayerIndex.get()).getPlayerName();
                    statusLabel.setText(playerNum + ". mängija - " + playerName + " kord!\n\nVeereta täringuid alustamiseks!");
                    diceValues.clear();
                    dicePanel.updateDiceImages(List.of(1, 1, 1, 1, 1));
                    dicePanel.resetCheckboxes(); // Reset for new player turn
                    dicePanel.setCheckboxesDisabled(true); // Disable until first roll
                    dicePanel.setFocusedDieIndex(-1);
                    scoreboardController.clearKeyboardFocus();
                });
            }
        }
    }
    
    /**
     * Wires hover effects for scoreboard cells to reflect interactivity and state.
     *
    * @param playerColumns scoreboard columns per player
    * @param playerUsedCombinations tracking list marking used combinations
    * @param activePlayerIndex index of the active player
     * @param themeController controller providing styles
     */
    public static void setupHoverEffects(
            List<List<Label>> playerColumns,
            List<List<Integer>> playerUsedCombinations,
            AtomicInteger activePlayerIndex,
            ThemeController themeController) {
        
        for (int playerIndex = 0; playerIndex < playerColumns.size(); playerIndex++) {
            final List<Label> playerColumn = playerColumns.get(playerIndex);
            final List<Integer> columnCombos = playerUsedCombinations.get(playerIndex);

            for (int cellIndex = 0; cellIndex < playerColumn.size(); cellIndex++) {
                Label scoreCell = playerColumn.get(cellIndex);
                final int uiIndex = cellIndex;
                
                // Skip header and calculated rows
                if (isNonInteractiveRow(uiIndex)) {
                    continue;
                }
                
                scoreCell.setOnMouseEntered(e -> {
                    int currentPlayerIdx = activePlayerIndex.get();
                    if (currentPlayerIdx < 0 || currentPlayerIdx >= playerColumns.size()) {
                        return;
                    }
                    List<Label> activePlayerColumn = playerColumns.get(currentPlayerIdx);
                    boolean isActivePlayerColumn = (playerColumn == activePlayerColumn);
                    
                    // All score cells on active player - use same hover style
                    if (isActivePlayerColumn) {
                        // Check if this cell is currently selected (highlighted with border)
                        if (scoreCell.getStyle().contains("border-width: 2")) {
                            scoreCell.setStyle(themeController.getScoreCellSelectedHoverStyle());
                            scoreCell.setCursor(Cursor.CLOSED_HAND);
                        } 
                        // Used cells - show closed hand
                        else if (columnCombos.get(uiIndex) == 1) {
                            scoreCell.setStyle(themeController.getScoreCellHoverStyle());
                            scoreCell.setCursor(Cursor.CLOSED_HAND);
                        }
                        // Unused cells - clickable with hand cursor
                        else {
                            scoreCell.setStyle(themeController.getScoreCellHoverStyle());
                            scoreCell.setCursor(Cursor.HAND);
                        }
                    }
                    // Inactive player's cells or used cells - closed hand cursor
                    else {
                        scoreCell.setCursor(Cursor.CLOSED_HAND);
                    }
                });
                
                scoreCell.setOnMouseExited(e -> {
                    scoreCell.setCursor(Cursor.DEFAULT);
                    
                    // Already used combinations - restore used style
                    if (columnCombos.get(uiIndex) == 1) {
                        scoreCell.setStyle(themeController.getScoreCellUsedStyle());
                    }
                    // Unused combinations - restore default green/light background
                    else {
                        scoreCell.setStyle(themeController.getScoreCellDefaultStyle());
                    }
                });
            }
        }
    }
}

