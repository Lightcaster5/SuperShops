package dev.jonahm.supershops.utils;

import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.classes.ShopPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Balance {

    public static double getBalance(ShopPlayer shopPlayer) {
        return SuperShops.economy.getBalance(Bukkit.getOfflinePlayer(UUID.fromString(shopPlayer.getUUID())));
    }

    public static void takeMoney(ShopPlayer shopPlayer, double amount) {
        SuperShops.economy.withdrawPlayer(Bukkit.getOfflinePlayer(UUID.fromString(shopPlayer.getUUID())), amount);
    }

    public static void giveMoney(ShopPlayer shopPlayer, double amount) {
        SuperShops.economy.depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(shopPlayer.getUUID())), amount);
    }

}
