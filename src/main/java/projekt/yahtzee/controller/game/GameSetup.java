package projekt.yahtzee.controller.game;

import projekt.yahtzee.util.GameConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * What the setup screen hands to a new game: the player names and whether the official
 * extra rules (Yahtzee bonus and Joker rule) are on.
 *
 * @param playerNames cleaned-up player names, in turn order
 * @param extraRules  true to play with the Yahtzee bonus and Joker rule
 * @author sandersirge
 * @version 1.2.0
 */
public record GameSetup(List<String> playerNames, boolean extraRules) {

    /** Longest name kept; longer ones are cut so the score sheet header stays readable. */
    public static final int MAX_NAME_LENGTH = 20;

    /**
     * Creates a setup with an unmodifiable copy of the names.
     *
     * @param playerNames player names
     * @param extraRules  extra rules on or off
     */
    public GameSetup {
        playerNames = List.copyOf(playerNames);
    }

    /**
     * Cleans up typed names: trims spaces, cuts names that are too long, and gives empty
     * ones the default "N. mängija" name.
     *
     * @param typed names as typed, in player order
     * @return the names to play with
     */
    public static List<String> resolveNames(List<String> typed) {
        List<String> names = new ArrayList<>(typed.size());
        for (int i = 0; i < typed.size(); i++) {
            String name = typed.get(i) == null ? "" : typed.get(i).strip();
            if (name.length() > MAX_NAME_LENGTH) {
                name = name.substring(0, MAX_NAME_LENGTH).strip();
            }
            names.add(name.isEmpty() ? GameConstants.getDefaultPlayerName(i + 1) : name);
        }
        return names;
    }

    /**
     * Finds the first name used twice, ignoring letter case. Statistics are kept per name,
     * so two players with the same name would be counted as one.
     *
     * @param names resolved player names
     * @return the repeated name, if any
     */
    public static Optional<String> findDuplicate(List<String> names) {
        Set<String> seen = new HashSet<>();
        for (String name : names) {
            if (!seen.add(name.toLowerCase(Locale.ROOT))) {
                return Optional.of(name);
            }
        }
        return Optional.empty();
    }
}
