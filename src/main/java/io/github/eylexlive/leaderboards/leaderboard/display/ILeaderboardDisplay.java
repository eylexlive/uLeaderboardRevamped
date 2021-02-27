package io.github.eylexlive.leaderboards.leaderboard.display;

import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.uLeaderboards;
import org.bukkit.Location;

public interface ILeaderboardDisplay {

    uLeaderboards plugin = uLeaderboards.getInstance();

    void setup(Leaderboard leaderboard);

    boolean setup(Leaderboard leaderboard, Location location, int index);

    void unSetup(Leaderboard leaderboard);

    void unSetup(Leaderboard leaderboard, int index);

}
