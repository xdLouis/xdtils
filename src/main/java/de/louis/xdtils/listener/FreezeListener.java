package de.louis.xdtils.listener;

import de.louis.xdtils.manager.FreezeManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeListener implements Listener {

    private final FreezeManager freezeManager;

    public FreezeListener(FreezeManager freezeManager) {
        this.freezeManager = freezeManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!freezeManager.isFrozen(player.getUniqueId())) return;

        // Nur Position sperren, Kopfbewegung erlauben
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            event.setCancelled(true);
            player.sendMessage(MessageUtil.prefixed("<gray>Du bist eingefroren.</gray>"));
        }
    }
}