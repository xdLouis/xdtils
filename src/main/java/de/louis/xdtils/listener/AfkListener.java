package de.louis.xdtils.listener;

import de.louis.xdtils.manager.AfkManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;

public class AfkListener implements Listener {

    private final AfkManager afkManager;

    public AfkListener(AfkManager afkManager) {
        this.afkManager = afkManager;
    }

    // Bewegung → AFK automatisch entfernen
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!afkManager.isAfk(player.getUniqueId())) return;

        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            afkManager.toggle(player); // entfernt AFK
        }
    }
}