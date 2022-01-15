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
    public static final HashMap<UUID, Double> playerScore = new HashMap<>();
    public static final HashMap<TopLuckOres, HashMap<UUID, Long>> playerOreCount = new HashMap<>();
    public static final HashMap<TopLuckOres, HashMap<UUID, Double>> playerOreScore = new HashMap<>();

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
            lang.getStringList("TopLuck.GUI.DefaultLore").forEach(s -> {
                lore.add(s.replaceAll("%tlGlobalScore%", TopLuck.playerScore.get(target.getUniqueId()) + "")
                        .replaceAll("%tlAsvadiumCount%", String.valueOf(TopLuck.playerOreCount.get(TopLuckOres.ASVADIUM).get(target.getUniqueId())))
                        .replaceAll("%tlTopazeCount%", String.valueOf(TopLuck.playerOreCount.get(TopLuckOres.TOPAZE).get(target.getUniqueId())))
                        .replaceAll("%tlRubisCount%", String.valueOf(TopLuck.playerOreCount.get(TopLuckOres.RUBIS).get(target.getUniqueId())))
                        .replaceAll("%tlSaphirCount%", String.valueOf(TopLuck.playerOreCount.get(TopLuckOres.SAPHIR).get(target.getUniqueId())))
                        .replaceAll("%tlDiamondCount%", String.valueOf(TopLuck.playerOreCount.get(TopLuckOres.DIAMOND).get(target.getUniqueId())))
                        .replaceAll("%tlEmeraldCount%", String.valueOf(TopLuck.playerOreCount.get(TopLuckOres.EMERALD).get(target.getUniqueId())))
                        .replaceAll("%tlAsvadiumScore%", String.valueOf(TopLuck.playerOreScore.get(TopLuckOres.ASVADIUM).get(target.getUniqueId())))
                        .replaceAll("%tlTopazeScore%", String.valueOf(TopLuck.playerOreScore.get(TopLuckOres.TOPAZE).get(target.getUniqueId())))
                        .replaceAll("%tlRubisScore%", String.valueOf(TopLuck.playerOreScore.get(TopLuckOres.RUBIS).get(target.getUniqueId())))
                        .replaceAll("%tlSaphirScore%", String.valueOf(TopLuck.playerOreScore.get(TopLuckOres.SAPHIR).get(target.getUniqueId())))
                        .replaceAll("%tlDiamondScore%", String.valueOf(TopLuck.playerOreScore.get(TopLuckOres.DIAMOND).get(target.getUniqueId())))
                        .replaceAll("%tlEmeraldScore%", String.valueOf(TopLuck.playerOreScore.get(TopLuckOres.EMERALD).get(target.getUniqueId()))));
            });
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
        DecimalFormat decimalFormat = new DecimalFormat("##,####");

        for (Player player : Bukkit.getOnlinePlayers()) {
            putIfAbsent(player);

            double globalScore = 0.0;
            for (TopLuckOres topLuckOres : TopLuckOres.values()) {
                if (topLuckOres.getOre() == null)
                    continue;

                double score = Double.parseDouble(decimalFormat.format(((double) playerOreCount.get(topLuckOres).get(player.getUniqueId()) /  (double) playerOreCount.get(TopLuckOres.ALL).get(player.getUniqueId())) * 100.0).replaceAll(",", "."));
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
            long n = 0;
            if (topLuckOres == TopLuckOres.ALL)
                n = 1L;
            playerOreCount.get(topLuckOres).putIfAbsent(player.getUniqueId(), n);
            playerOreScore.putIfAbsent(topLuckOres, new HashMap<>());
            playerOreScore.get(topLuckOres).putIfAbsent(player.getUniqueId(), 0.0);
        }
    }
}
