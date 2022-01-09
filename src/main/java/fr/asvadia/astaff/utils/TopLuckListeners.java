package fr.asvadia.astaff.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TopLuckListeners implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        TopLuck.putIfAbsent(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBreak(BlockBreakEvent event) {
        TopLuck.putIfAbsent(event.getPlayer());

        TopLuck.playerOreCount.get(TopLuckOres.ALL).put(event.getPlayer().getUniqueId(),
                TopLuck.playerOreCount.get(TopLuckOres.ALL).get(event.getPlayer().getUniqueId())+1);

        TopLuckOres topLuckOres = TopLuckOres.getByOre(event.getBlock().getType());
        if (topLuckOres != null)
            TopLuck.playerOreCount.get(topLuckOres).put(event.getPlayer().getUniqueId(),
                    TopLuck.playerOreCount.get(topLuckOres).get(event.getPlayer().getUniqueId())+1);
    }
}
