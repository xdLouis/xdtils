package de.louis.xdtils.manager;

import java.util.*;

public class SpyManager {

    private final Set<UUID> commandSpies = new HashSet<>();
    private final Set<UUID> socialSpies  = new HashSet<>();

    public boolean toggleCommandSpy(UUID uuid) {
        if (commandSpies.contains(uuid)) { commandSpies.remove(uuid); return false; }
        commandSpies.add(uuid); return true;
    }

    public boolean toggleSocialSpy(UUID uuid) {
        if (socialSpies.contains(uuid)) { socialSpies.remove(uuid); return false; }
        socialSpies.add(uuid); return true;
    }

    public boolean hasCommandSpy(UUID uuid) { return commandSpies.contains(uuid); }
    public boolean hasSocialSpy(UUID uuid)  { return socialSpies.contains(uuid); }
}