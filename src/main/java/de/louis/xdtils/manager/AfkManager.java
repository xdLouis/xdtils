package de.louis.xdtils.manager;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AfkManager {

    private final Set<UUID> afkPlayers = new HashSet<>();

    public void toggle(Player player) {
        if (afkPlayers.contains(player.getUniqueId())) {
            afkPlayers.remove(player.getUniqueId());
            player.displayName(null);
            Bukkit.broadcast(MessageUtil.prefixed("<gray>"
                    + MessageUtil.player(player.getName())
                    + "<gray> ist nicht mehr AFK.</gray>"));
        } else {
            afkPlayers.add(player.getUniqueId());
            player.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    .deserialize("<gray>[AFK] <#4DA3FF>" + player.getName() + "</#4DA3FF>"));
            Bukkit.broadcast(MessageUtil.prefixed("<gray>"
                    + MessageUtil.player(player.getName())
                    + "<gray> ist jetzt AFK.</gray>"));
        }
    }

    public boolean isAfk(UUID uuid) { return afkPlayers.contains(uuid); }
}