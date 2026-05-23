package de.louis.xdtils.commands;

import de.louis.xdtils.manager.HistoryManager;
import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd.MM.yy HH:mm");
    private final HistoryManager historyManager;

    public HistoryCommand(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.history")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("history") + "<gray> <spieler></gray>"));
            return true;
        }

        String targetName = args[0];
        UUID uuid = historyManager.getUUID(targetName);
        if (uuid == null) {
            sender.sendMessage(MessageUtil.playerNotFound(targetName));
            return true;
        }

        List<HistoryManager.HistoryEntry> entries = historyManager.getHistory(uuid);

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize("  <#4DA3FF><b>Historie von</b></#4DA3FF> "
                + MessageUtil.player(targetName)));
        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));

        if (entries.isEmpty()) {
            sender.sendMessage(MM.deserialize("  <gray>Keine Einträge vorhanden.</gray>"));
        } else {
            for (HistoryManager.HistoryEntry e : entries) {
                String typeColor = switch (e.type()) {
                    case "BAN"  -> "<#F87171>";
                    case "KICK" -> "<#FCD34D>";
                    case "MUTE" -> "<#C084FC>";
                    case "WARN" -> "<#F59E0B>";
                    default     -> "<gray>";
                };
                String typeClose = switch (e.type()) {
                    case "BAN"  -> "</#F87171>";
                    case "KICK" -> "</#FCD34D>";
                    case "MUTE" -> "</#C084FC>";
                    case "WARN" -> "</#F59E0B>";
                    default     -> "</gray>";
                };
                sender.sendMessage(MM.deserialize(
                        "  <dark_gray>[" + FMT.format(new Date(e.timestamp())) + "]</dark_gray>"
                                + " " + typeColor + e.type() + typeClose
                                + " <dark_gray>by</dark_gray> <#86EFAC>" + e.by() + "</#86EFAC>"
                                + " <dark_gray>—</dark_gray> <gray>" + e.reason() + "</gray>"));
            }
        }

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
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