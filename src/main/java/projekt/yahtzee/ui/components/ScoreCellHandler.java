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
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles scoreboard cell click and hover interactions.
 */
public class ScoreCellHandler {
    
    /**
     * Wires click handlers for every scoreboard cell and resolves scoring actions.
     *
     * @param mängijateVeerud scoreboard columns per player
     * @param kasVeeretatudMängijal tracking list marking used combinations
     * @param mängijaKordadeLoendur index of the active player
     * @param väärtused current dice values
     * @param gameController controller managing game logic
     * @param scoreboardController controller rendering scoreboard updates
     * @param dicePanel dice panel component
     * @param veeretusLoendur roll counter label
     * @param teavitused status label for user messages
     * @param pealava primary stage
     * @param themeController controller providing styles
     * @param soundController controller playing sound effects
     * @param statisticsController controller updating statistics
     * @param onGameEnd callback fired when the game finishes
     */
    public static void setupClickHandlers(
            List<List<Label>> mängijateVeerud,
            List<List<Integer>> kasVeeretatudMängijal,
            AtomicInteger mängijaKordadeLoendur,
            List<Integer> väärtused,
            GameController gameController,
            ScoreboardController scoreboardController,
            DicePanel dicePanel,
            Label veeretusLoendur,
            Label teavitused,
            Stage pealava,
            ThemeController themeController,
            SoundController soundController,
            StatisticsController statisticsController,
            Runnable onGameEnd) {
        
        // Attach click handlers to each player header cell.
        for (List<Label> mängijaVeerg : mängijateVeerud) {
            Label nimiLahter = mängijaVeerg.get(0);
            nimiLahter.setOnMouseClicked(e -> {
                // Play either the pan-hit or bonk sound at random.
                Random random = new Random();
                if (random.nextBoolean()) {
                    if (soundController.getPanHitSound() != null) {
                        soundController.getPanHitSound().stop();
                        soundController.getPanHitSound().play();
                    }
                } else {
                    if (soundController.getBonkSound() != null) {
                        soundController.getBonkSound().stop();
                        soundController.getBonkSound().play();
                    }
                }
            });
        }
        
        // Attach click handlers to the scoreboard cells.
        for (int playerIndex = 0; playerIndex < mängijateVeerud.size(); playerIndex++) {
            final List<Label> mängijaVeerg = mängijateVeerud.get(playerIndex);
            
            for (int cellIndex = 0; cellIndex < mängijaVeerg.size(); cellIndex++) {
                Label punktiLahter = mängijaVeerg.get(cellIndex);
                final int uiIndex = cellIndex;
                
                // Skip header and calculated rows, but add vine-boom click handler
                if (uiIndex == 0 || uiIndex == 7 || uiIndex == 8 || uiIndex == 16 || uiIndex == 17) {
                    // Add the vine boom sound to non-interactive rows except the header.
                    if (uiIndex != 0) {
                        punktiLahter.setOnMouseClicked(e -> {
                            if (soundController.getVineBoomSound() != null) {
                                soundController.getVineBoomSound().stop();
                                soundController.getVineBoomSound().play();
                            }
                        });
                    }
                    continue;
                }
                
                punktiLahter.setCursor(Cursor.HAND);
                punktiLahter.setOnMouseClicked(actionEvent -> {
                    int currentPlayerIdx = mängijaKordadeLoendur.get();
                    if (currentPlayerIdx < 0 || currentPlayerIdx >= mängijateVeerud.size()) {
                        return;
                    }
                    List<Label> aktiivseMängijaVeerg = mängijateVeerud.get(currentPlayerIdx);
                    
                    // Only allow clicks on active player's cells
                    if (mängijaVeerg != aktiivseMängijaVeerg) {
                        // Play the "ah hell nah" clip.
                        if (soundController.getInvalidClickSound() != null) {
                            soundController.getInvalidClickSound().stop();
                            soundController.getInvalidClickSound().play();
                        }
                        return;
                    }
                    
                    // Prevent clicking before first roll
                    if (gameController.getCurrentRollCount() == 0) {
                        // Play the violin screech clip.
                        if (soundController.getViolinScreechSound() != null) {
                            soundController.getViolinScreechSound().stop();
                            soundController.getViolinScreechSound().play();
                        }
                        teavitused.setText("Veereta kõigepealt täringuid!");
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
                    if (kasVeeretatudMängijal.get(mängijaKordadeLoendur.get()).get(uiIndex) == 1) {
                        // Play the violin screech clip.
                        if (soundController.getViolinScreechSound() != null) {
                            soundController.getViolinScreechSound().stop();
                            soundController.getViolinScreechSound().play();
                        }
                        return;
                    }
                    
                    Player currentPlayer = gameController.getPlayers().get(mängijaKordadeLoendur.get());
                    int earnedPoints = gameController.saveScore(comboIndex, väärtused);
                    
                    // Choose an appropriate sound effect based on the score.
                    if (earnedPoints > 0) {
                        // Positive score: play the ding sound.
                        if (soundController.getDingSound() != null) {
                            soundController.getDingSound().stop();
                            soundController.getDingSound().play();
                        }
                    } else {
                        // Zero score: play one of the SpongeBob clips at random.
                        Random random = new Random();
                        if (random.nextBoolean()) {
                            if (soundController.getSpongebobBoowompSound() != null) {
                                soundController.getSpongebobBoowompSound().stop();
                                soundController.getSpongebobBoowompSound().play();
                            }
                        } else {
                            if (soundController.getSpongebobFailSound() != null) {
                                soundController.getSpongebobFailSound().stop();
                                soundController.getSpongebobFailSound().play();
                            }
                        }
                    }
                    
                    kasVeeretatudMängijal.get(mängijaKordadeLoendur.get()).set(uiIndex, 1);
                    
                    // Clear preview scores
                    for (Label label : aktiivseMängijaVeerg) {
                        int labelIndex = aktiivseMängijaVeerg.indexOf(label);
                        if (labelIndex != 0 && labelIndex != 7 && labelIndex != 8 && 
                            labelIndex != 16 && labelIndex != 17) {
                            if (labelIndex != uiIndex && 
                                kasVeeretatudMängijal.get(mängijaKordadeLoendur.get()).get(labelIndex) == 0) {
                                label.setText(GameConstants.SCORE_PLACEHOLDER);
                            }
                        }
                    }
                    
                    punktiLahter.setText(earnedPoints == 0 ? GameConstants.SCORE_INVALID : String.valueOf(earnedPoints));
                    
                    // Visual highlight for the selected cell.
                    punktiLahter.setStyle(themeController.getScoreCellSelectedStyle());
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(ev -> punktiLahter.setStyle(themeController.getScoreCellUsedStyle()));
                    pause.play();
                    
                    // Update sums and totals
                    aktiivseMängijaVeerg.get(8).setText(String.valueOf(currentPlayer.getUpperSectionScore()));
                    
                    if (currentPlayer.isUpperSectionBonusAwarded() && aktiivseMängijaVeerg.get(7).getText().equals(GameConstants.SCORE_PLACEHOLDER)) {
                        aktiivseMängijaVeerg.get(7).setText("35");
                    }
                    
                    aktiivseMängijaVeerg.get(16).setText(String.valueOf(currentPlayer.getLowerSectionScore()));
                    aktiivseMängijaVeerg.get(17).setText(String.valueOf(currentPlayer.getTotalScore()));
                    
                    // Check if the game is finished.
                    if (gameController.isGameFinished()) {
                        GameEndDialog endDialog = new GameEndDialog(
                            pealava, themeController, soundController, statisticsController, gameController, onGameEnd);
                        endDialog.handleGameEnd(teavitused);
                        return;
                    }
                    
                    // Move to the next player.
                    gameController.resetRollCount();
                    gameController.nextPlayer();
                    
                    // Increment and wrap around if needed.
                    int nextPlayerIndex = (mängijaKordadeLoendur.get() + 1) % gameController.getPlayers().size();
                    mängijaKordadeLoendur.set(nextPlayerIndex);
                    
                    // Highlight the new active player.
                    scoreboardController.highlightPlayer(mängijaKordadeLoendur.get());
                    
                    veeretusLoendur.setText("0/3");
                    int playerNum = mängijaKordadeLoendur.get() + 1;
                    String playerName = gameController.getPlayers().get(mängijaKordadeLoendur.get()).getPlayerName();
                    teavitused.setText(playerNum + ". mängija - " + playerName + " kord!\n\nVeereta täringuid alustamiseks!");
                    väärtused.clear();
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
     * @param mängijateVeerud scoreboard columns per player
     * @param kasVeeretatudMängijal tracking list marking used combinations
     * @param mängijaKordadeLoendur index of the active player
     * @param themeController controller providing styles
     */
    public static void setupHoverEffects(
            List<List<Label>> mängijateVeerud,
            List<List<Integer>> kasVeeretatudMängijal,
            AtomicInteger mängijaKordadeLoendur,
            ThemeController themeController) {
        
        for (int playerIndex = 0; playerIndex < mängijateVeerud.size(); playerIndex++) {
            final List<Label> mängijaVeerg = mängijateVeerud.get(playerIndex);
            
            for (int cellIndex = 0; cellIndex < mängijaVeerg.size(); cellIndex++) {
                Label punktiLahter = mängijaVeerg.get(cellIndex);
                final int uiIndex = cellIndex;
                
                // Skip header and calculated rows
                if (uiIndex == 0 || uiIndex == 7 || uiIndex == 8 || uiIndex == 16 || uiIndex == 17) {
                    continue;
                }
                
                punktiLahter.setOnMouseEntered(e -> {
                    int currentPlayerIdx = mängijaKordadeLoendur.get();
                    if (currentPlayerIdx < 0 || currentPlayerIdx >= mängijateVeerud.size()) {
                        return;
                    }
                    List<Label> aktiivseMängijaVeerg = mängijateVeerud.get(currentPlayerIdx);
                    boolean onAktiivseMängijaVeerg = (mängijaVeerg == aktiivseMängijaVeerg);
                    int currentVeergIndex = mängijateVeerud.indexOf(mängijaVeerg);
                    
                    // All score cells on active player - use same hover style
                    if (onAktiivseMängijaVeerg) {
                        // Check if this cell is currently selected (highlighted with border)
                        if (punktiLahter.getStyle().contains("border-width: 2")) {
                            punktiLahter.setStyle(themeController.getScoreCellSelectedHoverStyle());
                            punktiLahter.setCursor(Cursor.CLOSED_HAND);
                        } 
                        // Used cells - show closed hand
                        else if (kasVeeretatudMängijal.get(currentVeergIndex).get(uiIndex) == 1) {
                            punktiLahter.setStyle(themeController.getScoreCellHoverStyle());
                            punktiLahter.setCursor(Cursor.CLOSED_HAND);
                        }
                        // Unused cells - clickable with hand cursor
                        else {
                            punktiLahter.setStyle(themeController.getScoreCellHoverStyle());
                            punktiLahter.setCursor(Cursor.HAND);
                        }
                    }
                    // Inactive player's cells or used cells - closed hand cursor
                    else {
                        punktiLahter.setCursor(Cursor.CLOSED_HAND);
                    }
                });
                
                punktiLahter.setOnMouseExited(e -> {
                    int currentVeergIndex = mängijateVeerud.indexOf(mängijaVeerg);
                    punktiLahter.setCursor(Cursor.DEFAULT);
                    
                    // Already used combinations - restore used style
                    if (kasVeeretatudMängijal.get(currentVeergIndex).get(uiIndex) == 1) {
                        punktiLahter.setStyle(themeController.getScoreCellUsedStyle());
                    }
                    // Unused combinations - restore default green/light background
                    else if (kasVeeretatudMängijal.get(currentVeergIndex).get(uiIndex) == 0) {
                        punktiLahter.setStyle(themeController.getScoreCellDefaultStyle());
                    }
                });
            }
        }
    }
}
