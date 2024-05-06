package uk.ac.york.student.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DebugRenderer {
    private final ShapeDrawer shapeDrawer;

    private TiledMap map = null;
    private MapObjects mapObjects;

    public DebugRenderer(Batch batch) {
        var texture = new Texture(Gdx.files.internal("white_pixel.png"));
        this.shapeDrawer = new ShapeDrawer(batch, new TextureRegion(texture, 0, 0, 1, 1));
    }

    public void setMap(TiledMap map) {
        this.map = map;
        this.mapObjects = map.getLayers().get("gameObjects").getObjects();
    }

    public void render() {
        if (map == null) {
            return;
        }

        for (var object : mapObjects) {
            var rect = ((RectangleMapObject) object).getRectangle();
            shapeDrawer.rectangle(new Rectangle(rect.x, rect.y, rect.width, rect.height), Color.RED, 0.5f);
        }
    }
}
