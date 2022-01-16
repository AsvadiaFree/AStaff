package fr.asvadia.astaff.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopLuckWebHook extends BukkitRunnable {
    private final WebhookClient webhook;

    public TopLuckWebHook() {
        YamlConfiguration config = FileManager.getValues().get(Files.Config);
        webhook = WebhookClient.withUrl(config.getString("TopLuck.Webhook.Url"));

        this.runTaskTimerAsynchronously(Main.getInstance(), config.getInt("TopLuck.Webhook.Time"), config.getInt("TopLuck.Webhook.Time"));
    }

    @Override
    public void run() {
        YamlConfiguration config = FileManager.getValues().get(Files.Config);

        TopLuck.updateTopLuck();

        List<Player> players = ((List<Player>) new ArrayList<>(Bukkit.getOnlinePlayers())).stream()
                .sorted(Comparator.comparingDouble(value -> TopLuck.playerScore.get(value.getUniqueId())))
                .toList();
        boolean isAlert = false;

        WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                .setColor(0xFF00EE)
                .setAuthor(new WebhookEmbed.EmbedAuthor("TopLuck", "https://asvadia.eu/storage/img/logo.png", "https://asvadia.eu/"));

        for (int i = 0; i < 5; i++) {
            String name = i+1 + ". ";
            StringBuilder value = new StringBuilder();
            Player target = null;
            if (players.size() > i)
                target = players.get(i);

            if (target == null) {
                name += "N/A";
                value.append("N/A");
            } else {
                name += target.getName();
                if (TopLuck.playerScore.get(target.getUniqueId()) >= config.getInt("TopLuck.Webhook.Alert"))
                    isAlert = true;

                value.append("Global Score : ").append(TopLuck.playerScore.get(target.getUniqueId())).append("\n")
                        .append("\n");
                for (TopLuckOres topLuckOres : TopLuckOres.values()) {
                    if (topLuckOres == TopLuckOres.ALL)
                        continue;
                    value.append(topLuckOres.getName())
                            .append(" : (")
                            .append(TopLuck.playerOreCount.get(topLuckOres).get(target.getUniqueId()))
                            .append(") ")
                            .append(TopLuck.playerOreScore.get(topLuckOres).get(target.getUniqueId()))
                            .append("\n");
                }
            }

            embed.addField(new WebhookEmbed.EmbedField(true, name, value.toString()));
        }
        webhook.send(embed.build());
        if (isAlert)
            webhook.send("<@" + config.getString("TopLuck.Webhook.AlertRole") + ">");
    }
}
