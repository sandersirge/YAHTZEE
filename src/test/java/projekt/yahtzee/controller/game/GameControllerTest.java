package projekt.yahtzee.controller.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projekt.yahtzee.model.Die;
import projekt.yahtzee.model.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GameController class.
 *
 * @author sandersirge
 * @version 1.1.0
 */
class GameControllerTest {

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = new GameController();
    }

    @Test
    void initiallyNoPlayers() {
        assertTrue(controller.getPlayers().isEmpty());
    }

    @Test
    void addPlayerIncreasesCount() {
        controller.addPlayer("Alice");
        assertEquals(1, controller.getPlayers().size());
        assertEquals("Alice", controller.getPlayers().getFirst().getPlayerName());
    }

    @Test
    void addMultiplePlayers() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.addPlayer("Charlie");
        assertEquals(3, controller.getPlayers().size());
    }

    @Test
    void initiallyFiveDice() {
        assertEquals(5, controller.getDice().size());
    }

    @Test
    void initialRollCountIsZero() {
        assertEquals(0, controller.getCurrentRollCount());
    }

    @Test
    void currentPlayerIndexStartsAtZero() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        assertEquals(0, controller.getCurrentPlayerIndex());
    }

    @Test
    void getCurrentPlayerReturnsFirstPlayer() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        assertEquals("Alice", controller.getCurrentPlayer().getPlayerName());
    }

    @Test
    void rollDiceIncrementsRollCount() {
        controller.addPlayer("Alice");
        int[] kept = {0, 0, 0, 0, 0};
        controller.rollDice(kept);
        assertEquals(1, controller.getCurrentRollCount());
    }

    @Test
    void rollDiceProducesValidValues() {
        controller.addPlayer("Alice");
        int[] kept = {0, 0, 0, 0, 0};
        controller.rollDice(kept);
        for (int value : controller.getDiceValues()) {
            assertTrue(value >= 1 && value <= 6, "Dice value should be 1-6 but was " + value);
        }
    }

    @Test
    void rollDiceReturnsSortedValues() {
        controller.addPlayer("Alice");
        int[] kept = {0, 0, 0, 0, 0};
        controller.rollDice(kept);
        List<Integer> values = controller.getDiceValues();
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) <= values.get(i + 1),
                "Dice values should be sorted but " + values.get(i) + " > " + values.get(i + 1));
        }
    }

    @Test
    void rollDiceKeepsDice() {
        controller.addPlayer("Alice");
        // First roll to get values
        controller.rollDice(new int[]{0, 0, 0, 0, 0});

        // Note: after roll dice are sorted, so keeping specific indices
        // tests the keep mechanism correctly
        List<Integer> firstRoll = controller.getDiceValues();
        int[] kept = {1, 1, 1, 1, 1}; // keep all
        controller.rollDice(kept);
        List<Integer> secondRoll = controller.getDiceValues();
        assertEquals(firstRoll, secondRoll, "All kept dice should maintain their values");
    }

    @Test
    void nextPlayerCyclesCorrectly() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.addPlayer("Charlie");

        assertEquals(0, controller.getCurrentPlayerIndex());
        controller.nextPlayer();
        assertEquals(1, controller.getCurrentPlayerIndex());
        controller.nextPlayer();
        assertEquals(2, controller.getCurrentPlayerIndex());
        controller.nextPlayer();
        assertEquals(0, controller.getCurrentPlayerIndex()); // wraps around
    }

    @Test
    void nextPlayerResetsRollCount() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.rollDice(new int[]{0, 0, 0, 0, 0});
        assertEquals(1, controller.getCurrentRollCount());
        controller.nextPlayer();
        assertEquals(0, controller.getCurrentRollCount());
    }

    @Test
    void resetRollCountWorks() {
        controller.addPlayer("Alice");
        controller.rollDice(new int[]{0, 0, 0, 0, 0});
        controller.resetRollCount();
        assertEquals(0, controller.getCurrentRollCount());
    }

    @Test
    void saveScoreUpdatesPlayerScore() {
        controller.addPlayer("Alice");
        // Set dice to known values: [1, 1, 1, 2, 3]
        List<Die> dice = controller.getDice();
        dice.get(0).setCurrentValue(1);
        dice.get(1).setCurrentValue(1);
        dice.get(2).setCurrentValue(1);
        dice.get(3).setCurrentValue(2);
        dice.get(4).setCurrentValue(3);

        List<Integer> values = controller.getDiceValues();
        // Save Ones combo (index 0) - should be 3 points (3 × 1)
        int points = controller.saveScore(0, values);
        assertEquals(3, points);
        assertEquals(3, controller.getCurrentPlayer().getTotalScore());
    }

    @Test
    void saveScoreMarksComboAsUsed() {
        controller.addPlayer("Alice");
        List<Die> dice = controller.getDice();
        dice.get(0).setCurrentValue(3);
        dice.get(1).setCurrentValue(3);
        dice.get(2).setCurrentValue(3);
        dice.get(3).setCurrentValue(3);
        dice.get(4).setCurrentValue(3);

        List<Integer> values = controller.getDiceValues();
        controller.saveScore(0, values);

        int[] usedCombos = controller.getCurrentPlayer().getUsedCombos();
        assertEquals(1, usedCombos[0]);
    }

    @Test
    void saveScoreUpdatesUpperSectionScore() {
        controller.addPlayer("Alice");
        List<Die> dice = controller.getDice();
        dice.get(0).setCurrentValue(3);
        dice.get(1).setCurrentValue(3);
        dice.get(2).setCurrentValue(3);
        dice.get(3).setCurrentValue(4);
        dice.get(4).setCurrentValue(5);

        List<Integer> values = controller.getDiceValues();
        // Save Threes combo (index 2) — should be 9 points
        controller.saveScore(2, values);
        assertEquals(9, controller.getCurrentPlayer().getUpperSectionScore());
    }

    @Test
    void saveScoreUpdatesLowerSectionScore() {
        controller.addPlayer("Alice");
        List<Die> dice = controller.getDice();
        dice.get(0).setCurrentValue(1);
        dice.get(1).setCurrentValue(2);
        dice.get(2).setCurrentValue(3);
        dice.get(3).setCurrentValue(4);
        dice.get(4).setCurrentValue(5);

        List<Integer> values = controller.getDiceValues();
        // Save Chance combo (index 12) — sum of all = 15
        controller.saveScore(12, values);
        assertEquals(15, controller.getCurrentPlayer().getLowerSectionScore());
    }

    @Test
    void upperSectionBonusAwardedAt63() {
        controller.addPlayer("Alice");
        List<Die> dice = controller.getDice();
        Player player = controller.getCurrentPlayer();

        // Manually set upper section score to 60 first
        player.setUpperSectionScore(60);
        player.setTotalScore(60);

        // Set dice to give us 3 points for Ones
        dice.get(0).setCurrentValue(1);
        dice.get(1).setCurrentValue(1);
        dice.get(2).setCurrentValue(1);
        dice.get(3).setCurrentValue(2);
        dice.get(4).setCurrentValue(5);

        List<Integer> values = controller.getDiceValues();
        // Ones = 3 points, upper section total = 60 + 3 = 63 → bonus!
        controller.saveScore(0, values);

        assertTrue(player.isUpperSectionBonusAwarded());
        assertEquals(60 + 3 + 35, player.getTotalScore()); // original 60 + 3 for ones + 35 bonus
    }

    @Test
    void upperSectionBonusNotAwardedBelow63() {
        controller.addPlayer("Alice");
        List<Die> dice = controller.getDice();
        Player player = controller.getCurrentPlayer();

        player.setUpperSectionScore(50);
        player.setTotalScore(50);

        dice.get(0).setCurrentValue(1);
        dice.get(1).setCurrentValue(1);
        dice.get(2).setCurrentValue(2);
        dice.get(3).setCurrentValue(3);
        dice.get(4).setCurrentValue(4);

        List<Integer> values = controller.getDiceValues();
        // Ones = 2 points, upper total = 52 < 63, no bonus
        controller.saveScore(0, values);

        assertFalse(player.isUpperSectionBonusAwarded());
        assertEquals(52, player.getTotalScore());
    }

    @Test
    void calculatePossibleScoresReturnsCorrectValues() {
        controller.addPlayer("Alice");
        List<Integer> values = List.of(1, 2, 3, 4, 5);
        int[] usedCombos = new int[13]; // all unused

        String[] scores = controller.calculatePossibleScores(values, usedCombos);
        assertEquals(13, scores.length);

        // Ones = 1
        assertEquals("1", scores[0]);
        // Large straight possible = 40
        assertEquals("40", scores[10]);
        // Yahtzee not possible
        assertEquals("/", scores[11]);
        // Chance = 15
        assertEquals("15", scores[12]);
    }

    @Test
    void calculatePossibleScoresReturnsNullForUsedCombos() {
        controller.addPlayer("Alice");
        List<Integer> values = List.of(1, 2, 3, 4, 5);
        int[] usedCombos = new int[13];
        usedCombos[0] = 1; // Ones already used

        String[] scores = controller.calculatePossibleScores(values, usedCombos);
        assertNull(scores[0], "Used combo should return null");
    }

    @Test
    void isGameFinishedFalseInitially() {
        controller.addPlayer("Alice");
        assertFalse(controller.isGameFinished());
    }

    @Test
    void isGameFinishedTrueWhenAllCombosUsed() {
        controller.addPlayer("Alice");
        Player player = controller.getCurrentPlayer();
        // Simulate 13 combos played
        for (int i = 0; i < 13; i++) {
            player.addRolledCombo("combo" + i, 10);
        }
        assertTrue(controller.isGameFinished());
    }

    @Test
    void isGameFinishedRequiresAllPlayers() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");

        Player alice = controller.getPlayers().getFirst();
        for (int i = 0; i < 13; i++) {
            alice.addRolledCombo("combo" + i, 10);
        }
        // Bob hasn't played yet
        assertFalse(controller.isGameFinished());
    }

    @Test
    void getWinnersReturnsSingleWinner() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.getPlayers().get(0).setTotalScore(200);
        controller.getPlayers().get(1).setTotalScore(100);

        List<Player> winners = controller.getWinners();
        assertEquals(1, winners.size());
        assertEquals("Alice", winners.getFirst().getPlayerName());
    }

    @Test
    void getWinnersReturnsTiedPlayers() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.getPlayers().get(0).setTotalScore(150);
        controller.getPlayers().get(1).setTotalScore(150);

        List<Player> winners = controller.getWinners();
        assertEquals(2, winners.size());
    }

    @Test
    void getWinnersEmptyForNoPlayers() {
        assertTrue(controller.getWinners().isEmpty());
    }

    @Test
    void getSortedPlayersReturnsHighestFirst() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.addPlayer("Charlie");
        controller.getPlayers().get(0).setTotalScore(100);
        controller.getPlayers().get(1).setTotalScore(300);
        controller.getPlayers().get(2).setTotalScore(200);

        List<Player> sorted = controller.getSortedPlayers();
        assertEquals("Bob", sorted.get(0).getPlayerName());
        assertEquals("Charlie", sorted.get(1).getPlayerName());
        assertEquals("Alice", sorted.get(2).getPlayerName());
    }

    @Test
    void registryIsNotNull() {
        assertNotNull(controller.getRegistry());
        assertEquals(13, controller.getRegistry().getAllCombos().size());
    }
}

