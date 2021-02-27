package io.github.eylexlive.leaderboards.util;

import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardManager;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardStat;
import io.github.eylexlive.leaderboards.uLeaderboards;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders extends PlaceholderExpansion {
    private final uLeaderboards plugin;

    private final Pattern highNamePattern;
    private final Pattern highValuePattern;
    private final Pattern highValueFormattedPattern;
    private final Pattern positionPattern;

    public Placeholders(uLeaderboards plugin) {
        this.highNamePattern = Pattern.compile("(.*)_([1-9][0-9]*)_name");
        this.highValuePattern = Pattern.compile("(.*)_([1-9][0-9]*)_value");
        this.highValueFormattedPattern =  Pattern.compile("(.*)_([1-9][0-9]*)_value_formatted");
        this.positionPattern = Pattern.compile("(.*)_position");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        final LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();

        final Matcher highNameMatcher = highNamePattern.matcher(identifier);

        if (highNameMatcher.find()) {
            final Leaderboard leaderboardEntry = leaderboardManager.getLeaderboardByName(highNameMatcher.group(1));
            if (leaderboardEntry == null || !NumberUtils.isNumber(highNameMatcher.group(2)))
                return "Invalid placeholder or leaderboard.";
            final int i = Integer.parseInt(highNameMatcher.group(2));
            final LeaderboardStat stat = leaderboardManager.getStat(leaderboardEntry, i, false);
            return stat.getName();
        }

        final Matcher highValueMatcher = highValuePattern.matcher(identifier);

        if (highValueMatcher.find()) {
            final Leaderboard leaderboardEntry = leaderboardManager.getLeaderboardByName(highValueMatcher.group(1));
            if (leaderboardEntry == null || !NumberUtils.isNumber(highValueMatcher.group(2)))
                return "Invalid placeholder or leaderboard.";
            final int i = Integer.parseInt(highValueMatcher.group(2));
            final LeaderboardStat stat = leaderboardManager.getStat(leaderboardEntry, i, false);
            return stat.getValue();
        }

        final Matcher highValueFormattedMatcher = highValueFormattedPattern.matcher(identifier);

        if (highValueFormattedMatcher.find()) {
            final Leaderboard leaderboardEntry = leaderboardManager.getLeaderboardByName(highValueFormattedMatcher.group(1));
            if (leaderboardEntry == null || !NumberUtils.isNumber(highValueFormattedMatcher.group(2)))
                return "Invalid placeholder or leaderboard.";
            final int i = Integer.parseInt(highValueFormattedMatcher.group(2));
            final LeaderboardStat stat = leaderboardManager.getStat(leaderboardEntry, i, true);
            return stat.getValue();
        }

        final Matcher positionMatcher = positionPattern.matcher(identifier);

        if (positionMatcher.find()) {
            final Leaderboard leaderboardEntry = leaderboardManager.getLeaderboardByName(positionMatcher.group(1));
            if (leaderboardEntry == null)
                return "Invalid leaderboard.";
            return String.valueOf(leaderboardManager.getPosition(player, leaderboardEntry));
        }

        return null;
    }
    @Override
    public boolean register(){
        return super.register();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "EylexLive";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "uleaderboards";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
