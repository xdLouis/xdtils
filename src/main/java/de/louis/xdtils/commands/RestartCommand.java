package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RestartCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final JavaPlugin plugin;
    private BukkitTask countdownTask;

    public RestartCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.restart")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // /restart cancel
        if (args.length > 0 && args[0].equalsIgnoreCase("cancel")) {
            if (countdownTask == null || countdownTask.isCancelled()) {
                sender.sendMessage(MessageUtil.prefixed("<gray>Kein Neustart aktiv.</gray>"));
                return true;
            }
            countdownTask.cancel();
            countdownTask = null;
            Bukkit.broadcast(MessageUtil.prefixed("<gray>Neustart wurde <#86EFAC>abgebrochen</#86EFAC><gray>.</gray>"));
            return true;
        }

        int seconds = 30;
        if (args.length > 0) {
            try {
                seconds = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtil.prefixed("<gray>Ungültige Sekunden: <#F87171>" + args[0] + "</#F87171></gray>"));
                return true;
            }
        }

        final int[] remaining = {seconds};
        final int[] announceAt = {60, 30, 20, 10, 5, 4, 3, 2, 1};

        Bukkit.broadcast(MessageUtil.prefixed("<gray>Server wird in <#F87171>"
                + seconds + " Sekunden</#F87171><gray> neugestartet.</gray>"));

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            remaining[0]--;

            for (int a : announceAt) {
                if (remaining[0] == a) {
                    Bukkit.broadcast(MessageUtil.prefixed("<gray>Neustart in <#F87171>"
                            + remaining[0] + " Sekunde" + (remaining[0] == 1 ? "" : "n") + "</#F87171><gray>.</gray>"));

                    // Title an alle
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.showTitle(net.kyori.adventure.title.Title.title(
                                MM.deserialize("<#F87171><b>Neustart</b></#F87171>"),
                                MM.deserialize("<gray>in " + remaining[0] + " Sekunde"
                                        + (remaining[0] == 1 ? "" : "n") + "</gray>")
                        ));
                    }
                }
            }

            if (remaining[0] <= 0) {
                countdownTask.cancel();
                Bukkit.broadcast(MessageUtil.prefixed("<gray>Server wird jetzt neugestartet...</gray>"));
                Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.spigot().restart(), 20L);
            }
        }, 20L, 20L);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("10", "30", "60", "cancel");
        return List.of();
    }
}