package uk.ac.york.student.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.york.student.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class ScoreManagerTest {
    @Test
    public void initScoresDatabaseExecutesCorrectQuery() throws SQLException {
        var stmt = mock(PreparedStatement.class);
        var conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);

        try (var mockScoreManager = mockStatic(ScoreManager.class)) {
            mockScoreManager.when(ScoreManager::getConnection).thenReturn(conn);
            mockScoreManager.when(ScoreManager::initScoresDatabase).thenCallRealMethod();

            ScoreManager.initScoresDatabase();

            verify(conn, times(1)).prepareStatement(ScoreManager.SCHEMA);
        }
    }

    @Test
    public void saveScoreAddsScoreToDatabase() throws SQLException {
        var stmt = mock(PreparedStatement.class);
        var conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);

        try (var mockScoreManager = mockStatic(ScoreManager.class)) {
            mockScoreManager.when(ScoreManager::getConnection).thenReturn(conn);
            mockScoreManager
                    .when(() -> ScoreManager.saveScore(anyString(), anyInt()))
                    .thenCallRealMethod();

            ScoreManager.saveScore("foo", 10);

            verify(stmt, times(1)).setString(1, "foo");
            verify(stmt, times(1)).setInt(2, 10);
        }
    }

    @Test
    public void getTop10ScoresReturnsCorrectValues() throws SQLException {
        var results = mock(ResultSet.class);

        final var next = new AtomicBoolean(true);
        when(results.next()).thenAnswer(call -> {
            var toReturn = next.get();
            next.set(false);
            return toReturn;
        });
        when(results.getString(anyString())).thenReturn("foo");
        when(results.getInt(anyString())).thenReturn(10);

        var stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(results);
        var conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);

        try (var mockScoreManager = mockStatic(ScoreManager.class)) {
            mockScoreManager.when(ScoreManager::getConnection).thenReturn(conn);
            mockScoreManager.when(ScoreManager::getTop10Scores).thenCallRealMethod();

            var scores = ScoreManager.getTop10Scores();
            assertThat(scores).hasSize(1);
            assertThat(scores.get(0).getLeft()).isEqualTo("foo");
            assertThat(scores.get(0).getRight()).isEqualTo(10);

            verify(results, times(1)).getString(anyString());
            verify(results, times(1)).getInt(anyString());
        }
    }
}
