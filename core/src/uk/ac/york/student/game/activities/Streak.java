package uk.ac.york.student.game.activities;

import lombok.Getter;

/**
 * Class which represents a streak of activities.
 */
@Getter
public class Streak {
    /**
     * A boolean value used to lock the value of streak for if the player does the activity twice in one day.
     */
    boolean daily;
    /**
     * An int which tracks the length of the streak.
     */
    int streak;

    final String activity;

    /**
     * Constructor for the Streak class.
     *
     * @param activity A string which is used when referring to the streak.
     */
    public Streak(String activity) {
        this.activity = activity;
        daily = false;
        streak = 0;
    }

    /**
     * Method to reset the streak back to zero.
     */
    private void resetStreak() {
        streak = 0;
    }

    /**
     * Method to increase the streak by 1. Only works if the activity has not been completed on this day.
     */
    public void increaseStreak() {
        if (!daily) {
            streak++;
        }
        daily = true;
    }

    /**
     * Method to alert the streak that it should be moved onto the next day.
     */
    public void nextDay() {
        if (!daily) {
            resetStreak();
        }
        daily = false;
    }
}
