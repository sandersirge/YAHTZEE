package projekt.yahtzee.ui.scenes;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.handlers.UIHelper;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.util.UIFonts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages the setup scene where players enter how many people are participating and their names.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public class GameSetupScene {
    private final ThemeController themeController;
    private final SoundController soundController;

    /**
     * Constructs a new setup scene manager.
     *
     * @param themeController controller providing theme-dependent styles
     * @param soundController controller responsible for playing sounds
     */
    public GameSetupScene(ThemeController themeController, SoundController soundController) {
        this.themeController = themeController;
        this.soundController = soundController;
    }

    /**
     * Builds the setup UI that collects player count and player names.
     *
     * @param onStartGame callback invoked when the game starts (receives the list of name fields)
     * @param onBackToMenu callback invoked when the user wants to return to the main menu
     * @return configured setup scene
     */
    public Scene createSetupScene(Consumer<List<TextField>> onStartGame, Runnable onBackToMenu) {
        StackPane setupRoot = new StackPane();
        setupRoot.setStyle(themeController.getBackgroundStyle());

        VBox contentContainer = new VBox();
        contentContainer.setSpacing(50);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setPadding(new Insets(50));
        contentContainer.setMaxWidth(GameConstants.SETUP_WINDOW_WIDTH - 120);
        contentContainer.setMaxHeight(GameConstants.SETUP_WINDOW_HEIGHT - 60);
        String contentBackgroundStyle = "-fx-background-color: " + themeController.getBoxBackgroundColor() + ";";
        contentContainer.setStyle(contentBackgroundStyle + " -fx-border-radius: 15; -fx-background-radius: 15;");

        VBox playersBox = new VBox();
        playersBox.setSpacing(30);
        playersBox.setAlignment(Pos.CENTER);
        playersBox.setPadding(new Insets(0));

        // Title label.
        Label titleLabel = new Label(GameConstants.LABEL_PLAYER_COUNT);
        titleLabel.setFont(UIFonts.getTitleFont());
        titleLabel.setStyle(themeController.getLabelTextFill());

        StackPane titleContainer = new StackPane();
        titleContainer.setPadding(new Insets(15, 30, 15, 30));
        titleContainer.setMaxWidth(Region.USE_PREF_SIZE);
        String titleBackgroundStyle = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        titleContainer.setStyle(titleBackgroundStyle + " -fx-border-radius: 10; -fx-background-radius: 10;");
        titleContainer.getChildren().add(titleLabel);
        
        // Warning banner.
        Label warningLabel = new Label();
        warningLabel.setFont(UIFonts.getMediumFont());
        warningLabel.setStyle("-fx-text-fill: white;");

        StackPane warningContainer = new StackPane();
        warningContainer.setPadding(new Insets(10, 20, 10, 20));
        warningContainer.setMaxWidth(Region.USE_PREF_SIZE);
        warningContainer.setVisible(false);
        warningContainer.setManaged(false);
        String warningBackgroundStyle = "-fx-background-color: #EF5350; -fx-border-radius: 8; -fx-background-radius: 8;";
        warningContainer.setStyle(warningBackgroundStyle);
        warningContainer.getChildren().add(warningLabel);

        // Player-count input field.
        VBox playerCountBox = new VBox();
        playerCountBox.setAlignment(Pos.CENTER);
        playerCountBox.setSpacing(0);
        playerCountBox.setPadding(new Insets(0, 0, 30, 0));

        TextField playerCountField = new TextField();
        playerCountField.setMaxWidth(GameConstants.INPUT_FIELD_MAX_WIDTH);
        playerCountField.setScaleX(GameConstants.SCALE_TEXTFIELD);
        playerCountField.setScaleY(GameConstants.SCALE_TEXTFIELD);
        playerCountField.setStyle(themeController.getTextFieldStyle(false));
        playerCountField.setPromptText("Sisesta number (" + GameConstants.MIN_PLAYERS + "-" + GameConstants.MAX_PLAYERS + ")");
        
        // Focus styling.
        addTextFieldFocusListener(playerCountField);
        playerCountBox.getChildren().add(playerCountField);

        // Action buttons.
        Button startButton = new Button(GameConstants.BUTTON_START_GAME);
        startButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        startButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        startButton.setStyle(GameConstants.BUTTON_SUCCESS_STYLE);
        UIHelper.attachButtonAnimations(startButton, soundController);

        Button backButton = new Button("↩ Tagasi peamenüüsse");
        backButton.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        backButton.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        backButton.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(backButton, soundController);
        backButton.setOnAction(e -> onBackToMenu.run());

        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setSpacing(GameConstants.SPACING_DICE_CHECKBOX_ROW);
        buttonRow.setPadding(new Insets(40, 0, 0, 0));

        List<TextField> playerNameFields = new ArrayList<>();

        // React to player-count changes.
        playerCountField.textProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
                try {
                    if (newValue.equalsIgnoreCase("")) throw new IllegalArgumentException();
                    int playerCount = Integer.parseInt(newValue);
                    if (playerCount > GameConstants.MAX_PLAYERS || playerCount < GameConstants.MIN_PLAYERS) {
                        throw new ArithmeticException(GameConstants.ERROR_PLAYER_COUNT);
                    }
                    playersBox.getChildren().clear();
                    playerNameFields.clear();
                    for (int i = 0; i < playerCount; i++) {
                        TextField playerField = createPlayerTextField(i + 1);
                        playerNameFields.add(playerField);
                        playersBox.getChildren().add(playerField);
                    }
                    playersBox.setAlignment(Pos.CENTER);
                    warningLabel.setText("");
                    warningContainer.setVisible(false);
                    warningContainer.setManaged(false);
                    if (!buttonRow.getChildren().contains(startButton)) {
                        buttonRow.getChildren().clear();
                        buttonRow.getChildren().addAll(startButton, backButton);
                    }
                } catch (IllegalArgumentException ex) {
                    if (newValue.equalsIgnoreCase("")) {
                        warningLabel.setText(GameConstants.ERROR_EMPTY_FIELD);
                    } else {
                        warningLabel.setText(GameConstants.ERROR_ONLY_INTEGERS);
                    }
                    warningContainer.setVisible(true);
                    warningContainer.setManaged(true);
                    buttonRow.getChildren().clear();
                    buttonRow.getChildren().add(backButton);
                } catch (ArithmeticException ex) {
                    warningLabel.setText(ex.getMessage());
                    warningContainer.setVisible(true);
                    warningContainer.setManaged(true);
                    buttonRow.getChildren().clear();
                    buttonRow.getChildren().add(backButton);
                }
            }));

        // Configure start-button action.
        startButton.setOnAction(e -> onStartGame.accept(playerNameFields));

        buttonRow.getChildren().add(backButton);

        contentContainer.getChildren().addAll(titleContainer, warningContainer, playerCountBox, playersBox, buttonRow);
        setupRoot.getChildren().add(contentContainer);
        StackPane.setAlignment(contentContainer, Pos.CENTER);

        return new Scene(setupRoot, GameConstants.SETUP_WINDOW_WIDTH, GameConstants.SETUP_WINDOW_HEIGHT);
    }

    /**
     * Adds a focus listener that switches the text field style when focus changes.
     */
    private void addTextFieldFocusListener(TextField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) ->
            field.setStyle(themeController.getTextFieldStyle(newVal)));
    }
    
    /**
     * Creates a name input field for a player.
     */
    private TextField createPlayerTextField(int playerNumber) {
        TextField field = new TextField();
        field.setMaxWidth(GameConstants.INPUT_FIELD_MAX_WIDTH);
        field.setScaleY(GameConstants.SCALE_TEXTFIELD);
        field.setScaleX(GameConstants.SCALE_TEXTFIELD);
        field.setPromptText(GameConstants.getPlayerNamePrompt(playerNumber));
        field.setStyle(themeController.getTextFieldStyle(false));
        addTextFieldFocusListener(field);
        return field;
    }

}
