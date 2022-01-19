package fr.asvadia.astaff.scanner;

import fr.asvadia.astaff.utils.PlayerManager;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class EnderChestScanner extends Scanner {
    private final YamlConfiguration ecs;

    public EnderChestScanner(Type type) {
        super(type);

        this.ecs = FileManager.getValues().get(Files.EnderChestScanner);
    }

    @Override
    public void start(boolean restart, Object... values) {
        sendEmbed("Statistiques", "Nombre de players : " + Bukkit.getOfflinePlayers().length);
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            Player p = PlayerManager.loadPlayer(player);
            if (p != null) {
                ecs.set(player.getName() + ".uuid", player.getUniqueId());
                ecs.set(player.getName() + ".inv", p.getEnderChest().getContents());
            }
        }
        FileManager.save(Files.EnderChestScanner);
        sendEmbed("Mise à jour de la Progression", "Le EnderChestScanner a terminé ! Retrouve toute les donnés dans le fichier ecs.yml !");
    }

    @Override
    public void run() {

    }


}
