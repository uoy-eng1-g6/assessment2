package uk.ac.york.student.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import java.util.HashMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import uk.ac.york.student.assets.map.MapManager;

/**
 * The Player class extends the Actor class and implements the PlayerScore and InputProcessor interfaces.
 * This class represents a player in the game, handling player movement, interaction with the game map, and input processing.
 * It also manages the player's score and the player's sprite on the screen.
 */
// CHANGE new functionality throughout to allow for player's collisions, and other changes to work with box2d
@Getter
public class Player implements InputProcessor {
    public static final float HITBOX_RADIUS = 0.25f;
    public static final float MOVE_SPEED = 4;

    /**
     * PlayerMetrics object to store and manage player-specific metrics.
     */
    private final PlayerMetrics metrics = new PlayerMetrics();

    /**
     * PlayerStreaks object to store and manage player streaks.
     */
    // CHANGE add PlayerStreaks to player
    private final PlayerStreaks streaks = new PlayerStreaks();

    /**
     * Sprite object representing the player's character on the screen.
     */
    private Sprite sprite;

    /**
     * Sprite objects for each direction to face in.
     *<p>
     *     There's no right facing region in the Atlas so the left one is flipped
     *     when facing right.
     *</p>
     */
    private final TextureAtlas.AtlasRegion SPRITETOWARDSREGION;

    private final TextureAtlas.AtlasRegion SPRITEAWAYREGION;
    private final TextureAtlas.AtlasRegion SPRITELEFTREGION;

    /**
     * TiledMap object representing the current game map.
     */
    private TiledMap map;

    private String mapName;

    /**
     * TextureAtlas object containing the textures for the player's sprite.
     */
    private final TextureAtlas textureAtlas;

    private final Vector2 velocity = new Vector2();

    private final World world;
    Fixture fixture;

    private final HashMap<String, Vector2> mapPositions = new HashMap<>();

    /**
     * Constructor for the Player class.
     */
    public Player(World world, TextureAtlas textureAtlas) {
        this.world = world;
        this.textureAtlas = textureAtlas;

        // Create a sprite for the player and set its position, opacity, and size
        SPRITETOWARDSREGION = textureAtlas.findRegion("char3_towards");
        SPRITEAWAYREGION = textureAtlas.findRegion("char3_away");
        SPRITELEFTREGION = textureAtlas.findRegion("char3_left");
        sprite = textureAtlas.createSprite("char3_towards");
        sprite.setAlpha(1);
    }

    public Player(World world) {
        this(world, new TextureAtlas("sprite-atlases/character-sprites.atlas"));
    }

    public Vector2 getTilePosition() {
        return fixture.getBody().getPosition();
    }

    Vector2 findStartPosition() {
        var backgroundLayer = (TiledMapTileLayer) map.getLayers().get(0);
        var tileWidth = backgroundLayer.getTileWidth();
        var tileHeight = backgroundLayer.getTileHeight();

        var mapLayer = map.getLayers().get("gameObjects");

        for (var object : mapLayer.getObjects()) {
            if (object.getProperties().containsKey("isSpawnPoint")
                    && object.getProperties().get("isSpawnPoint", Boolean.class)) {
                // Get the center point of the spawn position
                var rect = ((RectangleMapObject) object).getRectangle();
                return new Vector2(
                        (rect.getX() + (rect.width / 2)) / tileWidth, (rect.getY() + (rect.height / 2)) / tileHeight);
            }
        }
        return null;
    }

    /**
     * Sets the current game map for the player and updates related properties.
     *
     * @param map The TiledMap object representing the new game map.
     */
    public void setMap(@NotNull String name, @NotNull TiledMap map) {
        if (fixture != null) {
            mapPositions.put(mapName, fixture.getBody().getPosition().cpy());
        }

        this.map = map; // Assign the provided map to the player's map
        this.mapName = name;

        Vector2 startPosition;
        if (mapPositions.containsKey(mapName)) {
            startPosition = mapPositions.get(mapName);
        } else {
            startPosition = findStartPosition();
        }

        if (startPosition == null) {
            throw new IllegalStateException("No start position found");
        }

        var bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosition);

        var body = world.createBody(bodyDef);
        var shape = new CircleShape();
        shape.setRadius(Player.HITBOX_RADIUS);

        fixture = body.createFixture(shape, 1f);
        shape.dispose();

        // Clear the existing bounding boxes of the map objects
        tileObjectBoundingBoxes.clear();
        // Load the bounding boxes of the new map objects
        loadMapObjectBoundingBoxes();
    }

    /**
     * Enum representing the possible movements of the player.
     * It includes UP, DOWN, LEFT, RIGHT.
     * BOOST is also included to represent the boost movement (faster movement).
     * Each movement has a boolean state indicating whether it is currently active or not.
     */
    @Getter
    enum Movement {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        BOOST;

        /**
         * Boolean state of the movement.
         * If true, the movement is currently active.
         * If false, the movement is not active.
         */
        private boolean is;

        /**
         * Sets the state of the movement.
         *
         * @param is The new state of the movement. True if the movement is active, false otherwise.
         */
        void set(boolean is) {
            this.is = is;
        }
    }

    /**
     * Moves the player's sprite on the game map.
     * The movement is based on the current active movements (UP, DOWN, LEFT, RIGHT) and BOOST.
     * The sprite's position is updated and the player's bounds are set to the new position.
     * The sprite cannot move outside the bounds of the game map.
     */
    public void move() {
        velocity.set(0, 0);
        // Move the sprite up if the UP movement is active and the sprite is not at the top of the map
        if (Movement.UP.is) {
            velocity.set(velocity.x, MOVE_SPEED);
        }

        // Move the sprite down if the DOWN movement is active and the sprite is not at the bottom of the map
        if (Movement.DOWN.is) {
            velocity.set(velocity.x, -MOVE_SPEED);
        }

        // Move the sprite left if the LEFT movement is active and the sprite is not at the left edge of the map
        if (Movement.LEFT.is) {
            velocity.set(-MOVE_SPEED, velocity.y);
        }

        // Move the sprite right if the RIGHT movement is active and the sprite is not at the right edge of the map
        if (Movement.RIGHT.is) {
            velocity.set(MOVE_SPEED, velocity.y);
        }

        fixture.getBody().setLinearVelocity(velocity);
    }

    /**
     * Enum representing the possible transitions for the player.
     * It includes NEW_MAP and ACTIVITY transitions.
     * NEW_MAP is used when the player transitions to a new map.
     * ACTIVITY is used when the player does an activity on the map.
     */
    public enum Transition {
        NEW_MAP,
        ACTIVITY
    }

    /**
     * A HashMap storing the bounding boxes of the map objects.
     * The key is a MapObject and the value is a Rectangle.
     * This is used to store the bounding boxes of the map objects for collision detection.
     */
    @Getter
    private final HashMap<MapObject, Rectangle> tileObjectBoundingBoxes = new HashMap<>();

    /**
     * Loads the bounding boxes of the actionable game objects from the map.
     * The bounding boxes are stored in the tileObjectBoundingBoxes HashMap.
     * The key is a MapObject and the value is a BoundingBox.
     * This method is typically called when a new map is set for the player.
     */
    public void loadMapObjectBoundingBoxes() {
        for (var actionableObject : MapManager.getMapObjectData(mapName).getActionableObjects()) {
            tileObjectBoundingBoxes.put(actionableObject.getObject(), actionableObject.asRectangle());
        }
    }

    /**
     * Returns the current map object that the player's sprite is on.
     * This is determined by checking if the player's center position is within the bounding box of each map object.
     * If the player's sprite is not on any map object, null is returned.
     *
     * @return The MapObject that the player's sprite is currently on, or null if the sprite is not on any map object.
     */
    public @Nullable MapObject getCurrentMapObject() {
        var position = getTilePosition();
        // Iterate over each entry in the tileObjectBoundingBoxes HashMap
        for (var entry : tileObjectBoundingBoxes.entrySet()) {
            // Get the center position of the player's sprite
            if (Intersector.overlaps(new Circle(position, HITBOX_RADIUS), entry.getValue())) {
                return entry.getKey();
            }
        }

        // If the player's sprite is not on any map object, return null
        return null;
    }

    /**
     * Checks if the player's sprite is currently on a transition tile.
     * A transition tile is a tile that has either the "isNewMap" or "isActivity" property set to true.
     * If the player's sprite is on a tile with the "isNewMap" property set to true, the NEW_MAP transition is returned.
     * If the player's sprite is on a tile with the "isActivity" property set to true, the ACTIVITY transition is returned.
     * If the player's sprite is not on any transition tile, null is returned.
     *
     * @return The Transition that the player's sprite is currently on, or null if the sprite is not on any transition tile.
     */
    public @Nullable Transition isInTransitionTile() {
        // Retrieve the current map object that the player's sprite is on
        MapObject tileObject = getCurrentMapObject();

        // If the player's sprite is not on any map object, return null
        if (tileObject == null) return null;

        // If the map object has the "isNewMap" property set to true, return the NEW_MAP transition
        if (Boolean.TRUE.equals(tileObject.getProperties().get("isNewMap", Boolean.class))) {
            return Transition.NEW_MAP;
        }
        // If the map object has the "isActivity" property set to true, return the ACTIVITY transition
        else if (Boolean.TRUE.equals(tileObject.getProperties().get("isActivity", Boolean.class))) {
            return Transition.ACTIVITY;
        }

        // If the map object does not have either the "isNewMap" or "isActivity" property set to true, return null
        return null;
    }

    /**
     * Handles the key press events for the player's movement.
     * This method is called when a key is pressed.
     * The player's movement is updated based on the key pressed.
     * The possible keys are W (up), S (down), A (left), D (right)
     * If the key pressed is not one of these, the method returns false.
     * If the key pressed is one of these, the corresponding movement is set to active and the method returns true.
     *
     * @param keycode The integer value representing the key pressed. Use {@link Input.Keys} to get the key codes.
     * @return True if the key pressed corresponds to a movement, false otherwise.
     */
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                Movement.UP.set(true);
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                Movement.DOWN.set(true);
                break;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                Movement.LEFT.set(true);
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                Movement.RIGHT.set(true);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Handles the key release events for the player's movement.
     * This method is called when a key is released.
     * The player's movement is updated based on the key released.
     * The possible keys are W (up), S (down), A (left), D (right)
     * If the key released is not one of these, the method returns false.
     * If the key released is one of these, the corresponding movement is set to inactive and the method returns true.
     *
     * @param keycode The integer value representing the key released. Use {@link Input.Keys} to get the key codes.
     * @return True if the key released corresponds to a movement, false otherwise.
     */
    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                Movement.UP.set(false);
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                Movement.DOWN.set(false);
                break;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                Movement.LEFT.set(false);
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                Movement.RIGHT.set(false);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Handles the key typed event.
     * This method is called when a key is typed.
     * Currently, this method does not perform any action and always returns false.
     *
     * @param character The character of the key typed.
     * @return False, as this method does not perform any action.
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Handles the touch down event.
     * This method is called when the screen is touched.
     * Currently, this method does not perform any action and always returns false.
     *
     * @param screenX The x-coordinate of the touch event.
     * @param screenY The y-coordinate of the touch event.
     * @param pointer The pointer for the touch event.
     * @param button The button of the touch event.
     * @return False, as this method does not perform any action.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Handles the touch up event.
     * This method is called when a touch on the screen is released.
     * Currently, this method does not perform any action and always returns false.
     *
     * @param screenX The x-coordinate of the touch event.
     * @param screenY The y-coordinate of the touch event.
     * @param pointer The pointer for the touch event.
     * @param button The button of the touch event.
     * @return False, as this method does not perform any action.
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Handles the touch cancelled event.
     * This method is called when a touch event is cancelled.
     * Currently, this method does not perform any action and always returns false.
     *
     * @param screenX The x-coordinate of the touch event.
     * @param screenY The y-coordinate of the touch event.
     * @param pointer The pointer for the touch event.
     * @param button The button of the touch event.
     * @return False, as this method does not perform any action.
     */
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Handles the touch dragged event.
     * This method is called when a touch on the screen is dragged.
     * Currently, this method does not perform any action and always returns false.
     *
     * @param screenX The x-coordinate of the touch event.
     * @param screenY The y-coordinate of the touch event.
     * @param pointer The pointer for the touch event.
     * @return False, as this method does not perform any action.
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    /**
     * Handles the mouse moved event.
     * This method is called when the mouse is moved.
     * Currently, this method does not perform any action and always returns false.
     *
     * @param screenX The x-coordinate of the mouse event.
     * @param screenY The y-coordinate of the mouse event.
     * @return False, as this method does not perform any action.
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    /**
     * Handles the scrolled event.
     * This method is called when the mouse wheel is scrolled.
     * Currently, this method does not perform any action and always returns false.
     *
     * @param amountX The amount of horizontal scroll.
     * @param amountY The amount of vertical scroll.
     * @return False, as this method does not perform any action.
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    /**
     * Disposes of the resources used by the player.
     * This method is called when the player is no longer needed, to free up memory.
     */
    public void dispose() {
        textureAtlas.dispose(); // Dispose of the TextureAtlas
        // CHANGE do not dispose of the PlayerMetrics
    }

    /**
     * Sets the opacity of the player's sprite.
     * The opacity is a value between 0 (completely transparent) and 1 (completely opaque).
     *
     * @param opacity The opacity value to set. This should be a float between 0 and 1.
     */
    public void setOpacity(@Range(from = 0, to = 1) float opacity) {
        sprite.setAlpha(opacity);
    }
}
