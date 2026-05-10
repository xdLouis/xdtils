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

public class FlyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.fly")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // /fly → toggle für sich selbst
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }
            toggleFly(player, !player.getAllowFlight(), sender.getName());
            return true;
        }

        // /fly <on|off>
        if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }
            boolean enable = args[0].equalsIgnoreCase("on");
            toggleFly(player, enable, sender.getName());
            return true;
        }

        // /fly <spieler> [on|off]
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        boolean enable = args.length >= 2
                ? args[1].equalsIgnoreCase("on")
                : !target.getAllowFlight();

        target.setAllowFlight(enable);
        if (!enable) target.setFlying(false);

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            target.sendMessage(MessageUtil.flySelf(enable));
        } else {
            sender.sendMessage(MessageUtil.flyOther(target.getName(), enable));
            target.sendMessage(MessageUtil.flyByOther(sender.getName(), enable));
        }
        return true;
    }

    private void toggleFly(Player player, boolean enable, String actorName) {
        player.setAllowFlight(enable);
        if (!enable) player.setFlying(false);
        player.sendMessage(MessageUtil.flySelf(enable));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            if ("on".startsWith(input)) list.add("on");
            if ("off".startsWith(input)) list.add("off");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        if (args.length == 2) {
            return List.of("on", "off");
        }
        return list;
    }
}