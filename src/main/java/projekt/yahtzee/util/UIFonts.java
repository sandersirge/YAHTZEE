package projekt.yahtzee.util;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Contains all JavaFX-dependent font and color utility methods.
 * Separated from {@link GameConstants} so that constants remain testable
 * without requiring a live JavaFX toolkit.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public final class UIFonts {

    private UIFonts() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates a standard title font.
     *
     * @return Font object with title size
     */
    public static Font getTitleFont() {
        return new Font(GameConstants.FONT_FAMILY, GameConstants.FONT_SIZE_TITLE);
    }

    /**
     * Creates a standard large font.
     *
     * @return Font object with large size
     */
    public static Font getLargeFont() {
        return new Font(GameConstants.FONT_SIZE_LARGE);
    }

    /**
     * Creates a standard medium font.
     *
     * @return Font object with medium size
     */
    public static Font getMediumFont() {
        return new Font(GameConstants.FONT_SIZE_MEDIUM);
    }


    /**
     * Creates a bold cell font.
     *
     * @return Font object with cell size and bold weight
     */
    public static Font getCellFontBold() {
        return Font.font(GameConstants.FONT_FAMILY, FontWeight.BOLD, GameConstants.FONT_SIZE_CELL);
    }

    /**
     * Creates a standard cell font.
     *
     * @return Font object with cell size
     */
    public static Font getCellFont() {
        return new Font(GameConstants.FONT_FAMILY, GameConstants.FONT_SIZE_CELL);
    }

    /**
     * Creates a checkbox label font.
     *
     * @return Font object with checkbox label size
     */
    public static Font getCheckboxLabelFont() {
        return new Font(GameConstants.FONT_FAMILY, GameConstants.FONT_SIZE_CHECKBOX_LABEL);
    }
}

