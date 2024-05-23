package uk.ac.york.student.game.activities;

import org.junit.Test;

public class StreakTest {
    @Test
    public void emptyStreakTest() {
        var streak = new Streak("test");
        for (int i = 0; i < 7; i++) {
            streak.nextDay();
        }
        assert streak.getStreak() == 0;
    }

    @Test
    public void sevenStreakTest() {
        var streak = new Streak("test");
        for (int i = 0; i < 7; i++) {
            streak.increaseStreak();
            streak.nextDay();
        }
        assert streak.getStreak() == 7;
    }

    @Test
    public void resetStreakTest() {
        var streak = new Streak("test");
        for (int i = 0; i < 7; i++) {
            streak.increaseStreak();
            streak.nextDay();
        }
        streak.nextDay();
        assert streak.getStreak() == 0;
    }
}
