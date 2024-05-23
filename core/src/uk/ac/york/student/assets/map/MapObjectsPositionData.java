package uk.ac.york.student.assets.map;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class used to store data about the map objects positions.
 */
// CHANGE new class
@Getter
public class MapObjectsPositionData {
    /**
     * Class used to store data about the collision objects.
     */
    @Getter
    @RequiredArgsConstructor
    public static class CollisionObjectData {
        private final float x;
        private final float y;
        private final float[] vertices;
    }

    /**
     * Class used to store data about the actionable objects.
     */
    @Getter
    @RequiredArgsConstructor
    public static class ActionableObjectData {
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        private final MapObject object;

        public Rectangle asRectangle() {
            return new Rectangle(x, y, width, height);
        }
    }

    private final ArrayList<CollisionObjectData> collisionObjects = new ArrayList<>();
    private final ArrayList<ActionableObjectData> actionableObjects = new ArrayList<>();
}
