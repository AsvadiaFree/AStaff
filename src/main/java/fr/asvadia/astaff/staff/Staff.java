package fr.asvadia.astaff.staff;

import fr.asvadia.astaff.staff.modules.Module;
import fr.asvadia.astaff.staff.modules.Vanish;
import fr.asvadia.astaff.staff.modules.XRay;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Staff {
    public static List<Player> staffed = new ArrayList<>();
    public static boolean safeStop = false;
    private static boolean chatLock = false;
    private static final StaffModules[] staffModules = StaffModules.values();

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

    public static void init() {
        YamlConfiguration staff = FileManager.getValues().get(Files.Staff);
        if (staff.contains("players")) {
            staff.getConfigurationSection("players").getKeys(false).forEach(s -> {
                if (staff.getBoolean("players." + s + ".active")) {
                    Player player = Bukkit.getPlayer(s);
                    if (player != null)
                        changeStaff(true, player);
                }
            });
        }
    }

    public static void changeStaff(boolean status, Player player) {
        YamlConfiguration staff = FileManager.getValues().get(Files.Staff);
        staff.set("players." + player.getName().toLowerCase() + ".active", status);

        if (status) {
            staff.set("players." + player.getName().toLowerCase() + ".oldInventory", player.getInventory().getContents());

            staffed.add(player);

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.setAllowFlight(true);

            YamlConfiguration config = FileManager.getValues().get(Files.Config);

            config.getConfigurationSection("Staff.Stuff").getKeys(false).forEach(s -> {
                if (player.hasPermission(config.getString("Staff.Stuff." + s + ".Permission"))) {
                    SimpleItem item = new SimpleItem(Material.matchMaterial(config.getString("Staff.Stuff." + s + ".Material")));
                    item.setName(config.getString("Staff.Stuff." + s + ".Name"));
                    item.setLore(config.getStringList("Staff.Stuff." + s + ".Lore"));
                    item.onClick((player1, simpleItem, event) -> {
                        StaffModules staffModule = Staff.getByName(s);
                        if (staffModule != null
                                && player1.hasPermission(config.getString("Staff.Stuff." + s + ".Permission")))
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
            player.setAllowFlight(false);
            List<ItemStack> items = (List<ItemStack>) staff.getList("players." + player.getName().toLowerCase() + ".oldInventory");
            if (items != null)
                player.getInventory().setContents(items.toArray(new ItemStack[0]));

            staffed.remove(player);
            Vanish.vanished.add(player);
            if (!XRay.chunkLoaded.containsKey(player))
                XRay.chunkLoaded.put(player, new ArrayList<>());
            StaffModules.XRay.getModule().apply(player, null);
            StaffModules.VANISH.getModule().apply(player, null);
        }
        FileManager.save(Files.Staff);
    }

    public static List<Player> getStaffed() {
        return staffed;
    }

    public static boolean isChatLock() {
        return chatLock;
    }

    public static void setChatLock(boolean chatLock) {
        Staff.chatLock = chatLock;
    }

    public static StaffModules[] getStaffModules() {
        return staffModules;
    }
}
