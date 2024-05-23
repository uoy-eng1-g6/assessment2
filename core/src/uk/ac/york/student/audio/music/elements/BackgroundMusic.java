package uk.ac.york.student.audio.music.elements;

import com.badlogic.gdx.audio.Music;
import lombok.Getter;
import uk.ac.york.student.audio.music.GameMusic;

/**
 * This class extends GameMusic and implements the Music interface.
 * It represents the background music for the game.
 */
@Getter
public class BackgroundMusic extends GameMusic implements Music {

    /**
     * Default constructor for the BackgroundMusic class.
     * It initialises the object with the default path to the background music file.
     */
    public BackgroundMusic() {
        super("audio/music/background.mp3");
    }

    // CHANGE remove unused overloaded constructor
}
