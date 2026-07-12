package de.louis.xdtils.listener;

import de.louis.xdtils.manager.BanManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Prüft beim Login, ob ein Spieler noch temporär gebannt ist.
 * Läuft mit HIGH-Priority damit andere Plugins (z.B. Whitelist) zuerst greifen.
 */
public final class TempBanListener implements Listener {

    private final BanManager banManager;

    public TempBanListener(BanManager banManager) {
        this.banManager = banManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        String name = event.getPlayer().getName();

        if (!banManager.isTempBanned(name)) return;

        if (banManager.isTempBanExpired(name)) {
            banManager.removeTempBan(name);
            return;
        }

        String reason = banManager.getTempBanReason(name);
        String expiry = banManager.getTempBanExpiryFormatted(name);

        event.disallow(
                PlayerLoginEvent.Result.KICK_BANNED,
                net.kyori.adventure.text.Component.text(
                        "\u00a7cDu bist temporär gebannt!\n"
                        + "\u00a77Grund: \u00a7f" + reason + "\n"
                        + "\u00a77Gebannt bis: \u00a7f" + expiry
                )
        );
    }
}
