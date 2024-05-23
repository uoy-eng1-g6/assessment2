package uk.ac.york.student.assets.skins;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import java.util.HashMap;
import lombok.experimental.UtilityClass;

/**
 * This utility class manages the loading and storage of Skin objects.
 * It uses a HashMap to store the cached skins, allowing for lazy loading.
 */
@UtilityClass
public class SkinManager {
    // CHANGE refactor class to fix issues
    private static final HashMap<Skins, Skin> cachedSkins = new HashMap<>();

    public static Skin getSkin(Skins skin) {
        if (cachedSkins.containsKey(skin)) {
            return cachedSkins.get(skin);
        }

        cachedSkins.put(skin, new Skin(skin.getHandle()));
        return getSkin(skin);
    }

    public static void dispose() {
        for (Skin skin : cachedSkins.values()) {
            skin.dispose();
        }
        cachedSkins.clear();
    }
}
