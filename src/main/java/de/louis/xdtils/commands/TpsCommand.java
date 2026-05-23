package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

public class TpsCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.tps")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        double[] tps = Bukkit.getServer().getTPS();

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize("  <#4DA3FF><b>Server Performance</b></#4DA3FF>"));
        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>TPS (1m):</gray>  " + tpsColor(tps[0])));
        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>TPS (5m):</gray>  " + tpsColor(tps[1])));
        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>TPS (15m):</gray> " + tpsColor(tps[2])));

        // Memory
        Runtime rt = Runtime.getRuntime();
        long usedMb  = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        long totalMb = rt.totalMemory() / 1024 / 1024;
        long maxMb   = rt.maxMemory() / 1024 / 1024;
        int percent  = (int) ((usedMb * 100) / maxMb);
        String memColor = percent < 60 ? "<#86EFAC>" : percent < 80 ? "<#FCD34D>" : "<#F87171>";

        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>RAM:</gray>      "
                + memColor + usedMb + "MB</" + memColor.substring(1)
                + " <dark_gray>/</dark_gray> <gray>" + totalMb + "MB</gray>"
                + " <dark_gray>(</dark_gray>" + memColor + percent + "%</" + memColor.substring(1)
                + "<dark_gray>)</dark_gray>"));

        sender.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Online:</gray>   <#67E8F9>"
                + Bukkit.getOnlinePlayers().size() + "</#67E8F9><gray>/"
                + Bukkit.getMaxPlayers() + "</gray>"));

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
    }

    private String tpsColor(double tps) {
        double capped = Math.min(tps, 20.0);
        String val = String.format("%.2f", capped);
        if (capped >= 18) return "<#86EFAC>" + val + "</#86EFAC>";
        if (capped >= 15) return "<#FCD34D>" + val + "</#FCD34D>";
        if (capped >= 10) return "<#F59E0B>" + val + "</#F59E0B>";
        return "<#F87171>" + val + "</#F87171>";
    }
}