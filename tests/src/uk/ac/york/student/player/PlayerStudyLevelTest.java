package uk.ac.york.student.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.york.student.GdxTestRunner;
import uk.ac.york.student.assets.skins.SkinManager;
import uk.ac.york.student.assets.skins.Skins;

@RunWith(GdxTestRunner.class)
public class PlayerStudyLevelTest {
    @Test
    public void increaseAndDecreaseBehavesCorrectly() {
        var skin = mock(Skin.class);
        try (var skinManager = mockStatic(SkinManager.class);
                var ignored = mockConstruction(ProgressBar.class)) {
            skinManager.when(() -> SkinManager.getSkin(any(Skins.class))).thenReturn(skin);

            var metric = new PlayerStudyLevel();
            assertThat(metric.get()).isCloseTo(metric.getDefault(), Offset.offset(0.001f));

            metric.increase(0.5f);
            assertThat(metric.get()).isCloseTo(PlayerStudyLevel.PROGRESS_BAR_MINIMUM + 0.5f, Offset.offset(0.001f));

            metric.decrease(0.5f);
            assertThat(metric.get()).isCloseTo(PlayerStudyLevel.PROGRESS_BAR_MINIMUM, Offset.offset(0.001f));

            metric.increase(2);
            assertThat(metric.get()).isOne();

            metric.decrease(2);
            assertThat(metric.get()).isCloseTo(PlayerStudyLevel.PROGRESS_BAR_MINIMUM, Offset.offset(0.001f));
        }
    }
}
