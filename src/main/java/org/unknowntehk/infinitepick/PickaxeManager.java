package org.unknowntehk.infinitepick;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PickaxeManager {
    private JavaPlugin plugin;
    private ConfigManager configManager;

    public PickaxeManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void createAndSavePickaxe(String name, Material material) {
        if (material != null && material.isItem()) {
            // Creating a new ItemStack with the given material
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(name);
            itemStack.setItemMeta(meta);

            // Saving the pickaxe to the config
            configManager.getPicksConfig().set("pickaxes." + name, material.toString());
            configManager.savePicksConfig();
        } else {
            plugin.getLogger().info("Invalid material type provided.");
        }
    }

    public void giveCustomPickaxe(Player player, String pickaxeName) {
        String materialName = configManager.getPicksConfig().getString("pickaxes." + pickaxeName);
        if (materialName != null) {
            Material material = Material.matchMaterial(materialName);
            if (material != null && material.isItem()) {
                ItemStack itemStack = new ItemStack(material);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(pickaxeName);
                itemStack.setItemMeta(meta);

                player.getInventory().addItem(itemStack);
                player.sendMessage("You have received a custom pickaxe named '" + pickaxeName + "'.");
            } else {
                player.sendMessage("Failed to create custom pickaxe: Invalid type '" + materialName + "'.");
            }
        } else {
            player.sendMessage("No pickaxe found with the name '" + pickaxeName + "'.");
        }
    }

    public List<ItemStack> getAllCustomPickaxes() {
    return null;
    }

    public void addBlockToInventory(Player player, ItemStack itemStack) {
    }
}
