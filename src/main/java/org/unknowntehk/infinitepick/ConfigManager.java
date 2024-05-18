package org.unknowntehk.infinitepick;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConfigManager {
    private final InfinitePickaxe plugin;
    private final HashMap<String, PickaxeInventory> pickaxeInventories = new HashMap<>();

    public ConfigManager(InfinitePickaxe plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
    }

    public Set<String> getPickaxeNames() {
        return plugin.getConfig().getConfigurationSection("pickaxes").getKeys(false);
    }

    public ItemStack createPickaxe(String pickaxeKey) {
        FileConfiguration config = plugin.getConfig();
        String path = "pickaxes." + pickaxeKey + ".";
        String name = config.getString(path + "name");
        List<String> lore = config.getStringList(path + "lore");
        Material material = Material.getMaterial(config.getString(path + "material"));
        int customModelData = config.getInt(path + "custom_model_data");

        if (name == null || material == null) {
            return null;
        }

        ItemStack pickaxe = new ItemStack(material);
        ItemMeta meta = pickaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.setCustomModelData(customModelData);
            meta.getPersistentDataContainer().set(plugin.getPickaxeKey(), PersistentDataType.STRING, pickaxeKey);
            pickaxe.setItemMeta(meta);
        }

        // Initialize the pickaxe inventory if not already done
        pickaxeInventories.putIfAbsent(pickaxeKey, new PickaxeInventory(name));

        return pickaxe;
    }

    public PickaxeInventory getPickaxeInventory(String pickaxeKey) {
        pickaxeInventories.putIfAbsent(pickaxeKey, new PickaxeInventory(pickaxeKey));
        return pickaxeInventories.get(pickaxeKey);
    }

    public Material getStoredBlock(String pickaxeKey) {
        String storedBlockString = plugin.getConfig().getString("pickaxes." + pickaxeKey + ".stored_block");
        return Material.getMaterial(storedBlockString);
    }

    public Material getDropItem(String pickaxeKey) {
        String dropItemString = plugin.getConfig().getString("pickaxes." + pickaxeKey + ".drop_item");
        return Material.getMaterial(dropItemString);
    }
}
