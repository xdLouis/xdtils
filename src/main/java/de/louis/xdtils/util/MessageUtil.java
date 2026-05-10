package de.louis.xdtils.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;

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

    // ── Back ──────────────────────────────────────────────────────────

    public static Component backTeleported() {
        return prefixed("<gray>Du wurdest zu deiner letzten Position zurückgebracht.</gray>");
    }

    public static Component backNoLocation() {
        return prefixed("<gray>Es gibt keine gespeicherte Position für dich.</gray>");
    }

    // ── Teleport ──────────────────────────────────────────────────────

    public static Component tpUsage() {
        return prefixed("<gray>Benutzung: " + command("tp")
                + "<gray> <spieler> | @a <ziel> | <x> <y> <z> | <spieler> <x> <y> <z></gray>");
    }

    public static Component tpToPlayer(String targetName) {
        return prefixed("<gray>Du wurdest zu " + player(targetName) + "<gray> teleportiert.</gray>");
    }

    public static Component tpToPlayerByOther(String actorName, String targetName) {
        return prefixed("<gray>Du wurdest von " + player(actorName)
                + "<gray> zu " + player(targetName) + "<gray> teleportiert.</gray>");
    }

    public static Component tpPlayerToPlayer(String fromName, String toName) {
        return prefixed("<gray>" + player(fromName) + "<gray> wurde zu "
                + player(toName) + "<gray> teleportiert.</gray>");
    }

    public static Component tpAllToPlayer(String targetName, int count) {
        return prefixed("<gray><#4DA3FF>" + count + "</#4DA3FF><gray> Spieler wurden zu "
                + player(targetName) + "<gray> teleportiert.</gray>");
    }

    public static Component tpToCoords(Location loc) {
        return prefixed("<gray>Du wurdest zu "
                + value((int) loc.getX() + ", " + (int) loc.getY() + ", " + (int) loc.getZ())
                + "<gray> teleportiert.</gray>");
    }

    public static Component tpPlayerToCoords(String targetName, Location loc) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde zu "
                + value((int) loc.getX() + ", " + (int) loc.getY() + ", " + (int) loc.getZ())
                + "<gray> teleportiert.</gray>");
    }

    public static Component tpToCoordsBy(String actorName, Location loc) {
        return prefixed("<gray>Du wurdest von " + player(actorName) + "<gray> zu "
                + value((int) loc.getX() + ", " + (int) loc.getY() + ", " + (int) loc.getZ())
                + "<gray> teleportiert.</gray>");
    }

    public static Component tpInvalidCoords() {
        return prefixed("<gray>Ungültige Koordinaten. Nutze Zahlen oder <#86EFAC>~</#86EFAC><gray> für relative Werte.</gray>");
    }

    // ── Clear ─────────────────────────────────────────────────────────

    public static Component clearSelf() {
        return prefixed("<gray>Dein Inventar wurde geleert.</gray>");
    }

    public static Component clearOther(String targetName) {
        return prefixed("<gray>Das Inventar von " + player(targetName) + "<gray> wurde geleert.</gray>");
    }

    public static Component clearByOther(String actorName) {
        return prefixed("<gray>Dein Inventar wurde von " + player(actorName) + "<gray> geleert.</gray>");
    }

    public static Component clearAll(int count) {
        return prefixed("<gray>Das Inventar von <#4DA3FF>" + count + "</#4DA3FF><gray> Spielern wurde geleert.</gray>");
    }

    // ── Trash ─────────────────────────────────────────────────────────

    public static Component trashOpened() {
        return prefixed("<gray>Mülleimer geöffnet. Items hier werden beim Schließen gelöscht.</gray>");
    }

    public static Component trashCleared() {
        return prefixed("<gray>Mülleimer wurde geleert.</gray>");
    }

// ── Item (/i) ─────────────────────────────────────────────────────

    public static Component itemUsage() {
        return prefixed("<gray>Benutzung: " + command("i") + "<gray> <item> [menge]</gray>");
    }

    public static Component itemUnknown(String input) {
        return prefixed("<gray>Unbekanntes Item: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

    public static Component itemInvalidAmount() {
        return prefixed("<gray>Ungültige Menge. Nutze eine Zahl zwischen <#86EFAC>1</#86EFAC><gray> und <#86EFAC>64</#86EFAC><gray>.</gray>");
    }

    public static Component itemGiven(String itemName, int amount) {
        return prefixed("<gray>Du hast <#4DA3FF>" + amount + "x</#4DA3FF> "
                + item(itemName) + "<gray> erhalten.</gray>");
    }

// ── Give ──────────────────────────────────────────────────────────

    public static Component giveUsage() {
        return prefixed("<gray>Benutzung: " + command("give")
                + "<gray> <spieler|@a> <item> [menge]</gray>");
    }

    public static Component giveSelf(String itemName, int amount) {
        return prefixed("<gray>Du hast dir <#4DA3FF>" + amount + "x</#4DA3FF> "
                + item(itemName) + "<gray> gegeben.</gray>");
    }

    public static Component giveOther(String targetName, String itemName, int amount) {
        return prefixed("<gray>Du hast " + player(targetName) + "<gray> <#4DA3FF>" + amount
                + "x</#4DA3FF> " + item(itemName) + "<gray> gegeben.</gray>");
    }

    public static Component giveReceived(String actorName, String itemName, int amount) {
        return prefixed("<gray>Du hast <#4DA3FF>" + amount + "x</#4DA3FF> "
                + item(itemName) + "<gray> von " + player(actorName) + "<gray> erhalten.</gray>");
    }

    public static Component giveReceivedAll(String actorName, String itemName, int amount) {
        return prefixed("<gray>Du hast <#4DA3FF>" + amount + "x</#4DA3FF> "
                + item(itemName) + "<gray> von " + player(actorName) + "<gray> erhalten.</gray>");
    }

    public static Component giveAllPlayers(String itemName, int amount, int count) {
        return prefixed("<gray><#4DA3FF>" + count + "</#4DA3FF><gray> Spieler haben <#4DA3FF>"
                + amount + "x</#4DA3FF> " + item(itemName) + "<gray> erhalten.</gray>");
    }
    // ── Kick ─────────────────────────────────────────────────────────

    public static net.kyori.adventure.text.Component kickScreen(String reason, String actorName) {
        return parse("<red><bold>Du wurdest gekickt</bold></red>\n\n"
                + "<gray>Grund: <white>" + escape(reason) + "</white></gray>\n"
                + "<gray>Von: " + player(actorName) + "</gray>");
    }

    public static Component kickBroadcast(String targetName, String actorName, String reason) {
        return prefixed(player(targetName) + "<gray> wurde von " + player(actorName)
                + "<gray> gekickt. </gray><gray>(<#F87171>" + escape(reason) + "</#F87171><gray>)</gray>");
    }

// ── Ban ──────────────────────────────────────────────────────────

    public static net.kyori.adventure.text.Component banScreen(String reason, String actorName) {
        return parse("<red><bold>Du wurdest gebannt</bold></red>\n\n"
                + "<gray>Grund: <white>" + escape(reason) + "</white></gray>\n"
                + "<gray>Von: " + player(actorName) + "</gray>");
    }

    public static Component banBroadcast(String targetName, String actorName, String reason) {
        return prefixed(player(targetName) + "<gray> wurde von " + player(actorName)
                + "<gray> gebannt. </gray><gray>(<#F87171>" + escape(reason) + "</#F87171><gray>)</gray>");
    }

    public static Component banIpSuccess(String ip, String actorName, String reason) {
        return prefixed("<gray>IP <#F87171>" + escape(ip) + "</#F87171><gray> wurde von "
                + player(actorName) + "<gray> gebannt. Grund: <#F87171>" + escape(reason) + "</#F87171></gray>");
    }

// ── Pardon ───────────────────────────────────────────────────────

    public static Component pardonSuccess(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde entbannt.</gray>");
    }

    public static Component pardonNotBanned(String targetName) {
        return prefixed("<gray><#F87171>" + escape(targetName) + "</#F87171><gray> ist nicht gebannt.</gray>");
    }

// ── Op / Deop ────────────────────────────────────────────────────

    public static Component opSuccess(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde zum Operator gemacht.</gray>");
    }

    public static Component opNotify() {
        return prefixed("<gray>Du bist jetzt <#86EFAC>Operator</#86EFAC><gray>.</gray>");
    }

    public static Component deopSuccess(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde der Operator-Status entzogen.</gray>");
    }

    public static Component deopNotify() {
        return prefixed("<gray>Dein <#F87171>Operator-Status</#F87171><gray> wurde entzogen.</gray>");
    }

// ── Kill ─────────────────────────────────────────────────────────

    public static Component killSelf() {
        return prefixed("<gray>Du wurdest getötet.</gray>");
    }

    public static Component killOther(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde getötet.</gray>");
    }

    public static Component killByOther(String actorName) {
        return prefixed("<gray>Du wurdest von " + player(actorName) + "<gray> getötet.</gray>");
    }

    public static Component killAll(int count) {
        return prefixed("<gray><#4DA3FF>" + count + "</#4DA3FF><gray> Spieler wurden getötet.</gray>");
    }

// ── Time ─────────────────────────────────────────────────────────

    public static Component timeUsage() {
        return prefixed("<gray>Benutzung: " + command("time")
                + "<gray> <set|add|query> [tag|nacht|zahl]</gray>");
    }

    public static Component timeSet(String val) {
        return prefixed("<gray>Zeit wurde auf " + value(val) + "<gray> gesetzt.</gray>");
    }

    public static Component timeAdded(long ticks) {
        return prefixed("<gray>" + value(String.valueOf(ticks)) + "<gray> Ticks wurden hinzugefügt.</gray>");
    }

    public static Component timeQuery(long ticks) {
        return prefixed("<gray>Aktuelle Zeit: " + value(String.valueOf(ticks)) + "<gray> Ticks.</gray>");
    }

    public static Component timeInvalid(String input) {
        return prefixed("<gray>Ungültiger Wert: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

// ── Weather ──────────────────────────────────────────────────────

    public static Component weatherUsage() {
        return prefixed("<gray>Benutzung: " + command("weather")
                + "<gray> <clear|rain|thunder></gray>");
    }

    public static Component weatherSet(String typeName) {
        return prefixed("<gray>Wetter wurde auf " + value(typeName) + "<gray> gesetzt.</gray>");
    }

    public static Component weatherInvalid(String input) {
        return prefixed("<gray>Unbekanntes Wetter: <#F87171>" + escape(input)
                + "</#F87171><gray>. Nutze <#86EFAC>clear</#86EFAC><gray>, "
                + "<#86EFAC>rain</#86EFAC><gray> oder <#86EFAC>thunder</#86EFAC><gray>.</gray>");
    }

// ── Difficulty ───────────────────────────────────────────────────

    public static Component difficultyUsage() {
        return prefixed("<gray>Benutzung: " + command("difficulty")
                + "<gray> <peaceful|easy|normal|hard></gray>");
    }

    public static Component difficultySet(String diffName) {
        return prefixed("<gray>Schwierigkeit wurde auf " + value(diffName) + "<gray> gesetzt.</gray>");
    }

    public static Component difficultyInvalid(String input) {
        return prefixed("<gray>Unbekannte Schwierigkeit: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

// ── Whitelist ────────────────────────────────────────────────────

    public static Component whitelistUsage() {
        return prefixed("<gray>Benutzung: " + command("whitelist")
                + "<gray> <on|off|add|remove|list|reload></gray>");
    }

    public static Component whitelistEnabled() {
        return prefixed("<gray>Whitelist wurde <#86EFAC>aktiviert</#86EFAC><gray>.</gray>");
    }

    public static Component whitelistDisabled() {
        return prefixed("<gray>Whitelist wurde <#F87171>deaktiviert</#F87171><gray>.</gray>");
    }

    public static Component whitelistAdded(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde zur Whitelist hinzugefügt.</gray>");
    }

    public static Component whitelistRemoved(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde von der Whitelist entfernt.</gray>");
    }

    public static Component whitelistList(java.util.List<String> names, int count) {
        String joined = names.isEmpty() ? "<gray>Keine Einträge</gray>" : String.join("<gray>, </gray>", names.stream().map(n -> player(n)).toList());
        return prefixed("<gray>Whitelist (<#4DA3FF>" + count + "</#4DA3FF><gray>): </gray>" + joined);
    }

    public static Component whitelistReloaded() {
        return prefixed("<gray>Whitelist wurde neu geladen.</gray>");
    }

// ── Heal ─────────────────────────────────────────────────────────

    public static Component healSelf() {
        return prefixed("<gray>Du wurdest geheilt.</gray>");
    }

    public static Component healOther(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde geheilt.</gray>");
    }

    public static Component healByOther(String actorName) {
        return prefixed("<gray>Du wurdest von " + player(actorName) + "<gray> geheilt.</gray>");
    }

    public static Component healAll(int count) {
        return prefixed("<gray><#4DA3FF>" + count + "</#4DA3FF><gray> Spieler wurden geheilt.</gray>");
    }

// ── Feed ─────────────────────────────────────────────────────────

    public static Component feedSelf() {
        return prefixed("<gray>Du wurdest gesättigt.</gray>");
    }

    public static Component feedOther(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde gesättigt.</gray>");
    }

    public static Component feedByOther(String actorName) {
        return prefixed("<gray>Du wurdest von " + player(actorName) + "<gray> gesättigt.</gray>");
    }

    public static Component feedAll(int count) {
        return prefixed("<gray><#4DA3FF>" + count + "</#4DA3FF><gray> Spieler wurden gesättigt.</gray>");
    }

// ── Fly ──────────────────────────────────────────────────────────

    public static Component flySelf(boolean enabled) {
        return prefixed("<gray>Flugmodus wurde " + (enabled
                ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component flyOther(String targetName, boolean enabled) {
        return prefixed("<gray>Flugmodus von " + player(targetName) + "<gray> wurde "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component flyByOther(String actorName, boolean enabled) {
        return prefixed("<gray>Dein Flugmodus wurde von " + player(actorName) + "<gray> "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }
    // ── TpHere ───────────────────────────────────────────────────────

    public static Component tpHereSuccess(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde zu dir teleportiert.</gray>");
    }

    public static Component tpHereNotify(String actorName) {
        return prefixed("<gray>Du wurdest von " + player(actorName) + "<gray> zu sich gerufen.</gray>");
    }

    // ── ClearChat ─────────────────────────────────────────────────────

    public static Component chatCleared(String actorName) {
        return prefixed("<gray>Der Chat wurde von " + player(actorName) + "<gray> geleert.</gray>");
    }
}

