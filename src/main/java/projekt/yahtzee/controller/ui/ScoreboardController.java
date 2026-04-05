package projekt.yahtzee.controller.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.util.UIFonts;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the scoreboard UI for the Yahtzee game.
 * Handles creation of scoreboard columns, score display, and player highlighting.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class ScoreboardController {
    private final ThemeController themeController;
    private final List<List<Label>> playerColumns;
    private final List<String> playerNames;
    private Label focusedCell;
    private String focusedCellBaseStyle;

    /**
     * Constructs a new scoreboard manager.
     * 
     * @param themeController the theme controller instance
     * @param playerNames list of player names
     */
    public ScoreboardController(ThemeController themeController, List<String> playerNames) {
        this.themeController = themeController;
        this.playerNames = playerNames;
        this.playerColumns = new ArrayList<>();
    }
    
    /**
     * Creates the scoreboard UI with all columns.
     * 
     * @return HBox containing the complete scoreboard
     */
    public HBox createScoreboard() {
        HBox scoreboard = new HBox();
        scoreboard.setPrefWidth(GameConstants.SCOREBOARD_WIDTH);
        scoreboard.setMinHeight(0);
        scoreboard.setMaxHeight(Double.MAX_VALUE);
        scoreboard.setSpacing(0);
        scoreboard.setPadding(new Insets(0));
        scoreboard.setStyle(themeController.getScoreboardStyle());
        HBox.setHgrow(scoreboard, Priority.ALWAYS);
        
        NumberBinding rowHeightBinding = Bindings.divide(scoreboard.heightProperty(), GameConstants.TOTAL_ROWS);

        // Create combo names column
        VBox combos = createComboNamesColumn(rowHeightBinding);
        scoreboard.getChildren().add(combos);
        
        // Create player columns with dynamic width.
        // Width calculation: (total width - combo column width) / player count.
        double playerColumnWidth = (GameConstants.SCOREBOARD_WIDTH - GameConstants.COMBO_COLUMN_WIDTH) / playerNames.size();
        
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            int playerNumber = i + 1; // convert to 1-based index for display
            VBox playerColumn = createPlayerColumn(playerNumber + ". " + playerName, playerColumnWidth, rowHeightBinding);
            HBox.setHgrow(playerColumn, Priority.ALWAYS);
            scoreboard.getChildren().add(playerColumn);
        }
        
        return scoreboard;
    }
    
    /**
     * Creates the column with combination names.
     * 
     * @return VBox with all combo names
     */
    private VBox createComboNamesColumn(NumberBinding rowHeightBinding) {
        VBox combos = new VBox();
        combos.setPrefWidth(GameConstants.COMBO_COLUMN_WIDTH);
        combos.setMinHeight(0);
        combos.setMaxHeight(Double.MAX_VALUE);
        combos.setAlignment(Pos.CENTER);
        combos.setStyle(GameConstants.CELL_BORDER_STYLE + "-fx-background-color: " + themeController.getComboColumnBackground() + ";");
        combos.prefHeightProperty().bind(rowHeightBinding.multiply(GameConstants.TOTAL_ROWS));
        combos.minHeightProperty().bind(combos.prefHeightProperty());
        combos.maxHeightProperty().bind(combos.prefHeightProperty());
        
        List<Label> comboNames = new ArrayList<>();
        comboNames.add(new Label(GameConstants.COMBO_PLAYERS));
        comboNames.add(new Label(GameConstants.COMBO_ONES));
        comboNames.add(new Label(GameConstants.COMBO_TWOS));
        comboNames.add(new Label(GameConstants.COMBO_THREES));
        comboNames.add(new Label(GameConstants.COMBO_FOURS));
        comboNames.add(new Label(GameConstants.COMBO_FIVES));
        comboNames.add(new Label(GameConstants.COMBO_SIXES));
        comboNames.add(new Label(GameConstants.COMBO_BONUS));
        comboNames.add(new Label(GameConstants.COMBO_UPPER_SUM));
        comboNames.add(new Label(GameConstants.COMBO_THREE_KIND));
        comboNames.add(new Label(GameConstants.COMBO_FOUR_KIND));
        comboNames.add(new Label(GameConstants.COMBO_FULL_HOUSE));
        comboNames.add(new Label(GameConstants.COMBO_SMALL_STRAIGHT));
        comboNames.add(new Label(GameConstants.COMBO_LARGE_STRAIGHT));
        comboNames.add(new Label(GameConstants.COMBO_YAHTZEE));
        comboNames.add(new Label(GameConstants.COMBO_CHANCE));
        comboNames.add(new Label(GameConstants.COMBO_LOWER_SUM));
        comboNames.add(new Label(GameConstants.COMBO_TOTAL));
        
        // Style combo name cells.
        for (int i = 0; i < comboNames.size(); i++) {
            Label label = comboNames.get(i);
            label.setFont(UIFonts.getCellFontBold());
            label.setPrefWidth(GameConstants.COMBO_COLUMN_WIDTH);
            label.setAlignment(Pos.CENTER);
            label.setStyle(GameConstants.CELL_BORDER_STYLE + "-fx-background-color: " + themeController.getComboColumnBackground() + ";" + themeController.getLabelTextFill());
            label.setCursor(Cursor.CLOSED_HAND);
            label.prefHeightProperty().bind(rowHeightBinding);
            label.minHeightProperty().bind(rowHeightBinding);
            label.maxHeightProperty().bind(rowHeightBinding);
            
            // Special styling for bonus and total rows.
            if (i == 7) { // bonus row
                label.setStyle(GameConstants.CELL_BORDER_STYLE + themeController.getBonusRowStyle() + themeController.getLabelTextFill());
            } else if (i == 8 || i == 16 || i == 17) { // sum and total rows
                label.setStyle(GameConstants.CELL_BORDER_STYLE + themeController.getTotalRowStyle());
            }
        }
        
        combos.getChildren().addAll(comboNames);
        return combos;
    }
    
    /**
     * Creates a player column for the scoreboard.
     * 
     * @param playerName the name of the player
     * @param columnWidth the width for this column
     * @return VBox containing the player's column
     */
    private VBox createPlayerColumn(String playerName, double columnWidth, NumberBinding rowHeightBinding) {
        VBox playerColumn = new VBox();
        playerColumn.setPrefWidth(columnWidth);
        playerColumn.setMinWidth(columnWidth);
        playerColumn.setMaxWidth(Double.MAX_VALUE);
        playerColumn.setMinHeight(0);
        playerColumn.setMaxHeight(Double.MAX_VALUE);
        playerColumn.setAlignment(Pos.CENTER);
        playerColumn.setStyle(GameConstants.CELL_BORDER_STYLE + "-fx-background-color: " + themeController.getComboColumnBackground() + ";");
        playerColumn.prefHeightProperty().bind(rowHeightBinding.multiply(GameConstants.TOTAL_ROWS));
        playerColumn.minHeightProperty().bind(playerColumn.prefHeightProperty());
        playerColumn.maxHeightProperty().bind(playerColumn.prefHeightProperty());
        
        List<Label> columnCells = new ArrayList<>();
        
        // Player name header.
        Label nameLabel = new Label(playerName);
        nameLabel.setFont(UIFonts.getCellFontBold());
        nameLabel.setMinWidth(columnWidth);
        nameLabel.setPrefWidth(columnWidth);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle(themeController.getPlayerHeaderStyle());
        nameLabel.setCursor(Cursor.CLOSED_HAND);
        nameLabel.prefHeightProperty().bind(rowHeightBinding);
        nameLabel.minHeightProperty().bind(rowHeightBinding);
        nameLabel.maxHeightProperty().bind(rowHeightBinding);
        columnCells.add(nameLabel);
        
        // Create 17 score cells (6 upper + bonus + upper sum + 6 lower + lower sum + total).
        for (int i = 1; i < GameConstants.TOTAL_ROWS; i++) {
            Label cell = new Label("-");
            cell.setFont(new Font(GameConstants.FONT_SIZE_CELL));
            cell.setMinWidth(columnWidth);
            cell.setPrefWidth(columnWidth);
            cell.setMaxWidth(Double.MAX_VALUE);
            cell.setAlignment(Pos.CENTER);
            cell.prefHeightProperty().bind(rowHeightBinding);
            cell.minHeightProperty().bind(rowHeightBinding);
            cell.maxHeightProperty().bind(rowHeightBinding);
            
            // Special styling for bonus and total rows.
            if (i == 7) { // bonus row
                cell.setStyle(GameConstants.CELL_BORDER_STYLE + themeController.getBonusRowStyle() + themeController.getLabelTextFill());
                cell.setCursor(Cursor.CLOSED_HAND);
            } else if (i == 8 || i == 16 || i == 17) { // sum and total rows
                cell.setStyle(GameConstants.CELL_BORDER_STYLE + themeController.getTotalRowStyle());
                cell.setCursor(Cursor.CLOSED_HAND);
            } else {
                // Standard score cell styling.
                cell.setStyle(themeController.getScoreCellDefaultStyle() + themeController.getLabelTextFill());
            }
            
            columnCells.add(cell);
        }
        
        playerColumns.add(columnCells);
        playerColumn.getChildren().addAll(columnCells);
        return playerColumn;
    }
    
    /**
     * Gets all player columns.
     * 
     * @return list of player columns (each column is a list of labels)
     */
    public List<List<Label>> getPlayerColumns() {
        return playerColumns;
    }

    /**
     * Removes the keyboard-focus highlight from the currently focused cell.
     */
    public void clearKeyboardFocus() {
        if (focusedCell != null) {
            if (focusedCellBaseStyle == null || focusedCellBaseStyle.isEmpty()) {
                focusedCell.setStyle(null);
            } else {
                focusedCell.setStyle(focusedCellBaseStyle);
            }
        }
        focusedCell = null;
        focusedCellBaseStyle = null;
    }

    /**
     * Applies the keyboard-focus highlight to a specific scoreboard cell.
     *
     * @param playerIndex column index of the player
     * @param rowIndex    row index within the column
     */
    public void setKeyboardFocus(int playerIndex, int rowIndex) {
        if (playerIndex < 0 || playerIndex >= playerColumns.size()) {
            clearKeyboardFocus();
            return;
        }
        List<Label> column = playerColumns.get(playerIndex);
        if (rowIndex < 0 || rowIndex >= column.size()) {
            clearKeyboardFocus();
            return;
        }

        Label newCell = column.get(rowIndex);
        if (focusedCell == newCell) {
            return;
        }

        clearKeyboardFocus();

        focusedCell = newCell;
        String baseStyle = newCell.getStyle();
        focusedCellBaseStyle = baseStyle == null ? "" : baseStyle;
        newCell.setStyle(focusedCellBaseStyle + themeController.getKeyboardFocusDecoration());
    }

    /**
     * Highlights the active player's column.
     * 
     * @param playerIndex index of the player to highlight
     */
    public void highlightPlayer(int playerIndex) {
        clearKeyboardFocus();
        // Remove highlight from all players.
        for (List<Label> column : playerColumns) {
            column.getFirst().setStyle(themeController.getPlayerHeaderStyle());
        }
        
        // Highlight current player column.
        if (playerIndex >= 0 && playerIndex < playerColumns.size()) {
            playerColumns.get(playerIndex).getFirst().setStyle(themeController.getActivePlayerStyle());
        }
    }
    
    /**
     * Calculates and displays possible scores for the provided dice values.
     *
     * @param gameController the game controller instance
     * @param diceValues current dice values
     * @param playerIndex index of the active player
     * @param usedCombinations list tracking which combinations are already used
     */
    public void displayPossibleScores(GameController gameController, List<Integer> diceValues,
                                      int playerIndex, List<Integer> usedCombinations) {
        if (playerIndex < 0 || playerIndex >= playerColumns.size()) {
            return;
        }
        
        List<Label> playerColumn = playerColumns.get(playerIndex);
        
        // Convert usage flags to primitive array for GameController.
        int[] usedCombos = new int[13];
        
        // Upper section: UI rows 1-6 map to controller indices 0-5.
        for (int i = 0; i < 6; i++) {
            usedCombos[i] = usedCombinations.get(i + 1);
        }
        
        // Lower section: UI rows 9-15 map to controller indices 6-12.
        for (int i = 0; i < 7; i++) {
            usedCombos[i + 6] = usedCombinations.get(i + 9);
        }
        
        // Calculate possible scores.
        String[] scores = gameController.calculatePossibleScores(diceValues, usedCombos);
        
        // Update UI labels with potential scores.
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] != null) {
                int uiIndex = (i < 6) ? i + 1 : i + 3;
                if (uiIndex < playerColumn.size()) {
                    playerColumn.get(uiIndex).setText(scores[i]);
                }
            }
        }
    }
}
