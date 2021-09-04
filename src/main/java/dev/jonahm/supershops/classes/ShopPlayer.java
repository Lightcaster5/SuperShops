package dev.jonahm.supershops.classes;

import dev.jonahm.supershops.inventories.ShopInventory;
import dev.jonahm.supershops.SuperShops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class ShopPlayer {

    private final Player player;
    private final OfflinePlayer offlinePlayer;

    public ShopPlayer(Player player) {
        this.player = player;
        this.offlinePlayer = null;
    }

    public ShopPlayer(UUID uuid) {
        this.player = null;
        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);

    }

    public Player getPlayer() {
        return this.player;
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public String getName() {
        return player == null ? this.offlinePlayer.getName() : this.player.getName();
    }

    public String getUUID() {
        return player == null ? this.offlinePlayer.getUniqueId().toString() : this.player.getUniqueId().toString();
    }

    public void openInventory(Inventory inventory, Shop shop, ShopInventory.InventoryType inventoryType) {
        this.player.openInventory(inventory);
        setOpenedShop(shop);
        setInventoryType(inventoryType);
    }

    public Shop getOpenedShop() {
        return ShopManager.shopMap.get(this.getName());
    }

    public void setOpenedShop(Shop shop) {
        ShopManager.shopMap.put(this.getName(), shop);
    }

    public ShopInventory.InventoryType getInventoryType() {
        return ShopManager.shopInventoryTypeMap.get(this.getName());
    }

    public void setInventoryType(ShopInventory.InventoryType inventoryType) {
        ShopManager.shopInventoryTypeMap.put(this.getName(), inventoryType);
    }

    public void runActivity(Activity activity, Shop shop) {
        switch (activity) {
            case BUYING:
                this.setInventoryType(ShopInventory.InventoryType.BUYING);
                this.openInventory(shop.getBuyInventory(), shop,
                        ShopInventory.InventoryType.BUYING);
                break;
            case SELLING:
                this.setInventoryType(ShopInventory.InventoryType.SELLING);
                this.openInventory(shop.getSellInventory(), shop,
                        ShopInventory.InventoryType.SELLING);
                break;
            case SHOWING:
                this.setInventoryType(ShopInventory.InventoryType.SHOWING);
                this.openInventory(shop.getShowingInventory(), shop,
                        ShopInventory.InventoryType.SHOWING);
                break;
            case SETTINGS:
                this.setInventoryType(ShopInventory.InventoryType.SETTINGS);
                this.openInventory(shop.getSettingsInventory(), shop,
                        ShopInventory.InventoryType.SETTINGS);
                break;
            case STORAGE:
                this.setInventoryType(ShopInventory.InventoryType.STORAGE);
                this.openInventory(shop.getStorageInventory(), shop,
                        ShopInventory.InventoryType.STORAGE);
                break;
            case CHANGE_ITEM:
                this.setInventoryType(ShopInventory.InventoryType.CHANGE_ITEM);
                this.openInventory(shop.getChangeItemInventory(), shop,
                        ShopInventory.InventoryType.CHANGE_ITEM);
                break;
            default:
                this.setInventoryType(null);
                break;
        }
    }

    public void sendMessage(String message) {
        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendConfigMessage(String path) {
        sendMessage(SuperShops.config.getString(path));
    }

}
