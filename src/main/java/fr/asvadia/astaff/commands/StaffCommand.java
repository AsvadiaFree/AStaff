package fr.asvadia.astaff.commands;

import fr.asvadia.astaff.modules.Freeze;
import fr.asvadia.astaff.modules.PlayerViewer;
import fr.asvadia.astaff.utils.Staff;
import fr.asvadia.astaff.utils.StaffModules;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
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
            if (args.length == 0) {
                if (p.hasPermission(config.getString("Staff.Permission"))) {
                    YamlConfiguration staff = FileManager.getValues().get(Files.Staff);
                    Staff.changeStaff(!staff.contains("players." + p.getName().toLowerCase())
                            || !staff.getBoolean("players." + p.getName().toLowerCase() + ".active"), p);
                }
            } else {
                YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
                switch (args[0].toLowerCase()) {
                    case "view":
                        if (p.hasPermission(config.getString("Staff.Stuff.PlayerViewer.Permission"))) {
                            if (args.length == 2) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if (target == null) {
                                    p.sendMessage(lang.getString("Staff.PlayerViewer.PlayerNotFound").replaceAll("%player%", args[0]));
                                    return false;
                                }
                                PlayerViewer.openPlayerGui(p, target);
                            } else
                                p.sendMessage(lang.getString("Staff.PlayerViewer.PlayerNotFound").replaceAll("%player%", "N/A"));
                        }
                        break;
                    case "freeze":
                        if (p.hasPermission(config.getString("Staff.Stuff.Freeze.Permission"))) {
                            if (args.length == 2) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if (target == null) {
                                    p.sendMessage(lang.getString("Staff.Freeze.PlayerNotFound").replaceAll("%player%", args[0]));
                                    return false;
                                }
                                Freeze.freeze(p, target);
                            } else
                                p.sendMessage(lang.getString("Staff.Freeze.PlayerNotFound").replaceAll("%player%", "N/A"));
                        }
                        break;
                }
            }
        }
        return false;
    }
}
