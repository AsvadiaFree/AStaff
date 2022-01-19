package fr.asvadia.astaff.scanner;

import fr.asvadia.astaff.utils.PlayerManager;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerScanner extends Scanner {
    private final YamlConfiguration ps;

    public PlayerScanner(Type type) {
        super(type);

        this.ps = FileManager.getValues().get(Files.PlayerScanner);
    }

    @Override
    public void start(boolean restart, Object... values) {
        sendEmbed("Statistiques", "Nombre de players : " + Bukkit.getOfflinePlayers().length);
        List<ItemStack> items;
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            Player p = PlayerManager.loadPlayer(player);
            if (p != null) {
                ps.set(player.getName() + ".uuid", p.getUniqueId().toString());
                ps.set(player.getName() + ".inv", p.getInventory().getContents());
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        FileManager.save(Files.PlayerScanner);
        sendEmbed("Mise à jour de la Progression", "Le PlayerScanner a terminé ! Retrouve toute les donnés dans le fichier ps.yml !");
    }


    @Override
    public void run() {
    }
}
