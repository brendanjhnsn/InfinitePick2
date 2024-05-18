package org.unknowntehk.infinitepick;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PickaxeCommand implements CommandExecutor, TabCompleter {
    private final InfinitePickaxe plugin;

    public PickaxeCommand(InfinitePickaxe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /givepickaxe <player> <pickaxe_name>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        String pickaxeName = args[1];
        ItemStack pickaxe = plugin.getConfigManager().createPickaxe(pickaxeName);
        if (pickaxe == null) {
            sender.sendMessage("Pickaxe not found in configuration.");
            return false;
        }

        plugin.getDatabaseManager().loadPickaxeData(pickaxe, target.getUniqueId().toString());
        target.getInventory().addItem(pickaxe);
        sender.sendMessage("Gave " + target.getName() + " the " + pickaxeName + " pickaxe.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(plugin.getConfigManager().getPickaxeNames());
        }
        return null;
    }
}
