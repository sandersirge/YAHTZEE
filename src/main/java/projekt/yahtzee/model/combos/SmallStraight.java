package projekt.yahtzee.model.combos;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the Small Straight combination in Yahtzee.
 * Requires at least 4 consecutive dice values.
 * Always scores 30 points when valid.
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public class SmallStraight extends Combination {
    /**
     * Constructs a Small Straight combination.
     * 
     * @param index the position of this combination in the score sheet
     */
    public SmallStraight(int index) {
        super("Small Straight", index);
    }
    
    /**
     * Calculates points for Small Straight.
     * A Small Straight is always worth 30 points if valid.
     * 
     * @param values the sorted list of dice values
     * @return 30 if valid Small Straight, 0 otherwise
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        return isPossible(values) ? 30 : 0;
    }
    
    /**
     * Checks if dice contain at least 4 consecutive numbers.
     * Valid patterns: 1-2-3-4, 2-3-4-5, or 3-4-5-6.
     * Uses a set-based approach for cleaner detection.
     * 
     * @param values the sorted list of dice values
     * @return true if at least 4 consecutive numbers exist
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        Set<Integer> uniqueValues = new HashSet<>(values);
        
        // Check for sequences: 1-2-3-4, 2-3-4-5, 3-4-5-6
        return (uniqueValues.contains(1) && uniqueValues.contains(2) && 
                uniqueValues.contains(3) && uniqueValues.contains(4)) ||
               (uniqueValues.contains(2) && uniqueValues.contains(3) && 
                uniqueValues.contains(4) && uniqueValues.contains(5)) ||
               (uniqueValues.contains(3) && uniqueValues.contains(4) && 
                uniqueValues.contains(5) && uniqueValues.contains(6));
    }
}

