package fr.asvadia.astaff.modules;

import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XRay extends Module {
    public static final HashMap<Player, List<Chunk>> chunkLoaded = new HashMap<>();
    private static List<Material> blocks;

    @Override
    public void apply(Player player, SimpleItem item) {
        if (blocks == null) {
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            blocks = new ArrayList<>();
            config.getStringList("Staff.XRay.Blocks").forEach(s ->
                    blocks.add(Material.matchMaterial(s)));
        }

        if (chunkLoaded.containsKey(player)) {
            player.setGameMode(GameMode.CREATIVE);
            chunkLoaded.get(player).forEach(chunk ->
                    ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle())));
            chunkLoaded.remove(player);
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 255, false, false));
            chunkLoaded.put(player, new ArrayList<>());
            BlockPosition base = new BlockPosition(0, 255, 0);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (chunkLoaded.containsKey(player)) {
                        for (int x = 16; x >= -16; x-=16) {
                            for (int z = 16; z >= -16; z-=16) {
                                Chunk chunk = player.getLocation().add(x, 0, z).getChunk();
                                if (chunkLoaded.containsKey(player)
                                        && !chunkLoaded.get(player).contains(chunk))  {
                                    chunkLoaded.get(player).add(chunk);
                                    for (int X = 0; X < 16; X++) {
                                        for (int Z = 0; Z < 16; Z++) {
                                            int y = 0;
                                            Block block;
                                            List<Packet> packets = new ArrayList<>();
                                            while (chunk.getBlock(X, y, Z).getType() != Material.AIR) {
                                                block = chunk.getBlock(X,y,Z);
                                                BlockPosition blockPosition = new BlockPosition(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                                                if (!blocks.contains(chunk.getBlock(X, y, Z).getType()))
                                                    packets.add(new PacketPlayOutBlockChange(blockPosition, ((CraftWorld) chunk.getWorld()).getHandle().getType(base)));
                                                y++;
                                            }
                                            PlayerConnection p = ((CraftPlayer) player).getHandle().b;
                                            packets.forEach(p::sendPacket);
                                        }
                                    }
                                }
                            }
                        }
                    } else
                        this.cancel();
                }
            }.runTaskTimerAsynchronously(Main.getInstance(), 0, 10);
        }
    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {
        this.apply(player, item);

    }
}
