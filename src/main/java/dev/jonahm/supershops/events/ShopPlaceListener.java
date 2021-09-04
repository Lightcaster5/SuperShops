package dev.jonahm.supershops.events;

import dev.jonahm.supershops.classes.ShopPlayer;
import dev.jonahm.supershops.utils.item.ItemUtils;
import dev.jonahm.supershops.utils.shop.ShopStorage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ShopPlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void shopPlace(BlockPlaceEvent event) {
        ShopPlayer shopPlayer = new ShopPlayer(event.getPlayer());
        Block block = event.getBlock();
        ItemStack item = event.getItemInHand();
        if (ItemUtils.isSuperShopItem(item)) {
            int size = ItemUtils.getSuperShopItemSize(item);
            ShopStorage.createShop(shopPlayer, block, size);
        }
    }

    @EventHandler
    public void shopBreak(BlockBreakEvent event) {
        ShopPlayer shopPlayer = new ShopPlayer(event.getPlayer());
        Block block = event.getBlock();
        if (block.getType() == Material.ENCHANTMENT_TABLE) {
            if (ShopStorage.getShop(block) != null) {
                event.setCancelled(true);
            }
        }
    }

}
