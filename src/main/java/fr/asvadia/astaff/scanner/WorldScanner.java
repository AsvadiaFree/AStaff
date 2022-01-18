package fr.asvadia.astaff.scanner;

import club.minnced.discord.webhook.WebhookClient;
import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldScanner extends Scanner {
    private final List<Chunk> chunks;
    private final YamlConfiguration ws;
    private final YamlConfiguration scanner;
    private int size;
    private World world;

    public WorldScanner(Type type) {
        super(type);

        this.ws = FileManager.getValues().get(Files.WorldScanner);
        this.scanner = FileManager.getValues().get(Files.Scanner);
        this.chunks = new ArrayList<>();
    }


    public WorldScanner setSize(int size) {
        this.size = size;
        scanner.set("ws.size", size);
        return this;
    }

    public WorldScanner setWorld(World world) {
        this.world = world;
        scanner.set("ws.world", world.getName());
        return this;
    }

    private void addChunk(Chunk chunk) {
        this.chunks.add(chunk);
    }

    @Override
    public void start(boolean restart, Object... values) {
        final int[] a;
        if (restart)
            a = new int[]{(int) values[0], (int) values[1]};
        else
            a = new int[]{-size, -size};

        boolean start = true;
        scanner.set("ws.start", true);
        FileManager.save(Files.Scanner);

        if (getWebhook() != null) {
            getWebhook().send("Lancement du scan des chunks !");
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendEmbed("Mise à jour de la Progression", "Chunk X : " + a[0] + "\nChunk Z : " + a[1]);
                    FileManager.save(Files.WorldScanner);

                    scanner.set("ws.X", a[0]);
                    scanner.set("ws.Z", a[1]);
                    FileManager.save(Files.Scanner);

                    if (a[1] > size)
                        this.cancel();
                }
            }.runTaskTimerAsynchronously(Main.getInstance(), 0, 600);
        }

        this.runTaskTimer(Main.getInstance(), 0, 0);
        while (start) {
            if (MinecraftServer.getServer().recentTps[0] > 19) {
                addChunk(world.getChunkAt(a[0]++, a[1]));

                if (a[0] > size) {
                    a[0] = -size;
                    a[1]++;
                }

                if (a[1] > size) {
                    this.cancel();
                    start = false;
                    scanner.set("ws.start", false);
                    FileManager.save(Files.Scanner);
                    FileManager.save(Files.WorldScanner);

                    sendEmbed("Mise à jour de la Progression", "Le WorldScanner a terminé ! Retrouve toute les donnés dans le fichier ws.yml !");
                }
            }
        }
    }

    @Override
    public void run() {
        if (!chunks.isEmpty()) {
            for (BlockState state : chunks.remove(0).getTileEntities())
                if (state instanceof Container c) {
                    List<String> str = new ArrayList<>();
                    for (ItemStack item : c.getInventory().getContents())
                        if (item != null)
                            str.add(item.getAmount() + "x " + item.getType().name() + " data : " + (item.hasItemMeta() ? Objects.requireNonNull(item.getItemMeta()).hasCustomModelData() ? item.getItemMeta().getCustomModelData() : 0 : 0));
                    if (!str.isEmpty()) {
                        Location loc = c.getLocation();
                        ws.set(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ(), str.toArray());
                    }
                }
        }
    }
}
