package fr.asvadia.astaff.commands;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fr.asvadia.astaff.utils.file.FileManager;
import fr.asvadia.astaff.utils.file.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class StaffTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            List<String> tab = new ArrayList<>();
            if (sender.hasPermission(config.getString("Staff.Stuff.PlayerViewer.Permission")))
                tab.add("view");
            if (sender.hasPermission(config.getString("Staff.Stuff.Freeze.Permission")))
                tab.add("freeze");
            if (sender.hasPermission("astaff.lockchat"))
                tab.add("lockchat");
            return a(args, tab);
        }
        return null;
    }

    public static List<String> a(String[] var0, String... var1) {
        return a(var0, Arrays.asList(var1));
    }

    public static List<String> a(String[] var0, Collection<?> var1) {
        String var2 = var0[var0.length - 1];
        ArrayList var3 = Lists.newArrayList();
        if (!var1.isEmpty()) {
            Iterator var4 = Iterables.transform(var1, Functions.toStringFunction()).iterator();

            while(var4.hasNext()) {
                String var5 = (String)var4.next();
                if (a(var2, var5)) {
                    var3.add(var5);
                }
            }

            if (var3.isEmpty()) {
                var4 = var1.iterator();

                while(var4.hasNext()) {
                    Object var6 = var4.next();
                    if (a(var2, (String) var6)) {
                        var3.add(String.valueOf(var6));
                    }
                }
            }
        }

        return var3;
    }

    public static boolean a(String var0, String var1) {
        return var1.regionMatches(true, 0, var0, 0, var0.length());
    }
}
