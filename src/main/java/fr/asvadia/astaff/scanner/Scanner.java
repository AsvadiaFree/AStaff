package fr.asvadia.astaff.scanner;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;

public abstract class Scanner extends BukkitRunnable {
    private static WebhookClient webhook;
    private final Type type;
    private Thread thread;

    Scanner(Type type) {
        this.type = type;
    }

    public void asyncStart(boolean restart, Object... values) {
        new Thread(() -> start(restart, values))
                .start();
    }
    public abstract void start(boolean restart, Object... values);

    public Type getType() {
        return type;
    }

    public Thread getThread() {
        return thread;
    }

    public WebhookClient getWebhook() {
        return webhook;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void sendEmbed(String str, String str2) {
        if (getWebhook() != null)
            getWebhook().send(new WebhookEmbedBuilder()
                    .setAuthor(new WebhookEmbed.EmbedAuthor("Scanner - " + type.getName(), "https://asvadia.eu/storage/img/logo.png", "https://asvadia.eu/"))
                    .setColor(Color.GREEN.getRGB())
                    .addField(new WebhookEmbed.EmbedField(true, str, str2))
                    .build());
    }

    public static void init() {
        try {
            webhook = WebhookClient.withUrl("https://discord.com/api/webhooks/932796247599886336/cjCsTA8ot-7IXQIEv5Gr-CdgdaE97WR5Eoldvf5UQdhCrsilaKIfes5oYE7_cKYesaBb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Type {
        WORLD("world"),
        PLAYER("player"),
        ENDER_CHEST("enderchest");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Scanner.Type getByName(String name) {
            for (Type t : Type.values())
                if (t.getName().equals(name))
                    return t;
            return null;
        }
    }
}
