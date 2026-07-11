package de.louis.xdtils.listener;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Listener für die BanHistory-GUI:
 * - Verhindert Item-Klau
 * - Schließt bei BARRIER-Klick
 */
public final class BanMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Nur xdTils-History-GUIs abfangen
        Inventory inv = event.getInventory();
        if (inv.getHolder() != null) return; // hat keinen custom Holder → nicht unsere GUI

        // Titel prüfen
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("xdTils") || !title.contains("History:")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Schließen-Button
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }
    }
}
