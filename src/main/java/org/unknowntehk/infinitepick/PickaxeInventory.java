package org.unknowntehk.infinitepick;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class PickaxeInventory implements InventoryHolder {
    private final Inventory inventory;

    public PickaxeInventory(String pickaxeName) {
        this.inventory = Bukkit.createInventory(this, 54, pickaxeName + " Storage");
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void addItem(ItemStack item) {
        inventory.addItem(item);
    }
}
