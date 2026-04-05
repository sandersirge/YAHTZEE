package projekt.yahtzee.model.combos;

import java.util.List;

/**
 * Represents the Chance combination in Yahtzee.
 * Always valid with any dice values.
 * Scores the sum of all five dice.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class Chance extends Combination {
    /**
     * Constructs a Chance combination.
     */
    public Chance() {
        super("Chance");
    }
    
    /**
     * Calculates points for Chance.
     * The Chance combination scores the sum of all dice, regardless of values.
     * 
     * @param values the sorted list of dice values
     * @return the sum of all dice values
     */
    @Override
    public int calculatePoints(List<Integer> values) {
        return sumAll(values);
    }
    
    /**
     * Checks if the Chance combination is possible.
     * This combination is always valid with any dice values.
     * 
     * @param values the sorted list of dice values
     * @return always returns true
     */
    @Override
    public boolean isPossible(List<Integer> values) {
        return true;
    }
}

