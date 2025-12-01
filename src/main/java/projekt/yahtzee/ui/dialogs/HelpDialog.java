package projekt.yahtzee.ui.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.handlers.UIHelper;
import projekt.yahtzee.util.GameConstants;

import java.util.List;

/**
 * Displays the in-game keyboard help in a themed dialog.
 */
public final class HelpDialog {
    private HelpDialog() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Shows the keyboard help dialog using Yahtzee themed styling.
     *
     * @param ownerStage parent stage for modality
     * @param themeController theme controller supplying colors
     * @param soundController sound controller for button effects
     * @param helpLines shortcut descriptions to render
     * @return the displayed help dialog stage
     */
    public static Stage show(Stage ownerStage,
                             ThemeController themeController,
                             SoundController soundController,
                             List<String> helpLines) {
        Stage helpStage = new Stage();
        helpStage.initOwner(ownerStage);
        helpStage.setTitle("Klaviatuuri juhised");

        Font titleFont = GameConstants.getCellFontBold();
        Font lineFont = GameConstants.getCellFont();

        double containerPadding = 80;
        double cardHorizontalPadding = 40;
        double containerMinWidth = 380;
        double containerMaxWidth = 720;

        double titleWidth = DialogLayoutUtil.measureTextWidth("Klaviatuuri kiirklahvid", titleFont);
        double linesWidth = helpLines.stream()
            .map(line -> "\u2022 " + line)
            .mapToDouble(text -> DialogLayoutUtil.measureTextWidth(text, lineFont))
            .max().orElse(0.0);

        double desiredInnerWidth = Math.max(titleWidth + cardHorizontalPadding,
            Math.max(420, linesWidth + cardHorizontalPadding));
        double preferredWidth = Math.max(containerMinWidth,
            Math.min(containerMaxWidth, desiredInnerWidth + containerPadding));
        double contentAreaWidth = preferredWidth - containerPadding;
        double lineMaxWidth = Math.max(0, contentAreaWidth - cardHorizontalPadding);

        String cardStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";

        Label titleLabel = new Label("Klaviatuuri kiirklahvid");
        titleLabel.setFont(titleFont);
        titleLabel.setStyle(themeController.getLabelTextFill());
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(lineMaxWidth);

        StackPane titleCard = new StackPane();
        titleCard.setPadding(new Insets(20, 32, 20, 32));
        titleCard.setMaxWidth(contentAreaWidth);
        titleCard.setStyle(cardStyle + " -fx-background-radius: 12; -fx-border-radius: 12;");
        titleCard.getChildren().add(titleLabel);

        VBox helpCard = new VBox();
        helpCard.setAlignment(Pos.TOP_LEFT);
        helpCard.setSpacing(10);
        helpCard.setPadding(new Insets(20));
        helpCard.setPrefWidth(contentAreaWidth);
        helpCard.setMaxWidth(contentAreaWidth);
        helpCard.setStyle(cardStyle + " -fx-background-radius: 12; -fx-border-radius: 12;");

        for (String line : helpLines) {
            Label lineLabel = new Label("\u2022 " + line);
            lineLabel.setFont(lineFont);
            lineLabel.setStyle(themeController.getLabelTextFill());
            lineLabel.setWrapText(true);
            lineLabel.setTextAlignment(TextAlignment.LEFT);
            lineLabel.setAlignment(Pos.TOP_LEFT);
            lineLabel.setMaxWidth(lineMaxWidth);
            helpCard.getChildren().add(lineLabel);
        }

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(GameConstants.SPACING_NORMAL);

        Button closeButton = new Button("Sulge");
        closeButton.setPrefWidth(GameConstants.BUTTON_EXIT_WIDTH);
        closeButton.setPrefHeight(GameConstants.BUTTON_EXIT_HEIGHT);
        closeButton.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(closeButton, soundController);
        closeButton.setOnAction(event -> helpStage.close());

        buttons.getChildren().add(closeButton);

        StackPane root = ThemedDialogBuilder.create(themeController)
            .withMinWidth(containerMinWidth)
            .withMaxWidth(containerMaxWidth)
            .withPreferredWidth(preferredWidth)
            .build(titleCard, helpCard, buttons);

        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.H || code == KeyCode.ESCAPE) {
                helpStage.close();
                event.consume();
            }
        });
        helpStage.setResizable(false);
        helpStage.setScene(scene);
        helpStage.sizeToScene();
        helpStage.show();

        return helpStage;
    }

}

