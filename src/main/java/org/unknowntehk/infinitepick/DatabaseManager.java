package org.unknowntehk.infinitepick;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private final InfinitePickaxe plugin;
    private Connection connection;

    public DatabaseManager(InfinitePickaxe plugin) {
        this.plugin = plugin;
    }

    public void setupDatabase() {
        try {
            if (plugin.getConfig().getString("database.type").equalsIgnoreCase("mysql")) {
                String host = plugin.getConfig().getString("database.host");
                int port = plugin.getConfig().getInt("database.port");
                String database = plugin.getConfig().getString("database.database");
                String username = plugin.getConfig().getString("database.username");
                String password = plugin.getConfig().getString("database.password");

                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            } else {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/pickaxes.db");
            }

            // Create table if it doesn't exist
            try (PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS pickaxes (uuid TEXT, type TEXT, items TEXT, PRIMARY KEY (uuid, type))")) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Could not establish database connection: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not close database connection: " + e.getMessage());
        }
    }

    public void savePickaxeData(ItemStack pickaxe, String uuid) {
        String pickaxeKey = pickaxe.getItemMeta().getPersistentDataContainer().get(plugin.getPickaxeKey(), PersistentDataType.STRING);
        PickaxeInventory inventory = plugin.getConfigManager().getPickaxeInventory(pickaxeKey);
        String items = serializeInventory(inventory.getInventory());

        try (PreparedStatement statement = connection.prepareStatement(
                "REPLACE INTO pickaxes (uuid, type, items) VALUES (?, ?, ?)")) {
            statement.setString(1, uuid);
            statement.setString(2, pickaxeKey);
            statement.setString(3, items);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not save pickaxe data: " + e.getMessage());
        }
    }

    public void loadPickaxeData(ItemStack pickaxe, String uuid) {
        String pickaxeKey = pickaxe.getItemMeta().getPersistentDataContainer().get(plugin.getPickaxeKey(), PersistentDataType.STRING);
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT items FROM pickaxes WHERE uuid = ? AND type = ?")) {
            statement.setString(1, uuid);
            statement.setString(2, pickaxeKey);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String items = resultSet.getString("items");

                PickaxeInventory inventory = plugin.getConfigManager().getPickaxeInventory(pickaxeKey);
                deserializeInventory(inventory.getInventory(), items);
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Could not load pickaxe data: " + e.getMessage());
        }
    }

    private String serializeInventory(Inventory inventory) {
        StringBuilder serialized = new StringBuilder();
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                serialized.append(item.getType().name()).append(":").append(item.getAmount()).append(";");
            }
        }
        return serialized.toString();
    }

    private void deserializeInventory(Inventory inventory, String data) {
        inventory.clear();
        String[] items = data.split(";");
        for (String itemData : items) {
            if (!itemData.isEmpty()) {
                String[] itemParts = itemData.split(":");
                Material material = Material.getMaterial(itemParts[0]);
                int amount = Integer.parseInt(itemParts[1]);

                inventory.addItem(new ItemStack(material, amount));
            }
        }
    }

    public void saveAllPickaxes() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && isCustomPickaxe(item)) {
                    savePickaxeData(item, player.getUniqueId().toString());
                }
            }
        }
    }

    private boolean isCustomPickaxe(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(plugin.getPickaxeKey(), PersistentDataType.STRING);
    }
}
