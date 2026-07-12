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

    public static Component kickUsage() {
        return prefixed("<gray>Benutzung: " + command("kick") + "<gray> <spieler> [grund]</gray>");
    }

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

    public static Component banUsage() {
        return prefixed("<gray>Benutzung: " + command("ban") + "<gray> <spieler> [grund]</gray>");
    }

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

// ── TempBan ──────────────────────────────────────────────────────

    public static Component tempBanUsage() {
        return prefixed("<gray>Benutzung: " + command("tempban")
                + "<gray> <spieler> <zeit> [grund]</gray>\n"
                + "<gray>Zeitformate: <#86EFAC>10s</#86EFAC><gray>, <#86EFAC>5m</#86EFAC><gray>, "
                + "<#86EFAC>2h</#86EFAC><gray>, <#86EFAC>3d</#86EFAC><gray>, <#86EFAC>1w</#86EFAC>");
    }

    public static Component tempBanInvalidDuration(String input) {
        return prefixed("<gray>Ungültige Zeitangabe: <#F87171>" + escape(input)
                + "</#F87171><gray>. Beispiel: <#86EFAC>1h</#86EFAC><gray>, "
                + "<#86EFAC>30m</#86EFAC><gray>, <#86EFAC>7d</#86EFAC>");
    }

    public static net.kyori.adventure.text.Component tempBanScreen(String reason, String actorName, String duration) {
        return parse("<red><bold>Du wurdest temporär gebannt</bold></red>\n\n"
                + "<gray>Grund: <white>" + escape(reason) + "</white></gray>\n"
                + "<gray>Von: " + player(actorName) + "</gray>\n"
                + "<gray>Dauer: <#F87171>" + escape(duration) + "</#F87171></gray>");
    }

    public static Component tempBanBroadcast(String targetName, String actorName, String reason, String duration) {
        return prefixed(player(targetName) + "<gray> wurde von " + player(actorName)
                + "<gray> für <#F87171>" + escape(duration) + "</#F87171><gray> gebannt."
                + " (<#F87171>" + escape(reason) + "</#F87171><gray>)</gray>");
    }

// ── Unban ─────────────────────────────────────────────────────────

    public static Component unbanSuccess(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde entbannt.</gray>");
    }

    public static Component notBanned(String targetName) {
        return prefixed("<gray><#F87171>" + escape(targetName) + "</#F87171><gray> ist nicht gebannt.</gray>");
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

    // ── Vanish ────────────────────────────────────────────────────────

    public static Component vanishToggled(boolean vanished) {
        return prefixed("<gray>Vanish wurde "
                + (vanished ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component vanishToggledOther(String targetName, boolean vanished) {
        return prefixed("<gray>Vanish von " + player(targetName) + "<gray> wurde "
                + (vanished ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component vanishRestored() {
        return prefixed("<gray>Dein Vanish wurde <#86EFAC>wiederhergestellt</#86EFAC><gray>.</gray>");
    }

    // ── Msg ──────────────────────────────────────────────────────────

    public static Component msgSent(String toName, String message) {
        return parse("<dark_gray>[</dark_gray><gray>Du → " + player(toName)
                + "<dark_gray>]</dark_gray> <white>" + escape(message) + "</white>");
    }

    public static Component msgReceived(String fromName, String message) {
        return parse("<dark_gray>[</dark_gray><gray>" + player(fromName)
                + "<gray> → Du<dark_gray>]</dark_gray> <white>" + escape(message) + "</white>");
    }

    public static Component permissionsDisabled() {
        return prefixed("<gray>Das Permission-System ist deaktiviert.</gray>");
    }

    public static Component permissionsEnabled() {
        return prefixed("<gray>Das Permission-System ist <#86EFAC>aktiviert</#86EFAC><gray>.</gray>");
    }

    public static Component permissionGroupCreated(String group) {
        return prefixed("<gray>Gruppe " + player(group) + "<gray> wurde erstellt.</gray>");
    }

    public static Component permissionGroupDeleted(String group) {
        return prefixed("<gray>Gruppe " + player(group) + "<gray> wurde gelöscht.</gray>");
    }

    public static Component permissionAdded(String target, String permission) {
        return prefixed("<gray>Permission " + command(permission) + "<gray> wurde für "
                + player(target) + "<gray> hinzugefügt.</gray>");
    }

    public static Component permissionRemoved(String target, String permission) {
        return prefixed("<gray>Permission " + command(permission) + "<gray> wurde für "
                + player(target) + "<gray> entfernt.</gray>");
    }

    public static Component groupAdded(String target, String group) {
        return prefixed("<gray>Gruppe " + player(group) + "<gray> wurde "
                + "für " + player(target) + "<gray> hinzugefügt.</gray>");
    }

    public static Component groupRemoved(String target, String group) {
        return prefixed("<gray>Gruppe " + player(group) + "<gray> wurde "
                + "von " + player(target) + "<gray> entfernt.</gray>");
    }

    // ── Glow ──────────────────────────────────────────────────────────

    public static String color(String name) {
        return "<gradient:#67E8F9:#3B82F6>" + escape(name) + "</gradient>";
    }

    public static Component glowDisabled() {
        return prefixed("<gray>Das Glow-System ist deaktiviert.</gray>");
    }

    public static Component glowColorsDisabled() {
        return prefixed("<gray>Glow-Farben sind deaktiviert.</gray>");
    }

    public static Component glowInvalidColor(String input) {
        return prefixed("<gray>Unbekannte Glow-Farbe: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

    public static Component glowUsage() {
        return prefixed("<gray>Benutzung: " + command("glow") + "<gray> [spieler] [farbe|on|off]</gray>");
    }

    public static Component glowSelf(boolean enabled) {
        return prefixed("<gray>Glow wurde "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component glowOther(String targetName, boolean enabled) {
        return prefixed("<gray>Glow von " + player(targetName) + "<gray> wurde "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component glowByOther(String actorName, boolean enabled) {
        return prefixed("<gray>Dein Glow wurde von " + player(actorName) + "<gray> "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component glowColorSelf(String colorName) {
        return prefixed("<gray>Deine Glow-Farbe wurde auf " + color(colorName) + "<gray> gesetzt.</gray>");
    }

    public static Component glowColorOther(String targetName, String colorName) {
        return prefixed("<gray>Die Glow-Farbe von " + player(targetName) + "<gray> wurde auf "
                + color(colorName) + "<gray> gesetzt.</gray>");
    }

    public static Component glowColorByOther(String actorName, String colorName) {
        return prefixed("<gray>Deine Glow-Farbe wurde von " + player(actorName) + "<gray> auf "
                + color(colorName) + "<gray> gesetzt.</gray>");
    }

    // ── EnderChest ────────────────────────────────────────────────────

    public static Component enderchestUsage() {
        return prefixed("<gray>Benutzung: " + command("enderchest") + "<gray> [spieler]</gray>");
    }

    public static Component enderchestOpenedSelf() {
        return prefixed("<gray>Deine Enderchest wurde geöffnet.</gray>");
    }

    public static Component enderchestOpenedOther(String targetName) {
        return prefixed("<gray>Die Enderchest von " + player(targetName) + "<gray> wurde geöffnet.</gray>");
    }

    // ── Top / Bottom / Mob ───────────────────────────────────────────

    public static Component topTeleported() {
        return prefixed("<gray>Du wurdest nach <#86EFAC>oben</#86EFAC> teleportiert.</gray>");
    }

    public static Component topNotSafe() {
        return prefixed("<gray>Es konnte kein sicherer Block über dir gefunden werden.</gray>");
    }

    public static Component bottomTeleported() {
        return prefixed("<gray>Du wurdest nach <#86EFAC>unten</#86EFAC> teleportiert.</gray>");
    }

    public static Component bottomNotSafe() {
        return prefixed("<gray>Es konnte kein sicherer Block unter dir gefunden werden.</gray>");
    }

    public static Component mobUsage() {
        return prefixed("<gray>Benutzung: " + command("mob") + "<gray> <mob> [anzahl]</gray>");
    }

    public static Component mobUnknown(String input) {
        return prefixed("<gray>Unbekannter Mob: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

    public static Component mobInvalidAmount() {
        return prefixed("<gray>Ungültige Anzahl. Nutze eine Zahl zwischen <#86EFAC>1</#86EFAC><gray> und <#86EFAC>100</#86EFAC><gray>.</gray>");
    }

    public static Component mobSpawned(String mobName, int amount) {
        return prefixed("<gray><#4DA3FF>" + amount + "</#4DA3FF><gray>x "
                + value(mobName) + "<gray> wurde gespawnt.</gray>");
    }

    // ── ArmorTrim ─────────────────────────────────────────────────────

    public static Component armorTrimNoArmor() {
        return prefixed("<gray>Du hältst kein Rüstungsteil in der Hand.</gray>");
    }

    public static Component armorTrimGuiOpened() {
        return prefixed("<gray>Wähle ein <gradient:#C084FC:#818CF8>Muster</gradient><gray> und ein "
                + "<gradient:#FCD34D:#F59E0B>Material</gradient><gray> aus dem Menü.</gray>");
    }

    public static Component armorTrimApplied(String pattern, String material) {
        String patternFormatted = pattern.replace("_", " ");
        patternFormatted = Character.toUpperCase(patternFormatted.charAt(0)) + patternFormatted.substring(1);
        String materialFormatted = material.replace("_", " ");
        materialFormatted = Character.toUpperCase(materialFormatted.charAt(0)) + materialFormatted.substring(1);
        return prefixed("<gray>Rüstungsverzierung <gradient:#C084FC:#818CF8>" + escape(patternFormatted)
                + "</gradient> <gray>mit Material <gradient:#FCD34D:#F59E0B>" + escape(materialFormatted)
                + "</gradient> <gray>wurde angewendet.</gray>");
    }

    // ── LeatherColor ─────────────────────────────────────────────────

    public static Component leatherColorUsage() {
        return prefixed("<gray>Benutzung: " + command("leathercolor")
                + "<gray> <#86EFAC><#RRGGBB|farbe></#86EFAC></gray>"
                + "<gray>. Beispiel: <#67E8F9>/leathercolor #FF5500</#67E8F9></gray>");
    }

    public static Component leatherColorNoLeather() {
        return prefixed("<gray>Du hältst kein Lederrüstungsteil in der Hand.</gray>");
    }

    public static Component leatherColorInvalid(String input) {
        return prefixed("<gray>Ungültige Farbe: <#F87171>" + escape(input) + "</#F87171><gray>."
                + " Nutze einen Hex-Code wie <#67E8F9>#FF5500</#67E8F9><gray> oder einen Farbnamen.</gray>");
    }

    public static Component leatherColorSet(String hex) {
        return prefixed("<gray>Lederrüstungsfarbe wurde auf <" + escape(hex) + ">" + escape(hex)
                + "</" + escape(hex) + "><gray> gesetzt.</gray>");
    }

    // ── Nick ──────────────────────────────────────────────────────────

    public static Component nickUsage() {
        return prefixed("<gray>Benutzung: " + command("nick") + "<gray> <name|off> [spieler]</gray>");
    }

    public static Component nickSet(String nick) {
        return prefixed("<gray>Dein Nickname wurde auf " + value(nick) + "<gray> gesetzt.</gray>");
    }

    public static Component nickSetOther(String targetName, String nick) {
        return prefixed("<gray>Nickname von " + player(targetName) + "<gray> wurde auf " + value(nick) + "<gray> gesetzt.</gray>");
    }

    public static Component nickReset() {
        return prefixed("<gray>Dein Nickname wurde <#F87171>zurückgesetzt</#F87171><gray>.</gray>");
    }

    public static Component nickResetOther(String targetName) {
        return prefixed("<gray>Nickname von " + player(targetName) + "<gray> wurde zurückgesetzt.</gray>");
    }

    // ── Freeze ────────────────────────────────────────────────────────

    public static Component freezeUsage() {
        return prefixed("<gray>Benutzung: " + command("freeze") + "<gray> <spieler></gray>");
    }

    public static Component freezeToggled(String targetName, boolean frozen) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde "
                + (frozen ? "<#86EFAC>eingefroren</#86EFAC>" : "<#F87171>aufgetaut</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component freezeNotified(boolean frozen) {
        return prefixed("<gray>Du wurdest "
                + (frozen ? "<#86EFAC>eingefroren</#86EFAC>" : "<#F87171>aufgetaut</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component freezeBlocked() {
        return prefixed("<gray>Du bist eingefroren und kannst dich nicht bewegen.</gray>");
    }

    // ── Mute ──────────────────────────────────────────────────────────

    public static Component muteUsage() {
        return prefixed("<gray>Benutzung: " + command("mute") + "<gray> <spieler> [grund]</gray>");
    }

    public static Component mutedPlayer(String targetName, String reason) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde gemutet. Grund: <#F87171>" + escape(reason) + "</#F87171><gray>.</gray>");
    }

    public static Component mutedNotified(String reason) {
        return prefixed("<gray>Du wurdest gemutet. Grund: <#F87171>" + escape(reason) + "</#F87171><gray>.</gray>");
    }

    public static Component unmutedPlayer(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde entmutet.</gray>");
    }

    public static Component unmutedNotified() {
        return prefixed("<gray>Du wurdest <#86EFAC>entmutet</#86EFAC><gray>.</gray>");
    }

    public static Component alreadyMuted(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> ist bereits gemutet.</gray>");
    }

    public static Component notMuted(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> ist nicht gemutet.</gray>");
    }

    // ── Broadcast ────────────────────────────────────────────────────

    public static Component broadcastUsage() {
        return prefixed("<gray>Benutzung: " + command("broadcast") + "<gray> <nachricht></gray>");
    }

    public static Component broadcastMessage(String message) {
        return parse("<gold><bold>[Broadcast]</bold></gold> <white>" + escape(message) + "</white>");
    }

    // ── AFK ───────────────────────────────────────────────────────────

    public static Component afkEnabled(String playerName) {
        return prefixed("<gray>" + player(playerName) + "<gray> ist jetzt <#F59E0B>AFK</#F59E0B><gray>.</gray>");
    }

    public static Component afkDisabled(String playerName) {
        return prefixed("<gray>" + player(playerName) + "<gray> ist nicht mehr AFK.</gray>");
    }

    // ── Seen ─────────────────────────────────────────────────────────

    public static Component seenUsage() {
        return prefixed("<gray>Benutzung: " + command("seen") + "<gray> <spieler></gray>");
    }

    public static Component seenOnline(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> ist gerade <#86EFAC>online</#86EFAC><gray>.</gray>");
    }

    public static Component seenOffline(String targetName, String lastSeen) {
        return prefixed("<gray>" + player(targetName) + "<gray> war zuletzt online: " + value(lastSeen) + "<gray>.</gray>");
    }

    public static Component seenNeverJoined(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> hat den Server noch nie betreten.</gray>");
    }

    // ── Staff ─────────────────────────────────────────────────────────

    public static Component staffUsage() {
        return prefixed("<gray>Benutzung: " + command("staff") + "<gray> <nachricht></gray>");
    }

    public static Component staffMessage(String senderName, String message) {
        return parse("<dark_gray>[</dark_gray><gradient:#67E8F9:#3B82F6>Staff</gradient><dark_gray>]</dark_gray> "
                + player(senderName) + "<gray>: <white>" + escape(message) + "</white>");
    }

    // ── Repair ───────────────────────────────────────────────────────

    public static Component repairSuccess() {
        return prefixed("<gray>Das Item in deiner Hand wurde <#86EFAC>repariert</#86EFAC><gray>.</gray>");
    }

    public static Component repairAllSuccess() {
        return prefixed("<gray>Alle Items in deinem Inventar wurden <#86EFAC>repariert</#86EFAC><gray>.</gray>");
    }

    public static Component repairNoItem() {
        return prefixed("<gray>Du hältst kein reparierbares Item in der Hand.</gray>");
    }

    // ── God ───────────────────────────────────────────────────────────

    public static Component godSelf(boolean enabled) {
        return prefixed("<gray>Gottmodus wurde "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component godOther(String targetName, boolean enabled) {
        return prefixed("<gray>Gottmodus von " + player(targetName) + "<gray> wurde "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component godByOther(String actorName, boolean enabled) {
        return prefixed("<gray>Dein Gottmodus wurde von " + player(actorName) + "<gray> "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    // ── Sudo ──────────────────────────────────────────────────────────

    public static Component sudoUsage() {
        return prefixed("<gray>Benutzung: " + command("sudo") + "<gray> <spieler> <befehl></gray>");
    }

    public static Component sudoSuccess(String targetName, String cmd) {
        return prefixed("<gray>" + player(targetName) + "<gray> führt aus: " + value(cmd) + "<gray>.</gray>");
    }

    // ── Rename ────────────────────────────────────────────────────────

    public static Component renameUsage() {
        return prefixed("<gray>Benutzung: " + command("rename") + "<gray> <name></gray>");
    }

    public static Component renameSuccess(String name) {
        return prefixed("<gray>Item wurde in " + value(name) + "<gray> umbenannt.</gray>");
    }

    public static Component renameNoItem() {
        return prefixed("<gray>Du hältst kein Item in der Hand.</gray>");
    }

    // ── Lore ──────────────────────────────────────────────────────────

    public static Component loreUsage() {
        return prefixed("<gray>Benutzung: " + command("lore") + "<gray> <set|add|remove|clear> [zeile] [text]</gray>");
    }

    public static Component loreSet(int line, String text) {
        return prefixed("<gray>Lore Zeile " + value(String.valueOf(line)) + "<gray> wurde gesetzt: " + value(text) + "<gray>.</gray>");
    }

    public static Component loreAdded(String text) {
        return prefixed("<gray>Lore-Zeile " + value(text) + "<gray> wurde hinzugefügt.</gray>");
    }

    public static Component loreRemoved(int line) {
        return prefixed("<gray>Lore Zeile " + value(String.valueOf(line)) + "<gray> wurde entfernt.</gray>");
    }

    public static Component loreCleared() {
        return prefixed("<gray>Die gesamte Lore wurde gelöscht.</gray>");
    }

    public static Component loreNoItem() {
        return prefixed("<gray>Du hältst kein Item in der Hand.</gray>");
    }

    public static Component loreInvalidLine() {
        return prefixed("<gray>Ungültige Zeilennummer.</gray>");
    }

    // ── Balance ───────────────────────────────────────────────────────

    public static Component balanceUsage() {
        return prefixed("<gray>Benutzung: " + command("balance") + "<gray> [spieler]</gray>");
    }

    public static Component balanceSelf(double amount) {
        return prefixed("<gray>Dein Kontostand: " + value(String.format("%.2f", amount)) + "<gray> Coins.</gray>");
    }

    public static Component balanceOther(String targetName, double amount) {
        return prefixed("<gray>Kontostand von " + player(targetName) + "<gray>: " + value(String.format("%.2f", amount)) + "<gray> Coins.</gray>");
    }

    public static Component economyDisabled() {
        return prefixed("<gray>Das Economy-System ist deaktiviert.</gray>");
    }

    // ── Pay ───────────────────────────────────────────────────────────

    public static Component payUsage() {
        return prefixed("<gray>Benutzung: " + command("pay") + "<gray> <spieler> <betrag></gray>");
    }

    public static Component paySent(String targetName, double amount) {
        return prefixed("<gray>Du hast " + player(targetName) + "<gray> " + value(String.format("%.2f", amount)) + "<gray> Coins gesendet.</gray>");
    }

    public static Component payReceived(String senderName, double amount) {
        return prefixed("<gray>Du hast " + value(String.format("%.2f", amount)) + "<gray> Coins von " + player(senderName) + "<gray> erhalten.</gray>");
    }

    public static Component payInvalidAmount() {
        return prefixed("<gray>Ungültiger Betrag. Nutze eine positive Zahl.</gray>");
    }

    public static Component payNotEnough() {
        return prefixed("<gray>Du hast nicht genug Coins.</gray>");
    }

    public static Component payNotSelf() {
        return prefixed("<gray>Du kannst dir nicht selbst Coins senden.</gray>");
    }

    // ── Near ──────────────────────────────────────────────────────────

    public static Component nearUsage() {
        return prefixed("<gray>Benutzung: " + command("near") + "<gray> [radius]</gray>");
    }

    public static Component nearResult(int count, int radius) {
        return prefixed("<gray>Spieler in " + value(String.valueOf(radius)) + "<gray> Blöcken: <#4DA3FF>" + count + "</#4DA3FF><gray>.</gray>");
    }

    public static Component nearEmpty(int radius) {
        return prefixed("<gray>Keine Spieler in " + value(String.valueOf(radius)) + "<gray> Blöcken gefunden.</gray>");
    }

    // ── XP ────────────────────────────────────────────────────────────

    public static Component xpUsage() {
        return prefixed("<gray>Benutzung: " + command("xp") + "<gray> <set|add|remove> <menge> [spieler]</gray>");
    }

    public static Component xpSet(int amount) {
        return prefixed("<gray>Dein XP wurde auf " + value(String.valueOf(amount)) + "<gray> gesetzt.</gray>");
    }

    public static Component xpSetOther(String targetName, int amount) {
        return prefixed("<gray>XP von " + player(targetName) + "<gray> wurde auf " + value(String.valueOf(amount)) + "<gray> gesetzt.</gray>");
    }

    public static Component xpAdded(int amount) {
        return prefixed("<gray>" + value(String.valueOf(amount)) + "<gray> XP wurden hinzugefügt.</gray>");
    }

    public static Component xpAddedOther(String targetName, int amount) {
        return prefixed("<gray>" + value(String.valueOf(amount)) + "<gray> XP wurden " + player(targetName) + "<gray> hinzugefügt.</gray>");
    }

    public static Component xpRemoved(int amount) {
        return prefixed("<gray>" + value(String.valueOf(amount)) + "<gray> XP wurden entfernt.</gray>");
    }

    public static Component xpRemovedOther(String targetName, int amount) {
        return prefixed("<gray>" + value(String.valueOf(amount)) + "<gray> XP wurden von " + player(targetName) + "<gray> entfernt.</gray>");
    }

    public static Component xpInvalidAmount() {
        return prefixed("<gray>Ungültige XP-Menge.</gray>");
    }

    // ── Ping ─────────────────────────────────────────────────────────

    public static Component pingSelf(int ping) {
        return prefixed("<gray>Dein Ping: " + value(ping + "ms") + "<gray>.</gray>");
    }

    public static Component pingOther(String targetName, int ping) {
        return prefixed("<gray>Ping von " + player(targetName) + "<gray>: " + value(ping + "ms") + "<gray>.</gray>");
    }

    // ── Playtime ─────────────────────────────────────────────────────

    public static Component playtimeSelf(String formatted) {
        return prefixed("<gray>Deine Spielzeit: " + value(formatted) + "<gray>.</gray>");
    }

    public static Component playtimeOther(String targetName, String formatted) {
        return prefixed("<gray>Spielzeit von " + player(targetName) + "<gray>: " + value(formatted) + "<gray>.</gray>");
    }

    // ── History ──────────────────────────────────────────────────────

    public static Component historyUsage() {
        return prefixed("<gray>Benutzung: " + command("history") + "<gray> <spieler></gray>");
    }

    public static Component historyHeader(String targetName) {
        return prefixed("<gray>Straf-Historie von " + player(targetName) + "<gray>:</gray>");
    }

    public static Component historyEmpty(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> hat keine Straf-Historie.</gray>");
    }

    public static Component historyEntry(int index, String type, String reason, String actor, String date) {
        return parse("<dark_gray>" + index + ". </dark_gray><#F87171>" + escape(type) + "</#F87171>"
                + " <gray>– " + escape(reason) + " <dark_gray>(" + escape(actor) + ", " + escape(date) + ")</dark_gray>");
    }

    // ── Maintenance ──────────────────────────────────────────────────

    public static Component maintenanceEnabled() {
        return prefixed("<gray>Wartungsmodus wurde <#86EFAC>aktiviert</#86EFAC><gray>.</gray>");
    }

    public static Component maintenanceDisabled() {
        return prefixed("<gray>Wartungsmodus wurde <#F87171>deaktiviert</#F87171><gray>.</gray>");
    }

    // ── Restart ──────────────────────────────────────────────────────

    public static Component restartUsage() {
        return prefixed("<gray>Benutzung: " + command("restart") + "<gray> [sekunden]</gray>");
    }

    public static Component restartScheduled(int seconds) {
        return prefixed("<gray>Server-Neustart in " + value(String.valueOf(seconds)) + "<gray> Sekunden.</gray>");
    }

    public static Component restartBroadcast(int seconds) {
        return parse("<red><bold>[!]</bold></red> <gray>Der Server startet in <#F87171>" + seconds + "</#F87171> Sekunden neu.</gray>");
    }

    public static Component restartCancelled() {
        return prefixed("<gray>Server-Neustart wurde <#F87171>abgebrochen</#F87171><gray>.</gray>");
    }

    // ── TPS ──────────────────────────────────────────────────────────

    public static Component tpsInfo(String tps1, String tps5, String tps15) {
        return prefixed("<gray>TPS: " + value(tps1) + "<gray> (1m) | " + value(tps5) + "<gray> (5m) | " + value(tps15) + "<gray> (15m)</gray>");
    }

    // ── Memory ───────────────────────────────────────────────────────

    public static Component memoryInfo(long usedMB, long maxMB) {
        return prefixed("<gray>RAM: " + value(usedMB + " MB") + "<gray> / " + value(maxMB + " MB") + "<gray> genutzt.</gray>");
    }

    // ── More ──────────────────────────────────────────────────────────

    public static Component moreNoItem() {
        return prefixed("<gray>Du hältst kein Item in der Hand.</gray>");
    }

    public static Component moreFilled(String itemName) {
        return prefixed("<gray>" + item(itemName) + "<gray> wurde auf " + value("64") + "<gray> aufgefüllt.</gray>");
    }

    // ── Skull ─────────────────────────────────────────────────────────

    public static Component skullUsage() {
        return prefixed("<gray>Benutzung: " + command("skull") + "<gray> [spieler]</gray>");
    }

    public static Component skullGiven(String ownerName) {
        return prefixed("<gray>Kopf von " + player(ownerName) + "<gray> wurde gegeben.</gray>");
    }

    // ── InvSave / InvLoad ─────────────────────────────────────────────

    public static Component invSaveUsage() {
        return prefixed("<gray>Benutzung: " + command("invsave") + "<gray> <name></gray>");
    }

    public static Component invSaved(String name) {
        return prefixed("<gray>Inventar unter " + value(name) + "<gray> gespeichert.</gray>");
    }

    public static Component invLoadUsage() {
        return prefixed("<gray>Benutzung: " + command("invload") + "<gray> <name></gray>");
    }

    public static Component invLoaded(String name) {
        return prefixed("<gray>Inventar " + value(name) + "<gray> wurde geladen.</gray>");
    }

    public static Component invNotFound(String name) {
        return prefixed("<gray>Kein gespeichertes Inventar mit dem Namen " + value(name) + "<gray> gefunden.</gray>");
    }

    // ── CommandSpy / SocialSpy ────────────────────────────────────────

    public static Component commandSpyToggled(boolean enabled) {
        return prefixed("<gray>CommandSpy wurde "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component socialSpyToggled(boolean enabled) {
        return prefixed("<gray>SocialSpy wurde "
                + (enabled ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>");
    }

    public static Component commandSpyLog(String playerName, String cmd) {
        return parse("<dark_gray>[<#67E8F9>CSpy</#67E8F9>] " + player(playerName) + "<dark_gray>: <gray>" + escape(cmd));
    }

    public static Component socialSpyLog(String fromName, String toName, String message) {
        return parse("<dark_gray>[<#67E8F9>SSpy</#67E8F9>] " + player(fromName)
                + "<dark_gray> → " + player(toName) + "<dark_gray>: <white>" + escape(message) + "</white>");
    }

    // ── Ignore ────────────────────────────────────────────────────────

    public static Component ignoreUsage() {
        return prefixed("<gray>Benutzung: " + command("ignore") + "<gray> <spieler></gray>");
    }

    public static Component ignoreAdded(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wird jetzt ignoriert.</gray>");
    }

    public static Component ignoreRemoved(String targetName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wird nicht mehr ignoriert.</gray>");
    }

    public static Component ignoreBlocked(String senderName) {
        return prefixed("<gray>Du ignorierst " + player(senderName) + "<gray>.</gray>");
    }

    // ── Eco ───────────────────────────────────────────────────────────

    public static Component ecoUsage() {
        return prefixed("<gray>Benutzung: " + command("eco") + "<gray> <give|take|set|reset> <spieler> [betrag]</gray>");
    }

    public static Component ecoGive(String targetName, double amount) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurden " + value(String.format("%.2f", amount)) + "<gray> Coins gegeben.</gray>");
    }

    public static Component ecoTake(String targetName, double amount) {
        return prefixed("<gray>Von " + player(targetName) + "<gray> wurden " + value(String.format("%.2f", amount)) + "<gray> Coins abgezogen.</gray>");
    }

    public static Component ecoSet(String targetName, double amount) {
        return prefixed("<gray>Kontostand von " + player(targetName) + "<gray> wurde auf " + value(String.format("%.2f", amount)) + "<gray> gesetzt.</gray>");
    }

    public static Component ecoReset(String targetName) {
        return prefixed("<gray>Kontostand von " + player(targetName) + "<gray> wurde zurückgesetzt.</gray>");
    }

    // ── Trail ─────────────────────────────────────────────────────────

    public static Component trailUsage() {
        return prefixed("<gray>Benutzung: " + command("trail") + "<gray> <typ|off> [spieler]</gray>");
    }

    public static Component trailSet(String trailName) {
        return prefixed("<gray>Dein Trail wurde auf " + value(trailName) + "<gray> gesetzt.</gray>");
    }

    public static Component trailSetOther(String targetName, String trailName) {
        return prefixed("<gray>Trail von " + player(targetName) + "<gray> wurde auf " + value(trailName) + "<gray> gesetzt.</gray>");
    }

    public static Component trailDisabled() {
        return prefixed("<gray>Dein Trail wurde <#F87171>deaktiviert</#F87171><gray>.</gray>");
    }

    public static Component trailInvalid(String input) {
        return prefixed("<gray>Unbekannter Trail-Typ: <#F87171>" + escape(input) + "</#F87171><gray>.</gray>");
    }

    // ── Firework ──────────────────────────────────────────────────────

    public static Component fireworkUsage() {
        return prefixed("<gray>Benutzung: " + command("firework") + "<gray> [anzahl]</gray>");
    }

    public static Component fireworkLaunched(int count) {
        return prefixed("<gray><#4DA3FF>" + count + "</#4DA3FF><gray>x Feuerwerk wurde gestartet.</gray>");
    }

    // ── TpWorld ──────────────────────────────────────────────────────

    public static Component tpWorldUsage() {
        return prefixed("<gray>Benutzung: " + command("tpworld") + "<gray> <welt> [spieler]</gray>");
    }

    public static Component tpWorldSuccess(String worldName) {
        return prefixed("<gray>Du wurdest in die Welt " + value(worldName) + "<gray> teleportiert.</gray>");
    }

    public static Component tpWorldSuccessOther(String targetName, String worldName) {
        return prefixed("<gray>" + player(targetName) + "<gray> wurde in die Welt " + value(worldName) + "<gray> teleportiert.</gray>");
    }

    public static Component tpWorldNotFound(String worldName) {
        return prefixed("<gray>Die Welt " + value(worldName) + "<gray> wurde nicht gefunden oder ist nicht geladen.</gray>");
    }
}
