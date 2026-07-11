package de.louis.xdtils.manager;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TrailManager {

    private final Map<UUID, Particle> trails = new HashMap<>();
    private final BukkitTask task;

    public TrailManager(JavaPlugin plugin) {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<UUID, Particle> entry : trails.entrySet()) {
                Player p = plugin.getServer().getPlayer(entry.getKey());
                if (p == null || !p.isOnline()) continue;
                p.getWorld().spawnParticle(entry.getValue(),
                        p.getLocation().add(0, 0.1, 0),
                        3, 0.2, 0.1, 0.2, 0);
            }
        }, 0L, 5L);
    }

    public void setTrail(UUID uuid, Particle particle) {
        trails.put(uuid, particle);
    }

    public void removeTrail(UUID uuid) {
        trails.remove(uuid);
    }

    public Particle getTrail(UUID uuid) {
        return trails.get(uuid);
    }

    public void shutdown() {
        task.cancel();
    }
}