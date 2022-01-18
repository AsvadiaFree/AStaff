package fr.asvadia.astaff.staff.modules;

import fr.asvadia.api.bukkit.menu.inventory.AInventoryGUI;
import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.sanction.Sanction;
import fr.asvadia.astaff.topluck.TopLuck;
import fr.asvadia.astaff.topluck.TopLuckOres;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.ItemCreator;
import fr.skyfighttv.simpleitem.SimpleItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PlayerViewer extends Module {
    private static final List<Player> temp = new ArrayList<>();

    @Override
    public void apply(Player player, SimpleItem item) {

    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        if (event instanceof PlayerInteractEntityEvent e) {
            if (e.getRightClicked() instanceof Player && !temp.contains((Player) e.getRightClicked())) {
                temp.add((Player) e.getRightClicked());
                YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
                player.sendMessage(lang.getString("Staff.PlayerViewer.InspectOf").replaceAll("%player%", e.getRightClicked().getName()));
                openPlayerGui(player, (Player) e.getRightClicked());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        temp.remove((Player) e.getRightClicked());
                    }
                }.runTaskTimer(Main.getInstance(), 0, 10);
            }
        }
    }

    public static void openPlayerGui(Player inspector, Player target) {
        // Init
        TopLuck.updateTopLuck();
        YamlConfiguration config = FileManager.getValues().get(Files.Config);
        YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
        AInventoryGUI.Builder inv = new AInventoryGUI.Builder()
                .size(54)
                .title("Inspection de " + target.getName());
        int slot;

        // Create player skull Item
        List<String> lore = new ArrayList<>();
        lang.getStringList("Staff.PlayerViewer.GUI.PlayerSkull.Lore").forEach(s -> {
            lore.add(PlaceholderAPI.setPlaceholders(target, s.replaceAll("%tlGlobalScore%", TopLuck.playerScore.get(target.getUniqueId()) + "")
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
                    .replaceAll("%tlEmeraldScore%", String.valueOf(TopLuck.playerOreScore.get(TopLuckOres.EMERALD).get(target.getUniqueId())))));
        });
        ItemCreator playerSkull = new ItemCreator(Material.PLAYER_HEAD, 1)
                .setName(lang.getString("Staff.PlayerViewer.GUI.PlayerSkull.Name").replaceAll("%player%", target.getName()))
                .setLore(lore)
                .setSkullOwner(target.getName());
        inv.item(config.getInt("Staff.PlayerViewer.GUI.PlayerSkull.Slot"), playerSkull.toItemStack());

        // Create Teleport Player Item
        ItemCreator teleportPlayer = new ItemCreator(Material.matchMaterial(config.getString("Staff.PlayerViewer.GUI.TeleportPlayer.Material")))
                .setName(lang.getString("Staff.PlayerViewer.GUI.TeleportPlayer.Name").replaceAll("%player%", target.getName()))
                .setLore(lang.getStringList("Staff.PlayerViewer.GUI.TeleportPlayer.Lore"));
        slot = config.getInt("Staff.PlayerViewer.GUI.TeleportPlayer.Slot");
        inv.item(slot, teleportPlayer.toItemStack());
        inv.clickButton(slot, (player, aInventoryGUI, clickType) -> {
            inspector.closeInventory();
            inspector.teleport(target);
            inspector.sendMessage(lang.getString("Staff.PlayerViewer.GUI.TeleportPlayer.TeleportToPlayer").replaceAll("%player%", target.getName()));
        });

        // Create Sanction Item
        if (inspector.hasPermission(config.getString("Sanction.Permission"))) {
            ItemCreator sanction = new ItemCreator(Material.matchMaterial(config.getString("Staff.PlayerViewer.GUI.Sanction.Material")))
                    .setName(lang.getString("Staff.PlayerViewer.GUI.Sanction.Name").replaceAll("%player%", target.getName()))
                    .setLore(lang.getStringList("Staff.PlayerViewer.GUI.Sanction.Lore"));
            slot = config.getInt("Staff.PlayerViewer.GUI.Sanction.Slot");
            inv.item(slot, sanction.toItemStack());
            inv.clickButton(slot, (player, aInventoryGUI, clickType) -> {
                inspector.closeInventory();
                Sanction.openSanctionGUI(player, target);
            });
        }

        //Create TopLuck item
        if (inspector.hasPermission(config.getString("TopLuck.Permission"))) {
            ItemCreator topLuck = new ItemCreator(Material.matchMaterial(config.getString("Staff.PlayerViewer.GUI.TopLuck.Material")))
                    .setName(lang.getString("Staff.PlayerViewer.GUI.TopLuck.Name").replaceAll("%player%", target.getName()))
                    .setLore(lang.getStringList("Staff.PlayerViewer.GUI.TopLuck.Lore"));
            slot = config.getInt("Staff.PlayerViewer.GUI.TopLuck.Slot");
            inv.item(slot, topLuck.toItemStack());
            inv.clickButton(slot, (player, aInventoryGUI, clickType) -> {
                inspector.closeInventory();
                TopLuck.openTopLuck(player);
            });
        }

        for (int i = 4; i < 14; i++)
            inv.item(i, new ItemCreator(Material.GRAY_STAINED_GLASS_PANE).toItemStack());

        ItemStack[] contents = target.getInventory().getContents();

        for (int i = 0; i < 9; i++)
            if (contents[i] != null)
                inv.item(45 + i, contents[i]);
        for (int i = 0; i < 27; i++)
            if (contents[i+9] != null)
                inv.item(18 + i, contents[i + 9]);
        for (int i = 0; i < 4; i++)
            if (contents[i + contents.length - 5] != null)
                inv.item(17 - i, contents[i + contents.length - 5]);

        inspector.openInventory(inv.build().getInventory());
    }
}
