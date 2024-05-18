package org.unknowntehk.infinitepick;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinListener implements Listener {
    private final InfinitePickaxe plugin;

    public PlayerJoinListener(InfinitePickaxe plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (ItemStack item : event.getPlayer().getInventory().getContents()) {
            if (item != null && isCustomPickaxe(item)) {
                plugin.getDatabaseManager().loadPickaxeData(item, event.getPlayer().getUniqueId().toString());
            }
        }
    }

    private boolean isCustomPickaxe(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(plugin.getPickaxeKey(), PersistentDataType.STRING);
    }
}
