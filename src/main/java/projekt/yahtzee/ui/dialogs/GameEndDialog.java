package projekt.yahtzee.ui.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import projekt.yahtzee.model.Player;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.util.ResultsFileManager;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.data.StatisticsController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.handlers.UIHelper;

import java.util.List;
import java.util.ArrayList;

/**
 * Creates and displays the game end dialog with results.
 * 
 * @author Yahtzee Game Project
 * @version 1.0
 */
public class GameEndDialog {
    private final Stage ownerStage;
    private final ThemeController themeController;
    private final SoundController soundController;
    private final StatisticsController statisticsController;
    private final GameController gameController;
    private final Runnable onReturnToMenu;
    
    /**
     * Constructs a new game end dialog.
     * 
     * @param ownerStage the parent stage
     * @param themeController the theme manager instance
     * @param soundController the sound manager instance
     * @param statisticsController the statistics manager instance
     * @param gameController the game manager instance
     * @param onReturnToMenu callback for returning to main menu
     */
    public GameEndDialog(Stage ownerStage, ThemeController themeController, 
                         SoundController soundController, StatisticsController statisticsController,
                         GameController gameController, Runnable onReturnToMenu) {
        this.ownerStage = ownerStage;
        this.themeController = themeController;
        this.soundController = soundController;
        this.statisticsController = statisticsController;
        this.gameController = gameController;
        this.onReturnToMenu = onReturnToMenu;
    }
    
    /**
     * Handles game end: displays winners, saves results, and shows end dialog.
     * 
     * @param teavitused Label to display winner message
     */
    public void handleGameEnd(Label statusLabel) {
        // Retrieve winners from the controller.
        List<Player> winners = gameController.getWinners();
        List<Player> sortedPlayers = gameController.getSortedPlayers();
        
        // Build the winner summary string.
        StringBuilder winnerText = new StringBuilder("MÄNG LÄBI!\n\n");
        if (winners.size() == 1) {
            winnerText.append("VÕITJA: ").append(winners.get(0).getPlayerName())
                      .append("\nSKOOR: ").append(winners.get(0).getTotalScore());
        } else {
            winnerText.append("VIIK! VÕITJAD:\n");
            for (Player winner : winners) {
                winnerText.append(winner.getPlayerName())
                          .append(" - ").append(winner.getTotalScore()).append("\n");
            }
        }
        
        // Update the status label on the board.
        statusLabel.setText(winnerText.toString());
        
        statisticsController.updateStatistics(gameController.getPlayers(), winners);
        ResultsFileManager.saveResults(sortedPlayers);
        
        showEndDialog(winnerText.toString(), sortedPlayers);
    }
    
    /**
     * Displays the end-of-game dialog with the full scoreboard results.
     *
     * @param winnerText winner summary text
     * @param sortedPlayers players sorted by score
     */
    public void showEndDialog(String winnerText, List<Player> sortedPlayers) {
        // Play the wow sound on game end.
        if (soundController != null) {
            soundController.playClip(soundController.getWowSound());
        }
        
        Font boldFont = GameConstants.getCellFontBold();
        Font regularFont = GameConstants.getCellFont();

        List<String> resultRows = new ArrayList<>();
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            resultRows.add((i + 1) + ". " + player.getPlayerName() +
                           " - " + player.getTotalScore() + " punkti");
        }

        double winnerBoxPadding = 64;
        double resultsBoxPadding = 40;
        double containerPadding = 80;
        double containerMaxWidth = 680;
        double containerMinWidth = 380;

        double winnerLineWidth = DialogLayoutUtil.measureMaxLineWidth(winnerText, boldFont);
        double titleWidth = DialogLayoutUtil.measureTextWidth("KÕIK TULEMUSED:", boldFont);
        double resultsLineWidth = resultRows.stream()
            .mapToDouble(line -> DialogLayoutUtil.measureTextWidth(line, regularFont))
            .max().orElse(0.0);

        double desiredInnerWidth = Math.max(winnerLineWidth + winnerBoxPadding,
            Math.max(420, Math.max(titleWidth + resultsBoxPadding, resultsLineWidth + resultsBoxPadding)));
        double preferredWidth = Math.max(containerMinWidth,
            Math.min(containerMaxWidth, desiredInnerWidth + containerPadding));
        double contentAreaWidth = preferredWidth - containerPadding;
        double winnerLabelMaxWidth = Math.max(0, contentAreaWidth - winnerBoxPadding);
        double resultLabelMaxWidth = Math.max(0, contentAreaWidth - resultsBoxPadding);

        Stage endDialog = new Stage();
        endDialog.initOwner(ownerStage);
        endDialog.setTitle("Mäng läbi!");

        // Winner message card.
        Label winnerLabel = new Label(winnerText);
        winnerLabel.setFont(boldFont);
        winnerLabel.setStyle(themeController.getLabelTextFill());
        winnerLabel.setWrapText(true);
        winnerLabel.setTextAlignment(TextAlignment.CENTER);
        winnerLabel.setAlignment(Pos.CENTER);
        winnerLabel.setMaxWidth(winnerLabelMaxWidth);

        StackPane winnerContainer = new StackPane();
        winnerContainer.setPadding(new Insets(20, 32, 20, 32));
        String contentBackgroundStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        winnerContainer.setStyle(contentBackgroundStyle + " -fx-background-radius: 12; -fx-border-radius: 12;");
        winnerContainer.setMaxWidth(contentAreaWidth);
        winnerContainer.getChildren().add(winnerLabel);

        // Results list card.
        VBox resultsContainer = new VBox();
        resultsContainer.setAlignment(Pos.CENTER_LEFT);
        resultsContainer.setSpacing(8);
        resultsContainer.setPadding(new Insets(20));
        resultsContainer.setPrefWidth(contentAreaWidth);
        resultsContainer.setMaxWidth(contentAreaWidth);
        resultsContainer.setStyle(contentBackgroundStyle + " -fx-background-radius: 12; -fx-border-radius: 12;");

        Label resultsTitle = new Label("KÕIK TULEMUSED:");
        resultsTitle.setFont(boldFont);
        resultsTitle.setStyle(themeController.getLabelTextFill());
        resultsTitle.setTextAlignment(TextAlignment.LEFT);
        resultsTitle.setAlignment(Pos.CENTER_LEFT);
        resultsTitle.setMaxWidth(resultLabelMaxWidth);
        resultsContainer.getChildren().add(resultsTitle);

        for (String resultText : resultRows) {
            Label resultLabel = new Label(resultText);
            resultLabel.setFont(regularFont);
            resultLabel.setStyle(themeController.getLabelTextFill());
            resultLabel.setWrapText(true);
            resultLabel.setTextAlignment(TextAlignment.LEFT);
            resultLabel.setAlignment(Pos.CENTER_LEFT);
            resultLabel.setMaxWidth(resultLabelMaxWidth);
            resultsContainer.getChildren().add(resultLabel);
        }

        // Action buttons.
        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setSpacing(GameConstants.SPACING_NORMAL);

        Button mainMenuButton = new Button("Tagasi peamenüüsse");
        mainMenuButton.setPrefWidth(GameConstants.BUTTON_EXIT_WIDTH);
        mainMenuButton.setPrefHeight(GameConstants.BUTTON_EXIT_HEIGHT);
        mainMenuButton.setStyle(GameConstants.BUTTON_WHEAT_STYLE);
        UIHelper.attachButtonAnimations(mainMenuButton, soundController);
        mainMenuButton.setOnAction(e -> {
            endDialog.close();
            onReturnToMenu.run();
        });

        Button closeButton = new Button("Sulge");
        closeButton.setPrefWidth(GameConstants.BUTTON_EXIT_WIDTH);
        closeButton.setPrefHeight(GameConstants.BUTTON_EXIT_HEIGHT);
        closeButton.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(closeButton, soundController);
        closeButton.setOnAction(e -> {
            endDialog.close();
            Platform.exit();
        });

        buttonRow.getChildren().addAll(mainMenuButton, closeButton);

        StackPane backgroundContainer = ThemedDialogBuilder.create(themeController)
            .withMinWidth(containerMinWidth)
            .withMaxWidth(containerMaxWidth)
            .withPreferredWidth(preferredWidth)
            .build(winnerContainer, resultsContainer, buttonRow);

        Scene dialogScene = new Scene(backgroundContainer);
        endDialog.setResizable(false);
        endDialog.setScene(dialogScene);
        endDialog.sizeToScene();
        endDialog.show();
    }

}
