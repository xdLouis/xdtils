package de.louis.xdtils.commands;

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
import java.util.List;
import java.util.Locale;

public class SpeedCommand implements CommandExecutor, TabCompleter {

    public enum SpeedType {
        AUTO, WALK, FLY
    }

    private final SpeedType speedType;

    public SpeedCommand(SpeedType speedType) {
        this.speedType = speedType;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        String permission = "xdtils." + label.toLowerCase(Locale.ROOT);
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.speedUsage(label));
            return true;
        }

        int displayValue;
        try {
            displayValue = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.speedInvalid());
            return true;
        }

        if (displayValue < 0 || displayValue > 10) {
            sender.sendMessage(MessageUtil.speedInvalid());
            return true;
        }

        float bukkit = displayValue / 10.0f;

        // Target: self or other player
        Player target;
        boolean isSelf;

        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[1]));
                return true;
            }
            isSelf = sender instanceof Player sp && sp.getUniqueId().equals(target.getUniqueId());
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }
            target = player;
            isSelf = true;
        }

        // Determine actual type (AUTO detects from state)
        SpeedType resolved = speedType;
        if (resolved == SpeedType.AUTO) {
            resolved = target.isFlying() ? SpeedType.FLY : SpeedType.WALK;
        }

        String typeName = resolved == SpeedType.FLY ? "Flyspeed" : "Walkspeed";

        if (resolved == SpeedType.FLY) {
            target.setFlySpeed(bukkit);
        } else {
            target.setWalkSpeed(bukkit);
        }

        if (isSelf) {
            target.sendMessage(MessageUtil.speedChanged(typeName, displayValue));
        } else {
            sender.sendMessage(MessageUtil.speedChangedOther(target.getName(), typeName, displayValue));
            target.sendMessage(MessageUtil.speedChangedByOther(sender.getName(), typeName, displayValue));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            for (int i = 0; i <= 10; i++) {
                String val = String.valueOf(i);
                if (val.startsWith(args[0])) {
                    list.add(val);
                }
            }
            return list;
        }

        if (args.length == 2) {
            String input = args[1].toLowerCase(Locale.ROOT);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(player.getName());
                }
            }
        }

        return list;
    }
}