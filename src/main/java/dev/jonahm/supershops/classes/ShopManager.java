package dev.jonahm.supershops.classes;

import dev.jonahm.supershops.inventories.ShopInventory;

import java.util.HashMap;

public class ShopManager {

    public static HashMap<String, Shop> shopMap = new HashMap<>();
    public static HashMap<String, ShopInventory.InventoryType> shopInventoryTypeMap = new HashMap<>();

}
