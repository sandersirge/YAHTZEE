package projekt.yahtzee.model.combos;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the Large Straight combination in Yahtzee.
 * Requires all 5 dice to form a consecutive sequence.
 * Always scores 40 points when valid.
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public class LargeStraight extends Combination {
    /**
     * Constructs a Large Straight combination.
     * 
     * @param index the position of this combination in the score sheet
     */
    public LargeStraight(int index) {
        super("Large Straight", index);
    }
    
    /**
     * Calculates points for Large Straight.
     * A Large Straight is always worth 40 points if valid.
     * 
     * @param values the sorted list of dice values
     * @return 40 if valid Large Straight, 0 otherwise
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        return isPossible(values) ? 40 : 0;
    }
    
    /**
     * Checks if all 5 dice form a consecutive sequence.
     * Valid patterns: 1-2-3-4-5 or 2-3-4-5-6.
     * Uses a set-based approach for cleaner detection.
     * 
     * @param values the sorted list of dice values
     * @return true if all 5 dice are consecutive
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        Set<Integer> uniqueValues = new HashSet<>(values);
        
        // Must have exactly 5 different values for a straight
        if (uniqueValues.size() != 5) {
            return false;
        }
        
        // Check for sequences: 1-2-3-4-5 or 2-3-4-5-6
        return (uniqueValues.contains(1) && uniqueValues.contains(2) && 
                uniqueValues.contains(3) && uniqueValues.contains(4) && 
                uniqueValues.contains(5)) ||
               (uniqueValues.contains(2) && uniqueValues.contains(3) && 
                uniqueValues.contains(4) && uniqueValues.contains(5) && 
                uniqueValues.contains(6));
    }
}

