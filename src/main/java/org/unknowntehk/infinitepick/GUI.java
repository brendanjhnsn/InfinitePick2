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
                    handleItemInteraction(player, clickedItem, inv);
                }
            }
        }
    }

    private void handleItemInteraction(Player player, ItemStack clickedItem, Inventory inv) {
        String itemName = clickedItem.getItemMeta().getDisplayName();
        switch (itemName) {
            case "Base Material":
            case "Choose Base Material":
                openMaterialMenu(player);
                break;
            case "Set Custom Model Data":
                awaitingInput.put(player.getUniqueId(), "modelData");
                player.closeInventory();  // Close inventory before asking for input
                player.sendMessage("Please enter the custom model data in chat.");
                break;
            case "Set Lore":
                awaitingInput.put(player.getUniqueId(), "lore");
                player.closeInventory();  // Close inventory before asking for input
                player.sendMessage("Please enter the lore in chat, use | to separate lines.");
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
        materialInv.addItem(createItem(Material.IRON_ORE, "Iron Ore"));
        materialInv.addItem(createItem(Material.DIAMOND, "Diamonds"));
        materialInv.addItem(createItem(Material.REDSTONE_ORE, "Redstone Ore"));
        materialInv.addItem(createItem(Material.NETHERRACK, "Netherrack"));
        materialInv.addItem(createItem(Material.STONE, "Stone"));
        materialInv.addItem(createItem(Material.GRANITE, "Granite"));
        player.openInventory(materialInv);
        materialInventories.add(materialInv);
    }
}
