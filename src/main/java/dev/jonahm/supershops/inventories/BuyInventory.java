package dev.jonahm.supershops.inventories;

import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.utils.item.ItemBuilder;
import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.utils.string.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BuyInventory extends ShopInventory {

    public Inventory build(Shop shop) {
        Inventory inventory = Bukkit.createInventory(null, size, "Player Shop");
        for (int i = 0; i < 36; i++) {
            if (i >= 3 && i <= 5 || i >= 12 && i <= 14 || i >= 21 && i <= 23) {
                inventory.setItem(i, yellow);
            } else {
                if (i >= 27) {
                    inventory.setItem(i, white);
                } else {
                    inventory.setItem(i, gray);
                }
            }
        }

        ItemStack sell1 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                .setName(CC.color("&c&lSell 1"))
                .addLoreLine(CC.color("&7&o(( &7&oClicking this will sell &b&o&n1&r &7&oto the shop. ))"))
                .toItemStack();

        ItemStack sell64 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                .setName(CC.color("&c&lSell 64"))
                .addLoreLine(CC.color("&7&o(( &7&oClicking this will sell &b&o&n64&r &7&oto the shop. ))"))
                .toItemStack();

        ItemStack sellAll = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                .setName(CC.color("&c&lSell All"))
                .addLoreLine(CC.color("&7&o(( &7&oClicking this will sell &b&o&nALL&r &7&oto the shop. ))"))
                .toItemStack();

        ItemStack shopItem;

        if (shop.getItem() == null) {
            shopItem = new ItemBuilder(Material.BARRIER)
                    .setName(CC.color("&cAbsolutely nothing!"))
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
            String price = shop.getPrice() == 0 ? "&an/a" : "&a$" + SuperShops.nf.format(shop.getPrice());
            shopItem = new ItemBuilder(shopItem)
                    .addLoreLine(CC.color("&e&m-----&r"))
                    .addLoreLine(CC.color("&6&l * &e&lQuantity: &f&l" + quantity + " / " + storage))
                    .addLoreLine(CC.color("&6&l * &e&lPrice: " + price))
                    .toItemStack();
        }

        inventory.setItem(13, shopItem);
        inventory.setItem(29, sell1);
        inventory.setItem(31, sell64);
        inventory.setItem(33, sellAll);

        return inventory;
    }
}
