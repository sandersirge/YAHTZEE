package projekt.yahtzee.ui.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
import projekt.yahtzee.controller.data.StatisticsController;
import projekt.yahtzee.ui.components.DicePanel;
import projekt.yahtzee.ui.components.ScoreCellHandler;
import projekt.yahtzee.ui.dialogs.ExitDialog;
import projekt.yahtzee.ui.handlers.KeyboardHandler;
import projekt.yahtzee.ui.handlers.UIHelper;
import projekt.yahtzee.util.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builds and orchestrates the main game board scene, including dice controls and the scoreboard.
 */
public class GameBoardScene {
    private final GameController gameController;
    private final ThemeController themeController;
    private final SoundController soundController;
    private final StatisticsController statisticsController;

    private static class DiceSectionContext {
        final VBox container;
        final DicePanel dicePanel;
        final Button rollButton;
        final Button exitButton;
        final Label rollCounterLabel;
        final Label statusLabel;

        DiceSectionContext(VBox container,
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
    }
    
    /**
     * Creates a new scene builder that wires together controllers required for the game board.
     *
     * @param gameController controller responsible for overall game state
     * @param themeController controller that provides theme-dependent styles
     * @param soundController controller that plays sound effects
     * @param statisticsController controller used for persisting statistics
     */
    public GameBoardScene(GameController gameController, ThemeController themeController, 
                         SoundController soundController, StatisticsController statisticsController) {
        this.gameController = gameController;
        this.themeController = themeController;
        this.soundController = soundController;
        this.statisticsController = statisticsController;
    }
    
    /**
     * Builds the game scene with the dice panel, scoreboard, and related interactions.
     *
    * @param primaryStage primary stage that hosts the scene
    * @param nameFields list of player name input fields
     * @param onGameEnd callback invoked when the user exits the game
     * @return constructed game board scene
     */
    public Scene createGameBoardScene(Stage primaryStage, List<TextField> nameFields, Runnable onGameEnd) {
        // Play a random game-start sound.
        playGameStartSound();
        
        // Initialize game state collections.
        final List<List<Label>> playerColumns = new ArrayList<>();
        final List<List<Integer>> playerUsedCombinations = new ArrayList<>();
        final List<Integer> diceValues = new ArrayList<>();
        final AtomicInteger playerTurnCounter = new AtomicInteger(0);
        
        // Build primary layout containers.
        StackPane boardRoot = createMainLayout();
        HBox layoutContent = (HBox) boardRoot.getChildren().get(0);
        
        // Create scoreboard first to obtain controller instance.
        List<String> playerNames = initializePlayers(nameFields);
        final ScoreboardController scoreboardController = new ScoreboardController(gameController, themeController, playerNames);
        HBox scoreboardUI = scoreboardController.createScoreboard();
        playerColumns.addAll(scoreboardController.getPlayerColumns());
        scoreboardUI.setMinHeight(0);
        scoreboardUI.setMaxHeight(Double.MAX_VALUE);
        
        // Build dice panel (needs scoreboard controller).
        DiceSectionContext diceContext = createDiceSection(
            playerColumns,
            playerUsedCombinations,
            diceValues,
            playerTurnCounter,
            primaryStage,
            onGameEnd,
            scoreboardController
        );
        VBox dicePanelContainer = diceContext.container;
        
        // Attach vine-boom sound to combo name column.
        addComboNameSounds(scoreboardUI);
        
        layoutContent.getChildren().addAll(dicePanelContainer, scoreboardUI);
        
        // Prepare list tracking used combinations.
        initializeUsedCombos(playerUsedCombinations, playerNames.size());
        
        // Wire click and hover handlers.
        ScoreCellHandler.setupClickHandlers(
            playerColumns,
            playerUsedCombinations,
            playerTurnCounter,
            diceValues,
            gameController,
            scoreboardController,
            diceContext.dicePanel,
            diceContext.rollCounterLabel,
            diceContext.statusLabel,
            primaryStage,
            themeController,
            soundController,
            statisticsController,
            onGameEnd
        );
        
        ScoreCellHandler.setupHoverEffects(playerColumns, playerUsedCombinations, 
                                          playerTurnCounter, themeController);
        
        // Highlight the first player column.
        scoreboardController.highlightPlayer(0);
        
        Scene gameBoardScene = new Scene(boardRoot, GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);

        boardRoot.prefWidthProperty().bind(gameBoardScene.widthProperty());
        boardRoot.prefHeightProperty().bind(gameBoardScene.heightProperty());
        layoutContent.prefHeightProperty().bind(boardRoot.heightProperty());
        dicePanelContainer.prefHeightProperty().bind(boardRoot.heightProperty());
        scoreboardUI.prefHeightProperty().bind(boardRoot.heightProperty());
        
        // Install keyboard controls.
        KeyboardHandler.setupKeyboardControls(
            gameBoardScene,
            diceContext.rollButton,
            diceContext.exitButton,
            diceContext.dicePanel,
            scoreboardController,
            diceContext.statusLabel,
            playerColumns,
            playerTurnCounter,
            playerUsedCombinations,
            gameController,
            primaryStage,
            themeController,
            soundController
        );
        
        return gameBoardScene;
    }
    
    /**
     * Plays one of the game-start sound effects when the board scene is created.
     */
    private void playGameStartSound() {
        Random gameStartRandom = new Random();
        if (gameStartRandom.nextBoolean()) {
            if (soundController.getTacoBellSound() != null) {
                soundController.getTacoBellSound().stop();
                soundController.getTacoBellSound().play();
            }
        } else {
            if (soundController.getUndertakersBellSound() != null) {
                soundController.getUndertakersBellSound().stop();
                soundController.getUndertakersBellSound().play();
            }
        }
    }
    
    /**
     * Creates the root layout container used by the game board scene.
     */
    private StackPane createMainLayout() {
        StackPane boardRoot = new StackPane();
        boardRoot.setStyle("-fx-background-color: " + themeController.getPanelBackgroundColor() + ";");
        boardRoot.setPadding(new Insets(0));
        boardRoot.setMinSize(0, 0);
        boardRoot.setPrefSize(GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
        boardRoot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox layoutContent = new HBox();
        layoutContent.setMinSize(0, 0);
        layoutContent.setPrefSize(GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
        layoutContent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        layoutContent.setSpacing(0);
        layoutContent.setPadding(new Insets(0));
        HBox.setHgrow(layoutContent, Priority.ALWAYS);
        StackPane.setAlignment(layoutContent, Pos.CENTER_LEFT);
        boardRoot.getChildren().add(layoutContent);

        return boardRoot;
    }
    
    /**
     * Creates the dice section, including roll/exit controls and status messaging.
     */
    private DiceSectionContext createDiceSection(List<List<Label>> playerColumns,
                                                 List<List<Integer>> playerUsedCombinations,
                                                 List<Integer> diceValues,
                                                 AtomicInteger playerTurnCounter,
                                                 Stage primaryStage,
                                                 Runnable onGameEnd,
                                                 ScoreboardController scoreboardController) {
        VBox dicePanelContainer = new VBox();
        dicePanelContainer.setPrefWidth(GameConstants.DICE_PANEL_WIDTH);
        dicePanelContainer.setMinHeight(0);
        dicePanelContainer.setPrefHeight(GameConstants.MAIN_WINDOW_HEIGHT);
        dicePanelContainer.setMaxHeight(Double.MAX_VALUE);
        dicePanelContainer.setSpacing(0);
        dicePanelContainer.setPadding(new Insets(0));

        VBox upperSection = new VBox();
        upperSection.setAlignment(Pos.TOP_CENTER);
        upperSection.setPrefWidth(GameConstants.DICE_PANEL_WIDTH);
        upperSection.setSpacing(GameConstants.SPACING_MEDIUM);
        upperSection.setPadding(new Insets(0));
        upperSection.setStyle(themeController.getDicePanelStyle());
        VBox.setVgrow(upperSection, Priority.ALWAYS);

        VBox lowerSection = new VBox();
        lowerSection.setAlignment(Pos.CENTER);
        lowerSection.setPrefWidth(GameConstants.DICE_PANEL_WIDTH);
        lowerSection.setSpacing(GameConstants.SPACING_LARGE);
        lowerSection.setPadding(new Insets(0));
        lowerSection.setStyle(themeController.getDicePanelStyle());
        VBox.setVgrow(lowerSection, Priority.ALWAYS);

        dicePanelContainer.getChildren().addAll(upperSection, lowerSection);

        Label titleLabel = new Label(GameConstants.TITLE_YAHTZEE);
        titleLabel.setFont(GameConstants.getTitleFont());
        titleLabel.setStyle(themeController.getLabelTextFill());
        
        // Construct dice panel using DicePanel component.
        final DicePanel dicePanel = new DicePanel(themeController, gameController.getDice());
        VBox diceBoxContainer = dicePanel.createDicePanel();
        final List<CheckBox> keepCheckboxes = dicePanel.getKeepCheckboxes();
        dicePanel.setCheckboxesDisabled(true);

        upperSection.getChildren().addAll(titleLabel, diceBoxContainer);

        Label rollCounterLabel = new Label("0/3");
        rollCounterLabel.setFont(Font.font(GameConstants.FONT_FAMILY, FontWeight.BOLD, 48));
        rollCounterLabel.setTextFill(Color.web(themeController.getRollCounterColor()));

        String initialMessage = GameConstants.FIRST_PLAYER_TURN + "\n\nVeereta täringuid alustamiseks!";
        if (!gameController.getPlayers().isEmpty()) {
            String firstPlayerName = gameController.getPlayers().get(0).getPlayerName();
            initialMessage = "1. mängija - " + firstPlayerName + " kord!\n\nVeereta täringuid alustamiseks!";
        }
        Label statusLabel = new Label(initialMessage);
        statusLabel.setFont(GameConstants.getNormalFont());
        statusLabel.setTextAlignment(TextAlignment.CENTER);
        statusLabel.setStyle(themeController.getLabelTextFill());

        Button rollButton = createRollButton(keepCheckboxes, dicePanel, diceValues, rollCounterLabel,
            statusLabel, playerColumns, playerTurnCounter, playerUsedCombinations, scoreboardController);

        Button exitButton = createExitButton(primaryStage, onGameEnd);

        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setSpacing(130);
        buttonRow.getChildren().addAll(rollButton, exitButton);
        lowerSection.getChildren().addAll(rollCounterLabel, statusLabel, buttonRow);

        return new DiceSectionContext(dicePanelContainer, dicePanel, rollButton, exitButton, rollCounterLabel, statusLabel);
    }
    
    /**
     * Creates the roll button and wires the rolling logic.
     */
    private Button createRollButton(List<CheckBox> keepCheckboxes, DicePanel dicePanel,
                                   List<Integer> diceValues, Label rollCounterLabel,
                                   Label statusLabel, List<List<Label>> playerColumns,
                                   AtomicInteger playerTurnCounter,
                                   List<List<Integer>> playerUsedCombinations,
                                   ScoreboardController scoreboardController) {
        Button rollButton = new Button(GameConstants.ROLL_BUTTON);
        rollButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        rollButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        rollButton.setStyle(themeController.getButtonStyle());
        rollButton.setCursor(Cursor.HAND);
        UIHelper.attachButtonAnimations(rollButton, soundController);

        rollButton.setOnAction(actionEvent -> {
            int rollCount = gameController.getCurrentRollCount();
            
            if (rollCount >= GameConstants.MAX_ROLLS) {
                statusLabel.setText(GameConstants.MSG_NO_MORE_ROLLS);
                return;
            }
            
            int[] kept = new int[GameConstants.DICE_COUNT];
            for (int i = 0; i < keepCheckboxes.size(); i++) {
                kept[i] = keepCheckboxes.get(i).isSelected() ? 1 : 0;
            }
            
            rollButton.setDisable(true);
            
            if (soundController.getRollSound() != null) {
                soundController.getRollSound().play();
            }
            
            gameController.rollDice(kept);
            
            List<Integer> updatedValues = gameController.getDiceValues();
            diceValues.clear();
            diceValues.addAll(updatedValues);

            dicePanel.animateDiceRoll(kept, updatedValues, () -> {
                dicePanel.resetCheckboxes();
                dicePanel.setCheckboxesDisabled(false);
                dicePanel.setFocusedDieIndex(0);
                
                int uusRollCount = gameController.getCurrentRollCount();
                rollCounterLabel.setText(uusRollCount + "/3");
                statusLabel.setText("Vali alleshoitavad täringud\nvõi pane endale punktid tabelisse");

                int currentPlayerIndex = playerTurnCounter.get();
                if (currentPlayerIndex >= 0 && currentPlayerIndex < playerColumns.size()) {
                    scoreboardController.displayPossibleScores(gameController, diceValues, 
                                           currentPlayerIndex, 
                                           playerUsedCombinations.get(currentPlayerIndex));

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
                
                rollButton.setDisable(false);
            });
        });
        
        return rollButton;
    }
    
    /**
     * Creates the exit button that opens the exit confirmation dialog.
     */
    private Button createExitButton(Stage primaryStage, Runnable onGameEnd) {
        Button exitButton = new Button(GameConstants.BUTTON_EXIT_GAME);
        exitButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        exitButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        exitButton.setStyle(themeController.getButtonStyle());
        exitButton.setCursor(Cursor.HAND);
        UIHelper.attachButtonAnimations(exitButton, soundController);

        exitButton.setOnAction(actionEvent -> {
            ExitDialog.show(primaryStage, themeController, soundController, onGameEnd);
        });
        
        return exitButton;
    }
    
    /**
     * Initializes players inside the game controller and returns their names.
     */
    private List<String> initializePlayers(List<TextField> nameFields) {
        List<String> playerNames = new ArrayList<>();
        for (TextField tf : nameFields) {
            String name = tf.getText();
            if (name.isEmpty()) {
                name = GameConstants.getDefaultPlayerName(nameFields.indexOf(tf) + 1);
            }
            playerNames.add(name);
            gameController.addPlayer(name);
        }
        return playerNames;
    }
    
    /**
     * Initializes the tracking structure for used combinations for each player.
     */
    private void initializeUsedCombos(List<List<Integer>> playerUsedCombinations, int playerCount) {
        for (int p = 0; p < playerCount; p++) {
            List<Integer> comboUsage = new ArrayList<>();
            for (int i = 0; i < 18; i++) {
                comboUsage.add(0);
            }
            playerUsedCombinations.add(comboUsage);
        }
    }
    
    /**
     * Attaches the vine boom sound effect to the combo name column.
     */
    private void addComboNameSounds(HBox scoreboardUI) {
        VBox comboNameColumn = (VBox) scoreboardUI.getChildren().get(0);
        for (Node node : comboNameColumn.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setOnMouseClicked(e -> {
                    if (soundController.getVineBoomSound() != null) {
                        soundController.getVineBoomSound().stop();
                        soundController.getVineBoomSound().play();
                    }
                });
            }
        }
    }
}

