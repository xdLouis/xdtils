package de.louis.xdtils.listener;

import de.louis.xdtils.manager.MaintenanceManager;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerLoginEvent;

public class MaintenanceListener implements Listener {

    private final MaintenanceManager maintenanceManager;

    public MaintenanceListener(MaintenanceManager maintenanceManager) {
        this.maintenanceManager = maintenanceManager;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (!maintenanceManager.isEnabled()) return;
        if (event.getPlayer().isOp()) return;

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                        "<red><b>Wartungsmodus</b></red>\n\n<gray>Der Server ist derzeit in Wartung.\nBitte später erneut versuchen.</gray>"
                ));
    }
}