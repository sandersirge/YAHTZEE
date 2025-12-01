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
    public void handleGameEnd(Label teavitused) {
        // Retrieve winners from the controller.
        List<Player> võitjad = gameController.getWinners();
        List<Player> sorteeritudMängijad = gameController.getSortedPlayers();
        
        // Build the winner summary string.
        StringBuilder võitjaTekst = new StringBuilder("MÄNG LÄBI!\n\n");
        if (võitjad.size() == 1) {
            võitjaTekst.append("VÕITJA: ").append(võitjad.get(0).getPlayerName())
                      .append("\nSKOOR: ").append(võitjad.get(0).getTotalScore());
        } else {
            võitjaTekst.append("VIIK! VÕITJAD:\n");
            for (Player võitja : võitjad) {
                võitjaTekst.append(võitja.getPlayerName())
                          .append(" - ").append(võitja.getTotalScore()).append("\n");
            }
        }
        
        // Update the status label on the board.
        teavitused.setText(võitjaTekst.toString());
        
        statisticsController.updateStatistics(gameController.getPlayers(), võitjad);
        ResultsFileManager.salvestaTulemused(sorteeritudMängijad);
        
        kuvaLõppdialoog(võitjaTekst.toString(), sorteeritudMängijad);
    }
    
    /**
     * Displays the end-of-game dialog with the full scoreboard results.
     *
     * @param võitjaTekst winner summary text
     * @param sorteeritudMängijad players sorted by score
     */
    public void kuvaLõppdialoog(String võitjaTekst, List<Player> sorteeritudMängijad) {
        // Play the wow sound on game end.
        if (soundController.getWowSound() != null) {
            soundController.getWowSound().stop();
            soundController.getWowSound().play();
        }
        
        Font boldFont = GameConstants.getCellFontBold();
        Font regularFont = GameConstants.getCellFont();

        List<String> tulemuseRead = new ArrayList<>();
        for (int i = 0; i < sorteeritudMängijad.size(); i++) {
            Player mängija = sorteeritudMängijad.get(i);
            tulemuseRead.add((i + 1) + ". " + mängija.getPlayerName() +
                             " - " + mängija.getTotalScore() + " punkti");
        }

        double winnerBoxPadding = 64;
        double resultsBoxPadding = 40;
        double containerPadding = 80;
        double konteinerMaxWidth = 680;
        double konteinerMinWidth = 380;

        double winnerLineWidth = DialogLayoutUtil.measureMaxLineWidth(võitjaTekst, boldFont);
        double titleWidth = DialogLayoutUtil.measureTextWidth("KÕIK TULEMUSED:", boldFont);
        double resultsLineWidth = tulemuseRead.stream()
            .mapToDouble(line -> DialogLayoutUtil.measureTextWidth(line, regularFont))
            .max().orElse(0.0);

        double desiredInnerWidth = Math.max(winnerLineWidth + winnerBoxPadding,
            Math.max(420, Math.max(titleWidth + resultsBoxPadding, resultsLineWidth + resultsBoxPadding)));
        double preferredWidth = Math.max(konteinerMinWidth,
            Math.min(konteinerMaxWidth, desiredInnerWidth + containerPadding));
        double contentAreaWidth = preferredWidth - containerPadding;
        double winnerLabelMaxWidth = Math.max(0, contentAreaWidth - winnerBoxPadding);
        double resultLabelMaxWidth = Math.max(0, contentAreaWidth - resultsBoxPadding);

        Stage lõppDialoog = new Stage();
        lõppDialoog.initOwner(ownerStage);
        lõppDialoog.setTitle("Mäng läbi!");

        // Winner message card.
        Label võitjaLabel = new Label(võitjaTekst);
        võitjaLabel.setFont(boldFont);
        võitjaLabel.setStyle(themeController.getLabelTextFill());
        võitjaLabel.setWrapText(true);
        võitjaLabel.setTextAlignment(TextAlignment.CENTER);
        võitjaLabel.setAlignment(Pos.CENTER);
        võitjaLabel.setMaxWidth(winnerLabelMaxWidth);

        StackPane võitjaKast = new StackPane();
        võitjaKast.setPadding(new Insets(20, 32, 20, 32));
        String sisuTaust = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        võitjaKast.setStyle(sisuTaust + " -fx-background-radius: 12; -fx-border-radius: 12;");
        võitjaKast.setMaxWidth(contentAreaWidth);
        võitjaKast.getChildren().add(võitjaLabel);

        // Results list card.
        VBox tulemusteKast = new VBox();
        tulemusteKast.setAlignment(Pos.CENTER_LEFT);
        tulemusteKast.setSpacing(8);
        tulemusteKast.setPadding(new Insets(20));
        tulemusteKast.setPrefWidth(contentAreaWidth);
        tulemusteKast.setMaxWidth(contentAreaWidth);
        tulemusteKast.setStyle(sisuTaust + " -fx-background-radius: 12; -fx-border-radius: 12;");

        Label tulemusedPealkiri = new Label("KÕIK TULEMUSED:");
        tulemusedPealkiri.setFont(boldFont);
        tulemusedPealkiri.setStyle(themeController.getLabelTextFill());
        tulemusedPealkiri.setTextAlignment(TextAlignment.LEFT);
        tulemusedPealkiri.setAlignment(Pos.CENTER_LEFT);
        tulemusedPealkiri.setMaxWidth(resultLabelMaxWidth);
        tulemusteKast.getChildren().add(tulemusedPealkiri);

        for (String tulemusTekst : tulemuseRead) {
            Label tulemusLabel = new Label(tulemusTekst);
            tulemusLabel.setFont(regularFont);
            tulemusLabel.setStyle(themeController.getLabelTextFill());
            tulemusLabel.setWrapText(true);
            tulemusLabel.setTextAlignment(TextAlignment.LEFT);
            tulemusLabel.setAlignment(Pos.CENTER_LEFT);
            tulemusLabel.setMaxWidth(resultLabelMaxWidth);
            tulemusteKast.getChildren().add(tulemusLabel);
        }

        // Action buttons.
        HBox nupud = new HBox();
        nupud.setAlignment(Pos.CENTER);
        nupud.setSpacing(GameConstants.SPACING_NORMAL);

        Button peamenüüNupp = new Button("Tagasi peamenüüsse");
        peamenüüNupp.setPrefWidth(GameConstants.BUTTON_EXIT_WIDTH);
        peamenüüNupp.setPrefHeight(GameConstants.BUTTON_EXIT_HEIGHT);
        peamenüüNupp.setStyle(GameConstants.BUTTON_WHEAT_STYLE);
        UIHelper.attachButtonAnimations(peamenüüNupp, soundController);
        peamenüüNupp.setOnAction(e -> {
            lõppDialoog.close();
            onReturnToMenu.run();
        });

        Button sulgeNupp = new Button("Sulge");
        sulgeNupp.setPrefWidth(GameConstants.BUTTON_EXIT_WIDTH);
        sulgeNupp.setPrefHeight(GameConstants.BUTTON_EXIT_HEIGHT);
        sulgeNupp.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(sulgeNupp, soundController);
        sulgeNupp.setOnAction(e -> {
            lõppDialoog.close();
            Platform.exit();
        });

        nupud.getChildren().addAll(peamenüüNupp, sulgeNupp);

        StackPane taust = ThemedDialogBuilder.create(themeController)
            .withMinWidth(konteinerMinWidth)
            .withMaxWidth(konteinerMaxWidth)
            .withPreferredWidth(preferredWidth)
            .build(võitjaKast, tulemusteKast, nupud);

        Scene stseen = new Scene(taust);
        lõppDialoog.setResizable(false);
        lõppDialoog.setScene(stseen);
        lõppDialoog.sizeToScene();
        lõppDialoog.show();
    }

}
