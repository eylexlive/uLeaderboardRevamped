package io.github.eylexlive.leaderboards.leaderboard.display;

import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardStat;
import io.github.eylexlive.leaderboards.util.ColorUtil;
import io.github.eylexlive.leaderboards.util.LocUtil;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SignDisplay implements ILeaderboardDisplay {

    @Override
    public void setup(Leaderboard leaderboard) {
        for (int i = 1; i <= leaderboard.getLimit(); i++) {
            final LeaderboardSign sign = getSign(leaderboard, i);
            if (sign == null)
                continue;
            updateSign(sign);
        }
    }

    @Override
    public boolean setup(Leaderboard leaderboard, Location location, int index) {
        final LeaderboardSign sign = new LeaderboardSign(location, leaderboard, index);

        final List<String> list = ConfigUtil.getStringList("sign-locations");

        final String serialize = s(sign.getLocation(), sign.getLeaderboardEntry(), sign.getPosition());
        if (list.contains(serialize))
            return false;

        list.add(serialize);

        ConfigUtil.set("sign-locations", list);

        updateSign(sign);
        return true;
    }

    @Override
    public void unSetup(Leaderboard leaderboard, int index) {
        final LeaderboardSign sign = getSign(leaderboard, index);
        if (sign == null)
            return;

        final Location location = sign.getLocation();
        location.getBlock().setType(Material.AIR);

        final List<String> list = plugin.getConfig().getStringList("sign-locations");
        list.remove(s(sign.getLocation(), sign.getLeaderboardEntry(), sign.getPosition()));

        ConfigUtil.set("sign-locations", list);
    }

    private LeaderboardSign getSign(Leaderboard leaderboardEntry, int position) {
        final List<String> list = ConfigUtil.getStringList("sign-locations");
        for (String str : list) {
            if (!str.contains(leaderboardEntry.getLeaderboard()))
                continue;

            final LeaderboardSign sign = d(str);
            if (sign.getLeaderboardEntry().equals(leaderboardEntry) && sign.getPosition() == position)
                return sign;
        }
        return null;
    }

    private void updateSign(LeaderboardSign sign) {
        final Leaderboard leaderboard = sign.getLeaderboardEntry();

        final List<String> list = new ArrayList<>();
        final List<String> lines = leaderboard.getStringListMap().get("signLines");

        final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, sign.getPosition(), true);
        if (lines.size() < 1) {
            list.addAll(
                    Arrays.asList(
                            "&4&LKURULUM BAŞARISIZ!",
                            "&f", "&cTabelya satırları",
                            "&cConfigde bulunamadı!"
                    )
            );
        } else {
            for (String line : lines) {
                String str = line.replace("{lb_index}", String.valueOf(sign.getPosition()))
                        .replace("{lb_player}", stat.getName())
                        .replace("{lb_value}", stat.getValue());
                list.add(str);
            }
        }

        sign.setupSign(ColorUtil.translate(list));
        updateHead(sign, stat.getName());
    }

    private void updateHead(LeaderboardSign sign, String playerName) {
        final Block block = sign.getSign().getBlock();
        final Block copiedBlock;

        final BlockFace signFace = sign.getFacing();

        final int[] parts = (
                signFace == BlockFace.EAST ? new int[] { -1, 1, 0 }
                : signFace == BlockFace.WEST ? new int[] { 1, 1, 0 }
                : signFace == BlockFace.SOUTH ? new int[] { 0, 1, -1 }
                : signFace == BlockFace.NORTH ? new int[] { 0, 1, 1 }
                : null
        );

        if (parts == null)
            copiedBlock = block.getRelative(BlockFace.UP);
        else
            copiedBlock = block.getRelative(parts[0], parts[1], parts[2]);

        final Block copiedBlock2 = block.getRelative(BlockFace.UP, 1);
        if (copiedBlock2.getType() == Material.matchMaterial("SKULL")) {
            final Skull skull = (Skull) copiedBlock2.getState();
            skull.setOwner(playerName);
            skull.setSkullType(SkullType.PLAYER);
            skull.update();
        }

        else if (copiedBlock.getType() == Material.matchMaterial("SKULL")) {
            final Skull skull = (Skull) copiedBlock.getState();
            skull.setSkullType(SkullType.PLAYER);
            skull.setOwner(playerName);
            skull.update();
        }
    }

    private String s(Location location, Leaderboard board, int position) {
        return LocUtil.locToString(location) + ";" + board.getLeaderboard() + ";" + position;
    }

    private LeaderboardSign d(String s) {
        final String[] parts = s.split(";");
        final Location loc = LocUtil.stringToLoc(parts[0]);
        final Leaderboard board = plugin.getLeaderboardManager().getLeaderboardByName(parts[1]);
        final int pos = Integer.parseInt(parts[2]);
        return new LeaderboardSign(loc, board, pos);
    }

    @Override
    public void unSetup(Leaderboard leaderboard) {

    }

    private class LeaderboardSign {

        private final Location location;

        private final Leaderboard leaderboardEntry;

        private final int position;

        public LeaderboardSign(Location location, Leaderboard leaderboardEntry, int position) {
            this.location = location;
            this.leaderboardEntry = leaderboardEntry;
            this.position = position;
        }

        public void setupSign(List<String> lines) {
            final BlockState blockState = location.getBlock().getState();
            if (!(blockState instanceof Sign))
                return;
            final Sign sign = (Sign) blockState;
            int i = 0;
            for (String line: lines) {
                sign.setLine(i, line);
                i++;
            }
            sign.update();
        }

        public Location getLocation() {
            return location;
        }

        public Leaderboard getLeaderboardEntry() {
            return leaderboardEntry;
        }

        public int getPosition() {
            return position;
        }

        public Sign getSign() {
            final BlockState blockState = location.getBlock().getState();
            if (!(blockState instanceof  Sign))
                return null;
            return (Sign) blockState;
        }

        public BlockFace getFacing() {
            final org.bukkit.material.Sign sign = (org.bukkit.material.Sign) getSign().getData();
            return sign.getFacing();
        }
    }
}
