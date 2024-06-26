package uk.ac.york.student.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.ac.york.student.GdxGame;
import uk.ac.york.student.assets.map.ActionMapObject;
import uk.ac.york.student.assets.map.ActivityMapObject;
import uk.ac.york.student.assets.map.MapManager;
import uk.ac.york.student.assets.map.TransitionMapObject;
import uk.ac.york.student.assets.skins.SkinManager;
import uk.ac.york.student.assets.skins.Skins;
import uk.ac.york.student.debug.DebugRenderer;
import uk.ac.york.student.game.GameTime;
import uk.ac.york.student.game.activities.Activity;
import uk.ac.york.student.interactables.AnimatedInteractable;
import uk.ac.york.student.interactables.Interactable;
import uk.ac.york.student.interactables.InteractableManager;
import uk.ac.york.student.player.Player;
import uk.ac.york.student.player.PlayerMetric;
import uk.ac.york.student.player.PlayerMetrics;
import uk.ac.york.student.settings.DebugScreenPreferences;
import uk.ac.york.student.settings.GamePreferences;
import uk.ac.york.student.utils.Pair;

/**
 * The {@link GameScreen} class extends the {@link BaseScreen} class and implements the {@link InputProcessor} interface.
 * This class is responsible for handling the game screen and its related functionalities.
 * It includes methods for rendering the game screen, handling user inputs, managing game activities, and more.
 */
public class GameScreen extends BaseScreen implements InputProcessor {
    /**
     * The key code for the action key. This is used to trigger actions in the game.
     */
    private static final int ACTION_KEY = Input.Keys.E;

    /**
     * The stage for this game screen. This is where all the actors for the game are added.
     */
    @Getter
    private final Stage processor;

    /**
     * The player of the game. This is the main character that the user controls.
     */
    private final Player player;

    /**
     * The name of the map which is currently active.
     */
    // CHANGE this attribute is new
    private String currentMapName;

    /**
     * The interactable manager. This is what creates and stores the information for the interactable objects.
     */
    // CHANGE this attribute is new
    private final InteractableManager interactableManager;

    /**
     * The game time. This keeps track of the current time in the game.
     */
    private final GameTime gameTime;

    /**
     * The map for the game. This is loaded from the {@link MapManager} with {@link MapManager#getMap(String)}.
     */
    private TiledMap map;

    /**
     * The scale of the map. This is used to adjust the size of the map to fit the screen.
     */
    private float mapScale;

    /**
     * The renderer for the map. This is used to draw the map on the screen.
     */
    private TiledMapRenderer renderer;

    /**
     * The skin for the game. This is used to style the game's UI elements.
     */
    // CHANGE use better name for skin attribute and use improved method in SkinManager
    private final Skin skin = SkinManager.getSkin(Skins.CRAFTACULAR);

    /**
     * The table for the action UI. This is where the action label is added.
     */
    private final Table actionTable = new Table(skin);

    /**
     * The table for the metrics UI. This is where the metrics labels and progress bars are added.
     */
    private final Table metricsTable = new Table();

    /**
     * The table for the time UI. This is where the time label and progress bar are added.
     */
    private final Table timeTable = new Table();

    /**
     * The label for the action UI. This displays the current action that the player can perform.
     */
    private final Label actionLabel = new Label("ENG1 Project. Super cool. (You will never see this)", skin);

    /**
     * The label for the time UI. This displays the current time in the game.
     */
    private final Label timeLabel = new Label("You exist outside of the space-time continuum.", skin);

    // CHANGE below attributes are new
    /**
     * The box2d world for managing physics.
     */
    private World world;

    private final OrthographicCamera gameCamera;
    private final Viewport gameViewport;

    /**
     * Boolean for whether the game's debug mode is enabled or not.
     */
    private final Boolean debugEnabled;
    /**
     * box2d debug renderer for rendering collision boxes in debug mode.
     */
    private final Box2DDebugRenderer box2dDebugRenderer;

    /**
     * debug renderer for all other bounding boxes in debug mode.
     */
    private final DebugRenderer debugRenderer;

    /**
     * boolean for whether the GameScreen should fade in or not.
     */
    private final boolean shouldFadeIn;
    /**
     * float for how long the fade in should last if shouldFadeIn is true.
     */
    private final float fadeInTime;

    /**
     * Constructor for the {@link GameScreen} class.
     *
     * @param game The {@link GdxGame} instance that this screen is part of.
     */
    // CHANGE added parameters shouldFadeIn and fadeInTime
    public GameScreen(GdxGame game, boolean shouldFadeIn, float fadeInTime) {
        super(game);

        // CHANGE remove code for setting up tilemap from the constructor of GameScreen

        this.shouldFadeIn = shouldFadeIn;
        this.fadeInTime = fadeInTime;

        // Initialize the game time
        gameTime = new GameTime();

        // Initialize the current map name. CHANGE new attribute to initialize
        currentMapName = "map";

        // CHANGE new attribute to initialize
        world = new World(new Vector2(0, 0), true);

        // Initialize the player CHANGE to work with the box2d world
        player = new Player(world);

        // Initialize the stage and set it as the input processor CHANGE new attributes to initialize
        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(1, 1, gameCamera);

        var stageViewport = new FitViewport(1, 1);
        processor = new Stage(stageViewport);

        Gdx.input.setInputProcessor(processor);

        // CHANGE new attribute to initialize
        interactableManager = new InteractableManager((OrthographicCamera) processor.getCamera());
        interactableManager.onEnable();

        // Add a listener to the stage to handle key events
        processor.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return GameScreen.this.keyDown(keycode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return GameScreen.this.keyUp(keycode);
            }
        });

        // CHANGE new attributes to initialize
        debugEnabled = ((DebugScreenPreferences) GamePreferences.DEBUG_SCREEN.getPreference()).isEnabled();
        debugRenderer = new DebugRenderer(player, processor.getBatch());
        box2dDebugRenderer = new Box2DDebugRenderer();

        // CHANGE set up tile map in changeMap function
        changeMap("map", true);
    }

    /**
     * Changes the current map to a new map specified by the mapName parameter.
     * The screen fades out to black, then the new map is loaded and the screen fades back in.
     *
     * @param mapName The name of the new map to load.
     */
    // CHANGE refactor method to implement collisions with box2d
    public void changeMap(String mapName, boolean immediate) {
        Runnable mapChangeFn = () -> {
            // Load the new map
            map = MapManager.getMap(mapName);
            debugRenderer.setMap(map);

            // Clear collision objects from world
            var bodies = new Array<Body>();
            world.getBodies(bodies);
            for (int i = 0; i < bodies.size; i++) {
                world.destroyBody(bodies.get(i));
            }

            player.setMap(mapName, map);

            // Get the first layer of the map
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
            // Get the width and height of map tiles and the map background
            var mapWidth = layer.getWidth();
            var mapHeight = layer.getHeight();
            var tileWidth = layer.getTileWidth();

            mapScale = (float) 1 / tileWidth;

            // Initialize the map renderer for the new map
            renderer = new OrthogonalTiledMapRenderer(map, mapScale);

            for (var collidable : MapManager.getMapObjectData(mapName).getCollisionObjects()) {
                var bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(collidable.getX(), collidable.getY());

                var body = world.createBody(bodyDef);
                var shape = new PolygonShape();
                shape.set(collidable.getVertices());

                body.createFixture(shape, 0f);
                shape.dispose();
            }

            gameCamera.setToOrtho(false, mapWidth, mapHeight);
            gameViewport.setWorldSize(mapWidth, mapHeight);
            processor.getViewport().setWorldSize(mapWidth * tileWidth, mapHeight * tileWidth);

            // Set the view of the map renderer to the camera of the stage
            renderer.setView(gameCamera);

            // Set the input processor to the stage
            Gdx.input.setInputProcessor(processor);

            // set currentMapName to mapName
            currentMapName = mapName;
        };

        if (immediate) {
            mapChangeFn.run();
            return;
        }

        // make the screen black slowly
        processor.getRoot().getColor().a = 1;
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.fadeOut(0.5f));
        sequenceAction.addAction(Actions.run(mapChangeFn));
        // Fade the screen back in
        sequenceAction.addAction(Actions.fadeIn(0.5f));
        // Add the sequence action to the root of the stage
        processor.getRoot().addAction(sequenceAction);
    }

    /**
     * This method is called when this screen becomes the current screen for the {@link GdxGame}.
     * It sets up the game UI, including the action table, metrics table, and timetable.
     * It also updates the viewport of the stage.
     */
    @Override
    public void show() {
        // CHANGE check if shouldFadeIn before fading in
        if (shouldFadeIn) {
            processor.getRoot().getColor().a = 0;
            processor.getRoot().addAction(fadeIn(fadeInTime));
        }

        // Get the width and height of the screen
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // Set up the action table
        actionTable.setFillParent(true);
        processor.addActor(actionTable);
        actionLabel.setFontScale(0.3f); // CHANGE set the font scale
        actionTable.add(actionLabel);
        actionTable.bottom();
        actionTable.padBottom(10);

        // CHANGE set debug mode to the debugEnabled Boolean
        actionTable.setDebug(debugEnabled);
        metricsTable.setDebug(debugEnabled);

        // Set up the metrics table
        metricsTable.setFillParent(true);
        processor.addActor(metricsTable);
        PlayerMetrics metrics = player.getMetrics();

        // CHANGE use a map instead of two separate lists for related variables
        var metricComponents = metrics.getMetrics().stream()
                .map(metric -> Pair.of(metric.getLabel(), metric.getProgressBar()))
                .collect(Collectors.toList());

        // CHANGE iterate through components instead of iterator
        for (var component : metricComponents) {
            var label = new Label(component.getLeft(), skin);
            label.setFontScale(0.25f);
            metricsTable.add(label).padRight(2);
            metricsTable.add(component.getRight()).width(100).height(30).row();
        }

        metricsTable.bottom().right();
        metricsTable.padBottom(2); // CHANGE padding value
        metricsTable.padRight(2); // CHANGE padding value

        // Set up the timetable
        ProgressBar timeBar = gameTime.getProgressBar();
        String currentHour = getCurrentHourString();
        String currentDay = "Day " + (gameTime.getCurrentDay() + 1);
        String time = currentDay + " " + currentHour;
        timeTable.setFillParent(true);
        processor.addActor(timeTable);
        timeTable.setWidth(500);
        timeLabel.setText(time);
        timeLabel.setFontScale(0.5f);
        timeTable.add(timeLabel);
        timeTable.row();
        timeTable.add(timeBar); // CHANGE remove .width(500)
        timeTable.top();
        timeTable.padTop(10);

        // Update the viewport of the stage
        processor.getViewport().update((int) width, (int) height);
    }

    /**
     * This method returns a string representation of the current hour in the game.
     * The game starts at 8 AM and ends at 12 AM (midnight). The time is formatted as HH:MM AM/PM.
     * However, MM is always 00 because the game progresses in hourly increments.
     * If the game is at the end of the day, the method returns "00:00 - Time to sleep!".
     *
     * @return A string representing the current hour in the game.
     */
    @NotNull private String getCurrentHourString() {
        int currentHourNum = gameTime.getCurrentHour(); // Get the current hour number from the game time
        final int startHour = 8; // Define the start hour of the game
        final int midday = 12 - startHour; // Calculate the hour number for midday
        boolean isAm = currentHourNum < (12 - startHour); // Determine if the current time is AM
        String currentHour; // Initialize the string to hold the current hour
        if (!gameTime.isEndOfDay()) { // If it's not the end of the day
            // Calculate the current hour based on whether it's AM or PM
            currentHour = String.valueOf(
                    isAm || currentHourNum == midday
                            ? currentHourNum + startHour
                            : currentHourNum - (12 - startHour)); // 9am start
            if (currentHour.length() == 1)
                currentHour = "0" + currentHour; // Add a leading zero if the hour is a single digit
            currentHour += ":00"; // Add the minutes (always 00)
            if (isAm) { // If it's AM
                currentHour += " AM"; // Add " AM" to the current hour
            } else { // If it's PM
                currentHour += " PM"; // Add " PM" to the current hour
            }
        } else { // If it's the end of the day
            currentHour = "00:00 - Time to sleep!"; // Set the current hour to "00:00 - Time to sleep!"
        }
        return currentHour; // Return the current hour
    }

    /**
     * An {@link AtomicReference} to an {@link ActionMapObject}. This object represents the current action that the player can perform.
     * It is nullable, meaning it can be null if there is no current action.
     * {@link AtomicReference} is used to ensure thread-safety when accessing and updating this variable.
     */
    private final AtomicReference<@Nullable ActionMapObject> currentActionMapObject = new AtomicReference<>(null);
    /**
     * This method is called every frame to render the game screen.
     * It clears the screen, updates the player's position, sets the opacity of the player and map layers,
     * calculates and sets the camera's position, updates the positions of the UI tables, renders the map,
     * draws the player, updates the action label, and updates and draws the stage.
     *
     * @param v The time in seconds since the last frame.
     */
    // CHANGE refactor this function to improve rendering logic and add functionality
    @Override
    public void render(float v) {
        // Set the blend function for the OpenGL context. This determines how new pixels are combined with existing
        // pixels.
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Clear the screen. This wipes out all previous drawings and sets the screen to a blank state.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set the active texture unit to texture unit 0. This is the default and most commonly used texture unit.
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        // Set the clear color to black. This is the color that the screen is cleared to when glClear is called.
        Gdx.gl.glClearColor(0, 0, 0, 1);

        if (renderer != null) {
            // Move the player. This updates the player's position based on their current velocity and the elapsed time
            // since the last frame.
            player.move();

            // Set the opacity of the player. This determines how transparent the player is. A value of 1 means fully
            // opaque, and a value of 0 means fully transparent.
            player.setOpacity(processor.getRoot().getColor().a);

            // Set the opacity of all layers in the map. This determines how transparent the layers are. A value of 1
            // means
            // fully opaque, and a value of 0 means fully transparent.
            // CHANGE use better logic for this part
            map.getLayers()
                    .forEach(layer -> layer.setOpacity(processor.getRoot().getColor().a));

            // Set the view of the map renderer to the camera. This determines what part of the map is drawn to the
            // screen.
            renderer.setView(gameCamera);

            // Render the map. This draws the map to the screen.
            renderer.render();

            // Draw interactable objects.
            for (Interactable interactable :
                    interactableManager.getInteractableMap().values()) {
                if (Objects.equals(interactable.getMap(), currentMapName)) {
                    interactable.draw();
                }
                interactable.setAlpha(processor.getRoot().getColor().a);
            }
            // Animate the currentInteractable if it is animated and should currently be animating.
            if (interactableManager.getCurrentInteractable() instanceof AnimatedInteractable) {
                AnimatedInteractable animatedInteractable =
                        (AnimatedInteractable) interactableManager.getCurrentInteractable();
                if (Objects.equals(currentMapName, animatedInteractable.getMap())
                        && animatedInteractable.isAnimating()) {
                    animatedInteractable.animate();
                }
            }

            // Check if the player is in a transition tile. If they are, update the action label to reflect the possible
            // action.
            Player.Transition transitionTile = player.isInTransitionTile();
            if (transitionTile != null) {
                setActionLabel(transitionTile);
            } else {
                // If the player is not in a transition tile, hide the action label.
                currentActionMapObject.set(null);
                actionLabel.setVisible(false);
            }

            // Get the batch for the stage. This is used to draw the player and other game objects.
            Batch batch = processor.getBatch();
            batch.begin();

            if (debugEnabled) {
                debugRenderer.render();
            }

            var rawPosition = player.getTilePosition();
            batch.draw(
                    player.getSprite(),
                    (rawPosition.x * 16) - (player.getSprite().getWidth() / 2),
                    (rawPosition.y * 16) - (player.getSprite().getHeight() / 4));
            batch.end();

            if (debugEnabled) {
                box2dDebugRenderer.render(world, gameCamera.combined);
            }
        }

        // Draw the stage. This renders all actors added to the stage, including the player and UI elements.
        processor.draw();

        // Update the stage. This updates the state of all actors added to the stage, including the player and UI
        // elements.
        processor.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        // Step the world. This updates the position of all physics objects on the map (just the player).
        if (world != null) {
            world.step(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f), 8, 3);
        }
    }

    /**
     * This method sets the action label based on the player's current transition tile.
     * It first gets the {@link ActionMapObject} associated with the transition tile and the player's current map object.
     * Then it constructs the action text based on the type of the {@link ActionMapObject}.
     * If the {@link ActionMapObject} is an {@link ActivityMapObject}, it checks if the player has enough time and resources to perform the activity.
     * If the player does not have enough time or resources, the action text is updated to reflect this.
     * Finally, the action text is set as the text of the action label, and the action label is made visible.
     *
     * @param transitionTile The player's current transition tile.
     */
    private void setActionLabel(Player.Transition transitionTile) {
        // Get the ActionMapObject associated with the transition tile and the player's current map object
        var actionMapObject = getActionMapObject(transitionTile, player.getCurrentMapObject());
        currentActionMapObject.set(actionMapObject);

        // Construct the action text based on the type of the ActionMapObject
        StringBuilder actionText = new StringBuilder(getActionText(actionMapObject));

        // Initialize the vector for the outline color. CHANGE new variable
        Color outlineColor = new Color(0.0F, 255.0F, 0F, processor.getRoot().getColor().a);

        // Check if the ActionMapObject is an instance of ActivityMapObject
        if (actionMapObject instanceof ActivityMapObject) {
            // Set the outline color to red by default, and we will make it green if it passes checks.
            // CHANGE new functionality
            outlineColor.r = 255.0F;
            outlineColor.g = 0.0F;
            // Cast the ActionMapObject to an ActivityMapObject
            ActivityMapObject activityMapObject = (ActivityMapObject) actionMapObject;

            // Get the required time for the activity
            int requiredTime = activityMapObject.getTime();

            // Get the type of the activity
            Activity activity = activityMapObject.getType();

            // Get a list of negative effects from the activity's effects
            // Negative effects are those that decrease a player metric
            List<Pair<PlayerMetrics.MetricType, PlayerMetrics.MetricEffect>> negativeEffects =
                    activity.getEffects().stream()
                            .filter(x -> x.getRight().equals(PlayerMetrics.MetricEffect.DECREASE))
                            .collect(Collectors.toList());

            // Initialize a boolean to track if the player has enough resources for the activity
            boolean hasEnough = true;

            // Initialize a list to store the names of the metrics that the player does not have enough of
            List<String> negativeEffectNames = new ArrayList<>();

            // Iterate over the negative effects
            for (Pair<PlayerMetrics.MetricType, PlayerMetrics.MetricEffect> negativeEffect : negativeEffects) {
                // Get the type of the metric
                PlayerMetrics.MetricType metricType = negativeEffect.getLeft();

                // Get the amount by which the activity changes the metric
                float changeAmount = activityMapObject.getChangeAmount(metricType);

                // Get the current value of the metric for the player
                PlayerMetric metric = player.getMetrics().getMetric(metricType);
                float currentMetric = metric.get();

                // Check if the player has enough of the metric for the activity
                boolean tempEnough = currentMetric >= changeAmount;

                // Update the hasEnough boolean if the player does not have enough of the metric
                if (hasEnough) {
                    hasEnough = tempEnough;
                }

                // If the player does not have enough of the metric, add the metric's label to the list of negative
                // effect names
                if (!tempEnough) {
                    negativeEffectNames.add(metric.getLabel());
                }
            }

            // Check if the player does not have enough time or resources to perform the activity
            // If it's the end of the day and the activity is not sleeping, set the action text to "Night owl, it's time
            // to sleep!"
            if (gameTime.isEndOfDay() && !activity.equals(Activity.SLEEP)) {
                actionText = new StringBuilder("Night owl, it's time to sleep!");
            }
            // If the current hour plus the required time for the activity is greater than the length of the day and the
            // activity is not sleeping,
            // set the action text to "You don't have enough time to do this activity."
            else if (gameTime.getCurrentHour() + requiredTime > GameTime.getDayLength()
                    && !activity.equals(Activity.SLEEP)) {
                actionText = new StringBuilder("You don't have enough time to do this activity.");
            }
            // If there are negative effects and the player does not have enough resources and the activity is not
            // sleeping,
            // set the action text to "You don't have enough [resource] to do this activity."
            else if (!negativeEffects.isEmpty() && !hasEnough && !activity.equals(Activity.SLEEP)) {
                actionText = new StringBuilder("You don't have enough ");
                // If there is only one resource the player does not have enough of, append the name of that resource to
                // the action text
                if (negativeEffectNames.size() == 1) {
                    actionText.append(negativeEffectNames.get(0));
                }
                // If there are multiple resources the player does not have enough of, append each resource name to the
                // action text,
                // separated by commas and an "and" before the last resource
                else {
                    for (int i = 0; i < negativeEffectNames.size(); i++) {
                        actionText.append(negativeEffectNames.get(i));
                        if (i == negativeEffectNames.size() - 2) {
                            actionText.append(" and ");
                        } else if (i < negativeEffectNames.size() - 2) {
                            actionText.append(", ");
                        }
                    }
                    actionText.append(" to do this activity.");
                }
            } else {
                // Checks passed, set the outline color to green. CHANGE new functionality
                outlineColor.r = 0.0F;
                outlineColor.g = 255.0F;
                // If the player has enough resources and time to perform the activity
                if (!activity.equals(Activity.SLEEP)) {
                    // If the activity is not sleeping, append the required time for the activity to the action text
                    actionText.append(" (").append(requiredTime).append(" hours)");

                } else {
                    // If the activity is sleeping CHANGE refactor with ? operator
                    actionText.append(gameTime.isEndOfDays() ? " (End of the game!)" : " (End of the day)");
                }
            }
        }
        // Set the action text as the text of the action label, and make the action label visible
        actionLabel.setText(actionText.toString());
        actionLabel.setVisible(true);

        // CHANGE new functionality
        interactableManager.setCurrentInteractable(
                Objects.requireNonNull(currentActionMapObject.get()).getStr());
        var currentInteractable = interactableManager.getCurrentInteractable();
        if (currentInteractable != null && Objects.equals(currentInteractable.getMap(), currentMapName)) {
            currentInteractable.setOutlineColor(outlineColor);
        }
    }

    /**
     * This method constructs a string that represents the action text for a given {@link ActionMapObject}.
     * The action text is a string that instructs the player to press a certain key to perform an action.
     * The action is determined by the string representation of the {@link ActionMapObject}.
     *
     * @param actionMapObject The {@link ActionMapObject} for which to construct the action text.
     * @return A {@link String} representing the action text for the given {@link ActionMapObject}.
     */
    @NotNull private String getActionText(@NotNull ActionMapObject actionMapObject) {
        String actionText = "Press " + Input.Keys.toString(ACTION_KEY) + " to ";
        actionText += actionMapObject.getStr();
        return actionText;
    }

    /**
     * This method returns an {@link ActionMapObject} based on the player's current transition tile and the associated map object.
     * If the transition tile is an {@link Player.Transition#ACTIVITY}, it returns an {@link ActivityMapObject}.
     * If the transition tile is a {@link Player.Transition#NEW_MAP}, it returns a {@link TransitionMapObject}.
     * If the transition tile is neither an {@link Player.Transition#ACTIVITY} nor a {@link Player.Transition#NEW_MAP}, it throws an {@link IllegalStateException}.
     *
     * @param transitionTile The player's current transition tile.
     * @param tileObject The map object associated with the transition tile.
     * @return An {@link ActionMapObject} based on the transition tile and map object.
     * @throws IllegalStateException If the transition tile is neither an {@link Player.Transition#ACTIVITY} nor a {@link Player.Transition#NEW_MAP}.
     */
    @NotNull private static ActionMapObject getActionMapObject(Player.@NotNull Transition transitionTile, MapObject tileObject) {
        ActionMapObject actionMapObject;
        if (transitionTile.equals(Player.Transition.ACTIVITY)) {
            actionMapObject = new ActivityMapObject(tileObject);
        } else if (transitionTile.equals(Player.Transition.NEW_MAP)) {
            actionMapObject = new TransitionMapObject(tileObject);
        } else {
            throw new IllegalStateException("Unexpected value: " + transitionTile);
        }
        return actionMapObject;
    }

    /**
     * This method is called when the screen size changes. It resizes the game screen to fit the new screen size.
     * It recalculates the scale of the map based on the new screen size and tile size, and initializes the map renderer with the new map scale.
     * It also updates the viewport of the stage with the new screen width and height, and sets the camera's position to the player's position,
     * but constrained within the minimum and maximum x and y coordinates. Finally, it sets the view of the map renderer to the camera.
     *
     * @param screenWidth The new width of the screen.
     * @param screenHeight The new height of the screen.
     */
    // CHANGE refactor a lot of this functionality out to simplify logic and fix issues
    @Override
    public void resize(int screenWidth, int screenHeight) {
        gameViewport.update(screenWidth, screenHeight);
        processor.getViewport().update(screenWidth, screenHeight, true);

        if (renderer != null) {
            // Set the view of the map renderer to the camera
            renderer.setView(gameCamera);
        }
    }

    /**
     * This method is called when the game is paused.
     * Currently, it does not perform any actions when the game is paused.
     */
    @Override
    public void pause() {}

    /**
     * This method is called when the game is resumed from a paused state.
     * Currently, it does not perform any actions when the game is resumed.
     */
    @Override
    public void resume() {}

    /**
     * This method is called when the game screen is hidden or minimized.
     * Currently, it does not perform any actions when the game screen is hidden.
     */
    @Override
    public void hide() {}

    /**
     * This method is called when the game screen is being disposed of.
     * It disposes of the {@link GameScreen#map}, {@link GameScreen#processor},
     * {@link GameScreen#skin}, {@link GameScreen#player}, and {@link GameScreen#world}
     * to free up resources and prevent memory leaks.
     */
    @Override
    public void dispose() {
        processor.dispose();
        player.dispose();

        world.dispose();
        world = null;
    }

    /**
     * This method is called when a key is pressed down.
     * It first checks if the key pressed is the action key (defined as a constant).
     * If it is, it retrieves the current {@link ActionMapObject} (which represents the current action that the player can perform).
     * If the {@link ActionMapObject} is not null, it checks if it is an instance of {@link ActivityMapObject} or {@link TransitionMapObject}.
     * If it's an {@link ActivityMapObject}, it calls the {@link GameScreen#doActivity(ActivityMapObject)} method and returns its result.
     * If it's a {@link TransitionMapObject}, it calls the {@link GameScreen#doMapChange(TransitionMapObject)} method and returns its result.
     * If the {@link ActionMapObject} is neither an {@link ActivityMapObject} nor a {@link TransitionMapObject}, it throws an {@link IllegalStateException}.
     * If the key pressed is not the action key, it calls the {@link Player#keyDown(int)} method of the {@link Player} and returns its result.
     *
     * @param keycode The key code of the key that was pressed down.
     * @return A boolean indicating whether the key press was handled.
     * @throws IllegalStateException If the {@link ActionMapObject} is neither an {@link ActivityMapObject} nor a {@link TransitionMapObject}.
     */
    @Override
    public boolean keyDown(int keycode) {
        boolean playerKeyDown = player.keyDown(keycode);
        if (keycode == ACTION_KEY) {
            ActionMapObject actionMapObject = currentActionMapObject.get();
            if (actionMapObject != null) {
                if (actionMapObject instanceof ActivityMapObject) {
                    return doActivity((ActivityMapObject) actionMapObject);
                } else if (actionMapObject instanceof TransitionMapObject) {
                    return doMapChange((TransitionMapObject) actionMapObject);
                } else {
                    throw new IllegalStateException("Unexpected value: " + actionMapObject);
                }
            }
        }
        return playerKeyDown;
    }

    /**
     * This method is used to change the current map to a new map.
     * The new map is specified by the type of the provided {@link TransitionMapObject}.
     * After changing the map, it returns true to indicate that the map change was successful.
     *
     * @param actionMapObject The {@link TransitionMapObject} that contains the type of the new map.
     * @return A boolean indicating whether the map change was successful.
     */
    private boolean doMapChange(@NotNull TransitionMapObject actionMapObject) {
        changeMap(actionMapObject.getType(), false);

        // Trigger the current interactable object's animation if it is animated. CHANGE new functionality
        if (interactableManager.getCurrentInteractable() instanceof AnimatedInteractable) {
            ((AnimatedInteractable) interactableManager.getCurrentInteractable()).setAnimating(true);
        }

        return true;
    }

    /**
     * This method is used to perform an activity in the game.
     * The activity is specified by the provided {@link ActivityMapObject}.
     * It first checks if the game is at the end of the day and if the activity is not sleeping, if so it returns false.
     * Then it checks if the current hour plus the required time for the activity is greater than the length of the day and if the activity is not sleeping, if so it returns false.
     * It then checks if the player has enough resources to perform the activity, if not it returns false.
     * If all checks pass, it performs the activity by changing the player's metrics based on the effects of the activity.
     * If the activity is sleeping, it resets the game time to the start of the next day.
     * Finally, it updates the time label with the current day and hour, and returns true to indicate that the activity was performed successfully.
     *
     * @param actionMapObject The {@link ActivityMapObject} that represents the activity to be performed.
     * @return A boolean indicating whether the activity was performed successfully.
     */
    private boolean doActivity(@NotNull ActivityMapObject actionMapObject) {
        // Get the type of the activity from the ActivityMapObject
        Activity type = actionMapObject.getType();

        // Check if the game is at the end of the day and if the activity is not sleeping
        // If it is, return false to indicate that the activity cannot be performed
        if (gameTime.isEndOfDay() && !type.equals(Activity.SLEEP)) return false;

        // Get the required time for the activity from the ActivityMapObject
        int requiredTime = actionMapObject.getTime();

        // Check if the current hour plus the required time for the activity is greater than the length of the day
        // and if the activity is not sleeping
        // If it is, return false to indicate that the activity cannot be performed
        if (gameTime.getCurrentHour() + requiredTime > GameTime.getDayLength() && !type.equals(Activity.SLEEP))
            return false;

        // Get the effects of the activity from the ActivityMapObject
        // These effects represent how the activity will change the player's metrics
        List<Pair<PlayerMetrics.MetricType, PlayerMetrics.MetricEffect>> effects = type.getEffects();

        // Filter the effects to get only the negative effects
        // Negative effects are those that decrease a player metric
        List<Pair<PlayerMetrics.MetricType, PlayerMetrics.MetricEffect>> negativeEffects = effects.stream()
                .filter(x -> x.getRight().equals(PlayerMetrics.MetricEffect.DECREASE))
                .collect(Collectors.toList());

        // Get the player's current metrics
        PlayerMetrics metrics = player.getMetrics();
        // Check if there are any negative effects from the activity
        if (!negativeEffects.isEmpty()) {
            // Initialize a boolean to track if the player has enough resources for the activity
            boolean hasEnough = true;

            // Iterate over the negative effects
            for (Pair<PlayerMetrics.MetricType, PlayerMetrics.MetricEffect> negativeEffect : negativeEffects) {
                // Get the type of the metric
                PlayerMetrics.MetricType metricType = negativeEffect.getLeft();

                // Get the amount by which the activity changes the metric
                float changeAmount = actionMapObject.getChangeAmount(metricType);

                // Get the current value of the metric for the player
                PlayerMetric metric = metrics.getMetric(metricType);
                float currentMetric = metric.get();

                // Check if the player has enough of the metric for the activity
                hasEnough = currentMetric >= changeAmount;

                // If the player does not have enough of the metric, break the loop
                if (!hasEnough) break;
            }

            // If the player does not have enough resources to perform the activity, return false
            if (!hasEnough) return false;
        }
        // Check if the activity is sleeping
        if (type.equals(Activity.SLEEP)) {
            // Get all player metrics
            List<PlayerMetric> allMetrics = metrics.getMetrics();
            // Iterate over all player metrics
            for (PlayerMetric m : allMetrics) {
                // Increase the total of each metric by its current value
                m.increaseTotal(m.get());
            }
            // Check if the current day plus one equals the total number of days
            if (gameTime.isEndOfDays()) {
                // If it does, transition the screen to the end screen and return true CHANGE pass arguments
                game.transitionScreen(Screens.END, player.getMetrics(), player.getStreaks());
                return true;
            } else {
                // If it doesn't, increment the current day
                gameTime.incrementDay();
                player.getStreaks().nextDay();
            }
        } else {
            // If the activity is not sleeping, increment the current hour by the required time for the activity
            gameTime.incrementHour(requiredTime);
        }
        // Iterate over the effects of the activity CHANGE move this to fix a bug with score calculation
        for (Pair<PlayerMetrics.MetricType, PlayerMetrics.MetricEffect> effect : effects) {
            // Get the type of the metric from the effect
            PlayerMetrics.MetricType metricType = effect.getLeft();
            // Get the effect on the metric (increase or decrease)
            PlayerMetrics.MetricEffect metricEffect = effect.getRight();
            // Get the amount by which the activity changes the metric
            float changeAmount = actionMapObject.getChangeAmount(metricType);
            // Apply the effect to the metric
            metrics.changeMetric(metricType, metricEffect, changeAmount);
        }
        // Increment the streak CHANGE new functionality
        player.getStreaks().increaseStreak(type == Activity.STUDY ? "study" : actionMapObject.getStr());
        // Get the current hour as a string using the getCurrentHourString method
        String currentHour = getCurrentHourString();

        // Construct a string representing the current day by adding 1 to the current day from gameTime
        String currentDay = "Day " + (gameTime.getCurrentDay() + 1);

        // Construct a string representing the current time by concatenating the current day and current hour
        String time = currentDay + " " + currentHour;

        // Set the text of the timeLabel to the constructed time string
        timeLabel.setText(time);

        // Return true indicating the operation was successful
        return true;
    }

    // spotless:off
    /* === UNUSED INPUT METHODS === */
    @Override
    public boolean keyUp(int keycode) {
        return player.keyUp(keycode);
    }
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    // spotless:on
}
