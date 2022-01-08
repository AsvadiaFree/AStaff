package fr.asvadia.astaff;

import fr.asvadia.astaff.commands.StaffCommand;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    private static Main instance;
    public List<Player> staffMembers = new ArrayList<>();

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
    }

    public static Main getInstance() {
        return instance;
    }
}
