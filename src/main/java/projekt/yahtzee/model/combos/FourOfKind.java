package projekt.yahtzee.model.combos;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the Four of a Kind combination in Yahtzee.
 * Requires at least four dice showing the same value.
 * Scores the sum of all five dice.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class FourOfKind extends Combination {
    /**
     * Constructs a Four of a Kind combination.
     */
    public FourOfKind() {
        super("Four of a Kind");
    }
    
    /**
     * Calculates points for Four of a Kind.
     * If the combination is valid, returns the sum of all dice.
     * 
     * @param values the sorted list of dice values
     * @return sum of all dice if valid, 0 otherwise
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        if (!isPossible(values)) return 0;
        return sumAll(values);
    }
    
    /**
     * Checks if at least four dice show the same value.
     * Uses a frequency map for more reliable detection.
     * 
     * @param values the sorted list of dice values
     * @return true if at least four dice have the same value
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (Integer value : values) {
            frequency.put(value, frequency.getOrDefault(value, 0) + 1);
            if (frequency.get(value) >= 4) {
                return true;
            }
        }
        return false;
    }
}

