package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.broadcast")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("broadcast") + "<gray> <nachricht></gray>"));
            return true;
        }

        String raw = String.join(" ", args);
        Bukkit.broadcast(MM.deserialize(
                "<dark_gray>[</dark_gray><#F59E0B><b>!</b></#F59E0B><dark_gray>]</dark_gray> <white>" + raw + "</white>"
        ));
        return true;
    }
}