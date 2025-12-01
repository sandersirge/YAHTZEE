package projekt.yahtzee.controller.data;

import projekt.yahtzee.model.Player;

import java.io.*;
import java.util.*;

/**
 * Manages game statistics such as total games, high scores, and win counts stored in statistics.txt.
 */
public class StatisticsController {
    private static final String STATS_FILE = "statistics.txt";
    
    private int totalGames;
    private int highestScore;
    private String highestScorePlayer;
    private double averageScore;
    private int totalScore;
    private Map<String, Integer> playerWins;
    private Map<String, Integer> playerGamesPlayed;
    private Map<String, Integer> playerHighScores;
    
    public StatisticsController() {
        this.totalGames = 0;
        this.highestScore = 0;
        this.highestScorePlayer = "";
        this.averageScore = 0.0;
        this.totalScore = 0;
        this.playerWins = new HashMap<>();
        this.playerGamesPlayed = new HashMap<>();
        this.playerHighScores = new HashMap<>();
        
        loadStatistics();
    }
    
    /**
     * Loads persisted statistics from disk.
     */
    private void loadStatistics() {
        File file = new File(STATS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("TOTAL_GAMES=")) {
                    totalGames = Integer.parseInt(line.substring(12));
                } else if (line.startsWith("HIGHEST_SCORE=")) {
                    highestScore = Integer.parseInt(line.substring(14));
                } else if (line.startsWith("HIGHEST_SCORE_PLAYER=")) {
                    highestScorePlayer = line.substring(21);
                } else if (line.startsWith("TOTAL_SCORE=")) {
                    totalScore = Integer.parseInt(line.substring(12));
                } else if (line.startsWith("PLAYER_WIN=")) {
                    String[] parts = line.substring(11).split(":");
                    playerWins.put(parts[0], Integer.parseInt(parts[1]));
                } else if (line.startsWith("PLAYER_GAMES=")) {
                    String[] parts = line.substring(13).split(":");
                    playerGamesPlayed.put(parts[0], Integer.parseInt(parts[1]));
                } else if (line.startsWith("PLAYER_HIGH=")) {
                    String[] parts = line.substring(12).split(":");
                    playerHighScores.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            
            if (totalGames > 0) {
                averageScore = (double) totalScore / totalGames;
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load statistics: " + e.getMessage());
        }
    }
    
    /**
     * Saves the current statistics to disk.
     */
    private void saveStatistics() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STATS_FILE))) {
            writer.println("TOTAL_GAMES=" + totalGames);
            writer.println("HIGHEST_SCORE=" + highestScore);
            writer.println("HIGHEST_SCORE_PLAYER=" + highestScorePlayer);
            writer.println("TOTAL_SCORE=" + totalScore);
            
            for (Map.Entry<String, Integer> entry : playerWins.entrySet()) {
                writer.println("PLAYER_WIN=" + entry.getKey() + ":" + entry.getValue());
            }
            
            for (Map.Entry<String, Integer> entry : playerGamesPlayed.entrySet()) {
                writer.println("PLAYER_GAMES=" + entry.getKey() + ":" + entry.getValue());
            }
            
            for (Map.Entry<String, Integer> entry : playerHighScores.entrySet()) {
                writer.println("PLAYER_HIGH=" + entry.getKey() + ":" + entry.getValue());
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
        
        // Recalculate average score.
        averageScore = (double) totalScore / (totalGames * players.size());
        
        saveStatistics();
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
