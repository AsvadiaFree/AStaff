package fr.asvadia.astaff.staff.modules;

import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class Module {
    public abstract void apply(Player player, SimpleItem item);

    public abstract void apply(Player player, SimpleItem item, Event event);
}
