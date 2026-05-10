package de.louis.xdtils.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class MessageUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private MessageUtil() {
    }

    public static Component parse(String text) {
        return MM.deserialize(text);
    }

    public static Component prefix() {
        return parse("<gray>[</gray><gradient:#67E8F9:#3B82F6><bold>xdTils</bold></gradient><gray>]</gray> ");
    }

    public static Component prefixed(String text) {
        return prefix().append(parse(text));
    }

    public static String player(String name) {
        return "<#4DA3FF>" + escape(name) + "</#4DA3FF>";
    }

    public static String command(String name) {
        return "<#86EFAC>/" + escape(name) + "</#86EFAC>";
    }

    public static String mode(String name) {
        return "<gradient:#86EFAC:#22C55E>" + escape(name) + "</gradient>";
    }

    public static Component noPermission(String cmd) {
        return prefixed("<gray>Du hast keine Rechte für " + command(cmd) + "<gray>.</gray>");
    }

    public static Component onlyPlayers() {
        return prefixed("<gray>Dieser Befehl kann nur von einem Spieler verwendet werden.</gray>");
    }

    public static Component playerNotFound(String input) {
        return prefixed("<gray>Der Spieler " + player(input) + "<gray> wurde nicht gefunden.</gray>");
    }

    public static Component invalidGamemode(String input) {
        return prefixed("<gray>Unbekannter Spielmodus: <#F87171>" + escape(input) + "</#F87171><gray>.</gray> "
                + "<gray>Nutze <#86EFAC>survival</#86EFAC><gray>, <#86EFAC>creative</#86EFAC><gray>, "
                + "<#86EFAC>adventure</#86EFAC><gray> oder <#86EFAC>spectator</#86EFAC><gray>.</gray>");
    }

    public static Component gamemodeUsage() {
        return prefixed("<gray>Benutzung: " + command("gamemode") + "<gray> <#86EFAC><mode></#86EFAC> [spieler]</gray>");
    }

    public static Component gamemodeSelf(String modeName) {
        return prefixed("<gray>Dein Spielmodus wurde zu " + mode(modeName) + "<gray> geändert.</gray>");
    }

    public static Component gamemodeOther(String targetName, String modeName) {
        return prefixed("<gray>Der Spielmodus von " + player(targetName) + "<gray> wurde zu " + mode(modeName) + "<gray> geändert.</gray>");
    }

    public static Component gamemodeChangedBy(String actorName, String modeName) {
        return prefixed("<gray>Dein Spielmodus wurde von " + player(actorName) + "<gray> zu " + mode(modeName) + "<gray> geändert.</gray>");
    }

    private static String escape(String text) {
        return text.replace("<", "\\<").replace(">", "\\>");
    }
}