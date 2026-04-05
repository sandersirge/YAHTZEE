package projekt.yahtzee.model.combos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parameterised tests for all Yahtzee combination implementations.
 *
 * @author sandersirge
 * @version 1.1.0
 */
class CombinationTest {

    // ====== NUMBERS (Ones through Sixes) ======

    @ParameterizedTest(name = "Ones: {0} → possible={1}, points={2}")
    @MethodSource("onesProvider")
    void testOnes(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        Numbers ones = new Numbers("Ones", 1);
        assertEquals(expectedPossible, ones.isPossible(values));
        assertEquals(expectedPoints, ones.calculatePoints(values));
    }

    static Stream<Arguments> onesProvider() {
        return Stream.of(
            Arguments.of(List.of(1, 1, 1, 2, 3), true, 3),
            Arguments.of(List.of(1, 2, 3, 4, 5), true, 1),
            Arguments.of(List.of(2, 3, 4, 5, 6), false, 0),
            Arguments.of(List.of(1, 1, 1, 1, 1), true, 5)
        );
    }

    @ParameterizedTest(name = "Sixes: {0} → possible={1}, points={2}")
    @MethodSource("sixesProvider")
    void testSixes(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        Numbers sixes = new Numbers("Sixes", 6);
        assertEquals(expectedPossible, sixes.isPossible(values));
        assertEquals(expectedPoints, sixes.calculatePoints(values));
    }

    static Stream<Arguments> sixesProvider() {
        return Stream.of(
            Arguments.of(List.of(1, 2, 3, 4, 6), true, 6),
            Arguments.of(List.of(6, 6, 6, 6, 6), true, 30),
            Arguments.of(List.of(1, 2, 3, 4, 5), false, 0),
            Arguments.of(List.of(1, 6, 6, 2, 3), true, 12)
        );
    }

    @Test
    void numbersMiddleValues() {
        Numbers threes = new Numbers("Threes", 3);
        assertEquals(9, threes.calculatePoints(List.of(1, 3, 3, 3, 5)));
        assertTrue(threes.isPossible(List.of(3, 3, 3, 3, 3)));
        assertFalse(threes.isPossible(List.of(1, 2, 4, 5, 6)));
    }

    // ====== THREE OF A KIND ======

    @ParameterizedTest(name = "ThreeOfKind: {0} → possible={1}, points={2}")
    @MethodSource("threeOfKindProvider")
    void testThreeOfKind(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        ThreeOfKind tok = new ThreeOfKind();
        assertEquals(expectedPossible, tok.isPossible(values));
        assertEquals(expectedPoints, tok.calculatePoints(values));
    }

    static Stream<Arguments> threeOfKindProvider() {
        return Stream.of(
            Arguments.of(List.of(3, 3, 3, 4, 5), true, 18),
            Arguments.of(List.of(1, 1, 1, 1, 1), true, 5),
            Arguments.of(List.of(1, 2, 3, 4, 5), false, 0),
            Arguments.of(List.of(6, 6, 6, 1, 2), true, 21),
            Arguments.of(List.of(2, 2, 3, 3, 4), false, 0)
        );
    }

    // ====== FOUR OF A KIND ======

    @ParameterizedTest(name = "FourOfKind: {0} → possible={1}, points={2}")
    @MethodSource("fourOfKindProvider")
    void testFourOfKind(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        FourOfKind fok = new FourOfKind();
        assertEquals(expectedPossible, fok.isPossible(values));
        assertEquals(expectedPoints, fok.calculatePoints(values));
    }

    static Stream<Arguments> fourOfKindProvider() {
        return Stream.of(
            Arguments.of(List.of(4, 4, 4, 4, 5), true, 21),
            Arguments.of(List.of(2, 2, 2, 2, 2), true, 10),
            Arguments.of(List.of(3, 3, 3, 4, 5), false, 0),
            Arguments.of(List.of(1, 2, 3, 4, 5), false, 0)
        );
    }

    // ====== FULL HOUSE ======

    @ParameterizedTest(name = "FullHouse: {0} → possible={1}, points={2}")
    @MethodSource("fullHouseProvider")
    void testFullHouse(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        FullHouse fh = new FullHouse();
        assertEquals(expectedPossible, fh.isPossible(values));
        assertEquals(expectedPoints, fh.calculatePoints(values));
    }

    static Stream<Arguments> fullHouseProvider() {
        return Stream.of(
            Arguments.of(List.of(2, 2, 3, 3, 3), true, 25),
            Arguments.of(List.of(1, 1, 6, 6, 6), true, 25),
            Arguments.of(List.of(1, 2, 3, 4, 5), false, 0),
            Arguments.of(List.of(3, 3, 3, 3, 3), false, 0),   // 5 of a kind is NOT full house
            Arguments.of(List.of(1, 1, 2, 2, 3), false, 0)    // two pairs is not full house
        );
    }

    // ====== SMALL STRAIGHT ======

    @ParameterizedTest(name = "SmallStraight: {0} → possible={1}, points={2}")
    @MethodSource("smallStraightProvider")
    void testSmallStraight(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        SmallStraight ss = new SmallStraight();
        assertEquals(expectedPossible, ss.isPossible(values));
        assertEquals(expectedPoints, ss.calculatePoints(values));
    }

    static Stream<Arguments> smallStraightProvider() {
        return Stream.of(
            Arguments.of(List.of(1, 2, 3, 4, 6), true, 30),
            Arguments.of(List.of(2, 3, 4, 5, 5), true, 30),
            Arguments.of(List.of(3, 4, 5, 6, 6), true, 30),
            Arguments.of(List.of(1, 2, 3, 4, 5), true, 30),
            Arguments.of(List.of(2, 3, 4, 5, 6), true, 30),
            Arguments.of(List.of(1, 2, 4, 5, 6), false, 0),
            Arguments.of(List.of(1, 1, 1, 1, 1), false, 0)
        );
    }

    // ====== LARGE STRAIGHT ======

    @ParameterizedTest(name = "LargeStraight: {0} → possible={1}, points={2}")
    @MethodSource("largeStraightProvider")
    void testLargeStraight(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        LargeStraight ls = new LargeStraight();
        assertEquals(expectedPossible, ls.isPossible(values));
        assertEquals(expectedPoints, ls.calculatePoints(values));
    }

    static Stream<Arguments> largeStraightProvider() {
        return Stream.of(
            Arguments.of(List.of(1, 2, 3, 4, 5), true, 40),
            Arguments.of(List.of(2, 3, 4, 5, 6), true, 40),
            Arguments.of(List.of(1, 2, 3, 4, 6), false, 0),
            Arguments.of(List.of(1, 1, 3, 4, 5), false, 0),
            Arguments.of(List.of(3, 3, 3, 3, 3), false, 0)
        );
    }

    // ====== YAHTZEE ======

    @ParameterizedTest(name = "Yahtzee: {0} → possible={1}, points={2}")
    @MethodSource("yahtzeeProvider")
    void testYahtzee(List<Integer> values, boolean expectedPossible, int expectedPoints) {
        YahtzeeCombination yc = new YahtzeeCombination();
        assertEquals(expectedPossible, yc.isPossible(values));
        assertEquals(expectedPoints, yc.calculatePoints(values));
    }

    static Stream<Arguments> yahtzeeProvider() {
        return Stream.of(
            Arguments.of(List.of(1, 1, 1, 1, 1), true, 50),
            Arguments.of(List.of(6, 6, 6, 6, 6), true, 50),
            Arguments.of(List.of(3, 3, 3, 3, 3), true, 50),
            Arguments.of(List.of(1, 1, 1, 1, 2), false, 0),
            Arguments.of(List.of(1, 2, 3, 4, 5), false, 0)
        );
    }

    // ====== CHANCE ======

    @ParameterizedTest(name = "Chance: {0} → points={1}")
    @MethodSource("chanceProvider")
    void testChance(List<Integer> values, int expectedPoints) {
        Chance chance = new Chance();
        assertTrue(chance.isPossible(values), "Chance is always possible");
        assertEquals(expectedPoints, chance.calculatePoints(values));
    }

    static Stream<Arguments> chanceProvider() {
        return Stream.of(
            Arguments.of(List.of(1, 2, 3, 4, 5), 15),
            Arguments.of(List.of(6, 6, 6, 6, 6), 30),
            Arguments.of(List.of(1, 1, 1, 1, 1), 5),
            Arguments.of(List.of(2, 3, 5, 5, 6), 21)
        );
    }

    // ====== Combination base class properties ======

    @Test
    void combinationGetters() {
        Chance c = new Chance();
        assertEquals("Chance", c.getComboName());
        assertEquals("Chance", c.toString());
    }
}
