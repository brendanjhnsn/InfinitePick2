package org.unknowntehk.infinitepick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class InfinitePick extends JavaPlugin {
    private ConfigManager configManager;
    private PickaxeManager pickaxeManager;
    private GUI gui;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        pickaxeManager = new PickaxeManager(this, configManager);
        gui = new GUI(this, pickaxeManager, configManager);
        getLogger().info("InfinitePick enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("InfinitePick disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("Usage: /infinitepick <create|give> [player] [type]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (player.hasPermission("infinitepick.use")) {
                    gui.openGUI(player);
                } else {
                    player.sendMessage("You do not have permission to create pickaxes.");
                }
                break;
            case "give":
                if (args.length < 3) {
                    player.sendMessage("Usage: /infinitepick give <player> <type>");
                    return true;
                }
                if (player.hasPermission("infinitepick.give")) {
                    pickaxeManager.giveCustomPickaxe(player, args[1]);
                } else {
                    player.sendMessage("You do not have permission to give pickaxes.");
                }
                break;
            default:
                sender.sendMessage("Invalid command. Use /infinitepick <create|give>");
                break;
        }
        return true;
    }
}
