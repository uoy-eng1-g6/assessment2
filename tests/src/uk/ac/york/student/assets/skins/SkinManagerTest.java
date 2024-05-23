package uk.ac.york.student.assets.skins;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.york.student.GdxTestRunner;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GdxTestRunner.class)
public class SkinManagerTest {
    @Test
    public void getSkinReturnsSameObjectIfCalledTwice() {
        var oldFiles = Gdx.files;

        var mockFiles = mock(Files.class);
        when(mockFiles.internal(anyString())).then(call -> oldFiles.absolute(new File("../assets/" + call.getArgument(0)).getAbsolutePath()));

        try {
            Gdx.files = mockFiles;

            var skin = SkinManager.getSkin(Skins.CRAFTACULAR);
            var skin2 = SkinManager.getSkin(Skins.CRAFTACULAR);

            assertThat(skin).isSameAs(skin2);
        } finally {
            Gdx.files = oldFiles;
            SkinManager.dispose();
        }
    }
}
