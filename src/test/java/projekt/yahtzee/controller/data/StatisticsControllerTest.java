package projekt.yahtzee.controller.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import projekt.yahtzee.model.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsControllerTest {

    @TempDir
    Path dir;

    private static Player player(String name, int score) {
        Player p = new Player(name);
        p.setTotalScore(score);
        return p;
    }

    @Test
    void statisticsSurviveASaveAndLoad() {
        Path file = dir.resolve("statistics.txt");
        StatisticsController stats = new StatisticsController(file);
        Player mari = player("Mari", 210);
        Player odd = player("Kass: Miisu", 150);
        stats.updateStatistics(List.of(mari, odd), List.of(mari));

        StatisticsController reloaded = new StatisticsController(file);
        assertEquals(1, reloaded.getTotalGames());
        assertEquals(210, reloaded.getHighestScore());
        assertEquals("Mari", reloaded.getHighestScorePlayer());
        assertEquals(180.0, reloaded.getAverageScore(), 1e-9);
        assertEquals(1, reloaded.getPlayerWins().get("Mari"));
        assertEquals(150, reloaded.getPlayerHighScores().get("Kass: Miisu"), "names may contain colons");
    }

    @Test
    void multiDigitPlayerEntryCountIsReadInFull() throws IOException {
        Path file = dir.resolve("statistics.txt");
        Files.writeString(file, "TOTAL_GAMES=4\nTOTAL_SCORE=2400\nTOTAL_PLAYER_ENTRIES=12\n", StandardCharsets.UTF_8);
        assertEquals(200.0, new StatisticsController(file).getAverageScore(), 1e-9);
    }

    @Test
    void badLinesAreSkippedWithoutLosingTheRest() throws IOException {
        Path file = dir.resolve("statistics.txt");
        Files.writeString(file, String.join("\n",
                "TOTAL_GAMES=3",
                "PLAYER_WIN=broken",
                "PLAYER_WIN=Jüri:notanumber",
                "garbage",
                "PLAYER_WIN=Mari:2",
                "PLAYER_GAMES=Mari:3",
                "HIGHEST_SCORE=250"), StandardCharsets.UTF_8);

        StatisticsController stats = new StatisticsController(file);
        assertEquals(3, stats.getTotalGames());
        assertEquals(250, stats.getHighestScore());
        assertEquals(2, stats.getPlayerWins().get("Mari"));
        assertFalse(stats.getPlayerWins().containsKey("Jüri"));
    }

    @Test
    void missingFileStartsEmpty() {
        StatisticsController stats = new StatisticsController(dir.resolve("none.txt"));
        assertEquals(0, stats.getTotalGames());
        assertTrue(stats.getPlayerWins().isEmpty());
    }
}
