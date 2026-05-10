package de.louis.xdtils.listener;

import de.louis.xdtils.manager.BackManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackListener implements Listener {

    // Speichere Position VOR dem Tod
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location deathLoc = event.getPlayer().getLocation();
        BackManager.setLastLocation(event.getPlayer().getUniqueId(), deathLoc);
    }

    // Teleportation: vorherige Position speichern
    // Cause-Filter: ignoriere PLUGIN-interne /back-Teleports (eigene Cause)
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Ignoriere Ursachen die keine echte "letzte Position" darstellen
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.UNKNOWN) return;

        Location from = event.getFrom();
        if (from == null || from.getWorld() == null) return;

        // Nur speichern wenn sich Position wirklich ändert
        Location to = event.getTo();
        if (to != null && from.getWorld().equals(to.getWorld())
                && from.distanceSquared(to) < 1) return;

        BackManager.setLastLocation(event.getPlayer().getUniqueId(), from);
    }

    // Respawn: nach dem Respawn die Respawn-Position NICHT als /back überschreiben
    // (damit /back nach dem Tod zur Todesposition geht, nicht zum Spawn)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Nichts tun — Todesposition wurde bereits bei onPlayerDeath gespeichert
        // Respawn selbst ist auch ein Teleport, aber wir wollen zur Todesposition zurück
        // Daher: nach Respawn den bereits gespeicherten Wert NICHT überschreiben
    }
}