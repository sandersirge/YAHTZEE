package projekt.yahtzee.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Yahtzee game.
 * Tracks player score, used combinations, and game statistics.
 * Players are comparable based on their total score.
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public class Player implements Comparable<Player> {
    private String playerName;
    private int totalScore;
    private int[] usedCombos;
    private int upperSectionScore;
    private int lowerSectionScore;
    private boolean upperSectionBonusAwarded;
    private final List<String> rolledComboNames;
    private final List<Integer> rolledComboScores;
    
    /**
     * Constructs a new player with the given name.
     * Initializes score to 0 and creates empty combination tracking lists.
     * 
     * @param name the player's name
     */
    public Player(String name) {
        this.playerName = name;
        this.totalScore = 0;
        this.rolledComboNames = new ArrayList<>();
        this.rolledComboScores = new ArrayList<>();
        this.usedCombos = new int[13];
        this.upperSectionScore = 0;
        this.lowerSectionScore = 0;
        this.upperSectionBonusAwarded = false;
    }
    
    /**
     * Gets the player's name.
     * 
     * @return the player name
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Sets the player's name.
     * 
     * @param playerName the new player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    /**
     * Gets the player's total score including bonuses.
     * 
     * @return the total score
     */
    public int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Sets the player's total score.
     * 
     * @param totalScore the new total score
     */
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
    
    /**
     * Adds a rolled combination to the player's score history.
     * 
     * @param name the name of the combination
     * @param score the points earned from this combination
     */
    public void addRolledCombo(String name, int score) {
        rolledComboNames.add(name);
        rolledComboScores.add(score);
    }
    
    /**
     * Gets the number of combinations the player has rolled.
     * 
     * @return the count of rolled combinations
     */
    public int getRolledComboCount() {
        return rolledComboNames.size();
    }
    
    /**
     * Gets the array tracking which combinations have been used.
     * Index corresponds to combination index, value 0 means unused, 1 means used.
     * 
     * @return the used combinations tracking array
     */
    public int[] getUsedCombos() {
        return usedCombos;
    }
    
    /**
     * Sets the used combinations tracking array.
     * 
     * @param newUsed the new tracking array
     */
    public void setUsedCombos(int[] newUsed) {
        this.usedCombos = newUsed;
    }

    /**
     * Checks if the upper section bonus has been awarded.
     * 
     * @return true if bonus has been awarded, false otherwise
     */
    public boolean isUpperSectionBonusAwarded() {
        return upperSectionBonusAwarded;
    }

    /**
     * Sets whether the upper section bonus has been awarded.
     * 
     * @param upperSectionBonusAwarded true if bonus awarded, false otherwise
     */
    public void setUpperSectionBonusAwarded(boolean upperSectionBonusAwarded) {
        this.upperSectionBonusAwarded = upperSectionBonusAwarded;
    }

    /**
     * Gets the upper section score (sum of Ones through Sixes).
     * 
     * @return the upper section score
     */
    public int getUpperSectionScore() {
        return upperSectionScore;
    }

    /**
     * Sets the upper section score.
     * 
     * @param upperSectionScore the new upper section score
     */
    public void setUpperSectionScore(int upperSectionScore) {
        this.upperSectionScore = upperSectionScore;
    }

    /**
     * Gets the lower section score (sum of all combination scores).
     * 
     * @return the lower section score
     */
    public int getLowerSectionScore() {
        return lowerSectionScore;
    }

    /**
     * Sets the lower section score.
     * 
     * @param lowerSectionScore the new lower section score
     */
    public void setLowerSectionScore(int lowerSectionScore) {
        this.lowerSectionScore = lowerSectionScore;
    }

    /**
     * Rolls all dice that are not marked as kept.
     * 
     * @param dice the list of dice to potentially roll
     * @param kept array where 0 means roll the die, 1 means keep it
     */
    public void rollDice(List<Die> dice, int[] kept) {
        int counter = 0;
        for (Die die : dice) {
            if (kept[counter++] == 0) die.roll();
        }
    }
    
    /**
     * Creates a formatted string displaying all rolled combinations and their scores.
     * 
     * @return formatted string of all rolled combinations
     */
    public String displayCombos() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < rolledComboNames.size(); i++) {
            result.append("\n").append(rolledComboNames.get(i))
                  .append(" - earned points: ").append(rolledComboScores.get(i));
        }
        return result + "\n";
    }
    
    /**
     * Returns a string representation of the player including name, score, and all rolled combinations.
     * 
     * @return formatted player information
     */
    @Override
    public String toString() {
        return "PLAYER NAME: " + playerName + "\n" +
               "SCORE: [ " + totalScore + " ]\n" +
               "ROLLED COMBOS:" + displayCombos();
    }
    
    /**
     * Compares this player to another based on total score.
     * 
     * @param p the other player to compare to
     * @return negative if this player has lower score, positive if higher, 0 if equal
     */
    @Override
    public int compareTo(Player p) {
        return Integer.compare(this.getTotalScore(), p.getTotalScore());
    }
}

