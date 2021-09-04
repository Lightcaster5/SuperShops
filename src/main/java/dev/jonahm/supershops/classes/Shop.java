package dev.jonahm.supershops.classes;

import dev.jonahm.supershops.utils.shop.ShopStorage;
import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.inventories.*;
import dev.jonahm.supershops.utils.display.FloatingText;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class Shop {

    private final Integer ID;
    private ItemStack item;
    private Integer storage, quantity;
    private Long price;
    private final ShopPlayer owner;
    private ShopType type;
    private final Location location;

    public Shop(Integer ID) {
        this.ID = ID;
        this.item = ShopStorage.getItem(ID);
        this.storage = ShopStorage.getStorage(ID);
        this.quantity = ShopStorage.getQuantity(ID);
        this.price = ShopStorage.getPrice(ID);
        this.type = ShopStorage.getShopType(ID);
        this.owner = new ShopPlayer(UUID.fromString(Objects.requireNonNull(ShopStorage.getOwnerUUID(ID))));
        this.location = ShopStorage.getLocation(ID);
    }

    public void reload() {
        this.item = ShopStorage.getItem(ID);
        this.storage = ShopStorage.getStorage(ID);
        this.quantity = ShopStorage.getQuantity(ID);
        this.price = ShopStorage.getPrice(ID);
        this.type = ShopStorage.getShopType(ID);
        final Shop shop = this;
        Bukkit.getScheduler().runTask(SuperShops.plugin, () -> FloatingText.updateShopText(shop));
    }

    public Integer getID() {
        return this.ID;
    }

    public ItemStack getItem() {
        return item == null ? null : new ItemStack(item);
    }

    public Integer getStorage() {
        if (storage == -1) {
            return quantity + 10000;
        }
        return storage;
    }

    public boolean isInfinite() {
        return storage == -1;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getPrice() {
        return price;
    }

    public ShopPlayer getOwner() {
        return owner;
    }

    public ShopType getType() { return type; }

    public Location getLocation() {
        return this.location;
    }

    public Inventory getBuyInventory() { return new BuyInventory().build(this); }

    public Inventory getSellInventory() { return new SellInventory().build(this); }

    public Inventory getShowingInventory() { return new ShowingInventory().build(this); }

    public Inventory getStorageInventory() { return new StorageInventory().build(this); }

    public Inventory getChangeItemInventory() { return new ChangeItemInventory().build(this); }

    public Inventory getSettingsInventory() { return new SettingsInventory().build(this); }



    public enum ShopType {
        BUYING,
        SELLING,
        SHOWING;
        public static ShopType fromString(String string) {
            switch (string.toLowerCase()) {
                case "buying":
                    return ShopType.BUYING;
                case "selling":
                    return ShopType.SELLING;
                case "showing":
                    return ShopType.SHOWING;
            }
            return SHOWING;
        }
        public static String formattedType(ShopType shopType) {
            switch (shopType) {
                case BUYING:
                    return "Buying";
                case SELLING:
                    return "Selling";
                case SHOWING:
                    return "Showing";
            }
            return "";
        }
    }

}
