package io.github.eylexlive.leaderboards.command;

import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.uLeaderboards;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderboardTabCompleter implements TabCompleter {

    private final uLeaderboards plugin;

    public LeaderboardTabCompleter(uLeaderboards plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp())
            return new ArrayList<>();
        if (args.length <= 1)
            return sender.isOp() ?
                    Arrays.asList(
                            "manage", "create",
                            "delete", "holo",
                            "npc", "as",
                            "sign", "gui",
                            "book", "position",
                            "reload", "reloadConfig"
                    )
                    :
                    Arrays.asList(
                            "gui",
                            "book",
                            "position"
                    );
        if (args.length == 2) {
            final List<String> list = new ArrayList<>();
            final List<Leaderboard> leaderboardEntries = plugin.getLeaderboardManager().getBoardsFromList();

            leaderboardEntries.forEach(leaderboardEntry -> list.add(leaderboardEntry.getLeaderboard()));
            switch (args[0]) {
                case "holo":
                case "npc":
                case "as":
                case "sign":
                case "gui":
                case "book":
                case "position":
                case "delete":
                    return list;
            }
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}
