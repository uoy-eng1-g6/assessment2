package uk.ac.york.student.assets.map;

import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.york.student.GdxTestRunner;

import java.io.File;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

@RunWith(GdxTestRunner.class)
public class MapManagerTest {
    @Test
    public void getMapReturnsSameObjectIfCalledTwice() {
        var oldLoader = MapManager.MAP_LOADER;

        try (var mapManager = mockStatic(MapManager.class)) {
            mapManager.when(() -> MapManager.getMapPath(anyString()))
                    .then(call -> new File("../assets/map/" + call.getArguments()[0] + ".tmx").getAbsolutePath());
            mapManager.when(() -> MapManager.getMap(anyString())).thenCallRealMethod();

            MapManager.MAP_LOADER = new TmxMapLoader(new AbsoluteFileHandleResolver());

            var map = MapManager.getMap("blankMap");
            var map2 = MapManager.getMap("blankMap");

            assertThat(map).isSameAs(map2);
        } finally {
            MapManager.MAP_LOADER = oldLoader;
            MapManager.dispose();
        }
    }

    @Test
    public void getMapObjectDataReturnsSameObjectIfCalledTwice() {
        var oldLoader = MapManager.MAP_LOADER;

        try (var mapManager = mockStatic(MapManager.class)) {
            mapManager.when(() -> MapManager.getMapPath(anyString()))
                    .then(call -> new File("../assets/map/" + call.getArguments()[0] + ".tmx").getAbsolutePath());

            mapManager.when(() -> MapManager.getMap(anyString())).thenCallRealMethod();
            mapManager.when(() -> MapManager.getMapObjectData(anyString())).thenCallRealMethod();

            MapManager.MAP_LOADER = new TmxMapLoader(new AbsoluteFileHandleResolver());

            var mapObjectData = MapManager.getMapObjectData("blankMap");
            var mapObjectData2 = MapManager.getMapObjectData("blankMap");

            assertThat(mapObjectData).isSameAs(mapObjectData2);
        } finally {
            MapManager.MAP_LOADER = oldLoader;
            MapManager.dispose();
        }
    }

    @Test
    public void getMapObjectDataReturnsCorrectActionableObject() {
        var oldLoader = MapManager.MAP_LOADER;

        try (var mapManager = mockStatic(MapManager.class)) {
            mapManager.when(() -> MapManager.getMapPath(anyString()))
                    .then(call -> new File("../assets/map/" + call.getArguments()[0] + ".tmx").getAbsolutePath());

            mapManager.when(() -> MapManager.getMap(anyString())).thenCallRealMethod();
            mapManager.when(() -> MapManager.getMapObjectData(anyString())).thenCallRealMethod();
            mapManager.when(() -> MapManager.loadActionableObjectDataFromMapLayer(any(MapObjectsPositionData.class), anyInt(), anyInt(), any(MapLayer.class))).thenCallRealMethod();

            MapManager.MAP_LOADER = new TmxMapLoader(new AbsoluteFileHandleResolver());

            var mapObjectData = MapManager.getMapObjectData("test_maps/single_actionable_object");
            assertThat(mapObjectData.getActionableObjects()).hasSize(1);

            var rectangle = mapObjectData.getActionableObjects().get(0).asRectangle();
            assertThat(rectangle.getX()).isEqualTo(1);
            assertThat(rectangle.getY()).isEqualTo(1);
            assertThat(rectangle.getWidth()).isEqualTo(3);
            assertThat(rectangle.getHeight()).isEqualTo(3);
        } finally {
            MapManager.MAP_LOADER = oldLoader;
            MapManager.dispose();
        }
    }

    @Test
    public void getMapObjectDataReturnsCorrectCollidablesFromRectangle() {
        var oldLoader = MapManager.MAP_LOADER;

        try (var mapManager = mockStatic(MapManager.class)) {
            mapManager.when(() -> MapManager.getMapPath(anyString()))
                    .then(call -> new File("../assets/map/" + call.getArguments()[0] + ".tmx").getAbsolutePath());

            mapManager.when(() -> MapManager.getMap(anyString())).thenCallRealMethod();
            mapManager.when(() -> MapManager.getMapObjectData(anyString())).thenCallRealMethod();
            mapManager.when(() -> MapManager.loadCollisionObjectsFromMapLayer(any(MapObjectsPositionData.class), anyInt(), anyInt(), any(MapLayer.class))).thenCallRealMethod();

            MapManager.MAP_LOADER = new TmxMapLoader(new AbsoluteFileHandleResolver());

            var mapObjectData = MapManager.getMapObjectData("test_maps/single_rectangle_collidable_object");
            assertThat(mapObjectData.getCollisionObjects()).hasSize(1);

            var object = mapObjectData.getCollisionObjects().get(0);
            assertThat(object.getX()).isEqualTo(1);
            assertThat(object.getY()).isEqualTo(1);
            assertThat(object.getVertices()).containsExactlyInAnyOrder(0, 0, 0, 3, 3, 3, 3, 0);
        } finally {
            MapManager.MAP_LOADER = oldLoader;
            MapManager.dispose();
        }
    }

    @Test
    public void getMapObjectDataReturnsCorrectCollidablesFromPolygon() {
        var oldLoader = MapManager.MAP_LOADER;

        try (var mapManager = mockStatic(MapManager.class)) {
            mapManager.when(() -> MapManager.getMapPath(anyString()))
                    .then(call -> new File("../assets/map/" + call.getArguments()[0] + ".tmx").getAbsolutePath());

            mapManager.when(() -> MapManager.getMap(anyString())).thenCallRealMethod();
            mapManager.when(() -> MapManager.getMapObjectData(anyString())).thenCallRealMethod();
            mapManager.when(() -> MapManager.loadCollisionObjectsFromMapLayer(any(MapObjectsPositionData.class), anyInt(), anyInt(), any(MapLayer.class))).thenCallRealMethod();

            MapManager.MAP_LOADER = new TmxMapLoader(new AbsoluteFileHandleResolver());

            var mapObjectData = MapManager.getMapObjectData("test_maps/single_polygon_collidable_object");
            assertThat(mapObjectData.getCollisionObjects()).hasSize(1);

            var object = mapObjectData.getCollisionObjects().get(0);
            assertThat(object.getX()).isEqualTo(1);
            assertThat(object.getY()).isEqualTo(1);
            assertThat(object.getVertices()).containsExactlyInAnyOrder(0, 0, 0, 2, 1, 3, 2, 3, 3, 2, 3, 0);
        } finally {
            MapManager.MAP_LOADER = oldLoader;
            MapManager.dispose();
        }
    }
}
