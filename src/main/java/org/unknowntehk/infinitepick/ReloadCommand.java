package org.unknowntehk.infinitepick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final InfinitePickaxe plugin;

    public ReloadCommand(InfinitePickaxe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            return false;
        }

        plugin.reloadConfig();
        plugin.getConfigManager().loadConfig();
        sender.sendMessage("Configuration reloaded.");
        return true;
    }
}
