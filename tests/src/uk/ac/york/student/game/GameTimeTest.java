package uk.ac.york.student.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.york.student.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class GameTimeTest {
    /**
     * Test that after 16 hours it is time for the player to sleep.
     */
    @Test
    public void endOfDayTest() {
        var gameTime = new GameTime();
        gameTime.incrementHour(16);
        assertTrue(gameTime.isEndOfDay());
    }

    /**
     * Test that after sleeping for 6 nights it is the final day.
     */
    @Test
    public void endOfDaysTest() {
        var gameTime = new GameTime();
        for (int i = 0; i < 6; i++) {
            gameTime.incrementDay();
        }
        assertTrue(gameTime.isEndOfDays());
    }

    /**
     * Test that there are 16 hours in the day to meet the requirement FR_SLEEP
     */
    @Test
    public void hoursOfDayTest() {
        assertEquals(GameTime.getDayLength(), 16);
    }
}
