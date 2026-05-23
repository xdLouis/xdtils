package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class XpCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.xp")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("xp")
                    + "<gray> <give|set|take> <spieler> <menge> [levels]</gray>"));
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[1]));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Ungültige Menge: <#F87171>" + args[2] + "</#F87171></gray>"));
            return true;
        }

        boolean levels = args.length >= 4 && args[3].equalsIgnoreCase("levels");

        switch (sub) {
            case "give" -> {
                if (levels) target.giveExpLevels(amount);
                else target.giveExp(amount);
                sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                        + "<gray> hat <#86EFAC>+" + amount + (levels ? " Level" : " XP") + "</#86EFAC><gray> erhalten.</gray>"));
                target.sendMessage(MessageUtil.prefixed("<gray>Du hast <#86EFAC>+" + amount
                        + (levels ? " Level" : " XP") + "</#86EFAC><gray> erhalten.</gray>"));
            }
            case "take" -> {
                if (levels) target.giveExpLevels(-amount);
                else target.giveExp(-amount);
                sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                        + "<gray> hat <#F87171>-" + amount + (levels ? " Level" : " XP") + "</#F87171><gray> verloren.</gray>"));
                target.sendMessage(MessageUtil.prefixed("<gray>Dir wurden <#F87171>-" + amount
                        + (levels ? " Level" : " XP") + "</#F87171><gray> abgezogen.</gray>"));
            }
            case "set" -> {
                if (levels) target.setLevel(amount);
                else target.setTotalExperience(amount);
                sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                        + "<gray> XP gesetzt auf <#67E8F9>" + amount
                        + (levels ? " Level" : " XP") + "</#67E8F9><gray>.</gray>"));
            }
            default -> sender.sendMessage(MessageUtil.prefixed("<gray>Ungültige Aktion. Nutze give, set oder take.</gray>"));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("give", "set", "take");
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            String input = args[1].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
            return list;
        }
        if (args.length == 4) return List.of("levels");
        return List.of();
    }
}