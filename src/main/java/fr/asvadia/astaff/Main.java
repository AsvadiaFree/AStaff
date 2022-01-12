package fr.asvadia.astaff;

import fr.asvadia.astaff.commands.SanctionCommand;
import fr.asvadia.astaff.commands.StaffCommand;
import fr.asvadia.astaff.commands.StaffTabCompleter;
import fr.asvadia.astaff.commands.TopLuckCommand;
import fr.asvadia.astaff.utils.StaffListeners;
import fr.asvadia.astaff.utils.TopLuckListeners;
import fr.asvadia.astaff.utils.TopLuckWebHook;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.skyfighttv.simpleitem.SimpleItem;
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
        TopLuckWebHook topLuckWebHook = new TopLuckWebHook();

        getCommand("astaff").setExecutor(new StaffCommand());
        getCommand("astaff").setTabCompleter(new StaffTabCompleter());
        getCommand("asanction").setExecutor(new SanctionCommand());
        getCommand("atopluck").setExecutor(new TopLuckCommand());
        getServer().getPluginManager().registerEvents(new StaffListeners(), this);
        getServer().getPluginManager().registerEvents(new TopLuckListeners(), this);
    }

    public static Main getInstance() {
        return instance;
    }
}
