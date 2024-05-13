package uk.ac.york.student.interactables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * The Interactable Manager class serves as the place where interactable objects are created, stored and accessed from.
 */
public class InteractableManager {
    private final TextureAtlas interactableAtlas;
    private final Color defaultOutlineColor;

    @Getter
    private final Map<String, Interactable> interactableMap;

    private final OrthographicCamera camera;

    @Getter
    private Interactable currentInteractable;

    /**
     * Constructor for the InteractableManager class.
     *
     * @param camera The OrthographicCamera for the interactable objects to be drawn using
     */
    public InteractableManager(OrthographicCamera camera) {
        this.camera = camera;
        interactableAtlas = new TextureAtlas(Gdx.files.internal("sprite-atlases/interactable.atlas"));
        defaultOutlineColor = new Color(255.0F, 255.0F, 0.0F, 0.8F);
        interactableMap = new HashMap<>();
        currentInteractable = null;

        // Call the functions to set the interactable objects up.
        setupMap();
        setupHouse();
        setupPub();
        setupLibrary();
    }

    /**
     * Sets the current interactable object to match the current action.
     *
     * @param currentActionStr A {@link String} of the current action. This is the key in interactableMap.
     */
    public void setCurrentInteractable(@NotNull String currentActionStr) {
        currentInteractable = interactableMap.get(currentActionStr);
    }

    private void setupMap() {
        var pubDoor = new AnimatedInteractable(418, 155, 32, 32, 0.167f);
        pubDoor.setAnimationRegions(interactableAtlas.findRegions("pubdoor"));
        pubDoor.setCamera(camera);
        pubDoor.setDefaultOutlineColor(defaultOutlineColor);
        interactableMap.put("go into the pub", pubDoor);

        var homeDoor = new AnimatedInteractable(336, 91, 32, 32, 0.167f);
        homeDoor.setAnimationRegions(interactableAtlas.findRegions("homedoor"));
        homeDoor.setCamera(camera);
        homeDoor.setDefaultOutlineColor(defaultOutlineColor);
        interactableMap.put("go into your house", homeDoor);

        var libraryDoor = new AnimatedInteractable(65, 221, 32, 32, 0.167f);
        libraryDoor.setAnimationRegions(interactableAtlas.findRegions("librarydoor"));
        libraryDoor.setCamera(camera);
        libraryDoor.setDefaultOutlineColor(defaultOutlineColor);
        interactableMap.put("go into the library", libraryDoor);

        var bench = new Interactable(217, 239, 32, 33);
        bench.setRegion(interactableAtlas.findRegion("bench"));
        bench.setCamera(camera);
        bench.setDefaultOutlineColor(defaultOutlineColor);
        interactableMap.put("feed the ducks", bench);

        var busStop = new Interactable(56, 79, 32, 34);
        busStop.setRegion(interactableAtlas.findRegion("busstop"));
        busStop.setCamera(camera);
        busStop.setDefaultOutlineColor(defaultOutlineColor);
        interactableMap.put("head to town", busStop);
    }

    private void setupHouse() {
        var bed = new Interactable(273, 111, 32, 34);
        bed.setRegion(interactableAtlas.findRegion("bed"));
        bed.setCamera(camera);
        bed.setDefaultOutlineColor(defaultOutlineColor);
        bed.setMap("inside_house");
        interactableMap.put("go to bed", bed);

        var deskChair = new Interactable(169, 88, 32, 32);
        deskChair.setRegion(interactableAtlas.findRegion("deskchair"));
        deskChair.setCamera(camera);
        deskChair.setDefaultOutlineColor(defaultOutlineColor);
        deskChair.setMap("inside_house");
        interactableMap.put("study at your desk", deskChair);

        var fishStool = new Interactable(176, 108, 32, 32);
        fishStool.setRegion(interactableAtlas.findRegion("fishstool"));
        fishStool.setCamera(camera);
        fishStool.setDefaultOutlineColor(defaultOutlineColor);
        fishStool.setMap("inside_house");
        interactableMap.put("watch the fish", fishStool);

        var oven = new Interactable(216, 216, 32, 32);
        oven.setRegion(interactableAtlas.findRegion("oven"));
        oven.setCamera(camera);
        oven.setDefaultOutlineColor(defaultOutlineColor);
        oven.setMap("inside_house");
        interactableMap.put("cook a meal", oven);

        var fridge = new Interactable(264, 223, 32, 32);
        fridge.setRegion(interactableAtlas.findRegion("fridge"));
        fridge.setCamera(camera);
        fridge.setDefaultOutlineColor(defaultOutlineColor);
        fridge.setMap("inside_house");
        interactableMap.put("have a snack", fridge);
    }

    private void setupPub() {
        var pool = new Interactable(288, 104, 32, 32);
        pool.setRegion(interactableAtlas.findRegion("pool"));
        pool.setCamera(camera);
        pool.setDefaultOutlineColor(defaultOutlineColor);
        pool.setMap("inside_pub");
        interactableMap.put("play pool", pool);

        var pubTable = new Interactable(169, 120, 32, 32);
        pubTable.setRegion(interactableAtlas.findRegion("pubtable"));
        pubTable.setCamera(camera);
        pubTable.setDefaultOutlineColor(defaultOutlineColor);
        pubTable.setMap("inside_pub");
        interactableMap.put("eat at pub", pubTable);

        var barStool = new Interactable(168, 169, 32, 32);
        barStool.setRegion(interactableAtlas.findRegion("barstool"));
        barStool.setCamera(camera);
        barStool.setDefaultOutlineColor(defaultOutlineColor);
        barStool.setMap("inside_pub");
        interactableMap.put("drink at the bar", barStool);
    }

    private void setupLibrary() {
        var read = new Interactable(280, 58, 32, 32);
        read.setRegion(interactableAtlas.findRegion("read"));
        read.setCamera(camera);
        read.setDefaultOutlineColor(defaultOutlineColor);
        read.setMap("inside_library");
        interactableMap.put("read a book", read);

        var study = new Interactable(286, 190, 32, 32);
        study.setRegion(interactableAtlas.findRegion("study"));
        study.setCamera(camera);
        study.setDefaultOutlineColor(defaultOutlineColor);
        study.setMap("inside_library");
        interactableMap.put("study in the library", study);
    }
}
