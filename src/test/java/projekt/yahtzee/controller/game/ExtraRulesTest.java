package projekt.yahtzee.controller.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projekt.yahtzee.model.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Yahtzee bonus and Joker rule in {@link GameController}.
 * Box indices: 0-5 Ones..Sixes, 6 Three of a Kind, 7 Four of a Kind, 8 Full House,
 * 9 Small Straight, 10 Large Straight, 11 Yahtzee, 12 Chance.
 */
class ExtraRulesTest {

    private static final List<Integer> FIVES = List.of(5, 5, 5, 5, 5);
    private static final List<Integer> TWOS = List.of(2, 2, 2, 2, 2);

    private GameController controller;
    private Player player;

    @BeforeEach
    void setUp() {
        controller = new GameController();
        controller.addPlayer("Alice");
        controller.setExtraRules(true);
        player = controller.getCurrentPlayer();
    }

    @Test
    void firstYahtzeeIsNotAJoker() {
        assertFalse(controller.isJoker(FIVES));
        assertEquals(50, controller.saveScore(11, FIVES));
        assertEquals(50, player.getComboScore(11));
        assertEquals(0, player.getYahtzeeBonusCount());
    }

    @Test
    void secondYahtzeeEarnsBonusAndMustUseMatchingUpperBox() {
        controller.saveScore(11, FIVES);

        assertTrue(controller.isJoker(FIVES));
        assertTrue(controller.earnsYahtzeeBonus(FIVES));
        assertTrue(controller.isAllowed(4, FIVES), "Fives is the matching upper box");
        assertFalse(controller.isAllowed(12, FIVES), "Chance is not allowed while Fives is open");
        assertFalse(controller.isAllowed(0, FIVES));

        assertEquals(25, controller.saveScore(4, FIVES));
        assertEquals(1, player.getYahtzeeBonusCount());
        assertEquals(50 + 25 + 100, player.getTotalScore());
        assertEquals(50 + 100, player.getLowerSectionScore(), "bonus counts in the lower section");
    }

    @Test
    void jokerScoresFullPointsInLowerBoxesWhenUpperBoxIsFull() {
        controller.saveScore(11, FIVES);
        controller.saveScore(4, List.of(5, 5, 1, 2, 3));

        assertFalse(controller.isAllowed(4, FIVES), "Fives is used");
        assertTrue(controller.isAllowed(8, FIVES));
        assertFalse(controller.isAllowed(0, FIVES), "upper boxes only once the lower section is full");
        assertEquals(25, controller.pointsFor(8, FIVES));
        assertEquals(30, controller.pointsFor(9, FIVES));
        assertEquals(40, controller.pointsFor(10, FIVES));
        assertEquals(25, controller.pointsFor(6, FIVES));
        assertEquals(25, controller.pointsFor(12, FIVES));
        assertTrue(controller.jokerHint(FIVES).contains("alumisest"));
    }

    @Test
    void jokerScratchesAnUpperBoxWhenLowerSectionIsFull() {
        controller.saveScore(11, FIVES);
        controller.saveScore(4, List.of(5, 1, 2, 3, 4));
        for (int lower : new int[] {6, 7, 8, 9, 10, 12}) {
            controller.saveScore(lower, List.of(1, 2, 3, 4, 6));
        }
        assertTrue(controller.isAllowed(0, FIVES));
        assertFalse(controller.isAllowed(8, FIVES));
        assertEquals(0, controller.pointsFor(0, FIVES));
        assertTrue(controller.jokerHint(FIVES).contains("kriipsuta"));
    }

    @Test
    void scratchedYahtzeeGivesJokerButNoBonus() {
        controller.saveScore(11, List.of(1, 2, 3, 4, 6));
        assertEquals(0, player.getComboScore(11));

        assertTrue(controller.isJoker(TWOS));
        assertFalse(controller.earnsYahtzeeBonus(TWOS));
        controller.saveScore(1, TWOS);
        assertEquals(0, player.getYahtzeeBonusCount());
        assertEquals(10, player.getTotalScore());
    }

    @Test
    void withoutExtraRulesSecondYahtzeeIsOrdinary() {
        controller.setExtraRules(false);
        controller.saveScore(11, FIVES);

        assertFalse(controller.isJoker(FIVES));
        assertTrue(controller.isAllowed(12, FIVES));
        assertEquals(0, controller.pointsFor(9, FIVES), "no straight without the joker");
        controller.saveScore(12, FIVES);
        assertEquals(75, player.getTotalScore());
    }

    @Test
    void possibleScoresShowOnlyAllowedBoxesForAJoker() {
        controller.saveScore(11, FIVES);
        String[] scores = controller.calculatePossibleScores(FIVES, player.getUsedCombos());
        assertEquals("25", scores[4]);
        for (int i = 0; i < scores.length; i++) {
            if (i != 4) {
                assertNull(scores[i], "box " + i + " should not be offered");
            }
        }
    }

    @Test
    void upperSectionCompleteOnlyWhenAllSixAreFilled() {
        for (int i = 0; i < 5; i++) {
            controller.saveScore(i, List.of(1, 2, 3, 4, 5));
            assertFalse(controller.isUpperSectionComplete(player));
        }
        controller.saveScore(5, List.of(1, 2, 3, 4, 5));
        assertTrue(controller.isUpperSectionComplete(player));
        assertFalse(player.isUpperSectionBonusAwarded());
    }
}
