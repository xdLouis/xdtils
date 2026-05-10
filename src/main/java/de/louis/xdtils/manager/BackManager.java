package de.louis.xdtils.manager;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackManager {

    private static final Map<UUID, Location> lastLocations = new HashMap<>();

    private BackManager() {
    }

    public static void setLastLocation(UUID uuid, Location location) {
        if (location == null || location.getWorld() == null) return;
        lastLocations.put(uuid, location.clone());
    }

    public static Location getLastLocation(UUID uuid) {
        return lastLocations.get(uuid);
    }

    public static boolean hasLastLocation(UUID uuid) {
        return lastLocations.containsKey(uuid);
    }

    public static void clearLastLocation(UUID uuid) {
        lastLocations.remove(uuid);
    }
}