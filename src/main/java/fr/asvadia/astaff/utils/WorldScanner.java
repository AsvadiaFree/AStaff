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
import java.util.List;
import java.util.Objects;

public class WorldScanner extends BukkitRunnable {
    private static WebhookClient client;
    private static boolean running = false;
    private final List<Chunk> chunks;
    private final YamlConfiguration ws;
    private boolean runnable;

    public WorldScanner(YamlConfiguration ws) {
        this.ws = ws;
        this.chunks = new ArrayList<>();
        this.runnable = false;
    }

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
        WorldScanner worldScanner = new WorldScanner(ws);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (MinecraftServer.getServer().recentTps[0] > 19) {
                    int finalX = X[0]++;
                    int finalZ = Z[0];

                    if (X[0] > x) {
                        X[0] = -x;
                        Z[0]++;
                    }

                    worldScanner.addChunk(world.getChunkAt(finalX, finalZ));

                    if (Z[0] >= x) {
                        worldScanner.cancel();
                        this.cancel();
                        FileManager.save(Files.WorldScanner);

                        if (client != null)
                            client.send("Scan fini !");
                        running = false;
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 0);

        if (client != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    client.send("Progression actuelle >> X : " + X[0] + " et Z : " + Z[0]);
                    if (Z[0] >= x)
                        this.cancel();
                }
            }.runTaskTimerAsynchronously(Main.getInstance(), 0, 600);
        }
    }

    @Override
    public void run() {
        if (!chunks.isEmpty())
            for (Chunk chunk : new ArrayList<>(chunks)) {
                Bukkit.getLogger().info("Check chunk");
                for (BlockState state : chunk.getTileEntities()) {
                    if (state instanceof Container c) {
                        List<String> str = new ArrayList<>();
                        for (ItemStack item : c.getInventory().getContents())
                            if (item != null)
                                str.add(item.getAmount() + "x " + item.getType().name() + " data : " + (item.hasItemMeta() ? Objects.requireNonNull(item.getItemMeta()).getCustomModelData() : 0));
                        if (!str.isEmpty()) {
                            Location loc = c.getLocation();
                            ws.set(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ(), str.toArray());
                        }
                    }
                }
                this.chunks.remove(chunk);
            }
        this.runnable = true;
    }

    public void addChunk(Chunk chunk) {
        this.chunks.add(chunk);
        if (this.runnable) {
            this.runnable = false;
            this.runTask(Main.getInstance());
        }
    }
}
