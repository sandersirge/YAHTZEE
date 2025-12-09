package projekt.yahtzee.util;

import projekt.yahtzee.model.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Manages saving game results to file.
 * 
 * @author Yahtzee Game Project
 * @version 1.0
 */
public class ResultsFileManager {
    
    /**
     * Appends the sorted game results to the results file.
     *
     * @param sortedPlayers players sorted by score
     */
    public static void saveResults(List<Player> sortedPlayers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GameConstants.RESULTS_FILE, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            writer.write("\n=== GAME " + LocalDateTime.now().format(formatter) + " ===\n");
            
            for (int i = 0; i < sortedPlayers.size(); i++) {
                Player player = sortedPlayers.get(i);
                writer.write((i + 1) + ". " + player.getPlayerName() +
                           " - " + player.getTotalScore() + " points\n");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Failed to save results to " + GameConstants.RESULTS_FILE + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
