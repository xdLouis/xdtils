package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InfoCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.info")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("info") + "<gray> <spieler></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        sendInfo(sender, target);
        return true;
    }

    private void sendInfo(CommandSender sender, Player target) {
        String name        = target.getName();
        String uuid        = target.getUniqueId().toString();
        int ping           = target.getPing();
        String gamemode    = formatGamemode(target.getGameMode().name());
        String world       = target.getWorld().getName();
        String location    = (int) target.getLocation().getX() + ", "
                + (int) target.getLocation().getY() + ", "
                + (int) target.getLocation().getZ();
        String health      = String.format("%.1f", target.getHealth()) + " / "
                + String.format("%.1f", target.getAttribute(
                org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
        String food        = target.getFoodLevel() + " / 20";
        String level       = target.getLevel() + " (" + target.getTotalExperience() + " XP)";
        String firstJoin   = DATE_FORMAT.format(new Date(target.getFirstPlayed()));
        String lastSeen    = DATE_FORMAT.format(new Date(target.getLastLogin()));
        long playtimeTicks = target.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
        String playtime    = formatPlaytime(playtimeTicks);
        boolean isOp       = target.isOp();
        boolean isFlying   = target.isFlying();
        boolean isVanished = !target.canSee(target); // crude check — du kannst VanishManager injecten
        String opStatus    = isOp ? "<#86EFAC>Ja</#86EFAC>" : "<#F87171>Nein</#F87171>";
        String flyStatus   = isFlying ? "<#86EFAC>Ja</#86EFAC>" : "<#F87171>Nein</#F87171>";

        // IP — versteckt mit Obfuscation, Reveal-Button daneben
        InetSocketAddress addr = target.getAddress();
        String rawIp = addr != null ? addr.getAddress().getHostAddress() : "Unbekannt";

        // ── Header ──────────────────────────────────────────────────────
        sender.sendMessage(MM.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize(
                "  <#4DA3FF><b>" + esc(name) + "</b></#4DA3FF>"
                        + "  <dark_gray>|</dark_gray>  "
                        + pingColor(ping) + ping + "ms</" + pingColorClose(ping) + ">"));
        sender.sendMessage(MM.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));

        // ── Allgemein ────────────────────────────────────────────────────
        sender.sendMessage(row("UUID",      uuid));
        sender.sendMessage(row("Gamemode",  gamemode));
        sender.sendMessage(row("Welt",      world));
        sender.sendMessage(row("Position",  location));
        sender.sendMessage(row("Health",    health));
        sender.sendMessage(row("Hunger",    food));
        sender.sendMessage(row("Level",     level));
        sender.sendMessage(row("OP",        opStatus));
        sender.sendMessage(row("Fliegt",    flyStatus));
        sender.sendMessage(row("Playtime",  playtime));
        sender.sendMessage(row("Erster Join", firstJoin));
        sender.sendMessage(row("Letzter Login", lastSeen));

        // ── IP (versteckt) ───────────────────────────────────────────────
        sender.sendMessage(buildIpRow(rawIp));

        // ── Aktionen ─────────────────────────────────────────────────────
        sender.sendMessage(Component.empty());
        sender.sendMessage(buildActions(target));

        sender.sendMessage(MM.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
    }

    // ── Zeilen-Builder ───────────────────────────────────────────────────

    private Component row(String label, String value) {
        return MM.deserialize(
                "  <dark_gray>»</dark_gray> <gray>" + label + ":</gray> <#67E8F9>" + value + "</#67E8F9>");
    }

    private Component buildIpRow(String rawIp) {
        // Obfuscated-Placeholder
        Component hidden = MM.deserialize(
                "  <dark_gray>»</dark_gray> <gray>IP:</gray> <#F87171><obf>xxxx.xxxx.xxxx</obf></#F87171>");

        // [Anzeigen]-Button
        Component reveal = MM.deserialize(" <dark_gray>[</dark_gray><yellow>Anzeigen</yellow><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.copyToClipboard(rawIp))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray>Klicken um IP anzuzeigen & zu kopieren\n<#67E8F9>" + esc(rawIp) + "</#67E8F9>")
                ));

        return hidden.append(reveal);
    }

    private Component buildActions(Player target) {
        String name = target.getName();

        Component label = MM.deserialize("  <dark_gray>»</dark_gray> <gray>Aktionen:</gray> ");

        // [InvSee]
        Component invsee = MM.deserialize("<dark_gray>[</dark_gray><gradient:#67E8F9:#3B82F6>InvSee</gradient><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/invsee " + name))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray>Inventar von <#4DA3FF>" + esc(name) + "</#4DA3FF><gray> öffnen</gray>")
                ));

        // [TP]
        Component tp = MM.deserialize(" <dark_gray>[</dark_gray><green>TP</green><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/tp " + name))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray>Zu <#4DA3FF>" + esc(name) + "</#4DA3FF><gray> teleportieren</gray>")
                ));

        // [Holen]
        Component tphere = MM.deserialize(" <dark_gray>[</dark_gray><aqua>Holen</aqua><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/tphere " + name))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray><#4DA3FF>" + esc(name) + "</#4DA3FF><gray> zu dir holen</gray>")
                ));

        // [Kick]
        Component kick = MM.deserialize(" <dark_gray>[</dark_gray><red>Kick</red><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/kick " + name))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray><#F87171>" + esc(name) + "</#F87171><gray> kicken</gray>")
                ));

        // [Ban]
        Component ban = MM.deserialize(" <dark_gray>[</dark_gray><dark_red>Ban</dark_red><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.suggestCommand("/ban " + name + " "))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray><#F87171>" + esc(name) + "</#F87171><gray> bannen (Grund eingeben)</gray>")
                ));

        // [Heal]
        Component heal = MM.deserialize(" <dark_gray>[</dark_gray><#86EFAC>Heal</#86EFAC><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/heal " + name))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray><#4DA3FF>" + esc(name) + "</#4DA3FF><gray> heilen</gray>")
                ));

        // [GM Creative]
        Component gmc = MM.deserialize(" <dark_gray>[</dark_gray><#C084FC>Creative</#C084FC><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/gamemode creative " + name))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray>Gamemode von <#4DA3FF>" + esc(name) + "</#4DA3FF><gray> auf Creative setzen</gray>")
                ));

        // [GM Survival]
        Component gms = MM.deserialize(" <dark_gray>[</dark_gray><#FCD34D>Survival</#FCD34D><dark_gray>]</dark_gray>")
                .clickEvent(ClickEvent.runCommand("/gamemode survival " + name))
                .hoverEvent(HoverEvent.showText(
                        MM.deserialize("<gray>Gamemode von <#4DA3FF>" + esc(name) + "</#4DA3FF><gray> auf Survival setzen</gray>")
                ));

        return label
                .append(invsee)
                .append(tp)
                .append(tphere)
                .append(kick)
                .append(ban)
                .append(heal)
                .append(gmc)
                .append(gms);
    }

    // ── Hilfsmethoden ────────────────────────────────────────────────────

    private String formatPlaytime(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours   = minutes / 60;
        long days    = hours / 24;
        return days + "d " + (hours % 24) + "h " + (minutes % 60) + "m";
    }

    private String formatGamemode(String raw) {
        return switch (raw) {
            case "CREATIVE"  -> "<#C084FC>Creative</#C084FC>";
            case "SURVIVAL"  -> "<#FCD34D>Survival</#FCD34D>";
            case "ADVENTURE" -> "<#67E8F9>Adventure</#67E8F9>";
            case "SPECTATOR" -> "<gray>Spectator</gray>";
            default          -> raw;
        };
    }

    private String pingColor(int ping) {
        if (ping < 60)  return "<#86EFAC>";
        if (ping < 120) return "<#FCD34D>";
        if (ping < 200) return "<#F59E0B>";
        return "<#F87171>";
    }

    private String pingColorClose(int ping) {
        if (ping < 60)  return "#86EFAC";
        if (ping < 120) return "#FCD34D";
        if (ping < 200) return "#F59E0B";
        return "#F87171";
    }

    private String esc(String text) {
        return text.replace("<", "\\<").replace(">", "\\>");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}