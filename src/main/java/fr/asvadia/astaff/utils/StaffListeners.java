package fr.asvadia.astaff.utils;

import fr.asvadia.astaff.modules.Freeze;
import fr.asvadia.astaff.modules.Vanish;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

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
        }
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player)
            if(Freeze.frozen.contains((Player)event.getDamager()) || Freeze.frozen.contains((Player)event.getEntity()))
                event.setCancelled(true);
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (Freeze.frozen.contains(event.getPlayer()) && event.getFrom().distance(event.getTo()) != 0.0)
            event.setCancelled(true);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        for (Player vanish : Vanish.vanished)
            event.getPlayer().hidePlayer(vanish);
    }

    @EventHandler
    private void onPickup(PlayerPickupItemEvent event) {
        if(Staff.staffed.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        if(Staff.staffed.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    private void onDamageStaff(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            if(Staff.staffed.contains((Player)event.getEntity()))
                event.setCancelled(true);
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        if(Staff.staffed.contains(event.getPlayer()))
            event.getPlayer().setGameMode(GameMode.CREATIVE);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if(Staff.staffed.contains(event.getPlayer()))
            Staff.changeStaff(false, event.getPlayer());
    }
}
