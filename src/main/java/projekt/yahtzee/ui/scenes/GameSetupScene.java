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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages the setup scene where players enter how many people are participating and their names.
 */
public class GameSetupScene {
    private final ThemeController themeController;
    private final SoundController soundController;

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
        StackPane mang = new StackPane();
        mang.setStyle(themeController.getBackgroundStyle());
        
        VBox elemendid = new VBox();
        elemendid.setSpacing(50);
        elemendid.setAlignment(Pos.CENTER);
        elemendid.setPadding(new Insets(50));
        elemendid.setMaxWidth(GameConstants.SETUP_WINDOW_WIDTH - 120);
        elemendid.setMaxHeight(GameConstants.SETUP_WINDOW_HEIGHT - 60);
        String elemendidTaust = "-fx-background-color: " + themeController.getBoxBackgroundColor() + ";";
        elemendid.setStyle(elemendidTaust + " -fx-border-radius: 15; -fx-background-radius: 15;");
        
        VBox mangijad = new VBox();
        mangijad.setSpacing(30);
        mangijad.setAlignment(Pos.CENTER);
        mangijad.setPadding(new Insets(0));

        // Title label.
        Label tekst = new Label(GameConstants.LABEL_PLAYER_COUNT);
        tekst.setFont(GameConstants.getTitleFont());
        tekst.setStyle(themeController.getLabelTextFill());
        
        StackPane pealkirjaKast = new StackPane();
        pealkirjaKast.setPadding(new Insets(15, 30, 15, 30));
        pealkirjaKast.setMaxWidth(Region.USE_PREF_SIZE);
        String pealkirjaTaust = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        pealkirjaKast.setStyle(pealkirjaTaust + " -fx-border-radius: 10; -fx-background-radius: 10;");
        pealkirjaKast.getChildren().add(tekst);
        
        // Warning banner.
        Label hoiatus = new Label();
        hoiatus.setFont(GameConstants.getMediumFont());
        hoiatus.setStyle("-fx-text-fill: white;");
        
        StackPane hoiatusKast = new StackPane();
        hoiatusKast.setPadding(new Insets(10, 20, 10, 20));
        hoiatusKast.setMaxWidth(Region.USE_PREF_SIZE);
        hoiatusKast.setVisible(false);
        hoiatusKast.setManaged(false);
        String hoiatusTaust = "-fx-background-color: #EF5350; -fx-border-radius: 8; -fx-background-radius: 8;";
        hoiatusKast.setStyle(hoiatusTaust);
        hoiatusKast.getChildren().add(hoiatus);

        // Player-count input field.
        VBox arvuVBox = new VBox();
        arvuVBox.setAlignment(Pos.CENTER);
        arvuVBox.setSpacing(0);
        arvuVBox.setPadding(new Insets(0, 0, 30, 0));
        
        TextField mangijateArv = new TextField();
        mangijateArv.setMaxWidth(GameConstants.INPUT_FIELD_MAX_WIDTH);
        mangijateArv.setScaleX(GameConstants.SCALE_TEXTFIELD);
        mangijateArv.setScaleY(GameConstants.SCALE_TEXTFIELD);
        mangijateArv.setStyle(themeController.getTextFieldStyle(false));
        mangijateArv.setPromptText("Sisesta number (" + GameConstants.MIN_PLAYERS + "-" + GameConstants.MAX_PLAYERS + ")");
        
        // Focus styling.
        addTextFieldFocusListener(mangijateArv);
        arvuVBox.getChildren().add(mangijateArv);

        // Action buttons.
        Button alusta = new Button(GameConstants.BUTTON_START_GAME);
        alusta.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        alusta.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        alusta.setStyle(GameConstants.BUTTON_SUCCESS_STYLE);
        UIHelper.attachButtonAnimations(alusta, soundController);

        Button tagasi = new Button("↩ Tagasi peamenüüsse");
        tagasi.setScaleX(GameConstants.SCALE_BUTTON_LARGE);
        tagasi.setScaleY(GameConstants.SCALE_BUTTON_LARGE);
        tagasi.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        UIHelper.attachButtonAnimations(tagasi, soundController);
        tagasi.setOnAction(e -> onBackToMenu.run());
        
        HBox nupudHBox = new HBox();
        nupudHBox.setAlignment(Pos.CENTER);
        nupudHBox.setSpacing(150);
        nupudHBox.setPadding(new Insets(40, 0, 0, 0));

        List<TextField> nimed = new ArrayList<>();

        // React to player-count changes.
        mangijateArv.textProperty().addListener((ov, vana, uus) -> {
            Platform.runLater(() -> {
                try {
                    if (uus.equalsIgnoreCase("")) throw new IllegalArgumentException();
                    int arv = Integer.parseInt(uus);
                    if (arv > GameConstants.MAX_PLAYERS || arv < GameConstants.MIN_PLAYERS) {
                        throw new ArithmeticException(GameConstants.ERROR_PLAYER_COUNT);
                    }
                    mangijad.getChildren().clear();
                    nimed.clear();
                    for (int i = 0; i < arv; i++) {
                        TextField mangija = createPlayerTextField(i + 1);
                        nimed.add(mangija);
                        mangijad.getChildren().add(mangija);
                    }
                    mangijad.setAlignment(Pos.CENTER);
                    hoiatus.setText("");
                    hoiatusKast.setVisible(false);
                    hoiatusKast.setManaged(false);
                    if (!nupudHBox.getChildren().contains(alusta)) {
                        nupudHBox.getChildren().clear();
                        nupudHBox.getChildren().addAll(alusta, tagasi);
                    }
                } catch (IllegalArgumentException ex) {
                    if (uus.equalsIgnoreCase("")) {
                        hoiatus.setText(GameConstants.ERROR_EMPTY_FIELD);
                    } else {
                        hoiatus.setText(GameConstants.ERROR_ONLY_INTEGERS);
                    }
                    hoiatusKast.setVisible(true);
                    hoiatusKast.setManaged(true);
                    nupudHBox.getChildren().clear();
                    nupudHBox.getChildren().add(tagasi);
                } catch (ArithmeticException ex) {
                    hoiatus.setText(ex.getMessage());
                    hoiatusKast.setVisible(true);
                    hoiatusKast.setManaged(true);
                    nupudHBox.getChildren().clear();
                    nupudHBox.getChildren().add(tagasi);
                }
            });
        });
        
        // Configure start-button action.
        alusta.setOnAction(e -> onStartGame.accept(nimed));

        nupudHBox.getChildren().add(tagasi);
        
        elemendid.getChildren().addAll(pealkirjaKast, hoiatusKast, arvuVBox, mangijad, nupudHBox);
        mang.getChildren().add(elemendid);
        StackPane.setAlignment(elemendid, Pos.CENTER);

        return new Scene(mang, GameConstants.SETUP_WINDOW_WIDTH, GameConstants.SETUP_WINDOW_HEIGHT);
    }

    /**
     * Adds a focus listener that switches the text field style when focus changes.
     */
    private void addTextFieldFocusListener(TextField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            field.setStyle(themeController.getTextFieldStyle(newVal));
        });
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
