package projekt.yahtzee.controller.ui;

import projekt.yahtzee.util.GameConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Manages the application theme (light or dark) and persists the selection in settings.txt.
 */
public class ThemeController {
    private static final String SETTINGS_FILE = "settings.txt";
    
    public enum Theme {
        LIGHT, DARK
    }
    
    private Theme currentTheme;
    
    public ThemeController() {
        loadTheme();
    }
    
    /**
     * Loads the theme from the settings file.
     */
    private void loadTheme() {
        try {
            File file = new File(SETTINGS_FILE);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line = reader.readLine();
                    if (line != null && line.startsWith("THEME=")) {
                        String themeName = line.substring(6);
                        currentTheme = Theme.valueOf(themeName);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load theme: " + e.getMessage());
        }
        // Default to light theme when no persisted value exists.
        currentTheme = Theme.LIGHT;
    }
    
    /**
     * Persists the current theme to the settings file.
     */
    private void saveTheme() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
            writer.println("THEME=" + currentTheme.name());
        } catch (Exception e) {
            System.err.println("Failed to save theme: " + e.getMessage());
        }
    }
    
    /**
     * Toggles the theme between light and dark.
     */
    public void toggleTheme() {
        currentTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
        saveTheme();
    }
    
    /**
     * Sets a specific theme and persists it.
     */
    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        saveTheme();
    }
    
    /**
     * Returns the currently active theme.
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Returns whether the dark theme is active.
     */
    public boolean isDarkTheme() {
        return currentTheme == Theme.DARK;
    }
    
    // Theme-dependent colors and styles.
    
    public String getBackgroundStyle() {
        return isDarkTheme() 
            ? "-fx-background-color: linear-gradient(to bottom, #37474F, #263238);" 
            : "-fx-background-color: linear-gradient(to bottom, #E8F5E9, #A5D6A7);";
    }
    
    public String getTextColor() {
        return isDarkTheme() ? "#FFFFFF" : "#263238";
    }
    
    public String getPanelBackgroundColor() {
        return isDarkTheme() ? "#37474F" : "#90CAF9";
    }
    
    public String getScoreboardStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #37474F; -fx-border-color: #546E7A;"
            : "-fx-background-color: #90CAF9; -fx-border-color: #64B5F6;";
    }
    
    public String getActivePlayerStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #37474F; -fx-text-fill: #FFFFFF; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: #FFB74D;"
            : "-fx-background-color: #C5CAE9; -fx-text-fill: #263238; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: #FFA000;";
    }
    
    public String getPlayerHeaderStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #37474F; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #546E7A;"
            : "-fx-background-color: #C5CAE9; -fx-text-fill: #263238; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    public String getLabelTextFill() {
        return "-fx-text-fill: " + getTextColor() + ";";
    }
    
    /**
     * Returns the background color used for box containers.
     */
    public String getBoxBackgroundColor() {
        return isDarkTheme() ? "#3E5058" : "rgba(0, 0, 0, 0.1)";
    }
    
    /**
     * Returns the background color for title boxes.
     */
    public String getTitleBoxBackground() {
        return isDarkTheme() ? "#455A64" : "rgba(0, 0, 0, 0.2)";
    }
    
    public String getButtonStyle() {
        return isDarkTheme()
            ? GameConstants.BUTTON_WHEAT_STYLE
            : GameConstants.BUTTON_LIGHT_STYLE;
    }
    
    public String getScoreCellDefaultStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #455A64; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #546E7A;"
            : "-fx-background-color: #26C6DA; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    public String getScoreCellHoverStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #607D8B; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #78909C;"
            : "-fx-background-color: #00ACC1; -fx-text-fill: #E8EAF6; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    public String getScoreCellSelectedStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #A5D6A7; -fx-text-fill: #000000; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #66BB6A;"
            : "-fx-background-color: #80DEEA; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #00838F;";
    }
    
    public String getScoreCellSelectedHoverStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #607D8B; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #66BB6A;"
            : "-fx-background-color: #00ACC1; -fx-text-fill: #E8EAF6; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #00838F;";
    }
    
    public String getScoreCellUsedStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #546E7A; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #455A64;"
            : "-fx-background-color: #E0E0E0; -fx-border-style: solid; -fx-border-width: 1;";
    }

    public String getKeyboardFocusDecoration() {
        String color = isDarkTheme() ? "#FFD54F" : "#FFB300";
        return " -fx-border-color: " + color + ";" +
               " -fx-border-width: 3;" +
               " -fx-border-style: solid;" +
               " -fx-background-insets: 0;" +
               " -fx-border-radius: 6;";
    }
    
    public String getRollCounterColor() {
        return isDarkTheme() ? "#81C784" : "#2E7D32";
    }
    
    public String getBonusRowStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #3E4A50; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #546E7A;"
            : "-fx-background-color: #81D4FA; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    public String getTotalRowStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #FFB74D; -fx-text-fill: #000000; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #FFA726;"
            : "-fx-background-color: #FF7043; -fx-text-fill: #263238; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    public String getDicePanelStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #37474F; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #546E7A;"
            : "-fx-background-color: #90CAF9; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #64B5F6;";
    }
    
    /**
     * Returns the background color for the combo-name column.
     */
    public String getComboColumnBackground() {
        return isDarkTheme() ? "#37474F" : "#90CAF9";
    }
    
    /**
     * Returns the checkbox style string.
     */
    public String getCheckboxStyle() {
        return isDarkTheme()
            ? "-fx-scale-x: 1.8; -fx-scale-y: 1.8; " +
              "-fx-background-color: transparent; " +
              "-fx-border-color: transparent; " +
              "-fx-background-insets: 0; " +
              "-fx-padding: 0; " +
              "-fx-cursor: hand; " +
              "-fx-mark-color: #81C784;"
            : "-fx-scale-x: 1.8; -fx-scale-y: 1.8; " +
              "-fx-background-color: transparent; " +
              "-fx-border-color: transparent; " +
              "-fx-background-insets: 0; " +
              "-fx-padding: 0; " +
              "-fx-cursor: hand; " +
              "-fx-mark-color: #1976D2;";
    }
    
    /**
     * Returns the text-field style definition.
     *
     * @param focused whether the text field currently has focus
     * @return CSS style string
     */
    public String getTextFieldStyle(boolean focused) {
        if (isDarkTheme()) {
            if (focused) {
                return "-fx-background-color: #546E7A; " +
                       "-fx-text-fill: white; " +
                       "-fx-prompt-text-fill: rgba(255, 255, 255, 0.6); " +
                       "-fx-background-radius: 8; " +
                       "-fx-border-radius: 8; " +
                       "-fx-border-color: #42A5F5; " +
                       "-fx-border-width: 2; " +
                       "-fx-padding: 8; " +
                       "-fx-font-size: 16px;";
            } else {
                return "-fx-background-color: #455A64; " +
                       "-fx-text-fill: white; " +
                       "-fx-prompt-text-fill: rgba(255, 255, 255, 0.6); " +
                       "-fx-background-radius: 8; " +
                       "-fx-border-radius: 8; " +
                       "-fx-border-color: #546E7A; " +
                       "-fx-border-width: 2; " +
                       "-fx-padding: 8; " +
                       "-fx-font-size: 16px;";
            }
        } else {
            if (focused) {
                return "-fx-background-color: #EEEEEE; " +
                       "-fx-text-fill: black; " +
                       "-fx-prompt-text-fill: rgba(0, 0, 0, 0.5); " +
                       "-fx-background-radius: 8; " +
                       "-fx-border-radius: 8; " +
                       "-fx-border-color: #42A5F5; " +
                       "-fx-border-width: 2; " +
                       "-fx-padding: 8; " +
                       "-fx-font-size: 16px;";
            } else {
                return "-fx-background-color: #E0E0E0; " +
                       "-fx-text-fill: black; " +
                       "-fx-prompt-text-fill: rgba(0, 0, 0, 0.5); " +
                       "-fx-background-radius: 8; " +
                       "-fx-border-radius: 8; " +
                       "-fx-border-color: #BDBDBD; " +
                       "-fx-border-width: 2; " +
                       "-fx-padding: 8; " +
                       "-fx-font-size: 16px;";
            }
        }
    }
}
