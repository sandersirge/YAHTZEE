package projekt.yahtzee.ui.dialogs;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Shared helpers for measuring text when sizing themed dialogs.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public final class DialogLayoutUtil {
    private DialogLayoutUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Measures the widest line in the given multi-line text.
     *
     * @param text multi-line text content to measure
     * @param font font applied to the rendered text
     * @return layout width of the widest line
     */
    public static double measureMaxLineWidth(String text, Font font) {
        double maxWidth = 0;
        if (text == null || text.isEmpty()) {
            return maxWidth;
        }
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }
            maxWidth = Math.max(maxWidth, measureTextWidth(line, font));
        }
        return maxWidth;
    }

    /**
     * Computes layout bounds width for the provided string.
     *
     * @param content text to measure
     * @param font font used to render the text
     * @return layout width for the given text and font
     */
    public static double measureTextWidth(String content, Font font) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        Text textNode = new Text(content);
        textNode.setFont(font);
        return textNode.getLayoutBounds().getWidth();
    }
}

