package uk.ac.york.student.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import uk.ac.york.student.game.activities.Streak;

/**
 * Class to monitor streaks achieved by the player. These streaks factor into the final score.
 */
// CHANGE new class
@Getter
public class PlayerStreaks {
    private final Map<String, Streak> streaks;

    public PlayerStreaks() {
        streaks = new HashMap<>();
        streaks.put("study", new Streak("Study every day"));
        streaks.put("feed the ducks", new Streak("Feed the ducks every day"));
        streaks.put("head to town", new Streak("Go into town every day"));
    }

    /**
     * Method to call the method nextDay() in each streak in the streaks map.
     */
    public void nextDay() {
        for (Streak streak : streaks.values()) {
            streak.nextDay();
        }
    }

    /**
     * Method to increase the streak on the given activity, given the activity has a streak.
     * @param activity A String which represents the activity the streak is for.
     */
    public void increaseStreak(String activity) {
        if (streaks.containsKey(activity)) {
            streaks.get(activity).increaseStreak();
        }
    }

    /**
     * Method to get the achievements the player has achieved. This method should be run when the score is calculated.
     * Achievements refer to streaks that have reached seven days.
     * @return A list of strings of the name of the streaks that have reached seven days (achievements).
     */
    public ArrayList<String> getAchievements() {
        var achievements = new ArrayList<String>();
        for (Streak streak : streaks.values()) {
            if (streak.getStreak() >= 7) {
                achievements.add(streak.getActivity());
            }
        }
        return achievements;
    }
}
