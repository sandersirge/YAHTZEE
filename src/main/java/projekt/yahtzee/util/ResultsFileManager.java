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
     * @param sorteeritudMängijad players sorted by score
     */
    public static void salvestaTulemused(List<Player> sorteeritudMängijad) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GameConstants.RESULTS_FILE, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            writer.write("\n=== GAME " + LocalDateTime.now().format(formatter) + " ===\n");
            
            for (int i = 0; i < sorteeritudMängijad.size(); i++) {
                Player mängija = sorteeritudMängijad.get(i);
                writer.write((i + 1) + ". " + mängija.getPlayerName() +
                           " - " + mängija.getTotalScore() + " points\n");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Failed to save results: " + e.getMessage());
        }
    }
}
