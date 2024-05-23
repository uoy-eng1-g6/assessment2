package uk.ac.york.student.assets.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import java.util.HashMap;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * This utility class manages the loading and storage of TiledMap objects.
 * It uses a MapOfSuppliers to store the maps, allowing for lazy loading.
 */
// CHANGE refactor class to use simple hashmap cache with better object disposal to fix many issues
@UtilityClass
public final class MapManager {
    public static final List<String> MAP_NAMES =
            List.of("map", "inside_house", "inside_pub", "inside_library", "blankMap");

    static TmxMapLoader MAP_LOADER;
    static TmxMapLoader.Parameters MAP_LOADER_PARAMETERS;

    static final HashMap<String, TiledMap> cachedMaps = new HashMap<>();
    static final HashMap<String, MapObjectsPositionData> cachedMapObjectData = new HashMap<>();

    static {
        MAP_LOADER = new TmxMapLoader();
        MAP_LOADER_PARAMETERS = new TmxMapLoader.Parameters();
        MAP_LOADER_PARAMETERS.textureMinFilter = Texture.TextureFilter.Nearest;
        MAP_LOADER_PARAMETERS.textureMagFilter = Texture.TextureFilter.Nearest;
    }

    static String getMapPath(String mapName) {
        return "map/" + mapName + ".tmx";
    }

    public static TiledMap getMap(String mapName) {
        if (cachedMaps.containsKey(mapName)) {
            return cachedMaps.get(mapName);
        }

        var map = MAP_LOADER.load(getMapPath(mapName), MAP_LOADER_PARAMETERS);
        cachedMaps.put(mapName, map);
        return map;
    }

    // CHANGE new method
    static void loadCollisionObjectsFromMapLayer(
            MapObjectsPositionData data, int tileWidth, int tileHeight, MapLayer mapLayer) {
        for (var object : mapLayer.getObjects()) {
            float x, y;
            float[] vertices;
            if (object instanceof RectangleMapObject) {
                var rectangleObject = (RectangleMapObject) object;
                var rectangle = rectangleObject.getRectangle();

                x = rectangle.getX();
                y = rectangle.getY();
                var width = rectangle.getWidth();
                var height = rectangle.getHeight();

                vertices = new float[] {x, y, x, y + height, x + width, y + height, x + width, y};

                x = x / tileWidth;
                y = y / tileHeight;
            } else if (object instanceof PolygonMapObject) {
                var polygonObject = (PolygonMapObject) object;
                var polygon = polygonObject.getPolygon();

                x = polygon.getX() / tileWidth;
                y = polygon.getY() / tileHeight;
                vertices = polygon.getTransformedVertices();
            } else {
                // Can there be another type of map object?
                System.out.printf(
                        "Unrecognised collision object: %s", object.getClass().getSimpleName());
                continue;
            }

            // Normalize the vertices to tile scale instead of pixel scale
            // - Box2D prefers smaller worlds which allows for smaller velocities
            for (var i = 0; i < vertices.length; i += 2) {
                vertices[i] /= tileWidth;
                vertices[i] -= x;
                vertices[i + 1] /= tileHeight;
                vertices[i + 1] -= y;
            }

            data.getCollisionObjects().add(new MapObjectsPositionData.CollisionObjectData(x, y, vertices));
        }
    }

    // CHANGE new method
    static void loadActionableObjectDataFromMapLayer(
            MapObjectsPositionData data, int tileWidth, int tileHeight, MapLayer mapLayer) {
        for (var object : mapLayer.getObjects()) {
            Boolean actionable = object.getProperties().get("actionable", Boolean.class);
            if (Boolean.FALSE.equals(actionable)) continue;

            var rectangleObject = (RectangleMapObject) object;
            var rectangle = rectangleObject.getRectangle();

            data.getActionableObjects()
                    .add(new MapObjectsPositionData.ActionableObjectData(
                            rectangle.x / tileWidth,
                            rectangle.y / tileHeight,
                            rectangle.width / tileWidth,
                            rectangle.height / tileHeight,
                            object));
        }
    }

    // CHANGE new method
    public static MapObjectsPositionData getMapObjectData(String mapName) {
        if (cachedMapObjectData.containsKey(mapName)) {
            return cachedMapObjectData.get(mapName);
        }

        var map = getMap(mapName);
        var backgroundLayer = (TiledMapTileLayer) map.getLayers().get(0);
        var tileWidth = backgroundLayer.getTileWidth();
        var tileHeight = backgroundLayer.getTileHeight();

        var data = new MapObjectsPositionData();

        var collisionLayer = map.getLayers().get("collisions");
        if (collisionLayer != null) {
            loadCollisionObjectsFromMapLayer(data, tileWidth, tileHeight, collisionLayer);
        }
        var actionableObjectLayer = map.getLayers().get("gameObjects");
        if (actionableObjectLayer != null) {
            loadActionableObjectDataFromMapLayer(data, tileWidth, tileHeight, actionableObjectLayer);
        }

        cachedMapObjectData.put(mapName, data);
        return data;
    }

    public static void dispose() {
        for (var map : cachedMaps.values()) {
            map.dispose();
        }
        cachedMaps.clear();
    }
}
