package projekt.yahtzee.controller.ui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Random;
import projekt.yahtzee.util.ResourceLoader;

/**
 * Manages all game sound effects and background music loaded from resources.
 *
 * @author sandersirge
 * @version 1.1.0
 */
public class SoundController {
    // Sound effects.
    private AudioClip rollSound;
    private AudioClip buttonHoverSound;
    private AudioClip buttonClickSound;
    private AudioClip invalidClickSound;
    private AudioClip panHitSound;
    private AudioClip bonkSound;
    private AudioClip wowSound;
    private AudioClip dingSound;
    private AudioClip answerCorrectSound;
    private AudioClip spongebobBoowompSound;
    private AudioClip spongebobFailSound;
    private AudioClip buzzerErrorSound;
    private AudioClip tacoBellSound;
    private AudioClip undertakersBellSound;
    private AudioClip vineBoomSound;
    private AudioClip violinScreechSound;
    private AudioClip miBomboclatSound;
    
    // Background music.
    private MediaPlayer menuMusic;
    private MediaPlayer gameMusic;
    private final Random random = new Random();
    
    /**
     * Creates the controller and loads every sound effect.
     */
    public SoundController() {
        loadSounds();
    }
    
    /**
     * Loads all sound effects and background music clips.
     */
    private void loadSounds() {
        // Dice roll sound.
        rollSound = loadAudioClip("/projekt/yahtzee/sounds/rolling_dice.mp3", 0.5);
        
        // Button hover sound.
        buttonHoverSound = loadAudioClip("/projekt/yahtzee/sounds/hover.mp3", 0.2);
        
        // Button click sound.
        buttonClickSound = loadAudioClip("/projekt/yahtzee/sounds/click.mp3", 0.3);
        
        // Invalid click sound.
        invalidClickSound = loadAudioClip("/projekt/yahtzee/sounds/ah-hell-nahhh.mp3", 0.5);
        
        // Pan hit sound.
        panHitSound = loadAudioClip("/projekt/yahtzee/sounds/pan_hit.mp3", 0.4);
        
        // Bonk sound.
        bonkSound = loadAudioClip("/projekt/yahtzee/sounds/bonk.mp3", 0.4);
        
        // Wow sound (game end).
        wowSound = loadAudioClip("/projekt/yahtzee/sounds/anime-wow.mp3", 0.5);
        
        // Ding sound (score > 0).
        dingSound = loadAudioClip("/projekt/yahtzee/sounds/ding.mp3", 0.4);
        answerCorrectSound = loadAudioClip("/projekt/yahtzee/sounds/answer-correct.mp3", 0.4);
        
        // SpongeBob boowomp sound (score = 0).
        spongebobBoowompSound = loadAudioClip("/projekt/yahtzee/sounds/spongebob-boowomp.mp3", 0.5);
        
        // SpongeBob fail sound (score = 0).
        spongebobFailSound = loadAudioClip("/projekt/yahtzee/sounds/spongebob-fail.mp3", 0.5);
        buzzerErrorSound = loadAudioClip("/projekt/yahtzee/sounds/buzzer-error.mp3", 0.5);
        
        // Taco Bell sound (game start).
        tacoBellSound = loadAudioClip("/projekt/yahtzee/sounds/taco-bell.mp3", 0.5);
        
        // Undertaker's bell sound (game start).
        undertakersBellSound = loadAudioClip("/projekt/yahtzee/sounds/undertakers-bell.mp3", 0.5);
        
        // Vine boom sound (non-interactive cells).
        vineBoomSound = loadAudioClip("/projekt/yahtzee/sounds/vine-boom.mp3", 0.4);
        
        // Violin screech sound (already used cell).
        violinScreechSound = loadAudioClip("/projekt/yahtzee/sounds/violin-screech-meme.mp3", 0.4);

        // Yahtzee celebration sound.
        miBomboclatSound = loadAudioClip("/projekt/yahtzee/sounds/mi-bomboclat.mp3", 0.5);
        
        // Menu background music.
        menuMusic = loadMediaPlayer("/projekt/yahtzee/sounds/menu_elevator_music.mp3");
        
        // In-game background music.
        gameMusic = loadMediaPlayer("/projekt/yahtzee/sounds/game_music.mp3");
    }
    
    /**
     * Loads an {@link AudioClip} from the given resource path.
     *
     * @param path resource path of the audio file
     * @param volume volume in the range 0.0 to 1.0
     * @return loaded audio clip or {@code null} when loading fails
     */
    private AudioClip loadAudioClip(String path, double volume) {
        try {
            File tempFile = ResourceLoader.copyResourceToCachedTempFile(path);
            AudioClip clip = new AudioClip(tempFile.toURI().toString());
            clip.setVolume(volume);
            return clip;
        } catch (Exception e) {
            logResourceLoadFailure(path, e);
            return null;
        }
    }
    
    /**
     * Loads a {@link MediaPlayer} from the given resource path.
     *
     * @param path resource path of the music file
     * @return configured media player or {@code null} when loading fails
     */
    private MediaPlayer loadMediaPlayer(String path) {
        try {
            File tempFile = ResourceLoader.copyResourceToCachedTempFile(path);
            Media media = new Media(tempFile.toURI().toString());
            return new MediaPlayer(media);
        } catch (Exception e) {
            logResourceLoadFailure(path, e);
            return null;
        }
    }

    /**
     * Logs a warning when an audio resource fails to load.
     *
     * @param path      resource path that failed
     * @param exception the exception that occurred
     */
    private void logResourceLoadFailure(String path, Exception exception) {
        System.err.println("Failed to load audio resource " + path + ": " + exception.getMessage());
        exception.printStackTrace(System.err);
    }

    /**
     * Plays the provided audio clip from the start when it is available.
    /** @return the dice rolling sound clip */
    public AudioClip getRollSound() { return rollSound; }
    /** @return the button hover sound clip */
    public AudioClip getButtonHoverSound() { return buttonHoverSound; }
    /** @return the button click sound clip */
    public AudioClip getButtonClickSound() { return buttonClickSound; }
    /** @return the invalid click sound clip */
    public AudioClip getInvalidClickSound() { return invalidClickSound; }
    /** @return the wow celebration sound clip */
    public AudioClip getWowSound() { return wowSound; }
    /** @return the vine boom non-interactive cell sound clip */
    public AudioClip getVineBoomSound() { return vineBoomSound; }
    /** @return the violin screech used-cell sound clip */
    public AudioClip getViolinScreechSound() { return violinScreechSound; }

    /**
     * Plays the provided audio clip from the start when it is available.
     *
     * @param clip clip to play
     */
    public void playClip(AudioClip clip) {
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.play();
    }

    /**
     * Starts the menu background music in an infinite loop.
     */
    public void startMenuMusic() {
        if (menuMusic != null) {
            menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
            menuMusic.setVolume(0.3);
            menuMusic.play();
        }
    }
    
    /**
     * Stops the menu background music.
     */
    public void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
        }
    }
    
    /**
     * Starts the in-game background music in an infinite loop.
     */
    public void startGameMusic() {
        if (gameMusic != null) {
            gameMusic.setCycleCount(MediaPlayer.INDEFINITE);
            gameMusic.setVolume(0.15);
            gameMusic.play();
        }
    }
    
    /**
     * Stops the in-game background music.
     */
    public void stopGameMusic() {
        if (gameMusic != null) {
            gameMusic.stop();
        }
    }
    
    /**
     * Switches between menu and in-game music.
     *
     * @param toMenu {@code true} to play menu music, {@code false} for in-game music
     */
    public void switchMusic(boolean toMenu) {
        if (toMenu) {
            stopGameMusic();
            startMenuMusic();
        } else {
            stopMenuMusic();
            startGameMusic();
        }
    }
    
    /**
     * Plays either the Taco Bell or Undertaker's bell sound at random.
     */
    public void playRandomGameStartSound() {
        if (random.nextBoolean()) {
            playClip(tacoBellSound);
        } else {
            playClip(undertakersBellSound);
        }
    }
    
    /**
     * Plays either the pan hit or bonk sound at random.
     */
    public void playRandomPlayerNameClickSound() {
        if (random.nextBoolean()) {
            playClip(panHitSound);
        } else {
            playClip(bonkSound);
        }
    }
    
    /**
     * Plays one of the zero-score failure sounds at random.
     */
    public void playRandomZeroScoreSound() {
        int choice = random.nextInt(3);
        switch (choice) {
            case 0 -> playClip(spongebobBoowompSound);
            case 1 -> playClip(spongebobFailSound);
            default -> playClip(buzzerErrorSound);
        }
    }

    /**
     * Plays either the ding or answer correct sound when a positive score is recorded.
     */
    public void playRandomPositiveScoreSound() {
        if (random.nextBoolean()) {
            playClip(dingSound);
        } else {
            playClip(answerCorrectSound);
        }
    }

    /**
     * Plays the celebratory sound used for Yahtzee scores.
     */
    public void playYahtzeeCelebration() {
        playClip(miBomboclatSound);
    }
}
