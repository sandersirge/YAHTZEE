package projekt.yahtzee.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataPathsTest {

    @Test
    void environmentVariableWins() {
        assertEquals(Paths.get("/data"),
                DataPaths.dataDir(Map.of(DataPaths.DATA_DIR_ENV, "/data", "APPDATA", "C:/x"), "Windows 11", "/home/u"));
    }

    @Test
    void windowsUsesAppData() {
        assertEquals(Paths.get("C:/Users/u/AppData/Roaming", "Yahtzee"),
                DataPaths.dataDir(Map.of("APPDATA", "C:/Users/u/AppData/Roaming"), "Windows 11", "C:/Users/u"));
        assertEquals(Paths.get("C:/Users/u", "AppData", "Roaming", "Yahtzee"),
                DataPaths.dataDir(Map.of(), "Windows 11", "C:/Users/u"));
    }

    @Test
    void macUsesApplicationSupport() {
        assertEquals(Paths.get("/Users/u", "Library", "Application Support", "Yahtzee"),
                DataPaths.dataDir(Map.of(), "Mac OS X", "/Users/u"));
    }

    @Test
    void linuxUsesXdgDataHome() {
        assertEquals(Paths.get("/xdg", "yahtzee"), DataPaths.dataDir(Map.of("XDG_DATA_HOME", "/xdg"), "Linux", "/home/u"));
        assertEquals(Paths.get("/home/u", ".local", "share", "yahtzee"), DataPaths.dataDir(Map.of(), "Linux", "/home/u"));
    }

    @Test
    void blankOverrideIsIgnored() {
        assertEquals(Paths.get("/home/u", ".local", "share", "yahtzee"),
                DataPaths.dataDir(Map.of(DataPaths.DATA_DIR_ENV, "  "), "Linux", "/home/u"));
    }
}
