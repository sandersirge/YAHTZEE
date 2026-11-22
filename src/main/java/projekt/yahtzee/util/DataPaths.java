package projekt.yahtzee.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

/**
 * Where the game keeps its files (settings, results, statistics).
 *
 * <p>Order of precedence:
 * <ol>
 *   <li>the {@code yahtzee.data.dir} system property ({@code -Dyahtzee.data.dir=...})</li>
 *   <li>the {@code YAHTZEE_DATA_DIR} environment variable (used by the Docker image)</li>
 *   <li>Windows: {@code %APPDATA%\Yahtzee}</li>
 *   <li>macOS: {@code ~/Library/Application Support/Yahtzee}</li>
 *   <li>Linux and others: {@code $XDG_DATA_HOME/yahtzee}, or {@code ~/.local/share/yahtzee}</li>
 * </ol>
 *
 * <p>Earlier versions wrote the files to the current working directory. When a file is
 * missing from the data folder but exists in the working directory, it is copied over once,
 * so existing settings and statistics carry over.
 *
 * @author sandersirge
 * @version 1.2.0
 */
public final class DataPaths {

    /** Environment variable that overrides the data folder. */
    public static final String DATA_DIR_ENV = "YAHTZEE_DATA_DIR";

    /** System property that overrides the data folder (and the environment variable). */
    public static final String DATA_DIR_PROPERTY = "yahtzee.data.dir";

    private DataPaths() {
        throw new IllegalStateException("Utility class");
    }

    /** @return path of the theme settings file */
    public static Path settingsFile() {
        return resolve(GameConstants.SETTINGS_FILE);
    }

    /** @return path of the game results log */
    public static Path resultsFile() {
        return resolve(GameConstants.RESULTS_FILE);
    }

    /** @return path of the statistics file */
    public static Path statisticsFile() {
        return resolve(GameConstants.STATISTICS_FILE);
    }

    /** @return the data folder for this user and platform */
    public static Path dataDir() {
        String property = System.getProperty(DATA_DIR_PROPERTY);
        if (property != null && !property.isBlank()) {
            return Paths.get(property.trim());
        }
        return dataDir(System.getenv(), System.getProperty("os.name", ""), System.getProperty("user.home", "."));
    }

    /**
     * Works out the data folder from the given environment, so it can be tested.
     *
     * @param env    environment variables
     * @param osName value of {@code os.name}
     * @param home   value of {@code user.home}
     * @return the data folder
     */
    static Path dataDir(Map<String, String> env, String osName, String home) {
        String override = env.get(DATA_DIR_ENV);
        if (override != null && !override.isBlank()) {
            return Paths.get(override.trim());
        }
        String os = osName.toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            String appData = env.get("APPDATA");
            Path base = appData != null && !appData.isBlank()
                ? Paths.get(appData)
                : Paths.get(home, "AppData", "Roaming");
            return base.resolve("Yahtzee");
        }
        if (os.contains("mac")) {
            return Paths.get(home, "Library", "Application Support", "Yahtzee");
        }
        String xdg = env.get("XDG_DATA_HOME");
        Path base = xdg != null && !xdg.isBlank() ? Paths.get(xdg) : Paths.get(home, ".local", "share");
        return base.resolve("yahtzee");
    }

    private static Path resolve(String fileName) {
        Path dir = dataDir();
        Path target = dir.resolve(fileName);
        try {
            Files.createDirectories(dir);
            Path legacy = Paths.get(fileName).toAbsolutePath();
            if (Files.notExists(target) && Files.isRegularFile(legacy) && !legacy.equals(target.toAbsolutePath())) {
                Files.copy(legacy, target);
            }
        } catch (IOException e) {
            System.err.println("Could not prepare data folder " + dir + ": " + e.getMessage());
        }
        return target;
    }
}
