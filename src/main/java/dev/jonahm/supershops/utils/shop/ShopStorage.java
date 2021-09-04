package dev.jonahm.supershops.utils.shop;

import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.classes.ShopPlayer;
import dev.jonahm.supershops.utils.display.FloatingText;
import dev.jonahm.supershops.utils.item.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ShopStorage {

    public static void createShop(Player player, Block block, Integer storage) {
        FileConfiguration shops = SuperShops.shops;
        int shopID = shops.getKeys(false).size() + 1;
        while (isIdUsed(shopID)) {
            shopID++;
        }
        String base = "shop-" + shopID;
        shops.set(base + ".id", shopID);
        shops.set(base + ".owner-uuid", player.getUniqueId().toString());
        shops.set(base + ".owner-name", player.getName().toString());
        shops.set(base + ".location.x", block.getX());
        shops.set(base + ".location.y", block.getY());
        shops.set(base + ".location.z", block.getZ());
        shops.set(base + ".location.world", block.getWorld().getName().toString());
        shops.set(base + ".price", 0);
        shops.set(base + ".type", "SHOWING");
        shops.set(base + ".holograms", Collections.emptyList());
        shops.set(base + ".contents.item", "");
        shops.set(base + ".contents.quantity", 0);
        shops.set(base + ".contents.storage", storage);
        SuperShops.saveConfigs();
        Shop shop = new Shop(shopID);
        FloatingText.createShopText(shop);
    }

    public static void createShop(ShopPlayer shopPlayer, Block block, Integer storage) {
        FileConfiguration shops = SuperShops.shops;
        int shopID = shops.getKeys(false).size() + 1;
        while (isIdUsed(shopID)) {
            shopID++;
        }
        System.out.println("Creating new shop with an id of " + shopID);
        String base = "shop-" + shopID;
        shops.set(base + ".id", shopID);
        shops.set(base + ".owner-uuid", shopPlayer.getPlayer().getUniqueId().toString());
        shops.set(base + ".owner-name", shopPlayer.getName());
        shops.set(base + ".location.x", block.getX());
        shops.set(base + ".location.y", block.getY());
        shops.set(base + ".location.z", block.getZ());
        shops.set(base + ".location.world", block.getWorld().getName());
        shops.set(base + ".price", 0);
        shops.set(base + ".type", "SHOWING");
        shops.set(base + ".holograms", Collections.emptyList());
        shops.set(base + ".contents.item", "");
        shops.set(base + ".contents.quantity", 0);
        shops.set(base + ".contents.storage", storage);
        SuperShops.saveConfigs();
        SuperShops.reloadShopConfigs();
        Shop shop = new Shop(shopID);
        FloatingText.createShopText(shop);
    }

    public static void removeShop(Integer ID) {
        Shop shop = new Shop(ID);
        FloatingText.removeShopText(shop);
        FileConfiguration shops = SuperShops.shops;
        String base = "shop-" + ID;
        shops.set(base, null);
        SuperShops.saveConfigs();
    }

    public static boolean isIdUsed(Integer ID) {
        FileConfiguration shops = SuperShops.shops;
        for (String shop : shops.getKeys(false)) {
            return shops.getInt(shop + ".id") == ID;
        }
        return false;
    }

    public static Shop getShop(Block block) {
        int x, y, z;
        String world = block.getWorld().getName();
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        for (String shop : SuperShops.shops.getKeys(false)) {
            int dx, dy, dz;
            String dworld = SuperShops.shops.getString(shop + ".location.world");
            dx = SuperShops.shops.getInt(shop + ".location.x");
            dy = SuperShops.shops.getInt(shop + ".location.y");
            dz = SuperShops.shops.getInt(shop + ".location.z");
            if (dworld.equalsIgnoreCase(world) && x == dx && y == dy && z == dz) {
                return new Shop(SuperShops.shops.getInt(shop + ".id"));
            }
        }
        return null;
    }

    public static String getPath(Integer ID) {
        for (String shop : SuperShops.shops.getKeys(false)) {
            if (SuperShops.shops.getInt(shop + ".id") == ID) {
                return shop;
            }
        }
        return null;
    }

    public static Long getPrice(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            return SuperShops.shops.getLong(base + ".price");
        }
        return 0L;
    }

    public static void setPrice(Integer ID, Long amount) {
        String base = getPath(ID);
        if (base != null) {
            SuperShops.shops.set(base + ".price", amount);
        }
        SuperShops.saveConfigs();
    }

    public static Integer getStorage(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            return SuperShops.shops.getInt(base + ".contents.storage");
        }
        return 0;
    }

    public static Integer getQuantity(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            return SuperShops.shops.getInt(base + ".contents.quantity");
        }
        return 0;
    }

    public static void setQuantity(Integer ID, Integer amount) {
        String base = getPath(ID);
        if (base != null) {
            SuperShops.shops.set(base + ".contents.quantity", amount);
        }
        SuperShops.saveConfigs();
    }

    public static Shop.ShopType getShopType(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            String type = SuperShops.shops.getString(base + ".type");
            return Shop.ShopType.fromString(type);
        }
        return null;
    }

    public static void setShopType(Integer ID, Shop.ShopType shopType) {
        String base = getPath(ID);
        if (base != null) {
            SuperShops.shops.set(base + ".type", shopType.toString());
        }
        SuperShops.saveConfigs();
    }

    public static void setHolograms(Integer ID, List<String> list) {
        String base = getPath(ID);
        if (base != null) {
            SuperShops.shops.set(base + ".holograms", list);
        }
        SuperShops.saveConfigs();
    }

    public static List<String> getHolograms(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            return SuperShops.shops.getStringList(base + ".holograms");
        }
        return null;
    }

    public static void setFloatingItem(Integer ID, String uuid) {
        String base = getPath(ID);
        if (base != null) {
            SuperShops.shops.set(base + ".floating-item", uuid);
        }
        SuperShops.saveConfigs();
    }

    public static String getFloatingItem(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            return SuperShops.shops.getString(base + ".floating-item");
        }
        return null;
    }

    public static String getOwner(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            return SuperShops.shops.getString(base + ".owner-name");
        }
        return null;
    }

    public static String getOwnerUUID(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            return SuperShops.shops.getString(base + ".owner-uuid");
        }
        return null;
    }

    public static ItemStack getItem(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            String item = SuperShops.shops.getString(base + ".contents.item");
            if (item.length() == 0 || item.equals("") || item.isEmpty()) {
                return null;
            }
            try {
                return ItemUtils.stringToItem(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setItem(Integer ID, ItemStack item) {
        item.setAmount(1);
        String base = getPath(ID);
        if (base != null) {
            String itemString = ItemUtils.itemToString(item);
            SuperShops.shops.set(base + ".contents.item", itemString);
            SuperShops.saveConfigs();
        }
    }

    public static Location getLocation(Integer ID) {
        String base = getPath(ID);
        if (base != null) {
            int x, y, z;
            String world = SuperShops.shops.getString(base + ".location.world");
            x = SuperShops.shops.getInt(base + ".location.x");
            y = SuperShops.shops.getInt(base + ".location.y");
            z = SuperShops.shops.getInt(base + ".location.z");
            return new Location(Bukkit.getWorld(world), x, y, z);
        }
        return null;
    }
}
