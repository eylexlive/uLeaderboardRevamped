package io.github.eylexlive.leaderboards.command;

import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardManager;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardTask;
import io.github.eylexlive.leaderboards.leaderboard.display.*;
import io.github.eylexlive.leaderboards.uLeaderboards;
import io.github.eylexlive.leaderboards.util.ColorUtil;
import io.github.eylexlive.leaderboards.util.ReplaceUtil;
import io.github.eylexlive.leaderboards.util.book.BookUtil;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;
import io.github.eylexlive.leaderboards.util.inventory.InventoryUI;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LeaderboardCommand implements CommandExecutor {

    private final uLeaderboards plugin;

    private final String invalidLeaderboard;

    private final String[] adminMsg;
    private final String[] playerMsg;

    public LeaderboardCommand(uLeaderboards plugin) {
        this.plugin = plugin;
        this.invalidLeaderboard = "§cGeçersiz sıralama.";
        this.adminMsg = new String[] {
                "",
                "   §8▸ §e/ulb manage §8| §7Sıralama yönetim menüsünü açar!",
                "",
                "   §8▸ §e/ulb create §a<tür> §8| §7Yeni bir sıralama oluşturur!",
                "   §8▸ §e/ulb delete §a<tür> §8| §7Sıralamayı siler!",
                "",
                "   §8▸ §e/ulb holo §a<tür> §8| §7Hologram kurar!",
                "   §8▸ §e/ulb npc §a<tür> §c<değer> §8| §7NPC kurar!",
                "   §8▸ §e/ulb as §a<tür> §c<değer> §8| §7ArmorStand kurar!",
                "   §8▸ §e/ulb sign §a<tür> §c<değer> §8| §7Tabelayı ayarlar!",
                "",
                "   §8▸ §e/ulb book §a<tür> §8| §7Sıralamayı gösterir!",
                "   §8▸ §e/ulb gui §a<tür> §8| §7Sıralamayı gösterir!",
                "   §8▸ §e/ulb position §a<tür> §8| §7Sıralamadaki yeri gösterir!",
                "",
                "   §8▸ §e/ulb reload §8| §7Sıralamaları yeniler!",
                "   §8▸ §e/ulb reloadConfig §8| §7Configi yeniler!",
                "",
        };
        this.playerMsg = new String[]{
                "",
                "   §8▸ §e/ulb book §a<tür> §8| §7Sıralamayı gösterir!",
                "   §8▸ §e/ulb guş §a<tür> §8| §7Sıralamayı gösterir!",
                "",
                "            §f§ouLeaderboards §f§Lby Umut Erarslan#8378.",
                "",
        };
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("uLeaderboards commands can be usable in the game!");
            return true;
        }

        final Player player = (Player) commandSender;
        final LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();

        if (args.length == 0) {
            player.sendMessage(player.isOp() ? adminMsg : playerMsg);
        } else if (args.length == 1) {
            if (!player.isOp())
                return true;

            if (args[0].equalsIgnoreCase("manage")) {
                CompletableFuture.runAsync(leaderboardManager::setupManageInventory).whenComplete((f, err) -> {
                    if (err == null)
                        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(leaderboardManager.getManageInventory().getCurrentPage()));
                    else
                        err.printStackTrace();
                });
            }

            else if (args[0].equalsIgnoreCase("reload")) {
                player.sendMessage("§aTüm sıralamalar yenileniyor, konsoldan ilerleyişi takip edebilirsin.");
                final LeaderboardTask task = new LeaderboardTask(plugin);
                task.runTaskTimerAsynchronously(plugin, 0L,60L);
            }

            else if (args[0].equalsIgnoreCase("reloadconfig")) {
                plugin.reloadConfig();
                player.sendMessage("§aConfig yenilendi.");
            }

        } else if (args.length == 2) {
            final Leaderboard leaderboard = leaderboardManager.getLeaderboardByName(args[1]);

            if (args[0].equalsIgnoreCase("gui")) {
                if (leaderboard == null) {
                    player.sendMessage(this.invalidLeaderboard);
                    return true;
                }

                final GuiDisplay gui = new GuiDisplay();
                Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(gui.getGui(leaderboard).getCurrentPage()));
            } else if (args[0].equalsIgnoreCase("book")) {
                if (leaderboard == null) {
                    player.sendMessage(this.invalidLeaderboard);
                    return true;
                }

                final BookDisplay book = new BookDisplay();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    final BookUtil.BookBuilder playerBook = book.getBook(leaderboard);

                    playerBook.author(player.getName());
                    playerBook.pagesRaw(book.getPages(leaderboard, player));

                    BookUtil.openPlayer(player, playerBook.build());
                });

            } else if (args[0].equalsIgnoreCase("position")) {
                if (leaderboard == null) {
                    player.sendMessage(this.invalidLeaderboard);
                    return true;
                }

                final List<String> stringList = ColorUtil.translate(ConfigUtil.getStringList("position-msg"));

                if (stringList.size() == 0) {
                    player.sendMessage("§cerr = Key not found!");
                    return true;
                }

                ReplaceUtil.replacePlh(
                        stringList,
                        "pos:" + leaderboardManager.getPosition(player, leaderboard)
                );

                stringList.forEach(player::sendMessage);
            }

            if (player.isOp()) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (!leaderboardManager.createBoard(args[1])) {
                        player.sendMessage("§cSıralama oluşturulurken bir hata oluştu!");
                        return true;
                    }

                    player.sendMessage(
                            new String[]{
                                    "§a§l" + args[1] + "§a adlı sıralama başarıyla oluşturuldu!",
                                    "",
                                    "§aSıralama dosyası §fplugins/uLeaderboards/leaderboards/" + args[1] + ".yml §akısmına oluşturuldu.",
                                    "",
                                    "§aBundan sonra izlemeniz gereken adımlar§8:",
                                    "§8- §bSıralanacak placeholder değerini ayarlayın.",
                                    "§a  **Sıralama dosyasından veya §a§l/lb manage §aile**",
                                    "",
                                    "§8- §bSıralamanın yüklenmesi için expansion indirmeniz gerekebilir.",
                                    "§a  **/papi ecloud download <expansion>**",
                                    "",
                                    "§8- §bExpansion indirdikten sonra §a/papi reload §batın.",
                                    "§8- §bTamamlandı! Görünümleri ayarlamak için sıralama configini kontrol edin.",
                            }
                    );

                }

                else if (args[0].equalsIgnoreCase("delete")) {
                    if (!leaderboardManager.deleteBoard(args[1])) {
                        player.sendMessage("§cSıralama silinirken bir hata oluştu!");
                        return true;
                    }

                    player.sendMessage("§aSıralama başarıyla silindi!");
                }

                else if (args[0].equalsIgnoreCase("holo")) {
                    if (leaderboard == null) {
                        player.sendMessage(this.invalidLeaderboard);
                        return true;
                    }

                    final HologramDisplay holo = new HologramDisplay();
                    if (!holo.setup(leaderboard, player.getLocation(), 0))  {
                        holo.unSetup(leaderboard);
                        player.sendMessage("§bHologram başarıyla kaldırıldı!");
                    } else {
                        player.sendMessage("§aHologram başarıyla oluşturuldu!");
                    }

                }
            }
        } else if (args.length == 3) {
            if (!player.isOp() || !NumberUtils.isNumber(args[2]))
                return true;

            final Leaderboard leaderboard = leaderboardManager.getLeaderboardByName(args[1]);
            if (leaderboard == null) {
                player.sendMessage(this.invalidLeaderboard);
                return true;
            }

            final int pos = Integer.parseInt(args[2]);
            final Location location = player.getLocation();

            if (args[0].equalsIgnoreCase("npc")) {
                final NpcDisplay npc = new NpcDisplay();
                if (!npc.setup(leaderboard, location, pos)) {
                    npc.unSetup(leaderboard, pos);
                    player.sendMessage("§bSıralamanın " + pos + ". npcsi kaldırıldı.");
                } else {
                    player.sendMessage("§aSıralamanın " + pos + ". npcsi kuruldu.");
                }
            }

            else if (args[0].equalsIgnoreCase("as")) {
                final ArmorStandDisplay as = new ArmorStandDisplay();
                if (!as.setup(leaderboard, location, pos)) {
                    as.unSetup(leaderboard, pos);
                    player.sendMessage("§bSıralamanın " + pos + ". armorstand'i kaldırıldı.");
                } else {
                    player.sendMessage("§aSıralamanın " + pos + ". armorstand'i kuruldu.");
                }
            }

            else if (args[0].equalsIgnoreCase("sign")) {
                final Block target = player.getTargetBlock((Set<Material>) null, 10);
                if (!target.getType().toString().contains("SIGN")) {
                    player.sendMessage("§cLütfen bir tabelaya bakın.");
                    return true;
                }

                final SignDisplay sign = new SignDisplay();
                if (!sign.setup(leaderboard, target.getLocation(), pos)) {
                    sign.unSetup(leaderboard, pos);
                    player.sendMessage("§bSıralamanın " + pos + ". tabelası kaldırıldı.");
                } else {
                    player.sendMessage("§aSıralamanın " + pos + ". tabelası ayarlandı.");
                }
            }
        }
        return true;
    }
}
