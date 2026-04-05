package projekt.yahtzee.controller.game;

import projekt.yahtzee.model.Player;
import projekt.yahtzee.model.Die;
import projekt.yahtzee.model.combos.Combination;
import projekt.yahtzee.model.combos.CombinationRegistry;

import java.util.*;

/**
 * Manages the game logic for the graphical Yahtzee game.
 * Handles player turns, dice rolling, scoring, and bonus calculations.
 * Does not contain any UI logic - that's handled by the JavaFX components.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class GameController {
    private final List<Player> players;
    private final List<Die> dice;
    private final CombinationRegistry registry;
    private int currentPlayerIndex;
    private int currentRollCount;
    
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
     * Returns "/" for invalid combinations and actual points for valid ones.
     * 
     * @param values current dice values
     * @param usedCombos array tracking which combos are already used
     * @return array of strings showing possible scores
     */
    public String[] calculatePossibleScores(List<Integer> values, int[] usedCombos) {
        // Values are already sorted from rollDice(), no need to sort again
        String[] scores = new String[13];
        List<Combination> combos = registry.getAllCombos();
        
        for (int i = 0; i < combos.size(); i++) {
            if (usedCombos[i] == 0) {
                Combination combo = combos.get(i);
                if (combo.isPossible(values)) {
                    scores[i] = String.valueOf(combo.calculatePoints(values));
                } else {
                    scores[i] = "/";
                }
            } else {
                scores[i] = null; // Already used
            }
        }
        
        return scores;
    }
    
    /**
     * Saves the chosen combination and updates player score.
     * Also checks and awards upper section bonus if applicable.
     * 
     * @param comboIndex the index of the chosen combination
     * @param values current dice values
     * @return the points earned from this combination
     */
    public int saveScore(int comboIndex, List<Integer> values) {
        // Values are already sorted from rollDice()
        Player player = getCurrentPlayer();
        Combination chosen = registry.getComboByIndex(comboIndex);
        int points = chosen.calculatePoints(values);
        
        // Mark combination as used
        int[] usedCombos = player.getUsedCombos();
        usedCombos[comboIndex] = 1;
        player.setUsedCombos(usedCombos);
        
        // Add to player's history
        player.addRolledCombo(chosen.getComboName(), points);
        
        // Update section scores
        if (registry.isUpperSection(comboIndex)) {
            player.setUpperSectionScore(player.getUpperSectionScore() + points);
        } else {
            player.setLowerSectionScore(player.getLowerSectionScore() + points);
        }
        
        // Update total score
        player.setTotalScore(player.getTotalScore() + points);
        
        // Check for upper section bonus (63+ points = 35 bonus)
        if (!player.isUpperSectionBonusAwarded() && player.getUpperSectionScore() >= 63) {
            player.setTotalScore(player.getTotalScore() + 35);
            player.setUpperSectionBonusAwarded(true);
        }
        
        return points;
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

