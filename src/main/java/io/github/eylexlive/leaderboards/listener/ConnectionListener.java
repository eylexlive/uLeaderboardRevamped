package io.github.eylexlive.leaderboards.listener;

import io.github.eylexlive.leaderboards.uLeaderboards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.concurrent.CompletableFuture;

public class ConnectionListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void handleJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        CompletableFuture.runAsync(() -> uLeaderboards.getInstance().getLeaderboardManager().updatePlayer(player));
    }
}
