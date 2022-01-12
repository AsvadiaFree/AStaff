package fr.asvadia.astaff.modules;

import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class TopLuck extends Module {
    @Override
    public void apply(Player player, SimpleItem item) {
        fr.asvadia.astaff.utils.TopLuck.openTopLuck(player);
    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        fr.asvadia.astaff.utils.TopLuck.openTopLuck(player);
    }
}
