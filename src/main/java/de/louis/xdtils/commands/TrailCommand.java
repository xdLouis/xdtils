package de.louis.xdtils.commands;

import de.louis.xdtils.manager.TrailManager;
import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Particle;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrailCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final TrailManager trailManager;

    public TrailCommand(TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    // Verfügbare Trail-Typen
    private static final Map<String, Particle> TRAILS = new LinkedHashMap<>();
    static {
        TRAILS.put("hearts",       Particle.HEART);
        TRAILS.put("flames",       Particle.FLAME);
        TRAILS.put("enchant",      Particle.ENCHANT);
        TRAILS.put("notes",        Particle.NOTE);
        TRAILS.put("snow",         Particle.SNOWFLAKE);
        TRAILS.put("magic",        Particle.WITCH);
        TRAILS.put("bubbles",      Particle.BUBBLE_POP);
        TRAILS.put("smoke",        Particle.CAMPFIRE_COSY_SMOKE);
        TRAILS.put("end",          Particle.PORTAL);
        TRAILS.put("totem",        Particle.TOTEM_OF_UNDYING);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.trail")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // /trail off
        if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
            trailManager.removeTrail(player.getUniqueId());
            player.sendMessage(MessageUtil.prefixed("<gray>Trail <#F87171>deaktiviert</#F87171><gray>.</gray>"));
            return true;
        }

        // /trail → Liste
        if (args.length == 0) {
            player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
            player.sendMessage(MM.deserialize("  <#4DA3FF><b>Verfügbare Trails</b></#4DA3FF>"));
            player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
            for (String name : TRAILS.keySet()) {
                boolean active = trailManager.getTrail(player.getUniqueId()) == TRAILS.get(name);
                player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <#67E8F9>"
                        + name + "</#67E8F9>"
                        + (active ? " <#86EFAC>✔</#86EFAC>" : "")));
            }
            player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <#F87171>off</#F87171>"));
            player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
            return true;
        }

        String type = args[0].toLowerCase(Locale.ROOT);
        Particle particle = TRAILS.get(type);
        if (particle == null) {
            player.sendMessage(MessageUtil.prefixed("<gray>Unbekannter Trail: <#F87171>"
                    + type + "</#F87171><gray>. Nutze </gray>"
                    + MessageUtil.command("trail") + "<gray> für die Liste.</gray>"));
            return true;
        }

        trailManager.setTrail(player.getUniqueId(), particle);
        player.sendMessage(MessageUtil.prefixed("<gray>Trail <#67E8F9>"
                + type + "</#67E8F9><gray> aktiviert.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>(TRAILS.keySet());
            list.add("off");
            return list;
        }
        return List.of();
    }
}