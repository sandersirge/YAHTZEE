package projekt.yahtzee.ui.handlers;

import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.ui.components.ScoreCellHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility helpers for common UI interactions and animations.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public class UIHelper {
    
    /**
     * Adds basic hover and click animations to a button and wires sound effects.
     *
     * @param button button to decorate
     * @param soundController controller used for playing UI sounds
     */
    public static void attachButtonAnimations(Button button, SoundController soundController) {
        button.setCursor(Cursor.HAND);
        
        // Remember base scale values; default to 1.0 when unset.
        if (button.getScaleX() == 0) button.setScaleX(1.0);
        if (button.getScaleY() == 0) button.setScaleY(1.0);
        
        final double originalScaleX = button.getScaleX();
        final double originalScaleY = button.getScaleY();
        
        // Hover-in animation and sound.
        button.setOnMouseEntered(e -> {
            // Play hover sound.
            if (soundController != null) {
                soundController.playClip(soundController.getButtonHoverSound());
            }
            
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setFromX(originalScaleX);
            st.setFromY(originalScaleY);
            st.setToX(originalScaleX * 1.05);
            st.setToY(originalScaleY * 1.05);
            st.play();
        });
        
        // Hover-out animation restores scale.
        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setFromX(button.getScaleX());
            st.setFromY(button.getScaleY());
            st.setToX(originalScaleX);
            st.setToY(originalScaleY);
            st.play();
        });
        
        // Press animation and click sound.
        button.setOnMousePressed(e -> {
            // Play click sound.
            if (soundController != null) {
                soundController.playClip(soundController.getButtonClickSound());
            }
            
            ScaleTransition st = new ScaleTransition(Duration.millis(50), button);
            st.setFromX(button.getScaleX());
            st.setFromY(button.getScaleY());
            st.setToX(originalScaleX * 0.95);
            st.setToY(originalScaleY * 0.95);
            st.play();
        });
        
        button.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(50), button);
            st.setFromX(button.getScaleX());
            st.setFromY(button.getScaleY());
            st.setToX(originalScaleX * 1.05);
            st.setToY(originalScaleY * 1.05);
            st.play();
        });
    }
    
    /**
     * Adds F11-based fullscreen toggling to the provided scene.
     *
     * @param scene scene to decorate with the shortcut
     * @param stage owning stage
     */
    public static void addFullscreenToggle(Scene scene, Stage stage) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                event.consume();
            }
        });
    }
    
    /**
     * Triggers a combination click from keyboard interaction.
     *
     * @param uiIndex index of the score cell label within the UI column
     * @param playerColumns score columns per player
     * @param playerTurnCounter atomic index tracking the active player
     * @param usedCellsByPlayer usage tracking list (1 when a combo is already set)
     * @param gameController active game controller instance
     */
    public static void triggerComboClick(int uiIndex,
                                         List<List<Label>> playerColumns,
                                         AtomicInteger playerTurnCounter,
                                         List<List<Integer>> usedCellsByPlayer,
                                         GameController gameController) {
        // Ignore non-interactive scoreboard cells.
        if (ScoreCellHandler.isNonInteractiveRow(uiIndex)) {
            return;
        }
        
        // Block clicks before the first roll.
        if (gameController.getCurrentRollCount() == 0) {
            return;
        }
        
        // Skip already used combinations.
        int currentPlayerIdx = playerTurnCounter.get();
        if (currentPlayerIdx < 0 || currentPlayerIdx >= playerColumns.size()) {
            return;
        }
        if (usedCellsByPlayer.get(currentPlayerIdx).get(uiIndex) == 1) {
            return;
        }
        
        List<Label> activePlayerColumn = playerColumns.get(currentPlayerIdx);
        Label scoreCell = activePlayerColumn.get(uiIndex);
        
        // Fire a synthetic mouse click event.
        scoreCell.fireEvent(new MouseEvent(
            MouseEvent.MOUSE_CLICKED,
            0, 0, 0, 0,
            MouseButton.PRIMARY, 1,
            false, false, false, false,
            true, false, false, false, false, false, null
        ));
    }
}
