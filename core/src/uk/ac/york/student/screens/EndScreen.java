package uk.ac.york.student.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import uk.ac.york.student.GdxGame;
import uk.ac.york.student.assets.skins.SkinManager;
import uk.ac.york.student.assets.skins.Skins;
import uk.ac.york.student.player.PlayerMetrics;
import uk.ac.york.student.score.ScoreManager;
import uk.ac.york.student.settings.DebugScreenPreferences;
import uk.ac.york.student.settings.GamePreferences;

@Getter
public class EndScreen extends BaseScreen {
    private final Skin skin = SkinManager.getSkin(Skins.CRAFTACULAR);

    private final Stage processor;

    public EndScreen(GdxGame game) {
        super(game);
        throw new UnsupportedOperationException("This constructor is not supported (must pass in object args!)");
    }

    public EndScreen(GdxGame game, boolean shouldFadeIn, float fadeInTime, Object @NotNull [] args) {
        super(game);
        processor = new Stage(new ScreenViewport());

        var metrics = (PlayerMetrics) args[0];
        var score = ScoreManager.calculateScore(
                metrics.getEnergy().getTotal(),
                metrics.getEnergy().getMaxTotal(),
                metrics.getStudyLevel().getTotal(),
                metrics.getStudyLevel().getMaxTotal(),
                metrics.getHappiness().getTotal(),
                metrics.getHappiness().getMaxTotal());
        var actualScore = (int) Math.floor(score * 100);
        ScoreManager.saveScore(actualScore);
        var scoreString = ScoreManager.convertScoreToString(score);

        var highScores = ScoreManager.getTop10Scores();

        var scoresTable = new Table();
        scoresTable.setFillParent(true);
        scoresTable.setDebug(((DebugScreenPreferences) GamePreferences.DEBUG_SCREEN.getPreference()).isEnabled());

        var scoresTitle = new Label("Highscores", skin);
        scoresTable.right().padRight(25);
        scoresTable.add(scoresTitle).center().padBottom(10).row();

        for (var highScore : highScores) {
            var label = new Label(String.valueOf(highScore), skin);
            label.setFontScale(0.7f);
            scoresTable.add(label).center().row();
        }

        processor.addActor(scoresTable);

        var centerTable = new Table();
        centerTable.setFillParent(true);
        centerTable.setDebug(((DebugScreenPreferences) GamePreferences.DEBUG_SCREEN.getPreference()).isEnabled());

        var title = new Label("Game Over", skin);
        centerTable.center();
        centerTable.pad(0, 50, 0, 50);
        centerTable.add(title).padBottom(20).row();

        var headerLabel = new Label(actualScore >= highScores.get(0) ? "NEW HIGHSCORE" : "Final Score:", skin);
        headerLabel.setFontScale(0.8f);
        var textScoreLabel = new Label(scoreString, skin);
        textScoreLabel.setFontScale(0.7f);
        var scoreLabel = new Label(String.valueOf(actualScore), skin);
        scoreLabel.setFontScale(0.7f);

        centerTable.add(headerLabel).padBottom(5).row();
        centerTable.add(textScoreLabel).row();
        centerTable.add(scoreLabel).padBottom(20).row();

        var mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.transitionScreen(Screens.MAIN_MENU);
            }
        });
        var quitButton = new TextButton("Exit", skin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        centerTable.add(mainMenuButton).fillX().row();
        centerTable.add(quitButton).fillX().row();

        processor.addActor(centerTable);

        Gdx.input.setInputProcessor(processor);
    }

    @Override
    public void show() {}

    @Override
    public void render(float v) {
        ScreenUtils.clear(Color.BLACK);

        processor.act(v);
        processor.draw();
    }

    @Override
    public void resize(int i, int i1) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        processor.dispose();
    }
}
