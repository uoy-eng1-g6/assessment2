package uk.ac.york.student.assets.skins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import lombok.Getter;

/**
 * This enum represents the different types of skins that can be used in the game.
 */
public enum Skins {
    CRAFTACULAR(Gdx.files.internal("skins/craftacular/skin/craftacular-ui.json"));

    @Getter
    private final FileHandle handle;

    Skins(final FileHandle handle) {
        this.handle = handle;
    }
}
