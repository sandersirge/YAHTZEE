package projekt.yahtzee.controller.game;

import org.junit.jupiter.api.Test;
import projekt.yahtzee.util.GameConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GameSetupTest {

    @Test
    void namesAreTrimmedAndEmptyOnesGetDefaults() {
        List<String> names = GameSetup.resolveNames(Arrays.asList("  Mari ", "   ", "", null));
        assertEquals(List.of("Mari",
                GameConstants.getDefaultPlayerName(2),
                GameConstants.getDefaultPlayerName(3),
                GameConstants.getDefaultPlayerName(4)), names);
    }

    @Test
    void longNamesAreCut() {
        String name = GameSetup.resolveNames(List.of("x".repeat(40))).getFirst();
        assertEquals(GameSetup.MAX_NAME_LENGTH, name.length());
    }

    @Test
    void duplicatesAreFoundIgnoringCase() {
        assertEquals(Optional.of("mari"), GameSetup.findDuplicate(List.of("Mari", "Jüri", "mari")));
        assertEquals(Optional.empty(), GameSetup.findDuplicate(List.of("Mari", "Jüri")));
    }

    @Test
    void namesListIsCopied() {
        GameSetup setup = new GameSetup(List.of("A", "B"), true);
        assertThrows(UnsupportedOperationException.class, () -> setup.playerNames().add("C"));
        assertTrue(setup.extraRules());
    }
}
