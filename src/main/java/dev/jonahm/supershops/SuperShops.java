package dev.jonahm.supershops;

import dev.jonahm.supershops.commands.SuperShopCommand;
import dev.jonahm.supershops.events.ShopClickListener;
import dev.jonahm.supershops.events.ShopOpenCloseListener;
import dev.jonahm.supershops.events.ShopPlaceListener;
import dev.jonahm.supershops.utils.item.ItemName;
import dev.jonahm.supershops.utils.display.FloatingText;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.Locale;

public class SuperShops extends JavaPlugin {

    public static SuperShops plugin;

    public static File configFile, shopsFile;
    public static FileConfiguration config, shops;

    public static Economy economy = null;

    public static NumberFormat nf = NumberFormat.getInstance(Locale.US);

    @Override
    public void onEnable() {
        plugin = this;

        console("§2§l[!]§a Loading files");
        loadConfigs();

        int shopCount = shops.getKeys(false).size();
        String end = shopCount == 1 ? "shop" : "shops";
        console("§2§l[!]§a Loaded " + nf.format(shopCount) + " " + end);

        ItemName.load();
        FloatingText.loadHolograms();

        register(new SuperShopCommand());
        getServer().getPluginManager().registerEvents(new ShopPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new ShopClickListener(), this);
        getServer().getPluginManager().registerEvents(new ShopOpenCloseListener(), this);

        console("§2§l[!]§a Attempting to establish vault hook");
        hookVault();
    }

    public void register(Command command) {
        final Field bukkitCommandMap;
        try {
            bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(command.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void hookVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            console("§4§l[!]§c Vault not found, disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                console("§4§l[!]§c Economy plugin not found, disabling plugin");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                economy = rsp.getProvider();
                if (economy == null) {
                    console("§4§l[!]§c Economy plugin not found, disabling plugin");
                    getServer().getPluginManager().disablePlugin(this);
                } else {
                    console("§2§l[!]§a Hooked into Vault using " + economy.getName());
                }
            }
        }
    }

    public static void loadConfigs() {
        if (!plugin.getDataFolder().exists()) {
            console("§2§l[!]§a Creating plugin folder");
            plugin.getDataFolder().mkdir();
        }
        configFile = new File(plugin.getDataFolder(), "config.yml");
        shopsFile = new File(plugin.getDataFolder(), "shops.yml");
        InputStream stream = plugin.getResource("config.yml");
        if (!configFile.exists()) {
            try {
                console("§2§l[!]§a Creating config.yml");
                Files.copy(stream, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (shopsFile.createNewFile()) {
                console("§2§l[!]§a Creating shops.yml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        shops = YamlConfiguration.loadConfiguration(shopsFile);
    }

    public static void saveConfigs() {
        try {
            config.save(configFile);
            shops.save(shopsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfigs() {
        config = YamlConfiguration.loadConfiguration(configFile);
        shops = YamlConfiguration.loadConfiguration(shopsFile);
    }

    public static void reloadShopConfigs() {
        shops = YamlConfiguration.loadConfiguration(shopsFile);
    }

    public static void console(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

}
