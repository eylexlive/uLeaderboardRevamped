package io.github.eylexlive.leaderboards.leaderboard.display;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardStat;
import io.github.eylexlive.leaderboards.util.ColorUtil;
import io.github.eylexlive.leaderboards.util.ReplaceUtil;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NpcDisplay implements ILeaderboardDisplay {

    @Override
    public void setup(Leaderboard leaderboard) {
        if (!plugin.isCitizensEnabled()) return;

        for (int i = 1; i <= leaderboard.getLimit(); i++) {
            final Location location = (Location) ConfigUtil.get("npc-hologram-locations." + leaderboard.getLeaderboard() + ".loc" + i);
            if (location == null)
                continue;

            final List<String> holoLines = new ArrayList<>(leaderboard.getStringListMap().get("npcHoloLines"));
            final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, i, true);

            ReplaceUtil.replacePlh(
                    holoLines,
                    "lb_index:" + i,
                    "lb_player:" + stat.getName(),
                    "lb_value:" + stat.getValue()
            );

            if (plugin.isHdEnabled()) {
                final Hologram hologram = plugin.getLeaderboardManager().getHologramByLocation(location);

                if (holoLines.size() < 1) {
                    Arrays.asList("&4&LKURULUM BAŞARISIZ!", "&cNPC-Holo satırları", "&cConfigde bulunamadı!").forEach(str -> hologram.appendTextLine(ColorUtil.translate(str)));
                } else {
                    if (hologram.size() < 1)
                        for (String str: holoLines)
                            hologram.appendTextLine(ColorUtil.translate(str));
                    else
                        for (String str: holoLines)
                            ((TextLine) hologram.getLine(holoLines.indexOf(str))).setText(ColorUtil.translate(str));
                }
            }

            final NPC npc = CitizensAPI.getNPCRegistry().getById(
                    ConfigUtil.getInt("npc-hologram-locations." + leaderboard.getLeaderboard() + ".npc" + i)
            );

            npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, stat.getName());
        }
    }

    @Override
    public boolean setup(Leaderboard leaderboard, Location location, int index) {
        if (!plugin.isCitizensEnabled()) throw  new IllegalStateException("HolographicDisplays or Citizens not found!");

        if (ConfigUtil.get("npc-hologram-locations." + leaderboard.getLeaderboard() + ".loc" + index) != null)
            return false;

        final List<String> holoLines = new ArrayList<>(leaderboard.getStringListMap().get("npcHoloLines"));
        final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, index, true);

        ReplaceUtil.replacePlh(
                holoLines,
                "lb_index:" + index,
                "lb_player:" + stat.getName(),
                "lb_value:" + stat.getValue()
        );

        final NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, leaderboard.getLeaderboard() + "#" + index);
        npc.spawn(location);
        npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, stat.getName());

        final Location clone = npc.getStoredLocation().clone();
        clone.add(0, 2.900,0);

        if (plugin.isHdEnabled()) {
            final Hologram hologram =  HologramsAPI.createHologram(plugin, clone);

            if (holoLines.size() < 1)
                Arrays.asList("&4&LKURULUM BAŞARISIZ!", "&cNPC-Holo satırları", "&cConfigde bulunamadı!").forEach(str -> hologram.appendTextLine(ColorUtil.translate(str)));
            else
                for (String str: holoLines) hologram.appendTextLine(ColorUtil.translate(str));
        }

        ConfigUtil.set("npc-hologram-locations." + leaderboard.getLeaderboard() + ".loc" + index, clone);
        ConfigUtil.set("npc-hologram-locations." + leaderboard.getLeaderboard() + ".npc" + index, npc.getId());
        return true;
    }

    @Override
    public void unSetup(Leaderboard leaderboard, int index) {
        if (!plugin.isCitizensEnabled()) return;
        
        final NPC npc = CitizensAPI.getNPCRegistry().getById(
                ConfigUtil.getInt("npc-hologram-locations." + leaderboard.getLeaderboard() + ".npc" + index)
        );

        final Location location = (Location) ConfigUtil.get("npc-hologram-locations." + leaderboard.getLeaderboard() + ".loc" + index);
        if (location != null && plugin.isHdEnabled())
            plugin.getLeaderboardManager().getHologramByLocation(location).delete();

        if (npc != null)
            npc.despawn();

        ConfigUtil.set("npc-hologram-locations." + leaderboard.getLeaderboard() + ".loc" + index, null);
        ConfigUtil.set("npc-hologram-locations." + leaderboard.getLeaderboard() + ".npc" + index, null);
    }

    @Override
    public void unSetup(Leaderboard leaderboard) {

    }
}
