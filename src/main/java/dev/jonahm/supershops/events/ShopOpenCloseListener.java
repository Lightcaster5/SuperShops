package dev.jonahm.supershops.events;

import dev.jonahm.supershops.classes.Activity;
import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.classes.ShopPlayer;
import dev.jonahm.supershops.utils.shop.ShopStorage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShopOpenCloseListener implements Listener {

    @EventHandler
    public void shopOpen(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ShopPlayer shopPlayer = new ShopPlayer(event.getPlayer());
            Block block = event.getClickedBlock();
            if (block == null) return;
            if (block.getType() == Material.ENCHANTMENT_TABLE) {
                Shop shop = ShopStorage.getShop(block);
                if (shop != null) {
                    event.setCancelled(true);
                    shopPlayer.setOpenedShop(shop);
                    if (shop.getOwner().getName().equals(shopPlayer.getName())) {
                        shopPlayer.runActivity(Activity.SETTINGS, shop);
                    } else {
                        Shop.ShopType shopType = shop.getType();
                        switch (shopType) {
                            case BUYING:
                                shopPlayer.runActivity(Activity.BUYING, shop);
                                break;
                            case SELLING:
                                shopPlayer.runActivity(Activity.SELLING, shop);
                                break;
                            case SHOWING:
                                shopPlayer.runActivity(Activity.SHOWING, shop);
                                break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void shopClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            ShopPlayer shopPlayer = new ShopPlayer((Player) event.getPlayer());
            shopPlayer.setOpenedShop(null);
            shopPlayer.setInventoryType(null);
        }
    }

}
