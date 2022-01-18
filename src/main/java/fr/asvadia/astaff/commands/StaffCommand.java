package fr.asvadia.astaff.commands;

import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.modules.Freeze;
import fr.asvadia.astaff.modules.PlayerViewer;
import fr.asvadia.astaff.utils.Staff;
import fr.asvadia.astaff.utils.WorldScanner;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StaffCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
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
                    case "lockchat":
                        if (p.hasPermission("astaff.lockchat")){
                            Staff.setChatLock(!Staff.isChatLock());
                            if (Staff.isChatLock())
                                p.sendMessage("§6§lStaff §f§l» §r§fVous venez de désactiver le chat !");
                            else
                                p.sendMessage("§6§lStaff §f§l» §r§fVous venez de réactiver le chat !");
                        }
                        break;
                    case "worldscanner":
                        if (p.hasPermission("astaff.worldscanner")
                                && args.length == 2) {
                            WorldScanner.scan(p.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[1]));
                        }
                        break;
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            switch (args[0].toLowerCase()) {
                case "lockchat":
                    Staff.setChatLock(!Staff.isChatLock());
                    if (Staff.isChatLock())
                        sender.sendMessage("§6§lStaff §f§l» §r§fVous venez de désactiver le chat !");
                    else
                        sender.sendMessage("§6§lStaff §f§l» §r§fVous venez de réactiver le chat !");
                    break;
                case "stop":
                    Staff.safeStop = true;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.closeInventory();
                        player.kickPlayer("§6§lRedemarrage §f§l» §r§fRedémarrage automatique...");
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.shutdown();
                        }
                    }.runTaskLater(Main.getInstance(), 200);
                    break;
                case "worldscanner":
                    WorldScanner.scan(Bukkit.getWorld("world"), Integer.parseInt(args[1]), Integer.parseInt(args[1]));
                    break;
            }
        }
        return false;
    }
}
