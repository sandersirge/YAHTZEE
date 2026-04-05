package projekt.yahtzee.model.combos;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the Yahtzee combination in Yahtzee game.
 * Requires all 5 dice to show the same value.
 * Always scores 50 points when valid.
 * Note: Named YahtzeeCombination to avoid conflict with game name.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class YahtzeeCombination extends Combination {
    /**
     * Constructs a Yahtzee combination.
     */
    public YahtzeeCombination() {
        super("Yahtzee");
    }
    
    /**
     * Calculates points for Yahtzee.
     * A Yahtzee is always worth 50 points if valid.
     * 
     * @param values the sorted list of dice values
     * @return 50 if valid Yahtzee, 0 otherwise
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        return isPossible(values) ? 50 : 0;
    }
    
    /**
     * Checks if all 5 dice show the same value.
     * Uses a set to verify all values are identical.
     * 
     * @param values the sorted list of dice values
     * @return true if all 5 dice have the same value
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        Set<Integer> uniqueValues = new HashSet<>(values);
        return uniqueValues.size() == 1;
    }
}

