package projekt.yahtzee.controller.ui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Random;

/**
 * Manages all game sound effects and background music loaded from resources.
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
    private AudioClip spongebobBoowompSound;
    private AudioClip spongebobFailSound;
    private AudioClip tacoBellSound;
    private AudioClip undertakersBellSound;
    private AudioClip vineBoomSound;
    private AudioClip violinScreechSound;
    
    // Background music.
    private MediaPlayer menuMusic;
    private MediaPlayer gameMusic;
    
    /**
     * Creates the controller and loads every sound effect.
     */
    public SoundController() {
        laadiHelid();
    }
    
    /**
     * Loads all sound effects and background music clips.
     */
    private void laadiHelid() {
        // Dice roll sound.
        rollSound = laadiAudioClip("/projekt/yahtzee/sounds/rolling_dice.mp3", 0.5);
        
        // Button hover sound.
        buttonHoverSound = laadiAudioClip("/projekt/yahtzee/sounds/hover.mp3", 0.2);
        
        // Button click sound.
        buttonClickSound = laadiAudioClip("/projekt/yahtzee/sounds/click.mp3", 0.3);
        
        // Invalid click sound.
        invalidClickSound = laadiAudioClip("/projekt/yahtzee/sounds/ah-hell-nahhh.mp3", 0.5);
        
        // Pan hit sound.
        panHitSound = laadiAudioClip("/projekt/yahtzee/sounds/pan_hit.mp3", 0.4);
        
        // Bonk sound.
        bonkSound = laadiAudioClip("/projekt/yahtzee/sounds/bonk.mp3", 0.4);
        
        // Wow sound (game end).
        wowSound = laadiAudioClip("/projekt/yahtzee/sounds/anime-wow.mp3", 0.5);
        
        // Ding sound (score > 0).
        dingSound = laadiAudioClip("/projekt/yahtzee/sounds/ding.mp3", 0.4);
        
        // SpongeBob boowomp sound (score = 0).
        spongebobBoowompSound = laadiAudioClip("/projekt/yahtzee/sounds/spongebob-boowomp.mp3", 0.5);
        
        // SpongeBob fail sound (score = 0).
        spongebobFailSound = laadiAudioClip("/projekt/yahtzee/sounds/spongebob-fail.mp3", 0.5);
        
        // Taco Bell sound (game start).
        tacoBellSound = laadiAudioClip("/projekt/yahtzee/sounds/taco-bell.mp3", 0.5);
        
        // Undertaker's bell sound (game start).
        undertakersBellSound = laadiAudioClip("/projekt/yahtzee/sounds/undertakers-bell.mp3", 0.5);
        
        // Vine boom sound (non-interactive cells).
        vineBoomSound = laadiAudioClip("/projekt/yahtzee/sounds/vine-boom.mp3", 0.4);
        
        // Violin screech sound (already used cell).
        violinScreechSound = laadiAudioClip("/projekt/yahtzee/sounds/violin-screech-meme.mp3", 0.4);
        
        // Menu background music.
        menuMusic = laadiMediaPlayer("/projekt/yahtzee/sounds/menu_elevator_music.mp3");
        
        // In-game background music.
        gameMusic = laadiMediaPlayer("/projekt/yahtzee/sounds/game_music.mp3");
    }
    
    /**
     * Loads an {@link AudioClip} from the given resource path.
     *
     * @param path resource path of the audio file
     * @param volume volume in the range 0.0 to 1.0
     * @return loaded audio clip or {@code null} when loading fails
     */
    private AudioClip laadiAudioClip(String path, double volume) {
        try {
            InputStream stream = laadiRessurss(path);
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            
            File tempFile = File.createTempFile(baseName, extension);
            tempFile.deleteOnExit();
            Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            AudioClip clip = new AudioClip(tempFile.toURI().toString());
            clip.setVolume(volume);
            stream.close();
            
            return clip;
        } catch (Exception e) {
            System.out.println(path + " failed to load: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Loads a {@link MediaPlayer} from the given resource path.
     *
     * @param path resource path of the music file
     * @return configured media player or {@code null} when loading fails
     */
    private MediaPlayer laadiMediaPlayer(String path) {
        try {
            InputStream stream = laadiRessurss(path);
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            
            File tempFile = File.createTempFile(baseName, extension);
            tempFile.deleteOnExit();
            Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            Media media = new Media(tempFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            stream.close();
            
            return player;
        } catch (Exception e) {
            System.out.println(path + " failed to load: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Opens a resource stream using several fallback lookups.
     *
     * @param tee resource path
     * @return input stream for the resource
     * @throws RuntimeException when the resource cannot be found
     */
    private InputStream laadiRessurss(String tee) {
        InputStream stream = getClass().getResourceAsStream(tee);
        if (stream != null) return stream;
        
        String teeTaSlash = tee.startsWith("/") ? tee.substring(1) : tee;
        stream = getClass().getClassLoader().getResourceAsStream(teeTaSlash);
        if (stream != null) return stream;
        
        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(teeTaSlash);
        if (stream != null) return stream;
        
        try {
            stream = getClass().getModule().getResourceAsStream(teeTaSlash);
            if (stream != null) return stream;
        } catch (IOException e) {
            // Ignore missing module lookups.
        }
        
        String[] võimalikudTeed = {
            "src/main/resources/" + teeTaSlash,
            "YAHTZEE/src/main/resources/" + teeTaSlash,
            "../YAHTZEE/src/main/resources/" + teeTaSlash,
            "build/resources/main/" + teeTaSlash
        };
        
        for (String failTee : võimalikudTeed) {
            try {
                File fail = new File(failTee);
                if (fail.exists()) return new FileInputStream(fail);
            } catch (FileNotFoundException e) {
                // Keep searching.
            }
        }
        
        throw new RuntimeException("Resource not found: " + tee);
    }
    
    // Sound-effect getters.
    
    public AudioClip getRollSound() { return rollSound; }
    public AudioClip getButtonHoverSound() { return buttonHoverSound; }
    public AudioClip getButtonClickSound() { return buttonClickSound; }
    public AudioClip getInvalidClickSound() { return invalidClickSound; }
    public AudioClip getPanHitSound() { return panHitSound; }
    public AudioClip getBonkSound() { return bonkSound; }
    public AudioClip getWowSound() { return wowSound; }
    public AudioClip getDingSound() { return dingSound; }
    public AudioClip getSpongebobBoowompSound() { return spongebobBoowompSound; }
    public AudioClip getSpongebobFailSound() { return spongebobFailSound; }
    public AudioClip getTacoBellSound() { return tacoBellSound; }
    public AudioClip getUndertakersBellSound() { return undertakersBellSound; }
    public AudioClip getVineBoomSound() { return vineBoomSound; }
    public AudioClip getViolinScreechSound() { return violinScreechSound; }
    
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
        Random random = new Random();
        if (random.nextBoolean()) {
            if (tacoBellSound != null) {
                tacoBellSound.stop();
                tacoBellSound.play();
            }
        } else {
            if (undertakersBellSound != null) {
                undertakersBellSound.stop();
                undertakersBellSound.play();
            }
        }
    }
    
    /**
     * Plays either the pan hit or bonk sound at random.
     */
    public void playRandomPlayerNameClickSound() {
        Random random = new Random();
        if (random.nextBoolean()) {
            if (panHitSound != null) {
                panHitSound.stop();
                panHitSound.play();
            }
        } else {
            if (bonkSound != null) {
                bonkSound.stop();
                bonkSound.play();
            }
        }
    }
    
    /**
     * Plays either the SpongeBob boowomp or fail sound at random.
     */
    public void playRandomZeroScoreSound() {
        Random random = new Random();
        if (random.nextBoolean()) {
            if (spongebobBoowompSound != null) {
                spongebobBoowompSound.stop();
                spongebobBoowompSound.play();
            }
        } else {
            if (spongebobFailSound != null) {
                spongebobFailSound.stop();
                spongebobFailSound.play();
            }
        }
    }
}
