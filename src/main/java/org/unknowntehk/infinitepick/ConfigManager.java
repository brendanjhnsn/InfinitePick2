package org.unknowntehk.infinitepick;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

public class ConfigManager {
    private JavaPlugin plugin;
    private FileConfiguration picksConfig;
    private File picksFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initConfig();
        loadPicksConfig();
    }

    private void initConfig() {
        plugin.saveDefaultConfig();
    }

    private void loadPicksConfig() {
        picksFile = new File(plugin.getDataFolder(), "picks.yml");
        if (!picksFile.exists()) {
            try {
                picksFile.createNewFile(); // Ensure the file exists
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        picksConfig = YamlConfiguration.loadConfiguration(picksFile);
    }

    public FileConfiguration getPicksConfig() {
        return picksConfig;
    }

    public void savePicksConfig() {
        try {
            picksConfig.save(picksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResourceBundle getConfig() {
        return null;
    }
}
