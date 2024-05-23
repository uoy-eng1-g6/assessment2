package uk.ac.york.student.interactables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The AnimatedInteractable class extends the Interactable class, adding functionality for animations.
 */
// CHANGE new class
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
     * @param interactInfo A list of the interactInfo from Tiled. Must be of 4 items: x, y, width, height. This
     *                     information is stored in Tiled as a string as so: interactInfo: x,y,width,height
     */
    public AnimatedInteractable(List<Float> interactInfo) {
        super(interactInfo);
        // the duration for the animations, since there are three frames and the fade time is 0.5, 0.5/3=0.167.
        this.frameDuration = 0.167f;
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
