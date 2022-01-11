package fr.asvadia.astaff.modules;

import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class Freeze extends Module {
    public static final List<Player> frozen = new ArrayList<>();

    @Override
    public void apply(Player player, SimpleItem item) {

    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        if (event instanceof PlayerInteractEntityEvent) {
            YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
            PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
            if (e.getRightClicked() instanceof Player) {
                Player target = (Player) e.getRightClicked();
                if (frozen.contains(target)) {
                    frozen.remove(target);

                    target.sendMessage(lang.getString("Staff.Freeze.UnfreezeByPlayer").replaceAll("%player%", player.getName()));
                    player.sendMessage(lang.getString("Staff.Freeze.PlayerUnfreeze").replaceAll("%player%", target.getName()));
                } else {
                    frozen.add(target);

                    target.teleport(target.getWorld().getHighestBlockAt(target.getLocation()).getLocation().add(0, 1, 0));

                    target.sendMessage(lang.getString("Staff.Freeze.FreezeByPlayer").replaceAll("%player%", player.getName()));
                    player.sendMessage(lang.getString("Staff.Freeze.PlayerFreeze").replaceAll("%player%", target.getName()));
                }
            }
        }
    }
}
