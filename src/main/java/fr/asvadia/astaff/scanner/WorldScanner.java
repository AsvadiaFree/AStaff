package fr.asvadia.astaff.scanner;

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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WorldScanner extends Scanner {
    private final List<Chunk> chunks;
    private final YamlConfiguration ws;
    private final YamlConfiguration scanner;
    private boolean start;
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
        if (world != null) {
            this.world = world;
            scanner.set("ws.world", world.getName());
        }
        return this;
    }

    private void addChunk(Chunk chunk) {
        this.chunks.add(chunk);
    }

    @Override
    public void start(boolean restart, Object... values) {
        if (this.world == null)
            return;
        final int[] a;
        if (restart)
            a = new int[]{(int) values[0], (int) values[1]};
        else
            a = new int[]{-size, -size};

        start = true;
        scanner.set("ws.start", true);
        FileManager.save(Files.Scanner);

        if (getWebhook() != null) {
            getWebhook().send("Lancement du scan des chunks !");
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendEmbed("Mise ?? jour de la Progression", "Chunk X : " + a[0] + "\nChunk Z : " + a[1] + "\nChunks size : " + chunks.size());
                    FileManager.save(Files.WorldScanner);

                    scanner.set("ws.X", a[0]);
                    scanner.set("ws.Z", a[1]);
                    FileManager.save(Files.Scanner);

                    if (!start && chunks.isEmpty())
                        this.cancel();
                }
            }.runTaskTimerAsynchronously(Main.getInstance(), 0, 600);
        }

        this.runTaskTimer(Main.getInstance(), 0, 0);
        for (;;) {
            addChunk(world.getChunkAt(a[0]++, a[1]));

            if (a[0] > size) {
                a[0] = -size;
                a[1]++;
            }

            if (a[1] >= size) {
                start = false;
                scanner.set("ws.start", false);
                FileManager.save(Files.Scanner);
                FileManager.save(Files.WorldScanner);
                sendEmbed("Mise ?? jour de la Progression", "Le WorldScanner a termin?? son pr??chargement des chunks! Retrouve toute les donn??s dans le fichier ws.yml !");
                return;
            }
        }
    }

    @Override
    public void run() {
        if (MinecraftServer.getServer().recentTps[0] > 19) {
            if (!chunks.isEmpty()) {
                for (BlockState state : chunks.remove(0).getTileEntities()) {
                    if (state instanceof Container c) {
                        Location loc = c.getLocation();
                        ws.set(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ(), c.getInventory().getContents());
                    }
                }
            } else if (!start) {
                this.cancel();
                FileManager.save(Files.Scanner);
                FileManager.save(Files.WorldScanner);
                sendEmbed("Mise ?? jour de la Progression", "Le WorldScanner a termin?? son check des chunks! Retrouve toute les donn??s dans le fichier ws.yml !");
            }
        }
    }
}
