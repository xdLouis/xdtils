package de.louis.xdtils.manager.permissions;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class PermissionMenuHolder implements InventoryHolder {

    private final PermissionMenuType type;
    private final String target;
    private final Inventory inventory;

    public PermissionMenuHolder(PermissionMenuType type, String target, int size, String title) {
        this.type = type;
        this.target = target;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public PermissionMenuType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}