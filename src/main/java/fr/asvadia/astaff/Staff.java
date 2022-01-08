package fr.asvadia.astaff;

import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Staff {
    private static final HashMap<String, SimpleItem> items = new HashMap<>();

    public static void activeModule(StaffModules module, Player player) {

    }

    public static void changeStaff(boolean status, Player player) {
        YamlConfiguration staff = FileManager.getValues().get(Files.Staff);
        staff.set("players." + player.getName().toLowerCase() + ".active", status);

        if (status) {
            staff.set("players." + player.getName().toLowerCase() + ".oldInventory", player.getInventory().getContents());
            FileManager.save(Files.Staff);

            Main.getInstance().staffMembers.add(player);

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.setGameMode(GameMode.CREATIVE);

            YamlConfiguration config = FileManager.getValues().get(Files.Config);

            config.getConfigurationSection("Staff.Stuff").getKeys(false).forEach(s -> {
                if (player.hasPermission(config.getString("Staff.Stuff." + s + ".Permission"))) {
                    SimpleItem item = new SimpleItem(Material.matchMaterial(config.getString("Staff.Stuff." + s + ".Material")));
                    item.setName(config.getString("Staff.Stuff." + s + ".Name"));
                    item.setLore(config.getStringList("Staff.Stuff." + s + ".Lore"));
                    item.onClick((player1, simpleItem, itemStacks) -> activeModule(StaffModules.getByModule(s), player1));
                    player.getInventory().setItem(config.getInt("Staff.Stuff." + s + ".Slot"), item.toItemStack());
                }
            });
        } else {

        }
    }
}
