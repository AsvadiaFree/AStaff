package fr.asvadia.astaff;

import fr.asvadia.astaff.sanction.SanctionCommand;
import fr.asvadia.astaff.scanner.Scanner;
import fr.asvadia.astaff.scanner.EnderChestScanner;
import fr.asvadia.astaff.staff.commands.StaffCommand;
import fr.asvadia.astaff.staff.commands.StaffTabCompleter;
import fr.asvadia.astaff.topluck.TopLuckCommand;
import fr.asvadia.astaff.staff.StaffListeners;
import fr.asvadia.astaff.topluck.TopLuckListeners;
import fr.asvadia.astaff.topluck.TopLuckWebHook;
import fr.asvadia.astaff.scanner.WorldScanner;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        try {
            FileManager.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        SimpleItem.init(this);
        WorldScanner.init();

        getCommand("astaff").setExecutor(new StaffCommand());
        getCommand("astaff").setTabCompleter(new StaffTabCompleter());
        getCommand("asanction").setExecutor(new SanctionCommand());
        getServer().getPluginManager().registerEvents(new StaffListeners(), this);
        if (FileManager.getValues().get(Files.Config).getBoolean("TopLuck.Enable")) {
            getCommand("atopluck").setExecutor(new TopLuckCommand());
            new TopLuckWebHook();
            getServer().getPluginManager().registerEvents(new TopLuckListeners(), this);
        }

        //World Scanner
        YamlConfiguration scanner = FileManager.getValues().get(Files.Scanner);
        if (scanner.contains("ws.start")
                && scanner.getBoolean("ws.start")) {
            new WorldScanner(Scanner.Type.WORLD)
                    .setSize(scanner.getInt("ws.size"))
                    .setWorld(Bukkit.getWorld(scanner.getString("ws.world")))
                    .asyncStart(true, scanner.getInt("ws.X"), scanner.getInt("ws.Z"));
        }

        new EnderChestScanner(Scanner.Type.ENDER_CHEST).start(false);
    }

    public static Main getInstance() {
        return instance;
    }
}
