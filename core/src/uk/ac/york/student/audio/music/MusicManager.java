package uk.ac.york.student.audio.music;

import lombok.Getter;
import uk.ac.york.student.audio.AudioManager;
import uk.ac.york.student.audio.music.elements.BackgroundMusic;
import uk.ac.york.student.settings.GamePreferences;
import uk.ac.york.student.settings.MusicPreferences;

/**
 * Singleton class that manages the music for the game.
 * It implements the AudioManager interface and controls the background music.
 */
public class MusicManager implements AudioManager {
    /**
     * The instance of the music manager.
     * It is a static final instance of MusicManager.
     */
    @Getter
    private static final MusicManager instance = new MusicManager();

    private BackgroundMusic backgroundMusic = null;

    /**
     * Private constructor to prevent instantiation.
     * As this is a singleton class, the constructor is private.
     */
    private MusicManager() {}

    public void setBackgroundMusicVolume(float volume) {
        backgroundMusic.setVolume(volume);
    }

    /**
     * Called when the game is started.
     * It checks the music preferences and if enabled, starts playing the background music.
     */
    @Override
    public void onEnable() {
        if (backgroundMusic == null) {
            backgroundMusic = new BackgroundMusic();
        }

        MusicPreferences musicPreferences = (MusicPreferences) GamePreferences.MUSIC.getPreference();
        if (musicPreferences.isEnabled()) {
            backgroundMusic.setLooping(true);
            backgroundMusic.play();
            backgroundMusic.setVolume(musicPreferences.getVolume());
        }
    }

    /**
     * Called when the game is stopped.
     * It stops and disposes the background music.
     */
    @Override
    public void onDisable() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }
}
