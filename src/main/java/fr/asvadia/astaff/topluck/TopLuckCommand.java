package fr.asvadia.astaff.topluck;

import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TopLuckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            if (p.hasPermission(config.getString("TopLuck.Permission")))
                TopLuck.openTopLuck(p);
        }
        return false;
    }
}
