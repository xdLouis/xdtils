package de.louis.xdtils.listener;

import de.louis.xdtils.manager.SpyManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpyListener implements Listener {

    private final SpyManager spyManager;

    public CommandSpyListener(SpyManager spyManager) {
        this.spyManager = spyManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player sender = event.getPlayer();
        String cmd = event.getMessage();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!spyManager.hasCommandSpy(p.getUniqueId())) continue;
            if (p.getUniqueId().equals(sender.getUniqueId())) continue;
            p.sendMessage(MessageUtil.prefixed(
                    "<dark_gray>[SPY]</dark_gray> <gray>"
                            + MessageUtil.player(sender.getName())
                            + "<gray>: <#67E8F9>" + cmd + "</#67E8F9></gray>"));
        }
    }
}