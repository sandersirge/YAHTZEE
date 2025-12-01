package projekt.yahtzee.ui.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import projekt.yahtzee.model.Die;
import projekt.yahtzee.util.GameConstants;
import projekt.yahtzee.controller.ui.ThemeController;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages the dice panel UI for the Yahtzee game.
 * Handles dice display, animations, and keep/roll checkboxes.
 * 
 * @author Yahtzee Game Project
 * @version 1.0
 */
public class DicePanel {
    private final ThemeController themeController;
    private final List<ImageView> diceImages;
    private final List<CheckBox> keepCheckboxes;
    private final Random random;
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
        this.keepCheckboxes = new ArrayList<>();
        this.random = new Random();
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
            loadDiceImage(diceImage, 1); // Start with 1
            diceImages.add(diceImage);
            diceContainer.getChildren().add(diceImage);
        }
        
        // Create checkboxes
        HBox checkboxContainer = new HBox();
        checkboxContainer.setAlignment(Pos.CENTER);
        // Adjust spacing to align checkboxes under dice centers
        // Dice are 125px wide with 40px spacing, checkboxes are ~20px wide
        // 125 + 40 - 20 = 145
        checkboxContainer.setSpacing(150);
        checkboxContainer.setPrefWidth(GameConstants.DICE_CONTAINER_WIDTH);
        checkboxContainer.setMinHeight(GameConstants.CHECKBOX_CONTAINER_HEIGHT);
        
        for (int i = 0; i < GameConstants.DICE_COUNT; i++) {
            CheckBox checkbox = new CheckBox();
            checkbox.setStyle(themeController.getCheckboxStyle());
            keepCheckboxes.add(checkbox);
            checkboxContainer.getChildren().add(checkbox);
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
    
    /**
     * Loads a resource as InputStream with fallback options.
     */
    private InputStream loadResource(String path) {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) return stream;
        
        String pathWithoutSlash = path.startsWith("/") ? path.substring(1) : path;
        stream = getClass().getClassLoader().getResourceAsStream(pathWithoutSlash);
        if (stream != null) return stream;
        
        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathWithoutSlash);
        if (stream != null) return stream;
        
        throw new RuntimeException("Resource not found: " + path);
    }
    
    /**
     * Loads a dice image for a specific value.
     * 
     * @param imageView the ImageView to load the image into
     * @param value the dice value (1-6)
     */
    private void loadDiceImage(ImageView imageView, int value) {
        try {
            String imagePath = GameConstants.getDiceImagePath(value);
            // Try loading with getClass().getResource() first
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm());
                imageView.setImage(image);
            } else {
                // Fallback: try with InputStream
                InputStream imageStream = loadResource(imagePath);
                Image image = new Image(imageStream);
                imageView.setImage(image);
                imageStream.close();
            }
        } catch (Exception e) {
            System.err.println("Error loading dice image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Updates dice images based on current dice values.
     * 
     * @param values list of dice values to display
     */
    public void updateDiceImages(List<Integer> values) {
        for (int i = 0; i < Math.min(values.size(), diceImages.size()); i++) {
            loadDiceImage(diceImages.get(i), values.get(i));
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
                        loadDiceImage(diceImages.get(i), value);
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
    public List<CheckBox> getKeepCheckboxes() {
        return keepCheckboxes;
    }
    
    /**
     * Gets the keep status as an array.
     * 
     * @return array where 0 means roll, 1 means keep
     */
    public int[] getKeepStatus() {
        int[] kept = new int[GameConstants.DICE_COUNT];
        for (int i = 0; i < keepCheckboxes.size(); i++) {
            kept[i] = keepCheckboxes.get(i).isSelected() ? 1 : 0;
        }
        return kept;
    }
    
    /**
     * Resets all checkboxes to unchecked.
     */
    public void resetCheckboxes() {
        for (CheckBox checkbox : keepCheckboxes) {
            checkbox.setSelected(false);
        }
        refreshDiceEffects();
    }
    
    /**
     * Sets the disabled state of all checkboxes.
     * 
     * @param disabled true to disable checkboxes, false to enable them
     */
    public void setCheckboxesDisabled(boolean disabled) {
        for (CheckBox checkbox : keepCheckboxes) {
            checkbox.setDisable(disabled);
        }
        refreshDiceEffects();
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
        if (index < 0 || index >= keepCheckboxes.size()) {
            return;
        }
        if (keepCheckboxes.get(index).isDisable()) {
            return;
        }
        keepCheckboxes.get(index).setSelected(!keepCheckboxes.get(index).isSelected());
        refreshDiceEffects();
    }

    public void setKeepSelected(int index, boolean selected) {
        if (index < 0 || index >= keepCheckboxes.size()) {
            return;
        }
        keepCheckboxes.get(index).setSelected(selected);
        refreshDiceEffects();
    }

    public boolean isKeepSelected(int index) {
        if (index < 0 || index >= keepCheckboxes.size()) {
            return false;
        }
        return keepCheckboxes.get(index).isSelected();
    }

    public boolean areCheckboxesDisabled() {
        return keepCheckboxes.stream().allMatch(CheckBox::isDisable);
    }

    private void refreshDiceEffects() {
        for (int i = 0; i < diceImages.size(); i++) {
            ImageView imageView = diceImages.get(i);
            imageView.setEffect(null);

            DropShadow base = null;
            if (keepCheckboxes.get(i).isSelected()) {
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
}
