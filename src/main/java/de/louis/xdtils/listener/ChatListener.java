package de.louis.xdtils.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player sender = event.getPlayer();
        String message = event.getMessage();

        Component nameComponent = MM.deserialize("<#4DA3FF><b>" + escape(sender.getName()) + "</b></#4DA3FF>");

        Component fullMessage = Component.empty()
                .append(nameComponent)
                .append(MM.deserialize("<dark_gray> » </dark_gray>"))
                .append(MM.deserialize("<gray>" + escape(message) + "</gray>"));

        for (Player receiver : Bukkit.getOnlinePlayers()) {
            if (receiver.isOp()) {
                Component opMessage = Component.empty()
                        .append(buildKickButton(sender))
                        .append(Component.space())
                        .append(buildTpButton(sender))
                        .append(Component.space())
                        .append(fullMessage);
                receiver.sendMessage(opMessage);
            } else {
                receiver.sendMessage(fullMessage);
            }
        }

        Bukkit.getConsoleSender().sendMessage(
                Component.empty()
                        .append(nameComponent)
                        .append(MM.deserialize("<dark_gray> » </dark_gray>"))
                        .append(MM.deserialize("<gray>" + escape(message) + "</gray>"))
        );
    }

    private Component buildKickButton(Player target) {
        return MM.deserialize("<dark_gray>[</dark_gray><red><b>X</b></red><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/kick " + target.getName() + " Gekickt von einem Operator"))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray>Klicken → <#F87171>" + escape(target.getName()) + "</#F87171><gray> kicken</gray>")
                ));
    }

    private Component buildTpButton(Player target) {
        return MM.deserialize("<dark_gray>[</dark_gray><green><b>TP</b></green><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/tp " + target.getName()))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray>Klicken → Zu <#4DA3FF>" + escape(target.getName()) + "</#4DA3FF><gray> teleportieren</gray>")
                ));
    }



    private String escape(String text) {
        return text.replace("<", "\\<").replace(">", "\\>");
    }
}