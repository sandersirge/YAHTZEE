package projekt.yahtzee.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Die class.
 *
 * @author sandersirge
 * @version 1.1.0
 */
class DieTest {

    private Die die;

    @BeforeEach
    void setUp() {
        die = new Die();
    }

    @Test
    void initialValueIsZero() {
        assertEquals(0, die.getCurrentValue());
    }

    @RepeatedTest(50)
    void rollProducesValueBetween1And6() {
        die.roll();
        int value = die.getCurrentValue();
        assertTrue(value >= 1 && value <= 6,
                "Die value should be between 1 and 6, but was " + value);
    }

    @Test
    void setCurrentValueWorks() {
        die.setCurrentValue(4);
        assertEquals(4, die.getCurrentValue());
    }

    @Test
    void toStringReturnsValueString() {
        die.setCurrentValue(5);
        assertEquals("5", die.toString());
    }

    @Test
    void compareToLowerValue() {
        Die other = new Die();
        die.setCurrentValue(2);
        other.setCurrentValue(5);
        assertTrue(die.compareTo(other) < 0);
    }

    @Test
    void compareToHigherValue() {
        Die other = new Die();
        die.setCurrentValue(6);
        other.setCurrentValue(1);
        assertTrue(die.compareTo(other) > 0);
    }

    @Test
    void compareToEqualValue() {
        Die other = new Die();
        die.setCurrentValue(3);
        other.setCurrentValue(3);
        assertEquals(0, die.compareTo(other));
    }

    @Test
    void dieImplementsRollable() {
        assertInstanceOf(Rollable.class, die);
    }
}

