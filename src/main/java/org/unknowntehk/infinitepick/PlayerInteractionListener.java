package org.unknowntehk.infinitepick;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractionListener implements Listener {
    private PickaxeManager pickaxeManager;
    private ConfigManager configManager;

    public PlayerInteractionListener(PickaxeManager pickaxeManager, ConfigManager configManager) {
        this.pickaxeManager = pickaxeManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.DIAMOND_PICKAXE && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasLore()) {
            String action = itemInHand.getItemMeta().getLore().get(0);
            if (action.equals("Infinite Mining")) {
                event.setCancelled(true);
                player.sendMessage("You are using your Infinite Mining Pickaxe!");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.DIAMOND_PICKAXE && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasCustomModelData()) {
            if (player.hasPermission("infinitepick.use")) {
                String blockType = configManager.getConfig().getString("allowedBlocks." + event.getBlock().getType().toString());
                if (blockType != null && blockType.equals("true")) {
                    pickaxeManager.addBlockToInventory(player, new ItemStack(event.getBlock().getType()));
                    event.setDropItems(false);
                }
            } else {
                player.sendMessage("You do not have permission to use this custom pickaxe.");
            }
        }
    }
}
