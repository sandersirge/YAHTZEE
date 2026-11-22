package projekt.yahtzee.util;

import projekt.yahtzee.model.Player;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Manages saving game results to file.
 * 
 * @author sandersirge
 * @version 1.1.0
 */
public class ResultsFileManager {
    
    /**
     * Appends the sorted game results to the results file.
     *
     * @param sortedPlayers players sorted by score
     */
    public static void saveResults(List<Player> sortedPlayers) {
        saveResults(DataPaths.resultsFile(), sortedPlayers);
    }

    /**
     * Appends a game's results to the given file.
     *
     * @param resultsPath   results log
     * @param sortedPlayers players ordered by score
     */
    public static void saveResults(Path resultsPath, List<Player> sortedPlayers) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                resultsPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            writer.write("\n=== GAME " + LocalDateTime.now().format(formatter) + " ===\n");
            
            for (int i = 0; i < sortedPlayers.size(); i++) {
                Player player = sortedPlayers.get(i);
                writer.write((i + 1) + ". " + player.getPlayerName() +
                           " - " + player.getTotalScore() + " points\n");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Failed to save results to " + resultsPath + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Loads the saved results file into memory using UTF-8. Callers are responsible for handling missing files.
     *
     * @return lines from the results file in order
     * @throws IOException if the file cannot be read
     */
    public static List<String> loadResults() throws IOException {
        return loadResults(DataPaths.resultsFile());
    }

    /**
     * Reads the results log.
     *
     * @param resultsPath results log
     * @return its lines
     * @throws IOException if it cannot be read
     */
    public static List<String> loadResults(Path resultsPath) throws IOException {
        return Files.readAllLines(resultsPath, StandardCharsets.UTF_8);
    }
}
