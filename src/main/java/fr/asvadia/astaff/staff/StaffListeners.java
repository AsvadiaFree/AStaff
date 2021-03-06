package fr.asvadia.astaff.staff;

import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.staff.modules.Freeze;
import fr.asvadia.astaff.staff.modules.Vanish;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

public class StaffListeners implements Listener {
    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        if (Freeze.frozen.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(FileManager.getValues().get(Files.Lang).getString("Staff.Freeze.WrongAction"));
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (Freeze.frozen.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(FileManager.getValues().get(Files.Lang).getString("Staff.Freeze.WrongAction"));
        } else if (Staff.staffed.contains(event.getPlayer())
                && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null
                && event.getClickedBlock().getType() == Material.CHEST) {
            event.setCancelled(true);
            Chest c = (Chest) event.getClickedBlock().getState();
            event.getPlayer().closeInventory();
            event.getPlayer().openInventory(c.getInventory());
            event.getPlayer().sendMessage("§6§lStaff §f» §7Ouverture silencieuse du coffre.");
        } else if (event.getItem() != null
                && event.getItem().getType() == Material.FISHING_ROD) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§6§lFaction §f» §7Les cannes à pêche sont temporairement désactivé !");
        }
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player)
            if (Freeze.frozen.contains((Player) event.getDamager()) || Freeze.frozen.contains((Player) event.getEntity()))
                event.setCancelled(true);
        if (event.getDamager() instanceof Fish f) {
            f.remove();
            event.getEntity().setVelocity(new Vector(0, 0, 0));
        }
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (Freeze.frozen.contains(event.getPlayer()) && event.getFrom().distance(event.getTo()) != 0.0)
            event.setCancelled(true);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        for (Player vanish : Vanish.vanished)
            event.getPlayer().hidePlayer(Main.getInstance(), vanish);
    }

    @EventHandler
    private void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player p
                && Staff.staffed.contains(p))
            event.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        if (Staff.staffed.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        if (Staff.staffed.contains(event.getPlayer()))
            event.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    private void onDamageStaff(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p
                && Staff.staffed.contains(p))
            event.setCancelled(true);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (Staff.staffed.contains(event.getPlayer()))
            Staff.changeStaff(false, event.getPlayer());
        if (Freeze.frozen.contains(event.getPlayer())) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (Staff.staffed.contains(player))
                    player.sendMessage(FileManager.getValues().get(Files.Lang).getString("Staff.Freeze.PlayerDisconnected").replaceAll("%player%", event.getPlayer().getName()));
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onChat(AsyncPlayerChatEvent event) {
        if (Staff.isChatLock()
                && !event.getPlayer().hasPermission("astaff")
                && !event.getMessage().startsWith("/"))
            event.setCancelled(true);
    }

    @EventHandler
    private void onLoad(PlayerLoginEvent event) {
        if (Staff.safeStop)
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
    }
}
