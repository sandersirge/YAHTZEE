package projekt.yahtzee.ui.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import projekt.yahtzee.model.Die;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.controller.ui.ThemeController;
import projekt.yahtzee.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages the dice panel UI for the Yahtzee game.
 * Handles dice display, animations, and keep/roll checkboxes.
 * 
 * @author Yahtzee Game Project
 * @version 1.0
 */
public class DicePanel {
    private static final int DICE_FACE_COUNT = 6;
    private final ThemeController themeController;
    private final List<ImageView> diceImages;
    private final List<Label> keepIndicators;
    private final boolean[] keepSelected;
    private final boolean[] keepDisabled;
    private final Random random;
    private final Image[] diceImageCache;
    private int focusedDieIndex = -1;
    
    /**
     * Constructs a new dice panel.
     * 
     * @param themeController the theme manager instance
     * @param dice list of dice from game manager
     */
    public DicePanel(ThemeController themeController, List<Die> dice) {
        this.themeController = themeController;
        this.diceImages = new ArrayList<>();
        this.keepIndicators = new ArrayList<>();
        this.keepSelected = new boolean[GameConstants.DICE_COUNT];
        this.keepDisabled = new boolean[GameConstants.DICE_COUNT];
        this.random = new Random();
        this.diceImageCache = new Image[7];
        preloadDiceImages();
    }
    
    /**
     * Creates the dice display panel with images and checkboxes.
     * 
     * @return VBox containing the dice panel
     */
    public VBox createDicePanel() {
        VBox dicePanel = new VBox();
        dicePanel.setAlignment(Pos.TOP_CENTER);
        dicePanel.setPrefWidth(GameConstants.DICE_PANEL_WIDTH);
        dicePanel.setSpacing(GameConstants.SPACING_MEDIUM);
        
        // Create dice images
        HBox diceContainer = new HBox();
        diceContainer.setAlignment(Pos.CENTER);
        diceContainer.setSpacing(GameConstants.DICE_SPACING);
        diceContainer.setPrefWidth(GameConstants.DICE_CONTAINER_WIDTH);
        diceContainer.setMinHeight(GameConstants.DICE_CONTAINER_HEIGHT);
        
        for (int i = 0; i < GameConstants.DICE_COUNT; i++) {
            ImageView diceImage = new ImageView();
            diceImage.setFitWidth(GameConstants.DICE_IMAGE_SIZE);
            diceImage.setFitHeight(GameConstants.DICE_IMAGE_SIZE);
            diceImage.setPreserveRatio(true);
            diceImage.setPickOnBounds(true); // allow clicks on transparent area
            diceImage.setCursor(Cursor.HAND);
            setDiceImage(diceImage, 1); // Start with 1
            final int index = i;
            diceImage.setOnMouseClicked(event -> toggleKeep(index));
            diceImages.add(diceImage);
            diceContainer.getChildren().add(diceImage);
        }
        
        // Create selection indicators (labels that show a tick when selected)
        HBox checkboxContainer = new HBox();
        checkboxContainer.setAlignment(Pos.CENTER);
        // Adjust spacing to align indicators under dice centers.
        checkboxContainer.setSpacing(GameConstants.SPACING_DICE_CHECKBOX_ROW);
        checkboxContainer.setPrefWidth(GameConstants.DICE_CONTAINER_WIDTH);
        checkboxContainer.setMinHeight(GameConstants.CHECKBOX_CONTAINER_HEIGHT);
        
        for (int i = 0; i < GameConstants.DICE_COUNT; i++) {
            Label indicator = new Label();
            indicator.setStyle(themeController.getCheckboxStyle());
            indicator.setFont(Font.font(GameConstants.FONT_FAMILY, 28));
            indicator.setMinWidth(34);
            indicator.setAlignment(Pos.CENTER);
            indicator.setMouseTransparent(true); // indicators display state only
            keepIndicators.add(indicator);
            checkboxContainer.getChildren().add(indicator);
        }
        
        // Keep dice label
        Label keepLabel = new Label(GameConstants.LABEL_KEEP_DICE);
        keepLabel.setFont(GameConstants.getCheckboxLabelFont());
        keepLabel.setStyle(themeController.getLabelTextFill());
        
        HBox keepLabelContainer = new HBox();
        keepLabelContainer.setAlignment(Pos.CENTER);
        keepLabelContainer.setPrefWidth(GameConstants.DICE_CONTAINER_WIDTH);
        keepLabelContainer.setMinHeight(GameConstants.TEXT_CONTAINER_HEIGHT);
        keepLabelContainer.getChildren().add(keepLabel);
        
        dicePanel.getChildren().addAll(diceContainer, checkboxContainer, keepLabelContainer);
        return dicePanel;
    }
    
    private void preloadDiceImages() {
        for (int value = 1; value <= DICE_FACE_COUNT; value++) {
            diceImageCache[value] = loadDiceImage(value);
        }
    }

    private void setDiceImage(ImageView imageView, int value) {
        if (value < 1 || value > DICE_FACE_COUNT) {
            throw new IllegalArgumentException("Dice value out of range: " + value);
        }
        Image cachedImage = diceImageCache[value];
        if (cachedImage == null) {
            cachedImage = loadDiceImage(value);
            diceImageCache[value] = cachedImage;
        }
        imageView.setImage(cachedImage);
    }

    /**
     * Loads a dice image for a specific value.
     * 
     * @param imageView the ImageView to load the image into
     * @param value the dice value (1-6)
     */
    private Image loadDiceImage(int value) {
        try {
            String imagePath = GameConstants.getDiceImagePath(value);
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                return new Image(imageUrl.toExternalForm());
            }

            try (InputStream imageStream = ResourceLoader.openResourceStream(imagePath)) {
                return new Image(imageStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading dice image for value " + value, e);
        }
    }
    
    /**
     * Updates dice images based on current dice values.
     * 
     * @param values list of dice values to display
     */
    public void updateDiceImages(List<Integer> values) {
        for (int i = 0; i < Math.min(values.size(), diceImages.size()); i++) {
            setDiceImage(diceImages.get(i), values.get(i));
        }
    }
    
    /**
     * Animates the dice rolling with rotation, scaling, and translation.
     * Animation lasts 1 second total. Updates images during animation and calls callback when finished.
     * 
     * @param kept array indicating which dice to keep (1=keep, 0=roll)
     * @param values final values to display after animation
     * @param onFinished callback to run after animation completes
     */
    public void animateDiceRoll(int[] kept, List<Integer> values, Runnable onFinished) {
        Timeline imageAnimation = new Timeline();
        
        // Create 20 frames, every 50ms (total 1000ms) for image updating
        for (int frame = 0; frame <= 20; frame++) {
            final int frameNumber = frame;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(frame * 50),
                event -> {
                    for (int i = 0; i < diceImages.size(); i++) {
                        int value;
                        if (frameNumber == 20) {
                            // Last frame - show correct result for ALL dice (kept and rolled)
                            value = values.get(i);
                        } else {
                            // Intermediate frames - only animate rolled dice
                            if (kept[i] == 1) continue; // Skip kept dice during animation
                            value = random.nextInt(6) + 1;
                        }
                        setDiceImage(diceImages.get(i), value);
                    }
                }
            );
            imageAnimation.getKeyFrames().add(keyFrame);
        }
        
        // Animation transitions for dice movement
        for (int i = 0; i < diceImages.size(); i++) {
            if (kept[i] == 1) continue; // Skip kept dice
            
            ImageView diceImage = diceImages.get(i);
            
            // Rotation animation (360-720 degrees)
            RotateTransition rotate = new RotateTransition(Duration.seconds(1), diceImage);
            rotate.setByAngle(360 + random.nextInt(360));
            rotate.setCycleCount(1);
            rotate.setInterpolator(Interpolator.EASE_OUT);
            rotate.setOnFinished(e -> diceImage.setRotate(0)); // Reset rotation
            
            // Scale animation (grow and shrink)
            ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), diceImage);
            scale.setToX(1.15);
            scale.setToY(1.15);
            scale.setCycleCount(2);
            scale.setAutoReverse(true);
            scale.setInterpolator(Interpolator.EASE_BOTH);
            
            // Translation animation (bounce up and down)
            TranslateTransition translate = new TranslateTransition(Duration.seconds(0.5), diceImage);
            translate.setByY(-30);
            translate.setCycleCount(2);
            translate.setAutoReverse(true);
            translate.setInterpolator(Interpolator.EASE_BOTH);
            
            // Play animations in parallel
            ParallelTransition parallel = new ParallelTransition(rotate, scale, translate);
            parallel.play();
        }
        
        // Call callback after animation completes
        imageAnimation.setOnFinished(event -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        
        imageAnimation.play();
    }
    
    /**
     * Gets the keep checkboxes.
     * 
     * @return list of checkboxes
     */
    public List<Label> getKeepIndicators() {
        return keepIndicators;
    }
    
    /**
     * Gets the keep status as an array.
     * 
     * @return array where 0 means roll, 1 means keep
     */
    public int[] getKeepStatus() {
        int[] kept = new int[GameConstants.DICE_COUNT];
        for (int i = 0; i < keepIndicators.size(); i++) {
            kept[i] = keepSelected[i] ? 1 : 0;
        }
        return kept;
    }
    
    /**
     * Resets all checkboxes to unchecked.
     */
    public void resetCheckboxes() {
        for (int i = 0; i < keepSelected.length; i++) {
            keepSelected[i] = false;
            updateIndicatorVisual(i);
        }
        refreshDiceEffects();
    }
    
    /**
     * Sets the disabled state of all checkboxes.
     * 
     * @param disabled true to disable checkboxes, false to enable them
     */
    public void setCheckboxesDisabled(boolean disabled) {
        for (int i = 0; i < keepDisabled.length; i++) {
            keepDisabled[i] = disabled;
            updateIndicatorDisabledState(i);
        }
        refreshDiceEffects();
        updateDiceCursors(disabled);
    }
    
    /**
     * Gets the dice images.
     * 
     * @return list of dice image views
     */
    public List<ImageView> getDiceImages() {
        return diceImages;
    }

    public void setFocusedDieIndex(int index) {
        if (index < -1 || index >= diceImages.size()) {
            return;
        }
        this.focusedDieIndex = index;
        refreshDiceEffects();
    }

    public int getFocusedDieIndex() {
        return focusedDieIndex;
    }

    public void toggleKeep(int index) {
        if (index < 0 || index >= keepIndicators.size()) {
            return;
        }
        if (keepDisabled[index]) {
            return;
        }
        keepSelected[index] = !keepSelected[index];
        updateIndicatorVisual(index);
        refreshDiceEffects();
    }

    public void setKeepSelected(int index, boolean selected) {
        if (index < 0 || index >= keepIndicators.size()) {
            return;
        }
        keepSelected[index] = selected;
        updateIndicatorVisual(index);
        refreshDiceEffects();
    }

    public boolean isKeepSelected(int index) {
        if (index < 0 || index >= keepIndicators.size()) {
            return false;
        }
        return keepSelected[index];
    }

    public boolean areCheckboxesDisabled() {
        for (boolean disabled : keepDisabled) {
            if (!disabled) {
                return false;
            }
        }
        return true;
    }

    public void syncKeepSelections(Map<Integer, Integer> keptValueCounts, List<Integer> sortedValues) {
        Arrays.fill(keepSelected, false);
        for (int i = 0; i < sortedValues.size() && i < keepSelected.length; i++) {
            int value = sortedValues.get(i);
            int remaining = keptValueCounts.getOrDefault(value, 0);
            if (remaining > 0) {
                keepSelected[i] = true;
                keptValueCounts.put(value, remaining - 1);
            }
            updateIndicatorVisual(i);
        }
        refreshDiceEffects();
    }

    private void refreshDiceEffects() {
        for (int i = 0; i < diceImages.size(); i++) {
            ImageView imageView = diceImages.get(i);
            imageView.setEffect(null);

            DropShadow base = null;
            if (keepSelected[i]) {
                base = createShadow(Color.web("#66BB6A"), 18);
            }

            if (i == focusedDieIndex) {
                DropShadow focus = createShadow(Color.web("#FFD54F"), 22);
                if (base != null) {
                    focus.setInput(base);
                }
                imageView.setEffect(focus);
            } else if (base != null) {
                imageView.setEffect(base);
            }
        }
    }

    private DropShadow createShadow(Color color, double radius) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(color);
        shadow.setRadius(radius);
        shadow.setSpread(0.6);
        return shadow;
    }

    private void updateIndicatorVisual(int index) {
        Label indicator = keepIndicators.get(index);
        indicator.setText(keepSelected[index] ? "✓" : "");
        indicator.setTextFill(Color.web(themeController.isDarkTheme() ? "#FFFFFF" : "#263238"));
    }

    private void updateIndicatorDisabledState(int index) {
        Label indicator = keepIndicators.get(index);
        indicator.setDisable(keepDisabled[index]);
        indicator.setOpacity(keepDisabled[index] ? 0.5 : 1.0);
    }

    private void updateDiceCursors(boolean disabled) {
        Cursor cursor = disabled ? Cursor.DEFAULT : Cursor.HAND;
        for (ImageView imageView : diceImages) {
            imageView.setCursor(cursor);
        }
    }
}
