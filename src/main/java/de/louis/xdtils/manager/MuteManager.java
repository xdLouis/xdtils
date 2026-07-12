package de.louis.xdtils.manager;

import de.louis.xdtils.main.Main;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * MuteManager — verwaltet permanente Mutes und TempMutes.
 * TempMutes laufen automatisch ab via Scheduler.
 */
public class MuteManager {

    public record MuteEntry(String mutedBy, String reason, long timestamp, long expiresAt) {
        /** expiresAt == -1 → permanent */
        public boolean isTemp() { return expiresAt != -1; }
        public boolean isExpired() { return isTemp() && System.currentTimeMillis() >= expiresAt; }
        public long remainingMs() { return isTemp() ? Math.max(0, expiresAt - System.currentTimeMillis()) : -1; }
    }

    private final Map<UUID, MuteEntry> mutes = new HashMap<>();
    private BukkitTask cleanupTask;

    public MuteManager() {}

    /** Startet den Cleanup-Task für abgelaufene TempMutes */
    public void startCleanupTask(Main plugin) {
        cleanupTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            mutes.entrySet().removeIf(e -> e.getValue().isExpired());
        }, 20L * 10, 20L * 10); // alle 10 Sekunden
    }

    public void stopCleanupTask() {
        if (cleanupTask != null) cleanupTask.cancel();
    }

    // ── Permanenter Mute ──────────────────────────────────────────────

    public void mute(UUID uuid, String mutedBy, String reason) {
        mutes.put(uuid, new MuteEntry(mutedBy, reason, System.currentTimeMillis(), -1));
    }

    // ── Temporärer Mute ───────────────────────────────────────────────

    public void tempMute(UUID uuid, String mutedBy, String reason, long durationMs) {
        long expires = System.currentTimeMillis() + durationMs;
        mutes.put(uuid, new MuteEntry(mutedBy, reason, System.currentTimeMillis(), expires));
    }

    public void unmute(UUID uuid) {
        mutes.remove(uuid);
    }

    public boolean isMuted(UUID uuid) {
        MuteEntry entry = mutes.get(uuid);
        if (entry == null) return false;
        if (entry.isExpired()) {
            mutes.remove(uuid);
            return false;
        }
        return true;
    }

    public MuteEntry getEntry(UUID uuid) {
        MuteEntry entry = mutes.get(uuid);
        if (entry != null && entry.isExpired()) {
            mutes.remove(uuid);
            return null;
        }
        return entry;
    }
}
