package projekt.yahtzee.model.combos;

import java.util.List;

/**
 * Represents the upper section number combinations (Ones, Twos, Threes, Fours, Fives, Sixes).
 * Scores the sum of all dice matching the target number.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class Numbers extends Combination {
    private final int targetNumber;
    
    /**
     * Constructs a Numbers combination for a specific target number.
     * 
     * @param comboName the display name (e.g., "Ones", "Twos")
     * @param number the target number to count (1-6)
     */
    public Numbers(String comboName, int number) {
        super(comboName);
        this.targetNumber = number;
    }
    
    /**
     * Calculates points by counting all dice matching the target number.
     * Score equals the count multiplied by the target number.
     * 
     * @param values the sorted list of dice values
     * @return count of matching dice times the target number
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        int count = 0;
        for (Integer v : values) {
            if (v == targetNumber) count++;
        }
        return count * targetNumber;
    }
    
    /**
     * Checks if at least one die shows the target number.
     * 
     * @param values the sorted list of dice values
     * @return true if at least one die matches the target number
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        return values.contains(targetNumber);
    }
}

