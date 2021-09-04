package dev.jonahm.supershops.inventories;

import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChangeItemInventory extends ShopInventory {

    public Inventory build(Shop shop) {
        size = 27;
        Inventory inventory = Bukkit.createInventory(null, size, "Set Item - Player Shop");

        for (int i = 0; i < size; i++) {
            if (i >= 3 && i <= 5 || i >= 12 && i <= 14 || i >= 21 && i <= 23) {
                inventory.setItem(i, yellow);
            } else {
                inventory.setItem(i, gray);
            }
        }

        ItemStack setItem = new ItemBuilder(Material.ITEM_FRAME)
                .setName("§4§l[!]§c§l SET SHOP ITEM §7(Click)")
                .addLoreLine("")
                .addLoreLine("§7§o(( §f§oClick §7§othe item in your §a§oinventory")
                .addLoreLine("§7§oyou want to select for your §c§oPlayer Shop§7§o. ))")
                .addEnchant(Enchantment.DURABILITY, 1)
                .hideItemFlags()
                .toItemStack();

        inventory.setItem(13, setItem);

        return inventory;
    }
}
