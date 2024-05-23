package uk.ac.york.student.assets.skins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * This enum represents the different types of skins that can be used in the game.
 */
// CHANGE extend so enum stores skin name instead of hardcoding directly in SkinManager
public enum Skins {
    CRAFTACULAR(() -> Gdx.files.internal("skins/craftacular/skin/craftacular-ui.json"));

    @Getter
    private final Supplier<FileHandle> handleSupplier;

    Skins(final Supplier<FileHandle> handleSupplier) {
        this.handleSupplier = handleSupplier;
    }
}
