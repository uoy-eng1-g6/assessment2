package uk.ac.york.student.interactables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector4;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Range;

/**
 * The Interactable class represents an object the player can interact with. It helps make it clearer what is
 * interactable by giving it an outline, and allowing that outline to be changed depending on events.
 */
@Getter
public class Interactable {
    private final SpriteBatch batch;
    private final ShaderProgram shaderProgram;
    private Vector4 shaderColor;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final float scale;
    private final Color color;

    @Setter
    private TextureRegion region;

    @Setter
    private OrthographicCamera camera;

    @Setter
    private String map;

    /**
     * Constructor for the Interactable class.
     *
     * @param x The x position of the class.
     * @param y The y position of the class.
     * @param width The width of the class.
     * @param height The height of the class.
     * @param scale The scale that should be applied to the class.
     */
    public Interactable(float x, float y, float width, float height, float scale) {
        this.batch = new SpriteBatch();
        this.x = x * scale;
        this.y = y * scale;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.color = batch.getColor();
        this.map = "map";

        // Uses a custom shader to add an outline to the object.
        String vertexShader = Gdx.files.internal("shaders/vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shaders/fragment.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        batch.setShader(shaderProgram);
    }

    /**
     * Sets the color of the object's outline.
     *
     * @param vec A {@link Vector4} containing red, green, blue, alpha of the outline.
     */
    public void setOutlineColor(Vector4 vec) {
        batch.begin();
        batch.setColor(color);
        shaderProgram.setUniformf("u_color", vec);
        redraw();
        batch.end();
    }

    /**
     * Sets the default color of the object's outline.
     *
     * @param vec A {@link Vector4} containing red, green, blue, alpha of the default outline.
     */
    public void setDefaultOutlineColor(Vector4 vec) {
        shaderColor = vec;
    }

    public void draw() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(color);
        shaderProgram.setUniformf("u_color", shaderColor);
        redraw();
        batch.end();
    }

    /**
     * Sets the alpha, or opacity, of the object.
     * @param a The alpha.
     */
    public void setAlpha(@Range(from = 0, to = 1) float a) {
        color.a = a;
        shaderColor.w = a;
    }

    /**
     * Calls batch.draw(), therefore should only be used in between batch.begin() and batch.end().
     */
    private void redraw() {
        batch.draw(region, x, y, 0f, 0f, width, height, scale, scale, 0);
    }
}
