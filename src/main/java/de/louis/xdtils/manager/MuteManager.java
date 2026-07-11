package de.louis.xdtils.manager;

import java.util.*;

public class MuteManager {

    public record MuteEntry(String mutedBy, String reason, long timestamp) {}

    private final Map<UUID, MuteEntry> mutes = new HashMap<>();

    public void mute(UUID uuid, String mutedBy, String reason) {
        mutes.put(uuid, new MuteEntry(mutedBy, reason, System.currentTimeMillis()));
    }

    public void unmute(UUID uuid) {
        mutes.remove(uuid);
    }

    public boolean isMuted(UUID uuid) {
        return mutes.containsKey(uuid);
    }

    public MuteEntry getEntry(UUID uuid) {
        return mutes.get(uuid);
    }
}