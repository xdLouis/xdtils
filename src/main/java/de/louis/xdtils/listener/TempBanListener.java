package de.louis.xdtils.listener;

import de.louis.xdtils.manager.BanManager;
import de.louis.xdtils.manager.BanManager.BanEntry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Prüft beim Login ob ein Spieler aktiv (temp-)gebannt ist.
 */
public final class TempBanListener implements Listener {

    private final BanManager banManager;

    public TempBanListener(BanManager banManager) {
        this.banManager = banManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        String name = event.getPlayer().getName();

        if (!banManager.isBanned(name)) return;

        BanEntry ban = banManager.getBan(name);
        if (ban == null) return;

        String reason = ban.reason();

        String durationInfo = ban.isTemp()
                ? "\u00a77Gebannt bis: \u00a7f" + BanManager.formatTimestamp(ban.expiresAt())
                  + " (noch " + BanManager.formatDuration(ban.remainingMs()) + ")"
                : "\u00a77Dauer: \u00a7fPermanent";

        event.disallow(
                PlayerLoginEvent.Result.KICK_BANNED,
                Component.text(
                        "\u00a7cDu bist gebannt!\n"
                        + "\u00a77Grund: \u00a7f" + reason + "\n"
                        + durationInfo
                )
        );
    }
}
