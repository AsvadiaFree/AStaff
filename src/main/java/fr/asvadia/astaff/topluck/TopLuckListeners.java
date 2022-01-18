package fr.asvadia.astaff.topluck;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TopLuckListeners implements Listener {

    public TopLuckOres getByOre(Material material) {
        for (TopLuckOres topLuckOres : TopLuckOres.values()) {
            if (topLuckOres.getOre() == null)
                continue;
            if (topLuckOres.getOre() == material)
                return topLuckOres;
        }
        return null;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        TopLuck.putIfAbsent(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        TopLuck.putIfAbsent(event.getPlayer());

        TopLuck.playerOreCount.get(TopLuckOres.ALL).put(event.getPlayer().getUniqueId(),
                TopLuck.playerOreCount.get(TopLuckOres.ALL).get(event.getPlayer().getUniqueId())+1);

        TopLuckOres topLuckOres = getByOre(event.getBlock().getType());
        if (topLuckOres != null)
            TopLuck.playerOreCount.get(topLuckOres).put(event.getPlayer().getUniqueId(),
                    TopLuck.playerOreCount.get(topLuckOres).get(event.getPlayer().getUniqueId())+1);
    }
}
