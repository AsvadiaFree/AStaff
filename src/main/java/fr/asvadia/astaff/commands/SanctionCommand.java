package fr.asvadia.astaff.commands;

import fr.asvadia.astaff.utils.Sanction;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SanctionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
            if (p.hasPermission(config.getString("Sanction.Permission")))
                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null)
                        Sanction.openSanctionGUI(p, target);
                    else
                        p.sendMessage(lang.getString("Sanction.PlayerNotFound").replaceAll("%player%", args[0]));
                } else
                    p.sendMessage(lang.getString("Sanction.NotPlayerSpecified"));
        }
        return false;
    }
}