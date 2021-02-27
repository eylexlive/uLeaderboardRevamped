package io.github.eylexlive.leaderboards.util;

public class CommaUtil {

    public static String comma(long l) {
        final String str = String.valueOf(l);
        return str.replaceAll("(?<=\\d)(?=(\\d\\d\\d)+(?!\\d))", ".");
    }
}
