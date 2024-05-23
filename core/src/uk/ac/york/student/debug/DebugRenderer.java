package uk.ac.york.student.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import space.earlygrey.shapedrawer.ShapeDrawer;
import uk.ac.york.student.player.Player;

/**
 * Class used to render debug bounding boxes in in-game overlay.
 */
public class DebugRenderer {
    private final ShapeDrawer shapeDrawer;

    private final Player player;

    private Integer tileWidth = null;
    private Integer tileHeight = null;

    /**
     * Class used to render debug bounding boxes in in-game overlay.
     *
     * @param player Player object which contains the bounding boxes to draw.
     * @param batch Batch object to use when drawing the bounding boxes.
     */
    public DebugRenderer(Player player, Batch batch) {
        this.player = player;
        var texture = new Texture(Gdx.files.internal("white_pixel.png"));
        this.shapeDrawer = new ShapeDrawer(batch, new TextureRegion(texture, 0, 0, 1, 1));
    }

    /**
     * Method to set the map when changed, to let the DebugRenderer update.
     * @param map TiledMap object of the map being changed to.
     */
    public void setMap(TiledMap map) {
        var layer = (TiledMapTileLayer) map.getLayers().get(0);
        tileWidth = layer.getTileWidth();
        tileHeight = layer.getTileHeight();
    }

    public void render() {
        if (tileWidth == null) {
            return;
        }

        for (var object : player.getTileObjectBoundingBoxes().values()) {
            shapeDrawer.rectangle(
                    object.x * tileWidth,
                    object.y * tileHeight,
                    object.width * tileWidth,
                    object.height * tileHeight,
                    Color.RED,
                    0.5f);
        }
    }
}
