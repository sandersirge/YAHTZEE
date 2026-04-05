package projekt.yahtzee.model.combos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CombinationRegistry.
c *
 * @author sandersirge
 * @version 1.1.0
 */
class CombinationRegistryTest {

    private CombinationRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CombinationRegistry();
    }

    @Test
    void registryContains13Combinations() {
        assertEquals(13, registry.getAllCombos().size());
    }

    @Test
    void getComboByIndexReturnsCorrectCombo() {
        assertEquals("Ones", registry.getComboByIndex(0).getComboName());
        assertEquals("Twos", registry.getComboByIndex(1).getComboName());
        assertEquals("Threes", registry.getComboByIndex(2).getComboName());
        assertEquals("Fours", registry.getComboByIndex(3).getComboName());
        assertEquals("Fives", registry.getComboByIndex(4).getComboName());
        assertEquals("Sixes", registry.getComboByIndex(5).getComboName());
    }

    @Test
    void lowerSectionCombosPresent() {
        assertEquals("Three of a Kind", registry.getComboByIndex(6).getComboName());
        assertEquals("Four of a Kind", registry.getComboByIndex(7).getComboName());
        assertEquals("Full House", registry.getComboByIndex(8).getComboName());
        assertEquals("Small Straight", registry.getComboByIndex(9).getComboName());
        assertEquals("Large Straight", registry.getComboByIndex(10).getComboName());
        assertEquals("Yahtzee", registry.getComboByIndex(11).getComboName());
        assertEquals("Chance", registry.getComboByIndex(12).getComboName());
    }

    @Test
    void isUpperSectionTrueForIndices0To5() {
        for (int i = 0; i <= 5; i++) {
            assertTrue(registry.isUpperSection(i), "Index " + i + " should be upper section");
        }
    }

    @Test
    void isUpperSectionFalseForIndices6To12() {
        for (int i = 6; i <= 12; i++) {
            assertFalse(registry.isUpperSection(i), "Index " + i + " should NOT be upper section");
        }
    }

    @Test
    void isUpperSectionFalseForNegativeIndex() {
        assertFalse(registry.isUpperSection(-1));
    }

}

