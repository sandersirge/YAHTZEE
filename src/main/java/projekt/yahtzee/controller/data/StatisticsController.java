package projekt.yahtzee.controller.data;

import projekt.yahtzee.model.Player;
import projekt.yahtzee.util.GameConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages game statistics such as total games, high scores, and win counts stored in statistics.txt.
 */
public class StatisticsController {
    private int totalGames;
    private int highestScore;
    private String highestScorePlayer;
    private double averageScore;
    private int totalScore;
    private int totalPlayerEntries;
    private Map<String, Integer> playerWins;
    private Map<String, Integer> playerGamesPlayed;
    private Map<String, Integer> playerHighScores;
    
    public StatisticsController() {
        this.totalGames = 0;
        this.highestScore = 0;
        this.highestScorePlayer = "";
        this.averageScore = 0.0;
        this.totalScore = 0;
        this.totalPlayerEntries = 0;
        this.playerWins = new HashMap<>();
        this.playerGamesPlayed = new HashMap<>();
        this.playerHighScores = new HashMap<>();
        
        loadStatistics();
    }
    
    /**
     * Loads persisted statistics from disk.
     */
    private void loadStatistics() {
        Path statsPath = Paths.get(GameConstants.STATISTICS_FILE);
        if (Files.notExists(statsPath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(statsPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                if (trimmed.startsWith("TOTAL_GAMES=")) {
                    totalGames = parseIntSafe(trimmed.substring(12), totalGames);
                } else if (trimmed.startsWith("HIGHEST_SCORE=")) {
                    highestScore = parseIntSafe(trimmed.substring(14), highestScore);
                } else if (trimmed.startsWith("HIGHEST_SCORE_PLAYER=")) {
                    highestScorePlayer = trimmed.substring(21);
                } else if (trimmed.startsWith("TOTAL_SCORE=")) {
                    totalScore = parseIntSafe(trimmed.substring(12), totalScore);
                } else if (trimmed.startsWith("TOTAL_PLAYER_ENTRIES=")) {
                    totalPlayerEntries = parseIntSafe(trimmed.substring(22), totalPlayerEntries);
                } else if (trimmed.startsWith("PLAYER_WIN=")) {
                    String[] parts = trimmed.substring(11).split(":");
                    playerWins.put(parts[0], Integer.parseInt(parts[1]));
                } else if (trimmed.startsWith("PLAYER_GAMES=")) {
                    String[] parts = trimmed.substring(13).split(":");
                    playerGamesPlayed.put(parts[0], Integer.parseInt(parts[1]));
                } else if (trimmed.startsWith("PLAYER_HIGH=")) {
                    String[] parts = trimmed.substring(12).split(":");
                    playerHighScores.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            if (totalPlayerEntries == 0) {
                totalPlayerEntries = playerGamesPlayed.values().stream().mapToInt(Integer::intValue).sum();
            }
            if (totalPlayerEntries > 0) {
                averageScore = (double) totalScore / totalPlayerEntries;
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load statistics: " + e.getMessage());
        }
    }
    
    /**
     * Saves the current statistics to disk.
     */
    private void saveStatistics() {
        Path statsPath = Paths.get(GameConstants.STATISTICS_FILE);
        try {
            Path parent = statsPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                statsPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
                writer.write("TOTAL_GAMES=" + totalGames);
                writer.newLine();
                writer.write("HIGHEST_SCORE=" + highestScore);
                writer.newLine();
                writer.write("HIGHEST_SCORE_PLAYER=" + highestScorePlayer);
                writer.newLine();
                writer.write("TOTAL_SCORE=" + totalScore);
                writer.newLine();
                writer.write("TOTAL_PLAYER_ENTRIES=" + totalPlayerEntries);
                writer.newLine();

                writeEntries(writer, "PLAYER_WIN=", playerWins);
                writeEntries(writer, "PLAYER_GAMES=", playerGamesPlayed);
                writeEntries(writer, "PLAYER_HIGH=", playerHighScores);
            }
        } catch (IOException e) {
            System.err.println("Failed to save statistics: " + e.getMessage());
        }
    }
    
    /**
     * Updates statistics after a game concludes.
     */
    public void updateStatistics(List<Player> players, List<Player> winners) {
        totalGames++;
        totalPlayerEntries += players.size();
        
        for (Player player : players) {
            String name = player.getPlayerName();
            int score = player.getTotalScore();
            
            // Update combined score.
            totalScore += score;
            
            // Update the number of games played by the player.
            playerGamesPlayed.put(name, playerGamesPlayed.getOrDefault(name, 0) + 1);
            
            // Update the global highest score.
            if (score > highestScore) {
                highestScore = score;
                highestScorePlayer = name;
            }
            
            // Update the player's personal best score.
            int currentHigh = playerHighScores.getOrDefault(name, 0);
            if (score > currentHigh) {
                playerHighScores.put(name, score);
            }
        }
        
        // Update win counts.
        for (Player winner : winners) {
            String name = winner.getPlayerName();
            playerWins.put(name, playerWins.getOrDefault(name, 0) + 1);
        }
        
        // Recalculate average score across all player entries.
        averageScore = totalPlayerEntries == 0 ? 0.0 : (double) totalScore / totalPlayerEntries;
        
        saveStatistics();
    }

    private int parseIntSafe(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void writeEntries(BufferedWriter writer, String prefix, Map<String, Integer> values) throws IOException {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(values.entrySet());
        entries.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, Integer> entry : entries) {
            writer.write(prefix + entry.getKey() + ":" + entry.getValue());
            writer.newLine();
        }
    }
    
    // Getters.
    public int getTotalGames() {
        return totalGames;
    }
    
    public int getHighestScore() {
        return highestScore;
    }
    
    public String getHighestScorePlayer() {
        return highestScorePlayer;
    }
    
    public double getAverageScore() {
        return averageScore;
    }
    
    public Map<String, Integer> getPlayerWins() {
        return new HashMap<>(playerWins);
    }
    
    public Map<String, Integer> getPlayerGamesPlayed() {
        return new HashMap<>(playerGamesPlayed);
    }
    
    public Map<String, Integer> getPlayerHighScores() {
        return new HashMap<>(playerHighScores);
    }
    
    public int getPlayerWinCount(String playerName) {
        return playerWins.getOrDefault(playerName, 0);
    }
    
    public int getPlayerGamesCount(String playerName) {
        return playerGamesPlayed.getOrDefault(playerName, 0);
    }
    
    public int getPlayerHighScore(String playerName) {
        return playerHighScores.getOrDefault(playerName, 0);
    }
}
