package io.github.eylexlive.leaderboards.leaderboard.display;

import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardStat;
import io.github.eylexlive.leaderboards.util.ColorUtil;
import io.github.eylexlive.leaderboards.util.ReplaceUtil;
import io.github.eylexlive.leaderboards.util.inventory.InventoryUI;
import io.github.eylexlive.leaderboards.util.inventory.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GuiDisplay implements ILeaderboardDisplay {

    private final Map<Leaderboard, InventoryUI> guiMap = new HashMap<>();

    @Override
    public void setup(Leaderboard leaderboard) {
        final InventoryUI inventory = new InventoryUI(
                ColorUtil.translate((String) leaderboard.getObjectMap().get("gui_title")), (int) leaderboard.getObjectMap().get("gui_rows")
        );

        for (int i = 1; i <= leaderboard.getLimit(); i++) {
            final String material = (String) leaderboard.getObjectMap().get("gui_material");
            if (material == null)
                continue;
            final boolean isSkull = material.equals("{player_skull}");
            final ItemStack itemStack = (
                    (!isSkull ? new ItemStack(Material.getMaterial(material)) : ItemUtil.createItem(Material.getMaterial("SKULL_ITEM"), "PLAYER_HEAD", " ", 1, (short) SkullType.PLAYER.ordinal()))
            );

            final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, i, true);

            final String display = ColorUtil.translate((String) leaderboard.getObjectMap().get("gui_display"))
                    .replace("{lb_index}", String.valueOf(i))
                    .replace("{lb_player}", stat.getName())
                    .replace("{lb_value}", stat.getValue());

            final List<String> lore = ColorUtil.translate((List<String>) leaderboard.getObjectMap().get("gui_lore"));

            if (lore.size() < 1) lore.addAll(ColorUtil.translate(Arrays.asList("", "&cLore gösterilemiyor.", "&cConfigte lore bulunamadı.")));

            ReplaceUtil.replacePlh(
                    lore,
                    "lb_player:" + stat.getName(),
                    "lb_value:" + stat.getValue()
            );

            if (isSkull) {
                final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

                skullMeta.setOwner(stat.getName());
                skullMeta.setDisplayName(display);
                skullMeta.setLore(lore);
                itemStack.setItemMeta(skullMeta);
            } else {
                final ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(display);
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }

            inventory.addItem(new InventoryUI.EmptyClickableItem(itemStack) {});
        }

        guiMap.put(leaderboard, inventory);
    }

    @Override
    public void unSetup(Leaderboard leaderboard) {
        guiMap.remove(leaderboard);
    }

    public InventoryUI getGui(Leaderboard leaderboard) {
        if (guiMap.get(leaderboard) == null)
            setup(leaderboard);
        return guiMap.get(leaderboard);
    }

    @Override
    public boolean setup(Leaderboard leaderboard, Location location, int index) {
        return true;
    }

    @Override
    public void unSetup(Leaderboard leaderboard, int index) {

    }
}
