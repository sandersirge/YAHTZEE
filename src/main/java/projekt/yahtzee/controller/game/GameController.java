package projekt.yahtzee.controller.game;

import projekt.yahtzee.model.Player;
import projekt.yahtzee.model.Die;
import projekt.yahtzee.model.combos.Combination;
import projekt.yahtzee.model.combos.CombinationRegistry;
import projekt.yahtzee.model.combos.FullHouse;
import projekt.yahtzee.model.combos.LargeStraight;
import projekt.yahtzee.model.combos.SmallStraight;
import projekt.yahtzee.util.GameConstants;

import java.util.*;

/**
 * Manages the game logic for the graphical Yahtzee game.
 * Handles player turns, dice rolling, scoring, and bonus calculations.
 * Does not contain any UI logic - that's handled by the JavaFX components.
 * 
 * @author sandersirge
 * @version 1.2.0
 */
public class GameController {
    private final List<Player> players;
    private final List<Die> dice;
    private final CombinationRegistry registry;
    private int currentPlayerIndex;
    private int currentRollCount;
    private boolean extraRules;

    /** Index of the Yahtzee box in the registry. */
    public static final int YAHTZEE_INDEX = 11;
    
    /**
     * Constructs a new game manager.
     * Initializes dice and combination registry.
     */
    public GameController() {
        this.players = new ArrayList<>();
        this.dice = new ArrayList<>();
        this.registry = new CombinationRegistry();
        this.currentPlayerIndex = 0;
        this.currentRollCount = 0;
        
        // Initialize 5 dice
        for (int i = 0; i < 5; i++) {
            dice.add(new Die());
        }
    }
    
    /**
     * Adds a player to the game.
     * 
     * @param name the player's name
     */
    public void addPlayer(String name) {
        players.add(new Player(name));
    }
    
    /**
     * Gets all players in the game.
     * 
     * @return list of all players
     */
    public List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Gets the current active player.
     * 
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Gets the index of the current player.
     * 
     * @return current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    /**
     * Gets all dice in the game.
     * 
     * @return list of all dice
     */
    public List<Die> getDice() {
        return dice;
    }
    
    /**
     * Gets the combination registry.
     * 
     * @return the combination registry
     */
    public CombinationRegistry getRegistry() {
        return registry;
    }
    
    /**
     * Gets the current roll count for this turn.
     * 
     * @return number of rolls taken (0-3)
     */
    public int getCurrentRollCount() {
        return currentRollCount;
    }
    
    /**
     * Rolls the dice based on which ones should be kept.
     * Sorts the dice after rolling so they display in ascending order.
     * 
     * @param kept array where 0 means roll, 1 means keep
     */
    public void rollDice(int[] kept) {
        getCurrentPlayer().rollDice(dice, kept);
        // Sort dice so they display in ascending order
        Collections.sort(dice);
        currentRollCount++;
    }
    
    /**
     * Gets current dice values as a list.
     * Returns values in sorted order (ascending).
     * 
     * @return list of current dice values in sorted order
     */
    public List<Integer> getDiceValues() {
        List<Integer> values = new ArrayList<>();
        for (Die die : dice) {
            values.add(die.getCurrentValue());
        }
        return values;
    }
    
    /**
     * Calculates and returns possible scores for all combinations.
     * Returns "/" for combinations the dice do not make (choosing one scratches it for 0),
     * the points for valid ones, and {@code null} for boxes that are used or, under the
     * Joker rule, not allowed this turn.
     *
     * @param values current dice values
     * @param usedCombos array tracking which combos are already used
     * @return array of strings showing possible scores
     */
    public String[] calculatePossibleScores(List<Integer> values, int[] usedCombos) {
        String[] scores = new String[13];
        List<Combination> combos = registry.getAllCombos();
        boolean joker = !players.isEmpty() && isJoker(values);

        for (int i = 0; i < combos.size(); i++) {
            if (usedCombos[i] != 0 || (joker && !isAllowed(i, values))) {
                scores[i] = null;
                continue;
            }
            Combination combo = combos.get(i);
            if (joker || combo.isPossible(values)) {
                scores[i] = String.valueOf(pointsFor(i, values));
            } else {
                scores[i] = "/";
            }
        }
        return scores;
    }

    /**
     * Turns the official extra rules (Yahtzee bonus and Joker rule) on or off.
     *
     * @param extraRules true to play with them
     */
    public void setExtraRules(boolean extraRules) {
        this.extraRules = extraRules;
    }

    /**
     * @return true if the Yahtzee bonus and Joker rule are on
     */
    public boolean isExtraRules() {
        return extraRules;
    }

    /**
     * Joker rule: with extra rules on, a Yahtzee rolled after the Yahtzee box is filled
     * (with 50 or 0) is a joker for the current player.
     *
     * @param values current dice values
     * @return true if these dice are a joker
     */
    public boolean isJoker(List<Integer> values) {
        return extraRules
            && values.size() == GameConstants.DICE_COUNT
            && values.getFirst() > 0
            && registry.getComboByIndex(YAHTZEE_INDEX).isPossible(values)
            && getCurrentPlayer().isComboUsed(YAHTZEE_INDEX);
    }

    /**
     * Yahtzee bonus: a joker earns +100 when the Yahtzee box holds 50 (not when it was
     * scratched for 0).
     *
     * @param values current dice values
     * @return true if scoring these dice earns the bonus
     */
    public boolean earnsYahtzeeBonus(List<Integer> values) {
        return isJoker(values) && getCurrentPlayer().getComboScore(YAHTZEE_INDEX) == GameConstants.YAHTZEE_POINTS;
    }

    /**
     * Points the current player would get in a box. A joker scores full points in Full House
     * (25) and the straights (30, 40) even though the dice are not a straight.
     *
     * @param comboIndex box index (0-12)
     * @param values     current dice values
     * @return the points
     */
    public int pointsFor(int comboIndex, List<Integer> values) {
        Combination combo = registry.getComboByIndex(comboIndex);
        if (!isJoker(values)) {
            return combo.calculatePoints(values);
        }
        return switch (combo) {
            case FullHouse ignored -> 25;
            case SmallStraight ignored -> 30;
            case LargeStraight ignored -> 40;
            default -> combo.calculatePoints(values);
        };
    }

    /**
     * Tells whether the current player may write these dice into a box. Without a joker any
     * open box is allowed. With a joker the official order applies: the matching upper box if
     * it is open; otherwise any open lower box; only when all lower boxes are full, an upper
     * box (for 0).
     *
     * @param comboIndex box index (0-12)
     * @param values     current dice values
     * @return true if the box may be chosen
     */
    public boolean isAllowed(int comboIndex, List<Integer> values) {
        Player player = getCurrentPlayer();
        if (comboIndex < 0 || comboIndex >= registry.getAllCombos().size() || player.isComboUsed(comboIndex)) {
            return false;
        }
        if (!isJoker(values)) {
            return true;
        }
        int matchingUpper = values.getFirst() - 1;
        if (!player.isComboUsed(matchingUpper)) {
            return comboIndex == matchingUpper;
        }
        return anyLowerOpen(player) != registry.isUpperSection(comboIndex);
    }

    private boolean anyLowerOpen(Player player) {
        for (int i = 6; i < registry.getAllCombos().size(); i++) {
            if (!player.isComboUsed(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Explains where a joker must go, for the status line.
     *
     * @param values current dice values
     * @return a short hint, or an empty string when the dice are not a joker
     */
    public String jokerHint(List<Integer> values) {
        if (!isJoker(values)) {
            return "";
        }
        Player player = getCurrentPlayer();
        int matchingUpper = values.getFirst() - 1;
        if (!player.isComboUsed(matchingUpper)) {
            return GameConstants.MSG_JOKER_UPPER + GameConstants.UPPER_BOX_NAMES[matchingUpper];
        }
        return anyLowerOpen(player) ? GameConstants.MSG_JOKER_LOWER : GameConstants.MSG_JOKER_SCRATCH;
    }

    /**
     * Saves the chosen combination and updates player score.
     * Also awards the upper section bonus (35 at 63 points) and, with extra rules on, the
     * Yahtzee bonus (+100, counted in the lower section).
     *
     * @param comboIndex the index of the chosen combination
     * @param values current dice values
     * @return the points earned from this combination (without the Yahtzee bonus)
     */
    public int saveScore(int comboIndex, List<Integer> values) {
        Player player = getCurrentPlayer();
        Combination chosen = registry.getComboByIndex(comboIndex);
        boolean yahtzeeBonus = earnsYahtzeeBonus(values);
        int points = pointsFor(comboIndex, values);

        player.markComboUsed(comboIndex, points);
        player.addRolledCombo(chosen.getComboName(), points);

        if (registry.isUpperSection(comboIndex)) {
            player.setUpperSectionScore(player.getUpperSectionScore() + points);
        } else {
            player.setLowerSectionScore(player.getLowerSectionScore() + points);
        }
        player.setTotalScore(player.getTotalScore() + points);

        if (yahtzeeBonus) {
            player.addYahtzeeBonus();
            player.setLowerSectionScore(player.getLowerSectionScore() + GameConstants.YAHTZEE_BONUS_POINTS);
            player.setTotalScore(player.getTotalScore() + GameConstants.YAHTZEE_BONUS_POINTS);
        }

        if (!player.isUpperSectionBonusAwarded()
                && player.getUpperSectionScore() >= GameConstants.UPPER_SECTION_BONUS_THRESHOLD) {
            player.setTotalScore(player.getTotalScore() + GameConstants.UPPER_SECTION_BONUS_POINTS);
            player.setUpperSectionBonusAwarded(true);
        }

        return points;
    }

    /**
     * Tells whether all six upper boxes of a player are filled.
     *
     * @param player the player
     * @return true when the upper section is complete
     */
    public boolean isUpperSectionComplete(Player player) {
        for (int i = 0; i < 6; i++) {
            if (!player.isComboUsed(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Advances to the next player's turn.
     * Resets roll count for the new turn.
     */
    public void nextPlayer() {
        currentRollCount = 0;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
    
    /**
     * Resets the roll count for a new turn.
     */
    public void resetRollCount() {
        currentRollCount = 0;
    }
    
    /**
     * Checks if the game is finished.
     * Game is finished when all players have used all 13 combinations.
     * 
     * @return true if game is finished, false otherwise
     */
    public boolean isGameFinished() {
        for (Player player : players) {
            if (player.getRolledComboCount() < 13) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets the winner(s) of the game.
     * Sorts players by score and returns those with the highest score.
     * 
     * @return list of winning players (may be multiple in case of tie)
     */
    public List<Player> getWinners() {
        if (players.isEmpty()) return new ArrayList<>();
        
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Collections.reverseOrder());

        List<Player> winners = new ArrayList<>();
        int highestScore = sortedPlayers.getFirst().getTotalScore();

        for (Player player : sortedPlayers) {
            if (player.getTotalScore() == highestScore) {
                winners.add(player);
            } else {
                break;
            }
        }
        
        return winners;
    }
    
    /**
     * Gets all players sorted by score (highest first).
     * 
     * @return sorted list of players
     */
    public List<Player> getSortedPlayers() {
        List<Player> sorted = new ArrayList<>(players);
        sorted.sort(Collections.reverseOrder());
        return sorted;
    }
}

