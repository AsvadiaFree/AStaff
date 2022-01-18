package fr.asvadia.astaff.utils;

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
import java.util.Arrays;
import java.util.List;

public class WorldScanner {
    private static WebhookClient client;
    private static boolean running = false;

    public static void init() {
        try {
            client = WebhookClient.withUrl("https://discord.com/api/webhooks/932796247599886336/cjCsTA8ot-7IXQIEv5Gr-CdgdaE97WR5Eoldvf5UQdhCrsilaKIfes5oYE7_cKYesaBb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scan(World world, int x, int z) {
        if (running)
            return;
        running = true;

        Bukkit.getLogger().info("Start");

        if (client != null)
            client.send("Lancement du scan des chunks !");

        YamlConfiguration ws = FileManager.getValues().get(Files.WorldScanner);

        final int[] X = {-x};
        final int[] Z = {-z};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (MinecraftServer.getServer().recentTps[0] > 17.5) {
                    int finalX = X[0]++;
                    int finalZ = Z[0];

                    if (X[0] > x) {
                        X[0] = -x;
                        Z[0]++;
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Chunk chunk = world.getChunkAt(finalX, finalZ);
                            for (BlockState state : chunk.getTileEntities())
                                if (state instanceof Container c) {
                                    List<String> str = new ArrayList<>();
                                    for (ItemStack item : c.getInventory().getContents())
                                        if (item != null)
                                            str.add(item.getAmount() + "x " + item.getType().name());
                                    if (!str.isEmpty()) {
                                        Location loc = c.getLocation();
                                        ws.set(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ(), str.toArray());
                                    }
                                }
                        }
                    }.runTask(Main.getInstance());

                    if (Z[0] >= x) {
                        this.cancel();
                        FileManager.save(Files.WorldScanner);

                        if (client != null)
                            client.send("Scan fini !");
                        running = false;
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
    }
}
