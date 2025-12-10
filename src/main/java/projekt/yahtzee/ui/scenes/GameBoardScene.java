package projekt.yahtzee.ui.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.controller.ui.ScoreboardController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.controller.data.StatisticsController;
import projekt.yahtzee.ui.components.GameControlsPanel;
import projekt.yahtzee.ui.components.ScoreCellHandler;
import projekt.yahtzee.ui.handlers.KeyboardHandler;
import projekt.yahtzee.util.GameConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builds and orchestrates the main game board scene, including dice controls and the scoreboard.
 */
public class GameBoardScene {
    private final GameController gameController;
    private final ThemeController themeController;
    private final SoundController soundController;
    private final StatisticsController statisticsController;

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
        GameControlsPanel diceSection = GameControlsPanel.create(
            gameController,
            themeController,
            soundController,
            scoreboardController,
            playerColumns,
            playerUsedCombinations,
            diceValues,
            playerTurnCounter,
            primaryStage,
            onGameEnd
        );
        VBox dicePanelContainer = diceSection.getContainer();
        
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
            diceSection.getDicePanel(),
            diceSection.getRollCounterLabel(),
            diceSection.getStatusLabel(),
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
            diceSection.getRollButton(),
            diceSection.getExitButton(),
            diceSection.getDicePanel(),
            scoreboardController,
            diceSection.getStatusLabel(),
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
        soundController.playRandomGameStartSound();
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
     * Initializes players inside the game controller and returns their names.
     */
    private List<String> initializePlayers(List<TextField> nameFields) {
        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < nameFields.size(); i++) {
            TextField field = nameFields.get(i);
            String name = field.getText();
            if (name.isEmpty()) {
                name = GameConstants.getDefaultPlayerName(i + 1);
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
            playerUsedCombinations.add(new ArrayList<>(Collections.nCopies(GameConstants.TOTAL_ROWS, 0)));
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
                    soundController.playClip(soundController.getVineBoomSound());
                });
            }
        }
    }
}

