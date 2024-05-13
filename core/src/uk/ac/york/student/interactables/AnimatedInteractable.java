package uk.ac.york.student.interactables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import lombok.Setter;

/**
 * The AnimatedInteractable class extends the Interactable class, adding functionality for animations.
 */
@Getter
public class AnimatedInteractable extends Interactable {
    private final float frameDuration;
    private Animation<TextureRegion> animation;
    private float stateTime;

    @Setter
    private boolean animating;

    /**
     * Constructor for the AnimatedInteractable class.
     *
     * @param x The x position of the class.
     * @param y The y position of the class.
     * @param width The width of the class.
     * @param height The height of the class.
     * @param frameDuration The duration (in seconds) each frame should last in the animation.
     */
    public AnimatedInteractable(float x, float y, float width, float height, float frameDuration) {
        super(x, y, width, height);
        this.frameDuration = frameDuration;
        stateTime = 0f;
        animating = false;
    }

    /**
     * Sets the atlas regions for the animation of the AnimatedInteractable.
     *
     * @param animationRegions An {@link Array<>} of {@link TextureAtlas.AtlasRegion}.
     */
    public void setAnimationRegions(Array<TextureAtlas.AtlasRegion> animationRegions) {
        this.animation = new Animation<>(frameDuration, animationRegions, Animation.PlayMode.NORMAL);
        super.setRegion(animation.getKeyFrame(stateTime));
    }

    /**
     * Activates the animation.
     */
    public void animate() {
        stateTime += Gdx.graphics.getDeltaTime();
        if (stateTime > animation.getFrameDuration() * animation.getKeyFrames().length) {
            stateTime = 0;
            animating = false;
        }
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);
        super.setRegion(currentFrame);
        super.draw();
    }
}
