package fr.asvadia.astaff.utils;

import fr.asvadia.api.bukkit.menu.inventory.AInventoryGUI;
import fr.asvadia.astaff.modules.PlayerViewer;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class TopLuck {
    public static HashMap<UUID, Double> playerScore = new HashMap<>();
    public static HashMap<TopLuckOres, HashMap<UUID, Long>> playerOreCount = new HashMap<>();
    public static HashMap<TopLuckOres, HashMap<UUID, Double>> playerOreScore = new HashMap<>();

    public static void openTopLuck(Player player) {
        updateTopLuck();

        YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        AInventoryGUI.Builder inv = AInventoryGUI.builder()
                .size(54)
                .title(lang.getString("TopLuck.GUI.Title"));

        for (int i = 0; i < 54; i++) {
            if (players.size() < 1)
                break;

            Player target = players.stream()
                    .max(Comparator.comparing(o -> playerScore.get(o.getUniqueId())))
                    .get();

            players.remove(target);

            List<String> lore = new ArrayList<>();
            ItemCreator item = new ItemCreator(Material.PLAYER_HEAD, 1)
                    .setSkullOwner(target.getName())
                    .setName(target.getName())
                    .setLore(lore);
            inv.item(i, item.toItemStack());
            inv.clickButton(i, (player1, aInventoryGUI, clickType) -> {
                player1.closeInventory();
                PlayerViewer.openPlayerGui(player1, target);
            });
        }

        player.openInventory(inv.build().getInventory());
    }

    public static void updateTopLuck() {
        DecimalFormat decimalFormat = new DecimalFormat("###.####");

        for (Player player : Bukkit.getOnlinePlayers()) {
            putIfAbsent(player);

            double globalScore = 0.0;
            for (TopLuckOres topLuckOres : TopLuckOres.values()) {
                if (topLuckOres.getOre() == null)
                    continue;

                double score = Double.parseDouble(decimalFormat.format((double) (playerOreCount.get(topLuckOres).get(player.getUniqueId()) / playerOreCount.get(TopLuckOres.ALL).get(player.getUniqueId()) * 100)).replaceAll(",", "."));
                globalScore += score;
                playerOreScore.get(topLuckOres).put(player.getUniqueId(), score);
            }

            playerScore.put(player.getUniqueId(), Double.parseDouble(decimalFormat.format(globalScore)));
        }
    }

    public static void putIfAbsent(Player player) {
        playerScore.putIfAbsent(player.getUniqueId(), 0.0);
        for (TopLuckOres topLuckOres : TopLuckOres.values()) {
            playerOreCount.putIfAbsent(topLuckOres, new HashMap<>());
            playerOreCount.get(topLuckOres).putIfAbsent(player.getUniqueId(), 0L);
            playerOreScore.putIfAbsent(topLuckOres, new HashMap<>());
            playerOreScore.get(topLuckOres).putIfAbsent(player.getUniqueId(), 0.0);
        }
    }

}
