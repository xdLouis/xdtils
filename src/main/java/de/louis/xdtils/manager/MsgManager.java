package de.louis.xdtils.manager;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class MsgManager {

    // UUID des Spielers → Name des letzten Gesprächspartners
    private final Map<UUID, String> lastReply = new HashMap<>();
    // UUID des Ignorierenden → Set von ignorierten UUIDs
    private final Map<UUID, Set<UUID>> ignoreMap = new HashMap<>();

    private final SpyManager spyManager;

    public MsgManager(SpyManager spyManager) {
        this.spyManager = spyManager;
    }

    public void setLastReply(UUID uuid, String name) {
        lastReply.put(uuid, name);
    }

    public String getLastReply(UUID uuid) {
        return lastReply.get(uuid);
    }

    public boolean toggleIgnore(UUID ignorer, UUID target) {
        ignoreMap.computeIfAbsent(ignorer, k -> new HashSet<>());
        Set<UUID> ignored = ignoreMap.get(ignorer);
        if (ignored.contains(target)) {
            ignored.remove(target);
            return false;
        } else {
            ignored.add(target);
            return true;
        }
    }

    public boolean isIgnored(UUID ignorer, UUID target) {
        return ignoreMap.getOrDefault(ignorer, Set.of()).contains(target);
    }

    public void broadcastSocialSpy(String from, String to, String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (spyManager.hasSocialSpy(p.getUniqueId())) {
                p.sendMessage(MessageUtil.prefixed(
                        "<dark_gray>[SPY]</dark_gray> <gray>"
                                + MessageUtil.player(from) + "<gray> → "
                                + MessageUtil.player(to) + "<gray>: " + message + "</gray>"));
            }
        }
    }
}