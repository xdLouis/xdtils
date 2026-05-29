package de.louis.xdtils.listener;

import de.louis.xdtils.manager.permissions.PermissionSystemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PermissionPlayerListener implements Listener {

    private final PermissionSystemManager permissionSystemManager;

    public PermissionPlayerListener(PermissionSystemManager permissionSystemManager) {
        this.permissionSystemManager = permissionSystemManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        permissionSystemManager.handleJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        permissionSystemManager.handleQuit(event.getPlayer());
    }
}