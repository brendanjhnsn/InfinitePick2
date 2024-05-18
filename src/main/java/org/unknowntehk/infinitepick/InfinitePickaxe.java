package org.unknowntehk.infinitepick;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class InfinitePickaxe extends JavaPlugin {
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private NamespacedKey pickaxeKey;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfig();
        this.pickaxeKey = new NamespacedKey(this, "pickaxe_key");
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.setupDatabase();

        PickaxeCommand pickaxeCommand = new PickaxeCommand(this);
        getCommand("givepickaxe").setExecutor(pickaxeCommand);
        getCommand("givepickaxe").setTabCompleter(pickaxeCommand);
        getCommand("infinitepickreload").setExecutor(new ReloadCommand(this));

        getServer().getPluginManager().registerEvents(new PickaxeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getLogger().info("InfinitePickaxe has been enabled.");
    }

    @Override
    public void onDisable() {
        databaseManager.saveAllPickaxes();
        databaseManager.closeConnection();
        getLogger().info("InfinitePickaxe has been disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public NamespacedKey getPickaxeKey() {
        return pickaxeKey;
    }
}
