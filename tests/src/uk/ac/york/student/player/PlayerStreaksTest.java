package uk.ac.york.student.player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlayerStreaksTest {
    @Test
    public void testStreakAchievements() {
        var playerStreaks = new PlayerStreaks();
        for (int i = 0; i < 7; i++) {
            playerStreaks.increaseStreak("study");
            playerStreaks.nextDay();
        }
        assertEquals(playerStreaks.getAchievements().get(0), "Study every day");
    }
}
