package io.github.eylexlive.leaderboards.leaderboard.display;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardStat;
import io.github.eylexlive.leaderboards.util.ColorUtil;
import io.github.eylexlive.leaderboards.util.ReplaceUtil;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;
import io.github.eylexlive.leaderboards.util.inventory.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ArmorStandDisplay implements ILeaderboardDisplay {

    @Override
    public void setup(Leaderboard leaderboard) {

        for (int i = 1; i <= leaderboard.getLimit(); i++) {
            final ArmorStand armorStand = getArmorStand(leaderboard, i);
            if (armorStand == null)
                continue;

            final List<String> holoLines = new ArrayList<>((List<String>) leaderboard.getObjectMap().get("armorStand_lines"));
            final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, i, true);

            ReplaceUtil.replacePlh(
                    holoLines,
                    "lb_index:" + i,
                    "lb_player:" + stat.getName(),
                    "lb_value:" + stat.getValue()
            );

            final Location location = armorStand.getLocation();
            final Location clonedLocation = location.clone();

            final boolean isSmall =  (boolean) leaderboard.getObjectMap().get("armorStand_small");
            clonedLocation.add(0, (!isSmall ? 3.100 : 2.100),0);

            if (plugin.isHdEnabled()) {
                final Hologram hologram =  plugin.getLeaderboardManager().getHologramByLocation(clonedLocation);
                if (holoLines.size() < 1) {
                    Arrays.asList("&4&LKURULUM BAŞARISIZ!", "&cAS-Holo satırları", "&cConfigde bulunamadı!").forEach(str -> hologram.appendTextLine(ColorUtil.translate(str)));
                } else {
                    if (hologram.size() < 1)
                        for (String str: holoLines)
                            hologram.appendTextLine(ColorUtil.translate(str));
                    else
                        for (String str: holoLines)
                            ((TextLine) hologram.getLine(holoLines.indexOf(str))).setText(ColorUtil.translate(str));
                }
            }

            updateArmorStandHead(armorStand, stat.getName());
        }
    }

    @Override
    public boolean setup(Leaderboard leaderboard, Location location, int index) {
        if (ConfigUtil.get("as-hologram-locations." + leaderboard.getLeaderboard() + ".as" + index) != null)
            return false;

        final List<String> holoLines = new ArrayList<>((List<String>) leaderboard.getObjectMap().get("armorStand_lines"));
        final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, index, true);

        ReplaceUtil.replacePlh(
                holoLines,
                "lb_index:" + index,
                "lb_player:" + stat.getName(),
                "lb_value:" + stat.getValue()
        );

        final ArmorStand armorStand = (ArmorStand) Objects.requireNonNull(
                location.getWorld()).spawnEntity(location, EntityType.ARMOR_STAND
        );

        final boolean isSmall = (boolean) leaderboard.getObjectMap().get("armorStand_small");
        armorStand.setArms(true);
        armorStand.setSmall(isSmall);
        this.updateArmorStandHead(armorStand, stat.getName());

        final Location clonedLocation = armorStand.getLocation().clone();
        clonedLocation.add(0, (!isSmall ? 3.100 : 2.100),0);

        if (plugin.isHdEnabled()) {
            final Hologram hologram =  HologramsAPI.createHologram(plugin, clonedLocation);

            if (holoLines.size() < 1)
                Arrays.asList("&4&LKURULUM BAŞARISIZ!", "&cAS-Holo satırları", "&cConfigde bulunamadı!").forEach(str -> hologram.appendTextLine(ColorUtil.translate(str)));
            else
                for (String str: holoLines) hologram.appendTextLine(ColorUtil.translate(str));
        }


        ConfigUtil.set("as-hologram-locations." + leaderboard.getLeaderboard() + ".as" + index, armorStand.getLocation());
        return true;
    }

    @Override
    public void unSetup(Leaderboard leaderboard, int index) {
        final ArmorStand armorStand = getArmorStand(leaderboard, index);

        if (armorStand != null)
            armorStand.remove();

        final Location location = (Location) ConfigUtil.get("as-hologram-locations." + leaderboard.getLeaderboard() + ".as" + index);
        if (location != null) {
            final Location clonedLocation = location.clone();
            final boolean isSmall =  (boolean) leaderboard.getObjectMap().get("armorStand_small");
            clonedLocation.add(0, (!isSmall ? 3.100 : 2.100),0);

            if (plugin.isHdEnabled())
                plugin.getLeaderboardManager().getHologramByLocation(clonedLocation).delete();
        }

        ConfigUtil.set("as-hologram-locations." + leaderboard.getLeaderboard() + ".as" + index, null);
    }

    private ArmorStand getArmorStand(Leaderboard leaderboard, int index) {
        final List<ArmorStand> armorStandList = new ArrayList<>();
        final Location location = (Location) ConfigUtil.get("as-hologram-locations." + leaderboard.getLeaderboard() + ".as" + index);

        if (location == null)
            return null;

        final Location clonedLocation = location.clone();

        clonedLocation.getWorld().getNearbyEntities(clonedLocation, 0.5D, 3.0D, 0.55D)
                .stream()
                .filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND))
                .forEach(entity -> armorStandList.add((ArmorStand) entity));

        if (armorStandList.size() != 1)
            return null;
        return armorStandList.get(0);
    }

    private void updateArmorStandHead(ArmorStand armorStand, String playerName) {
        final ItemStack itemStack = ItemUtil.createItem(Material.getMaterial("SKULL_ITEM"), "PLAYER_HEAD", " ", 1, (short) SkullType.PLAYER.ordinal());
        final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

        meta.setOwner(playerName);
        itemStack.setItemMeta(meta);
        armorStand.setHelmet(itemStack);
    }

    @Override
    public void unSetup(Leaderboard leaderboard) {

    }
}
