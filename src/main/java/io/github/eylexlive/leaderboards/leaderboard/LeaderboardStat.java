package io.github.eylexlive.leaderboards.leaderboard;

public class LeaderboardStat {

    private final String name;

    private final String value;

    public LeaderboardStat(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
