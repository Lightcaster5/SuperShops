package dev.jonahm.supershops.inventories;

import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StorageInventory extends ShopInventory {

    public Inventory build(Shop shop) {
        size = 27;

        Inventory inventory = Bukkit.createInventory(null, size, "Storage - Player Shop");

        ItemStack minus1 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                .setName("§c§l-1")
                .addLoreLine("§7§o(( §f§oClick §7§oto withdraw §b§o§n1§r §7§ofrom the stack. ))")
                .toItemStack();

        ItemStack minus64 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                .setName("§c§l-64")
                .addLoreLine("§7§o(( §f§oClick §7§oto withdraw §b§o§n1§r §7§ofrom the stack. ))")
                .toItemStack();

        ItemStack minusAll = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                .setName("§c§lWithdraw All")
                .addLoreLine("§7§o(( §f§oClick §7§oto withdraw §b§o§nALL§r §7§ofrom the stack. ))")
                .toItemStack();

        ItemStack plus1 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setName("§a§l+1")
                .addLoreLine("§7§o(( §f§oClick §7§oto deposit §b§o§n1§r §7§oto the stack. ))")
                .toItemStack();

        ItemStack plus64 = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setName("§a§l+64")
                .addLoreLine("§7§o(( §f§oClick §7§oto deposit §b§o§n64§r §7§oto the stack. ))")
                .toItemStack();

        ItemStack plusAll = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setName("§a§lDeposit All")
                .addLoreLine("§7§o(( §f§oClick §7§oto deposit §b§o§nALL§r §7§oto the stack. ))")
                .toItemStack();

        ItemStack shopItem;

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
                .toItemStack();

        inventory.setItem(9, minusAll);
        inventory.setItem(10, minus64);
        inventory.setItem(11, minus1);
        inventory.setItem(13, shopItem);
        inventory.setItem(15, plus1);
        inventory.setItem(16, plus64);
        inventory.setItem(17, plusAll);

        return inventory;
    }

}
