package fr.asvadia.astaff.modules;

import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class RandomTp extends Module {
    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        Optional<? extends Player> op = Bukkit.getOnlinePlayers().stream()
                .filter(player1 -> !player1.equals(player))
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .findFirst();
    }
}
