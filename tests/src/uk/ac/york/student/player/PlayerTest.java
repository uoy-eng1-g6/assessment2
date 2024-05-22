package uk.ac.york.student.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import uk.ac.york.student.GdxTestRunner;
import uk.ac.york.student.assets.map.MapManager;
import uk.ac.york.student.assets.map.MapObjectsPositionData;
import uk.ac.york.student.assets.skins.SkinManager;
import uk.ac.york.student.assets.skins.Skins;
import uk.ac.york.student.utils.Pair;

@RunWith(GdxTestRunner.class)
public class PlayerTest {
    @Test
    public void setMapThrowsExceptionWhenStartPositionNotFound() {
        var backgroundLayer = mock(TiledMapTileLayer.class);
        when(backgroundLayer.getTileWidth()).thenReturn(4);
        when(backgroundLayer.getTileHeight()).thenReturn(4);

        var gameObjectsLayer = mock(MapLayer.class);
        when(gameObjectsLayer.getName()).thenReturn("gameObjects");
        var gameObjects = new MapObjects();
        when(gameObjectsLayer.getObjects()).thenReturn(gameObjects);

        var mapLayers = new MapLayers();
        mapLayers.add(backgroundLayer);
        mapLayers.add(gameObjectsLayer);
        var map = mock(TiledMap.class);
        when(map.getLayers()).thenReturn(mapLayers);

        var world = mock(World.class);

        var textureRegion = mock(TextureAtlas.AtlasRegion.class);
        var sprite = mock(Sprite.class);
        var textureAtlas = mock(TextureAtlas.class);
        when(textureAtlas.findRegion(anyString())).thenReturn(textureRegion);
        when(textureAtlas.createSprite(anyString())).thenReturn(sprite);

        var skin = mock(Skin.class);
        try (var skinManager = mockStatic(SkinManager.class);
                var ignored = mockConstruction(ProgressBar.class)) {
            skinManager.when(() -> SkinManager.getSkin(any(Skins.class))).thenReturn(skin);

            var player = new Player(world, textureAtlas);
            assertThatThrownBy(() -> player.setMap("_", map)).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    public void setMapCreatesPlayerAtCorrectPosition() {
        var backgroundLayer = mock(TiledMapTileLayer.class);
        when(backgroundLayer.getTileWidth()).thenReturn(4);
        when(backgroundLayer.getTileHeight()).thenReturn(4);

        var gameObjectsLayer = mock(MapLayer.class);
        when(gameObjectsLayer.getName()).thenReturn("gameObjects");
        var gameObjects = new MapObjects();
        when(gameObjectsLayer.getObjects()).thenReturn(gameObjects);

        var startPosition = mock(RectangleMapObject.class);
        when(startPosition.getRectangle()).thenReturn(new Rectangle(0, 0, 4, 4));
        var startPositionProperties = new MapProperties();
        startPositionProperties.put("isSpawnPoint", true);
        when(startPosition.getProperties()).thenReturn(startPositionProperties);
        gameObjects.add(startPosition);

        var mapLayers = new MapLayers();
        mapLayers.add(backgroundLayer);
        mapLayers.add(gameObjectsLayer);
        var map = mock(TiledMap.class);
        when(map.getLayers()).thenReturn(mapLayers);

        var fixture = mock(Fixture.class);
        var body = mock(Body.class);
        when(body.createFixture(any(FixtureDef.class))).thenReturn(fixture);
        var world = mock(World.class);
        when(world.createBody(any(BodyDef.class))).thenReturn(body);

        var textureRegion = mock(TextureAtlas.AtlasRegion.class);
        var sprite = mock(Sprite.class);
        var textureAtlas = mock(TextureAtlas.class);
        when(textureAtlas.findRegion(anyString())).thenReturn(textureRegion);
        when(textureAtlas.createSprite(anyString())).thenReturn(sprite);

        var skin = mock(Skin.class);
        try (var skinManager = mockStatic(SkinManager.class);
                var mapManager = mockStatic(MapManager.class);
                var ignored = mockConstruction(ProgressBar.class)) {
            skinManager.when(() -> SkinManager.getSkin(any(Skins.class))).thenReturn(skin);
            mapManager.when(() -> MapManager.getMapObjectData(anyString())).thenReturn(new MapObjectsPositionData());

            var player = new Player(world, textureAtlas);
            player.setMap("_", map);

            var bodyDefArg = ArgumentCaptor.forClass(BodyDef.class);
            verify(world, times(1)).createBody(bodyDefArg.capture());
            assertThat(bodyDefArg.getValue().position).isEqualTo(new Vector2(0.5f, 0.5f));
        }
    }

    @Test
    public void moveSetsCorrectVelocity() {
        var world = mock(World.class);

        var textureRegion = mock(TextureAtlas.AtlasRegion.class);
        var sprite = mock(Sprite.class);
        var textureAtlas = mock(TextureAtlas.class);
        when(textureAtlas.findRegion(anyString())).thenReturn(textureRegion);
        when(textureAtlas.createSprite(anyString())).thenReturn(sprite);

        var skin = mock(Skin.class);
        try (var skinManager = mockStatic(SkinManager.class);
                var ignored = mockConstruction(ProgressBar.class)) {
            skinManager.when(() -> SkinManager.getSkin(any(Skins.class))).thenReturn(skin);

            var player = new Player(world, textureAtlas);

            var body = mock(Body.class);
            var fixture = mock(Fixture.class);
            when(fixture.getBody()).thenReturn(body);
            player.fixture = fixture;

            var arg = ArgumentCaptor.forClass(Vector2.class);
            var times = 1;
            for (var pair : List.of(
                    Pair.of(Player.Movement.UP, new Vector2(0, Player.MOVE_SPEED)),
                    Pair.of(Player.Movement.DOWN, new Vector2(0, -Player.MOVE_SPEED)),
                    Pair.of(Player.Movement.LEFT, new Vector2(-Player.MOVE_SPEED, 0)),
                    Pair.of(Player.Movement.RIGHT, new Vector2(Player.MOVE_SPEED, 0)))) {
                pair.getLeft().set(true);

                player.move();
                verify(body, times(times)).setLinearVelocity(arg.capture());
                assertThat(arg.getValue()).isEqualTo(pair.getRight());

                pair.getLeft().set(false);
                times++;
            }
        }
    }
}
