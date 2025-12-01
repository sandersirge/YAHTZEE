package projekt.yahtzee.model.combos;

import java.util.List;

/**
 * Abstract base class representing a scoring combination in Yahtzee.
 * Each concrete implementation defines a specific scoring rule and validation logic.
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public abstract class Combination {
    private final String comboName;
    private final int index;
    
    /**
     * Constructs a new combination with a name and index.
     * 
     * @param comboName the display name of this combination
     * @param index the position of this combination in the score sheet (0-12)
     */
    public Combination(String comboName, int index) {
        this.comboName = comboName;
        this.index = index;
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
     * Gets the index of this combination in the score sheet.
     * 
     * @return the combination index (0-12)
     */
    public int getIndex() {
        return index;
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
     * Returns the string representation of this combination.
     * 
     * @return the combination name
     */
    @Override
    public String toString() {
        return comboName;
    }
}

