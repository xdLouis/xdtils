package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GodCommand implements CommandExecutor, TabCompleter {

    private final Set<UUID> godPlayers = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.god")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // /god → sich selbst
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }
            toggleGod(player, null);
            return true;
        }

        // /god <spieler>
        if (!sender.hasPermission("xdtils.god.others")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        toggleGod(target, sender.getName().equals(target.getName()) ? null : sender);
        return true;
    }

    private void toggleGod(Player player, CommandSender notifyOther) {
        boolean now;
        if (godPlayers.contains(player.getUniqueId())) {
            godPlayers.remove(player.getUniqueId());
            player.setInvulnerable(false);
            now = false;
        } else {
            godPlayers.add(player.getUniqueId());
            player.setInvulnerable(true);
            now = true;
        }

        player.sendMessage(MessageUtil.prefixed("<gray>Godmode "
                + (now ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>"));

        if (notifyOther != null) {
            notifyOther.sendMessage(MessageUtil.prefixed("<gray>Godmode von "
                    + MessageUtil.player(player.getName())
                    + "<gray> "
                    + (now ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                    + "<gray>.</gray>"));
        }
    }

    public boolean isGod(UUID uuid) {
        return godPlayers.contains(uuid);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("xdtils.god.others")) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}