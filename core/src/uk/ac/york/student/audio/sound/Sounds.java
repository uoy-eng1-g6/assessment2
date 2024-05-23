package uk.ac.york.student.audio.sound;

import lombok.Getter;
import uk.ac.york.student.audio.sound.elements.ButtonClickSound;

/**
 * Enum representing the different types of sounds in the game.
 */
// CHANGE extend so enum stores sound instead of hardcoding directly in SoundManager
public enum Sounds {
    BUTTON_CLICK(ButtonClickSound.class);

    @Getter
    private final Class<? extends GameSound> soundClass;

    Sounds(final Class<? extends GameSound> soundClass) {
        this.soundClass = soundClass;
    }
}
