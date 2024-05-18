package org.unknowntehk.infinitepick;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PickaxeListener implements Listener {
    private final InfinitePickaxe plugin;

    public PickaxeListener(InfinitePickaxe plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (isCustomPickaxe(item)) {
            String pickaxeKey = getPickaxeKey(item);
            Material storedBlock = Material.getMaterial(plugin.getConfig().getString("pickaxes." + pickaxeKey + ".stored_block"));
            if (event.getBlock().getType() == storedBlock) {
                event.setDropItems(false);

                // Simulate drop of the block type (e.g., Diamond Ore drops Diamond)
                Material dropMaterial = getDropMaterial(storedBlock);
                if (dropMaterial != null) {
                    ItemStack dropItem = new ItemStack(dropMaterial);
                    addItemToPickaxeStorage(item, dropItem);
                    plugin.getDatabaseManager().savePickaxeData(item, event.getPlayer().getUniqueId().toString());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT_CLICK") && event.getPlayer().isSneaking()) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (isCustomPickaxe(item)) {
                event.setCancelled(true);
                openPickaxeInventory(event.getPlayer(), item);
            }
        }
    }

    private boolean isCustomPickaxe(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(plugin.getPickaxeKey(), PersistentDataType.STRING);
    }

    private String getPickaxeKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().get(plugin.getPickaxeKey(), PersistentDataType.STRING);
        }
        return null;
    }

    private void addItemToPickaxeStorage(ItemStack item, ItemStack dropItem) {
        String pickaxeKey = getPickaxeKey(item);
        if (pickaxeKey != null) {
            PickaxeInventory inventory = plugin.getConfigManager().getPickaxeInventory(pickaxeKey);
            if (inventory != null) {
                inventory.addItem(dropItem);
            }
        }
    }

    private void openPickaxeInventory(Player player, ItemStack item) {
        String pickaxeKey = getPickaxeKey(item);
        if (pickaxeKey != null) {
            PickaxeInventory inventory = plugin.getConfigManager().getPickaxeInventory(pickaxeKey);
            if (inventory != null) {
                player.openInventory(inventory.getInventory());
            }
        }
    }

    private Material getDropMaterial(Material storedBlock) {
        // Add logic for other block drops as needed
        switch (storedBlock) {
            case DIAMOND_ORE:
                return Material.DIAMOND;
            case COAL_ORE:
                return Material.COAL;
            // Add cases for other blocks and their corresponding drops
            default:
                return storedBlock;
        }
    }
}
