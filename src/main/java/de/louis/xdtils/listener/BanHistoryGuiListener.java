package de.louis.xdtils.listener;

import de.louis.xdtils.commands.BanHistoryCommand;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public final class BanHistoryGuiListener implements Listener {

    private boolean isBanHistoryGui(org.bukkit.inventory.Inventory inv) {
        if (inv == null || inv.getViewers().isEmpty()) return false;
        // Titel-Check via PlainText
        // Der Inventory-Titel ist im View gespeichert
        return false; // wird unten über den View geprüft
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText()
            .serialize(event.getView().title());

        if (!title.startsWith(BanHistoryCommand.GUI_TITLE_PREFIX)) return;

        // Immer canceln — kein Item rausnehmen möglich
        event.setCancelled(true);

        // Schließen-Button (Barrier)
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null && clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        String title = PlainTextComponentSerializer.plainText()
            .serialize(event.getView().title());
        if (title.startsWith(BanHistoryCommand.GUI_TITLE_PREFIX)) {
            event.setCancelled(true);
        }
    }
}
