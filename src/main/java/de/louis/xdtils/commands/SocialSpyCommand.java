package de.louis.xdtils.commands;

import de.louis.xdtils.manager.SpyManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SocialSpyCommand implements CommandExecutor {

    private final SpyManager spyManager;

    public SocialSpyCommand(SpyManager spyManager) {
        this.spyManager = spyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.socialspy")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        boolean now = spyManager.toggleSocialSpy(player.getUniqueId());
        player.sendMessage(MessageUtil.prefixed("<gray>SocialSpy "
                + (now ? "<#86EFAC>aktiviert</#86EFAC>" : "<#F87171>deaktiviert</#F87171>")
                + "<gray>.</gray>"));
        return true;
    }
}