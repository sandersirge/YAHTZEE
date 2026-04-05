package projekt.yahtzee.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Player class.
 *
 * @author sandersirge
 * @version 1.1.0
 */
class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer");
    }

    @Test
    void constructorSetsNameAndZeroScore() {
        assertEquals("TestPlayer", player.getPlayerName());
        assertEquals(0, player.getTotalScore());
    }


    @Test
    void setTotalScoreWorks() {
        player.setTotalScore(150);
        assertEquals(150, player.getTotalScore());
    }

    @Test
    void addRolledComboIncrementsCount() {
        assertEquals(0, player.getRolledComboCount());
        player.addRolledCombo("Ones", 3);
        assertEquals(1, player.getRolledComboCount());
        player.addRolledCombo("Twos", 6);
        assertEquals(2, player.getRolledComboCount());
    }

    @Test
    void usedCombosInitiallyAllZero() {
        int[] used = player.getUsedCombos();
        assertEquals(13, used.length);
        for (int v : used) {
            assertEquals(0, v);
        }
    }

    @Test
    void setUsedCombosWorks() {
        int[] newUsed = new int[13];
        newUsed[0] = 1;
        newUsed[5] = 1;
        player.setUsedCombos(newUsed);
        assertEquals(1, player.getUsedCombos()[0]);
        assertEquals(1, player.getUsedCombos()[5]);
        assertEquals(0, player.getUsedCombos()[1]);
    }

    @Test
    void upperSectionScoreInitiallyZero() {
        assertEquals(0, player.getUpperSectionScore());
    }

    @Test
    void setUpperSectionScore() {
        player.setUpperSectionScore(42);
        assertEquals(42, player.getUpperSectionScore());
    }

    @Test
    void lowerSectionScoreInitiallyZero() {
        assertEquals(0, player.getLowerSectionScore());
    }

    @Test
    void setLowerSectionScore() {
        player.setLowerSectionScore(100);
        assertEquals(100, player.getLowerSectionScore());
    }

    @Test
    void upperSectionBonusNotAwardedByDefault() {
        assertFalse(player.isUpperSectionBonusAwarded());
    }

    @Test
    void setUpperSectionBonusAwarded() {
        player.setUpperSectionBonusAwarded(true);
        assertTrue(player.isUpperSectionBonusAwarded());
    }

    @Test
    void rollDiceRollsUnkeptDice() {
        List<Die> dice = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Die d = new Die();
            d.setCurrentValue(1);
            dice.add(d);
        }
        // Keep dice at indices 0 and 2
        int[] kept = {1, 0, 1, 0, 0};
        player.rollDice(dice, kept);

        // Kept dice stay at 1
        assertEquals(1, dice.get(0).getCurrentValue());
        assertEquals(1, dice.get(2).getCurrentValue());

        // Rolled dice should have values 1-6 (they were rolled)
        for (int i : new int[]{1, 3, 4}) {
            int val = dice.get(i).getCurrentValue();
            assertTrue(val >= 1 && val <= 6, "Rolled die should be 1-6 but was " + val);
        }
    }

    @Test
    void rollDiceKeepsAllDice() {
        List<Die> dice = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Die d = new Die();
            d.setCurrentValue(i + 1);
            dice.add(d);
        }
        int[] kept = {1, 1, 1, 1, 1};
        player.rollDice(dice, kept);

        // All dice keep their original values
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, dice.get(i).getCurrentValue());
        }
    }

    @Test
    void compareToLowerScore() {
        Player other = new Player("Other");
        player.setTotalScore(50);
        other.setTotalScore(100);
        assertTrue(player.compareTo(other) < 0);
    }

    @Test
    void compareToHigherScore() {
        Player other = new Player("Other");
        player.setTotalScore(200);
        other.setTotalScore(100);
        assertTrue(player.compareTo(other) > 0);
    }

    @Test
    void compareToEqualScore() {
        Player other = new Player("Other");
        player.setTotalScore(100);
        other.setTotalScore(100);
        assertEquals(0, player.compareTo(other));
    }

    @Test
    void toStringContainsCombos() {
        player.addRolledCombo("Ones", 3);
        player.addRolledCombo("Yahtzee", 50);
        String display = player.toString();
        assertTrue(display.contains("Ones"));
        assertTrue(display.contains("3"));
        assertTrue(display.contains("Yahtzee"));
        assertTrue(display.contains("50"));
    }

    @Test
    void toStringContainsNameAndScore() {
        player.setTotalScore(42);
        String result = player.toString();
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("42"));
    }
}

