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
 */
public final class ThemedDialogBuilder {
    private static final double DEFAULT_BACKGROUND_PADDING = 30;
    private static final Insets DEFAULT_CONTAINER_PADDING = new Insets(20, 40, 20, 40);
    private static final double DEFAULT_MIN_WIDTH = 380;
    private static final double DEFAULT_MAX_WIDTH = 720;

    private final ThemeController themeController;
    private double backgroundPadding = DEFAULT_BACKGROUND_PADDING;
    private Insets containerPadding = DEFAULT_CONTAINER_PADDING;
    private double minWidth = DEFAULT_MIN_WIDTH;
    private double maxWidth = DEFAULT_MAX_WIDTH;
    private double preferredWidth = DEFAULT_MIN_WIDTH;

    private ThemedDialogBuilder(ThemeController themeController) {
        this.themeController = themeController;
    }

    public static ThemedDialogBuilder create(ThemeController themeController) {
        return new ThemedDialogBuilder(themeController);
    }

    public ThemedDialogBuilder withMinWidth(double minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public ThemedDialogBuilder withMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public ThemedDialogBuilder withPreferredWidth(double preferredWidth) {
        this.preferredWidth = preferredWidth;
        return this;
    }

    public ThemedDialogBuilder withContainerPadding(Insets padding) {
        this.containerPadding = padding;
        return this;
    }

    public ThemedDialogBuilder withBackgroundPadding(double padding) {
        this.backgroundPadding = padding;
        return this;
    }

    public StackPane build(Node... content) {
        StackPane background = new StackPane();
        background.setStyle(themeController.getBackgroundStyle());
        background.setPadding(new Insets(backgroundPadding));

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(GameConstants.SPACING_NORMAL);
        container.setPadding(containerPadding);
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
