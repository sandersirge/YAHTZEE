package projekt.yahtzee.model.combos;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the Three of a Kind combination in Yahtzee.
 * Requires at least three dice showing the same value.
 * Scores the sum of all five dice.
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public class ThreeOfKind extends Combination {
    /**
     * Constructs a Three of a Kind combination.
     * 
     * @param index the position of this combination in the score sheet
     */
    public ThreeOfKind(int index) {
        super("Three of a Kind", index);
    }
    
    /**
     * Calculates points for Three of a Kind.
     * If the combination is valid, returns the sum of all dice.
     * 
     * @param values the sorted list of dice values
     * @return sum of all dice if valid, 0 otherwise
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        if (!isPossible(values)) return 0;
        int sum = 0;
        for (Integer v : values) {
            sum += v;
        }
        return sum;
    }
    
    /**
     * Checks if at least three dice show the same value.
     * Uses a frequency map for more reliable detection.
     * 
     * @param values the sorted list of dice values
     * @return true if at least three dice have the same value
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (Integer value : values) {
            frequency.put(value, frequency.getOrDefault(value, 0) + 1);
            if (frequency.get(value) >= 3) {
                return true;
            }
        }
        return false;
    }
}

