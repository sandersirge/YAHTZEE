package projekt.yahtzee.model.combos;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry that manages all available Yahtzee scoring combinations.
 * Creates and stores all 13 combinations (6 upper section + 7 lower section).
 * 
 * @author Sander Sirge
 * @version 1.0
 */
public class CombinationRegistry {
    private final List<Combination> allCombos;
    
    /**
     * Constructs a new registry and initializes all 13 Yahtzee combinations.
     */
    public CombinationRegistry() {
        allCombos = new ArrayList<>();
        initCombos();
    }
    
    /**
     * Initializes all 13 Yahtzee combinations in order.
     * Upper section (0-5): Ones through Sixes.
     * Lower section (6-12): Three of a Kind through Chance.
     */
    private void initCombos() {
        // Upper section (numbers 1-6)
        allCombos.add(new Numbers("Ones", 0, 1));
        allCombos.add(new Numbers("Twos", 1, 2));
        allCombos.add(new Numbers("Threes", 2, 3));
        allCombos.add(new Numbers("Fours", 3, 4));
        allCombos.add(new Numbers("Fives", 4, 5));
        allCombos.add(new Numbers("Sixes", 5, 6));
        
        // Lower section (combinations)
        allCombos.add(new ThreeOfKind(6));
        allCombos.add(new FourOfKind(7));
        allCombos.add(new FullHouse(8));
        allCombos.add(new SmallStraight(9));
        allCombos.add(new LargeStraight(10));
        allCombos.add(new YahtzeeCombination(11));
        allCombos.add(new Chance(12));
    }
    
    /**
     * Gets the complete list of all combinations.
     * 
     * @return list containing all 13 combinations
     */
    public List<Combination> getAllCombos() {
        return allCombos;
    }
    
    /**
     * Retrieves a specific combination by its index.
     * 
     * @param index the combination index (0-12)
     * @return the combination at the specified index
     */
    public Combination getComboByIndex(int index) {
        return allCombos.get(index);
    }
    
    /**
     * Checks if a combination index belongs to the upper section.
     * Upper section includes Ones through Sixes (indices 0-5).
     * Used for calculating the 35-point bonus when upper section totals 63+.
     * 
     * @param index the combination index to check
     * @return true if index is in upper section (0-5), false otherwise
     */
    public boolean isUpperSection(int index) {
        return index >= 0 && index <= 5;
    }
}

