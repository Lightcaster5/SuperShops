package dev.jonahm.supershops.inventories;

import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.utils.item.ItemBuilder;
import dev.jonahm.supershops.SuperShops;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShowingInventory extends ShopInventory {

    public Inventory build(Shop shop) {
        size = 27;
        Inventory inventory = Bukkit.createInventory(null, size, "Player Shop");

        for (int i = 0; i < size; i++) {
            if (i >= 3 && i <= 5 || i >= 12 && i <= 14 || i >= 21 && i <= 23) {
                inventory.setItem(i, yellow);
            } else {
                inventory.setItem(i, gray);
            }
        }

        ItemStack shopItem;

        if (shop.getItem() == null) {
            shopItem = new ItemBuilder(Material.BARRIER)
                    .setName("§cAbsolutely nothing!")
                    .toItemStack();
        } else {
            if (shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasDisplayName()) {
                shopItem = new ItemBuilder(shop.getItem())
                        .setName(shop.getItem().getItemMeta().getDisplayName())
                        .toItemStack();
            } else {
                shopItem = shop.getItem();
            }

            if (shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasLore()) {
                shopItem = new ItemBuilder(shopItem)
                        .setLore(shop.getItem().getItemMeta().getLore())
                        .toItemStack();
            }

            String quantity = "x" + SuperShops.nf.format(shop.getQuantity());
            String storage = "x" + SuperShops.nf.format(shop.getStorage());
            if (shop.isInfinite())
                storage = "oo";
            String price = shop.getPrice() == 0 ? "§fn/a" : "§a$" + SuperShops.nf.format(shop.getPrice());
            shopItem = new ItemBuilder(shopItem)
                    .addLoreLine("§e§m-----§r")
                    .addLoreLine("§6§l * §e§lQuantity: §f§l" + quantity + " / " + storage)
                    .addLoreLine("§6§l * §e§lPrice: " + price)
                    .toItemStack();
        }

        inventory.setItem(13, shopItem);

        return inventory;
    }

}
