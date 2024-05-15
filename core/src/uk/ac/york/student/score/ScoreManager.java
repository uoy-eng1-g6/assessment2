package uk.ac.york.student.score;

import com.badlogic.gdx.Gdx;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import uk.ac.york.student.player.PlayerStreaks;

@UtilityClass
public class ScoreManager {
    private static final String SCHEMA = ("CREATE TABLE IF NOT EXISTS scores ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "score INTEGER NOT NULL"
            + ");");
    private static final String SAVE_SCORE = "INSERT INTO scores (score) VALUES (?);";
    private static final String GET_TOP_10_SCORES = "SELECT score FROM scores ORDER BY score DESC LIMIT 10;";

    private static Driver driver = null;

    static String getDatabaseUrl() {
        return "jdbc:h2:file:"
                + Gdx.files
                        .absolute(Gdx.files.getExternalStoragePath())
                        .child("LetRonCooke/scores.h2")
                        .path();
    }

    static Connection getConnection() {
        Gdx.files.external("LetRonCooke").mkdirs();
        try {
            if (driver == null) {
                driver = DriverManager.getDriver(getDatabaseUrl());
            }

            var conn = driver.connect(getDatabaseUrl(), null);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initScoresDatabase() {
        try (var conn = getConnection()) {
            conn.prepareStatement(SCHEMA).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveScore(int score) {
        try (var conn = getConnection()) {
            var stmt = conn.prepareStatement(SAVE_SCORE);
            stmt.setInt(1, score);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Integer> getTop10Scores() {
        List<Integer> scores = new ArrayList<>();
        try (var conn = getConnection()) {
            var results = conn.prepareStatement(GET_TOP_10_SCORES).executeQuery();
            while (results.next()) {
                scores.add(results.getInt("score"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return scores;
    }

    public static float calculateScore(
            float studyLevel, float maxStudyLevel, float happiness, float maxHappiness, PlayerStreaks streaks) {
        int maxScore = 100;
        var achievements = streaks.getAchievements();
        int numOfStreaks = streaks.getStreaks().size();
        // reserve percentForStreaks% for streaks
        float percentForStreaks = 24f;
        float achievementsWeighting = percentForStreaks / numOfStreaks;

        float metricWeighting = (maxScore - percentForStreaks) / (maxHappiness + maxStudyLevel);

        float achievementsScore = achievements.size() * achievementsWeighting;
        float studyScore = studyLevel * metricWeighting;
        float happinessScore = happiness * metricWeighting;

        var totalScore = studyScore + happinessScore + achievementsScore;
        return Math.min(100, Math.max(0, totalScore));
    }

    public static String convertScoreToString(float score) {
        if (score >= 70) {
            return "First-class Honours";
        } else if (score >= 60) {
            return "Upper second-class Honours";
        } else if (score >= 50) {
            return "Lower second-class Honours";
        } else if (score >= 40) {
            return "Third-class Honours";
        } else {
            return "Fail";
        }
    }
}
