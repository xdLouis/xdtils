package de.louis.xdtils.commands;

import de.louis.xdtils.manager.InvSnapshotManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InvSaveCommand implements CommandExecutor, TabCompleter {

    private final InvSnapshotManager snapManager;

    public InvSaveCommand(InvSnapshotManager snapManager) {
        this.snapManager = snapManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.invsave")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        String slot = args.length > 0 ? args[0].toLowerCase() : "default";
        if (slot.length() > 16) {
            player.sendMessage(MessageUtil.prefixed("<gray>Slot-Name zu lang (max. 16 Zeichen).</gray>"));
            return true;
        }

        snapManager.save(player, slot);
        player.sendMessage(MessageUtil.prefixed("<gray>Inventar in Slot <#67E8F9>"
                + slot + "</#67E8F9><gray> gespeichert.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player p)
            return snapManager.getSlots(p.getUniqueId());
        return List.of();
    }
}