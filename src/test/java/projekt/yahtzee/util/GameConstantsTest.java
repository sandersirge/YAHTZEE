package projekt.yahtzee.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameConstants — verifies constants are accessible without JavaFX toolkit.
 *
 * @author sandersirge
 * @version 1.1.0
 */
@SuppressWarnings("ConstantValue")
class GameConstantsTest {

    @Test
    void windowSizesAreDefined() {
        assertTrue(GameConstants.MAIN_WINDOW_WIDTH > 0);
        assertTrue(GameConstants.MAIN_WINDOW_HEIGHT > 0);
        assertTrue(GameConstants.MAIN_MENU_WIDTH > 0);
    }

    @Test
    void diceSettingsAreDefined() {
        assertEquals(5, GameConstants.DICE_COUNT);
        assertTrue(GameConstants.DICE_IMAGE_SIZE > 0);
    }

    @Test
    void gameRulesAreCorrect() {
        assertEquals(3, GameConstants.MAX_PLAYERS);
        assertEquals(1, GameConstants.MIN_PLAYERS);
        assertEquals(3, GameConstants.MAX_ROLLS_PER_TURN);
        assertEquals(13, GameConstants.TOTAL_COMBINATIONS);
        assertEquals(63, GameConstants.UPPER_SECTION_BONUS_THRESHOLD);
        assertEquals(35, GameConstants.UPPER_SECTION_BONUS_POINTS);
    }

    @Test
    void comboNamesNotEmpty() {
        assertFalse(GameConstants.COMBO_ONES.isEmpty());
        assertFalse(GameConstants.COMBO_YAHTZEE.isEmpty());
        assertFalse(GameConstants.COMBO_CHANCE.isEmpty());
    }

    @Test
    void diceImagePathHelper() {
        String path = GameConstants.getDiceImagePath(3);
        assertTrue(path.contains("3"));
        assertTrue(path.endsWith(".png"));
    }

    @Test
    void playerNamePromptHelper() {
        String prompt = GameConstants.getPlayerNamePrompt(1);
        assertTrue(prompt.contains("1"));
    }

    @Test
    void defaultPlayerNameHelper() {
        String name = GameConstants.getDefaultPlayerName(2);
        assertTrue(name.contains("2"));
    }

    @Test
    void playerTurnMessageHelper() {
        String msg = GameConstants.getPlayerTurnMessage(1);
        assertTrue(msg.contains("1"));
    }

    @Test
    void rollMessageHelper() {
        String msg = GameConstants.getRollMessage(2);
        assertTrue(msg.contains("2"));
    }

    @Test
    void fontFamilyIsDefined() {
        assertNotNull(GameConstants.FONT_FAMILY);
        assertFalse(GameConstants.FONT_FAMILY.isEmpty());
    }

    @Test
    void buttonStylesAreDefined() {
        assertNotNull(GameConstants.BUTTON_SUCCESS_STYLE);
        assertNotNull(GameConstants.BUTTON_ERROR_STYLE);
        assertNotNull(GameConstants.BUTTON_INFO_STYLE);
    }
}

