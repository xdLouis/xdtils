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

    public static String workstation(String name) {
        return "<gradient:#67E8F9:#3B82F6>" + escape(name) + "</gradient>";
    }

    public static String value(String val) {
        return "<#67E8F9>" + escape(val) + "</#67E8F9>";
    }

    public static String enchant(String name) {
        return "<gradient:#C084FC:#818CF8>" + escape(name) + "</gradient>";
    }

    public static String item(String name) {
        return "<gradient:#FCD34D:#F59E0B>" + escape(name) + "</gradient>";
    }

    // ── Permissions / Errors ──────────────────────────────────────────

    public static Component noPermission(String cmd) {
        return prefixed("<gray>Du hast keine Rechte für " + command(cmd) + "<gray>.</gray>");
    }

    public static Component onlyPlayers() {
        return prefixed("<gray>Dieser Befehl kann nur von einem Spieler verwendet werden.</gray>");
    }

    public static Component playerNotFound(String input) {
        return prefixed("<gray>Der Spieler " + player(input) + "<gray> wurde nicht gefunden.</gray>");
    }

    // ── Gamemode ──────────────────────────────────────────────────────

    public static Component invalidGamemode(String input) {
        return prefixed("<gray>Unbekannter Spielmodus: <#F87171>" + escape(input) + "</#F87171><gray>.</gray> "
                + "<gray>Nutze <#86EFAC>survival</#86EFAC><gray>, <#86EFAC>creative</#86EFAC><gray>, "
                + "<#86EFAC>adventure</#86EFAC><gray> oder <#86EFAC>spectator</#86EFAC><gray>.</gray>");
    }

    public static Component gamemodeUsage() {
        return prefixed("<gray>Benutzung: " + command("gamemode") + "<gray> <#86EFAC><mode></#86EFAC> [spieler|@a]</gray>");
    }

    public static Component gamemodeSelf(String modeName) {
        return prefixed("<gray>Dein Spielmodus wurde zu " + mode(modeName) + "<gray> geändert.</gray>");
    }

    public static Component gamemodeOther(String targetName, String modeName) {
        return prefixed("<gray>Der Spielmodus von " + player(targetName) + "<gray> wurde zu " + mode(modeName) + "<gray> geändert.</gray>");
    }

    public static Component gamemodeAll(String modeName, int amount) {
        return prefixed("<gray>Der Spielmodus von <#4DA3FF>" + amount + "</#4DA3FF><gray> Spielern wurde zu " + mode(modeName) + "<gray> geändert.</gray>");
    }

    public static Component gamemodeChangedBy(String actorName, String modeName) {
        return prefixed("<gray>Dein Spielmodus wurde von " + player(actorName) + "<gray> zu " + mode(modeName) + "<gray> geändert.</gray>");
    }

    // ── Workstation ───────────────────────────────────────────────────

    public static Component workstationOpened(String workstationName) {
        return prefixed("<gray>Du hast " + workstation(workstationName) + "<gray> geöffnet.</gray>");
    }

    public static Component unknownWorkstation(String input) {
        return prefixed("<gray>Unbekannte Workstation: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

    // ── Speed ─────────────────────────────────────────────────────────

    public static Component speedChanged(String type, int displayValue) {
        return prefixed("<gray>" + type + " wurde auf " + value(String.valueOf(displayValue)) + "<gray> gesetzt.</gray>");
    }

    public static Component speedChangedOther(String targetName, String type, int displayValue) {
        return prefixed("<gray>" + type + " von " + player(targetName) + "<gray> wurde auf " + value(String.valueOf(displayValue)) + "<gray> gesetzt.</gray>");
    }

    public static Component speedChangedByOther(String actorName, String type, int displayValue) {
        return prefixed("<gray>Dein " + type + " wurde von " + player(actorName) + "<gray> auf " + value(String.valueOf(displayValue)) + "<gray> gesetzt.</gray>");
    }

    public static Component speedInvalid() {
        return prefixed("<gray>Ungültiger Wert. Nutze eine Zahl zwischen <#86EFAC>0</#86EFAC><gray> und <#86EFAC>10</#86EFAC><gray>.</gray>");
    }

    public static Component speedUsage(String cmd) {
        return prefixed("<gray>Benutzung: " + command(cmd) + "<gray> <#86EFAC><0-10></#86EFAC> [spieler]</gray>");
    }

    // ── Enchant ───────────────────────────────────────────────────────

    public static Component enchantApplied(String enchantName, int level) {
        return prefixed("<gray>Verzauberung " + enchant(enchantName) + "<gray> Level "
                + value(String.valueOf(level)) + "<gray> wurde angewendet.</gray>");
    }

    public static Component enchantAppliedOverlevel(String enchantName, int level) {
        return prefixed("<gray>Verzauberung " + enchant(enchantName) + "<gray> Level "
                + value(String.valueOf(level)) + "<gray> wurde als "
                + "<#F59E0B>Over-Level</color><gray> angewendet.</gray>");
    }

    public static Component enchantUnknown(String input) {
        return prefixed("<gray>Unbekannte Verzauberung: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

    public static Component enchantInvalidLevel() {
        return prefixed("<gray>Das Level muss <#86EFAC>1</#86EFAC><gray> oder höher sein.</gray>");
    }

    public static Component enchantLevelTooHigh(String enchantName, int max) {
        return prefixed("<gray>Das maximale Level für " + enchant(enchantName) + "<gray> ist "
                + value(String.valueOf(max)) + "<gray>. Nur OPs können Over-Level nutzen.</gray>");
    }

    public static Component enchantNoItem() {
        return prefixed("<gray>Du hältst kein verzauberbares Item in der Hand.</gray>");
    }

    public static Component enchantUsage() {
        return prefixed("<gray>Benutzung: " + command("enchant") + "<gray> <#86EFAC><verzauberung> <level></#86EFAC><gray>.</gray>");
    }

    // ── Hat ───────────────────────────────────────────────────────────

    public static Component hatEquipped(String itemName) {
        return prefixed("<gray>" + item(itemName) + "<gray> wurde als Hut aufgesetzt.</gray>");
    }

    public static Component hatNoItem() {
        return prefixed("<gray>Du hältst kein Item in der Hand.</gray>");
    }

    // ─────────────────────────────────────────────────────────────────

    private static String escape(String text) {
        return text.replace("<", "\\<").replace(">", "\\>");
    }

    // ── Invsee ────────────────────────────────────────────────────────

    public static Component invseeOpened(String targetName) {
        return prefixed("<gray>Du siehst das Inventar von " + player(targetName) + "<gray>.</gray>");
    }
}

