package io.github.eylexlive.leaderboards.leaderboard;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.eylexlive.leaderboards.leaderboard.display.*;
import io.github.eylexlive.leaderboards.uLeaderboards;
import io.github.eylexlive.leaderboards.util.CommaUtil;
import io.github.eylexlive.leaderboards.util.config.Config;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;
import io.github.eylexlive.leaderboards.util.inventory.InventoryUI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LeaderboardManager {

    private final uLeaderboards plugin;

    private final Map<Leaderboard, Map<String, Long>> leaderboardMap = new HashMap<>();
    private final Map<String, Leaderboard> leaderboardList = new HashMap<>();

    private InventoryUI manageInventory;

    public LeaderboardManager(uLeaderboards plugin) {
        this.plugin = plugin;
    }

    public boolean createBoard(String leaderboard) {
        if (leaderboardList.get(leaderboard) != null)
            return false;

        final boolean success = plugin.getDataBase().update(
                plugin.isMYSQLEnabled() ? "create table if not exists " + ConfigUtil.getString("mysql.table-prefix") + leaderboard + " (id TEXT, value NUMERIC)"
                        : "create table if not exists " + leaderboard + " (id TEXT PRIMARY KEY, value NUMERIC)"
        );

        leaderboardList.put(leaderboard, loadBoard(leaderboard));
        return success;
    }

    public boolean deleteBoard(String leaderboard) {
        if (leaderboardList.get(leaderboard) == null)
            return false;

        final boolean success = plugin.getDataBase().update(
                "drop table " + leaderboard,
                plugin.isMYSQLEnabled() ? leaderboard : null
        );

        leaderboardMap.remove(leaderboardList.get(leaderboard));
        leaderboardList.remove(leaderboard);
        return success;
    }

    public Leaderboard loadBoard(String leaderboard) {
        final Leaderboard validate = getLeaderboardByName(leaderboard);
        if (validate != null && validate.hasUpdate())
            saveBoard(validate);

        final Config config = new Config(leaderboard);

        final String placeholder = config.getString("placeholder");
        final int limit = config.getInt("limit");

        final Map<String, List<String>> stringListMap = new HashMap<>();
        stringListMap.put("holoLines", config.getStringList("holoLines"));
        stringListMap.put("npcHoloLines", config.getStringList("npcHoloLines"));
        stringListMap.put("bookPages", config.getStringList("bookPages"));
        stringListMap.put("signLines", config.getStringList("signLines"));

        final Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("armorStand_small", config.getBoolean("armorStandSmall"));
        objectMap.put("armorStand_lines", config.getStringList("armorStandHoloLines"));

        objectMap.put("gui_rows", config.getInt("guiRows"));
        objectMap.put("gui_title", config.getString("guiTitle"));
        objectMap.put("gui_material", config.getString("guiMaterial"));
        objectMap.put("gui_display", config.getString("guiDisplay"));
        objectMap.put("gui_lore", config.getStringList("guiLore"));


        final Leaderboard loadedBoard = new Leaderboard(
                leaderboard,
                placeholder,
                stringListMap,
                objectMap,
                limit
        );
        loadedBoard.setConfig(config);

        leaderboardList.put(leaderboard, loadedBoard);
        leaderboardMap.put(loadedBoard, reverse(getPlayers(loadedBoard)));

        return loadedBoard;
    }

    private void saveBoard(Leaderboard leaderboard) {
        final Config config = leaderboard.getConfig();

        config.set("placeholder", leaderboard.getPlaceholder());
        config.set("limit", leaderboard.getLimit());
        config.save();
    }

    public void saveBoards() {
        getBoardsFromList().stream().filter(Leaderboard::hasUpdate).forEach(this::saveBoard);
    }

    public void setupDisplays(Leaderboard leaderboard) {
        final NpcDisplay npc = new NpcDisplay();
        final HologramDisplay holo = new HologramDisplay();
        final ArmorStandDisplay as = new ArmorStandDisplay();
        final SignDisplay sign = new SignDisplay();

        final ILeaderboardDisplay[] displays = {
                holo, as, npc, sign
        };

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (ILeaderboardDisplay display : displays)
                display.setup(leaderboard);
        }, 10L);
    }

    public int getPosition(Player player, Leaderboard leaderboard) {
        return new ArrayList<>(getLeaderboard(leaderboard)
                .keySet())
                .indexOf(player.getName()) + 1;
    }

    public LeaderboardStat getStat(Leaderboard leaderboard, int index, boolean comma) {
        final Map<String, Long> map = getLeaderboard(leaderboard);
        final boolean isNull = index > map.size();
        final String name = (
                !isNull ? (String) map.keySet().toArray()[index - 1] : "----"
        );
        final long value = (
                !isNull ? map.get(name) : 0
        );

        return new LeaderboardStat(
                name,
                String.valueOf(comma ? CommaUtil.comma(value) : value)
        );
    }

    public void update(Leaderboard leaderboard, Player player) {
        final String outputRaw = PlaceholderAPI.setPlaceholders(player, "%" + leaderboard.getPlaceholder() + "%");
        final long output;

        try {
            output= Long.parseLong(outputRaw);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("The placeholder " + outputRaw + " is not valid!" + " /papi ecloud download <placeholder>");
            return;
        }

        if (!plugin.getDataBase().validateData(
                "select * from " + leaderboard.getLeaderboard() + " where id = '"+ player.getName() + "';" ,
                plugin.isMYSQLEnabled() ? leaderboard.getLeaderboard() : null)
        ) {
            plugin.getDataBase().update(
                    "insert into " + leaderboard.getLeaderboard() + " (id, value) values ('" + player.getName() + "', '" + output + "')",
                    plugin.isMYSQLEnabled() ? leaderboard.getLeaderboard() : null
            );

        } else {
            plugin.getDataBase().update(
                    "update " + leaderboard.getLeaderboard() + " set value=" + output + " where id='" + player.getName() + "'",
                    plugin.isMYSQLEnabled() ? leaderboard.getLeaderboard() : null
            );
        }
    }

    public Map<String, Long> getPlayers(Leaderboard leaderboard) {
        final Map<String, Long> map = new HashMap<>();
        final CompletableFuture<Map<String, Long>> future = CompletableFuture.supplyAsync(() -> {
            try {
                final ResultSet resultSet = plugin.getDataBase().query(
                        "select id,value from " + leaderboard.getLeaderboard() + ";",
                        plugin.isMYSQLEnabled() ? leaderboard.getLeaderboard() : null
                );

                while (resultSet.next())
                    map.put(resultSet.getString(1), resultSet.getLong(2));
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        });
        return future.join();
    }

    private Map<String, Long> reverse(Map<String, Long> map) {
        final List<Map.Entry<String, Long>> list = new LinkedList<>(map.entrySet());

        list.sort(
                Map.Entry.comparingByValue()
        );
        Collections.reverse(list);

        final Map<String, Long> reversedMap = new LinkedHashMap<>();
        list.forEach(entry ->
                reversedMap.put(entry.getKey(), entry.getValue())
        );

        return reversedMap;
    }

    public List<Leaderboard> getBoardsFromDatabase() {
        final List<Leaderboard> list = new ArrayList<>();
        final CompletableFuture<List<Leaderboard>> future = CompletableFuture.supplyAsync(() -> {
            try {
                final ResultSet resultSet = plugin.getDataBase().query(
                        plugin.isMYSQLEnabled() ? "show tables like '" + ConfigUtil.getString("mysql.table-prefix") + "%';"
                                :
                                "select name from sqlite_master where type ='table' and name not like 'sqlite_%';"
                );
                while (resultSet.next()) {
                    if (!plugin.isMYSQLEnabled())
                        list.add(new Leaderboard(resultSet.getString(1)));
                     else
                        list.add(new Leaderboard(resultSet.getString(1).split("_")[1]));
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        });
        return future.join();
    }

    public void setupBoardTask(){
        Bukkit.getScheduler().runTaskTimer(plugin, () ->  {
            if (getBoardsFromDatabase().size() < 1) {
                plugin.getLogger().info( "No leaderboard found.");
                return;
            }

            final LeaderboardTask task = new LeaderboardTask(plugin);
            task.runTaskTimerAsynchronously(plugin, 0L, 60L);
        }, 100L, 20 * 60 * ConfigUtil.getInt("auto-leaderboard-reload-delay"));
    }

    public Hologram getHologramByLocation(Location location) {
        for (Hologram holo : HologramsAPI.getHolograms(plugin)) {
            if (holo.getLocation().getX() == location.getX()
                    && holo.getLocation().getY() == location.getY()
                    && holo.getLocation().getZ() == location.getZ())
                return holo;
        }
        return HologramsAPI.createHologram(plugin, location);
    }

    public void setupManageInventory() {
        manageInventory = new InventoryUI("uLeaderboards > Sıralama yönetimi", 5);
        getBoardsFromList().forEach(leaderboard -> {
            final ItemStack itemStack = new ItemStack(Material.DIAMOND);
            itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§b" + leaderboard.getLeaderboard());
            itemMeta.setLore(
                    Arrays.asList(

                            "§fLimit: §e" + leaderboard.getLimit(),
                            "§fPlaceholder: §e" + leaderboard.getPlaceholder(),
                            "",
                            "§eDüzenlemek için tıkla."
                    )
            );
            itemStack.setItemMeta(itemMeta);

            manageInventory.addItem(new InventoryUI.AbstractClickableItem(itemStack) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    final InventoryUI inventoryUI = new InventoryUI("Yönet > " + leaderboard.getLeaderboard(), 1);
                    ItemStack item = new ItemStack(Material.EMERALD);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§ePlaceholder ayarla.");

                    item.setItemMeta(meta);

                    inventoryUI.setItem(3, new InventoryUI.AbstractClickableItem(item) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            final AnvilGUI.Builder builder = new AnvilGUI.Builder()
                                    .onComplete((player, s) -> {
                                        if (s.contains("%"))
                                            return AnvilGUI.Response.text("§cDeğeri içinde '%' olmadan girin!");

                                        leaderboard.setPlaceholder(s);
                                        player.sendMessage("§aPlaceholder değeri başarıyla §f" + s + "§a olarak ayarlandı!");
                                        return AnvilGUI.Response.close();
                                    })
                                    .text("§aLütfen bir değer girin.")
                                    .plugin(plugin);

                            Bukkit.getScheduler().runTask(plugin, () -> builder.open((Player) clickEvent.getWhoClicked()));
                        }
                    });

                    item = new ItemStack(Material.ANVIL);
                    meta = item.getItemMeta();

                    meta.setDisplayName("§eLimit ayarla.");

                    item.setItemMeta(meta);

                    inventoryUI.setItem(5, new InventoryUI.AbstractClickableItem(item) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            final AnvilGUI.Builder builder = new AnvilGUI.Builder()
                                    .onComplete((player, s) -> {
                                        if (!NumberUtils.isNumber(s))
                                            return AnvilGUI.Response.text("§cGeçersiz sayı!");

                                        leaderboard.setLimit(Integer.parseInt(s));
                                        player.sendMessage("§aLimit başarıyla §f" + s + "§a olarak ayarlandı!");
                                        return AnvilGUI.Response.close();
                                    })
                                    .text("§aLütfen bir sayı girin.")
                                    .plugin(plugin);

                            Bukkit.getScheduler().runTask(plugin, () -> builder.open((Player) clickEvent.getWhoClicked()));
                        }

                    });
                    event.getWhoClicked().openInventory(inventoryUI.getCurrentPage());
                }
            });
        });
    }

    public InventoryUI getManageInventory() {
        return manageInventory;
    }

    public void updatePlayer(Player player) {
        leaderboardList.forEach((key, value) -> update(value, player));
    }

    public Map<String, Long> getLeaderboard(Leaderboard leaderboard) {
        return leaderboardMap.get(leaderboard);
    }

    public Leaderboard getLeaderboardByName(String str) {
        return leaderboardList.get(str);
    }

    public List<Leaderboard> getBoardsFromList() {
        final List<Leaderboard> list = new ArrayList<>();
        leaderboardList.keySet().forEach(str ->
                list.add(leaderboardList.get(str))
        );
        return list;
    }
}
