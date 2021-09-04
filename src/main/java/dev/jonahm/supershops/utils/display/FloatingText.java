package dev.jonahm.supershops.utils.display;

import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.utils.item.ItemName;
import dev.jonahm.supershops.utils.shop.ShopStorage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FloatingText {

    public static HashMap<String, Shop> hologramsCache = new HashMap<>();

    public static void createShopText(Shop shop) {
        Location location = shop.getLocation().clone().add(0.5, 2, 0.5);
        ArrayList<ArmorStand> armorStandsList = new ArrayList<>();
        String[] lines = getLines(shop);
        int count = 0;

        if (lines != null) {
            for (String line : lines) {
                ArmorStand armorStandLine = location.getWorld().spawn(location.clone().subtract(0, (count * 0.24), 0),
                        ArmorStand.class);
                armorStandLine.setCustomName(line);
                armorStandsList.add(armorStandLine);
                count++;
            }
        }

        ArrayList<String> entityList = new ArrayList<>();
        for (ArmorStand armorStand : armorStandsList) {
            armorStand.setMarker(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomNameVisible(true);
            if (armorStand.getCustomName().equals("null")) {
                armorStand.setCustomNameVisible(false);
            }
            hologramsCache.put(String.valueOf(armorStand.getUniqueId()), shop);
            entityList.add(String.valueOf(armorStand.getUniqueId()));
        }

        ArmorStand floatingItem = shop.getLocation().clone().getWorld().spawn(shop.getLocation().clone().add(0.54, 0.4, 0.23), ArmorStand.class);
        floatingItem.setArms(true);
        floatingItem.setRightArmPose(new EulerAngle(-0.275, -0.8, 0));
        floatingItem.setLeftArmPose(new EulerAngle(0, 0, 0));
        floatingItem.setItemInHand(shop.getItem() == null ? new ItemStack(Material.BARRIER) : shop.getItem());
        floatingItem.setMarker(true);
        floatingItem.setVisible(false);
        floatingItem.setGravity(false);
        floatingItem.setCanPickupItems(false);
        floatingItem.setCustomNameVisible(false);
        floatingItem.setRemoveWhenFarAway(false);
        floatingItem.setRemoveWhenFarAway(false);
        hologramsCache.put(String.valueOf(floatingItem.getUniqueId()), shop);

        ShopStorage.setHolograms(shop.getID(), entityList);
        ShopStorage.setFloatingItem(shop.getID(), String.valueOf(floatingItem.getUniqueId()));
    }

    public static void updateShopText(Shop shop) {
        Location location = shop.getLocation();
        List<String> entityList = ShopStorage.getHolograms(shop.getID());
        if (entityList != null) {
            int lineNumber = 0;
            for (Entity entity : location.getWorld().getLivingEntities()) {
                if (entityList.contains(String.valueOf(entity.getUniqueId()))) {
                    String[] lines = getLines(shop);

                    entity.setCustomName(lines[lineNumber]);
                    entity.setCustomNameVisible(true);

                    if (lines[lineNumber].equals("null")) {
                        entity.setCustomNameVisible(false);
                    }

                    lineNumber++;
                }
            }
        }
        String floatingUUID = ShopStorage.getFloatingItem(shop.getID());
        for (Entity entity : location.getWorld().getLivingEntities()) {
            if (entity.getUniqueId().toString().equalsIgnoreCase(floatingUUID)) {
                ArmorStand floatingItem = (ArmorStand) entity;
                floatingItem.teleport(shop.getLocation().clone().add(0.54, 0.4, 0.23));
                floatingItem.setArms(true);
                floatingItem.setRightArmPose(new EulerAngle(-0.275, -0.8, 0));
                floatingItem.setLeftArmPose(new EulerAngle(0, 0, 0));
                floatingItem.setItemInHand(shop.getItem() == null ? new ItemStack(Material.BARRIER) : shop.getItem());
                floatingItem.setMarker(true);
                floatingItem.setVisible(false);
                floatingItem.setGravity(false);
                floatingItem.setCanPickupItems(false);
                floatingItem.setCustomNameVisible(false);
                floatingItem.setRemoveWhenFarAway(false);
                floatingItem.setRemoveWhenFarAway(false);
            }
        }
    }

    public static String[] getLines(Shop shop) {
        String owner = shop.getOwner().getName();
        Shop.ShopType shopType = shop.getType();
        Integer quantity = shop.getQuantity();
        Integer storage = shop.getStorage();
        String price = shop.getPrice() == 0 ? "n/a" : "$" + SuperShops.nf.format(shop.getPrice());
        String[] lines = null;
        String itemName =
                shop.getItem() == null ? "§cAbsolutely nothing!" :
                        (shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasDisplayName() ?
                                shop.getItem().getItemMeta().getDisplayName() :
                                "§7" + ItemName.getItemName(shop.getItem()));
        switch (shopType) {
            case SHOWING:
                lines = new String[]{"null", "§e" + owner + "§d§o is Showing...", itemName};
                break;
            case SELLING:
                String quantityString = "§f (x" + SuperShops.nf.format(quantity) + ")";
                lines = new String[]{"§e" + owner + "§d§o is Selling...", shop.getItem() == null ?
                        itemName : itemName + quantityString,
                        "§fPrice: §a" + price};
                break;
            case BUYING:
                String spaceString = "§f (x" + SuperShops.nf.format(storage - quantity) + ")";
                if (shop.isInfinite())
                    spaceString = "§f (oo)";
                lines = new String[]{"§e" + owner + "§d§o is Buying...", shop.getItem() == null ?
                        itemName : itemName + spaceString,
                        "§fPrice: §a" + price};
                break;
        }
        return lines;
    }

    public static void removeShopText(Shop shop) {
        Location location = shop.getLocation();
        List<String> entityList = ShopStorage.getHolograms(shop.getID());
        for (Entity entity : location.getWorld().getLivingEntities()) {
            if (hologramsCache.containsKey(String.valueOf(entity.getUniqueId()))) {
                if (hologramsCache.get(String.valueOf(entity.getUniqueId())).getID().equals(shop.getID())) {
                    hologramsCache.remove(String.valueOf(entity.getUniqueId()));
                    if (entityList != null)
                        entityList.remove(String.valueOf(entity.getUniqueId()));
                    entity.remove();
                }
            }
        }
        ShopStorage.setHolograms(shop.getID(), entityList);
        ShopStorage.setFloatingItem(shop.getID(), "");
    }

    public static void loadHolograms() {
        for (String shopString : SuperShops.shops.getKeys(false)) {
            List<String> entityUniqueIds = SuperShops.shops.getStringList(shopString + ".holograms");
            Shop shop = new Shop(SuperShops.shops.getInt(shopString + ".id"));
            for (String uuid : entityUniqueIds) {
                hologramsCache.put(uuid, shop);
            }
            hologramsCache.put(ShopStorage.getFloatingItem(shop.getID()), shop);
        }
    }

}
