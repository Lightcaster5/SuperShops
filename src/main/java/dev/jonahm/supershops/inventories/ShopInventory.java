package dev.jonahm.supershops.inventories;

import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ShopInventory {

    // Default Shop Size
    int size = 36;

    // Shop Items for background
    ItemStack gray = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§0").toItemStack();
    ItemStack white = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 0).setName("§0").toItemStack();
    ItemStack yellow = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 4).setName("§0").toItemStack();

    public abstract Inventory build(Shop shop);

    public static enum InventoryType {
        BUYING,
        SELLING,
        SHOWING,
        SETTINGS,
        CHANGE_ITEM,
        STORAGE
    }

}
