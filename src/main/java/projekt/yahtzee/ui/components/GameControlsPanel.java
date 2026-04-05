package projekt.yahtzee.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.controller.ui.ScoreboardController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.handlers.KeyboardHandler;
import projekt.yahtzee.ui.handlers.UIHelper;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.util.UIFonts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Encapsulates the construction of the left-hand dice controls section on the game board.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public final class GameControlsPanel {
    private final VBox container;
    private final DicePanel dicePanel;
    private final Button rollButton;
    private final Button exitButton;
    private final Label rollCounterLabel;
    private final Label statusLabel;

    private GameControlsPanel(VBox container,
                                DicePanel dicePanel,
                                Button rollButton,
                                Button exitButton,
                                Label rollCounterLabel,
                                Label statusLabel) {
        this.container = container;
        this.dicePanel = dicePanel;
        this.rollButton = rollButton;
        this.exitButton = exitButton;
        this.rollCounterLabel = rollCounterLabel;
        this.statusLabel = statusLabel;
    }

    /**
     * Factory method that builds the complete dice controls panel.
     *
     * @param gameController         controller managing game logic
     * @param themeController        controller providing theme styles
     * @param soundController        controller playing sound effects
     * @param scoreboardController   controller rendering scoreboard updates
     * @param playerColumns          scoreboard columns per player
     * @param playerUsedCombinations tracking list for used combinations
     * @param diceValues             current dice values list
     * @param playerTurnCounter      atomic index tracking the active player
     * @param primaryStage           primary stage reference
     * @param onGameEnd              callback fired when the game ends
     * @return a fully configured {@code GameControlsPanel}
     */
    public static GameControlsPanel create(GameController gameController,
                                             ThemeController themeController,
                                             SoundController soundController,
                                             ScoreboardController scoreboardController,
                                             List<List<Label>> playerColumns,
                                             List<List<Integer>> playerUsedCombinations,
                                             List<Integer> diceValues,
                                             AtomicInteger playerTurnCounter,
                                             Stage primaryStage,
                                             Runnable onGameEnd) {
        VBox dicePanelContainer = new VBox();
        dicePanelContainer.setPrefWidth(GameConstants.DICE_PANEL_WIDTH);
        dicePanelContainer.setMinHeight(0);
        dicePanelContainer.setPrefHeight(GameConstants.MAIN_WINDOW_HEIGHT);
        dicePanelContainer.setMaxHeight(Double.MAX_VALUE);
        dicePanelContainer.setSpacing(8);
        dicePanelContainer.setPadding(new Insets(12, 16, 16, 16));
        dicePanelContainer.setStyle(
            "-fx-background-color: " + themeController.getPanelBackgroundColor() + "; " +
            "-fx-background-radius: 14; -fx-border-radius: 14;"
        );

        VBox upperSection = new VBox();
        upperSection.setAlignment(Pos.TOP_CENTER);
        upperSection.setPrefWidth(GameConstants.DICE_PANEL_WIDTH);
        upperSection.setSpacing(GameConstants.SPACING_SMALL);
        upperSection.setPadding(new Insets(10, 20, 10, 20));
        upperSection.setStyle(themeController.getDicePanelStyle() + " -fx-background-color: " + themeController.getTitleBoxBackground() + "; -fx-background-radius: 12; -fx-border-radius: 12;");
        VBox.setVgrow(upperSection, Priority.ALWAYS);

        VBox lowerSection = new VBox();
        lowerSection.setAlignment(Pos.CENTER);
        lowerSection.setPrefWidth(GameConstants.DICE_PANEL_WIDTH);
        lowerSection.setSpacing(GameConstants.SPACING_MEDIUM);
        lowerSection.setPadding(new Insets(10, 20, 20, 20));
        lowerSection.setStyle(themeController.getDicePanelStyle() + " -fx-background-color: " + themeController.getTitleBoxBackground() + "; -fx-background-radius: 12; -fx-border-radius: 12;");
        VBox.setVgrow(lowerSection, Priority.ALWAYS);

        dicePanelContainer.getChildren().addAll(upperSection, lowerSection);

        Label titleLabel = new Label(GameConstants.TITLE_YAHTZEE.toUpperCase());
        titleLabel.setFont(Font.font(GameConstants.FONT_FAMILY, FontWeight.BOLD, GameConstants.FONT_SIZE_TITLE));
        titleLabel.setStyle(themeController.getLabelTextFill());
        StackPane titleCard = new StackPane(titleLabel);
        titleCard.setPadding(new Insets(12, 16, 12, 16));
        titleCard.setStyle("-fx-background-color: " + themeController.getTitleBoxBackground() + "; -fx-background-radius: 12; -fx-border-radius: 12;");

        DicePanel dicePanel = new DicePanel(themeController);
        VBox diceBoxContainer = dicePanel.createDicePanel();
        dicePanel.setCheckboxesDisabled(true);

        upperSection.getChildren().addAll(titleCard, diceBoxContainer);

        Label rollCounterLabel = new Label("Veeretusi tehtud: 0/3");
        rollCounterLabel.setFont(Font.font(GameConstants.FONT_FAMILY, FontWeight.BOLD, 36));
        rollCounterLabel.setTextFill(Color.web(themeController.getRollCounterColor()));

        String initialMessage = GameConstants.FIRST_PLAYER_TURN + "\n\nVeereta täringuid alustamiseks!";
        if (!gameController.getPlayers().isEmpty()) {
            String firstPlayerName = gameController.getPlayers().getFirst().getPlayerName();
            initialMessage = "1. mängija - " + firstPlayerName + " kord!\n\nVeereta täringuid alustamiseks!";
        }
        Label statusLabel = new Label(initialMessage);
        statusLabel.setFont(Font.font(GameConstants.FONT_FAMILY, FontWeight.BOLD, GameConstants.FONT_SIZE_NORMAL));
        statusLabel.setTextAlignment(TextAlignment.CENTER);
        statusLabel.setStyle(themeController.getLabelTextFill());
        StackPane statusCard = new StackPane(statusLabel);
        statusCard.setPadding(new Insets(12, 18, 12, 18));
        statusCard.setStyle("-fx-background-color: " + themeController.getTitleBoxBackground() + "; -fx-background-radius: 12; -fx-border-radius: 12;");

        Button rollButton = createRollButton(gameController,
            themeController,
            soundController,
            scoreboardController,
            playerColumns,
            playerUsedCombinations,
            diceValues,
            playerTurnCounter,
            dicePanel,
            rollCounterLabel,
            statusLabel);

        Button exitButton = createExitButton(themeController, soundController, primaryStage, onGameEnd);

        Button helpButton = new Button("?");
        helpButton.setPrefSize(46, 46);
        helpButton.setMinSize(46, 46);
        helpButton.setMaxSize(46, 46);
        helpButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 22; -fx-font-weight: bold; -fx-background-radius: 23; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 6, 0, 0, 2);");
        helpButton.setCursor(Cursor.HAND);
        UIHelper.attachButtonAnimations(helpButton, soundController);
        helpButton.setOnAction(e -> KeyboardHandler.toggleHelpDialog(primaryStage, themeController, soundController));

        Label helpLabel = new Label("Vajuta 'h' juhiste kuvamiseks");
        helpLabel.setFont(UIFonts.getCellFontBold());
        helpLabel.setStyle(themeController.getLabelTextFill());

        HBox helpRow = new HBox();
        helpRow.setAlignment(Pos.CENTER_LEFT);
        helpRow.setSpacing(12);
        helpRow.setPadding(new Insets(0, 0, 10, 0));
        StackPane rollCounterCard = new StackPane(rollCounterLabel);
        rollCounterCard.setPadding(new Insets(10, 16, 10, 16));
        rollCounterCard.setStyle("-fx-background-color: " + themeController.getTitleBoxBackground() + "; -fx-background-radius: 12; -fx-border-radius: 12;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        helpRow.getChildren().addAll(rollCounterCard, spacer, helpLabel, helpButton);

        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setSpacing(GameConstants.SPACING_BUTTON_ROW_WIDE);
        buttonRow.getChildren().addAll(rollButton, exitButton);
        lowerSection.getChildren().addAll(helpRow, statusCard, buttonRow);

        return new GameControlsPanel(dicePanelContainer, dicePanel, rollButton, exitButton, rollCounterLabel, statusLabel);
    }

    private static Button createRollButton(GameController gameController,
                                           ThemeController themeController,
                                           SoundController soundController,
                                           ScoreboardController scoreboardController,
                                           List<List<Label>> playerColumns,
                                           List<List<Integer>> playerUsedCombinations,
                                           List<Integer> diceValues,
                                           AtomicInteger playerTurnCounter,
                                           DicePanel dicePanel,
                                           Label rollCounterLabel,
                                           Label statusLabel) {
        Button rollButton = new Button(GameConstants.BUTTON_ROLL_DICE);
        rollButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        rollButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        rollButton.setStyle(themeController.getButtonStyle());
        rollButton.setCursor(Cursor.HAND);
        UIHelper.attachButtonAnimations(rollButton, soundController);

        rollButton.setOnAction(actionEvent -> {
            int rollCount = gameController.getCurrentRollCount();

            if (rollCount >= GameConstants.MAX_ROLLS_PER_TURN) {
                statusLabel.setText(GameConstants.MSG_NO_MORE_ROLLS);
                return;
            }

            int[] kept = dicePanel.getKeepStatus();
            List<Integer> preRollValues = gameController.getDiceValues();
            Map<Integer, Integer> keptValueCounts = new HashMap<>();
            for (int i = 0; i < kept.length && i < preRollValues.size(); i++) {
                if (kept[i] == 1) {
                    int val = preRollValues.get(i);
                    keptValueCounts.put(val, keptValueCounts.getOrDefault(val, 0) + 1);
                }
            }

            rollButton.setDisable(true);

            soundController.playClip(soundController.getRollSound());

            gameController.rollDice(kept);

            List<Integer> updatedValues = gameController.getDiceValues();
            diceValues.clear();
            diceValues.addAll(updatedValues);

            dicePanel.animateDiceRoll(kept, updatedValues, () -> {
                dicePanel.syncKeepSelections(keptValueCounts, updatedValues);
                dicePanel.setCheckboxesDisabled(false);
                int updatedRollCount = gameController.getCurrentRollCount();
                rollCounterLabel.setText("Veeretusi tehtud: " + updatedRollCount + "/3");

                int currentPlayerIndex = playerTurnCounter.get();
                if (currentPlayerIndex >= 0 && currentPlayerIndex < playerColumns.size()) {
                    scoreboardController.displayPossibleScores(gameController, diceValues,
                        currentPlayerIndex,
                        playerUsedCombinations.get(currentPlayerIndex));
                }

                boolean mustPickScore = updatedRollCount >= GameConstants.MAX_ROLLS_PER_TURN;
                if (mustPickScore) {
                    statusLabel.setText("Sa ei saa rohkem veeretada\nVali tabelist sobivad punktid!");
                    dicePanel.setCheckboxesDisabled(true);
                    dicePanel.setFocusedDieIndex(-1);

                    if (currentPlayerIndex >= 0 && currentPlayerIndex < playerColumns.size()) {
                        int[] focusableRows = {1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15};
                        boolean focusSet = false;
                        for (int row : focusableRows) {
                            if (playerUsedCombinations.get(currentPlayerIndex).get(row) == 0) {
                                scoreboardController.setKeyboardFocus(currentPlayerIndex, row);
                                focusSet = true;
                                break;
                            }
                        }
                        if (!focusSet) {
                            scoreboardController.clearKeyboardFocus();
                        }
                    }
                } else {
                    statusLabel.setText("Vali alleshoitavad täringud\nvõi pane endale punktid tabelisse");
                    scoreboardController.clearKeyboardFocus();
                    dicePanel.setCheckboxesDisabled(false);
                    dicePanel.setFocusedDieIndex(0);
                }

                rollButton.setDisable(false);
            });
        });

        return rollButton;
    }

    private static Button createExitButton(ThemeController themeController,
                                           SoundController soundController,
                                           Stage primaryStage,
                                           Runnable onGameEnd) {
        Button exitButton = new Button(GameConstants.BUTTON_EXIT_GAME);
        exitButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        exitButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        exitButton.setStyle(themeController.getButtonStyle());
        exitButton.setCursor(Cursor.HAND);
        UIHelper.attachButtonAnimations(exitButton, soundController);

        exitButton.setOnAction(actionEvent ->
            KeyboardHandler.toggleExitDialog(primaryStage, themeController, soundController, onGameEnd)
        );

        return exitButton;
    }

    /**
     * Gets the root container of the controls panel.
     *
     * @return the VBox container
     */
    public VBox getContainer() {
        return container;
    }

    /**
     * Gets the dice panel component.
     *
     * @return the dice panel
     */
    public DicePanel getDicePanel() {
        return dicePanel;
    }

    /**
     * Gets the roll button.
     *
     * @return the roll button
     */
    public Button getRollButton() {
        return rollButton;
    }

    /**
     * Gets the exit button.
     *
     * @return the exit button
     */
    public Button getExitButton() {
        return exitButton;
    }

    /**
     * Gets the label displaying the roll counter.
     *
     * @return the roll counter label
     */
    public Label getRollCounterLabel() {
        return rollCounterLabel;
    }

    /**
     * Gets the label displaying game status messages.
     *
     * @return the status label
     */
    public Label getStatusLabel() {
        return statusLabel;
    }
}
