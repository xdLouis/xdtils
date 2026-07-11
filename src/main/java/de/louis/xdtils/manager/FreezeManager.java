package de.louis.xdtils.manager;

import java.util.*;

public class FreezeManager {

    private final Set<UUID> frozenPlayers = new HashSet<>();

    public boolean toggle(UUID uuid) {
        if (frozenPlayers.contains(uuid)) { frozenPlayers.remove(uuid); return false; }
        frozenPlayers.add(uuid); return true;
    }

    public boolean isFrozen(UUID uuid) { return frozenPlayers.contains(uuid); }
}