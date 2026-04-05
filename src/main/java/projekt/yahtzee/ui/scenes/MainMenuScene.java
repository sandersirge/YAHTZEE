package projekt.yahtzee.ui.scenes;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.util.UIFonts;
import projekt.yahtzee.util.ResultsFileManager;
import projekt.yahtzee.controller.data.StatisticsController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.handlers.UIHelper;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages the menu-related scenes: main menu, results view, and statistics view.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public class MainMenuScene {
    private final ThemeController themeController;
    private final SoundController soundController;
    private final StatisticsController statisticsController;
    private final Stage stage;
    private Runnable onNewGame;
    
    /**
     * Creates a new menu scene manager.
     *
     * @param stage primary stage reference
     * @param themeController controller that manages theme styling
     * @param soundController controller that plays menu sounds
     * @param statisticsController controller responsible for statistics persistence
     */
    public MainMenuScene(Stage stage, ThemeController themeController, SoundController soundController, StatisticsController statisticsController) {
        this.stage = stage;
        this.themeController = themeController;
        this.soundController = soundController;
        this.statisticsController = statisticsController;
    }
    
    /**
     * Builds the main menu scene with actions for starting the game and viewing data screens.
     *
     * @param onNewGame callback executed when the user starts a new game
     * @return configured main menu scene
     */
    public Scene createMainMenu(Runnable onNewGame) {
        this.onNewGame = onNewGame;
        StackPane menuRoot = new StackPane();
        menuRoot.setStyle(themeController.getBackgroundStyle());

        // Title label with a background capsule sized to the label width.
        Label titleLabel = new Label(GameConstants.TITLE_WELCOME);
        titleLabel.setFont(UIFonts.getLargeFont());
        titleLabel.setStyle(themeController.getLabelTextFill());

        StackPane titleContainer = new StackPane();
        titleContainer.setPadding(new Insets(15, 30, 15, 30));
        titleContainer.setMaxWidth(Region.USE_PREF_SIZE);
        String titleBackgroundStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        titleContainer.setStyle(titleBackgroundStyle + " -fx-border-radius: 10; -fx-background-radius: 10;");
        titleContainer.getChildren().add(titleLabel);

        Button startButton = new Button(GameConstants.BUTTON_NEW_GAME);
        startButton.setPrefWidth(GameConstants.BUTTON_MENU_WIDTH);
        startButton.setPrefHeight(GameConstants.BUTTON_MENU_HEIGHT);
        startButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        startButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        startButton.setStyle(GameConstants.BUTTON_SUCCESS_STYLE);
        UIHelper.attachButtonAnimations(startButton, soundController);
        startButton.setOnAction(e -> {
            if (onNewGame != null) {
                onNewGame.run();
            }
        });

        Button resultsButton = new Button(GameConstants.BUTTON_BEST_RESULTS);
        resultsButton.setPrefWidth(GameConstants.BUTTON_MENU_WIDTH);
        resultsButton.setPrefHeight(GameConstants.BUTTON_MENU_HEIGHT);
        resultsButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        resultsButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        resultsButton.setStyle(GameConstants.BUTTON_INFO_STYLE);
        UIHelper.attachButtonAnimations(resultsButton, soundController);
        resultsButton.setOnAction(e -> {
            Scene resultsScene = createResultsScene();
            UIHelper.addFullscreenToggle(resultsScene, stage);
            stage.setScene(resultsScene);
        });

        Button statsButton = new Button("Statistika");
        statsButton.setPrefWidth(GameConstants.BUTTON_MENU_WIDTH);
        statsButton.setPrefHeight(GameConstants.BUTTON_MENU_HEIGHT);
        statsButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        statsButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        statsButton.setStyle(GameConstants.BUTTON_INFO_STYLE);
        UIHelper.attachButtonAnimations(statsButton, soundController);
        statsButton.setOnAction(e -> {
            Scene statsScene = createStatisticsScene();
            UIHelper.addFullscreenToggle(statsScene, stage);
            stage.setScene(statsScene);
        });

        String themeButtonText = themeController.isDarkTheme() ? "☀ Hele teema" : "🌙 Tume teema";
        Button themeButton = new Button(themeButtonText);
        themeButton.setPrefWidth(GameConstants.BUTTON_MENU_WIDTH);
        themeButton.setPrefHeight(GameConstants.BUTTON_MENU_HEIGHT);
        themeButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        themeButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        String themeButtonStyle = themeController.isDarkTheme()
            ? GameConstants.BUTTON_LIGHT_STYLE
            : GameConstants.BUTTON_NEUTRAL_STYLE;
        themeButton.setStyle(themeButtonStyle);
        UIHelper.attachButtonAnimations(themeButton, soundController);
        themeButton.setOnAction(e -> {
            themeController.toggleTheme();
            Scene updatedScene = createMainMenu(onNewGame);
            stage.setScene(updatedScene);
        });

        Button exitButton = new Button("Sulge");
        exitButton.setPrefWidth(GameConstants.BUTTON_MENU_WIDTH);
        exitButton.setPrefHeight(GameConstants.BUTTON_MENU_HEIGHT);
        exitButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        exitButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        exitButton.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(exitButton, soundController);
        exitButton.setOnAction(e -> Platform.exit());

        VBox buttonContainer = new VBox();
        buttonContainer.getChildren().addAll(startButton, resultsButton, statsButton, themeButton, exitButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(60);

        VBox menuContainer = new VBox();
        menuContainer.getChildren().addAll(titleContainer, buttonContainer);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setSpacing(GameConstants.SPACING_LARGE);
        menuContainer.setPadding(new Insets(50));
        menuContainer.setMaxWidth(GameConstants.MAIN_MENU_WIDTH - 120);
        menuContainer.setMaxHeight(GameConstants.MAIN_MENU_HEIGHT - 60);
        String menuContainerStyle = "-fx-background-color: " + themeController.getBoxBackgroundColor() + ";";
        menuContainer.setStyle(menuContainerStyle + " -fx-border-radius: 15; -fx-background-radius: 15;");

        menuRoot.getChildren().add(menuContainer);
        StackPane.setAlignment(menuContainer, Pos.CENTER);

        Scene scene = new Scene(menuRoot, GameConstants.MAIN_MENU_WIDTH, GameConstants.MAIN_MENU_HEIGHT);
        UIHelper.addFullscreenToggle(scene, stage);
        return scene;
    }
    
    /**
     * Builds the results scene that shows previous game outcomes.
     *
     * @return results scene
     */
    public Scene createResultsScene() {
        StackPane resultsRoot = new StackPane();
        resultsRoot.setStyle(themeController.getBackgroundStyle());

        VBox resultsContainer = new VBox();
        resultsContainer.setSpacing(GameConstants.SPACING_NORMAL);
        resultsContainer.setAlignment(Pos.CENTER);
        resultsContainer.setPadding(new Insets(20));

        // Title label with a background capsule.
        Label titleLabel = new Label("TULEMUSED");
        titleLabel.setFont(UIFonts.getLargeFont());
        titleLabel.setStyle(themeController.getLabelTextFill());

        StackPane titleContainer = new StackPane();
        titleContainer.setPadding(new Insets(15, 30, 15, 30));
        titleContainer.setMaxWidth(Region.USE_PREF_SIZE);
        String titleBackgroundStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        titleContainer.setStyle(titleBackgroundStyle + " -fx-border-radius: 10; -fx-background-radius: 10;");
        titleContainer.getChildren().add(titleLabel);

        // Results container with styled background.
        VBox resultsBox = new VBox();
        resultsBox.setAlignment(Pos.CENTER_LEFT);
        resultsBox.setSpacing(10);
        String resultsBoxStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        resultsBox.setStyle(resultsBoxStyle + " -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label resultsHeader = new Label(GameConstants.MSG_CURRENT_RESULTS);
        resultsHeader.setFont(UIFonts.getCellFont());
        resultsHeader.setStyle(themeController.getLabelTextFill());
        resultsBox.getChildren().add(resultsHeader);

        // Load saved results from the results file.
        try {
            List<String> resultLines = ResultsFileManager.loadResults();
            boolean hasVisibleLines = false;
            for (String line : resultLines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                hasVisibleLines = true;
                Label resultLabel = new Label(line);
                resultLabel.setFont(UIFonts.getCellFont());
                resultLabel.setStyle(themeController.getLabelTextFill());
                resultsBox.getChildren().add(resultLabel);
            }

            if (!hasVisibleLines) {
                Label emptyLabel = new Label("Tulemusi ei leitud");
                emptyLabel.setFont(UIFonts.getCellFont());
                emptyLabel.setStyle(themeController.getLabelTextFill());
                resultsBox.getChildren().add(emptyLabel);
            }
        } catch (NoSuchFileException ex) {
            Label errorLabel = new Label("Tulemusi ei leitud");
            errorLabel.setFont(UIFonts.getCellFont());
            errorLabel.setStyle(themeController.getLabelTextFill());
            resultsBox.getChildren().add(errorLabel);
        } catch (IOException ex) {
            Label errorLabel = new Label("Viga tulemuste lugemisel: " + ex.getMessage());
            errorLabel.setFont(UIFonts.getCellFont());
            errorLabel.setStyle(themeController.getLabelTextFill());
            resultsBox.getChildren().add(errorLabel);
        }

        // Back button.
        Button backButton = new Button("Tagasi");
        backButton.setScaleX(GameConstants.SCALE_BUTTON_MEDIUM);
        backButton.setScaleY(GameConstants.SCALE_BUTTON_MEDIUM);
        backButton.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(backButton, soundController);
        backButton.setOnAction(e -> stage.setScene(createMainMenu(this.onNewGame)));

        resultsContainer.getChildren().addAll(titleContainer, resultsBox, backButton);

        ScrollPane scroll = new ScrollPane(resultsContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        resultsRoot.getChildren().add(scroll);

        return new Scene(resultsRoot, GameConstants.SETUP_WINDOW_WIDTH, GameConstants.SETUP_WINDOW_HEIGHT);
    }
    
    /**
     * Builds the statistics scene that summarises aggregate and per-player stats.
     *
     * @return statistics scene
     */
    public Scene createStatisticsScene() {
        StackPane statsRoot = new StackPane();
        statsRoot.setStyle(themeController.getBackgroundStyle());

        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(GameConstants.SPACING_MEDIUM);
        content.setPadding(new Insets(20));

        // Title label with a background capsule.
        Label titleLabel = new Label("STATISTIKA");
        titleLabel.setFont(UIFonts.getLargeFont());
        titleLabel.setStyle(themeController.getLabelTextFill());

        StackPane titleContainer = new StackPane();
        titleContainer.setPadding(new Insets(15, 30, 15, 30));
        titleContainer.setMaxWidth(Region.USE_PREF_SIZE);
        String titleBackgroundStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        titleContainer.setStyle(titleBackgroundStyle + " -fx-border-radius: 10; -fx-background-radius: 10;");
        titleContainer.getChildren().add(titleLabel);

        VBox summaryStats = new VBox();
        summaryStats.setAlignment(Pos.CENTER_LEFT);
        summaryStats.setSpacing(10);
        String summaryStatsStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        summaryStats.setStyle(summaryStatsStyle + " -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label summaryTitleLabel = new Label("ÜLDINE STATISTIKA");
        summaryTitleLabel.setFont(UIFonts.getCellFontBold());
        summaryTitleLabel.setStyle(themeController.getLabelTextFill());

        Label totalGamesLabel = new Label("Mängitud mänge: " + statisticsController.getTotalGames());
        totalGamesLabel.setFont(UIFonts.getCellFont());
        totalGamesLabel.setStyle(themeController.getLabelTextFill());

        String highestScoreText = "Kõrgeim skoor: " + statisticsController.getHighestScore();
        if (!statisticsController.getHighestScorePlayer().isEmpty()) {
            highestScoreText += " (" + statisticsController.getHighestScorePlayer() + ")";
        }
        Label highestScoreLabel = new Label(highestScoreText);
        highestScoreLabel.setFont(UIFonts.getCellFont());
        highestScoreLabel.setStyle(themeController.getLabelTextFill());

        Label averageScoreLabel = new Label(String.format("Keskmine skoor: %.1f", statisticsController.getAverageScore()));
        averageScoreLabel.setFont(UIFonts.getCellFont());
        averageScoreLabel.setStyle(themeController.getLabelTextFill());

        summaryStats.getChildren().addAll(summaryTitleLabel, totalGamesLabel, highestScoreLabel, averageScoreLabel);

        // Player-specific statistics.
        VBox playerStatsContainer = new VBox();
        playerStatsContainer.setAlignment(Pos.CENTER_LEFT);
        playerStatsContainer.setSpacing(10);
        String playerStatsStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        playerStatsContainer.setStyle(playerStatsStyle + " -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label playerStatsTitle = new Label("MÄNGIJATE STATISTIKA");
        playerStatsTitle.setFont(UIFonts.getCellFontBold());
        playerStatsTitle.setStyle(themeController.getLabelTextFill());
        playerStatsContainer.getChildren().add(playerStatsTitle);

        Map<String, Integer> wins = statisticsController.getPlayerWins();
        Map<String, Integer> gamesPlayed = statisticsController.getPlayerGamesPlayed();
        Map<String, Integer> highScores = statisticsController.getPlayerHighScores();

        // Sort all players by number of wins.
        List<Map.Entry<String, Integer>> sortedPlayers = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gamesPlayed.entrySet()) {
            sortedPlayers.add(new AbstractMap.SimpleEntry<>(entry.getKey(), wins.getOrDefault(entry.getKey(), 0)));
        }
        sortedPlayers.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        if (sortedPlayers.isEmpty()) {
            Label emptyLabel = new Label("Statistika puudub - mängi esimene mäng!");
            emptyLabel.setFont(UIFonts.getCellFont());
            emptyLabel.setStyle(themeController.getLabelTextFill());
            playerStatsContainer.getChildren().add(emptyLabel);
        } else {
            for (Map.Entry<String, Integer> entry : sortedPlayers) {
                String name = entry.getKey();
                int winCount = entry.getValue();
                int gamesCount = gamesPlayed.getOrDefault(name, 0);
                int bestScore = highScores.getOrDefault(name, 0);
                double winPercentage = gamesCount > 0 ? (winCount * 100.0 / gamesCount) : 0;

                VBox playerInfo = new VBox(5);
                Label nameLabel = new Label(name);
                nameLabel.setFont(UIFonts.getCellFontBold());
                nameLabel.setStyle(themeController.getLabelTextFill());

                Label infoLabel = new Label(String.format("Võite: %d/%d (%.1f%%) | Parim skoor: %d",
                                                          winCount, gamesCount, winPercentage, bestScore));
                infoLabel.setFont(UIFonts.getCellFont());
                infoLabel.setStyle(themeController.getLabelTextFill());

                playerInfo.getChildren().addAll(nameLabel, infoLabel);
                playerStatsContainer.getChildren().add(playerInfo);
            }
        }

        Button backButton = new Button("Tagasi");
        backButton.setScaleX(GameConstants.SCALE_BUTTON_MEDIUM);
        backButton.setScaleY(GameConstants.SCALE_BUTTON_MEDIUM);
        backButton.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(backButton, soundController);
        backButton.setOnAction(e -> stage.setScene(createMainMenu(this.onNewGame)));

        content.getChildren().addAll(titleContainer, summaryStats, playerStatsContainer, backButton);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        statsRoot.getChildren().add(scroll);

        return new Scene(statsRoot, GameConstants.SETUP_WINDOW_WIDTH, GameConstants.SETUP_WINDOW_HEIGHT);
    }
}

