package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

public class MemoryCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.memory")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        Runtime rt = Runtime.getRuntime();
        long used  = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        long total = rt.totalMemory() / 1024 / 1024;
        long max   = rt.maxMemory() / 1024 / 1024;
        long free  = max - used;
        int percent = (int) ((used * 100) / max);

        String color = percent < 60 ? "<#86EFAC>" : percent < 80 ? "<#FCD34D>" : "<#F87171>";
        String close = percent < 60 ? "</#86EFAC>" : percent < 80 ? "</#FCD34D>" : "</#F87171>";

        // Balken
        int bars = 20;
        int filled = (int) ((percent / 100.0) * bars);
        StringBuilder bar = new StringBuilder(color);
        for (int i = 0; i < bars; i++) {
            bar.append(i < filled ? "█" : "<dark_gray>█</dark_gray>");
        }
        bar.append(close);

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize("  <#4DA3FF><b>RAM Übersicht</b></#4DA3FF>"));
        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Belegt:</gray>  "
                + color + used + "MB" + close));
        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Frei:</gray>    <#86EFAC>"
                + free + "MB</#86EFAC>"));
        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Gesamt:</gray>  <#67E8F9>"
                + max + "MB</#67E8F9>"));
        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Auslastung:</gray> "
                + color + percent + "%" + close));
        sender.sendMessage(MM.deserialize("  " + bar));
        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
    }
}