package dev.jonahm.supershops.inventories;

import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.utils.item.ItemBuilder;
import dev.jonahm.supershops.utils.string.StringFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SettingsInventory extends ShopInventory {

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

        ItemStack advertise = new ItemBuilder(Material.JUKEBOX)
                .setName("§2§l[!]§a§l ADVERTISEMENT §7(Click)")
                .addLoreLine("")
                .addLoreLine("§2Click §7to §aadvertise §7throughout the §aentire server§7.")
                .addLoreLine("§c(Cooldown of 180 seconds)")
                .addLoreLine("")
                .addLoreLine("§2§l * §a§lUSES REMAINING:§f 0")
                .toItemStack();

        ItemStack mode = new ItemBuilder(Material.SIGN)
                .setName("§5§l[!]§d§l SHOP MODE §7(Click)")
                .addLoreLine("")
                .addLoreLine("§5§l * §7Buying")
                .addLoreLine("§5§l * §7Selling")
                .addLoreLine("§5§l * §7Showing")
                .addLoreLine("")
                .addLoreLine("§7§o(( §f§oClick §7§oto cycle between modes. ))")
                .toItemStack();

        switch (shop.getType()) {
            case BUYING:
                mode = new ItemBuilder(mode)
                        .setLore("§5§l * §6> §fBuying §6<", 1)
                        .toItemStack();
                break;
            case SELLING:
                mode = new ItemBuilder(mode)
                        .setLore("§5§l * §6> §fSelling §6<", 2)
                        .toItemStack();
                break;
            case SHOWING:
                mode = new ItemBuilder(mode)
                        .setLore("§5§l * §6> §fShowing §6<", 3)
                        .toItemStack();
                break;
        }

        ItemStack price = new ItemBuilder(Material.EMERALD)
                .setName("§2§l[!]§a§l SET PRICE §7(Click)")
                .addLoreLine("")
                .addLoreLine("§2§l * §a§lCURRENT PRICE:§f n/a")
                .addLoreLine("")
                .addLoreLine("§7§o(( §f§fClick §7§oto adjust the §a§oasking price")
                .addLoreLine("§7§oof your §c§oPlayer Shop §7§oitem. ))")
                .toItemStack();

        if (shop.getPrice() != 0) {
            String stringPrice = StringFormatter.format(shop.getPrice());
            price = new ItemBuilder(price)
                    .setLore("§2§l * §a§lCURRENT PRICE:§f " + stringPrice, 1)
                    .toItemStack();
        }

        ItemStack despawn = new ItemBuilder(Material.BARRIER)
                .setName("§4§l[!]§c§l REMOVE SHOP §7(Click)")
                .addLoreLine("")
                .addLoreLine("§7Once clicked, your §cPlayer Shop will despawn§7,")
                .addLoreLine("§7and will be placed §ainto your inventory§7.")
                .addLoreLine("")
                .addLoreLine("§b§oNOTE: §f§oYou must empty all of the contents first.")
                .toItemStack();

        ItemStack shopItem;

        if (shop.getItem() == null) {
            shopItem = new ItemBuilder(Material.BARRIER)
                    .setName("§cAbsolutely nothing!")
                    .addLoreLine("§7§o(( §f§oClick§7§o to set the item. ))")
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
            shopItem = new ItemBuilder(shopItem)
                    .addLoreLine("§e§m-----§r")
                    .addLoreLine("§6§l * §e§lQuantity: §f§l" + quantity + " / " + storage)
                    .addLoreLine("§7§o(( §f§oLeft Click §7§oto set the item. ))")
                    .addLoreLine("§7§o(( §f§oRight Click §7§oto access storage. ))")
                    .toItemStack();
        }

        inventory.setItem(27, advertise);
        inventory.setItem(30, mode);
        inventory.setItem(32, price);
        inventory.setItem(35, despawn);
        inventory.setItem(13, shopItem);

        return inventory;
    }

}
