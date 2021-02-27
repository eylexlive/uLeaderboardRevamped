package io.github.eylexlive.leaderboards.util.config;

import io.github.eylexlive.leaderboards.uLeaderboards;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Pattern;

public class ConfigUtil {

    private static final uLeaderboards plugin = uLeaderboards.getInstance();

    public static String getString(String path) {
        final String str = plugin.getConfig().getString(path);
        if (str == null)
            return "Key not found!";
        final Pattern pattern = Pattern.compile("&([0-fk-or])");
        if (!pattern.matcher(str).find())
            return str;
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<String> getStringList(String path) {
        return plugin.getConfig().getStringList(path);
    }

    public static boolean getBoolean(String path) {
        return plugin.getConfig().getBoolean(path);
    }

    public static int getInt(String path) {
        return plugin.getConfig().getInt(path);
    }

    public static Object get(String path) { return plugin.getConfig().get(path); }

    public static void set(String path, Object object) { plugin.getConfig().set(path, object); }
}
