package fr.asvadia.astaff.scanner;

import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;


public class PlayerScanner extends Scanner {
    private static final NBTManager powerNBT = PowerNBT.getApi();
    private final YamlConfiguration ps;

    public PlayerScanner(Type type) {
        super(type);

        this.ps = FileManager.getValues().get(Files.PlayerScanner);
    }

    @Override
    public void start(boolean restart, Object... values) {
        sendEmbed("Statistiques", "Nombre de players : " + Bukkit.getOfflinePlayers().length);
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            ps.set(player.getName() + ".uuid", player.getUniqueId().toString());
            ps.set(player.getName() + ".inv", powerNBT.readOfflinePlayer(player).getList("Inventory").toArray());
        }
        FileManager.save(Files.PlayerScanner);
        sendEmbed("Mise à jour de la Progression", "Le PlayerScanner a terminé ! Retrouve toute les donnés dans le fichier ps.yml !");
    }


    @Override
    public void run() {
    }
}
