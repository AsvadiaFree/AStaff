package fr.asvadia.astaff.staff.modules;

import fr.asvadia.api.bukkit.menu.inventory.AInventoryGUI;
import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.ItemCreator;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RandomTp extends Module {
    private static final List<Player> temp = new ArrayList<>();
    YamlConfiguration lang = FileManager.getValues().get(Files.Lang);

    @Override
    public void apply(Player player, SimpleItem item) {

    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        if (!temp.contains(player)) {
            temp.add(player);
            if (event instanceof PlayerInteractEvent) {
                PlayerInteractEvent e = (PlayerInteractEvent) event;
                if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    AInventoryGUI.Builder inv = AInventoryGUI.builder()
                            .size(54)
                            .title("Téléportation aux joueurs");

                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    for (int i = 0; i < 45; i++) {
                        if (players.size() <= i)
                            break;
                        inv.item(i, new ItemCreator(Material.PLAYER_HEAD, 1)
                                .setSkullOwner(players.get(i).getName())
                                .toItemStack());
                        int finalI = i;
                        inv.clickButton(i, (player1, aInventoryGUI, clickType) -> {
                            if (players.get(finalI).isOnline()) {
                                player1.teleport(players.get(finalI));
                                player1.sendMessage(lang.getString("Staff.RandomTp.PlayerFound").replaceAll("%player%", players.get(finalI).getName()));
                            } else
                                player1.sendMessage(lang.getString("Staff.RandomTp.PlayerNotConnected").replaceAll("%player%", players.get(finalI).getName()));
                        });
                    }

                    player.openInventory(inv.build().getInventory());
                } else {
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
            new BukkitRunnable() {
                @Override
                public void run() {
                    temp.remove(player);
                }
            }.runTaskTimer(Main.getInstance(), 0, 10);
        }
    }
}
