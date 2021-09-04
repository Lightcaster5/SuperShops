package dev.jonahm.supershops.events;

import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.classes.Activity;
import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.classes.ShopPlayer;
import dev.jonahm.supershops.inventories.ShopInventory;
import dev.jonahm.supershops.utils.Balance;
import dev.jonahm.supershops.utils.item.ItemName;
import dev.jonahm.supershops.utils.item.ItemUtils;
import dev.jonahm.supershops.utils.shop.ShopStorage;
import dev.jonahm.supershops.utils.string.CC;
import dev.jonahm.supershops.utils.string.StringFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ShopClickListener implements Listener {

    public static ArrayList<String> changeItemList = new ArrayList<>();
    public static HashMap<String, Shop> changePriceMap = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            ShopPlayer shopPlayer = new ShopPlayer((Player) event.getWhoClicked());
            if (changeItemList.contains(shopPlayer.getUUID())) {
                return;
            }
            Inventory inventory = event.getClickedInventory();
            ItemStack clickedItem = event.getCurrentItem();
            if (inventory == null || clickedItem == null) {
                return;
            }
            if (shopPlayer.getOpenedShop() != null) {
                ShopInventory.InventoryType inventoryType = shopPlayer.getInventoryType();
                event.setCancelled(true);
                if (event.getClick() == ClickType.DOUBLE_CLICK) {
                    return;
                }
                switch (inventoryType) {
                    case BUYING:
                        buyingLogic(event);
                        break;
                    case SELLING:
                        sellingLogic(event);
                        break;
                    case STORAGE:
                        storageLogic(event);
                        break;
                    case CHANGE_ITEM:
                        changeItemList.add(shopPlayer.getUUID());
                        break;
                    case SETTINGS:
                        settingsLogic(event);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void changeItem(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            ShopPlayer shopPlayer = new ShopPlayer((Player) event.getWhoClicked());
            if (changeItemList.contains(shopPlayer.getUUID())) {
                Inventory inventory = event.getClickedInventory();
                ItemStack clickedItem = event.getCurrentItem();
                if (inventory == null || clickedItem == null) {
                    return;
                }
                clickedItem = new ItemStack(clickedItem);
                if (shopPlayer.getOpenedShop() != null) {
                    Shop shop = shopPlayer.getOpenedShop();
                    event.setCancelled(true);
                    if (clickedItem.getType() == Material.AIR) {
                        return;
                    }
                    if (inventory.getType() == InventoryType.PLAYER) {
                        String name = ItemName.getItemName(clickedItem);
                        shopPlayer.sendMessage("§6§l[!]§e Player Shop item set to §7" + name + "§e.");
                        ShopStorage.setItem(shop.getID(), clickedItem);
                        shop.reload();
                        shopPlayer.runActivity(Activity.SETTINGS, shop);
                    }
                }
                changeItemList.remove(shopPlayer.getUUID());
            }
        }
    }

    public static void buyingLogic(InventoryClickEvent event) {
        ShopPlayer shopPlayer = new ShopPlayer((Player) event.getWhoClicked());
        Inventory inventory = event.getClickedInventory();
        if (inventory == shopPlayer.getPlayer().getInventory()) {
            return;
        }
        ItemStack clickedItem = event.getCurrentItem();
        Shop shop = shopPlayer.getOpenedShop();
        ClickType clickType = event.getClick();
        int slot = event.getSlot();
        long price = shop.getPrice(); // price of shop item
        int quantity = shop.getQuantity(); // current shop item quantity
        int storage = shop.getStorage(); // shop max storage size
        int space = ItemUtils.getSpace(shopPlayer.getPlayer(), shop.getItem()); // how much space does the shopPlayer have for this item
        int amount = ItemUtils.getAmount(shopPlayer.getPlayer(), shop.getItem()); // how much of this item does shopPlayer have
        String itemName = shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasDisplayName() ?
                shop.getItem().getItemMeta().getDisplayName() : ItemName.getItemName(shop.getItem());
        switch (slot) {
            case 29:
                // let shop owner buy one
                if (amount >= 1) {
                    if (quantity < storage) {
                        if (Balance.getBalance(shop.getOwner()) >= price) {
                            takeItems(shopPlayer, 1, itemName, shop);
                        } else {
                            shopPlayer.sendMessage("§4§l[!]§c §7" + shop.getOwner().getName() + "§c cannot afford that");
                        }
                    } else {
                        shopPlayer.sendMessage("§4§l[!]§c Player Shop is full");
                    }
                } else {
                    shopPlayer.sendMessage("§4§l[!]§c You do not have any §7" + itemName + "§c to deposit");
                }
                break;
            case 31:
                // let shop owner buy 64
                if (amount >= 1) {
                    if (quantity < storage) {
                        if (Balance.getBalance(shop.getOwner()) >= price) {
                            if (amount < 64) {
                                if (storage - quantity >= amount) {
                                    if (Balance.getBalance(shop.getOwner()) >= price * amount) {
                                        takeItems(shopPlayer, amount, itemName, shop);
                                    } else {
                                        int maxAmount = (int) (Balance.getBalance(shop.getOwner()) / price);
                                        takeItems(shopPlayer, maxAmount, itemName, shop);
                                    }
                                } else {
                                    // sell all the shop can hold

                                    if (Balance.getBalance(shop.getOwner()) >= price * (storage - quantity)) {
                                        takeItems(shopPlayer, storage - quantity, itemName, shop);
                                    } else {
                                        int maxAmount = (int) (Balance.getBalance(shop.getOwner()) / price);
                                        takeItems(shopPlayer, maxAmount, itemName, shop);
                                    }
                                }
                            } else {
                                if (storage - quantity >= 64) {
                                    // sell 64
                                    if (Balance.getBalance(shop.getOwner()) >= price * 64) {
                                        takeItems(shopPlayer, 64, itemName, shop);
                                    } else {
                                        int maxAmount = (int) (Balance.getBalance(shop.getOwner()) / price);
                                        takeItems(shopPlayer, maxAmount, itemName, shop);
                                    }
                                } else {
                                    // sell what the shop can hold
                                    if (Balance.getBalance(shop.getOwner()) >= price * (storage - quantity)) {
                                        takeItems(shopPlayer, (storage - quantity), itemName, shop);
                                    } else {
                                        int maxAmount = (int) (Balance.getBalance(shop.getOwner()) / price);
                                        takeItems(shopPlayer, maxAmount, itemName, shop);
                                    }
                                }
                            }
                        } else {
                            shopPlayer.sendMessage("§4§l[!]§c §7" + shop.getOwner().getName() + "§c cannot afford that");
                        }
                    } else {
                        shopPlayer.sendMessage("§4§l[!]§c Player Shop is full");
                    }
                } else {
                    shopPlayer.sendMessage("§4§l[!]§c You do not have any §7" + itemName + "§c to deposit");
                }
                break;
            case 33:
                // let shop owner buy all
                if (amount >= 1) {
                    if (Balance.getBalance(shop.getOwner()) >= price) {
                        if (amount >= storage - quantity) {
                            // sell all that the shop can hold
                            if (Balance.getBalance(shop.getOwner()) >= price * (storage - quantity)) {
                                takeItems(shopPlayer, (storage - quantity), itemName, shop);
                            } else {
                                int maxAmount = (int) (Balance.getBalance(shop.getOwner()) / price);
                                takeItems(shopPlayer, maxAmount, itemName, shop);
                            }
                        } else {
                            // sell what the player has
                            if (Balance.getBalance(shop.getOwner()) >= price * amount) {
                                takeItems(shopPlayer, amount, itemName, shop);
                            } else {
                                int maxAmount = (int) (Balance.getBalance(shop.getOwner()) / price);
                                takeItems(shopPlayer, maxAmount, itemName, shop);
                            }
                        }
                    } else {
                        shopPlayer.sendMessage("§4§l[!]§c §7" + shop.getOwner().getName() + "§c cannot afford that");
                    }
                } else {
                    shopPlayer.sendMessage("§4§l[!]§c You do not have any §7" + itemName + "§c to deposit");
                }
                break;
        }

    }

    public static void sellingLogic(InventoryClickEvent event) {
        ShopPlayer shopPlayer = new ShopPlayer((Player) event.getWhoClicked());
        Inventory inventory = event.getClickedInventory();
        if (inventory == shopPlayer.getPlayer().getInventory()) {
            return;
        }
        ItemStack clickedItem = event.getCurrentItem();
        Shop shop = shopPlayer.getOpenedShop();
        ClickType clickType = event.getClick();
        int slot = event.getSlot();
        long price = shop.getPrice();
        int quantity = shop.getQuantity();
        int storage = shop.getStorage();
        int space = ItemUtils.getSpace(shopPlayer.getPlayer(), shop.getItem());
        int amount = ItemUtils.getAmount(shopPlayer.getPlayer(), shop.getItem());
        String itemName = shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasDisplayName() ?
                shop.getItem().getItemMeta().getDisplayName() : ItemName.getItemName(shop.getItem());
        switch (slot) {
            case 29:
                if (shop.getPrice() > 0) {
                    double balance = Balance.getBalance(shopPlayer);
                    if (quantity > 0) {
                        if (balance >= price) {
                            if (space > 0) {
                                giveItems(shopPlayer, 1, itemName, shop);
                            } else {
                                shopPlayer.sendMessage("§4§l[!]§c Inventory full");
                            }
                        } else {
                            shopPlayer.sendMessage("§4§l[!]§c Insufficient funds");
                        }
                    } else {
                        shopPlayer.sendMessage("§4§l[!]§c Player Shop is empty");
                    }
                } else {
                    shopPlayer.sendMessage("§4§l[!]§c Player shop is not setup");
                }
                break;
            case 31:
                if (shop.getPrice() > 0) {
                    double balance = Balance.getBalance(shopPlayer);
                    if (quantity > 0) {
                        if (balance >= price) {
                            if (space > 0) {
                                if (quantity < 64) {
                                    if (balance >= quantity * price) {
                                        if (space >= quantity) {
                                            // buy the rest
                                            giveItems(shopPlayer, quantity, itemName, shop);

                                        } else {
                                            // what their inv can hold
                                            giveItems(shopPlayer, space, itemName, shop);
                                        }

                                    } else {
                                        // buy how much their money can buy
                                        int maxAmount = (int) (balance / price);
                                        if (space >= maxAmount) {
                                            System.out.println("c");
                                            // buy the max amount
                                            giveItems(shopPlayer, maxAmount, itemName, shop);
                                        } else {
                                            System.out.println("d");
                                            // buy what their inv can hold
                                            giveItems(shopPlayer, space, itemName, shop);
                                        }
                                    }
                                } else {
                                    if (balance >= 64 * price) {
                                        if (space >= 64) {
                                            System.out.println("a");
                                            // buy 64
                                            giveItems(shopPlayer, 64, itemName, shop);
                                        } else {
                                            System.out.println("b");
                                            // buy what their inv can hold
                                            giveItems(shopPlayer, space, itemName, shop);
                                        }
                                    } else {
                                        int maxAmount = (int) (balance / price);
                                        if (space >= 64) {
                                            System.out.println("f");
                                            // buy 64
                                            giveItems(shopPlayer, maxAmount, itemName, shop);
                                        } else {
                                            System.out.println("e");
                                            // buy what their inv can hold
                                            giveItems(shopPlayer, Math.min(space, maxAmount), itemName, shop);
                                        }
                                    }
                                }
                            } else {
                                shopPlayer.sendMessage("§4§l[!]§c Inventory full");
                            }
                        } else {
                            shopPlayer.sendMessage("§4§l[!]§c Insufficient funds");
                        }
                    } else {
                        shopPlayer.sendMessage("§4§l[!]§c Player Shop is empty");
                    }
                } else {
                    shopPlayer.sendMessage("§4§l[!]§c Player shop is not setup");
                }
                break;
            case 33:
                if (shop.getPrice() > 0) {
                    double balance = Balance.getBalance(shopPlayer);
                    if (quantity > 0) {
                        if (balance >= price) {
                            if (space > 0) {
                                int maxAmount = (int) (balance / price);

                                if (space <= maxAmount) {
                                    giveItems(shopPlayer, Math.min(quantity, space), itemName, shop);
                                } else {
                                    giveItems(shopPlayer, Math.min(quantity, maxAmount), itemName, shop);
                                }

                            } else {
                                shopPlayer.sendMessage("§4§l[!]§c Inventory full");
                            }
                        } else {
                            shopPlayer.sendMessage("§4§l[!]§c Insufficient funds");
                        }
                    } else {
                        shopPlayer.sendMessage("§4§l[!]§c Player Shop is empty");
                    }
                } else {
                    shopPlayer.sendMessage("§4§l[!]§c Player shop is not setup");
                }
                break;
        }
    }

    public static void storageLogic(InventoryClickEvent event) {
        ShopPlayer shopPlayer = new ShopPlayer((Player) event.getWhoClicked());
        Inventory inventory = event.getClickedInventory();
        if (inventory == shopPlayer.getPlayer().getInventory()) {
            return;
        }
        Shop shop = shopPlayer.getOpenedShop();
        int slot = event.getSlot();
        int quantity = shop.getQuantity();
        int storage = shop.getStorage();
        int space = ItemUtils.getSpace(shopPlayer.getPlayer(), shop.getItem());
        int amount = ItemUtils.getAmount(shopPlayer.getPlayer(), shop.getItem());
        String itemName = shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasDisplayName() ?
                shop.getItem().getItemMeta().getDisplayName() : ItemName.getItemName(shop.getItem());
        switch (slot) {
            case 9:
                if (quantity < 1) {
                    shopPlayer.sendMessage("§4§l[!]§c Player Shop is empty");
                } else {
                    removeShopAmount(shopPlayer, shop, Math.min(space, quantity));
                }
                break;
            case 10:
                if (quantity < 1) {
                    shopPlayer.sendMessage("§4§l[!]§c Player Shop is empty");
                } else {
                    if (64 > quantity) {
                        removeShopAmount(shopPlayer, shop, quantity);
                    } else {
                        removeShopAmount(shopPlayer, shop, 64);
                    }
                }
                break;
            case 11:
                if (quantity < 1) {
                    shopPlayer.sendMessage("§4§l[!]§c Player Shop is empty");
                } else {
                    removeShopAmount(shopPlayer, shop, 1);
                }
                break;
            case 15:
                if (quantity >= storage) {
                    shopPlayer.sendMessage("§4§l[!]§c Your Player Shop is full.");
                } else {
                    if (amount < 1) {
                        shopPlayer.sendMessage("§4§l[!]§c You do not have any §7" + itemName + "§c to deposit.");
                    } else {
                        addShopAmount(shopPlayer, shop, 1);
                    }
                }
                break;
            case 16:
                if (quantity >= storage) {
                    shopPlayer.sendMessage("§4§l[!]§c Your Player Shop is full.");
                } else {
                    if (amount < 1) {
                        shopPlayer.sendMessage("§4§l[!]§c You do not have any §7" + itemName + "§c to deposit.");
                    } else {
                        if (64 < storage - quantity) {
                            addShopAmount(shopPlayer, shop, Math.min(amount, 64));
                        } else {
                            addShopAmount(shopPlayer, shop, Math.min(amount, storage - quantity));
                        }
                    }
                }
                break;
            case 17:
                if (quantity >= storage) {
                    shopPlayer.sendMessage("§4§l[!]§c Your Player Shop is full.");
                } else {
                    if (amount < 1) {
                        shopPlayer.sendMessage("§4§l[!]§c You do not have any §7" + itemName + "§c to deposit.");
                    } else {
                        addShopAmount(shopPlayer, shop, Math.min(amount, storage - quantity));
                    }
                }
                break;
        }
        shop.reload();
        shopPlayer.runActivity(Activity.STORAGE, shop);
    }


    public static void takeItems(ShopPlayer shopPlayer, int amount, String itemName, Shop shop) {
        ShopStorage.setQuantity(shop.getID(), shop.getQuantity() + amount);
        shop.reload();
        shopPlayer.runActivity(Activity.BUYING, shop);
        shopPlayer.sendMessage("§6§l[!]§e You sold §fx" + SuperShops.nf.format(amount) + " §7" + itemName + "§e for §a$" + StringFormatter.format(shop.getPrice() * amount));
        ItemUtils.takeAmount(shopPlayer.getPlayer(), shop.getItem(), amount);
        Balance.giveMoney(shopPlayer, shop.getPrice() * amount);
        Balance.takeMoney(shop.getOwner(), shop.getPrice() * amount);
        if (Bukkit.getPlayer(UUID.fromString(shop.getOwner().getUUID())) != null) {
            Bukkit.getPlayer(UUID.fromString(shop.getOwner().getUUID())).sendMessage(
                    "§6§l[!]§e Player Shop bought §fx" + SuperShops.nf.format(amount) + " §7" + itemName + "§e for §a$" + StringFormatter.format(shop.getPrice() * amount) + "§e from " + shopPlayer.getName()
            );
        }
    }

    /**
     *
     * @param shopPlayer the player to be given the items
     * @param amount amount of items to give the player
     * @param itemName name of the item for chat messages
     * @param shop the shop being bought from
     */
    public static void giveItems(ShopPlayer shopPlayer, int amount, String itemName, Shop shop) {
        ShopStorage.setQuantity(shop.getID(), shop.getQuantity() - amount);
        shop.reload();
        shopPlayer.runActivity(Activity.SELLING, shop);
        shopPlayer.sendMessage("§6§l[!]§e You bought §fx" + SuperShops.nf.format(amount) + " §7" + itemName + "§e for §a$" + StringFormatter.format(shop.getPrice() * amount));
        ItemUtils.spreadItems(shopPlayer.getPlayer(), shop.getItem(), amount);
        Balance.takeMoney(shopPlayer, shop.getPrice() * amount);
        Balance.giveMoney(shop.getOwner(), shop.getPrice() * amount);
        if (Bukkit.getPlayer(UUID.fromString(shop.getOwner().getUUID())) != null) {
            Bukkit.getPlayer(UUID.fromString(shop.getOwner().getUUID())).sendMessage(
                    "§6§l[!]§e Player Shop sold §fx" + SuperShops.nf.format(amount) + " §7" + itemName + "§e for §a$" + StringFormatter.format(shop.getPrice() * amount) + "§e to " + shopPlayer.getName()
            );
        }
    }

    public static void addShopAmount(ShopPlayer shopPlayer, Shop shop, Integer amount) {
        int quantity = shop.getQuantity();
        String itemName = shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasDisplayName() ?
                shop.getItem().getItemMeta().getDisplayName() : ItemName.getItemName(shop.getItem());
        ItemUtils.takeAmount(shopPlayer.getPlayer(), shop.getItem(), amount);
        shopPlayer.sendMessage("§6§l[!]§e You deposited §7x§f" + SuperShops.nf.format(amount) +
                " §7" + itemName + "§e.");
        ShopStorage.setQuantity(shop.getID(), quantity + amount);
    }

    public static void removeShopAmount(ShopPlayer shopPlayer, Shop shop, Integer amount) {
        int quantity = shop.getQuantity();
        String itemName = shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasDisplayName() ?
                shop.getItem().getItemMeta().getDisplayName() : ItemName.getItemName(shop.getItem());
        ItemUtils.spreadItems(shopPlayer.getPlayer(), shop.getItem(), amount);
        shopPlayer.sendMessage("§6§l[!]§e You withdrew §7x§f" + SuperShops.nf.format(amount) +
                " §7" + itemName + "§e.");
        ShopStorage.setQuantity(shop.getID(), quantity - amount);
    }

    public static void settingsLogic(InventoryClickEvent event) {
        ShopPlayer shopPlayer = new ShopPlayer((Player) event.getWhoClicked());
        Inventory inventory = event.getClickedInventory();
        if (inventory == shopPlayer.getPlayer().getInventory()) {
            return;
        }
        Shop shop = shopPlayer.getOpenedShop();
        ClickType clickType = event.getClick();
        int slot = event.getSlot();
        switch (slot) {
            case 13:
                if (shop.getItem() == null) {
                    shopPlayer.runActivity(Activity.CHANGE_ITEM, shop);
                    changeItemList.add(shopPlayer.getUUID());
                } else {
                    switch (clickType) {
                        case LEFT:
                            if (shop.getQuantity() > 0) {
                                shopPlayer.sendMessage("§4§l[!]§c Clear out your storage before changing the item.");
                            } else {
                                shopPlayer.runActivity(Activity.CHANGE_ITEM, shop);
                                changeItemList.add(shopPlayer.getUUID());
                            }
                            break;
                        case RIGHT:
                            shopPlayer.runActivity(Activity.STORAGE, shop);
                            break;
                    }
                    break;
                }
                break;
            case 27:
                shopPlayer.sendMessage(CC.color("&4&l[!]&c This feature is coming soon"));
                break;
            case 30:
                Shop.ShopType shopType = shop.getType();
                switch (shopType) {
                    case SHOWING:
                        ShopStorage.setShopType(shop.getID(), Shop.ShopType.BUYING);
                        shopPlayer.sendMessage("§6§l[!]§e Player Shop mode set to Buy.");
                        break;
                    case BUYING:
                        ShopStorage.setShopType(shop.getID(), Shop.ShopType.SELLING);
                        shopPlayer.sendMessage("§6§l[!]§e Player Shop mode set to Sell.");
                        break;
                    case SELLING:
                        ShopStorage.setShopType(shop.getID(), Shop.ShopType.SHOWING);
                        shopPlayer.sendMessage("§6§l[!]§e Player Shop mode set to Show.");
                        break;
                }
                shop.reload();
                shopPlayer.runActivity(Activity.SETTINGS, shop);
                break;
            case 32:
                changePriceMap.put(shopPlayer.getUUID(), shopPlayer.getOpenedShop());
                shopPlayer.sendMessage("§2§l[!]§a Enter an amount for your Player Shop");
                shopPlayer.getPlayer().closeInventory();
                break;
            case 35:
                int quantity = shop.getQuantity();
                if (quantity == 0) {
                    Location location = shop.getLocation();
                    if (shopPlayer.getPlayer().getInventory().addItem(ItemUtils.getSuperShopItem(shop.getStorage())).size() == 0) {
                        location.getWorld().getBlockAt(location).setType(Material.AIR);
                        ShopStorage.removeShop(shop.getID());
                        shopPlayer.setOpenedShop(null);
                        shopPlayer.setInventoryType(null);
                        shopPlayer.getPlayer().closeInventory();
                        shopPlayer.sendMessage("§4§l[!]§c Your Player Shop has been removed!");
                    } else {
                        shopPlayer.sendMessage("§4§l[!]§c Your inventory is full!");
                    }
                } else {
                    shopPlayer.sendMessage("§4§l[!]§c Clear out your storage before removing your shop.");
                }
                break;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        ShopPlayer shopPlayer = new ShopPlayer(event.getPlayer());
        Shop shop = changePriceMap.get(shopPlayer.getUUID());
        String message = event.getMessage();
        if (shop == null) {
            changePriceMap.remove(shopPlayer.getUUID());
            return;
        }
        if (changePriceMap.containsKey(shopPlayer.getUUID())) {
            event.setCancelled(true);
            changePriceMap.remove(shopPlayer.getUUID());
            message = message.replace(",", "");
            try {
                long amount = Long.parseLong(message);
                if (amount > 0) {
                    String stringAmount = StringFormatter.format(amount);
                    shopPlayer.sendMessage("§6§l[!]§e Player Shop price set to " + stringAmount + ".");
                    ShopStorage.setPrice(shop.getID(), amount);
                    shop.reload();
                } else {
                    System.out.println("b = " + event.getMessage());
                    shopPlayer.sendMessage("§4§l[!]§c Please enter a valid number");
                }
                shopPlayer.runActivity(Activity.SETTINGS, shop);
            } catch (NumberFormatException e) {
                System.out.println("a = " + event.getMessage());
                shopPlayer.sendMessage("§4§l[!]§c Please enter a valid number");
                shopPlayer.runActivity(Activity.SETTINGS, shop);
            }
        }
    }

}
