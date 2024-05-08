package org.unknowntehk.infinitepick;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GUI implements Listener {
    private JavaPlugin plugin;
    private PickaxeManager pickaxeManager;
    private ConfigManager configManager;
    private HashMap<UUID, String> awaitingInput;
    private HashMap<UUID, Inventory> openInventories;
    private HashSet<Inventory> materialInventories;

    public GUI(JavaPlugin plugin, PickaxeManager pickaxeManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.pickaxeManager = pickaxeManager;
        this.configManager = configManager;
        this.awaitingInput = new HashMap<>();
        this.openInventories = new HashMap<>();
        this.materialInventories = new HashSet<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "Pickaxe Management");

        // Displaying existing custom pickaxes
        List<ItemStack> pickaxes = pickaxeManager.getAllCustomPickaxes(); // Implement this method to retrieve saved pickaxes
        for (int i = 0; i < pickaxes.size() && i < 45; i++) {
            inv.setItem(i, pickaxes.get(i));
        }

        // Adding a special item to create new pickaxe
        inv.setItem(49, createItem(Material.ANVIL, "Create New Pickaxe", "Click to create a new custom pickaxe"));

        player.openInventory(inv);
        openInventories.put(player.getUniqueId(), inv);
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();

        if (inv != null && (openInventories.containsKey(player.getUniqueId()) || materialInventories.contains(inv))) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();
                if (materialInventories.contains(inv)) {
                    // Handle material selection
                    handleMaterialSelection(player, clickedItem);
                    materialInventories.remove(inv);  // Clean up
                    openGUI(player);  // Reopen the main GUI
                } else {
                    // Handling other item interactions
                    handleItemInteraction(player, String.valueOf(clickedItem), inv);
                }
            }
        }
    }

    private void handleItemInteraction(Player player, String itemName, Inventory inv) {
        switch (itemName) {
            case "Base Material":
            case "Choose Base Material":
                openMaterialMenu(player);
                break;
            case "Set Custom Model Data":
            case "Set Lore":
                awaitingInput.put(player.getUniqueId(), itemName.toLowerCase().replace(" ", ""));
                player.closeInventory();  // Close inventory before asking for input
                player.sendMessage("Please enter the " + itemName.toLowerCase() + " in chat.");
                break;
            case "Create New Pickaxe":
                awaitingInput.put(player.getUniqueId(), "newPickaxe");
                player.closeInventory();
                player.sendMessage("Please enter the name and material for your new pickaxe in chat, format: name,material");
                break;
            case "Save Changes":
                player.closeInventory();
                player.sendMessage("Your changes have been saved.");
                break;
        }
    }


    private void handleMaterialSelection(Player player, ItemStack selectedMaterial) {
        player.sendMessage("Selected material: " + selectedMaterial.getType());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (openInventories.containsKey(event.getWhoClicked().getUniqueId()) ||
                materialInventories.contains(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    private void openMaterialMenu(Player player) {
        Inventory materialInv = Bukkit.createInventory(null, 9, "Select Base Material");
        materialInv.addItem(createItem(Material.WOODEN_PICKAXE, "Wood Pickaxe"));
        materialInv.addItem(createItem(Material.STONE_PICKAXE, "Stone Pickaxe"));
        materialInv.addItem(createItem(Material.IRON_PICKAXE, "Iron Pickaxe"));
        materialInv.addItem(createItem(Material.DIAMOND_PICKAXE, "Diamond Pickaxe"));
        materialInv.addItem(createItem(Material.NETHERITE_PICKAXE, "Netherite Pickaxe"));
        player.openInventory(materialInv);
        materialInventories.add(materialInv);
    }
}
