package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OpCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final boolean deop;

    public OpCommand(boolean deop) {
        this.deop = deop;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        String permission = deop ? "xdtils.deop" : "xdtils.op";
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (!deop && args.length > 0 && args[0].equalsIgnoreCase("list")) {
            sendOpList(sender);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command(deop ? "deop" : "op")
                    + "<gray> <spieler>" + (!deop ? "|list" : "") + "</gray>"));
            return true;
        }

        String targetName = args[0];
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        boolean success = Bukkit.dispatchCommand(
                console,
                (deop ? "minecraft:deop " : "minecraft:op ") + targetName
        );

        if (!success) {
            sender.sendMessage(MessageUtil.prefixed("<gray>"
                    + MessageUtil.player(targetName)
                    + "<gray> konnte nicht "
                    + (deop ? "<#F87171>deopped</#F87171>" : "<#F87171>geopped</#F87171>")
                    + "<gray> werden.</gray>"));
            return true;
        }

        Player online = Bukkit.getPlayerExact(targetName);

        if (deop) {
            sender.sendMessage(MessageUtil.prefixed("<gray>"
                    + MessageUtil.player(targetName)
                    + "<gray> ist kein Operator mehr.</gray>"));
            if (online != null) {
                online.sendMessage(MessageUtil.prefixed(
                        "<gray>Dir wurde der <#F87171>Operator-Status</#F87171><gray> entzogen.</gray>"));
            }
        } else {
            sender.sendMessage(MessageUtil.prefixed("<gray>"
                    + MessageUtil.player(targetName)
                    + "<gray> ist jetzt <#86EFAC>Operator</#86EFAC><gray>.</gray>"));
            if (online != null) {
                online.sendMessage(MessageUtil.prefixed(
                        "<gray>Du bist jetzt <#86EFAC>Operator</#86EFAC><gray>.</gray>"));
            }
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    private void sendOpList(CommandSender sender) {
        Set<OfflinePlayer> ops = Bukkit.getOperators();

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize("  <#4DA3FF><b>Operators</b></#4DA3FF>"
                + "  <dark_gray>(" + ops.size() + ")</dark_gray>"));
        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));

        if (ops.isEmpty()) {
            sender.sendMessage(MM.deserialize("  <gray>Keine Operators vorhanden.</gray>"));
        } else {
            List<OfflinePlayer> sorted = new ArrayList<>(ops);
            sorted.sort(Comparator.comparing(op -> op.getName() == null ? "" : op.getName(), String.CASE_INSENSITIVE_ORDER));

            for (OfflinePlayer op : sorted) {
                String name = op.getName() == null ? "Unbekannt" : op.getName();
                String status = op.isOnline()
                        ? "<#86EFAC>● online</#86EFAC>"
                        : "<dark_gray>● offline</dark_gray>";

                sender.sendMessage(MM.deserialize(
                        "  <dark_gray>»</dark_gray> <#F59E0B>⭐</#F59E0B> "
                                + "<#4DA3FF>" + name + "</#4DA3FF>  " + status
                ));
            }
        }

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) return List.of();

        String input = args[0].toLowerCase(Locale.ROOT);
        List<String> list = new ArrayList<>();

        if (!deop) {
            if ("list".startsWith(input)) list.add("list");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.isOp() && p.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(p.getName());
                }
            }
        } else {
            for (OfflinePlayer op : Bukkit.getOperators()) {
                if (op.getName() != null && op.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(op.getName());
                }
            }
        }

        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }
}