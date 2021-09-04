package dev.jonahm.supershops.inventories;

import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.utils.item.ItemBuilder;
import dev.jonahm.supershops.SuperShops;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SellInventory extends ShopInventory {

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

        ItemStack buy1 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setName("§a§lBuy 1")
                .addLoreLine("§7§o(( §7§oClicking this will buy §b§o§n1§r §7§ofrom the shop. ))")
                .toItemStack();

        ItemStack buy64 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setName("§a§lBuy 64")
                .addLoreLine("§7§o(( §7§oClicking this will buy §b§o§n64§r §7§ofrom the shop. ))")
                .toItemStack();

        ItemStack buyAll = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setName("§a§lBuy All")
                .addLoreLine("§7§o(( §7§oClicking this will buy §b§o§nALL§r §7§ofrom the shop. ))")
                .toItemStack();

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
            String price = shop.getPrice() == 0 ? "§an/a" : "§a$" + SuperShops.nf.format(shop.getPrice());
            shopItem = new ItemBuilder(shopItem)
                    .addLoreLine("§e§m-----§r")
                    .addLoreLine("§6§l * §e§lQuantity: §f§l" + quantity + " / " + storage)
                    .addLoreLine("§6§l * §e§lPrice: " + price)
                    .toItemStack();
        }

        inventory.setItem(13, shopItem);
        inventory.setItem(29, buy1);
        inventory.setItem(31, buy64);
        inventory.setItem(33, buyAll);

        return inventory;
    }

}
