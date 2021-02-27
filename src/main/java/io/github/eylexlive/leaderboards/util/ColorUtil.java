package io.github.eylexlive.leaderboards.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ColorUtil {

    public static String translate(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<String> translate(List<String> list){
        return list
                .stream()
                .map(ColorUtil::translate)
                .collect(Collectors.toList());
    }
}
