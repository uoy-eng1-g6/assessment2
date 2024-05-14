package uk.ac.york.student.assets.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import java.util.HashMap;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * This utility class manages the loading and storage of TiledMap objects.
 * It uses a MapOfSuppliers to store the maps, allowing for lazy loading.
 */
@UtilityClass
public final class MapManager {
    public static final List<String> MAP_NAMES =
            List.of("map", "inside_house", "inside_pub", "inside_library", "blankMap");

    private static final TmxMapLoader MAP_LOADER;
    private static final TmxMapLoader.Parameters MAP_LOADER_PARAMETERS;

    private static final HashMap<String, TiledMap> cachedMaps = new HashMap<>();

    static {
        MAP_LOADER = new TmxMapLoader();
        MAP_LOADER_PARAMETERS = new TmxMapLoader.Parameters();
        MAP_LOADER_PARAMETERS.textureMinFilter = Texture.TextureFilter.Nearest;
        MAP_LOADER_PARAMETERS.textureMagFilter = Texture.TextureFilter.Nearest;
    }

    public static TiledMap getMap(String mapName) {
        if (cachedMaps.containsKey(mapName)) {
            return cachedMaps.get(mapName);
        }

        var map = MAP_LOADER.load("map/" + mapName + ".tmx", MAP_LOADER_PARAMETERS);
        cachedMaps.put(mapName, map);
        return map;
    }

    public static void dispose() {
        for (var map : cachedMaps.values()) {
            map.dispose();
        }
        cachedMaps.clear();
    }
}
