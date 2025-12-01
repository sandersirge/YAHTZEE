package projekt.yahtzee;

import javafx.application.Application;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projekt.yahtzee.controller.data.StatisticsController;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.navigation.SceneManager;

import java.util.List;

public class YahtzeeApplication extends Application {
    private SceneManager sceneManager;
    private SoundController soundController;
    private ThemeController themeController;
    private StatisticsController statisticsController;
    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        initializeControllers();

        sceneManager = new SceneManager(primaryStage, themeController, soundController, statisticsController);

        primaryStage.setTitle("Yahtzee tĆ¤ringumĆ¤ng");
        primaryStage.setResizable(true);

        soundController.startMenuMusic();

        showMainMenu();
        primaryStage.show();
    }

    private void initializeControllers() {
        gameController = new GameController();
        statisticsController = new StatisticsController();
        themeController = new ThemeController();
        soundController = new SoundController();
    }

    private void showMainMenu() {
        sceneManager.showMainMenu(this::showSetupScene);
    }

    private void showSetupScene() {
        sceneManager.showSetupScene(this::startNewGame, this::showMainMenu);
    }

    private void startNewGame(List<TextField> playerFields) {
        soundController.switchMusic(false);
        gameController = new GameController();
        sceneManager.showGameBoard(gameController, playerFields, this::returnToMainMenu);
    }

    private void returnToMainMenu() {
        soundController.switchMusic(true);
        showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

