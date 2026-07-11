package de.louis.xdtils.listener;

import de.louis.xdtils.manager.GlowManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GlowListener implements Listener {

    private final GlowManager glowManager;

    public GlowListener(GlowManager glowManager) {
        this.glowManager = glowManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        glowManager.handleJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        glowManager.handleQuit(event.getPlayer());
    }
}