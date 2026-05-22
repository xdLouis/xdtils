package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NickCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.nick")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        // /nick off → zurücksetzen
        if (args.length == 0 || args[0].equalsIgnoreCase("off")) {
            player.displayName(null);
            player.playerListName(null);
            player.sendMessage(MessageUtil.prefixed("<gray>Dein Nick wurde <#F87171>entfernt</#F87171><gray>.</gray>"));
            return true;
        }

        String nick = args[0];
        if (nick.length() > 32) {
            player.sendMessage(MessageUtil.prefixed("<gray>Nick darf maximal <#F87171>32 Zeichen</#F87171><gray> lang sein.</gray>"));
            return true;
        }

        var component = MM.deserialize(nick.replace("&", "§"));
        player.displayName(component);
        player.playerListName(component);
        player.sendMessage(MessageUtil.prefixed("<gray>Nick gesetzt auf: ").append(component));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("off");
        return List.of();
    }
}