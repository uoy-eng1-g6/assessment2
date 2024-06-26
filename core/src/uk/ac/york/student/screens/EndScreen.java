package uk.ac.york.student.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import uk.ac.york.student.GdxGame;
import uk.ac.york.student.assets.skins.SkinManager;
import uk.ac.york.student.assets.skins.Skins;
import uk.ac.york.student.audio.sound.GameSound;
import uk.ac.york.student.audio.sound.SoundManager;
import uk.ac.york.student.audio.sound.Sounds;
import uk.ac.york.student.player.PlayerMetrics;
import uk.ac.york.student.player.PlayerStreaks;
import uk.ac.york.student.score.ScoreManager;
import uk.ac.york.student.settings.DebugScreenPreferences;
import uk.ac.york.student.settings.GamePreferences;

@Getter
public class EndScreen extends BaseScreen {
    private final Skin skin = SkinManager.getSkin(Skins.CRAFTACULAR);

    private final Stage processor;
    private final Table table;

    private final GameSound buttonClick = SoundManager.getInstance().getSound(Sounds.BUTTON_CLICK);

    private final PlayerMetrics playerMetrics;
    private final PlayerStreaks playerStreaks;

    private final TextField nameInput;
    private String playerName = "";

    public EndScreen(GdxGame game, boolean shouldFadeIn, float fadeInTime, Object @NotNull [] args) {
        super(game);
        processor = new Stage(new ScreenViewport());

        // CHANGE get metrics and streaks from args
        nameInput = new TextField("", skin);
        nameInput.setMaxLength(12);

        table = new Table();
        table.setDebug(((DebugScreenPreferences) GamePreferences.DEBUG_SCREEN.getPreference()).isEnabled());
        table.setFillParent(true);
        table.pad(20);
        processor.addActor(table);

        playerMetrics = (PlayerMetrics) args[0];
        playerStreaks = (PlayerStreaks) args[1];

        // CHANGE do not calculate the score here, separate functionality into different class

        Gdx.input.setInputProcessor(processor);

        makeNameInputLayout();
    }

    void makeNameInputLayout() {
        table.clear();

        var label = new Label("Enter Name:", skin);
        table.add(label).center();
        table.row();

        table.add(nameInput).width(150).center();
        table.row();

        var submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (nameInput.getText().isEmpty()) {
                    return;
                }

                playerName = nameInput.getText();
                makeScoreDisplayLayout();
            }
        });
        table.add(submitButton).center();
    }

    void makeScoreDisplayLayout() {
        table.clear();

        var isDebug = ((DebugScreenPreferences) GamePreferences.DEBUG_SCREEN.getPreference()).isEnabled();
        // Achievements
        var achievementsTable = new Table();
        achievementsTable.setDebug(isDebug);

        var achievementsTitle = new Label("Achievements", skin);
        achievementsTable.add(achievementsTitle).center().padBottom(20).row();

        var achievements = playerStreaks.getAchievements();
        if (achievements.isEmpty()) {
            var label = new Label("Nothing to see here...", skin);
            label.setFontScale(0.7f);
            achievementsTable.add(label).center().row();
        } else {
            for (var achievement : achievements) {
                var label = new Label(achievement, skin);
                label.setFontScale(0.7f);
                achievementsTable.add(label).center().row();
            }
        }

        // Highscores
        var highScores = ScoreManager.getTop10Scores();

        var highScoresTable = new Table();
        highScoresTable.setDebug(isDebug);

        var highscoresTitle = new Label("Highscores", skin);
        highScoresTable.add(highscoresTitle).center().padBottom(20).row();

        for (var highScore : highScores) {
            var label = new Label(String.valueOf(highScore), skin);
            label.setFontScale(0.7f);
            highScoresTable.add(label).center().row();
        }

        // Score
        var score = ScoreManager.calculateScore(
                playerMetrics.getStudyLevel().getTotal(),
                playerMetrics.getStudyLevel().getMaxTotal(),
                playerMetrics.getHappiness().getTotal(),
                playerMetrics.getHappiness().getMaxTotal(),
                playerStreaks);
        var actualScore = (int) Math.floor(score * 100);
        ScoreManager.saveScore(playerName, actualScore);
        var scoreString = ScoreManager.convertScoreToString(score);

        var scoreTable = new Table();
        scoreTable.setDebug(isDebug);

        var scoreTitle = new Label(
                highScores.isEmpty() || (actualScore > highScores.get(0).getRight()) ? "NEW HIGHSCORE" : "Final Score:",
                skin);
        scoreTable.add(scoreTitle).center().padBottom(20).row();

        var scoreNumberLabel = new Label(String.valueOf(actualScore), skin);
        scoreNumberLabel.setFontScale(0.7f);
        var scoreStringLabel = new Label(scoreString, skin);
        scoreStringLabel.setFontScale(0.7f);

        scoreTable.add(scoreNumberLabel).center().row();
        scoreTable.add(scoreStringLabel).center().row();

        // Add sub-tables to root
        table.add(achievementsTable).center().top().uniformX().space(20);
        table.add(scoreTable).center().top().uniformX().space(20);
        table.add(highScoresTable).center().top().uniformX().space(20);
        table.row();

        // Add UI buttons
        var mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play();
                game.transitionScreen(Screens.MAIN_MENU);
            }
        });
        var quitButton = new TextButton("Exit", skin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClick.play();
                Gdx.app.exit();
            }
        });

        var buttonTable = new Table();
        buttonTable.setDebug(isDebug);

        buttonTable.add(mainMenuButton).padRight(5);
        buttonTable.add(quitButton).padLeft(5);

        table.add(buttonTable).padTop(20).center().colspan(3);
    }

    // CHANGE render method implemented to render end screen
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

    // CHANGE hide method implemented
    @Override
    public void hide() {
        processor.clear();
    }

    // CHANGE dispose method implemented
    @Override
    public void dispose() {
        processor.dispose();
    }
}
