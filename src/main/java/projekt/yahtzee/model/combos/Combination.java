package projekt.yahtzee.model.combos;

import java.util.List;

/**
 * Abstract base class representing a scoring combination in Yahtzee.
 * Each concrete implementation defines a specific scoring rule and validation logic.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public abstract class Combination {
    private final String comboName;

    /**
     * Constructs a new combination with a name.
     *
     * @param comboName the display name of this combination
     */
    public Combination(String comboName) {
        this.comboName = comboName;
    }
    
    /**
     * Gets the display name of this combination.
     * 
     * @return the combination name
     */
    public String getComboName() {
        return comboName;
    }
    
    /**
     * Calculates the score for this combination based on the given dice values.
     * If the combination is not possible, should return 0.
     * 
     * @param values the list of dice values (typically 5 dice, each 1-6)
     * @return the calculated score, or 0 if combination is invalid
     */
    public abstract int calculatePoints(List<Integer> values);
    
    /**
     * Checks if this combination can be formed with the given dice values.
     * 
     * @param values the list of dice values (typically 5 dice, each 1-6)
     * @return true if the combination is valid, false otherwise
     */
    public abstract boolean isPossible(List<Integer> values);

    /**
     * Returns the sum of all dice values in the list.
     * Shared helper used by Chance, ThreeOfKind, and FourOfKind.
     *
     * @param values the list of dice values
     * @return the total sum of all values
     */
    protected int sumAll(List<Integer> values) {
        int sum = 0;
        for (int v : values) {
            sum += v;
        }
        return sum;
    }

    /**
     * Returns the string representation of this combination.
     * 
     * @return the combination name
     */
    @Override
    public String toString() {
        return comboName;
    }
}

