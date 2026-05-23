package de.louis.xdtils.listener;

import de.louis.xdtils.commands.GodCommand;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;

public class GodListener implements Listener {

    private final GodCommand godCommand;

    public GodListener(GodCommand godCommand) {
        this.godCommand = godCommand;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (godCommand.isGod(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}