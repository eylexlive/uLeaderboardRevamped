package io.github.eylexlive.leaderboards.leaderboard;

import io.github.eylexlive.leaderboards.util.config.Config;

import java.util.List;
import java.util.Map;

public class Leaderboard {
    private Config config;

    private String leaderboard, placeholder;

    private Map<String, List<String>> stringListMap;

    private Map<String, Object> objectMap;

    private int limit;

    private boolean hasUpdate;

    public Leaderboard(String leaderboard) {
        this.leaderboard = leaderboard;
    }

    public Leaderboard(String leaderboard, String placeholder, Map<String, List<String>> stringListMap, Map<String, Object> objectMap, int limit)  {
        this.leaderboard = leaderboard;
        this.placeholder = placeholder;
        this.stringListMap = stringListMap;
        this.objectMap = objectMap;
        this.limit = limit;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(String leaderboard) {
        this.leaderboard = leaderboard;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        this.setHasUpdate();
    }

    public Map<String, List<String>> getStringListMap() {
        return stringListMap;
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        this.setHasUpdate();
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    private void setHasUpdate() {
        if (!this.hasUpdate) this.hasUpdate = true;
    }
}
