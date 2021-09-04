package dev.jonahm.supershops.utils.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class Serialization {

    public static String inventoryToBase64(Inventory inventory) {
        try {
            ArrayList<String> itemList = new ArrayList<>();
            for (ItemStack item : inventory.getContents()) {
                if (item == null) {
                    item = new ItemStack(Material.AIR);
                }
                itemList.add(toBase64(ItemUtils.serialize(item)));
            }
            return toBase64(itemList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Inventory base64ToInventory(String string) {
        try {
            ArrayList<String> itemList = (ArrayList<String>) fromBase46(string);
            Inventory inventory = Bukkit.createInventory(null, 27);
            int count = 0;
            for (String item : itemList) {
                ItemStack itemStack;
                itemStack = ItemUtils.deserialize((Map<String, Object>) fromBase46(item));
                inventory.setItem(count, itemStack);
                count++;
            }
            return inventory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object fromBase46(String s) {
        try {
            byte[] data = Base64.getDecoder().decode(s);
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toBase64(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
