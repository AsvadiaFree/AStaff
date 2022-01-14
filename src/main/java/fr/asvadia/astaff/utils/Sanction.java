package fr.asvadia.astaff.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import fr.asvadia.api.bukkit.menu.inventory.AInventoryGUI;
import fr.asvadia.api.bukkit.menu.inventory.button.ClickButton;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Sanction {
    private static final HashMap<Player, AInventoryGUI.Builder> sanctionInventory = new HashMap<>();
    private static WebhookClient webhook;

    public static void openSanctionGUI(Player player, Player target) {
        if (!sanctionInventory.containsKey(player)) {
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
            String defaultPath = "Sanction.GUI";
            AInventoryGUI.Builder inv = AInventoryGUI.builder()
                    .size(54)
                    .title(lang.getString(defaultPath + ".Title"));

            for (int i = 9; i < 18; i++)
                inv.item(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

            int slot = 0;
            config.getConfigurationSection(defaultPath).getKeys(false).forEach(s -> {
                if (player.hasPermission(config.getString(defaultPath + "." + s + ".Permission"))) {
                    inv.item(slot,
                            new ItemCreator(Material.matchMaterial(config.getString(defaultPath + "." + s + ".Material")))
                                    .setName(lang.getString(defaultPath + "." + s + ".Name"))
                                    .setLore(lang.getStringList(defaultPath + "." + s + ".Lore"))
                                    .toItemStack());
                    inv.clickButton(slot, (player1, aInventoryGUI, clickType) -> {
                        for (int i = 18; i < 53; i++)
                            if (aInventoryGUI.getInventory().getItem(i) != null)
                                aInventoryGUI.getInventory().setItem(i, null);

                        String defaultPathReason = defaultPath + "." + s + ".Reasons";
                        AtomicInteger slot2 = new AtomicInteger();

                        config.getConfigurationSection(defaultPathReason).getKeys(false).forEach(s1 -> {
                            if (player1.hasPermission(config.getString(defaultPathReason + "." + s1 + ".Permission"))) {
                                slot2.set(config.getInt(defaultPathReason + "." + s1 + ".Slot"));

                                aInventoryGUI.getInventory().setItem(slot2.get(), new ItemCreator(Material.matchMaterial(config.getString(defaultPathReason + "." + s1 + ".Material")))
                                        .setName(lang.getString(defaultPathReason + "." + s1 + ".Name"))
                                        .setLore(lang.getStringList(defaultPathReason + "." + s1 + ".Lore"))
                                        .toItemStack());
                                aInventoryGUI.getButtons().put(slot2.get(), new ClickButton(slot2.get(), (player2, aInventoryGUI1, clickType1) -> {
                                    if (target == null) {
                                        player2.sendMessage(lang.getString(defaultPath + ".SanctionError"));
                                        player2.closeInventory();
                                        return;
                                    }

                                    // Do sanction
                                    String type = config.getString(defaultPathReason + "." + s1 + ".Sanction.Type");
                                    String time = config.getString(defaultPathReason + "." + s1 + ".Sanction.Time");
                                    String reason = config.getString(defaultPathReason + "." + s1 + ".Sanction.Reason");
                                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), type + " " + target.getName() + "" + time + " " + reason);
                                    if (webhook == null)
                                        webhook = WebhookClient.withUrl(config.getString("Sanction.Webhook.Url"));
                                    WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                                            .setAuthor(new WebhookEmbed.EmbedAuthor("Sanction", "https://asvadia.eu/storage/img/logo.png", "https://asvadia.eu/"))
                                            .setColor(0xFF00EE)
                                            .addField(new WebhookEmbed.EmbedField(true, player2.getName() + " a sanctionné " + target.getName(), "Type: " + type +
                                                    "\nTime: " + time +
                                                    "\nRaison: " + reason));
                                    webhook.send(embed.build());
                                    player2.sendMessage(config.getString(defaultPathReason + "." + s1 + ".Messages.Modo"));
                                    player2.closeInventory();
                                }));
                            }
                        });
                    });
                }
            });
            sanctionInventory.put(player, inv);
        }
        player.openInventory(sanctionInventory.get(player).build().getInventory());
    }
}
