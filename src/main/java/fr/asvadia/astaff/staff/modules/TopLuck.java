package fr.asvadia.astaff.staff.modules;

import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class TopLuck extends Module {
    @Override
    public void apply(Player player, SimpleItem item) {
        fr.asvadia.astaff.topluck.TopLuck.openTopLuck(player);
    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        fr.asvadia.astaff.topluck.TopLuck.openTopLuck(player);
    }
}
