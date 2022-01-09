package fr.asvadia.astaff.modules;

import fr.asvadia.astaff.Main;
import fr.asvadia.astaff.utils.StaffModules;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import fr.skyfighttv.simpleitem.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class Vanish extends Module {
    public static final List<Player> vanished = new ArrayList<>();
    private final StaffModules module = StaffModules.getByModule(this);

    @Override
    public void apply(Player player, SimpleItem item) {
        YamlConfiguration config = FileManager.getValues().get(Files.Config);
        YamlConfiguration message = FileManager.getValues().get(Files.Lang);
        if (vanished.contains(player)) {
            Bukkit.getOnlinePlayers().forEach(player1 -> player1.showPlayer(Main.getInstance(), player));
            vanished.forEach(player1 -> player.hidePlayer(Main.getInstance(), player1));

            if (item != null)
                item.removeEnchantment(Enchantment.DURABILITY);

            vanished.remove(player);
            player.sendMessage(message.getString("Staff.Vanish.Desactive"));
        } else {
            Bukkit.getOnlinePlayers().forEach(player1 -> player1.hidePlayer(Main.getInstance(), player));
            vanished.forEach(player1 -> {
                player1.showPlayer(Main.getInstance(), player);
                player.showPlayer(Main.getInstance(), player1);
            });

            if (item != null)
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

            vanished.add(player);
            player.sendMessage(message.getString("Staff.Vanish.Active"));
        }
        assert module != null;
        player.getInventory().setItem(config.getInt("Staff.Stuff." + module.getName() + ".Slot"), item.toItemStack());
    }

    @Override
    public void apply(Player player, SimpleItem item, Event event) {

    }
}
