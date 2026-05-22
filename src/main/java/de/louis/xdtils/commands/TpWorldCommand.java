package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TpWorldCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.tpworld")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("tpworld") + "<gray> <welt></gray>"));
            return true;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            player.sendMessage(MessageUtil.prefixed("<gray>Welt <#F87171>" + args[0] + "</#F87171><gray> nicht gefunden.</gray>"));
            return true;
        }

        player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        player.sendMessage(MessageUtil.prefixed("<gray>Du wurdest zur Welt <#67E8F9>"
                + world.getName() + "</#67E8F9><gray> teleportiert.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            String input = args[0].toLowerCase(Locale.ROOT);
            for (World w : Bukkit.getWorlds()) {
                if (w.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(w.getName());
            }
            return list;
        }
        return List.of();
    }
}