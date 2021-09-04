package dev.jonahm.supershops.utils.string;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class StringFormatter {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
    }

    public static String format(long value) {
        if (value < 1000) {
            return Long.toString(value);
        }

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long amount = value / (divideBy / 10);
        String string = String.valueOf(amount / 10d).replace(".0", "");
        return string + suffix;
    }

}
