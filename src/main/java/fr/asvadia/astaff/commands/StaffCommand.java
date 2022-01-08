package fr.asvadia.astaff.commands;

import fr.asvadia.astaff.Staff;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            if (p.hasPermission(config.getString("Staff.Permission"))) {
                YamlConfiguration staff = FileManager.getValues().get(Files.Staff);
                Staff.changeStaff(!staff.contains("players." + p.getName().toLowerCase())
                        || !staff.getBoolean("players." + p.getName().toLowerCase() + ".active"), p);
            }
        }
        return false;
    }
}
