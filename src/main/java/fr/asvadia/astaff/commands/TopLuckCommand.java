package fr.asvadia.astaff.commands;

import fr.asvadia.astaff.utils.TopLuck;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TopLuckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            if (player.hasPermission(config.getString("TopLuck.Permission")))
                TopLuck.openTopLuck(player);
        }
        return false;
    }
}
