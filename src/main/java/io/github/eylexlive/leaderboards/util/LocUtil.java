package io.github.eylexlive.leaderboards.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocUtil {

    public static String locToString(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch() + ",";
    }

    public static Location stringToLoc(String str) {
        final String[] split = str.split(",");
        return new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5])
        );
    }
}
