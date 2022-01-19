package fr.asvadia.astaff.utils;

import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.storage.WorldNBTStorage;
import org.apache.logging.log4j.LogManager;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;

import java.io.File;

public class CustomPlayer extends CraftPlayer {
    public CustomPlayer(CraftServer server, EntityPlayer entity) {
        super(server, entity);
    }

    public void loadData() {
        NBTTagCompound loaded = (this.server.getHandle()).r.load(getHandle());
        if (loaded != null)
            readExtraData(loaded);
    }

    public void saveData() {
        EntityPlayer player = getHandle();
        try {
            WorldNBTStorage worldNBTStorage = (player.c.getPlayerList()).r;
            NBTTagCompound playerData = player.save(new NBTTagCompound());
            setExtraData(playerData);
            if (!isOnline()) {
                NBTTagCompound oldData = worldNBTStorage.load((EntityHuman)player);
                if (oldData != null && oldData.hasKeyOfType("RootVehicle", 10))
                    playerData.set("RootVehicle", (NBTBase)oldData.getCompound("RootVehicle"));
            }
            File file = File.createTempFile(player.getUniqueIDString() + "-", ".dat", worldNBTStorage.getPlayerDir());
            NBTCompressedStreamTools.a(playerData, file);
            File file1 = new File(worldNBTStorage.getPlayerDir(), player.getUniqueIDString() + ".dat");
            File file2 = new File(worldNBTStorage.getPlayerDir(), player.getUniqueIDString() + ".dat_old");
            SystemUtils.a(file1, file, file2);
        } catch (Exception e) {
            LogManager.getLogger().warn("Failed to save player data for {}", player.getDisplayName().getString());
        }
    }
}
