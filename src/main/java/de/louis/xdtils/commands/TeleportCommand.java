package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class TeleportCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.tp")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.tpUsage());
            return true;
        }

        // /tp <spieler> oder /tp @a <ziel>
        if (args.length == 1) {
            // Muss Spieler sein
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }

            if (args[0].equals("@a")) {
                sender.sendMessage(MessageUtil.tpUsage());
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[0]));
                return true;
            }

            teleportPlayer(player, target.getLocation());
            player.sendMessage(MessageUtil.tpToPlayer(target.getName()));
            return true;
        }

        // /tp @a <ziel>
        if (args[0].equals("@a")) {
            if (args.length < 2) {
                sender.sendMessage(MessageUtil.tpUsage());
                return true;
            }

            Player destination = Bukkit.getPlayerExact(args[1]);
            if (destination == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[1]));
                return true;
            }

            Collection<? extends Player> online = Bukkit.getOnlinePlayers();
            int count = 0;
            for (Player p : online) {
                if (p.getUniqueId().equals(destination.getUniqueId())) continue;
                teleportPlayer(p, destination.getLocation());
                p.sendMessage(MessageUtil.tpToPlayerByOther(sender.getName(), destination.getName()));
                count++;
            }

            sender.sendMessage(MessageUtil.tpAllToPlayer(destination.getName(), count));
            return true;
        }

        // /tp <x> <y> <z>
        if (args.length == 3 && isDouble(args[0]) && isDouble(args[1]) && isDouble(args[2])) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }

            Location loc = parseCoords(player.getLocation(), args[0], args[1], args[2]);
            if (loc == null) {
                sender.sendMessage(MessageUtil.tpInvalidCoords());
                return true;
            }

            teleportPlayer(player, loc);
            player.sendMessage(MessageUtil.tpToCoords(loc));
            return true;
        }

        // /tp <von> <zu> oder /tp <spieler> @a
        if (args.length == 2) {
            // /tp <spieler> <zu-spieler>
            Player from = Bukkit.getPlayerExact(args[0]);
            if (from == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[0]));
                return true;
            }

            if (args[1].equals("@a")) {
                sender.sendMessage(MessageUtil.tpUsage());
                return true;
            }

            Player to = Bukkit.getPlayerExact(args[1]);
            if (to == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[1]));
                return true;
            }

            teleportPlayer(from, to.getLocation());
            sender.sendMessage(MessageUtil.tpPlayerToPlayer(from.getName(), to.getName()));
            from.sendMessage(MessageUtil.tpToPlayerByOther(sender.getName(), to.getName()));
            return true;
        }

        // /tp <spieler> <x> <y> <z>
        if (args.length == 4 && isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3])) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[0]));
                return true;
            }

            Location loc = parseCoords(target.getLocation(), args[1], args[2], args[3]);
            if (loc == null) {
                sender.sendMessage(MessageUtil.tpInvalidCoords());
                return true;
            }

            teleportPlayer(target, loc);
            sender.sendMessage(MessageUtil.tpPlayerToCoords(target.getName(), loc));
            target.sendMessage(MessageUtil.tpToCoordsBy(sender.getName(), loc));
            return true;
        }

        sender.sendMessage(MessageUtil.tpUsage());
        return true;
    }

    private void teleportPlayer(Player player, Location location) {
        player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    @Nullable
    private Location parseCoords(Location base, String xStr, String yStr, String zStr) {
        try {
            double x = parseRelative(base.getX(), xStr);
            double y = parseRelative(base.getY(), yStr);
            double z = parseRelative(base.getZ(), zStr);
            return new Location(base.getWorld(), x, y, z, base.getYaw(), base.getPitch());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double parseRelative(double base, String input) {
        if (input.startsWith("~")) {
            String rest = input.substring(1);
            return rest.isEmpty() ? base : base + Double.parseDouble(rest);
        }
        return Double.parseDouble(input);
    }

    private boolean isDouble(String s) {
        try {
            // Erlaubt auch ~, ~5, ~-3
            if (s.startsWith("~")) {
                String rest = s.substring(1);
                if (rest.isEmpty()) return true;
                Double.parseDouble(rest);
                return true;
            }
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        String input = args[args.length - 1].toLowerCase(Locale.ROOT);

        if (args.length == 1) {
            if ("@a".startsWith(input)) list.add("@a");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(p.getName());
                }
            }
            return list;
        }

        if (args.length == 2) {
            // Nach @a: Zielspieler
            if (args[0].equals("@a")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                        list.add(p.getName());
                    }
                }
                return list;
            }

            // Nach Spielername: anderer Spieler oder Koordinate
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(p.getName());
                }
            }

            if ("~".startsWith(input)) list.add("~");
            return list;
        }

        // Koordinaten-Completion
        if (args.length == 3 || args.length == 4) {
            if ("~".startsWith(input)) list.add("~");
            if (sender instanceof Player player) {
                Location loc = player.getLocation();
                String coord = switch (args.length) {
                    case 3 -> String.valueOf((int) loc.getY());
                    case 4 -> String.valueOf((int) loc.getZ());
                    default -> "~";
                };
                if (coord.startsWith(input)) list.add(coord);
            }
        }

        return list;
    }
}