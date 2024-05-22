package uk.ac.york.student.interactables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import uk.ac.york.student.assets.map.MapManager;

/**
 * The Interactable Manager class serves as the place where interactable objects are created, stored and accessed from.
 */
// CHANGE new class
public class InteractableManager {
    private final TextureAtlas interactableAtlas;
    private final Color defaultOutlineColor;

    @Getter
    private final Map<String, Interactable> interactableMap;

    private final OrthographicCamera camera;

    @Getter
    private Interactable currentInteractable;

    public InteractableManager(OrthographicCamera camera, TextureAtlas interactableAtlas) {
        this.camera = camera;
        this.interactableAtlas = interactableAtlas;
        this.defaultOutlineColor = new Color(1f, 1f, 1f, 1f);
        this.interactableMap = new HashMap<>();
        this.currentInteractable = null;
    }

    /**
     * Constructor for the InteractableManager class.
     *
     * @param camera The OrthographicCamera for the interactable objects to be drawn using
     */
    public InteractableManager(OrthographicCamera camera) {
        this(camera, new TextureAtlas(Gdx.files.internal("sprite-atlases/interactable.atlas")));
    }

    public void onEnable() {
        // Set up interactable objects.
        for (String mapName : MapManager.MAP_NAMES) {
            if (!Objects.equals(mapName, "blankMap")) {
                setupInteractables(mapName);
            }
        }
    }

    /**
     * Sets the current interactable object to match the current action.
     *
     * @param currentActionStr A {@link String} of the current action. This is the key in interactableMap.
     */
    public void setCurrentInteractable(@NotNull String currentActionStr) {
        currentInteractable = interactableMap.get(currentActionStr);
    }

    /**
     * Sets up the interactable objects.
     * @param mapName A {@link String} of the map name to get the interactable objects from.
     */
    void setupInteractables(String mapName) {
        var map = MapManager.getMap(mapName);
        var mapLayer = map.getLayers().get("gameObjects");
        for (var object : mapLayer.getObjects()) {
            var properties = object.getProperties();
            var isActivity = properties.get("isActivity", Boolean.class);
            var interactInfo = properties.get("interactInfo", String.class);

            if (interactInfo == null) {
                continue;
            }

            var interactList = Arrays.stream(interactInfo.split(","))
                    .map(Float::parseFloat)
                    .collect(Collectors.toUnmodifiableList());

            Interactable interact;
            String key;

            if (Boolean.TRUE.equals(isActivity)) {
                key = properties.get("activityStr", String.class);
                interact = new Interactable(interactList);
                interact.setRegion(interactableAtlas.findRegion(key));
            } else { // if not an activity, but has an interactList, must be a door
                key = properties.get("newMapStr", String.class);
                interact = new AnimatedInteractable(interactList);
                ((AnimatedInteractable) interact).setAnimationRegions(interactableAtlas.findRegions(key));
            }
            interact.setCamera(camera);
            interact.setDefaultOutlineColor(defaultOutlineColor);
            interact.setMap(mapName);
            interactableMap.put(key, interact);
        }
    }
}
