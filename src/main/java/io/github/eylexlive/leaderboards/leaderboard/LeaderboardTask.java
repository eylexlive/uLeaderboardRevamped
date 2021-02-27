package io.github.eylexlive.leaderboards.leaderboard;

import io.github.eylexlive.leaderboards.uLeaderboards;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LeaderboardTask extends BukkitRunnable {

    private final uLeaderboards plugin;
    private final LeaderboardManager leaderboardManager;

    private final List<Leaderboard> leaderboardList;
    private Leaderboard loadedBoard;

    private boolean settingUp = false;
    private int sizeOfTrials, leaderboardSize;

    public LeaderboardTask(uLeaderboards plugin) {
        this.plugin = plugin;
        this.leaderboardManager = plugin.getLeaderboardManager();
        this.leaderboardList = leaderboardManager.getBoardsFromDatabase();
        this.leaderboardSize = leaderboardList.size();
    }

    @Override
    public void run() {
        if (leaderboardSize <= 0) {
            cancel();
            plugin.getLogger().info(
                    "[l] Successfully loaded " + leaderboardList.size() + " leaderboards!"
            );
            return;
        }

        if (!settingUp) {
            final Leaderboard leaderboard = leaderboardList.get(leaderboardSize - 1);
            plugin.getLogger().info(
                    "[l] Queued for leaderboard named: " + leaderboard.getLeaderboard()
            );
            settingUp = true;

            CompletableFuture.runAsync(() ->  {
                loadedBoard = leaderboardManager.loadBoard(leaderboard.getLeaderboard());
            }).whenComplete( (future, err) -> {
                if (err != null) {
                    plugin.getLogger().warning(
                            "[e] An error occurred while loading the leaderboard: " + leaderboard.getLeaderboard()
                    );
                    err.printStackTrace();
                } else {
                    leaderboardManager.setupDisplays(loadedBoard);
                    plugin.getLogger().info(
                            "[l] Leaderboard " + leaderboard.getLeaderboard() + " loaded!"
                    );
                }
                settingUp = false;
                leaderboardSize--;
            });

        } else {
            if (sizeOfTrials > 4) {
                cancel();
                plugin.getLogger().warning(
                        "[e] Task cancelled because server cannot be reaching!"
                );
                return;
            }

            plugin.getLogger().info(
                    "[l] Waiting for server's turn..."
            );
            sizeOfTrials++;
        }
    }
}
