package projekt.yahtzee.controller.ui;

import projekt.yahtzee.util.GameConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages the application theme (light or dark) and persists the selection in settings.txt.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public class ThemeController {
    /**
     * Enumeration of available application themes.
     */
    public enum Theme {
        /** Light colour scheme. */
        LIGHT,
        /** Dark colour scheme. */
        DARK
    }
    
    private Theme currentTheme;
    
    /**
     * Constructs a new theme controller and loads the persisted theme preference.
     */
    public ThemeController() {
        loadTheme();
    }
    
    /**
     * Resolves the path to the settings file.
     *
     * @return path to the settings file
     */
    private Path resolveSettingsPath() {
        return Paths.get(GameConstants.SETTINGS_FILE);
    }
    
    /**
     * Loads the theme from the settings file.
     */
    private void loadTheme() {
        Path settingsPath = resolveSettingsPath();
        if (!Files.exists(settingsPath)) {
            currentTheme = Theme.LIGHT;
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(settingsPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            Theme persistedTheme = parseTheme(line);
            currentTheme = persistedTheme != null ? persistedTheme : Theme.LIGHT;
        } catch (IOException e) {
            System.err.println("Failed to load theme from " + settingsPath + ": " + e.getMessage());
            currentTheme = Theme.LIGHT;
        }
    }
    
    /**
     * Persists the current theme to the settings file.
     */
    private void saveTheme() {
        Path settingsPath = resolveSettingsPath();
        Path parent = settingsPath.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(settingsPath, "THEME=" + currentTheme.name(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to save theme to " + settingsPath + ": " + e.getMessage());
        }
    }

    /**
     * Parses a theme value from a settings file line.
     *
     * @param line the raw line from the settings file
     * @return the parsed theme, or {@code null} if the line is invalid
     */
    private Theme parseTheme(String line) {
        if (line == null || !line.startsWith("THEME=")) {
            return null;
        }

        String themeName = line.substring(6);
        try {
            return Theme.valueOf(themeName);
        } catch (IllegalArgumentException e) {
            return null;
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
     * Returns whether the dark theme is active.
     *
     * @return {@code true} if dark theme is active, {@code false} otherwise
     */
    public boolean isDarkTheme() {
        return currentTheme == Theme.DARK;
    }
    
    // Theme-dependent colors and styles.
    
    /**
     * Returns the CSS background style for the main scene.
     *
     * @return CSS background gradient style
     */
    public String getBackgroundStyle() {
        return isDarkTheme() 
            ? "-fx-background-color: linear-gradient(to bottom, #37474F, #263238);" 
            : "-fx-background-color: linear-gradient(to bottom, #E8F5E9, #A5D6A7);";
    }
    
    /**
     * Returns the hex colour used for general text.
     *
     * @return CSS hex colour string
     */
    public String getTextColor() {
        return isDarkTheme() ? "#FFFFFF" : "#263238";
    }
    
    /**
     * Returns the background colour used for side panels.
     *
     * @return CSS hex colour string
     */
    public String getPanelBackgroundColor() {
        return isDarkTheme() ? "#37474F" : "#90CAF9";
    }
    
    /**
     * Returns the CSS style for the scoreboard container.
     *
     * @return CSS style string
     */
    public String getScoreboardStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #37474F; -fx-border-color: #546E7A;"
            : "-fx-background-color: #90CAF9; -fx-border-color: #64B5F6;";
    }
    
    /**
     * Returns the CSS style for the active (highlighted) player column header.
     *
     * @return CSS style string
     */
    public String getActivePlayerStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #37474F; -fx-text-fill: #FFFFFF; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: #FFB74D;"
            : "-fx-background-color: #C5CAE9; -fx-text-fill: #263238; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: #FFA000;";
    }
    
    /**
     * Returns the CSS style for a non-active player column header.
     *
     * @return CSS style string
     */
    public String getPlayerHeaderStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #37474F; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #546E7A;"
            : "-fx-background-color: #C5CAE9; -fx-text-fill: #263238; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    /**
     * Returns the CSS text-fill property matching the current theme.
     *
     * @return CSS text-fill style string
     */
    public String getLabelTextFill() {
        return "-fx-text-fill: " + getTextColor() + ";";
    }
    
    /**
     * Returns the background color used for box containers.
     *
     * @return CSS colour string
     */
    public String getBoxBackgroundColor() {
        return isDarkTheme() ? "#3e5058b6" : "rgba(0, 0, 0, 0.1)";
    }

    /**
     * Returns the background color for title boxes.
     *
     * @return CSS colour string
     */
    public String getTitleBoxBackground() {
        return isDarkTheme() ? "rgba(110, 130, 142, 0.44)" : "rgba(255, 255, 255, 0.5)";
    }

    /**
     * Returns the CSS style for standard buttons.
     *
     * @return CSS style string
     */
    public String getButtonStyle() {
        return isDarkTheme()
            ? GameConstants.BUTTON_WHEAT_STYLE
            : GameConstants.BUTTON_LIGHT_STYLE;
    }
    
    /**
     * Returns the default CSS style for an interactive score cell.
     *
     * @return CSS style string
     */
    public String getScoreCellDefaultStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #455A64; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #546E7A;"
            : "-fx-background-color: #26C6DA; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    /**
     * Returns the hover CSS style for an interactive score cell.
     *
     * @return CSS style string
     */
    public String getScoreCellHoverStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #607D8B; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #78909C;"
            : "-fx-background-color: #00ACC1; -fx-text-fill: #E8EAF6; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    /**
     * Returns the CSS style for a selected score cell.
     *
     * @return CSS style string
     */
    public String getScoreCellSelectedStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #A5D6A7; -fx-text-fill: #000000; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #66BB6A;"
            : "-fx-background-color: #80DEEA; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #00838F;";
    }
    
    /**
     * Returns the hover CSS style for a selected score cell.
     *
     * @return CSS style string
     */
    public String getScoreCellSelectedHoverStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #607D8B; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #66BB6A;"
            : "-fx-background-color: #00ACC1; -fx-text-fill: #E8EAF6; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #00838F;";
    }
    
    /**
     * Returns the CSS style for a used (locked) score cell.
     *
     * @return CSS style string
     */
    public String getScoreCellUsedStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #546E7A; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #455A64;"
            : "-fx-background-color: #E0E0E0; -fx-border-style: solid; -fx-border-width: 1;";
    }

    /**
     * Returns the CSS decoration applied to a keyboard-focused cell.
     *
     * @return CSS style string with border and radius
     */
    public String getKeyboardFocusDecoration() {
        String color = isDarkTheme() ? "#FFD54F" : "#FFB300";
        return " -fx-border-color: " + color + ";" +
               " -fx-border-width: 3;" +
               " -fx-border-style: solid;" +
               " -fx-background-insets: 0;" +
               " -fx-border-radius: 6;";
    }
    
    /**
     * Returns the hex colour used for the roll counter label.
     *
     * @return CSS hex colour string
     */
    public String getRollCounterColor() {
        return isDarkTheme() ? "#81C784" : "#2E7D32";
    }
    
    /**
     * Returns the CSS style for the bonus summary row.
     *
     * @return CSS style string
     */
    public String getBonusRowStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #3E4A50; -fx-text-fill: #FFFFFF; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #546E7A;"
            : "-fx-background-color: #81D4FA; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    /**
     * Returns the CSS style for the total summary row.
     *
     * @return CSS style string
     */
    public String getTotalRowStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #FFB74D; -fx-text-fill: #000000; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #FFA726;"
            : "-fx-background-color: #FF7043; -fx-text-fill: #263238; -fx-border-style: solid; -fx-border-width: 1;";
    }
    
    /**
     * Returns the CSS style for the dice panel border and background.
     *
     * @return CSS style string
     */
    public String getDicePanelStyle() {
        return isDarkTheme()
            ? "-fx-background-color: #32444C; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #4F616A;"
            : "-fx-background-color: #90CAF9; -fx-border-style: solid; -fx-border-width: 2; -fx-border-color: #64B5F6;";
    }
    
    /**
     * Returns the background color for the combo-name column.
     *
     * @return CSS hex colour string
     */
    public String getComboColumnBackground() {
        return isDarkTheme() ? "#37474F" : "#90CAF9";
    }
    
    /**
     * Returns the checkbox style string.
     *
     * @return CSS style string for checkboxes
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
