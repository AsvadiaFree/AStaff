package fr.asvadia.astaff.staff.commands;

import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.scanner.EnderChestScanner;
import fr.asvadia.astaff.scanner.PlayerScanner;
import fr.asvadia.astaff.scanner.Scanner;
import fr.asvadia.astaff.scanner.WorldScanner;
import fr.asvadia.astaff.staff.Staff;
import fr.asvadia.astaff.staff.modules.Freeze;
import fr.asvadia.astaff.staff.modules.PlayerViewer;
import fr.asvadia.astaff.utils.CustomPlayer;
import fr.asvadia.astaff.utils.PlayerManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                                    target = PlayerManager.loadPlayer(Bukkit.getOfflinePlayer(args[1]));
                                    if (target == null) {
                                        p.sendMessage(lang.getString("Staff.PlayerViewer.PlayerNotFound").replaceAll("%player%", args[1]));
                                        return false;
                                    }
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
                        if (p.hasPermission("astaff.lockchat")) {
                            Staff.setChatLock(!Staff.isChatLock());
                            if (Staff.isChatLock())
                                p.sendMessage("§6§lStaff §f§l» §r§fVous venez de désactiver le chat !");
                            else
                                p.sendMessage("§6§lStaff §f§l» §r§fVous venez de réactiver le chat !");
                        }
                        break;

                    case "ec":
                        if (p.hasPermission("astaff.ec")) {
                            if (args.length == 2) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if (target == null) {
                                    target = PlayerManager.loadPlayer(Bukkit.getOfflinePlayer(args[1]));
                                    if (target == null) {
                                        p.sendMessage(lang.getString("Staff.PlayerViewer.PlayerNotFound").replaceAll("%player%", args[1]));
                                        return false;
                                    }
                                }
                                p.sendMessage("§6§lStaff §f§l» §r§fOuverture de l'EnderChest du joueur : " + target.getName() + ((target instanceof CustomPlayer) ? " §7(Offline) §f!" : "§f!"));
                                p.openInventory(target.getEnderChest());
                            } else
                                p.sendMessage(lang.getString("Staff.PlayerViewer.PlayerNotFound").replaceAll("%player%", "N/A"));
                        }
                        break;

                    case "scanner":
                        if (p.hasPermission("astaff.scanner")) {
                            if (args.length == 1) return false;

                            Scanner.Type type = Scanner.Type.getByName(args[1]);
                            if (type == null) return false;

                            switch (type) {
                                case WORLD -> {
                                    if (args.length == 2
                                            || !p.hasPermission("astaff.scanner.world")) return false;

                                    p.sendMessage("§6§lStaff Scanner §f§l» §r§Lancement du scan du §7World §f!");
                                    new WorldScanner(type)
                                            .setWorld(p.getWorld())
                                            .setSize(Integer.parseInt(args[2]))
                                            .asyncStart(false);
                                }

                                case PLAYER -> {
                                    if (!p.hasPermission("astaff.scanner.player")) return false;

                                    p.sendMessage("§6§lStaff Scanner §f§l» §r§Lancement du scan des §7Players §f!");
                                    new PlayerScanner(type)
                                            .asyncStart(false);
                                }

                                case ENDER_CHEST -> {
                                    if (!p.hasPermission("astaff.scanner.enderchest")) return false;

                                    p.sendMessage("§6§lStaff Scanner §f§l» §r§Lancement du scan des §7EnderChest §f!");
                                    new EnderChestScanner(type)
                                            .asyncStart(false);
                                }
                            }
                        }
                        break;
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            switch (args[0].toLowerCase()) {
                case "lockchat" -> {
                    Staff.setChatLock(!Staff.isChatLock());
                    if (Staff.isChatLock())
                        sender.sendMessage("§6§lStaff §f§l» §r§fVous venez de désactiver le chat !");
                    else
                        sender.sendMessage("§6§lStaff §f§l» §r§fVous venez de réactiver le chat !");
                }

                case "stop" -> {
                    Staff.safeStop = true;
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    Player[] p = new Player[1];
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p[0] = players.remove(0);

                            p[0].closeInventory();
                            p[0].kickPlayer("§6§lRedemarrage §f§l» §r§fRedémarrage automatique...");

                            if (players.isEmpty()) {
                                this.cancel();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.shutdown();
                                    }
                                }.runTaskLater(Main.getInstance(), 200);
                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 0);
                }

                case "scanner" -> {
                    if (args.length == 1) return false;

                    Scanner.Type type = Scanner.Type.getByName(args[1]);
                    if (type == null) return false;

                    switch (type) {
                        case WORLD -> {
                            new WorldScanner(type)
                                    .setWorld(Objects.requireNonNull(Bukkit.getWorld("world")))
                                    .setSize(Integer.parseInt(args[2]))
                                    .asyncStart(false);
                        }

                        case PLAYER -> {
                            new PlayerScanner(type)
                                    .asyncStart(false);
                        }

                        case ENDER_CHEST -> {
                            new EnderChestScanner(type)
                                    .asyncStart(false);
                        }
                    }
                }
            }
        }
        return false;
    }
}
