package projekt.yahtzee.model.combos;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the Full House combination in Yahtzee.
 * Requires three of one value and two of another value.
 * Always scores 25 points when valid.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class FullHouse extends Combination {
    /**
     * Constructs a Full House combination.
     */
    public FullHouse() {
        super("Full House");
    }
    
    /**
     * Calculates points for Full House.
     * A Full House is always worth 25 points if valid.
     * 
     * @param values the sorted list of dice values
     * @return 25 if valid Full House, 0 otherwise
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        return isPossible(values) ? 25 : 0;
    }
    
    /**
     * Checks if dice form a Full House (three of one value and two of another).
     * Uses a frequency map to reliably detect the pattern.
     * 
     * @param values the sorted list of dice values
     * @return true if exactly one value appears 3 times and another appears 2 times
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (Integer value : values) {
            frequency.put(value, frequency.getOrDefault(value, 0) + 1);
        }
        
        boolean hasThree = false;
        boolean hasTwo = false;
        
        for (int count : frequency.values()) {
            if (count == 3) hasThree = true;
            if (count == 2) hasTwo = true;
        }
        
        return hasThree && hasTwo;
    }
}

