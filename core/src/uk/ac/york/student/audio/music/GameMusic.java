package uk.ac.york.student.audio.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import lombok.Getter;

/**
 * Base class for game music
 */
@Getter
public abstract class GameMusic implements Music {
    protected final Music music;

    /**
     * Creates a new GameMusic with the given path
     * @param path The path to the music file (in assets folder)
     */
    protected GameMusic(final String path)  {
        music = Gdx.audio.newMusic(Gdx.files.internal(path));
    }


    /**
     * Plays the music
     */
    @Override
    public void play() {
        music.play();
    }

    /**
     * Pauses the music
     */
    @Override
    public void pause() {
        music.pause();
    }

    /**
     * Stops the music
     */
    @Override
    public void stop() {
        music.stop();
    }

    /**
     * Is the music currently playing
     * @return True if the music is playing, false otherwise
     */
    @Override
    public boolean isPlaying() {
        return music.isPlaying();
    }

    /**
     * Set whether to loop the stream
     * @param isLooping whether to loop the stream
     */
    @Override
    public void setLooping(final boolean isLooping) {
        music.setLooping(isLooping);
    }

    /**
     * Is the music currently looping
     * @return True if the music is looping, false otherwise
     */
    @Override
    public boolean isLooping() {
        return music.isLooping();
    }

    /**
     * Set the volume
     * @param volume The volume in range [0.0, 1.0]
     */
    @Override
    public void setVolume(final float volume) {
        music.setVolume(volume);
    }

    /**
     * Get the volume
     * @return The volume in range [0.0, 1.0]
     */
    @Override
    public float getVolume() {
        return music.getVolume();
    }

    /**
     * Set the panning and volume of the music
     * @param pan panning in the range -1 (full left) to 1 (full right). 0 is center position.
     * @param volume the volume in the range [0,1].
     */
    @Override
    public void setPan(final float pan, final float volume) {
        music.setPan(pan, volume);
    }

    /**
     * Set the playback position in seconds
     * @param position the position in seconds
     */
    @Override
    public void setPosition(final float position) {
        music.setPosition(position);
    }

    /**
     * Get the playback position in seconds
     * @return the position in seconds
     */
    @Override
    public float getPosition() {
        return music.getPosition();
    }

    /**
     * Dispose of the music
     * Needs to be called when the Music is no longer needed.
     */
    @Override
    public void dispose() {
        music.dispose();
    }

    /**
     * Register a callback to be invoked when the end of a music stream has been reached during playback
     * @param listener the callback that will be run.
     */
    @Override
    public void setOnCompletionListener(final OnCompletionListener listener) {
        music.setOnCompletionListener(listener);
    }
}