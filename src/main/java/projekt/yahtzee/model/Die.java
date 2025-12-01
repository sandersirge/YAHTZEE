package projekt.yahtzee.model;

/**
 * Represents a single six-sided die in the Yahtzee game.
 * The die can be rolled to generate random values from 1 to 6.
 * Implements Rollable for rolling behavior and Comparable for sorting.
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public class Die implements Rollable, Comparable<Die> {
    private int currentValue;
    
    /**
     * Constructs a new die with an initial value of 0.
     * The die must be rolled to generate a valid value.
     */
    public Die() {
        this.currentValue = 0;
    }
    
    /**
     * Gets the current value showing on the die.
     * 
     * @return the current die value (1-6 after rolling, 0 initially)
     */
    public int getCurrentValue() {
        return currentValue;
    }
    
    /**
     * Sets the die to a specific value.
     * Used primarily for testing or restoring die state.
     * 
     * @param diceValue the value to set (typically 1-6)
     */
    public void setCurrentValue(int diceValue) {
        this.currentValue = diceValue;
    }
    
    /**
     * Rolls the die to generate a random value between 1 and 6.
     * Implements the Rollable interface.
     */
    @Override
    public void roll() {
        this.setCurrentValue((int)(Math.random() * 6 + 1));
    }
    
    /**
     * Returns a string representation of the die.
     * 
     * @return formatted string showing the die's current value
     */
    @Override
    public String toString() {
        return String.valueOf(currentValue);
    }
    
    /**
     * Compares this die to another die based on their current values.
     * Used for sorting dice in ascending order.
     * 
     * @param d the other die to compare to
     * @return negative if this die is lower, positive if higher, 0 if equal
     */
    @Override
    public int compareTo(Die d) {
        return Integer.compare(this.currentValue, d.getCurrentValue());
    }
}

