package de.louis.xdtils.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InvSnapshotManager {

    // UUID → (slotName → snapshot)
    private final Map<UUID, Map<String, ItemStack[]>> snapshots = new HashMap<>();

    public void save(Player player, String slot) {
        snapshots
                .computeIfAbsent(player.getUniqueId(), k -> new LinkedHashMap<>())
                .put(slot, player.getInventory().getContents().clone());
    }

    public void load(Player player, String slot) {
        ItemStack[] contents = snapshots
                .getOrDefault(player.getUniqueId(), Map.of())
                .get(slot);
        if (contents == null) return;
        player.getInventory().setContents(contents.clone());
    }

    public boolean hasSlot(UUID uuid, String slot) {
        return snapshots.getOrDefault(uuid, Map.of()).containsKey(slot);
    }

    public List<String> getSlots(UUID uuid) {
        return new ArrayList<>(snapshots.getOrDefault(uuid, Map.of()).keySet());
    }
}