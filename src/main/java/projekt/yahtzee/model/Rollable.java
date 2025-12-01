package projekt.yahtzee.model;

/**
 * Interface for objects that can be rolled (like dice).
 * Implementing classes should provide rolling behavior.
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public interface Rollable {
    /**
     * Rolls the object to generate a new random state.
     */
    void roll();
}

