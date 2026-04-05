package projekt.yahtzee.ui.navigation;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import projekt.yahtzee.controller.data.StatisticsController;
import projekt.yahtzee.controller.game.GameController;
import projekt.yahtzee.controller.ui.SoundController;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.ui.handlers.UIHelper;
import projekt.yahtzee.ui.scenes.GameBoardScene;
import projekt.yahtzee.ui.scenes.GameSetupScene;
import projekt.yahtzee.ui.scenes.MainMenuScene;
import projekt.yahtzee.util.GameConstants;

import java.util.List;
import java.util.function.Consumer;

/**
 * Coordinates scene creation and stage transitions for the application.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public class SceneManager {
    private final Stage stage;
    private final ThemeController themeController;
    private final SoundController soundController;
    private final StatisticsController statisticsController;

    private MainMenuScene mainMenuScene;
    private GameSetupScene gameSetupScene;

    /**
     * Constructs a new scene manager that coordinates scene transitions.
     *
     * @param stage                primary application stage
     * @param themeController      controller managing theme styling
     * @param soundController      controller managing sound effects
     * @param statisticsController controller responsible for statistics persistence
     */
    public SceneManager(Stage stage,
                        ThemeController themeController,
                        SoundController soundController,
                        StatisticsController statisticsController) {
        this.stage = stage;
        this.themeController = themeController;
        this.soundController = soundController;
        this.statisticsController = statisticsController;
    }

    /**
     * Shows the main menu scene.
     *
     * @param onStartNewGame callback executed when the user starts a new game
     */
    public void showMainMenu(Runnable onStartNewGame) {
        if (mainMenuScene == null) {
            mainMenuScene = new MainMenuScene(stage, themeController, soundController, statisticsController);
        }

        Scene mainMenu = mainMenuScene.createMainMenu(onStartNewGame);
        UIHelper.addFullscreenToggle(mainMenu, stage);
        stage.setScene(mainMenu);
        stage.setWidth(GameConstants.MAIN_MENU_WIDTH);
        stage.setHeight(GameConstants.MAIN_MENU_HEIGHT);
        stage.centerOnScreen();
    }

    /**
     * Shows the game setup scene where players enter their names.
     *
     * @param onStartGame  callback invoked when the game is ready to start
     * @param onBackToMenu callback invoked when the user navigates back
     */
    public void showSetupScene(Consumer<List<TextField>> onStartGame, Runnable onBackToMenu) {
        if (gameSetupScene == null) {
            gameSetupScene = new GameSetupScene(themeController, soundController);
        }

        Scene setupScene = gameSetupScene.createSetupScene(onStartGame, onBackToMenu);
        UIHelper.addFullscreenToggle(setupScene, stage);
        stage.setScene(setupScene);
    }

    /**
     * Shows the game board scene and sizes the stage to the primary screen.
     *
     * @param gameController controller managing game logic
     * @param playerFields   list of player name input fields
     * @param onGameExit     callback invoked when the user exits the game
     */
    public void showGameBoard(GameController gameController, List<TextField> playerFields, Runnable onGameExit) {
        GameBoardScene gameBoardScene = new GameBoardScene(gameController, themeController, soundController, statisticsController);
        Scene gameScene = gameBoardScene.createGameBoardScene(stage, playerFields, onGameExit);
        UIHelper.addFullscreenToggle(gameScene, stage);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setScene(gameScene);

        Platform.runLater(() -> gameScene.getRoot().requestFocus());
    }
}

