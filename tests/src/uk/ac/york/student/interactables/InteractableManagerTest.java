package uk.ac.york.student.interactables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.york.student.GdxTestRunner;
import uk.ac.york.student.assets.map.MapManager;

@RunWith(GdxTestRunner.class)
public class InteractableManagerTest {
    @Test
    public void setupInteractablesLoadsObjectsCorrectly() {
        var interactionObjectProperties = new MapProperties();
        interactionObjectProperties.put("isActivity", true);
        interactionObjectProperties.put("interactInfo", "1,2,3,4");
        interactionObjectProperties.put("activityStr", "activity");
        var interactionObject = mock(MapObject.class);
        when(interactionObject.getProperties()).thenReturn(interactionObjectProperties);

        var mapChangeObjectProperties = new MapProperties();
        mapChangeObjectProperties.put("interactInfo", "5,6,7,8");
        mapChangeObjectProperties.put("newMapStr", "newMap");
        var mapChangeObject = mock(MapObject.class);
        when(mapChangeObject.getProperties()).thenReturn(mapChangeObjectProperties);

        var mapObjects = new MapObjects();
        mapObjects.add(interactionObject);
        mapObjects.add(mapChangeObject);

        var objectLayer = mock(MapLayer.class);
        when(objectLayer.getObjects()).thenReturn(mapObjects);
        var mapLayers = mock(MapLayers.class);
        when(mapLayers.get("gameObjects")).thenReturn(objectLayer);
        var map = mock(TiledMap.class);
        when(map.getLayers()).thenReturn(mapLayers);

        var region = mock(TextureAtlas.AtlasRegion.class);
        var atlas = mock(TextureAtlas.class);
        when(atlas.findRegion(anyString())).thenReturn(region);

        var camera = mock(OrthographicCamera.class);

        var interactableManager = new InteractableManager(camera, atlas);
        try (var mapManager = mockStatic(MapManager.class);
                var ignored = mockConstruction(Interactable.class);
                var ignored2 = mockConstruction(AnimatedInteractable.class)) {
            mapManager.when(() -> MapManager.getMap(anyString())).thenReturn(map);

            interactableManager.setupInteractables("foo");

            assertThat(interactableManager.getInteractableMap().size()).isEqualTo(2);

            var activityInteractable = interactableManager.getInteractableMap().get("activity");
            assertThat(activityInteractable).isNotNull();

            var mapChangeInteractable = interactableManager.getInteractableMap().get("newMap");
            assertThat(mapChangeInteractable).isNotNull();
        }
    }
}
