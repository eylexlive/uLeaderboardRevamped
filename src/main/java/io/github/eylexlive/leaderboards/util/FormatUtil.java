package io.github.eylexlive.leaderboards.util;

import io.github.eylexlive.leaderboards.util.config.ConfigUtil;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FormatUtil {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(
                1000L, ConfigUtil.getString("stat-format.formats.words.1000")
        );
        suffixes.put(
                1000000L, ConfigUtil.getString("stat-format.formats.words.1000000")
        );
        suffixes.put(
                1000000000L, ConfigUtil.getString("stat-format.formats.words.1000000000")
        );
        suffixes.put(
                1000000000000L, ConfigUtil.getString("stat-format.formats.words.1000000000000")
        );
        suffixes.put(
                1000000000000000L, ConfigUtil.getString("stat-format.formats.words.1000000000000000")
        );
        suffixes.put(
                1000000000000000000L, ConfigUtil.getString("stat-format.formats.words.1000000000000000000")
        );
    }

    public static String format(long value) {
        final FormatType type = matchType();
        if (type == FormatType.COMMA) {
            return String.valueOf(value).replaceAll(
                    "(?<=\\d)(?=(\\d\\d\\d)+(?!\\d))",
                    ConfigUtil.getString("stat-format.formats.comma")
            );
        }

        else if (type == FormatType.WORD) {
            if (value == Long.MIN_VALUE) {
                return format(Long.MIN_VALUE + 1);
            }

            if (value < 0) {
                return "-" + format(-value);
            }

            if (value < 1000) {
                return Long.toString(value);
            }

            final Map.Entry<Long, String> entry = suffixes.floorEntry(value);
            final Long divideBy = entry.getKey();
            final String suffix = entry.getValue();

            final long truncated = value / (divideBy / 10);
            final boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);

            return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
        }

        else {
            throw new IllegalStateException(
                    "Format type not matched!!"
            );
        }
    }

    private static FormatType matchType() {
        final FormatType type;
        final String format = ConfigUtil.getString(
                "stat-format.type"
        );
        try {
            type = FormatType.valueOf(format);
        } catch (IllegalArgumentException e) {
            return FormatType.COMMA;
        }
        return type;
    }

    private enum FormatType {
        COMMA,
        WORD
    }
}
