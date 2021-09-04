package dev.jonahm.supershops.utils.item;

import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.utils.string.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    public static ItemStack getSuperShopItem(long size) {
        return new ItemBuilder(Material.ENCHANTMENT_TABLE)
                .setName(CC.color("&4&l[!]&c&l PLAYER SHOP&7 (Place Down)"))
                .addLoreLine(CC.color("&0"))
                .addLoreLine(CC.color("&7Player shops allow players to buy or sell"))
                .addLoreLine(CC.color("&7items from you. Place down this item to begin!"))
                .addLoreLine(CC.color("&0"))
                .addLoreLine(CC.color("&4&l * &c&lSTORAGE:&f " + (size == -1 ? "oo" : "x" + SuperShops.nf.format(size))))
                .addLoreLine(CC.color("&0"))
                .toItemStack();
    }

    public static boolean isSuperShopItem(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                if (CC.strip(meta.getDisplayName()).equalsIgnoreCase("[!] PLAYER SHOP (Place Down)")) {
                    if (meta.hasLore()) {
                        List<String> lore = meta.getLore();
                        if (lore.size() == 6) {
                            return lore.get(4).contains("STORAGE");
                        }
                    }
                }
            }
        }
        return false;
    }

    public static int getSuperShopItemSize(ItemStack item) {
        if (isSuperShopItem(item)) {
            String loreLine = item.getItemMeta().getLore().get(4);
            loreLine = CC.strip(loreLine);

            loreLine = loreLine.replace(",", "");
            loreLine = loreLine.replace("x", "");

            String end = loreLine.split(": ")[1];

            int size;
            if (end.equals("oo")) {
                size = -1;
            } else {
                size = Integer.parseInt(end);
            }

            return size;
        }
        return 0;
    }

    public static Integer getSpace(Player player, ItemStack itemStack) {
        ItemStack itemStackClone = itemStack.clone();
        if (itemStack.getMaxStackSize() == 1) {
            int empty = 0;
            for (ItemStack i : player.getInventory().getContents()) {
                if (i == null) empty++;
            }
            return empty;
        } else {
            int maxStackSize = itemStack.getMaxStackSize();
            int space = 0;
            for (ItemStack i : player.getInventory().getContents()) {
                if (i == null) {
                    space += maxStackSize;
                    continue;
                }
                ItemStack iClone = i.clone();
                iClone.setAmount(1);
                itemStack.setAmount(1);
                if (isSimilar(iClone, itemStackClone, false)) {
                    space += (maxStackSize - i.getAmount());
                }
            }
            return space;
        }
    }

    public static void spreadItems(Player player, ItemStack itemStack, int amount) {
        if (amount == -1) {
            amount = getSpace(player, itemStack);
        }
        int maxStackSize = itemStack.getMaxStackSize();
        int loopTimes = (int) amount / maxStackSize;
        for (int i = 0; i < loopTimes; i++) {
            itemStack.setAmount(maxStackSize);
            player.getInventory().addItem(itemStack);
        }
        double remainder = (double) amount / maxStackSize - loopTimes;
        int remainderAmount = (int) (maxStackSize * remainder);
        if (remainderAmount != 0) {
            itemStack.setAmount(remainderAmount);
            player.getInventory().addItem(itemStack);
        }
    }

    public static Integer getAmount(Player player, ItemStack itemStack) {
        Inventory inventory = player.getInventory();
        ItemStack itemStackClone = itemStack.clone();
        int count = 0;
        for (ItemStack i : inventory.getContents()) {
            if (i == null) continue;
            ItemStack iClone = i.clone();
            if (isSimilar(iClone, itemStackClone, false)) {
                count += i.getAmount();
            }
        }
        return count;
    }

    public static void takeAmount(Player player, ItemStack itemStack, int amount) {
        ItemStack itemStackClone = itemStack.clone();
        int remaining = amount;
        for (int x = 0; x < player.getInventory().getSize(); x++) {
            ItemStack i = player.getInventory().getItem(x);
            if (i == null) continue;
            ItemStack iClone = i.clone();
            if (isSimilar(iClone, itemStackClone, false)) {
                if (i.getAmount() > remaining) {
                    i.setAmount(i.getAmount() - remaining);
                    break;
                } else {
                    remaining -= i.getAmount();
                    player.getInventory().setItem(x, null);
                }
            }
        }
    }

    public static boolean isSimilar(ItemStack item1, ItemStack item2, boolean amount) {
        boolean similar = false;
        if (item1 == null || item2 == null) {
            return similar;
        }
        boolean sameTypeId = (item1.getTypeId() == item2.getTypeId());
        boolean sameMaterial = (item1.getType() == item2.getType());
        boolean sameDurability = (item1.getDurability() == item2.getDurability());
        boolean sameHasItemMeta = (item1.hasItemMeta() == item2.hasItemMeta());
        boolean sameEnchantments = (item1.getEnchantments().equals(item2.getEnchantments()));
        boolean sameItemMeta = Bukkit.getItemFactory().equals(item1.getItemMeta(), item2.getItemMeta());
        similar =
                (sameTypeId && sameMaterial && sameDurability && sameHasItemMeta && sameEnchantments && sameItemMeta);
        if (amount) {
            similar = item1.getAmount() == item2.getAmount();
        }

        return similar;
    }

    public static String itemToString(ItemStack item) {
        Map<String, Object> itemMap = serialize(item);
        return toBase64(itemMap);
    }

    public static ItemStack stringToItem(String string) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> itemMap = (Map<String, Object>) fromBase46(string);
        return deserialize(itemMap);
    }

    public static Object fromBase46(String s) {
        try {
            byte[] data = Base64.getDecoder().decode(s);
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toBase64(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets an item back from the Map created by serialize()
     *
     * @param map The map to deserialize from.
     * @return The deserialized item.
     * @throws IllegalAccessException    Things can go wrong.
     * @throws IllegalArgumentException  Things can go wrong.
     * @throws InvocationTargetException Things can go wrong.
     */
    public static ItemStack deserialize(Map<String, Object> map) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        ItemStack i = ItemStack.deserialize(map);
        if (map.containsKey("meta")) {
            try {
                //  org.bukkit.craftbukkit.v1_8_R3.CraftMetaItem$SerializableMeta
                //  CraftMetaItem.SerializableMeta.deserialize(Map<String, Object>)
                if (ITEM_META_DESERIALIZATOR != null) {
                    ItemMeta im = (ItemMeta) DESERIALIZE.invoke(i, map.get("meta"));
                    i.setItemMeta(im);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw e;
            }
        }
        return i;
    }

    /**
     * Serializes an ItemStack and it's ItemMeta, use deserialize() to
     * get the item back.
     *
     * @param item Item to serialize
     * @return A HashMap with the serialized item
     */
    public static Map<String, Object> serialize(ItemStack item) {
        HashMap<String, Object> itemDocument = new HashMap(item.serialize());
        if (item.hasItemMeta()) {
            itemDocument.put("meta", new HashMap(item.getItemMeta().serialize()));
        }
        return itemDocument;
    }

    //Below here lays some crazy shit that make the above methods work :D yay!
    // <editor-fold desc="Some crazy shit" defaultstate="collapsed">
    /*
     * @return The string used in the CraftBukkit package for the version.
     */
    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1) + ".";
        return version;
    }

    /**
     * Basic reflection.
     *
     * @param className
     * @return
     */
    public static Class<?> getOBCClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    private static final Class ITEM_META_DESERIALIZATOR = getOBCClass("inventory.CraftMetaItem").getClasses()[0];
    private static final Method DESERIALIZE = getDeserialize();

    private static Method getDeserialize() {

        try {
            return ITEM_META_DESERIALIZATOR.getMethod("deserialize", new Class[]{Map.class});
        } catch (NoSuchMethodException | SecurityException ex) {
            return null;
        }
    }
    // </editor-fold>

}
