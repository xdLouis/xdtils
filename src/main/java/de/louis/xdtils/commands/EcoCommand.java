package de.louis.xdtils.commands;

import de.louis.xdtils.manager.EconomyManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EcoCommand implements CommandExecutor, TabCompleter {

    private final EconomyManager economyManager;

    public EcoCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.eco")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("eco <set|add|remove> <spieler> <betrag>") + "</gray>"));
            return true;
        }

        String action = args[0].toLowerCase(Locale.ROOT);
        var target = economyManager.findPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[1]));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Bitte gib einen gültigen Betrag an.</gray>"));
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Der Betrag darf nicht negativ sein.</gray>"));
            return true;
        }

        switch (action) {
            case "set" -> economyManager.setBalance(target, amount);
            case "add" -> economyManager.addBalance(target, amount);
            case "remove" -> {
                if (!economyManager.removeBalance(target, amount)) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Dieser Spieler hat nicht genug Guthaben.</gray>"));
                    return true;
                }
            }
            default -> {
                sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                        + MessageUtil.command("eco <set|add|remove> <spieler> <betrag>") + "</gray>"));
                return true;
            }
        }

        String targetName = target.getName() != null ? target.getName() : args[1];
        double newBalance = economyManager.getBalance(target);

        sender.sendMessage(MessageUtil.prefixed("<gray>Kontostand von "
                + MessageUtil.player(targetName)
                + "<gray> aktualisiert: <#86EFAC>" + economyManager.format(newBalance) + "</#86EFAC><gray>.</gray>"));

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(MessageUtil.prefixed("<gray>Dein Kontostand wurde auf <#86EFAC>"
                    + economyManager.format(newBalance) + "</#86EFAC><gray> geändert.</gray>"));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("set", "add", "remove").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String input = args[1].toLowerCase(Locale.ROOT);
            return sender.getServer().getOnlinePlayers().stream()
                    .map(player -> player.getName())
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}