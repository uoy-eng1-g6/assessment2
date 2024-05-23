package uk.ac.york.student.assets.map;

import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.york.student.GdxTestRunner;

import java.io.File;

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
}
