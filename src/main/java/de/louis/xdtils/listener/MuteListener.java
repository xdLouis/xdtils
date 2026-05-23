package de.louis.xdtils.listener;

import de.louis.xdtils.manager.MuteManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteListener implements Listener {

    private final MuteManager muteManager;

    public MuteListener(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onChat(AsyncPlayerChatEvent event) {
        if (!muteManager.isMuted(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
        var entry = muteManager.getEntry(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage(MessageUtil.prefixed(
                "<gray>Du bist <#F87171>stummgeschaltet</#F87171><gray>.</gray>\n"
                        + "<gray>Grund: <#F87171>" + entry.reason() + "</#F87171></gray>"));
    }
}