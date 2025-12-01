package projekt.yahtzee.ui.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
     * @param pealava parent stage that owns the dialog
     * @param themeController controller providing theme-dependent styles
     * @param soundController controller responsible for sound effects
     * @param onExit callback executed after the user confirms exiting
     */
    public static void show(Stage pealava, ThemeController themeController, SoundController soundController, Runnable onExit) {
        Stage exitDialog = new Stage();
        exitDialog.initOwner(pealava);
        exitDialog.setTitle("Mängust väljumine");
        
        StackPane lahkumineVaheleht = new StackPane();
        lahkumineVaheleht.setStyle(themeController.getBackgroundStyle());
        lahkumineVaheleht.setPadding(new Insets(30));
        
        Font tekstFont = GameConstants.getCheckboxLabelFont();
        double tekstikastiPadding = 64;
        double konteineriPadding = 80;
        double konteineriMinLaius = 360;
        double konteineriMaxLaius = 520;

        String message = "Kas soovid mängust väljuda ja peamenüüsse naasta?";
        double sõnumiLaius = measureTextWidth(message, tekstFont);
        double soovitudSisemineLaius = Math.max(320, sõnumiLaius + tekstikastiPadding);
        double eelistatudLaius = Math.max(konteineriMinLaius,
            Math.min(konteineriMaxLaius, soovitudSisemineLaius + konteineriPadding));
        double sisuAlaLaius = eelistatudLaius - konteineriPadding;
        double tekstiMaxLaius = Math.max(0, sisuAlaLaius - tekstikastiPadding);

        VBox lahkumineElemendid = new VBox();
        lahkumineElemendid.setAlignment(Pos.CENTER);
        lahkumineElemendid.setSpacing(GameConstants.SPACING_LARGE);
        lahkumineElemendid.setPadding(new Insets(20, 40, 20, 40));
        lahkumineElemendid.setMinWidth(konteineriMinLaius);
        lahkumineElemendid.setMaxWidth(konteineriMaxLaius);
        lahkumineElemendid.setPrefWidth(eelistatudLaius);
        String konteineriTaust = "-fx-background-color: " + themeController.getBoxBackgroundColor() + ";";
        lahkumineElemendid.setStyle(konteineriTaust + " -fx-background-radius: 18; -fx-border-radius: 18;");

        Label väljumineTekst = new Label(message);
        väljumineTekst.setFont(tekstFont);
        väljumineTekst.setStyle(themeController.getLabelTextFill());
        väljumineTekst.setWrapText(true);
        väljumineTekst.setTextAlignment(TextAlignment.CENTER);
        väljumineTekst.setAlignment(Pos.CENTER);
        väljumineTekst.setMaxWidth(tekstiMaxLaius);

        StackPane tekstikast = new StackPane();
        tekstikast.setPadding(new Insets(20, 32, 20, 32));
        String tekstikastiTaust = "-fx-background-color: " + themeController.getTitleBoxBackground() + ";";
        tekstikast.setStyle(tekstikastiTaust + " -fx-background-radius: 12; -fx-border-radius: 12;");
        tekstikast.setMaxWidth(sisuAlaLaius);
        tekstikast.getChildren().add(väljumineTekst);

        HBox väljumisNupud = new HBox();
        väljumisNupud.setAlignment(Pos.CENTER);
        väljumisNupud.setSpacing(GameConstants.SPACING_LARGE);
        
        Button jahNupp = new Button(GameConstants.BUTTON_YES);
        jahNupp.setStyle(GameConstants.BUTTON_ERROR_STYLE);
        jahNupp.setScaleX(GameConstants.SCALE_BUTTON_MEDIUM);
        jahNupp.setScaleY(GameConstants.SCALE_BUTTON_MEDIUM);
        UIHelper.attachButtonAnimations(jahNupp, soundController);
        jahNupp.setOnAction(e -> {
            exitDialog.close();
            if (onExit != null) {
                onExit.run();
            }
        });
        
        Button eiNupp = new Button(GameConstants.BUTTON_NO);
        eiNupp.setStyle(GameConstants.BUTTON_SUCCESS_STYLE);
        eiNupp.setScaleX(GameConstants.SCALE_BUTTON_MEDIUM);
        eiNupp.setScaleY(GameConstants.SCALE_BUTTON_MEDIUM);
        UIHelper.attachButtonAnimations(eiNupp, soundController);
        eiNupp.setOnAction(e -> exitDialog.close());
        
        väljumisNupud.getChildren().addAll(jahNupp, eiNupp);
        lahkumineElemendid.getChildren().addAll(tekstikast, väljumisNupud);
        lahkumineVaheleht.getChildren().add(lahkumineElemendid);
        
        Scene lahkumisStseen = new Scene(lahkumineVaheleht);
        exitDialog.setResizable(false);
        exitDialog.setScene(lahkumisStseen);
        exitDialog.sizeToScene();
        exitDialog.show();
    }

    private static double measureTextWidth(String content, Font font) {
        Text textNode = new Text(content);
        textNode.setFont(font);
        return textNode.getLayoutBounds().getWidth();
    }
}
