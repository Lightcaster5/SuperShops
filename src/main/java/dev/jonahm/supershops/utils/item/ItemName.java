package dev.jonahm.supershops.utils.item;

import dev.jonahm.supershops.SuperShops;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ItemName {

    public static Map<String, String> items = new HashMap<>();
    public static HashMap<Integer, String> potionEffects = new HashMap<>();
    
    public static void load() {
        try {
            InputStream stream = SuperShops.plugin.getResource("items.tsv");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String row;
                while ((row = reader.readLine()) != null) {
                    row = row.trim();
                    if (row.isEmpty())
                        continue;
                    String[] cols = row.split("\t");
                    String name = cols[2];
                    String id = cols[0];
                    String metadata = cols[1];
                    String idAndMetadata = id + ":" + metadata;
                    items.put(idAndMetadata, name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        potionEffects.put(0, "Mundane Potion");
        potionEffects.put(1, "Potion of Regeneration");
        potionEffects.put(2, "Potion of Swiftness");
        potionEffects.put(3, "Potion of Fire Resistance");
        potionEffects.put(4, "Potion of Poison");
        potionEffects.put(5, "Potion of Healing");
        potionEffects.put(6, "Potion of Night Vision");
        potionEffects.put(7, "Clear Potion");
        potionEffects.put(8, "Potion of Weakness");
        potionEffects.put(9, "Potion of Strength");
        potionEffects.put(10, "Potion of Slowness");
        potionEffects.put(11, "Potion of Leaping");
        potionEffects.put(12, "Potion of Harming");
        potionEffects.put(13, "Potion of Water Breathing");
        potionEffects.put(14, "Potion of Invisibility");
        potionEffects.put(15, "Thin Potion");
        potionEffects.put(16, "Awkward Potion");
        potionEffects.put(23, "Bungling Potion");
        potionEffects.put(31, "Debonair Potion");
        potionEffects.put(32, "Thick Potion");
        potionEffects.put(39, "Charming Potion");
        potionEffects.put(47, "Sparkling Potion");
        potionEffects.put(48, "Potent Potion");
        potionEffects.put(55, "Rank Potion");
        potionEffects.put(63, "Stinky Potion");
    }

    public static String getPotionName(short durability) {
        StringBuilder potion = new StringBuilder();
        if (((durability >> 6) & 1) == 1) {
            potion.append("Extended ");
        }
        if (((durability >> 14) & 1) == 1) {
            potion.append("Splash ");
        }
        int remainder = durability % 64;
        if (potionEffects.containsKey(remainder)) {
            potion.append(potionEffects.get(remainder));
        } else {
            potion.append(potionEffects.get(remainder % 16));
        }
        if (((durability >> 5) & 1) == 1) {
            potion.append(" II");
        }
        return potion.toString();
    }

    public static String getItemName(ItemStack itemStack) {
        int id = itemStack.getTypeId();
        short data = itemStack.getDurability();
        String stringID = id + ":" + data;
        if (items.get(stringID) == null) {
            return getPotionName(data);
        }
        return items.get(stringID);
    }

}
