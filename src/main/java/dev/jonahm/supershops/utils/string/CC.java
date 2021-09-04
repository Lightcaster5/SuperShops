package dev.jonahm.supershops.utils.string;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CC {

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<String> color(List<String> list) {
        List<String> colored = new ArrayList<>();
        for (String listElement : list) {
            colored.add(CC.color(listElement));
        }
        return colored;
    }

    public static String strip(String str) {
        return ChatColor.stripColor(str);
    }

}