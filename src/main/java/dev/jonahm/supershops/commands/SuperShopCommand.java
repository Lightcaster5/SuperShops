package dev.jonahm.supershops.commands;

import dev.jonahm.supershops.SuperShops;
import dev.jonahm.supershops.classes.Shop;
import dev.jonahm.supershops.utils.item.ItemUtils;
import dev.jonahm.supershops.utils.shop.ShopStorage;
import dev.jonahm.supershops.utils.string.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.Arrays;

public class SuperShopCommand extends Command {

    public SuperShopCommand() {
        super("supershop", "Super shop command", "/supershop", Arrays.asList("supershop", "supershops"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("supershop.admin")) {
            if (args.length == 0) {
                sender.sendMessage(CC.color("&cSuper Shops Help"));
                sender.sendMessage(CC.color("&c/supershop give <player> <size> - Give player a shop with specified storage size"));
                sender.sendMessage(CC.color("&c/supershop reload - Reload configs"));
                sender.sendMessage(CC.color("&c/supershop delete - Deletes the shop you're looking at"));
            } else {
                if (args[0].equalsIgnoreCase("give")) {
                    if (args.length > 2) {
                        String targetName = args[1];
                        Player target = null;
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            if (online.getName().equalsIgnoreCase(targetName)) {
                                target = online;
                            }
                        }
                        if (target != null) {
                            try {
                                int size = Integer.parseInt(args[2]);
                                if (size == -1 || size > 0) {
                                    if (target.getInventory().addItem(ItemUtils.getSuperShopItem(size)).size() == 0) {
                                        String sizeStr = size == -1 ? "oo" : "x" + SuperShops.nf.format(size);
                                        sender.sendMessage(CC.color("&2&l[!]&a Given a Super Shop to &2" + target.getName() + "&a with a size of &2" + sizeStr));
                                    } else {
                                        sender.sendMessage(CC.color("&4&l[!]&c " + target.getName() + "'s inventory is full"));
                                    }
                                } else {
                                    sender.sendMessage(CC.color("&4&l[!]&c Invalid shop amount"));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(CC.color("&4&l[!]&c \"&7" + args[2] + "&c\" is not an integer"));
                            }
                        } else {
                            sender.sendMessage(CC.color("&4&l[!]&c \"&7" + targetName + "&c\" is not online"));
                        }
                    } else {
                        sender.sendMessage(CC.color("&4&l[!]&c Invalid usage, usage: /supershop give <player> <size>"));
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    SuperShops.reloadConfigs();
                    sender.sendMessage(CC.color("&2&l[!]&a Config files reloaded"));
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Block block = getTargetBlock(player, 5);
                        if (block != null) {
                            if (ShopStorage.getShop(block) != null) {
                                Shop shop = ShopStorage.getShop(block);
                                Location location = shop.getLocation();
                                location.getWorld().getBlockAt(location).setType(Material.AIR);
                                ShopStorage.removeShop(shop.getID());
                                sender.sendMessage(CC.color("&2&l[!]&a Shop removed"));
                            } else {
                                sender.sendMessage(CC.color("&4&l[!]&c You are not looking at a shop"));
                            }
                        } else {
                            sender.sendMessage(CC.color("&4&l[!]&c You are not looking at a shop"));
                        }
                    } else {
                        sender.sendMessage(CC.color("&4&l[!]&c You must be a player to use that command"));
                    }
                }
            }
        } else {
            sender.sendMessage(CC.color("&4&l[!]&c You do not have permission to use that command"));
        }
        return true;

    }

    public final Block getTargetBlock(Player player, int range) {
        BlockIterator iterator = new BlockIterator(player, range);
        Block lastBlock = iterator.next();
        while (iterator.hasNext()) {
            lastBlock = iterator.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

}
