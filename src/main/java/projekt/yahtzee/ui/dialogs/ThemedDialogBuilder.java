package projekt.yahtzee.ui.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.util.GameConstants;

/**
 * Builds the common layout scaffolding used by themed dialogs, providing a centered container
 * with Yahtzee styling and consistent padding.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public final class ThemedDialogBuilder {
    private static final double DEFAULT_BACKGROUND_PADDING = 30;
    private static final Insets DEFAULT_CONTAINER_PADDING = new Insets(20, 40, 20, 40);
    private static final double DEFAULT_MIN_WIDTH = 380;
    private static final double DEFAULT_MAX_WIDTH = 720;

    private final ThemeController themeController;
    private double minWidth = DEFAULT_MIN_WIDTH;
    private double maxWidth = DEFAULT_MAX_WIDTH;
    private double preferredWidth = DEFAULT_MIN_WIDTH;

    private ThemedDialogBuilder(ThemeController themeController) {
        this.themeController = themeController;
    }

    /**
     * Creates a new builder instance.
     *
     * @param themeController controller providing theme-dependent styles
     * @return new builder instance
     */
    public static ThemedDialogBuilder create(ThemeController themeController) {
        return new ThemedDialogBuilder(themeController);
    }

    /**
     * Sets the minimum width of the dialog container.
     *
     * @param minWidth minimum width in pixels
     * @return this builder for chaining
     */
    public ThemedDialogBuilder withMinWidth(double minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    /**
     * Sets the maximum width of the dialog container.
     *
     * @param maxWidth maximum width in pixels
     * @return this builder for chaining
     */
    public ThemedDialogBuilder withMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    /**
     * Sets the preferred width of the dialog container.
     *
     * @param preferredWidth preferred width in pixels
     * @return this builder for chaining
     */
    public ThemedDialogBuilder withPreferredWidth(double preferredWidth) {
        this.preferredWidth = preferredWidth;
        return this;
    }


    /**
     * Builds the themed dialog layout with the given content nodes.
     *
     * @param content child nodes to place inside the dialog container
     * @return the completed background pane ready for use in a scene
     */
    public StackPane build(Node... content) {
        StackPane background = new StackPane();
        background.setStyle(themeController.getBackgroundStyle());
        background.setPadding(new Insets(DEFAULT_BACKGROUND_PADDING));

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(GameConstants.SPACING_NORMAL);
        container.setPadding(DEFAULT_CONTAINER_PADDING);
        container.setMinWidth(minWidth);
        container.setMaxWidth(maxWidth);
        container.setPrefWidth(preferredWidth);
        String baseStyle = "-fx-background-color: " + themeController.getBoxBackgroundColor() + ";";
        container.setStyle(baseStyle + " -fx-background-radius: 18; -fx-border-radius: 18;");

        container.getChildren().addAll(content);
        background.getChildren().add(container);
        return background;
    }
}
