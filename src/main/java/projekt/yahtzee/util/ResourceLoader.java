package projekt.yahtzee.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility to load classpath resources with fallbacks and to materialize them as temp files when needed.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public final class ResourceLoader {
    private static final Map<String, Path> TEMP_FILE_CACHE = new ConcurrentHashMap<>();

    private ResourceLoader() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Opens a resource stream using multiple lookup strategies.
     *
     * @param resourcePath path to the resource (leading slash allowed)
     * @return input stream to the resource
     * @throws IOException when the resource cannot be found or opened
     */
    public static InputStream openResourceStream(String resourcePath) throws IOException {
        InputStream stream = ResourceLoader.class.getResourceAsStream(resourcePath);
        if (stream != null) return stream;

        String trimmedPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;

        ClassLoader loader = ResourceLoader.class.getClassLoader();
        if (loader != null) {
            stream = loader.getResourceAsStream(trimmedPath);
            if (stream != null) return stream;
        }

        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
            stream = contextLoader.getResourceAsStream(trimmedPath);
            if (stream != null) return stream;
        }

        try {
            stream = ResourceLoader.class.getModule().getResourceAsStream(trimmedPath);
            if (stream != null) return stream;
        } catch (IOException ignored) {
            // Ignore missing module lookups.
        }

        String[] possiblePaths = {
            "src/main/resources/" + trimmedPath,
            "YAHTZEE/src/main/resources/" + trimmedPath,
            "../YAHTZEE/src/main/resources/" + trimmedPath,
            "build/resources/main/" + trimmedPath
        };

        for (String filePath : possiblePaths) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    return new FileInputStream(file);
                }
            } catch (FileNotFoundException ignored) {
                // Keep searching.
            }
        }

        throw new IOException("Resource not found: " + resourcePath);
    }

    /**
     * Copies a resource into a temporary file.
     *
     * @param resourcePath path of the resource to copy
     * @return temp file containing the resource contents
     * @throws IOException when the resource cannot be read or the temp file cannot be written
     */
    public static File copyResourceToTempFile(String resourcePath) throws IOException {
        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        int extensionIndex = fileName.lastIndexOf('.');
        String baseName = extensionIndex >= 0 ? fileName.substring(0, extensionIndex) : fileName;
        String extension = extensionIndex >= 0 ? fileName.substring(extensionIndex) : ".tmp";

        File tempFile = File.createTempFile(baseName, extension);
        tempFile.deleteOnExit();

        try (InputStream stream = openResourceStream(resourcePath)) {
            Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return tempFile;
    }

    /**
     * Retrieves a cached temporary file for the given resource, copying it once per application run.
     *
     * @param resourcePath path of the resource to materialize
     * @return cached temp file containing the resource contents
     * @throws IOException when the resource cannot be read or the temp file cannot be written
     */
    public static File copyResourceToCachedTempFile(String resourcePath) throws IOException {
        Path cachedPath = TEMP_FILE_CACHE.get(resourcePath);
        if (cachedPath != null && Files.exists(cachedPath)) {
            return cachedPath.toFile();
        }

        synchronized (TEMP_FILE_CACHE) {
            cachedPath = TEMP_FILE_CACHE.get(resourcePath);
            if (cachedPath != null && Files.exists(cachedPath)) {
                return cachedPath.toFile();
            }

            File tempFile = copyResourceToTempFile(resourcePath);
            TEMP_FILE_CACHE.put(resourcePath, tempFile.toPath());
            return tempFile;
        }
    }
}
