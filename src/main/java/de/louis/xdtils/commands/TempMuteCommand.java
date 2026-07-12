package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BanManager;
import de.louis.xdtils.manager.MuteManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class TempMuteCommand implements CommandExecutor, TabCompleter {

    private final MuteManager muteManager;
    private final BanManager banManager;

    public TempMuteCommand(MuteManager muteManager, BanManager banManager) {
        this.muteManager = muteManager;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("xdtils.tempmute")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.prefixed(
                "<gray>Benutzung: " + MessageUtil.command("tempmute")
                + "<gray> <spieler> <zeit> [grund]</gray>\n"
                + "<gray>Zeitformate: <#86EFAC>10s</#86EFAC><gray>, <#86EFAC>5m</#86EFAC><gray>, "
                + "<#86EFAC>2h</#86EFAC><gray>, <#86EFAC>3d</#86EFAC><gray>, <#86EFAC>1w</#86EFAC>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (muteManager.isMuted(target.getUniqueId())) {
            sender.sendMessage(MessageUtil.prefixed(
                "<gray>" + MessageUtil.player(target.getName()) + "<gray> ist bereits stummgeschaltet."));
            return true;
        }

        long durationMs = BanManager.parseDuration(args[1]);
        if (durationMs <= 0) {
            sender.sendMessage(MessageUtil.tempBanInvalidDuration(args[1]));
            return true;
        }

        String reason = args.length >= 3
            ? String.join(" ", Arrays.copyOfRange(args, 2, args.length))
            : "Kein Grund angegeben";

        String actorName = sender instanceof Player p ? p.getName() : "Konsole";
        String durationFormatted = BanManager.formatDuration(durationMs);

        muteManager.tempMute(target.getUniqueId(), actorName, reason, durationMs);

        // History-Eintrag im BanManager (wird in /banhistory angezeigt)
        banManager.logMute(target.getName(), reason, actorName, durationMs);

        target.sendMessage(MessageUtil.prefixed(
            "<gray>Du wurdest für <#FCD34D>" + durationFormatted + "</#FCD34D><gray> "
            + "<#F87171>stummgeschaltet</#F87171><gray>.\n"
            + "<gray>Grund: <#F87171>" + reason + "</#F87171></gray>"));

        sender.sendMessage(MessageUtil.prefixed(
            "<gray>" + MessageUtil.player(target.getName())
            + "<gray> wurde für <#FCD34D>" + durationFormatted + "</#FCD34D><gray> stummgeschaltet."));

        Bukkit.broadcast(MessageUtil.prefixed(
            "<gray>" + MessageUtil.player(target.getName())
            + "<gray> wurde für <#FCD34D>" + durationFormatted + "</#FCD34D><gray> stummgeschaltet."
            + " <dark_gray>(" + reason + ")</dark_gray>"),
            "xdtils.staff");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(input))
                .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return List.of("10m", "30m", "1h", "6h", "12h", "1d", "3d", "7d")
                .stream().filter(t -> t.startsWith(args[1])).collect(Collectors.toList());
        }
        if (args.length == 3) {
            return new ArrayList<>(List.of("Spam", "Beleidigung", "Werbung", "Toxisches Verhalten"));
        }
        return List.of();
    }
}
