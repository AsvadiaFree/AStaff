package fr.asvadia.astaff.utils;

import fr.asvadia.astaff.modules.Module;
import fr.asvadia.astaff.modules.Vanish;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Staff {
    public static List<Player> staffed = new ArrayList<>();
    private static StaffModules[] staffModules = StaffModules.values();

    public static StaffModules getByName(String name) {
        if (staffModules == null)
            return null;
        for (StaffModules staffModule : staffModules)
            if (staffModule.getName().equals(name))
                return staffModule;
        return null;
    }

    public static StaffModules getByModule(Module module) {
        if (staffModules == null)
            return null;
        for (StaffModules staffModule : staffModules)
            if (staffModule.getModule().equals(module))
                return staffModule;
        return null;
    }

    public static void changeStaff(boolean status, Player player) {
        YamlConfiguration staff = FileManager.getValues().get(Files.Staff);
        staff.set("players." + player.getName().toLowerCase() + ".active", status);

        if (status) {
            staff.set("players." + player.getName().toLowerCase() + ".oldInventory", player.getInventory().getContents());
            FileManager.save(Files.Staff);

            staffed.add(player);

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.setGameMode(GameMode.CREATIVE);

            YamlConfiguration config = FileManager.getValues().get(Files.Config);

            config.getConfigurationSection("Staff.Stuff").getKeys(false).forEach(s -> {
                if (player.hasPermission(config.getString("Staff.Stuff." + s + ".Permission"))) {
                    SimpleItem item = new SimpleItem(Material.matchMaterial(config.getString("Staff.Stuff." + s + ".Material")));
                    item.setName(config.getString("Staff.Stuff." + s + ".Name"));
                    item.setLore(config.getStringList("Staff.Stuff." + s + ".Lore"));
                    item.onClick((player1, simpleItem, event) -> {
                        StaffModules staffModule = Staff.getByName(s);
                        Bukkit.getLogger().info("click / " + staffModule + " / " + event.getEventName());
                        if (staffModule != null)
                            staffModule.getModule().apply(player1, simpleItem, event);
                    });
                    if (s.equals("Vanish"))
                        StaffModules.VANISH.getModule().apply(player, item);
                    player.getInventory().setItem(config.getInt("Staff.Stuff." + s + ".Slot"), item.toItemStack());
                }
            });
        } else {
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setContents(staff.getList("players." + player.getName().toLowerCase() + ".oldInventory").toArray(new ItemStack[0]));

            staffed.remove(player);
            Vanish.vanished.add(player);
            StaffModules.VANISH.getModule().apply(player, null);
        }
    }
}