package io.github.eylexlive.leaderboards.leaderboard.display;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardStat;
import io.github.eylexlive.leaderboards.util.ColorUtil;
import io.github.eylexlive.leaderboards.util.ReplaceUtil;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class HologramDisplay implements ILeaderboardDisplay {

    @Override
    public void setup(Leaderboard leaderboard) {
        if (!plugin.isHdEnabled()) return;

        final Location clone = (Location) ConfigUtil.get("hologram-locations." + leaderboard.getLeaderboard() + ".location");
        if (clone == null)
            return;

        final List<String> stringList = leaderboard.getStringListMap().get("holoLines");
        for (int i = 1; i <= leaderboard.getLimit(); i++) {
            final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, i, true);
            ReplaceUtil.replacePlh(
                    stringList,
                    "lb_" + i + ":" + stat.getName(),
                    "lb_" + i + "_value:" + stat.getValue()
            );
        }

        final Hologram hologram = plugin.getLeaderboardManager().getHologramByLocation(clone);

        if (stringList.size() < 1) {
            Arrays.asList("&4&LKURULUM BAŞARISIZ!", "&cHologram satırları", "&cConfigde bulunamadı!").forEach(str -> hologram.appendTextLine(ColorUtil.translate(str)));
        } else {
            if (hologram.size() < 1)
                for (String str: stringList)
                    hologram.appendTextLine(ColorUtil.translate(str));
            else
                for (String str: stringList)
                    ((TextLine) hologram.getLine(stringList.indexOf(str))).setText(ColorUtil.translate(str));
        }
    }

    @Override
    public boolean setup(Leaderboard leaderboard, Location location, int index) {
        if (!plugin.isHdEnabled()) throw new IllegalStateException("HolographicDisplays not found!");

        if (ConfigUtil.get("hologram-locations." + leaderboard.getLeaderboard() +".location") != null)
            return false;

        final List<String> stringList = leaderboard.getStringListMap().get("holoLines");
        for (int i = 1; i <= leaderboard.getLimit(); i++) {
            final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, i, true);
            ReplaceUtil.replacePlh(
                    stringList,
                    "lb_" + i + ":" + stat.getName(),
                    "lb_" + i + "_value:" + stat.getValue()
            );
        }

        final Hologram hologram = HologramsAPI.createHologram(plugin, location);

        if (stringList.size() < 1)
            Arrays.asList("&4&LKURULUM BAŞARISIZ!", "&cHologram satırları", "&cConfigde bulunamadı!").forEach(str -> hologram.appendTextLine(ColorUtil.translate(str)));
        else
            for (String str: stringList) hologram.appendTextLine(ColorUtil.translate(str));

        ConfigUtil.set("hologram-locations." + leaderboard.getLeaderboard() + ".location", hologram.getLocation());
        return true;
    }

    @Override
    public void unSetup(Leaderboard leaderboard) {
        final Location location = (Location) ConfigUtil.get("hologram-locations." + leaderboard.getLeaderboard() + ".location");
        if (location != null)
            plugin.getLeaderboardManager().getHologramByLocation(location).delete();

        ConfigUtil.set("hologram-locations." + leaderboard.getLeaderboard() + ".location", null);
    }

    @Override
    public void unSetup(Leaderboard leaderboard, int index) {

    }
}
