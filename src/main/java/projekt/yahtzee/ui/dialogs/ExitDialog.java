package projekt.yahtzee.ui.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.handlers.UIHelper;

/**
 * Builds the confirmation dialog shown when the player tries to exit the game.
 */
public class ExitDialog {
    
    /**
     * Displays the exit confirmation dialog.
     *
    * @param ownerStage parent stage that owns the dialog
     * @param themeController controller providing theme-dependent styles
     * @param soundController controller responsible for sound effects
     * @param onExit callback executed after the user confirms exiting
     */
    public static Stage show(Stage ownerStage, ThemeController themeController, SoundController soundController, Runnable onExit) {
        Stage exitDialog = new Stage();
        exitDialog.initOwner(ownerStage);
        exitDialog.setTitle("Mängust väljumine");
        
        StackPane exitRoot = new StackPane();
        exitRoot.setStyle(themeController.getBackgroundStyle());
        exitRoot.setPadding(new Insets(30));
        
        Font messageFont = GameConstants.getCheckboxLabelFont();
        double messageBoxPadding = 64;
        double containerPadding = 80;
        double containerMinWidth = 360;
        double containerMaxWidth = 520;

        String message = "Kas soovid mängust väljuda ja peamenüüsse naasta?";
        double messageWidth = measureTextWidth(message, messageFont);
        double desiredInnerWidth = Math.max(320, messageWidth + messageBoxPadding);
        double preferredWidth = Math.max(containerMinWidth,
            Math.min(containerMaxWidth, desiredInnerWidth + containerPadding));
        double contentAreaWidth = preferredWidth - containerPadding;
        double messageMaxWidth = Math.max(0, contentAreaWidth - messageBoxPadding);

        VBox exitContent = new VBox();
        exitContent.setAlignment(Pos.CENTER);
        exitContent.setSpacing(GameConstants.SPACING_LARGE);
        exitContent.setPadding(new Insets(20, 40, 20, 40));
        exitContent.setMinWidth(containerMinWidth);
        exitContent.setMaxWidth(containerMaxWidth);
        exitContent.setPrefWidth(preferredWidth);
        String containerBackgroundStyle = "-fx-background-color: " + themeController.getBoxBackgroundColor() + ";";
        exitContent.setStyle(containerBackgroundStyle + " -fx-background-radius: 18; -fx-border-radius: 18;");

        Label messageLabel = new Label(message);
        messageLabel.setFont(messageFont);
        messageLabel.setStyle(themeController.getLabelTextFill());
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(messageMaxWidth);

        StackPane messageContainer = new StackPane();
        messageContainer.setPadding(new Insets(20, 32, 20, 32));
        String messageContainerBackground = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        messageContainer.setStyle(messageContainerBackground + " -fx-background-radius: 12; -fx-border-radius: 12;");
        messageContainer.setMaxWidth(contentAreaWidth);
        messageContainer.getChildren().add(messageLabel);

        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setSpacing(GameConstants.SPACING_LARGE);

        Button yesButton = new Button(GameConstants.BUTTON_YES);
        yesButton.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        yesButton.setScaleX(GameConstants.SCALE_BUTTON_MEDIUM);
        yesButton.setScaleY(GameConstants.SCALE_BUTTON_MEDIUM);
        UIHelper.attachButtonAnimations(yesButton, soundController);
        yesButton.setOnAction(e -> {
            exitDialog.close();
            if (onExit != null) {
                onExit.run();
            }
        });
        
        Button noButton = new Button(GameConstants.BUTTON_NO);
        noButton.setStyle(GameConstants.BUTTON_SUCCESS_STYLE);
        noButton.setScaleX(GameConstants.SCALE_BUTTON_MEDIUM);
        noButton.setScaleY(GameConstants.SCALE_BUTTON_MEDIUM);
        UIHelper.attachButtonAnimations(noButton, soundController);
        noButton.setOnAction(e -> exitDialog.close());

        buttonRow.getChildren().addAll(yesButton, noButton);
        exitContent.getChildren().addAll(messageContainer, buttonRow);
        exitRoot.getChildren().add(exitContent);

        Scene exitScene = new Scene(exitRoot);
        exitScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                exitDialog.close();
                event.consume();
            }
        });
        exitDialog.setResizable(false);
        exitDialog.setScene(exitScene);
        exitDialog.sizeToScene();
        exitDialog.show();
        return exitDialog;
    }

    private static double measureTextWidth(String content, Font font) {
        Text textNode = new Text(content);
        textNode.setFont(font);
        return textNode.getLayoutBounds().getWidth();
    }
}
