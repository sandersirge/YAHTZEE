package projekt.yahtzee.util;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Contains all constants for the Yahtzee game UI.
 * Centralizes colors, fonts, sizes, and text constants to avoid hardcoded values.
 * 
 * @author Yahtzee Game Project
 * @version 1.0
 */
public class GameConstants {
    
    // ========== WINDOW SIZES ==========
    public static final double MAIN_WINDOW_WIDTH = 1920;
    public static final double MAIN_WINDOW_HEIGHT = 1080;
    public static final double MAIN_MENU_WIDTH = 1000;
    public static final double MAIN_MENU_HEIGHT = 800;
    public static final double SETUP_WINDOW_WIDTH = 1000;
    public static final double SETUP_WINDOW_HEIGHT = 1100;
    public static final double EXIT_WINDOW_WIDTH = 700;
    public static final double EXIT_WINDOW_HEIGHT = 700;
    
    // ========== DICE PANEL SIZES ==========
    public static final double DICE_PANEL_WIDTH = 800;
    public static final double DICE_PANEL_HEIGHT = 1080;
    public static final double DICE_PANEL_UPPER_HEIGHT = 540;
    public static final double DICE_PANEL_LOWER_HEIGHT = 540;
    public static final double DICE_CONTAINER_WIDTH = 700;
    public static final double DICE_CONTAINER_HEIGHT = 130;
    public static final double CHECKBOX_CONTAINER_HEIGHT = 100;
    public static final double TEXT_CONTAINER_HEIGHT = 100;
    
    // ========== SCOREBOARD SIZES ==========
    public static final double SCOREBOARD_WIDTH = 1120;
    public static final double SCOREBOARD_HEIGHT = 1080;
    public static final double COMBO_COLUMN_WIDTH = 300;
    public static final int TOTAL_ROWS = 18;
    
    // ========== DICE SETTINGS ==========
    public static final int DICE_COUNT = 5;
    public static final double DICE_IMAGE_SIZE = 125;
    public static final double DICE_DISPLAY_SIZE = 75;
    public static final double DICE_SPACING = 40;
    
    // ========== COLORS (MATERIAL DESIGN) ==========
    public static final Color ERROR_COLOR = Color.web("#D32F2F");
    public static final Color BUTTON_SUCCESS_COLOR = Color.web("#66BB6A");
    public static final Color BUTTON_ERROR_COLOR = Color.web("#EF5350");
    public static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    public static final Color BUTTON_INFO_COLOR = Color.web("#42A5F5");
    
    // ========== CSS STYLES ==========
    // Note: most styles now come from ThemeController based on the active theme.
    public static final String CELL_BORDER_STYLE = "-fx-border-style: solid;-fx-border-width: 1;";
    
    // Material Design button styles (rounded corners + drop shadow).
    private static final String BUTTON_BASE_STYLE = "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2); -fx-font-weight: bold; -fx-padding: 10 20 10 20;";
    public static final String BUTTON_WHEAT_STYLE = "-fx-background-color: #FFD54F; -fx-text-fill: #000000; " + BUTTON_BASE_STYLE;
    public static final String BUTTON_LIGHT_STYLE = "-fx-background-color: #CFD8DC; -fx-text-fill: #000000; " + BUTTON_BASE_STYLE;
    public static final String BUTTON_SUCCESS_STYLE = "-fx-background-color: #66BB6A; -fx-text-fill: white; " + BUTTON_BASE_STYLE;
    public static final String BUTTON_ERROR_STYLE = "-fx-background-color: #EF5350; -fx-text-fill: white; " + BUTTON_BASE_STYLE;
    public static final String BUTTON_INFO_STYLE = "-fx-background-color: #42A5F5; -fx-text-fill: white; " + BUTTON_BASE_STYLE;
    public static final String BUTTON_NEUTRAL_STYLE = "-fx-background-color: #757575; -fx-text-fill: white; " + BUTTON_BASE_STYLE;
    
    // Checkbox styles.
    public static final String CHECKBOX_STYLE = "-fx-scale-x: 1.5; -fx-scale-y: 1.5;";
    public static final String CHECKBOX_HOVER_STYLE = "-fx-cursor: hand;";
    
    // Score cell styles are supplied by ThemeController.
    // Bonus and total row styles also come from ThemeController.
    
    // ========== FONTS ==========
    public static final String FONT_FAMILY = "Comfortaa";
    public static final double FONT_SIZE_TITLE = 40;
    public static final double FONT_SIZE_LARGE = 36;
    public static final double FONT_SIZE_MEDIUM = 28;
    public static final double FONT_SIZE_NORMAL = 26;
    public static final double FONT_SIZE_CELL = 20;
    public static final double FONT_SIZE_CHECKBOX_LABEL = 22;
    
    // ========== SPACING & PADDING ==========
    public static final double SPACING_LARGE = 100;
    public static final double SPACING_MEDIUM = 30;
    public static final double SPACING_NORMAL = 30;
    public static final double SPACING_SMALL = 20;
    public static final double SPACING_CHECKBOX = 40; // same as DICE_SPACING
    public static final double PADDING_STANDARD = 20;
    
    // ========== BUTTON SIZES ==========
    public static final double BUTTON_MENU_WIDTH = 250;
    public static final double BUTTON_MENU_HEIGHT = 50;
    public static final double BUTTON_EXIT_WIDTH = 180;
    public static final double BUTTON_EXIT_HEIGHT = 40;
    
    // ========== SCALE FACTORS ==========
    public static final double SCALE_BUTTON_LARGE = 1.7;
    public static final double SCALE_BUTTON_MEDIUM = 1.5;
    public static final double SCALE_TEXTFIELD = 1.5;
    
    // ========== TEXT CONSTANTS (ESTONIAN UI COPY) ==========
    public static final String TITLE_WELCOME = "Meme Yahtzee";
    public static final String TITLE_YAHTZEE = "Yahtzee";
    public static final String LABEL_PLAYER_COUNT = "Sisesta mängijate arv (maksimum 3)";
    public static final String LABEL_KEEP_DICE = "Linnukest kasti märkides saad valida, mida uuesti ei veeretata.";
    public static final String BUTTON_NEW_GAME = "Uus mäng";
    public static final String BUTTON_START_GAME = "Alusta mängu";
    public static final String BUTTON_ROLL_DICE = "Veereta täringuid";
    public static final String ROLL_BUTTON = "Veereta täringuid";
    public static final String BUTTON_EXIT_GAME = "Lahku mängust";
    public static final String BUTTON_BEST_RESULTS = "Mängutulemused";
    public static final String BUTTON_YES = "Jah";
    public static final String BUTTON_NO = "Ei";
    
    // ========== ERROR MESSAGES ==========
    public static final String ERROR_EMPTY_FIELD = "Tekstiväli ei tohi olla tühi!";
    public static final String ERROR_ONLY_INTEGERS = "Sisesta ainult täisarve tekstiväljale!";
    public static final String ERROR_PLAYER_COUNT = "Mängijate arv peab olema 1-3 vahel!";
    public static final String ERROR_CANNOT_CLICK = "Selle kasti peale vajutades\nei saa punkte!";
    
    // ========== GAME MESSAGES ==========
    public static final String FIRST_PLAYER_TURN = "1. mängija kord";
    public static final String MSG_PLAYER_TURN = ". mängija kord";
    public static final String MSG_ROLL_NUMBER = ". veeretus\nVali alleshoitavad täringud\nvõi pane endale punktid tabelisse";
    public static final String NO_MORE_ROLLS = "Sa ei saa rohkem veeretada\nVali tabelist sobivad punktid!";
    public static final String MSG_NO_MORE_ROLLS = "Sa ei saa rohkem veeretada\nVali tabelist sobivad punktid!";
    public static final String MSG_EXIT_CONFIRM = "Kas oled kindel, et soovid mängust lahkuda?";
    public static final String MSG_BONUS_AWARDED = "Boonus omistatud!";
    public static final String MSG_CURRENT_RESULTS = "Hetkel kuvatavad tulemused:";
    
    // ========== COMBO NAMES (ESTONIAN) ==========
    public static final String COMBO_PLAYERS = "Võistlejad";
    public static final String COMBO_ONES = "Ühed";
    public static final String COMBO_TWOS = "Kahed";
    public static final String COMBO_THREES = "Kolmed";
    public static final String COMBO_FOURS = "Neljad";
    public static final String COMBO_FIVES = "Viied";
    public static final String COMBO_SIXES = "Kuued";
    public static final String COMBO_BONUS = "BOONUS";
    public static final String COMBO_UPPER_SUM = "ÜLEMINE SUMMA";
    public static final String COMBO_THREE_KIND = "Kolmik";
    public static final String COMBO_FOUR_KIND = "Nelik";
    public static final String COMBO_FULL_HOUSE = "Maja";
    public static final String COMBO_SMALL_STRAIGHT = "Väike rida";
    public static final String COMBO_LARGE_STRAIGHT = "Suur rida";
    public static final String COMBO_YAHTZEE = "Yahtzee";
    public static final String COMBO_CHANCE = "Summa";
    public static final String COMBO_LOWER_SUM = "ALUMINE SUMMA";
    public static final String COMBO_TOTAL = "KOKKU";
    
    // ========== GAME RULES ==========
    public static final int MAX_PLAYERS = 3;
    public static final int MIN_PLAYERS = 1;
    public static final int MAX_ROLLS_PER_TURN = 3;
    public static final int MAX_ROLLS = 3;
    public static final int TOTAL_COMBINATIONS = 13;
    public static final int UPPER_SECTION_BONUS_THRESHOLD = 63;
    public static final int UPPER_SECTION_BONUS_POINTS = 35;
    
    // ========== FILE PATHS ==========
    public static final String RESULTS_FILE = "tulemused.txt";
    public static final String DICE_IMAGE_PATH = "/projekt/yahtzee/images/täring";
    public static final String DICE_IMAGE_EXTENSION = ".png";
    
    // ========== INPUT FIELD SETTINGS ==========
    public static final double INPUT_FIELD_MAX_WIDTH = 400;
    public static final String PLAYER_NAME_PROMPT = "Sisesta siia ";
    public static final String PLAYER_NAME_SUFFIX = ". mängija nimi";
    public static final String DEFAULT_PLAYER_PREFIX = "Mängija ";
    
    // ========== SPECIAL CHARACTERS ==========
    public static final String SCORE_PLACEHOLDER = "-";
    public static final String SCORE_INVALID = "/";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private GameConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Creates a standard title font.
     * 
     * @return Font object with title size
     */
    public static Font getTitleFont() {
        return new Font(FONT_FAMILY, FONT_SIZE_TITLE);
    }
    
    /**
     * Creates a standard large font.
     * 
     * @return Font object with large size
     */
    public static Font getLargeFont() {
        return new Font(FONT_SIZE_LARGE);
    }
    
    /**
     * Creates a standard medium font.
     * 
     * @return Font object with medium size
     */
    public static Font getMediumFont() {
        return new Font(FONT_SIZE_MEDIUM);
    }
    
    /**
     * Creates a standard normal font.
     * 
     * @return Font object with normal size
     */
    public static Font getNormalFont() {
        return new Font(FONT_FAMILY, FONT_SIZE_NORMAL);
    }
    
    /**
     * Creates a bold cell font.
     * 
     * @return Font object with cell size and bold weight
     */
    public static Font getCellFontBold() {
        return Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_CELL);
    }
    
    /**
     * Creates a standard cell font.
     * 
     * @return Font object with cell size
     */
    public static Font getCellFont() {
        return new Font(FONT_FAMILY, FONT_SIZE_CELL);
    }
    
    /**
     * Creates a checkbox label font.
     * 
     * @return Font object with checkbox label size
     */
    public static Font getCheckboxLabelFont() {
        return new Font(FONT_FAMILY, FONT_SIZE_CHECKBOX_LABEL);
    }
    
    /**
     * Gets the full path to a dice image.
     * 
     * @param diceValue the value of the die (1-6)
     * @return the full resource path to the dice image
     */
    public static String getDiceImagePath(int diceValue) {
        return DICE_IMAGE_PATH + diceValue + DICE_IMAGE_EXTENSION;
    }
    
    /**
     * Gets the player name prompt text.
     * 
     * @param playerNumber the player number (1-based)
     * @return the prompt text for player name input
     */
    public static String getPlayerNamePrompt(int playerNumber) {
        return PLAYER_NAME_PROMPT + playerNumber + PLAYER_NAME_SUFFIX;
    }
    
    /**
     * Gets the default player name.
     * 
     * @param playerNumber the player number (1-based)
     * @return the default player name
     */
    public static String getDefaultPlayerName(int playerNumber) {
        return DEFAULT_PLAYER_PREFIX + playerNumber;
    }
    
    /**
     * Gets the player turn message.
     * 
     * @param playerNumber the player number (1-based)
     * @return the player turn message
     */
    public static String getPlayerTurnMessage(int playerNumber) {
        return playerNumber + MSG_PLAYER_TURN;
    }
    
    /**
     * Gets the roll count message.
     * 
     * @param rollNumber the roll number (1-3)
     * @return the roll count message
     */
    public static String getRollMessage(int rollNumber) {
        return rollNumber + MSG_ROLL_NUMBER;
    }
}
