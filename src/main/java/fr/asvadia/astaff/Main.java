package fr.asvadia.astaff;

import fr.asvadia.astaff.commands.StaffCommand;
import fr.asvadia.astaff.utils.StaffListeners;
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

        getCommand("astaff").setExecutor(new StaffCommand());
        getServer().getPluginManager().registerEvents(new StaffListeners(), this);
    }

    public static Main getInstance() {
        return instance;
    }
}
