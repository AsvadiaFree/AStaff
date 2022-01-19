package fr.asvadia.astaff.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PlayerManager {
    private static Field bukkitEntity;

    public static Player loadPlayer(OfflinePlayer offline) {
        if (!offline.hasPlayedBefore())
            return null;
        if (offline.getPlayer() != null)
            return offline.getPlayer();

        GameProfile profile = new GameProfile(offline.getUniqueId(), (offline.getName() != null) ? offline.getName() : offline.getUniqueId().toString());
        DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = dedicatedServer.getWorldServer(World.f);
        if (worldServer == null)
            return null;

        EntityPlayer entity = new EntityPlayer(dedicatedServer, worldServer, profile);

        try {
            if (bukkitEntity == null)
                bukkitEntity = Entity.class.getDeclaredField("bukkitEntity");
            bukkitEntity.setAccessible(true);
            bukkitEntity.set(entity, new CustomPlayer(entity.c.server, entity));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        CraftPlayer craftPlayer = entity.getBukkitEntity();
        if (craftPlayer != null)
            craftPlayer.loadData();
        return craftPlayer;
    }
}
