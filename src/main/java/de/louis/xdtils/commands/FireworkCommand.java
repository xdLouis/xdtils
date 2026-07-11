package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FireworkCommand implements CommandExecutor {

    private static final Random RANDOM = new Random();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.firework")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        int count = 1;
        if (args.length > 0) {
            try {
                count = Math.min(Integer.parseInt(args[0]), 10);
            } catch (NumberFormatException ignored) {}
        }

        for (int i = 0; i < count; i++) {
            Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta meta = fw.getFireworkMeta();
            meta.setPower(1);
            meta.addEffect(FireworkEffect.builder()
                    .with(FireworkEffect.Type.values()[RANDOM.nextInt(FireworkEffect.Type.values().length)])
                    .withColor(Color.fromRGB(RANDOM.nextInt(0xFFFFFF)))
                    .withFade(Color.fromRGB(RANDOM.nextInt(0xFFFFFF)))
                    .trail(RANDOM.nextBoolean())
                    .flicker(RANDOM.nextBoolean())
                    .build());
            fw.setFireworkMeta(meta);
        }

        player.sendMessage(MessageUtil.prefixed("<gray><#67E8F9>" + count
                + "</#67E8F9><gray> Feuerwerk gestartet.</gray>"));
        return true;
    }
}