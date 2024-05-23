package uk.ac.york.student.audio.sound;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import lombok.Getter;
import uk.ac.york.student.audio.AudioManager;

/**
 * Singleton class that manages the sound for the game.
 * It implements the AudioManager interface and controls the game sounds.
 */
// CHANGE refactor class with cached hash map to fix issues
public class SoundManager implements AudioManager {
    /**
     * The instance of the sound manager.
     * It is a static final instance of SoundManager.
     */
    @Getter
    private static final SoundManager instance = new SoundManager();

    private final HashMap<Sounds, GameSound> soundCache = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     * As this is a singleton class, the constructor is private.
     */
    private SoundManager() {}

    public GameSound getSound(Sounds sound) {
        if (soundCache.containsKey(sound)) {
            return soundCache.get(sound);
        }

        try {
            soundCache.put(sound, sound.getSoundClass().getDeclaredConstructor().newInstance());
        } catch (InstantiationException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return getSound(sound);
    }

    /**
     * Called when the game is started.
     * This method is part of the AudioManager interface.
     */
    @Override
    public void onEnable() {}

    /**
     * Called when the game is stopped.
     * This method is part of the AudioManager interface.
     * It disposes all the game sounds.
     */
    @Override
    public void onDisable() {
        for (var sound : soundCache.values()) {
            sound.dispose();
        }
    }
}
