package de.louis.xdtils.commands;

import de.louis.xdtils.manager.EconomyManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PayCommand implements CommandExecutor, TabCompleter {

    private final EconomyManager economyManager;

    public PayCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Dieser Befehl ist nur für Spieler.</gray>"));
            return true;
        }

        if (!sender.hasPermission("xdtils.pay")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("pay <spieler> <betrag>") + "</gray>"));
            return true;
        }

        var target = economyManager.findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Du kannst dir nicht selbst Geld senden.</gray>"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Bitte gib einen gültigen Betrag an.</gray>"));
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Der Betrag muss größer als 0 sein.</gray>"));
            return true;
        }

        if (!economyManager.removeBalance(player, amount)) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Du hast nicht genug Guthaben.</gray>"));
            return true;
        }

        economyManager.addBalance(target, amount);

        String targetName = target.getName() != null ? target.getName() : args[0];
        sender.sendMessage(MessageUtil.prefixed("<gray>Du hast "
                + MessageUtil.player(targetName)
                + "<gray> <#86EFAC>" + economyManager.format(amount) + "</#86EFAC><gray> gesendet.</gray>"));

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(MessageUtil.prefixed("<gray>Du hast von "
                    + MessageUtil.player(player.getName())
                    + "<gray> <#86EFAC>" + economyManager.format(amount) + "</#86EFAC><gray> erhalten.</gray>"));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            return sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return List.of();
    }
}