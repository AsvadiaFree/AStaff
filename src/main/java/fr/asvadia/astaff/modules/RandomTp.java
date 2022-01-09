package fr.asvadia.astaff.modules;

import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class RandomTp extends Module {
    YamlConfiguration lang = FileManager.getValues().get(Files.Lang);

    @Override
    public void apply(Player player, SimpleItem item) {

    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        Optional<? extends Player> op = Bukkit.getOnlinePlayers().stream()
                .filter(player1 -> !player1.equals(player))
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .findFirst();
        if (op.isPresent()) {
            player.teleport(op.get());
            player.sendMessage(lang.getString("Staff.RandomTp.PlayerFound").replaceAll("%player%", op.get().getName()));
        } else
            player.sendMessage(lang.getString("Staff.RandomTp.PlayerNotFound"));
    }
}
